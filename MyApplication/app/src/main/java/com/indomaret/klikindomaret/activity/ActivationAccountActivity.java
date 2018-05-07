package com.indomaret.klikindomaret.activity;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.indomaret.klikindomaret.API;
import com.indomaret.klikindomaret.R;
import com.indomaret.klikindomaret.app.AppController;
import com.indomaret.klikindomaret.helper.SessionManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ActivationAccountActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private SessionManager sessionManager;
    private Intent intent;
    private String activationCode, token, email, emailText, passwordText;
    private static final int READ_CONTACT_REQUEST_CODE = 1;

    private TextView textEmail, resendEmail;
    private EditText activationCode1, activationCode2, activationCode3, activationCode4;
    private Button btnActivation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activation_account);

        sessionManager = new SessionManager(ActivationAccountActivity.this);
        intent = getIntent();
        emailText = intent.getStringExtra("email");
        passwordText = intent.getStringExtra("pass");

        textEmail = (TextView) findViewById(R.id.text_email);
        resendEmail = (TextView) findViewById(R.id.resend_email);
        activationCode1 = (EditText) findViewById(R.id.activation_code_1);
        activationCode2 = (EditText) findViewById(R.id.activation_code_2);
        activationCode3 = (EditText) findViewById(R.id.activation_code_3);
        activationCode4 = (EditText) findViewById(R.id.activation_code_4);
        btnActivation = (Button) findViewById(R.id.btn_activation);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED ) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.READ_CONTACTS}, READ_CONTACT_REQUEST_CODE);
        }

        jsonArrayRequest(API.getInstance().getProfileId()+"?id="+sessionManager.getResponseId(), "profile");
        resendEmail.setText(Html.fromHtml("<u><b>Kirim ulang email aktivasi</b></u>"));

        resendEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    JSONObject emailObject = new JSONObject();
                    emailObject.put("Email", email);

                    makeJsonPost(API.getInstance().getApiResendActivation()+"?mfp_id="+sessionManager.getKeyMfpId(), emailObject, "resend");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        btnActivation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    JSONObject tokenObject = new JSONObject();
                    activationCode = activationCode1.getText().toString() + activationCode2.getText().toString() +
                            activationCode3.getText().toString() + activationCode4.getText().toString();

                    tokenObject.put("Token", token);
                    tokenObject.put("PINCode", activationCode);

                    if (activationCode.length() < 4){
                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ActivationAccountActivity.this);
                        alertDialogBuilder.setMessage("Kode OTP tidak valid.");

                        alertDialogBuilder.setPositiveButton("Tutup", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface arg0, int arg1) {
                            }
                        });

                        final AlertDialog alertDialog = alertDialogBuilder.create();
                        alertDialog.setCanceledOnTouchOutside(false);
                        alertDialog.show();
                    }else {
                        makeJsonPost(API.getInstance().getApiActivation()+"?mfp_id="+sessionManager.getKeyMfpId(), tokenObject, "activation");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        activationCode1.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {

            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(count > 0) {
                    activationCode2.requestFocus();
                }
            }
        });

        activationCode2.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {

            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(count > 0) {
                    activationCode3.requestFocus();
                } else if(count == 0) {
                    activationCode1.requestFocus();
                }
            }
        });

        activationCode3.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {

            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(count > 0) {
                    activationCode4.requestFocus();
                } else if(count == 0) {
                    activationCode2.requestFocus();
                }
            }
        });

        activationCode4.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {

            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(count == 0) {
                    activationCode3.requestFocus();
                }
            }
        });
    }

    public void jsonArrayRequest(String url, final String type){
        System.out.println("url get profile = " + url);

        JsonArrayRequest jsonArrayReq = new JsonArrayRequest(url,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            if (response == null || response.length() == 0){
                                Toast.makeText(ActivationAccountActivity.this, "Gagal terkoneksi server", Toast.LENGTH_LONG).show();
                            }else{
                                if (type.equals("profile")){
                                    System.out.println("responese activation : "+response.getJSONObject(0));
                                    email = response.getJSONObject(0).getString("Email");
                                    token = response.getJSONObject(0).getString("ActivationToken");

                                    textEmail.setText(Html.fromHtml("Terima kasih sudah mendaftar, untuk petunjuk dan kode aktivasi telah dikirim ke "
                                            + "<b>"+email+"</b>"
                                            +". Jika tidak menerima pesan baru coba cek folder spam/junk di email Anda."));
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
                Toast.makeText(ActivationAccountActivity.this, "Gagal terkoneksi server", Toast.LENGTH_LONG).show();
            }
        }, this);

        AppController.getInstance().addToRequestQueue(jsonArrayReq);
    }

    public void makeJsonPost(String urlJsonObj, JSONObject jsonObject, final String type){
        System.out.println("token url : "+urlJsonObj);
        System.out.println("token object : "+jsonObject);

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST, urlJsonObj, jsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if (response == null || response.length() == 0){
                                Toast.makeText(ActivationAccountActivity.this, "Gagal terkoneksi server", Toast.LENGTH_LONG).show();
                            }else{
                                final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ActivationAccountActivity.this);

                                if (type.equals("activation")){
                                    if (response.getString("Message").equals("Success")){
                                        finish();
                                        intent = new Intent(ActivationAccountActivity.this, DirectLoginActivity.class);
                                        intent.putExtra("email", emailText);
                                        intent.putExtra("pass", passwordText);
                                        intent.putExtra("token", token);
                                        intent.putExtra("manual", true);

                                        startActivity(intent);
                                        overridePendingTransition(R.anim.left_in, R.anim.left_out);
                                    }else if(response.getString("Message").contains("sudah diaktifkan")){
                                        alertDialogBuilder.setMessage("Akun anda sudah diaktifkan.");

                                        alertDialogBuilder.setPositiveButton("Tutup", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface arg0, int arg1) {
                                                finish();
                                                intent = new Intent(ActivationAccountActivity.this, DirectLoginActivity.class);
                                                intent.putExtra("email", emailText);
                                                intent.putExtra("pass", passwordText);
                                                intent.putExtra("token", token);
                                                intent.putExtra("manual", true);

                                                startActivity(intent);
                                                overridePendingTransition(R.anim.left_in, R.anim.left_out);
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
                                } else if (type.equals("resend")){
                                    if (response.getString("Message").equals("Success")){
                                        alertDialogBuilder.setMessage("Email aktivasi sudah dikirim ulang, silahkan cek email Anda untuk aktivasi akun. " +
                                                "Jika tidak menerima pesan baru coba cek folder spam/junk di email Anda.");

                                        alertDialogBuilder.setPositiveButton("Tutup", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface arg0, int arg1) {
                                            }
                                        });

                                        final AlertDialog alertDialog = alertDialogBuilder.create();
                                        alertDialog.setCanceledOnTouchOutside(false);
                                        alertDialog.show();
                                    } else {
                                        alertDialogBuilder.setMessage(response.getString("Message"));

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
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(ActivationAccountActivity.this, "Gagal terkoneksi server", Toast.LENGTH_LONG).show();
            }
        });

        AppController.getInstance().addToRequestQueue(jsonObjReq);
    }

    public void onBackPressed() {
//        if (activationCode1.getText().toString().length() > 0){
            finish();
            overridePendingTransition(R.anim.right_in, R.anim.right_out);
//        }
    }

    @Override
    public void onResume(){
        super.onResume();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED ) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.READ_SMS}, READ_CONTACT_REQUEST_CODE);
        }
    }
}
