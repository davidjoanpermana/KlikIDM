package com.indomaret.klikindomaret.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.multidex.MultiDex;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.indomaret.klikindomaret.API;
import com.indomaret.klikindomaret.R;
import com.indomaret.klikindomaret.adapter.ProfileKAIAdapter;
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

public class ProfileKAIActivity extends AppCompatActivity {
    private SessionManager sessionManager;
    private Intent intent;
    private ProfileKAIAdapter profileKAIAdapter;
    private HeightAdjustableListView passengerList;
    private Button btnAddAdult, btnAddInfant;
    private RelativeLayout preloader;
    private Encode2 encode = new Encode2();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        MultiDex.install(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_kai);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        sessionManager = new SessionManager(ProfileKAIActivity.this);

        passengerList = (HeightAdjustableListView) findViewById(R.id.list_passenger);
        btnAddAdult = (Button) findViewById(R.id.btn_add_adult);
        btnAddInfant = (Button) findViewById(R.id.btn_add_infant);
        preloader = (RelativeLayout) findViewById(R.id.preloader);

        showPassenger();

        btnAddAdult.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(ProfileKAIActivity.this, NewProfileKAIActivity.class);
                intent.putExtra("type", "newAdult");
                startActivity(intent);
                overridePendingTransition(R.anim.left_in, R.anim.left_out);
            }
        });

        btnAddInfant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(ProfileKAIActivity.this, NewProfileKAIActivity.class);
                intent.putExtra("type", "newInfant");
                startActivity(intent);
                overridePendingTransition(R.anim.left_in, R.anim.left_out);
            }
        });
    }

    public void showPassenger(){
        preloader.setVisibility(View.VISIBLE);
        requestWithSomeHttpHeaders(API.getInstance().getApiGetPassenger()
                +"?ID="+sessionManager.getUserID(), "passenger");
    }

    public void setPassengerList(JSONArray jsonArray){
        profileKAIAdapter = new ProfileKAIAdapter(this, jsonArray);
        passengerList.setAdapter(profileKAIAdapter);
    }

    public void requestWithSomeHttpHeaders(String url, final String type) {
        RequestQueue queue = Volley.newRequestQueue(this);
        System.out.println("--- passenger url : "+url);
        StringRequest postRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {
                        try {
                            if (response == null || response.length() == 0){
                                preloader.setVisibility(View.GONE);
                                Toast.makeText(ProfileKAIActivity.this, "Gagal terkoneksi server", Toast.LENGTH_LONG).show();
                            }else{
                                JSONObject jObject = new JSONObject(response);
                                if (jObject.getBoolean("IsSuccess")) {
                                    setPassengerList(jObject.getJSONArray("Data"));
                                }

                                preloader.setVisibility(View.GONE);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            preloader.setVisibility(View.GONE);
                            Toast.makeText(ProfileKAIActivity.this, "Gagal terkoneksi server", Toast.LENGTH_LONG).show();
                        }
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        preloader.setVisibility(View.GONE);
                        Toast.makeText(ProfileKAIActivity.this, "Gagal terkoneksi server", Toast.LENGTH_LONG).show();
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
        queue.add(postRequest);
    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.right_in, R.anim.right_out);
    }

    @Override
    public void onResume(){
        super.onResume();
        showPassenger();
    }
}
