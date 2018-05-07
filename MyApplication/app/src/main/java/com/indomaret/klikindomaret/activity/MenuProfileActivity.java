package com.indomaret.klikindomaret.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
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
import com.android.volley.toolbox.JsonObjectRequest;
import com.indomaret.klikindomaret.API;
import com.indomaret.klikindomaret.MainActivity;
import com.indomaret.klikindomaret.R;
import com.indomaret.klikindomaret.app.AppController;
import com.indomaret.klikindomaret.helper.SQLiteHandler;
import com.indomaret.klikindomaret.helper.SessionManager;

import org.json.JSONException;
import org.json.JSONObject;

public class MenuProfileActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private SessionManager sessionManager;
    private SQLiteHandler sqLiteHandler;
    private Intent intent;
    private TextView mTitle;
    private TextView userName, btnProfile, btnAddressBook, btnHhistory, btnBelanja, btnNotif;
    private Button btnLogout;
    private RelativeLayout preloader;
    private StringBuilder stringBuilder = new StringBuilder();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_profile);

        toolbar = (Toolbar) findViewById(R.id.app_toolbar);
        toolbar.setTitle("");
        mTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
        mTitle.setText("Beranda");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        sessionManager = new SessionManager(this);
        intent = getIntent();

        userName = (TextView) findViewById(R.id.user_name);
        btnProfile = (TextView) findViewById(R.id.btn_profile);
        btnAddressBook = (TextView) findViewById(R.id.btn_address);
        btnHhistory = (TextView) findViewById(R.id.btn_history);
        btnBelanja = (TextView) findViewById(R.id.btn_belanja);
        btnNotif = (TextView) findViewById(R.id.btn_notif);
        btnLogout = (Button) findViewById(R.id.btn_logout);
        preloader = (RelativeLayout) findViewById(R.id.preloader);

        btnNotif.setText(btnNotif.getText() + "(" + sessionManager.getKeyCountNotif() + ")");

        btnProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(MenuProfileActivity.this, ProfileActivity.class);
                intent.putExtra("pageindex", "" + 0);
                startActivity(intent);
                overridePendingTransition(R.anim.right_in, R.anim.right_out);
            }
        });

        btnAddressBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(MenuProfileActivity.this, ProfileActivity.class);
                intent.putExtra("pageindex", "" + 1);
                startActivity(intent);
                overridePendingTransition(R.anim.right_in, R.anim.right_out);
            }
        });

        btnHhistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(MenuProfileActivity.this, ProfileActivity.class);
                intent.putExtra("pageindex", "" + 2);
                startActivity(intent);
                overridePendingTransition(R.anim.right_in, R.anim.right_out);
            }
        });

        btnBelanja.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(MenuProfileActivity.this, ProfileActivity.class);
                intent.putExtra("pageindex", "" + 3);
                startActivity(intent);
                overridePendingTransition(R.anim.right_in, R.anim.right_out);
            }
        });

        btnNotif.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(MenuProfileActivity.this, ProfileActivity.class);
                intent.putExtra("pageindex", "" + 4);
                startActivity(intent);
                overridePendingTransition(R.anim.left_in, R.anim.left_out);
            }
        });

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logout();
            }
        });
    }

    public void logout(){
        sqLiteHandler = new SQLiteHandler(this);

        preloader.setVisibility(View.VISIBLE);
        sessionManager.setKeyShipping(false);
        sessionManager.setKeyPlazaShipping(false);
        getMFPToken(API.getInstance().getMfpId()+"?device_token=" + sessionManager.getDeviceToken());
        sqLiteHandler.deleteData();
        sessionManager.setKeyShippingPlaza(0);

        sessionManager.setKeyTotalPrice(0);
        sessionManager.setKeyTotalVoucher(0);
        sessionManager.setKeyTotalDiscount(0);
        sessionManager.setKeyTotalShippingCost(0);
        sessionManager.setKeyTotalCoupon(0);
        sessionManager.setKeyCouponList("");
        sessionManager.setKeyTokenCookie("");
        sessionManager.setkeyDate("");
        sessionManager.setKeyTime("");
    }

    public void getMFPToken(String url){
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        if (response == null || response.length() == 0){
                            preloader.setVisibility(View.GONE);
                            Toast.makeText(MenuProfileActivity.this, "Gagal terkoneksi server", Toast.LENGTH_LONG).show();
                        }else{
                            try {
                                preloader.setVisibility(View.GONE);
                                sessionManager.setKeyMfpId(response.getString("Message"));
                                sessionManager.setLogin(false);
                                sessionManager.setCartId(null);
                                sessionManager.setUserID("00000000-0000-0000-0000-000000000000");

                                intent = new Intent(MenuProfileActivity.this, MainActivity.class);
                                overridePendingTransition(0, 0);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                                overridePendingTransition(0, 0);
                                finish();
                                startActivity(intent);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MenuProfileActivity.this, "Gagal terkoneksi server", Toast.LENGTH_LONG).show();
                preloader.setVisibility(View.GONE);
            }
        });

        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(60000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppController.getInstance().addToRequestQueue(jsonObjReq);
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

    @Override
    public void onResume() {
        super.onResume();

        stringBuilder = new StringBuilder();
        if (sessionManager.getUsername() != null && sessionManager.getUsername().length() > 0){
            stringBuilder.append(sessionManager.getUsername());
        }

        if (sessionManager.getLastUsername() != null && sessionManager.getLastUsername().length() > 0){
            stringBuilder.append(" " + sessionManager.getLastUsername());
        }

        userName.setText(stringBuilder);
    }
}
