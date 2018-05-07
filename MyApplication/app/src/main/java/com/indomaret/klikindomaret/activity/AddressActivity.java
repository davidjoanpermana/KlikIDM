package com.indomaret.klikindomaret.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.indomaret.klikindomaret.API;
import com.indomaret.klikindomaret.R;
import com.indomaret.klikindomaret.adapter.AddressItemAdapter;
import com.indomaret.klikindomaret.app.AppController;
import com.indomaret.klikindomaret.helper.SQLiteHandler;
import com.indomaret.klikindomaret.helper.SessionManager;
import com.indomaret.klikindomaret.views.HeightAdjustableListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class AddressActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private Intent intent;
    private SessionManager sessionManager;
    private SQLiteHandler sqLiteHandler;
    private HeightAdjustableListView addressListView;
    private ScrollView scrollView;
    private TextView mTitle;
    private JSONArray userProfile = new JSONArray();
    private JSONArray addressArray = new JSONArray();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addressctivity);

        toolbar = (Toolbar) findViewById(R.id.app_toolbar);
        toolbar.setTitle("");
        mTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
        mTitle.setText("Buku Alamat");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        intent = getIntent();
        sessionManager = new SessionManager(this);
        sqLiteHandler = new SQLiteHandler(this);

        addressListView = (HeightAdjustableListView) findViewById(R.id.address_list);
        scrollView = (ScrollView) findViewById(R.id.scroll_view);

        scrollView.fullScroll(ScrollView.FOCUS_UP);
    }

    public void setData(JSONObject addressObject, String infoPlace, String latitude, String longitude){
        JSONObject object = new JSONObject();
        try {
            object.put("IsDelivery", true);
            object.put("ShoppingCartID", sessionManager.getCartId());
            object.put("CustomerAddressID", addressObject.getString("ID"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        jsonPost(API.getInstance().getApiStoreZoneSlot()+"?mfp_id="+sessionManager.getKeyMfpId(), object, addressObject, infoPlace, latitude, longitude);
    }

    public void jsonPost(String urlJsonObj, JSONObject jsonObject, final JSONObject addressObject, final String infoPlace, final String latitude, final String longitude){
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                urlJsonObj, jsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(final JSONObject response) {
                        try {
                            intent = getIntent();
                            intent.putExtra("mCustomerAddressId", addressObject.getString("ID"));
                            intent.putExtra("address", addressObject.toString());
                            intent.putExtra("shippingcost", response.getJSONObject("ResponseObject").getString("CostDelivery"));
                            intent.putExtra("infoStore", infoPlace);
                            intent.putExtra("latitude", latitude);
                            intent.putExtra("longitude", longitude);
                            setResult(RESULT_OK, intent);
                            finish();
                            overridePendingTransition(R.anim.right_in, R.anim.right_out);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(AddressActivity.this, "Gagal terkoneksi server", Toast.LENGTH_LONG).show();
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
        getMenuInflater().inflate(R.menu.add_address_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.add_address_btn) {
            intent = new Intent(AddressActivity.this, AddAdressActivity.class);
            intent.putExtra("type", "addShipping");
            startActivityForResult(intent, 1);
            overridePendingTransition(R.anim.left_in, R.anim.left_out);

            return true;
        } else if (id == android.R.id.home) {
            finish();
            overridePendingTransition(R.anim.right_in, R.anim.right_out);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume() {
        super.onResume();

        try {
            userProfile = new JSONArray(sqLiteHandler.getProfile());
            addressArray = userProfile.getJSONObject(0).getJSONArray("Address");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        AddressItemAdapter addressItemAdapter = new AddressItemAdapter(this, addressArray);
        addressListView.setAdapter(addressItemAdapter);
    }
}
