package com.indomaret.klikindomaret.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.indomaret.klikindomaret.API;
import com.indomaret.klikindomaret.R;
import com.indomaret.klikindomaret.helper.Encode2;
import com.indomaret.klikindomaret.helper.SessionManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

public class CekTicketActivity extends AppCompatActivity {
    private Intent intent;
    private SessionManager sessionManager;
    private EditText bookingCode, email;
    private Button btnCek;
    private JSONObject jsonObject = new JSONObject();
    private Encode2 encode = new Encode2();
    private RelativeLayout preloader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cek_ticket);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        intent = getIntent();
        sessionManager = new SessionManager(CekTicketActivity.this);

        bookingCode = (EditText) findViewById(R.id.booking_code);
        email = (EditText) findViewById(R.id.email);
        btnCek = (Button) findViewById(R.id.btn_cek);
        preloader = (RelativeLayout) findViewById(R.id.preloader);

        btnCek.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                jsonObject = new JSONObject();
                preloader.setVisibility(View.VISIBLE);

                try {
                    if (bookingCode.getText().toString().equals("")){
                        Toast.makeText(CekTicketActivity.this, "Kode Pesanan tidak boleh kosong", Toast.LENGTH_SHORT).show();
                        preloader.setVisibility(View.GONE);
                        return;
                    }else if (email.getText().toString().equals("")){
                        Toast.makeText(CekTicketActivity.this, "Email tidak boleh kosong", Toast.LENGTH_SHORT).show();
                        preloader.setVisibility(View.GONE);
                        return;
                    } else{
                        jsonObject.put("Email", email.getText().toString());
                        jsonObject.put("BookingCode", bookingCode.getText().toString());

                        jsonPost(API.getInstance().getApiTicketByEmailBooingCode() + "?mfp_id=" + sessionManager.getKeyMfpId(), jsonObject, "");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    preloader.setVisibility(View.GONE);
                }
            }
        });
    }

    public void jsonPost(String urlJsonObj, JSONObject jsonObject, final String type) {
        RequestQueue queue = Volley.newRequestQueue(this);
        System.out.println("--- cek ticket url : "+urlJsonObj);
        System.out.println("--- cek ticket obj : "+jsonObject);
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                urlJsonObj, jsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if (response == null || response.length() == 0){
                                preloader.setVisibility(View.GONE);
                                Toast.makeText(CekTicketActivity.this, "Gagal terkoneksi server", Toast.LENGTH_LONG).show();
                            }else{
                                if (response.getString("Message").equals("Success")){
                                    intent = new Intent(CekTicketActivity.this, MyTicketListActivity.class);
                                    intent.putExtra("dataObject", response.toString());
                                    intent.putExtra("from", "cek");
                                    finish();
                                    startActivity(intent);
                                    overridePendingTransition(R.anim.left_in, R.anim.left_out);
                                }else{
                                    Toast.makeText(CekTicketActivity.this, response.getString("Message"), Toast.LENGTH_SHORT).show();
                                    preloader.setVisibility(View.GONE);
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            preloader.setVisibility(View.GONE);
                        }
                    }
                },
                new Response.ErrorListener(){
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(CekTicketActivity.this, "Gagal terkoneksi server", Toast.LENGTH_LONG).show();
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
                params.put("Authorization", "bearer "+token);
                System.out.println("-- token : "+"bearer "+token);

                return params;
            }
        };
        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(50000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(jsonObjReq);
    }
}
