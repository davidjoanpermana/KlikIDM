package com.indomaret.klikindomaret.adapter;

import android.app.Activity;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.indomaret.klikindomaret.R;
import com.indomaret.klikindomaret.views.WrapContentViewPager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by indomaretitsd7 on 6/23/16.
 */
public class PromoBannerAdapter extends RecyclerView.Adapter<PromoBannerAdapter.ViewHolder> implements ViewPager.OnPageChangeListener{
    private Activity activity;
    private JSONArray promoList;

    public PromoBannerAdapter(Activity activity, JSONArray promoList){
        this.activity = activity;
        this.promoList = promoList;
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView promoName;
        public WrapContentViewPager slideBanner;
        public View viewLine;

        public ViewHolder(View view) {
            super(view);
            promoName = (TextView) view.findViewById(R.id.text_banner);
            slideBanner = (WrapContentViewPager) view.findViewById(R.id.pager_banner);
            viewLine = (View) view.findViewById(R.id.view_line);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.promo_banner, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        try {
            JSONArray banners = new JSONArray();
            List<JSONObject> list = new ArrayList<>();

            holder.promoName.setText(promoList.getJSONObject(position).getString("Name"));

            for (int i=0; i<promoList.getJSONObject(position).getJSONArray("Banners").length(); i++){
                list.add(promoList.getJSONObject(position).getJSONArray("Banners").getJSONObject(i));
            }

            Collections.sort(list, new Comparator<JSONObject>() {
                public int compare(JSONObject a, JSONObject b) {
                    String valA = new String();
                    String valB = new String();

                    try {
                        valA = a.getString("Sortno");
                        valB = b.getString("Sortno");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    return valA.compareTo(valB);
                }
            });

            for (int i=0; i<list.size(); i++){
                banners.put(list.get(i).getJSONObject("Banner"));
            }

            ViewPagerPromoAdapter mAdapter = new ViewPagerPromoAdapter(activity, banners);

            holder.slideBanner.setAdapter(mAdapter);
            holder.slideBanner.setCurrentItem(0);
            holder.slideBanner.setPageMargin(20);
            holder.slideBanner.addOnPageChangeListener(this);

            DisplayMetrics displayMetrics = new DisplayMetrics();
            activity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
            int height = displayMetrics.heightPixels;

            ViewGroup.LayoutParams params = holder.slideBanner.getLayoutParams();
            params.height = (height / 3) + 10;

            holder.slideBanner.setLayoutParams(params);

            if (position == (promoList.length()-1)) holder.viewLine.setVisibility(View.GONE);
            else holder.viewLine.setVisibility(View.VISIBLE);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return promoList.length();
    }
}
