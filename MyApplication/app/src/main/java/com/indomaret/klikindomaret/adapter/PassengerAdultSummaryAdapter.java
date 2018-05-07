package com.indomaret.klikindomaret.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.indomaret.klikindomaret.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by indomaretitsd7 on 6/14/16.
 */
public class PassengerAdultSummaryAdapter extends BaseAdapter{
    private LayoutInflater inflater;
    private JSONArray adultList;
    private Activity activity;

    public PassengerAdultSummaryAdapter(Activity activity, JSONArray adultList){
        this.activity = activity;
        this.adultList = adultList;
    }

    @Override
    public int getCount() {
        return adultList.length();
    }

    @Override
    public Object getItem(int position) {
        try {
            return adultList.getString(position);
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

        JSONObject passenger = new JSONObject();
        try {
            passenger = adultList.getJSONObject(position);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (convertView == null)
            convertView = inflater.inflate(R.layout.list_passenger_adult_summary, null);

        TextView passengerName = (TextView) convertView.findViewById(R.id.passenger_name);
        TextView trainInfo = (TextView) convertView.findViewById(R.id.infant_info);
        LinearLayout linearLayout = (LinearLayout) convertView.findViewById(R.id.linear_passenger);

        linearLayout.setVisibility(View.GONE);

        try {
            passengerName.setText(passenger.getString("Name") + " (Dewasa)");
            trainInfo.setText(passenger.getString("wagonName") + "  " + passenger.getString("wagonNumber") + "/ Kursi " +passenger.getString("SeatRow") + passenger.getString("SeatPotition"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return convertView;
    }
}