package com.indomaret.klikindomaret.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.content.IntentCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.google.android.gms.analytics.Tracker;
import com.indomaret.klikindomaret.MainActivity;
import com.indomaret.klikindomaret.R;
import com.indomaret.klikindomaret.app.AppController;
import com.indomaret.klikindomaret.helper.UncaughtExceptionHandler;

import pl.droidsonroids.gif.GifImageView;

public class PaymentThirdPartyActivity extends AppCompatActivity {
    private Intent intent;
    private Tracker mTracker;
    private WebView myWebView;

    private Toolbar toolbar;
    private TextView mTitle;
    private RelativeLayout preloader;
    private SwipeRefreshLayout pullToRefreshContainer;

    private String callbakUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_third_party);

        UncaughtExceptionHandler uncaughtExceptionHandler = new UncaughtExceptionHandler();
        uncaughtExceptionHandler.uncaughtException(this);

        AppController application = (AppController) getApplication();
        mTracker = application.getDefaultTracker();

        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);

        intent = getIntent();
        callbakUrl = intent.getStringExtra("callbackUrl");

        toolbar = (Toolbar) findViewById(R.id.app_toolbar);
        mTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
        mTitle.setText("Pembayaran");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        System.out.println("third party = " + callbakUrl);

        preloader = (RelativeLayout) findViewById(R.id.preloader);

        preloader.setVisibility(View.VISIBLE);

        prepareWebContent();
        pullToRefresh();
    }

    public void prepareWebContent(){
        myWebView = (WebView) findViewById(R.id.payment_third_webview);
        myWebView.clearCache(true);
        myWebView.getSettings().setJavaScriptEnabled(true);
        myWebView.getSettings().setSaveFormData(false);
        myWebView.setWebViewClient(new MyBrowser());
        myWebView.loadUrl(callbakUrl);
    }

    public void pullToRefresh(){
        pullToRefreshContainer = (SwipeRefreshLayout) findViewById(R.id.payment_third_swipe_container);

        pullToRefreshContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                preloader.setVisibility(View.VISIBLE);

                pullToRefreshContainer.setRefreshing(false);

                prepareWebContent();
            }
        });

        pullToRefreshContainer.setColorSchemeResources(R.color.colorPrimary,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
    }

    private class MyBrowser extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            return false;
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);

            preloader.setVisibility(View.VISIBLE);

//            if((url.toLowerCase().contains("payment.klikindomaret.com"))){}
            if(url.toLowerCase().contains("faspay")){}
            else if(url.toLowerCase().contains("bca")){}
            else if(url.toLowerCase().contains("trxid=")){
                AppEventsLogger logger = AppEventsLogger.newLogger(PaymentThirdPartyActivity.this);
                logger.logEvent("Thankyou page");

                String[] trx = url.split("=");

                intent = new Intent(PaymentThirdPartyActivity.this, ThankyouPageActivity.class);
                intent.putExtra("transactionCode", trx[1]);
                intent.putExtra("from", "pay");
                finish();
                startActivity(intent);
                overridePendingTransition(R.anim.left_in, R.anim.left_out);
            }

            myWebView.clearHistory();
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);

            preloader.setVisibility(View.GONE);
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
            intent = new Intent(PaymentThirdPartyActivity.this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | IntentCompat.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
            overridePendingTransition(R.anim.right_in, R.anim.right_out);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void onBackPressed() {
        intent = new Intent(PaymentThirdPartyActivity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | IntentCompat.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
        overridePendingTransition(R.anim.right_in, R.anim.right_out);
    }
}
