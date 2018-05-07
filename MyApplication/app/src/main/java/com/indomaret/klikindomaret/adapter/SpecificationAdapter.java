package com.indomaret.klikindomaret.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.indomaret.klikindomaret.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * Created by USER on 4/8/2016.
 */
public class SpecificationAdapter extends BaseAdapter {
    private LayoutInflater inflater;
    private Activity activity;
    private List<String> wording;
    private JSONObject object = new JSONObject();

    public SpecificationAdapter(Activity activity, List<String> wording, String from){
        this.activity = activity;
        this.wording = wording;
    }

    @Override
    public int getCount() {
        return wording.size();
    }

    @Override
    public Object getItem(int position) {
        return wording.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (inflater == null)
            inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (convertView == null)
            convertView = inflater.inflate(R.layout.specification_item, null);

        TextView item = (TextView) convertView.findViewById(R.id.item);
        LinearLayout linearLayout = (LinearLayout) convertView.findViewById(R.id.linearLayout);

        try {
            object = new JSONObject(wording.get(position));
            item.setText(object.getString("data"));

            if (object.getString("status").equals("1")){
                linearLayout.setBackgroundResource(R.color.colorPrimary);
                item.setTextColor(Color.WHITE);
            }else{
                linearLayout.setBackgroundResource(R.drawable.border2);
                item.setTextColor(Color.parseColor("#0079C2"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return convertView;
    }
}
