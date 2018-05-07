package com.indomaret.klikindomaret.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.indomaret.klikindomaret.API;
import com.indomaret.klikindomaret.R;
import com.indomaret.klikindomaret.adapter.NotificationAdapter;
import com.indomaret.klikindomaret.app.AppController;
import com.indomaret.klikindomaret.helper.SessionManager;
import com.indomaret.klikindomaret.views.HeightAdjustableListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class NotificationActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private SessionManager sessionManager;
    private HeightAdjustableListView notifList;
    private RelativeLayout preloader, cover;

    private TextView mTitle;
    private JSONObject notifObject;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        toolbar = (Toolbar) findViewById(R.id.app_toolbar);
        toolbar.setTitle("");
        mTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
        mTitle.setText("Notifikasi");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        sessionManager = new SessionManager(this);

        notifList = (HeightAdjustableListView) findViewById(R.id.list_notif);
        preloader = (RelativeLayout) findViewById(R.id.preloader);
        cover = (RelativeLayout) findViewById(R.id.cover);

        preloader.setVisibility(View.VISIBLE);
        cover.setVisibility(View.VISIBLE);

        makeJsonObjectGet(API.getInstance().getApiGetProfile()+"?access_token="+sessionManager.getResponseId()+"&mfp_id="+sessionManager.getResponseId());
    }

    public void makeJsonObjectGet(String url){
        System.out.println("address url = " + url);

        JsonArrayRequest jsonArrayReq = new JsonArrayRequest(url,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        if (response == null || response.length() == 0){
                            Toast.makeText(NotificationActivity.this, "Gagal terkoneksi server", Toast.LENGTH_LONG).show();
                        }else{
                            prossessNotif(response);
                        }

                        preloader.setVisibility(View.GONE);
                        cover.setVisibility(View.GONE);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(NotificationActivity.this, "Gagal terkoneksi server", Toast.LENGTH_LONG).show();
                preloader.setVisibility(View.GONE);
                cover.setVisibility(View.GONE);
            }
        }, this);

        jsonArrayReq.setRetryPolicy(new DefaultRetryPolicy(60000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppController.getInstance().addToRequestQueue(jsonArrayReq);
    }

    public void jsonPost(String urlJsonObj, JSONObject jsonObject){
        System.out.println("update url= " + urlJsonObj);
        System.out.println("update = object" + jsonObject);

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                urlJsonObj, jsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(final JSONObject response) {
                        if (response == null || response.length() == 0){
                            Toast.makeText(NotificationActivity.this, "Gagal terkoneksi server", Toast.LENGTH_LONG).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(NotificationActivity.this, "Gagal terkoneksi server", Toast.LENGTH_LONG).show();
                preloader.setVisibility(View.GONE);
                cover.setVisibility(View.GONE);
            }
        });

        AppController.getInstance().addToRequestQueue(jsonObjReq);
    }

    public void prossessNotif(JSONArray response){
        notifObject = new JSONObject();

        try {
            NotificationAdapter notificationAdapter = new NotificationAdapter(this, response.getJSONObject(0).getJSONArray("CustNotifications"));
            notifList.setAdapter(notificationAdapter);

            notifObject.put("ID", sessionManager.getUserID());
            jsonPost(API.getInstance().getApiSetNotif()+ "?mfp_id=" + sessionManager.getKeyMfpId(), notifObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }
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

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.right_in, R.anim.right_out);
    }
}
