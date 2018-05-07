package com.indomaret.klikindomaret.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.indomaret.klikindomaret.R;
import com.indomaret.klikindomaret.helper.Month;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by USER on 8/25/2017.
 */

public class TrackingHistoryAdapter extends BaseAdapter {
    private Activity activity;
    private LayoutInflater inflater;
    private JSONArray trackingList;
    private Month month = new Month();

    public TrackingHistoryAdapter(Activity activity, JSONArray trackingList){
        this.activity = activity;
        this.trackingList = trackingList;
    }

    @Override
    public int getCount() {
        return trackingList.length();
    }

    @Override
    public Object getItem(int position) {
        try {
            return trackingList.getJSONObject(position);
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

        if (convertView == null)
            convertView = inflater.inflate(R.layout.item_tracking, null);

        TextView tvStatus = (TextView) convertView.findViewById(R.id.tvStatus);
        TextView tvDate = (TextView) convertView.findViewById(R.id.tvDate);

        try {
            final JSONObject productObject = trackingList.getJSONObject(position);
            tvStatus.setText(productObject.getString("StatusDesc"));
            tvDate.setText(setShownDate(productObject.getString("ShippingDate")) + " WIB");

        }catch (Exception e){

        }

        return convertView;
    }

    private String setShownDate(final String stringDate){
        String result = "";

        if(!stringDate.isEmpty()) {
            String[] splitDateTime = stringDate.split("T");
            if(splitDateTime.length > 0) {
                String[] splitDate = splitDateTime[0].split("-");
                month = new Month();

                String[] splitTime = splitDateTime[1].split(":");
                result = splitDate[2] + " " + month.getMonth(splitDate[1]) + " " + splitDate[0] + " " + splitTime[0] + ":" + splitTime[1];
            }
        }

        return result;
    }

}
