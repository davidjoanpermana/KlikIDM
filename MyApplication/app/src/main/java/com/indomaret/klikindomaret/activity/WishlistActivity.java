package com.indomaret.klikindomaret.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.Button;
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
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.indomaret.klikindomaret.API;
import com.indomaret.klikindomaret.R;
import com.indomaret.klikindomaret.adapter.WishListAdapter;
import com.indomaret.klikindomaret.app.AppController;
import com.indomaret.klikindomaret.helper.SessionManager;
import com.indomaret.klikindomaret.helper.UncaughtExceptionHandler;
import com.indomaret.klikindomaret.views.BadgeDrawable;
import com.indomaret.klikindomaret.views.HeightAdjustableGridView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import pl.droidsonroids.gif.GifImageView;

public class WishlistActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private FloatingActionButton scrollToTop;
    private RelativeLayout preloader;
    private Button btnBuyAll, btnBuy, btnDeleteAll, btnDelete;
    private HeightAdjustableGridView wishlistProductGridview;
    private TextView emptyWishlist;

    private WishListAdapter wishListAdapter;
    private SessionManager sessionManager;
    private Intent intent;

    private Tracker mTracker;

    private List<JSONObject> products = new ArrayList<>();
    private List<String> wishList = new ArrayList<>();
    private JSONArray productList;
    private int position = 0;
    private int page = 1;
    private int pageSize = 24;
    private int totalRecords;
    private int totalItemInCart = 0;
    private String url;
    private boolean isBuyAll, isContinous, isDeletedAll;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wishlist);

        UncaughtExceptionHandler uncaughtExceptionHandler = new UncaughtExceptionHandler();
        uncaughtExceptionHandler.uncaughtException(this);

        AppController application = (AppController) getApplication();
        mTracker = application.getDefaultTracker();

        toolbar = (Toolbar) findViewById(R.id.app_toolbar);
        TextView mTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
        mTitle.setText("Wishlist");
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        sessionManager = new SessionManager(WishlistActivity.this);

        preloader = (RelativeLayout) findViewById(R.id.preloader);
        emptyWishlist = (TextView) findViewById(R.id.empty_wishlist);
        wishlistProductGridview = (HeightAdjustableGridView) findViewById(R.id.wishlist_gridview);
        btnBuyAll = (Button) findViewById(R.id.btn_buy_all);
        btnBuy = (Button) findViewById(R.id.btn_buy);
        btnDeleteAll = (Button) findViewById(R.id.btn_delete_all);
        btnDelete = (Button) findViewById(R.id.btn_delete);

        preloader.setVisibility(View.VISIBLE);

        if(sessionManager.isLoggedIn() != true){
            intent = new Intent(WishlistActivity.this, LoginActivity.class);
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

        scrollToTop = (FloatingActionButton) findViewById(R.id.scroll_to_top);
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

        btnBuyAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (products.size() > 0){
                    preloader.setVisibility(View.VISIBLE);

                    try {
                        jsonArrayRequest(API.getInstance().getApiModifyCart()
                                +"?cartRef=mobile"
                                +"&pId=" + products.get(0).getString("ID")
                                +"&mod=add"
                                +"&qty=1"
                                +"&regionID=" + sessionManager.getRegionID()
                                +"&id="
                                +"&scId=" + sessionManager.getCartId()
                                +"&cId=" + sessionManager.getUserID()
                                +"&isPair=false"
                                +"&mfp_id=" + sessionManager.getKeyMfpId(), "modify");
                        isBuyAll = true;
                        isContinous = true;
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    jsonArrayRequest(url, "cart");
                }
            }
        });

        btnBuy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (products.size() > 0){
                    preloader.setVisibility(View.VISIBLE);

                    boolean isProsses = false;
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

                                position = i;
                                isProsses = true;
                                isBuyAll = false;
                                if (!isContinous) isContinous = true;
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    if (isProsses){
                        jsonArrayRequest(url, "cart");
                    }else{
                        preloader.setVisibility(View.GONE);
                    }
                }
            }
        });

        btnDeleteAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (products.size() > 0){
                    preloader.setVisibility(View.VISIBLE);

                    try{
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
                        productWishlist.put("ProductID", products.get(0).getString("ID"));
                        productWishlist.put("Description", "");
                        productWishlist.put("Created", "");

                        productArray.put(productWishlist);
                        customer.put("WishListItems", productArray);

                        String url = API.getInstance().getApiAddWishlist()
                                +"?mfp_id=" + sessionManager.getKeyMfpId()
                                +"&regionID=" + sessionManager.getRegionID();

                        jsonArrayPost(url, customer, products.get(0).getString("ID"));
                        isDeletedAll = true;
                    } catch(Exception e){

                    }
                }
            }
        });

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (products.size() > 0){
                    preloader.setVisibility(View.VISIBLE);

                    boolean isProsses = false;
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

                                String url = API.getInstance().getApiAddWishlist()
                                        +"?mfp_id=" + sessionManager.getKeyMfpId()
                                        +"&regionID=" + sessionManager.getRegionID();

                                final String produkId = products.get(i).getString("ID");
                                JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                                        url, customer,
                                        new Response.Listener<JSONObject>() {

                                            @Override
                                            public void onResponse(JSONObject response) {
                                                try {
                                                    if (response == null || response.length() == 0){
                                                        preloader.setVisibility(View.GONE);
                                                        Toast.makeText(WishlistActivity.this, "Gagal terkoneksi server", Toast.LENGTH_LONG).show();
                                                    }else{
                                                        if (response.getString("Message").equals("success")){
                                                            if (sessionManager.getKeyWishlist().length() > 0){
                                                                wishList = new ArrayList<>(Arrays.asList(sessionManager.getKeyWishlist().replace("[", "").replace("]", "").replace(" ", "").split(",")));
                                                                for (int i=0; i<wishList.size(); i++){
                                                                    if (wishList.get(i).equals(produkId)){
                                                                        wishList.remove(i);
                                                                    }
                                                                }

                                                                sessionManager.setKeyWishlist(wishList.toString());
                                                            }
                                                        }
                                                    }
                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                        }, new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        Toast.makeText(WishlistActivity.this, "Gagal terkoneksi server", Toast.LENGTH_LONG).show();
                                        preloader.setVisibility(View.GONE);
                                    }
                                });

                                AppController.getInstance().addToRequestQueue(jsonObjReq);
                                isProsses = true;
                            }
                        }

                        if (isProsses){
                            removeWishlistResponse();
                        }else{
                            preloader.setVisibility(View.GONE);
                        }
                    } catch(Exception e){

                    }
                }
            }
        });

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
    }

    public void modifyResponse(JSONArray response){
        try {
            if (sessionManager.getCartId() == null || sessionManager.getCartId().equals("00000000-0000-0000-0000-000000000000")){
                try {
                    sessionManager.setCartId(response.getJSONObject(0).getString("ResponseID"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            JSONObject cart = response.getJSONObject(0);

            if(cart.getBoolean("Success")) {
                jsonArrayRequest(API.getInstance().getCartTotal()
                        +"?cartId=" + sessionManager.getCartId()
                        +"&customerId=" + sessionManager.getUserID()
                        +"&isVirtual=false"
                        +"&mfp_id=" + sessionManager.getKeyMfpId() , "cart");

                final Toast toast = Toast.makeText(this, "Produk masuk ke keranjang", Toast.LENGTH_SHORT);
                toast.show();

                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        toast.cancel();
                    }
                }, 500);

                if (isContinous && products.size() > 1){
                    if (isBuyAll){
                        for (int i=1; i<products.size(); i++){
                            try {
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
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }else{
                        for (int i=0; i<products.size(); i++){
                            try {
                                if (products.get(i).getBoolean("selected") && position != i ){
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
                    }
                }

                isContinous = false;
                for(int i=0; i<products.size(); i++){
                    try {
                        products.get(i).put("selected", false);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                wishListAdapter.notifyDataSetChanged();

                preloader.setVisibility(View.GONE);
            } else {
                preloader.setVisibility(View.GONE);

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
                alertDialogBuilder.setMessage(cart.getString("ErrorMessage"));
                alertDialogBuilder.setNegativeButton("Tutup", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {}
                });

                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.setCanceledOnTouchOutside(false);
                alertDialog.show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    //JSON ARRAY REQUEST
    public void jsonArrayRequest(String url, final String type) {
        System.out.println("wishlist url = " + url);

        JsonArrayRequest jsonArrayReq = new JsonArrayRequest(url,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        if (response == null || response.length() == 0){
                            preloader.setVisibility(View.GONE);
                            Toast.makeText(WishlistActivity.this, "Gagal terkoneksi server", Toast.LENGTH_LONG).show();
                        }else{
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
                                modifyResponse(response);
                            }
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if(type.equals("modify")){
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(WishlistActivity.this);
                    alertDialogBuilder.setMessage("Gagal menambahkan ke keranjang");
                    alertDialogBuilder.setNegativeButton("Tutup", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface arg0, int arg1) {}
                    });

                    AlertDialog alertDialog = alertDialogBuilder.create();
                    alertDialog.setCanceledOnTouchOutside(false);
                    alertDialog.show();
                }

                preloader.setVisibility(View.GONE);
            }
        }, this);

        jsonArrayReq.setRetryPolicy(new DefaultRetryPolicy(60000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppController.getInstance().addToRequestQueue(jsonArrayReq);
    }

    public void jsonArrayPost(String url, JSONObject jsonObject, final String id) {
        System.out.println("wishlist url = " + url);

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                url, jsonObject,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if (response == null || response.length() == 0){
                                preloader.setVisibility(View.GONE);
                                Toast.makeText(WishlistActivity.this, "Gagal terkoneksi server", Toast.LENGTH_LONG).show();
                            }else{
                                if (response.getString("Message").equals("success")){
                                    if (isDeletedAll){
                                        for (int i=1; i<products.size(); i++){
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

                                            String url = API.getInstance().getApiAddWishlist()
                                                    +"?mfp_id=" + sessionManager.getKeyMfpId()
                                                    +"&regionID=" + sessionManager.getRegionID();

                                            jsonArrayPost(url, customer, products.get(i).getString("ID"));
                                            isDeletedAll = false;
                                        }
                                    }

                                    if (sessionManager.getKeyWishlist().length() > 0){
                                        wishList = new ArrayList<>(Arrays.asList(sessionManager.getKeyWishlist().replace("[", "").replace("]", "").replace(" ", "").split(",")));
                                        for (int i=0; i<wishList.size(); i++){
                                            if (wishList.get(i).equals(id)){
                                                wishList.remove(i);
                                            }
                                        }

                                        sessionManager.setKeyWishlist(wishList.toString());
                                    }

                                    removeWishlistResponse();
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(WishlistActivity.this, "Gagal terkoneksi server", Toast.LENGTH_LONG).show();
                preloader.setVisibility(View.GONE);
            }
        });

        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(60000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppController.getInstance().addToRequestQueue(jsonObjReq);
    }

    public void removeWishlistResponse(){
        preloader.setVisibility(View.GONE);

        for (int i=0; i<products.size(); i++){
            products.remove(i);
            wishListAdapter.notifyDataSetChanged();
            totalRecords = 0;
        }
    }

    public void processWishList(JSONArray response, String type){
        preloader.setVisibility(View.GONE);

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
//        boolean isselected = false;
        this.products = products;

//        for (int i=0; i<products.size(); i++){
//            try {
//                if (products.get(i).getBoolean("selected")){
//                    isselected = true;
//                }
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//        }

        wishListAdapter.notifyDataSetChanged();
    }

    public void prepareGridView(){
        wishListAdapter = new WishListAdapter(WishlistActivity.this, products);
        wishlistProductGridview.setAdapter(wishListAdapter);
        wishListAdapter.notifyDataSetChanged();
    }

    public void updateCartTotal(int total) {
        totalItemInCart = total;
        this.invalidateOptionsMenu();
    }

    @Override
    @SuppressWarnings("deprecation")
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.category_menu, menu);
        MenuItem itemCart = menu.findItem(R.id.keranjang_top_btn);
        LayerDrawable icon = (LayerDrawable) itemCart.getIcon();
        setBadgeCount(this, icon, "" + totalItemInCart);

        return true;
    }

    public static void setBadgeCount(Context context, LayerDrawable icon, String count) {
        BadgeDrawable badge;
        Drawable reuse = icon.findDrawableByLayerId(R.id.ic_badge);

        if (reuse != null && reuse instanceof BadgeDrawable) {
            badge = (BadgeDrawable) reuse;
        } else {
            badge = new BadgeDrawable(context);
        }

        badge.setCount(count);
        icon.mutate();
        icon.setDrawableByLayerId(R.id.ic_badge, badge);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            finish();
            overridePendingTransition(R.anim.right_in, R.anim.right_out);
            return true;
        } else if(id == R.id.keranjang_top_btn){
            if(totalItemInCart == 0){
                android.support.v7.app.AlertDialog.Builder alertDialogBuilder = new android.support.v7.app.AlertDialog.Builder(WishlistActivity.this);
                alertDialogBuilder.setMessage("Keranjang belanja Anda kosong.");
                alertDialogBuilder.setNegativeButton("Mulai Belanja", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {}
                });

                android.support.v7.app.AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
            } else {
                sessionManager.setEmptyProd(null);
                intent = new Intent(WishlistActivity.this, CartActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.left_in, R.anim.left_out);
            }

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.right_in, R.anim.right_out);
    }

    @Override
    public void onResume(){
        super.onResume();

        mTracker.setScreenName("Wishlist");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());

//        if (sessionManager.isLoggedIn()){
//            if(sessionManager.getCartId() == null || sessionManager.getCartId() == "00000000-0000-0000-0000-000000000000"){
//                stringRequest(API.getInstance().getLastestShoppingCartId() + "?CustomerId=" + sessionManager.getUserID() + "&mfp_id=" + sessionManager.getKeyMfpId()+"&RegionId="+sessionManager.getRegionID());
//            }
//        }

        url = API.getInstance().getCartTotal()+"?cartId=" + sessionManager.getCartId()+"&customerId=" + sessionManager.getUserID()+"&mfp_id=" + sessionManager.getKeyMfpId();
        jsonArrayRequest(url, "cart");
    }

    // STRING REQUEST
//    public void stringRequest(String url){
//        StringRequest strReq = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
//            @Override
//            public void onResponse(String response) {
//                System.out.println("get lastest cart id = " + response);
//            }
//        }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                preloader.setVisibility(View.GONE);
//            }
//        });
//
//        AppController.getInstance().addToRequestQueue(strReq);
//    }
}
