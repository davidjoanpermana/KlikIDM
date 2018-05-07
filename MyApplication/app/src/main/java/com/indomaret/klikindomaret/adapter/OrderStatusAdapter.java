package com.indomaret.klikindomaret.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.indomaret.klikindomaret.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * Created by indomaretitsd7 on 6/15/16.
 */
public class OrderStatusAdapter extends BaseAdapter {
    private LayoutInflater inflater;
    private Activity activity;
    private List<JSONObject> orderStatusList;

    public OrderStatusAdapter (Activity activity, List<JSONObject> orderStatusList){
        this.activity = activity;
        this.orderStatusList = orderStatusList;
    }

    @Override
    public int getCount() {
        return orderStatusList.size();
    }

    @Override
    public Object getItem(int position) {
        return orderStatusList.get(position);
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
            convertView = inflater.inflate(R.layout.fragment_order_status_single, null);

        TextView orderCode = (TextView) convertView.findViewById(R.id.status_order_code);

        try {
            orderCode.setText(orderStatusList.get(position).getString("SalesOrderNo"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return convertView;
    }
}
