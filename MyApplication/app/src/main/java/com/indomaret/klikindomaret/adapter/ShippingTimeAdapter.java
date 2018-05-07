package com.indomaret.klikindomaret.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.indomaret.klikindomaret.R;
import com.indomaret.klikindomaret.activity.ShippingListTimeActivity;
import com.indomaret.klikindomaret.app.AppController;
import com.indomaret.klikindomaret.helper.SessionManager;

import org.json.JSONArray;
import org.json.JSONException;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by indomaretitsd7 on 6/21/16.
 */
public class ShippingTimeAdapter extends BaseAdapter {
    private LayoutInflater inflater;
    private SessionManager sessionManager;
    private Activity activity;
    private JSONArray times;
    private String time, serverTime;
    private String index = "";
    private JSONArray slotPengiriman = new JSONArray();
    private boolean currentDay;

    ImageLoader imageLoader = AppController.getInstance().getImageLoader();

    public ShippingTimeAdapter(Activity activity, JSONArray times, String serverTime, boolean currentDay, String slotPengiriman){
        this.activity = activity;
        this.times = times;
        this.serverTime = serverTime;
        this.currentDay = currentDay;

        try {
            this.slotPengiriman = new JSONArray(slotPengiriman);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getCount() {
        return times.length();
    }

    @Override
    public Object getItem(int position) {
        try {
            return times.getJSONObject(position);
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
            convertView = inflater.inflate(R.layout.activity_shipping_time_detail, null);

        sessionManager = new SessionManager(activity);
        final LinearLayout linear = (LinearLayout) convertView.findViewById(R.id.linear);
        final TextView date = (TextView) convertView.findViewById(R.id.time_text);
        final TextView infoTime = (TextView) convertView.findViewById(R.id.info_time);

        try {
            date.setText(times.getJSONObject(position).getString("SlotLabel"));

            for (int i=0; i<slotPengiriman.length(); i++){
                if (i == position){
                    if (slotPengiriman.getJSONObject(i).getBoolean("IsActive")){
                        infoTime.setText("");
                        linear.setEnabled(true);
                        linear.setAlpha((float) 1);
                    }else{
                        linear.setEnabled(false);
                        linear.setAlpha((float) 0.2);
                        if (slotPengiriman.getJSONObject(i).getString("Message") != null &&
                                slotPengiriman.getJSONObject(i).getString("Message").length() > 0 &&
                                !slotPengiriman.getJSONObject(i).getString("Message").toLowerCase().equals("null")){
                            infoTime.setText(slotPengiriman.getJSONObject(i).getString("Message"));
                        }else{
                            infoTime.setText("");
                        }
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        linear.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            ((ShippingListTimeActivity) activity).setData(times.getJSONObject(position).getString("SlotLabel"),
                                    times.getJSONObject(position).getString("ExpiredDate"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
        );

        return convertView;
    }
}
