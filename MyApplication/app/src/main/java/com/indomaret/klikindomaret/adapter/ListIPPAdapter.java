package com.indomaret.klikindomaret.adapter;

import android.app.Activity;
import android.graphics.Paint;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.indomaret.klikindomaret.R;

import org.json.JSONArray;
import org.json.JSONException;

import java.text.DecimalFormat;

/**
 * Created by USER on 4/26/2016.
 */
public class ListIPPAdapter extends RecyclerView.Adapter<ListIPPAdapter.ViewHolder> {
    private JSONArray data;
    private DecimalFormat df = new DecimalFormat("#,###");

    public ListIPPAdapter(Activity activity, JSONArray data){
        this.data = data;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        public TextView productName;
        public TextView productPLU;
        public TextView productPrice;
        public TextView productQTY;
        public TextView productTotalPrice;
        public TextView initialPrice;
        public LinearLayout linear;

        public ViewHolder(View view) {
            super(view);
            productName = (TextView) view.findViewById(R.id.product_name);
            productPLU = (TextView) view.findViewById(R.id.product_plu);
            productPrice = (TextView) view.findViewById(R.id.product_price);
            productQTY = (TextView) view.findViewById(R.id.product_qty);
            productTotalPrice = (TextView) view.findViewById(R.id.product_total_price);
            linear = (LinearLayout) view.findViewById(R.id.linear);
            initialPrice = (TextView) view.findViewById(R.id.initial_price);
        }
    }

    @Override
    public ListIPPAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_ipp, parent, false);

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
            if (data.getJSONObject(position).getString("PLU").equals("10036492")){
                holder.linear.setVisibility(View.GONE);
            }else{
                holder.linear.setVisibility(View.VISIBLE);
            }

            double total = data.getJSONObject(position).getDouble("HargaSatuanWithDiscount") * data.getJSONObject(position).getDouble("Qty");
            holder.productName.setText(data.getJSONObject(position).getString("Nama"));
            holder.productPLU.setText(data.getJSONObject(position).getString("PLU"));
            holder.productQTY.setText(data.getJSONObject(position).getString("Qty"));
            holder.productPrice.setText("Rp " + df.format(data.getJSONObject(position).getDouble("HargaSatuanWithDiscount")).replace(",", "."));
            holder.productTotalPrice.setText("Rp " + df.format(total).replace(",", "."));

            if (data.getJSONObject(position).getDouble("HargaSatuan") - data.getJSONObject(position).getDouble("HargaSatuanWithDiscount") == 0){
                holder.initialPrice.setVisibility(View.GONE);
            }else{
                holder.initialPrice.setVisibility(View.VISIBLE);
                holder.initialPrice.setText("Rp " + df.format(data.getJSONObject(position).getDouble("HargaSatuan")).replace(",", "."));
                holder.initialPrice.setPaintFlags(holder.initialPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
