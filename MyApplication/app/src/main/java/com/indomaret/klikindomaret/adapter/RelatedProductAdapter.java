package com.indomaret.klikindomaret.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Paint;
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
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.android.gms.analytics.ecommerce.Product;
import com.google.android.gms.analytics.ecommerce.ProductAction;
import com.indomaret.klikindomaret.API;
import com.indomaret.klikindomaret.R;
import com.indomaret.klikindomaret.activity.CategoryActivity;
import com.indomaret.klikindomaret.activity.ProductActivity;
import com.indomaret.klikindomaret.app.AppController;
import com.indomaret.klikindomaret.helper.SessionManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by USER on 6/7/2016.
 */
public class RelatedProductAdapter extends BaseAdapter {
    private Activity activity;
    private SessionManager sessionManager;
    private LayoutInflater inflater;
    private JSONArray relatedProduct;
    private ImageLoader imageLoader = AppController.getInstance().getImageLoader();
    private JSONObject object;
    private DecimalFormat df = new DecimalFormat("#,###");
    private String badgeArray;

    public RelatedProductAdapter(Activity activity, JSONArray relatedProduct){
        this.activity = activity;
        this.relatedProduct = relatedProduct;
    }

    @Override
    public int getCount() {
        return relatedProduct.length();
    }

    @Override
    public Object getItem(int position) {
        try {
            return relatedProduct.getJSONObject(position);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        try {
            object = relatedProduct.getJSONObject(position);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (inflater == null)
            inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (convertView == null)
            convertView = inflater.inflate(R.layout.activity_product_related_single, null);

        if (imageLoader == null)
            imageLoader = AppController.getInstance().getImageLoader();

        sessionManager = new SessionManager(activity);

        NetworkImageView relatedProductImage = (NetworkImageView) convertView.findViewById(R.id.related_product_image);
        TextView relatedProductName = (TextView) convertView.findViewById(R.id.related_product_name);
        TextView relatedProductPrice = (TextView) convertView.findViewById(R.id.related_product_price);

        TextView productSendFrom = (TextView) convertView.findViewById(R.id.product_sender);
        ImageView sendToWishList = (ImageView) convertView.findViewById(R.id.product_send_to_wishlist);
        TextView productDiscount = (TextView) convertView.findViewById(R.id.product_discount);
        TextView productInstallment = (TextView) convertView.findViewById(R.id.product_installment);
        TextView productInitialPrice = (TextView) convertView.findViewById(R.id.product_initial_price);
        LinearLayout buyButton = (LinearLayout) convertView.findViewById(R.id.btn_category_buy);
        LinearLayout soldoutButton = (LinearLayout) convertView.findViewById(R.id.btn_category_soldout);
        LinearLayout soldoutButtonPromo = (LinearLayout) convertView.findViewById(R.id.btn_category_soldout_promo);

        try {
            String[] imageName = object.getString("ImageThumb").split("_");
            Double hargaWebsite = Double.valueOf(object.getString("HargaWebsite"));
            Double discount = Double.valueOf(object.getString("Discount"));
            Double price = hargaWebsite - discount;

            String imageUrl = API.getInstance().getAssetsUrl()+"products/"+imageName[0]+"/"+object.getString("ImageThumb");
            System.out.println("image url = " + imageUrl);

            relatedProductImage.setImageUrl(imageUrl, imageLoader);
            relatedProductName.setText(object.getString("Title"));
            relatedProductPrice.setText("Rp " + df.format(price).replace(",","."));
            productSendFrom.setText(object.getString("SendDesc"));

            if(!discount.equals(0.0)){
                productInitialPrice.setText("Rp " + df.format(hargaWebsite).replace(",","."));
                productInitialPrice.setVisibility(View.VISIBLE);
                productInitialPrice.setPaintFlags(productInitialPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            } else {
                productInitialPrice.setVisibility(View.GONE);
            }

            if (object.getString("BadgeTag").equals("") || object.getString("BadgeTag") == null || object.getString("BadgeTag").equals("null")){
                badgeArray = "";
            }else{
                badgeArray = object.getString("BadgeTag");
            }

            if(badgeArray.length() != 0){
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
            } else {
                productDiscount.setVisibility(View.GONE);
            }

            if(!object.getString("IsInstallment").equals("null")){
                productInstallment.setText("Cicilan 0%");
                productInstallment.setVisibility(View.VISIBLE);
            } else {
                productInstallment.setVisibility(View.GONE);
            }

            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String[] stringSplit = object.getString("EndDatePromo").split("T");

            Date endDatePromo = null;
            try {
                endDatePromo = formatter.parse(stringSplit[0] + " " + stringSplit[1]);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            if (object.getString("IsSoldOut") == "false" && object.getString("IsHideAddToCart") == "true"){
                buyButton.setVisibility(View.GONE);
                soldoutButton.setVisibility(View.GONE);
                soldoutButtonPromo.setVisibility(View.VISIBLE);
            } else if(object.getString("IsSoldOut") == "true"){
                buyButton.setVisibility(View.GONE);
                soldoutButton.setVisibility(View.VISIBLE);
                soldoutButtonPromo.setVisibility(View.GONE);
            } else if(object.getInt("TotalTransactionQuota") > 0 && object.getInt("MaxTransactionCMS") > 0
                    && object.getInt("TotalTransactionQuota") >= object.getInt("MaxTransactionCMS") && endDatePromo.before(new Date())){
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

        sendToWishList.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ((ProductActivity) activity).runPreloader();
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
                                productWishlist.put("ProductID", object.getString("ID"));
                                productWishlist.put("Description", "");
                                productWishlist.put("Created", "");

                                productArray.put(productWishlist);
                                customer.put("WishListItems", productArray);

                                jsonPost(API.getInstance().getApiAddWishlist()
                                        +"?mfp_id=" + sessionManager.getKeyMfpId()
                                        +"&regionID=" + sessionManager.getRegionID(), customer);
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

                            ((CategoryActivity) activity).stopPreloader();
                        }
                    }
                }
        );

        buyButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            ((ProductActivity) activity).runPreloader();
                            String variant = "";

                            if (object.getString("Color").equals(null) || object.getString("Color").equals("null")){
                                variant = object.getString("Flavour");
                            }else{
                                variant = object.getString("Color");
                            }

                            Product productModel =  new Product()
                                    .setId(object.getString("ID"))
                                    .setName(object.getString("Description"))
                                    .setBrand(object.getJSONObject("ProductBrand").getString("Name"))
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
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        if(sessionManager.isLoggedIn()){
                            if(sessionManager.getCartId() == null){
                                try {
                                    arrayRequest(API.getInstance().getApiModifyCart()
                                            +"?cartRef=mobile"
                                            +"&pId=" + object.getString("ID")
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
                                            +"&pId=" + object.getString("ID")
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
                                            +"&pId=" + object.getString("ID")
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
                                            +"&pId=" + object.getString("ID")
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

    //POST REQUEST
    public void jsonPost(String urlJsonObj, JSONObject jsonObject){
        System.out.println("wishlist = " + urlJsonObj);
        System.out.println("wishlist = " + jsonObject);

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                urlJsonObj, jsonObject,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        if (response == null || response.length() == 0){
                            ((CategoryActivity) activity).stopPreloader();
                            Toast.makeText(activity, "Gagal terkoneksi server", Toast.LENGTH_LONG).show();
                        }else{
                            addWishListResponse(response);
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(activity, "Gagal terkoneksi server", Toast.LENGTH_LONG).show();
                ((CategoryActivity) activity).stopPreloader();
            }
        });
        AppController.getInstance().addToRequestQueue(jsonObjReq);
    }

    public void addWishListResponse(JSONObject response){
        ((CategoryActivity) activity).stopPreloader();
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(activity);

        try{
            if(response.getBoolean("IsSuccess")){
                ((ProductActivity) activity).stopPreloader();

                if(response.getString("Message").equals("success")){
                    alertDialogBuilder.setMessage("Berhasil menambahkan produk ke Wishlist");
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

    //ARRAY REQUEST
    public void arrayRequest(final String url, final String type) {
        System.out.println("add to cart = " + url);

        JsonArrayRequest jsonArrayReq = new JsonArrayRequest(url,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        if (response == null || response.length() == 0){
                            ((CategoryActivity) activity).stopPreloader();
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
                ((CategoryActivity) activity).stopPreloader();
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
            } else {
                ((CategoryActivity) activity).stopPreloader();
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
            ((ProductActivity) activity).updateCartTotal(response.getInt(0));
            ((ProductActivity) activity).stopPreloader();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
