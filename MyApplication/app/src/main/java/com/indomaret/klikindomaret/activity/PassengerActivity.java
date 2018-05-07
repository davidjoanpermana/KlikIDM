package com.indomaret.klikindomaret.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.multidex.MultiDex;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
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
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.indomaret.klikindomaret.API;
import com.indomaret.klikindomaret.R;
import com.indomaret.klikindomaret.adapter.PassengerAdultAdapter;
import com.indomaret.klikindomaret.adapter.PassengerBabyAdapter;
import com.indomaret.klikindomaret.helper.Encode2;
import com.indomaret.klikindomaret.helper.Month;
import com.indomaret.klikindomaret.helper.SQLiteHandler;
import com.indomaret.klikindomaret.helper.SessionManager;
import com.indomaret.klikindomaret.views.HeightAdjustableListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PassengerActivity extends AppCompatActivity {
    private Intent intent;
    private SessionManager sessionManager;
    private SQLiteHandler sqLiteHandler;
    private String countAdult, countBaby, date, month, year, hour, minute;
    private boolean scheduleDestination;
    private JSONArray userProfileObjectArray;
    private JSONObject userProfileObject;
    private JSONArray adultList = new JSONArray();
    private JSONArray babytList = new JSONArray();
    private JSONObject objectDataTicketOriginal = new JSONObject();
    private JSONObject objectDataTicketDestination = new JSONObject();
    private JSONObject objectDataPassengerAdult = new JSONObject();
    private JSONObject objectDataPassengerBaby = new JSONObject();
    private JSONObject userData = new JSONObject();
    private TextView originTrainName, originTrainClass, originTrainDuration, originTrainTime1, originTrainDate1, originTrainStation1, infoText,
            originTrainTime2, originTrainDate2, originTrainStation2,
            destinationTrainName, destinationTrainClass, destinationTrainDuration, destinationTrainTime1, destinationTrainDate1, destinationTrainStation1,
            destinationTrainTime2, destinationTrainDate2, destinationTrainStation2, emptyDate, emptyMonth, emptyYear;
    public static EditText inputName, inputTlp, inputEmail;
    private Spinner inputGender, inputDate, inputMonth, inputYear;
    private ArrayList<String> genderList, dateList, monthList, yearList;
    private Button btnSummaryTicket, btnToSit, btnToPayment;
    private LinearLayout linearSummary, linearSummaryDestination;
    private RelativeLayout infoLogin;
    private RelativeLayout preloader;
    private HeightAdjustableListView listPassengerAdult, listPassengerBaby;
    private PassengerAdultAdapter passengerAdultAdapter;
    private PassengerBabyAdapter passengerBabyAdapter;
    private JSONObject dataObject = new JSONObject();
    private JSONArray trainList = new JSONArray();
    private JSONArray passengerList = new JSONArray();
    private JSONArray passengerObjectList = new JSONArray();
    private JSONObject trainObject;
    private Month dateName = new Month();
    private boolean isPairAdult, isPairBaby;
    private Encode2 encode = new Encode2();
    private final int GET_FILTERING = 77;
    public static Activity passengerActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        MultiDex.install(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_passenger);
        passengerActivity = this;

        Toolbar toolbar = (Toolbar) findViewById(R.id.app_toolbar);
        setSupportActionBar(toolbar);
        intent = getIntent();
        sessionManager = new SessionManager(PassengerActivity.this);
        sqLiteHandler = new SQLiteHandler(PassengerActivity.this);

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
        emptyDate = (TextView) findViewById(R.id.empty_date);
        emptyMonth = (TextView) findViewById(R.id.empty_month);
        emptyYear = (TextView) findViewById(R.id.empty_year);
        infoText = (TextView) findViewById(R.id.info_text);

        inputName = (EditText) findViewById(R.id.input_name);
        inputTlp = (EditText) findViewById(R.id.input_tlp);
        inputEmail = (EditText) findViewById(R.id.input_email);

        inputGender = (Spinner) findViewById(R.id.input_gender);
        inputDate = (Spinner) findViewById(R.id.input_date);
        inputMonth = (Spinner) findViewById(R.id.input_month);
        inputYear = (Spinner) findViewById(R.id.input_year);

        btnSummaryTicket = (Button) findViewById(R.id.btn_summary);
        btnToSit = (Button) findViewById(R.id.btn_to_sit);
        btnToPayment = (Button) findViewById(R.id.btn_to_payment);

        linearSummary = (LinearLayout) findViewById(R.id.linear_summary_ticket);
        linearSummaryDestination = (LinearLayout) findViewById(R.id.linear_summary_destination);
        infoLogin = (RelativeLayout) findViewById(R.id.info_login);

        preloader = (RelativeLayout) findViewById(R.id.preloader);

        listPassengerAdult = (HeightAdjustableListView) findViewById(R.id.list_passenger_adult);
        listPassengerBaby = (HeightAdjustableListView) findViewById(R.id.list_passenger_baby);

        btnToSit.setText(Html.fromHtml("<u>Pilih Kursi</u>"));

        requestWithSomeHttpHeaders(API.getInstance().getApiGetPassenger() +"?ID="+sessionManager.getUserID(), "passenger");

        setSpinner();

        scheduleDestination= intent.getBooleanExtra("scheduleDestination", false);
        countAdult = intent.getStringExtra("countAdult");

        try {
            objectDataTicketOriginal = new JSONObject(intent.getStringExtra("objectDataTicketOriginal"));
            userData = new JSONObject(intent.getStringExtra("userData"));
            if (scheduleDestination){
                objectDataTicketDestination = new JSONObject(intent.getStringExtra("objectDataTicketDestination"));
                setSummaryTicket(objectDataTicketOriginal, objectDataTicketDestination);
                linearSummaryDestination.setVisibility(View.VISIBLE);
            }else{
                setSummaryTicket(objectDataTicketOriginal, null);
                linearSummaryDestination.setVisibility(View.GONE);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (intent.getStringExtra("countBaby") == null || intent.getStringExtra("countBaby").equals("") || intent.getStringExtra("countBaby").length() == 0){
            countBaby = "0";
        }else{
            countBaby = intent.getStringExtra("countBaby");
        }

        for (int i=0; i<Integer.valueOf(countAdult); i++){
            adultList.put("Dewasa " + (i+1));
        }

        for (int i=0; i<Integer.valueOf(countBaby); i++){
            babytList.put("Bayi " + (i+1));
        }

        infoText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(PassengerActivity.this, LoginActivity.class);
                intent.putExtra("from", "passenger");
                startActivityForResult(intent, GET_FILTERING);
                overridePendingTransition(R.anim.left_in, R.anim.left_out);
            }
        });

        btnSummaryTicket.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (linearSummary.getVisibility() == View.GONE){
                    btnSummaryTicket.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.arrow_up, 0);
                    linearSummary.setVisibility(View.VISIBLE);
                } else {
                    btnSummaryTicket.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.arrow_down, 0);
                    linearSummary.setVisibility(View.GONE);
                }
            }
        });

        btnToSit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnToSit.setEnabled(false);
                setBooking("toSeat");
            }
        });

        btnToPayment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setBooking("toPayment");
            }
        });

        if(sessionManager.isLoggedIn()){
            setPemesan();
        }else{
            intent = new Intent(PassengerActivity.this, LoginActivity.class);
            intent.putExtra("from", "passenger");
            startActivityForResult(intent, GET_FILTERING);
            overridePendingTransition(R.anim.left_in, R.anim.left_out);
        }
    }

    public void setPemesan(){
        String userProfile = sqLiteHandler.getProfile();

        try {
            userProfileObjectArray = new JSONArray(userProfile);
            userProfileObject = userProfileObjectArray.getJSONObject(0);

            inputName.setText(userProfileObject.getString("FName"));
            inputTlp.setText(userProfileObject.getString("Mobile"));
            inputEmail.setText(userProfileObject.getString("Email"));

            if(userProfileObject.getString("Gender").equals("Pria")){
                inputGender.setSelection(0);
            } else if(userProfileObject.getString("Gender").equals("Wanita")){
                inputGender.setSelection(2);
            }

            String dateString = userProfileObject.getString("DateOfBirthStringFormatted").substring(0, 2);
            String monthString = userProfileObject.getString("DateOfBirthStringFormatted").substring(3, 5);
            int year = Integer.valueOf(userProfileObject.getString("DateOfBirthStringFormatted").substring(6, 10));

            int date, month;
            if (dateString.substring(0).equals("0")){
                date = Integer.valueOf(dateString.substring(1));
            }else{
                date = Integer.valueOf(dateString);
            }

            if (monthString.substring(0).equals("0")){
                month = Integer.valueOf(monthString.substring(1));
            }else{
                month = Integer.valueOf(monthString);
            }

            inputDate.setSelection(date);
            inputMonth.setSelection(month);
            inputYear.setSelection(year - 1962);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void setBooking(String type){
        objectDataPassengerAdult = new JSONObject();
        String regex = "[a-zA-Z0-9 ]+";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(inputName.getText().toString());
        boolean isSuccess = true;
        boolean isSuccessAdult = true;
        boolean isSuccessInfant = true;

        try {
            if (inputName.getText().toString() == null || inputName.getText().toString().equals("")){
                inputName.setError("Nama Pemesan tidak boleh kosong");
                isSuccess = false;
            }else  if (!matcher.matches()) {
                inputName.setError("Nama pemesan tidak boleh mengandung spesial karakter");
                isSuccess = false;
            }

            if (inputTlp.getText().toString() == null || inputTlp.getText().toString().equals("")){
                inputTlp.setError("No Telp Pemesan tidak boleh kosong");
                isSuccess = false;
            }else  if (inputTlp.getText().toString().length() < 8){
                inputTlp.setError("Format nomot telp/hp salah");
                isSuccess = false;
            }else  if (inputTlp.getText().toString().length() > 16){
                inputTlp.setError("Nomor telp pemesan maksimal 16 karakter");
                isSuccess = false;
            }

            if (inputEmail.getText().toString() == null || inputEmail.getText().toString().equals("")){
                inputEmail.setError("Email Pemesan tidak boleh kosong");
                isSuccess = false;
            }

            if (inputDate.getSelectedItem().toString() == null || inputDate.getSelectedItem().toString().equals("Tanggal")){
                emptyDate.setText("* Silahkan Pilih hari lahir Pemesan");
                emptyDate.setVisibility(View.VISIBLE);
                isSuccess = false;
            }else {
                emptyDate.setVisibility(View.GONE);
            }

            if (inputMonth.getSelectedItem().toString() == null || inputMonth.getSelectedItem().toString().equals("Bulan")){
                emptyMonth.setText("* Silahkan Pilih bulan lahir Pemesan");
                emptyMonth.setVisibility(View.VISIBLE);
                isSuccess = false;
            }else {
                emptyMonth.setVisibility(View.GONE);
            }

            if (inputYear.getSelectedItem().toString() == null || inputYear.getSelectedItem().toString().equals("Tahun")){
                emptyYear.setText("* Silahkan Pilih tahun lahir Pemesan");
                emptyYear.setVisibility(View.VISIBLE);
                isSuccess = false;
            }else {
                emptyYear.setVisibility(View.GONE);
            }

            if (isSuccess){
                runLoader();
                JSONObject jsonObject = new JSONObject();
                passengerList = new JSONArray();

                for (int u=0; u<listPassengerAdult.getAdapter().getCount(); u++){
                    View view = listPassengerAdult.getChildAt(u);
                    EditText nameText = (EditText) view.findViewById(R.id.input_name);
                    Spinner inputGender = (Spinner) view.findViewById(R.id.input_gender);
                    EditText inputIdentitas = (EditText) view.findViewById(R.id.input_identitas);

                    isPairAdult = false;
                    objectDataPassengerAdult = new JSONObject();
                    objectDataPassengerAdult.put("PassengerType", "Adult");
                    objectDataPassengerAdult.put("PassengerName", nameText.getText().toString());
                    objectDataPassengerAdult.put("PassengerPhone", inputTlp.getText().toString());
                    objectDataPassengerAdult.put("PassengerIdentityNo", inputIdentitas.getText().toString());
                    objectDataPassengerAdult.put("Salutation", String.valueOf(inputGender.getSelectedItemPosition()));

                    if (nameText.getText() == null || nameText.getText().toString().equals("")){
                        nameText.setError("Nama penumpang dewasa harus diisi");
                        stopLoader();
                        isSuccessAdult = false;
                    }else if (!pattern.matcher(nameText.getText().toString()).matches()) {
                        nameText.setError("Nama penumpang dewasa tidak boleh mengandung spesial karakter");
                        stopLoader();
                        isSuccessAdult = false;
                    }

                    if (inputIdentitas.getText() == null || inputIdentitas.getText().toString().equals("")){
                        inputIdentitas.setError("Nomor identitas atau tanggal lahir penumpang dewasa harus diisi");
                        stopLoader();
                        isSuccessAdult = false;
                    }else if (inputIdentitas.getText().toString().length() > 20){
                        inputIdentitas.setError("Nomor identitas atau tanggal lahir penumpang dewasa maksimal 20 karakter");
                        stopLoader();
                        isSuccessAdult = false;
                    }

                    if (isSuccessAdult){
                        for (int j=0; j<passengerObjectList.length(); j++){
                            if (passengerObjectList.getJSONObject(j).getString("FullName").equals(nameText.getText().toString())){
                                if (!passengerObjectList.getJSONObject(j).getString("FullName").equals(nameText.getText().toString())
                                        || !passengerObjectList.getJSONObject(j).getString("Identity").equals(inputIdentitas.getText().toString())
                                        || !passengerObjectList.getJSONObject(j).getString("Salutation").equals(String.valueOf(inputGender.getSelectedItemPosition()))){
                                    jsonObject.put("ID", passengerObjectList.getJSONObject(j).getString("ID"));
                                    jsonObject.put("CustomerID", passengerObjectList.getJSONObject(j).getString("CustomerID"));
                                    jsonObject.put("FullName", nameText.getText().toString());
                                    jsonObject.put("Phone", inputTlp.getText().toString());
                                    jsonObject.put("Identity", inputIdentitas.getText().toString());
                                    jsonObject.put("Salutation", String.valueOf(inputGender.getSelectedItemPosition()));
                                    jsonObject.put("Maturity", "0");

                                    jsonPost(API.getInstance().getApiEditPassenger(), jsonObject, "passenger");
                                }

                                isPairAdult = true;
                            }
                        }

                        if (!isPairAdult){
                            if (nameText.getText().toString().length() > 0 || inputIdentitas.getText().toString().length() > 0
                                    || String.valueOf(inputGender.getSelectedItemPosition()).length() > 0){
                                jsonObject.put("ID", "");
                                jsonObject.put("CustomerID", sessionManager.getUserID());
                                jsonObject.put("FullName", nameText.getText().toString());
                                jsonObject.put("Phone", inputTlp.getText().toString());
                                jsonObject.put("Identity", inputIdentitas.getText().toString());
                                jsonObject.put("Salutation", String.valueOf(inputGender.getSelectedItemPosition()));
                                jsonObject.put("Maturity", "0");

                                jsonPost(API.getInstance().getApiAddPassenger(), jsonObject, "passenger");
                            }
                        }

                        passengerList.put(objectDataPassengerAdult);
                        objectDataPassengerAdult = new JSONObject();
                    }
                }

                for (int u=0; u<listPassengerBaby.getAdapter().getCount(); u++) {
                    View view = listPassengerBaby.getChildAt(u);
                    EditText nameText = (EditText) view.findViewById(R.id.input_name);
                    Spinner inputGender = (Spinner) view.findViewById(R.id.input_gender);

                    isPairBaby = false;
                    objectDataPassengerBaby = new JSONObject();
                    objectDataPassengerBaby.put("PassengerType", "Infant");
                    objectDataPassengerBaby.put("PassengerName", nameText.getText().toString());
                    objectDataPassengerBaby.put("PassengerPhone", inputTlp.getText().toString());
                    objectDataPassengerBaby.put("Salutation", String.valueOf(inputGender.getSelectedItemPosition()));

                    if (nameText.getText() == null || nameText.getText().toString().equals("")){
                        nameText.setError("Nama penumpang bayi harus diisi");
                        stopLoader();
                        isSuccessInfant = false;
                    }else if (!pattern.matcher(inputGender.getSelectedItem().toString()).matches()) {
                        nameText.setError("Nama penumpang bayi tidak boleh mengandung spesial karakter");
                        stopLoader();
                        isSuccessInfant = false;
                    }

                    if (isSuccessInfant){
                        for (int j=0; j<passengerObjectList.length(); j++){
                            if (passengerObjectList.getJSONObject(j).getString("FullName").equals(nameText.getText().toString())){
                                if (!passengerObjectList.getJSONObject(j).getString("FullName").equals(nameText.getText().toString())
                                        || !passengerObjectList.getJSONObject(j).getString("Salutation").equals(String.valueOf(inputGender.getSelectedItemPosition()))){
                                    jsonObject.put("ID", passengerObjectList.getJSONObject(j).getString("ID"));
                                    jsonObject.put("CustomerID", passengerObjectList.getJSONObject(j).getString("CustomerID"));
                                    jsonObject.put("FullName", nameText.getText().toString());
                                    jsonObject.put("Phone", inputTlp.getText().toString());
                                    jsonObject.put("Identity", "");
                                    jsonObject.put("Salutation", String.valueOf(inputGender.getSelectedItemPosition()));
                                    jsonObject.put("Maturity", "1");

                                    jsonPost(API.getInstance().getApiEditPassenger(), jsonObject, "passenger");
                                }

                                isPairBaby = true;
                            }
                        }

                        if (!isPairBaby){
                            if (nameText.getText().toString().length() > 0 || String.valueOf(inputGender.getSelectedItemPosition()).length() > 0){
                                jsonObject.put("ID", "");
                                jsonObject.put("CustomerID", sessionManager.getUserID());
                                jsonObject.put("FullName", nameText.getText().toString());
                                jsonObject.put("Phone", inputTlp.getText().toString());
                                jsonObject.put("Identity", "");
                                jsonObject.put("Salutation", String.valueOf(inputGender.getSelectedItemPosition()));
                                jsonObject.put("Maturity", "1");

                                jsonPost(API.getInstance().getApiAddPassenger(), jsonObject, "passenger");
                            }
                        }

                        passengerList.put(objectDataPassengerBaby);
                        objectDataPassengerBaby = new JSONObject();
                    }
                }

                if (isSuccessAdult && isSuccessInfant){
                    trainObject = new JSONObject();
                    trainList = new JSONArray();
                    trainObject.put("Original", objectDataTicketOriginal.getString("Original"));
                    trainObject.put("Destination", objectDataTicketOriginal.getString("Destination"));

                    date = objectDataTicketOriginal.getString("DepartureDate").split("T")[0].substring(8, 10);
                    month = objectDataTicketOriginal.getString("DepartureDate").split("T")[0].substring(5, 7);
                    year = objectDataTicketOriginal.getString("DepartureDate").split("T")[0].substring(0, 4);
                    hour = objectDataTicketOriginal.getString("DepartureDate").split("T")[1].substring(0, 2);
                    minute = objectDataTicketOriginal.getString("DepartureDate").split("T")[1].substring(3, 5);

                    trainObject.put("DepartureDate", date+month+year);
                    trainObject.put("DepartureTime", hour+minute);

                    date = objectDataTicketOriginal.getString("ArrivalDate").split("T")[0].substring(8, 10);
                    month = objectDataTicketOriginal.getString("ArrivalDate").split("T")[0].substring(5, 7);
                    year = objectDataTicketOriginal.getString("ArrivalDate").split("T")[0].substring(0, 4);
                    hour = objectDataTicketOriginal.getString("ArrivalDate").split("T")[1].substring(0, 2);
                    minute = objectDataTicketOriginal.getString("ArrivalDate").split("T")[1].substring(3, 5);

                    trainObject.put("ArrivalDate",  date+month+year);
                    trainObject.put("ArrivalTime", hour+minute);
                    trainObject.put("TrainClassCode", objectDataTicketOriginal.getString("TrainClassCode"));
                    trainObject.put("TrainSubClassCode", objectDataTicketOriginal.getString("TrainSubClassCode"));
                    trainObject.put("TicketPriceAdult", objectDataTicketOriginal.getString("AdultPrice"));
                    trainObject.put("TicketPriceChild", objectDataTicketOriginal.getString("ChildPrice"));
                    trainObject.put("TicketPriceInfant", objectDataTicketOriginal.getString("InfantPrice"));
                    trainObject.put("TotalRemainingSeat", "");
                    trainObject.put("TrainNumber", objectDataTicketOriginal.getString("TrainNumber"));
                    trainObject.put("TrainName", objectDataTicketOriginal.getString("TrainName"));
                    trainObject.put("passengers", passengerList);

                    trainList.put(trainObject);

                    trainObject = new JSONObject();
                    if (objectDataTicketDestination.length() > 0){
                        trainObject.put("Original", objectDataTicketDestination.getString("Original"));
                        trainObject.put("Destination", objectDataTicketDestination.getString("Destination"));

                        date = objectDataTicketDestination.getString("DepartureDate").split("T")[0].substring(8, 10);
                        month = objectDataTicketDestination.getString("DepartureDate").split("T")[0].substring(5, 7);
                        year = objectDataTicketDestination.getString("DepartureDate").split("T")[0].substring(0, 4);
                        hour = objectDataTicketDestination.getString("DepartureDate").split("T")[1].substring(0, 2);
                        minute = objectDataTicketDestination.getString("DepartureDate").split("T")[1].substring(3, 5);

                        trainObject.put("DepartureDate", date+month+year);
                        trainObject.put("DepartureTime", hour+minute);

                        date = objectDataTicketDestination.getString("ArrivalDate").split("T")[0].substring(8, 10);
                        month = objectDataTicketDestination.getString("ArrivalDate").split("T")[0].substring(5, 7);
                        year = objectDataTicketDestination.getString("ArrivalDate").split("T")[0].substring(0, 4);
                        hour = objectDataTicketDestination.getString("ArrivalDate").split("T")[1].substring(0, 2);
                        minute = objectDataTicketDestination.getString("ArrivalDate").split("T")[1].substring(3, 5);

                        trainObject.put("ArrivalDate", date+month+year);
                        trainObject.put("ArrivalTime", hour+minute);
                        trainObject.put("TrainClassCode", objectDataTicketDestination.getString("TrainClassCode"));
                        trainObject.put("TrainSubClassCode", objectDataTicketDestination.getString("TrainSubClassCode"));
                        trainObject.put("TicketPriceAdult", objectDataTicketDestination.getString("AdultPrice"));
                        trainObject.put("TicketPriceChild", objectDataTicketDestination.getString("ChildPrice"));
                        trainObject.put("TicketPriceInfant", objectDataTicketDestination.getString("InfantPrice"));
                        trainObject.put("TotalRemainingSeat", "");
                        trainObject.put("TrainNumber", objectDataTicketDestination.getString("TrainNumber"));
                        trainObject.put("TrainName", objectDataTicketDestination.getString("TrainName"));
                        trainObject.put("passengers", passengerList);

                        trainList.put(trainObject);
                    }


                    if (inputDate.getSelectedItem().toString().length() > 1){
                        date = inputDate.getSelectedItem().toString();
                    }else{
                        date = "0"+ inputDate.getSelectedItem().toString();
                    }

                    if (inputMonth.getSelectedItem().toString().length() > 1){
                        month = inputMonth.getSelectedItem().toString();
                    }else{
                        month = "0"+ inputMonth.getSelectedItem().toString();
                    }

                    year = inputYear.getSelectedItem().toString();
                    dataObject = new JSONObject();
                    dataObject.put("CustomerID", sessionManager.getUserID());
                    dataObject.put("JmlDewasa", Integer.valueOf(countAdult));
                    dataObject.put("JmlInfant", Integer.valueOf(countBaby));
                    dataObject.put("CustomerPhoneNumber", inputTlp.getText().toString());
                    dataObject.put("CustomerEmail", inputEmail.getText().toString());
                    dataObject.put("CustomerName", inputName.getText().toString());
                    dataObject.put("CustomerDateOfBirth", date+dateName.getMonthNumber(month)+year);
                    dataObject.put("CustomerSallutation", inputGender.getSelectedItemId());
                    dataObject.put("Detail", trainList);

                    jsonPost(API.getInstance().getApiBookingBooking(), dataObject, type);
                }else{
                    btnToSit.setEnabled(true);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
            stopLoader();
        }
    }

    public JSONObject getData(){
        JSONObject object = new JSONObject();
        try {
            object.put("Salutation", inputGender.getSelectedItemId());
            object.put("PassengerName", inputName.getText().toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return object;
    }

    public void setSpinner(){
        List<String> months = new ArrayList<>();
        genderList = new ArrayList<>();
        dateList = new ArrayList<>();
        monthList = new ArrayList<>();
        yearList = new ArrayList<>();

        genderList.add("Tuan");
        genderList.add("Nyonya");
        genderList.add("Nona");

        months.add("Januari");
        months.add("Februari");
        months.add("Maret");
        months.add("April");
        months.add("Mei");
        months.add("Juni");
        months.add("Juli");
        months.add("Agustus");
        months.add("September");
        months.add("Oktober");
        months.add("November");
        months.add("Desember");

        Calendar calendar = Calendar.getInstance();

        dateList.add("Tanggal");
        for (int i=0; i<31; i++){
            dateList.add(String.valueOf(i+1));
        }

        monthList.add("Bulan");
        for (int i=0; i<11; i++){
            monthList.add(months.get(i));
        }

        yearList.add("Tahun");
        for (int i=(calendar.get(Calendar.YEAR ) - 11); i>(calendar.get(Calendar.YEAR) - 100); i--){
            yearList.add(String.valueOf(i+1));
        }

        ArrayAdapter<String> dataAdapterGender = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, genderList);
        dataAdapterGender.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        inputGender.setAdapter(dataAdapterGender);

        ArrayAdapter<String> dataAdapterDate = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, dateList);
        dataAdapterDate.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        inputDate.setAdapter(dataAdapterDate);

        ArrayAdapter<String> dataAdapterMonth = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, monthList);
        dataAdapterMonth.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        inputMonth.setAdapter(dataAdapterMonth);

        ArrayAdapter<String> dataAdapterYear = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, yearList);
        dataAdapterYear.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        inputYear.setAdapter(dataAdapterYear);
    }

    public void setSummaryTicket (JSONObject objectOriginal, JSONObject objectDestination){
        try {
            originTrainName.setText(objectOriginal.getString("TrainName") + " " + objectOriginal.getString("TrainNumber"));
            originTrainClass.setText(objectOriginal.getString("TrainClassName") + " (" + objectOriginal.getString("TrainSubClassCode") + ")");
            originTrainDuration.setText(objectOriginal.getString("DurationInHour").split("\\.")[0] + "J " + objectOriginal.getString("DurationInMinutes").split("\\.")[0] + "M ");
            originTrainTime1.setText(objectOriginal.getString("DepartureDate").split("T")[1].substring(0, 5));

            date = objectOriginal.getString("DepartureDate").split("T")[0].substring(8, 10);
            month = objectOriginal.getString("DepartureDate").split("T")[0].substring(5, 7);
            year = objectOriginal.getString("DepartureDate").split("T")[0].substring(0, 4);

            originTrainDate1.setText(date + " " + dateName.getMonth2(month.toString()) + " " + year);
            originTrainStation1.setText(userData.getString("originalStation").split(",")[1]);
            destinationTrainTime1.setText(objectOriginal.getString("ArrivalDate").split("T")[1].substring(0, 5));

            date = objectOriginal.getString("ArrivalDate").split("T")[0].substring(8, 10);
            month = objectOriginal.getString("ArrivalDate").split("T")[0].substring(5, 7);
            year = objectOriginal.getString("ArrivalDate").split("T")[0].substring(0, 4);

            destinationTrainDate1.setText(date + " " + dateName.getMonth2(month.toString()) + " " + year);
            destinationTrainStation1.setText(userData.getString("destinationStation").split(",")[1]);

            if (scheduleDestination){
                destinationTrainName.setText(objectDestination.getString("TrainName") + " " + objectDestination.getString("TrainNumber"));
                destinationTrainClass.setText(objectDestination.getString("TrainClassName") + " (" + objectDestination.getString("TrainSubClassCode") + ")");
                destinationTrainDuration.setText(objectDestination.getString("DurationInHour").split("\\.")[0] + "J " + objectDestination.getString("DurationInMinutes").split("\\.")[0] + "M ");
                originTrainTime2.setText(objectDestination.getString("DepartureDate").split("T")[1].subSequence(0, 5));

                date = objectDestination.getString("DepartureDate").split("T")[0].substring(8, 10);
                month = objectDestination.getString("DepartureDate").split("T")[0].substring(5, 7);
                year = objectDestination.getString("DepartureDate").split("T")[0].substring(0, 4);

                originTrainDate2.setText(date + " " + dateName.getMonth2(month.toString()) + " " + year);
                originTrainStation2.setText(userData.getString("destinationStation").split(",")[1]);
                destinationTrainTime2.setText(objectDestination.getString("ArrivalDate").split("T")[1].subSequence(0, 5));

                date = objectDestination.getString("ArrivalDate").split("T")[0].substring(8, 10);
                month = objectDestination.getString("ArrivalDate").split("T")[0].substring(5, 7);
                year = objectDestination.getString("ArrivalDate").split("T")[0].substring(0, 4);

                destinationTrainDate2.setText(date + " " + dateName.getMonth2(month.toString()) + " " + year);
                destinationTrainStation2.setText(userData.getString("originalStation").split(",")[1]);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void jsonPost(String urlJsonObj, final JSONObject jsonObject, final String type) {
        RequestQueue queue = Volley.newRequestQueue(this);
        System.out.println("--- passenger url : "+urlJsonObj);
        System.out.println("--- passenger obj : "+jsonObject);
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                urlJsonObj, jsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if (response == null || response.length() == 0){
                                stopLoader();
                                Toast.makeText(PassengerActivity.this, "Gagal terkoneksi server", Toast.LENGTH_LONG).show();
                            }else{
                                if (response.getString("Message").equals("Success")){
                                    if (type.equals("toSeat")){
                                        intent = new Intent(PassengerActivity.this, ChooseSeatActivity.class);
                                        intent.putExtra("response", response.toString());
                                        intent.putExtra("jsonObject", jsonObject.toString());
                                        intent.putExtra("current", "original");
                                        intent.putExtra("from", "passenger");
                                        intent.putExtra("userData", userData.toString());
                                        intent.putExtra("objectDataTicketOriginal", objectDataTicketOriginal.toString());
                                        if (scheduleDestination) intent.putExtra("objectDataTicketDestination", objectDataTicketDestination.toString());
                                        intent.putExtra("scheduleDestination", scheduleDestination);

                                        if (response.getJSONArray("Data").length() == 2){
                                            intent.putExtra("isDestination", true);
                                        }else{
                                            intent.putExtra("isDestination", false);
                                        }

                                        startActivity(intent);
                                        overridePendingTransition(R.anim.left_in, R.anim.left_out);
                                    } else if (type.equals("toPayment")){
                                        requestWithSomeHttpHeaders(API.getInstance().getApiSalesOrderId()
                                                +"?ID="+response.getJSONArray("Data").getJSONObject(0).getString("SalesOrderHeaderID"), "salesOrder");
                                    }else if (type.equals("passenger")){
                                        requestWithSomeHttpHeaders(API.getInstance().getApiGetPassenger() +"?ID="+sessionManager.getUserID(), "passenger");
                                    }
                                } else if (response.getString("Code").equals("606")){
                                    stopLoader();
                                    btnToSit.setEnabled(true);
                                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(PassengerActivity.this);
                                    LayoutInflater factory = LayoutInflater.from(PassengerActivity.this);
                                    final View view = factory.inflate(R.layout.timeout_kai, null);
                                    alertDialogBuilder.setView(view);

                                    Button btnBack = (Button) view.findViewById(R.id.btn_back);
                                    TextView messageText = (TextView) view.findViewById(R.id.message);

                                    messageText.setText("Kursi telah habis.\nSilakan lakukan pencarian kembali.");

                                    btnBack.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            intent = new Intent(PassengerActivity.this, HomeKAIActivity.class);
                                            intent.putExtra("from", "backHome");
                                            intent.putExtra("dataTrain", userData.toString());
                                            if (ListTicketKAIActivity.listTicketKAIActivity != null) ListTicketKAIActivity.listTicketKAIActivity.finish();
                                            finish();
                                            startActivity(intent);
                                            overridePendingTransition(R.anim.left_in, R.anim.left_out);
                                        }
                                    });
                                    alertDialogBuilder.show();
                                } else {
                                    Toast.makeText(PassengerActivity.this, response.getString("Message"), Toast.LENGTH_SHORT).show();
                                    stopLoader();
                                    btnToSit.setEnabled(true);
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            stopLoader();
                            btnToSit.setEnabled(true);
                        }
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        btnToSit.setEnabled(true);
                        Toast.makeText(PassengerActivity.this, "Gagal terkoneksi server", Toast.LENGTH_LONG).show();
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
                if (type.equals("passenger")){
                    params.put("Authorization", "bearer "+token+"#"+sessionManager.getUserID());
                }else{
                    params.put("Authorization", "bearer "+token);
                }

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
        System.out.println("--- url passnger : "+url);
        StringRequest postRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>(){
            @Override
            public void onResponse(String response) {
                try {
                    if (response == null || response.length() == 0){
                        stopLoader();
                        Toast.makeText(PassengerActivity.this, "Gagal terkoneksi server", Toast.LENGTH_LONG).show();
                    }else{
                        JSONObject jObject = new JSONObject(response);
                        if (jObject.getBoolean("IsSuccess")) {
                            if (type.equals("salesOrder")){
                                intent = new Intent(PassengerActivity.this, OrderSummaryKAIActivity.class);
                                intent.putExtra("from", "passenger");
                                intent.putExtra("response", response.toString());
                                intent.putExtra("userData", userData.toString());
                                intent.putExtra("objectDataTicketOriginal", objectDataTicketOriginal.toString());
                                if (scheduleDestination) intent.putExtra("objectDataTicketDestination", objectDataTicketDestination.toString());
                                intent.putExtra("scheduleDestination", scheduleDestination);
                                finish();
                                startActivity(intent);
                                overridePendingTransition(R.anim.left_in, R.anim.left_out);
                            }else if (type.equals("passenger")){
                                passengerObjectList = jObject.getJSONArray("Data");

                                passengerAdultAdapter = new PassengerAdultAdapter(PassengerActivity.this, adultList, passengerObjectList);
                                listPassengerAdult.setAdapter(passengerAdultAdapter);

                                passengerBabyAdapter = new PassengerBabyAdapter(PassengerActivity.this, babytList, passengerObjectList);
                                listPassengerBaby.setAdapter(passengerBabyAdapter);
                            }
                        }else{
                            Toast.makeText(PassengerActivity.this, jObject.getString("Message"), Toast.LENGTH_SHORT).show();
                            stopLoader();
                            btnToSit.setEnabled(true);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    stopLoader();
                    btnToSit.setEnabled(true);
                    Toast.makeText(PassengerActivity.this, "Gagal terkoneksi server", Toast.LENGTH_LONG).show();
                }
            }
        },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        btnToSit.setEnabled(true);
                        stopLoader();
                        Toast.makeText(PassengerActivity.this, "Gagal terkoneksi server", Toast.LENGTH_LONG).show();
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
                if (type.equals("passenger")){
                    params.put("Authorization", "bearer "+token+"#"+sessionManager.getUserID());
                }else{
                    params.put("Authorization", "bearer "+token);
                }

                return params;
            }
        };

        postRequest.setRetryPolicy(new DefaultRetryPolicy(50000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(postRequest);
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
                    if(sessionManager.isLoggedIn()){
                        infoLogin.setVisibility(View.GONE);
                        setPemesan();
                    }else{
                        infoLogin.setVisibility(View.VISIBLE);
                    }
            }
        }else if (resultCode == RESULT_OK) {
            finish();
            overridePendingTransition(R.anim.right_in, R.anim.right_out);
        }
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
    protected void onResume() {
        super.onResume();

        if(sessionManager.isLoggedIn()){
            infoLogin.setVisibility(View.GONE);
        }else{
            infoLogin.setVisibility(View.VISIBLE);
        }
    }
}
