package com.indomaret.klikindomaret.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
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
import com.indomaret.klikindomaret.app.AppController;
import com.indomaret.klikindomaret.helper.SQLiteHandler;
import com.indomaret.klikindomaret.helper.SessionManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import pl.droidsonroids.gif.GifImageView;

public class DirectLoginActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private Intent intent;
    private String token, codePin, email, pass;
    private boolean manual;
    private SessionManager sessionManager;
    private SQLiteHandler sqLiteHandler;

    private TextView emailText;
    private Button btnSkip;
    private RelativeLayout preloader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_direct_login);

        toolbar = (Toolbar) findViewById(R.id.app_toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        sessionManager = new SessionManager(DirectLoginActivity.this);
        sqLiteHandler = new SQLiteHandler(this);

        intent = getIntent();
        token = intent.getStringExtra("token");
        if (intent.getStringExtra("codePin") != null) codePin = intent.getStringExtra("codePin");
        email = intent.getStringExtra("email");
        pass = intent.getStringExtra("pass");
        manual = intent.getBooleanExtra("manual", false);

        btnSkip = (Button) findViewById(R.id.btn_skip);
        preloader = (RelativeLayout) findViewById(R.id.preloader);
        emailText = (TextView) findViewById(R.id.text_email);

        jsonArrayRequest(API.getInstance().getProfileId()+"?id="+sessionManager.getResponseId(), "profile");

        btnSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(DirectLoginActivity.this, VerificationPhoneActivity.class);
                intent.putExtra("type", "otpUser");
                startActivity(intent);
                overridePendingTransition(R.anim.left_in, R.anim.left_out);
            }
        });

        if (!manual){
            getActivation();
        } else {
            getLogin();
        }
    }

    public void getActivation(){
        try {
            JSONObject tokenObject = new JSONObject();
            tokenObject.put("Token", token);
            tokenObject.put("pinCode", codePin);
            makeJsonPost(API.getInstance().getApiActivation()+"?mfp_id="+sessionManager.getKeyMfpId(), tokenObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void getLogin(){
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("Email", email);
            jsonObject.put("Password", pass);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        preloader.setVisibility(View.VISIBLE);

        if (manual){
            makeJsonPostLogin(API.getInstance().getApiLoginMobile()+"?device_token="+sessionManager.getDeviceToken()+"&mfp_id="+sessionManager.getKeyMfpId()+"&isMobile=true", jsonObject);
        }else{
            makeJsonPostLogin(API.getInstance().getApiLogin()+"?device_token="+sessionManager.getDeviceToken()+"&mfp_id="+sessionManager.getKeyMfpId()+"&isMobile=true", jsonObject);
        }
    }

    public void jsonArrayRequest(String url, final String type){
        System.out.println("url shipping = " + url);

        JsonArrayRequest jsonArrayReq = new JsonArrayRequest(url,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            if (response == null || response.length() == 0){
                                preloader.setVisibility(View.GONE);
                                Toast.makeText(DirectLoginActivity.this, "Gagal terkoneksi server", Toast.LENGTH_LONG).show();
                            }else{
                                if (type.equals("profile")){
                                    email = response.getJSONObject(0).getString("Email");
                                    pass = response.getJSONObject(0).getString("Password");
                                    token = response.getJSONObject(0).getString("ActivationToken");

                                    if (email == null || email.toLowerCase().equals("null") || email.length() == 0){
                                        emailText.setText("Aktivasi akun Anda berhasil.");
                                    }else{
                                        emailText.setText(Html.fromHtml("Aktivasi akun "
                                                + "<b>"+email+"</b>"
                                                +" berhasil."));
                                    }
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        System.out.println("email 1 : "+email);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(DirectLoginActivity.this, "Gagal terkoneksi server", Toast.LENGTH_LONG).show();
                preloader.setVisibility(View.GONE);
            }
        }, this);

        AppController.getInstance().addToRequestQueue(jsonArrayReq);
    }

    public void makeJsonPost(String urlJsonObj, JSONObject jsonObject){
        System.out.println("token url : "+urlJsonObj);
        System.out.println("token object : "+jsonObject);

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST, urlJsonObj, jsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if (response == null || response.length() == 0){
                                preloader.setVisibility(View.GONE);
                                Toast.makeText(DirectLoginActivity.this, "Gagal terkoneksi server", Toast.LENGTH_LONG).show();
                            }else{
                                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(DirectLoginActivity.this);
                                if (response.getString("Message").equals("Success")){
                                    getLogin();
                                }else if(response.getString("Message").contains("sudah diaktifkan")){
                                    alertDialogBuilder.setMessage("Akun anda sudah diaktifkan.");

                                    alertDialogBuilder.setPositiveButton("Tutup", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface arg0, int arg1) {
                                        }
                                    });

                                    final AlertDialog alertDialog = alertDialogBuilder.create();
                                    alertDialog.setCanceledOnTouchOutside(false);
                                    alertDialog.show();
                                }else{
                                    alertDialogBuilder.setMessage("Aktivasi Anda gagal. Silahkan klik link aktivasi pada email Anda " +
                                            "kembali atau hub customer care kami.");

                                    alertDialogBuilder.setPositiveButton("Tutup", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface arg0, int arg1) {
                                        }
                                    });

                                    final AlertDialog alertDialog = alertDialogBuilder.create();
                                    alertDialog.setCanceledOnTouchOutside(false);
                                    alertDialog.show();
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            System.out.println("--- error login : "+response);
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(DirectLoginActivity.this, "Gagal terkoneksi server", Toast.LENGTH_LONG).show();
                preloader.setVisibility(View.GONE);
            }
        });

        AppController.getInstance().addToRequestQueue(jsonObjReq);
    }

    public void makeJsonPostLogin (String urlJsonObj, JSONObject jsonObject){
        System.out.println("---token url : "+urlJsonObj);
        System.out.println("---token object : "+jsonObject);

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST, urlJsonObj, jsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        if (response == null || response.length() == 0){
                            preloader.setVisibility(View.GONE);
                            Toast.makeText(DirectLoginActivity.this, "Gagal terkoneksi server", Toast.LENGTH_LONG).show();
                        }else{
                            processResponse(response);
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(DirectLoginActivity.this, "Gagal terkoneksi server", Toast.LENGTH_LONG).show();
                preloader.setVisibility(View.GONE);
            }
        });

        AppController.getInstance().addToRequestQueue(jsonObjReq);
    }

    public void processResponse(JSONObject response){
        Boolean IsSuccess = null;
        try {
            IsSuccess = response.getBoolean("IsSuccess");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if(IsSuccess.equals(true)){
            try {
                System.out.println("--- success login");
                JSONObject object = response.getJSONObject("ResponseObject");

                sessionManager.setLogin(true);
                sessionManager.setResponseId(response.getString("ResponseID"));
                sessionManager.setKeyMfpId(response.getString("ResponseID"));
                sessionManager.setUsername(object.getString("FName"));
                sessionManager.setUserEmail(object.getString("Email"));

                makeJsonObjectGet(API.getInstance().getApiGetProfile()+"?access_token="+sessionManager.getKeyMfpId()+"&mfp_id="+sessionManager.getKeyMfpId());
            } catch (JSONException e) {
                preloader.setVisibility(View.GONE);
                e.printStackTrace();
            }
        } else {
            try {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(DirectLoginActivity.this);
                alertDialogBuilder.setMessage(response.getString("Message"));
                alertDialogBuilder.setNegativeButton("Tutup", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        preloader.setVisibility(View.GONE);;}
                });

                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
            } catch (JSONException e) {
                preloader.setVisibility(View.GONE);
                e.printStackTrace();
            }
        }
    }

    public void makeJsonObjectGet(String url){
        System.out.println("address url = " + url);

        JsonArrayRequest jsonArrayReq = new JsonArrayRequest(url,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        if (response == null || response.length() == 0){
                            preloader.setVisibility(View.GONE);
                            Toast.makeText(DirectLoginActivity.this, "Gagal terkoneksi server", Toast.LENGTH_LONG).show();
                        }else{
                            getProfileRespon(response);
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                preloader.setVisibility(View.GONE);
                Log.i("Error", error.toString());

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(DirectLoginActivity.this);
                alertDialogBuilder.setMessage("Login Gagal");
                alertDialogBuilder.setNegativeButton("Tutup", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {}
                });

                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
            }
        }, this);

        jsonArrayReq.setRetryPolicy(new DefaultRetryPolicy(60000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppController.getInstance().addToRequestQueue(jsonArrayReq);
    }

    public void getProfileRespon(JSONArray response){
        sqLiteHandler.insertProfile(response.toString());

        try {
            sessionManager.setUserID(response.getJSONObject(0).getString("ID"));
            JSONArray address = response.getJSONObject(0).getJSONArray("Address");
//            sessionManager.setKeyMobileVerified(response.getJSONObject(0).getBoolean("MobileVerified"));

            for (int i=0; i<address.length(); i++){
                if(address.getJSONObject(i).getString("IsDefault").equals("true")){
                    sqLiteHandler.insertDefaultAddress(address.getJSONObject(i).toString());
                }
            }

            preloader.setVisibility(View.GONE);

            btnSkip.setEnabled(true);
        } catch (JSONException e) {
            preloader.setVisibility(View.GONE);

            e.printStackTrace();
        }
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
