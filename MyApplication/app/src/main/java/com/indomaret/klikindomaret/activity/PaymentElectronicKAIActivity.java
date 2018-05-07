package com.indomaret.klikindomaret.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.indomaret.klikindomaret.API;
import com.indomaret.klikindomaret.R;
import com.indomaret.klikindomaret.helper.Encode2;
import com.indomaret.klikindomaret.helper.SessionManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

public class PaymentElectronicKAIActivity extends AppCompatActivity {
    private Intent intent;
    private SessionManager sessionManager;
    private TextView headerText, backPayment, infoPayment;
    private EditText code;
    private ImageView imageHeader;
    private Button btnBuy;
    private RelativeLayout preloader;

    private String paymentCode = "";
    private String from = "";
    private JSONObject objectResponse = new JSONObject();
    private Encode2 encode = new Encode2();
    CartActivity cartActivity = new CartActivity();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_electronic_kai);

        intent = getIntent();
        sessionManager = new SessionManager(this);

        headerText = (TextView) findViewById(R.id.header_text);
        backPayment = (TextView) findViewById(R.id.back_payment);
        infoPayment = (TextView) findViewById(R.id.info_payment);

        code = (EditText) findViewById(R.id.code);
        btnBuy = (Button) findViewById(R.id.btn_buy);
        imageHeader = (ImageView) findViewById(R.id.image_header);
        preloader = (RelativeLayout) findViewById(R.id.preloader);

        try {
            paymentCode = intent.getStringExtra("paymentCode");
            objectResponse = new JSONObject(intent.getStringExtra("objectResponse"));

            JSONArray paymentList = objectResponse.getJSONObject("Data").getJSONArray("PaymentTypeList");
            for (int i=0; i<paymentList.length(); i++){
                if (paymentList.getJSONObject(i).getString("Code").equals(paymentCode)){
                    infoPayment.setText(Html.fromHtml(paymentList.getJSONObject(i).getString("NextStep")));
                }
            }

            if (paymentCode.equals("RKPON")){
                headerText.setText("Masukan kode Rekening Ponsel anda untuk menyelesaikan pembayaran anda");
                imageHeader.setImageDrawable(getResources().getDrawable(R.drawable.rkpon));
            }else if (paymentCode.equals("BPISAKU")){
                headerText.setText("Masukan kode iSaku anda untuk menyelesaikan pembayaran anda");
                imageHeader.setImageDrawable(getResources().getDrawable(R.drawable.isaku_header));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        backPayment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(R.anim.left_in, R.anim.left_out);
            }
        });

        btnBuy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JSONObject jsonObject = new JSONObject();
                if (code.getText().length() < 4){
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(PaymentElectronicKAIActivity.this);
                    alertDialogBuilder.setTitle("KlikIndomaret");
                    alertDialogBuilder.setMessage("Kode Token tidak boleh kurang dari 4 digit ");
                    alertDialogBuilder.setNegativeButton("Tutup", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface arg0, int arg1) {
                        }
                    });

                    AlertDialog alertDialog = alertDialogBuilder.create();
                    alertDialog.setCanceledOnTouchOutside(false);
                    alertDialog.show();
                }else{
                    try {
                        jsonObject.put("ID", objectResponse.getJSONObject("Data").getString("SalesOrderHeaderId"));
                        jsonObject.put("PaymentMethodCode", paymentCode);
                        jsonObject.put("TokenEMoney", code.getText().toString());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    runLoader();
                    jsonPost(API.getInstance().getApiPaymentCheckout() + "?mfp_id=" + sessionManager.getKeyMfpId(), jsonObject);
                }
            }
        });
    }

    public void jsonPost(String urlJsonObj, JSONObject jsonObject) {
        RequestQueue queue = Volley.newRequestQueue(this);
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                urlJsonObj, jsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if (response == null || response.length() == 0){
                                stopLoader();
                                Toast.makeText(PaymentElectronicKAIActivity.this, "Gagal terkoneksi server", Toast.LENGTH_LONG).show();
                            }else{
                                if (response.getString("Message").equals("Success")){
                                    if (response.getJSONObject("Data").getBoolean("IsThankYouPage")){
                                        requestWithSomeHttpHeaders(API.getInstance().getApiGetSummaryKAI()
                                                +"?TransactionCode=" + response.getJSONObject("Data").getString("transactionCode"), "tankyouPage");
                                    }else{
                                        intent = new Intent(PaymentElectronicKAIActivity.this, PaymentThirdPartyActivity.class);
                                        intent.putExtra("callbackUrl", response.getJSONObject("Data").getString("RedirectUrl"));
                                        intent.putExtra("data", response.getJSONObject("Data").getString("transactionCode"));
                                        startActivity(intent);
                                        overridePendingTransition(R.anim.left_in, R.anim.left_out);
                                    }

                                    stopLoader();
                                }else{
                                    stopLoader();
                                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(PaymentElectronicKAIActivity.this);
                                    alertDialogBuilder.setMessage(response.getString("Message"));
                                    alertDialogBuilder.setNegativeButton("Ok", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface arg0, int arg1) {
                                        }
                                    });

                                    AlertDialog alertDialog = alertDialogBuilder.create();
                                    alertDialog.setCanceledOnTouchOutside(false);
                                    alertDialog.show();
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            stopLoader();
                            Toast.makeText(PaymentElectronicKAIActivity.this, "Gagal terkoneksi server", Toast.LENGTH_LONG).show();
                        }
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        stopLoader();
                        Toast.makeText(PaymentElectronicKAIActivity.this, "Gagal terkoneksi server", Toast.LENGTH_LONG).show();
                    }
                }
        ) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String>  params = new HashMap<String, String>();
                String token = "";
                try {
                    token = encode.SHA1(encode.md5("66E2C13840534C139D85CEE1B433C1FX"));
                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }

                params.put("ApplicationKey", "indomaret");
                params.put("Authorization", "bearer "+token);

                return params;
            }
        };
        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(60000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(jsonObjReq);
    }

    public void requestWithSomeHttpHeaders(String url, final String type) {
        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest postRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {
                        try {
                            if (response == null || response.length() == 0){
                                stopLoader();
                                Toast.makeText(PaymentElectronicKAIActivity.this, "Gagal terkoneksi server", Toast.LENGTH_LONG).show();
                            }else{
                                JSONObject jObject = new JSONObject(response);
                                if (jObject.getString("Message").equals("Success")){
                                    if (type.equals("tankyouPage")){
                                        intent = new Intent(PaymentElectronicKAIActivity.this, ThankyouPageKAIActivity.class);
                                        intent.putExtra("objectResponse", jObject.toString());
                                        finish();
                                        startActivity(intent);
                                        overridePendingTransition(R.anim.left_in, R.anim.left_out);
                                    }
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            stopLoader();
                            Toast.makeText(PaymentElectronicKAIActivity.this, "Gagal terkoneksi server", Toast.LENGTH_LONG).show();
                        }
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        stopLoader();
                        Toast.makeText(PaymentElectronicKAIActivity.this, "Gagal terkoneksi server", Toast.LENGTH_LONG).show();
                    }
                }
        ) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String>  params = new HashMap<String, String>();
                String token = "";
                try {
                    token = encode.SHA1(encode.md5("66E2C13840534C139D85CEE1B433C1FX"));
                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }

                params.put("ApplicationKey", "indomaret");
                params.put("Authorization", "bearer "+token);

                return params;
            }
        };

        postRequest.setRetryPolicy(new DefaultRetryPolicy(60000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(postRequest);
    }

    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.right_in, R.anim.right_out);
    }

    public void runLoader(){
        preloader.setVisibility(View.VISIBLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }

    public void stopLoader(){
        preloader.setVisibility(View.GONE);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }
}
