package com.indomaret.klikindomaret.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.indomaret.klikindomaret.API;
import com.indomaret.klikindomaret.MainActivity;
import com.indomaret.klikindomaret.R;
import com.indomaret.klikindomaret.adapter.ReturProductAdapter;
import com.indomaret.klikindomaret.app.AppController;
import com.indomaret.klikindomaret.helper.Month;
import com.indomaret.klikindomaret.helper.SessionManager;
import com.indomaret.klikindomaret.helper.UncaughtExceptionHandler;
import com.indomaret.klikindomaret.views.HeightAdjustableListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ReturActivity extends AppCompatActivity {

    private ReturProductAdapter returProductAdapter;
    private HeightAdjustableListView returProductList;
    private TextView custName, custAddress, custEmail, custTelp, orderDate, shippingDate, fppbNoLbl, mTitle;
    private RelativeLayout preloader;
    private Intent intent;
    private SessionManager sessionManager;
    private JSONObject fppbModel;
    Button saveBtn;
    Month month = new Month();
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_retur);

        UncaughtExceptionHandler uncaughtExceptionHandler = new UncaughtExceptionHandler();
        uncaughtExceptionHandler.uncaughtException(this);

        sessionManager = new SessionManager(ReturActivity.this);

        toolbar = (Toolbar) findViewById(R.id.app_toolbar);
        mTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
        mTitle.setText("Retur");

        intent = getIntent();
        String fppbNo = intent.getStringExtra("fppbNo");

        returProductList = (HeightAdjustableListView) findViewById(R.id.retur_product_list);

        custName = (TextView) findViewById(R.id.custName);
        custAddress = (TextView) findViewById(R.id.custAddress);
        custEmail = (TextView) findViewById(R.id.custEmail);
        custTelp = (TextView) findViewById(R.id.custTelp);
        orderDate = (TextView) findViewById(R.id.orderDate);
        shippingDate = (TextView) findViewById(R.id.shippingDate);
        fppbNoLbl = (TextView) findViewById(R.id.fppbNoLbl);
        saveBtn = (Button) findViewById(R.id.saveBtn);
        preloader = (RelativeLayout) findViewById(R.id.preloader) ;

        fppbNoLbl.setText(fppbNo);

        jsonArrayGet(API.getInstance().getApiFppb() + "?orderNumber=" + fppbNo, "getFppb");
    }

    public void jsonArrayGet(String urlJsonObj, final String type){
        runLoader();
        System.out.println("summary = " + urlJsonObj);

        JsonArrayRequest jsonArrayReq = new JsonArrayRequest(urlJsonObj,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            if (response == null || response.length() == 0){
                                Toast.makeText(ReturActivity.this, "Gagal terkoneksi server", Toast.LENGTH_LONG).show();
                                stopLoader();
                            }else{
                                if (type.equals("getFppb")) {
                                    fppbModel = response.getJSONObject(0);
                                    custName.setText(response.getJSONObject(0).getString("CustomerName"));
                                    custAddress.setText(response.getJSONObject(0).getString("CustomerAddress"));
                                    custEmail.setText(response.getJSONObject(0).getString("CustomerEmail"));
                                    custTelp.setText(response.getJSONObject(0).getString("CustomerPhone"));

                                    String[] orderDates = response.getJSONObject(0).getString("SoDate").split("T");
                                    String[] orderSingleDates = orderDates[0].split("-");
                                    String[] orderSingleTime = orderDates[1].split(":");

                                    orderDate.setText(orderSingleDates[2] + " " + month.getMonth(orderSingleDates[1]) + " " + orderSingleDates[0] + " " + orderSingleTime[0] + ":" + orderSingleTime[1] + " WIB");

                                    if(!response.getJSONObject(0).getString("ReceivedDate").isEmpty()) {
                                        String[] receivedDates = response.getJSONObject(0).getString("ReceivedDate").split("T");
                                        String[] receivedSingleDates = receivedDates[0].split("-");
                                        String[] receivedSingleTime = receivedDates[1].split(":");

                                        if(!receivedSingleDates[0].toString().equals("1753")) {
                                            shippingDate.setText(receivedSingleDates[2] + " " + month.getMonth(receivedSingleDates[1]) + " " + receivedSingleDates[0] + " " + receivedSingleTime[0] + ":" + receivedSingleTime[1] + " WIB");
                                        }
                                    }

                                    if(response.getJSONObject(0).getInt("Status") > 0){
                                        saveBtn.setVisibility(View.GONE);
                                    }

                                    List<JSONObject> productList = new ArrayList<>();

                                    JSONArray fppb = response.getJSONObject(0).getJSONArray("FPPBDetails");

                                    for (int i=0; i<fppb.length(); i++){
                                        productList.add(fppb.getJSONObject(i));
                                    }

                                    returProductAdapter = new ReturProductAdapter(ReturActivity.this, productList, response.getJSONObject(0).getInt("Status"), "retur");
                                    returProductList.setAdapter(returProductAdapter);
                                }
                            }
                            stopLoader();
                        }catch (Exception e){
                            e.printStackTrace();
                            stopLoader();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(ReturActivity.this, "Gagal terkoneksi server", Toast.LENGTH_LONG).show();
                stopLoader();
            }
        }, this);

        AppController.getInstance().addToRequestQueue(jsonArrayReq);
    }

    public void saveReason(String reason, int index){
        try {
            fppbModel.getJSONArray("FPPBDetails").getJSONObject(index).put("ReasonRetur", reason);
        } catch (Exception e){

        }
    }

    public void saveRetur(View view) {

        JSONObject jsonObject = new JSONObject();
        try {
            fppbModel.put("Status", 1);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        makeJsonPost(API.getInstance().getApiUpdateFppb() + "?device_token=" + sessionManager.getDeviceToken() + "&mfp_id=" + sessionManager.getKeyMfpId() + "&isMobile=true", fppbModel, "saveRetur");
    }

    public void makeJsonPost(String urlJsonObj, JSONObject jsonObject, final String type) {
        runLoader();
        Log.i("Refund URL", urlJsonObj);
        Log.i("Refund Object", jsonObject.toString());

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                urlJsonObj, jsonObject,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        if (response == null || response.length() == 0){
                            Toast.makeText(ReturActivity.this, "Gagal terkoneksi server", Toast.LENGTH_LONG).show();
                            stopLoader();
                        }else{
                            if (type.equals("saveRetur")) {
                                intent = new Intent(ReturActivity.this, MainActivity.class);
                                startActivity(intent);
                                overridePendingTransition(R.anim.left_in, R.anim.left_out);
                                finish();
                            }
                            stopLoader();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i("Error", error.toString());

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ReturActivity.this);
                alertDialogBuilder.setMessage("Simpan Gagal");
                alertDialogBuilder.setNegativeButton("Tutup", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                    }
                });

                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
                stopLoader();
            }
        });

        AppController.getInstance().addToRequestQueue(jsonObjReq);
    }

    public void runLoader(){
        preloader.setVisibility(View.VISIBLE);
    }

    public void stopLoader(){
        preloader.setVisibility(View.GONE);
    }
}
