package com.indomaret.klikindomaret.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import com.indomaret.klikindomaret.R;
import com.indomaret.klikindomaret.adapter.ViewPagerFullImageAdapter;
import com.indomaret.klikindomaret.helper.UncaughtExceptionHandler;

import org.json.JSONArray;
import org.json.JSONException;

public class FullScreenImageActivity extends AppCompatActivity {
    Intent intent;
    private ViewPager productImages;
    private ViewPagerFullImageAdapter viewPagerFullImageAdapter;

    private String mImageResources;
    private int position;
    JSONArray imagesList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_screen_image);

        UncaughtExceptionHandler uncaughtExceptionHandler = new UncaughtExceptionHandler();
        uncaughtExceptionHandler.uncaughtException(this);

        intent = getIntent();
        mImageResources = intent.getStringExtra("image");
        position = Integer.parseInt(intent.getStringExtra("pos"));

        try {
            imagesList = new JSONArray(mImageResources);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        productImages = (ViewPager) findViewById(R.id.full_product_image);
        viewPagerFullImageAdapter = new ViewPagerFullImageAdapter(FullScreenImageActivity.this, imagesList);
        productImages.setAdapter(viewPagerFullImageAdapter);
        productImages.setCurrentItem(position);
    }
}
