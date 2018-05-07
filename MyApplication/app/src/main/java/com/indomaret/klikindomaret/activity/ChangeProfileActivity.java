package com.indomaret.klikindomaret.activity;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.indomaret.klikindomaret.R;
import com.indomaret.klikindomaret.app.AppController;
import com.indomaret.klikindomaret.helper.UncaughtExceptionHandler;

import org.json.JSONObject;

import java.util.Calendar;

public class ChangeProfileActivity extends AppCompatActivity {
    private Toolbar toolbar;
    EditText firstName, lastName, phoneNumber, birthDate;
    Spinner gender;
    private Tracker mTracker;

    Intent intent;

    private int year, month, day;

    public ChangeProfileActivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_profile);

        UncaughtExceptionHandler uncaughtExceptionHandler = new UncaughtExceptionHandler();
        uncaughtExceptionHandler.uncaughtException(this);

        AppController application = (AppController) getApplication();
        mTracker = application.getDefaultTracker();

        mTracker.setScreenName("Change Profil Page");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());

        toolbar = (Toolbar) findViewById(R.id.app_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        firstName = (EditText) findViewById(R.id.input_fist_name);
        lastName = (EditText) findViewById(R.id.input_last_name);
        phoneNumber = (EditText) findViewById(R.id.input_phone_number);
        birthDate = (EditText) findViewById(R.id.input_birthdate);

        gender = (Spinner) findViewById(R.id.chage_profile_spinner_gendre);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(ChangeProfileActivity.this, R.array.gendre_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        gender.setAdapter(adapter);

        intent = getIntent();
        firstName.setText(intent.getStringExtra("FName"));
        lastName.setText(intent.getStringExtra("LName"));
        phoneNumber.setText(intent.getStringExtra("PhoneNumber"));
        birthDate.setText(intent.getStringExtra("BirthDate"));
    }

    public void updateProfile(View view){
        String inputFirstName = firstName.getText().toString();
        String inputLastName = lastName.getText().toString();
        String inputPhoneNumber = phoneNumber.getText().toString();
        String inputBirthDate = birthDate.getText().toString();

        if(TextUtils.isEmpty(inputFirstName)){
            firstName.setError("Harap isi nama depan");
            return;
        }

        if(TextUtils.isEmpty(inputLastName)){
            lastName.setError("Harap isi nama belakang");
            return;
        }

        if(TextUtils.isEmpty(inputPhoneNumber)){
            phoneNumber.setError("Harap isi nomor telepon");
            return;
        }

        if(TextUtils.isEmpty(inputBirthDate)){
            firstName.setError("Harap isi tanggal lahir");
            return;
        }

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("FName", inputFirstName);
            jsonObject.put("LName", inputLastName);
            jsonObject.put("PhoneNumber", inputPhoneNumber);
            jsonObject.put("BirthDate", inputBirthDate);
            jsonObject.put("Gender", gender);
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    @SuppressWarnings("deprecation")
    public void setDate(View view) {
        showDialog(999);
    }

    @Override
    @SuppressWarnings("deprecation")
    protected Dialog onCreateDialog(int id) {
        if (id == 999) {
            final Calendar c = Calendar.getInstance();
            year = c.get(Calendar.YEAR);
            month = c.get(Calendar.MONTH);
            day = c.get(Calendar.DAY_OF_MONTH);

            return new DatePickerDialog(this, myDateListener, year, month, day);
        }

        return null;
    }

    private DatePickerDialog.OnDateSetListener myDateListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker arg0, int arg1, int arg2, int arg3) {
            showDate(arg1, arg2+1, arg3);
        }
    };

    private void showDate(int year, int month, int day) {
        birthDate.setText(new StringBuilder().append(day).append("-").append(month).append("-").append(year));
    }
}
