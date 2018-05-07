package com.indomaret.klikindomaret.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.indomaret.klikindomaret.MainActivity;
import com.indomaret.klikindomaret.R;

public class SuccessVerifivationActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_success_verifivation);
        intent = getIntent();

        toolbar = (Toolbar) findViewById(R.id.app_toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    public void backHome(View view){
        intent = new Intent(SuccessVerifivationActivity.this, MainActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.left_in, R.anim.left_out);
    }

    public void onBackPressed() {
        intent = new Intent(SuccessVerifivationActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
        overridePendingTransition(R.anim.right_in, R.anim.right_out);
    }
}
