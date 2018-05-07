package com.indomaret.klikindomaret.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.indomaret.klikindomaret.R;
import com.indomaret.klikindomaret.activity.ShippingListTimeActivity;
import com.indomaret.klikindomaret.activity.ShippingTimeActivity;
import com.indomaret.klikindomaret.helper.Month;

import org.json.JSONArray;
import org.json.JSONException;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by USER on 5/20/2016.
 */
public class ShippingTimeDaysAdapter extends BaseAdapter{
    private Intent intent;
    private LayoutInflater inflater;
    private Activity activity;
    private List<String> dayList;
    private String dateServer, day, stringDate, date;
    private String customerAddressID = "";
    private String storeId = "";
    private boolean isSend;
    private JSONArray listSlot = new JSONArray();
    private Month month = new Month();

    private TextView dayName, dateName;
    private LinearLayout linear, linearContent;
    private View viewLine;

    public ShippingTimeDaysAdapter(Activity activity, List<String> dayList, String dateServer, String customerAddressID, String storeId, boolean isSend, JSONArray listSlot){
        this.activity = activity;
        this.dayList = dayList;
        this.dateServer = dateServer;
        this.isSend = isSend;
        this.customerAddressID = customerAddressID;
        this.storeId = storeId;
        this.listSlot = listSlot;
    }

    @Override
    public int getCount() {
        return dayList.size();
    }

    @Override
    public Object getItem(int position) {
        return dayList.get(position);
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
            convertView = inflater.inflate(R.layout.day_single_item, null);

        intent = activity.getIntent();

        dayName = (TextView) convertView.findViewById(R.id.day_name);
        dateName = (TextView) convertView.findViewById(R.id.date_name);
        linear = (LinearLayout) convertView.findViewById(R.id.linear);
        linearContent = (LinearLayout) convertView.findViewById(R.id.linear_content);
        viewLine = (View) convertView.findViewById(R.id.view_line);

        if (position == dayList.size()-1){

            viewLine.setVisibility(View.VISIBLE);
        }else{
            viewLine.setVisibility(View.GONE);
        }
        day = dayList.get(position);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        Calendar calendar2 = Calendar.getInstance();
        calendar2.set(Integer.parseInt(dateServer.split(" ")[0].split("-")[0]), Integer.parseInt(dateServer.split(" ")[0].split("-")[1])-1,
                Integer.parseInt(dateServer.split(" ")[0].split("-")[2])+1);
        String date2 = dateFormat.format(calendar2.getTime());

        String[] arrayDate = day.split(" ");
        date = arrayDate[3] + "-" + arrayDate[2] + "-" + arrayDate[1];

        if (date.equals(dateServer.split(" ")[0])){
            stringDate = arrayDate[1]+" "+month.getMonth(arrayDate[2])+" "+arrayDate[3];
            dayName.setText("Hari ini");
            dateName.setText(", "+stringDate);
        }else if (date.equals(date2)){
            stringDate = arrayDate[1]+" "+month.getMonth(arrayDate[2])+" "+arrayDate[3];
            dayName.setText("Besok");
            dateName.setText(", "+stringDate);
        }else{
            stringDate = arrayDate[1]+" "+month.getMonth(arrayDate[2])+" "+arrayDate[3];
            dayName.setText(month.getDay(arrayDate[0]));
            dateName.setText(", "+stringDate);
        }

        for (int i=0; i<listSlot.length(); i++){
            try {
                if (listSlot.getJSONObject(i).getString("DayLabel").equals(dayName.getText().toString()) && !listSlot.getJSONObject(i).getBoolean("IsActive")){
                    linear.setEnabled(false);
                    linear.setAlpha((float) 0.2);
                }else{
                    linear.setEnabled(true);
                    linear.setAlpha((float) 1);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        linear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String[] arrayDate = dayList.get(position).split(" ");
                stringDate = month.getDay(arrayDate[0])+","+arrayDate[1]+" "+month.getMonth(arrayDate[2])+" "+arrayDate[3];
                date = arrayDate[3] + "-" + arrayDate[2] + "-" + arrayDate[1];
                ((ShippingTimeActivity)activity).setData(stringDate);

                intent = new Intent(activity, ShippingListTimeActivity.class);
                if (date.equals(dateServer.split(" ")[0])){
                    intent.putExtra("currentDay", true);
                }

                String tanggal = arrayDate[1]+" "+month.getMonth(arrayDate[2])+" "+arrayDate[3];
                for (int i=0; i<listSlot.length(); i++){
                    try {
                        if (tanggal.equals(listSlot.getJSONObject(i).getString("DateLabel"))){
                            intent.putExtra("slotPengiriman", listSlot.getJSONObject(i).getJSONArray("SlotPengiriman").toString());
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                intent.putExtra("mTitle", stringDate);
                intent.putExtra("serverTime", dateServer);
                activity.startActivityForResult(intent, 10);
                activity.overridePendingTransition(R.anim.left_in, R.anim.left_out);
            }
        });

        return convertView;
    }
}
