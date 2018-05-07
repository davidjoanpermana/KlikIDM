package com.indomaret.klikindomaret.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.indomaret.klikindomaret.R;
import com.indomaret.klikindomaret.app.AppController;
import com.indomaret.klikindomaret.helper.Month;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by indomaretitsd7 on 6/22/16.
 */
public class NotificationAdapter extends BaseAdapter {
    private LayoutInflater inflater;
    private Activity activity;
    private JSONArray notifList;
    private Month dateName = new Month();

    ImageLoader imageLoader = AppController.getInstance().getImageLoader();

    public NotificationAdapter(Activity activity, JSONArray notifList){
        this.activity = activity;
        this.notifList = notifList;
    }

    @Override
    public int getCount() {
        return notifList.length();
    }

    @Override
    public Object getItem(int position) {
        try {
            return notifList.get(position);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
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
            convertView = inflater.inflate(R.layout.activity_item_notification, null);

        TextView titleText = (TextView) convertView.findViewById(R.id.title);
        TextView dateText = (TextView) convertView.findViewById(R.id.date);
        TextView messageText = (TextView) convertView.findViewById(R.id.message);

        try {
            JSONObject notifObject = notifList.getJSONObject(position);

            String calendar = notifObject.getString("Created").split("T")[0];
            String time = notifObject.getString("Created").split("T")[1];
//          example  2017-10-20T10:58:55.3
            String year = calendar.split("-")[0];
            String month = calendar.split("-")[1];
            String date = calendar.split("-")[2];

            String day = dateName.getCurrentDay(year, month, date);

            titleText.setText(notifObject.getString("Title"));
            dateText.setText(day + ", " + date + " " + dateName.getMonth(month) + " " + year + " " + time.split("\\.")[0] + " WIB");
            messageText.setText(notifObject.getString("Message"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return convertView;
    }
}
