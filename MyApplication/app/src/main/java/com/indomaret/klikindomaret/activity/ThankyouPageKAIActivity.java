package com.indomaret.klikindomaret.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.multidex.MultiDex;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.indomaret.klikindomaret.R;
import com.indomaret.klikindomaret.adapter.PassengerPaymentKAIAdapter;
import com.indomaret.klikindomaret.app.AppController;
import com.indomaret.klikindomaret.helper.Month;
import com.indomaret.klikindomaret.views.HeightAdjustableListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class ThankyouPageKAIActivity extends AppCompatActivity {
    Intent intent;
    private String date, month, year;
    private DecimalFormat df = new DecimalFormat("#,###");
    private EditText kodeBooking;
    private TextView originTrainName, originstation1, destinationStation1, infoSchedule1, originDuration, originTime1, destinationTime1,
            destinationTrainName, originstation2, destinationStation2, infoSchedule2, destinationDuration, originTime2, destinationTime2,
            expiredDate, expiredTime, paymentName, paymentNote, totalPyment, infoPayment, messageError;
    private ImageView paymentImage;
    private Button btnBackHome;
    private LinearLayout linearDestination, linearInfoSuccess, linearInfoError;
    private HeightAdjustableListView passengerList;
    private JSONObject objectData;
    private JSONArray passengerArray;
    private PassengerPaymentKAIAdapter passengerPaymentKAIAdapter;
    private Month dateName = new Month();
    private Tracker mTracker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        MultiDex.install(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thankyou_page_kai);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        intent = getIntent();

        AppController application = (AppController) getApplication();
        mTracker = application.getDefaultTracker();
        mTracker.setScreenName("Thank you Page Kereta Api");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());

        //facebook pixel
        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);

        kodeBooking = (EditText) findViewById(R.id.kode_text);

        infoPayment = (TextView) findViewById(R.id.info_payment);
        originTrainName = (TextView) findViewById(R.id.origin_train_name);
        originstation1 = (TextView) findViewById(R.id.original_station1);
        destinationStation1 = (TextView) findViewById(R.id.destination_station1);
        infoSchedule1 = (TextView) findViewById(R.id.info_scedule);
        originDuration = (TextView) findViewById(R.id.origin_train_duration);
        originTime1 = (TextView) findViewById(R.id.origin_train_time1);
        destinationTime1 = (TextView) findViewById(R.id.destination_train_time1);
        destinationTrainName = (TextView) findViewById(R.id.destination_train_name);
        originstation2 = (TextView) findViewById(R.id.original_station2);
        destinationStation2 = (TextView) findViewById(R.id.destination_station2);
        infoSchedule2 = (TextView) findViewById(R.id.info_scedule2);
        destinationDuration = (TextView) findViewById(R.id.destination_train_duration);
        originTime2 = (TextView) findViewById(R.id.origin_train_time2);
        destinationTime2 = (TextView) findViewById(R.id.destination_train_time2);
        expiredDate = (TextView) findViewById(R.id.expired_date);
        expiredTime = (TextView) findViewById(R.id.expired_time);
        paymentName = (TextView) findViewById(R.id.payment_name);
        paymentNote = (TextView) findViewById(R.id.payment_note);
        totalPyment = (TextView) findViewById(R.id.total_payment);
        messageError = (TextView) findViewById(R.id.message_error);

        paymentImage = (ImageView) findViewById(R.id.payment_image);
        btnBackHome = (Button) findViewById(R.id.btn_back_home);
        linearDestination = (LinearLayout) findViewById(R.id.linear_destination);
        linearInfoSuccess = (LinearLayout) findViewById(R.id.linear_info_success);
        linearInfoError = (LinearLayout) findViewById(R.id.linear_info_error);
        passengerList = (HeightAdjustableListView) findViewById(R.id.list_passenger);

        try {
            objectData = new JSONObject(intent.getStringExtra("objectResponse"));

            if (objectData.getJSONObject("Data").getString("ResponseMapping").equals("") || objectData.getJSONObject("Data").getString("ResponseMapping") == null ||
                    objectData.getJSONObject("Data").getString("ResponseMapping").equals("null")){
                linearInfoSuccess.setVisibility(View.VISIBLE);
                linearInfoError.setVisibility(View.GONE);
                infoPayment.setText("Anda telah berhasil melakukan pemesanan dengan menggunakan metode pembayaran " + objectData.getJSONObject("Data").getJSONObject("PaymentTypeData").getString("Name") + ".");
            }else{
                linearInfoSuccess.setVisibility(View.GONE);
                linearInfoError.setVisibility(View.VISIBLE);
                messageError.setText(objectData.getJSONObject("Data").getString("ResponseMapping"));
            }

            kodeBooking.setText(objectData.getJSONObject("Data").getString("TransactionCode"));

            if (objectData.getJSONObject("Data").getJSONObject("SalesOrderSummary").getString("BookingPulang") == null
                    || objectData.getJSONObject("Data").getJSONObject("SalesOrderSummary").getString("BookingPulang").equals("null")){
                linearDestination.setVisibility(View.GONE);
            }else{
                linearDestination.setVisibility(View.VISIBLE);
                setSummaryDestination(objectData.getJSONObject("Data").getJSONObject("SalesOrderSummary").getJSONObject("BookingPulang"));
            }

            setPayment(objectData.getJSONObject("Data"));
            setSummaryOriginal(objectData.getJSONObject("Data").getJSONObject("SalesOrderSummary").getJSONObject("BookingPergi"));

            passengerArray = objectData.getJSONObject("Data").getJSONObject("SalesOrderSummary").getJSONObject("BookingPergi").getJSONArray("Passenger");

            passengerPaymentKAIAdapter = new PassengerPaymentKAIAdapter(this, passengerArray);
            passengerList.setAdapter(passengerPaymentKAIAdapter);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        btnBackHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(ThankyouPageKAIActivity.this, HomeKAIActivity.class);
                intent.putExtra("from", "klikindomaret");
                if (ListTicketKAIActivity.listTicketKAIActivity != null) ListTicketKAIActivity.listTicketKAIActivity.finish();
                if (PassengerActivity.passengerActivity != null) PassengerActivity.passengerActivity.finish();
                if (ChooseSeatActivity.chooseSeatActivity != null) ChooseSeatActivity.chooseSeatActivity.finish();
                if (OrderSummaryKAIActivity.orderSummaryActivity != null) OrderSummaryKAIActivity.orderSummaryActivity.finish();
                if (PaymentKAIActivity.paymentKAIActivity != null) PaymentKAIActivity.paymentKAIActivity.finish();
                finish();
                startActivity(intent);
                overridePendingTransition(R.anim.left_in, R.anim.left_out);
            }
        });
    }

    public void setSummaryOriginal(JSONObject jsonObject){
        try {
            date = jsonObject.getString("DepartureDate").split("T")[0].substring(8, 10);
            month = jsonObject.getString("DepartureDate").split("T")[0].substring(5, 7);
            year = jsonObject.getString("DepartureDate").split("T")[0].substring(0, 4);

            originTrainName.setText(jsonObject.getString("TrainName") + " " + jsonObject.getString("TrainNo"));
            originstation1.setText(jsonObject.getJSONObject("originStation").getString("NamaStasiun").toUpperCase());
            destinationStation1.setText(jsonObject.getJSONObject("destinationStation").getString("NamaStasiun").toUpperCase());
            infoSchedule1.setText(setDateName(date, month, year) + ", " + date + " " + dateName.getMonth(month) + " " + year);
            originDuration.setText(jsonObject.getString("DurationInHour") + "J " + jsonObject.getString("DurationInMinutes") + "M");
            originTime1.setText((jsonObject.getString("DepartureDate").split("T")[1]).split("\\.")[0].substring(0, 5));
            destinationTime1.setText((jsonObject.getString("ArrivalDate").split("T")[1]).split("\\.")[0].substring(0, 5));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void setSummaryDestination(JSONObject jsonObject){
        try {
            date = jsonObject.getString("DepartureDate").split("T")[0].substring(8, 10);
            month = jsonObject.getString("DepartureDate").split("T")[0].substring(5, 7);
            year = jsonObject.getString("DepartureDate").split("T")[0].substring(0, 4);

            destinationTrainName.setText(jsonObject.getString("TrainName") + " " + jsonObject.getString("TrainNo"));
            originstation2.setText(jsonObject.getJSONObject("originStation").getString("NamaStasiun"));
            destinationStation2.setText(jsonObject.getJSONObject("destinationStation").getString("NamaStasiun"));
            infoSchedule2.setText(setDateName(date, month, year) + ", " + date + " " + dateName.getMonth(month) + " " + year);
            destinationDuration.setText(jsonObject.getString("DurationInHour") + "J " + jsonObject.getString("DurationInMinutes") + "M");
            originTime2.setText((jsonObject.getString("DepartureDate").split("T")[1]).split("\\.")[0].substring(0, 5));
            destinationTime2.setText((jsonObject.getString("ArrivalDate").split("T")[1]).split("\\.")[0].substring(0, 5));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void setPayment(JSONObject jsonObject){
        try {
            date = jsonObject.getString("ExpiredDate").split("T")[0].substring(8, 10);
            month = jsonObject.getString("ExpiredDate").split("T")[0].substring(5, 7);
            year = jsonObject.getString("ExpiredDate").split("T")[0].substring(0, 4);
            String dayName = "";

            Calendar calendar = new GregorianCalendar(Integer.valueOf(year), Integer.valueOf(month) - 1, Integer.valueOf(date));
            int resultDay = calendar.get(Calendar.DAY_OF_WEEK);
            switch (resultDay) {
                case Calendar.MONDAY:
                    dayName = "Senin";
                    break;
                case Calendar.TUESDAY:
                    dayName = "Selasa";
                    break;
                case Calendar.WEDNESDAY:
                    dayName = "Rabu";
                    break;
                case Calendar.THURSDAY:
                    dayName = "Kamis";
                    break;
                case Calendar.FRIDAY:
                    dayName = "Jumat";
                    break;
                case Calendar.SATURDAY:
                    dayName = "Sabtu";
                    break;
                case Calendar.SUNDAY:
                    dayName = "Minggu";
                    break;
            }

            countDownStart(jsonObject);
            expiredDate.setText(dayName+", " + date + " " + dateName.getMonth(month.toString()) + " " + year + "  " + (jsonObject.getString("ExpiredDate").split("T")[1]).split("\\.")[0].substring(0, 5) + " WIB");
            paymentName.setText(jsonObject.getJSONObject("PaymentTypeData").getString("Name"));
            setImagePayment(jsonObject.getJSONObject("PaymentTypeData"));
            paymentNote.setText(jsonObject.getJSONObject("PaymentTypeData").getString("Note"));
            totalPyment.setText("Rp " + df.format(Double.parseDouble(jsonObject.getJSONObject("SalesOrderSummary").getString("GrandTotal"))).replace(",", "."));
        } catch (JSONException e) {
            e.printStackTrace();
        }
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

            cal.add(Calendar.MINUTE, jsonObject.getInt("ExpiredInMinutes"));
            cal.add(Calendar.SECOND, jsonObject.getInt("ExpiredInSeconds"));

            newTime = df.format(cal.getTime());
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        final String finalHour = newTime;
        Runnable runnable = new Runnable() {
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

                        expiredTime.setText(String.format("%02d", minutes) +" Menit "+ String.format("%02d", seconds)+" Detik");
                    } else {
                        intent = new Intent(ThankyouPageKAIActivity.this, HomeKAIActivity.class);
                        intent.putExtra("from", "klikindomaret");
                        if (ListTicketKAIActivity.listTicketKAIActivity != null) ListTicketKAIActivity.listTicketKAIActivity.finish();
                        if (PassengerActivity.passengerActivity!= null) PassengerActivity.passengerActivity.finish();
                        if (ChooseSeatActivity.chooseSeatActivity != null) ChooseSeatActivity.chooseSeatActivity.finish();
                        if (OrderSummaryKAIActivity.orderSummaryActivity != null) OrderSummaryKAIActivity.orderSummaryActivity.finish();
                        if (PaymentKAIActivity.paymentKAIActivity != null) PaymentKAIActivity.paymentKAIActivity.finish();
                        finish();
                        startActivity(intent);
                        overridePendingTransition(R.anim.left_in, R.anim.left_out);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        handler.postDelayed(runnable, 0);
    }

    public void setImagePayment(JSONObject paymentObject){
        try {
            if(paymentObject.getInt("PaymentPlan") == 01){
                if (paymentObject.getString("Code").equals("402")){
                    paymentImage.setVisibility(View.VISIBLE);
                    paymentImage.setImageResource(R.drawable.transfer);
                } else if (paymentObject.getString("Code").equals("405")){
                    paymentImage.setVisibility(View.VISIBLE);
                    paymentImage.setImageResource(R.drawable.bcaklikpay);
                } else if (paymentObject.getString("Code").equals("406")){
                    paymentImage.setVisibility(View.VISIBLE);
                    paymentImage.setImageResource(R.drawable.mandiriklikpay);
                } else if (paymentObject.getString("Code").equals("500")){
                    paymentImage.setVisibility(View.VISIBLE);
                    paymentImage.setImageResource(R.drawable.kredit);
                } else if (paymentObject.getString("Code").equals("BPPID")){
                    paymentImage.setVisibility(View.VISIBLE);
                    paymentImage.setImageResource(R.drawable.bppid);
                } else if (paymentObject.getString("Code").equals("RKPON")){
                    paymentImage.setVisibility(View.VISIBLE);
                    paymentImage.setImageResource(R.drawable.rkpon);
                } else if (paymentObject.getString("Code").equals("COD")){
                    paymentImage.setVisibility(View.VISIBLE);
                    paymentImage.setImageResource(R.drawable.cod);
                } else if (paymentObject.getString("Code").equals("702")){
                    paymentImage.setVisibility(View.VISIBLE);
                    paymentImage.setImageResource(R.drawable.bca_virtual);
                }
            } else if(paymentObject.getInt("PaymentPlan") == 02){
                paymentImage.setVisibility(View.VISIBLE);
                paymentImage.setImageResource(R.drawable.bkkcn);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public String setDateName(String date, String month, String year){
        String dayName = "";
        Calendar calendar = new GregorianCalendar(Integer.valueOf(year), Integer.valueOf(month) - 1, Integer.valueOf(date));
        int resultDay = calendar.get(Calendar.DAY_OF_WEEK);
        switch (resultDay) {
            case Calendar.MONDAY:
                dayName = "Senin";
                break;
            case Calendar.TUESDAY:
                dayName = "Selasa";
                break;
            case Calendar.WEDNESDAY:
                dayName = "Rabu";
                break;
            case Calendar.THURSDAY:
                dayName = "Kamis";
                break;
            case Calendar.FRIDAY:
                dayName = "Jumat";
                break;
            case Calendar.SATURDAY:
                dayName = "Sabtu";
                break;
            case Calendar.SUNDAY:
                dayName = "Minggu";
                break;
        }

        return dayName;
    }

    @Override
    public void onBackPressed() {
        intent = new Intent(ThankyouPageKAIActivity.this, HomeKAIActivity.class);
        intent.putExtra("from", "klikindomaret");
        if (ListTicketKAIActivity.listTicketKAIActivity != null) ListTicketKAIActivity.listTicketKAIActivity.finish();
        if (PassengerActivity.passengerActivity != null) PassengerActivity.passengerActivity.finish();
        if (ChooseSeatActivity.chooseSeatActivity != null) ChooseSeatActivity.chooseSeatActivity.finish();
        if (OrderSummaryKAIActivity.orderSummaryActivity != null) OrderSummaryKAIActivity.orderSummaryActivity.finish();
        if (PaymentKAIActivity.paymentKAIActivity != null) PaymentKAIActivity.paymentKAIActivity.finish();
        finish();
        startActivity(intent);
        overridePendingTransition(R.anim.left_in, R.anim.left_out);
    }
}
