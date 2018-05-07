package com.indomaret.klikindomaret.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.indomaret.klikindomaret.R;
import com.indomaret.klikindomaret.activity.FullScreenImageActivity;
import com.indomaret.klikindomaret.app.AppController;

import org.json.JSONArray;
import org.json.JSONException;

/**
 * Created by Wasim on 11-06-2015.
 */
public class ProductViewPagerAdapter extends PagerAdapter {
    private Context mContext;
    private JSONArray mResources;
    private ImageLoader imageLoader = AppController.getInstance().getImageLoader();

    public ProductViewPagerAdapter(Context mContext, JSONArray mResources) {
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
        View itemView = LayoutInflater.from(mContext).inflate(R.layout.pager_item_product, container, false);
        NetworkImageView imageView = (NetworkImageView) itemView.findViewById(R.id.img_pager_item_product);

        try {
            imageView.setImageUrl(mResources.getJSONObject(position).getString("AssetUrl"), imageLoader);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        imageView.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(mContext, FullScreenImageActivity.class);
                        intent.putExtra("image", mResources.toString());
                        intent.putExtra("pos", "" + position);
                        mContext.startActivity(intent);
                    }
                }
        );

        container.addView(itemView);

        return itemView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((RelativeLayout) object);
    }
}