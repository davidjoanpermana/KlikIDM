package com.indomaret.klikindomaret.activity;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.indomaret.klikindomaret.API;
import com.indomaret.klikindomaret.R;
import com.indomaret.klikindomaret.adapter.SearchAddressAdapter;
import com.indomaret.klikindomaret.app.AppController;
import com.indomaret.klikindomaret.helper.SQLiteHandler;
import com.indomaret.klikindomaret.helper.SessionManager;
import com.indomaret.klikindomaret.helper.UncaughtExceptionHandler;
import com.indomaret.klikindomaret.utils.Helper;
import com.indomaret.klikindomaret.views.HeightAdjustableListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import pl.droidsonroids.gif.GifImageView;

public class  AddAdressActivity extends AppCompatActivity implements OnMapReadyCallback, ActivityCompat.OnRequestPermissionsResultCallback,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, ResultCallback<LocationSettingsResult> {
    private SessionManager sessionManager;
    private SQLiteHandler sqLiteHandler;
    private Intent intent;
    private JSONObject address;
    private String types;
    private String googleAddress = "";
    private String ketAddress = "";
    private String currentLat = "" ;
    private String currentLong = "";
    private Tracker mTracker;

    private RelativeLayout preloader;

    private EditText receiverName, receiverPhone, receiverAddress, receiverAddress2, receiverAddress3, zipcode, initialAddress;
    private Spinner region, city, district, subdistrict;
    private Button save;
    private ScrollView scrollView;
    private RelativeLayout relativeMap;
    private ImageView imageView, imagePlace;

    private JSONArray regionArray, cityArray, districtArray, subdistrictArray;
    //maps
    protected GoogleMap mMap;
    protected SupportMapFragment mapFragment;
    protected GoogleApiClient mGoogleApiClient;
    protected Location lastCurrentLocation;
    protected LatLng currentlatLng, addressLatLng;
    protected LocationSettingsRequest mLocationSettingsRequest;
    protected LocationRequest mLocationRequest;
    private PlaceAutocompleteFragment autocompleteFragment;
    private static final long UPDATE_INTERVAL_IN_MILLISECONDS = 30000;
    private static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS =
            UPDATE_INTERVAL_IN_MILLISECONDS / 2;
    private TextView tvAlamatToko, btnEditMap;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 19;
    protected static final int REQUEST_CHECK_SETTINGS = 0x1;
    private String addressTextValue = "";
    private EditText etKetTambahAlamat;
    private boolean isInitialUpdate;
    private EditText etSearchAddress;
    private HeightAdjustableListView searchAddressList;
    private JSONArray addressList = new JSONArray();
    private LinearLayout linListSearch, linearSearchMap, linearParent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_adress);

        UncaughtExceptionHandler uncaughtExceptionHandler = new UncaughtExceptionHandler();
        uncaughtExceptionHandler.uncaughtException(this);

        AppController application = (AppController) getApplication();
        mTracker = application.getDefaultTracker();

        mTracker.setScreenName("Add Address Page");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());

        intent = getIntent();
        types = intent.getStringExtra("type");

        Toolbar toolbar = (Toolbar) findViewById(R.id.app_toolbar);
        TextView mTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        sessionManager = new SessionManager(AddAdressActivity.this);
        sqLiteHandler = new SQLiteHandler(AddAdressActivity.this);

        preloader = (RelativeLayout) findViewById(R.id.preloader);

        receiverName = (EditText) findViewById(R.id.receiver_name);
        receiverPhone = (EditText) findViewById(R.id.receiver_mobile);
        receiverAddress = (EditText) findViewById(R.id.address);
        receiverAddress2 = (EditText) findViewById(R.id.address2);
        receiverAddress3 = (EditText) findViewById(R.id.address3);
        region = (Spinner) findViewById(R.id.region_spinner);
        city = (Spinner) findViewById(R.id.city_spinner);
        district = (Spinner) findViewById(R.id.district_spinner);
        subdistrict = (Spinner) findViewById(R.id.subdistrict_spinner);
        zipcode = (EditText) findViewById(R.id.zipcode);
        initialAddress = (EditText) findViewById(R.id.initial_address);
        scrollView = (ScrollView) findViewById(R.id.scrollView);
        relativeMap = (RelativeLayout) findViewById(R.id.relativeMap);
        imageView = (ImageView) findViewById(R.id.imagetrans);
        imagePlace = (ImageView) findViewById(R.id.imagePlace);
        tvAlamatToko = (TextView) findViewById(R.id.tvAlamatToko);
        btnEditMap = (TextView) findViewById(R.id.btn_edit_map);
        etKetTambahAlamat = (EditText) findViewById(R.id.etKetTambahAlamat);
        etSearchAddress = (EditText) findViewById(R.id.etSearchAddress);
        searchAddressList = (HeightAdjustableListView) findViewById(R.id.searchAddressList);
        linListSearch = (LinearLayout) findViewById(R.id.linListSearch);
        linearSearchMap = (LinearLayout) findViewById(R.id.linear_search_map);
        linearParent = (LinearLayout) findViewById(R.id.linear_parent);

        autocompleteFragment = (PlaceAutocompleteFragment) getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);
        ((EditText)autocompleteFragment.getView().findViewById(R.id.place_autocomplete_search_input)).setTextSize(15.0f);
        ((View)findViewById(R.id.place_autocomplete_search_button)).setPadding(0,0,0,0);
        autocompleteFragment.setHint("Masukan kata pencarian Anda disini...");

        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                currentlatLng = new LatLng(place.getLatLng().latitude, place.getLatLng().longitude);
                Helper.moveMapCamera(mMap, currentlatLng, 13);
            }

            @Override
            public void onError(Status status) {
                // TODO: Handle the error.
            }
        });

        if (types.contains("add")){
            mTitle.setText("Tambah Alamat Baru");
        }else{
            mTitle.setText("Ubah Alamat");
        }

        etSearchAddress.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                timer.cancel();
            }

            @Override
            public void afterTextChanged(Editable editable) {
                timer.start();
            }
        });

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        buildGoogleApiClient();
        createLocationRequest();
        buildLocationSettingsRequest();
        checkLocationSettings();

        if(types.equals("update") || types.equals("updateShipping")){
            try {
                address = new JSONObject(intent.getStringExtra("data"));

                if (address.getString("GoogleAddress") == null || address.getString("GoogleAddress").equals("") ||
                        address.getString("GoogleAddress").toLowerCase().equals("null")){
                    tvAlamatToko.setText("Mohon tandai lokasi pada peta");
                    btnEditMap.setText("Tambah");
                }else{
                    tvAlamatToko.setText(address.getString("KetAlamat"));
                    btnEditMap.setText("Ubah");
                }

                receiverName.setText(address.getString("ReceiverName"));
                receiverPhone.setText(address.getString("ReceiverPhone"));
                receiverAddress.setText(address.getString("Street"));

                if(!address.getString("Street2").equals("null")){
                    receiverAddress2.setText(address.getString("Street2"));
                }else{
                    receiverAddress2.setText("");
                }

                if(!address.getString("Street3").equals("null")){
                    receiverAddress3.setText(address.getString("Street3"));
                }else{
                    receiverAddress3.setText("");
                }

                initialAddress.setText(address.getString("AddressTitle").toUpperCase());

                if(!address.getString("KetAlamat").equalsIgnoreCase("null") &&
                        !TextUtils.isEmpty(address.getString("KetAlamat"))){
                    etKetTambahAlamat.setText(address.getString("KetAlamat"));
                }

                if(address.getDouble("Latitude") != 0 && address.getDouble("Longitude") != 0){
                    addressLatLng = new LatLng(address.getDouble("Latitude"), address.getDouble("Longitude"));
                    isInitialUpdate = true;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            //default address
            tvAlamatToko.setText("Mohon tandai lokasi pada peta");
            btnEditMap.setText("Tambah");
            addressLatLng = new LatLng(-6.155512519708498, 106.8514071802915);
        }

        btnEditMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                linearSearchMap.setVisibility(View.VISIBLE);
                relativeMap.setVisibility(View.VISIBLE);
                btnEditMap.setVisibility(View.GONE);
            }
        });

        imageView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                int action = event.getAction();
                switch (action) {
                    case MotionEvent.ACTION_DOWN:
                        // Disallow ScrollView to intercept touch events.
                        scrollView.requestDisallowInterceptTouchEvent(true);
                        // Disable touch on transparent view
                        return false;

                    case MotionEvent.ACTION_UP:
                        // Allow ScrollView to intercept touch events.
                        scrollView.requestDisallowInterceptTouchEvent(false);
                        return true;

                    case MotionEvent.ACTION_MOVE:
                        scrollView.requestDisallowInterceptTouchEvent(true);
                        return false;

                    default:
                        return true;
                }
            }
        });

        save = (Button) findViewById(R.id.btn_save_update);

        System.out.println("default add = " + sqLiteHandler.getDefaultAddress());
        makeJsonObjectGet(API.getInstance().getApiGetProvince()+"?mfp_id="+sessionManager.getKeyMfpId(), "region");

        region.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        if (position > 0){
                            try {
                                makeJsonObjectGet(API.getInstance().getApiGetRegionByProvince()+"?ProvinceId="+regionArray.getJSONObject(position-1).getString("ID")+"&mfp_id="+sessionManager.getKeyMfpId(), "city");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        } else {
                            processCity(new JSONArray());
                            processDistrict(new JSONArray());
                            processSubdistrict(new JSONArray());
                            zipcode.setText("");
                        }
                    }
                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                }
        );

        city.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        if(position > 0){
                            try {
                                makeJsonObjectGet(API.getInstance().getApiGetDsitrictByCity()+"?RegionId="+cityArray.getJSONObject(position-1).getString("ID")+"&mfp_id="+sessionManager.getKeyMfpId(), "district");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        } else {
                            processDistrict(new JSONArray());
                            processSubdistrict(new JSONArray());
                            zipcode.setText("");
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                }
        );

        district.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        if(position > 0){
                            try{
                                makeJsonObjectGet(API.getInstance().getApiGetSubdistrictByDistrict()+"?cityId="+districtArray.getJSONObject(position-1).getString("CityId")+"&mfp_id="+sessionManager.getKeyMfpId(), "subdistrict");
                            } catch (JSONException e){
                                e.printStackTrace();
                            }
                        } else {
                            processSubdistrict(new JSONArray());
                            zipcode.setText("");
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {}
                }
        );

        subdistrict.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        if (position > 0){
                            try {
                                makeJsonObjectGet(API.getInstance().getApiGetZipcodebySubdistrict()+"?districtId="+subdistrictArray.getJSONObject(position-1).getString("ID")+"&mfp_id="+sessionManager.getKeyMfpId(), "zipcode");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        } else {
                            zipcode.setText("");
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                }
        );

        save.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        JSONObject addressObject = new JSONObject();
                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(AddAdressActivity.this);
                        alertDialogBuilder.setNegativeButton("Tutup", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface arg0, int arg1) {

                            }
                        });

                        String name = receiverName.getText().toString();
                        String phone = receiverPhone.getText().toString();
                        String address1 = receiverAddress.getText().toString();
                        String address2 = receiverAddress2.getText().toString();
                        String address3 = receiverAddress3.getText().toString();
                        String addressZipcode = zipcode.getText().toString();
                        String initial = initialAddress.getText().toString().toUpperCase();
                        int provinceIndex = region.getSelectedItemPosition();
                        int cityIndex = city.getSelectedItemPosition();
                        int districtIndex = district.getSelectedItemPosition();
                        int subdistrictIndex = subdistrict.getSelectedItemPosition();

                        if (TextUtils.isEmpty(name)) {
                            alertDialogBuilder.setMessage("Harap isi Nama Penerima");
                            AlertDialog alertDialog = alertDialogBuilder.create();
                            alertDialog.show();
                            return;
                        }

                        if (TextUtils.isEmpty(phone)) {
                            alertDialogBuilder.setMessage("Harap isi nomor telepon penerima");
                            AlertDialog alertDialog = alertDialogBuilder.create();
                            alertDialog.show();
                            return;
                        }

                        if (phone.length() < 9){
                            alertDialogBuilder.setMessage("Format nomor telepon tidak valid");
                            AlertDialog alertDialog = alertDialogBuilder.create();
                            alertDialog.show();
                            return;
                        }

                        if (TextUtils.isEmpty(address1)) {
                            alertDialogBuilder.setMessage("Harap isi Alamat Penerima");
                            AlertDialog alertDialog = alertDialogBuilder.create();
                            alertDialog.show();
                            return;
                        }

                        if(provinceIndex == 0){
                            alertDialogBuilder.setMessage("Harap pilih Provinsi");
                            AlertDialog alertDialog = alertDialogBuilder.create();
                            alertDialog.show();
                            return;
                        }

                        if(cityIndex == 0){
                            alertDialogBuilder.setMessage("Harap pilih Kabupaten/Kota");
                            AlertDialog alertDialog = alertDialogBuilder.create();
                            alertDialog.show();
                            return;
                        }

                        if(districtIndex == 0){
                            alertDialogBuilder.setMessage("Harap pilih Kecamatan");
                            AlertDialog alertDialog = alertDialogBuilder.create();
                            alertDialog.show();
                            return;
                        }

                        if(subdistrictIndex == 0){
                            alertDialogBuilder.setMessage("Harap pilih Kelurahan");
                            AlertDialog alertDialog = alertDialogBuilder.create();
                            alertDialog.show();
                            return;
                        }

                        if (TextUtils.isEmpty(addressZipcode)){
                            alertDialogBuilder.setMessage("Harap isi Kode Pos");
                            AlertDialog alertDialog = alertDialogBuilder.create();
                            alertDialog.show();
                            return;
                        }

                        runLoader();

                        if(types.equals("update") || types.equals("updateShipping")){
                            try {
                                address.put("Phone", phone);
                                address.put("Street", address1);
                                address.put("District", subdistrictArray.getJSONObject(subdistrict.getSelectedItemPosition()-1).getString("Name"));
                                address.put("ZipCode", addressZipcode);
                                address.put("IsDefault", false);
                                address.put("ProvinceId", regionArray.getJSONObject(region.getSelectedItemPosition()-1).getString("ID"));
                                address.put("Street2", address2);
                                address.put("CityId",districtArray.getJSONObject(district.getSelectedItemPosition()-1).getString("CityId"));
                                address.put("Region", cityArray.getJSONObject(city.getSelectedItemPosition()-1).getString("ID"));
                                address.put("CityLabel", districtArray.getJSONObject(district.getSelectedItemPosition()-1).getString("Name"));
                                address.put("Street3", address3);
                                address.put("ProvinceName", regionArray.getJSONObject(region.getSelectedItemPosition()-1).getString("ProvinceName"));
                                address.put("RegionName", cityArray.getJSONObject(city.getSelectedItemPosition()-1).getString("RegionName"));
                                address.put("DistrictId", subdistrictArray.getJSONObject(subdistrict.getSelectedItemPosition()-1).getString("ID"));
                                address.put("ReceiverName", name);
                                address.put("ReceiverPhone", phone);
                                address.put("IsIPP", true);

                                if (currentLong.length() > 0){
                                    address.put("Longitude", currentLong);
                                }

                                if (currentLat.length() > 0){
                                    address.put("Latitude", currentLat);
                                }

                                if (googleAddress.length() > 0){
                                    address.put("GoogleAddress", googleAddress);
                                }

                                if (etKetTambahAlamat.getText().toString().length() > 0){
                                    address.put("KetAlamat", etKetTambahAlamat.getText().toString());
                                }

                                if (initial != null) address.put("AddressTitle", initial);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            makeJsonPost(API.getInstance().getApiUpdateAddress()+"?mfp_id="+sessionManager.getResponseId(), address, "update");
                        } else if(types.equals("add") || types.equals("addShipping")){
                            try {
                                addressObject.put("ID","00000000-0000-0000-0000-000000000000");
                                addressObject.put("CustomerID", sessionManager.getUserID());
                                addressObject.put("Phone", phone);
                                addressObject.put("Street", address1);
                                addressObject.put("District", subdistrictArray.getJSONObject(subdistrict.getSelectedItemPosition()-1).getString("Name"));
                                addressObject.put("ZipCode", addressZipcode);
                                addressObject.put("IsDefault", true);
                                addressObject.put("ProvinceId", regionArray.getJSONObject(region.getSelectedItemPosition()-1).getString("ID"));
                                addressObject.put("Street2", address2);
                                addressObject.put("CityId",districtArray.getJSONObject(district.getSelectedItemPosition()-1).getString("CityId"));
                                addressObject.put("Region", cityArray.getJSONObject(city.getSelectedItemPosition()-1).getString("ID"));
                                addressObject.put("CityLabel", districtArray.getJSONObject(district.getSelectedItemPosition()-1).getString("Name"));
                                addressObject.put("Street3", address3);
                                addressObject.put("ProvinceName", regionArray.getJSONObject(region.getSelectedItemPosition()-1).getString("ProvinceName"));
                                addressObject.put("RegionName", cityArray.getJSONObject(city.getSelectedItemPosition()-1).getString("RegionName"));
                                addressObject.put("DistrictId", subdistrictArray.getJSONObject(subdistrict.getSelectedItemPosition()-1).getString("ID"));
                                addressObject.put("ReceiverName", name);
                                addressObject.put("ReceiverPhone", phone);
                                addressObject.put("DefaultAddressID", "");
                                addressObject.put("IsIPP", true);

                                if(addressLatLng != null){
                                    addressObject.put("Longitude", currentLong);
                                    addressObject.put("Latitude", currentLat);
                                }

                                addressObject.put("GoogleAddress", googleAddress);
                                addressObject.put("KetAlamat", ketAddress);

                                if (initial != null) addressObject.put("AddressTitle", initial);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            makeJsonPost(API.getInstance().getApiAddAddress()+"?mfp_id="+sessionManager.getKeyMfpId(), addressObject, "add");
                        }

                    }
                }
        );

        imagePlace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                linearSearchMap.setVisibility(View.GONE);
                relativeMap.setVisibility(View.GONE);
                btnEditMap.setVisibility(View.VISIBLE);
                btnEditMap.setText("Ubah");

                currentLong = String.valueOf(addressLatLng.longitude);
                currentLat = String.valueOf(addressLatLng.latitude);
                googleAddress = tvAlamatToko.getText().toString();
                ketAddress = etKetTambahAlamat.getText().toString();
            }
        });
    }

    public void makeJsonPost(String urlJsonObj, JSONObject jsonObject, final String type){
        System.out.println("--- object = " + jsonObject);

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                urlJsonObj, jsonObject,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(final JSONObject response) {
                        try{
                            if (response == null || response.length() == 0){
                                stopLoader();
                                Toast.makeText(AddAdressActivity.this, "Gagal terkoneksi server", Toast.LENGTH_LONG).show();
                            }else{
                                if(type.equals("add")){
                                    if(response.getString("IsSuccess").equals("true")){
                                        makeJsonObjectGet(API.getInstance().getApiGetProfile()+"?access_token="+sessionManager.getResponseId()+"&mfp_id="+sessionManager.getResponseId(), "prof");
                                    }
                                }else if(type.equals("update")){
                                    makeJsonObjectGet(API.getInstance().getApiGetProfile()+"?access_token="+sessionManager.getResponseId()+"&mfp_id="+sessionManager.getResponseId(), "prof");
                                }
                            }
                        }catch(Exception e){
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(AddAdressActivity.this, "Gagal terkoneksi server", Toast.LENGTH_LONG).show();
                stopLoader();
            }
        });

        AppController.getInstance().addToRequestQueue(jsonObjReq);
    }


    public void makeJsonObjectGet(String url, final String type){
        System.out.println("url " + type + "==" + url);
        runLoader();
        JsonArrayRequest jsonArrayReq = new JsonArrayRequest(url,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        if(type.equals("region")){
                            processRegion(response);
                        } else if(type.equals("city")){
                            processCity(response);
                        } else if(type.equals("district")){
                            processDistrict(response);
                        } else if(type.equals("subdistrict")){
                            processSubdistrict(response);
                        } else if(type.equals("zipcode")){
                            processZipcode(response);
                        } else if(type.equals("prof")){
                            processProfile(response);
                        }

                        stopLoader();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(AddAdressActivity.this, "Gagal terkoneksi server", Toast.LENGTH_LONG).show();
                stopLoader();
            }
        }, this);

        jsonArrayReq.setRetryPolicy(new DefaultRetryPolicy(60000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppController.getInstance().addToRequestQueue(jsonArrayReq);
    }

    public void makeGetJsonObject(String url, final String type){
        System.out.println("'mfp " + url);

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                url, null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        System.out.println("'mfp " + response);
                        try {
                            if (response == null || response.length() == 0){
                                stopLoader();
                                Toast.makeText(AddAdressActivity.this, "Gagal terkoneksi server", Toast.LENGTH_LONG).show();
                            }else{
                                if (type.equalsIgnoreCase("getMap")) {
                                    JSONArray results = response.getJSONArray("results");
                                    JSONObject geometry = results.getJSONObject(0).getJSONObject("geometry");
                                    JSONObject location = geometry.getJSONObject("location");

                                    currentLat = location.getString("lat");
                                    currentLong = location.getString("lng");
                                    addressLatLng = new LatLng(location.getDouble("lat"), location.getDouble("lng"));
                                    if (addressLatLng != null) {
                                        Helper.moveMapCamera(mMap, addressLatLng, 15);
                                    }
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(AddAdressActivity.this, "Gagal terkoneksi server", Toast.LENGTH_LONG).show();
            }
        });

        AppController.getInstance().addToRequestQueue(jsonObjReq);
    }

    public void processRegion(JSONArray response){
        regionArray = response;
        List<String> province = new ArrayList<>();
        province.add(" -- Pilih Provinsi -- ");

        for (int i=0; i<regionArray.length(); i++){
            try {
                province.add(regionArray.getJSONObject(i).getString("ProvinceName"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, province);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        region.setAdapter(adapter);

        try {
            if(types.equals("update") || types.equals("updateShipping")){
                String provinceId = address.getString("ProvinceId");
                for (int i=0; i<regionArray.length(); i++){
                    if(provinceId.equals(regionArray.getJSONObject(i).getString("ID"))){
                        region.setSelection(i+1);
                    }
                }
            }
        } catch (Exception e){
            e.printStackTrace();
        }

        processCity(new JSONArray());
    }

    public void processCity(JSONArray response){
        cityArray = response;
        List<String> cityList = new ArrayList<>();
        cityList.add(" -- Pilih Kabupaten/Kota -- ");

        if (response.length()>0){
            for (int i=0; i<cityArray.length(); i++){
                try {
                    cityList.add(cityArray.getJSONObject(i).getString("RegionName"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            city.setVisibility(View.VISIBLE);
        } else {
            processDistrict(new JSONArray());
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, cityList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        city.setAdapter(adapter);

        try {
            if(types.equals("update") || types.equals("updateShipping")){
                String regionId = address.getString("Region");

                for (int i=0; i<cityArray.length(); i++){
                    if(regionId.equals(cityArray.getJSONObject(i).getString("ID"))){
                        city.setSelection(i+1);
                    }
                }
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public void processDistrict(JSONArray response){
        districtArray = response;
        List<String> districtList = new ArrayList<>();
        districtList.add(" -- Pilih Kecamatan -- ");

        if (response.length() > 0){
            for (int i=0; i<districtArray.length(); i++){
                try {
                    districtList.add(districtArray.getJSONObject(i).getString("Name"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            district.setVisibility(View.VISIBLE);
        } else {
            processSubdistrict(new JSONArray());
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, districtList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        district.setAdapter(adapter);

        try {
            if(types.equals("update") || types.equals("updateShipping")){
                String districtId = address.getString("CityId");
                for (int i=0; i<districtArray.length(); i++){
                    if(districtId.equals(districtArray.getJSONObject(i).getString("CityId"))){
                        district.setSelection(i+1);
                    }
                }
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public void processSubdistrict(JSONArray response){
        subdistrictArray = response;
        List<String> subdistrictList = new ArrayList<>();
        subdistrictList.add(" -- Pilih Kelurahan -- ");

        if (response.length() > 0){
            for (int i=0; i<subdistrictArray.length(); i++){
                try {
                    subdistrictList.add(subdistrictArray.getJSONObject(i).getString("Name"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            subdistrict.setVisibility(View.VISIBLE);
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, subdistrictList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        subdistrict.setAdapter(adapter);

        try {
            if(types.equals("update") || types.equals("updateShipping")){
                String subdistrictId = address.getString("DistrictId");
                System.out.println("subdis = " + subdistrictArray.toString());

                for (int i=0; i<subdistrictArray.length(); i++){
                    if(subdistrictId.equals(subdistrictArray.getJSONObject(i).getString("ID"))){
                        subdistrict.setSelection(i+1);
                    }
                }
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public void processZipcode(JSONArray response){
        try {
            String zipCodeBefore = zipcode.getText().toString();
            if(zipCodeBefore.isEmpty()) {
                zipcode.setFocusable(false);
                zipcode.setVisibility(View.VISIBLE);
                receiverAddress.setVisibility(View.VISIBLE);
                receiverAddress2.setVisibility(View.VISIBLE);
                receiverAddress3.setVisibility(View.VISIBLE);
                zipcode.setText(response.getJSONObject(0).getString("ZipCode"));
                if (!isInitialUpdate) {
                    String url = "http://maps.googleapis.com/maps/api/geocode/json?address="
                            + subdistrict.getSelectedItem().toString() + " "
                            + district.getSelectedItem().toString() + " "
                            + city.getSelectedItem().toString() + " "
                            + region.getSelectedItem().toString() + " "
                            + response.getJSONObject(0).getString("ZipCode")
                            + " indonesia";

                    url = url.replace(" ", "%20");
                    url = url.replace("\t", "");
                    makeGetJsonObject(url, "getMap");
                }else{
                    Helper.moveMapCamera(mMap, addressLatLng, 15);
                }
            } else {
                zipcode.setText(response.getJSONObject(0).getString("ZipCode"));
                if(zipCodeBefore != response.getJSONObject(0).getString("ZipCode")){
                    String url = "http://maps.googleapis.com/maps/api/geocode/json?address="
                            + subdistrict.getSelectedItem().toString() + " "
                            + district.getSelectedItem().toString() + " "
                            + city.getSelectedItem().toString() + " "
                            + region.getSelectedItem().toString() + " "
                            + response.getJSONObject(0).getString("ZipCode")
                            + " indonesia";

                    url = url.replace(" ", "%20");
                    url = url.replace("\t", "");
                    makeGetJsonObject(url, "getMap");
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void processProfile(final JSONArray response){
        sqLiteHandler.insertProfile(response.toString());

        try {
            sessionManager.setUserID(response.getJSONObject(0).getString("ID"));
            final JSONArray address = response.getJSONObject(0).getJSONArray("Address");

            for (int i=0; i<address.length(); i++){
                if(address.getJSONObject(i).getString("IsDefault").equals("true")){
                    sqLiteHandler.insertDefaultAddress(address.getJSONObject(i).toString());
                }
            }

            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(AddAdressActivity.this);
            alertDialogBuilder.setPositiveButton("Tutup", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface arg0, int arg1) {
                    if (!types.equals("updateShipping") && !types.equals("addShipping")){
                        intent = new Intent(AddAdressActivity.this, ProfileActivity.class);
                        intent.putExtra("pageindex", "" + 1);
                        startActivity(intent);
                    }

                    finish();
                    overridePendingTransition(R.anim.right_in, R.anim.right_out);
                }
            });

            if(types.equals("update")){
                alertDialogBuilder.setMessage("Berhasil mengubah alamat");
            } else {
                alertDialogBuilder.setMessage("Berhasil menambahkan alamat");
            }

            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    @SuppressWarnings("deprecation")
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            finish();
            overridePendingTransition(R.anim.right_in, R.anim.right_out);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.right_in, R.anim.right_out);
    }

    @Override
    public void onConnected(Bundle bundle) {
        getMyLocation();
    }

    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    @Override
    public void onResult(LocationSettingsResult locationSettingsResult) {
        final Status status = locationSettingsResult.getStatus();
        switch (status.getStatusCode()) {
            case LocationSettingsStatusCodes.SUCCESS:
                getMyLocation();
                break;
            case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                try {
                    // Show the dialog by calling startResolutionForResult(), and check the result
                    // in onActivityResult().
                    status.startResolutionForResult(AddAdressActivity.this, REQUEST_CHECK_SETTINGS);
                } catch (IntentSender.SendIntentException e) {
                    //Log.i(TAG, "PendingIntent unable to execute request.");
                }
                break;
            case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                break;
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.getUiSettings().setZoomControlsEnabled(true);
        getMyLocation();
    }


    private void getMyLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            mMap.setMyLocationEnabled(true);
            LocationManager locationManagerCt = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            lastCurrentLocation = locationManagerCt.getLastKnownLocation(LocationManager.GPS_PROVIDER);

            if (lastCurrentLocation != null) {
                currentlatLng = new LatLng(lastCurrentLocation.getLatitude(), lastCurrentLocation.getLongitude());

                if(addressLatLng != null) {
                    new ReverseGeocodingTask(getBaseContext()).execute(addressLatLng);

                    mMap.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
                        @Override
                        public void onCameraChange(CameraPosition cameraPosition) {
                            currentlatLng = cameraPosition.target;
                            new ReverseGeocodingTask(getBaseContext()).execute(cameraPosition.target);
                        }
                    });

                    Helper.moveMapCamera(mMap, addressLatLng, 15);
                }else if (currentlatLng != null) {
                    new ReverseGeocodingTask(getBaseContext()).execute(currentlatLng);

                    mMap.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
                        @Override
                        public void onCameraChange(CameraPosition cameraPosition) {
                            currentlatLng = cameraPosition.target;
                            new ReverseGeocodingTask(getBaseContext()).execute(cameraPosition.target);
                        }
                    });

                    Helper.moveMapCamera(mMap, currentlatLng, 15);
                }
            } else {
                if(addressLatLng != null){
                    new ReverseGeocodingTask(getBaseContext()).execute(addressLatLng);

                    mMap.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
                        @Override
                        public void onCameraChange(CameraPosition cameraPosition) {
                            currentlatLng = cameraPosition.target;
                            new ReverseGeocodingTask(getBaseContext()).execute(cameraPosition.target);
                        }
                    });

                    Helper.moveMapCamera(mMap, addressLatLng, 15);
                }
            }
        }
    }


    private class ReverseGeocodingTask extends AsyncTask<LatLng, Void, String> {
        final Context mContext;

        public ReverseGeocodingTask(Context context) {
            super();
            mContext = context;
        }

        @Override
        protected void onPreExecute() {
            addressTextValue = "";
            tvAlamatToko.setText("");
        }

        @Override
        protected String doInBackground(LatLng... params) {
            final Locale localeUs = new Locale("en", "US");
            final Geocoder geocoder = new Geocoder(mContext, localeUs);
            double latitude = 0.0;
            double longitude = 0.0;

            String addressText = "";
//            if (types.equals("add")){
                latitude = params[0].latitude;
                longitude = params[0].longitude;
                addressLatLng = new LatLng(latitude, longitude);
//            }else{
//                try {
//                    latitude = address.getDouble("Latitude");
//                    longitude = address.getDouble("Longitude");
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//            }

            final String defaultAddress = String.format(Locale.ENGLISH, "%4.3f, %4.3f",
                    latitude, longitude);
            List<Address> addresses = null;

            try {
                addresses = geocoder.getFromLocation(latitude, longitude, 1);
            } catch (Exception e) {
                addressTextValue = defaultAddress;
                addressText = defaultAddress;
            }

            if (addresses != null && addresses.size() > 0) {
                final Address address = addresses.get(0);
                final String streetAddress = address.getAddressLine(0);

                if (!TextUtils.isEmpty(streetAddress)) {
                    addressText = addressTextValue = streetAddress;
                } else {
                    String streetAddress2 = address.getThoroughfare();
                    if (!TextUtils.isEmpty(streetAddress2)) {
                        streetAddress2 = "Jalan " + streetAddress2;
                        addressText = String.format("%s, %s",
                                streetAddress2,
                                address.getSubLocality());
                        addressTextValue = addressText;
                    } else {
                        addressText = defaultAddress;
                        addressTextValue = defaultAddress;
                    }
                }
            }

            if (TextUtils.isEmpty(addressText)) {
                addressTextValue = defaultAddress;
                addressText = defaultAddress;
            }

            return addressText;
        }

        @Override
        protected void onPostExecute(String addressText) {
            if (relativeMap.getVisibility() == View.VISIBLE){
                tvAlamatToko.setText(addressText);
            }else{
                try {
                    if(types.equals("update")){
                        if (address.getString("GoogleAddress") == null || address.getString("GoogleAddress").equals("") ||
                                address.getString("GoogleAddress").toLowerCase().equals("null")){
                            tvAlamatToko.setText("Mohon tandai lokasi pada peta");
                        }else{
                            tvAlamatToko.setText(addressText);
                        }
                    }else{
                        tvAlamatToko.setText("Mohon tandai lokasi pada peta");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        mGoogleApiClient.isConnected();
    }


    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
    }


    protected void buildLocationSettingsRequest() {
        final LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(mLocationRequest);
        mLocationSettingsRequest = builder.build();
    }


    protected void checkLocationSettings() {
        final PendingResult<LocationSettingsResult> result =
                LocationServices.SettingsApi.checkLocationSettings(
                        mGoogleApiClient,
                        mLocationSettingsRequest
                );
        result.setResultCallback(this);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode != LOCATION_PERMISSION_REQUEST_CODE) {
            return;
        }

        if (Helper.isPermissionGranted(permissions, grantResults, Manifest.permission.ACCESS_FINE_LOCATION)) {
            getMyLocation();
        } else {
            // Display the missing permission error dialog when the fragments resume.
        }
    }


    private class ReverseGeocodingTaskByZipCode extends AsyncTask<String, Void, LatLng> {
        final Context mContext;
        private String googleAddress;
        private boolean isSearch;
        private List<Address> mListAddress;

        public ReverseGeocodingTaskByZipCode(Context context, String address, final boolean isSearchMap) {
            super();
            mContext = context;
            googleAddress = address;
            isSearch = isSearchMap;
            mListAddress = null;
        }


        @Override
        protected LatLng doInBackground(String... strings) {
            LatLng resultLatLng = new LatLng(currentlatLng.latitude, currentlatLng.longitude);

            if(!TextUtils.isEmpty(googleAddress)){
                final Locale localeUs = new Locale("en", "US");
                final Geocoder geocoder = new Geocoder(mContext, localeUs);
                List<Address> addresses = null;

                try {
                    if (isSearch) {
                        mListAddress = geocoder.getFromLocationName(googleAddress, 5);
                    } else {
                        addresses = geocoder.getFromLocationName(googleAddress, 1);
                    }
                } catch (Exception e) {
                }

                if (addresses != null && addresses.size() > 0) {
                    final Address address = addresses.get(0);
                    resultLatLng = new LatLng(address.getLatitude(), address.getLongitude());
                }
            }

            return resultLatLng;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(LatLng latLng) {
            super.onPostExecute(latLng);

            if (isSearch) {
                if (mListAddress != null && mListAddress.size() > 0) {
                    linListSearch.setVisibility(View.VISIBLE);
                    searchAddressList.setAdapter(new SearchAddressAdapter(AddAdressActivity.this, mListAddress));
                } else {
                    linListSearch.setVisibility(View.GONE);
                }
            } else {
                if(latLng != null){
                    addressLatLng = latLng;
                    if (addressLatLng != null) {
                        Helper.moveMapCamera(mMap, addressLatLng, 15);
                    }
                }
            }
        }
    }


    CountDownTimer timer = new CountDownTimer(500, 1000) { // adjust the milli seconds here
        public void onTick(long millisUntilFinished) {

        }

        public void onFinish() {
            //update List
            String keyword = etSearchAddress.getText().toString();
            if (!TextUtils.isEmpty(keyword)) {
                keyword = keyword + ", Indonesia";

                ReverseGeocodingTaskByZipCode searchAddress =
                        new ReverseGeocodingTaskByZipCode(AddAdressActivity.this, keyword, true);
                searchAddress.execute();
            } else {
                linListSearch.setVisibility(View.GONE);
            }
        }
    };

    public void selectLocation(Address address){
        if(address != null){
            addressLatLng = new LatLng(address.getLatitude(), address.getLongitude());
            if(addressLatLng != null && mMap != null){
                etSearchAddress.setText("");
                linListSearch.setVisibility(View.GONE);
                Helper.moveMapCamera(mMap, addressLatLng, 15);
            }
        }
    }

    public void runLoader(){
        preloader.setVisibility(View.VISIBLE);
        setEnable(false);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }

    public void stopLoader(){
        preloader.setVisibility(View.GONE);
        setEnable(true);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }

    public void setEnable(boolean value){
        for ( int i = 0; i < linearParent.getChildCount();  i++ ){
            View view = linearParent.getChildAt(i);
            view.setEnabled(value); // Or whatever you want to do with the view.
        }
    }
}
