package com.indomaret.klikindomaret.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.indomaret.klikindomaret.R;
import com.indomaret.klikindomaret.activity.FilterActivity;

import java.util.ArrayList;

/**
 * Created by indomaretitsd7 on 6/18/16.
 */
public class BrandFilterAdapter extends BaseAdapter {
    private LayoutInflater inflater;
    private Activity activity;
    private String[] brandArray;
    private ArrayList<Integer> brandIndex;
    private String search;
    private String type;

    public BrandFilterAdapter(Activity activity, String[] brandArray, ArrayList<Integer> brandIndex, String search, String type){
        this.activity = activity;
        this.brandArray = brandArray;
        this.brandIndex = brandIndex;
        this.search = search;
        this.type = type;
    }

    @Override
    public int getCount() {
        return brandArray.length;
    }

    @Override
    public Object getItem(int position) {
        return brandArray[position];
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
            convertView = inflater.inflate(R.layout.brand_filter_single_item, null);

        TextView brandName = (TextView) convertView.findViewById(R.id.brand_name);
        final ImageView checkBrand = (ImageView) convertView.findViewById(R.id.check_brand);
        LinearLayout linearLayoutBrand = (LinearLayout) convertView.findViewById(R.id.linear_brand);

        brandName.setText(brandArray[position]);

        if(brandIndex != null) {
            if (brandIndex.contains(position)) {
                checkBrand.setImageResource(R.drawable.checkbox_filled);
            } else {
                checkBrand.setImageResource(R.drawable.checkbox_null);
            }
        }else{
            checkBrand.setImageResource(R.drawable.checkbox_null);
        }

        convertView.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(type == "brand"){
                            //brandIndex.add(position);
                            ((FilterActivity) activity).setBrandIndex(position);
                        }else{
                            //brandIndex.add(position);
                            ((FilterActivity) activity).setCategoryId(position);
                        }
                    }
                }
        );

        if(brandArray[position].toLowerCase().contains(search.toLowerCase())){
            convertView.setVisibility(View.VISIBLE);
            linearLayoutBrand.setVisibility(View.VISIBLE);
        } else {
            convertView.setVisibility(View.GONE);
            linearLayoutBrand.setVisibility(View.GONE);
        }

        return convertView;
    }
}
