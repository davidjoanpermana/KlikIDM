package com.indomaret.klikindomaret.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RatingBar;
import android.widget.TextView;

import com.indomaret.klikindomaret.R;
import com.indomaret.klikindomaret.helper.Month;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by indomaretitsd7 on 6/22/16.
 */
public class ReviewAdapter extends BaseAdapter {
    private Activity activity;
    private LayoutInflater inflater;
    private JSONArray reviewList;
    private Month month = new Month();

    public ReviewAdapter (Activity activity, JSONArray reviewList){
        this.activity = activity;
        this.reviewList = reviewList;
    }

    @Override
    public int getCount() {
        return reviewList.length();
    }

    @Override
    public Object getItem(int position) {
        try {
            return reviewList.getJSONObject(position);
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
    public View getView(int position, View convertView, ViewGroup parent) {
        if (inflater == null)
            inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (convertView == null)
            convertView = inflater.inflate(R.layout.activity_review_single, null);

        TextView reviewUsername = (TextView) convertView.findViewById(R.id.review_username);
        TextView reviewEmail = (TextView) convertView.findViewById(R.id.review_user_email);
        TextView reviewContent = (TextView) convertView.findViewById(R.id.review_content);
        RatingBar reviewRating = (RatingBar) convertView.findViewById(R.id.product_rating);

        try {
            JSONObject reviewObject = reviewList.getJSONObject(position);

            String[] createdDate = reviewObject.getString("Created").split("T");
            String[] date = createdDate[0].split("-");

            reviewUsername.setText(reviewObject.getString("CustomerName"));
            reviewEmail.setText(date[2]+" "+month.getMonth(date[1])+" "+date[0]);
            reviewContent.setText(reviewObject.getString("Body"));
            reviewRating.setRating((float) reviewObject.getDouble("OverallRating"));

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return convertView;
    }
}
