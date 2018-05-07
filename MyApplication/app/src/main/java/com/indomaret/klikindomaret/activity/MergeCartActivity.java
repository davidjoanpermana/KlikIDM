package com.indomaret.klikindomaret.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.indomaret.klikindomaret.API;
import com.indomaret.klikindomaret.R;
import com.indomaret.klikindomaret.adapter.MergeCartAdapter;
import com.indomaret.klikindomaret.app.AppController;
import com.indomaret.klikindomaret.helper.SessionManager;
import com.indomaret.klikindomaret.helper.UncaughtExceptionHandler;
import com.indomaret.klikindomaret.views.HeightAdjustableListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import pl.droidsonroids.gif.GifImageView;

public class MergeCartActivity extends AppCompatActivity {
    RelativeLayout preloader;
    HeightAdjustableListView newCarts, oldCarts;

    SessionManager sessionManager;
    MergeCartAdapter mergeCartAdapter;
    Intent intent;
    Button yesUpdate, noUpdate;

    JSONArray oldCart;
    JSONArray newCart;
    JSONObject mergeResponse;

    private Toolbar toolbar;
    private TextView mTitle;

    private Tracker mTracker;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_merge_cart);

        UncaughtExceptionHandler uncaughtExceptionHandler = new UncaughtExceptionHandler();
        uncaughtExceptionHandler.uncaughtException(this);

        toolbar = (Toolbar) findViewById(R.id.app_toolbar);
        mTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
        mTitle.setText("Gabungkan Keranjang");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        AppController application = (AppController) getApplication();
        mTracker = application.getDefaultTracker();

        mTracker.setScreenName("Merge Cart Page");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());

        sessionManager = new SessionManager(MergeCartActivity.this);

        preloader = (RelativeLayout) findViewById(R.id.preloader);

        newCarts = (HeightAdjustableListView) findViewById(R.id.new_cart);
        oldCarts = (HeightAdjustableListView) findViewById(R.id.old_cart);

        yesUpdate = (Button) findViewById(R.id.yes_update);
        noUpdate = (Button) findViewById(R.id.no_update);

        JSONObject cartObject = new JSONObject();
        JSONArray itemArray = new JSONArray();

        try {
            cartObject.put("CartId", sessionManager.getCartId());
            cartObject.put("NewCartId", "00000000-0000-0000-0000-000000000000");
            cartObject.put("OldCustomerId", "00000000-0000-0000-0000-000000000000");
            cartObject.put("NewCustomerID", sessionManager.getUserID());
            cartObject.put("Items", itemArray);
            cartObject.put("oldCart", null);
            cartObject.put("newCart", null);
            cartObject.put("DeleteOld", false);
            cartObject.put("RegionId", sessionManager.getRegionID());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        makeJsonPost(API.getInstance().getApiGetMerge()+"?mfp_id=" + sessionManager.getKeyMfpId() +"&regionID="+sessionManager.getRegionID(), cartObject, "merge");

        yesUpdate.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        JSONObject cartObject = new JSONObject();
                        JSONArray itemArray = new JSONArray();

                        try {
                            cartObject.put("CartId", sessionManager.getCartId());
                            cartObject.put("NewCartId", mergeResponse.getString("NewCartId"));
                            cartObject.put("OldCustomerId", "00000000-0000-0000-0000-000000000000");
                            cartObject.put("NewCustomerID", mergeResponse.getString("NewCustomerID"));
                            cartObject.put("Items", itemArray);
                            cartObject.put("oldCart", null);
                            cartObject.put("newCart", null);
                            cartObject.put("DeleteOld", false);
                            cartObject.put("RegionId", sessionManager.getRegionID());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        makeJsonPost(API.getInstance().getApiUpdateCustomer()+"?mfp_id=" + sessionManager.getKeyMfpId() +"&regionID="+sessionManager.getRegionID(), cartObject, "update");
                    }
                }
        );

        noUpdate.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        JSONObject cartObject = new JSONObject();
                        JSONArray itemArray = new JSONArray();

                        try {
                            cartObject.put("CartId", sessionManager.getCartId());
                            cartObject.put("NewCartId", mergeResponse.getString("NewCartId"));
                            cartObject.put("OldCustomerId", "00000000-0000-0000-0000-000000000000");
                            cartObject.put("NewCustomerID", mergeResponse.getString("NewCustomerID"));
                            cartObject.put("Items", itemArray);
                            cartObject.put("oldCart", null);
                            cartObject.put("newCart", null);
                            cartObject.put("DeleteOld", true);
                            cartObject.put("RegionId", sessionManager.getRegionID());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        makeJsonPost(API.getInstance().getApiUpdateCustomer()+"?mfp_id=" + sessionManager.getKeyMfpId() +"&regionID="+sessionManager.getRegionID(), cartObject, "update");
                    }
                }
        );

    }

    // POST OBJECT
    public void makeJsonPost(String urlJsonObj, JSONObject jsonObject, final String type){
        System.out.println("url + " + urlJsonObj);
        System.out.println("url + " + jsonObject);
        System.out.println("url + " + type);

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                urlJsonObj, jsonObject,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        if (response == null || response.length() == 0){
                            preloader.setVisibility(View.GONE);
                            Toast.makeText(MergeCartActivity.this, "Gagal terkoneksi server", Toast.LENGTH_LONG).show();
                        }else{
                            if(type.equals("merge")){
                                oldNewcartResponse(response);
                            }

                            if(type.equals("update")){
                                updateCustomerResponse(response);
                            }
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MergeCartActivity.this, "Gagal terkoneksi server", Toast.LENGTH_LONG).show();
                preloader.setVisibility(View.GONE);
            }
        });

        AppController.getInstance().addToRequestQueue(jsonObjReq);
    }

    public void updateCustomerResponse(JSONObject response){
        System.out.println("update" + response);
        try {
            if(response.getBoolean("IsSuccess")){
                intent = new Intent(MergeCartActivity.this, CartActivity.class);
                startActivity(intent);
                finish();
                overridePendingTransition(R.anim.right_out, R.anim.right_in);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void oldNewcartResponse(JSONObject response){
        try {
            System.out.println("merge" + response);

            mergeResponse = response.getJSONArray("ResponseObject").getJSONObject(0);

            newCart = response.getJSONArray("ResponseObject").getJSONObject(0).getJSONObject("oldCart").getJSONArray("ShoppingCartItems");
            oldCart = response.getJSONArray("ResponseObject").getJSONObject(0).getJSONObject("newCart").getJSONArray("ShoppingCartItems");

            mergeCartAdapter = new MergeCartAdapter(MergeCartActivity.this, oldCart);
            oldCarts.setAdapter(mergeCartAdapter);

            mergeCartAdapter = new MergeCartAdapter(MergeCartActivity.this, newCart);
            newCarts.setAdapter(mergeCartAdapter);

            preloader.setVisibility(View.GONE);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
