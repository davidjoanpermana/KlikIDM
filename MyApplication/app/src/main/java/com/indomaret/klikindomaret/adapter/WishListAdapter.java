package com.indomaret.klikindomaret.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
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
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.NetworkImageView;
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
import com.indomaret.klikindomaret.activity.WishlistActivity;
import com.indomaret.klikindomaret.app.AppController;
import com.indomaret.klikindomaret.helper.SessionManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by USER on 4/12/2016.
 */
public class WishListAdapter extends BaseAdapter {
    private SessionManager sessionManager;
    private Activity activity;
    private LayoutInflater inflater;

    private ImageLoader imageLoader = AppController.getInstance().getImageLoader();

    private List<JSONObject> productList = new ArrayList<>();
    private String sendDesc;

    private DecimalFormat df = new DecimalFormat("#,###");

    public WishListAdapter(Activity activity, List<JSONObject> productList){
        this.activity = activity;
        this.productList = productList;
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
            convertView = inflater.inflate(R.layout.activity_wishlist_single_item, null);

        if (imageLoader == null)
            imageLoader = AppController.getInstance().getImageLoader();

        sessionManager = new SessionManager(activity);

        NetworkImageView productImage = (NetworkImageView) convertView.findViewById(R.id.product_image_category);

        TextView productSendFrom = (TextView) convertView.findViewById(R.id.product_sender);
        TextView productName = (TextView) convertView.findViewById(R.id.product_name);
        TextView productDiscount = (TextView) convertView.findViewById(R.id.product_discount);
        TextView productPrice = (TextView) convertView.findViewById(R.id.product_price);
        TextView productInstallment = (TextView) convertView.findViewById(R.id.product_installment);
        TextView productInitialPrice = (TextView) convertView.findViewById(R.id.product_initial_price);
        LinearLayout buyButton = (LinearLayout) convertView.findViewById(R.id.btn_category_buy);
        TextView btnText = (TextView) convertView.findViewById(R.id.button_text);


        final ImageView imageWishlist = (ImageView) convertView.findViewById(R.id.wishlist_checklist);
        LinearLayout linearWishlist = (LinearLayout) convertView.findViewById(R.id.linear_wishlist);
        final RelativeLayout relativeLayout = (RelativeLayout) convertView.findViewById(R.id.single_wishlist_rl);

        try {
            if(product.getString("IsSoldOut") == "true"){
                btnText.setText("HABIS");
                buyButton.setBackgroundResource(R.drawable.button_style_4);
            } else {
                btnText.setText("BELI");
                buyButton.setBackgroundResource(R.drawable.button_style_1);
            }

            if (product.getBoolean("selected")){
                imageWishlist.setVisibility(View.VISIBLE);
                relativeLayout.setBackgroundResource(R.drawable.card_product_style_2);
            }else{
                imageWishlist.setVisibility(View.GONE);
                relativeLayout.setBackgroundResource(R.drawable.card_product_style_1);
            }

            if (product.getString("Title").length() > 0 && !product.getString("Title").equals("null") ){
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
            Double validPrice = Double.parseDouble(product.getString("HargaWebsite"));
            Double discount = Double.parseDouble(product.getString("Discount"));
            Double price = validPrice - discount;
            productPrice.setText("Rp " + df.format(price).replace(",", "."));

            if(!product.getString("IsInstallment").equals("null")){
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

            String badgeArray = product.getString("BadgeTag");

            if(badgeArray.length() != 0){
                JSONArray jsonArray = new JSONArray(badgeArray);
                productDiscount.setText(jsonArray.getJSONObject(0).getString("BadgeDesc"));
                productDiscount.setVisibility(View.VISIBLE);
            } else {
                productDiscount.setVisibility(View.GONE);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        linearWishlist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (imageWishlist.getVisibility() == View.VISIBLE){
                    try {
                        productList.get(position).put("selected", false);
                        ((WishlistActivity)activity).setItem(productList);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }else{
                    try {
                        productList.get(position).put("selected", true);
                        ((WishlistActivity)activity).setItem(productList);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        buyButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if(sessionManager.isLoggedIn()){
                            if(sessionManager.getCartId() == null){
                                try {
                                    ((WishlistActivity)activity).jsonArrayRequest(API.getInstance().getApiModifyCart()
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
                                    ((WishlistActivity)activity).jsonArrayRequest(API.getInstance().getApiModifyCart()
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
                                    ((WishlistActivity)activity).jsonArrayRequest(API.getInstance().getApiModifyCart()
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
                                    ((WishlistActivity)activity).jsonArrayRequest(API.getInstance().getApiModifyCart()
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
}
