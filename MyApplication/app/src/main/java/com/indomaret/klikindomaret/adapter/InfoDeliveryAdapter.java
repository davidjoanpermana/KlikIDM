package com.indomaret.klikindomaret.adapter;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.indomaret.klikindomaret.R;
import com.indomaret.klikindomaret.helper.Month;

import org.json.JSONArray;
import org.json.JSONException;

/**
 * Created by USER on 4/26/2016.
 */
public class InfoDeliveryAdapter extends RecyclerView.Adapter<InfoDeliveryAdapter.ViewHolder> {
    private JSONArray data;
    private Month dateName = new Month();

    public InfoDeliveryAdapter(Activity activity, JSONArray data){
        this.data = data;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        public TextView dateDelivery;
        public TextView positionDelivery;

        public ViewHolder(View view) {
            super(view);
            dateDelivery = (TextView) view.findViewById(R.id.date_delivery);
            positionDelivery = (TextView) view.findViewById(R.id.position_delivery);
        }
    }

    @Override
    public InfoDeliveryAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.info_delivery, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public int getItemCount() {
        return data.length();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        try {
            String times = data.getJSONObject(position).getString("UpdatedDate").split("T")[1].substring(0, 5);
            String dates = data.getJSONObject(position).getString("UpdatedDate").split("T")[0];
            String date = dates.split("-")[2];
            String month = dates.split("-")[1];
            String year = dates.split("-")[0];

            holder.dateDelivery.setText(date + " " + dateName.getMonth(month) + " " + year + "|" + times);
            holder.positionDelivery.setText(data.getJSONObject(position).getString("Status") + " " + data.getJSONObject(position).getString("StatusDesc"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
