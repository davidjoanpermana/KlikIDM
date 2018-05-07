package com.indomaret.klikindomaret.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
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

import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegisterActivity extends AppCompatActivity {
    private Toolbar toolbar;

    private TextView validateEmailText, mTitle, btnHome;
    private EditText emailAddress, konfMmailAddress, firstName, lastName, phoneNumber, birthDate, password, confirmationPassword;
    private CheckBox newsletter, privacyPolicy;
    private Button registerButton, btnMan, btnWoman;
    private Tracker mTracker;
    private JSONObject registerObject;

    private SessionManager sessionManager;
    private Intent intent;
    private String gendre;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        UncaughtExceptionHandler uncaughtExceptionHandler = new UncaughtExceptionHandler();
        uncaughtExceptionHandler.uncaughtException(this);

        AppController application = (AppController) getApplication();
        mTracker = application.getDefaultTracker();

        mTracker.setScreenName("Register Page");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
        intent = getIntent();

        toolbar = (Toolbar) findViewById(R.id.app_toolbar);
        mTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
        mTitle.setText("Daftar Baru");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        sessionManager = new SessionManager(RegisterActivity.this);

        emailAddress = (EditText) findViewById(R.id.input_email);
        konfMmailAddress = (EditText) findViewById(R.id.konf_email);
        validateEmailText = (TextView) findViewById(R.id.validate_email);
        btnHome = (TextView) findViewById(R.id.btn_home);
        firstName = (EditText) findViewById(R.id.input_first_name);
        lastName = (EditText) findViewById(R.id.input_last_name);
        phoneNumber = (EditText) findViewById(R.id.input_phone_number);
        birthDate = (EditText) findViewById(R.id.edittext_profile_birthdate);
        password = (EditText) findViewById(R.id.input_password);
        confirmationPassword = (EditText) findViewById(R.id.input_confirmation_password);
        newsletter = (CheckBox) findViewById(R.id.register_newsletter);
        privacyPolicy = (CheckBox) findViewById(R.id.register_policy);

        registerButton = (Button) findViewById(R.id.btn_signup);
        btnMan = (Button) findViewById(R.id.btn_man);
        btnWoman = (Button) findViewById(R.id.btn_woman);

        btnHome.setText(Html.fromHtml("<u><b>Kembali ke Beranda</b></u>"));
        gendre = "";

        if (intent.getStringExtra("from").equals("yahooLogin")){
            try {
                JSONObject object = new JSONObject(intent.getStringExtra("profile"));

                firstName.setText(object.getString("nickname"));
                lastName.setText(object.getString("familyName"));
                phoneNumber.setText(object.getJSONArray("phones").getJSONObject(0).getString("number"));
                emailAddress.setText(object.getJSONArray("emails").getJSONObject(0).getString("handle"));
                konfMmailAddress.setText(object.getJSONArray("emails").getJSONObject(0).getString("handle"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }else if (intent.getStringExtra("from").equals("facebookLogin")){
            try {
                JSONObject object = new JSONObject(intent.getStringExtra("profile"));

                firstName.setText(object.getString("name"));
                emailAddress.setText(object.getString("email"));
                konfMmailAddress.setText(object.getString("email"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }else if (intent.getStringExtra("from").equals("googleLogin")){
            try {
                JSONObject object = new JSONObject(intent.getStringExtra("profile"));

                firstName.setText(object.getString("name"));
                emailAddress.setText(object.getString("email"));
                konfMmailAddress.setText(object.getString("email"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        btnHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(RegisterActivity.this, MainActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.left_in, R.anim.left_out);
            }
        });

        btnMan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnMan.setBackgroundResource(R.drawable.button_style_1);
                btnMan.setTextColor(Color.parseColor("#FFFFFF"));
                btnWoman.setBackgroundResource(R.drawable.button_style_5);
                btnWoman.setTextColor(Color.parseColor("#000000"));

                gendre = "Pria";
            }
        });

        btnWoman.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnWoman.setBackgroundResource(R.drawable.button_style_1);
                btnWoman.setTextColor(Color.parseColor("#FFFFFF"));
                btnMan.setBackgroundResource(R.drawable.button_style_5);
                btnMan.setTextColor(Color.parseColor("#000000"));

                gendre = "Wanita";
            }
        });

        birthDate.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        LayoutInflater inflater = (LayoutInflater) RegisterActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                        View writeReview = null;

                        if (writeReview == null) {
                            writeReview = inflater.inflate(R.layout.date_picker, null);
                        }

                        final Calendar calendar = Calendar.getInstance();
                        Integer year, month, day;
                        final StringBuilder builder=new StringBuilder();
                        final DatePicker datePicker = (DatePicker) writeReview.findViewById(R.id.date_picker);
                        final Button done = (Button) writeReview.findViewById(R.id.send_review);

                        datePicker.setMaxDate(calendar.getTimeInMillis());

                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(RegisterActivity.this);
                        alertDialogBuilder.setView(writeReview);
                        final AlertDialog alertDialog = alertDialogBuilder.create();
                        alertDialog.setCanceledOnTouchOutside(false);
                        alertDialog.show();

                        if(birthDate.getText().toString().contains("/")){
                            String[] splitString = birthDate.getText().toString().split("/");
                            month = Integer.valueOf(splitString[1]);
                            day = Integer.valueOf(splitString[0]);
                            year = Integer.valueOf(splitString[2]);

                            datePicker.updateDate(year, month-1, day);
                        }

                        done.setOnClickListener(
                                new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {

                                        if (String.valueOf(datePicker.getDayOfMonth()).length() == 1){
                                            builder.append("0" + datePicker.getDayOfMonth() + "-");
                                        }else{
                                            builder.append(datePicker.getDayOfMonth()+"-");
                                        }

                                        if (String.valueOf(datePicker.getMonth()).length() == 1){
                                            builder.append("0" + (datePicker.getMonth() + 1) + "-");
                                        }else{
                                            builder.append((datePicker.getMonth() + 1)+"-");
                                        }

                                        builder.append(datePicker.getYear());
                                        birthDate.setText(builder.toString());
                                        alertDialog.dismiss();
                                    }
                                }
                        );
                    }
                }
        );

        emailAddress.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!emailAddress.getText().toString().equals("")){
                    if (isEmailValid(emailAddress.getText().toString())){
                        validateEmailText.setVisibility(View.GONE);
                    }else{
                        validateEmailText.setVisibility(View.VISIBLE);
                    }
                }
            }
        });

        registerButton.setOnClickListener(
                new View.OnClickListener() {
                    @SuppressWarnings("deprecation")
                    @Override
                    public void onClick(View v) {
                        registerButton.setEnabled(false);

                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(RegisterActivity.this);
                        alertDialogBuilder.setNegativeButton("Tutup", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface arg0, int arg1) {
                            }
                        });

                        String inputEmail = "";
                        if (isEmailValid(emailAddress.getText().toString())){
                            validateEmailText.setVisibility(View.GONE);
                            inputEmail = emailAddress.getText().toString();
                        }else{
                            validateEmailText.setVisibility(View.VISIBLE);
                            alertDialogBuilder.setMessage("Alamat e-mail tidak sesuai dengan format seharusnya");
                            AlertDialog alertDialog = alertDialogBuilder.create();
                            alertDialog.show();
                            registerButton.setEnabled(true);
                            return;
                        }

                        String inputFirstName = firstName.getText().toString();
                        String inputLastName = lastName.getText().toString();
                        String inputPhoneNumber = phoneNumber.getText().toString();
                        String inputBirthDate = birthDate.getText().toString();
                        String inputPassword = password.getText().toString();
                        String inputConfirmationPassword = confirmationPassword.getText().toString();
                        boolean checkedNewsletter = newsletter.isChecked();
                        boolean checkedPolicy = privacyPolicy.isChecked();

                        if (TextUtils.isEmpty(inputFirstName)) {
                            alertDialogBuilder.setMessage("Harap isi Nama Depan");
                            AlertDialog alertDialog = alertDialogBuilder.create();
                            alertDialog.show();
                            registerButton.setEnabled(true);
                            firstName.setError("Harap isi Nama Depan");
                            return;
                        }

                        if (TextUtils.isEmpty(inputLastName)) {
                            alertDialogBuilder.setMessage("Harap isi Nama Belakang");
                            AlertDialog alertDialog = alertDialogBuilder.create();
                            alertDialog.show();
                            registerButton.setEnabled(true);
                            lastName.setError("Harap isi Nama Belakang");
                            return;
                        }

                        if (TextUtils.isEmpty(inputEmail)) {
                            alertDialogBuilder.setMessage("Harap isi Alamat Email");
                            AlertDialog alertDialog = alertDialogBuilder.create();
                            alertDialog.show();
                            registerButton.setEnabled(true);
                            return;
                        }

                        if(!konfMmailAddress.getText().toString().equals(inputEmail)){
                            alertDialogBuilder.setMessage("Email dan Konfirmasi Email tidak sama");
                            AlertDialog alertDialog = alertDialogBuilder.create();
                            alertDialog.show();
                            registerButton.setEnabled(true);
                            emailAddress.setError("Email dan Konfirmasi Email tidak sama");
                            return;
                        }

                        if (TextUtils.isEmpty(inputPhoneNumber)) {
                            alertDialogBuilder.setMessage("Harap isi Nomor Telepon");
                            AlertDialog alertDialog = alertDialogBuilder.create();
                            alertDialog.show();
                            registerButton.setEnabled(true);
                            phoneNumber.setError("Harap isi Nomor Telepon");
                            return;
                        }

                        if (phoneNumber.getText().toString().length() < 9){
                            alertDialogBuilder.setMessage("Format nomor telepon tidak valid");
                            AlertDialog alertDialog = alertDialogBuilder.create();
                            alertDialog.show();
                            registerButton.setEnabled(true);
                            phoneNumber.setError("Format nomor telepon tidak valid");
                            return;
                        }

                        if (gendre.equals("")) {
                            alertDialogBuilder.setMessage("Harap pilih Jenis Kelamin");
                            AlertDialog alertDialog = alertDialogBuilder.create();
                            alertDialog.show();
                            registerButton.setEnabled(true);
                            return;
                        }

                        if (TextUtils.isEmpty(inputBirthDate)) {
                            alertDialogBuilder.setMessage("Harap isi Tanggal Lahir");
                            AlertDialog alertDialog = alertDialogBuilder.create();
                            alertDialog.show();
                            registerButton.setEnabled(true);
                            birthDate.setError("Harap isi Tanggal Lahir");
                            return;
                        }

                        if (TextUtils.isEmpty(inputPassword)) {
                            alertDialogBuilder.setMessage("Harap isi Kata Sandi");
                            AlertDialog alertDialog = alertDialogBuilder.create();
                            alertDialog.show();
                            registerButton.setEnabled(true);
                            password.setError("Harap isi Kata Sandi");
                            return;
                        }

                        if (TextUtils.isEmpty(inputConfirmationPassword)) {
                            alertDialogBuilder.setMessage("Harap isi Konfirmasi Kata Sandi");
                            AlertDialog alertDialog = alertDialogBuilder.create();
                            alertDialog.show();
                            registerButton.setEnabled(true);
                            confirmationPassword.setError("Harap isi Konfirmasi Kata Sandi");
                            return;
                        }

                        if(!inputConfirmationPassword.equals(inputPassword)){
                            alertDialogBuilder.setMessage("Kata Sandi dan Konfirmasi Kata Sandi tidak sama");
                            AlertDialog alertDialog = alertDialogBuilder.create();
                            alertDialog.show();
                            registerButton.setEnabled(true);
                            password.setError("Kata Sandi dan Konfirmasi Kata Sandi tidak sama");
                            return;
                        }

                        if(!checkedPolicy){
                            alertDialogBuilder.setMessage("Harap setujui Persyaratan dan Ketentuan");
                            AlertDialog alertDialog = alertDialogBuilder.create();
                            alertDialog.show();
                            registerButton.setEnabled(true);
                            return;
                        }

                        String[] birthdateSplit = inputBirthDate.split("-");
                        int year = Integer.parseInt(birthdateSplit[2]);
                        int month = Integer.parseInt(birthdateSplit[1]);
                        int day = Integer.parseInt(birthdateSplit[0]);

                        registerObject = new JSONObject();

                        try {
                            registerObject.put("ID", "00000000-0000-0000-0000-000000000000 ");
                            registerObject.put("FBID", null);
                            registerObject.put("FName", inputFirstName);
                            registerObject.put("LName", inputLastName);
                            registerObject.put("Email", inputEmail);
                            registerObject.put("Address", null);
                            registerObject.put("Mobile", inputPhoneNumber);
                            registerObject.put("Password", inputPassword);
                            registerObject.put("IPAddress", "192.168.56.132");
                            registerObject.put("IsConfirmed", checkedPolicy);
                            registerObject.put("IsSubscribed", checkedNewsletter);
                            registerObject.put("Phone", null);
                            registerObject.put("DateOfBirth", year+"-"+month+"-"+day+"T00:00:00");
                            registerObject.put("CustomerAddress", null);
                            registerObject.put("DefaultStoreID", null);
                            registerObject.put("CreatedBy", null);
                            registerObject.put("UpdatedBy", null);
                            registerObject.put("AllowSMS", false);
                            registerObject.put("IsNewsLetterSubscriber", checkedNewsletter);
                            registerObject.put("Gender", gendre);
                            registerObject.put("LastUpdate", year+"-"+month+"-"+day+"T00:00:00");
                            registerObject.put("FullName", null);
                            registerObject.put("ConfirmPassword", inputPassword);
                            registerObject.put("NewEmail", null);
                            registerObject.put("ConfirmEmail", null);
                            registerObject.put("NewPassword", null);
                            registerObject.put("DateOfBirthStringFormatted", year+"-"+month+"-"+day);
                            registerObject.put("TypePushEmail", 0);
                            registerObject.put("IsUpload", false);
                            registerObject.put("IsActivated", false);
                            registerObject.put("OTPCode", null);
                            registerObject.put("OTPCodeCreatedDate", null);
                            registerObject.put("ValidateOTPDate", null);
                            registerObject.put("MobileVerified", false);
                            registerObject.put("DateOfBirthExists", "0001-01-01T00:00:00");
                            registerObject.put("OTPValidationExpired", false);
                            registerObject.put("IsFromOtherSystem", false);
                            registerObject.put("FaspayToken", null);
                            registerObject.put("OTPCount", 0);
                            registerObject.put("OTPAvailable", 0);
                            registerObject.put("Day", null);
                            registerObject.put("Month", null);
                            registerObject.put("Year", null);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        makeJsonPost(API.getInstance().getApiRegistrationMobile()+"?device_token="+sessionManager.getDeviceToken()+"&mfp_id="+sessionManager.getKeyMfpId(), registerObject);
                    }
                }
        );
    }

    public boolean isEmailValid(String email){
        String regExpn =
                "^(([\\w-]+\\.)+[\\w-]+|([a-zA-Z]{1}|[\\w-]{2,}))@"
                        +"((([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                        +"[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\."
                        +"([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                        +"[0-9]{1,2}|25[0-5]|2[0-4][0-9])){1}|"
                        +"([a-zA-Z]+[\\w-]+\\.)+[a-zA-Z]{2,4})$";

        CharSequence inputStr = email;
        Pattern pattern = Pattern.compile(regExpn, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(inputStr);

        if(matcher.matches()) return true;
        else return false;
    }

    public void makeJsonPost(String urlJsonObj, JSONObject jsonObject){
        System.out.println("reg obj = " + jsonObject);
        System.out.println("reg url = " + urlJsonObj);

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                urlJsonObj, jsonObject,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        if (response == null || response.length() == 0){
                            Toast.makeText(RegisterActivity.this, "Gagal terkoneksi server", Toast.LENGTH_LONG).show();
                        }else{
                            registerRespon(response);
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(RegisterActivity.this, "Gagal terkoneksi server", Toast.LENGTH_LONG).show();
            }
        });
        AppController.getInstance().addToRequestQueue(jsonObjReq);
    }

    public void registerRespon(JSONObject response){
        try {
            System.out.println("register obj : "+response);
            sessionManager.setResponseId(response.getString("ResponseID"));

            if(response.getString("IsSuccess").equals("true")){
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(RegisterActivity.this);
                alertDialogBuilder.setPositiveButton("Tutup", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        finish();
                        intent = new Intent(RegisterActivity.this, ActivationAccountActivity.class);
                        try {
                            intent.putExtra("email", registerObject.getString("Email"));
                            intent.putExtra("pass", registerObject.getString("Password"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        startActivity(intent);
                        overridePendingTransition(R.anim.left_in, R.anim.left_out);
                    }
                });

                alertDialogBuilder.setMessage("Terima kasih sudah mendaftar, silahkan cek email Anda untuk aktivasi akun. Jika tidak menerima pesan baru coba cek folder spam/junk di email Anda.");
                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
                registerButton.setEnabled(true);
            } else {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(RegisterActivity.this);
                alertDialogBuilder.setPositiveButton("Tutup", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {

                    }
                });

                alertDialogBuilder.setMessage(response.getString("Message"));
                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
                registerButton.setEnabled(true);
            }
        } catch (JSONException e) {
            e.printStackTrace();
            registerButton.setEnabled(true);
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
