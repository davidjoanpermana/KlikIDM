package com.indomaret.klikindomaret.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.indomaret.klikindomaret.R;
import com.indomaret.klikindomaret.activity.FilterKAIActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by indomaretitsd7 on 6/14/16.
 */
public class ListFilterAdapter extends BaseAdapter {
    private Activity activity;
    private JSONArray itemList;
    protected JSONObject jsonObject;
    private LayoutInflater inflater;
    private String type;

    public ListFilterAdapter(Activity activity, JSONArray itemList, String type) {
        this.activity = activity;
        this.itemList = itemList;
        this.type = type;
    }

    @Override
    public int getCount() {
        return itemList.length();
    }

    @Override
    public Object getItem(int position) {
        try {
            return itemList.getJSONObject(position);
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
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
            convertView = inflater.inflate(R.layout.list_filter, null);

        final TextView item = (TextView) convertView.findViewById(R.id.item);
        final CheckBox btnCheck = (CheckBox) convertView.findViewById(R.id.btn_check);
        LinearLayout linearLayoutItem = (LinearLayout) convertView.findViewById(R.id.linearLayout_item);

        try {
            jsonObject = new JSONObject();
            jsonObject = itemList.getJSONObject(position);

            if (jsonObject.getString("status").equals("0")) {
                btnCheck.setChecked(false);
            } else {
                btnCheck.setChecked(true);
            }

            if (type.equals("time")) {
//                item.setText(jsonObject.getString("DepartureDate").split("T")[1].substring(0, 5));
                item.setText(jsonObject.getString("DepartureDate"));
            } else if (type.equals("class")) {
                item.setText(jsonObject.getString("TrainClassName"));
            } else if (type.equals("train")) {
                item.setText(jsonObject.getString("TrainName"));
            } else if (type.equals("price")) {
                item.setText(jsonObject.getString("AdultPrice"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        linearLayoutItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (btnCheck.isChecked()) {
                        btnCheck.setChecked(false);

                        itemList.getJSONObject(position).put("status", "0");
                        ((FilterKAIActivity) activity).setList(itemList.getJSONObject(position), type, position);
                    } else {
                        btnCheck.setChecked(true);

                        itemList.getJSONObject(position).put("status", "1");
                        ((FilterKAIActivity) activity).setList(itemList.getJSONObject(position), type, position);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        btnCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (btnCheck.isChecked()) {
                        itemList.getJSONObject(position).put("status", "1");
                        ((FilterKAIActivity) activity).setList(itemList.getJSONObject(position), type, position);
                    } else {
                        itemList.getJSONObject(position).put("status", "0");
                        ((FilterKAIActivity) activity).setList(itemList.getJSONObject(position), type, position);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

//        btnCheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
//                try {
////                    itemList.getJSONObject(position).put("status", b ? "1" : "0");
////                    ((FilterKAIActivity) activity).setList(itemList.getJSONObject(position), type, position);
//
//                    if (b == false) {
//                        itemList.getJSONObject(position).put("status", "0");
//                        ((FilterKAIActivity) activity).setList(itemList.getJSONObject(position), type, position);
//                    } else {
//                        itemList.getJSONObject(position).put("status", "1");
//                        ((FilterKAIActivity) activity).setList(itemList.getJSONObject(position), type, position);
//                    }
//                } catch (Exception e) {
//
//                }
//            }
//        });

        return convertView;
    }

}