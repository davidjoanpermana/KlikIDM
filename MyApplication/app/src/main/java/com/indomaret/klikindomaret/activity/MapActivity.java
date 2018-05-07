package com.indomaret.klikindomaret.activity;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
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
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
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
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.indomaret.klikindomaret.API;
import com.indomaret.klikindomaret.R;
import com.indomaret.klikindomaret.adapter.AutoCompleteAdapter;
import com.indomaret.klikindomaret.adapter.InfoListStoreAdapter;
import com.indomaret.klikindomaret.app.AppController;
import com.indomaret.klikindomaret.helper.SessionManager;
import com.indomaret.klikindomaret.utils.Helper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback, ActivityCompat.OnRequestPermissionsResultCallback,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, ResultCallback<LocationSettingsResult> {
    private Toolbar toolbar;
    private SessionManager sessionManager;
    private Intent intent;
    private String storeNameString, storeCode;
    private String store = "";
    private String latitude = "";
    private String longitude = "";
    private String storeCodeString = "";
    private boolean isChangeStoreLocation, isDeliveryOrder;
    private String storeAddressString = "";
    private Marker currentMarker;
    protected Location lastCurrentLocation;
    protected LatLng currentlatLng, storeLatLng, addressLatLng;
    protected GoogleMap mMap;
    private BitmapDescriptor selectedIcon;
    protected GoogleApiClient mGoogleApiClient;
    protected LocationRequest mLocationRequest;
    protected LocationSettingsRequest mLocationSettingsRequest;
    private PlaceAutocompleteFragment autocompleteFragment;

    private static final long UPDATE_INTERVAL_IN_MILLISECONDS = 30000;
    private static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS = UPDATE_INTERVAL_IN_MILLISECONDS / 2;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 19;
    protected static final int REQUEST_CHECK_SETTINGS = 0x1;

    private LinearLayout linearCountStore, linearSearchMap, linIPPStore;
    private RelativeLayout relativeMap, preloader;
    private TextView countStoreText, countStoreName, tvInfoStore, tvAddressStore, mTitle;
    private SearchView searchView;
    private ImageView imagetrans;
    private Spinner spArea;
    private RecyclerView searchListview, infoListStore;
    private SupportMapFragment mapFragment;
    private ScrollView cartContainer;

    private JSONArray ippStoreList = new JSONArray();
    private JSONObject storeObject = new JSONObject();
    private ArrayList<Marker> markerArrayList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map_layout);

        sessionManager = new SessionManager(this);
        intent = this.getIntent();

        toolbar = (Toolbar) findViewById(R.id.app_toolbar);
        mTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
        toolbar.setTitle("");
        mTitle.setText("Cari Toko");
        setSupportActionBar(toolbar);

        linearCountStore = (LinearLayout) findViewById(R.id.linear_count_store);
        countStoreText = (TextView) findViewById(R.id.count_store_text);
        countStoreName = (TextView) findViewById(R.id.count_store_name);
        tvInfoStore = (TextView) findViewById(R.id.tvInfoToko);
        tvAddressStore = (TextView) findViewById(R.id.tvAlamatToko);
        searchListview = (RecyclerView) findViewById(R.id.search_listview);
        infoListStore = (RecyclerView) findViewById(R.id.info_list_store);
        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        spArea = (Spinner) findViewById(R.id.sp_area);
        relativeMap = (RelativeLayout) findViewById(R.id.relativeMap);
        linearSearchMap = (LinearLayout) findViewById(R.id.linear_search_map);
        preloader = (RelativeLayout) findViewById(R.id.preloader);
        imagetrans = (ImageView) findViewById(R.id.imagetrans);
        cartContainer = (ScrollView) findViewById(R.id.cart_container);
        linIPPStore = (LinearLayout) findViewById(R.id.linIPPStore);

        cartContainer.fullScroll(ScrollView.FOCUS_UP);

        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.icon_storeidm);
        Bitmap resized = Bitmap.createScaledBitmap(bitmap, 100, 60, true);

        selectedIcon = BitmapDescriptorFactory.fromBitmap(resized);

        linIPPStore.setVisibility(View.VISIBLE);
        relativeMap.setVisibility(View.VISIBLE);

        linearSearchMap.setVisibility(View.VISIBLE);
        relativeMap.setVisibility(View.VISIBLE);

        mapFragment.getMapAsync(this);
        buildGoogleApiClient();
        createLocationRequest();
        buildLocationSettingsRequest();
        checkLocationSettings();

        imagetrans.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                int action = event.getAction();
                switch (action) {
                    case MotionEvent.ACTION_DOWN:
                        cartContainer.requestDisallowInterceptTouchEvent(true);
                        return false;

                    case MotionEvent.ACTION_UP:
                        cartContainer.requestDisallowInterceptTouchEvent(false);
                        return true;

                    case MotionEvent.ACTION_MOVE:
                        cartContainer.requestDisallowInterceptTouchEvent(true);
                        return false;

                    default:
                        return true;
                }
            }
        });

        autocompleteFragment = (PlaceAutocompleteFragment) getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);
        ((EditText)autocompleteFragment.getView().findViewById(R.id.place_autocomplete_search_input)).setTextSize(15.0f);
        ((View)findViewById(R.id.place_autocomplete_search_button)).setPadding(0,0,0,0);
        autocompleteFragment.setHint("Masukan kata pencarian Anda disini...");

        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                latitude = String.valueOf(place.getLatLng().latitude);
                longitude = String.valueOf(place.getLatLng().longitude);
                storeAddressString = String.valueOf(place.getAddress());

                jsonArrayRequest(API.getInstance().getApiIPPStoreGetStoreByRegion() + "?Lat="+latitude
                        +"&Long="+longitude, "getStore");
            }

            @Override
            public void onError(Status status) {
                // TODO: Handle the error.
            }
        });

        searchView = (SearchView) findViewById(R.id.search_view);
        searchView.onActionViewExpanded();
        searchView.findViewById(android.support.v7.appcompat.R.id.search_plate).setBackgroundColor(ContextCompat.getColor(this, android.R.color.white));
        searchView.requestFocus();
        searchView.clearFocus();
        searchView.setFocusable(true);
        searchView.setIconified(false);
        searchView.requestFocusFromTouch();
        searchView.setQueryHint("Masukan kode toko...");

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.length() >= 4) {
                    searchListview.setVisibility(View.GONE);
                }else if (newText.length() >= 2) {
                    jsonArrayRequest(API.getInstance().getApiIPPStoreGetListStore() + "?StoreCode="+newText, "getListStore");
                } else {
                    searchListview.setVisibility(View.GONE);
                }

                return false;
            }
        });

        if (spArea.getSelectedItemPosition() == 0){
            autocompleteFragment.getView().setVisibility(View.VISIBLE);
            searchView.setVisibility(View.GONE);
        }else if (spArea.getSelectedItemPosition() == 1){
            autocompleteFragment.getView().setVisibility(View.GONE);
            searchView.setVisibility(View.VISIBLE);
        }

        spArea.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                infoListStore.setVisibility(View.GONE);
                linearCountStore.setVisibility(View.GONE);
                mMap.clear();

                if (spArea.getSelectedItem().toString().equals("Area Wilayah")){
                    autocompleteFragment.getView().setVisibility(View.VISIBLE);
                    searchView.setVisibility(View.GONE);
                }else if (spArea.getSelectedItem().toString().equals("Kode Toko")){
                    autocompleteFragment.getView().setVisibility(View.GONE);
                    searchView.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {}
        });

        isDeliveryOrder = false;
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
        final PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi.checkLocationSettings(
                mGoogleApiClient,
                mLocationSettingsRequest
        );
        result.setResultCallback(this);
    }

    public void jsonArrayRequest(String url, final String type){
        preloader.setVisibility(View.VISIBLE);
        JsonArrayRequest jsonArrayReq = new JsonArrayRequest(url,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        if (type.equalsIgnoreCase("getStore")) {
                            ippStoreList = new JSONArray();


                            linearCountStore.setVisibility(View.VISIBLE);
                            countStoreText.setText(response.length() + "");
                            countStoreName.setText(storeAddressString);

                            if (response != null && response.length() > 0) {
                                ippStoreList = response;
                            }

                            for (int i=0; i<ippStoreList.length(); i++){
                                try {
                                    ippStoreList.getJSONObject(i).put("StoreCode", ippStoreList.getJSONObject(i).getString("ID"));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }

                            drawMarker("wilayah");
                            preloader.setVisibility(View.GONE);
                        }else if (type.equalsIgnoreCase("getListStore")) {
                            List<String> listStore = new ArrayList<>();
                            try {
                                for (int i=0; i<response.length(); i++){
                                    response.getJSONObject(i).put("StoreCode", response.getJSONObject(i).getString("value"));

                                    listStore.add(response.getJSONObject(i).getString("value"));
                                }

                                searchListview.setVisibility(View.VISIBLE);
                                searchListview.setHasFixedSize(true);
                                searchListview.setLayoutManager(new LinearLayoutManager(MapActivity.this));

                                AutoCompleteAdapter areaAutoCompleteAdapter = new AutoCompleteAdapter(MapActivity.this, listStore, "store");
                                searchListview.setAdapter(areaAutoCompleteAdapter);

                                preloader.setVisibility(View.GONE);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }else if (type.equals("shipping")) {
                            cartResponse(response);
                            preloader.setVisibility(View.GONE);
                        }else if (type.equalsIgnoreCase("getStoreByCode")) {
                            if (response.length() > 0){
                                ippStoreList = new JSONArray();
                                ippStoreList = response;

                                linearCountStore.setVisibility(View.VISIBLE);
                                countStoreText.setText(response.length() + "");
                                countStoreName.setText(storeAddressString);

                                drawMarker("toko");
                            }else{
                                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MapActivity.this);
                                alertDialogBuilder.setTitle("KlikIndomaret");
                                alertDialogBuilder.setMessage("Toko tidak ditemukan");
                                alertDialogBuilder.setPositiveButton("Tutup", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface arg0, int arg1) {
                                    }
                                });

                                AlertDialog alertDialog = alertDialogBuilder.create();
                                alertDialog.show();
                            }
                            preloader.setVisibility(View.GONE);
                        }else if (type.equalsIgnoreCase("storebywilayah")){
                            Marker marker = mMap.addMarker(new MarkerOptions()
                                    .position(new LatLng(Double.valueOf(latitude), Double.valueOf(longitude)))
                                    .title(storeNameString)
                                    .snippet(storeAddressString)
                                    .icon(selectedIcon));

                            showMarkerMap(storeNameString, storeAddressString, marker);
                            currentMarker = marker;
                            tvInfoStore.setText(currentMarker.getTitle());
                            tvAddressStore.setText(getAddressStoreByCode(currentMarker.getSnippet()));
                            preloader.setVisibility(View.GONE);
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MapActivity.this, "Gagal terkoneksi server", Toast.LENGTH_LONG).show();
                preloader.setVisibility(View.GONE);
            }
        }, this);

        jsonArrayReq.setRetryPolicy(new DefaultRetryPolicy(60000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppController.getInstance().addToRequestQueue(jsonArrayReq);
    }

    public void setSearchStoreCode(String value){
        searchListview.setVisibility(View.GONE);
        searchView.setQuery(value, false);
        searchView.clearFocus();
        storeCodeString = value;
        storeAddressString = value;

        jsonArrayRequest(API.getInstance().getApiIPPStoreGetStoreByStoreCodeIppStore() + "?StoreCode="+searchView.getQuery(), "getStoreByCode");
    }

    public void setStoreCode(JSONObject store, int position){
        if (currentMarker != null) {
            currentMarker.setIcon(selectedIcon);
        }

        try {
            storeCode = store.getString("ID");
            storeNameString = store.getString("store") + "(" + store.getString("ID") + ")";
            storeAddressString = store.getString("address");
            latitude = store.getString("lat");
            longitude = store.getString("lang");

            store.put("Name", store.getString("store"));
            store.put("Street", store.getString("address"));
            this.store = store.toString();

            storeLatLng = new LatLng(Double.valueOf(latitude), Double.valueOf(longitude));
            Helper.moveMapCamera(mMap, storeLatLng, 13);

            jsonArrayRequest(API.getInstance().getApiShippingMethod()
                    + "?id=" + sessionManager.getCartId()
                    + "&customerID=" + sessionManager.getUserID()
                    + "&isVirtual=false"
                    + "&regionId="+sessionManager.getRegionID()
                    + "&CustomerAddressID="
                    + "&isParcelView=false"
                    + "&IsIPP=false"
                    + "&IPPServiceType=1"
                    + "&IPPStoreCode=" + storeCode
                    + "&mfp_id=" + sessionManager.getKeyMfpId(), "storebywilayah");

            cartContainer.fullScroll(ScrollView.FOCUS_UP);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void cartResponse(JSONArray response){

    }

    private void drawMarker(String type) {
        boolean selected = false;
        tvInfoStore.setText(getResources().getString(R.string.please_select_store));
        tvAddressStore.setText("");

        isChangeStoreLocation = true;
        if (currentMarker != null) {
            currentMarker.remove();
        }

        if (markerArrayList != null) {
            if (markerArrayList.size() > 0) {
                for (int i = 0; i < markerArrayList.size(); i++) {
                    markerArrayList.get(i).remove();
                }
            }
        }

        markerArrayList = new ArrayList<>();

        if (lastCurrentLocation != null) {
            storeLatLng = new LatLng(lastCurrentLocation.getLatitude(), lastCurrentLocation.getLongitude());
        }

        if (ippStoreList != null) {
            if (ippStoreList.length() > 0) {
                for (int i = 0; i < ippStoreList.length(); i++) {
                    try {
                        if (type.equals("lastStore")){
                            Marker marker = mMap.addMarker(new MarkerOptions()
                                    .position(new LatLng(ippStoreList.getJSONObject(i).getDouble("Latitude"),
                                            ippStoreList.getJSONObject(i).getDouble("Longitude")))
                                    .title(ippStoreList.getJSONObject(i).getString("Name") + "(" + ippStoreList.getJSONObject(i).getString("Code") + ")")
                                    .snippet(ippStoreList.getJSONObject(i).getString("Street"))
                                    .icon(selectedIcon));

                            showMarkerMap(ippStoreList.getJSONObject(i).getString("Name") + "(" + ippStoreList.getJSONObject(i).getString("Code") + ")",
                                    ippStoreList.getJSONObject(i).getString("Street"), marker);
//                            marker.showInfoWindow();
                            markerArrayList.add(marker);
                            selected = false;

                            storeLatLng = new LatLng(ippStoreList.getJSONObject(i).getDouble("Latitude"),
                                    ippStoreList.getJSONObject(i).getDouble("Longitude"));
                        }else{
                            Marker marker = mMap.addMarker(new MarkerOptions()
                                    .position(new LatLng(ippStoreList.getJSONObject(i).getDouble("lat"),
                                            ippStoreList.getJSONObject(i).getDouble("lang")))
                                    .title(ippStoreList.getJSONObject(i).getString("store") + "(" + ippStoreList.getJSONObject(i).getString("ID") + ")")
                                    .snippet(ippStoreList.getJSONObject(i).getString("address"))
                                    .icon(selectedIcon));

                            markerArrayList.add(marker);

                            storeLatLng = new LatLng(ippStoreList.getJSONObject(i).getDouble("lat"),
                                    ippStoreList.getJSONObject(i).getDouble("lang"));

                            if (storeCodeString.length() > 0){
                                if (ippStoreList.getJSONObject(i).getString("ID").equals(storeCodeString)){
                                    showMarkerMap(ippStoreList.getJSONObject(i).getString("Name") + "(" + ippStoreList.getJSONObject(i).getString("Code") + ")",
                                            ippStoreList.getJSONObject(i).getString("Street"), marker);
//                                    marker.showInfoWindow();

                                    setListInfoStore(ippStoreList, i+"", "storeSearch");
                                    store = ippStoreList.getJSONObject(i).toString();

                                    selected = true;
                                    Helper.moveMapCamera(mMap, storeLatLng, 13);
                                }
                            }
                        }
                    } catch (Exception e) {}
                }

                infoListStore.setVisibility(View.VISIBLE);

                if (!selected){
                    if (type.equals("wilayah") || type.equals("toko")){

                        setListInfoStore(ippStoreList, "", "storeSearch");
                    }else{

                        setListInfoStore(ippStoreList, "", "store");
                    }
                }
            }
        }

        if (!selected) Helper.moveMapCamera(mMap, storeLatLng, 13);
//        preloader.setVisibility(View.GONE);
    }

    private void getMyLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            mMap.setMyLocationEnabled(true);
            LocationManager locationManagerCt = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
            lastCurrentLocation = locationManagerCt.getLastKnownLocation(LocationManager.GPS_PROVIDER);

            if (lastCurrentLocation != null) {
                currentlatLng = new LatLng(lastCurrentLocation.getLatitude(), lastCurrentLocation.getLongitude());

                if (currentlatLng != null) {
                    new ReverseGeocodingTask(getBaseContext()).execute(currentlatLng);
                }

                mMap.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
                    @Override
                    public void onCameraChange(CameraPosition cameraPosition) {
                        currentlatLng = cameraPosition.target;
                        if (isDeliveryOrder) {
                            new ReverseGeocodingTask(getBaseContext()).execute(cameraPosition.target);
                        }
                    }
                });

                Helper.moveMapCamera(mMap, currentlatLng, 15);
            } else {
                mMap.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
                    @Override
                    public void onCameraChange(CameraPosition cameraPosition) {
                        currentlatLng = cameraPosition.target;
                        if (isDeliveryOrder) {
                            new ReverseGeocodingTask(getBaseContext()).execute(cameraPosition.target);
                        }
                    }
                });
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
            tvInfoStore.setText("Mencari alamat...");
        }

        @Override
        protected String doInBackground(LatLng... params) {
            final Locale localeUs = new Locale("en", "US");
            final Geocoder geocoder = new Geocoder(mContext, localeUs);
            double latitude = params[0].latitude;
            double longitude = params[0].longitude;
            final String defaultAddress = String.format(Locale.ENGLISH, "%4.3f, %4.3f",
                    latitude, longitude);
            List<Address> addresses = null;
            String addressText = "";
            addressLatLng = new LatLng(latitude, longitude);

            try {
                addresses = geocoder.getFromLocation(latitude, longitude, 1);
            } catch (Exception e) {
                addressText = defaultAddress;
            }

            if (addresses != null && addresses.size() > 0) {
                final Address address = addresses.get(0);
                final String streetAddress = address.getAddressLine(0);

                if (!TextUtils.isEmpty(streetAddress)) {
                } else {
                    String streetAddress2 = address.getThoroughfare();
                    if (!TextUtils.isEmpty(streetAddress2)) {
                        streetAddress2 = "Jalan " + streetAddress2;
                        addressText = String.format("%s, %s",
                                streetAddress2,
                                address.getSubLocality());
                    } else {
                        addressText = defaultAddress;
                    }
                }
            }

            if (TextUtils.isEmpty(addressText)) {
                addressText = defaultAddress;
            }

            return addressText;
        }

        @Override
        protected void onPostExecute(String addressText) {
            tvInfoStore.setText(addressText);
            tvAddressStore.setText("");
        }
    }

    private String getAddressStoreByCode(String cityName) {
        String result = "";

        if (ippStoreList != null) {
            if (ippStoreList.length() > 0) {
                for (int i = 0; i < ippStoreList.length(); i++) {
                    try {
                        if (cityName.equalsIgnoreCase(ippStoreList.getJSONObject(i).getString("Code"))) {
                            result = ippStoreList.getJSONObject(i).getString("Street");
                        }
                    } catch (Exception ignored) {

                    }
                }
            }
        }

        return result;
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onResult(@NonNull LocationSettingsResult locationSettingsResult) {
        final Status status = locationSettingsResult.getStatus();
        switch (status.getStatusCode()) {
            case LocationSettingsStatusCodes.SUCCESS:
                getMyLocation();
                break;
            case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                try {
                    status.startResolutionForResult(MapActivity.this, REQUEST_CHECK_SETTINGS);
                } catch (IntentSender.SendIntentException ignored) {
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

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                if (currentMarker != null && !isChangeStoreLocation) {
                    currentMarker.setIcon(selectedIcon);
                }

                isChangeStoreLocation = false;

                if (markerArrayList != null) {
                    if (markerArrayList.size() > 0) {
                        for (Marker marker1 : markerArrayList) {
                            if (marker1.getSnippet().equalsIgnoreCase(marker.getSnippet())) {
                                currentMarker = marker;
                                currentMarker.setIcon(selectedIcon);

                                showMarkerMap(currentMarker.getTitle(), currentMarker.getSnippet(), currentMarker);

                                tvInfoStore.setText(currentMarker.getTitle());
                                tvAddressStore.setText(currentMarker.getSnippet());

                                for (int i = 0; i < ippStoreList.length(); i++) {
                                    try {
                                        if (currentMarker.getPosition().latitude == ippStoreList.getJSONObject(i).getDouble("lat")
                                                && currentMarker.getPosition().longitude == ippStoreList.getJSONObject(i).getDouble("lang")) {

                                            setListInfoStore(ippStoreList, i+"", "storeSearch");

                                            ippStoreList.getJSONObject(i).put("Name", ippStoreList.getJSONObject(i).getString("store"));
                                            ippStoreList.getJSONObject(i).put("Street", ippStoreList.getJSONObject(i).getString("address"));
                                            store = ippStoreList.getJSONObject(i).toString();
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }

                                break;
                            }
                        }
                    }
                }

                return false;
            }
        });
    }

    public void showMarkerMap(final String title, final String description, final Marker marker){
        mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
            @Override
            public View getInfoWindow(Marker arg0) {
                return null;
            }

            @Override
            public View getInfoContents(Marker arg0) {
                View v = getLayoutInflater().inflate(R.layout.marker_map, null);

                RelativeLayout content = (RelativeLayout) v.findViewById(R.id.relative_content);
                TextView titleName = (TextView) v.findViewById(R.id.title);
                TextView desName = (TextView) v.findViewById(R.id.description);
                Button btnPilih = (Button) v.findViewById(R.id.btn_pilih);

                titleName.setText(title);
                desName.setText(description);

                content.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                });

                return v;

            }
        });

        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                intent = new Intent();
                intent.putExtra("store", store);

                try {
                    JSONObject storeObejct = new JSONObject(store);
                    storeObejct.put("Name", storeObejct.getString("store"));
                    storeObejct.put("Code", storeObejct.getString("ID"));
                    storeObejct.put("Street", storeObejct.getString("address"));
                    sessionManager.setKeyLastStore(storeObejct.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                setResult(RESULT_OK, intent);
                finish();
                overridePendingTransition(R.anim.right_in, R.anim.right_out);
            }
        });

        marker.showInfoWindow();
    }

    public void setListInfoStore(JSONArray listStore, String index, String type){
        infoListStore.setHasFixedSize(true);
        infoListStore.setLayoutManager(new LinearLayoutManager(MapActivity.this));

        InfoListStoreAdapter autoCompleteAdapter = new InfoListStoreAdapter(MapActivity.this, listStore, index, type);
        infoListStore.setAdapter(autoCompleteAdapter);
        autoCompleteAdapter.notifyDataSetChanged();
    }

    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.right_in, R.anim.right_out);
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
}
