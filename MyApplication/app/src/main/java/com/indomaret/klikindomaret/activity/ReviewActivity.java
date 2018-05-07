package com.indomaret.klikindomaret.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.indomaret.klikindomaret.API;
import com.indomaret.klikindomaret.R;
import com.indomaret.klikindomaret.adapter.ReviewAdapter;
import com.indomaret.klikindomaret.app.AppController;
import com.indomaret.klikindomaret.helper.SessionManager;
import com.indomaret.klikindomaret.helper.UncaughtExceptionHandler;
import com.indomaret.klikindomaret.views.HeightAdjustableListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import pl.droidsonroids.gif.GifImageView;

public class ReviewActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private TextView mTitle;
    private HeightAdjustableListView reviewList;
    private Button writeReview;
    private RelativeLayout preloader;

    private Intent intent;
    private SessionManager sessionManager;
    private ReviewAdapter reviewAdapter;

    String productId, productPermalink, urlGetReview, urlWriteReview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review);

        UncaughtExceptionHandler uncaughtExceptionHandler = new UncaughtExceptionHandler();
        uncaughtExceptionHandler.uncaughtException(this);

        intent = getIntent();
        sessionManager = new SessionManager(ReviewActivity.this);

        productId = intent.getStringExtra("productid");
        productPermalink = intent.getStringExtra("permalink");
        urlGetReview = API.getInstance().getApiGetReview()+"?ProductID="+productId+"&mfp_id="+sessionManager.getKeyMfpId();
        urlWriteReview = API.getInstance().getApiWriteReview()+"?mfp_id="+sessionManager.getKeyMfpId();

        toolbar = (Toolbar) findViewById(R.id.app_toolbar);
        mTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
        mTitle.setText("Ulasan");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        preloader = (RelativeLayout) findViewById(R.id.preloader);

        reviewList = (HeightAdjustableListView) findViewById(R.id.review_list);
        writeReview = (Button) findViewById(R.id.write_review);

        writeReview.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(sessionManager.isLoggedIn()){
                            LayoutInflater inflater = (LayoutInflater) ReviewActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                            View writeReview = null;

                            if (writeReview == null) {
                                writeReview = inflater.inflate(R.layout.activity_review_write, null);
                            }

                            final RatingBar rateReview = (RatingBar) writeReview.findViewById(R.id.rate_review);
                            final EditText contentReview = (EditText) writeReview.findViewById(R.id.content_review);
                            Button cancelReview = (Button) writeReview.findViewById(R.id.cancel_review);
                            Button sendReview = (Button) writeReview.findViewById(R.id.send_review);

                            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ReviewActivity.this);
                            alertDialogBuilder.setView(writeReview);
                            final AlertDialog alertDialog = alertDialogBuilder.create();
                            alertDialog.setCanceledOnTouchOutside(false);
                            alertDialog.show();

                            cancelReview.setOnClickListener(
                                    new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            alertDialog.hide();
                                        }
                                    }
                            );

                            sendReview.setOnClickListener(
                                    new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            Float rate = rateReview.getRating();
                                            String reviewContent = contentReview.getText().toString();

                                            JSONObject writeReviewObject = new JSONObject();

                                            try {
                                                writeReviewObject.put("ID", "00000000-0000-0000-0000-000000000000");
                                                writeReviewObject.put("CustomerID", sessionManager.getUserID());
                                                writeReviewObject.put("ProductID", productId);
                                                writeReviewObject.put("Body", reviewContent);
                                                writeReviewObject.put("OverallRating", rate);
                                                writeReviewObject.put("IPAddress", "");
                                                writeReviewObject.put("OwnProduct", false);
                                                writeReviewObject.put("Created", "0001-01-01T00:00:00");
                                                writeReviewObject.put("AliasName", null);
                                                writeReviewObject.put("CustomerName", null);
                                                writeReviewObject.put("CustomerEmail", null);
                                                writeReviewObject.put("CreatedText", null);
                                                writeReviewObject.put("Permalink", productPermalink);
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }

                                            jsonPost(urlWriteReview, writeReviewObject);

                                            alertDialog.hide();

                                            preloader.setVisibility(View.VISIBLE);
                                        }
                                    }
                            );
                        } else {
                            intent = new Intent(ReviewActivity.this, LoginActivity.class);
                            startActivity(intent);
                            overridePendingTransition(R.anim.left_out, R.anim.left_in);
                        }

                    }
                }
        );

        jsonArrayRequest(urlGetReview);
    }

    public void jsonArrayRequest(String url){
        preloader.setVisibility(View.VISIBLE);

        JsonArrayRequest jsonArrayReq = new JsonArrayRequest(url,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        if (response == null || response.length() == 0){
                            preloader.setVisibility(View.GONE);
                            Toast.makeText(ReviewActivity.this, "Gagal terkoneksi server", Toast.LENGTH_LONG).show();
                        }else{
                            reviewListResponse(response);
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(ReviewActivity.this, "Gagal terkoneksi server", Toast.LENGTH_LONG).show();
                preloader.setVisibility(View.GONE);

            }
        }, this);

        jsonArrayReq.setRetryPolicy(new DefaultRetryPolicy(60000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppController.getInstance().addToRequestQueue(jsonArrayReq);
    }

    //POST REQUEST
    public void jsonPost(String urlJsonObj, JSONObject jsonObject){
        System.out.println("review = " + urlJsonObj);
        System.out.println("review = " + jsonObject);

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                urlJsonObj, jsonObject,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        if (response == null || response.length() == 0){
                            preloader.setVisibility(View.GONE);
                            Toast.makeText(ReviewActivity.this, "Gagal terkoneksi server", Toast.LENGTH_LONG).show();
                        }else{
                            writeReviewResponse(response);
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(ReviewActivity.this, "Gagal terkoneksi server", Toast.LENGTH_LONG).show();
                preloader.setVisibility(View.GONE);
            }
        });

        AppController.getInstance().addToRequestQueue(jsonObjReq);
    }

    public void writeReviewResponse(JSONObject response){
        System.out.println("review = " + response);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ReviewActivity.this);

        try {
            if(response.getBoolean("IsSuccess")){
                jsonArrayRequest(urlGetReview);
                alertDialogBuilder.setMessage("Berhasil menambahkan ulasan.");
            } else {
                 alertDialogBuilder.setMessage("Gagal menambahkan ulasan.");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        alertDialogBuilder.setNegativeButton("Tutup", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {}
        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.show();
    }

    public void reviewListResponse(JSONArray response){
        reviewAdapter = new ReviewAdapter(ReviewActivity.this, response);
        reviewList.setAdapter(reviewAdapter);

        preloader.setVisibility(View.GONE);
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
}
