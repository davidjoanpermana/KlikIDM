package com.indomaret.klikindomaret.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.indomaret.klikindomaret.API;
import com.indomaret.klikindomaret.R;
import com.indomaret.klikindomaret.adapter.ShippingTimeDaysAdapter;
import com.indomaret.klikindomaret.app.AppController;
import com.indomaret.klikindomaret.helper.SessionManager;
import com.indomaret.klikindomaret.helper.UncaughtExceptionHandler;
import com.indomaret.klikindomaret.views.HeightAdjustableListView;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class ShippingTimeActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private SessionManager sessionManager;
    private TextView mTitle;
    private HeightAdjustableListView daysShip;

    private DateFormat dateFormat = new SimpleDateFormat("EEE dd MM yyyy");
    private DateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
    private Intent intent;

    private String serverTime, times, date;
    private String customerAddressID = "";
    private String storeId = "";
    private boolean isSend;
    private List<String> aDate = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shipping_time);

        UncaughtExceptionHandler uncaughtExceptionHandler = new UncaughtExceptionHandler();
        uncaughtExceptionHandler.uncaughtException(this);

        sessionManager = new SessionManager(this);
        daysShip = (HeightAdjustableListView) findViewById(R.id.days_ship);

        toolbar = (Toolbar) findViewById(R.id.app_toolbar);
        mTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
        toolbar.setTitle("");
        mTitle.setText("Pilih Tanggal");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        intent = getIntent();
        serverTime = intent.getStringExtra("time");

        if (intent.getStringExtra("customerAddressID") != null && intent.getStringExtra("customerAddressID").length() > 0){
            customerAddressID = intent.getStringExtra("customerAddressID");
            isSend = true;
        }else if (intent.getStringExtra("storeId") != null && intent.getStringExtra("storeId").length() > 0){
            storeId = intent.getStringExtra("storeId");
            isSend = false;
        }

        JSONObject object = new JSONObject();
        try {
            object.put("IsDelivery", isSend);
            object.put("ShoppingCartID", sessionManager.getCartId());
            if (customerAddressID.length() > 0){
                object.put("CustomerAddressID", customerAddressID);
            }else if (storeId.length() > 0){
                object.put("StoreID", storeId);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        jsonPost(API.getInstance().getApiStoreZoneSlot()+"?mfp_id="+sessionManager.getKeyMfpId(), object);
    }

    public void setData(String date){
        this.date = date;
    }

    public void jsonPost(String urlJsonObj, JSONObject jsonObject){
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                urlJsonObj, jsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(final JSONObject response) {
                        String[] splitDate1 = serverTime.split("T");
                        String[] splitDate2 = splitDate1[1].split("\\.");
                        String dateServer = splitDate1[0] + " " + splitDate2[0];

                        for (int i=0; i<7; i++){
                            try {
                                Calendar calendar = Calendar.getInstance();
                                SimpleDateFormat simpleDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

                                calendar.setTime(simpleDate.parse(dateServer));

                                calendar.add(calendar.DATE, i);
                                Date today = calendar.getTime();
                                String dates = dateFormat.format(today);
                                times = timeFormat.format(today);

                                aDate.add(dates);
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                        }

                        ShippingTimeDaysAdapter shippingTimeDaysAdapter = null;
                        try {
                            shippingTimeDaysAdapter = new ShippingTimeDaysAdapter(ShippingTimeActivity.this, aDate, dateServer, customerAddressID, storeId, isSend, response.getJSONObject("ResponseObject").getJSONArray("ListSlot"));
                            daysShip.setAdapter(shippingTimeDaysAdapter);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(ShippingTimeActivity.this, "Gagal terkoneksi server", Toast.LENGTH_LONG).show();
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

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == RESULT_OK){
            super.onActivityResult(requestCode, resultCode, data);
            intent = new Intent();
            intent.putExtra("date", date);
            intent.putExtra("time", data.getStringExtra("time"));
            intent.putExtra("expiredDate", data.getStringExtra("expiredDate"));
            setResult(RESULT_OK, intent);
            finish();
            overridePendingTransition(R.anim.right_in, R.anim.right_out);
        }
    }
}