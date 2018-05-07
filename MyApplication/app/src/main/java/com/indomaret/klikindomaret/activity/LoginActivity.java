package com.indomaret.klikindomaret.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.multidex.MultiDex;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.indomaret.klikindomaret.API;
import com.indomaret.klikindomaret.MainActivity;
import com.indomaret.klikindomaret.R;
import com.indomaret.klikindomaret.app.AppController;
import com.indomaret.klikindomaret.helper.SQLiteHandler;
import com.indomaret.klikindomaret.helper.SessionManager;
import com.indomaret.klikindomaret.helper.UncaughtExceptionHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

import pl.droidsonroids.gif.GifImageView;

public class LoginActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {
    private SessionManager sessionManager;
    private SQLiteHandler sqLiteHandler;

    private Button btLoginGmail, btLoginFacebook, btLoginYahoo;
    private EditText inputEmail, inputPassword;
    private TextView forgotPassword, mTitle, tvDaftar, tvBackHome;

    private RelativeLayout preloader;
    private LinearLayout linearParent;
    private RelativeLayout preloaderShadow;
    private Toolbar toolbar;
    private Tracker mTracker;
    private Intent intent;
    private String from;
    private JSONArray dataList = new JSONArray();

    private CallbackManager mCallbackManager;
    private static GoogleApiClient mGoogleApiClient;
    private final int REQUEST_GOOGLE_SIGN = 72;
//    private final int REQUEST_GOOGLE_SIGN = 9001;

    protected void onCreate(Bundle savedInstanceState) {
        MultiDex.install(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        UncaughtExceptionHandler uncaughtExceptionHandler = new UncaughtExceptionHandler();
        uncaughtExceptionHandler.uncaughtException(this);

        toolbar = (Toolbar) findViewById(R.id.app_toolbar);
        mTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
        mTitle.setText("Masuk Akun");
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        intent = getIntent();
        from = intent.getStringExtra("from");

        AppController application = (AppController) getApplication();
        mTracker = application.getDefaultTracker();

        mTracker.setScreenName("Login Page");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());

        sessionManager = new SessionManager(LoginActivity.this);
        sqLiteHandler = new SQLiteHandler(LoginActivity.this);

        inputEmail = (EditText) findViewById(R.id.input_email);
        inputPassword = (EditText) findViewById((R.id.input_password));
        forgotPassword = (TextView) findViewById(R.id.link_forgot_password);
        tvDaftar = (TextView) findViewById(R.id.daftar);
        tvBackHome = (TextView) findViewById(R.id.tvBackHome);

        tvDaftar.setText(Html.fromHtml("Belum ada Akun? <u>Daftar disini</u>"));
        forgotPassword.setText(Html.fromHtml("<u>Lupa Kata Sandi</u>"));
        tvBackHome.setText(Html.fromHtml("<u>Kembali ke Beranda</u>"));
        tvBackHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        btLoginGmail = (Button) findViewById(R.id.btLoginGmail);
        btLoginFacebook = (Button) findViewById(R.id.btLoginFacebook);
        btLoginYahoo = (Button) findViewById(R.id.btLoginYahoo);

        preloader = (RelativeLayout) findViewById(R.id.preloader);
        linearParent = (LinearLayout) findViewById(R.id.linear_parent);
        preloaderShadow = (RelativeLayout) findViewById(R.id.preloader);

        if (from.equals("yahooLogin") || from.equals("facebookLogin")  || from.equals("googleLogin")){
            try {
                dataList = new JSONArray(intent.getStringExtra("data"));

                getLogin(dataList.getJSONObject(0).getString("Email"), dataList.getJSONObject(0).getString("Password"), true);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        btLoginFacebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LoginManager.getInstance().logInWithReadPermissions(LoginActivity.this,
                        Arrays.asList("public_profile", "email"));
            }
        });

        btLoginGmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signIn();
            }
        });

        btLoginYahoo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String urlYahoo = "https://api.login.yahoo.com/oauth2/request_auth" +
                        "?client_id=dj0yJmk9TVJRR3cxaFl3dmEwJmQ9WVdrOVYyNXBhVmR4TnpZbWNHbzlNQS0tJnM9Y29uc3VtZXJzZWNyZXQmeD0yOA--" +
                        "&redirect_uri=http://beta.klikindomaret.com/Customer/RedirectFromYahoo" +
                        "&response_type=code" +
                        "&language=en-us";
                intent = new Intent(LoginActivity.this, YahooActivity.class);
                intent.putExtra("callbackUrl", urlYahoo);
                startActivity(intent);
                overridePendingTransition(R.anim.left_in, R.anim.left_out);
            }
        });

        initializeGoogleSignIn();
        initializeCallbackFB();
    }

    private void initializeGoogleSignIn(){
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
    }

    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, REQUEST_GOOGLE_SIGN);
    }

    private void signOut(){
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(new ResultCallback<Status>() {
            @Override
            public void onResult(@NonNull Status status) {}
        });
    }

    private void initializeCallbackFB() {
        mCallbackManager = CallbackManager.Factory.create();
        LoginManager.getInstance().registerCallback(mCallbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(final LoginResult loginResult) {
                        if (loginResult != null) {
                            if (loginResult.getAccessToken() != null) {
//                                doLoginSocialMedia(loginResult.getAccessToken().getToken(),
//                                        Helpers.getString(R.string.provider_facebook));
                                /*Get Email facebook */
                                GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(),
                                        new GraphRequest.GraphJSONObjectCallback() {
                                            @Override
                                            public void onCompleted(JSONObject object, GraphResponse response) {
                                                try {
                                                    if (object.has("email")) {
                                                        arrayRequest(API.getInstance().getApiEmail()+"?Email="+object.getString("email"), object.toString(), "facebook");
                                                    }
                                                } catch (JSONException e) {}
                                            }
                                        });
                                Bundle parameters = new Bundle();
                                parameters.putString("fields", "id,name,email,gender, birthday");
                                request.setParameters(parameters);
                                request.executeAsync();
                            }
                        }
                    }

                    @Override
                    public void onCancel() {
                        Toast.makeText(LoginActivity.this, "Login Facebook has been Cancelled",Toast.LENGTH_SHORT).show();

                    }

                    @Override
                    public void onError(FacebookException exception) {
                    }
                });


    }

    public void arrayRequest(final String url, final String profile, final String type) {
        System.out.println("arrayRequest cat : "+url);
        JsonArrayRequest jsonObjRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                            if (type.equals("facebook")){
                                if (response.length() > 0){
                                    try {
                                        getLogin(response.getJSONObject(0).getString("Email"), response.getJSONObject(0).getString("Password"), true);
                                    }catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }else{
                                    intent = new Intent(LoginActivity.this, RegisterActivity.class);
                                    intent.putExtra("from", "facebookLogin");
                                    intent.putExtra("profile", profile);
                                    finish();
                                    startActivity(intent);
                                    overridePendingTransition(R.anim.left_in, R.anim.left_out);
                                }
                            }else if (type.equals("google")){
                                signOut();
                                if (response.length() > 0){
                                    try {
                                        getLogin(response.getJSONObject(0).getString("Email"), response.getJSONObject(0).getString("Password"), true);
                                    }catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }else{
                                    intent = new Intent(LoginActivity.this, RegisterActivity.class);
                                    intent.putExtra("from", "googleLogin");
                                    intent.putExtra("profile", profile);
                                    finish();
                                    startActivity(intent);
                                    overridePendingTransition(R.anim.left_in, R.anim.left_out);
                                }
                            }

                            stopLoader();
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(LoginActivity.this, "Gagal terkoneksi server", Toast.LENGTH_LONG).show();
                stopLoader();
            }
        });

        jsonObjRequest.setRetryPolicy(new DefaultRetryPolicy(50000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppController.getInstance().addToRequestQueue(jsonObjRequest);
    }

    //login button
    public void loginAccount(View view) {
        String email = inputEmail.getText().toString();
        String pass = inputPassword.getText().toString();

        getLogin(email, pass, false);
    }

    public void getLogin(String email, String password, boolean isFromSocialMedia){
        if (TextUtils.isEmpty(email)) {
            inputEmail.setError("Harap isi Email");
            return;
        }

        if (TextUtils.isEmpty(password)) {
            inputPassword.setError("Harap isi Password");
            return;
        }

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("Email", email);
            jsonObject.put("Password", password);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        runLoader();

        String url = API.getInstance().getApiLoginMobile();
        if(isFromSocialMedia){
            url = API.getInstance().getApiLogin();
        }

        makeJsonPost(url + "?device_token=" + sessionManager.getDeviceToken() + "&mfp_id=" + sessionManager.getKeyMfpId() + "&isMobile=true", jsonObject, "login");
    }

    public void toForgotPassword(View view) {
        intent = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
        intent.putExtra("value", "password");
        startActivity(intent);
        overridePendingTransition(R.anim.left_in, R.anim.left_out);
    }

    public void toRegister(View view) {
        intent = new Intent(LoginActivity.this, RegisterActivity.class);
        intent.putExtra("from", "login");
        startActivity(intent);
        overridePendingTransition(R.anim.left_in, R.anim.left_out);
    }

    //PROCESS LOGIN RESPON
    public void processResponse(JSONObject response) {
        Boolean IsSuccess = null;
        String message = "";

        try {
            IsSuccess = response.getBoolean("IsSuccess");
            message = response.getString("Message");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (IsSuccess.equals(true)) {
            try {
                JSONObject object = response.getJSONObject("ResponseObject");
                sessionManager.setLogin(true);
                sessionManager.setResponseId(response.getString("ResponseID"));
                sessionManager.setKeyMfpId(response.getString("ResponseID"));
                sessionManager.setUsername(object.getString("FName"));
                sessionManager.setLastUsername(object.getString("LName"));
                sessionManager.setUserEmail(object.getString("Email"));
                sessionManager.setKeyTokenCookie(response.getJSONObject("ResponseObject").getString("TokenCookie"));

                makeJsonObjectGet(API.getInstance().getApiGetProfile() + "?access_token=" + sessionManager.getKeyMfpId() + "&mfp_id=" + sessionManager.getKeyMfpId());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else if (message.toLowerCase().contains("tidak aktif")) {
            String email = inputEmail.getText().toString();
            String pass = inputPassword.getText().toString();
            try {
                sessionManager.setResponseId(response.getString("ResponseID"));
            } catch (JSONException e) {
                e.printStackTrace();
            }

            finish();
            intent = new Intent(LoginActivity.this, ActivationAccountActivity.class);
            intent.putExtra("email", email);
            intent.putExtra("pass", pass);
            startActivity(intent);
            overridePendingTransition(R.anim.left_in, R.anim.left_out);
        } else {
            try {
                stopLoader();
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(LoginActivity.this);
                alertDialogBuilder.setMessage(response.getString("Message"));
                alertDialogBuilder.setNegativeButton("Tutup", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                    }
                });

                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    // GET PROFILE RESPONSE
    public void getProfileRespon(JSONArray response) {
        sqLiteHandler.insertProfile(response.toString());

        try {
            sessionManager.setUserID(response.getJSONObject(0).getString("ID"));
            JSONArray address = response.getJSONObject(0).getJSONArray("Address");
//            sessionManager.setKeyMobileVerified(response.getJSONObject(0).getBoolean("MobileVerified"));

            for (int i = 0; i < address.length(); i++) {
                if (address.getJSONObject(i).getString("IsDefault").equals("true")) {
                    sqLiteHandler.insertDefaultAddress(address.getJSONObject(i).toString());
                }
            }
        } catch (JSONException e) {
            stopLoader();
            e.printStackTrace();
        }

        if (sessionManager.getCartId() == null || sessionManager.getCartId() == "00000000-0000-0000-0000-000000000000") {
            stringRequest(API.getInstance().getLastestShoppingCartId() + "?CustomerId=" + sessionManager.getUserID() + "&mfp_id=" + sessionManager.getKeyMfpId() + "&RegionId=" + sessionManager.getRegionID());
        } else {
            JSONObject cartObject = new JSONObject();
            JSONArray itemArray = new JSONArray();

            try {
                cartObject.put("CartId", sessionManager.getCartId());
                cartObject.put("NewCartId", "00000000-0000-0000-0000-000000000000");
                cartObject.put("OldCustomerId", "00000000-0000-0000-0000-000000000000");
                cartObject.put("NewCustomerID", sessionManager.getUserID());
                cartObject.put("Items", itemArray);
                cartObject.put("oldCart", null);
                cartObject.put("newCart", null);
                cartObject.put("DeleteOld", false);
                cartObject.put("RegionId", sessionManager.getRegionID());
            } catch (JSONException e) {
                e.printStackTrace();
            }

            makeJsonPost(API.getInstance().getApiCheckMerge() + "?mfp_id=" + sessionManager.getKeyMfpId(), cartObject, "merge");
        }
    }

    // GET LASTEST CART ID RESPONSE
    public void getLastestCartIdResponse(String response) {
        stopLoader();
        sessionManager.setCartId(response.replace("\"", ""));

        if (from.equals("klikindomaret") || from.equals("yahooLogin") || from.equals("facebookLogin")  || from.equals("googleLogin")) {
            intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
            overridePendingTransition(R.anim.right_in, R.anim.right_out);
        } else if (from.equals("kai")) {
            intent = new Intent(LoginActivity.this, HomeKAIActivity.class);
            intent.putExtra("from", "klikindomaret");
            startActivity(intent);
            finish();
            overridePendingTransition(R.anim.right_in, R.anim.right_out);
        } else if (from.equals("hotel")) {
            intent = new Intent(LoginActivity.this, HotelActivity.class);
            intent.putExtra("from", "klikindomaret");
            startActivity(intent);
            finish();
            overridePendingTransition(R.anim.right_in, R.anim.right_out);
        }else if (from.equals("passenger")) {
            setResult(RESULT_OK, intent);
            finish();
        }
//        else {
//            finish();
//            overridePendingTransition(R.anim.right_in, R.anim.right_out);
//        }
    }

    public void checkMergeResponse(JSONObject response) {
        try {
            if (response.getBoolean("IsSuccess")) {
                JSONObject cartObject = new JSONObject();
                JSONArray itemArray = new JSONArray();

                try {
                    cartObject.put("CartId", sessionManager.getCartId());
                    cartObject.put("NewCartId", "00000000-0000-0000-0000-000000000000");
                    cartObject.put("OldCustomerId", "00000000-0000-0000-0000-000000000000");
                    cartObject.put("NewCustomerID", sessionManager.getUserID());
                    cartObject.put("Items", itemArray);
                    cartObject.put("oldCart", null);
                    cartObject.put("newCart", null);
                    cartObject.put("DeleteOld", false);
                    cartObject.put("RegionId", sessionManager.getRegionID());
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                makeJsonPost(API.getInstance().getApiUpdateCustomer() + "?mfp_id=" + sessionManager.getKeyMfpId(), cartObject, "update");

            } else {
                intent = new Intent(LoginActivity.this, MergeCartActivity.class);
                startActivity(intent);
                finish();
                overridePendingTransition(R.anim.left_in, R.anim.left_out);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void updateCustomerRespon(JSONObject response) {
        try {
            if (response.getBoolean("IsSuccess")) {
                if (from.equals("klikindomaret") || from.equals("yahooLogin") || from.equals("facebookLogin")  || from.equals("googleLogin")) {
                    intent = new Intent(LoginActivity.this, CartActivity.class);
                    startActivity(intent);
                    finish();
                    overridePendingTransition(R.anim.right_in, R.anim.right_out);
                }else if (from.equals("kai")) {
                    intent = new Intent(LoginActivity.this, HomeKAIActivity.class);
                    intent.putExtra("from", "klikindomaret");
                    startActivity(intent);
                    finish();
                    overridePendingTransition(R.anim.right_in, R.anim.right_out);
                }else if (from.equals("hotel")) {
                    intent = new Intent(LoginActivity.this, HotelActivity.class);
                    intent.putExtra("from", "klikindomaret");
                    startActivity(intent);
                    finish();
                    overridePendingTransition(R.anim.right_in, R.anim.right_out);
                }else if (from.equals("passenger")) {
                    setResult(RESULT_OK, intent);
                    finish();
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    //OBJECT REQUEST
    public void makeJsonObjectGet(String url) {
        System.out.println("address url = " + url);

        JsonArrayRequest jsonArrayReq = new JsonArrayRequest(url,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        if (response == null || response.length() == 0){
                            stopLoader();
                            Toast.makeText(LoginActivity.this, "Gagal terkoneksi server", Toast.LENGTH_LONG).show();
                        }else{
                            getProfileRespon(response);
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                stopLoader();
                Log.i("Error", error.toString());

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(LoginActivity.this);
                alertDialogBuilder.setMessage("Login Gagal");
                alertDialogBuilder.setNegativeButton("Tutup", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                    }
                });

                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
            }
        }, this);

        jsonArrayReq.setRetryPolicy(new DefaultRetryPolicy(60000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppController.getInstance().addToRequestQueue(jsonArrayReq);
    }

    // POST OBJECT
    public void makeJsonPost(String urlJsonObj, JSONObject jsonObject, final String type) {
        Log.i("Login URL", urlJsonObj);
        Log.i("Login Object", jsonObject.toString());

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                urlJsonObj, jsonObject,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        if (response == null || response.length() == 0){
                            stopLoader();
                            Toast.makeText(LoginActivity.this, "Gagal terkoneksi server", Toast.LENGTH_LONG).show();
                        }else{
                            if (type.equals("merge")) {
                                checkMergeResponse(response);
                            } else if (type.equals("login")) {
                                processResponse(response);
                            } else if (type.equals("update")) {
                                updateCustomerRespon(response);
                            }
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                stopLoader();
                Log.i("Error", error.toString());

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(LoginActivity.this);
                alertDialogBuilder.setMessage("Login Gagal");
                alertDialogBuilder.setNegativeButton("Tutup", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                    }
                });

                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
            }
        });

        AppController.getInstance().addToRequestQueue(jsonObjReq);
    }

    // STRING REQUEST
    public void stringRequest(String url) {
        StringRequest strReq = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (response == null || response.length() == 0){
                    stopLoader();
                    Toast.makeText(LoginActivity.this, "Gagal terkoneksi server", Toast.LENGTH_LONG).show();
                }else{
                    getLastestCartIdResponse(response);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(LoginActivity.this, "Gagal terkoneksi server", Toast.LENGTH_LONG).show();
                stopLoader();
            }
        });

        AppController.getInstance().addToRequestQueue(strReq);
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
            if (from.equals("klikindomaret") || from.equals("kai")){
                finish();
                overridePendingTransition(R.anim.right_in, R.anim.right_out);
                return true;
            }else{
                setResult(RESULT_CANCELED, intent);
                finish();
                return true;
            }
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mCallbackManager.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_GOOGLE_SIGN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }
    }

    private void handleSignInResult(GoogleSignInResult result) {
        if (result.isSuccess()) {
            try {
                GoogleSignInAccount acct = result.getSignInAccount();
                JSONObject dataAccount = new JSONObject();
                dataAccount.put("email", acct.getEmail());
                dataAccount.put("name", acct.getDisplayName());

                arrayRequest(API.getInstance().getApiEmail()+"?Email="+acct.getEmail(), dataAccount.toString(), "google");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
    }

    public void runLoader(){
        preloader.setVisibility(View.VISIBLE);
        preloaderShadow.setVisibility(View.VISIBLE);
        setEnable(false);
    }

    public void stopLoader(){
        preloader.setVisibility(View.GONE);
        preloaderShadow.setVisibility(View.GONE);
        setEnable(true);
    }

    public void setEnable(boolean value){
        for ( int i = 0; i < linearParent.getChildCount();  i++ ){
            View view = linearParent.getChildAt(i);
            view.setEnabled(value); // Or whatever you want to do with the view.
        }
    }

    public void onBackPressed() {
        if (from.equals("klikindomaret") || from.equals("kai")){
            finish();
            overridePendingTransition(R.anim.right_in, R.anim.right_out);
        }else{
            setResult(RESULT_CANCELED, intent);
            finish();
        }
    }
}