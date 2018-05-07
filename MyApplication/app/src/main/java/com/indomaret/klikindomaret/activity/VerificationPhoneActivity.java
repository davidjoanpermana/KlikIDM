package com.indomaret.klikindomaret.activity;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
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

import java.util.Date;

import pl.droidsonroids.gif.GifImageView;

public class VerificationPhoneActivity extends AppCompatActivity {
    private SessionManager sessionManager;
    private SQLiteHandler sqLiteHandler;
    private int countOTP = 0;
    private String otpCodes, userProfile, body, otpCode, type;
    private String otp1 = "", otp2 = "", otp3 = "", otp4 = "";
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private boolean stop = false;
    private long millis, timestamp;
    private Intent intent;
    private JSONArray userProfileObjectArray;
    private JSONObject userProfileObject;
    private JSONObject userBankObject = new JSONObject();

    private Button changeNumber, sendOtpCode, saveNumber, cancelNumber, sendVerivication;
    private EditText phoneNumber, otpCode1, otpCode2, otpCode3, otpCode4;
    private TextView verivicationNumber, otp;
    private LinearLayout firstLayout, secondLayout;
    private RelativeLayout profilePreloader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verification_phone);

        sessionManager = new SessionManager(this);
        sqLiteHandler = new SQLiteHandler(this);
        intent = getIntent();
        type = intent.getStringExtra("type");

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED ) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.READ_CONTACTS}, LOCATION_PERMISSION_REQUEST_CODE);
        }

        verivicationNumber = (TextView) findViewById(R.id.verified_number);
        otp = (TextView) findViewById(R.id.otp);
        otpCode1 = (EditText) findViewById(R.id.otp_code_1);
        otpCode2 = (EditText) findViewById(R.id.otp_code_2);
        otpCode3 = (EditText) findViewById(R.id.otp_code_3);
        otpCode4 = (EditText) findViewById(R.id.otp_code_4);
        phoneNumber = (EditText) findViewById(R.id.edittext_phone_number);
        sendOtpCode = (Button) findViewById(R.id.btn_send_code);
        changeNumber = (Button) findViewById(R.id.btn_change_number);
        sendVerivication = (Button) findViewById(R.id.btn_verification_number);
        saveNumber = (Button) findViewById(R.id.btn_change_number_save);
        cancelNumber = (Button) findViewById(R.id.btn_change_number_cancel);
        firstLayout = (LinearLayout) findViewById(R.id.first_layout);
        secondLayout = (LinearLayout) findViewById(R.id.second_layout);
        profilePreloader = (RelativeLayout) findViewById(R.id.profile_preloader_shadow);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED ) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.READ_SMS}, LOCATION_PERMISSION_REQUEST_CODE);
        }

        sendOtpCode.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        profilePreloader.setVisibility(View.VISIBLE);

                        if (countOTP == 0){
                            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(VerificationPhoneActivity.this);
                            alertDialogBuilder.setMessage("Batas permintaan kode OTP sudah habis");
                            alertDialogBuilder.setPositiveButton("Tutup", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface arg0, int arg1) {
                                }
                            });

                            AlertDialog alertDialog = alertDialogBuilder.create();
                            alertDialog.show();
                        }else{
                            if (type.equals("otpUser")){
                                makeJsonObjectGets(API.getInstance().getSendOtp()+"?userID="+sessionManager.getUserID()+"&mfp_id="+sessionManager.getKeyMfpId(), "otp");
                            }else{
                                try {
                                    userBankObject = new JSONObject(intent.getStringExtra("objectBank"));
                                    makeJsonObjectGets(API.getInstance().getSendOtpBank()+"?userID="+sessionManager.getUserID()+"&mfp_id="+sessionManager.getKeyMfpId(), "otp");
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                }
        );

        changeNumber.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        secondLayout.setVisibility(View.GONE);
                        firstLayout.setVisibility(View.VISIBLE);
                        phoneNumber.setFocusableInTouchMode(true);
                    }
                }
        );

        sendVerivication.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        otpCodes = otpCode1.getText().toString() + otpCode2.getText() + otpCode3.getText() + otpCode4.getText();
                        profilePreloader.setVisibility(View.VISIBLE);

                        if (type.equals("otpUser")){
                            makeJsonObjectGets(API.getInstance().getApiValidationOtpCode()+"?userID="+sessionManager.getUserID()
                                    +"&otpCode="+otpCodes
                                    +"&mfp_id="+sessionManager.getKeyMfpId(), "validation");
                        }else{
                            makeJsonObjectGets(API.getInstance().getApiValidationOtpCodeBank()+"?userID="+sessionManager.getUserID()
                                    +"&accountOtpCode="+otpCodes
                                    +"&mfp_id="+sessionManager.getKeyMfpId(), "validation");
                        }
                    }
                }
        );

        saveNumber.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (phoneNumber.getText().toString().length() < 9){
                            Toast.makeText(VerificationPhoneActivity.this, "Format nomor telepon tidak valid", Toast.LENGTH_LONG).show();
                        }else{
                            profilePreloader.setVisibility(View.VISIBLE);
                            makeJsonObjectGets(API.getInstance().getApiChangePhoneNumber()+"?mfp_id="+sessionManager.getKeyMfpId()+"&userID="+sessionManager.getUserID()+"&mobilePhone="+phoneNumber.getText().toString(), "number");
                        }
                    }
                }
        );

        cancelNumber.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        secondLayout.setVisibility(View.VISIBLE);
                        firstLayout.setVisibility(View.GONE);
                        phoneNumber.setFocusable(false);
                    }
                }
        );

        otpCode1.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {

            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(count > 0) {
                    otpCode2.requestFocus();
                }
            }
        });

        otpCode2.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {

            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(count > 0) {
                    otpCode3.requestFocus();
                } else if(count == 0) {
                    otpCode1.requestFocus();
                }
            }
        });

        otpCode3.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {

            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(count > 0) {
                    otpCode4.requestFocus();
                } else if(count == 0) {
                    otpCode2.requestFocus();
                }
            }
        });

        otpCode4.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {

            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(count == 0) {
                    otpCode3.requestFocus();
                }
            }
        });
    }

    public void setPhone(String profile){
        try {
            userProfileObjectArray = new JSONArray(profile);
            userProfileObject = userProfileObjectArray.getJSONObject(0);
            countOTP = 5 - userProfileObject.getInt("AccountOTPCount");

            phoneNumber.setText(userProfileObject.getString("Mobile"));
            otp.setText("*Sisa request OTP Anda "+countOTP+" kali");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void updateProfileResponse(JSONObject response){
        try {
            if(response.getString("IsSuccess").equals("true")){
                countOTP = 5 - response.getJSONObject("ResponseObject").getInt("OTPCount");
                phoneNumber.setText(response.getJSONObject("ResponseObject").getString("Mobile"));
                otp.setText("*Sisa request OTP Anda "+countOTP+ " kali");
                makeJsonObjectGet(API.getInstance().getApiGetProfile()+"?access_token="+sessionManager.getResponseId()+"&mfp_id="+sessionManager.getResponseId(), "number");
            }
        } catch (JSONException e) {
            e.printStackTrace();
            profilePreloader.setVisibility(View.GONE);
        }
    }

    public void makeJsonObjectGets(String url, final String type){
        System.out.println("address url = " + url);

        JsonArrayRequest jsonArrayReq = new JsonArrayRequest(url,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(final JSONArray response) {
                        if (response == null || response.length() == 0){
                            profilePreloader.setVisibility(View.GONE);
                            Toast.makeText(VerificationPhoneActivity.this, "Gagal terkoneksi server", Toast.LENGTH_LONG).show();
                        }else{
                            if(type.equals("number")){
                                try {
                                    if (response.getJSONObject(0).getString("ResponseCode").equals("SUCCESS")){
                                        userProfileObject.put("Password", null);
                                        userProfileObject.put("Mobile", phoneNumber.getText().toString());
                                        makeJsonPost(API.getInstance().getApiUpdateProfile()+"?isMyAccount=true&mfp_id="+sessionManager.getKeyMfpId(), userProfileObject, "prof");
                                        secondLayout.setVisibility(View.VISIBLE);
                                        firstLayout.setVisibility(View.GONE);
                                        phoneNumber.setFocusable(false);
                                    }else{
                                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(VerificationPhoneActivity.this);
                                        alertDialogBuilder.setMessage(response.getJSONObject(0).getString("Message"));
                                        alertDialogBuilder.setPositiveButton("Tutup", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface arg0, int arg1) {
                                                profilePreloader.setVisibility(View.GONE);
                                            }
                                        });

                                        AlertDialog alertDialog = alertDialogBuilder.create();
                                        alertDialog.show();
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            } else if(type.equals("otp")) {
                                makeJsonObjectGet(API.getInstance().getApiGetProfile()+"?access_token="+sessionManager.getResponseId()+"&mfp_id="+sessionManager.getResponseId(), "otp");

                                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(VerificationPhoneActivity.this);
                                alertDialogBuilder.setPositiveButton("Tutup", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface arg0, int arg1) {
                                        secondLayout.setVisibility(View.VISIBLE);
                                        firstLayout.setVisibility(View.GONE);
                                        phoneNumber.setFocusable(false);
                                        profilePreloader.setVisibility(View.GONE);
                                    }
                                });

                                try {
                                    alertDialogBuilder.setMessage(response.getJSONObject(0).getString("Message"));
                                    if (ActivityCompat.checkSelfPermission(VerificationPhoneActivity.this, Manifest.permission.READ_SMS) == PackageManager.PERMISSION_GRANTED ) {
                                        System.out.println("--- read sms");
                                        readSMS();

                                        final CountDownTimer timer = new CountDownTimer(300000, 1000) {
                                            @Override
                                            public void onTick(long millisUntilFinished) {
                                                if (!otp1.equals("") || !otp2.equals("") || !otp3.equals("") || !otp4.equals("")){
                                                    otpCode1.setText(otp1);
                                                    otpCode2.setText(otp2);
                                                    otpCode3.setText(otp3);
                                                    otpCode4.setText(otp4);

                                                    if (!stop){
                                                        otpCodes = otpCode1.getText().toString() + otpCode2.getText() + otpCode3.getText() + otpCode4.getText();
                                                        profilePreloader.setVisibility(View.VISIBLE);

                                                        if (type.equals("otpUser")){
                                                            makeJsonObjectGets(API.getInstance().getApiValidationOtpCode()+"?userID="+sessionManager.getUserID()
                                                                    +"&otpCode="+otpCodes
                                                                    +"&mfp_id="+sessionManager.getKeyMfpId(), "validation");
                                                        }else{
                                                            makeJsonObjectGets(API.getInstance().getApiValidationOtpCodeBank()+"?userID="+sessionManager.getUserID()
                                                                    +"&otpCode="+otpCodes
                                                                    +"&mfp_id="+sessionManager.getKeyMfpId(), "validation");
                                                        }

                                                        stop = true;
                                                    }
                                                }
                                            }

                                            @Override
                                            public void onFinish() {
                                            }
                                        }.start();
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                    profilePreloader.setVisibility(View.GONE);
                                }

                                AlertDialog alertDialog = alertDialogBuilder.create();
                                alertDialog.show();
                            }else if(type.equals("validation")) {
                                try {
                                    if (response.getJSONObject(0).getString("Message").toLowerCase().contains("berhasil")){
                                        verivicationNumber.setText("(Sudah Terverifikasi)");
                                        verivicationNumber.setTextColor(Color.parseColor("#009933"));
                                        otpCode1.setText("");
                                        otpCode2.setText("");
                                        otpCode3.setText("");
                                        otpCode4.setText("");

                                        makeJsonObjectGet(API.getInstance().getApiGetProfile()+"?access_token="+sessionManager.getResponseId()+"&mfp_id="+sessionManager.getResponseId(), "setDataBank");

                                        if (type.equals("otpUser")){
                                            intent = new Intent(VerificationPhoneActivity.this, SuccessVerifivationActivity.class);
                                            finish();
                                            startActivity(intent);
                                            overridePendingTransition(R.anim.left_in, R.anim.left_out);
                                        }
                                    } else {
                                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(VerificationPhoneActivity.this);
                                        alertDialogBuilder.setMessage(response.getJSONObject(0).getString("Message"));
                                        alertDialogBuilder.setPositiveButton("Tutup", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface arg0, int arg1) {

                                            }
                                        });

                                        AlertDialog alertDialog = alertDialogBuilder.create();
                                        alertDialog.show();
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                    profilePreloader.setVisibility(View.GONE);
                                }
                            }

                            profilePreloader.setVisibility(View.GONE);
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(VerificationPhoneActivity.this, "Gagal terkoneksi server", Toast.LENGTH_LONG).show();
                profilePreloader.setVisibility(View.GONE);
            }
        }, this);

        jsonArrayReq.setRetryPolicy(new DefaultRetryPolicy(60000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppController.getInstance().addToRequestQueue(jsonArrayReq);
    }

    public void makeJsonObjectGet(String url, final String type){
         System.out.println("address url = " + url);

        JsonArrayRequest jsonArrayReq = new JsonArrayRequest(url,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            if (response == null || response.length() == 0){
                                profilePreloader.setVisibility(View.GONE);
                                Toast.makeText(VerificationPhoneActivity.this, "Gagal terkoneksi server", Toast.LENGTH_LONG).show();
                            }else{
                                sqLiteHandler.insertProfile(response.toString());
                                JSONArray address = response.getJSONObject(0).getJSONArray("Address");

                                for (int i=0; i<address.length(); i++){
                                    if(address.getJSONObject(i).getString("IsDefault").equals("true")){
                                        sqLiteHandler.insertDefaultAddress(address.getJSONObject(i).toString());
                                    }
                                }


                                setPhone(sqLiteHandler.getProfile());

                                if (type.equals("setDataBank")){
                                    response.getJSONObject(0).put("AccountName", userBankObject.getString("AccountName"));
                                    response.getJSONObject(0).put("Bank", userBankObject.getString("Bank"));
                                    response.getJSONObject(0).put("AccountNumber", userBankObject.getString("AccountNumber"));
                                    response.getJSONObject(0).put("BranchBank", userBankObject.getString("BranchBank"));

                                    sqLiteHandler.insertProfile(response.toString());
                                    finish();
                                }

                                profilePreloader.setVisibility(View.GONE);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            profilePreloader.setVisibility(View.GONE);
                        }

                        profilePreloader.setVisibility(View.GONE);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(VerificationPhoneActivity.this, "Gagal terkoneksi server", Toast.LENGTH_LONG).show();
                profilePreloader.setVisibility(View.GONE);
            }
        }, this);

        jsonArrayReq.setRetryPolicy(new DefaultRetryPolicy(60000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppController.getInstance().addToRequestQueue(jsonArrayReq);
    }

    public void makeJsonPost(String urlJsonObj, JSONObject jsonObject, final String type){
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                urlJsonObj, jsonObject,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        if (response == null || response.length() == 0){
                            profilePreloader.setVisibility(View.GONE);
                            Toast.makeText(VerificationPhoneActivity.this, "Gagal terkoneksi server", Toast.LENGTH_LONG).show();
                        }else{
                            if(type.equals("prof")){
                                updateProfileResponse(response);
                            }
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(VerificationPhoneActivity.this, "Gagal terkoneksi server", Toast.LENGTH_LONG).show();
                profilePreloader.setVisibility(View.GONE);
            }
        });

        AppController.getInstance().addToRequestQueue(jsonObjReq);
    }

    public static String stripNonDigits(final CharSequence input){
        final StringBuilder sb = new StringBuilder(input.length());

        for(int i = 0; i < input.length(); i++){
            final char c = input.charAt(i);

            if(c > 47 && c < 58){
                sb.append(c);
            }
         }

        return sb.toString();
    }

    public void readSMS(){
        Cursor cursor = getContentResolver().query(Uri.parse("content://sms/inbox"), null, null, null, null);

        if (cursor.moveToFirst()) {
            do {
                for(int idx=0; idx < cursor.getColumnCount(); idx++) {
                    timestamp = cursor.getLong(4);
                    body = cursor.getString(12);
                }

                Date dateMessage = new Date(timestamp);
                Date dateNow = new Date(millis);

                if (body.contains("Gunakan kode OTP") && dateMessage.after(dateNow)){
                    otpCode = stripNonDigits(body);

                    otp1 = String.valueOf(otpCode.charAt(0));
                    otp2 = String.valueOf(otpCode.charAt(1));
                    otp3 = String.valueOf(otpCode.charAt(2));
                    otp4 = String.valueOf(otpCode.charAt(3));
                }
            } while (cursor.moveToNext() && cursor.getString(2).equals("INDOMARET"));
        }else{

        }
    }

    public void onBackPressed() {
//        if (otpCode1.getText().toString().length() > 0){
            finish();
//        }

        overridePendingTransition(R.anim.right_in, R.anim.right_out);
    }

    @Override
    public void onResume(){
        super.onResume();

        millis = new Date().getTime();
        userProfile = sqLiteHandler.getProfile();
        setPhone(userProfile);
    }
}
