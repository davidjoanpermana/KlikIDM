package com.indomaret.klikindomaret.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.google.android.gms.analytics.Tracker;
import com.indomaret.klikindomaret.API;
import com.indomaret.klikindomaret.R;
import com.indomaret.klikindomaret.adapter.AllBrandAdapter;
import com.indomaret.klikindomaret.app.AppController;
import com.indomaret.klikindomaret.helper.SessionManager;
import com.indomaret.klikindomaret.helper.UncaughtExceptionHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import pl.droidsonroids.gif.GifImageView;

public class BrandActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private TextView mTitle;
    private RelativeLayout preloader;
    private ListView brandListView;

    private SessionManager sessionManager;

    private List<JSONObject> brandObjectList;

    private AllBrandAdapter allBrandAdapter;
    private Intent intent;
    private Tracker mTracker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_brand);

        UncaughtExceptionHandler uncaughtExceptionHandler = new UncaughtExceptionHandler();
        uncaughtExceptionHandler.uncaughtException(this);

        toolbar = (Toolbar) findViewById(R.id.app_toolbar);
        mTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
        mTitle.setText("Semua Merek");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        AppController application = (AppController) getApplication();
        mTracker = application.getDefaultTracker();

        sessionManager = new SessionManager(BrandActivity.this);

        preloader = (RelativeLayout) findViewById(R.id.preloader);
        brandListView = (ListView) findViewById(R.id.brand_list);

        makeJsonArrayGet(API.getInstance().getApiAllBrands() + "?mfp_id=" + sessionManager.getKeyMfpId());
    }

    //get home data
    public void makeJsonArrayGet(String urlJsonObj) {
        System.out.println("--- url brand : "+urlJsonObj);
        JsonArrayRequest jsonArrayReq = new JsonArrayRequest(urlJsonObj,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        if (response == null || response.length() == 0){
                            preloader.setVisibility(View.GONE);
                            Toast.makeText(BrandActivity.this, "Gagal terkoneksi server", Toast.LENGTH_LONG).show();
                        }else{
                            processResponse(response);
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(BrandActivity.this, "Gagal terkoneksi server", Toast.LENGTH_LONG).show();
                preloader.setVisibility(View.GONE);
            }
        }, this);

        AppController.getInstance().addToRequestQueue(jsonArrayReq);
    }

    //process response data
    public void processResponse(JSONArray response) {
        brandObjectList = new ArrayList<>();
        JSONObject brandObject = null;

        try {
            brandObject = new JSONObject();
            brandObject.put("Name", "#");
            brandObjectList.add(brandObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            for(int i=0; i<response.length(); i++){
                JSONObject object = response.getJSONObject(i);

                if(object.getString("Name").length() > 0 && Integer.parseInt(object.getString("TotalProduct")) > 0){
                    if (Character.isDigit(object.getString("Name").charAt(0))){
                        brandObjectList.add(object);
                    } else if(object.getString("Name").charAt(0) == brandObject.getString("Name").charAt(0)){
                        brandObjectList.add(object);
                    } else {
                        try {
                            brandObject = new JSONObject();
                            brandObject.put("Name", object.getString("Name").substring(0,1));
                            brandObjectList.add(brandObject);
                            brandObjectList.add(object);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        allBrandAdapter = new AllBrandAdapter(BrandActivity.this, brandObjectList);
        brandListView.setAdapter(allBrandAdapter);

        preloader.setVisibility(View.GONE);

        brandListView.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        JSONObject brandObject = brandObjectList.get(position);
                        try {
                            if (brandObject.getString("Name").length() > 1) {
                                intent = new Intent(BrandActivity.this, CategoryActivity.class);
                                intent.putExtra("cat", "brand");
                                intent.putExtra("brand", brandObject.toString());
                                startActivity(intent);
                                overridePendingTransition(R.anim.right_in, R.anim.right_out);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
        );
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

    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.right_in, R.anim.right_out);
    }
}
