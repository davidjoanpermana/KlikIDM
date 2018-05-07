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
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.indomaret.klikindomaret.API;
import com.indomaret.klikindomaret.MainActivity;
import com.indomaret.klikindomaret.R;
import com.indomaret.klikindomaret.app.AppController;
import com.indomaret.klikindomaret.helper.UncaughtExceptionHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import pl.droidsonroids.gif.GifImageView;

public class YahooActivity extends AppCompatActivity {
    private Intent intent;
    private WebView myWebView;

    private Toolbar toolbar;
    private TextView mTitle;
    private RelativeLayout preloader;
    private SwipeRefreshLayout pullToRefreshContainer;

    private String callbakUrl, clientId, clientSecret;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_third_party);

        UncaughtExceptionHandler uncaughtExceptionHandler = new UncaughtExceptionHandler();
        uncaughtExceptionHandler.uncaughtException(this);

        intent = getIntent();
        callbakUrl = intent.getStringExtra("callbackUrl");
        clientId = "dj0yJmk9TVJRR3cxaFl3dmEwJmQ9WVdrOVYyNXBhVmR4TnpZbWNHbzlNQS0tJnM9Y29uc3VtZXJzZWNyZXQmeD0yOA--";
        clientSecret = "1adda1c11e3568f7d2913e720ae309107c5b5210";

        toolbar = (Toolbar) findViewById(R.id.app_toolbar);
        mTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
        mTitle.setText("Sign Yahoo");
        toolbar.setTitle("");
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

            String urls = url.replace("%2F", "/").replace("%3A", ":");
            System.out.println(urls);

            if (urls.contains("Customer/RedirectFromYahoo") && !urls.contains("client_id")){
                stringPost("https://api.login.yahoo.com/oauth2/get_token", urls);
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
            intent = new Intent(YahooActivity.this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | IntentCompat.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
            overridePendingTransition(R.anim.right_in, R.anim.right_out);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void onBackPressed() {
        intent = new Intent(YahooActivity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | IntentCompat.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
        overridePendingTransition(R.anim.right_in, R.anim.right_out);
    }

    public void stringPost(String urlJsonObj, final String param){
        StringRequest jsonObjRequest = new StringRequest(Request.Method.POST,
                urlJsonObj, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if (response == null || response.length() == 0){
                            preloader.setVisibility(View.GONE);
                            Toast.makeText(YahooActivity.this, "Gagal terkoneksi server", Toast.LENGTH_LONG).show();
                        }else{
                            preloader.setVisibility(View.VISIBLE);

                            try {
                                JSONObject object = new JSONObject(response);

                                objectRequest("https://social.yahooapis.com/v1/user/me/profile?format=json", object.getString("access_token"));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(YahooActivity.this, "Gagal terkoneksi server", Toast.LENGTH_LONG).show();
                preloader.setVisibility(View.GONE);
            }
        }) {
            @Override
            public String getBodyContentType() {
                return "application/x-www-form-urlencoded; charset=UTF-8";
            }

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("client_id", clientId);
                params.put("client_secret", clientSecret);
                params.put("redirect_uri", param.split("\\?")[0]);
                params.put("code", param.split("\\?")[1].split("=")[1]);
                params.put("grant_type", "authorization_code");

                return params;
            }
        };

        jsonObjRequest.setRetryPolicy(new DefaultRetryPolicy(50000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppController.getInstance().addToRequestQueue(jsonObjRequest);
    }

    public void objectRequest(final String url, final String token) {
        JsonObjectRequest jsonObjRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    if (response == null || response.length() == 0){
                        preloader.setVisibility(View.GONE);
                        Toast.makeText(YahooActivity.this, "Gagal terkoneksi server", Toast.LENGTH_LONG).show();
                    }else{
                        preloader.setVisibility(View.VISIBLE);

                        try {
                            arrayRequest(API.getInstance().getApiEmail()+"?Email="+response.getJSONObject("profile").getJSONArray("emails").getJSONObject(0).getString("handle"), String.valueOf(response.getJSONObject("profile")));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(YahooActivity.this, "Gagal terkoneksi server", Toast.LENGTH_LONG).show();
                    preloader.setVisibility(View.GONE);
                }
            }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String>  params = new HashMap<>();
                params.put("Authorization", "Bearer "+token);

                return params;
            }
        };

        jsonObjRequest.setRetryPolicy(new DefaultRetryPolicy(50000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppController.getInstance().addToRequestQueue(jsonObjRequest);
    }

    public void arrayRequest(final String url, final String profile) {
        System.out.println("arrayRequest cat : "+url);
        JsonArrayRequest jsonObjRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        if (response.length() > 0){
                            intent = new Intent(YahooActivity.this, LoginActivity.class);
                            intent.putExtra("from", "yahooLogin");
                            intent.putExtra("data", response.toString());
                            finish();
                            startActivity(intent);
                            overridePendingTransition(R.anim.left_in, R.anim.left_out);
                        }else{
                            intent = new Intent(YahooActivity.this, RegisterActivity.class);
                            intent.putExtra("from", "yahooLogin");
                            intent.putExtra("profile", profile);
                            finish();
                            startActivity(intent);
                            overridePendingTransition(R.anim.left_in, R.anim.left_out);
                        }

                        preloader.setVisibility(View.GONE);
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(YahooActivity.this, "Gagal terkoneksi server", Toast.LENGTH_LONG).show();
                preloader.setVisibility(View.GONE);
            }
        });

        jsonObjRequest.setRetryPolicy(new DefaultRetryPolicy(50000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppController.getInstance().addToRequestQueue(jsonObjRequest);
    }
}
