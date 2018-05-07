package com.indomaret.klikindomaret.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.indomaret.klikindomaret.R;
import com.indomaret.klikindomaret.activity.PaymentKAIActivity;
import com.indomaret.klikindomaret.app.AppController;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by indomaretitsd7 on 6/14/16.
 */
public class PaymentListKAIAdapter extends BaseAdapter{
    private LayoutInflater inflater;
    private Activity activity;
    private JSONArray paymentList;
    private ImageLoader imageLoader = AppController.getInstance().getImageLoader();

    public PaymentListKAIAdapter(Activity activity, JSONArray paymentList){
        this.activity = activity;
        this.paymentList = paymentList;
    }

    @Override
    public int getCount() {
        return paymentList.length();
    }

    @Override
    public Object getItem(int position) {
        try {
            return paymentList.getString(position);
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
            convertView = inflater.inflate(R.layout.list_payment_kai, null);

        TextView paymentName = (TextView) convertView.findViewById(R.id.payment_name);
        TextView paymentDetailInfo = (TextView) convertView.findViewById(R.id.payment_detail_info);
        ImageView paymentImage = (ImageView) convertView.findViewById(R.id.payment_image);
        ImageView bullet = (ImageView) convertView.findViewById(R.id.bullet);
        NetworkImageView cardViewNetwork = (NetworkImageView) convertView.findViewById(R.id.card_view_payment_network);
        LinearLayout linearLayout = (LinearLayout) convertView.findViewById(R.id.linearLayout);

        JSONObject paymentObject = new JSONObject();
        try {
            paymentObject = paymentList.getJSONObject(position);

            if (paymentObject.getString("status").equals("1")){
                bullet.setImageResource(R.drawable.icon_circle_blue);
            }else{
                bullet.setImageResource(R.drawable.icon_circle_grey);
            }

            paymentName.setText(paymentObject.getString("Name"));
            if (paymentObject.getString("Note") == null || paymentObject.getString("Note").equals("null")){
                paymentDetailInfo.setText("");
            }else{
                paymentDetailInfo.setText(paymentObject.getString("Note"));
            }

            if(paymentObject.getInt("PaymentPlan") == 01){
                if (paymentObject.getString("Code").equals("402")){
                    paymentImage.setVisibility(View.VISIBLE);
                    cardViewNetwork.setVisibility(View.GONE);
                    paymentImage.setImageResource(R.drawable.transfer);
                } else if (paymentObject.getString("Code").equals("405")){
                    paymentImage.setVisibility(View.VISIBLE);
                    cardViewNetwork.setVisibility(View.GONE);
                    paymentImage.setImageResource(R.drawable.bcaklikpay);
                } else if (paymentObject.getString("Code").equals("406")){
                    paymentImage.setVisibility(View.VISIBLE);
                    cardViewNetwork.setVisibility(View.GONE);
                    paymentImage.setImageResource(R.drawable.mandiriklikpay);
                } else if (paymentObject.getString("Code").equals("500")){
                    paymentImage.setVisibility(View.VISIBLE);
                    cardViewNetwork.setVisibility(View.GONE);
                    paymentImage.setImageResource(R.drawable.kredit);
                } else if (paymentObject.getString("Code").equals("BPPID")){
                    paymentImage.setVisibility(View.VISIBLE);
                    cardViewNetwork.setVisibility(View.GONE);
                    paymentImage.setImageResource(R.drawable.bppid);
                } else if (paymentObject.getString("Code").equals("RKPON")){
                    paymentImage.setVisibility(View.VISIBLE);
                    cardViewNetwork.setVisibility(View.GONE);
                    paymentImage.setImageResource(R.drawable.rkpon);
                } else if (paymentObject.getString("Code").equals("COD")){
                    paymentImage.setVisibility(View.VISIBLE);
                    cardViewNetwork.setVisibility(View.GONE);
                    paymentImage.setImageResource(R.drawable.cod);
                } else if (paymentObject.getString("Code").equals("702")){
                    paymentImage.setVisibility(View.VISIBLE);
                    cardViewNetwork.setVisibility(View.GONE);
                    paymentImage.setImageResource(R.drawable.bca_virtual);
                } else{
                    paymentImage.setVisibility(View.GONE);
                    cardViewNetwork.setVisibility(View.VISIBLE);
                    cardViewNetwork.setImageUrl("https://payment.klikindomaret.com/Asset/image/"+paymentObject.getString("Code")+".jpg", imageLoader);
                }
            } else if(paymentObject.getInt("PaymentPlan") == 02){
                paymentImage.setVisibility(View.VISIBLE);
                paymentImage.setImageResource(R.drawable.bkkcn);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    ((PaymentKAIActivity) activity).setPriceCode(paymentList.getJSONObject(position), position);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        return convertView;
    }
}