package com.indomaret.klikindomaret.adapter;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.indomaret.klikindomaret.R;
import com.indomaret.klikindomaret.helper.SessionManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * Created by indomaretitsd7 on 6/22/16.
 */
public class ShippingTimeStoreListAdapter extends BaseAdapter {
    private LayoutInflater inflater;
    private Activity activity;
    private List<JSONObject> data;
    private SessionManager sessionManager;

    public ShippingTimeStoreListAdapter(Activity activity, List<JSONObject> data){
        this.activity = activity;
        this.data = data;
        this.sessionManager = new SessionManager(activity);
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {

        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        System.out.println("loop = " + position);

        if (inflater == null)
            inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if(convertView == null){
            convertView = inflater.inflate(R.layout.activity_shipping_time_store, null);
        }

        TextView storeName = (TextView) convertView.findViewById(R.id.store_name);
//        TextView storeArea = (TextView) convertView.findViewById(R.id.store_area);
        TextView storeAddress = (TextView) convertView.findViewById(R.id.store_address);
        TextView storeZipcode = (TextView) convertView.findViewById(R.id.store_zipcode);

        try {
            JSONObject object = data.get(position);

            Log.d("Region Name", object.toString());
            storeName.setText(object.getString("Name"));
//            storeArea.setText(object.getString("District"));
            storeAddress.setText(object.getString("Street"));
            storeZipcode.setText(object.getString("ZipCode"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return convertView;
    }
}
