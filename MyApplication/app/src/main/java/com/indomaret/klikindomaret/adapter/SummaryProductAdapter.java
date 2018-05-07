package com.indomaret.klikindomaret.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.indomaret.klikindomaret.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.List;

/**
 * Created by indomaretitsd7 on 6/16/16.
 */
public class SummaryProductAdapter extends BaseAdapter {
    private LayoutInflater inflater;
    private Activity activity;
    private List<JSONObject> productList;
    private Integer qty;

    private DecimalFormat df = new DecimalFormat("#,###");

    public SummaryProductAdapter(Activity activity, List<JSONObject> productList){
        this.activity = activity;
        this.productList = productList;
    }

    @Override
    public int getCount() {
        return productList.size();
    }

    @Override
    public Object getItem(int position) {
        return productList.get(position);
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
            convertView = inflater.inflate(R.layout.activity_order_summary_store_product, null);

        final JSONObject productObject = productList.get(position);

        TextView sku = (TextView) convertView.findViewById(R.id.product_sku);
        TextView productName = (TextView) convertView.findViewById(R.id.product_name);
        TextView productPrice = (TextView) convertView.findViewById(R.id.single_product_price);
        TextView productInitialPrice = (TextView) convertView.findViewById(R.id.single_product_discount);
        TextView productQuantity = (TextView) convertView.findViewById(R.id.product_quantity);
        TextView totalprice = (TextView) convertView.findViewById(R.id.product_total_price);
        LinearLayout linearLayoutGreeting = (LinearLayout) convertView.findViewById(R.id.LinearLayout_greeting);
        LinearLayout linearLayoutDiscount = (LinearLayout) convertView.findViewById(R.id.LinearLayout_discount);

        try {
            sku.setText(productObject.getString("PLU"));
            productName.setText(productObject.getString("Nama"));

            int discount = productObject.getInt("Discount");
            Double priceWithDiscount = productObject.getDouble("HargaSatuanWithDiscount");
            Double price = productObject.getDouble("HargaSatuan");
            Double subtotalPrice = productObject.getDouble("SubTotal");

            if(discount != 0){
                productInitialPrice.setText("Rp " + df.format(price).replace(",","."));
                linearLayoutDiscount.setVisibility(View.VISIBLE);
                productInitialPrice.setPaintFlags(productInitialPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            } else {
                linearLayoutDiscount.setVisibility(View.GONE);
            }

            productPrice.setText("Rp " + df.format(priceWithDiscount).replace(",","."));
            productQuantity.setText(productObject.getString("Qty"));
            totalprice.setText("Rp " + df.format(subtotalPrice).replace(",","."));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String[] splitText;
        TextView valueText;
        TextView titleText;

        try {
            if (productObject.getBoolean("IsUseNote")){
                linearLayoutGreeting.setVisibility(View.VISIBLE);

                qty = productObject.getInt("Qty");
                splitText = productObject.getString("Note").split("\\|");
                LinearLayout.LayoutParams lparams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);

                if(productObject.getString("Note").equals("") || productObject.getString("Note").equals("null")){
                    linearLayoutGreeting.setVisibility(View.GONE);
                }else{
                    linearLayoutGreeting.removeAllViews();

                    for(int i=0;i<qty;i++){
                        titleText = new TextView(activity);
                        titleText.setText("Ucapan "+ (i+1));
                        titleText.setTextColor(Color.parseColor("#000000"));
                        titleText.setPadding(0, 5, 0, 0);

                        valueText = new TextView(activity);
                        valueText.setLayoutParams(lparams);
                        valueText.setBackgroundResource(R.drawable.card_product_style_1);
                        valueText.setPadding(5, 5, 5, 5);
                        valueText.setTextSize(13);
                        valueText.setEnabled(false);
                        valueText.setTextColor(Color.parseColor("#000000"));
                        valueText.setGravity(Gravity.START);
                        valueText.setBackgroundResource(R.drawable.customborder);

                        if ((i + 1) <= splitText.length){
                            if (splitText[i].length() != 0) valueText.setText(splitText[i]);
                            else valueText.setText("-");
                        } else {
                            valueText.setText("-");
                        }

                        linearLayoutGreeting.addView(titleText);
                        linearLayoutGreeting.addView(valueText);
                    }
                }
            }else{
                linearLayoutGreeting.setVisibility(View.INVISIBLE);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return convertView;
    }
}
