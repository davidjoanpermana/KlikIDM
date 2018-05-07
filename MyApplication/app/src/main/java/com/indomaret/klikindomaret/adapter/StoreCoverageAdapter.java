package com.indomaret.klikindomaret.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.indomaret.klikindomaret.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by USER on 5/30/2016.
 */
public class StoreCoverageAdapter extends BaseAdapter {
    private LayoutInflater inflater;
    private Activity activity;
    private JSONArray data;

    public StoreCoverageAdapter(Activity activity, JSONArray data){
        this.activity = activity;
        this.data = data;
    }

    @Override
    public int getCount() {
        return data.length();
    }

    @Override
    public Object getItem(int position) {
        try {
            return data.getJSONObject(position);
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
        if (inflater == null)
            inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if(convertView == null){
            convertView = inflater.inflate(R.layout.activity_postcode_coverage_single_store_item, null);
        }

        TextView storeName = (TextView) convertView.findViewById(R.id.store_coverage_name);
        TextView zipcode = (TextView) convertView.findViewById(R.id.store_coverage_zipcode);
        TextView storeCode = (TextView) convertView.findViewById(R.id.store_coverage_code);
        TextView storePhone = (TextView) convertView.findViewById(R.id.store_coverage_phone);

        try {
            JSONObject object = data.getJSONObject(position);
            String store = object.getString("Store");
            JSONObject object1 = new JSONObject(store);

            storeName.setText( object1.getString("Name"));
            zipcode.setText("Kode Pos : " + object1.getString("ZipCode"));
            storeCode.setText("Kode Toko : " + object1.getString("Code"));
            storePhone.setText("Telepon : " + object1.getString("Phone"));

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return convertView;
    }
}
