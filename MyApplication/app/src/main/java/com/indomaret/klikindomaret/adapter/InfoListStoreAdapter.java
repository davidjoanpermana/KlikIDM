package com.indomaret.klikindomaret.adapter;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import com.indomaret.klikindomaret.R;
import com.indomaret.klikindomaret.activity.MapActivity;
import com.indomaret.klikindomaret.activity.MapPlazaActivity;

import org.json.JSONArray;
import org.json.JSONException;

/**
 * Created by USER on 4/26/2016.
 */
public class InfoListStoreAdapter extends RecyclerView.Adapter<InfoListStoreAdapter.ViewHolder> {
    private Activity activity;
    private JSONArray data;
    private String index = "";
    private String type;

    public InfoListStoreAdapter(Activity activity, JSONArray data, String index, String type){
        this.activity = activity;
        this.data = data;
        this.index = index;
        this.type = type;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        public RadioButton radioButton;
        public TextView nameStore;
        public TextView addressStore;
        public LinearLayout linearLayout;

        public ViewHolder(View view) {
            super(view);
            radioButton = (RadioButton) view.findViewById(R.id.radio_button);
            nameStore = (TextView) view.findViewById(R.id.store_name);
            addressStore = (TextView) view.findViewById(R.id.store_address);
            linearLayout = (LinearLayout) view.findViewById(R.id.linear);
        }
    }

    @Override
    public InfoListStoreAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.info_list_store, parent, false);

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
        if (!index.equals("")){
            if(position == Integer.valueOf(index)){
                holder.radioButton.setChecked(true);
            }else{
                holder.radioButton.setChecked(false);
            }
        }

        try {
            if (type.equals("store")){
                holder.nameStore.setText(data.getJSONObject(position).getString("Name") + " ("+data.getJSONObject(position).getString("Code")+")");
                holder.addressStore.setText(data.getJSONObject(position).getString("Street"));
            }else if (type.equals("storeSearch")){
                holder.nameStore.setText(data.getJSONObject(position).getString("store") + " ("+data.getJSONObject(position).getString("ID")+")");
                holder.addressStore.setText(data.getJSONObject(position).getString("address"));
            }else{
                holder.nameStore.setText(data.getJSONObject(position).getString("store") + " ("+data.getJSONObject(position).getString("ID")+")");
                holder.addressStore.setText(data.getJSONObject(position).getString("address"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        holder.radioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (type.equals("plaza") || type.equals("plazaSearch")){
                        ((MapPlazaActivity)activity).setStoreCode(data.getJSONObject(position), position);
                    }else{
                        ((MapActivity)activity).setStoreCode(data.getJSONObject(position), position);
                    }

                    index = String.valueOf(position);
                    notifyDataSetChanged();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        holder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (type.equals("plaza") || type.equals("plazaSearch")){
                        ((MapPlazaActivity)activity).setStoreCode(data.getJSONObject(position), position);
                    }else{
                        ((MapActivity)activity).setStoreCode(data.getJSONObject(position), position);
                    }

                    index = String.valueOf(position);
                    notifyDataSetChanged();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
