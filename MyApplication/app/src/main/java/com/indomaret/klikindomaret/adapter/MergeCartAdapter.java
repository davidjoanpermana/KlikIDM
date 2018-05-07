package com.indomaret.klikindomaret.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.indomaret.klikindomaret.R;
import com.indomaret.klikindomaret.app.AppController;
import com.indomaret.klikindomaret.views.NetworkImagesView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by indomaretitsd7 on 6/18/16.
 */
public class MergeCartAdapter extends BaseAdapter {
    private LayoutInflater inflater;
    private Activity activity;
    private JSONArray cartObjectList;

    private ImageLoader imageLoader = AppController.getInstance().getImageLoader();

    public MergeCartAdapter(Activity activity, JSONArray cartObjectList){
        this.activity = activity;
        this.cartObjectList = cartObjectList;
    }

    @Override
    public int getCount() {
        return cartObjectList.length();
    }

    @Override
    public Object getItem(int position) {
        try {
            return cartObjectList.getJSONObject(position);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @SuppressWarnings("deprecation")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (inflater == null)
            inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (convertView == null)
            convertView = inflater.inflate(R.layout.activity_merge_cart_product, null);


        NetworkImagesView cartImage = (NetworkImagesView) convertView.findViewById(R.id.cart_image);
        TextView cartProductName = (TextView) convertView.findViewById(R.id.cart_product_name);
        TextView cartQty = (TextView) convertView.findViewById(R.id.cart_quantity);

        try {
            JSONObject cartObject = cartObjectList.getJSONObject(position);
            cartQty.setText("Kuantitas : " + cartObject.getString("Quantity"));

            if (cartObject.getJSONObject("Product").getString("Title") != null){
                cartProductName.setText(cartObject.getJSONObject("Product").getString("Title"));
            }else {
                cartProductName.setText(cartObject.getJSONObject("Product").getString("MetaTitle"));
            }

            cartImage.setImageUrl(cartObject.getJSONObject("Product").getString("ImageThumb"), imageLoader);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return convertView;
    }
}
