package com.indomaret.klikindomaret.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.indomaret.klikindomaret.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by USER on 6/7/2016.
 */
public class ProductDescriptionAdapter extends RecyclerView.Adapter<ProductDescriptionAdapter.ViewHolder> {
    private JSONArray productAttribut;

    public ProductDescriptionAdapter(JSONArray productAttribut){
        this.productAttribut = productAttribut;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView descriptionTitle, description;
        public ViewHolder(View view) {
            super(view);
            descriptionTitle = (TextView) view.findViewById(R.id.description_title);
            description = (TextView) view.findViewById(R.id.description_description);
        }
    }

    @Override
    public ProductDescriptionAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_product_description_single, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        try {
            JSONObject object = productAttribut.getJSONObject(position);
            JSONObject productTypeAttributeEnum = object.getJSONObject("ProductTypeAttributeEnum");

            holder.descriptionTitle.setText(productTypeAttributeEnum.getString("Name"));
            holder.description.setText(object.getString("Description"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return productAttribut.length();
    }
}
