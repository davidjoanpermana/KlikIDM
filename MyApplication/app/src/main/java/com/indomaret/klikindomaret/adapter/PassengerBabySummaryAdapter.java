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
 * Created by indomaretitsd7 on 6/14/16.
 */
public class PassengerBabySummaryAdapter extends BaseAdapter{
    private LayoutInflater inflater;
    private Activity activity;
    private JSONArray infantList;

    public PassengerBabySummaryAdapter(Activity activity, JSONArray infantList){
        this.activity = activity;
        this.infantList = infantList;
    }

    @Override
    public int getCount() {
        return infantList.length();
    }

    @Override
    public Object getItem(int position) {
        try {
            return infantList.getString(position);
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
            convertView = inflater.inflate(R.layout.list_passenger_baby_summary, null);

        JSONObject passenger = new JSONObject();
        try {
            passenger = infantList.getJSONObject(position);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        TextView passengerName = (TextView) convertView.findViewById(R.id.passenger_name);
        TextView trainInfo = (TextView) convertView.findViewById(R.id.train_info);

        try {
            passengerName.setText(passenger.getString("Name") + " (Bayi)");
            trainInfo.setVisibility(View.GONE);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return convertView;
    }
}