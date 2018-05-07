package com.indomaret.klikindomaret.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v4.content.IntentCompat;
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
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.facebook.appevents.AppEventsLogger;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.indomaret.klikindomaret.API;
import com.indomaret.klikindomaret.MainActivity;
import com.indomaret.klikindomaret.R;
import com.indomaret.klikindomaret.app.AppController;
import com.indomaret.klikindomaret.helper.Encode2;
import com.indomaret.klikindomaret.helper.Month;
import com.indomaret.klikindomaret.helper.SessionManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class PaymentElectronicActivity extends AppCompatActivity {
    private Intent intent;
    private SessionManager sessionManager;
    private TextView headerText, backPayment, infoPayment;
    private EditText code;
    private ImageView imageHeader;
    private Button btnBuy;
    private RelativeLayout preloader;

    private String paymentCode = "";
    private String time, date, sendDate, salesOrderid, from;
    private JSONObject objectResponse = new JSONObject();
    private JSONObject defaultAddress = new JSONObject();
    private JSONObject defaultAddressStoreObject = new JSONObject();
    private JSONObject storeObject = new JSONObject();
    private JSONObject defaultAddressObject = new JSONObject();
    private JSONArray promoProducts = new JSONArray();
    private JSONArray storeProducts= new JSONArray();
    private JSONArray nonstoreProducts = new JSONArray();
    private boolean isPick, isSend, isPaai, isDeliveryOrder;
    private Month dateName = new Month();
    private Tracker mTracker;
    private Encode2 encode = new Encode2();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_electronic);

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
            from = intent.getStringExtra("from");
            if (from.equals("klikindomaret")){
                defaultAddress = new JSONObject(intent.getStringExtra("defaultAddress"));
                isSend = intent.getBooleanExtra("isSend", false);
                isPick = intent.getBooleanExtra("isPick", false);
                isPaai = intent.getBooleanExtra("isPaai", false);
                isDeliveryOrder = intent.getBooleanExtra("isDeliveryOrder", false);
                time = intent.getStringExtra("time");
                date = intent.getStringExtra("date");
                sendDate = intent.getStringExtra("sendDate");
                if (intent.getStringExtra("defaultAddressStoreObject") != null) defaultAddressStoreObject = new JSONObject(intent.getStringExtra("defaultAddressStoreObject"));
                if (intent.getStringExtra("defaultAddressObject") != null) defaultAddressObject = new JSONObject(intent.getStringExtra("defaultAddressObject"));
                if (intent.getStringExtra("storeObject") != null) storeObject = new JSONObject(intent.getStringExtra("storeObject"));
                if (intent.getStringExtra("promoProducts") != null) promoProducts = new JSONArray(intent.getStringExtra("promoProducts"));
                if (intent.getStringExtra("storeProductString") != null) storeProducts = new JSONArray(intent.getStringExtra("storeProductString"));
                if (intent.getStringExtra("nonstoreProductString") != null) nonstoreProducts = new JSONArray(intent.getStringExtra("nonstoreProductString"));
            }

            paymentCode = intent.getStringExtra("paymentCode");
            objectResponse = new JSONObject(intent.getStringExtra("objectResponse"));
            salesOrderid = intent.getStringExtra("salesOrderid");

            if (objectResponse.getString("NextStep") != null
                    && objectResponse.getString("NextStep").length() > 0
                    && !objectResponse.getString("NextStep").toLowerCase().equals("null")){
                infoPayment.setVisibility(View.VISIBLE);
                infoPayment.setText(Html.fromHtml(objectResponse.getString("NextStep")));
            }else{
                infoPayment.setVisibility(View.GONE);
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
                if (code.getText().length() < 4){
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(PaymentElectronicActivity.this);
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
                    checkout();
                }
            }
        });
    }

    public void checkout(){
        final JSONObject checkOutObject = new JSONObject();
        String[] arrayDate;
        String stringDate;
        String[] clock;
        String stringClock;

        try {
            runLoader();

            if (from.equals("klikindomaret")){
                if (storeProducts != null && storeProducts.length() > 0){
                    if(isSend){
                        checkOutObject.put("StoreId", "00000000-0000-0000-0000-000000000000");
                        checkOutObject.put("IsDelivery", true);
                        checkOutObject.put("ShopZipCode", "");
                        checkOutObject.put("DistrictId", defaultAddressStoreObject.getString("DistrictId"));
                        checkOutObject.put("CustomerAddressId", defaultAddressStoreObject.getString("ID"));
                    } else if(isPick){
                        checkOutObject.put("StoreId", storeObject.getString("StoreId"));
                        checkOutObject.put("IsDelivery", false);
                        checkOutObject.put("ShopZipCode", storeObject.getString("ZipCode"));
                        checkOutObject.put("DistrictId", storeObject.getString("DistrictId"));
                        checkOutObject.put("CustomerAddressId", "00000000-0000-0000-0000-000000000000");
                    }

                    clock  = time.split("-");
                    stringClock = clock[0].replace(" ","");

                    arrayDate = date.replace(",", " ").split(" ");
                    stringDate = arrayDate[3]+"-"+dateName.getMonthNumber(arrayDate[2])+"-"+arrayDate[1]+"T00:00:00";
                } else {
                    stringClock = "00:00";
                    stringDate = "0000-00-00T00:00:00";
                }

                if(isPaai){
                    SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
                    SimpleDateFormat formatterString = new SimpleDateFormat("yyyy-MM-dd");

                    try {
                        if (sendDate.length() > 0){
                            Date date = formatter.parse(sendDate.toString());
                            String stringDate2 = formatterString.format(date);

                            checkOutObject.put("IPlazaShippingDate",stringDate2+"T00:00:00");
                        }
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }else{
                    checkOutObject.put("IStoreShippingDate", "");
                }

                checkOutObject.put("CityId", "00000000-0000-0000-0000-000000000000");
                checkOutObject.put("IStoreShippingDate", stringDate);
                checkOutObject.put("IStoreShippingDateDelivery", stringDate);
                checkOutObject.put("IStoreShippingDateShipping", stringDate);
                checkOutObject.put("IStorePreferTime", stringClock+":00");
                checkOutObject.put("IStorePreferTimeDelivery", stringClock+":00");
                checkOutObject.put("IStorePreferTimeShipping", stringClock+":00");
                checkOutObject.put("ShippingTariff", 0);
                checkOutObject.put("IsHaveInsurance", false);
                checkOutObject.put("PlazaStartEtd", 0);
                checkOutObject.put("PlazaEndEtd", 0);
                checkOutObject.put("WarehouseShippingPartner", "JNE");
                checkOutObject.put("JneTariff", 0);
                checkOutObject.put("RpxTariff", 0);
                checkOutObject.put("IsDeliveryStoreActivated", false);
                checkOutObject.put("IsVirtual", false);
                checkOutObject.put("Address", null);
                checkOutObject.put("CustomerID", sessionManager.getUserID());
                checkOutObject.put("ShoppingCartID", sessionManager.getCartId());
                checkOutObject.put("RegionID", sessionManager.getRegionID());
                checkOutObject.put("PaymentPromoSelected", promoProducts);

                if(storeProducts.length() > 0 && isSend) checkOutObject.put("AddressIDStore", defaultAddressStoreObject.getString("ID"));
                else checkOutObject.put("AddressIDStore", "00000000-0000-0000-0000-000000000000");

                if(nonstoreProducts.length() > 0 && isDeliveryOrder) checkOutObject.put("AddressIDPlaza", defaultAddressObject.getString("ID"));
                else checkOutObject.put("AddressIDPlaza", "00000000-0000-0000-0000-000000000000");

                final String regionId = defaultAddress.getString("Region");
                final String regionName = defaultAddress.getString("RegionName");

                if(regionId.equals(sessionManager.getRegionID())){
                    jsonPost(API.getInstance().getApiCheckout() + "?mfp_id=" + sessionManager.getKeyMfpId(), checkOutObject, "checkout");
                } else {
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(PaymentElectronicActivity.this);
                    alertDialogBuilder.setMessage("Perbedaan Kota Tujuan dapat mempengaruhi harga dan ketersediaan produk. Periksa kembali keranjang belanja Anda.");
                    alertDialogBuilder.setNegativeButton("Batal", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface arg0, int arg1) {
                            stopLoader();
                        }
                    });

                    alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface arg0, int arg1) {
                            sessionManager.setRegionID(regionId);
                            sessionManager.setRegionName(regionName);

                            jsonPost(API.getInstance().getApiCheckout() + "?mfp_id=" + sessionManager.getKeyMfpId(), checkOutObject, "checkout");

                        }
                    });

                    AlertDialog alertDialog = alertDialogBuilder.create();
                    alertDialog.setCanceledOnTouchOutside(false);
                    alertDialog.show();
                }
            }else{
                try {
                    checkOutObject.put("ID", salesOrderid);
                    checkOutObject.put("PaymentMethodCode", paymentCode);
                    checkOutObject.put("TokenEMoney", code.getText().toString());
                    jsonPostKAI(API.getInstance().getApiPaymentCheckout(), checkOutObject);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void insertPayment(JSONArray response){
        try {
            JSONObject paymentObject = response.getJSONObject(0);
            JSONObject insertPaymentObject = new JSONObject();
            salesOrderid = paymentObject.getString("SalesOrderHeaderId");

            insertPaymentObject.put("CreateBy", sessionManager.getUserID());
            insertPaymentObject.put("SalesOrderHeaderId", salesOrderid);
            insertPaymentObject.put("GatewayCode", paymentCode);
            insertPaymentObject.put("CODValue", "");
            insertPaymentObject.put("IsCODTimeValid", true);
            insertPaymentObject.put("ShippingTime", paymentObject.getString("ShippingTime"));
            insertPaymentObject.put("instalmentCredit", "");
            insertPaymentObject.put("vouchercode", "");
            insertPaymentObject.put("regionID", sessionManager.getRegionID());
            insertPaymentObject.put("NoHPEMoney", "");
            insertPaymentObject.put("TokenEMoney", code.getText().toString());

            jsonPost(API.getInstance().getApiPostInsertPayment() + "?mfp_id=" + sessionManager.getKeyMfpId(), insertPaymentObject, "insertPayment");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void jsonPost(String urlJsonObj, JSONObject jsonObject, final String type){
        System.out.println("update url= " + urlJsonObj);
        System.out.println("update = object" + jsonObject);

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                urlJsonObj, jsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(final JSONObject response) {
                        if (response == null || response.length() == 0){
                            stopLoader();
                            Toast.makeText(PaymentElectronicActivity.this, "Gagal terkoneksi server", Toast.LENGTH_LONG).show();
                        }else{
                            try {
                                if (type.equals("checkout")){
                                    String message = response.getString("Message");

                                    if(response.getBoolean("IsSuccess")){
                                        jsonArrayRequest(API.getInstance().getApiPayment() +"?SalesOrderHeaderId="+response.getJSONObject("ResponseObject").getString("SalesOrderHeaderId"), "getPayment");
                                    } else {
                                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(PaymentElectronicActivity.this);

                                        if (response.getString("Message").contains("belum lunas")){
                                            alertDialogBuilder.setMessage(message);

                                            alertDialogBuilder.setPositiveButton("Lihat Riwayat Pesanan", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface arg0, int arg1) {
                                                    intent = new Intent(PaymentElectronicActivity.this, ProfileActivity.class);
                                                    intent.putExtra("pageindex", "2");
                                                    startActivity(intent);

                                                    finish();
                                                    overridePendingTransition(R.anim.right_out, R.anim.right_in);
                                                }
                                            });
                                        }  else if(response.getString("Message").contains("kesalahan saat cek stok")){
                                            //nothing
                                        }  else if (response.getString("Message").toLowerCase().contains("terverifikasi")){
                                            alertDialogBuilder.setMessage("Nomor handphone belum terverifikasi, silahkan verifikasi nomor handphone Anda terlebih dahulu.");
                                            alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface arg0, int arg1) {
                                                    finish();
                                                    intent = new Intent(PaymentElectronicActivity.this, VerificationPhoneActivity.class);
                                                    intent.putExtra("type", "otpUser");
                                                    startActivity(intent);
                                                    overridePendingTransition(R.anim.left_in, R.anim.left_out);
                                                }
                                            });
                                        } else {
                                            alertDialogBuilder.setMessage(response.getString("Message"));
                                            alertDialogBuilder.setPositiveButton("Tutup", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface arg0, int arg1) {
                                                    try {
                                                        if (response.getString("Message").toLowerCase().contains("persediaan")){
                                                            sessionManager.setEmptyProd(response.getJSONObject("ResponseObject").toString());
                                                            sessionManager.setKeyStock(true);
                                                        }
                                                    } catch (JSONException e) {
                                                        e.printStackTrace();
                                                    }

                                                    finish();
                                                    overridePendingTransition(R.anim.right_out, R.anim.right_in);
                                                }
                                            });
                                        }

                                        AlertDialog alertDialog = alertDialogBuilder.create();
                                        alertDialog.show();
                                    }
                                }else if(type.equals("insertPayment")){
                                        if(response.getString("Message").contains("redirect")){
                                            String callbackUrl = response.getJSONObject("ResponseObject").getString("CallBackURL");
                                            String transactionCode = response.getJSONObject("ResponseObject").getString("TransactionCode");

                                            if(callbackUrl.toLowerCase().contains("faspay") || callbackUrl.toLowerCase().contains("klikindomaret.com")
                                                    || callbackUrl.toLowerCase().contains("klikbca.com")){
                                                sessionManager.setCartId("00000000-0000-0000-0000-000000000000");
                                                sessionManager.setKeyShipping(false);
                                                sessionManager.setKeyPlazaShipping(false);
                                                sessionManager.setkeyStore(null);

                                                System.out.println("callbackUrl = " + callbackUrl);

                                                intent = new Intent(PaymentElectronicActivity.this, PaymentThirdPartyActivity.class);
                                                intent.putExtra("callbackUrl", callbackUrl);
                                                intent.putExtra("data", salesOrderid);
                                                startActivity(intent);
                                                overridePendingTransition(R.anim.left_in, R.anim.left_out);
                                            } else {
                                                sessionManager.setCartId("00000000-0000-0000-0000-000000000000");

                                                System.out.println("payment transactionCode = " + transactionCode);

                                                intent = new Intent(PaymentElectronicActivity.this, ThankyouPageActivity.class);
                                                intent.putExtra("salesOrderId", salesOrderid);
                                                intent.putExtra("transactionCode", transactionCode);
                                                intent.putExtra("from", "pay");
                                                startActivity(intent);
                                                overridePendingTransition(R.anim.left_in, R.anim.left_out);
                                            }
                                        } else if (response.getString("Message").contains("DuplicateTrxID")){
                                            Toast.makeText(PaymentElectronicActivity.this, response.getString("Message"), Toast.LENGTH_SHORT).show();
                                        } else {
                                            if (response.getString("Message").toLowerCase().contains("sudah di proses") || response.getString("Message").toLowerCase().contains("didalam sistem")){
                                                Toast.makeText(PaymentElectronicActivity.this, response.getString("Message"), Toast.LENGTH_SHORT).show();
                                                sessionManager.setCartId("00000000-0000-0000-0000-000000000000");

                                                intent = new Intent(PaymentElectronicActivity.this, MainActivity.class);
                                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | IntentCompat.FLAG_ACTIVITY_CLEAR_TASK);
                                                startActivity(intent);
                                                finish();
                                                overridePendingTransition(R.anim.left_in, R.anim.left_out);
                                            }else{
                                                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(PaymentElectronicActivity.this);
                                                alertDialogBuilder.setMessage(response.getString("Message"));
                                                alertDialogBuilder.setPositiveButton("Tutup", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface arg0, int arg1) {
                                                    }
                                                });
                                                AlertDialog alertDialog = alertDialogBuilder.create();
                                                alertDialog.show();
                                                stopLoader();
                                            }
                                        }

                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(PaymentElectronicActivity.this, "Gagal terkoneksi server", Toast.LENGTH_LONG).show();
                stopLoader();
            }
        });

        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(60000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppController.getInstance().addToRequestQueue(jsonObjReq);
    }

    public void jsonArrayRequest(String url, final String type){
        runLoader();
        JsonArrayRequest jsonArrayReq = new JsonArrayRequest(url,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        if (response == null || response.length() == 0){
                            stopLoader();
                            Toast.makeText(PaymentElectronicActivity.this, "Gagal terkoneksi server", Toast.LENGTH_LONG).show();
                        }else{
                            if (type.equals("getPayment")){
                                insertPayment(response);
                            }
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(PaymentElectronicActivity.this, "Gagal terkoneksi server", Toast.LENGTH_LONG).show();
                stopLoader();
            }
        }, this);

        jsonArrayReq.setRetryPolicy(new DefaultRetryPolicy(60000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppController.getInstance().addToRequestQueue(jsonArrayReq);
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
                                Toast.makeText(PaymentElectronicActivity.this, "Gagal terkoneksi server", Toast.LENGTH_LONG).show();
                            }else{
                                JSONObject jObject = new JSONObject(response);
                                if (jObject.getString("Message").equals("Success")){
                                    if (type.equals("tankyouPage")){
                                        intent = new Intent(PaymentElectronicActivity.this, ThankyouPageKAIActivity.class);
                                        intent.putExtra("objectResponse", jObject.toString());
                                        finish();
                                        startActivity(intent);
                                        overridePendingTransition(R.anim.left_in, R.anim.left_out);
                                    }
                                }else{
                                    btnBuy.setEnabled(true);
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            btnBuy.setEnabled(true);
                            stopLoader();
                            Toast.makeText(PaymentElectronicActivity.this, "Gagal terkoneksi server", Toast.LENGTH_LONG).show();
                        }
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        btnBuy.setEnabled(true);
                        stopLoader();
                        Toast.makeText(PaymentElectronicActivity.this, "Gagal terkoneksi server", Toast.LENGTH_LONG).show();
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
        queue.add(postRequest);
    }

    public void jsonPostKAI(String urlJsonObj, JSONObject jsonObject) {
        RequestQueue queue = Volley.newRequestQueue(this);
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                urlJsonObj, jsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if (response == null || response.length() == 0){
                                stopLoader();
                                Toast.makeText(PaymentElectronicActivity.this, "Gagal terkoneksi server", Toast.LENGTH_LONG).show();
                            }else{
                                if (response.getString("Message").equals("Success")){
                                    if (response.getJSONObject("Data").getBoolean("IsThankYouPage")){
                                        requestWithSomeHttpHeaders(API.getInstance().getApiGetSummaryKAI()
                                                +"?TransactionCode=" + response.getJSONObject("Data").getString("transactionCode"), "tankyouPage");
                                    }else{
                                        intent = new Intent(PaymentElectronicActivity.this, PaymentThirdPartyActivity.class);
                                        intent.putExtra("callbackUrl", response.getJSONObject("Data").getString("RedirectUrl"));
                                        intent.putExtra("data", response.getJSONObject("Data").getString("transactionCode"));
                                        finish();
                                        startActivity(intent);
                                        overridePendingTransition(R.anim.left_in, R.anim.left_out);
                                    }

                                    stopLoader();
                                }else{
                                    AppController application = (AppController) getApplication();
                                    mTracker = application.getDefaultTracker();
                                    mTracker.setScreenName("Thankyou ticket page");
                                    mTracker.send(new HitBuilders.ScreenViewBuilder().build());

                                    AppEventsLogger logger = AppEventsLogger.newLogger(PaymentElectronicActivity.this);
                                    logger.logEvent("History ticket page");

                                    stopLoader();
                                    btnBuy.setEnabled(true);
                                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(PaymentElectronicActivity.this);
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
                            btnBuy.setEnabled(true);
                            Toast.makeText(PaymentElectronicActivity.this, "Gagal terkoneksi server", Toast.LENGTH_LONG).show();
                        }
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        btnBuy.setEnabled(true);
                        stopLoader();
                        Toast.makeText(PaymentElectronicActivity.this, "Gagal terkoneksi server", Toast.LENGTH_LONG).show();
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
        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(50000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(jsonObjReq);
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
