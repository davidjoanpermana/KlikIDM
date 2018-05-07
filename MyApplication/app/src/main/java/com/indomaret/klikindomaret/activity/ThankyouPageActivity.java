package com.indomaret.klikindomaret.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.indomaret.klikindomaret.API;
import com.indomaret.klikindomaret.MainActivity;
import com.indomaret.klikindomaret.R;
import com.indomaret.klikindomaret.app.AppController;
import com.indomaret.klikindomaret.helper.Month;
import com.indomaret.klikindomaret.helper.SessionManager;
import com.indomaret.klikindomaret.helper.UncaughtExceptionHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.Calendar;

import pl.droidsonroids.gif.GifImageView;

public class ThankyouPageActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private SessionManager sessionManager;
    private Intent intent;
    private Month dateName = new Month();

    private TextView statusPayment, infoPayment, transectionCode, totalPrice, detailPayment, backHome, footerBody, errorMessage, paymentDate, paymentBeforeDate;
    private LinearLayout footer, linearInfoError, linearInfoSuccess, linearPrice, linearDate, linearBeforeDate;
    private RelativeLayout preloader;

    private Tracker mTracker;
    private String transactionCode = "";
    private DecimalFormat df = new DecimalFormat("#,###");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thankyou_page);

        UncaughtExceptionHandler uncaughtExceptionHandler = new UncaughtExceptionHandler();
        uncaughtExceptionHandler.uncaughtException(this);

        AppController application = (AppController) getApplication();
        mTracker = application.getDefaultTracker();

        mTracker.setScreenName("Order Summary Page");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());

        toolbar = (Toolbar) findViewById(R.id.app_toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        sessionManager = new SessionManager(ThankyouPageActivity.this);
        intent = getIntent();

        transactionCode = intent.getStringExtra("transactionCode");

        statusPayment = (TextView) findViewById(R.id.status_payment);
        infoPayment = (TextView) findViewById(R.id.info_payment);
        transectionCode = (TextView) findViewById(R.id.transaction_code);
        totalPrice = (TextView) findViewById(R.id.total_price);
        detailPayment = (TextView) findViewById(R.id.detail_payment);
        backHome = (TextView) findViewById(R.id.back_home);
        footerBody = (TextView) findViewById(R.id.footer_body);
        errorMessage = (TextView) findViewById(R.id.message_error);
        paymentDate = (TextView) findViewById(R.id.payment_date);
        paymentBeforeDate = (TextView) findViewById(R.id.payment_before_date);

        footer = (LinearLayout) findViewById(R.id.footer);
        linearInfoSuccess = (LinearLayout) findViewById(R.id.linear_info_success);
        linearInfoError = (LinearLayout) findViewById(R.id.linear_info_error);
        linearPrice = (LinearLayout) findViewById(R.id.linear_price);
        linearDate = (LinearLayout) findViewById(R.id.linear_date);
        linearBeforeDate = (LinearLayout) findViewById(R.id.linear_before_date);

        preloader = (RelativeLayout) findViewById(R.id.preloader);

        preloader.setVisibility(View.VISIBLE);

        jsonArrayGet(API.getInstance().getApiDetailPayment() + "?TransactionCode=" + transactionCode + "&TypeCode=&RegionID="+ sessionManager.getRegionID() +"&mfp_id=" + sessionManager.getKeyMfpId(), "sales");

        detailPayment.setText(Html.fromHtml("<u>Lihat detail Pesanan</u>"));
        backHome.setText(Html.fromHtml("<u>Kembali ke Beranda</u>"));

        detailPayment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent = new Intent(ThankyouPageActivity.this, ProfileActivity.class);
                intent.putExtra("pageindex", "" + 2);
                finish();
                startActivity(intent);
                overridePendingTransition(R.anim.left_in, R.anim.left_out);
            }
        });

        backHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent = new Intent(ThankyouPageActivity.this, MainActivity.class);
                finish();
                startActivity(intent);
                overridePendingTransition(R.anim.left_in, R.anim.left_out);
            }
        });
    }

    public void jsonArrayGet(String urlJsonObj, final String type){
        preloader.setVisibility(View.VISIBLE);

        System.out.println("summary url = " + urlJsonObj);

        JsonArrayRequest jsonArrayReq = new JsonArrayRequest(urlJsonObj,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        if (response == null || response.length() == 0){
                            preloader.setVisibility(View.GONE);
                            Toast.makeText(ThankyouPageActivity.this, "Gagal terkoneksi server", Toast.LENGTH_LONG).show();
                        }else{
                            if(type.equals("sales")){
                                processOrderSummary(response);

                                preloader.setVisibility(View.GONE);
                            }
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(ThankyouPageActivity.this, "Gagal terkoneksi server", Toast.LENGTH_LONG).show();
                preloader.setVisibility(View.GONE);

            }
        }, this);

        jsonArrayReq.setRetryPolicy(new DefaultRetryPolicy(60000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppController.getInstance().addToRequestQueue(jsonArrayReq);
    }

    public void processOrderSummary (JSONArray response){
        try {
            JSONObject orderSummary = response.getJSONObject(0);

            if(orderSummary.getString("StatusDetailPayment").toLowerCase().equals("success")){
                linearPrice.setVisibility(View.VISIBLE);

                detailPayment.setText(Html.fromHtml("<u>Lihat Status Pembayaran</u>"));
                statusPayment.setText("Pembayaran Berhasil");
                statusPayment.setTextColor(Color.parseColor("#109e0b"));
                transectionCode.setText(orderSummary.getJSONObject("Payment").getString("TransactionCode"));
                totalPrice.setText("Rp " + df.format(orderSummary.getDouble("Total")).replace(",","."));

                if (orderSummary.getJSONObject("Payment").getJSONObject("PaymentType").getString("Note") != null
                        && orderSummary.getJSONObject("Payment").getJSONObject("PaymentType").getString("Note").length() > 0
                        && !orderSummary.getJSONObject("Payment").getJSONObject("PaymentType").getString("Note").toLowerCase().equals("null")){
                    infoPayment.setVisibility(View.VISIBLE);
                    infoPayment.setText(Html.fromHtml(orderSummary.getJSONObject("Payment").getJSONObject("PaymentType").getString("Note")));
                }else{
                    infoPayment.setVisibility(View.GONE);
                }
            }else if(orderSummary.getString("StatusDetailPayment").toLowerCase().equals("failed")){
                linearInfoError.setVisibility(View.VISIBLE);
                linearDate.setVisibility(View.VISIBLE);

                detailPayment.setText(Html.fromHtml("<u>Lihat Status Pembayaran</u>"));
                errorMessage.setText(orderSummary.getJSONObject("Payment").getString("ResponseMapping"));
                statusPayment.setText("Pembayaran Tidak Berhasil");
                statusPayment.setTextColor(Color.parseColor("#ffcc0000"));
                transectionCode.setText(orderSummary.getJSONObject("Payment").getString("TransactionCode"));

                String dates = orderSummary.getJSONObject("SalesOrder").getString("SalesOrderDate").split("T")[0];
                String time = orderSummary.getJSONObject("SalesOrder").getString("SalesOrderDate").split("T")[1];

                String date = dates.substring(8, 10);
                String month = dates.substring(5, 7);
                String year = dates.substring(0, 4);

                paymentDate.setText(date + " " + dateName.getMonth(month) + " " + year + ", Pukul " + time.substring(0, 5));

                if (orderSummary.getJSONObject("Payment").getJSONObject("PaymentType").getString("Note") != null
                        && orderSummary.getJSONObject("Payment").getJSONObject("PaymentType").getString("Note").length() > 0
                        && !orderSummary.getJSONObject("Payment").getJSONObject("PaymentType").getString("Note").toLowerCase().equals("null")){
                    infoPayment.setVisibility(View.VISIBLE);
                    infoPayment.setText(Html.fromHtml(orderSummary.getJSONObject("Payment").getJSONObject("PaymentType").getString("Note")));
                }else{
                    infoPayment.setVisibility(View.GONE);
                }
            }else if(orderSummary.getString("StatusDetailPayment").toLowerCase().equals("onprogress")){
                linearPrice.setVisibility(View.VISIBLE);

                detailPayment.setText(Html.fromHtml("<u>Lihat Status Pembayaran</u>"));
                statusPayment.setText("Pembayaran Sedang Proses");
                statusPayment.setTextColor(Color.parseColor("#0079c2"));

                if (orderSummary.getJSONObject("Payment").getJSONObject("PaymentType").getString("Note") != null
                        && orderSummary.getJSONObject("Payment").getJSONObject("PaymentType").getString("Note").length() > 0
                        && !orderSummary.getJSONObject("Payment").getJSONObject("PaymentType").getString("Note").toLowerCase().equals("null")){
                    infoPayment.setVisibility(View.VISIBLE);
                    infoPayment.setText(Html.fromHtml(orderSummary.getJSONObject("Payment").getJSONObject("PaymentType").getString("Note")));
                }else{
                    infoPayment.setVisibility(View.GONE);
                }
            }else if(orderSummary.getString("StatusDetailPayment").toLowerCase().equals("nextstep")){
                linearPrice.setVisibility(View.VISIBLE);

                statusPayment.setText("Langkah Selanjutnya");
                statusPayment.setTextColor(Color.parseColor("#109e0b"));
                transectionCode.setText(orderSummary.getJSONObject("Payment").getString("TransactionCode"));
                totalPrice.setText("Rp " + df.format(orderSummary.getDouble("Total")).replace(",","."));

                if (orderSummary.getJSONObject("Payment").getJSONObject("PaymentType").getString("Note") != null
                        && orderSummary.getJSONObject("Payment").getJSONObject("PaymentType").getString("Note").length() > 0
                        && !orderSummary.getJSONObject("Payment").getJSONObject("PaymentType").getString("Note").toLowerCase().equals("null")){
                    infoPayment.setVisibility(View.VISIBLE);
                    infoPayment.setText(Html.fromHtml(orderSummary.getJSONObject("Payment").getJSONObject("PaymentType").getString("Note")));
                }else{
                    infoPayment.setVisibility(View.GONE);
                }

                if (orderSummary.getString("PaymentTypeCode").toLowerCase().equals("cod")){
                    linearBeforeDate.setVisibility(View.GONE);
                }else{
                    linearBeforeDate.setVisibility(View.VISIBLE);
                    String limitPayment = orderSummary.getJSONObject("Payment").getString("ExpiredDate");
                    String[] limitDates = limitPayment.split("T");
                    String[] limitDate = limitDates[0].split("-");
                    String[] limitTime = limitDates[1].split(":");
                    String limit = limitDate[2] +" "+ dateName.getMonth(limitDate[1]) +" "+ limitDate[0]+", Pukul "+limitTime[0]+":"+limitTime[1]+" WIB";

                    paymentBeforeDate.setText(limit);
                }
            }

            if (orderSummary.getJSONObject("Payment").getJSONObject("PaymentType").getString("NextStep") != null
                    && orderSummary.getJSONObject("Payment").getJSONObject("PaymentType").getString("NextStep").length() > 0
                    && !orderSummary.getJSONObject("Payment").getJSONObject("PaymentType").getString("NextStep").toLowerCase().equals("null")
                    && !orderSummary.getString("StatusDetailPayment").toLowerCase().equals("success")){
                footer.setVisibility(View.VISIBLE);
                footerBody.setText(Html.fromHtml(orderSummary.getJSONObject("Payment").getJSONObject("PaymentType").getString("NextStep")));
            }else{
                footer.setVisibility(View.GONE);
            }

//            if(orderSummary.getJSONObject("Payment").getString("PaymentStatus").equals("2")){
//                linearDate.setVisibility(View.GONE);
//                linearPrice.setVisibility(View.VISIBLE);
//                linearInfoError.setVisibility(View.GONE);
//
//                detailPayment.setText(Html.fromHtml("<u>Lihat Status Pembayaran</u>"));
//                footer.setVisibility(View.GONE);
//                statusPayment.setText("Pembayaran Berhasil");
//                statusPayment.setTextColor(Color.parseColor("#10cc00"));
//                infoPayment.setText("Terima kasih kami telah menerima pembayaran Anda.\nUntuk pesanan dengan detail dibawah ini");
//                transectionCode.setText(orderSummary.getJSONObject("Payment").getString("TransactionCode"));
//                totalPrice.setText("Rp " + df.format(orderSummary.getDouble("Total")).replace(",","."));
//            } else if(orderSummary.getJSONObject("Payment").getString("PaymentStatus").equals("3")){
//                linearInfoError.setVisibility(View.VISIBLE);
//                linearDate.setVisibility(View.VISIBLE);
//                linearPrice.setVisibility(View.GONE);
//                footer.setVisibility(View.GONE);
//
//                detailPayment.setText(Html.fromHtml("<u>Lihat Status Pembayaran</u>"));
//                errorMessage.setText(orderSummary.getJSONObject("Payment").getString("ResponseMapping"));
//                statusPayment.setText("Pembayaran Tidak Berhasil");
//                statusPayment.setTextColor(Color.parseColor("#ffcc0000"));
//                infoPayment.setText("Mohon maaf, status pembayaran untuk pesanan :");
//                transectionCode.setText(orderSummary.getJSONObject("Payment").getString("TransactionCode"));
//
//                String dates = orderSummary.getJSONObject("SalesOrder").getString("SalesOrderDate").split("T")[0];
//                String time = orderSummary.getJSONObject("SalesOrder").getString("SalesOrderDate").split("T")[1];
//
//                String date = dates.substring(8, 10);
//                String month = dates.substring(5, 7);
//                String year = dates.substring(0, 4);
//
//                paymentDate.setText(date + " " + dateName.getMonth(month) + " " + year + " | " + time.substring(0, 5));
//            } if(orderSummary.getJSONObject("Payment").getString("PaymentStatus").equals("0") && orderSummary.getString("PaymentTypeCode").contains("500")){
//                linearDate.setVisibility(View.GONE);
//                linearPrice.setVisibility(View.VISIBLE);
//                linearInfoError.setVisibility(View.GONE);
//                footer.setVisibility(View.GONE);
//                errorMessage.setVisibility(View.GONE);
//
//                detailPayment.setText(Html.fromHtml("<u>Lihat Status Pembayaran</u>"));
//                statusPayment.setText("Menunggu verifikasi");
//                statusPayment.setTextColor(Color.parseColor("#10cc00"));
//                infoPayment.setText("Terima kasih, pembayaran anda telah kami terima. \n Email konfirmasi akan segera kami kirimkan setelah pembayaran anda berhasil di verifikasi.");
//            } else if(orderSummary.getJSONObject("Payment").getString("PaymentStatus").equals("9") ||
//                    orderSummary.getJSONObject("Payment").getString("PaymentStatus").equals("8")){
//                linearDate.setVisibility(View.GONE);
//                linearPrice.setVisibility(View.VISIBLE);
//                linearInfoError.setVisibility(View.GONE);
//
//                detailPayment.setText(Html.fromHtml("<u>Lihat Status Pembayaran</u>"));
//
//                if (orderSummary.getJSONObject("Payment").getJSONObject("PaymentType").getString("PaymentPlan").equals("01")){
//                    footer.setVisibility(View.GONE);
//                    statusPayment.setText("Pembayaran Tidak Berhasil");
//                    statusPayment.setTextColor(Color.parseColor("#ffcc0000"));
//                    infoPayment.setText("Silakan melakukan pembayaran ulang dengan detail di bawah ini");
//                    transectionCode.setText(orderSummary.getJSONObject("Payment").getString("TransactionCode"));
//                    totalPrice.setText("Rp " + df.format(orderSummary.getDouble("Total")).replace(",","."));
//                } else if (orderSummary.getJSONObject("Payment").getJSONObject("PaymentType").getString("PaymentPlan").equals("02")){
//                    footer.setVisibility(View.VISIBLE);
//
//                    statusPayment.setText("Pembayaran Tidak Berhasil");
//                    statusPayment.setTextColor(Color.parseColor("#ffcc0000"));
//                    infoPayment.setText("Silakan melakukan pembayaran ulang dengan detail di bawah ini");
//                    transectionCode.setText(orderSummary.getJSONObject("Payment").getString("TransactionCode"));
//                    totalPrice.setText("Rp " + df.format(orderSummary.getDouble("Total")).replace(",","."));
//                    footerInfo.setText("Lakukan pembayaran kembali dengan metode pembayaran "+orderSummary.getString("PaymentTypeName"));
//                    imagePayment.setBackgroundResource(R.drawable.kredit);
//                    footerBody.setText("1. Pilih \"Ulangi Pembayaran\".\n" +
//                            "2. Ulangi kembali pembayaran atas nama Anda.\n" +
//                            "3. Lanjutkan proses hingga status pesanan berhasil.\n" +
//                            "4. Pesanan Anda dapat diambil/dikirim pada tanggal serta jam yang telah dipilih sebelumnya.");
//                }
//            }else {
//                linearDate.setVisibility(View.GONE);
//                linearPrice.setVisibility(View.VISIBLE);
//                linearInfoError.setVisibility(View.GONE);
//                detailPayment.setText(Html.fromHtml("<u>Lihat detail Pesanan</u>"));
//
//                if (orderSummary.getString("PaymentTypeCode").toLowerCase().equals("cod")){
//                    footer.setVisibility(View.GONE);
//                    linearBeforeDate.setVisibility(View.GONE);
//
//                    statusPayment.setText("Langkah Selanjutnya");
//                    infoPayment.setText("Silakan tunggu beberapa waktu, customer care kami akan menghubungi Anda untuk proses "+orderSummary.getString("PaymentTypeName"));
//                    transectionCode.setText(orderSummary.getJSONObject("Payment").getString("TransactionCode"));
//                    totalPrice.setText("Rp " + df.format(orderSummary.getDouble("Total")).replace(",","."));
//                }else if (orderSummary.getString("PaymentTypeCode").toLowerCase().equals("bppid")){
//                    footer.setVisibility(View.VISIBLE);
//                    linearBeforeDate.setVisibility(View.VISIBLE);
//
//                    String limitPayment = orderSummary.getJSONObject("Payment").getString("ExpiredDate");
//                    String[] limitDates = limitPayment.split("T");
//                    String[] limitDate = limitDates[0].split("-");
//                    String[] limitTime = limitDates[1].split(":");
//                    String limit = limitDate[2] +" "+ dateName.getMonth(limitDate[1]) +" "+ limitDate[0]+", Pukul "+limitTime[0]+":"+limitTime[1]+" WIB";
//
//                    paymentBeforeDate.setText(limit);
//                    statusPayment.setText("Langkah Selanjutnya");
//                    infoPayment.setText("Silakan lakukan pembayaran di toko Indomaret terdekat, untuk pesanan dengan detail dibawah ini:");
//                    transectionCode.setText(orderSummary.getJSONObject("Payment").getString("TransactionCode"));
//                    totalPrice.setText("Rp " + df.format(orderSummary.getDouble("Total")).replace(",","."));
//                    footerInfo.setText("Cara membayar di toko Indomaret ");
//                    imagePayment.setBackgroundResource(R.drawable.bppid);
//                    footerBody.setText("1. Kunjungi toko Indomaret terdekatmu.\n" +
//                            "2. Memberikan kode pembayaran pada kasir untuk melakukan pembayaran pesanan.\n" +
//                            "3. Perikas kembali Nama Penerima, Produk Pesanan, Alamat Pengiriman/Pengambilan serta Jumlah Pembelanjaan.\n" +
//                            "4. Tunggu konfirmasi lebih lanjut via email atau tim KlikIndomaret akan menghubungi Anda via telepon.");
//                }else {
//                    footer.setVisibility(View.GONE);
//                    linearBeforeDate.setVisibility(View.GONE);
//                    statusPayment.setText("Langkah Selanjutnya");
//                    statusPayment.setTextColor(Color.GREEN);
//
//                    String limitPayment = orderSummary.getString("LimitPayment");
//                    String[] limitDates = limitPayment.split("T");
//                    String[] limitDate = limitDates[0].split("-");
//                    String[] limitTime = limitDates[1].split(":");
//                    String limit = limitDate[2] +" "+ dateName.getMonth(limitDate[1]) +" "+ limitDate[0]+", Pukul "+limitTime[0]+":"+limitTime[1]+" WIB";
//
//                    Calendar calendar = Calendar.getInstance();
//                    int hourNow = calendar.HOUR;
//                    String duration = String.valueOf(Integer.valueOf(limitTime[0]) - hourNow);
//
//
//                    infoPayment.setText("Silakan melakukan pembayaran "+orderSummary.getString("PaymentTypeName") + " dalam kurun waktu " + duration + " jam dengan detail dibawah ini");
//                    transectionCode.setText(orderSummary.getJSONObject("Payment").getString("TransactionCode"));
//                    totalPrice.setText("Rp " + df.format(orderSummary.getDouble("Total")).replace(",","."));
//                }
//            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void onBackPressed() {
        intent = new Intent(ThankyouPageActivity.this, MainActivity.class);
        finish();
        startActivity(intent);
        overridePendingTransition(R.anim.right_in, R.anim.right_out);
    }
}
