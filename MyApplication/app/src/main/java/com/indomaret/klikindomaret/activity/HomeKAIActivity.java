package com.indomaret.klikindomaret.activity;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.multidex.MultiDex;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
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
import com.indomaret.klikindomaret.MainActivity;
import com.indomaret.klikindomaret.R;
import com.indomaret.klikindomaret.adapter.ListMenuAdapter;
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
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HomeKAIActivity extends AppCompatActivity implements ViewPager.OnPageChangeListener {
    private Toolbar toolbar;
    private DrawerLayout drawer;
    private SessionManager sessionManager;
    private Intent  intent;
    private ScrollView scrollView;
    private Button btnSatuArah, btnDuaArah, btnCari;
    private ImageView btnSwitch, infoAdult, infoBaby;
    private EditText jadwalBerangkatText, jadwalPulangText;
    private AutoCompleteTextView keretaAwalText, keretaTujuanText;
    private Spinner dewasaText, bayiText;
    private ArrayList<String> originStations, destinationStations;
    List<String> listMenu = new ArrayList<>();
    private String originCode, destinationStationCode, originText, destinationText;
    private JSONArray jArrayData, originStationsArray, destinationStationsArray;
    boolean destinationStatus;
    private ArrayList<String> dewasaList, bayiList;
    private LinearLayout linearDestination;
    private RelativeLayout preloder;
    private HeightAdjustableListView mListView;
    private ListMenuAdapter listMenuAdapter;
    private int keretaAwalCount = 0;
    private int keretaTujuanCount = 0;
    private Encode2 encode = new Encode2();
    private Month dateName = new Month();
    public static Activity homeKAIActivity = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        MultiDex.install(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kai);
        homeKAIActivity = this;

        toolbar = (Toolbar) findViewById(R.id.app_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        intent = getIntent();
        sessionManager = new SessionManager(HomeKAIActivity.this);

        btnSatuArah = (Button) findViewById(R.id.satu_arah);
        btnDuaArah = (Button) findViewById(R.id.dua_arah);
        btnCari = (Button) findViewById(R.id.btn_cari);
        btnSwitch = (ImageView) findViewById(R.id.btn_switch);
        infoAdult = (ImageView) findViewById(R.id.info_adult);
        infoBaby = (ImageView) findViewById(R.id.info_baby);

        keretaAwalText = (AutoCompleteTextView) findViewById(R.id.kereta_awal);
        keretaTujuanText = (AutoCompleteTextView) findViewById(R.id.keretea_tujuan);
        dewasaText = (Spinner) findViewById(R.id.dewasa);
        bayiText = (Spinner) findViewById(R.id.bayi);

        jadwalBerangkatText = (EditText) findViewById(R.id.jadwal_berangkat);
        jadwalPulangText = (EditText) findViewById(R.id.jadwal_pulang);

        linearDestination = (LinearLayout) findViewById(R.id.linear_destination);
        preloder = (RelativeLayout) findViewById(R.id.preloader);
        mListView = (HeightAdjustableListView) findViewById(R.id.listView);
        scrollView = (ScrollView)findViewById(R.id.scroll_view);
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        runLoader();
        btnCari.setEnabled(false);
        btnCari.setBackgroundResource(R.drawable.button_style_4);
        requestWithSomeHttpHeaders(API.getInstance().getApiAllDisplay());

        linearDestination.setVisibility(View.INVISIBLE);

        dewasaList = new ArrayList<>();
        for (int i=1; i<5; i++){
            dewasaList.add(String.valueOf(i));
        }

        setCountInfant(1);

        ArrayAdapter<String> dataAdapterDewasa = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, dewasaList);
        dataAdapterDewasa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dewasaText.setAdapter(dataAdapterDewasa);

        try {
            if (intent.getStringExtra("from").equals("backHome")){
                JSONObject dataTrain = new JSONObject(intent.getStringExtra("dataTrain"));
                setData(dataTrain);
            }else if (intent.getStringExtra("from").equals("klikindomaret")){
                if (sessionManager.getKeyUserDataKai() != null){
                    if (sessionManager.isLoggedIn() && sessionManager.getKeyUserDataKai().length() > 0){
                        JSONObject dataTrain = new JSONObject(sessionManager.getKeyUserDataKai());
                        setData(dataTrain);
                    }else{
                        setDataDefault();
                    }
                }else{
                    setDataDefault();
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this,
                drawer,
                toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close
        ) {
            @Override
            public void onDrawerOpened(View drawerView) {
            }

            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                super.onDrawerSlide(drawerView, slideOffset);
            }
        };

        drawer.setDrawerListener(toggle);
        toggle.syncState();

        View header_view = mListView.findViewById(R.id.header_menu);

        if (header_view == null) {
            header_view = getLayoutInflater().inflate(R.layout.header_layout_kai, null, false);
            mListView.addHeaderView(header_view);
        }

        TextView btnSignin = (TextView) header_view.findViewById(R.id.text_sign_in);

        if (sessionManager.isLoggedIn()){
            StringBuilder username = new StringBuilder();
            String[] name = sessionManager.getUsername().split(" ");

            for (int i=0; i<name.length; i++){
                if (i == 0 ){
                    username.append(name[i].substring(0,1).toUpperCase() + name[i].substring(1));
                }else{
                    username.append(" " + name[i].substring(0,1).toUpperCase() + name[i].substring(1));
                }
            }

            btnSignin.setText(username);
            btnSignin.setEnabled(false);
        }else{
            btnSignin.setText("Masuk");
        }

        btnSignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(HomeKAIActivity.this, LoginActivity.class);
                intent.putExtra("from", "kai");
                startActivity(intent);
            }
        });

//        View footer_view = mListView.findViewById(R.id.footer_menu);
//
//        if (footer_view == null) {
//            footer_view = getLayoutInflater().inflate(R.layout.footer_layout_kai, null);
//            mListView.addFooterView(footer_view);
//        }

        TextView btnback = (TextView) findViewById(R.id.back_home);
        btnback.setText("< Kembali ke Klikindomaret.com");

        btnback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(HomeKAIActivity.this, MainActivity.class);
                finish();
                startActivity(intent);
            }
        });

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 1:
                        intent = new Intent(HomeKAIActivity.this, CekTicketActivity.class);
                        startActivity(intent);
                        overridePendingTransition(R.anim.left_in, R.anim.left_out);
                        break;
                    case 2:
                        if (sessionManager.isLoggedIn()){
                            intent = new Intent(HomeKAIActivity.this, MyTicketListActivity.class);
                            intent.putExtra("isPassed", "true");
                            intent.putExtra("from", "main");
                            startActivity(intent);
                            overridePendingTransition(R.anim.left_in, R.anim.left_out);
                        }else{
                            Intent intent = new Intent(Intent.ACTION_DIAL);
                            intent.setData(Uri.parse(API.getInstance().getLayananPelangganUrl()));
                            startActivity(intent);
                        }
                        break;
                    case 3:
                        intent = new Intent(HomeKAIActivity.this, ProfileKAIActivity.class);
                        startActivity(intent);
                        overridePendingTransition(R.anim.left_in, R.anim.left_out);
                        break;
                    case 4:
                        intent = new Intent(HomeKAIActivity.this, MyTicketListActivity.class);
                        intent.putExtra("isPassed", "false");
                        intent.putExtra("from", "main");
                        startActivity(intent);
                        overridePendingTransition(R.anim.left_in, R.anim.left_out);
                        break;
                    case 5:
                        Intent intent = new Intent(Intent.ACTION_DIAL);
                        intent.setData(Uri.parse(API.getInstance().getLayananPelangganUrl()));
                        startActivity(intent);
                        break;
                }
                drawer.closeDrawer(GravityCompat.START);
            }
        });

        dewasaText.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                setCountInfant(i+1);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
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

        infoAdult.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(HomeKAIActivity.this, "3 tahun keatas", Toast.LENGTH_SHORT).show();
            }
        });

        infoBaby.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(HomeKAIActivity.this, "Dibawah 3 tahun", Toast.LENGTH_SHORT).show();
            }
        });

        btnSatuArah.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnSatuArah.setBackgroundResource(R.drawable.button_style_1);
                btnDuaArah.setBackgroundResource(R.drawable.button_style_4);

                linearDestination.setVisibility(View.INVISIBLE);
                jadwalPulangText.setText("");
                destinationStatus = false;
            }
        });

        btnDuaArah.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnSatuArah.setBackgroundResource(R.drawable.button_style_4);
                btnDuaArah.setBackgroundResource(R.drawable.button_style_1);

                linearDestination.setVisibility(View.VISIBLE);
                jadwalPulangText.setText("");
                destinationStatus = true;
            }
        });

        jadwalBerangkatText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater inflater = (LayoutInflater) HomeKAIActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(HomeKAIActivity.this);
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
                LayoutInflater inflater = (LayoutInflater) HomeKAIActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View writeReview = null;

                if (writeReview == null) {
                    writeReview = inflater.inflate(R.layout.date_picker, null);
                }

                final Calendar calendar = Calendar.getInstance();
                Integer year, month, day;
                final StringBuilder builder = new StringBuilder();
                final StringBuilder dataBuilder = new StringBuilder();
                final DatePicker datePicker = (DatePicker) writeReview.findViewById(R.id.date_picker);
                final Button done = (Button) writeReview.findViewById(R.id.send_review);

                if (originText.equals("") || originText == null){
                    jadwalPulangText.setEnabled(false);
                }else{
                    jadwalPulangText.setEnabled(true);
                    Calendar cal = Calendar.getInstance();
                    SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
                    try {
                        cal.setTime(sdf.parse(originText));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    datePicker.setMinDate(cal.getTimeInMillis());
                }

                calendar.add(Calendar.DATE, 90);
                datePicker.setMaxDate(calendar.getTimeInMillis());

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(HomeKAIActivity.this);
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

        btnCari.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                runLoader();
                JSONObject userData = new JSONObject();
                try {
                    if (keretaAwalText.getText().toString().equals("") || keretaAwalText.getText() == null){
                        Toast.makeText(HomeKAIActivity.this, "Stasiun asal tidak boleh kosong", Toast.LENGTH_LONG).show();
                    }else if (keretaTujuanText.getText().toString().equals("") || keretaTujuanText.getText() == null){
                        Toast.makeText(HomeKAIActivity.this, "Stasiun tujuan tidak boleh kosong", Toast.LENGTH_LONG).show();
                    }else if (originText.equals("") || originText == null){
                        Toast.makeText(HomeKAIActivity.this, "Tanggal keberangkatan tidak boleh kosong", Toast.LENGTH_LONG).show();
                    }else if (destinationStatus && (destinationText.equals("") || destinationText == null)){
                        Toast.makeText(HomeKAIActivity.this, "Tanggal kepulangan tidak boleh kosong", Toast.LENGTH_LONG).show();
                    } else if (dewasaText.getSelectedItem().toString().equals("0") || dewasaText.getSelectedItem() == null){
                        Toast.makeText(HomeKAIActivity.this, "Kuota penumpang dewasa tidak boleh kosong", Toast.LENGTH_LONG).show();
                    }else{
                        btnCari.setEnabled(false);
                        if (Integer.valueOf(bayiText.getSelectedItem().toString()) > Integer.valueOf(dewasaText.getSelectedItem().toString())){
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
                            Toast.makeText(HomeKAIActivity.this, "Stasiun asal tidak ditemukan", Toast.LENGTH_LONG).show();
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
                            Toast.makeText(HomeKAIActivity.this, "Stasiun tujuan tidak ditemukan", Toast.LENGTH_LONG).show();
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
                            userData.put("countAdult", dewasaText.getSelectedItem().toString());
                            userData.put("countBaby", bayiText.getSelectedItem().toString());
                            userData.put("indexAdult", dewasaText.getSelectedItemId());
                            userData.put("indexBaby", bayiText.getSelectedItemId());
                            userData.put("destinationStatus", destinationStatus);

                            sessionManager.setKeyUserDataKai(userData.toString());

                            intent = new Intent(HomeKAIActivity.this, ListTicketKAIActivity.class);
                            intent.putExtra("userData", userData.toString());
                            intent.putExtra("destinationStatus", destinationStatus);
                            intent.putExtra("getDestination", false);
                            intent.putExtra("scheduleNow", "pergi");
                            intent.putExtra("from", "home");

                            stopLoader();
                            btnCari.setEnabled(true);
                            startActivity(intent);
                            overridePendingTransition(R.anim.left_in, R.anim.left_out);
                        }else{
                            final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(HomeKAIActivity.this);
                            alertDialogBuilder.setMessage("Ada kesalahan saat proses data.");
                            alertDialogBuilder.setPositiveButton("Coba lagi", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface arg0, int arg1) {
                                    requestWithSomeHttpHeaders(API.getInstance().getApiAllDisplay());
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
    }

    public void setDataDefault(){
        SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");
        String currentDate = df.format(new Date());

        String date = currentDate.substring(0, 2);
        String month = currentDate.substring(3, 5);
        String year = currentDate.substring(6, 10);
        originText = currentDate;

        keretaAwalText.setText("JAKARTA, GAMBIR");
        keretaTujuanText.setText("BANDUNG, BANDUNG");
        jadwalBerangkatText.setText(date + "-" + dateName.getMonth2(month) + "-" + year);
        jadwalPulangText.setText("");
        dewasaText.setSelection(0);
        setCountInfant(1);
        bayiText.setSelection(0);

        linearDestination.setVisibility(View.INVISIBLE);
        btnSatuArah.setBackgroundResource(R.drawable.button_style_1);
        btnDuaArah.setBackgroundResource(R.drawable.button_style_4);
    }

    public void setData(JSONObject userData){
        try {
            SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");
            String StringDate = df.format(new Date());
            Date date = new Date();
            Date currentDate = new Date();

            try {
                date = df.parse(userData.getString("originalDateText"));
                currentDate = df.parse(StringDate);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            keretaAwalText.setText(userData.getString("originalStation"));
            keretaTujuanText.setText(userData.getString("destinationStation"));
            dewasaText.setSelection(userData.getInt("indexAdult"));
            setCountInfant(Integer.valueOf(userData.getString("countAdult")));
            bayiText.setSelection(userData.getInt("indexBaby"));

            if (userData.getBoolean("destinationStatus")) {
                linearDestination.setVisibility(View.VISIBLE);
                btnDuaArah.setBackgroundResource(R.drawable.button_style_1);
                btnSatuArah.setBackgroundResource(R.drawable.button_style_4);
            } else {
                linearDestination.setVisibility(View.INVISIBLE);
                btnSatuArah.setBackgroundResource(R.drawable.button_style_1);
                btnDuaArah.setBackgroundResource(R.drawable.button_style_4);
            }

            if (userData.getString("originalDateText").equals("") || userData.getString("originalDateText") == null){
                jadwalPulangText.setEnabled(false);
            }else{
                jadwalPulangText.setEnabled(true);
            }

            if (date.after(currentDate)){
                String dates = userData.getString("originalDateText").substring(0, 2);
                String month = userData.getString("originalDateText").substring(3, 5);
                String year = userData.getString("originalDateText").substring(6, 10);
                originText = userData.getString("originalDateText");

                jadwalBerangkatText.setText(dates + "-" + dateName.getMonth2(month) + "-" + year);

                if (userData.getBoolean("destinationStatus")){
                    dates = userData.getString("destinationDateText").substring(0, 2);
                    month = userData.getString("destinationDateText").substring(3, 5);
                    year = userData.getString("destinationDateText").substring(6, 10);
                    destinationText = userData.getString("destinationDateText");

                    jadwalPulangText.setText(dates + "-" + dateName.getMonth2(month) + "-" + year);

                    linearDestination.setVisibility(View.VISIBLE);
                    btnSatuArah.setBackgroundResource(R.drawable.button_style_4);
                    btnDuaArah.setBackgroundResource(R.drawable.button_style_1);
                    destinationStatus = userData.getBoolean("destinationStatus");
                }else{
                    linearDestination.setVisibility(View.INVISIBLE);
                    btnSatuArah.setBackgroundResource(R.drawable.button_style_1);
                    btnDuaArah.setBackgroundResource(R.drawable.button_style_4);
                    destinationStatus = userData.getBoolean("destinationStatus");
                }
            }else{
                String dates = StringDate.substring(0, 2);
                String month = StringDate.substring(3, 5);
                String year = StringDate.substring(6, 10);
                originText = StringDate;

                jadwalBerangkatText.setText(dates + "-" + dateName.getMonth2(month) + "-" + year);
                linearDestination.setVisibility(View.INVISIBLE);
                btnSatuArah.setBackgroundResource(R.drawable.button_style_1);
                btnDuaArah.setBackgroundResource(R.drawable.button_style_4);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
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

    public void requestWithSomeHttpHeaders(String url) {
        System.out.println("--- url display : "+url);
        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest postRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {
                        try {
                            if (response.length() > 0){
                                JSONObject jObject = new JSONObject(response);
                                if (jObject.getBoolean("IsSuccess")) {
                                    setListKereta(jObject);
                                }
                            }else{
                                final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(HomeKAIActivity.this);
                                alertDialogBuilder.setMessage("Gagal terkoneksi server.");
                                alertDialogBuilder.setPositiveButton("Coba lagi", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface arg0, int arg1) {
                                        intent = new Intent(HomeKAIActivity.this, HomeKAIActivity.class);
                                        intent.putExtra("from", "klikindomaret");
                                        finish();
                                        startActivity(intent);
                                    }
                                });

                                final AlertDialog alertDialog = alertDialogBuilder.create();
                                alertDialog.setCanceledOnTouchOutside(false);
                                alertDialog.show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();

                            final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(HomeKAIActivity.this);
                            alertDialogBuilder.setMessage("Gagal terkoneksi server.");
                            alertDialogBuilder.setPositiveButton("Coba lagi", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface arg0, int arg1) {
                                    intent = new Intent(HomeKAIActivity.this, HomeKAIActivity.class);
                                    intent.putExtra("from", "klikindomaret");
                                    finish();
                                    startActivity(intent);
                                }
                            });
                            final AlertDialog alertDialog = alertDialogBuilder.create();
                            alertDialog.setCanceledOnTouchOutside(false);
                            alertDialog.show();
                        }
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(HomeKAIActivity.this);
                        alertDialogBuilder.setMessage("Gagal terkoneksi server.");
                        alertDialogBuilder.setPositiveButton("Coba lagi", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface arg0, int arg1) {
                                intent = new Intent(HomeKAIActivity.this, HomeKAIActivity.class);
                                intent.putExtra("from", "klikindomaret");
                                finish();
                                startActivity(intent);
                            }
                        });
                        final AlertDialog alertDialog = alertDialogBuilder.create();
                        alertDialog.setCanceledOnTouchOutside(false);
                        alertDialog.show();
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

    public void setListKereta(JSONObject object){
        try {
            jArrayData = object.getJSONArray("Data");
            originStationsArray = jArrayData.getJSONObject(0).getJSONArray("OriginStations");
            destinationStationsArray = jArrayData.getJSONObject(0).getJSONArray("DestinationStations");

            originStations = new ArrayList<>();
            for (int i=0; i<originStationsArray.length(); i++){
                if (originStationsArray.getJSONObject(i).getString("City").equals("null") ||
                        originStationsArray.getJSONObject(i).getString("City") == null){
                    originStations.add(originStationsArray.getJSONObject(i).getString("NamaStasiun"));
                }else{
                    originStations.add(originStationsArray.getJSONObject(i).getString("City").toUpperCase() + ", "
                            +originStationsArray.getJSONObject(i).getString("NamaStasiun"));
                }
            }

            destinationStations = new ArrayList<>();
            for (int i=0; i<destinationStationsArray.length(); i++){
                if (destinationStationsArray.getJSONObject(i).getString("City").equals("null") ||
                        destinationStationsArray.getJSONObject(i).getString("City") == null){
                    destinationStations.add(destinationStationsArray.getJSONObject(i).getString("NamaStasiun"));
                }else{
                    destinationStations.add(destinationStationsArray.getJSONObject(i).getString("City").toUpperCase() + ", "
                            +destinationStationsArray.getJSONObject(i).getString("NamaStasiun"));
                }
            }

            if (originStations.size() > 0){
                ArrayAdapter<String> adapterOriginStasiun = new ArrayAdapter<>(this,android.R.layout.simple_spinner_dropdown_item, originStations);
                keretaAwalText.setThreshold(1);
                keretaAwalText.setAdapter(adapterOriginStasiun);
            }

            if (destinationStations.size() > 0){
                ArrayAdapter<String> adapterDestinationStasiun = new ArrayAdapter<>(this,android.R.layout.simple_spinner_dropdown_item, destinationStations);
                keretaTujuanText.setThreshold(1);
                keretaTujuanText.setAdapter(adapterDestinationStasiun);
            }


            stopLoader();
            btnCari.setEnabled(true);
            btnCari.setBackgroundColor(Color.parseColor("#0079C2"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            finish();
            overridePendingTransition(R.anim.right_in, R.anim.right_out);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        listMenu = new ArrayList<>();
        scrollView.fullScroll(View.FOCUS_UP);

        if (sessionManager.isLoggedIn()){
            listMenu.add("Cek Pesanan");
            listMenu.add("Tiket Saya");
            listMenu.add("Daftar Penumpang");
            listMenu.add("Daftar Transaksi");
            listMenu.add("Layanan Pelanggan");
        }else{
            listMenu.add("Cek Pesanan");
            listMenu.add("Layanan Pelanggan");
        }

        listMenuAdapter = new ListMenuAdapter(this, listMenu);
        mListView.setAdapter(listMenuAdapter);
    }

    @Override
    public void finish() {
        super.finish();
        homeKAIActivity = null;
    }

    public void runLoader(){
        preloder.setVisibility(View.VISIBLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }

    public void stopLoader(){
        preloder.setVisibility(View.GONE);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }
}
