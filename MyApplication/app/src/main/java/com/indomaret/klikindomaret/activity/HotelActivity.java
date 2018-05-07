package com.indomaret.klikindomaret.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.ConsoleMessage;
import android.webkit.CookieManager;
import android.webkit.HttpAuthHandler;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.indomaret.klikindomaret.MainActivity;
import com.indomaret.klikindomaret.R;
import com.indomaret.klikindomaret.adapter.ListMenuHotelAdapter;
import com.indomaret.klikindomaret.app.AppController;
import com.indomaret.klikindomaret.helper.SessionManager;
import com.indomaret.klikindomaret.helper.UncaughtExceptionHandler;
import com.indomaret.klikindomaret.views.HeightAdjustableListView;

import java.util.ArrayList;
import java.util.List;

import pl.droidsonroids.gif.GifImageView;

public class HotelActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private SessionManager sessionManager;
    private Intent intent;
    private WebView myWebView;
    private DrawerLayout drawer;
    private ImageView btnCloseMenu;
    private HeightAdjustableListView mListView;
    private RelativeLayout preloader;
    private SwipeRefreshLayout pullToRefreshContainer;
    private Tracker mTracker;

    private List<String> listMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hotel);

        UncaughtExceptionHandler uncaughtExceptionHandler = new UncaughtExceptionHandler();
        uncaughtExceptionHandler.uncaughtException(this);

        AppController application = (AppController) getApplication();
        mTracker = application.getDefaultTracker();

        mTracker.setScreenName("About Page");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
        intent = getIntent();
        sessionManager = new SessionManager(HotelActivity.this);

        toolbar = (Toolbar) findViewById(R.id.app_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        myWebView = (WebView) findViewById(R.id.about_webview);
        mListView = (HeightAdjustableListView) findViewById(R.id.listView);
        preloader = (RelativeLayout) findViewById(R.id.preloader);

        myWebView.getSettings().setJavaScriptEnabled(true);
        myWebView.getSettings().setDomStorageEnabled(true);
        myWebView.getSettings().setUserAgentString("KLIKINDOMARET_MOBILE_APPS");
        myWebView.setWebViewClient(new MyBrowser());

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
            header_view = getLayoutInflater().inflate(R.layout.header_layout_hotel, null, false);
            mListView.addHeaderView(header_view);
        }

        TextView btnSignin = (TextView) header_view.findViewById(R.id.text_sign_in);
        btnCloseMenu = (ImageView) header_view.findViewById(R.id.btn_close_menu);

        btnCloseMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawer.closeDrawer(GravityCompat.START);
            }
        });

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
            btnSignin.setText("Masuk / Daftar");
        }

        btnSignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(HotelActivity.this, LoginActivity.class);
                intent.putExtra("from", "hotel");
                startActivity(intent);
            }
        });

        View footer_view = mListView.findViewById(R.id.footer_menu);

        if (footer_view == null) {
            footer_view = getLayoutInflater().inflate(R.layout.footer_layout_hotel, null);
            mListView.addFooterView(footer_view);
        }

        TextView btnback = (TextView) footer_view.findViewById(R.id.text_back);
        btnback.setText("< Kembali ke Klikindomaret.com");

        btnback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(HotelActivity.this, MainActivity.class);
                finish();
                startActivity(intent);
            }
        });

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (listMenu.get(position-1).equals("Beranda")){
                    drawer.closeDrawer(GravityCompat.START);
                    openMenu("https://m.hotel.klikindomaret.com/");
                    runLoder();
                }else if (listMenu.get(position-1).equals("Riwayat Pemesanan")){
                    openMenu("https://m.hotel.klikindomaret.com/user/booking-history");
                    runLoder();
                }else if (listMenu.get(position-1).equals("Cara Memesan")){
                    openMenu("https://m.hotel.klikindomaret.com/cara-memesan");
                    runLoder();
                }else if (listMenu.get(position-1).equals("FAQ")){
                    openMenu("https://m.hotel.klikindomaret.com/faq");
                    runLoder();
                }else if (listMenu.get(position-1).equals("Kebijakan Pengembalian")){
                    openMenu("https://m.hotel.klikindomaret.com/kebijakan-pengembalian");
                    runLoder();
                }else if (listMenu.get(position-1).equals("Kebijakan Privasi")){
                    openMenu("https://m.hotel.klikindomaret.com/kebijakan-privasi");
                    runLoder();
                }else if (listMenu.get(position-1).equals("Persyaratan dan Ketentuan")){
                    openMenu("https://m.hotel.klikindomaret.com/tnc");
                    runLoder();
                }

                drawer.closeDrawer(GravityCompat.START);
            }
        });

        prepareWebContent();
        pullToRefresh();
    }

    public void pullToRefresh(){
        pullToRefreshContainer = (SwipeRefreshLayout) findViewById(R.id.about_swipe_container);

        pullToRefreshContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                runLoder();
                pullToRefreshContainer.setRefreshing(false);
                prepareWebContent();
            }
        });

        pullToRefreshContainer.setColorSchemeResources(R.color.colorPrimary,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
    }

    public void prepareWebContent(){
        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.setAcceptCookie(true);

        if (sessionManager.isLoggedIn()){
            String cookies = "KlikIndomaret_LOGIN_TOKEN="+sessionManager.getKeyTokenCookie();
            cookieManager.setCookie("https://m.hotel.klikindomaret.com", cookies);
            System.out.println("cookies : "+cookieManager.getCookie("https://m.hotel.klikindomaret.com"));
        }

        myWebView.loadUrl("https://m.hotel.klikindomaret.com");
    }

    public void openMenu(String url){
        myWebView.loadUrl(url);
    }

    private class MyBrowser extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            myWebView.loadUrl(url);
            return true;
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            stopLoder();
        }

        @Override
        public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error){
            Toast.makeText(HotelActivity.this, "Your Internet Connection May not be active Or " + error , Toast.LENGTH_LONG).show();
            super.onReceivedError(view, request, error);
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
        return;
    }

    public void runLoder(){
        preloader.setVisibility(View.VISIBLE);
    }

    public void stopLoder(){
        preloader.setVisibility(View.GONE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        listMenu = new ArrayList<>();

        if (sessionManager.isLoggedIn()){
            listMenu.add("Beranda");
            listMenu.add("Riwayat Pemesanan");
            listMenu.add("Cara Memesan");
            listMenu.add("FAQ");
            listMenu.add("Kebijakan Pengembalian");
            listMenu.add("Kebijakan Privasi");
            listMenu.add("Persyaratan dan Ketentuan");
        }else{
            listMenu.add("Beranda");
            listMenu.add("Cara Memesan");
            listMenu.add("FAQ");
            listMenu.add("Kebijakan Pengembalian");
            listMenu.add("Kebijakan Privasi");
            listMenu.add("Persyaratan dan Ketentuan");
        }

        ListMenuHotelAdapter listMenuHotelAdapter = new ListMenuHotelAdapter(this, listMenu);
        mListView.setAdapter(listMenuHotelAdapter);
    }
}
