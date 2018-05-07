package com.indomaret.klikindomaret.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.indomaret.klikindomaret.R;
import com.indomaret.klikindomaret.app.AppController;
import com.indomaret.klikindomaret.helper.UncaughtExceptionHandler;

import org.json.JSONObject;

public class ChangePasswordActivity extends AppCompatActivity {
    private Toolbar toolbar;
    Button buttonChangePassword;
    EditText oldPassword, newPassword, newPasswordConfirm;
    private Tracker mTracker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        UncaughtExceptionHandler uncaughtExceptionHandler = new UncaughtExceptionHandler();
        uncaughtExceptionHandler.uncaughtException(this);

        buttonChangePassword = (Button) findViewById(R.id.btn_change_password);

        oldPassword = (EditText) findViewById(R.id.old_password);
        newPassword = (EditText) findViewById(R.id.new_password);
        newPasswordConfirm = (EditText) findViewById(R.id.new_password_confirm);

        AppController application = (AppController) getApplication();
        mTracker = application.getDefaultTracker();

        mTracker.setScreenName("Change Password Page");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());

        toolbar = (Toolbar) findViewById(R.id.app_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        final DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        try {

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;
                }
            }
        };

        buttonChangePassword.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String oldPass = oldPassword.getText().toString();
                        String newPass = newPassword.getText().toString();
                        String newPassConfirm = newPasswordConfirm.getText().toString();

                        if (TextUtils.isEmpty(oldPass)) {
                            oldPassword.setError("Harap isi password lama");
                            return;
                        }

                        if (TextUtils.isEmpty(newPass)){
                            newPassword.setError("Harap isi password baru");
                            return;
                        }

                        if (TextUtils.isEmpty(newPassConfirm)){
                            newPasswordConfirm.setError("Harap isi konfirmasi password baru");
                            return;
                        }

                        if(!newPass.equals(newPassConfirm)){
                            AlertDialog.Builder builder = new AlertDialog.Builder(ChangePasswordActivity.this);
                            builder.setMessage("Konfirmasi password tidak cocok").setPositiveButton("Ya", dialogClickListener).show();
                        } else {
                            JSONObject object = new JSONObject();
                        }
                    }
                }
        );
    }
}
