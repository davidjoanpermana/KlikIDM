package com.indomaret.klikindomaret.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.indomaret.klikindomaret.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by USER on 6/7/2016.
 */
public class ProductPromoAdapter extends RecyclerView.Adapter<ProductPromoAdapter.ViewHolder> {
    private JSONArray productBadge;
    private JSONObject objectBadge;

    public ProductPromoAdapter(JSONArray productBadge){
        this.productBadge = productBadge;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView title, description;
        public ImageView promoImage;
        public LinearLayout linearPromo;

        public ViewHolder(View view) {
            super(view);
            promoImage = (ImageView) view.findViewById(R.id.image_promo);
            description = (TextView) view.findViewById(R.id.description_promo);
            title = (TextView) view.findViewById(R.id.title_promo);
            linearPromo = (LinearLayout) view.findViewById(R.id.linear_promo);
        }
    }

    @Override
    public ProductPromoAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_product_info_promo_single, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        try {
            objectBadge = productBadge.getJSONObject(position);
            if(objectBadge.getString("BadgeType").equals("1")){
                holder.promoImage.setImageResource(R.drawable.icon_promo_01);
                holder.title.setText(objectBadge.getString("BadgeDetailTitle"));
                holder.description.setText(objectBadge.getString("BadgeDetailDesc"));
            } else if(objectBadge.getString("BadgeType").equals("2")) {
                holder.promoImage.setImageResource(R.drawable.icon_promo_03);
                holder.title.setText(objectBadge.getString("BadgeDetailTitle"));
                holder.description.setText(objectBadge.getString("BadgeDetailDesc"));
            } else if(objectBadge.getString("BadgeType").equals("3")) {
                holder.promoImage.setImageResource(R.drawable.icon_promo_02);
                holder.title.setText(objectBadge.getString("BadgeDetailTitle"));
                holder.description.setText(objectBadge.getString("BadgeDetailDesc"));
            } else if(objectBadge.getString("BadgeType").equals("4")) {
                holder.promoImage.setImageResource(R.drawable.icon_promo_04);
                holder.title.setText(objectBadge.getString("BadgeDetailTitle"));
                holder.description.setText(objectBadge.getString("BadgeDetailDesc"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return productBadge.length();
    }
}