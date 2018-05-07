package com.indomaret.klikindomaret.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.indomaret.klikindomaret.API;
import com.indomaret.klikindomaret.R;
import com.indomaret.klikindomaret.adapter.ShippingTimeStoreListAdapter;
import com.indomaret.klikindomaret.app.AppController;
import com.indomaret.klikindomaret.helper.SessionManager;
import com.indomaret.klikindomaret.helper.UncaughtExceptionHandler;
import com.indomaret.klikindomaret.views.HeightAdjustableListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class StoreListActivity extends AppCompatActivity {
    private SessionManager sessionManager;
    private Intent intent;
    private ShippingTimeStoreListAdapter shippingTimeStoreListAdapter;

    private Toolbar toolbar;
    private HeightAdjustableListView storeList;
    private TextView outOfRange, mTitle;
    private Spinner spinnerRegion;

    private List<JSONObject> storeListObject = new ArrayList<>();
    private List<String> listRegion = new ArrayList<>();
    private JSONArray listRegionFull = new JSONArray();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store_list);

        UncaughtExceptionHandler uncaughtExceptionHandler = new UncaughtExceptionHandler();
        uncaughtExceptionHandler.uncaughtException(this);

        toolbar = (Toolbar) findViewById(R.id.app_toolbar);
        mTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
        mTitle.setText("Pilih Toko");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        sessionManager = new SessionManager(StoreListActivity.this);

        spinnerRegion = (Spinner) findViewById(R.id.spinner_region);
        storeList = (HeightAdjustableListView) findViewById(R.id.store_list);
        outOfRange = (TextView) findViewById(R.id.out_of_range);

        jsonArrayRequest(API.getInstance().getApiAllRegion()+"?mfp_id="+sessionManager.getKeyMfpId(), "region");
        jsonArrayRequest(API.getInstance().getApiGetStoreByRegionAndKey()+"/"+sessionManager.getRegionID()+"?key=&currPage=0&pageSize=0"+"&mfp_id="+sessionManager.getKeyMfpId(), "store");

        spinnerRegion.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position > 0){
                    String idRegion = "";
                    try {
                        idRegion = listRegionFull.getJSONObject(position - 1).getString("ID");
                        jsonArrayRequest(API.getInstance().getApiGetStoreByRegionAndKey()+"/"+idRegion+"?key=&currPage=0&pageSize=0"+"&mfp_id="+sessionManager.getKeyMfpId(), "store");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    public void jsonArrayRequest(String url, final String type) {
        System.out.println("store list url : "+url);
        JsonArrayRequest jsonArrayReq = new JsonArrayRequest(url,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        if (response == null || response.length() == 0){
                            Toast.makeText(StoreListActivity.this, "Gagal terkoneksi server", Toast.LENGTH_LONG).show();
                        }else{
                            if (type.equals("store")){
                                if(response.length() == 0){
                                    outOfRange.setVisibility(View.VISIBLE);
                                } else {
                                    outOfRange.setVisibility(View.GONE);
                                    storeListObject = new ArrayList<>();

                                    for (int i=0; i<response.length(); i++){
                                        try {
                                            Log.d("List toko",response.getJSONObject(i).toString());
                                            storeListObject.add(response.getJSONObject(i));
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }

                                    shippingTimeStoreListAdapter = new ShippingTimeStoreListAdapter(StoreListActivity.this, storeListObject);
                                    storeList.setAdapter(shippingTimeStoreListAdapter);
                                    shippingTimeStoreListAdapter.notifyDataSetChanged();

                                    storeList.setOnItemClickListener(
                                            new AdapterView.OnItemClickListener() {
                                                @Override
                                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                                    intent = new Intent();
                                                    intent.putExtra("store", storeListObject.get(position).toString());
                                                    setResult(RESULT_OK, intent);
                                                    finish();
                                                    overridePendingTransition(R.anim.right_in, R.anim.right_out);
                                                }
                                            }
                                    );
                                }
                            }else if (type.equals("region")){
                                processAllRegion(response);
                            }
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(StoreListActivity.this, "Gagal terkoneksi server", Toast.LENGTH_LONG).show();
            }
        }, this);

        jsonArrayReq.setRetryPolicy(new DefaultRetryPolicy(60000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppController.getInstance().addToRequestQueue(jsonArrayReq);
    }

    public void processAllRegion(JSONArray response){
        Integer positionCurrent = 1;
        listRegionFull = response;
        listRegion.add("-- Pilih Wilayah --");

        try {
            for (int i=0; i<listRegionFull.length();i++){
                if (listRegionFull.getJSONObject(i).getString("ID").equals(sessionManager.getRegionID())){
                    positionCurrent = i +1;
                }
            }

            for (int i=0; i<response.length();i++){
                listRegion.add(response.getJSONObject(i).getString("RegionName"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        final ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, listRegion);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerRegion.setAdapter(dataAdapter);
        spinnerRegion.setSelection(positionCurrent);
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
