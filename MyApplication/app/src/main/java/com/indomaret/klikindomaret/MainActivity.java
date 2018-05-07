package com.indomaret.klikindomaret;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Process;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.multidex.MultiDex;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
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
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.indomaret.klikindomaret.activity.BrandActivity;
import com.indomaret.klikindomaret.activity.CartActivity;
import com.indomaret.klikindomaret.activity.CategoryActivity;
import com.indomaret.klikindomaret.activity.CategoryHeroActivity;
import com.indomaret.klikindomaret.activity.CategoryLevel1Activity;
import com.indomaret.klikindomaret.activity.DirectLoginActivity;
import com.indomaret.klikindomaret.activity.HomeKAIActivity;
import com.indomaret.klikindomaret.activity.LoginActivity;
import com.indomaret.klikindomaret.activity.MenuProfileActivity;
import com.indomaret.klikindomaret.activity.NotificationActivity;
import com.indomaret.klikindomaret.activity.PostcodeCoverageActivity;
import com.indomaret.klikindomaret.activity.ProductActivity;
import com.indomaret.klikindomaret.activity.ProfileActivity;
import com.indomaret.klikindomaret.activity.RefundActivity;
import com.indomaret.klikindomaret.activity.ReturActivity;
import com.indomaret.klikindomaret.activity.ScannerActivity;
import com.indomaret.klikindomaret.activity.VerificationPhoneActivity;
import com.indomaret.klikindomaret.activity.VirtualCategoryActivity;
import com.indomaret.klikindomaret.activity.WebViewActivity;
import com.indomaret.klikindomaret.activity.WishlistActivity;
import com.indomaret.klikindomaret.adapter.AreaAutoCompleteAdapter;
import com.indomaret.klikindomaret.adapter.PromoBannerAdapter;
import com.indomaret.klikindomaret.adapter.SlideBrandAdapter;
import com.indomaret.klikindomaret.adapter.ViewPagerAdapter;
import com.indomaret.klikindomaret.app.AppController;
import com.indomaret.klikindomaret.app.Config;
import com.indomaret.klikindomaret.data.BaseItem;
import com.indomaret.klikindomaret.data.DataProvider;
import com.indomaret.klikindomaret.fragment.BlankFragment;
import com.indomaret.klikindomaret.fragment.PaketDataFragment;
import com.indomaret.klikindomaret.fragment.PulsaFragment;
import com.indomaret.klikindomaret.fragment.VoucherFragment;
import com.indomaret.klikindomaret.gcm.GcmIntentService;
import com.indomaret.klikindomaret.helper.SQLiteHandler;
import com.indomaret.klikindomaret.helper.SessionManager;
import com.indomaret.klikindomaret.views.BadgeDrawable;
import com.indomaret.klikindomaret.views.HeightAdjustableListView;
import com.indomaret.klikindomaret.views.LevelBeamView;
import com.indomaret.klikindomaret.views.WrapContentViewPager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import pl.openrnd.multilevellistview.ItemInfo;
import pl.openrnd.multilevellistview.MultiLevelListAdapter;
import pl.openrnd.multilevellistview.MultiLevelListView;
import pl.openrnd.multilevellistview.NestType;

public class MainActivity extends AppCompatActivity implements ViewPager.OnPageChangeListener, View.OnClickListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {
    private DrawerLayout drawer;
    public WebView mainWebview;

    private MultiLevelListView mListView;
    private boolean mAlwaysExpandend;
    boolean status = false;
    private DataProvider dataProvider;
    private Intent intent;
    private GoogleApiClient client;
    public static Context klikContext = null;
    private static final int PERMISSIONS_REQUEST_CALL_PHONE = 201;

    private int totalItemInCart;
    private String url, versionApp;
    private PackageInfo pinfo = null;
    public Menu topMenu;
    private Toolbar toolbar;

    private FragmentManager fragmentManager;
    private SessionManager sessionManager;
    private SQLiteHandler sqLiteHandler;

    //*** GCM ***//
    private String TAG = MainActivity.class.getSimpleName();
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private BroadcastReceiver mRegistrationBroadcastReceiver;
    //*** End GCM ***//

    //*** Content Management ***//
    private LinearLayout pager_indicator, linearParent;
    private RelativeLayout preloader;
    private HeightAdjustableListView searchListview;
    private Button editRegion;
    private GridView gridBrand;
    private ImageView scannerView;
    private ImageView imageDefault;
    private ImageView[] dots;
    private  List<String> searchValue;
    private TextView otherBrand;
    private SearchView searchView;
    private RecyclerView promoBanner;
    private ScrollView scrollView;

    private Handler handler = new Handler();

    private ViewPagerAdapter mAdapter;
    private WrapContentViewPager intro_images;

    private int dotsCount;
    int count = 0;

    private JSONObject objectResponse;
    private JSONArray heroBanner = null;
    private JSONArray homePageSection = null;
    private JSONArray catArray;

    private Tracker mTracker;
    private String[] deepLinkSplit;
    private Uri deepLink;

    private boolean checkVersion;
    private LinearLayout.LayoutParams params;

    private TabLayout virtualTabLayout;
    private ViewPager virtualViewPager;
    private RelativeLayout relativeListSearch;
    //***End Content Management***//

    private TextView tvUserName, historySearchText;
    private LinearLayout linFooter;

    private Spinner region, city, district, subdistrict;
    private EditText zipcode;
    private JSONArray regionArray, cityArray, districtArray, subdistrictArray;
    private JSONArray historyArray = new JSONArray();
    private JSONArray brandArrays = new JSONArray();

    protected GoogleApiClient mGoogleApiClient;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        MultiDex.install(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Obtain the shared Tracker instance.
        AppController application = (AppController) getApplication();
        mTracker = application.getDefaultTracker();
        mTracker.setScreenName("Home Page");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());

        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);

        klikContext = this;
        totalItemInCart = 0;

        fragmentManager = getSupportFragmentManager();
        sessionManager = new SessionManager(MainActivity.this);
        sessionManager.setKeyShipping(false);
        sessionManager.setKeyPlazaShipping(false);
        sessionManager.setKeyShippingPlaza(0);

        sessionManager.setKeyTotalPrice(0);
        sessionManager.setKeyTotalVoucher(0);
        sessionManager.setKeyTotalDiscount(0);
        sessionManager.setKeyTotalShippingCost(0);
        sessionManager.setKeyTotalCoupon(0);
        sessionManager.setKeyCouponList("");
        sessionManager.setKeyTokenCookie("");
        sessionManager.setkeyDate("");
        sessionManager.setKeyTime("");
        sqLiteHandler = new SQLiteHandler(this);
        System.out.println("custID = " + sessionManager.getUserID());

        //init toolbar
        toolbar = (Toolbar) findViewById(R.id.app_toolbar);
        toolbar.setLogo(R.drawable.transparantlogo);

        imageDefault = (ImageView) findViewById(R.id.image_default);
        linearParent = (LinearLayout) findViewById(R.id.linear_parent);
        historySearchText = (TextView) findViewById(R.id.text_terakhir_dilihat);
        editRegion = (Button) findViewById(R.id.btn_edit_region);

        otherBrand = (TextView) findViewById(R.id.other_brand);
        relativeListSearch = (RelativeLayout) findViewById(R.id.list_history_search);
        searchListview = (HeightAdjustableListView) findViewById(R.id.search_listview);
        promoBanner = (RecyclerView) findViewById(R.id.promo_banner);
        scrollView = (ScrollView) findViewById(R.id.scroll_view);
        gridBrand = (GridView) findViewById(R.id.grid_brand);

        scannerView = (ImageView) findViewById(R.id.scanner_view);
        scannerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ScannerActivity.class);
                startActivity(intent);
            }
        });

        searchView = (SearchView) findViewById(R.id.search_view);
        searchView.onActionViewExpanded();
        searchView.findViewById(android.support.v7.appcompat.R.id.search_plate).setBackgroundColor(ContextCompat.getColor(this, android.R.color.white));
        searchView.requestFocus();
        searchView.clearFocus();

        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(MainActivity.this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
            mGoogleApiClient.connect();
        }

        searchView.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus){
                    if (sessionManager.isLoggedIn()){
                        searchValue = new ArrayList<>();
                        jsonRequestSearch(API.getInstance().getApiSearchHistory() + "?USERID="+sessionManager.getUserID()+"&permalink=", "get");
                    }
                }else{
                    searchView.clearFocus();
                    relativeListSearch.setVisibility(View.GONE);
                    historySearchText.setVisibility(View.GONE);
                }
            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                intent = new Intent(MainActivity.this, CategoryActivity.class);
                intent.putExtra("cat", "search");
                intent.putExtra("searchKey", query);
                intent.putExtra("name", query);
                startActivity(intent);
                overridePendingTransition(R.anim.left_in, R.anim.left_out);

                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.length() >= 3) {
                    searchValue = new ArrayList<>();
                    historySearchText.setVisibility(View.GONE);
                    url = API.getInstance().getApiSearchAutoComplete() + "?key=" + newText.replace(" ", "%20") + "&mfp_id=" + sessionManager.getKeyMfpId();
                    makeJsonArrayGet(url, "autoComplate");
                } else {
                    relativeListSearch.setVisibility(View.GONE);
                    historySearchText.setVisibility(View.GONE);
                }

                return false;
            }
        });

        virtualViewPager = (ViewPager) findViewById(R.id.virtual_viewpager);
        setupViewPager(virtualViewPager);

        virtualTabLayout = (TabLayout) findViewById(R.id.virtual_tabs);
        virtualTabLayout.setupWithViewPager(virtualViewPager);

        selectedPage(0);
        final float scale = this.getResources().getDisplayMetrics().density;

        virtualViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(virtualTabLayout));
        virtualTabLayout.setOnTabSelectedListener(
                new TabLayout.OnTabSelectedListener() {
                    @Override
                    public void onTabSelected(TabLayout.Tab tab) {
                        switch (tab.getPosition()) {
                            case 0:
                                virtualViewPager.setCurrentItem(0);

                                int pixels1 = (int) (220 * scale + 0.5f);
                                params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, pixels1);
                                virtualViewPager.setLayoutParams(params);
                                break;
                            case 1:
                                virtualViewPager.setCurrentItem(1);

                                pixels1 = (int) (220 * scale + 0.5f);
                                params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, pixels1);
                                virtualViewPager.setLayoutParams(params);
                                break;
                            case 2:
                                virtualViewPager.setCurrentItem(2);

                                pixels1 = (int) (160 * scale + 0.5f);
                                params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, pixels1);
                                virtualViewPager.setLayoutParams(params);
                                break;
                            case 3:
                                virtualViewPager.setCurrentItem(3);
                                intent = new Intent(MainActivity.this, HomeKAIActivity.class);
                                intent.putExtra("from", "klikindomaret");
                                startActivity(intent);
                                overridePendingTransition(R.anim.left_in, R.anim.left_out);
                                break;
//                            case 4:
//                                virtualViewPager.setCurrentItem(3);
//                                intent = new Intent(MainActivity.this, HotelActivity.class);
//                                startActivity(intent);
//                                overridePendingTransition(R.anim.left_in, R.anim.left_out);
//                                break;
                        }
                    }

                    @Override
                    public void onTabUnselected(TabLayout.Tab tab) {
                    }

                    @Override
                    public void onTabReselected(TabLayout.Tab tab) {
                    }
                }
        );

        View view = toolbar.getChildAt(0);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                refreshHome();
            }
        });

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        intent = getIntent();
        deepLink = intent.getData();

        otherBrand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(MainActivity.this, BrandActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.left_in, R.anim.left_out);
            }
        });

        if (deepLink != null) {
            if (deepLink.toString().contains("product")) {
                intent = new Intent(MainActivity.this, ProductActivity.class);
                intent.putExtra("cat", "deep");
                intent.putExtra("data", deepLink.toString());
                startActivity(intent);
                overridePendingTransition(R.anim.left_in, R.anim.left_out);
            } else if (deepLink.toString().contains("Activation")) {
                deepLinkSplit = deepLink.toString().split("\\?");
                String[] codePin = deepLinkSplit[1].split("\\=");
                String[] token = codePin[1].split("\\&");

                intent = new Intent(MainActivity.this, DirectLoginActivity.class);
                intent.putExtra("token", token[0]);
                intent.putExtra("codePin", codePin[2]);
                intent.putExtra("manual", false);
                startActivity(intent);
                overridePendingTransition(R.anim.left_in, R.anim.left_out);
            } else if (deepLink.toString().contains("ResetPassword")) {
                android.support.v7.app.AlertDialog.Builder alertDialogBuilder = new android.support.v7.app.AlertDialog.Builder(MainActivity.this);
                alertDialogBuilder.setMessage("Untuk reset password silahkan klik link aktivasi pada email via browser");
                alertDialogBuilder.setPositiveButton("Tutup", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                    }
                });

                android.support.v7.app.AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.setCanceledOnTouchOutside(false);
                alertDialog.show();

                overridePendingTransition(R.anim.left_in, R.anim.left_out);
            } else if (deepLink.toString().contains("login")) {
                if (sessionManager.isLoggedIn()) {
                    intent = new Intent(MainActivity.this, ProfileActivity.class);
                    intent.putExtra("pageindex", "" + 0);
                    startActivity(intent);
                    overridePendingTransition(R.anim.left_in, R.anim.left_out);
                } else {
                    intent = new Intent(MainActivity.this, LoginActivity.class);
                    intent.putExtra("from", "klikindomaret");
                    startActivity(intent);
                    overridePendingTransition(R.anim.left_in, R.anim.left_out);
                }
            } else if (deepLink.toString().contains("about")) {
                intent = new Intent(MainActivity.this, WebViewActivity.class);
                intent.putExtra("url", API.getInstance().getTentangKamiUrl());
                startActivity(intent);
                overridePendingTransition(R.anim.left_in, R.anim.left_out);
            } else if (deepLink.toString().contains("faq")) {
                intent = new Intent(MainActivity.this, WebViewActivity.class);
                intent.putExtra("url", API.getInstance().getTanyaJawabUrl());
                startActivity(intent);
                overridePendingTransition(R.anim.left_in, R.anim.left_out);
            } else if (deepLink.toString().contains("/content/")) {
                deepLinkSplit = deepLink.toString().split("\\?");
                String deeplinkPermalink = deepLinkSplit[deepLinkSplit.length - 1];

                makeJsonArrayGet(API.getInstance().getApiBannerContent() + "?Permalink=" + deeplinkPermalink, "content");
            }else if (deepLink.toString().contains("category")) {
                try {
                    catArray = new JSONArray(sessionManager.getCategories());
                    deepLinkSplit = deepLink.toString().split("/");
                    String deeplinkPermalink = deepLinkSplit[deepLinkSplit.length - 1].split("\\?")[0];

                    for (int i=0; i<catArray.length(); i++){
                        if(catArray.getJSONObject(i).getString("Permalink").contains(deeplinkPermalink)){
                            sessionManager.saveSingleCategory(catArray.getJSONObject(i).toString());

                            intent = new Intent(MainActivity.this, CategoryActivity.class);
                            intent.putExtra("cat", "category");
                            startActivity(intent);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else if (deepLink.toString().contains("promo")) {
                deepLinkSplit = deepLink.toString().split("\\?");

                makeJsonArrayGet(API.getInstance().getApiBannerByUrl() + "?mfp_id=" + sessionManager.getKeyMfpId() + "&url=" + deepLinkSplit[0], "bannerurl");
            } else if (deepLink.toString().contains("how-to-order")) {
                intent = new Intent(MainActivity.this, WebViewActivity.class);
                intent.putExtra("url", API.getInstance().getCaraMemesanUrl());
                startActivity(intent);
                overridePendingTransition(R.anim.left_in, R.anim.left_out);;
            } else if (deepLink.toString().contains("customer-service")) {
                makePhoneCall();
            } else if (deepLink.toString().contains("virtualproduct")) {
                intent = new Intent(MainActivity.this, VirtualCategoryActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.left_in, R.anim.left_out);
            } else if (deepLink.toString().contains("coveragezipcode")) {
                intent = new Intent(MainActivity.this, PostcodeCoverageActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.left_in, R.anim.left_out);
            } else if (deepLink.toString().contains("customercare/retur")) {
                String url = deepLink.toString();
                String[] urlSplit = url.split("retur/");
                String fppbNo = urlSplit[1];

                intent = new Intent(MainActivity.this, ReturActivity.class);
                intent.putExtra("fppbNo", fppbNo);
                startActivity(intent);
                overridePendingTransition(R.anim.left_in, R.anim.left_out);
            } else if (deepLink.toString().contains("customercare/refund")) {
                String url = deepLink.toString();
                String[] urlSplit = url.split("fppbDetilIdList=");
                String[] fppbDetailSplit = urlSplit[1].split(";");
                String fppbDetailID = fppbDetailSplit[0];

                intent = new Intent(MainActivity.this, RefundActivity.class);
                intent.putExtra("fppbDetailID", fppbDetailID);
                startActivity(intent);
                overridePendingTransition(R.anim.left_in, R.anim.left_out);
            }
        }

        //add buttom menu to toolbar
        List<String> wording = new ArrayList<String>();
        wording.add("Cakupan Kode Pos");
        wording.add("Cara Memesan");
        wording.add("Layanan Pelanggan");

        List<Integer> images = new ArrayList<Integer>();
        images.add(R.drawable.cakupan_kode_pos);
        images.add(R.drawable.cara_memesan);
        images.add(R.drawable.layanan_pelanggan);

        //init drawer and hamburger menu btn
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

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

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();

        //*** Content Management ***//
        preloader = (RelativeLayout) findViewById(R.id.preloader);

        //*** GCM ***//
        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                // checking for type intent filter
                if (intent.getAction().equals(Config.REGISTRATION_COMPLETE)) {
                    String deviceToken = intent.getStringExtra("token");
                    sessionManager.setDeviceToken("aos" + deviceToken);
                    makeJsonObjectGet(API.getInstance().getMfpId() + "?device_token=" + sessionManager.getDeviceToken(), "mfp");
                    makeJsonArrayGet(API.getInstance().getApiAllRegion() + "?mfp_id=" + sessionManager.getKeyMfpId(), "region");

                    if (sessionManager.getCartId() != null) {
                        url = API.getInstance().getCartTotal()
                                + "?cartId=" + sessionManager.getCartId()
                                + "&customerId=" + sessionManager.getUserID()
                                + "&mfp_id=" + sessionManager.getKeyMfpId();

                        makeJsonArrayGet(url, "cart");
                    } else {
                        updateCartTotal(0);
                    }

                    loadSideMenu();
                } else if (intent.getAction().equals(Config.SENT_TOKEN_TO_SERVER)) {
                    // gcm registration id is stored in our server's MySQL
                    Toast.makeText(getApplicationContext(), "MainActivity GCM registration token is stored in server!", Toast.LENGTH_LONG).show();
                } else if (intent.getAction().equals(Config.PUSH_NOTIFICATION)) {
                    // new push notification is received
                    Toast.makeText(getApplicationContext(), "MainActivity Push notification is received!", Toast.LENGTH_LONG).show();
                }
            }
        };

        if (checkPlayServices()) {
            registerGCM();
        }

        editRegion.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        LayoutInflater inflater = (LayoutInflater) MainActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                        View view = null;

                        view = inflater.inflate(R.layout.popup_region, null);

                        zipcode = (EditText) view.findViewById(R.id.zipcode);
                        region = (Spinner) view.findViewById(R.id.region_spinner);
                        city = (Spinner) view.findViewById(R.id.city_spinner);
                        district = (Spinner) view.findViewById(R.id.district_spinner);
                        subdistrict = (Spinner) view.findViewById(R.id.subdistrict_spinner);
                        Button btnChoose = (Button) view.findViewById(R.id.btn_choose);

                        makeJsonArray(API.getInstance().getApiGetProvince()+"?mfp_id="+sessionManager.getKeyMfpId(), "region");

                        region.setOnItemSelectedListener(
                                new AdapterView.OnItemSelectedListener() {
                                    @Override
                                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                        if (position > 0){
                                            try {
                                                makeJsonArray(API.getInstance().getApiGetRegionByProvince()+"?ProvinceId="+regionArray.getJSONObject(position-1).getString("ID")+"&mfp_id="+sessionManager.getKeyMfpId(), "city");
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
                                                makeJsonArray(API.getInstance().getApiGetDsitrictByCity()+"?RegionId="+cityArray.getJSONObject(position-1).getString("ID")+"&mfp_id="+sessionManager.getKeyMfpId(), "district");
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
                                                makeJsonArray(API.getInstance().getApiGetSubdistrictByDistrict()+"?cityId="+districtArray.getJSONObject(position-1).getString("CityId")+"&mfp_id="+sessionManager.getKeyMfpId(), "subdistrict");
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
                                                makeJsonArray(API.getInstance().getApiGetZipcodebySubdistrict()+"?districtId="+subdistrictArray.getJSONObject(position-1).getString("ID")+"&mfp_id="+sessionManager.getKeyMfpId(), "zipcode");
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

                        android.support.v7.app.AlertDialog.Builder alertDialogBuilder = new android.support.v7.app.AlertDialog.Builder(MainActivity.this);
                        alertDialogBuilder.setView(view);
                        final android.support.v7.app.AlertDialog alertDialog = alertDialogBuilder.create();
                        alertDialog.setCanceledOnTouchOutside(false);
                        alertDialog.show();

                        btnChoose.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                TextView regHeader = (TextView) findViewById(R.id.region_header);

                                try {
                                    regHeader.setText(cityArray.getJSONObject(city.getSelectedItemPosition()-1).getString("RegionName"));
                                    sessionManager.setRegionName(cityArray.getJSONObject(city.getSelectedItemPosition()-1).getString("RegionName"));
                                    sessionManager.setRegionID(cityArray.getJSONObject(city.getSelectedItemPosition()-1).getString("ID"));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                                JSONObject cartObject = new JSONObject();

                                try {
                                    cartObject.put("CartId", sessionManager.getCartId());
                                    cartObject.put("RegionId", sessionManager.getRegionID());
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                makeJsonPost(API.getInstance().updateCartItemByRegion() + "?mfp_id=" + sessionManager.getKeyMfpId(), cartObject);
                                sessionManager.setKeyShipping(false);
                                sessionManager.setKeyPlazaShipping(false);

                                makeJsonArrayGet(API.getInstance().getApiHomeContent() + "?mfp_id=" + sessionManager.getKeyMfpId() + "&regionID=" + sessionManager.getRegionID()
                                        + "&positionGroup=LANDING_BANNER", "home");
                                alertDialog.dismiss();
                            }
                        });
                    }
                }
        );

        scrollView.fullScroll(ScrollView.FOCUS_UP);
    }

    public void showHistory(final JSONArray response) {
        boolean status = false;

        for (int i = 0; i < response.length(); i++) {
            try {
                if (searchValue.size() > 0){
                    for (String value:searchValue) {
                        if (value.equals(response.getJSONObject(i).getString("Keywords"))){
                            status = true;
                        }
                    }
                }

                if (!status){
                    historyArray.put(response.getJSONObject(i));
                    searchValue.add(response.getJSONObject(i).getString("Keywords"));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        jsonRequestSearch(API.getInstance().getApiSearchHistory() + "?USERID="+sessionManager.getUserID()+"&permalink=permalink", "getByPermalink");
    }

    public void showHistoryByPermalink(final JSONArray response) {
        boolean status = false;

        for (int i = 0; i < response.length(); i++) {
            try {
                if (searchValue.size() > 0){
                    for (String value:searchValue) {
                        if (value.equals(response.getJSONObject(i).getString("Keywords"))){
                            status = true;
                        }
                    }
                }

                if (!status){
                    historyArray.put(response.getJSONObject(i));
                    searchValue.add(response.getJSONObject(i).getString("Keywords"));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        showAutoCompleteSearch(null, "history");
    }

    public void showAutoCompleteSearch(final JSONArray response, final String type) {
        if (type.equals("search")){
            for (int i = 0; i < response.length(); i++) {
                try {
                    searchValue.add(response.getJSONObject(i).getString("Name"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

        if (searchValue.size() > 0) {
            relativeListSearch.setVisibility(View.VISIBLE);

            if (type.equals("search")){
                historySearchText.setVisibility(View.GONE);
            }else{
                historySearchText.setVisibility(View.VISIBLE);
            }

            AreaAutoCompleteAdapter areaAutoCompleteAdapter = new AreaAutoCompleteAdapter(MainActivity.this, searchValue);
            searchListview.setAdapter(areaAutoCompleteAdapter);
        } else {
            relativeListSearch.setVisibility(View.GONE);
            historySearchText.setVisibility(View.GONE);
        }

        searchListview.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        String value = searchValue.get(position);
                        searchView.clearFocus();

                        try {
                            if (sessionManager.isLoggedIn() && type.equals("search")){
                                String key= "";
                                try {
                                    key = URLEncoder.encode(response.getJSONObject(position).getString("Name"), "utf-8");
                                } catch (UnsupportedEncodingException e) {
                                    e.printStackTrace();
                                }

                                if (key.toLowerCase().contains("kategori")){
                                    jsonRequestSearch(API.getInstance().insertApiSearchHistory() + "?USERID="+sessionManager.getUserID()
                                            +"&key="+key
                                            +"&permalink="+response.getJSONObject(position).getString("Permalink"), "insert");
                                }
                            }

                            if (value.toLowerCase().contains("pulsa")) {
                                intent = new Intent(MainActivity.this, VirtualCategoryActivity.class);
                                startActivity(intent);
                                overridePendingTransition(R.anim.left_in, R.anim.left_out);
                            } else if (value.toLowerCase().contains("kategori")) {
                                intent = new Intent(MainActivity.this, CategoryActivity.class);
                                intent.putExtra("cat", "searchCategory");

                                if (type.equals("search")){
                                    intent.putExtra("permalink", response.getJSONObject(position).getString("Permalink"));
                                }else{
                                    intent.putExtra("permalink", historyArray.getJSONObject(position).getString("Permalink"));
                                }

                                startActivity(intent);
                                overridePendingTransition(R.anim.left_in, R.anim.left_out);
                            } else {
                                intent = new Intent(MainActivity.this, CategoryActivity.class);
                                intent.putExtra("cat", "search");
                                intent.putExtra("searchKey", value);

                                if (type.equals("search")){
                                    intent.putExtra("name", response.getJSONObject(position).getString("Name"));
                                }else{
                                    intent.putExtra("permalink", historyArray.getJSONObject(position).getString("Permalink"));
                                }

                                startActivity(intent);
                                overridePendingTransition(R.anim.left_in, R.anim.left_out);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
        );
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapterVirtual adapter = new ViewPagerAdapterVirtual(this.getSupportFragmentManager());
        adapter.addFragment(new PulsaFragment(), "Pulsa");
        adapter.addFragment(new PaketDataFragment(), "Paket Data");
        adapter.addFragment(new VoucherFragment(), "Voucher UniPin");
        adapter.addFragment(new BlankFragment(), "Tiket Kereta Api");
//        adapter.addFragment(new BlankFragment(), "Hotel");
        viewPager.setAdapter(adapter);
    }

    @Override
    public void onConnected(Bundle bundle) {
//        if (ActivityCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
//                ActivityCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(MainActivity.this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
//        }
//        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
//                ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
//        } else {
//            LocationManager locationManagerCt = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
//            Location lastCurrentLocation = locationManagerCt.getLastKnownLocation(LocationManager.GPS_PROVIDER);
//
//            if (lastCurrentLocation != null) {
//                LatLng currentlatLng = new LatLng(lastCurrentLocation.getLatitude(), lastCurrentLocation.getLongitude());
//                if (currentlatLng != null) {
//                    new ReverseGeocodingTask(getBaseContext()).execute(currentlatLng);
//                }
//            }
//        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    private class ViewPagerAdapterVirtual extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        ViewPagerAdapterVirtual(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
//            if (position == 3) {
//                return "";
//            } else {
//                return mFragmentTitleList.get(position);
//            }
            return mFragmentTitleList.get(position);
        }
    }

    public void selectedPage(int pageIndex) {
        virtualTabLayout.setScrollPosition(pageIndex, 0f, true);
        virtualViewPager.setCurrentItem(pageIndex);
    }

    public void makeJsonPost(String urlJsonObj, JSONObject jsonObject) {
        System.out.println("Url post = " + urlJsonObj);

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                urlJsonObj, jsonObject,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if (response == null || response.length() == 0){
                                Toast.makeText(MainActivity.this, "Gagal terkoneksi server", Toast.LENGTH_LONG).show();
                            }else{
                                if (response.getBoolean("IsSuccess")) {
                                    System.out.println("Sukses");
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MainActivity.this, "Gagal terkoneksi server", Toast.LENGTH_LONG).show();
            }
        });

        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(60000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppController.getInstance().addToRequestQueue(jsonObjReq);
    }

    //    method check application update
    private void checkUpdateApp() {
        try {
            pinfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            versionApp = pinfo.versionName.replace(".", "");

            //check latest app version
            String versionUrl = API.getInstance().getAPIBaseUrl() + "MobileAppsVersion/GetVersion?device=aos";

            JsonObjectRequest versionJsonObjReq = new JsonObjectRequest(Request.Method.GET, versionUrl, new JSONObject(), new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(final JSONObject response) {
                    try {
                        if (response == null || response.length() == 0){
                            Toast.makeText(MainActivity.this, "Gagal terkoneksi server", Toast.LENGTH_LONG).show();
                        }else{
                            String versionServer = response.getString("Version").replace(".", "");

                            if (Integer.valueOf(versionApp) < Integer.valueOf(versionServer)) {
                                checkVersion = true;
                                Intent intent = new Intent(Intent.ACTION_VIEW);
                                intent.setData(Uri.parse(response.getString("UpdateUrl")));
                                startActivity(intent);
                            } else {
                                checkVersion = false;
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e("error", "version error " + error.getCause());
                    Toast.makeText(MainActivity.this, "Gagal terkoneksi server", Toast.LENGTH_LONG).show();
                }
            });

            versionJsonObjReq.setRetryPolicy(new DefaultRetryPolicy(60000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            AppController.getInstance().addToRequestQueue(versionJsonObjReq);

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void jsonArrayRequest(String url, final String type){
        runLoader();
        System.out.println("update url= " + url);

        JsonArrayRequest jsonArrayReq = new JsonArrayRequest(url,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        if (response == null || response.length() == 0){
                            stopLoader();
                            Toast.makeText(MainActivity.this, "Gagal terkoneksi server", Toast.LENGTH_LONG).show();
                        }else{
                            if (type.equals("count")){
                                try {
                                    sessionManager.setKeyCountNotif(String.valueOf(response.getInt(0)));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                                stopLoader();
                            }
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MainActivity.this, "Gagal terkoneksi server", Toast.LENGTH_LONG).show();
                stopLoader();
            }
        }, this);

        jsonArrayReq.setRetryPolicy(new DefaultRetryPolicy(60000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppController.getInstance().addToRequestQueue(jsonArrayReq);
    }

    //fragment back to home
    public void backToHome() {
        fragmentManager.popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
    }

    public void setScrollOnTop(){
        scrollView.fullScroll(ScrollView.FOCUS_UP);
        scrollView.smoothScrollTo(0, 0);
    }

    @Override
    protected void onResume() {
        super.onResume();
        selectedPage(0);
        virtualViewPager.setCurrentItem(0);

//        if (ActivityCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
//                ActivityCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(MainActivity.this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
//        }

        checkUpdateApp();
        System.out.println("region name = " + sessionManager.getRegionName());

        if (!sessionManager.isLoggedIn() && sessionManager.isLoggedIn()) {
            sessionManager.setLogin(false);
        }

        if (sessionManager.isLoggedIn()){
            jsonArrayRequest(API.getInstance().getApiNotifCount()+"?customerId="+sessionManager.getUserID(), "count");
        }else{
            sessionManager.setUserID("00000000-0000-0000-0000-000000000000");
        }

        Log.i(TAG, "Setting screen name: Home");
        mTracker.setScreenName("Home Page");
        mTracker.send(new HitBuilders.ScreenViewBuilder().setNewSession().build());

        loadSideMenu();

        TextView regHeader = (TextView) findViewById(R.id.region_header);
        regHeader.setText(sessionManager.getRegionName());

        if (sessionManager.getCartId() != null) {
            url = API.getInstance().getCartTotal()
                    + "?cartId=" + sessionManager.getCartId()
                    + "&customerId=" + sessionManager.getUserID()
                    + "&mfp_id=" + sessionManager.getKeyMfpId();

            makeJsonArrayGet(url, "cart");
        } else {
            sessionManager.setCartId("00000000-0000-0000-0000-000000000000");
            updateCartTotal(0);
        }

        try {
            if (sessionManager.isLoggedIn()){
                String userProfile = sqLiteHandler.getProfile();
                JSONArray userProfileObjectArray = new JSONArray(userProfile);
                JSONObject userProfileObject = userProfileObjectArray.getJSONObject(0);

                if (!userProfileObject.getBoolean("MobileVerified")) {
                    android.support.v7.app.AlertDialog.Builder alertDialogBuilder = new android.support.v7.app.AlertDialog.Builder(MainActivity.this);
                    alertDialogBuilder.setMessage("Nomor HP belum terverifikasi. Silahkan verifikasi nomor HP Anda terlebih dahulu " +
                            "pada menu profil pengguna.");

                    alertDialogBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface arg0, int arg1) {
                            intent = new Intent(MainActivity.this, VerificationPhoneActivity.class);
                            intent.putExtra("type", "otpUser");
                            startActivity(intent);
                            overridePendingTransition(R.anim.left_in, R.anim.left_out);
                        }
                    });

                    android.support.v7.app.AlertDialog alertDialog = alertDialogBuilder.create();
                    alertDialog.show();
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // register GCM registration complete receiver
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver, new IntentFilter(Config.REGISTRATION_COMPLETE));

        // register new push message receiver
        // by doing this, the activity will be notified each time a new message arrives
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver, new IntentFilter(Config.PUSH_NOTIFICATION));
    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
        super.onPause();
    }

    //*** Side Menu ***//
    //method to load side menu content
    public void loadSideMenu() {
        System.out.println("--- load menu");
        final String categoriesUrl;

        if (sessionManager.isLoggedIn()) {
            categoriesUrl = API.getInstance().getAPIBaseUrl() + "category/GetAllCategories?mfp_id=" + sessionManager.getKeyMfpId();
        } else {
            categoriesUrl = API.getInstance().getAPIBaseUrl() + "category/GetAllCategories?device_token=" + sessionManager.getDeviceToken() + "&mfp_id=" + sessionManager.getKeyMfpId();
        }

        JsonArrayRequest jsonArrayReq = new JsonArrayRequest(categoriesUrl, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                if (response == null || response.length() == 0){
                    stopLoader();
                    Toast.makeText(MainActivity.this, "Gagal terkoneksi server", Toast.LENGTH_LONG).show();
                }else{
                    System.out.println("--- category url : " + response);

                    //store to local
                    sessionManager.saveCategories(response.toString());

                    //draw now
                    drawSideMenu(response);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MainActivity.this, "Gagal terkoneksi server", Toast.LENGTH_LONG).show();

                String categoriesResponseString = sessionManager.getCategories();
                if (categoriesResponseString != null) {
                    try {
                        drawSideMenu(new JSONArray(categoriesResponseString));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, this);

        jsonArrayReq.setRetryPolicy(new DefaultRetryPolicy(60000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppController.getInstance().addToRequestQueue(jsonArrayReq);
    }

    //method to draw side menu content
    private void drawSideMenu(JSONArray jsonArr) {
        try {
            JSONObject object = new JSONObject();
            object.put("ID", "home");
            object.put("ParentID", "mainDrawer");
            object.put("Permalink", "Home");
            object.put("Name", "Home");
            object.put("IsPackage", false);
            object.put("ProductTotal", 1);
            object.put("Level", 1);
            jsonArr.put(object);

            object = new JSONObject();
            object.put("ID", "kategoryBelanja");
            object.put("ParentID", "mainDrawer");
            object.put("Permalink", "Kategori Belanja");
            object.put("Name", "Kategori Belanja");
            object.put("IsPackage", false);
            object.put("ProductTotal", jsonArr.length());
            object.put("Level", 1);
            jsonArr.put(object);

            if (sessionManager.isLoggedIn()){
                object = new JSONObject();
                object.put("ID", "notifikasi");
                object.put("ParentID", "mainDrawer");
                object.put("Permalink", "Notifikasi");
                object.put("Name", "Notifikasi");
                object.put("IsPackage", false);
                object.put("ProductTotal", 1);
                object.put("Level", 1);
                jsonArr.put(object);

                object = new JSONObject();
                object.put("ID", "riwayatBelanja");
                object.put("ParentID", "mainDrawer");
                object.put("Permalink", "Daftar Transaksi");
                object.put("Name", "Daftar Transaksi");
                object.put("IsPackage", false);
                object.put("ProductTotal", 1);
                object.put("Level", 1);
                jsonArr.put(object);

                object = new JSONObject();
                object.put("ID", "daftarBelanja");
                object.put("ParentID", "mainDrawer");
                object.put("Permalink", "Wishlist");
                object.put("Name", "Wishlist");
                object.put("IsPackage", false);
                object.put("ProductTotal", 1);
                object.put("Level", 1);
                jsonArr.put(object);
            }

            object = new JSONObject();
            object.put("ID", "merchantCenter");
            object.put("ParentID", "mainDrawer");
            object.put("Permalink", "Merchant Center");
            object.put("Name", "Menjadi Merchant");
            object.put("IsPackage", false);
            object.put("ProductTotal", 0);
            object.put("Level", 1);
            jsonArr.put(object);

            object = new JSONObject();
            object.put("ID", "bantuan");
            object.put("ParentID", "mainDrawer");
            object.put("Permalink", "bantuan");
            object.put("Name", "Bantuan");
            object.put("IsPackage", false);
            object.put("ProductTotal", 1);
            object.put("Level", 1);
            jsonArr.put(object);

            object = new JSONObject();
            object.put("ID", "bantuanAboutIndomaret");
            object.put("ParentID", "bantuan");
            object.put("Permalink", "bantuan");
            object.put("Name", "Tentang Indomaret");
            object.put("IsPackage", false);
            object.put("ProductTotal", 1);
            object.put("Level", 2);
            jsonArr.put(object);

            object = new JSONObject();
            object.put("ID", "bantuanShopping");
            object.put("ParentID", "bantuan");
            object.put("Permalink", "bantuan");
            object.put("Name", "Cara Berbelanja");
            object.put("IsPackage", false);
            object.put("ProductTotal", 1);
            object.put("Level", 2);
            jsonArr.put(object);

            object = new JSONObject();
            object.put("ID", "bantuanPayment");
            object.put("ParentID", "bantuan");
            object.put("Permalink", "bantuan");
            object.put("Name", "Cara Pembayaran");
            object.put("IsPackage", false);
            object.put("ProductTotal", 1);
            object.put("Level", 2);
            jsonArr.put(object);

            object = new JSONObject();
            object.put("ID", "bantuanFAQ");
            object.put("ParentID", "bantuan");
            object.put("Permalink", "bantuanFAQ");
            object.put("Name", "FAQ");
            object.put("IsPackage", false);
            object.put("ProductTotal", 1);
            object.put("Level", 2);
            jsonArr.put(object);

            object = new JSONObject();
            object.put("ID", "bantuanKebijakan");
            object.put("ParentID", "bantuan");
            object.put("Permalink", "bantuanKebijakan");
            object.put("Name", "Kebijakan Pengembalian");
            object.put("IsPackage", false);
            object.put("ProductTotal", 1);
            object.put("Level", 2);
            jsonArr.put(object);

            object = new JSONObject();
            object.put("ID", "bantuanLayanan");
            object.put("ParentID", "bantuan");
            object.put("Permalink", "bantuanLayanan");
            object.put("Name", "Layanan Pelanggan");
            object.put("IsPackage", false);
            object.put("ProductTotal", 1);
            object.put("Level", 2);
            jsonArr.put(object);

            object = new JSONObject();
            object.put("ID", "bantuanKodePos");
            object.put("ParentID", "bantuan");
            object.put("Permalink", "bantuanKodePos");
            object.put("Name", "Cakupan Kode Pos");
            object.put("IsPackage", false);
            object.put("ProductTotal", 1);
            object.put("Level", 2);
            jsonArr.put(object);

            object = new JSONObject();
            object.put("ID", "bantuanKebijakanPrivasi");
            object.put("ParentID", "bantuan");
            object.put("Permalink", "bantuanKebijakanPrivasi");
            object.put("Name", "Kebijakan Privasi");
            object.put("IsPackage", false);
            object.put("ProductTotal", 1);
            object.put("Level", 2);
            jsonArr.put(object);

            object = new JSONObject();
            object.put("ID", "bantuanPersyaratan");
            object.put("ParentID", "bantuan");
            object.put("Permalink", "bantuanPersyaratan");
            object.put("Name", "Persyaratan dan Ketentuan");
            object.put("IsPackage", false);
            object.put("ProductTotal", 1);
            object.put("Level", 2);
            jsonArr.put(object);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        for (int i=0; i<jsonArr.length(); i++){
            try {
                if (jsonArr.getJSONObject(i).getString("Name").equals("Yoi5")){
                    jsonArr.getJSONObject(i).put("Level", "-1");
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        dataProvider = new DataProvider(jsonArr, "menu");

        TextView versionName = (TextView) findViewById(R.id.version_name);
        versionName.setText("Versi " + pinfo.versionName);
        //init drawer list
        mListView = (MultiLevelListView) findViewById(R.id.listView);

        setAlwaysExpanded(mAlwaysExpandend);
        setMultipleExpanding(false);

        //check if header existed
        View header_view = mListView.mListView.findViewById(R.id.header_menu);

        //add header buttons
        if (header_view == null) {
            header_view = getLayoutInflater().inflate(R.layout.header_layout_kai, null, false);
            mListView.mListView.addHeaderView(header_view);
        }

        //check if footer existed
        View footer_view = mListView.mListView.findViewById(R.id.footer_menu);

        //new Footer View
        if (footer_view == null) {
            footer_view = getLayoutInflater().inflate(R.layout.new_footer_main_drawer, null);
            linFooter = (LinearLayout) footer_view.findViewById(R.id.footer_menu);
            linFooter.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Logout();
                }
            });
            mListView.mListView.addFooterView(footer_view);
        }


        //new Header View
        tvUserName = (TextView) header_view.findViewById(R.id.text_sign_in);
        LinearLayout linHeader = (LinearLayout) header_view.findViewById(R.id.header_menu);

        //buttonLogin
        linHeader.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!checkVersion) {
                    if (sessionManager.isLoggedIn()) {
                        intent = new Intent(MainActivity.this, MenuProfileActivity.class);
                        startActivity(intent);
                        overridePendingTransition(R.anim.left_in, R.anim.left_out);
                    } else {
                        intent = new Intent(MainActivity.this, LoginActivity.class);
                        intent.putExtra("from", "klikindomaret");
                        startActivity(intent);
                        overridePendingTransition(R.anim.left_in, R.anim.left_out);
                    }

                    drawer.closeDrawer(GravityCompat.START);
                }
            }
        });

        if(sessionManager != null){
            if(sessionManager.isLoggedIn()){
                StringBuilder username = new StringBuilder();
                String[] name = sessionManager.getUsername().split(" ");

                for (int i=0; i<name.length; i++){
                    if (i == 0 ){
                        username.append(name[i].substring(0,1).toUpperCase() + name[i].substring(1));
                    }else{
                        username.append(" " + name[i].substring(0,1).toUpperCase() + name[i].substring(1));
                    }
                }

                tvUserName.setText(username);
                linFooter.setVisibility(View.VISIBLE);
            }else {
                tvUserName.setText("Masuk / Daftar");
                linFooter.setVisibility(View.GONE);
            }
        }

        final ListAdapter listAdapter = new ListAdapter();
        mListView.setAdapter(listAdapter);

        //remove divider between rows
        mListView.mListView.setDivider(null);
        mListView.mListView.setDividerHeight(0);

        listAdapter.setDataItems(dataProvider.getInitialItems());
    }

    //adapter fol multilevellistview
    private class ListAdapter extends MultiLevelListAdapter {
        private class ViewHolder {
            TextView nameView, notifText;
            ImageView arrowView;
            LevelBeamView levelBeamView;
            RelativeLayout bgView;
            ImageView imageIcon;
        }

        @Override
        public List<?> getSubObjects(Object object) {
            return MainActivity.this.dataProvider.getSubItems((BaseItem) object);
        }

        @Override
        public boolean isExpandable(Object object) {
            return DataProvider.isExpandable((BaseItem) object);
        }

        @Override
        public View getViewForObject(final Object object, View convertView, final ItemInfo itemInfo) {
            final ViewHolder viewHolder;

            if (convertView == null) {
                viewHolder = new ViewHolder();
                convertView = LayoutInflater.from(MainActivity.this).inflate(R.layout.data_item_menu, null);

                viewHolder.nameView = (TextView) convertView.findViewById(R.id.dataItemName);
                viewHolder.arrowView = (ImageView) convertView.findViewById(R.id.dataItemArrow);
                viewHolder.levelBeamView = (LevelBeamView) convertView.findViewById(R.id.dataItemLevelBeam);
                viewHolder.bgView = (RelativeLayout) convertView.findViewById(R.id.bg_view);
                viewHolder.imageIcon = (ImageView) convertView.findViewById(R.id.imageIcon);
                viewHolder.notifText = (TextView) convertView.findViewById(R.id.notifText);

                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            viewHolder.nameView.setText(((BaseItem) object).getName());

            if (itemInfo.isExpandable() && !mAlwaysExpandend) {
                viewHolder.arrowView.setVisibility(View.VISIBLE);
                viewHolder.arrowView.setImageResource(itemInfo.isExpanded() ? R.drawable.arrow_up : R.drawable.arrow_down);
            } else {
                viewHolder.arrowView.setVisibility(View.GONE);
            }

            try {
                if (((BaseItem) object).getCategoryObject().getString("Name").equalsIgnoreCase("Home")){
                    viewHolder.imageIcon.setImageResource(R.drawable.home);
                    viewHolder.imageIcon.getLayoutParams().height = 80;
                    viewHolder.imageIcon.getLayoutParams().width = 80;
                }else if (((BaseItem) object).getCategoryObject().getString("Name").equalsIgnoreCase("Kategori Belanja")){
                    viewHolder.imageIcon.setImageResource(R.drawable.image_category);
                    viewHolder.imageIcon.getLayoutParams().height = 80;
                    viewHolder.imageIcon.getLayoutParams().width = 80;
                }else if (((BaseItem) object).getCategoryObject().getString("Name").equalsIgnoreCase("Daftar Transaksi")){
                    viewHolder.imageIcon.setImageResource(R.drawable.category);
                    viewHolder.imageIcon.getLayoutParams().height = 80;
                    viewHolder.imageIcon.getLayoutParams().width = 80;
                }else if (((BaseItem) object).getCategoryObject().getString("Name").equalsIgnoreCase("Wishlist")){
                    viewHolder.imageIcon.setImageResource(R.drawable.daftar_belanja);
                    viewHolder.imageIcon.getLayoutParams().height = 80;
                    viewHolder.imageIcon.getLayoutParams().width = 80;
                }else if (((BaseItem) object).getCategoryObject().getString("Name").equalsIgnoreCase("Menjadi Merchant")){
                    viewHolder.imageIcon.setImageResource(R.drawable.merchant);
                    viewHolder.imageIcon.getLayoutParams().height = 80;
                    viewHolder.imageIcon.getLayoutParams().width = 80;
                }else if (((BaseItem) object).getCategoryObject().getString("Name").equalsIgnoreCase("Bantuan")){
                    viewHolder.imageIcon.setImageResource(R.drawable.bantuan);
                    viewHolder.imageIcon.getLayoutParams().height = 80;
                    viewHolder.imageIcon.getLayoutParams().width = 80;
                } else if (((BaseItem) object).getCategoryObject().getString("Name").equalsIgnoreCase("Notifikasi")){
                    viewHolder.imageIcon.setImageResource(R.drawable.notification);
                    viewHolder.imageIcon.getLayoutParams().height = 80;
                    viewHolder.imageIcon.getLayoutParams().width = 80;

                    if (sessionManager.getKeyCountNotif().equals("0")){
                        viewHolder.notifText.setVisibility(View.GONE);
                        viewHolder.notifText.setText(sessionManager.getKeyCountNotif());
                    }else{
                        viewHolder.notifText.setVisibility(View.VISIBLE);
                        viewHolder.notifText.setText(sessionManager.getKeyCountNotif());
                    }
                } else{
                    viewHolder.imageIcon.setVisibility(View.VISIBLE);
                    viewHolder.imageIcon.getLayoutParams().height = 0;
                    viewHolder.imageIcon.getLayoutParams().width = 0;
                    viewHolder.notifText.setVisibility(View.GONE);
                    viewHolder.imageIcon.setImageResource(R.drawable.beranda);
                }

            }catch (Exception ignored){
            }

            //set level background color
            viewHolder.levelBeamView.setLevel(itemInfo.getLevel());
            viewHolder.bgView.setBackgroundColor(getColor(getColorResIdForLevel(itemInfo.getLevel())));

            viewHolder.nameView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View arg0, MotionEvent arg1) {
                    if (itemInfo.getLevel() == 0 && itemInfo.isExpandable()){
                        return false;
                    }else{
                        switch (arg1.getAction()) {
                            case MotionEvent.ACTION_UP: {
                                final JSONObject item = ((BaseItem) object).getCategoryObject();

                                if (item != null) {
                                    gotoPage(item);
                                }

                                break;
                            }
                            case MotionEvent.ACTION_CANCEL: {
                                break;
                            }
                        }

                        return true;
                    }
                }
            });

            return convertView;
        }

        private int getColor(int colorResId) {
            return ContextCompat.getColor(klikContext, colorResId);
        }

        private int getColorResIdForLevel(int level) {
            switch (level) {
                case 0:
                    return R.color.level_0a;
                case 1:
                    return R.color.level_1a;
                case 2:
                    return R.color.level_2a;
                case 3:
                    return R.color.level_3a;
                case 4:
                    return R.color.level_4a;
                case 5:
                    return R.color.level_5a;
                default:
                    return R.color.level_defaulta;
            }
        }
    }
    //*** End Side Menu ***//

    private void gotoPage(JSONObject item) {
        try {
            if(item.getString("ParentID").equalsIgnoreCase("mainDrawer") ||
                    item.getString("ParentID").equalsIgnoreCase("bantuan")){
                switch (item.getString("ID")){
                    case "home" :
                        refreshHome();
                        drawer.closeDrawer(GravityCompat.START);
                        break;
                    case "notifikasi" :
                        intent = new Intent(MainActivity.this, NotificationActivity.class);
                        startActivity(intent);
                        overridePendingTransition(R.anim.left_in, R.anim.left_out);
                        drawer.closeDrawer(GravityCompat.START);
                        break;
                    case "riwayatBelanja" :
                        intent = new Intent(MainActivity.this, ProfileActivity.class);
                        intent.putExtra("pageindex", "" + 2);
                        startActivity(intent);
                        overridePendingTransition(R.anim.left_in, R.anim.left_out);
                        drawer.closeDrawer(GravityCompat.START);
                        break;
                    case "daftarBelanja" :
                        intent = new Intent(MainActivity.this, WishlistActivity.class);
                        startActivity(intent);
                        overridePendingTransition(R.anim.left_in, R.anim.left_out);
                        drawer.closeDrawer(GravityCompat.START);
                        break;
                    case "merchantCenter" :
                        break;
                    case "bantuanAboutIndomaret" :
                        intent = new Intent(MainActivity.this, WebViewActivity.class);
                        intent.putExtra("url", API.getInstance().getTentangKamiUrl());
                        startActivity(intent);
                        overridePendingTransition(R.anim.left_in, R.anim.left_out);
                        drawer.closeDrawer(GravityCompat.START);
                        break;
                    case "bantuanShopping" :
                        intent = new Intent(MainActivity.this, WebViewActivity.class);
                        intent.putExtra("url", API.getInstance().getCaraMemesanUrl());
                        startActivity(intent);
                        overridePendingTransition(R.anim.left_in, R.anim.left_out);
                        drawer.closeDrawer(GravityCompat.START);
                        break;
                    case "bantuanPayment" :
                        intent = new Intent(MainActivity.this, WebViewActivity.class);
                        intent.putExtra("url", API.getInstance().getInfoPaymentUrl());
                        startActivity(intent);
                        overridePendingTransition(R.anim.left_in, R.anim.left_out);
                        drawer.closeDrawer(GravityCompat.START);
                        break;
                    case "bantuanFAQ" :
                        intent = new Intent(MainActivity.this, WebViewActivity.class);
                        intent.putExtra("url", API.getInstance().getTanyaJawabUrl());
                        startActivity(intent);
                        overridePendingTransition(R.anim.left_in, R.anim.left_out);
                        drawer.closeDrawer(GravityCompat.START);
                        break;
                    case "bantuanKebijakan" :
                        intent = new Intent(MainActivity.this, WebViewActivity.class);
                        intent.putExtra("url", API.getInstance().getKebijakanPengembalianUrl());
                        startActivity(intent);
                        overridePendingTransition(R.anim.left_in, R.anim.left_out);
                        drawer.closeDrawer(GravityCompat.START);
                        break;
                    case "bantuanLayanan" :
                        makePhoneCall();
                        drawer.closeDrawer(GravityCompat.START);
                        break;
                    case "bantuanKodePos" :
                        intent = new Intent(MainActivity.this, PostcodeCoverageActivity.class);
                        startActivity(intent);
                        overridePendingTransition(R.anim.left_in, R.anim.left_out);
                        drawer.closeDrawer(GravityCompat.START);
                        break;
                    case "bantuanKebijakanPrivasi" :
                        intent = new Intent(MainActivity.this, WebViewActivity.class);
                        intent.putExtra("url", API.getInstance().getKebijakanUrl());
                        startActivity(intent);
                        overridePendingTransition(R.anim.left_in, R.anim.left_out);
                        drawer.closeDrawer(GravityCompat.START);
                        break;
                    case "bantuanPersyaratan" :
                        intent = new Intent(MainActivity.this, WebViewActivity.class);
                        intent.putExtra("url", API.getInstance().getSyaratDanKetentuanUrl());
                        startActivity(intent);
                        overridePendingTransition(R.anim.left_in, R.anim.left_out);
                        drawer.closeDrawer(GravityCompat.START);
                        break;
                }
            }else if (item.getString("Level").equals("2")){
                if (item.getString("Permalink").toLowerCase().contains("pulsa")) {
                    intent = new Intent(MainActivity.this, VirtualCategoryActivity.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.left_in, R.anim.left_out);
                    drawer.closeDrawer(GravityCompat.START);
                }else{
                    intent = new Intent(MainActivity.this, CategoryLevel1Activity.class);
                    intent.putExtra("permalink", item.getString("Permalink"));
                    intent.putExtra("name", item.getString("Name"));
                    startActivity(intent);
                    overridePendingTransition(R.anim.left_in, R.anim.left_out);
                    drawer.closeDrawer(GravityCompat.START);
                }
            } else {
                sessionManager.saveSingleCategory(item.toString());

                if (item.getString("Permalink").toLowerCase().contains("pulsa")) {
                    intent = new Intent(MainActivity.this, VirtualCategoryActivity.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.left_in, R.anim.left_out);
                    drawer.closeDrawer(GravityCompat.START);
                } else {
                    intent = new Intent(MainActivity.this, CategoryActivity.class);
                    intent.putExtra("cat", "category");
                    startActivity(intent);
                    overridePendingTransition(R.anim.left_in, R.anim.left_out);
                    drawer.closeDrawer(GravityCompat.START);
                }
            }
        }catch (Exception ignored){

        }
    }

    public void makePhoneCall() {
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse(API.getInstance().getLayananPelangganUrl()));
        startActivity(intent);
    }

    public static View getToolbarLogoIcon(Toolbar toolbar) {
        //check if contentDescription previously was set
        boolean hadContentDescription = android.text.TextUtils.isEmpty(toolbar.getLogoDescription());
        String contentDescription = String.valueOf(!hadContentDescription ? toolbar.getLogoDescription() : "logoContentDescription");
        toolbar.setLogoDescription(contentDescription);
        ArrayList<View> potentialViews = new ArrayList<View>();

        //find the view based on it's content description, set programatically or with android:contentDescription
        toolbar.findViewsWithText(potentialViews, contentDescription, View.FIND_VIEWS_WITH_CONTENT_DESCRIPTION);

        //Nav icon is always instantiated at this point because calling setLogoDescription ensures its existence
        View logoIcon = null;
        if (potentialViews.size() > 0) {
            logoIcon = potentialViews.get(0);
        }

        //Clear content description if not previously present
        if (hadContentDescription)
            toolbar.setLogoDescription(null);

        return logoIcon;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_REQUEST_CALL_PHONE: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    makePhoneCall();
                } else {
                    Toast.makeText(this, "Permission to make a call denied", Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    public void updateCartTotal(int total) {
        totalItemInCart = total;
        this.invalidateOptionsMenu();
    }

    @Override
    @SuppressWarnings("deprecation")
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        this.topMenu = menu;

        MenuItem itemCart = menu.findItem(R.id.keranjang_top_btn);
        LayerDrawable icon = (LayerDrawable) itemCart.getIcon();
        setBadgeCount(this, icon, "" + totalItemInCart);

        return true;
    }

    public static void setBadgeCount(Context context, LayerDrawable icon, String count) {
        BadgeDrawable badge;
        Drawable reuse = icon.findDrawableByLayerId(R.id.ic_badge);
        if (reuse != null && reuse instanceof BadgeDrawable) {
            badge = (BadgeDrawable) reuse;
        } else {
            badge = new BadgeDrawable(context);
        }

        badge.setCount(count);
        icon.mutate();
        icon.setDrawableByLayerId(R.id.ic_badge, badge);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.keranjang_top_btn) {
            if (totalItemInCart == 0) {
                android.support.v7.app.AlertDialog.Builder alertDialogBuilder = new android.support.v7.app.AlertDialog.Builder(this);
                alertDialogBuilder.setMessage("Keranjang belanja Anda kosong.");
                alertDialogBuilder.setNegativeButton("Mulai Belanja", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                    }
                });

                android.support.v7.app.AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
            }  else if(sessionManager.isLoggedIn()){
                sessionManager.setEmptyProd(null);
                intent = new Intent(MainActivity.this, CartActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.top_in, R.anim.top_out);
            } else {
                intent = new Intent(MainActivity.this, LoginActivity.class);
                intent.putExtra("from", "klikindomaret");
                startActivity(intent);
                overridePendingTransition(R.anim.left_in, R.anim.left_out);
            }

            return true;
        } else if (id == android.R.id.home) {

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void setAlwaysExpanded(boolean alwaysExpanded) {
        mAlwaysExpandend = alwaysExpanded;
        mListView.setAlwaysExpanded(alwaysExpanded);
    }

    private void setMultipleExpanding(boolean multipleExpanding) {
        mListView.setNestType(multipleExpanding ? NestType.MULTIPLE : NestType.SINGLE);
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            switch (keyCode) {
                case KeyEvent.KEYCODE_BACK:
                    if (drawer.isDrawerOpen(GravityCompat.START)) {
                        drawer.closeDrawer(GravityCompat.START);
                    } else if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
                        getSupportFragmentManager().popBackStackImmediate();
                    } else {
                        exitApp();
                    }
                    return true;
            }
        }

        return super.onKeyDown(keyCode, event);
    }

    //ask user to exist app
    private void exitApp() {
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == DialogInterface.BUTTON_POSITIVE) {
                    onDestroy();

                    finish();
                    System.exit(0);
                }
            }
        };
        AlertDialog.Builder builder = new AlertDialog.Builder(klikContext);
        builder.setMessage("Yakin untuk keluar aplikasi KlikIndomaret?").setPositiveButton("Ya", dialogClickListener)
                .setNegativeButton("Tidak", dialogClickListener).show();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        int id = android.os.Process.myPid();
        Process.sendSignal(id, Process.SIGNAL_KILL);
        android.os.Process.killProcess(id);
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Main Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://com.indomaret.klikindomaret/http/host/path")
        );

        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Main Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://com.indomaret.klikindomaret/http/host/path")
        );

        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("deprecation")
    public boolean isConnectedToInternet() {
        ConnectivityManager conMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return conMgr.getNetworkInfo(0).getState() == NetworkInfo.State.CONNECTED || conMgr.getNetworkInfo(1).getState() == NetworkInfo.State.CONNECTING;

    }

    //*** GCM ***//
    // starting the service to register with GCM
    private void registerGCM() {
        Intent intent = new Intent(this, GcmIntentService.class);
        intent.putExtra("key", "register");
        startService(intent);
    }

    //method check play services installed on device
    private boolean checkPlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);

        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(this, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Log.i(TAG, "This device is not supported. Google Play Services not installed!");
                Toast.makeText(getApplicationContext(), "This device is not supported. Google Play Services not installed!", Toast.LENGTH_LONG).show();
                finish();
            }

            return false;
        }

        return true;
    }
    //*** End GCM ***//

    //***Start Content Management***//
    //set timer banner
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if (count <= heroBanner.length()) {
                intro_images.setCurrentItem(count);
                count++;
            } else {
                count = 0;
                intro_images.setCurrentItem(count);
            }
            handler.postDelayed(this, 5000);
        }
    };

    public void refreshHome() {
        runLoader();
        makeJsonArrayGet(API.getInstance().getApiHomeContent() + "?mfp_id=" + sessionManager.getKeyMfpId() + "&regionID=" + sessionManager.getRegionID()
                + "&positionGroup=LANDING_BANNER", "home");
    }

    public void makeJsonObjectGet(String url, final String type) {
        System.out.println("'mfp " + url);

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                url, null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        System.out.println("'mfp " + response);
                        if (response == null || response.length() == 0){
                            stopLoader();
                            Toast.makeText(MainActivity.this, "Gagal terkoneksi server", Toast.LENGTH_LONG).show();
                        }else{
                            try {
                                if (type.equals("mfp") && !sessionManager.isLoggedIn()) {
                                    sessionManager.setKeyMfpId(response.getString("Message"));
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                Toast.makeText(MainActivity.this, "Gagal terkoneksi server", Toast.LENGTH_LONG).show();
            }
        });

        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(60000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppController.getInstance().addToRequestQueue(jsonObjReq);
    }

    public void makeJsonArray(String url, final String type){
        System.out.println("url " + type + "==" + url);

        JsonArrayRequest jsonArrayReq = new JsonArrayRequest(url,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        if (response == null || response.length() == 0){
                            stopLoader();
                            Toast.makeText(MainActivity.this, "Gagal terkoneksi server", Toast.LENGTH_LONG).show();
                        }else{
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
                            }
                        }

                        stopLoader();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MainActivity.this, "Gagal terkoneksi server", Toast.LENGTH_LONG).show();
                stopLoader();
            }
        }, this);

        jsonArrayReq.setRetryPolicy(new DefaultRetryPolicy(60000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppController.getInstance().addToRequestQueue(jsonArrayReq);
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
        } else {
            processDistrict(new JSONArray());
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, cityList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        city.setAdapter(adapter);
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
        } else {
            processSubdistrict(new JSONArray());
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, districtList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        district.setAdapter(adapter);
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
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, subdistrictList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        subdistrict.setAdapter(adapter);
    }

    public void processZipcode(JSONArray response){
        try {
            zipcode.setText(response.getJSONObject(0).getString("ZipCode"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    //get home data
    public void makeJsonArrayGet(final String urlJsonObj, final String type) {
        System.out.println("region " + urlJsonObj);

        JsonArrayRequest jsonArrayReq = new JsonArrayRequest(urlJsonObj,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        if (response == null || response.length() == 0){
                            stopLoader();
                            Toast.makeText(MainActivity.this, "Gagal terkoneksi server", Toast.LENGTH_LONG).show();
                        }else{
                            try {
                                switch (type) {
                                    case "home":
                                        processResponse(response);
                                        break;
                                    case "region":
                                        processAllRegion(response);
                                        break;
                                    case "cart":
                                        if (response.length() > 0) updateCartTotal(response.getInt(0));
                                        break;
                                    case "category":
                                        sessionManager.saveSingleCategory(response.getJSONObject(0).toString());
                                        intent = new Intent(MainActivity.this, CategoryActivity.class);
                                        intent.putExtra("cat", "category");
                                        startActivity(intent);
                                        overridePendingTransition(R.anim.left_in, R.anim.left_out);
                                        break;
                                    case "bannerurl": {
                                        String[] url = urlJsonObj.split("url=");

                                        if (response.length() > 0) {
                                            getByBannerUrl(response);
                                        } else {
                                            makeJsonArrayGet(API.getInstance().getApiProdGroupByUrl() + "?mfp_id=" + sessionManager.getKeyMfpId() + "&url=" + url[1], "prodGroup");
                                        }
                                        break;
                                    }
                                    case "prodGroup": {
                                        String[] url = urlJsonObj.split("url=");

                                        if (response.length() > 0) {
                                            getByProdGropuUrl(response);
                                        } else {
                                            makeJsonArrayGet(API.getInstance().getApiByAliasUrl() + "?mfp_id=" + sessionManager.getKeyMfpId() + "&url=" + url[1], "aliasurl");
                                        }
                                        break;
                                    }
                                    case "aliasurl": {
                                        String[] url = urlJsonObj.split("url=");

                                        if (response.length() > 0) {
                                            getAliasUrl(response);
                                        }
                                        break;
                                    }
                                    case "autoComplate":
                                        showAutoCompleteSearch(response, "search");
                                        break;
                                    case "content":
                                        JSONObject promo = new JSONObject();

                                        promo.put("MetaTitle", response.getJSONObject(0).getString("MetaTitle"));
                                        promo.put("Description", response.getJSONObject(0).getString("MetaTitle"));
                                        promo.put("TargetUrl", deepLink);

                                        intent = new Intent(MainActivity.this, CategoryHeroActivity.class);
                                        intent.putExtra("promo", promo.toString());
                                        startActivity(intent);
                                        overridePendingTransition(R.anim.left_in, R.anim.left_out);
                                        break;
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MainActivity.this, "Gagal terkoneksi server", Toast.LENGTH_LONG).show();
                stopLoader();
            }
        }, this);

        jsonArrayReq.setRetryPolicy(new DefaultRetryPolicy(60000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppController.getInstance().addToRequestQueue(jsonArrayReq);
    }

    public void jsonRequestSearch(String url, final String type) {
        StringRequest strReq = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONArray dataList = new JSONArray(response);
                    if (response == null || response.length() == 0){
                        stopLoader();
                        Toast.makeText(MainActivity.this, "Gagal terkoneksi server", Toast.LENGTH_LONG).show();
                    }else{
                        if (type.equals("get")){
                            if (dataList.length() > 0){
                                showHistory(dataList);
                            }else{
                                jsonRequestSearch(API.getInstance().getApiSearchHistory() + "?USERID="+sessionManager.getUserID()+"&permalink=permalink", "getByPermalink");
                            }
                        }else if (type.equals("getByPermalink")){
                            showHistoryByPermalink(dataList);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MainActivity.this, "Gagal terkoneksi server", Toast.LENGTH_LONG).show();
                stopLoader();
            }
        });

        AppController.getInstance().addToRequestQueue(strReq);
    }

    public void stringRequest(String url) {
        StringRequest strReq = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                JSONArray jObject = null;
                try {
                    if (response == null || response.length() == 0){
                        stopLoader();
                        Toast.makeText(MainActivity.this, "Gagal terkoneksi server", Toast.LENGTH_LONG).show();
                    }else{
                        jObject = new JSONArray(response);

//                    if (jObject.getJSONObject(0).getBoolean("IsSubscribe")){
                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainActivity.this);
                        alertDialogBuilder.setMessage("Pendaftaran informasi terbaru dan promo terbaik berhasil");
                        alertDialogBuilder.setPositiveButton("Tutup", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface arg0, int arg1) {
                            }
                        });

                        AlertDialog alertDialog = alertDialogBuilder.create();
                        alertDialog.setCanceledOnTouchOutside(false);
                        alertDialog.show();
//                    }else{
//                        stopLoader();
//                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainActivity.this);
//                        alertDialogBuilder.setMessage("Pendaftaran informasi terbaru dan promo terbaik gagal");
//                        alertDialogBuilder.setPositiveButton("Tutup", new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface arg0, int arg1) {
//                            }
//                        });
//
//                        AlertDialog alertDialog = alertDialogBuilder.create();
//                        alertDialog.setCanceledOnTouchOutside(false);
//                        alertDialog.show();
//                    }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                stopLoader();
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainActivity.this);
                alertDialogBuilder.setMessage("Pendaftaran informasi terbaru dan promo terbaik gagal");
                alertDialogBuilder.setPositiveButton("Tutup", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                    }
                });

                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.setCanceledOnTouchOutside(false);
                alertDialog.show();
            }
        });

        strReq.setRetryPolicy(new DefaultRetryPolicy(60000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppController.getInstance().addToRequestQueue(strReq);
    }

    public void processResponseBrand(final JSONArray brandArray) {
        brandArrays = brandArray;
        gridBrand.setVerticalScrollBarEnabled(false);

        SlideBrandAdapter slideBrandAdapter = new SlideBrandAdapter(this, brandArray, "main");
        gridBrand.setAdapter(slideBrandAdapter);
    }

    public void getByBannerUrl(JSONArray response) {
        final String bannerUrl;

        try {
            bannerUrl = response.getJSONObject(0).toString();

            intent = new Intent(MainActivity.this, CategoryActivity.class);
            intent.putExtra("cat", "promo");
            intent.putExtra("promo", bannerUrl);
            startActivity(intent);
            overridePendingTransition(R.anim.left_in, R.anim.left_out);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void getByProdGropuUrl(JSONArray response) {
        final String bannerUrl;

        try {
            bannerUrl = response.getJSONObject(0).toString();

            intent = new Intent(MainActivity.this, CategoryActivity.class);
            intent.putExtra("cat", "promo");
            intent.putExtra("promo", bannerUrl);
            startActivity(intent);
            overridePendingTransition(R.anim.left_in, R.anim.left_out);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void getAliasUrl(JSONArray response) {
        try {
            String url = response.getJSONObject(0).getString("url");

            if (url.toLowerCase().contains("klikindomaret.com")) {
                if (url.toLowerCase().contains("open=browser")) {
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(url));
                    startActivity(i);
                    finish();
                } else if (url.toLowerCase().contains("/search/")) {
                    String[] urlSplit = url.split("=");
                    intent = new Intent(MainActivity.this, CategoryActivity.class);
                    intent.putExtra("cat", "search");
                    intent.putExtra("searchKey", urlSplit[1]);
                    startActivity(intent);
                    overridePendingTransition(R.anim.left_in, R.anim.left_out);
                } else if (url.toLowerCase().contains("/content/")) {
                    intent = new Intent(MainActivity.this, CategoryHeroActivity.class);
                    intent.putExtra("promo", response.getJSONObject(0).toString());
                    startActivity(intent);
                    overridePendingTransition(R.anim.left_in, R.anim.left_out);
                } else if (url.toLowerCase().contains("/product/")) {
                    intent = new Intent(MainActivity.this, ProductActivity.class);
                    intent.putExtra("cat", "home");
                    intent.putExtra("data", response.getJSONObject(0).toString());
                    startActivity(intent);
                    overridePendingTransition(R.anim.left_in, R.anim.left_out);
                } else if (url.contains("/category/")) {
                    String[] separateTarget = url.split("/");
                    String url1 = API.getInstance().getApiCategoryGet() + "?Permalink=" + separateTarget[separateTarget.length - 1];
                    makeJsonArrayGet(url1, "category");
                } else if (url.contains("/promo/")) {
                    final String bigBannerString = response.getJSONObject(0).toString();
                    intent = new Intent(MainActivity.this, CategoryActivity.class);
                    intent.putExtra("cat", "promo");
                    intent.putExtra("promo", bigBannerString);
                    startActivity(intent);
                    overridePendingTransition(R.anim.left_in, R.anim.left_out);
                } else {
                    makeJsonArrayGet(API.getInstance().getApiBannerByUrl() + "?mfp_id=" + sessionManager.getKeyMfpId() + "&url=" + url, "bannerurl");
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void processAllRegion(JSONArray response) {
        System.out.println("session manager = " + sessionManager.getRegionID());
        TextView regHeader = (TextView) findViewById(R.id.region_header);

        if (sessionManager.getRegionID() == null) {
            for (int i = 0; i < response.length(); i++) {
                try {
                    System.out.println("session = " + response.getJSONObject(i).getString("RegionName"));

                    if (response.getJSONObject(i).getString("RegionName").toLowerCase().equals("jakarta pusat")) {
                        sessionManager.setRegionID(response.getJSONObject(i).getString("ID"));
                        sessionManager.setRegionName(response.getJSONObject(i).getString("RegionName"));

                        regHeader.setText(response.getJSONObject(i).getString("RegionName"));
                        System.out.println(i + "session = " + response.getJSONObject(i).getString("ID"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

        makeJsonArrayGet(API.getInstance().getApiHomeContent() + "?mfp_id=" + sessionManager.getKeyMfpId() + "&regionID=" + sessionManager.getRegionID()
                + "&positionGroup=LANDING_BANNER", "home");
    }

    //process response data
    public void processResponse(JSONArray response) {
        try {
            objectResponse = response.getJSONObject(0);
            heroBanner = objectResponse.getJSONArray("HeroBanners");
            homePageSection = objectResponse.getJSONArray("HomePageSection");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (heroBanner.length() != 0) {
            setFeaturedProduct();
        }else{
            setBannerPromo();
        }

        stopLoader();
    }

    //method set featured product
    public void setFeaturedProduct() {
        intro_images = (WrapContentViewPager) findViewById(R.id.pager_introduction);
        pager_indicator = (LinearLayout) findViewById(R.id.viewPagerCountDots);
        pager_indicator.removeAllViews();

        mAdapter = new ViewPagerAdapter(MainActivity.this, heroBanner);

        intro_images.setAdapter(mAdapter);
        intro_images.setCurrentItem(0);
        intro_images.addOnPageChangeListener(MainActivity.this);
        imageDefault.setVisibility(View.GONE);
        setBannerPromo();

        setUiPageViewController();
        setScrollOnTop();
    }

    public void setBannerPromo(){
        PromoBannerAdapter promoBannerAdapter = new PromoBannerAdapter(this, homePageSection);

        promoBanner.setHasFixedSize(true);
        promoBanner.setLayoutManager(new LinearLayoutManager(MainActivity.this));
        promoBanner.setAdapter(promoBannerAdapter);

        //method set grid brand
        try {
            processResponseBrand(objectResponse.getJSONArray("Brands"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    //method set featured image indicator
    private void setUiPageViewController() {
        count = 0;
        dotsCount = mAdapter.getCount();
        dots = new ImageView[dotsCount];

        for (int i = 0; i < dotsCount; i++) {
            dots[i] = new ImageView(MainActivity.this);
            dots[i].setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.nonselecteditem_dot, null));

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );

            params.setMargins(4, 0, 4, 0);
            pager_indicator.addView(dots[i], params);
        }

        dots[0].setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.selecteditem_dot, null));
        handler.removeMessages(0);
        handler.postDelayed(runnable, 500);
    }

    public void Logout(){
        runLoader();
        sessionManager.setKeyShipping(false);
        sessionManager.setKeyPlazaShipping(false);
        getMFPToken(API.getInstance().getMfpId()+"?device_token=" + sessionManager.getDeviceToken());
        sqLiteHandler.deleteData();
        sessionManager.setKeyShippingPlaza(0);

        sessionManager.setKeyTotalPrice(0);
        sessionManager.setKeyTotalVoucher(0);
        sessionManager.setKeyTotalDiscount(0);
        sessionManager.setKeyTotalShippingCost(0);
        sessionManager.setKeyTotalCoupon(0);
        sessionManager.setKeyCouponList("");
        sessionManager.setKeyTokenCookie("");
        sessionManager.setkeyDate("");
        sessionManager.setKeyTime("");
    }

    public void getMFPToken(String url){
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if (response == null || response.length() == 0){
                                stopLoader();
                                Toast.makeText(MainActivity.this, "Gagal terkoneksi server", Toast.LENGTH_LONG).show();
                            }else{
                                stopLoader();
                                sessionManager.setKeyMfpId(response.getString("Message"));
                                sessionManager.setLogin(false);
                                sessionManager.setCartId(null);
                                sessionManager.setUserID("00000000-0000-0000-0000-000000000000");

                                intent = getIntent();
                                overridePendingTransition(0, 0);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                                overridePendingTransition(0, 0);
                                startActivity(intent);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MainActivity.this, "Gagal terkoneksi server", Toast.LENGTH_LONG).show();
                stopLoader();
            }
        });

        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(60000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppController.getInstance().addToRequestQueue(jsonObjReq);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    }

    //method set featured image indicator when scrolled
    @Override
    public void onPageSelected(int position) {
        count = position;

        for (int i = 0; i < dotsCount; i++) {
            dots[i].setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.nonselecteditem_dot, null));
        }

        dots[position].setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.selecteditem_dot, null));
    }

    @Override
    public void onPageScrollStateChanged(int state) {
    }

    @Override
    public void onClick(View v) {
    }

    private class ReverseGeocodingTask extends AsyncTask<LatLng, Void, String> {
        final Context mContext;
        TextView tvInfoStore;
        String addressTextValue;

        public ReverseGeocodingTask(Context context) {
            super();
            mContext = context;
        }

        @Override
        protected void onPreExecute() {
            tvInfoStore = (TextView) findViewById(R.id.region_header);
            addressTextValue = "";
            tvInfoStore.setText("");
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

            try {
                addresses = geocoder.getFromLocation(latitude, longitude, 1);
            } catch (Exception e) {
                addressTextValue = defaultAddress;
                addressText = defaultAddress;
            }

            if (addresses != null && addresses.size() > 0) {
                final Address address = addresses.get(0);

                String streetAddress = address.getAddressLine(0);
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
            tvInfoStore.setText(addressText);
        }
    }
    //***End Content Management***//

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