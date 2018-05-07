package com.indomaret.klikindomaret.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
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
public class PassengerPaymentKAIAdapter extends BaseAdapter{
    private LayoutInflater inflater;
    private Activity activity;
    private JSONArray passengerList;
    private JSONObject passengerObject;
    private String Maturity;

    public PassengerPaymentKAIAdapter(Activity activity, JSONArray passengerList){
        this.activity = activity;
        this.passengerList = passengerList;
    }

    @Override
    public int getCount() {
        return passengerList.length();
    }

    @Override
    public Object getItem(int position) {
        try {
            return passengerList.getString(position);
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

        TextView passengerName = (TextView) convertView.findViewById(R.id.passenger_name);
        TextView trainInfo = (TextView) convertView.findViewById(R.id.train_info);

        trainInfo.setVisibility(View.GONE);
        passengerName.setTypeface(Typeface.DEFAULT);
        try {
            passengerObject = new JSONObject();
            passengerObject = passengerList.getJSONObject(position);
            if (passengerObject.getString("Maturity").equals("0")){
                Maturity = "Dewasa";
            }else{
                Maturity = "Bayi";
            }

            passengerName.setText(passengerObject.getString("Name") + " (" + Maturity + ")");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return convertView;
    }
}