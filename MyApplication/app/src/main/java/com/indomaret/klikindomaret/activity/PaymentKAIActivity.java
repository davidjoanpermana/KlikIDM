package com.indomaret.klikindomaret.activity;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.multidex.MultiDex;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
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
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.indomaret.klikindomaret.API;
import com.indomaret.klikindomaret.R;
import com.indomaret.klikindomaret.adapter.PassengerOrderSummaryAdapter;
import com.indomaret.klikindomaret.adapter.PaymentCategoryAdapter;
import com.indomaret.klikindomaret.adapter.PaymentListKAIAdapter;
import com.indomaret.klikindomaret.app.AppController;
import com.indomaret.klikindomaret.helper.Encode2;
import com.indomaret.klikindomaret.helper.Month;
import com.indomaret.klikindomaret.helper.SessionManager;
import com.indomaret.klikindomaret.views.HeightAdjustableListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PaymentKAIActivity extends AppCompatActivity {
    private Intent intent;
    private SessionManager sessionManager;
    private String  date, month, year, paymentCode, gatewayCode;
    private String TokenEMoney = "";
    private Double totalPassenger, normalPrice, fee, discount, price, subTotal, totalPassenger2, normalPrice2, fee2, discount2, price2, subTotal2;
    private boolean scheduleDestination;
    private DecimalFormat df = new DecimalFormat("#,###");
    private JSONObject objectResponse = new JSONObject();
    private JSONObject paymentObject = new JSONObject();
    private JSONObject paymentListObejct = new JSONObject();
    private JSONArray passengerArray = new JSONArray();
    private JSONArray paymentArray = new JSONArray();
    private TextView originTrainName, originTrainClass, originTrainDuration, originTrainTime1, originTrainDate1, originTrainStation1,
            originTrainTime2, originTrainDate2, originTrainStation2,
            destinationTrainName, destinationTrainClass, destinationTrainDuration, destinationTrainTime1, destinationTrainDate1, destinationTrainStation1,
            destinationTrainTime2, destinationTrainDate2, destinationTrainStation2,
            originalPriceText, originalPriceTrain, originalFee, originalDiscount, originalSubtotal,
            destinationPriceText, destinationPriceTrain, destinationFee, destinationDiscount, destinationSubtotal, totalPrice, textCountDown;
    private Button btnSummary, btnPassenger, btnPrice, btnPayment, btnBuy;
    private LinearLayout linearSummary, linearSummaryDestination, linearPassenger, linearPrice, linearPayment, linearPriceDestination;
    private HeightAdjustableListView listPassenger, listPayment;
    private PassengerOrderSummaryAdapter passengerOrderSummaryAdapter;
    private PaymentCategoryAdapter paymentCategoryAdapter;;
    private PaymentListKAIAdapter paymentListKAIAdapter;
    private RelativeLayout preloader;
    private Encode2 encode = new Encode2();
    private Handler handler;
    private Month dateName = new Month();
    private Runnable runnable;
    public static Activity paymentKAIActivity = null;
    private Tracker mTracker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        MultiDex.install(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_kai);
        paymentKAIActivity = this;

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        intent = getIntent();
        sessionManager = new SessionManager(PaymentKAIActivity.this);

        //google analytic
        AppController application = (AppController) getApplication();
        mTracker = application.getDefaultTracker();
        mTracker.setScreenName("Payment Page Kereta Api");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());

        //facebook pixel
        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);
        AppEventsLogger logger = AppEventsLogger.newLogger(this);
        logger.logEvent("Payment ticket page");

        originTrainName = (TextView) findViewById(R.id.origin_train_name);
        originTrainClass = (TextView) findViewById(R.id.origin_train_class);
        originTrainDuration = (TextView) findViewById(R.id.origin_train_duration);
        originTrainTime1 = (TextView) findViewById(R.id.origin_train_time1);
        originTrainDate1 = (TextView) findViewById(R.id.origin_train_date1);
        originTrainStation1 = (TextView) findViewById(R.id.origin_train_station1);
        originTrainTime2 = (TextView) findViewById(R.id.origin_train_time2);
        originTrainDate2 = (TextView) findViewById(R.id.origin_train_date2);
        originTrainStation2 = (TextView) findViewById(R.id.origin_train_station2);
        destinationTrainName = (TextView) findViewById(R.id.destination_train_name);
        destinationTrainClass = (TextView) findViewById(R.id.destination_train_class);
        destinationTrainDuration = (TextView) findViewById(R.id.destination_train_duration);
        destinationTrainTime1 = (TextView) findViewById(R.id.destination_train_time1);
        destinationTrainDate1 = (TextView) findViewById(R.id.destination_train_date1);
        destinationTrainStation1 = (TextView) findViewById(R.id.destination_train_station1);
        destinationTrainTime2 = (TextView) findViewById(R.id.destination_train_time2);
        destinationTrainDate2 = (TextView) findViewById(R.id.destination_train_date2);
        destinationTrainStation2 = (TextView) findViewById(R.id.destination_train_station2);

        originalPriceText = (TextView) findViewById(R.id.original_price_text);
        originalPriceTrain = (TextView) findViewById(R.id.original_price_train);
        originalFee = (TextView) findViewById(R.id.original_fee);
        originalDiscount = (TextView) findViewById(R.id.original_discount);
        originalSubtotal = (TextView) findViewById(R.id.original_subtotal);
        destinationPriceText = (TextView) findViewById(R.id.destination_price_text);
        destinationPriceTrain = (TextView) findViewById(R.id.destination_price_train);
        destinationFee = (TextView) findViewById(R.id.destination_fee);
        destinationDiscount = (TextView) findViewById(R.id.destination_discount);
        destinationSubtotal = (TextView) findViewById(R.id.destination_subtotal);
        totalPrice = (TextView) findViewById(R.id.total_price);
        textCountDown = (TextView) findViewById(R.id.text_countdown);

        btnSummary = (Button) findViewById(R.id.btn_summary);
        btnPayment = (Button) findViewById(R.id.btn_payment);
        btnPassenger = (Button) findViewById(R.id.btn_passenger);
        btnPrice = (Button) findViewById(R.id.btn_price);
        btnBuy = (Button) findViewById(R.id.btn_buy);

        linearSummary = (LinearLayout) findViewById(R.id.linear_summary_ticket);
        linearSummaryDestination = (LinearLayout) findViewById(R.id.linear_summary_destination);
        linearPassenger = (LinearLayout) findViewById(R.id.linear_passenger);
        linearPrice = (LinearLayout) findViewById(R.id.linear_price);
        linearPriceDestination = (LinearLayout) findViewById(R.id.linear_price_destination);
        linearPayment = (LinearLayout) findViewById(R.id.linear_payment);

        listPassenger = (HeightAdjustableListView) findViewById(R.id.list_passenger);
        listPayment = (HeightAdjustableListView) findViewById(R.id.list_payment);
        preloader = (RelativeLayout) findViewById(R.id.preloader);

        try {
            scheduleDestination = intent.getBooleanExtra("scheduleDestination", false);
            objectResponse = new JSONObject(intent.getStringExtra("response"));
            countDownStart(objectResponse.getJSONObject("Data"));

            if (scheduleDestination){
                linearSummaryDestination.setVisibility(View.VISIBLE);
                linearPriceDestination.setVisibility(View.VISIBLE);
            }else{
                linearSummaryDestination.setVisibility(View.GONE);
                linearPriceDestination.setVisibility(View.GONE);
            }

            setRadioPayment(objectResponse.getJSONObject("Data"));
            passengerArray = objectResponse.getJSONObject("Data").getJSONArray("passenger");

            passengerOrderSummaryAdapter = new PassengerOrderSummaryAdapter(PaymentKAIActivity.this, passengerArray, scheduleDestination);
            listPassenger.setAdapter(passengerOrderSummaryAdapter);

            paymentArray = objectResponse.getJSONObject("Data").getJSONArray("PaymentTypeList");

            for (int i=0; i<paymentArray.length(); i++){
                paymentArray.getJSONObject(i).put("status", "0");
            }

            paymentListKAIAdapter = new PaymentListKAIAdapter(PaymentKAIActivity.this, paymentArray);
            listPayment.setAdapter(paymentListKAIAdapter);

            setSummaryTicket(objectResponse);
            setPrice(objectResponse);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        btnSummary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (linearSummary.getVisibility() == View.GONE){
                    btnSummary.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.arrow_up, 0);
                    linearSummary.setVisibility(View.VISIBLE);
                } else {
                    btnSummary.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.arrow_down, 0);
                    linearSummary.setVisibility(View.GONE);
                }
            }
        });

        btnPassenger.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (linearPassenger.getVisibility() == View.GONE){
                    btnPassenger.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.arrow_up, 0);
                    linearPassenger.setVisibility(View.VISIBLE);
                } else {
                    btnPassenger.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.arrow_down, 0);
                    linearPassenger.setVisibility(View.GONE);
                }
            }
        });

        btnPrice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (linearPrice.getVisibility() == View.GONE){
                    btnPrice.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.arrow_up, 0);
                    linearPrice.setVisibility(View.VISIBLE);
                } else {
                    btnPrice.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.arrow_down, 0);
                    linearPrice.setVisibility(View.GONE);
                }
            }
        });

        btnPayment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (linearPayment.getVisibility() == View.GONE){
                    btnPayment.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.arrow_up, 0);
                    linearPayment.setVisibility(View.VISIBLE);
                } else {
                    btnPayment.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.arrow_down, 0);
                    linearPayment.setVisibility(View.GONE);
                }
            }
        });

        btnBuy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnBuy.setEnabled(false);
                runLoader();
                JSONObject jsonObject = new JSONObject();
//                try {
//                    jsonObject.put("ID", objectResponse.getJSONObject("Data").getString("SalesOrderHeaderId"));
////                    jsonObject.put("PaymentMethodCode", paymentCode);
//                    jsonObject.put("PaymentMethodCode", gatewayCode);
//                    jsonObject.put("TokenEMoney", TokenEMoney);
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//
//                jsonPost(API.getInstance().getApiPaymentCheckout() + "?mfp_id=" + sessionManager.getKeyMfpId(), jsonObject);
                getBuy(gatewayCode);
            }
        });
    }

    public void getBuy(String code){
        try {
            if(code.equals("RKPON") || code.equals("BPISAKU")){
                stopLoader();
                intent = new Intent(PaymentKAIActivity.this, PaymentElectronicActivity.class);
                intent.putExtra("paymentCode", gatewayCode);
                intent.putExtra("objectResponse", paymentObject.toString());
                intent.putExtra("salesOrderid", objectResponse.getJSONObject("Data").getString("SalesOrderHeaderId"));
                intent.putExtra("from", "kai");
                startActivity(intent);
                overridePendingTransition(R.anim.left_in, R.anim.left_out);

//                LayoutInflater inflater = (LayoutInflater) PaymentKAIActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//                View addRkponLayout = null;
//
//                if (addRkponLayout == null) {
//                    addRkponLayout = inflater.inflate(R.layout.rkpon_emoney_layout, null);
//                }
//
//                final EditText rkCouponCodeTxt = (EditText) addRkponLayout.findViewById(R.id.rk_coupon_code);
//                final Button btnTutup = (Button) addRkponLayout.findViewById(R.id.cancel_rkponPay);
//                final Button btnBayar = (Button) addRkponLayout.findViewById(R.id.submit_rkponPay);
//
//                if (code.equals("RKPON")){
//                    rkCouponCodeTxt.setHint("Kode Kupon Rekening Ponsel");
//                }else{
//                    rkCouponCodeTxt.setHint("Kode ISaku Rekening Ponsel");
//                }
//
//                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(PaymentKAIActivity.this);
//                alertDialogBuilder.setView(addRkponLayout);
//                final AlertDialog alertDialog = alertDialogBuilder.create();
//                alertDialog.setCanceledOnTouchOutside(false);
//                alertDialog.show();
//
//                btnTutup.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        alertDialog.hide();
//                        stopLoader();
//                    }
//                });
//
//                btnBayar.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        if (rkCouponCodeTxt.getText().length() < 4){
//                            Toast.makeText(PaymentKAIActivity.this, "Kode Token tidak boleh kurang dari 4 digit", Toast.LENGTH_SHORT).show();
//                        }else {
//                            TokenEMoney = rkCouponCodeTxt.getText().toString();
////                            alertDialog.dismiss();
//
//                            runLoader();
//                            JSONObject jsonObject = new JSONObject();
//                            try {
//                                jsonObject.put("ID", objectResponse.getJSONObject("Data").getString("SalesOrderHeaderId"));
////                                jsonObject.put("PaymentMethodCode", paymentCode);
//                                jsonObject.put("PaymentMethodCode", gatewayCode);
//                                jsonObject.put("TokenEMoney", TokenEMoney);
//                            } catch (JSONException e) {
//                                e.printStackTrace();
//                            }
//
//                            jsonPost(API.getInstance().getApiPaymentCheckout(), jsonObject);
//                        }
//                    }
//                });
            }else if (code.equalsIgnoreCase("disat") || code.equalsIgnoreCase("xltun")){
                LayoutInflater inflater = (LayoutInflater) PaymentKAIActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View addXlIndLayout = null;

                if (addXlIndLayout == null) {
                    addXlIndLayout = inflater.inflate(R.layout.indosat_xl_emoney_layout, null);
                }

                final EditText tokenCode = (EditText)  addXlIndLayout.findViewById(R.id.token_code);
                Button btnTutup = (Button) addXlIndLayout.findViewById(R.id.cancel_xl_indPay);
                Button btnBayar = (Button) addXlIndLayout.findViewById(R.id.submit_xl_indPay);

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(PaymentKAIActivity.this);
                alertDialogBuilder.setView(addXlIndLayout);
                final AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.setCanceledOnTouchOutside(false);
                alertDialog.show();

                btnTutup.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog.hide();
                        stopLoader();
                    }
                });

                btnBayar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (tokenCode.getText().length() < 4){
                            Toast.makeText(PaymentKAIActivity.this, "Kode Token tidak boleh kurang dari 4 digit", Toast.LENGTH_SHORT).show();
                        }else {
                            TokenEMoney = tokenCode.getText().toString();
//                            alertDialog.dismiss();

                            runLoader();
                            JSONObject jsonObject = new JSONObject();
                            try {
                                jsonObject.put("ID", objectResponse.getJSONObject("Data").getString("SalesOrderHeaderId"));
//                                jsonObject.put("PaymentMethodCode", paymentCode);
                                jsonObject.put("PaymentMethodCode", gatewayCode);
                                jsonObject.put("TokenEMoney", TokenEMoney);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            jsonPost(API.getInstance().getApiPaymentCheckout(), jsonObject);
                        }
                    }
                });
            } else {
                runLoader();
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("ID", objectResponse.getJSONObject("Data").getString("SalesOrderHeaderId"));
//                                jsonObject.put("PaymentMethodCode", paymentCode);
                    jsonObject.put("PaymentMethodCode", gatewayCode);
                    jsonObject.put("TokenEMoney", TokenEMoney);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                jsonPost(API.getInstance().getApiPaymentCheckout(), jsonObject);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void setPriceCode(JSONObject priceObaject, int position){
        try {
            paymentCode = priceObaject.getString("Code");
            for (int i=0; i<paymentArray.length(); i++){
                if (i == position){
                    paymentArray.getJSONObject(i).put("status", "1");
                }else{
                    paymentArray.getJSONObject(i).put("status", "0");
                }
            }

            if (paymentCode.toLowerCase().equals("rkpon")){
                LayoutInflater inflater = (LayoutInflater) PaymentKAIActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View addRkponLayout = null;

                if (addRkponLayout == null) {
                    addRkponLayout = inflater.inflate(R.layout.rkpon_emoney_layout, null);
                }

                final EditText rkCouponCodeTxt = (EditText) addRkponLayout.findViewById(R.id.rk_coupon_code);
                final Button btnTutup = (Button) addRkponLayout.findViewById(R.id.cancel_rkponPay);
                final Button btnBayar = (Button) addRkponLayout.findViewById(R.id.submit_rkponPay);

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(PaymentKAIActivity.this);
                alertDialogBuilder.setView(addRkponLayout);
                final AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.setCanceledOnTouchOutside(false);
                alertDialog.show();

                btnTutup.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog.hide();
                    }
                });

                btnBayar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (rkCouponCodeTxt.getText().length() < 4){
                            Toast.makeText(PaymentKAIActivity.this, "Kode Token tidak boleh kurang dari 4 digit", Toast.LENGTH_SHORT).show();
                        }else{
                            TokenEMoney = rkCouponCodeTxt.getText().toString();
                            alertDialog.hide();
                        }
                    }
                });
            }

            paymentListKAIAdapter = new PaymentListKAIAdapter(PaymentKAIActivity.this, paymentArray);
            listPayment.setAdapter(paymentListKAIAdapter);

            setButtonBuy();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    public void countDownStart(JSONObject jsonObject) {
        handler = new Handler();
        String newTime = "";

        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String myTime = df.format(new Date());
        Date d = new Date();
        try {
            d = df.parse(myTime);

            Calendar cal = Calendar.getInstance();
            cal.setTime(d);

            cal.add(Calendar.MINUTE, jsonObject.getInt("ExpiredInMinute"));
            cal.add(Calendar.SECOND, jsonObject.getInt("ExpiredInSecond"));

            newTime = df.format(cal.getTime());
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        final String finalHour = newTime;
        runnable = new Runnable() {
            @Override
            public void run() {
                handler.postDelayed(this, 1000);
                try {
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    Date eventDate = dateFormat.parse(finalHour);
                    Date currentDate = new Date();

                    if (!currentDate.after(eventDate)) {
                        long diff = eventDate.getTime() - currentDate.getTime();
                        long minutes = diff / (60 * 1000);
                        diff -= minutes * (60 * 1000);
                        long seconds = diff / 1000;

                        textCountDown.setText(String.format("%02d", minutes) +" Menit "+ String.format("%02d", seconds)+" Detik");
                    } else {
                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(PaymentKAIActivity.this);
                        LayoutInflater factory = LayoutInflater.from(PaymentKAIActivity.this);
                        final View view = factory.inflate(R.layout.timeout_kai, null);
                        alertDialogBuilder.setView(view);

                        Button btnBack = (Button) view.findViewById(R.id.btn_back);

                        btnBack.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                handler.removeCallbacks(runnable);
                                intent = new Intent(PaymentKAIActivity.this, HomeKAIActivity.class);
                                intent.putExtra("from", "klikindomaret");
                                if (HomeKAIActivity.homeKAIActivity != null) HomeKAIActivity.homeKAIActivity.finish();
                                if (ListTicketKAIActivity.listTicketKAIActivity != null) ListTicketKAIActivity.listTicketKAIActivity.finish();
                                if (PassengerActivity.passengerActivity != null) PassengerActivity.passengerActivity.finish();
                                if (ChooseSeatActivity.chooseSeatActivity != null) ChooseSeatActivity.chooseSeatActivity.finish();
                                if (OrderSummaryKAIActivity.orderSummaryActivity != null) OrderSummaryKAIActivity.orderSummaryActivity.finish();
                                finish();
                                startActivity(intent);
                                overridePendingTransition(R.anim.left_in, R.anim.left_out);
                            }
                        });
                        alertDialogBuilder.show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        handler.postDelayed(runnable, 0);
    }

    public void setButtonBuy(){
        btnBuy.setBackgroundColor(Color.parseColor("#0079C2"));
        btnBuy.setEnabled(true);
    }

    public void setDisableButtonBuy(){
        btnBuy.setBackgroundResource(R.drawable.button_style_4);
        btnBuy.setEnabled(false);
    }

    public void setPrice(JSONObject objectData){
        try {
            JSONObject trainArray = objectData.getJSONObject("Data");

            totalPassenger = Double.valueOf(trainArray.getJSONObject("BookingPergi").getString("TotalAdultPassenger"));
            normalPrice = trainArray.getJSONObject("BookingPergi").getDouble("PriceAdult");
            fee = trainArray.getJSONObject("BookingPergi").getDouble("idm_fee")
                    + trainArray.getJSONObject("BookingPergi").getDouble("Extra_fee");
            discount = trainArray.getJSONObject("BookingPergi").getDouble("Discount");
            price = totalPassenger * normalPrice;
            subTotal = price + fee - discount;
            subTotal2 = 0.0;

            originalPriceText.setText("Dewasa (" + totalPassenger.toString().split("\\.")[0] + " x Rp " + df.format(normalPrice).replace(",", ".") + ")");
            originalPriceTrain.setText("Rp " + df.format(price).replace(",", "."));
            originalFee.setText("Rp " + df.format(fee).replace(",", "."));
            originalDiscount.setText("Rp -" + df.format(discount).replace(",", "."));
            originalSubtotal.setText("Rp " + df.format(subTotal).replace(",", "."));

            if (scheduleDestination){
                totalPassenger2 = Double.valueOf(trainArray.getJSONObject("BookingPulang").getString("TotalAdultPassenger"));
                normalPrice2 = trainArray.getJSONObject("BookingPulang").getDouble("PriceAdult");
                fee2 = trainArray.getJSONObject("BookingPulang").getDouble("idm_fee")
                        + trainArray.getJSONObject("BookingPulang").getDouble("Extra_fee");
                discount2 = trainArray.getJSONObject("BookingPulang").getDouble("Discount");
                price2 = totalPassenger2 * normalPrice2;
                subTotal2 = price2 + fee2 - discount2;

                destinationPriceText.setText("Dewasa (" + totalPassenger2.toString().split("\\.")[0] + " x Rp " + df.format(normalPrice2).replace(",", ".") + ")");
                destinationPriceTrain.setText("Rp " + df.format(price2).replace(",", "."));
                destinationFee.setText("Rp " + df.format(fee2).replace(",", "."));
                destinationDiscount.setText("Rp -" + df.format(discount2).replace(",", "."));
                destinationSubtotal.setText("Rp " + df.format(subTotal2).replace(",", "."));
            }

            Double desSubTotal;
            if (subTotal2.equals("")){
                desSubTotal = 0.0;
            }else{
                desSubTotal = subTotal2;
            }

            String total = String.valueOf(subTotal + desSubTotal);
            totalPrice.setText("Rp " + df.format(Double.parseDouble(total)).replace(",", "."));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void setSummaryTicket (JSONObject objectResponse){
        try {
            JSONObject bookingPergi = objectResponse.getJSONObject("Data").getJSONObject("BookingPergi");
            originTrainName.setText(bookingPergi.getString("TrainName") + " " + bookingPergi.getString("TrainNo"));
            originTrainClass.setText(bookingPergi.getString("TrainClassName") + " (" + bookingPergi.getString("TrainSubClassCode") + ")");
            originTrainDuration.setText(bookingPergi.getString("DurationInHour") + "J " + bookingPergi.getString("DurationInMinutes") + "M ");
            originTrainTime1.setText(bookingPergi.getString("DepartureDate").split("T")[1].subSequence(0, 5));

            date = bookingPergi.getString("DepartureDate").split("T")[0].substring(8, 10);
            month = bookingPergi.getString("DepartureDate").split("T")[0].substring(5, 7);
            year = bookingPergi.getString("DepartureDate").split("T")[0].substring(0, 4);

            originTrainDate1.setText(date + " " + dateName.getMonth2(month) + " " + year);
            originTrainStation1.setText(bookingPergi.getJSONObject("originStation").getString("NamaStasiun"));
            destinationTrainTime1.setText(bookingPergi.getString("ArrivalDate").split("T")[1].subSequence(0, 5));

            date = bookingPergi.getString("ArrivalDate").split("T")[0].substring(8, 10);
            month = bookingPergi.getString("ArrivalDate").split("T")[0].substring(5, 7);
            year = bookingPergi.getString("ArrivalDate").split("T")[0].substring(0, 4);

            destinationTrainDate1.setText(date + " " + dateName.getMonth2(month) + " " + year);
            destinationTrainStation1.setText(bookingPergi.getJSONObject("destinationStation").getString("NamaStasiun"));

            if (scheduleDestination){
                JSONObject bookingPulang = objectResponse.getJSONObject("Data").getJSONObject("BookingPulang");
                destinationTrainName.setText(bookingPulang.getString("TrainName") + " " + bookingPulang.getString("TrainNo"));
                destinationTrainClass.setText(bookingPulang.getString("TrainClassName") + " (" + bookingPulang.getString("TrainSubClassCode") + ")");
                destinationTrainDuration.setText(bookingPulang.getString("DurationInHour") + "J " + bookingPulang.getString("DurationInMinutes") + "M ");
                originTrainTime2.setText(bookingPulang.getString("DepartureDate").split("T")[1].subSequence(0, 5));

                date = bookingPulang.getString("DepartureDate").split("T")[0].substring(8, 10);
                month = bookingPulang.getString("DepartureDate").split("T")[0].substring(5, 7);
                year = bookingPulang.getString("DepartureDate").split("T")[0].substring(0, 4);

                originTrainDate2.setText(date + " " + dateName.getMonth2(month) + " " + year);
                originTrainStation2.setText(bookingPulang.getJSONObject("originStation").getString("NamaStasiun"));
                destinationTrainTime2.setText(bookingPulang.getString("ArrivalDate").split("T")[1].subSequence(0, 5));

                date = bookingPulang.getString("DepartureDate").split("T")[0].substring(8, 10);
                month = bookingPulang.getString("DepartureDate").split("T")[0].substring(5, 7);
                year = bookingPulang.getString("DepartureDate").split("T")[0].substring(0, 4);

                destinationTrainDate2.setText(date + " " + dateName.getMonth2(month) + " " + year);
                destinationTrainStation2.setText(bookingPulang.getJSONObject("destinationStation").getString("NamaStasiun"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
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
                                Toast.makeText(PaymentKAIActivity.this, "Gagal terkoneksi server", Toast.LENGTH_LONG).show();
                            }else{
                                if (response.getString("Message").equals("Success")){
                                    if (response.getJSONObject("Data").getBoolean("IsThankYouPage")){
                                        requestWithSomeHttpHeaders(API.getInstance().getApiGetSummaryKAI()
                                                +"?TransactionCode=" + response.getJSONObject("Data").getString("transactionCode"), "tankyouPage");
                                    }else{
                                        intent = new Intent(PaymentKAIActivity.this, PaymentThirdPartyActivity.class);
                                        intent.putExtra("callbackUrl", response.getJSONObject("Data").getString("RedirectUrl"));
                                        intent.putExtra("data", response.getJSONObject("Data").getString("transactionCode"));

                                        if (HomeKAIActivity.homeKAIActivity != null) HomeKAIActivity.homeKAIActivity.finish();
                                        if (ListTicketKAIActivity.listTicketKAIActivity != null) ListTicketKAIActivity.listTicketKAIActivity.finish();
                                        if (PassengerActivity.passengerActivity != null) PassengerActivity.passengerActivity.finish();
                                        if (ChooseSeatActivity.chooseSeatActivity != null) ChooseSeatActivity.chooseSeatActivity.finish();
                                        if (OrderSummaryKAIActivity.orderSummaryActivity != null) OrderSummaryKAIActivity.orderSummaryActivity.finish();
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

                                    AppEventsLogger logger = AppEventsLogger.newLogger(PaymentKAIActivity.this);
                                    logger.logEvent("History ticket page");

                                    stopLoader();
                                    btnBuy.setEnabled(true);
                                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(PaymentKAIActivity.this);
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
                            Toast.makeText(PaymentKAIActivity.this, "Gagal terkoneksi server", Toast.LENGTH_LONG).show();
                        }
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        btnBuy.setEnabled(true);
                        stopLoader();
                        Toast.makeText(PaymentKAIActivity.this, "Gagal terkoneksi server", Toast.LENGTH_LONG).show();
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
                                Toast.makeText(PaymentKAIActivity.this, "Gagal terkoneksi server", Toast.LENGTH_LONG).show();
                            }else{
                                JSONObject jObject = new JSONObject(response);
                                if (jObject.getString("Message").equals("Success")){
                                    if (type.equals("tankyouPage")){
                                        intent = new Intent(PaymentKAIActivity.this, ThankyouPageKAIActivity.class);
                                        intent.putExtra("objectResponse", jObject.toString());
                                        handler.removeCallbacks(runnable);
                                        if (HomeKAIActivity.homeKAIActivity != null) HomeKAIActivity.homeKAIActivity.finish();
                                        if (ListTicketKAIActivity.listTicketKAIActivity != null) ListTicketKAIActivity.listTicketKAIActivity.finish();
                                        if (PassengerActivity.passengerActivity != null) PassengerActivity.passengerActivity.finish();
                                        if (ChooseSeatActivity.chooseSeatActivity != null) ChooseSeatActivity.chooseSeatActivity.finish();
                                        if (OrderSummaryKAIActivity.orderSummaryActivity != null) OrderSummaryKAIActivity.orderSummaryActivity.finish();
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
                            Toast.makeText(PaymentKAIActivity.this, "Gagal terkoneksi server", Toast.LENGTH_LONG).show();
                        }
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        btnBuy.setEnabled(true);
                        stopLoader();
                        Toast.makeText(PaymentKAIActivity.this, "Gagal terkoneksi server", Toast.LENGTH_LONG).show();
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

    public void setRadioPayment(JSONObject paymentAdapterObject){
        try {
            LinearLayout linearListPayment = (LinearLayout) findViewById(R.id.linear_list_payment);
            linearListPayment.removeAllViews();

            for (int j=0; j<paymentAdapterObject.getJSONArray("PaymentTypeList").length(); j++){
                paymentAdapterObject.getJSONArray("PaymentTypeList").getJSONObject(j).put("selected", "0");

                if (paymentAdapterObject.getJSONArray("PaymentTypeList").getJSONObject(j).getString("Code").contains("500C")){
                    paymentAdapterObject.getJSONArray("PaymentTypeList").getJSONObject(j).put("PaymentMethod", "9");
                }
            }

            paymentListObejct = paymentAdapterObject;

            for (int i=0; i<paymentAdapterObject.getJSONArray("PaymentMethod").length(); i++){
                RecyclerView listPayment = new RecyclerView(PaymentKAIActivity.this);
                JSONObject paymentMethodObject = new JSONObject();
                JSONArray list = new JSONArray();

                paymentMethodObject.put("paymentMethod", paymentAdapterObject.getJSONArray("PaymentMethod").get(i));
                list.put(paymentMethodObject);

                listPayment.setHasFixedSize(true);
                listPayment.setLayoutManager(new LinearLayoutManager(PaymentKAIActivity.this));
                paymentCategoryAdapter = new PaymentCategoryAdapter(PaymentKAIActivity.this, list, paymentAdapterObject, "kai", null, "");

                listPayment.setAdapter(paymentCategoryAdapter);
                linearListPayment.addView(listPayment);

                if (paymentAdapterObject.toString().contains("500C")){
                    if (paymentAdapterObject.getJSONArray("PaymentMethod").get(i).toString().split(",")[2].contains("Kartu Kredit")){
                        listPayment = new RecyclerView(PaymentKAIActivity.this);
                        list = new JSONArray();
                        paymentMethodObject = new JSONObject();
                        String paymentCicilan = "9,9,Cicilan,http://klikindomaret.com/content/index/cara-pembayaran";

                        paymentMethodObject.put("paymentMethod", paymentCicilan);
                        list.put(paymentMethodObject);

                        listPayment.setHasFixedSize(true);
                        listPayment.setLayoutManager(new LinearLayoutManager(PaymentKAIActivity.this));
                        paymentCategoryAdapter = new PaymentCategoryAdapter(PaymentKAIActivity.this, list, paymentAdapterObject, "kai", null, "");

                        listPayment.setAdapter(paymentCategoryAdapter);
                        linearListPayment.addView(listPayment);
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void setRadioButton(JSONObject paymentObject, String codePayment){
        this.paymentObject = paymentObject;
        gatewayCode = codePayment;

        try{
            LinearLayout linearListPayment = (LinearLayout) findViewById(R.id.linear_list_payment);
            linearListPayment.removeAllViews();


            for (int j=0; j<paymentListObejct.getJSONArray("PaymentTypeList").length(); j++){
                if (paymentListObejct.getJSONArray("PaymentTypeList").getJSONObject(j).getString("Code").equals(gatewayCode)){
                    paymentListObejct.getJSONArray("PaymentTypeList").getJSONObject(j).put("selected", "1");
                }else{
                    paymentListObejct.getJSONArray("PaymentTypeList").getJSONObject(j).put("selected", "0");
                }
            }

            for (int i=0; i<paymentListObejct.getJSONArray("PaymentMethod").length(); i++){
                RecyclerView listPayment = new RecyclerView(PaymentKAIActivity.this);
                JSONObject paymentMethodObject = new JSONObject();
                JSONArray list = new JSONArray();

                paymentMethodObject.put("paymentMethod", paymentListObejct.getJSONArray("PaymentMethod").get(i));
                list.put(paymentMethodObject);

                listPayment.setHasFixedSize(true);
                listPayment.setLayoutManager(new LinearLayoutManager(PaymentKAIActivity.this));
                paymentCategoryAdapter = new PaymentCategoryAdapter(PaymentKAIActivity.this, list, paymentListObejct, "kai", null, "");

                listPayment.setAdapter(paymentCategoryAdapter);
                linearListPayment.addView(listPayment);

                if (paymentListObejct.toString().contains("500C")){
                    if (paymentListObejct.getJSONArray("PaymentMethod").get(i).toString().split(",")[2].contains("Kartu Kredit")){
                        listPayment = new RecyclerView(PaymentKAIActivity.this);
                        list = new JSONArray();
                        paymentMethodObject = new JSONObject();
                        String paymentCicilan = "9,9,Cicilan,http://klikindomaret.com/content/index/cara-pembayaran";

                        paymentMethodObject.put("paymentMethod", paymentCicilan);
                        list.put(paymentMethodObject);

                        listPayment.setHasFixedSize(true);
                        listPayment.setLayoutManager(new LinearLayoutManager(PaymentKAIActivity.this));
                        paymentCategoryAdapter = new PaymentCategoryAdapter(PaymentKAIActivity.this, list, paymentListObejct, "kai", null, "");

                        listPayment.setAdapter(paymentCategoryAdapter);
                        linearListPayment.addView(listPayment);
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
//        getBuy(gatewayCode);
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
