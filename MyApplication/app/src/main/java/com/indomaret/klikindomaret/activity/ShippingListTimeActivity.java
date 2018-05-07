package com.indomaret.klikindomaret.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.indomaret.klikindomaret.R;
import com.indomaret.klikindomaret.adapter.ShippingTimeAdapter;
import com.indomaret.klikindomaret.views.HeightAdjustableListView;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ShippingListTimeActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private Intent intent;
    private HeightAdjustableListView listTime;
    private TextView mTitle;
    private JSONArray timeList = new JSONArray();
    private String slotPengiriman = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shipping_list_time);

        intent = getIntent();

        toolbar = (Toolbar) findViewById(R.id.app_toolbar);
        mTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
        toolbar.setTitle("");
        mTitle.setText(intent.getStringExtra("mTitle"));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        listTime = (HeightAdjustableListView) findViewById(R.id.list_time);

        if (intent.getStringExtra("slotPengiriman") != null && intent.getStringExtra("slotPengiriman").length() > 0){
            slotPengiriman = intent.getStringExtra("slotPengiriman");
        }

        try {
            timeList = new JSONArray(slotPengiriman);
            ShippingTimeAdapter shippingTimeAdapter = new ShippingTimeAdapter(this, timeList, intent.getStringExtra("serverTime"), intent.getBooleanExtra("currentDay", false), slotPengiriman);
            listTime.setAdapter(shippingTimeAdapter);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void setData(String time, String expiredDate){
        intent = new Intent();
        intent.putExtra("expiredDate", expiredDate);
        intent.putExtra("time", time);
        setResult(RESULT_OK, intent);
        finish();
        overridePendingTransition(R.anim.right_in, R.anim.right_out);
    }

    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.right_in, R.anim.right_out);
    }

    @Override
    @SuppressWarnings("deprecation")
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            finish();
            overridePendingTransition(R.anim.right_in, R.anim.right_out);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
