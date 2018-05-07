package com.indomaret.klikindomaret.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.android.volley.toolbox.ImageLoader;
import com.indomaret.klikindomaret.R;
import com.indomaret.klikindomaret.app.AppController;
import com.indomaret.klikindomaret.views.NetworkImagesView;

import org.json.JSONArray;
import org.json.JSONException;

/**
 * Created by Wasim on 11-06-2015.
 */
public class ViewPagerFullImageAdapter extends PagerAdapter {
    private Context mContext;
    private JSONArray mResources;
    private ImageLoader imageLoader = AppController.getInstance().getImageLoader();

    public ViewPagerFullImageAdapter(Context mContext, JSONArray mResources) {
        this.mContext = mContext;
        this.mResources = mResources;
    }

    @Override
    public int getCount() {
        return mResources.length();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position) {
        View itemView = LayoutInflater.from(mContext).inflate(R.layout.full_image_product, container, false);

        NetworkImagesView imageView = (NetworkImagesView) itemView.findViewById(R.id.full_image_product_id);
        try {
            imageView.setImageUrl(mResources.getJSONObject(position).getString("AssetUrl"), imageLoader);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        container.addView(itemView);

        return itemView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((RelativeLayout) object);
    }
}