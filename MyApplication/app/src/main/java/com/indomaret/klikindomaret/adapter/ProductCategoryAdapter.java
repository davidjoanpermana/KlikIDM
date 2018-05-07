package com.indomaret.klikindomaret.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.NetworkImageView;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsConstants;
import com.facebook.appevents.AppEventsLogger;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.android.gms.analytics.ecommerce.Product;
import com.google.android.gms.analytics.ecommerce.ProductAction;
import com.indomaret.klikindomaret.API;
import com.indomaret.klikindomaret.R;
import com.indomaret.klikindomaret.activity.CategoryActivity;
import com.indomaret.klikindomaret.activity.CategoryLevel1Activity;
import com.indomaret.klikindomaret.app.AppController;
import com.indomaret.klikindomaret.helper.SessionManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * Created by USER on 4/12/2016.
 */
public class ProductCategoryAdapter extends BaseAdapter {
    private SessionManager sessionManager;
    private Activity activity;
    private LayoutInflater inflater;
    private ImageLoader imageLoader = AppController.getInstance().getImageLoader();

    private List<JSONObject> productList = new ArrayList<>();
    private List<String> wishList = new ArrayList<>();
    private String sendDesc, from;
    private Double validPrice = 0.0;
    private Double discount = 0.0;
    private Double price = 0.0;
    private DecimalFormat df = new DecimalFormat("#,###");

    public ProductCategoryAdapter(Activity activity, List<JSONObject> productList, String from){
        this.activity = activity;
        this.productList = productList;
        this.from = from;
        sessionManager = new SessionManager(activity);
    }

    @Override
    public int getCount() {
        return productList.size();
    }

    @Override
    public Object getItem(int position) {
        return productList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final JSONObject product = productList.get(position);

        if (inflater == null)
            inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (convertView == null)
            convertView = inflater.inflate(R.layout.category_gridview_single_item, null);

        if (imageLoader == null)
            imageLoader = AppController.getInstance().getImageLoader();

        NetworkImageView productImage = (NetworkImageView) convertView.findViewById(R.id.product_image_category);
        final ImageView sendToWishList = (ImageView) convertView.findViewById(R.id.product_send_to_wishlist);

        TextView productSendFrom = (TextView) convertView.findViewById(R.id.product_sender);
        TextView productName = (TextView) convertView.findViewById(R.id.product_name);
        TextView productDiscount = (TextView) convertView.findViewById(R.id.product_discount);
        TextView productPrice = (TextView) convertView.findViewById(R.id.product_price);
        TextView productInstallment = (TextView) convertView.findViewById(R.id.product_installment);
        TextView productInitialPrice = (TextView) convertView.findViewById(R.id.product_initial_price);

        LinearLayout buyButton = (LinearLayout) convertView.findViewById(R.id.btn_category_buy);
        LinearLayout soldoutButton = (LinearLayout) convertView.findViewById(R.id.btn_category_soldout);
        LinearLayout soldoutButtonPromo = (LinearLayout) convertView.findViewById(R.id.btn_category_soldout_promo);
        LinearLayout linearAdapter = (LinearLayout) convertView.findViewById(R.id.linear_adapter);

        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(activity);

        try {
            if (sessionManager.getKeyWishlist().length() > 0){
                wishList = new ArrayList<String>(Arrays.asList(sessionManager.getKeyWishlist().replace("[", "").replace("]", "").replace(" ", "").split(",")));

                for (int i=0; i<wishList.size(); i++){
                    if (wishList.get(i).equals(product.getString("ID"))){
                        sendToWishList.setColorFilter(Color.parseColor("#c60000"));
                    }
                }
            }

            if (product.getString("Title").length()>0 && !product.getString("Title").toLowerCase().equals("null") ){
                productName.setText(product.getString("Title"));
            } else {
                productName.setText(product.getString("Name"));
            }

            productImage.setImageUrl(product.getString("ImageThumb"), imageLoader);

            if (product.getString("ProductFlag").toLowerCase().equals("plaza")){
                if(product.getString("Flag_Produk").substring(0,1).equals("D")){
                    sendDesc = "Dikirim dari Gudang";
                } else {
                    if(product.getString("Flag_Produk").substring(0,1).equals("B")){
                        sendDesc = "Dikirim dari Penjual";
                    }
                }
            } else {
                sendDesc = "Dikirim dari Toko";
            }

            productSendFrom.setText(sendDesc);
            if (product.getString("HargaWebsite") != null && !product.getString("HargaWebsite").toLowerCase().equals("null") && !product.getString("HargaWebsite").equals("")){
                linearAdapter.setVisibility(View.VISIBLE);
                validPrice = Double.parseDouble(product.getString("HargaWebsite"));
                discount = Double.parseDouble(product.getString("Discount"));
                price = validPrice - discount;
                productPrice.setText("Rp " + df.format(price).replace(",", "."));
            }else{
                linearAdapter.setVisibility(View.GONE);
            }

            if(!product.getString("IsInstallment").toLowerCase().equals("null")){
                productInstallment.setText("Cicilan 0%");
                productInstallment.setVisibility(View.VISIBLE);
            } else {
                productInstallment.setVisibility(View.GONE);
            }

            if(!discount.equals(0.0)){
                productInitialPrice.setText("Rp " + df.format(validPrice).replace(",","."));
                productInitialPrice.setVisibility(View.VISIBLE);
                productInitialPrice.setPaintFlags(productInitialPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            } else {
                productInitialPrice.setVisibility(View.GONE);
            }

            if (product.getString("BadgeTag").equals("") || product.getString("BadgeTag") == null
                    || product.getString("BadgeTag").toLowerCase().equals("null")){
                productDiscount.setVisibility(View.GONE);
            }else{
                String badgeArray = product.getString("BadgeTag");

                JSONArray jsonBadgeArray = new JSONArray(badgeArray);
                int badgeType = 100;

                for(int i=0; i<jsonBadgeArray.length(); i++){
                    if (jsonBadgeArray.getJSONObject(i).getInt("BadgeType") < badgeType){
                        badgeType = jsonBadgeArray.getJSONObject(i).getInt("BadgeType");

                        if (jsonBadgeArray.getJSONObject(i).getString("BadgeDesc").toLowerCase().equals("tebus murah")){
                            productDiscount.setVisibility(View.GONE);
                        }else{
                            productDiscount.setVisibility(View.VISIBLE);
                            productDiscount.setText(jsonBadgeArray.getJSONObject(i).getString("BadgeDesc"));
                        }
                    }
                }
            }

            Date endDatePromo = null;

            if (product.getString("EndDatePromo").equals("") || product.getString("EndDatePromo") == null
                    || product.getString("EndDatePromo").toLowerCase().equals("null")){

            }else{
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String[] stringSplit = product.getString("EndDatePromo").split("T");

                try {
                    endDatePromo = formatter.parse(stringSplit[0] + " " + stringSplit[1]);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }

            if (product.getString("IsSoldOut") == "false" && product.getString("IsHideAddToCart") == "true"){
                buyButton.setVisibility(View.GONE);
                soldoutButton.setVisibility(View.GONE);
                soldoutButtonPromo.setVisibility(View.VISIBLE);
            } else if(product.getString("IsSoldOut") == "true"){
                buyButton.setVisibility(View.GONE);
                soldoutButton.setVisibility(View.VISIBLE);
                soldoutButtonPromo.setVisibility(View.GONE);
            } else if(product.getInt("TotalTransactionQuota") > 0 && product.getInt("MaxTransactionCMS") > 0
                    && product.getInt("TotalTransactionQuota") >= product.getInt("MaxTransactionCMS") && endDatePromo.before(new Date())){
                buyButton.setVisibility(View.GONE);
                soldoutButton.setVisibility(View.VISIBLE);
                soldoutButtonPromo.setVisibility(View.GONE);
            } else {
                buyButton.setVisibility(View.VISIBLE);
                soldoutButton.setVisibility(View.GONE);
                soldoutButtonPromo.setVisibility(View.GONE);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        //send to wishlist function
        sendToWishList.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        sendToWishList.setColorFilter(Color.parseColor("#c60000"));
                        if (from.equals("cat")){
                            ((CategoryActivity) activity).runPreloader();
                        }else if (from.equals("catByLevel")){
                            ((CategoryLevel1Activity) activity).runPreloader();
                        }

                        JSONObject customer = new JSONObject();
                        JSONObject productWishlist = new JSONObject();
                        JSONArray productArray = new JSONArray();

                        if (sessionManager.isLoggedIn()){
                            try{
                                customer.put("ID", "00000000-0000-0000-0000-000000000000");
                                customer.put("CustomerID", sessionManager.getUserID());
                                customer.put("IsShared", false);
                                customer.put("ShareCode", "");
                                customer.put("Action", "add");

                                productWishlist.put("ID", "00000000-0000-0000-0000-000000000000");
                                productWishlist.put("WishListID", "00000000-0000-0000-0000-000000000000");
                                productWishlist.put("ProductID", product.getString("ID"));
                                productWishlist.put("Description", "");
                                productWishlist.put("Created", "");

                                productArray.put(productWishlist);
                                customer.put("WishListItems", productArray);
                                String url = API.getInstance().getApiAddWishlist()
                                        +"?mfp_id=" + sessionManager.getKeyMfpId()
                                        +"&regionID=" + sessionManager.getRegionID();

                                JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                                        url, customer,
                                        new Response.Listener<JSONObject>() {

                                            @Override
                                            public void onResponse(JSONObject response) {
                                                if (from.equals("cat")){
                                                    ((CategoryActivity) activity).stopPreloader();
                                                }else if (from.equals("catByLevel")){
                                                    ((CategoryLevel1Activity) activity).stopPreloader();
                                                }

                                                if (response == null || response.length() == 0){
                                                    Toast.makeText(activity, "Gagal terkoneksi server", Toast.LENGTH_LONG).show();
                                                }else{
                                                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(activity);

                                                    try{
                                                        if(response.getBoolean("IsSuccess")){
                                                            if(response.getString("Message").equals("success")){
                                                                alertDialogBuilder.setMessage("Berhasil menambahkan produk ke Wishlist");

                                                                if (sessionManager.getKeyWishlist().length() > 0){
                                                                    wishList = new ArrayList<String>(Arrays.asList(sessionManager.getKeyWishlist().replace("[", "").replace("]", "").replace(" ", "").split(",")));
                                                                    try {
                                                                        wishList.add(productList.get(position).getString("ID"));
                                                                        sessionManager.setKeyWishlist(wishList.toString());
                                                                    } catch (JSONException e) {
                                                                        e.printStackTrace();
                                                                    }
                                                                }else{
                                                                    wishList.add(productList.get(position).getString("ID"));
                                                                    sessionManager.setKeyWishlist(wishList.toString());
                                                                }
                                                            } else if(response.getString("Message").equals("already")){
                                                                alertDialogBuilder.setMessage("Produk sudah ada di Wishlist");
                                                            }
                                                        } else {
                                                            alertDialogBuilder.setMessage("Gagal menambahkan produk ke Wishlist");
                                                        }
                                                    } catch(Exception e) {
                                                        alertDialogBuilder.setMessage("Gagal melakukan koneksi");
                                                    }

                                                    alertDialogBuilder.setNegativeButton("Tutup", new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface arg0, int arg1) {}
                                                    });

                                                    AlertDialog alertDialog = alertDialogBuilder.create();
                                                    alertDialog.show();
                                                }
                                            }
                                        }, new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        Toast.makeText(activity, "Gagal terkoneksi server", Toast.LENGTH_LONG).show();
                                        if (from.equals("cat")){
                                            ((CategoryActivity) activity).stopPreloader();
                                        }else if (from.equals("catByLevel")){
                                            ((CategoryLevel1Activity) activity).stopPreloader();
                                        }
                                    }
                                });
                                AppController.getInstance().addToRequestQueue(jsonObjReq);
                            } catch(Exception e){
                                e.printStackTrace();
                            }
                        }else {
                            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(activity);
                            alertDialogBuilder.setTitle("KlikIndomaret");
                            alertDialogBuilder.setMessage("Silahkan Login terlebih dahulu");
                            alertDialogBuilder.setNegativeButton("Tutup", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface arg0, int arg1) {}
                            });

                            AlertDialog alertDialog = alertDialogBuilder.create();
                            alertDialog.setCanceledOnTouchOutside(false);
                            alertDialog.show();

                            if (from.equals("cat")){
                                ((CategoryActivity) activity).stopPreloader();
                            }else if (from.equals("catByLevel")){
                                ((CategoryLevel1Activity) activity).stopPreloader();
                            }
                        }
                    }
                }
        );

        //buy product function
        buyButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (from.equals("cat")){
                            ((CategoryActivity) activity).runPreloader();
                        }else if (from.equals("catByLevel")){
                            ((CategoryLevel1Activity) activity).runPreloader();
                        }

                        try {
                            String variant = "";

                            if (product.getString("Color").equals(null) || product.getString("Color").equals("null")){
                                variant = product.getString("Flavour");
                            }else{
                                variant = product.getString("Color");
                            }

                            Product productModel =  new Product()
                                    .setId(product.getString("ID"))
                                    .setName(product.getString("Description"))
                                    .setBrand(product.getJSONObject("ProductBrand").getString("Name"))
                                    .setVariant(variant)
                                    .setPosition(1);

                            ProductAction productAction = new ProductAction(ProductAction.ACTION_ADD)
                                    .setProductActionList("Search Results");

                            HitBuilders.ScreenViewBuilder builder = new HitBuilders.ScreenViewBuilder()
                                    .addProduct(productModel)
                                    .setProductAction(productAction);

                            AppController application = (AppController) activity.getApplication();
                            Tracker t = application.getDefaultTracker();
                            t.setScreenName("add product");
                            t.send(builder.build());

                            Bundle parameters = new Bundle();
                            parameters.putString(AppEventsConstants.EVENT_PARAM_DESCRIPTION, product.getString("Description"));
                            parameters.putString(AppEventsConstants.EVENT_PARAM_CONTENT_ID, product.getString("ID"));

                            AppEventsLogger logger = AppEventsLogger.newLogger(activity);
                            logger.logEvent(AppEventsConstants.EVENT_NAME_ADDED_TO_CART, parameters);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        if(sessionManager.isLoggedIn()){
                            if(sessionManager.getCartId() == null){
                                try {
                                    arrayRequest(API.getInstance().getApiModifyCart()
                                            +"?cartRef=mobile"
                                            +"&pId=" + product.getString("ID")
                                            +"&mod=add"
                                            +"&qty=1"
                                            +"&regionID=" + sessionManager.getRegionID()
                                            +"&id="
                                            +"&scId=00000000-0000-0000-0000-000000000000"
                                            +"&cId=" + sessionManager.getUserID()
                                            +"&isPair=false"
                                            +"&mfp_id=" + sessionManager.getKeyMfpId(), "modify");

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            } else {
                                try {
                                    arrayRequest(API.getInstance().getApiModifyCart()
                                            +"?cartRef=mobile"
                                            +"&pId=" + product.getString("ID")
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
                        } else {
                            if(sessionManager.getCartId() == null){
                                try {
                                    arrayRequest(API.getInstance().getApiModifyCart()
                                            +"?cartRef=mobile"
                                            +"&pId=" + product.getString("ID")
                                            +"&mod=add"
                                            +"&qty=1"
                                            +"&regionID=" + sessionManager.getRegionID()
                                            +"&id="
                                            +"&scId=00000000-0000-0000-0000-000000000000"
                                            +"&cId=00000000-0000-0000-0000-000000000000"
                                            +"&isPair=false"
                                            +"&mfp_id=" + sessionManager.getKeyMfpId(), "modify");
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            } else {
                                try {
                                    arrayRequest(API.getInstance().getApiModifyCart()
                                            +"?cartRef=mobile"
                                            +"&pId=" + product.getString("ID")
                                            +"&mod=add"
                                            +"&qty=1"
                                            +"&regionID=" + sessionManager.getRegionID()
                                            +"&id="
                                            +"&scId=" + sessionManager.getCartId()
                                            +"&cId=00000000-0000-0000-0000-000000000000"
                                            +"&isPair=false"
                                            +"&mfp_id=" + sessionManager.getKeyMfpId(), "modify");
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                }
        );

        return convertView;
    }

    //ARRAY REQUEST
    public void arrayRequest(final String url, final String type) {
        System.out.println("add to cart = " + url);

        JsonArrayRequest jsonArrayReq = new JsonArrayRequest(url,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        if (response == null || response.length() == 0){
                            Toast.makeText(activity, "Gagal terkoneksi server", Toast.LENGTH_LONG).show();
                        }else{
                            if (type.equals("modify")){
                                modifyResponse(response);
                            } else if (type.equals("cart")){
                                cartTotalResponse(response);
                            }
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(activity, "Gagal terkoneksi server", Toast.LENGTH_LONG).show();
            }
        }, activity);

        AppController.getInstance().addToRequestQueue(jsonArrayReq);
    }

    public void modifyResponse(JSONArray response){
        if (sessionManager.getCartId() == null || sessionManager.getCartId().equals("00000000-0000-0000-0000-000000000000")){
            try {
                sessionManager.setCartId(response.getJSONObject(0).getString("ResponseID"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        try {
            JSONObject cart = response.getJSONObject(0);

            if(cart.getBoolean("Success")) {
                arrayRequest(API.getInstance().getCartTotal()
                        +"?cartId=" + sessionManager.getCartId()
                        +"&customerId=" + sessionManager.getUserID()
                        +"&isVirtual=false"
                        +"&mfp_id=" + sessionManager.getKeyMfpId() , "cart");

                final Toast toast = Toast.makeText(activity, "Produk masuk ke keranjang", Toast.LENGTH_SHORT);
                toast.show();

                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        toast.cancel();
                    }
                }, 500);

                if (from.equals("cat")){
                    ((CategoryActivity) activity).stopPreloader();
                }else if (from.equals("catByLevel")){
                    ((CategoryLevel1Activity) activity).stopPreloader();
                }
            } else {
                if (from.equals("cat")){
                    ((CategoryActivity) activity).stopPreloader();
                }else if (from.equals("catByLevel")){
                    ((CategoryLevel1Activity) activity).stopPreloader();
                }

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(activity);
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

    public void cartTotalResponse(JSONArray response){
        try {
            if (from.equals("cat")){
                ((CategoryActivity) activity).updateCartTotal(response.getInt(0));
                ((CategoryActivity) activity).stopPreloader();
            }else if(from.equals("catByLevel")){
                ((CategoryLevel1Activity) activity).updateCartTotal(response.getInt(0));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
