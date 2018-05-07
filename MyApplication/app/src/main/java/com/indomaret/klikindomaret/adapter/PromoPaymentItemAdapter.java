package com.indomaret.klikindomaret.adapter;

import android.app.Activity;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.indomaret.klikindomaret.R;
import com.indomaret.klikindomaret.activity.CartActivity;
import com.indomaret.klikindomaret.activity.PaymentActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;

/**
 * Created by USER on 4/18/2016.
 */
public class PromoPaymentItemAdapter extends RecyclerView.Adapter<PromoPaymentItemAdapter.ViewHolder> {
    private Activity activity;
    private DecimalFormat df = new DecimalFormat("#,###");
    private JSONArray productsPromo;

    public PromoPaymentItemAdapter(Activity activity, JSONArray productsPromo){
        this.activity = activity;
        this.productsPromo = productsPromo;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView namePromo;
        public ImageView btnDelete;
        public LinearLayout linearPromo;

        public ViewHolder(View view) {
            super(view);
            namePromo = (TextView) view.findViewById(R.id.name_promo);
            btnDelete = (ImageView) view.findViewById(R.id.btn_delete);
            linearPromo = (LinearLayout) view.findViewById(R.id.linear_promo_item);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.promo_item, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        try {
            JSONObject promo = productsPromo.getJSONObject(position);

            if (promo.getString("CartRefValue").toLowerCase().contains("diskon") || promo.getString("CartRefValue").toLowerCase().contains("potongan")){
                holder.namePromo.setText("Potongan langsung " + promo.getJSONObject("Product").getString("Description") + " Rp " + df.format(promo.getDouble("Discount")).replace(",","."));
            }else if (promo.getString("CartRefValue").toLowerCase().contains("gratis") || promo.getString("CartRefValue").toLowerCase().contains("hadiah")){
                holder.namePromo.setText("Gratis " + promo.getJSONObject("Product").getString("Description"));
            }else if (promo.getString("CartRefValue").toLowerCase().contains("tebus murah")){
                holder.namePromo.setText("Tebus murah " +promo.getJSONObject("Product").getString("Description"));
            }else if (promo.getString("CartRefValue").toLowerCase().contains("potongan harga atau lebih murah atau diskon")){
                holder.namePromo.setText("Potongan harga " +promo.getJSONObject("Product").getString("Description"));
            }else if (promo.getString("CartRefValue").toLowerCase().contains("gkupon")){
                holder.namePromo.setText("Gkupon " +promo.getJSONObject("Product").getString("Description"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        holder.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    productsPromo.getJSONObject(position).put("IsSelectedPromo", false);
                    ((PaymentActivity)activity).updatePromoPayment(productsPromo);

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                        productsPromo.remove(position);
                        notifyDataSetChanged();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                notifyDataSetChanged();
            }
        });
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return productsPromo.length();
    }
}

