package com.indomaret.klikindomaret.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.indomaret.klikindomaret.API;
import com.indomaret.klikindomaret.R;
import com.indomaret.klikindomaret.app.AppController;
import com.indomaret.klikindomaret.helper.SessionManager;
import com.indomaret.klikindomaret.helper.UncaughtExceptionHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import pl.droidsonroids.gif.GifImageView;

public class CategoryHeroActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private RelativeLayout preloader;
    private SwipeRefreshLayout pullToRefreshContainer;
    private WebView myWebView;
    private Tracker mTracker;
    boolean hasLoaded = false;

    SessionManager sessionManager;

    Intent intent;
    String url, title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_hero);

        UncaughtExceptionHandler uncaughtExceptionHandler = new UncaughtExceptionHandler();
        uncaughtExceptionHandler.uncaughtException(this);

        AppController application = (AppController) getApplication();
        mTracker = application.getDefaultTracker();

        sessionManager = new SessionManager(CategoryHeroActivity.this);

        intent = getIntent();

        try{
            String data = intent.getStringExtra("promo");
            JSONObject object = new JSONObject(data);
            Log.d("Object Metatitle",object.toString());
            url = object.getString("TargetUrl");

            if (object.getString("MetaTitle").contains("null") || object.getString("MetaTitle").length() == 0){
                title = object.getString("Description");
            }else {
                title = object.getString("MetaTitle");
            }

            mTracker.setScreenName("Category Hero "+title+" Promo Page");
            mTracker.send(new HitBuilders.ScreenViewBuilder().setNewSession().build());
        }catch (Exception e){
            e.printStackTrace();
        }


        toolbar = (Toolbar) findViewById(R.id.app_toolbar);
        TextView mTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
        mTitle.setText(title);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        preloader = (RelativeLayout) findViewById(R.id.preloader);

        pullToRefresh();
    }

    public void pullToRefresh(){
        pullToRefreshContainer = (SwipeRefreshLayout) findViewById(R.id.hero_swipe_container);

        pullToRefreshContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                preloader.setVisibility(View.VISIBLE);

                pullToRefreshContainer.setRefreshing(false);
                prepareWebContent(url);
            }
        });

        pullToRefreshContainer.setColorSchemeResources(R.color.colorPrimary,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
    }

    public void prepareWebContent(String url){
        myWebView = (WebView) findViewById(R.id.hero_webview);
        myWebView.clearCache(true);
        myWebView.setWebViewClient(new MyBrowser());
        myWebView.setWebChromeClient(new WebChromeClient());
        myWebView.getSettings().setJavaScriptEnabled(true);
        myWebView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        myWebView.getSettings().setBuiltInZoomControls(true);
        myWebView.getSettings().setUserAgentString("klikindomaretmobile");
        myWebView.loadUrl(url);
    }

    private class MyBrowser extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            System.out.println("Url Promo = "+url);

            if (hasLoaded){
                if(url.contains("klikindomaret.com")){
                    if (url.contains("open=browser")){
                        Intent i = new Intent(Intent.ACTION_VIEW);
                        i.setData(Uri.parse(url));
                        startActivity(i);
                        finish();
                    } else if (url.contains("search")){
                        String[] urlSplit = url.split("=");
                        intent = new Intent(CategoryHeroActivity.this, CategoryActivity.class);
                        intent.putExtra("cat", "search");
                        intent.putExtra("promo", urlSplit[1]);
                        finish();
                        startActivity(intent);
                        overridePendingTransition(R.anim.left_in, R.anim.left_out);
                        finish();
                    } else if (url.contains("/promo/")||url.contains("/category/")){
                        String data = intent.getStringExtra("promo");
                        intent = new Intent(CategoryHeroActivity.this, CategoryActivity.class);
                        intent.putExtra("cat", "hero");
                        intent.putExtra("hero", data);
                        intent.putExtra("url", url);
                        finish();
                        startActivity(intent);
                        overridePendingTransition(R.anim.left_in, R.anim.left_out);
                        finish();
                    } else if (url.contains("/product/")){
                        String data = intent.getStringExtra("promo");
                        intent = new Intent(CategoryHeroActivity.this, ProductActivity.class);
                        intent.putExtra("cat", "hero");
                        intent.putExtra("hero", data);
                        intent.putExtra("url", url);
                        finish();
                        startActivity(intent);
                        overridePendingTransition(R.anim.left_in, R.anim.left_out);
                        finish();
                    } else if (url.contains("/content/")){
                        myWebView.loadUrl(url);
                    }else {

                        makeJsonArrayGet(API.getInstance().getApiBannerByUrl()+"?mfp_id="+sessionManager.getKeyMfpId()+"&url="+url, "bannerurl");
                    }
                } else {
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(url));
                    startActivity(i);
                    finish();
                }
            }else {
                System.out.println("Url Promo Bawah = "+url);
                myWebView.loadUrl(url);
            }
            return true;
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);

            hasLoaded = true;
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
            finish();
            overridePendingTransition(R.anim.right_in, R.anim.right_out);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void makeJsonArrayGet(final String urlJsonObj, final String type){
        System.out.println("region " + urlJsonObj);

        JsonArrayRequest jsonArrayReq = new JsonArrayRequest(urlJsonObj,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        System.out.println("region " + response);

                        if (type.equals("bannerurl")){
                            String[] url = urlJsonObj.split("url=");
                            if (response.length() > 0){
                                getByBannerUrl(response);
                            }else {
                                makeJsonArrayGet(API.getInstance().getApiProdGroupByUrl()+"?mfp_id="+sessionManager.getKeyMfpId()+"&url="+url[1], "prodGroup");
                            }
                        }else if (type.equals("prodGroup")){
                            String[] url = urlJsonObj.split("url=");
                            if (response.length() > 0){
                                getByProdGropuUrl(response);
                            }
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(CategoryHeroActivity.this, "Gagal terkoneksi server", Toast.LENGTH_LONG).show();
                preloader.setVisibility(View.GONE);
            }
        }, this);

        jsonArrayReq.setRetryPolicy(new DefaultRetryPolicy(60000, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppController.getInstance().addToRequestQueue(jsonArrayReq);
    }

    public void getByBannerUrl(JSONArray response){
        try {
            String bannerUrl = response.getJSONObject(0).toString();
            intent = new Intent(CategoryHeroActivity.this, CategoryActivity.class);
            intent.putExtra("cat", "promo");
            intent.putExtra("promo", bannerUrl);
            startActivity(intent);
            overridePendingTransition(R.anim.left_in, R.anim.left_out);
            finish();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void getByProdGropuUrl(JSONArray response){
        try {
            String bannerUrl = response.getJSONObject(0).toString();
            JSONObject bannerObject = new JSONObject();
            bannerObject.put("ID", response.getJSONObject(0).getString("ID"));
            bannerObject.put("MetaTitle", response.getJSONObject(0).getString("MetaTitle"));

            if (response.getJSONObject(0).toString().contains("ProductGroupDescription"))
                bannerObject.put("ProductGroupDescription", response.getJSONObject(0).getString("ProductGroupDescription"));
            else
                bannerObject.put("Description", response.getJSONObject(0).getString("Description"));

            intent = new Intent(CategoryHeroActivity.this, CategoryActivity.class);
            intent.putExtra("cat", "promo");
            intent.putExtra("promo", bannerObject.toString());
            finish();
            startActivity(intent);
            overridePendingTransition(R.anim.left_in, R.anim.left_out);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.right_in, R.anim.right_out);
    }

    @Override
    public void onResume(){
        super.onResume();
        prepareWebContent(url);
    }
}
