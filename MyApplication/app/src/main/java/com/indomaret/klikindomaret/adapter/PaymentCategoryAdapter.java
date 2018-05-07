package com.indomaret.klikindomaret.adapter;

import android.app.Activity;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.indomaret.klikindomaret.R;
import com.indomaret.klikindomaret.views.HeightAdjustableListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

/**
 * Created by indomaretitsd7 on 6/14/16.
 */
public class PaymentCategoryAdapter extends RecyclerView.Adapter<PaymentCategoryAdapter.ViewHolder>{
    private Activity activity;
    private JSONArray paymentTypeList = new JSONArray();
    private JSONArray paymentKreditList = new JSONArray();
    private JSONArray paymentMethodList, promoProduct;
    private JSONArray paymentType;
    private JSONObject jsonObject;
    private String from = "";
    private String codePayment = "";
    private String expiredDate = "";
    private int indexPaymentMethod;
    private int storeCount = 0;
    private Runnable runnable;

    public PaymentCategoryAdapter(Activity activity, JSONArray paymentMethodList, JSONObject jsonObject, String from, JSONArray promoProduct, String codePayment){
        this.activity = activity;
        this.paymentMethodList = paymentMethodList;
        this.jsonObject = jsonObject;
        this.from = from;
        this.promoProduct = promoProduct;
        this.codePayment = codePayment;
    }

    public PaymentCategoryAdapter(Activity activity, JSONArray paymentMethodList, JSONObject jsonObject, String from, JSONArray promoProduct, String codePayment,
                                  int storeCount, String expiredDate){
        this.activity = activity;
        this.paymentMethodList = paymentMethodList;
        this.jsonObject = jsonObject;
        this.from = from;
        this.promoProduct = promoProduct;
        this.codePayment = codePayment;
        this.storeCount = storeCount;
        this.expiredDate = expiredDate;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public HeightAdjustableListView listPayment;
        public TextView categoryName;
        public View sparator;
        RecyclerView messageCountdown;

        public ViewHolder(View view) {
            super(view);
            listPayment = (HeightAdjustableListView) view.findViewById(R.id.list_payment_detail);
            categoryName = (TextView) view.findViewById(R.id.category_payment);
            sparator = (View) view.findViewById(R.id.sparator);
            messageCountdown = (RecyclerView) view.findViewById(R.id.message_countdown);
        }
    }

    @Override
    public PaymentCategoryAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.category_payment, parent, false);

        return new ViewHolder(view);
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        try {
            JSONObject installment = new JSONObject();
            paymentTypeList = new JSONArray();
            paymentKreditList = new JSONArray();
            List<JSONObject> list = new ArrayList<>();
            indexPaymentMethod = Integer.valueOf(paymentMethodList.getJSONObject(position).getString("paymentMethod").split(",")[0]);

            String categoryPaymentName = paymentMethodList.getJSONObject(position).getString("paymentMethod").split(",")[2];
            if (storeCount > 0 && (categoryPaymentName.toLowerCase().equals("bank transfer") || categoryPaymentName.toLowerCase().equals("indomaret payment point"))){
                holder.messageCountdown.setVisibility(View.VISIBLE);
                holder.messageCountdown.setHasFixedSize(true);
                holder.messageCountdown.setLayoutManager(new LinearLayoutManager(activity));
                holder.messageCountdown.setAdapter(new CountDownPaymentAdapter(activity, expiredDate));
            }else{
                holder.messageCountdown.setVisibility(View.GONE);
            }

            holder.categoryName.setText(categoryPaymentName);

            if (from.equals("kai")){
                paymentType = jsonObject.getJSONArray("PaymentTypeList");

                for (int i=0; i<paymentType.length(); i++){
                    if (paymentType.getJSONObject(i).getInt("PaymentMethod") == indexPaymentMethod){
                        if (paymentType.getJSONObject(i).getString("Code").contains("500C")){
                            paymentKreditList.put(paymentType.getJSONObject(i));
                        }else{
                            paymentTypeList.put(paymentType.getJSONObject(i));
                        }
                    }
                }

                if (jsonObject.getInt("PaymentStatus") == 0 && paymentKreditList.length() > 0){
                    installment.put("message", jsonObject.getString("ErrorMessagePayment"));
                    installment.put("Code", "message_installment");
                    list.add(installment);
                }
            }else{
                paymentType = jsonObject.getJSONArray("PaymentType");

                for (int i=0; i<paymentType.length(); i++){
                    if (paymentType.getJSONObject(i).getInt("PaymentMethod") == indexPaymentMethod){
                        if (paymentType.getJSONObject(i).getString("Code").contains("500C")){
                            paymentKreditList.put(paymentType.getJSONObject(i));
                        }else{
                            paymentTypeList.put(paymentType.getJSONObject(i));
                        }
                    }
                }

                if (!jsonObject.getBoolean("IsInstallment") && paymentKreditList.length() > 0){
                    installment.put("message", jsonObject.getString("ErrorMessagePayment"));
                    installment.put("Code", "message_installment");
                    list.add(installment);
                }
            }

            for (int i=0; i<paymentTypeList.length(); i++){
                list.add(paymentTypeList.getJSONObject(i));
            }

            Collections.sort(list, new Comparator<JSONObject>() {
                public int compare(JSONObject a, JSONObject b) {
                    String valA = new String();
                    String valB = new String();

                    try {
                        valA = a.getString("Name");
                        valB = b.getString("Name");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    return valA.compareTo(valB);
                }
            });

            for (int i=0; i<paymentKreditList.length(); i++){
                list.add(paymentKreditList.getJSONObject(i));
            }

            PaymentListAdapter paymentListAdapter = new PaymentListAdapter(activity, list, jsonObject, from, promoProduct, codePayment);
            holder.listPayment.setAdapter(paymentListAdapter);
            holder.listPayment.setVisibility(View.VISIBLE);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return paymentMethodList.length();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
}