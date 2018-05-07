package com.indomaret.klikindomaret.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.indomaret.klikindomaret.API;
import com.indomaret.klikindomaret.MainActivity;
import com.indomaret.klikindomaret.R;
import com.indomaret.klikindomaret.app.AppController;
import com.indomaret.klikindomaret.helper.SessionManager;
import com.indomaret.klikindomaret.helper.UncaughtExceptionHandler;

import org.json.JSONException;
import org.json.JSONObject;

public class ForgotPasswordActivity extends AppCompatActivity {
    private SessionManager sessionManager;
    private Intent intent;

    private Toolbar toolbar;
    private TextView mTitle, btnHelp;
    private EditText inputEmail;
    private Button btnForgotPassword;
    private String api;
    private Tracker mTracker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        UncaughtExceptionHandler uncaughtExceptionHandler = new UncaughtExceptionHandler();
        uncaughtExceptionHandler.uncaughtException(this);
        intent = getIntent();

        AppController application = (AppController) getApplication();
        mTracker = application.getDefaultTracker();

        mTracker.setScreenName("Forgot Password Page");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());

        toolbar = (Toolbar) findViewById(R.id.app_toolbar);
        mTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
        mTitle.setText("Lupa Kata Sandi");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        sessionManager = new SessionManager(ForgotPasswordActivity.this);

        inputEmail = (EditText) findViewById(R.id.input_email);
        btnHelp = (TextView) findViewById(R.id.btn_help);
        btnForgotPassword = (Button) findViewById(R.id.btn_forgot_password);

        btnHelp.setText(Html.fromHtml("<u><b>Hubungi Layanan Pelanggan</b></u>"));
        api = API.getInstance().getApiForgotPasswordMobile()+"?mfp_id="+sessionManager.getKeyMfpId()+"&device_token="+sessionManager.getDeviceToken();

        btnHelp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse(API.getInstance().getLayananPelangganUrl()));
                startActivity(intent);
            }
        });

        btnForgotPassword.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        android.support.v7.app.AlertDialog.Builder alertDialogBuilder = new android.support.v7.app.AlertDialog.Builder(ForgotPasswordActivity.this);
                        alertDialogBuilder.setNegativeButton("Tutup", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface arg0, int arg1) {

                            }
                        });

                        if(inputEmail.getText().toString().isEmpty()){
                            alertDialogBuilder.setMessage("Harap isi Alamat Email");
                            android.support.v7.app.AlertDialog alertDialog = alertDialogBuilder.create();
                            alertDialog.show();
                            return;
                        }

                        JSONObject emailObject = new JSONObject();
                        try {
                            emailObject.put("Email", inputEmail.getText().toString());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        makeJsonPost(api, emailObject);
                    }
                }
        );
    }

    //send email data
    public void makeJsonPost(String urlJsonObj, JSONObject jsonObject){
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                urlJsonObj, jsonObject,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        if (response == null || response.length() == 0){
                            Toast.makeText(ForgotPasswordActivity.this, "Gagal terkoneksi server", Toast.LENGTH_LONG).show();
                        }else{
                            processResponse(response);
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(ForgotPasswordActivity.this, "Gagal terkoneksi server", Toast.LENGTH_LONG).show();
            }
        });
        AppController.getInstance().addToRequestQueue(jsonObjReq);
    }

    //process login respon
    public void processResponse(JSONObject response){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        try {
            if(response.getString("Message").equals("")){
                builder.setMessage("Tautan untuk melakukan perubahan kata sandi sudah kami kirim ke email Anda");
            } else if(response.getString("Message").contains("15")){
                builder.setMessage("Tunggu 15 menit untuk menggunakan kembali fasilitas lupa kata sandi");
            } else if(response.getString("Message").contains("register")){
                builder.setMessage("Email yang Anda masukkan belum terdaftar");
            } else {
                builder.setMessage("Link untuk menyetel ulang Password Anda telah dikirimkan ke e-mail Anda. Silahkan periksa e-mail Anda.");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        builder.setNegativeButton("Tutup", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                intent = new Intent(ForgotPasswordActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
                overridePendingTransition(R.anim.right_in, R.anim.right_out);
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
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
