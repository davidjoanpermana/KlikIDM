package com.indomaret.klikindomaret.adapter;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.indomaret.klikindomaret.R;
import com.indomaret.klikindomaret.activity.MapActivity;
import com.indomaret.klikindomaret.activity.MapPlazaActivity;

import java.util.List;

/**
 * Created by USER on 4/26/2016.
 */
public class AutoCompleteAdapter extends RecyclerView.Adapter<AutoCompleteAdapter.ViewHolder> {
    private Activity activity;
    private List<String> data;
    private String type;

    public AutoCompleteAdapter(Activity activity, List<String> data, String type){
        this.activity = activity;
        this.data = data;
        this.type = type;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        public TextView suggest;

        public ViewHolder(View view) {
            super(view);
            suggest = (TextView) view.findViewById(R.id.autocomplete_text);
        }
    }

    @Override
    public AutoCompleteAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.autocomplete, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        String suggestion = data.get(position);
        holder.suggest.setText(suggestion);

        holder.suggest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (type.equals("plaza") || type.equals("plazaSearch")){
                    ((MapPlazaActivity)activity).setSearchStoreCode(holder.suggest.getText().toString());
                }else{
                    ((MapActivity)activity).setSearchStoreCode(holder.suggest.getText().toString());
                }
            }
        });
    }
}
