package com.indomaret.klikindomaret.activity;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.multidex.MultiDex;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
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
import com.indomaret.klikindomaret.adapter.ListTicketKAIAdapter;
import com.indomaret.klikindomaret.helper.Encode2;
import com.indomaret.klikindomaret.helper.Month;
import com.indomaret.klikindomaret.helper.SessionManager;
import com.indomaret.klikindomaret.views.HeightAdjustableListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ListTicketKAIActivity extends AppCompatActivity {
    public static Activity listTicketKAIActivity;
    private Toolbar toolbar;
    private Intent intent;
    private SessionManager sessionManager;
    private Button btnCari, btnResearch, btnSorting, btnFilter, btnBack, btnSatuArah, btnDuaArah, btnSubscribe;
    private ImageView btnSwitch, infoAdult, infoBaby;
    private TextView copyrightText, originalStationText, destinationStationText, infoScedule;
    private EditText jadwalBerangkatText, jadwalPulangText, emailText;
    private Spinner dewasaText, bayiText;
    private AutoCompleteTextView keretaAwalText, keretaTujuanText;
    private HeightAdjustableListView listTicketKAI;
    private LinearLayout linearSearch, linearDestination, linearFooter, linearFilter, linearNotFound;
    private ImageView imageArrow;
    private ScrollView scrollView;
    private ProgressBar progressBar;
    private String originCode, destinationStationCode, scheduleNow, from, originText, destinationText;
    private int progressStatus = 0;
    private int countPassenger = 0;
    private int keretaAwalCount = 0;
    private int keretaTujuanCount = 0;
    private boolean destinationStatus = false;
    private boolean getDestination = false;
    private ListTicketKAIAdapter listTicketKAIAdapter;
    private Handler handler = new Handler();
    private ArrayList<String> originStations, destinationStations;
    private JSONObject userData = new JSONObject();
    private JSONArray jsonObjects = new JSONArray();
    private JSONObject objectTrain = new JSONObject();
    private JSONArray jArrayData, originStationsArray, destinationStationsArray;
    private JSONArray dataReturnArray = new JSONArray();
    private JSONArray dataReturnPriceArray = new JSONArray();
    private JSONArray jsonArray = new JSONArray();
    private JSONArray dataArray = new JSONArray();
    private RelativeLayout preloader;
    private ArrayList<String> dewasaList, bayiList;
    private Encode2 encode = new Encode2();
    private final int GET_FILTERING = 77;
    private int currentPositionOriginal = 0;
    private int currentPositionDestination = 0;
    private int currentPositionDuration = 0;
    private int currentPositionPrice = 0;
    private Month dateName = new Month();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        MultiDex.install(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_ticket_kai);
        listTicketKAIActivity = this;

        toolbar = (Toolbar) findViewById(R.id.app_toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        intent = getIntent();
        sessionManager = new SessionManager(ListTicketKAIActivity.this);

        btnResearch = (Button) findViewById(R.id.btn_research);
        btnCari = (Button) findViewById(R.id.btn_cari);
        btnSorting = (Button) findViewById(R.id.btn_sorting);
        btnFilter = (Button) findViewById(R.id.btn_filter);
        btnSwitch = (ImageView) findViewById(R.id.btn_switch);
        btnBack = (Button) findViewById(R.id.btn_back);
        btnSatuArah = (Button) findViewById(R.id.satu_arah);
        btnDuaArah = (Button) findViewById(R.id.dua_arah);
        btnSubscribe = (Button) findViewById(R.id.btn_subscribe);

        copyrightText = (TextView) findViewById(R.id.copyright_text);
        originalStationText = (TextView) findViewById(R.id.original_station);
        destinationStationText = (TextView) findViewById(R.id.destination_station);
        infoScedule = (TextView) findViewById(R.id.info_scedule);

        keretaAwalText = (AutoCompleteTextView) findViewById(R.id.kereta_awal);
        keretaTujuanText = (AutoCompleteTextView) findViewById(R.id.keretea_tujuan);
        jadwalBerangkatText = (EditText) findViewById(R.id.jadwal_berangkat);
        jadwalPulangText = (EditText) findViewById(R.id.jadwal_pulang);
        emailText = (EditText) findViewById(R.id.email_text);

        dewasaText = (Spinner) findViewById(R.id.dewasa);
        bayiText = (Spinner) findViewById(R.id.bayi);

        linearSearch = (LinearLayout) findViewById(R.id.linear_search);
        linearDestination = (LinearLayout) findViewById(R.id.linear_destination);
        linearFooter = (LinearLayout) findViewById(R.id.linear_footer);
        linearFilter = (LinearLayout) findViewById(R.id.linear_filter);
        linearNotFound = (LinearLayout) findViewById(R.id.linear_not_found);

        imageArrow = (ImageView) findViewById(R.id.image_arrow);
        infoAdult = (ImageView) findViewById(R.id.info_adult);
        infoBaby = (ImageView) findViewById(R.id.info_baby);
        listTicketKAI = (HeightAdjustableListView) findViewById(R.id.list_ticket_kai);
        preloader = (RelativeLayout) findViewById(R.id.preloader);
        scrollView = (ScrollView) findViewById(R.id.scroll_view);

        runLoader();

        destinationStatus = intent.getBooleanExtra("destinationStatus", false);
        getDestination = intent.getBooleanExtra("getDestination", false);
        scheduleNow = intent.getStringExtra("scheduleNow");
        from = intent.getStringExtra("from");

        setCountPassenger();
        setData(intent.getStringExtra("userData"));

        dewasaText.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (!from.equals("home")) setCountInfant(i+1);
                from = "";
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

//        new Thread(new Runnable() {
//            public void run() {
//                while (progressStatus < 100) {
//                    progressStatus += 1;
//                    handler.post(new Runnable() {
//                        public void run() {
//                            progressBar.setProgress(progressStatus);
//                            progressbarText.setText(progressStatus + "%");
//                            progressbarText.setPadding((int) (progressStatus * 4.5),0,0,0);
//                        }
//                    });
//                    try {
//                        // Sleep for 200 milliseconds.
//                        Thread.sleep(200);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                }
//            }
//        }).start();

        infoAdult.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(ListTicketKAIActivity.this, "Tiga tahun keatas", Toast.LENGTH_SHORT).show();
            }
        });

        infoBaby.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(ListTicketKAIActivity.this, "Dibawah Tiga tahun", Toast.LENGTH_SHORT).show();
            }
        });

        btnSatuArah.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnSatuArah.setBackgroundResource(R.drawable.button_style_1);
                btnDuaArah.setBackgroundResource(R.drawable.button_style_4);

                linearDestination.setVisibility(View.INVISIBLE);
                jadwalPulangText.setText("");
            }
        });

        btnDuaArah.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnSatuArah.setBackgroundResource(R.drawable.button_style_4);
                btnDuaArah.setBackgroundResource(R.drawable.button_style_1);

                linearDestination.setVisibility(View.VISIBLE);
                jadwalPulangText.setText("");
            }
        });

        btnSubscribe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestWithSomeHttpHeaders(API.getInstance().getApiGetSubScribe()
                        + "?email=" + emailText.getText().toString(), "subscribe");
                emailText.setText("");
            }
        });

        btnSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String originStation = keretaAwalText.getText().toString();
                String destinationStation = keretaTujuanText.getText().toString();

                keretaAwalText.setText(destinationStation);
                keretaTujuanText.setText(originStation);
            }
        });

        btnSorting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ListTicketKAIActivity.this);
                alertDialogBuilder.setTitle("Urutkan");
                alertDialogBuilder.setItems(R.array.sorting_kai, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == 0) {
                            listTicketKAIAdapter = new ListTicketKAIAdapter(ListTicketKAIActivity.this, sortJsonArray(sortJsonArray(jsonObjects, "AdultPrice"), "DepartureDate"), countPassenger);
                            listTicketKAI.setAdapter(listTicketKAIAdapter);
                            linearFilter.setVisibility(View.VISIBLE);

                            if (currentPositionOriginal == 0) currentPositionOriginal = 1;
                            else currentPositionOriginal = 0;

                            listTicketKAIAdapter.notifyDataSetChanged();
                        } else if (which == 1) {
                            listTicketKAIAdapter = new ListTicketKAIAdapter(ListTicketKAIActivity.this, sortJsonArray(sortJsonArray(jsonObjects, "AdultPrice"), "ArrivalDate"), countPassenger);
                            listTicketKAI.setAdapter(listTicketKAIAdapter);
                            linearFilter.setVisibility(View.VISIBLE);

                            if (currentPositionDestination == 0) currentPositionDestination = 1;
                            else currentPositionDestination = 0;

                            listTicketKAIAdapter.notifyDataSetChanged();
                        } else if (which == 2) {
                            listTicketKAIAdapter = new ListTicketKAIAdapter(ListTicketKAIActivity.this, sortJsonArray(sortJsonArray(jsonObjects, "AdultPrice"), "Duration"), countPassenger);
                            listTicketKAI.setAdapter(listTicketKAIAdapter);
                            linearFilter.setVisibility(View.VISIBLE);

                            if (currentPositionDuration == 0) currentPositionDuration = 1;
                            else currentPositionDuration = 0;

                            listTicketKAIAdapter.notifyDataSetChanged();
                        } else if (which == 3) {
                            listTicketKAIAdapter = new ListTicketKAIAdapter(ListTicketKAIActivity.this, sortJsonArray(sortJsonArray(jsonObjects, "DepartureDate"), "AdultPrice"), countPassenger);
                            listTicketKAI.setAdapter(listTicketKAIAdapter);
                            linearFilter.setVisibility(View.VISIBLE);

                            if (currentPositionPrice == 0) currentPositionPrice = 1;
                            else currentPositionPrice = 0;

                            listTicketKAIAdapter.notifyDataSetChanged();
                        }
                    }
                });

                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
                scrollView.fullScroll(View.FOCUS_UP);
            }
        });

        btnFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(ListTicketKAIActivity.this, FilterKAIActivity.class);
                intent.putExtra("jsonObjects", jsonObjects.toString());
                intent.putExtra("userData", userData.toString());
                intent.putExtra("destinationStatus", destinationStatus);
                intent.putExtra("getDestination", false);
                intent.putExtra("scheduleNow", scheduleNow);

                startActivityForResult(intent, GET_FILTERING);
                overridePendingTransition(R.anim.left_in, R.anim.left_out);
            }
        });

        btnSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String originStation = keretaAwalText.getText().toString();
                String destinationStation = keretaTujuanText.getText().toString();

                keretaAwalText.setText(destinationStation);
                keretaTujuanText.setText(originStation);
            }
        });

        jadwalBerangkatText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater inflater = (LayoutInflater) ListTicketKAIActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View writeReview = null;

                if (writeReview == null) {
                    writeReview = inflater.inflate(R.layout.date_picker, null);
                }

                final Calendar calendar = Calendar.getInstance();
                final StringBuilder builder = new StringBuilder();
                final StringBuilder dataBuilder = new StringBuilder();
                final DatePicker datePicker = (DatePicker) writeReview.findViewById(R.id.date_picker);
                final Button done = (Button) writeReview.findViewById(R.id.send_review);

                datePicker.setMinDate(System.currentTimeMillis() - 1000);

                calendar.add(Calendar.DATE, 90);
                datePicker.setMaxDate(calendar.getTimeInMillis());

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ListTicketKAIActivity.this);
                alertDialogBuilder.setView(writeReview);
                final AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.setCanceledOnTouchOutside(false);
                alertDialog.show();

                done.setOnClickListener(
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (String.valueOf(datePicker.getDayOfMonth()).length() == 1){
                                    builder.append("0" + datePicker.getDayOfMonth() + "-");
                                    dataBuilder.append("0" + datePicker.getDayOfMonth() + "-");
                                }else{
                                    builder.append(datePicker.getDayOfMonth()+"-");
                                    dataBuilder.append(datePicker.getDayOfMonth()+"-");
                                }

                                if (String.valueOf(datePicker.getMonth() + 1).length() == 1){
                                    builder.append(dateName.getMonth2("0" + String.valueOf(datePicker.getMonth() + 1)) + "-");
                                    dataBuilder.append("0" + (datePicker.getMonth() + 1) + "-");
                                }else{
                                    builder.append(dateName.getMonth2(String.valueOf(datePicker.getMonth() + 1))+"-");
                                    dataBuilder.append((datePicker.getMonth() + 1)+"-");
                                }

                                builder.append(datePicker.getYear());
                                dataBuilder.append(datePicker.getYear());

                                jadwalBerangkatText.setText(builder.toString());
                                originText = dataBuilder.toString();

                                jadwalPulangText.setEnabled(true);
                                jadwalPulangText.setText("");
                                alertDialog.dismiss();
                            }
                        }
                );
            }
        });

        jadwalPulangText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater inflater = (LayoutInflater) ListTicketKAIActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View writeReview = null;

                if (writeReview == null) {
                    writeReview = inflater.inflate(R.layout.date_picker, null);
                }

                final Calendar calendar = Calendar.getInstance();
                final StringBuilder builder = new StringBuilder();
                final StringBuilder dataBuilder = new StringBuilder();
                final DatePicker datePicker = (DatePicker) writeReview.findViewById(R.id.date_picker);
                final Button done = (Button) writeReview.findViewById(R.id.send_review);

                Calendar cal = Calendar.getInstance();
                SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
                try {
                    cal.setTime(sdf.parse(originText));
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                datePicker.setMinDate(cal.getTimeInMillis());

                calendar.add(Calendar.DATE, 90);
                datePicker.setMaxDate(calendar.getTimeInMillis());

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ListTicketKAIActivity.this);
                alertDialogBuilder.setView(writeReview);
                final AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.setCanceledOnTouchOutside(false);
                alertDialog.show();

                done.setOnClickListener(
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (String.valueOf(datePicker.getDayOfMonth()).length() == 1){
                                    builder.append("0" + datePicker.getDayOfMonth() + "-");
                                    dataBuilder.append("0" + datePicker.getDayOfMonth() + "-");
                                }else{
                                    builder.append(datePicker.getDayOfMonth()+"-");
                                    dataBuilder.append(datePicker.getDayOfMonth()+"-");
                                }

                                if (String.valueOf(datePicker.getMonth() + 1).length() == 1){
                                    builder.append(dateName.getMonth2("0" + String.valueOf(datePicker.getMonth() + 1)) + "-");
                                    dataBuilder.append("0" + (datePicker.getMonth() + 1) + "-");
                                }else{
                                    builder.append(dateName.getMonth2(String.valueOf(datePicker.getMonth() + 1))+"-");
                                    dataBuilder.append((datePicker.getMonth() + 1)+"-");
                                }

                                builder.append(datePicker.getYear());
                                dataBuilder.append(datePicker.getYear());

                                jadwalPulangText.setText(builder.toString());
                                destinationText = dataBuilder.toString();
                                alertDialog.dismiss();
                            }
                        }
                );
            }
        });

        btnResearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (linearSearch.getVisibility() == View.GONE) {
                    linearSearch.setVisibility(View.VISIBLE);
                } else {
                    linearSearch.setVisibility(View.GONE);
                }
            }
        });

        btnCari.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (linearDestination.getVisibility() == View.INVISIBLE) {
                        destinationStatus = false;
                    } else {
                        destinationStatus = true;
                    }

                    if (keretaAwalText.getText().toString().equals("") || keretaAwalText.getText() == null) {
                        Toast.makeText(ListTicketKAIActivity.this, "Stasiun asal tidak boleh kosong", Toast.LENGTH_LONG).show();
                    } else if (keretaTujuanText.getText().toString().equals("") || keretaTujuanText.getText() == null) {
                        Toast.makeText(ListTicketKAIActivity.this, "Stasiun tujuan tidak boleh kosong", Toast.LENGTH_LONG).show();
                    } else if (originText.equals("") || originText == null) {
                        Toast.makeText(ListTicketKAIActivity.this, "Tanggal keberangkatan tidak boleh kosong", Toast.LENGTH_LONG).show();
                    } else if (destinationStatus && (destinationText.equals("") || destinationText == null)){
                        Toast.makeText(ListTicketKAIActivity.this, "Tanggal kepulangan tidak boleh kosong", Toast.LENGTH_LONG).show();
                    }else {
                        if (Integer.valueOf(bayiText.getSelectedItem().toString()) > Integer.valueOf(dewasaText.getSelectedItem().toString())) {
                            bayiText.setSelection(Integer.valueOf(dewasaText.getSelectedItem().toString()));
                        }

                        if (keretaAwalText.getText().toString().contains(",")){
                            keretaAwalCount = keretaAwalText.getText().toString().split(",")[1].length();
                            for (int i=0; i<originStationsArray.length(); i++){
                                if (originStationsArray.getJSONObject(i).getString("NamaStasiun").toLowerCase().equals(keretaAwalText.getText().toString().toLowerCase().split(",")[1].substring(1, keretaAwalCount))){
                                    originCode = originStationsArray.getJSONObject(i).getString("KodeStasiun");
                                }
                            }
                        }else{
                            Toast.makeText(ListTicketKAIActivity.this, "Stasiun asal tidak ditemukan", Toast.LENGTH_LONG).show();
                            btnCari.setEnabled(true);
                            stopLoader();
                            return;
                        }

                        if (keretaTujuanText.getText().toString().contains(",")){
                            keretaTujuanCount = keretaTujuanText.getText().toString().split(",")[1].length();
                            for (int i=0; i<destinationStationsArray.length(); i++){
                                if (destinationStationsArray.getJSONObject(i).getString("NamaStasiun").toLowerCase().equals(keretaTujuanText.getText().toString().toLowerCase().split(",")[1].substring(1, keretaTujuanCount))){
                                    destinationStationCode = destinationStationsArray.getJSONObject(i).getString("KodeStasiun");
                                }
                            }
                        }else{
                            Toast.makeText(ListTicketKAIActivity.this, "Stasiun tujuan tidak ditemukan", Toast.LENGTH_LONG).show();
                            btnCari.setEnabled(true);
                            stopLoader();
                            return;
                        }

                        if ((originCode != null && originCode.length() > 0) || (destinationStationCode != null && destinationStationCode.length() > 0)){
                            userData.put("originalStation", keretaAwalText.getText());
                            userData.put("originalCode", originCode);
                            userData.put("destinationStation", keretaTujuanText.getText());
                            userData.put("destinationCode", destinationStationCode);
                            userData.put("originalDate", originText.replaceAll("-", ""));
                            userData.put("originalDateText", originText);
                            if (destinationStatus){
                                userData.put("destinationDate", destinationText.replaceAll("-", ""));
                                userData.put("destinationDateText", destinationText);
                            }else{
                                userData.put("destinationDate", "");
                                userData.put("destinationDateText", "");
                            }
                            userData.put("countAdult", dewasaText.getSelectedItem());
                            userData.put("countBaby", bayiText.getSelectedItem());
                            userData.put("indexAdult", dewasaText.getSelectedItemId());
                            userData.put("indexBaby", bayiText.getSelectedItemId());
                            userData.put("destinationStatus", destinationStatus);

                            intent = new Intent(ListTicketKAIActivity.this, ListTicketKAIActivity.class);
                            intent.putExtra("userData", userData.toString());
                            intent.putExtra("destinationStatus", destinationStatus);
                            intent.putExtra("getDestination", false);
                            intent.putExtra("scheduleNow", scheduleNow);
                            intent.putExtra("from", "home");

                            runLoader();
                            linearSearch.setVisibility(View.GONE);

                            destinationStatus = intent.getBooleanExtra("destinationStatus", false);
                            getDestination = intent.getBooleanExtra("getDestination", false);
                            scheduleNow = intent.getStringExtra("scheduleNow");
                            from = intent.getStringExtra("from");

                            setCountPassenger();
                            setData(intent.getStringExtra("userData"));
                        }else{
                            final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ListTicketKAIActivity.this);
                            alertDialogBuilder.setMessage("Ada kesalahan saat proses data.");
                            alertDialogBuilder.setPositiveButton("Coba lagi", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface arg0, int arg1) {
                                    requestWithSomeHttpHeaders(API.getInstance().getApiAllDisplay(), "display");
                                }
                            });
                            final AlertDialog alertDialog = alertDialogBuilder.create();
                            alertDialog.setCanceledOnTouchOutside(false);
                            alertDialog.show();
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        imageArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (linearFooter.getVisibility() == View.GONE) {
                    linearFooter.setVisibility(View.VISIBLE);
                    copyrightText.setVisibility(View.GONE);
                    imageArrow.setImageResource(R.drawable.arrow_up);
                } else {
                    linearFooter.setVisibility(View.GONE);
                    copyrightText.setVisibility(View.VISIBLE);
                    imageArrow.setImageResource(R.drawable.arrow_down);
                }
            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                linearSearch.setVisibility(View.VISIBLE);
            }
        });
    }

    public void clickTicket(JSONObject objectDataTicket){
        try {
            String[] splitString = objectDataTicket.getString("ArrivalDate").split("T");
            String[] splitStringDate = splitString[0].split("-");
            String[] splitStringTime = splitString[1].split(":");
            String day = splitStringDate[2];
            String month = splitStringDate[1];
            String year = splitStringDate[0];

            String hour = splitStringTime[0];
            String minute = splitStringTime[1];
            String time = "";

            SimpleDateFormat df = new SimpleDateFormat("ddMMyyyy");
            try {
                Date destinationDate = null;
                Date originalDate = df.parse(userData.getString("originalDate"));

                if (!userData.getString("destinationDate").equals("")){
                    destinationDate = df.parse(userData.getString("destinationDate"));

                    if (destinationDate.after(originalDate)){
                        time = "0000";
                    }else{
                        time = hour+minute;
                    }
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }

            if (objectDataTicket.getInt("TotalRemainingSeats") != 0) {
                if (destinationStatus) {
                    sessionManager.setKeyTicketKai(null);
                    sessionManager.setKeyTicketKai(objectDataTicket.toString());
                    intent = new Intent(ListTicketKAIActivity.this, ListTicketKAIActivity.class);
                    intent.putExtra("userData", userData.toString());
                    intent.putExtra("destinationStatus", false);
                    intent.putExtra("getDestination", true);
                    intent.putExtra("scheduleNow", "pulang");
                    intent.putExtra("time", time);
                    intent.putExtra("from", "home");
                    startActivity(intent);
                    overridePendingTransition(R.anim.left_in, R.anim.left_out);
                } else {
                    intent = new Intent(ListTicketKAIActivity.this, PassengerActivity.class);
                    intent.putExtra("countAdult", dewasaText.getSelectedItem().toString());
                    intent.putExtra("countBaby", bayiText.getSelectedItem().toString());
                    intent.putExtra("userData", userData.toString());

                    if (scheduleNow.equals("pergi")) {
                        intent.putExtra("scheduleDestination", false);
                        intent.putExtra("objectDataTicketOriginal", objectDataTicket.toString());
                    } else {
                        intent.putExtra("scheduleDestination", true);
                        intent.putExtra("objectDataTicketDestination", objectDataTicket.toString());
                        intent.putExtra("objectDataTicketOriginal", sessionManager.getKeyTicketKai());
                    }

                    startActivity(intent);
                    overridePendingTransition(R.anim.left_in, R.anim.left_out);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void setData(String data){
        try {
            userData = new JSONObject(data);
            setUser(userData);

            if (userData.getBoolean("destinationStatus")) {
                linearDestination.setVisibility(View.VISIBLE);
                btnDuaArah.setBackgroundResource(R.drawable.button_style_1);
                btnSatuArah.setBackgroundResource(R.drawable.button_style_4);
            } else {
                linearDestination.setVisibility(View.INVISIBLE);
                btnSatuArah.setBackgroundResource(R.drawable.button_style_1);
                btnDuaArah.setBackgroundResource(R.drawable.button_style_4);
            }

            if (getDestination) {
                String time = intent.getStringExtra("time");
                requestWithSomeHttpHeaders(API.getInstance().getApiBooking()
                        + "?Origin=" + userData.getString("destinationCode")
                        + "&Destination=" + userData.getString("originalCode")
                        + "&DepartureDate=" + userData.getString("destinationDate")
                        + "&Mindate=" + userData.getString("destinationDate")+time, "booking");
            } else {
                requestWithSomeHttpHeaders(API.getInstance().getApiBooking()
                        + "?Origin=" + userData.getString("originalCode")
                        + "&Destination=" + userData.getString("destinationCode")
                        + "&DepartureDate=" + userData.getString("originalDate")
                        + "&Mindate=" + "", "booking");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void setCountPassenger(){
        dewasaList = new ArrayList<>();
        for (int i = 1; i < 5; i++) {
            dewasaList.add(String.valueOf(i));
        }

        setCountInfant(1);

        ArrayAdapter<String> dataAdapterDewasa = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, dewasaList);
        dataAdapterDewasa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dewasaText.setAdapter(dataAdapterDewasa);
    }

    public void setCountInfant(int count){
        bayiList = new ArrayList<>();
        for (int j=0; j<=count; j++){
            bayiList.add(String.valueOf(j));
        }

        ArrayAdapter<String> dataAdapterBayi = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, bayiList);
        dataAdapterBayi.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        bayiText.setAdapter(dataAdapterBayi);
    }

    public JSONArray sortJsonArray(JSONArray array, final String type) {
        List<JSONObject> jsons = new ArrayList<JSONObject>();
        for (int i = 0; i < array.length(); i++) {
            try {
                jsons.add(array.getJSONObject(i));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        Collections.sort(jsons, new Comparator<JSONObject>() {
            @Override
            public int compare(JSONObject lhs, JSONObject rhs) {
                String lid = "";
                String rid = "";
                try {
                    if(type.equals("AdultPrice")){
                        if (currentPositionPrice == 0)
                            return lhs.getInt("AdultPrice") > rhs.getInt("AdultPrice") ? 1 : (lhs.getInt("AdultPrice") < rhs.getInt("AdultPrice") ? -1 : 0);
                        else
                            return lhs.getInt("AdultPrice") < rhs.getInt("AdultPrice") ? 1 : (lhs.getInt("AdultPrice") > rhs.getInt("AdultPrice") ? -1 : 0);
                    } else if(type.equals("DepartureDate")){
                        lid = lhs.getString(type);
                        rid = rhs.getString(type);

                        if (currentPositionOriginal == 0) return lid.compareTo(rid);
                        else return rid.compareTo(lid);
                    }else if(type.equals("ArrivalDate")){
                        lid = lhs.getString(type);
                        rid = rhs.getString(type);

                        if (currentPositionDestination == 0) return lid.compareTo(rid);
                        else return rid.compareTo(lid);
                    }else if(type.equals("Duration")){
                        lid = (lhs.getString("DurationInHour") + ":" + lhs.getString("DurationInMinutes"));
                        rid = (rhs.getString("DurationInHour") + ":" + rhs.getString("DurationInMinutes"));

                        if (currentPositionDuration == 0) return lid.compareTo(rid);
                        else return rid.compareTo(lid);
                    }else if(type.equals("CountSeat")){
                        lid = (lhs.getString("TotalRemainingSeats"));
                        rid = (rhs.getString("TotalRemainingSeats"));

                        if (currentPositionDuration == 0) return rid.compareTo(lid);
                        else return lid.compareTo(rid);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                return lid.compareTo(rid);
            }
        });

        return new JSONArray(jsons);
    }

    public void setUser(JSONObject object) {
        try {
            String dates = "";
            String dayName = "";
            if (getDestination) {
                dates = object.getString("destinationDate");

                originalStationText.setText(object.getString("destinationStation").split(",")[0] + " (" + object.getString("destinationCode") + ")");
                destinationStationText.setText(object.getString("originalStation").split(",")[0] + " (" + object.getString("originalCode") + ")");

                keretaAwalText.setText(object.getString("destinationStation"));
                keretaTujuanText.setText(object.getString("originalStation"));
            } else {
                dates = object.getString("originalDate");

                originalStationText.setText(object.getString("originalStation").split(",")[0] + " (" + object.getString("originalCode") + ")");
                destinationStationText.setText(object.getString("destinationStation").split(",")[0] + " (" + object.getString("destinationCode") + ")");

                keretaAwalText.setText(object.getString("originalStation"));
                keretaTujuanText.setText(object.getString("destinationStation"));
            }

            String date = dates.substring(0, 2);
            String month = dates.substring(2, 4);
            String year = dates.substring(4, 8);

            String countAdult = object.getString("countAdult");
            String countBaby = object.getString("countBaby");
            countPassenger = Integer.valueOf(countAdult);

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

            infoScedule.setText(dayName + ", " + date + " " + dateName.getMonth2(month.toString()) + " " + year + ", " + countAdult + " Dewasa, " + countBaby + " Bayi");

            String d = object.getString("originalDateText").substring(0, 2);
            String m = object.getString("originalDateText").substring(3, 5);
            String y = object.getString("originalDateText").substring(6, 10);
            originText = object.getString("originalDateText");

            jadwalBerangkatText.setText(d + "-" + dateName.getMonth2(m) + "-" + y);

            if (userData.getBoolean("destinationStatus")){
                d = object.getString("destinationDateText").substring(0, 2);
                m = object.getString("destinationDateText").substring(3, 5);
                y = object.getString("destinationDateText").substring(6, 10);
                destinationText = object.getString("destinationDateText");

                jadwalPulangText.setText(d + "-" + dateName.getMonth2(m) + "-" + y);

                linearDestination.setVisibility(View.VISIBLE);
                btnSatuArah.setBackgroundResource(R.drawable.button_style_4);
                btnDuaArah.setBackgroundResource(R.drawable.button_style_1);
//                destinationStatus = userData.getBoolean("destinationStatus");
            }

            dewasaText.setSelection(object.getInt("indexAdult"));
            setCountInfant(Integer.valueOf(countAdult));
            bayiText.setSelection(object.getInt("indexBaby"));

            if (jadwalBerangkatText.getText().toString().equals("") || jadwalBerangkatText.getText().toString() == null){
                jadwalPulangText.setEnabled(false);
            }else{
                jadwalPulangText.setEnabled(true);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void requestWithSomeHttpHeaders(String url, final String type) {
        RequestQueue queue = Volley.newRequestQueue(this);
        System.out.println("--- url list kai : " + url);
        StringRequest postRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    if (response == null || response.length() == 0){
                        stopLoader();
                        linearNotFound.setVisibility(View.VISIBLE);
                        Toast.makeText(ListTicketKAIActivity.this, "Gagal terkoneksi server", Toast.LENGTH_LONG).show();
                    }else{
                        JSONObject jObject = new JSONObject(response);
                        if (jObject.getBoolean("IsSuccess")) {
                            if (type.equals("booking")) {
                                getBooking(jObject);
                            } else if (type.equals("display")) {
                                setListKereta(jObject);
                            } else if (type.equals("subscribe")) {
                                Toast.makeText(ListTicketKAIActivity.this, jObject.getString("Message"), Toast.LENGTH_LONG).show();
                            }
                        } else {
                            linearNotFound.setVisibility(View.VISIBLE);
                            stopLoader();;
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    linearNotFound.setVisibility(View.VISIBLE);
                    stopLoader();;
                }
            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(ListTicketKAIActivity.this, "Gagal terkoneksi server", Toast.LENGTH_LONG).show();
                        linearNotFound.setVisibility(View.VISIBLE);
                        stopLoader();;
                    }
                }
        ) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                String token = "";
                try {
                    token = encode.SHA1(encode.md5("66E2C13840534C139D85CEE1B433C1FX"));
                    System.out.print("--- token : " + token);
                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }

                params.put("ApplicationKey", "indomaret");
                params.put("Authorization", "bearer " + token);

                return params;
            }
        };

        postRequest.setRetryPolicy(new DefaultRetryPolicy(50000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(postRequest);
    }

    public void getBooking(JSONObject object) {
        try {
            if (object.getJSONObject("Data").getString("Details") == null || object.getJSONObject("Data").getJSONArray("Details").length() == 0) {
                linearNotFound.setVisibility(View.VISIBLE);
                scrollView.setPadding(0, 0, 0, 0);
                stopLoader();;
            } else {
                scrollView.setPadding(0, 0, 0, 110);
                linearNotFound.setVisibility(View.GONE);
                jArrayData = object.getJSONObject("Data").getJSONArray("Details");
                JSONArray jsonObjectZero = new JSONArray();
                jsonObjects = new JSONArray();

                for (int i = 0; i < jArrayData.length(); i++) {
                    JSONArray jsonArray = jArrayData.getJSONObject(i).getJSONObject("TrainFare").getJSONArray("Details");
                    for (int j = 0; j < jsonArray.length(); j++) {
                        objectTrain.put("TrainNumber", jArrayData.getJSONObject(i).getString("TrainNumber"));
                        objectTrain.put("TrainName", jArrayData.getJSONObject(i).getString("TrainName"));
                        objectTrain.put("DepartureDate", jArrayData.getJSONObject(i).getString("DepartureDate"));
                        objectTrain.put("ArrivalDate", jArrayData.getJSONObject(i).getString("ArrivalDate"));
                        objectTrain.put("DurationInHour", jArrayData.getJSONObject(i).getString("DurationInHour"));
                        objectTrain.put("DurationInMinutes", jArrayData.getJSONObject(i).getString("DurationInMinutes"));
                        objectTrain.put("Original", jArrayData.getJSONObject(i).getJSONObject("TrainFare").getString("Original"));
                        objectTrain.put("Destination", jArrayData.getJSONObject(i).getJSONObject("TrainFare").getString("Destination"));
                        objectTrain.put("DepartureDateTrainFare", jArrayData.getJSONObject(i).getJSONObject("TrainFare").getString("DepartureDate"));
                        objectTrain.put("TrainClassCode", jsonArray.getJSONObject(j).getString("TrainClassCode"));
                        objectTrain.put("TrainSubClassCode", jsonArray.getJSONObject(j).getString("TrainSubClassCode"));
                        objectTrain.put("AdultPrice", jsonArray.getJSONObject(j).getString("AdultPrice"));
                        objectTrain.put("ChildPrice", jsonArray.getJSONObject(j).getString("ChildPrice"));
                        objectTrain.put("InfantPrice", jsonArray.getJSONObject(j).getString("InfantPrice"));
                        objectTrain.put("TotalRemainingSeats", jsonArray.getJSONObject(j).getString("TotalRemainingSeats"));
                        objectTrain.put("TrainClassName", jsonArray.getJSONObject(j).getString("TrainClassName"));

                        jsonObjects.put(objectTrain);
                        objectTrain = new JSONObject();
                    }
                }

                if (from.equals("filter")) {
                    jsonArray = new JSONArray();
                    dataArray = new JSONArray();
                    dataReturnArray = new JSONArray(intent.getStringExtra("dataReturnArray"));

                    try {
                        int index = 0;
                        for (int i = 0; i < jsonObjects.length(); i++) {
                            for (int j = 0; j < dataReturnArray.length(); j++) {
                                if (jsonObjects.getJSONObject(i).toString().contains(dataReturnArray.getString(j))) {
                                    jsonArray.put(jsonObjects.getJSONObject(i));
                                }
                            }
                        }

                        if (intent.getStringExtra("dataReturnPriceArray") != null && dataReturnArray.length() > 0) {
                            dataReturnPriceArray = new JSONArray(intent.getStringExtra("dataReturnPriceArray"));

                            for (int i = 0; i < jsonArray.length(); i++) {
                                for (int j = 0; j < dataReturnPriceArray.length(); j++) {
                                    if (Double.valueOf(jsonArray.getJSONObject(i).getString("AdultPrice")) < Double.valueOf(dataReturnPriceArray.getJSONObject(j).getString("maxPrice"))
                                            && Double.valueOf(jsonArray.getJSONObject(i).getString("AdultPrice")) > Double.valueOf(dataReturnPriceArray.getJSONObject(j).getString("minPrice"))) {
                                        dataArray.put(jsonArray.getJSONObject(i));
                                    }
                                }
                            }
                        } else if (intent.getStringExtra("dataReturnPriceArray") != null) {
                            dataReturnPriceArray = new JSONArray(intent.getStringExtra("dataReturnPriceArray"));
                            for (int i = 0; i < jsonObjects.length(); i++) {
                                for (int j = 0; j < dataReturnPriceArray.length(); j++) {
                                    if (Double.valueOf(jsonObjects.getJSONObject(i).getString("AdultPrice")) < Double.valueOf(dataReturnPriceArray.getJSONObject(j).getString("maxPrice"))
                                            && Double.valueOf(jsonObjects.getJSONObject(i).getString("AdultPrice")) > Double.valueOf(dataReturnPriceArray.getJSONObject(j).getString("minPrice"))) {
                                        dataArray.put(jsonObjects.getJSONObject(i));
                                    }
                                }
                            }
                        }

                        if (dataArray.length() > 0) {
                            listTicketKAIAdapter = new ListTicketKAIAdapter(ListTicketKAIActivity.this, dataArray, countPassenger);
                        } else {
                            listTicketKAIAdapter = new ListTicketKAIAdapter(ListTicketKAIActivity.this, jsonArray, countPassenger);
                        }

                        listTicketKAI.setAdapter(listTicketKAIAdapter);
                        linearFilter.setVisibility(View.VISIBLE);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    listTicketKAIAdapter = new ListTicketKAIAdapter(ListTicketKAIActivity.this, sortJsonArray(sortJsonArray(jsonObjects, "AdultPrice"), "DepartureDate"), countPassenger);
                    listTicketKAI.setAdapter(listTicketKAIAdapter);
                    linearFilter.setVisibility(View.VISIBLE);
                }

                scrollView.fullScroll(View.FOCUS_UP);
                stopLoader();;
            }
        } catch (JSONException e) {
            e.printStackTrace();
            stopLoader();;
        }
    }

    public void setListKereta(JSONObject object) {
        try {
            jArrayData = object.getJSONArray("Data");
            originStationsArray = jArrayData.getJSONObject(0).getJSONArray("OriginStations");
            destinationStationsArray = jArrayData.getJSONObject(0).getJSONArray("DestinationStations");

            originStations = new ArrayList<>();
            for (int i = 0; i < originStationsArray.length(); i++) {
                if (originStationsArray.getJSONObject(i).getString("City").equals("null") ||
                        originStationsArray.getJSONObject(i).getString("City") == null) {
                    originStations.add(originStationsArray.getJSONObject(i).getString("NamaStasiun"));
                } else {
                    originStations.add(originStationsArray.getJSONObject(i).getString("City").toUpperCase() + ", "
                            + originStationsArray.getJSONObject(i).getString("NamaStasiun"));
                }
            }

            destinationStations = new ArrayList<>();
            for (int i = 0; i < destinationStationsArray.length(); i++) {
                if (destinationStationsArray.getJSONObject(i).getString("City").equals("null") ||
                        destinationStationsArray.getJSONObject(i).getString("City") == null) {
                    destinationStations.add(destinationStationsArray.getJSONObject(i).getString("NamaStasiun"));
                } else {
                    destinationStations.add(destinationStationsArray.getJSONObject(i).getString("City").toUpperCase() + ", "
                            + destinationStationsArray.getJSONObject(i).getString("NamaStasiun"));
                }
            }

            if (originStations.size() > 0) {
                ArrayAdapter<String> adapterOriginStasiun = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, originStations);
                keretaAwalText.setThreshold(1);
                keretaAwalText.setAdapter(adapterOriginStasiun);
            }

            if (destinationStations.size() > 0) {
                ArrayAdapter<String> adapterDestinationStasiun = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, destinationStations);
                keretaTujuanText.setThreshold(1);
                keretaTujuanText.setAdapter(adapterDestinationStasiun);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.right_in, R.anim.right_out);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case GET_FILTERING:
                    if (data != null) {
                        boolean isShowAll = data.getBooleanExtra("showAll", true);

                        if (isShowAll) {
                            //showAll
                            listTicketKAIAdapter = new ListTicketKAIAdapter(ListTicketKAIActivity.this, jsonObjects, countPassenger);
                            listTicketKAI.setAdapter(listTicketKAIAdapter);
                            linearFilter.setVisibility(View.VISIBLE);

                            scrollView.fullScroll(View.FOCUS_UP);
                        } else {
                            //filter
                            JSONArray filterByTime = new JSONArray();
                            JSONArray filterByClass = new JSONArray();
                            JSONArray filterByTrain = new JSONArray();
                            JSONArray filterByPrice = new JSONArray();
                            JSONArray resultFiltering = new JSONArray();

                            try {
                                filterByTime = new JSONArray(data.getStringExtra("filterByTime"));
                            } catch (Exception e) {

                            }

                            try {
                                filterByClass = new JSONArray(data.getStringExtra("filterByClass"));
                            } catch (Exception e) {

                            }

                            try {
                                filterByTrain = new JSONArray(data.getStringExtra("filterByTrain"));
                            } catch (Exception e) {

                            }

                            try {
                                filterByPrice = new JSONArray(data.getStringExtra("filterByPrice"));
                            } catch (Exception e) {

                            }

                            if (jsonObjects != null) {
                                if (jsonObjects.length() > 0) {

                                    for (int i = 0; i < jsonObjects.length(); i++) {

                                        boolean isValid = true;

                                        //filterByTime
                                        if (filterByTime != null && filterByTime.length() > 0) {
                                            try {
                                                Date dateTrain = convertStringToDate(jsonObjects.getJSONObject(i).
                                                        getString("DepartureDate"));
                                                if (dateTrain != null) {
                                                    boolean isValidFilterByTime = false;
                                                    for (int posTime = 0; posTime < filterByTime.length(); posTime++) {

                                                        String type = filterByTime.get(posTime).toString();
                                                        Date startDate = getStartEndDate(dateTrain, type, "start");
                                                        Date endDate = getStartEndDate(dateTrain, type, "end");

                                                        if (startDate != null && endDate != null) {
                                                            if (!dateTrain.before(startDate) && !dateTrain.after(endDate)) {
                                                                isValidFilterByTime = true;
                                                            }
                                                        }
                                                    }

                                                    if(!isValidFilterByTime){
                                                        isValid = false;
                                                    }
                                                }
                                            } catch (Exception e) {

                                            }
                                        }

                                        if(isValid){
                                            //filteringByClass
                                            if (filterByClass != null && filterByClass.length() > 0) {
                                                boolean isValidFilterClass = false;
                                                for (int posClass = 0; posClass < filterByClass.length(); posClass++) {
                                                    try {
                                                        if (filterByClass.get(posClass).toString().equals(jsonObjects.getJSONObject(i).
                                                                getString("TrainClassName"))) {
                                                            isValidFilterClass = true;
                                                        }

                                                    } catch (Exception e) {

                                                    }
                                                }

                                                if(!isValidFilterClass){
                                                    isValid = false;
                                                }

                                            }

                                            if(isValid){
                                                //filteringByTrain
                                                if (filterByTrain != null && filterByTrain.length() > 0) {
                                                    boolean isValidByTrain = false;
                                                    for (int posTrain = 0; posTrain < filterByTrain.length(); posTrain++) {
                                                        try {
                                                            if (filterByTrain.get(posTrain).toString().equals(jsonObjects.getJSONObject(i).
                                                                    getString("TrainName"))) {
                                                                isValidByTrain = true;
                                                            }

                                                        } catch (Exception e) {

                                                        }
                                                    }

                                                    if(!isValidByTrain){
                                                        isValid = false;
                                                    }
                                                }

                                                if(isValid){
                                                    //filteringByPrice
                                                    if (filterByPrice != null && filterByPrice.length() > 0) {
                                                        boolean isValidPrice = false;
                                                        for (int posPrice = 0; posPrice < filterByPrice.length(); posPrice++) {
                                                            try {
                                                                double trainPrice = jsonObjects.getJSONObject(i).getDouble("AdultPrice");
                                                                if (trainPrice >= filterByPrice.getJSONObject(posPrice).getInt("minPrice") &&
                                                                        trainPrice <= filterByPrice.getJSONObject(posPrice).getInt("maxPrice")) {
                                                                    isValidPrice = true;
                                                                }

                                                            } catch (Exception e) {

                                                            }
                                                        }

                                                        if(!isValidPrice){
                                                            isValid = false;
                                                        }
                                                    }

                                                    if(isValid){
                                                        try{
                                                            resultFiltering.put(jsonObjects.getJSONObject(i));
                                                        }catch (Exception e){

                                                        }
                                                    }

                                                }


                                            }

                                        }

                                    }

                                    listTicketKAIAdapter = new ListTicketKAIAdapter(ListTicketKAIActivity.this, resultFiltering, countPassenger);
                                    listTicketKAI.setAdapter(listTicketKAIAdapter);
                                    linearFilter.setVisibility(View.VISIBLE);

                                    scrollView.fullScroll(View.FOCUS_UP);
                                }
                            }

                        }
                    }
                    break;
            }
        }
    }

    private Date convertStringToDate(final String date) {
        Date result = null;

        if (!TextUtils.isEmpty(date)) {
            try {
                String replaceT = date.replace("T", " ");
                result = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",
                        Locale.ENGLISH).parse(replaceT);

            } catch (Exception e) {
                result = null;
            }
        }

        return result;
    }

    private Date getStartEndDate(Date trainDate, String type, String startEnd){
        Date date = null;

        if(trainDate != null){
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(trainDate);

            switch (type){
                case "1":
                    if(startEnd.equalsIgnoreCase("start")){
                        calendar.set(Calendar.HOUR_OF_DAY, 0);
                        calendar.set(Calendar.MINUTE, 0);
                    }else {
                        calendar.set(Calendar.HOUR_OF_DAY, 5);
                        calendar.set(Calendar.MINUTE, 59);
                    }
                    break;
                case "2":
                    if(startEnd.equalsIgnoreCase("start")){
                        calendar.set(Calendar.HOUR_OF_DAY, 6);
                        calendar.set(Calendar.MINUTE, 0);
                    }else {
                        calendar.set(Calendar.HOUR_OF_DAY, 5);
                        calendar.set(Calendar.MINUTE, 59);
                    }
                    break;
                case "3":
                    if(startEnd.equalsIgnoreCase("start")){
                        calendar.set(Calendar.HOUR_OF_DAY, 12);
                        calendar.set(Calendar.MINUTE, 0);
                    }else {
                        calendar.set(Calendar.HOUR_OF_DAY, 17);
                        calendar.set(Calendar.MINUTE, 59);
                    }
                    break;
                case "4":
                    if(startEnd.equalsIgnoreCase("start")){
                        calendar.set(Calendar.HOUR_OF_DAY, 18);
                        calendar.set(Calendar.MINUTE, 0);
                    }else {
                        calendar.set(Calendar.HOUR_OF_DAY, 23);
                        calendar.set(Calendar.MINUTE, 59);
                    }
                    break;
            }

            date = calendar.getTime();

        }

        return date;
    }

    @Override
    protected void onResume() {
        super.onResume();
        scrollView.fullScroll(View.FOCUS_UP);

        requestWithSomeHttpHeaders(API.getInstance().getApiAllDisplay(), "display");
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
