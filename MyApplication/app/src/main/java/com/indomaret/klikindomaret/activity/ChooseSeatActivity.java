package com.indomaret.klikindomaret.activity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.multidex.MultiDex;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.indomaret.klikindomaret.API;
import com.indomaret.klikindomaret.R;
import com.indomaret.klikindomaret.adapter.ListSeatKAIAdapter;
import com.indomaret.klikindomaret.adapter.PassengerAdultSummaryAdapter;
import com.indomaret.klikindomaret.adapter.PassengerBabySummaryAdapter;
import com.indomaret.klikindomaret.helper.Encode2;
import com.indomaret.klikindomaret.helper.SessionManager;
import com.indomaret.klikindomaret.views.HeightAdjustableListView;
import com.indomaret.klikindomaret.views.WrapContentViewPager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChooseSeatActivity extends AppCompatActivity implements ViewPager.OnPageChangeListener {
    private Intent intent;
    private SessionManager sessionManager;
    private TextView originalStation, destinationStation, trainName, textCountDown;
    private Button btnPayment, btnPassenger;
    private HeightAdjustableListView listPassengerAdult, listPassengerBaby;
    private PassengerAdultSummaryAdapter passengerAdultSummaryAdapter;
    private PassengerBabySummaryAdapter passengerBabySummaryAdapter;
    private Spinner inputWagon;
    private JSONObject objectResponse = new JSONObject();
    private JSONObject objectDataTicketOriginal = new JSONObject();
    private JSONObject objectDataTicketDestination = new JSONObject();
    private JSONObject objectData = new JSONObject();
    private JSONArray passengerlList = new JSONArray();
    private JSONArray adultList = new JSONArray();
    private JSONArray infantlList = new JSONArray();
    private JSONArray updateChangeSeats = new JSONArray();
    private WrapContentViewPager viewPager;
    private LinearLayout pager_indicator, linearPassenger;
    private ListSeatKAIAdapter listSeatKAIAdapter;
    private int dotsCount;
    private ImageView[] dots;
    private boolean isDestination, scheduleDestination;
    private String current;
    private List<String> wagonList = new ArrayList<>();
    private JSONObject userData = new JSONObject();
    private JSONArray passengerList = new JSONArray();
    private JSONArray seatList = new JSONArray();
    private RelativeLayout preloader;
    private Encode2 encode = new Encode2();
    private Runnable runnable;
    public static Activity chooseSeatActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        MultiDex.install(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_sit);
        chooseSeatActivity = this;

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        intent = getIntent();
        sessionManager = new SessionManager(ChooseSeatActivity.this);

        originalStation = (TextView) findViewById(R.id.original_station);
        destinationStation = (TextView) findViewById(R.id.destination_station);
        trainName = (TextView) findViewById(R.id.train_name);
        textCountDown = (TextView) findViewById(R.id.text_countdown);

        listPassengerAdult = (HeightAdjustableListView) findViewById(R.id.list_passenger_adult);
        listPassengerBaby = (HeightAdjustableListView) findViewById(R.id.list_passenger_baby);
        inputWagon = (Spinner) findViewById(R.id.input_wagon);

        viewPager = (WrapContentViewPager) findViewById(R.id.pager_introduction);
        pager_indicator = (LinearLayout) findViewById(R.id.viewPagerCountDots);
        linearPassenger = (LinearLayout) findViewById(R.id.linear_passenger);

        btnPayment = (Button) findViewById(R.id.btn_to_payment);
        btnPassenger = (Button) findViewById(R.id.btn_data_passenger);
        preloader = (RelativeLayout) findViewById(R.id.preloader);

        try {
            userData = new JSONObject(intent.getStringExtra("userData"));
            objectDataTicketOriginal = new JSONObject(intent.getStringExtra("objectDataTicketOriginal"));
            scheduleDestination = intent.getBooleanExtra("scheduleDestination", false);
            if (scheduleDestination) objectDataTicketDestination = new JSONObject(intent.getStringExtra("objectDataTicketDestination"));

            objectData = new JSONObject(intent.getStringExtra("jsonObject"));
            objectResponse = new JSONObject(intent.getStringExtra("response"));
            isDestination = intent.getBooleanExtra("isDestination", false);
            current = intent.getStringExtra("current");

            if (current.equals("original")){
                runLoader();
                requestWithSomeHttpHeaders(API.getInstance().getApiBookingSeatMap()
                        +"?id="+objectResponse.getJSONArray("Data").getJSONObject(0).getString("SalesOrderHeaderID"), "seatMap");
            }else{
                runLoader();
                requestWithSomeHttpHeaders(API.getInstance().getApiBookingSeatMap()
                        +"?id="+objectResponse.getJSONArray("Data").getJSONObject(1).getString("SalesOrderHeaderID"), "seatMap");
            }

            if (isDestination){
                btnPayment.setText("Pilih Kursi Pulang");
            }else{
                btnPayment.setText("Lanjut Pembayaran");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

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

        inputWagon.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                viewPager.setCurrentItem(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        btnPayment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnPayment.setEnabled(false);
                runLoader();
                if (updateChangeSeats.length() > 0){
                    jsonPost(API.getInstance().getApiChangeSeat(), updateChangeSeats);
                }else{
                    nexChangeSeat();
                }
            }
        });
    }

    public void nexChangeSeat(){
        if (isDestination){
            intent = new Intent(ChooseSeatActivity.this, ChooseSeatActivity.class);
            intent.putExtra("response", objectResponse.toString());
            intent.putExtra("isDestination", false);
            intent.putExtra("jsonObject", objectData.toString());
            intent.putExtra("current", "destination");
            intent.putExtra("userData", userData.toString());
            intent.putExtra("objectDataTicketOriginal", objectDataTicketOriginal.toString());
            if (scheduleDestination) intent.putExtra("objectDataTicketDestination", objectDataTicketDestination.toString());
            intent.putExtra("scheduleDestination", scheduleDestination);
            finish();
            startActivity(intent);
            overridePendingTransition(R.anim.left_in, R.anim.left_out);
        }else{
            try {
                requestWithSomeHttpHeaders(API.getInstance().getApiSalesOrderId()
                        +"?ID="+objectResponse.getJSONArray("Data").getJSONObject(0).getString("SalesOrderHeaderID"), "booking");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public void setSeat(JSONArray jsonArray, JSONObject jsonObejct, String firstWagonNo, String wagonNo){
        JSONObject object = new JSONObject();
        JSONArray wagonArray = new JSONArray();
        int position;

        final float scale = this.getResources().getDisplayMetrics().density;
        int pixels0 = (int) (700 * scale + 0.5f);
        int pixels1 = (int) (850 * scale + 0.5f);
        int pixels2 = (int) (1000 * scale + 0.5f);

        if (current.equals("original")){
            position = 0;
        }else{
            position = 1;
        }

        try {
            if (seatList.getJSONObject(0).getJSONArray("SeatDetails").length() < 60){
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, pixels0);
                viewPager.setLayoutParams(params);
            }else if (seatList.getJSONObject(0).getJSONArray("SeatDetails").length() >= 60 && seatList.getJSONObject(0).getJSONArray("SeatDetails").length() < 80){
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, pixels1);
                viewPager.setLayoutParams(params);
            }else if (seatList.getJSONObject(0).getJSONArray("SeatDetails").length() >= 80){
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, pixels2);
                viewPager.setLayoutParams(params);
            }

            wagonArray = seatList.getJSONObject(Integer.valueOf(firstWagonNo)-1).getJSONArray("SeatDetails");
            for (int j=0; j<wagonArray.length(); j++){
                for (int i=0; i<jsonArray.length(); i++){
                    if (wagonArray.getJSONObject(j).getString("RowSeat").equals(jsonObejct.getString("RowSeat")) &&
                            wagonArray.getJSONObject(j).getString("ColumnSeat").equals(jsonObejct.getString("ColumnSeat"))){
                        seatList.getJSONObject(Integer.valueOf(firstWagonNo)-1).getJSONArray("SeatDetails").getJSONObject(j).put("Status", "0");
                    }
                }
            }

            for (int i=0; i<jsonArray.length(); i++){
                if (!objectResponse.getJSONArray("Data").getJSONObject(position).getJSONArray("Passenger").getJSONObject(i).getString("SeatRow").equals(jsonArray.getJSONObject(i).getString("SeatRow"))
                        || !objectResponse.getJSONArray("Data").getJSONObject(position).getJSONArray("Passenger").getJSONObject(i).getString("SeatPotition").equals(jsonArray.getJSONObject(i).getString("SeatPotition"))
                        || !objectResponse.getJSONArray("Data").getJSONObject(position).getJSONArray("Passenger").getJSONObject(i).getString("wagonNumber").equals(jsonArray.getJSONObject(i).getString("wagonNumber"))){
                    objectResponse.getJSONArray("Data").getJSONObject(position).getJSONArray("Passenger").getJSONObject(i).put("SeatRow", jsonArray.getJSONObject(i).getString("SeatRow"));
                    objectResponse.getJSONArray("Data").getJSONObject(position).getJSONArray("Passenger").getJSONObject(i).put("SeatPotition", jsonArray.getJSONObject(i).getString("SeatPotition"));
                    objectResponse.getJSONArray("Data").getJSONObject(position).getJSONArray("Passenger").getJSONObject(i).put("wagonNumber", jsonArray.getJSONObject(i).getString("wagonNumber"));

                    object.put("BookingNo", objectResponse.getJSONArray("Data").getJSONObject(position).getString("BookingNumber"));
                    object.put("PassengerName", jsonArray.getJSONObject(i).getString("Name"));
                    object.put("wagonNo", jsonArray.getJSONObject(i).getString("wagonNumber"));
                    object.put("SeatRow", jsonArray.getJSONObject(i).getString("SeatRow"));
                    object.put("SeatPosition", jsonArray.getJSONObject(i).getString("SeatPotition"));

                    boolean isExisting = false;
                    int index = 0;
                    if (updateChangeSeats.length() > 0){
                        for (int j=0; j<updateChangeSeats.length(); j++){
                            if (updateChangeSeats.getJSONObject(j).getString("PassengerName").equals(jsonArray.getJSONObject(i).getString("Name"))){
                                isExisting = true;
                                index = j;
                            }
                        }
                    }

                    if (isExisting){
                        updateChangeSeats.getJSONObject(index).put("BookingNo", objectResponse.getJSONArray("Data").getJSONObject(position).getString("BookingNumber"));
                        updateChangeSeats.getJSONObject(index).put("wagonNo", jsonArray.getJSONObject(i).getString("wagonNumber"));
                        updateChangeSeats.getJSONObject(index).put("SeatRow", jsonArray.getJSONObject(i).getString("SeatRow"));
                        updateChangeSeats.getJSONObject(index).put("SeatPosition", jsonArray.getJSONObject(i).getString("SeatPotition"));
                    }else{
                        updateChangeSeats.put(object);
                    }

                    object = new JSONObject();

                    listSeatKAIAdapter = new ListSeatKAIAdapter(this, seatList, jsonArray);
                    viewPager.setAdapter(listSeatKAIAdapter);
                    listSeatKAIAdapter.notifyDataSetChanged();

                    viewPager.setCurrentItem(Integer.valueOf(wagonNo)-1);
                }
            }

            passengerAdultSummaryAdapter = new PassengerAdultSummaryAdapter(ChooseSeatActivity.this, jsonArray);
            listPassengerAdult.setAdapter(passengerAdultSummaryAdapter);
            passengerAdultSummaryAdapter.notifyDataSetChanged();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void countDownStart(JSONObject jsonObject) {
        final Handler handler = new Handler();
        String hour = "";
        try {
            hour = jsonObject.getString("ExpiredDate").replace("T", " ").split("\\.")[0];
        } catch (JSONException e) {
            e.printStackTrace();
        }

        final String finalHour = hour;
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
                        long days = diff / (24 * 60 * 60 * 1000);
                        diff -= days * (24 * 60 * 60 * 1000);
                        long hours = diff / (60 * 60 * 1000);
                        diff -= hours * (60 * 60 * 1000);
                        long minutes = diff / (60 * 1000);
                        diff -= minutes * (60 * 1000);
                        long seconds = diff / 1000;


                        if (String.format("%02d", hours).equals("00")){
                            textCountDown.setText(String.format("%02d", minutes) +" Menit "+ String.format("%02d", seconds)+" Detik");
                        }else{
                            textCountDown.setText(String.format("%02d", hours) +" Jam "+ String.format("%02d", minutes) +" Menit "+ String.format("%02d", seconds)+" Detik");
                        }
                    } else {
                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ChooseSeatActivity.this);
                        LayoutInflater factory = LayoutInflater.from(ChooseSeatActivity.this);
                        final View view = factory.inflate(R.layout.timeout_kai, null);
                        alertDialogBuilder.setView(view);

                        Button btnBack = (Button) view.findViewById(R.id.btn_back);

                        btnBack.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                handler.removeCallbacks(runnable);
                                intent = new Intent(ChooseSeatActivity.this, HomeKAIActivity.class);
                                if (HomeKAIActivity.homeKAIActivity != null) HomeKAIActivity.homeKAIActivity.finish();
                                if (ListTicketKAIActivity.listTicketKAIActivity != null) ListTicketKAIActivity.listTicketKAIActivity.finish();
                                if (PassengerActivity.passengerActivity != null) PassengerActivity.passengerActivity.finish();
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

    public void setPassenger(JSONArray jsonArray, String current){
        int position;
        if (current.equals("original")){
            position = 0;
        }else{
            position = 1;
        }

        try {
            originalStation.setText(jsonArray.getJSONObject(position).getString("OriginalName") + " (" + jsonArray.getJSONObject(0).getString("Original") + ")");
            destinationStation.setText(jsonArray.getJSONObject(position).getString("DestinationName") + " (" + jsonArray.getJSONObject(0).getString("Destination") + ")");
            trainName.setText(jsonArray.getJSONObject(position).getString("TrainName") + " " + jsonArray.getJSONObject(position).getString("TrainNo"));

            infantlList = new JSONArray();
            adultList = new JSONArray();

            passengerlList = jsonArray.getJSONObject(position).getJSONArray("Passengers");
            for (int j=0; j<passengerlList.length(); j++){
                if (passengerlList.getJSONObject(j).getString("Maturity").equals("1")){
                    infantlList.put(passengerlList.getJSONObject(j));
                } else if (passengerlList.getJSONObject(j).getString("Maturity").equals("0")){
                    adultList.put(passengerlList.getJSONObject(j));
                }
            }

            passengerAdultSummaryAdapter = new PassengerAdultSummaryAdapter(ChooseSeatActivity.this, adultList);
            listPassengerAdult.setAdapter(passengerAdultSummaryAdapter);

            passengerBabySummaryAdapter = new PassengerBabySummaryAdapter(ChooseSeatActivity.this, infantlList);
            listPassengerBaby.setAdapter(passengerBabySummaryAdapter);

            passengerAdultSummaryAdapter.notifyDataSetChanged();
            passengerBabySummaryAdapter.notifyDataSetChanged();
            seatMap(jsonArray.getJSONObject(position));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void seatMap(JSONObject jsonObject){
        wagonList = new ArrayList<>();
        passengerList = new JSONArray();
        seatList = new JSONArray();
        final float scale = this.getResources().getDisplayMetrics().density;
        int pixels0 = (int) (700 * scale + 0.5f);
        int pixels1 = (int) (850 * scale + 0.5f);
        int pixels2 = (int) (1000 * scale + 0.5f);

        try {
            for (int i=0; i<jsonObject.getJSONArray("Passengers").length(); i++){
                if (jsonObject.getJSONArray("Passengers").getJSONObject(i).getString("Maturity").equals("0")){
                    passengerList.put(jsonObject.getJSONArray("Passengers").getJSONObject(i));
                }
            }

            seatList = jsonObject.getJSONArray("SeatMaps");
            for (int i=0; i<seatList.length(); i++){
                wagonList.add(seatList.getJSONObject(i).getString("WagonName") + " " + seatList.getJSONObject(i).getString("WagonNo"));
            }

            if (seatList.getJSONObject(0).getJSONArray("SeatDetails").length() < 60){
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, pixels0);
                viewPager.setLayoutParams(params);
            }else if (seatList.getJSONObject(0).getJSONArray("SeatDetails").length() >= 60 && seatList.getJSONObject(0).getJSONArray("SeatDetails").length() < 80){
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, pixels1);
                viewPager.setLayoutParams(params);
            }else if (seatList.getJSONObject(0).getJSONArray("SeatDetails").length() >= 80){
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, pixels2);
                viewPager.setLayoutParams(params);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(ChooseSeatActivity.this, android.R.layout.simple_spinner_item, wagonList);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        inputWagon.setAdapter(dataAdapter);

        pager_indicator.removeAllViews();
        listSeatKAIAdapter = new ListSeatKAIAdapter(this, seatList, passengerList);
        viewPager.setAdapter(listSeatKAIAdapter);

        listSeatKAIAdapter.notifyDataSetChanged();
        viewPager.setCurrentItem(0);
        viewPager.addOnPageChangeListener(this);

        setUiPageViewController();
    }

    private void setUiPageViewController() {
        dotsCount = listSeatKAIAdapter.getCount();
        dots = new ImageView[dotsCount];

        for (int i = 0; i < dotsCount; i++) {
            dots[i] = new ImageView(ChooseSeatActivity.this);
            dots[i].setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.nonselecteditem_dot, null));

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );

            params.setMargins(4, 0, 4, 0);

            pager_indicator.addView(dots[i], params);
        }

        dots[0].setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.selecteditem_dot, null));
    }

    public void backHome(){
        intent = new Intent(ChooseSeatActivity.this, HomeKAIActivity.class);
        intent.putExtra("from", "backHome");
        intent.putExtra("dataTrain", userData.toString());
        if (HomeKAIActivity.homeKAIActivity != null) HomeKAIActivity.homeKAIActivity.finish();
        if (ListTicketKAIActivity.listTicketKAIActivity != null) ListTicketKAIActivity.listTicketKAIActivity.finish();
        if (PassengerActivity.passengerActivity != null) PassengerActivity.passengerActivity.finish();
        finish();
        startActivity(intent);
        overridePendingTransition(R.anim.left_in, R.anim.left_out);
    }

    public void requestWithSomeHttpHeaders(String url, final String type) {
        RequestQueue queue = Volley.newRequestQueue(this);
        runLoader();
        StringRequest postRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>(){
            @Override
            public void onResponse(String response) {
                try {
                    if (response == null || response.length() == 0){
                        stopLoader();
                        Toast.makeText(ChooseSeatActivity.this, "Gagal terkoneksi server", Toast.LENGTH_LONG).show();
                    }else{
                        JSONObject jObject = new JSONObject(response);
                        if (jObject.getBoolean("IsSuccess")) {
                            JSONObject responseObject = new JSONObject(response);
                            if (type.equals("booking")){
                                intent = new Intent(ChooseSeatActivity.this, OrderSummaryKAIActivity.class);
                                intent.putExtra("response", response.toString());
                                intent.putExtra("userData", userData.toString());
                                intent.putExtra("objectDataTicketOriginal", objectDataTicketOriginal.toString());
                                if (scheduleDestination) intent.putExtra("objectDataTicketDestination", objectDataTicketDestination.toString());
                                intent.putExtra("scheduleDestination", scheduleDestination);
                                startActivity(intent);
                                overridePendingTransition(R.anim.left_in, R.anim.left_out);
                                stopLoader();
                            }else if(type.equals("seatMap")){
                                countDownStart(responseObject.getJSONObject("Data"));
                                setPassenger(responseObject.getJSONObject("Data").getJSONArray("BookingSeatMaps"), current);
                                stopLoader();
                            }else if(type.equals("reversal")){
                                backHome();
                            }
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    btnPayment.setEnabled(true);
                    stopLoader();
                }
            }
        },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(ChooseSeatActivity.this, "Gagal terkoneksi server", Toast.LENGTH_LONG).show();
                        stopLoader();
                        btnPayment.setEnabled(true);
                    }
                }
        ) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String>  params = new HashMap<String, String>();
                String token = "";
                try {
                    token = encode.SHA1(encode.md5("66E2C13840534C139D85CEE1B433C1FX"));
                    System.out.println("--- token : "+token);
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

    public void jsonPost(String urlJsonObj, JSONArray jsonArray) {
        RequestQueue queue = Volley.newRequestQueue(this);

        JsonObjectArrayRequest jsonObjReq = new JsonObjectArrayRequest(Request.Method.POST,
                urlJsonObj, jsonArray,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if (response == null || response.length() == 0){
                                stopLoader();
                                Toast.makeText(ChooseSeatActivity.this, "Gagal terkoneksi server", Toast.LENGTH_LONG).show();
                            }else{
                                if (response.getString("Message").equals("Success")){
                                    nexChangeSeat();
                                }else{
                                    stopLoader();
                                    btnPayment.setEnabled(true);
                                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ChooseSeatActivity.this);
                                    alertDialogBuilder.setMessage(response.getString("Message"));

                                    alertDialogBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface arg0, int arg1) {
                                            try {
                                                runLoader();
                                                requestWithSomeHttpHeaders(API.getInstance().getApiBookingSeatMap()
                                                        +"?id="+objectResponse.getJSONArray("Data").getJSONObject(0).getString("SalesOrderHeaderID"), "seatMap");
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    });

                                    AlertDialog alertDialog = alertDialogBuilder.create();
                                    alertDialog.show();
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            btnPayment.setEnabled(true);
                            stopLoader();
                        }
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(ChooseSeatActivity.this, "Gagal terkoneksi server", Toast.LENGTH_LONG).show();
                        btnPayment.setEnabled(true);
                        stopLoader();
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

    public void runLoader(){
        preloader.setVisibility(View.VISIBLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }

    public void stopLoader(){
        preloader.setVisibility(View.GONE);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        for (int i = 0; i < dotsCount; i++) {
            dots[i].setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.nonselecteditem_dot, null));
        }
        dots[position].setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.selecteditem_dot, null));
        inputWagon.setSelection(position);
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ChooseSeatActivity.this);
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
                            +"?SalesOrderHeaderID="+objectResponse.getJSONArray("Data").getJSONObject(0).getString("SalesOrderHeaderID"), "reversal");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }
}
