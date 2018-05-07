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
public class PassengerOrderSummaryAdapter extends BaseAdapter{
    private LayoutInflater inflater;
    private JSONArray adultList;
    private Activity activity;
    private String status = "";
    private boolean scheduleDestination;

    public PassengerOrderSummaryAdapter(Activity activity, JSONArray adultList, boolean scheduleDestination){
        this.activity = activity;
        this.adultList = adultList;
        this.scheduleDestination = scheduleDestination;
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
        TextView identitas = (TextView) convertView.findViewById(R.id.identitas);
        TextView oriTrainInfo = (TextView) convertView.findViewById(R.id.original_train_info);
        TextView desTrainInfo = (TextView) convertView.findViewById(R.id.destination_train_info);
        LinearLayout linearDestination = (LinearLayout) convertView.findViewById(R.id.linear_destination);
        LinearLayout linearInfo = (LinearLayout) convertView.findViewById(R.id.linear_passenger);
        TextView infantInfo = (TextView) convertView.findViewById(R.id.infant_info);

        infantInfo.setVisibility(View.GONE);
        try {
            if (passenger.getString("Maturity").equals("0")){
                status = "Dewasa";
                linearInfo.setVisibility(View.VISIBLE);
                identitas.setText(passenger.getString("Identity"));
            }else{
                status = "Bayi";
                linearInfo.setVisibility(View.GONE);
            }

            passengerName.setText((position+1) + ". " + passenger.getString("Name") + " (" + status + ")");
            oriTrainInfo.setText(passenger.getString("PergiWagonName") + "  " + passenger.getString("PergiWagonNumber") + "/ Kursi " +passenger.getString("PergiSeatRow") + passenger.getString("PergiSeatPotition"));
            if (scheduleDestination){
                linearDestination.setVisibility(View.VISIBLE);
                desTrainInfo.setText(passenger.getString("PulangWagonName") + "  " + passenger.getString("PulangWagonNumber") + "/ Kursi " +passenger.getString("PulangSeatRow") + passenger.getString("PulangSeatPotition"));
            }else{
                linearDestination.setVisibility(View.GONE);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return convertView;
    }
}