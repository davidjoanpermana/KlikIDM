package com.indomaret.klikindomaret.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.indomaret.klikindomaret.R;

import java.util.List;

/**
 * Created by USER on 4/26/2016.
 */
public class AreaAutoCompleteAdapter extends BaseAdapter {
    private LayoutInflater inflater;
    private Activity activity;
    private List<String> data;

    public AreaAutoCompleteAdapter(Activity activity, List<String> data){
        this.activity = activity;
        this.data = data;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (inflater == null)
            inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if(convertView == null){
            convertView = inflater.inflate(R.layout.activity_postcode_coverage_autocomplete, null);
        }

        TextView suggest = (TextView) convertView.findViewById(R.id.autocomplete_text);
        String suggestion = data.get(position);
        suggest.setText(suggestion);

        return convertView;
    }
}
