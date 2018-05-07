package com.indomaret.klikindomaret.activity;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.google.zxing.Result;
import com.indomaret.klikindomaret.API;
import com.indomaret.klikindomaret.MainActivity;
import com.indomaret.klikindomaret.R;
import com.indomaret.klikindomaret.app.AppController;
import com.indomaret.klikindomaret.helper.SessionManager;
import com.indomaret.klikindomaret.helper.UncaughtExceptionHandler;

import org.json.JSONArray;
import org.json.JSONException;

import me.dm7.barcodescanner.core.IViewFinder;
import me.dm7.barcodescanner.core.ViewFinderView;
import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class ScannerActivity extends BaseScannerActivity implements ZXingScannerView.ResultHandler{
    private ZXingScannerView mScannerView;
    private Toolbar toolbar;
    private TextView mTitle, infoText;
    private Button btnSetting, btnRequest;
    private LinearLayout linear;
    private ViewGroup contentFrame;
    private SharedPreferences prefs;
    private int PERMISSIONS_CODE = 1;
    private SessionManager sessionManager;
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanner);

        UncaughtExceptionHandler uncaughtExceptionHandler = new UncaughtExceptionHandler();
        uncaughtExceptionHandler.uncaughtException(this);

        toolbar = (Toolbar) findViewById(R.id.app_toolbar);
        mTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
        mTitle.setText("Pencarian Barcode");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        sessionManager = new SessionManager(ScannerActivity.this);
        contentFrame = (ViewGroup) findViewById(R.id.content_frame);
        linear = (LinearLayout) findViewById(R.id.linear_setting);
        infoText = (TextView) findViewById(R.id.info_text);
        btnSetting = (Button) findViewById(R.id.btn_setting);
        btnRequest = (Button) findViewById(R.id.btn_request);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, PERMISSIONS_CODE);
        }

        mScannerView = new ZXingScannerView(this) {
            @Override
            protected IViewFinder createViewFinderView(Context context) {
                return new CustomViewFinderView(context);
            }
        };

        btnSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                Uri uri = Uri.fromParts("package", getPackageName(), null);
                intent.setData(uri);
                startActivityForResult(intent, PERMISSIONS_CODE);
            }
        });

        btnRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ActivityCompat.checkSelfPermission(ScannerActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ) {
                    ActivityCompat.requestPermissions(ScannerActivity.this, new String[]{Manifest.permission.CAMERA}, PERMISSIONS_CODE);
                }
            }
        });

        contentFrame.addView(mScannerView);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSIONS_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
                boolean showRationale = false;
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                    showRationale = shouldShowRequestPermissionRationale( permissions[0] );
                }

                if (!showRationale) {
                    contentFrame.setVisibility(View.GONE);
                    linear.setVisibility(View.VISIBLE);
                    btnSetting.setVisibility(View.VISIBLE);
                    btnRequest.setVisibility(View.GONE);
                    infoText.setText("Fitur Pencarian Barcode tidak bisa dijalankan. Apabila Anda ingin menggunakan fitur Pencarian Barcode mohon mengaktifkan permission camera dengan menekan tombol pengaturan dibawah." +
                            "\n\nMasuk <App permissions> kemudian aktifkan <Camera>.");
                } else if (Manifest.permission.CAMERA.equals(permissions[0])) {
                    contentFrame.setVisibility(View.GONE);
                    linear.setVisibility(View.VISIBLE);
                    btnSetting.setVisibility(View.GONE);
                    btnRequest.setVisibility(View.VISIBLE);
                    infoText.setText("Fitur Pencarian Barcode tidak bisa dijalankan. Apabila Anda ingin menggunakan fitur Pencarian Barcode mohon mengaktifkan permission camera dengan menekan tombol pengaturan dibawah.");
                }
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ) {
            contentFrame.setVisibility(View.GONE);
            linear.setVisibility(View.VISIBLE);
        }else{
            contentFrame.setVisibility(View.VISIBLE);
            linear.setVisibility(View.GONE);
        }

        mScannerView.setResultHandler(this);
        mScannerView.startCamera();
    }

    @Override
    public void onPause() {
        super.onPause();
        mScannerView.stopCamera();
    }

    @Override
    public void handleResult(Result result) {
        jsonArrayRequest(API.getInstance().getSearchByBarcode()+"?mfp_id="+sessionManager.getKeyMfpId()+"&regionID="+sessionManager.getRegionID()+"&barcode="+result.getText());
    }

    private static class CustomViewFinderView extends ViewFinderView {
        public static final String TRADE_MARK_TEXT = "KlikIndomaret";
        public static final int TRADE_MARK_TEXT_SIZE_SP = 30;
        public final Paint PAINT = new Paint();

        public CustomViewFinderView(Context context) {
            super(context);
            init();
        }

        public CustomViewFinderView(Context context, AttributeSet attrs) {
            super(context, attrs);
            init();
        }

        private void init() {
            PAINT.setColor(Color.WHITE);
            PAINT.setAntiAlias(true);
            float textPixelSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, TRADE_MARK_TEXT_SIZE_SP, getResources().getDisplayMetrics());
            PAINT.setTextSize(textPixelSize);
        }

        @Override
        public void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            drawTradeMark(canvas);
        }

        private void drawTradeMark(Canvas canvas) {
            Rect framingRect = getFramingRect();
            float tradeMarkTop;
            float tradeMarkLeft;

            if (framingRect != null) {
                tradeMarkTop = framingRect.bottom + PAINT.getTextSize() + 10;
                tradeMarkLeft = framingRect.left;
            } else {
                tradeMarkTop = 10;
                tradeMarkLeft = canvas.getHeight() - PAINT.getTextSize() - 10;
            }

            canvas.drawText(TRADE_MARK_TEXT, tradeMarkLeft, tradeMarkTop, PAINT);
        }
    }

    public void jsonArrayRequest(String url){
        JsonArrayRequest jsonArrayReq = new JsonArrayRequest(url,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        if (response.length() > 0){
                            barcodeSearch(response);
                        }else{
                            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ScannerActivity.this);
                            alertDialogBuilder.setMessage("Produk tidak ditemukan");
                            alertDialogBuilder.setNegativeButton("Ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    mScannerView.setResultHandler(ScannerActivity.this);
                                    mScannerView.startCamera();
                                }
                            });

                            AlertDialog alertDialog = alertDialogBuilder.create();
                            alertDialog.show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "Gagal terhubung ke server", Toast.LENGTH_LONG).show();
            }
        }, this);

        jsonArrayReq.setRetryPolicy(new DefaultRetryPolicy(60000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppController.getInstance().addToRequestQueue(jsonArrayReq);
    }

    public void barcodeSearch(JSONArray response){
        try {
            intent = new Intent(ScannerActivity.this, ProductActivity.class);
            intent.putExtra("data", response.getJSONObject(0).toString());
            intent.putExtra("cat", "category");
            startActivity(intent);
            finish();
            overridePendingTransition(R.anim.left_in, R.anim.left_out);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
