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
import android.widget.EditText;
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

public class RefundActivity extends AppCompatActivity {

    private ReturProductAdapter returProductAdapter;
    private HeightAdjustableListView returProductList;
    private TextView custName, custAddress, custEmail, custTelp, orderDate, fppbNo, totalRefund, mTitle;
    private EditText noAccout, nameAccount, bankName;
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
        setContentView(R.layout.activity_refund);

        UncaughtExceptionHandler uncaughtExceptionHandler = new UncaughtExceptionHandler();
        uncaughtExceptionHandler.uncaughtException(this);

        sessionManager = new SessionManager(RefundActivity.this);

        toolbar = (Toolbar) findViewById(R.id.app_toolbar);
        mTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
        mTitle.setText("Refund");

        intent = getIntent();
        String fppbDetailID = intent.getStringExtra("fppbDetailID");

        returProductList = (HeightAdjustableListView) findViewById(R.id.retur_product_list);

        custName = (TextView) findViewById(R.id.custName);
        custAddress = (TextView) findViewById(R.id.custAddress);
        custEmail = (TextView) findViewById(R.id.custEmail);
        custTelp = (TextView) findViewById(R.id.custTelp);
        orderDate = (TextView) findViewById(R.id.orderDate);
        fppbNo = (TextView) findViewById(R.id.fppbNo);
        totalRefund = (TextView) findViewById(R.id.totalRefund);

        noAccout = (EditText) findViewById(R.id.noAccount);
        nameAccount = (EditText) findViewById(R.id.nameAccount);
        bankName = (EditText) findViewById(R.id.bankName);

        saveBtn = (Button) findViewById(R.id.saveBtn);
        preloader = (RelativeLayout) findViewById(R.id.preloader);

        jsonArrayGet(API.getInstance().getApiRefund() + "?fppbDetailId=" + fppbDetailID, "getRefund");
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
                                Toast.makeText(RefundActivity.this, "Gagal terkoneksi server", Toast.LENGTH_LONG).show();
                                stopLoader();
                            }else{
                                if (type.equals("getRefund")) {
                                    String fppbNumber = response.getJSONObject(0).getJSONObject("FppbHeader").getString("FPPBNo");
                                    fppbNo.setText(fppbNumber);

                                    if(!response.getJSONObject(0).getString("BankAccountNo").equals("null")){
                                        saveBtn.setVisibility(View.GONE);

                                        noAccout.setText(response.getJSONObject(0).getString("BankAccountNo"));
                                        nameAccount.setText(response.getJSONObject(0).getString("BankAccountOwner"));
                                        bankName.setText(response.getJSONObject(0).getString("BankBranchName"));

                                        noAccout.setEnabled(false);
                                        nameAccount.setEnabled(false);
                                        bankName.setEnabled(false);
                                    }

                                    totalRefund.setText("Rp " + response.getJSONObject(0).getString("TotalRefund"));

                                    jsonArrayGet(API.getInstance().getApiFppb() + "?orderNumber=" + fppbNumber, "getFppb");
                                }
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

                                    List<JSONObject> productList = new ArrayList<>();

                                    JSONArray fppb = response.getJSONObject(0).getJSONArray("FPPBDetails");

                                    for (int i=0; i<fppb.length(); i++){
                                        productList.add(fppb.getJSONObject(i));
                                    }

                                    returProductAdapter = new ReturProductAdapter(RefundActivity.this, productList, response.getJSONObject(0).getInt("Status"), "refund");
                                    returProductList.setAdapter(returProductAdapter);
                                    stopLoader();
                                }
                            }
                        }catch (Exception e){
                            e.printStackTrace();
                            stopLoader();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(RefundActivity.this, "Gagal terkoneksi server", Toast.LENGTH_LONG).show();
                stopLoader();
            }
        }, this);

        AppController.getInstance().addToRequestQueue(jsonArrayReq);
    }

    public void saveRefund(View view) {
        String noAcc = noAccout.getText().toString();
        String nameAcc = nameAccount.getText().toString();
        String nameBank = bankName.getText().toString();

        try {
            fppbModel.getJSONObject("Refund").put("BankAccountNo", noAcc);
            fppbModel.getJSONObject("Refund").put("BankAccountOwner", nameAcc);
            fppbModel.getJSONObject("Refund").put("BankBranchName", nameBank);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        makeJsonPost(API.getInstance().getApiUpdateFppb() + "?device_token=" + sessionManager.getDeviceToken() + "&mfp_id=" + sessionManager.getKeyMfpId() + "&isMobile=true", fppbModel, "saveRefund");
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
                            Toast.makeText(RefundActivity.this, "Gagal terkoneksi server", Toast.LENGTH_LONG).show();
                            stopLoader();
                        }else{
                            if (type.equals("saveRefund")) {
                                intent = new Intent(RefundActivity.this, MainActivity.class);
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

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(RefundActivity.this);
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
