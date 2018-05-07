package com.indomaret.klikindomaret.activity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.multidex.MultiDex;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
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
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.indomaret.klikindomaret.API;
import com.indomaret.klikindomaret.R;
import com.indomaret.klikindomaret.adapter.PassengerOrderSummaryAdapter;
import com.indomaret.klikindomaret.helper.Encode2;
import com.indomaret.klikindomaret.helper.Month;
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
import java.util.Map;

public class OrderSummaryKAIActivity extends AppCompatActivity {
    private Intent intent;
    private String  date, month, year;
    private boolean scheduleDestination;
    private DecimalFormat df = new DecimalFormat("#,###");
    private JSONObject objectResponse = new JSONObject();
    private JSONObject userData = new JSONObject();
    private JSONArray passengerArray = new JSONArray();
    private TextView originTrainName, originTrainClass, originTrainDuration, originTrainTime1, originTrainDate1, originTrainStation1,
            originTrainTime2, originTrainDate2, originTrainStation2,
            destinationTrainName, destinationTrainClass, destinationTrainDuration, destinationTrainTime1, destinationTrainDate1, destinationTrainStation1,
            destinationTrainTime2, destinationTrainDate2, destinationTrainStation2,
            originalPriceText, originalPriceTrain, originalFee, originalDiscount, originalSubtotal,
            destinationPriceText, destinationPriceTrain, destinationFee, destinationDiscount, destinationSubtotal, totalPrice, textCountDown;
    private Button btnSummary, btnPassenger, btnPrice, btnPayment;
    private LinearLayout linearSummary,linearSummaryDestination, linearPassenger, linearPrice, linearPriceDestination, linearDestinationDiscount, linearOriginalDiscount,
            linearOriginalFee, linearDestinationFee;
    private HeightAdjustableListView listPassenger;
    private RelativeLayout preloader;
    private PassengerOrderSummaryAdapter passengerOrderSummaryAdapter;
    private Month dateName = new Month();
    private Encode2 encode = new Encode2();
    private Runnable runnable;
    public static Activity orderSummaryActivity = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        MultiDex.install(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_summary_kai);
        orderSummaryActivity = this;

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        intent = getIntent();

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


        linearSummary = (LinearLayout) findViewById(R.id.linear_summary_ticket);
        linearSummaryDestination = (LinearLayout) findViewById(R.id.linear_summary_destination);
        linearPassenger = (LinearLayout) findViewById(R.id.linear_passenger);
        linearPrice = (LinearLayout) findViewById(R.id.linear_price);
        linearPriceDestination = (LinearLayout) findViewById(R.id.linear_price_destination);
        linearOriginalDiscount = (LinearLayout) findViewById(R.id.linear_original_discount);
        linearDestinationDiscount = (LinearLayout) findViewById(R.id.linear_destination_discount);
        linearDestinationFee = (LinearLayout) findViewById(R.id.linear_destination_fee);
        linearOriginalFee = (LinearLayout) findViewById(R.id.linear_original_fee);
        preloader = (RelativeLayout) findViewById(R.id.preloader);

        listPassenger = (HeightAdjustableListView) findViewById(R.id.list_passenger);

        try {
            userData = new JSONObject(intent.getStringExtra("userData"));
            scheduleDestination = intent.getBooleanExtra("scheduleDestination", false);

            if (scheduleDestination){
                linearSummaryDestination.setVisibility(View.VISIBLE);
                linearPriceDestination.setVisibility(View.VISIBLE);
            }else{
                linearSummaryDestination.setVisibility(View.GONE);
                linearPriceDestination.setVisibility(View.GONE);
            }

            objectResponse = new JSONObject(intent.getStringExtra("response"));

            passengerArray = objectResponse.getJSONObject("Data").getJSONArray("passenger");

            passengerOrderSummaryAdapter = new PassengerOrderSummaryAdapter(OrderSummaryKAIActivity.this, passengerArray, scheduleDestination);
            listPassenger.setAdapter(passengerOrderSummaryAdapter);

            setSummaryTicket(objectResponse);
            countDownStart(objectResponse.getJSONObject("Data"));

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
                try {
                    btnPayment.setEnabled(false);
                    preloader.setVisibility(View.VISIBLE);

                    requestWithSomeHttpHeaders(API.getInstance().getApiSalesOrderId()
                            +"?ID="+objectResponse.getJSONObject("Data").getString("SalesOrderHeaderId"), "booking");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void countDownStart(JSONObject jsonObject) {
        final Handler handler = new Handler();
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
                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(OrderSummaryKAIActivity.this);
                        LayoutInflater factory = LayoutInflater.from(OrderSummaryKAIActivity.this);
                        final View view = factory.inflate(R.layout.timeout_kai, null);
                        alertDialogBuilder.setView(view);

                        Button btnBack = (Button) view.findViewById(R.id.btn_back);

                        btnBack.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                handler.removeCallbacks(runnable);
                                intent = new Intent(OrderSummaryKAIActivity.this, HomeKAIActivity.class);
                                intent.putExtra("from", "klikindomaret");
                                startActivity(intent);
                                if (HomeKAIActivity.homeKAIActivity != null) HomeKAIActivity.homeKAIActivity.finish();
                                if (ListTicketKAIActivity.listTicketKAIActivity != null) ListTicketKAIActivity.listTicketKAIActivity.finish();
                                if (PassengerActivity.passengerActivity != null) PassengerActivity.passengerActivity.finish();
                                if (ChooseSeatActivity.chooseSeatActivity != null) ChooseSeatActivity.chooseSeatActivity.finish();
                                finish();
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

    public void setPrice(JSONObject objectData){
        try {
            JSONObject trainArray = objectData.getJSONObject("Data");

            Double totalPassenger = Double.valueOf(trainArray.getJSONObject("BookingPergi").getString("TotalAdultPassenger"));
            Double normalPrice = trainArray.getJSONObject("BookingPergi").getDouble("PriceAdult");
            Double fee = trainArray.getJSONObject("BookingPergi").getDouble("idm_fee")
                    + trainArray.getJSONObject("BookingPergi").getDouble("Extra_fee");
            Double discount = trainArray.getJSONObject("BookingPergi").getDouble("Discount");
            Double price = totalPassenger * normalPrice;
            Double subTotal = price + fee - discount;
            Double subTotal2 = 0.0;

            originalPriceText.setText("Dewasa (" + trainArray.getJSONObject("BookingPergi").getString("TotalAdultPassenger") + " x Rp " + df.format(normalPrice).replace(",", ".") + ")");
            originalPriceTrain.setText("Rp " + df.format(price).replace(",", "."));
            originalFee.setText("Rp " + df.format(fee).replace(",", "."));
            originalDiscount.setText("Rp -" + df.format(discount).replace(",", "."));
            originalSubtotal.setText("Rp " + df.format(subTotal).replace(",", "."));

            if (fee == 0.0){
                linearOriginalFee.setVisibility(View.GONE);
            }else{
                linearOriginalFee.setVisibility(View.VISIBLE);
            }

            if (discount == 0.0){
                linearOriginalDiscount.setVisibility(View.GONE);
            }else{
                linearOriginalDiscount.setVisibility(View.VISIBLE);
            }

            if (scheduleDestination){
                Double totalPassenger2 = Double.valueOf(trainArray.getJSONObject("BookingPulang").getString("TotalAdultPassenger"));
                Double normalPrice2 = trainArray.getJSONObject("BookingPulang").getDouble("PriceAdult");
                Double fee2 = trainArray.getJSONObject("BookingPulang").getDouble("idm_fee")
                        + trainArray.getJSONObject("BookingPulang").getDouble("Extra_fee");
                Double discount2 = trainArray.getJSONObject("BookingPulang").getDouble("Discount");
                Double price2 = totalPassenger2 * normalPrice2;
                subTotal2 = price2 + fee2 - discount2;

                destinationPriceText.setText("Dewasa (" + trainArray.getJSONObject("BookingPulang").getString("TotalAdultPassenger") + " x Rp " + df.format(normalPrice2).replace(",", ".") + ")");
                destinationPriceTrain.setText("Rp " + df.format(price2).replace(",", "."));
                destinationFee.setText("Rp " + df.format(fee2).replace(",", "."));
                destinationDiscount.setText("Rp -" + df.format(discount2).replace(",", "."));
                destinationSubtotal.setText("Rp " + df.format(subTotal2).replace(",", "."));

                if (fee2 == 0.0){
                    linearDestinationFee.setVisibility(View.GONE);
                }else{
                    linearDestinationFee.setVisibility(View.VISIBLE);
                }

                if (discount2 == 0.0){
                    linearDestinationDiscount.setVisibility(View.GONE);
                }else{
                    linearDestinationDiscount.setVisibility(View.VISIBLE);
                }
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
            originTrainDuration.setText(bookingPergi.getString("DurationInHour").split("\\.")[0] + "J " + bookingPergi.getString("DurationInMinutes").split("\\.")[0] + "M ");
            originTrainTime1.setText(bookingPergi.getString("DepartureDate").split("T")[1].substring(0, 5));

            date = bookingPergi.getString("DepartureDate").split("T")[0].substring(8, 10);
            month = bookingPergi.getString("DepartureDate").split("T")[0].substring(5, 7);
            year = bookingPergi.getString("DepartureDate").split("T")[0].substring(0, 4);

            originTrainDate1.setText(date + " " + dateName.getMonth2(month) + " " + year);
            originTrainStation1.setText(bookingPergi.getJSONObject("originStation").getString("NamaStasiun"));
            destinationTrainTime1.setText(bookingPergi.getString("ArrivalDate").split("T")[1].substring(0, 5));

            date = bookingPergi.getString("ArrivalDate").split("T")[0].substring(8, 10);
            month = bookingPergi.getString("ArrivalDate").split("T")[0].substring(5, 7);
            year = bookingPergi.getString("ArrivalDate").split("T")[0].substring(0, 4);

            destinationTrainDate1.setText(date + " " + dateName.getMonth2(month) + " " + year);
            destinationTrainStation1.setText(bookingPergi.getJSONObject("destinationStation").getString("NamaStasiun"));

            if (scheduleDestination){
                JSONObject bookingPulang = objectResponse.getJSONObject("Data").getJSONObject("BookingPulang");
                destinationTrainName.setText(bookingPulang.getString("TrainName") + " " + bookingPulang.getString("TrainNo"));
                destinationTrainClass.setText(bookingPulang.getString("TrainClassName") + " (" + bookingPulang.getString("TrainSubClassCode") + ")");
                destinationTrainDuration.setText(bookingPulang.getString("DurationInHour").split("\\.")[0] + "J " + bookingPulang.getString("DurationInMinutes").split("\\.")[0] + "M ");
                originTrainTime2.setText(bookingPulang.getString("DepartureDate").split("T")[1].substring(0, 5));

                date = bookingPulang.getString("DepartureDate").split("T")[0].substring(8, 10);
                month = bookingPulang.getString("DepartureDate").split("T")[0].substring(5, 7);
                year = bookingPulang.getString("DepartureDate").split("T")[0].substring(0, 4);

                originTrainDate2.setText(date + " " + dateName.getMonth2(month) + " " + year);
                originTrainStation2.setText(bookingPulang.getJSONObject("originStation").getString("NamaStasiun"));
                destinationTrainTime2.setText(bookingPulang.getString("ArrivalDate").split("T")[1].substring(0, 5));

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

    public void backHome(){
        intent = new Intent(OrderSummaryKAIActivity.this, HomeKAIActivity.class);
        intent.putExtra("from", "backHome");
        intent.putExtra("dataTrain", userData.toString());
        if (HomeKAIActivity.homeKAIActivity != null) HomeKAIActivity.homeKAIActivity.finish();
        if (ListTicketKAIActivity.listTicketKAIActivity != null) ListTicketKAIActivity.listTicketKAIActivity.finish();
        if (PassengerActivity.passengerActivity != null) PassengerActivity.passengerActivity.finish();
        if (ChooseSeatActivity.chooseSeatActivity != null) ChooseSeatActivity.chooseSeatActivity.finish();
        finish();
        startActivity(intent);
        overridePendingTransition(R.anim.left_in, R.anim.left_out);
    }

    public void requestWithSomeHttpHeaders(String url, final String type) {
        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest postRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>(){
            @Override
            public void onResponse(String response) {
                try {
                    if (response == null || response.length() == 0){
                        preloader.setVisibility(View.GONE);
                        Toast.makeText(OrderSummaryKAIActivity.this, "Gagal terkoneksi server", Toast.LENGTH_LONG).show();
                    }else{
                        JSONObject jObject = new JSONObject(response);
                        if (jObject.getBoolean("IsSuccess")) {
                            if (type.equals("booking")){
                                JSONObject responseObject = new JSONObject(response);
                                preloader.setVisibility(View.GONE);

                                intent = new Intent(OrderSummaryKAIActivity.this, PaymentKAIActivity.class);
                                intent.putExtra("response", responseObject.toString());
                                intent.putExtra("scheduleDestination", scheduleDestination);
                                startActivity(intent);
                                overridePendingTransition(R.anim.left_in, R.anim.left_out);
                            }else if (type.equals("reversal")){
                                backHome();
                            }
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    btnPayment.setEnabled(true);
                    preloader.setVisibility(View.GONE);
                }
            }
        },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(OrderSummaryKAIActivity.this, "Gagal terkoneksi server", Toast.LENGTH_LONG).show();
                        btnPayment.setEnabled(true);
                        preloader.setVisibility(View.GONE);
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

        postRequest.setRetryPolicy(new DefaultRetryPolicy(50000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(postRequest);
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(OrderSummaryKAIActivity.this);
        alertDialogBuilder.setTitle("Apakah Anda yakin ingin meninggalkan halaman ini?");
        alertDialogBuilder.setMessage("Anda akan diarahkan ke Halaman Awal");
        alertDialogBuilder.setNegativeButton("Batal", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {}
        });

        alertDialogBuilder.setPositiveButton("Ya", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                try {
                    requestWithSomeHttpHeaders(API.getInstance().getApiGetReversal()
                            +"?SalesOrderHeaderID="+objectResponse.getJSONObject("Data").getString("SalesOrderHeaderId"), "reversal");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        btnPayment.setEnabled(true);
    }
}
