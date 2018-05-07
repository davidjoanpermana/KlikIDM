package com.indomaret.klikindomaret.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.TabLayout;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.android.gms.analytics.ecommerce.ProductAction;
import com.indomaret.klikindomaret.API;
import com.indomaret.klikindomaret.MainActivity;
import com.indomaret.klikindomaret.R;
import com.indomaret.klikindomaret.adapter.ViewPagerAdapter;
import com.indomaret.klikindomaret.app.AppController;
import com.indomaret.klikindomaret.helper.SessionManager;
import com.indomaret.klikindomaret.helper.UncaughtExceptionHandler;
import com.indomaret.klikindomaret.views.BadgeDrawable;
import com.indomaret.klikindomaret.views.WrapContentViewPager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import pl.droidsonroids.gif.GifImageView;

public class VirtualCategoryActivity extends AppCompatActivity implements ViewPager.OnPageChangeListener {
    private SessionManager sessionManager;
    private Intent intent;

    private Toolbar toolbar;
    private LinearLayout virtualVoucher, virtualPulsa, virtualFacebookCredit, virtualPaketInternet, virtualGooglPlay, pager_indicator;
    private RelativeLayout preloader;
    private Spinner gameVoucherNominal, pulsaVoucherNominal, facebookCreditNominal, paketInternetNominal, googlePlayNominal;
    private TextView mTitle;
    private EditText phoneNumber, phoneNumberPaket;
    private LinearLayout buyPulsa, buyVoucher, buyFacbookCredit, buyPaketInternet, buyGooglePlay;

    private JSONArray voucherList, pulsaList, facebokCreditList, paketList, googlePlayList;
    private JSONArray banners = new JSONArray();
    private String url;
    private int totalItemInCart;
    private Tracker mTracker;
    private int dotsCount;
    private int count = 0;
    private ImageView[] dots;
    private Handler handler = new Handler();

    private WrapContentViewPager intro_images;
    private ViewPagerAdapter mAdapter;
    private WrapContentViewPager slideBanner;
    private TabLayout virtualTabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_virtual_category);

        UncaughtExceptionHandler uncaughtExceptionHandler = new UncaughtExceptionHandler();
        uncaughtExceptionHandler.uncaughtException(this);

        AppController application = (AppController) getApplication();
        mTracker = application.getDefaultTracker();

        mTracker.setScreenName("Category Virtual Page");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());

        toolbar = (Toolbar) findViewById(R.id.app_toolbar);
        mTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
        mTitle.setText("Pulsa dan Voucher Game");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        sessionManager = new SessionManager(VirtualCategoryActivity.this);

        preloader = (RelativeLayout) findViewById(R.id.preloader);

        virtualVoucher = (LinearLayout) findViewById(R.id.virtual_voucher);
        virtualPulsa = (LinearLayout) findViewById(R.id.virtual_pulsa);
        virtualFacebookCredit = (LinearLayout) findViewById(R.id.virtual_facebookCredit);
        virtualPaketInternet = (LinearLayout) findViewById(R.id.virtual_paket_internet);
        virtualGooglPlay = (LinearLayout) findViewById(R.id.virtual_googlePlay);

        buyVoucher = (LinearLayout) findViewById(R.id.btn_buy_voucher);
        buyPulsa = (LinearLayout) findViewById(R.id.btn_buy_pulsa);
        buyFacbookCredit = (LinearLayout) findViewById(R.id.btn_buy_facebookCredit);
        buyPaketInternet = (LinearLayout) findViewById(R.id.btn_buy_paket);
        buyGooglePlay = (LinearLayout) findViewById(R.id.btn_buy_googlePlay);
        virtualTabLayout = (TabLayout) findViewById(R.id.virtual_tabs);

        buyVoucher.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int index = gameVoucherNominal.getSelectedItemPosition();
                        if(index > 0){
                            preloader.setVisibility(View.VISIBLE);

                            try {
                                JSONObject voucher = voucherList.getJSONObject(index-1);

                                com.google.android.gms.analytics.ecommerce.Product productModel =  new com.google.android.gms.analytics.ecommerce.Product()
                                        .setId(voucher.getString("ID"))
                                        .setName(voucher.getString("Description"))
                                        .setBrand(voucher.getJSONObject("ProductBrand").getString("Name"))
                                        .setPosition(1)
                                        .setCustomDimension(1, "Produk");
                                ProductAction productAction = new ProductAction(ProductAction.ACTION_ADD)
                                        .setProductActionList("Voucher Game");
                                HitBuilders.ScreenViewBuilder builder = new HitBuilders.ScreenViewBuilder()
                                        .addProduct(productModel)
                                        .setProductAction(productAction);

                                AppController application = (AppController) getApplication();
                                Tracker t = application.getDefaultTracker();
                                t.setScreenName("Add Product Virtual");
                                t.send(builder.build());

                                makeJsonArrayGetVoucherGame(API.getInstance().getApiModifyCart()
                                        +"?cartRef="
                                        +"&pId=" + voucher.getString("ID")
                                        +"&mod=add"
                                        +"&qty=1"
                                        +"&regionID=" + sessionManager.getRegionID()
                                        +"&id="
                                        +"&scId=" + sessionManager.getCartId()
                                        +"&cId=" + sessionManager.getUserID()
                                        +"&isPair=false"
                                        +"&mfp_id=" + sessionManager.getKeyMfpId(), "modify");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
        );

        buyFacbookCredit.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int index = facebookCreditNominal.getSelectedItemPosition();
                        if(index > 0){
                            preloader.setVisibility(View.VISIBLE);

                            try {
                                JSONObject voucher = facebokCreditList.getJSONObject(index-1);

                                com.google.android.gms.analytics.ecommerce.Product productModel =  new com.google.android.gms.analytics.ecommerce.Product()
                                        .setId(voucher.getString("ID"))
                                        .setName(voucher.getString("Description"))
                                        .setBrand(voucher.getJSONObject("ProductBrand").getString("Name"))
                                        .setPosition(1)
                                        .setCustomDimension(1, "Produk");
                                ProductAction productAction = new ProductAction(ProductAction.ACTION_ADD)
                                        .setProductActionList("Voucher Facebook");
                                HitBuilders.ScreenViewBuilder builder = new HitBuilders.ScreenViewBuilder()
                                        .addProduct(productModel)
                                        .setProductAction(productAction);

                                AppController application = (AppController) getApplication();
                                Tracker t = application.getDefaultTracker();
                                t.setScreenName("Add Product Virtual");
                                t.send(builder.build());

                                makeJsonArrayGetVoucherGame(API.getInstance().getApiModifyCart()
                                        +"?cartRef="
                                        +"&pId=" + voucher.getString("ID")
                                        +"&mod=add"
                                        +"&qty=1"
                                        +"&regionID=" + sessionManager.getRegionID()
                                        +"&id="
                                        +"&scId=" + sessionManager.getCartId()
                                        +"&cId=" + sessionManager.getUserID()
                                        +"&isPair=false"
                                        +"&mfp_id=" + sessionManager.getKeyMfpId(), "modify");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                    }
                }
        );

        buyPulsa.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int index = pulsaVoucherNominal.getSelectedItemPosition();

                        if(phoneNumber.getText().toString().length() < 9){
                            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(VirtualCategoryActivity.this);
                            alertDialogBuilder.setMessage("Nomor Hanphone tidak boleh kurang dari 9");
                            alertDialogBuilder.setNegativeButton("Tutup", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            });

                            AlertDialog alertDialog = alertDialogBuilder.create();
                            alertDialog.show();
                        } else if(index > 0){
                            preloader.setVisibility(View.VISIBLE);

                            try {
                                JSONObject pulsa = pulsaList.getJSONObject(index-1);

                                com.google.android.gms.analytics.ecommerce.Product productModel =  new com.google.android.gms.analytics.ecommerce.Product()
                                        .setId(pulsa.getString("ID"))
                                        .setName(pulsa.getString("Description"))
                                        .setBrand(pulsa.getJSONObject("ProductBrand").getString("Name"))
                                        .setPosition(1)
                                        .setCustomDimension(1, "Produk");
                                ProductAction productAction = new ProductAction(ProductAction.ACTION_ADD)
                                        .setProductActionList("Pulsa");
                                HitBuilders.ScreenViewBuilder builder = new HitBuilders.ScreenViewBuilder()
                                        .addProduct(productModel)
                                        .setProductAction(productAction);

                                AppController application = (AppController) getApplication();
                                Tracker t = application.getDefaultTracker();
                                t.setScreenName("Add Product Virtual");
                                t.send(builder.build());

                                makeJsonArrayGetVoucherGame(API.getInstance().getApiModifyCart()
                                        +"?cartRef="
                                        +"&pId=" + pulsa.getString("ID")+";"+phoneNumber.getText().toString()
                                        +"&mod=add"
                                        +"&qty=1"
                                        +"&regionID=" + sessionManager.getRegionID()
                                        +"&id="
                                        +"&scId=" + sessionManager.getCartId()
                                        +"&cId=" + sessionManager.getUserID()
                                        +"&isPair=false"
                                        +"&mfp_id=" + sessionManager.getKeyMfpId(), "modify");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                    }
                }
        );

        buyPaketInternet.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int index = paketInternetNominal.getSelectedItemPosition();

                        if(phoneNumberPaket.getText().toString().length() < 9){
                            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(VirtualCategoryActivity.this);
                            alertDialogBuilder.setMessage("Nomor Hanphone tidak boleh kurang dari 9");
                            alertDialogBuilder.setNegativeButton("Tutup", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            });

                            AlertDialog alertDialog = alertDialogBuilder.create();
                            alertDialog.show();
                        } else if(index > 0){
                            preloader.setVisibility(View.VISIBLE);

                            try {
                                JSONObject paket = paketList.getJSONObject(index-1);

                                com.google.android.gms.analytics.ecommerce.Product productModel =  new com.google.android.gms.analytics.ecommerce.Product()
                                        .setId(paket.getString("ID"))
                                        .setName(paket.getString("Description"))
                                        .setBrand(paket.getJSONObject("ProductBrand").getString("Name"))
                                        .setPosition(1)
                                        .setCustomDimension(1, "Produk");
                                ProductAction productAction = new ProductAction(ProductAction.ACTION_ADD)
                                        .setProductActionList("Paket Internet");
                                HitBuilders.ScreenViewBuilder builder = new HitBuilders.ScreenViewBuilder()
                                        .addProduct(productModel)
                                        .setProductAction(productAction);

                                AppController application = (AppController) getApplication();
                                Tracker t = application.getDefaultTracker();
                                t.setScreenName("Add Product Virtual");
                                t.send(builder.build());

                                makeJsonArrayGetVoucherGame(API.getInstance().getApiModifyCart()
                                        +"?cartRef="
                                        +"&pId=" + paket.getString("ID")+";"+phoneNumberPaket.getText().toString()
                                        +"&mod=add"
                                        +"&qty=1"
                                        +"&regionID=" + sessionManager.getRegionID()
                                        +"&id="
                                        +"&scId=" + sessionManager.getCartId()
                                        +"&cId=" + sessionManager.getUserID()
                                        +"&isPair=false"
                                        +"&mfp_id=" + sessionManager.getKeyMfpId(), "modify");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                    }
                }
        );

        buyGooglePlay.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int index = googlePlayNominal.getSelectedItemPosition();

                        if(index > 0){
                            preloader.setVisibility(View.VISIBLE);

                            try {
                                JSONObject voucher = googlePlayList.getJSONObject(index-1);

                                makeJsonArrayGetVoucherGame(API.getInstance().getApiModifyCart()
                                        +"?cartRef="
                                        +"&pId=" + voucher.getString("ID")
                                        +"&mod=add"
                                        +"&qty=1"
                                        +"&regionID=" + sessionManager.getRegionID()
                                        +"&id="
                                        +"&scId=" + sessionManager.getCartId()
                                        +"&cId=" + sessionManager.getUserID()
                                        +"&isPair=false"
                                        +"&mfp_id=" + sessionManager.getKeyMfpId(), "modify");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                    }
                }
        );

        virtualTabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int tabIndex = tab.getPosition();
                if(tabIndex == 0)
                {
                    googlePlayNominal.setSelection(0);
                    virtualGooglPlay.setVisibility(View.VISIBLE);
                    virtualPulsa.setVisibility(View.GONE);
                    virtualPaketInternet.setVisibility(View.GONE);
                    virtualVoucher.setVisibility(View.GONE);
                    virtualFacebookCredit.setVisibility(View.GONE);
                }
                else if(tabIndex == 1)
                {
                    phoneNumber.setText("");
                    virtualGooglPlay.setVisibility(View.GONE);
                    virtualPulsa.setVisibility(View.VISIBLE);
                    virtualPaketInternet.setVisibility(View.GONE);
                    virtualVoucher.setVisibility(View.GONE);
                    virtualFacebookCredit.setVisibility(View.GONE);
                }
                else if(tabIndex == 2)
                {
                    phoneNumberPaket.setText("");
                    virtualGooglPlay.setVisibility(View.GONE);
                    virtualPulsa.setVisibility(View.GONE);
                    virtualPaketInternet.setVisibility(View.VISIBLE);
                    virtualVoucher.setVisibility(View.GONE);
                    virtualFacebookCredit.setVisibility(View.GONE);
                }
                else if(tabIndex == 3)
                {
                    virtualGooglPlay.setVisibility(View.GONE);
                    virtualPulsa.setVisibility(View.GONE);
                    virtualPaketInternet.setVisibility(View.GONE);
                    virtualVoucher.setVisibility(View.GONE);
                    virtualFacebookCredit.setVisibility(View.GONE);

                    intent = new Intent(VirtualCategoryActivity.this, HomeKAIActivity.class);
                    intent.putExtra("from", "klikindomaret");
                    startActivity(intent);
                    overridePendingTransition(R.anim.left_in, R.anim.left_out);
                }
                else if(tabIndex == 4)
                {
                    gameVoucherNominal.setSelection(0);
                    virtualGooglPlay.setVisibility(View.GONE);
                    virtualPulsa.setVisibility(View.GONE);
                    virtualPaketInternet.setVisibility(View.GONE);
                    virtualVoucher.setVisibility(View.VISIBLE);
                    virtualFacebookCredit.setVisibility(View.GONE);
                }
                else if(tabIndex == 5)
                {
                    facebookCreditNominal.setSelection(0);
                    virtualGooglPlay.setVisibility(View.GONE);
                    virtualPulsa.setVisibility(View.GONE);
                    virtualPaketInternet.setVisibility(View.GONE);
                    virtualVoucher.setVisibility(View.GONE);
                    virtualFacebookCredit.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        if(sessionManager.getCartId() != null){
            url = API.getInstance().getCartTotal()
                    +"?cartId=" + sessionManager.getCartId()
                    +"&customerId=" + sessionManager.getUserID()
                    +"&mfp_id=" + sessionManager.getKeyMfpId();

            makeJsonArrayGetVoucherGame(url, "cart");
        }

        makeJsonArrayGetVoucherGame(API.getInstance().getApiVirtual()+"?mfp_id="+sessionManager.getKeyMfpId(), "virtual");
//        makeJsonArrayGetVoucherGame(API.getInstance().getApiVoucherGameOnline()+"?mfp_id="+sessionManager.getKeyMfpId(), "voucher");
//        makeJsonArrayGetVoucherGame(API.getInstance().getApiGetVoucherFacebook()+"?mfp_id="+sessionManager.getKeyMfpId(), "facebook");
//        makeJsonArrayGetVoucherGame(API.getInstance().getApiGetGooglePlay()+"?mfp_id="+sessionManager.getKeyMfpId(), "googlePlay");
        checkPulsaNumber();
        checkPaketNumber();
        setEmptyPulsaList();
        setEmptyPaketList();

        makeJsonArrayGetVoucherGame(API.getInstance().getApiBannerVirtual(), "banner");
    }

    public void setFeaturedProduct() {
        intro_images = (WrapContentViewPager) findViewById(R.id.pager_introduction);
        pager_indicator = (LinearLayout) findViewById(R.id.viewPagerCountDots);
        pager_indicator.removeAllViews();

        mAdapter = new ViewPagerAdapter(VirtualCategoryActivity.this, banners);

        intro_images.setAdapter(mAdapter);
        intro_images.setCurrentItem(0);
        intro_images.addOnPageChangeListener(VirtualCategoryActivity.this);

        setUiPageViewController();
    }

    private void setUiPageViewController() {
        count = 0;
        dotsCount = mAdapter.getCount();
        dots = new ImageView[dotsCount];

        for (int i = 0; i < dotsCount; i++) {
            dots[i] = new ImageView(VirtualCategoryActivity.this);
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

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if (count <= banners.length()) {
                intro_images.setCurrentItem(count);
                count++;
            } else {
                count = 0;
                intro_images.setCurrentItem(count);
            }
            handler.postDelayed(this, 5000);
        }
    };

    public void setSlideBanner(JSONArray response) {
        JSONArray banners = new JSONArray();
        try {

            JSONObject objectResponse = response.getJSONObject(0);

            banners = response;

            slideBanner = (WrapContentViewPager) findViewById(R.id.pager_banner);
            mAdapter = new ViewPagerAdapter(VirtualCategoryActivity.this, banners);

            slideBanner.setAdapter(mAdapter);
            slideBanner.setCurrentItem(0);
            slideBanner.addOnPageChangeListener(this);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    //get list voucher game
    public void makeJsonArrayGetVoucherGame(String url, final String check) {
        System.out.println("Url Virtual = "+url);

        JsonArrayRequest jsonArrayReq = new JsonArrayRequest(url,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
//                        if(check.equals("voucher")){
//                            setGameVoucherList(response);
//                        } else if(check.equals("facebook")){
//                            setFacebookCreditList(response);
//                        }else if(check.equals("pulsa")){
//                            setPulsaList(response);
//                        } else if (check.equals("paket")){
//                            setPaketList(response);
//                        }else if (check.equals("googlePlay")){
//                            setGooglePlayList(response);
//                        }else

                        if(check.equals("cart")){
                            try {
                                updateCartTotal(response.getInt(0));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        } else if(check.equals("modify")){
                            addToCart(response);
                        } else if(check.equals("virtual")){
                            try {
                                setGameVoucherList(response.getJSONObject(0).getJSONArray("ListVoucherGameOnline"));
                                setFacebookCreditList(response.getJSONObject(0).getJSONArray("ListVoucherFacebook"));
                                setGooglePlayList(response.getJSONObject(0).getJSONArray("ListGooglePlayCard"));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }else if(check.equals("pulsa")){
                            setPulsaList(response);
                        } else if (check.equals("paket")){
                            setPaketList(response);
                        }else if (check.equals("banner")){
                            banners = response;
                            setFeaturedProduct();
                        }

                        preloader.setVisibility(View.GONE);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(VirtualCategoryActivity.this, "Gagal terhubung, geser kebawah untuk refresh", Toast.LENGTH_LONG).show();
                preloader.setVisibility(View.GONE);
            }
        }, this);

        AppController.getInstance().addToRequestQueue(jsonArrayReq);
    }

    public void addToCart(JSONArray response){
        try {
            if (sessionManager.getCartId() == null || sessionManager.getCartId().equals("00000000-0000-0000-0000-000000000000")){
                sessionManager.setCartId(response.getJSONObject(0).getString("ResponseID"));
            }

            JSONObject cart = response.getJSONObject(0);
            System.out.println(response);

            if(sessionManager.getCartId() == null){
                if (cart.getString("ResponseID")!=null){
                    sessionManager.setCartId(cart.getString("ResponseID"));
                }
            }

            if(cart.getBoolean("Success")){
                makeJsonArrayGetVoucherGame(API.getInstance().getCartTotal()
                        +"?cartId=" + sessionManager.getCartId()
                        +"&customerId=" + sessionManager.getUserID()
                        +"&mfp_id=" + sessionManager.getKeyMfpId() , "cart");

                final Toast toast = Toast.makeText(this, "Produk masuk ke keranjang", Toast.LENGTH_SHORT);
                toast.show();

                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        toast.cancel();
                    }
                }, 500);
            } else{
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(VirtualCategoryActivity.this);
                alertDialogBuilder.setMessage(cart.getString("ErrorMessage"));
                alertDialogBuilder.setNegativeButton("Tutup", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void setGameVoucherList(JSONArray response){
        voucherList = response;
        gameVoucherNominal = (Spinner) findViewById(R.id.nominal_voucher);

        List<String> voucherName = new ArrayList<>();
        voucherName.add(" -- Pilih Voucher -- ");
        try {
            for (int i=0; i<response.length(); i++){
                voucherName.add(response.getJSONObject(i).getString("Name"));
            }
        } catch(Exception e){
            e.printStackTrace();
        }

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(this,android.R.layout.simple_spinner_item, voucherName);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        gameVoucherNominal.setAdapter(dataAdapter);
    }

    public  void setFacebookCreditList(JSONArray response){
        facebokCreditList = response;
        facebookCreditNominal = (Spinner) findViewById(R.id.nominal_facebookCredit);

        List<String> facebookCredit = new ArrayList<>();
        facebookCredit.add(" -- Pilih Voucher -- ");
        try {
            for (int i = 0; i < response.length(); i++){
                facebookCredit.add(response.getJSONObject(i).getString("Title"));
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(this,android.R.layout.simple_spinner_item, facebookCredit);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        facebookCreditNominal.setAdapter(dataAdapter);
    }

    public  void setGooglePlayList(JSONArray response){
        googlePlayList = response;
        googlePlayNominal = (Spinner) findViewById(R.id.nominal_googlePlay);

        List<String> googlePlay = new ArrayList<>();
        googlePlay.add(" -- Pilih Voucher -- ");
        try {
            for (int i = 0; i < response.length(); i++){
                googlePlay.add(response.getJSONObject(i).getString("Title"));
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(this,android.R.layout.simple_spinner_item, googlePlay);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        googlePlayNominal.setAdapter(dataAdapter);
    }

    public void checkPulsaNumber(){
        phoneNumber = (EditText) findViewById(R.id.phone_number);

        phoneNumber.addTextChangedListener(
                new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {

                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        if (s.length() > 3) {
                            makeJsonArrayGetVoucherGame(API.getInstance().getApiCheckPulsaNumber() + "/" + s + "?mfp_id=" + sessionManager.getKeyMfpId(), "pulsa");
                        } else {
                            setEmptyPulsaList();
                        }
                    }
                }
        );
    }

    public void checkPaketNumber(){
        phoneNumberPaket = (EditText) findViewById(R.id.phone_number_paket);

        phoneNumberPaket.addTextChangedListener(
                new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {

                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        if (s.length() > 3) {
                            makeJsonArrayGetVoucherGame(API.getInstance().getApiGetPaketInternet() + "/" + s + "?mfp_id=" + sessionManager.getKeyMfpId(), "paket");
                        } else {
                            setEmptyPaketList();
                        }
                    }
                }
        );
    }

    public void setPulsaList(JSONArray response){
        pulsaList = response;
        pulsaVoucherNominal = (Spinner) findViewById(R.id.nominal_pulsa);
        List<String> pulsaNominal = new ArrayList<>();

        if (response.length() == 0) pulsaNominal.add(" Kode provider anda tidak terdaftar ");
        else pulsaNominal.add(" -- Pilih Nominal -- ");

        try {
            for (int i=0; i<response.length(); i++){
                pulsaNominal.add(response.getJSONObject(i).getString("Title"));
            }
        } catch(Exception e){
            e.printStackTrace();
        }

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, pulsaNominal);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        pulsaVoucherNominal.setAdapter(dataAdapter);
    }

    public void setPaketList(JSONArray response){
        paketList = response;
        paketInternetNominal = (Spinner) findViewById(R.id.nominal_paket);
        List<String> paketNominal = new ArrayList<>();

        if (response.length() == 0) paketNominal.add(" Kode provider anda tidak terdaftar ");
        else paketNominal.add(" -- Pilih Nominal -- ");

        try {
            for (int i=0; i<response.length(); i++){
                paketNominal.add(response.getJSONObject(i).getString("Title"));
            }
        } catch(Exception e){
            e.printStackTrace();
        }

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, paketNominal);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        paketInternetNominal.setAdapter(dataAdapter);
    }

    public void setEmptyPulsaList(){
        pulsaVoucherNominal = (Spinner) findViewById(R.id.nominal_pulsa);
        List<String> pulsaNominal = new ArrayList<>();
        pulsaNominal.add("Isikan Nomor Handphone");
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(this,android.R.layout.simple_spinner_item, pulsaNominal);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        pulsaVoucherNominal.setAdapter(dataAdapter);
    }

    public void setEmptyPaketList(){
        paketInternetNominal = (Spinner) findViewById(R.id.nominal_paket);
        List<String> paketNominal = new ArrayList<>();
        paketNominal.add("Isikan Nomor Handphone");
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(this,android.R.layout.simple_spinner_item, paketNominal);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        paketInternetNominal.setAdapter(dataAdapter);
    }

    public void updateCartTotal(int total) {
        totalItemInCart = total;
        this.invalidateOptionsMenu();
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
    @SuppressWarnings("deprecation")
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.category_menu, menu);
        MenuItem itemCart = menu.findItem(R.id.keranjang_top_btn);
        LayerDrawable icon = (LayerDrawable) itemCart.getIcon();
        setBadgeCount(this, icon, ""+totalItemInCart);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            finish();
            overridePendingTransition(R.anim.right_in, R.anim.right_out);
            return true;
        } else if (id == R.id.keranjang_top_btn) {
            if(totalItemInCart == 0){
                android.support.v7.app.AlertDialog.Builder alertDialogBuilder = new android.support.v7.app.AlertDialog.Builder(VirtualCategoryActivity.this);
                alertDialogBuilder.setMessage("Keranjang belanja Anda kosong.");
                alertDialogBuilder.setNegativeButton("Mulai Belanja", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {}
                });

                android.support.v7.app.AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
            }
            else if(sessionManager.isLoggedIn()){
                intent = new Intent(VirtualCategoryActivity.this, CartActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.left_in, R.anim.left_out);
            } else {
                intent = new Intent(VirtualCategoryActivity.this, LoginActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.left_in, R.anim.left_out);
            }

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume(){
        super.onResume();

        mTracker.setScreenName("Virtual Category Page");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());

        if(sessionManager.getCartId() != null){
            String cartUrl = API.getInstance().getCartTotal()+"?cartId=" + sessionManager.getCartId()+"&customerId=" + sessionManager.getUserID()+"&mfp_id=" + sessionManager.getKeyMfpId();
            makeJsonArrayGetVoucherGame(cartUrl, "cart");
        } else {
            sessionManager.setCartId("00000000-0000-0000-0000-000000000000");
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
}
