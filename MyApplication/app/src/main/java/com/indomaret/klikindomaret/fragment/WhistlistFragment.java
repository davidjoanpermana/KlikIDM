package com.indomaret.klikindomaret.fragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.analytics.Tracker;
import com.indomaret.klikindomaret.API;
import com.indomaret.klikindomaret.MainActivity;
import com.indomaret.klikindomaret.R;
import com.indomaret.klikindomaret.activity.LoginActivity;
import com.indomaret.klikindomaret.activity.WishlistActivity;
import com.indomaret.klikindomaret.adapter.WishListAdapter;
import com.indomaret.klikindomaret.app.AppController;
import com.indomaret.klikindomaret.helper.SessionManager;
import com.indomaret.klikindomaret.views.HeightAdjustableGridView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import pl.droidsonroids.gif.GifImageView;

/**
 * A simple {@link Fragment} subclass.
 */
public class WhistlistFragment extends Fragment {
    private View view;
    private FloatingActionButton scrollToTop;
    private RelativeLayout preloader;
    private LinearLayout buyChecked, removeChecked;
    private HeightAdjustableGridView wishlistProductGridview;
    private TextView emptyWishlist;

    private WishListAdapter wishListAdapter;
    private SessionManager sessionManager;
    private Intent intent;

    private Tracker mTracker;

    private List<JSONObject> products = new ArrayList<>();
    private JSONArray productList;
    private int page = 1;
    private int pageSize = 24;
    private int totalRecords;
    private int totalItemInCart = 0;
    private String url;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_wishlist, container, false);
        sessionManager = new SessionManager(getActivity());

        preloader = (RelativeLayout) view.findViewById(R.id.preloader);

        buyChecked = (LinearLayout) view.findViewById(R.id.wishlist_btn_buy);
        removeChecked = (LinearLayout) view.findViewById(R.id.wishlist_btn_remove);

        emptyWishlist = (TextView) view.findViewById(R.id.empty_wishlist);

        wishlistProductGridview = (HeightAdjustableGridView) view.findViewById(R.id.wishlist_gridview);

        runLoader();

        if(sessionManager.isLoggedIn() != true){
            intent = new Intent(getActivity(), LoginActivity.class);
            startActivity(intent);
        }

        //CALL ITEM CART TOTAL
        if (sessionManager.getCartId() != null || !sessionManager.getCartId().equals("00000000-0000-0000-0000-000000000000")){
            url = API.getInstance().getCartTotal()+"?cartId=" + sessionManager.getCartId()+"&customerId=" + sessionManager.getUserID()+"&mfp_id=" + sessionManager.getKeyMfpId();
            jsonArrayRequest(url, "cart");
        }

        //CALL WISHLIST ITEM
        jsonArrayRequest(API.getInstance().getApiPaggingNoCache()+"?isPackage=false&isNoPaging=false&customerID=" + sessionManager.getUserID() + "&mfp_id=" + sessionManager.getKeyMfpId() + "&regionID=" + sessionManager.getRegionID() + "&page=" + page + "&pageSize=" + pageSize, "wish");
//        jsonArrayRequest(API.getInstance().getApiCategories()+"?isPackage=false&isNoPaging=false&customerID=" + sessionManager.getUserID() + "&mfp_id=" + sessionManager.getKeyMfpId() + "&regionID=" + sessionManager.getRegionID(), "wish");

        scrollToTop = (FloatingActionButton) view.findViewById(R.id.scroll_to_top);
        scrollToTop.animate().cancel();
        scrollToTop.animate().translationYBy(350);

        scrollToTop.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        wishlistProductGridview.smoothScrollToPosition(0);
                    }
                }
        );

        removeChecked.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        runLoader();

                        try{
                            for (int i=0; i<products.size(); i++){
                                if (products.get(i).getBoolean("selected")){
                                    JSONObject productWishlist = new JSONObject();
                                    JSONObject customer = new JSONObject();
                                    JSONArray productArray = new JSONArray();

                                    customer.put("ID", "00000000-0000-0000-0000-000000000000");
                                    customer.put("CustomerID", sessionManager.getUserID());
                                    customer.put("IsShared", false);
                                    customer.put("ShareCode", "");
                                    customer.put("Action", "remove");

                                    productWishlist.put("ID", "00000000-0000-0000-0000-000000000000");
                                    productWishlist.put("WishListID", "00000000-0000-0000-0000-000000000000");
                                    productWishlist.put("ProductID", products.get(i).getString("ID"));
                                    productWishlist.put("Description", "");
                                    productWishlist.put("Created", "");

                                    productArray.put(productWishlist);

                                    customer.put("WishListItems", productArray);

                                    jsonPost(API.getInstance().getApiAddWishlist()
                                            +"?mfp_id=" + sessionManager.getKeyMfpId()
                                            +"&regionID=" + sessionManager.getRegionID(), customer);
                                }
                            }

                            removeWishlistResponse();
                        } catch(Exception e){

                        }
                    }
                }
        );

        wishlistProductGridview.setOnScrollListener(
                new AbsListView.OnScrollListener() {
                    @Override
                    public void onScrollStateChanged(AbsListView view, int scrollState) {
                        int threshold = 1;
                        int count = wishlistProductGridview.getCount();

                        int btn_initPosY = scrollToTop.getScrollY();
                        if (scrollState == SCROLL_STATE_IDLE) {

                            if ((count < totalRecords) && ((wishlistProductGridview.getLastVisiblePosition()) >= (count - threshold))) {
                                jsonArrayRequest(API.getInstance().getApiPaggingNoCache() + "?isPackage=false&isNoPaging=false&customerID=" + sessionManager.getUserID() + "&mfp_id=" + sessionManager.getKeyMfpId() + "&regionID=" + sessionManager.getRegionID() + "&page=" + page + "&pageSize=" + pageSize, "more");
                            }

                            if (wishlistProductGridview.getFirstVisiblePosition() <= 1) {
                                scrollToTop.animate().cancel();
                                scrollToTop.animate().translationYBy(350);
                            } else {
                                scrollToTop.animate().cancel();
                                scrollToTop.animate().translationY(btn_initPosY);
                            }
                        }
                    }

                    @Override
                    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

                    }
                }
        );

        buyChecked.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        runLoader();

                        for (int i=0; i<products.size(); i++){
                            try {
                                if (products.get(i).getBoolean("selected")){
                                    jsonArrayRequest(API.getInstance().getApiModifyCart()
                                            +"?cartRef=mobile"
                                            +"&pId=" + products.get(i).getString("ID")
                                            +"&mod=add"
                                            +"&qty=1"
                                            +"&regionID=" + sessionManager.getRegionID()
                                            +"&id="
                                            +"&scId=" + sessionManager.getCartId()
                                            +"&cId=" + sessionManager.getUserID()
                                            +"&isPair=false"
                                            +"&mfp_id=" + sessionManager.getKeyMfpId(), "modify");
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        prepareGridView();

                        try{
                            Thread.sleep(2000);
                        } catch (Exception e){

                        }

                        jsonArrayRequest(url, "cart");
                        LinearLayout linearLayout = (LinearLayout) view.findViewById(R.id.wishlist_table_btn);
                        linearLayout.setVisibility(LinearLayout.GONE);
                    }
                }
        );

        return view;
    }

    //JSON ARRAY REQUEST
    public void jsonArrayRequest(String url, final String type) {
        System.out.println("wishlist url = " + url);

        JsonArrayRequest jsonArrayReq = new JsonArrayRequest(url,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        if(type.equals("wish")){
                            processWishList(response, type);
                        } else if(type.equals("more")){
                            processWishList(response, type);
                        } else if(type.equals("cart")){
                            try {
                                updateCartTotal(response.getInt(0));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        } else if(type.equals("del")){
                            processWishList(response, type);
                        }else if(type.equals("modify")){
                            for(int i=0; i<products.size(); i++){
                                try {
                                    products.get(i).put("selected", false);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }

                            stopLoader();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if(type.equals("modify")){
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
                    alertDialogBuilder.setMessage("Gagal menambahkan ke keranjang");
                    alertDialogBuilder.setNegativeButton("Tutup", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface arg0, int arg1) {}
                    });

                    AlertDialog alertDialog = alertDialogBuilder.create();
                    alertDialog.setCanceledOnTouchOutside(false);
                    alertDialog.show();
                }else{
                    Toast.makeText(getActivity(), "Gagal terkoneksi server", Toast.LENGTH_LONG).show();
                }

                stopLoader();
            }
        }, getActivity());

        jsonArrayReq.setRetryPolicy(new DefaultRetryPolicy(60000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppController.getInstance().addToRequestQueue(jsonArrayReq);
    }

    //POST REQUEST
    public void jsonPost(String urlJsonObj, JSONObject jsonObject){
        System.out.println("wishlist = " + urlJsonObj);
        System.out.println("wishlist = " + jsonObject);

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                urlJsonObj, jsonObject,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
//                        if (response == null || response.length() == 0){
//                            stopLoader();
//                            Toast.makeText(getActivity(), "Gagal terkoneksi server", Toast.LENGTH_LONG).show();
//                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getActivity(), "Gagal terkoneksi server", Toast.LENGTH_LONG).show();
                stopLoader();
            }
        });

        AppController.getInstance().addToRequestQueue(jsonObjReq);
    }

    public void removeWishlistResponse(){
        stopLoader();

        for (int i=0; i<products.size(); i++){
            try {
                if (products.get(i).getBoolean("selected")){
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
                    alertDialogBuilder.setMessage("Berhasil menghapus produk " + products.get(i).getString("Name") + "dari Wishlist");

                    final int finalI = i;
                    alertDialogBuilder.setNegativeButton("Tutup", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface arg0, int arg1) {
                            products.remove(finalI);
                            wishListAdapter.notifyDataSetChanged();
                            totalRecords = 0;
                            LinearLayout linearLayout = (LinearLayout) view.findViewById(R.id.wishlist_table_btn);
                            linearLayout.setVisibility(LinearLayout.GONE);
                        }
                    });

                    AlertDialog alertDialog = alertDialogBuilder.create();
                    alertDialog.show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public void processWishList(JSONArray response, String type){
        stopLoader();

        try {
            productList = response.getJSONObject(0).getJSONArray("ProductList");

            if (productList.length() > 0){
                for(int i=0; i<productList.length(); i++){
                    productList.getJSONObject(i).put("selected", false);
                    products.add(productList.getJSONObject(i));
                }

                totalRecords = response.getJSONObject(0).getInt("TotalRecord");
                emptyWishlist.setVisibility(View.GONE);
            } else {
                emptyWishlist.setVisibility(View.VISIBLE);
                return;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if(type.equals("more")){
            wishListAdapter.notifyDataSetChanged();
        } else if(type.equals("wish")){
            prepareGridView();
        } else if(type.equals("del")){
            prepareGridView();
        }
    }

    public void setItem(List<JSONObject> products){
        boolean isselected = false;
        this.products = products;

        for (int i=0; i<products.size(); i++){
            try {
                if (products.get(i).getBoolean("selected")){
                    isselected = true;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        if (isselected) {
            LinearLayout linearLayout = (LinearLayout) view.findViewById(R.id.wishlist_table_btn);
            linearLayout.setVisibility(LinearLayout.VISIBLE);
        } else {
            LinearLayout linearLayout = (LinearLayout) view.findViewById(R.id.wishlist_table_btn);
            linearLayout.setVisibility(LinearLayout.GONE);
        }

        wishListAdapter.notifyDataSetChanged();
    }

    public void prepareGridView(){
        wishListAdapter = new WishListAdapter(getActivity(), products);
        wishlistProductGridview.setAdapter(wishListAdapter);
    }

    public void updateCartTotal(int total) {
        totalItemInCart = total;
    }

    public void runLoader(){
        preloader.setVisibility(View.VISIBLE);
    }

    public void stopLoader(){
        preloader.setVisibility(View.GONE);
    }
}
