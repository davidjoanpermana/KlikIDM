package com.indomaret.klikindomaret.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.multidex.MultiDex;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.indomaret.klikindomaret.API;
import com.indomaret.klikindomaret.R;
import com.indomaret.klikindomaret.adapter.MyTicketAdapter;
import com.indomaret.klikindomaret.app.AppController;
import com.indomaret.klikindomaret.helper.Encode2;
import com.indomaret.klikindomaret.helper.SessionManager;
import com.indomaret.klikindomaret.views.HeightAdjustableListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

public class MyTicketListActivity extends AppCompatActivity {
    private Intent intent;
    private SessionManager sessionManager;
    private HeightAdjustableListView listTicketKAI;
    private JSONArray listTIcket = new JSONArray();
    private JSONObject dataObject;
    private MyTicketAdapter myTicketAdapter;
    private String isPassed, from;
    private Button btnSend;
    private RelativeLayout preloader;
    private LinearLayout linearEmptyTicket;
    private Encode2 encode = new Encode2();
    private Tracker mTracker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        MultiDex.install(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_ticket_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        intent = getIntent();
        sessionManager = new SessionManager(this);

        //google analytic
        AppController application = (AppController) getApplication();
        mTracker = application.getDefaultTracker();
        mTracker.setScreenName("History ticket page");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());

        //facebook pixel
        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);
        AppEventsLogger logger = AppEventsLogger.newLogger(this);
        logger.logEvent("History ticket page");

        listTicketKAI = (HeightAdjustableListView) findViewById(R.id.list_ticket);
        preloader = (RelativeLayout) findViewById(R.id.preloader);
        linearEmptyTicket = (LinearLayout) findViewById(R.id.linear_empty_ticket);
        btnSend = (Button) findViewById(R.id.btn_send);
        from = intent.getStringExtra("from");

        preloader.setVisibility(View.VISIBLE);

        if (from.equals("main")){
            isPassed = intent.getStringExtra("isPassed");
            requestWithSomeHttpHeaders(API.getInstance().getApiSalesByCustId()
                    +"?ID="+sessionManager.getUserID()+"&isPassed=" + isPassed, "passenger");
        }else {
            try {
                dataObject = new JSONObject(intent.getStringExtra("dataObject"));

                myTicketAdapter = new MyTicketAdapter(MyTicketListActivity.this, dataObject.getJSONArray("Data"));
                listTicketKAI.setAdapter(myTicketAdapter);

                preloader.setVisibility(View.GONE);
            } catch (JSONException e) {
                e.printStackTrace();
                preloader.setVisibility(View.GONE);
            }
        }

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(MyTicketListActivity.this, HomeKAIActivity.class);
                intent.putExtra("from", "");
                finish();
                startActivity(intent);
                overridePendingTransition(R.anim.left_in, R.anim.left_out);
            }
        });
    }

    public void requestWithSomeHttpHeaders(String url, final String type) {
        RequestQueue queue = Volley.newRequestQueue(this);
        System.out.println("--- url kai : "+url);
        StringRequest postRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>(){
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jObject = new JSONObject(response);
                    if (jObject.getBoolean("IsSuccess")) {
                        listTIcket = jObject.getJSONArray("Data");

                        if (listTIcket.length() > 0){
                            listTicketKAI.setVisibility(View.VISIBLE);
                            linearEmptyTicket.setVisibility(View.GONE);

                            myTicketAdapter = new MyTicketAdapter(MyTicketListActivity.this, listTIcket);
                            listTicketKAI.setAdapter(myTicketAdapter);
                        }else{
                            listTicketKAI.setVisibility(View.GONE);
                            linearEmptyTicket.setVisibility(View.VISIBLE);
                        }
                    }

                    preloader.setVisibility(View.GONE);
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(MyTicketListActivity.this, "Gagal terkoneksi server", Toast.LENGTH_LONG).show();
                    preloader.setVisibility(View.GONE);
                }
            }
        },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(MyTicketListActivity.this, "Gagal terkoneksi server", Toast.LENGTH_LONG).show();
                        preloader.setVisibility(View.GONE);
                    }
                }
        ) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String>  params = new HashMap<String, String>();
                String token = "";
                try {
                    token = encode.SHA1(encode.md5("66E2C13840534C139D85CEE1B433C1FX"));
                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }

                params.put("ApplicationKey", "indomaret");
                params.put("Authorization", "bearer "+token+"#"+sessionManager.getUserID());

                return params;
            }
        };

        postRequest.setRetryPolicy(new DefaultRetryPolicy(50000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(postRequest);
    }
}

