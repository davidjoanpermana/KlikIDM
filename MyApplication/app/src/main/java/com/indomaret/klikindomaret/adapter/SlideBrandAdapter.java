package com.indomaret.klikindomaret.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.indomaret.klikindomaret.API;
import com.indomaret.klikindomaret.R;
import com.indomaret.klikindomaret.activity.CategoryActivity;
import com.indomaret.klikindomaret.app.AppController;
import com.indomaret.klikindomaret.views.NetworkImagesView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by indomaretitsd7 on 6/14/16.
 */
public class SlideBrandAdapter extends BaseAdapter{
    private LayoutInflater inflater;
    private Intent intent;
    private Activity activity;
    private JSONArray brandList;
    private ImageLoader imageLoader = AppController.getInstance().getImageLoader();
    private JSONObject objectDataBrand = new JSONObject();
    private String from = "";

    public SlideBrandAdapter(Activity activity, JSONArray brandList, String from){
        this.activity = activity;
        this.brandList = brandList;
        this.from = from;
    }

    @Override
    public int getCount() {
        return brandList.length();
    }

    @Override
    public Object getItem(int position) {
        try {
            return brandList.getJSONObject(position);
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @SuppressWarnings("deprecation")
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (inflater == null)
            inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (convertView == null)
            convertView = inflater.inflate(R.layout.brand_item, null);

        try {
            objectDataBrand = brandList.getJSONObject(position);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        TextView brandName = (TextView) convertView.findViewById(R.id.brand_name);
        NetworkImagesView brandImage = (NetworkImagesView) convertView.findViewById(R.id.brand_image);
        intent = activity.getIntent();

        try {
            if (from.equals("main")){
                brandImage.setVisibility(View.VISIBLE);
                brandName.setVisibility(View.GONE);
                brandImage.setImageUrl(API.getInstance().getAssetsUrl() + objectDataBrand.getString("ImageUrl").replace(" ","%20"), imageLoader);
            } else if (from.equals("cat")){
                brandImage.setVisibility(View.GONE);
                brandName.setVisibility(View.VISIBLE);
                brandName.setText(objectDataBrand.getString("Name"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        brandImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (brandList.getJSONObject(position).getString("Name").length() > 1) {
                        intent = new Intent(activity, CategoryActivity.class);
                        intent.putExtra("cat", "brand");
                        intent.putExtra("brand", brandList.getJSONObject(position).toString());
                        activity.startActivity(intent);
                        activity.overridePendingTransition(R.anim.right_in, R.anim.right_out);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        return convertView;
    }
}