package com.indomaret.klikindomaret.activity;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.android.gms.analytics.ecommerce.Product;
import com.google.android.gms.analytics.ecommerce.ProductAction;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.indomaret.klikindomaret.API;
import com.indomaret.klikindomaret.R;
import com.indomaret.klikindomaret.adapter.CartAdapter;
import com.indomaret.klikindomaret.adapter.CouponAdapter;
import com.indomaret.klikindomaret.app.AppController;
import com.indomaret.klikindomaret.fragment.PromoFragment;
import com.indomaret.klikindomaret.helper.SQLiteHandler;
import com.indomaret.klikindomaret.helper.SessionManager;
import com.indomaret.klikindomaret.helper.UncaughtExceptionHandler;
import com.indomaret.klikindomaret.utils.Helper;
import com.indomaret.klikindomaret.views.HeightAdjustableListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class CartActivity extends AppCompatActivity  {
    public static Activity cartActivity;
    private Toolbar toolbar;
    private SessionManager sessionManager;
    private SQLiteHandler sqLiteHandler;
    private Intent intent;

    private Tracker mTracker;
    private Runnable runnable;
    private String apiGetCart, apiGetCount;
    private String variant = "";
    private String costStore = "Rp 0";
    private String[] listProductArray;
    private Integer positionNoteNull;
    private int qtys;
    private Double storeTotalPrice = 0.0;
    private Double storeMinShop = 0.0;
    private Double storeMaxShop = 0.0;
    private Double totalPrice = 0.0;
    private Double totalPricePromo = 0.0;
    private Double totalPriceWithDiscount = 0.0;
    private Double totalDiscount = 0.0;
    private Double totalCoupon = 0.0;
    private Double voucher = 0.0;
    private Double shippingCost = 0.0;
    private boolean checkStockProduct = true;
    private boolean promoBool, checkNote, checkStok;
    public boolean isStockEmpty;
    private List<String> coupon;
    private DecimalFormat df = new DecimalFormat("#,###");
    protected LatLng addressLatLng;
    protected GoogleMap mMap;
    private CouponAdapter couponAdapter;

    private List<String> listProductDetail;
    private JSONArray shoppingCartItems = new JSONArray();
    private JSONArray storeProduct = new JSONArray();
    private JSONArray nonStoreProduct = new JSONArray();
    private JSONArray virtualProduct = new JSONArray();
    private JSONArray cartProduct = new JSONArray();
    private JSONArray promoProduct;
    private JSONArray promoProductCart = new JSONArray();
    public JSONArray promoArrays = new JSONArray();
    public HashMap hm = new HashMap();

    private TextView totalTransaction, totalDiscountTransaction, totalPotongan, totalVoucher, titleProductStore, titleProductNonStore, titleProductVirtual,
            totalProductStore, totalProductNonStre, totalProductVirtual, totalShippingCost, voucherText, mTitle, infoVoucher;
    private Button nextToPromo, updateCart, btnVoucher;
    private LinearLayout linearPromo, linearRingkasan, listviewPromo, linearProductStore, linearProductNonStore, linearProductVirtual, linearFooter;
    private RelativeLayout preloader;
    private ScrollView cartContainer;
    private HeightAdjustableListView storeProductList, nonStoreProductList, virtualProductList, couponList;
    private TabLayout promoTabLayout;
    private ViewPager promoViewPager;
    private CartAdapter cartAdapter;
    private ImageView imageArrow, imageLeft, imageRight;

    private AlertDialog.Builder alertDialogBuilder;
    private AlertDialog alertDialog;

    @SuppressWarnings("unused")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        UncaughtExceptionHandler uncaughtExceptionHandler = new UncaughtExceptionHandler();
        uncaughtExceptionHandler.uncaughtException(this);

        toolbar = (Toolbar) findViewById(R.id.app_toolbar);
        mTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
        toolbar.setTitle("");
        mTitle.setText("Keranjang Belanja");
        setSupportActionBar(toolbar);

        sessionManager = new SessionManager(CartActivity.this);
        sqLiteHandler = new SQLiteHandler(CartActivity.this);
        intent = getIntent();
        cartActivity = this;

        apiGetCart = API.getInstance().getApiGetCart()+"?id=&customerId="+sessionManager.getUserID()+"&ShoppingCartID="+sessionManager.getCartId()+"&regionID="+sessionManager.getRegionID()+"&mfp_id="+sessionManager.getKeyMfpId();
        apiGetCount = API.getInstance().getCartTotal()+"?cartId="+sessionManager.getCartId()+"&customerId="+sessionManager.getUserID()+"&mfp_id=" + sessionManager.getKeyMfpId();

        nextToPromo = (Button) findViewById(R.id.next_to_promo);
        updateCart = (Button) findViewById(R.id.update_cart);
        btnVoucher = (Button) findViewById(R.id.btn_voucher);

        totalDiscountTransaction = (TextView) findViewById(R.id.total_discount);
        totalPotongan = (TextView) findViewById(R.id.total_potongan);
        totalVoucher = (TextView) findViewById(R.id.total_voucher);
        titleProductStore = (TextView) findViewById(R.id.title_prodct_store);
        titleProductNonStore = (TextView) findViewById(R.id.title_prodct_nonstore);
        titleProductVirtual = (TextView) findViewById(R.id.title_prodct_virtual);
        totalProductStore = (TextView) findViewById(R.id.total_prodct_store);
        totalProductNonStre = (TextView) findViewById(R.id.total_prodct_nonstore);
        totalProductVirtual = (TextView) findViewById(R.id.total_prodct_virtual);
        totalTransaction = (TextView) findViewById(R.id.total_transaction);
        totalShippingCost = (TextView) findViewById(R.id.total_ongkir);
        voucherText = (EditText) findViewById(R.id.voucher_text);
        infoVoucher = (TextView) findViewById(R.id.info_voucher);

        storeProductList = (HeightAdjustableListView) findViewById(R.id.store_product_list);
        nonStoreProductList = (HeightAdjustableListView) findViewById(R.id.nonstore_product_list);
        virtualProductList = (HeightAdjustableListView) findViewById(R.id.virtual_product_list);
        couponList = (HeightAdjustableListView) findViewById(R.id.coupon_list);

        promoViewPager = (ViewPager) findViewById(R.id.promo_viewpager);
        promoTabLayout = (TabLayout) findViewById(R.id.promo_tabs);

        preloader = (RelativeLayout) findViewById(R.id.preloader);

        linearPromo = (LinearLayout) findViewById(R.id.linear_promo);
        listviewPromo = (LinearLayout) findViewById(R.id.listview_promo);
        linearRingkasan = (LinearLayout) findViewById(R.id.discount_container);
        linearProductStore = (LinearLayout) findViewById(R.id.linear_product_store);
        linearProductNonStore = (LinearLayout) findViewById(R.id.linear_product_nonstore);
        linearProductVirtual = (LinearLayout) findViewById(R.id.linear_product_virtual);
        linearFooter = (LinearLayout) findViewById(R.id.footer);
        cartContainer = (ScrollView) findViewById(R.id.cart_container);
        imageArrow = (ImageView) findViewById(R.id.image_arrow);
        imageLeft = (ImageView) findViewById(R.id.left_nav);
        imageRight= (ImageView) findViewById(R.id.right_nav);

        AppController application = (AppController) getApplication();
        mTracker = application.getDefaultTracker();
        voucherText.setFilters(new InputFilter[]{new InputFilter.AllCaps()});


        promoBool = false;
        checkStok = intent.getBooleanExtra("checkStok", false);

        linearFooter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (linearRingkasan.getVisibility() == View.GONE){
                    imageArrow.setBackgroundResource(R.mipmap.icon_hide);
                    linearRingkasan.setVisibility(View.VISIBLE);
                } else {
                    imageArrow.setBackgroundResource(R.mipmap.icon_show);
                    linearRingkasan.setVisibility(View.GONE);
                }
            }
        });

        nextToPromo.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        runLoader();
                        checkCart();
                    }
                }
        );

        updateCart.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        updateCart();
                    }
                }
        );

        btnVoucher.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                JSONObject object = new JSONObject();

                try {
                    object.put("Code", voucherText.getText().toString());
                    object.put("ShoppingCartID", sessionManager.getCartId());
                    object.put("CustomerID", sessionManager.getUserID());
                    object.put("RegionID", sessionManager.getRegionID());
                    object.put("SalesOrderNo", voucherText.getText().toString());
                    object.put("Nominal", "");

                    jsonPost(API.getInstance().getApiBookingVoucherCoupon()+"?mfp_id="+sessionManager.getKeyMfpId(), object, "voucher");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        setCartRefresh();
        jsonArrayRequest(apiGetCount, "count");
    }

    public void updateCart(){
        JSONArray items = new JSONArray();
        JSONObject updateObject = new JSONObject();
        isStockEmpty = false;

        runLoader();

        try {
            updateObject.put("DeleteOld", false);
            updateObject.put("CartId", sessionManager.getCartId());
            updateObject.put("NewCartId", "00000000-0000-0000-0000-000000000000");
            updateObject.put("OldCustomerId", "00000000-0000-0000-0000-000000000000");
            updateObject.put("NewCustomerID", sessionManager.getUserID());
            updateObject.put("newCart", null);
            updateObject.put("RegionId", sessionManager.getRegionID());

            Set set = hm.entrySet();
            Iterator i = set.iterator();

            while (i.hasNext()) {
                Map.Entry me = (Map.Entry) i.next();
                items.put(me.getValue());
            }

            updateObject.put("Items", items);

            jsonPost(API.getInstance().getApiUpdateCartItem() + "?productPairIsUpdate=false&mfp_id=" + sessionManager.getKeyMfpId() + "&regionID=" + sessionManager.getRegionID(), updateObject, "update");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void callNotePopup(final Integer position){
        Integer qtyText = 0;
        String prodName = null;

        try {
            qtyText = nonStoreProduct.getJSONObject(position).getInt("Quantity");

            if(qtyText >= 1){
                final JSONObject wordObject = new JSONObject();
                LayoutInflater inflater = (LayoutInflater) CartActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View writeReview = null;

                writeReview = inflater.inflate(R.layout.activity_review_greeting_cards, null);

                try {
                    prodName = nonStoreProduct.getJSONObject(position).getJSONObject("Product").getString("Name");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                LinearLayout linearLayout = (LinearLayout) writeReview.findViewById(R.id.linearlayout_text);
                final TextView headerText = (TextView) writeReview.findViewById(R.id.header_review);
                Button cancelReview = (Button) writeReview.findViewById(R.id.cancel_review);
                Button sendReview = (Button) writeReview.findViewById(R.id.send_review);
                EditText editText = null;
                TextView titleText;

                final List<EditText> editList = new ArrayList<EditText>();
                InputFilter[] fArray = new InputFilter[1];
                fArray[0] = new InputFilter.LengthFilter(200);

                headerText.setText(prodName);

                for(int i=0; i<qtyText; i++){
                    titleText = new TextView(CartActivity.this);
                    titleText.setText("Ucapan "+ (i+1));
                    titleText.setPadding(0, 20, 0, 0);
                    titleText.setTypeface(Typeface.DEFAULT_BOLD);

                    editText = new EditText(CartActivity.this);
                    editText.setWidth(LinearLayout.LayoutParams.MATCH_PARENT);
                    editText.setHeight(280);
                    editText.setBackgroundResource(R.drawable.card_product_style_1);
                    editText.setPadding(5, 5, 5, 5);
                    editText.setFilters(fArray);
                    editText.setHint("Tulis pesan disini");
                    editText.setGravity(Gravity.START);

                    final EditText finalEditText = editText;
                    editText.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {
                            if (s.length() < 1 || start >= s.length() || start < 0)
                                return;

                            if (s.subSequence(start, start + 1).toString().equalsIgnoreCase("\n")) {
                                String s_text = start > 0 ? s.subSequence(0, start).toString() : "";
                                s_text += start < s.length() ? s.subSequence(start + 1, s.length()).toString() : "";
                                finalEditText.setText(s_text);

                                finalEditText.setSelection(s_text.length());
                            }
                        }

                        @Override
                        public void afterTextChanged(Editable s) {}
                    });

                    linearLayout.addView(titleText);
                    linearLayout.addView(editText);
                    editList.add(editText);
                }

                if(nonStoreProduct.getJSONObject(position).getString("Notes").equals("null") || nonStoreProduct.getJSONObject(position).getString("Notes").equals("")){
                    editText.setText("");
                }else {
                    String[] splitText = nonStoreProduct.getJSONObject(position).getString("Notes").split("\\|");
                    for(int j=0; j<splitText.length; j++){
                        for(int i=0; i<editList.size(); i++){
                            if(i == j) editList.get(i).setText(splitText[j]);
                        }
                    }
                }

                alertDialogBuilder = new AlertDialog.Builder(CartActivity.this);
                alertDialogBuilder.setView(writeReview);
                alertDialog = alertDialogBuilder.create();
                alertDialog.setCanceledOnTouchOutside(false);
                alertDialog.show();;

                cancelReview.setOnClickListener(
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                alertDialog.hide();
                                checkNote = true;
                                stopLoader();
                            }
                        }
                );

                final StringBuilder builder = new StringBuilder();
                sendReview.setOnClickListener(
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                try {
                                    wordObject.put("cartId", nonStoreProduct.getJSONObject(position).getString("ID"));

                                    for(int i=0; i<editList.size(); i++){
                                        if(i == 0){
                                            if(editList.get(i).getText().length() == 0) builder.append("");
                                            else builder.append(editList.get(i).getText().toString());
                                        }else if(i > 0){
                                            if(editList.get(i).getText().length() == 0) builder.append("|" + "");
                                            else builder.append("|" + editList.get(i).getText().toString());
                                        }

                                        String words = builder.toString();
                                        wordObject.put("Notes", words);
                                    }

                                    String url = API.getInstance().getApiSaveNote()+"?mfp_id="+sessionManager.getKeyMfpId();
                                    jsonPost(url, wordObject, "word");
                                    checkNote = false;
                                    alertDialog.hide();
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                );
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void setStockEmpty(boolean stokEmpty){
        isStockEmpty = stokEmpty;
    }

    public void setCartRefresh(){
        jsonArrayRequest(apiGetCart, "cart");
    }

    public void jsonArrayRequest(String url, final String type){
        runLoader();
        System.out.println("--- url : "+url);
        JsonArrayRequest jsonArrayReq = new JsonArrayRequest(url,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        if (response == null || response.length() == 0){
                            stopLoader();
                            Toast.makeText(CartActivity.this, "Gagal terkoneksi server", Toast.LENGTH_LONG).show();
                        }else{
                            if(type.equals("cart")){
                                try {
                                    sessionManager.setCartId(response.getJSONObject(0).getString("ID"));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                cartProduct = response;
                                showCart(response);
                            } else if(type.equals("updatePromo")){
                                cartProduct = response;
                                updatePromo();
                            } else if (type.equalsIgnoreCase("checkCostStore")) {
                                costStore = "0";
                                try {
                                    if (response.getJSONObject(0) != null) {
                                        Double price = response.getJSONObject(0).getDouble("OngkosKirimPlaza");
                                        costStore =  df.format(price).replace(",", ".");
                                    }
                                } catch (Exception ignored) {}

                                costStore = "Rp " + costStore;
                                stopLoader();
                            } else if(type.equals("count")){
                                try {
                                    if(!(response.getInt(0) > 0)){
                                        sessionManager.setCartId("00000000-0000-0000-0000-000000000000");

                                        alertDialogBuilder = new AlertDialog.Builder(CartActivity.this);
                                        alertDialogBuilder.setMessage("Keranjang Belanja Anda kosong.");
                                        alertDialogBuilder.setNegativeButton("Mulai Belanja", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface arg0, int arg1) {
                                                finish();
                                                overridePendingTransition(R.anim.right_in, R.anim.right_out);
                                            }
                                        });

                                        alertDialog = alertDialogBuilder.create();
                                        alertDialog.setCanceledOnTouchOutside(false);
                                        alertDialog.show();
                                    } else {
                                        jsonArrayRequest(apiGetCart, "cart");
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(CartActivity.this, "Gagal terkoneksi server", Toast.LENGTH_LONG).show();
                stopLoader();
            }
        }, this);

        jsonArrayReq.setRetryPolicy(new DefaultRetryPolicy(60000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppController.getInstance().addToRequestQueue(jsonArrayReq);
    }

    // POST OBJECT
    public void jsonPost(String urlJsonObj, JSONObject jsonObject, final String type){
        runLoader();
        System.out.println("--- update url= " + urlJsonObj);
        System.out.println("--- update = object" + jsonObject);

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                urlJsonObj, jsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(final JSONObject response) {
                        try {
                            switch (type) {
                                case "update":
                                    if (response.getBoolean("IsSuccess")){
                                        jsonArrayRequest(apiGetCart, "cart");
                                    }else{
                                        alertDialogBuilder = new AlertDialog.Builder(CartActivity.this);
                                        alertDialogBuilder.setMessage(response.getString("Message"));
                                        alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface arg0, int arg1) {
                                                jsonArrayRequest(apiGetCart, "cart");
                                            }
                                        });

                                        alertDialog = alertDialogBuilder.create();
                                        alertDialog.show();
                                    }
                                    break;
                                case "word":
                                    if (response.getBoolean("IsSuccess")){
                                        jsonArrayRequest(apiGetCart, "cart");
                                    }else{
                                        alertDialogBuilder = new AlertDialog.Builder(CartActivity.this);
                                        alertDialogBuilder.setMessage(response.getString("Message"));
                                        alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface arg0, int arg1) {
                                                jsonArrayRequest(apiGetCart, "cart");
                                            }
                                        });

                                        alertDialog = alertDialogBuilder.create();
                                        alertDialog.show();
                                    }

                                    break;
                                case "voucher":
                                    try {
                                        if (response.getJSONObject("ResponseObject").getString("status").equals("FAILED")) {
                                            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                            if (response.getJSONObject("ResponseObject").toString().contains("TenggangWaktu") &&
                                                    response.getJSONObject("ResponseObject").getString("TenggangWaktu") != null &&
                                                    response.getJSONObject("ResponseObject").getString("TenggangWaktu").length() > 0 &&
                                                    !response.getJSONObject("ResponseObject").getString("TenggangWaktu").toLowerCase().equals("null")) {
                                                final Date eventDate = dateFormat.parse(response.getJSONObject("ResponseObject").getString("TenggangWaktu").split("\\.")[0].replace("T", " "));
                                                final Date currentDate = new Date();

                                                if (!currentDate.after(eventDate)) {
                                                    countDownStart(response.getJSONObject("ResponseObject"), "kupon");
                                                } else {
                                                    infoVoucher.setVisibility(View.VISIBLE);
                                                    infoVoucher.setText(response.getJSONObject("ResponseObject").getString("keterangan"));
                                                    infoVoucher.setTextColor(Color.RED);
                                                    voucherText.setText("");
                                                }
                                            } else {
                                                infoVoucher.setVisibility(View.VISIBLE);
                                                infoVoucher.setText(response.getJSONObject("ResponseObject").getString("keterangan"));
                                                infoVoucher.setTextColor(Color.RED);
                                            }
                                        } else {
                                            infoVoucher.setVisibility(View.VISIBLE);
                                            infoVoucher.setText(response.getJSONObject("ResponseObject").getString("keterangan"));
                                            infoVoucher.setTextColor(Color.GREEN);
                                            voucherText.setText("");
                                            reloadCart();
                                        }

                                        stopLoader();
                                    }  catch (ParseException e) {
                                        e.printStackTrace();
                                    }

                                    break;
                            }
                        } catch (JSONException e) {
                            stopLoader();
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(CartActivity.this, "Gagal terkoneksi server", Toast.LENGTH_LONG).show();
                stopLoader();
            }
        });

        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(60000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppController.getInstance().addToRequestQueue(jsonObjReq);
    }

    @SuppressWarnings("deprecation")
    public void showCart(JSONArray response){
        totalPrice = 0.0;
        totalDiscount = 0.0;
        totalCoupon = 0.0;
        totalPriceWithDiscount = 0.0;
        storeTotalPrice = 0.0;
        coupon = new ArrayList<>();
        promoBool = false;
        double totalStore = 0.0;
        double totalNonStore = 0.0;
        double totalVirtual = 0.0;

        try {
            if (response.getJSONObject(0).getJSONObject("CartItemNotification").getJSONArray("Items").length() > 0){
                shoppingCartItems = response.getJSONObject(0).getJSONObject("CartItemNotification").getJSONArray("Items");
                storeMinShop = response.getJSONObject(0).getDouble("IstoreMinShop");
                storeMaxShop = response.getJSONObject(0).getDouble("IstoreMaxShop");

                if (shoppingCartItems.getJSONObject(0).getString("VBKNominal") == null ||
                        shoppingCartItems.getJSONObject(0).getString("VBKNominal").equals("null") ||
                        shoppingCartItems.getJSONObject(0).getString("VBKNominal").equals("")){
                    voucher = 0.0;
                    totalVoucher.setText("Rp " + df.format(voucher).replace(",","."));
                }else if (shoppingCartItems.getJSONObject(0).getDouble("VBKNominal") > 0.0){
                    voucher = shoppingCartItems.getJSONObject(0).getDouble("VBKNominal");
                    totalVoucher.setText("Rp " + df.format(voucher).replace(",","."));
                }

                sessionManager.setKeyTotalVoucher(Integer.valueOf(voucher.toString().split("\\.")[0]));

                if(response.getJSONObject(0).getJSONObject("CartItemNotification").getJSONArray("GKupon").length() > 0){
                    promoBool = true;
                }

                for (int i=0; i<shoppingCartItems.length(); i++){
                    if(shoppingCartItems.getJSONObject(i).getString("RootCategoryName").toLowerCase().equals("virtual")){
                        if (shoppingCartItems.getJSONObject(i).getString("VBKCode") != null && !shoppingCartItems.getJSONObject(i).getString("VBKCode").equals("")
                                && !shoppingCartItems.getJSONObject(i).getString("VBKCode").contains("null")){
                            if (coupon.size() > 0){
                                if (!coupon.toString().contains("Voucher : " + shoppingCartItems.getJSONObject(i).getString("VBKCode"))){
                                    coupon.add("Voucher : " + shoppingCartItems.getJSONObject(i).getString("VBKCode"));
                                }
                            }else{
                                coupon.add("Voucher : " + shoppingCartItems.getJSONObject(i).getString("VBKCode"));
                            }
                        }

                        virtualProduct = shoppingCartItems.getJSONObject(i).getJSONArray("ShoppingCartItems");

                        for (int j=0; j<virtualProduct.length(); j++){
                            JSONArray coupons = virtualProduct.getJSONObject(j).getJSONArray("Coupons");
                            for (int k=0; k<coupons.length(); k++){
                                if (!coupons.get(k).toString().toLowerCase().equals("null") && !coupons.get(k).equals(""))
                                    if (coupon.size() > 0){
                                        if (!coupon.toString().contains("i-Kupon : " + coupons.get(k).toString())){
                                            coupon.add("i-Kupon : " + coupons.get(k).toString());
                                        }
                                    }else{
                                        coupon.add("i-Kupon : " + coupons.get(k).toString());
                                    }
                            }
                        }

                        for(int j=0; j<virtualProduct.length(); j++){
                            totalPrice += virtualProduct.getJSONObject(j).getDouble("Price") * virtualProduct.getJSONObject(j).getDouble("Quantity");
                            totalDiscount += virtualProduct.getJSONObject(j).getDouble("Discount");
                            totalCoupon += virtualProduct.getJSONObject(j).getDouble("KuponNominal");
                            totalPriceWithDiscount += virtualProduct.getJSONObject(j).getDouble("PriceWithDiscount") * virtualProduct.getJSONObject(j).getDouble("Quantity");
                            totalVirtual += virtualProduct.getJSONObject(j).getDouble("PriceWithDiscount") * virtualProduct.getJSONObject(j).getDouble("Quantity");
                        }
                    } else if(shoppingCartItems.getJSONObject(i).getString("RootCategoryName").toLowerCase().equals("non-grocery")){
                        if (shoppingCartItems.getJSONObject(i).getString("VBKCode") != null && !shoppingCartItems.getJSONObject(i).getString("VBKCode").equals("")
                                && !shoppingCartItems.getJSONObject(i).getString("VBKCode").contains("null")){
                            if (coupon.size() > 0){
                                if (!coupon.toString().contains("Voucher : " + shoppingCartItems.getJSONObject(i).getString("VBKCode"))){
                                    coupon.add("Voucher : " + shoppingCartItems.getJSONObject(i).getString("VBKCode"));
                                }
                            }else{
                                coupon.add("Voucher : " + shoppingCartItems.getJSONObject(i).getString("VBKCode"));
                            }
                        }

                        nonStoreProduct = shoppingCartItems.getJSONObject(i).getJSONArray("ShoppingCartItems");

                        for (int j=0; j<nonStoreProduct.length(); j++){
                            JSONArray coupons = nonStoreProduct.getJSONObject(j).getJSONArray("Coupons");
                            for (int k=0; k<coupons.length(); k++){
                                if (!coupons.get(k).toString().toLowerCase().equals("null") && !coupons.get(k).equals(""))
                                    if (coupon.size() > 0){
                                        if (!coupon.toString().contains("i-Kupon : " + coupons.get(k).toString())){
                                            coupon.add("i-Kupon : " + coupons.get(k).toString());
                                        }
                                    }else{
                                        coupon.add("i-Kupon : " + coupons.get(k).toString());
                                    }
                            }
                        }

                        for(int j=0; j<nonStoreProduct.length(); j++){
                            totalPrice += nonStoreProduct.getJSONObject(j).getDouble("Price")  * nonStoreProduct.getJSONObject(j).getDouble("Quantity");
                            totalDiscount += nonStoreProduct.getJSONObject(j).getDouble("Discount");
                            totalCoupon += nonStoreProduct.getJSONObject(j).getDouble("KuponNominal");
                            totalPriceWithDiscount += nonStoreProduct.getJSONObject(j).getDouble("PriceWithDiscount") * nonStoreProduct.getJSONObject(j).getDouble("Quantity");
                            totalNonStore += nonStoreProduct.getJSONObject(j).getDouble("PriceWithDiscount") * nonStoreProduct.getJSONObject(j).getDouble("Quantity");
                        }
                    } else if(shoppingCartItems.getJSONObject(i).getString("RootCategoryName").toLowerCase().equals("grocery")){
                        if (shoppingCartItems.getJSONObject(i).getString("VBKCode") != null && !shoppingCartItems.getJSONObject(i).getString("VBKCode").equals("")
                                && !shoppingCartItems.getJSONObject(i).getString("VBKCode").contains("null")){
                            if (coupon.size() > 0){
                                if (!coupon.toString().contains("Voucher : " + shoppingCartItems.getJSONObject(i).getString("VBKCode"))){
                                    coupon.add("Voucher : " + shoppingCartItems.getJSONObject(i).getString("VBKCode"));
                                }
                            }else{
                                coupon.add("Voucher : " + shoppingCartItems.getJSONObject(i).getString("VBKCode"));
                            }
                        }

                        storeProduct = new JSONArray();
                        storeTotalPrice += shoppingCartItems.getJSONObject(i).getDouble("Total");

                        for(int j=0; j<shoppingCartItems.getJSONObject(i).getJSONArray("ShoppingCartItems").length(); j++){
                            storeProduct.put(shoppingCartItems.getJSONObject(i).getJSONArray("ShoppingCartItems").getJSONObject(j));
                            totalPrice += storeProduct.getJSONObject(j).getDouble("Price") * storeProduct.getJSONObject(j).getDouble("Quantity");
                            totalDiscount += storeProduct.getJSONObject(j).getDouble("Discount");
                            totalCoupon += storeProduct.getJSONObject(j).getDouble("KuponNominal");
                            totalPriceWithDiscount += storeProduct.getJSONObject(j).getDouble("PriceWithDiscount") * storeProduct.getJSONObject(j).getDouble("Quantity");
                            totalStore += storeProduct.getJSONObject(j).getDouble("PriceWithDiscount") * storeProduct.getJSONObject(j).getDouble("Quantity");

                            if (storeProduct.getJSONObject(j).getJSONArray("DetailParcel").length() > 0){
                                for (int k = 0; k<storeProduct.getJSONObject(j).getJSONArray("DetailParcel").length();k++){
                                    storeProduct.put(storeProduct.getJSONObject(j).getJSONArray("DetailParcel").getJSONObject(k));
                                }
                            }
                        }

                        for (int j=0; j<storeProduct.length(); j++){
                            JSONArray coupons = storeProduct.getJSONObject(j).getJSONArray("Coupons");
                            for (int k=0; k<coupons.length(); k++){
                                if (!coupons.get(k).toString().toLowerCase().equals("null") && !coupons.get(k).equals("")){
                                    if (coupon.size() > 0){
                                        if (!coupon.toString().contains("i-Kupon : " + coupons.get(k).toString())){
                                            coupon.add("i-Kupon : " + coupons.get(k).toString());
                                        }
                                    }else{
                                        coupon.add("i-Kupon : " + coupons.get(k).toString());
                                    }
                                }
                            }
                        }
                    } else {
                        promoBool = true;
                    }
                }

                if (sessionManager.getEmptyProd() != null){
                    if (storeProduct.length() > 0){
                        cartAdapter = new CartAdapter(CartActivity.this, storeProduct, listProductDetail);
                        storeProductList.setAdapter(cartAdapter);

                        titleProductStore.setText("Produk Toko (" + storeProduct.length() + ")");
                        totalProductStore.setText("Rp " + df.format(totalStore).replace(",","."));
                        linearProductStore.setVisibility(View.VISIBLE);
                    }else{
                        linearProductStore.setVisibility(View.GONE);
                    }

                    if (nonStoreProduct.length() > 0){
                        cartAdapter = new CartAdapter(CartActivity.this, nonStoreProduct, listProductDetail);
                        nonStoreProductList.setAdapter(cartAdapter);

                        titleProductNonStore.setText("Produk Non Toko (" + nonStoreProduct.length() + ")");
                        totalProductNonStre.setText("Rp " + df.format(totalNonStore).replace(",","."));
                        linearProductNonStore.setVisibility(View.VISIBLE);
                    }else{
                        linearProductNonStore.setVisibility(View.GONE);
                    }
                } else {
                    if (storeProduct.length() > 0){
                        cartAdapter = new CartAdapter(CartActivity.this, storeProduct);
                        storeProductList.setAdapter(cartAdapter);
                        cartAdapter.notifyDataSetChanged();

                        titleProductStore.setText("Produk Toko (" + storeProduct.length() + ")");
                        totalProductStore.setText("Rp " + df.format(totalStore).replace(",","."));
                        linearProductStore.setVisibility(View.VISIBLE);
                    }else{
                        linearProductStore.setVisibility(View.GONE);
                    }

                    if (nonStoreProduct.length() > 0){
                        cartAdapter = new CartAdapter(CartActivity.this, nonStoreProduct);
                        nonStoreProductList.setAdapter(cartAdapter);

                        titleProductNonStore.setText("Produk Non Toko (" + nonStoreProduct.length() + ")");
                        totalProductNonStre.setText("Rp " + df.format(totalNonStore).replace(",","."));
                        linearProductNonStore.setVisibility(View.VISIBLE);
                    }else{
                        linearProductNonStore.setVisibility(View.GONE);
                    }
                }

                if (virtualProduct.length() > 0){
                    cartAdapter = new CartAdapter(CartActivity.this, virtualProduct);
                    virtualProductList.setAdapter(cartAdapter);

                    titleProductVirtual.setText("Produk Virtual (" + virtualProduct.length() + ")");
                    totalProductVirtual.setText("Rp " + df.format(totalVirtual).replace(",","."));
                    linearProductVirtual.setVisibility(View.VISIBLE);
                }else{
                    linearProductVirtual.setVisibility(View.GONE);
                }

                if (!promoBool){
                    linearPromo.setVisibility(View.GONE);

                    if(totalDiscount > 0.0){
                        totalDiscountTransaction.setText("Rp " + df.format(totalDiscount).replace(",","."));
                    } else {
                        totalDiscountTransaction.setText("Rp 0");
                    }

                    shippingCost = Double.valueOf(sessionManager.getKeyTotalShippingCost());

                    totalTransaction.setText("Rp " + df.format(totalPriceWithDiscount - voucher).replace(",","."));
                    totalShippingCost.setText("Rp " + df.format(shippingCost).replace(",","."));
                    sessionManager.setKeyTotalPrice(Integer.valueOf(totalPriceWithDiscount.toString().split("\\.")[0]));
                    sessionManager.setKeyTotalVoucher(Integer.valueOf(voucher.toString().split("\\.")[0]));
                    sessionManager.setKeyTotalDiscount(Integer.valueOf(String.valueOf(totalDiscount).split("\\.")[0]));
                }

                if(totalCoupon > 0.0 || voucher > 0.0){
                    couponList.setVisibility(View.VISIBLE);
                    totalVoucher.setText("Rp " + df.format(totalCoupon + voucher).replace(",","."));
                    couponAdapter = new CouponAdapter(CartActivity.this, coupon, "cart");
                    couponList.setAdapter(couponAdapter);
                    sessionManager.setKeyTotalCoupon(Integer.valueOf(totalCoupon.toString().split("\\.")[0]));
                    sessionManager.setKeyCouponList(coupon.toString());
                }else{
                    couponList.setVisibility(View.GONE);
                }

                stopLoader();
                cartContainer.setVisibility(View.VISIBLE);

                if(promoBool){//jika ada promo
                    linearPromo.setVisibility(View.VISIBLE);

                    setPromo();
                    nextToPromo.setText("Lanjut");
                    nextToPromo.setEnabled(true);
                    nextToPromo.setBackgroundResource(R.drawable.button_style_1);
                }

                setButtonEnabled(false);
                stopLoader();
            }else{
                sessionManager.setCartId("00000000-0000-0000-0000-000000000000");

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
                alertDialogBuilder.setMessage("Keranjang Belanja Anda kosong.");
                alertDialogBuilder.setNegativeButton("Mulai Belanja", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        finish();
                        overridePendingTransition(R.anim.right_in, R.anim.right_out);
                    }
                });

                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.setCanceledOnTouchOutside(false);
                alertDialog.show();
            }

            if (checkStok){
                List<String> pluStock = new ArrayList<>(Arrays.asList(intent.getStringExtra("listProductDetail").split(",")));
                stockFromShipping(pluStock);
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public void stockFromShipping(List<String> pluStock){
        cartAdapter = new CartAdapter(CartActivity.this, storeProduct, pluStock);
        storeProductList.setAdapter(cartAdapter);

        cartAdapter = new CartAdapter(CartActivity.this, nonStoreProduct, pluStock);
        nonStoreProductList.setAdapter(cartAdapter);

        cartAdapter.notifyDataSetChanged();
    }

    public void setStock(boolean checkStockProduct){
        System.out.println("Check Stock Product = "+this.checkStockProduct);
        this.checkStockProduct = checkStockProduct;
    }

    public void deleteItemCartResponse(){
        storeProduct = new JSONArray();
        nonStoreProduct = new JSONArray();
        virtualProduct = new JSONArray();
        setStockEmpty(false);

        jsonArrayRequest(apiGetCart, "cart");
    }

    public void reloadCart(){
        jsonArrayRequest(apiGetCart, "cart");
//        setCartRefresh();
    }

    public void updateCartFromAdapter(String url, JSONObject cartObject, String type){
        if (type.equals("delete")){
            hm.put(url, cartObject);
            updateCart();
        }else{
            hm.put(url, cartObject);
        }
    }

    public void checkCart(){
        try {
            HitBuilders.ScreenViewBuilder builder = new HitBuilders.ScreenViewBuilder();

            for (int i=0; i<storeProduct.length();i++){
                if (storeProduct.getJSONObject(i).getJSONObject("Product").getString("Color").equals(null) ||
                        storeProduct.getJSONObject(i).getJSONObject("Product").getString("Color").equals("null")){
                    variant = storeProduct.getJSONObject(i).getJSONObject("Product").getString("Flavour");
                }else{
                    variant = storeProduct.getJSONObject(i).getJSONObject("Product").getString("Color");
                }

                Product product =  new Product()
                        .setId(storeProduct.getJSONObject(i).getJSONObject("Product").getString("ID"))
                        .setName(storeProduct.getJSONObject(i).getJSONObject("Product").getString("Name"))
                        .setBrand(storeProduct.getJSONObject(i).getJSONObject("Product").getJSONObject("ProductBrand").getString("Name"))
                        .setVariant(variant)
                        .setPrice(storeProduct.getJSONObject(i).getInt("Price"))
                        .setQuantity(storeProduct.getJSONObject(i).getInt("Quantity"));

                builder.addProduct(product);
            }

            for (int i=0; i<nonStoreProduct.length(); i++) {
                if (nonStoreProduct.getJSONObject(i).getJSONObject("Product").getString("Color").equals("") ||
                        nonStoreProduct.getJSONObject(i).getJSONObject("Product").getString("Color").equals("null")) {
                    variant = nonStoreProduct.getJSONObject(i).getJSONObject("Product").getString("Flavour");
                } else {
                    variant = nonStoreProduct.getJSONObject(i).getJSONObject("Product").getString("Color");
                }

                Product product = new Product()
                        .setId(nonStoreProduct.getJSONObject(i).getJSONObject("Product").getString("ID"))
                        .setName(nonStoreProduct.getJSONObject(i).getJSONObject("Product").getString("Name"))
                        .setBrand(nonStoreProduct.getJSONObject(i).getJSONObject("Product").getJSONObject("ProductBrand").getString("Name"))
                        .setVariant(variant)
                        .setPrice(nonStoreProduct.getJSONObject(i).getInt("Price"))
                        .setQuantity(nonStoreProduct.getJSONObject(i).getInt("Quantity"));

                builder.addProduct(product);
            }

            for (int i=0; i<virtualProduct.length(); i++) {
                Product product = new Product()
                        .setId(virtualProduct.getJSONObject(i).getJSONObject("Product").getString("ID"))
                        .setName(virtualProduct.getJSONObject(i).getJSONObject("Product").getString("Name"))
                        .setBrand(virtualProduct.getJSONObject(i).getJSONObject("Product").getJSONObject("ProductBrand").getString("Name"))
                        .setPrice(virtualProduct.getJSONObject(i).getInt("Price"))
                        .setQuantity(virtualProduct.getJSONObject(i).getInt("Quantity"));

                builder.addProduct(product);
            }

            ProductAction productAction = new ProductAction(ProductAction.ACTION_CHECKOUT).setCheckoutStep(1);
            builder.setProductAction(productAction);

            AppController application = (AppController) getApplication();
            Tracker t = application.getDefaultTracker();
            t.setScreenName("Shoping Cart");
            t.send(builder.build());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        for (int i=0; i<nonStoreProduct.length(); i++) {
            try {
                if (nonStoreProduct.getJSONObject(i).getJSONObject("Product").getBoolean("IsPAAI") && nonStoreProduct.getJSONObject(i).getBoolean("IsUseNote")) {
                    if (nonStoreProduct.getJSONObject(i).getString("Notes").replace("|", "").length() == 0 ||
                            nonStoreProduct.getJSONObject(i).getString("Notes").equals("null")) {
                        checkNote = true;
                        positionNoteNull = i;
                        break;
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

//        if (checkNote){
//            alertDialogBuilder = new AlertDialog.Builder(CartActivity.this);
//            alertDialogBuilder.setMessage("Kartu ucapan belum ditulis");
//            alertDialogBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
//                @Override
//                public void onClick(DialogInterface arg0, int arg1) {
//                    callNotePopup(positionNoteNull);
//                }
//            });
//
//            alertDialog = alertDialogBuilder.create();
//            alertDialog.setCanceledOnTouchOutside(false);
//            alertDialog.show();
//        }else
        if (isStockEmpty){
            stopLoader();

            alertDialogBuilder = new AlertDialog.Builder(CartActivity.this);
            alertDialogBuilder.setMessage("Persediaan barang tidak mencukupi, silakan perbaharui keranjang belanja Anda");
            alertDialogBuilder.setNegativeButton("Tutup", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface arg0, int arg1) {}
            });

            alertDialog = alertDialogBuilder.create();
            alertDialog.setCanceledOnTouchOutside(false);
            alertDialog.show();
        } else if(storeTotalPrice!=0.0 && storeTotalPrice < storeMinShop){
            alertDialogBuilder = new AlertDialog.Builder(CartActivity.this);
            alertDialogBuilder.setMessage("Jumlah belanja produk Grocery tidak bisa kurang dari Rp "+df.format(storeMinShop).replace(",","."));
            alertDialogBuilder.setNegativeButton("Tutup", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface arg0, int arg1) {
                    stopLoader();
                }
            });

            alertDialog = alertDialogBuilder.create();
            alertDialog.setCanceledOnTouchOutside(false);
            alertDialog.show();
        } else if(storeTotalPrice != 0.0 && storeTotalPrice > storeMaxShop){
            alertDialogBuilder = new AlertDialog.Builder(CartActivity.this);
            alertDialogBuilder.setMessage("Jumlah belanja produk Grocery tidak bisa lebih dari Rp "+df.format(storeMaxShop).replace(",","."));
            alertDialogBuilder.setNegativeButton("Tutup", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface arg0, int arg1) {}
            });

            alertDialog = alertDialogBuilder.create();
            alertDialog.setCanceledOnTouchOutside(false);
            alertDialog.show();
        } else if(checkStockProduct){
            if(!promoBool){
                if(!sessionManager.isLoggedIn()){
                    intent = new Intent(CartActivity.this, LoginActivity.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.left_in, R.anim.left_out);
                } else{
                    stopLoader();
                    intent = new Intent(CartActivity.this, ShippingActivity.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.left_in, R.anim.left_out);
                }
            }else{
                stopLoader();
                intent = new Intent(CartActivity.this, ShippingActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.left_in, R.anim.left_out);
            }
        }else{
            stopLoader();
            intent = new Intent(CartActivity.this, ShippingActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.left_in, R.anim.left_out);
        }
    }

    public void setPromo(){
        try {
            if(cartProduct.getJSONObject(0).getJSONObject("CartItemNotification").getJSONArray("GKupon").length() > 0){
                promoBool = true;
            }

            if(!promoBool){
                if(!sessionManager.isLoggedIn()){
                    intent = new Intent(CartActivity.this, LoginActivity.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.left_in, R.anim.left_out);
                } else{
                    if(sqLiteHandler.getDefaultAddressCount() > 0) {
                        try {
                            JSONObject defaultAddress = new JSONObject(sqLiteHandler.getDefaultAddress());
                            String regionId = defaultAddress.getString("Region");

                            if(!regionId.equals(sessionManager.getRegionID())){
                                alertDialogBuilder = new AlertDialog.Builder(CartActivity.this);
                                alertDialogBuilder.setMessage("Silakan isi atau pilih alamat sesuai dengan Kota yang Anda Pilih");
                                alertDialogBuilder.setNegativeButton("Lanjut", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface arg0, int arg1) {
                                        intent = new Intent(CartActivity.this, ProfileActivity.class);
                                        intent.putExtra("pageindex", "" + 1);
                                        startActivity(intent);
                                        overridePendingTransition(R.anim.left_in, R.anim.left_out);
                                    }
                                });

                                alertDialog = alertDialogBuilder.create();
                                alertDialog.setCanceledOnTouchOutside(false);
                                alertDialog.show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
            } else {
                if(promoBool){//jika ada promo
                    preparePromo();
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void preparePromo(){
        listviewPromo.removeAllViews();
        promoProductCart = new JSONArray();

        try {
            shoppingCartItems = cartProduct.getJSONObject(0).getJSONObject("CartItemNotification").getJSONArray("Items");

            for (int i=0; i<shoppingCartItems.length(); i++){
                if(!shoppingCartItems.getJSONObject(i).getString("RootCategoryName").toLowerCase().equals("virtual") &&
                        !shoppingCartItems.getJSONObject(i).getString("RootCategoryName").toLowerCase().equals("non-grocery") &&
                        !shoppingCartItems.getJSONObject(i).getString("RootCategoryName").toLowerCase().equals("grocery")){

                    promoProductCart.put(shoppingCartItems.getJSONObject(i));
                }
            }
        } catch (Exception e){
            e.printStackTrace();
        }

        setupViewPager(promoViewPager);
        promoTabLayout.setupWithViewPager(promoViewPager);
        selectedPage(0);

        if (promoProductCart.length() > 0){
            imageRight.setVisibility(View.VISIBLE);
            imageLeft.setVisibility(View.GONE);
        }

        promoTabLayout.setOnTabSelectedListener(
                new TabLayout.OnTabSelectedListener() {
                    @Override
                    public void onTabSelected(TabLayout.Tab tab) {
                        selectedPage(tab.getPosition());

                        if (tab.getPosition() == 0){
                            if (promoProductCart.length() > 0){
                                imageRight.setVisibility(View.VISIBLE);
                                imageLeft.setVisibility(View.GONE);
                            }else{
                                imageRight.setVisibility(View.GONE);
                                imageLeft.setVisibility(View.GONE);
                            }
                        }else if (tab.getPosition() == promoProductCart.length()){
                            imageLeft.setVisibility(View.VISIBLE);
                            imageRight.setVisibility(View.GONE);
                        }else{
                            imageRight.setVisibility(View.VISIBLE);
                            imageLeft.setVisibility(View.VISIBLE);
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

        totalPrice = 0.0;
        totalDiscount = 0.0;
        totalPriceWithDiscount = 0.0;

        try {
            shoppingCartItems = cartProduct.getJSONObject(0).getJSONObject("CartItemNotification").getJSONArray("Items");

            for (int i=0; i<shoppingCartItems.length(); i++){
                if(shoppingCartItems.getJSONObject(i).getString("RootCategoryName").toLowerCase().equals("virtual")){
                    virtualProduct = shoppingCartItems.getJSONObject(i).getJSONArray("ShoppingCartItems");

                    for(int j=0; j<virtualProduct.length(); j++){
                        totalPrice +=  virtualProduct.getJSONObject(j).getDouble("Price") * virtualProduct.getJSONObject(j).getInt("Quantity");
//                        totalDiscount += virtualProduct.getJSONObject(j).getDouble("Discount") * virtualProduct.getJSONObject(j).getInt("Quantity");
                        totalDiscount += virtualProduct.getJSONObject(j).getDouble("Discount");
                        totalPriceWithDiscount += virtualProduct.getJSONObject(j).getDouble("PriceWithDiscount") * virtualProduct.getJSONObject(j).getInt("Quantity");
                    }
                } else if(shoppingCartItems.getJSONObject(i).getString("RootCategoryName").toLowerCase().equals("non-grocery")){
                    nonStoreProduct = shoppingCartItems.getJSONObject(i).getJSONArray("ShoppingCartItems");

                    for(int j=0; j<nonStoreProduct.length(); j++){
                        totalPrice += nonStoreProduct.getJSONObject(j).getDouble("Price") * nonStoreProduct.getJSONObject(j).getInt("Quantity");
//                        totalDiscount += nonStoreProduct.getJSONObject(j).getDouble("Discount") * nonStoreProduct.getJSONObject(j).getInt("Quantity");
                        totalDiscount += nonStoreProduct.getJSONObject(j).getDouble("Discount");
                        totalPriceWithDiscount += nonStoreProduct.getJSONObject(j).getDouble("PriceWithDiscount") * nonStoreProduct.getJSONObject(j).getInt("Quantity");
                    }
                } else if(shoppingCartItems.getJSONObject(i).getString("RootCategoryName").toLowerCase().equals("grocery")){
                    storeProduct = shoppingCartItems.getJSONObject(i).getJSONArray("ShoppingCartItems");

                    for(int j=0; j<storeProduct.length(); j++){
                        totalPrice += storeProduct.getJSONObject(j).getDouble("Price") * storeProduct.getJSONObject(j).getInt("Quantity");
//                        totalDiscount += storeProduct.getJSONObject(j).getDouble("Discount") * storeProduct.getJSONObject(j).getInt("Quantity");
                        totalDiscount += storeProduct.getJSONObject(j).getDouble("Discount");
                        totalPriceWithDiscount += storeProduct.getJSONObject(j).getDouble("PriceWithDiscount") * storeProduct.getJSONObject(j).getInt("Quantity");
                    }
                } else {
                    promoProductCart = shoppingCartItems.getJSONObject(i).getJSONArray("ShoppingCartItems");

                    for(int j=0; j<promoProductCart.length(); j++){
                        if(promoProductCart.getJSONObject(j).getBoolean("IsSelectedPromo")){
                            if(promoProductCart.getJSONObject(j).getBoolean("IsQuantityUpdate")){
                                qtys = promoProductCart.getJSONObject(j).getInt("QuantityUpdate");
                            } else {
                                qtys = promoProductCart.getJSONObject(j).getInt("Quantity");
                            }

                            totalPrice += promoProductCart.getJSONObject(j).getDouble("Price") * qtys;
                            totalPriceWithDiscount += promoProductCart.getJSONObject(j).getDouble("PriceWithDiscount") * qtys;
//                            totalDiscount += promoProduct.getJSONObject(j).getDouble("Discount");
                            totalDiscount = totalPrice - totalPriceWithDiscount;
                        }
                    }
                }
            }
        } catch (Exception e){
            e.printStackTrace();
        }

//        totalBelanja.setText("Rp " + df.format(totalPrice).replace(",","."));

        if(totalDiscount > 0.0){
            totalDiscountTransaction.setText("Rp " + df.format(totalDiscount).replace(",","."));
        } else {
            totalDiscountTransaction.setText("Rp 0");
        }

        shippingCost = Double.valueOf(sessionManager.getKeyTotalShippingCost());

        totalTransaction.setText("Rp " + df.format(totalPrice - totalDiscount  - voucher).replace(",","."));
        totalShippingCost.setText("Rp " + df.format(shippingCost).replace(",","."));

        sessionManager.setKeyTotalPrice(Integer.valueOf(totalPrice.toString().split("\\.")[0]));
        sessionManager.setKeyTotalDiscount(Integer.valueOf(String.valueOf(totalDiscount).split("\\.")[0]));
        sessionManager.setKeyTotalVoucher(Integer.valueOf(voucher.toString().split("\\.")[0]));
        stopLoader();
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapterVirtual adapter = new ViewPagerAdapterVirtual(CartActivity.this.getSupportFragmentManager());
        String qty = "";

        for (int i=0; i<promoProductCart.length(); i++){
            try {
                qty = promoProductCart.getJSONObject(i).getJSONArray("ShoppingCartItems").getJSONObject(0).getString("Quantity");
                PromoFragment promoFragment = new PromoFragment(promoProductCart.getJSONObject(i), i);
                promoArrays = promoProductCart;
                adapter.addFragment(promoFragment, "Promo " + (i+1) + " (maks " + qty + ")");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        viewPager.setAdapter(adapter);
    }

    public void setTabView(final int position, int value, int maxValue) {
        TabLayout.Tab tab = promoTabLayout.getTabAt(position);

        if (tab.getCustomView() == null) tab.setCustomView(LayoutInflater.from(CartActivity.this).inflate(R.layout.tab_item, null));

        View view=tab.getCustomView();
        TextView tabName= (TextView) view.findViewById(R.id.tab_name);
        TextView notifCount= (TextView) view.findViewById(R.id.notifText);

        tabName.setText(tab.getText());
        if (value > 0){
            notifCount.setVisibility(View.VISIBLE);
            notifCount.setText(value+"");
        }else{
            notifCount.setVisibility(View.GONE);
        }
    }

    public void selectedPage(int pageIndex) {
        promoTabLayout.setScrollPosition(pageIndex, 0f, true);
        promoViewPager.setCurrentItem(pageIndex);
    }

    class ViewPagerAdapterVirtual extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapterVirtual(FragmentManager manager) {
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

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

    public void loadPromo(String status){
        if(status.equals("select")){
            runLoader();
        }else{
            stopLoader();
        }

        jsonArrayRequest(apiGetCart, "updatePromo");
    }

    public void updatePromo(){
        System.out.println("start update promo");
        promoProduct = new JSONArray();

        try {
            shoppingCartItems = cartProduct.getJSONObject(0).getJSONObject("CartItemNotification").getJSONArray("Items");

            for (int i=0; i<shoppingCartItems.length(); i++){
                if(!shoppingCartItems.getJSONObject(i).getString("RootCategoryName").toLowerCase().equals("virtual") &&
                        !shoppingCartItems.getJSONObject(i).getString("RootCategoryName").toLowerCase().equals("non-grocery") &&
                        !shoppingCartItems.getJSONObject(i).getString("RootCategoryName").toLowerCase().equals("grocery")){

                    promoProduct.put(shoppingCartItems.getJSONObject(i));
                }
            }
        } catch (Exception e){
            e.printStackTrace();
        }

        totalPrice = 0.0;
        totalDiscount = 0.0;
        totalPriceWithDiscount = 0.0;

        try {
            shoppingCartItems = cartProduct.getJSONObject(0).getJSONObject("CartItemNotification").getJSONArray("Items");

            for (int i=0; i<shoppingCartItems.length(); i++){
                if(shoppingCartItems.getJSONObject(i).getString("RootCategoryName").toLowerCase().equals("virtual")){
                    virtualProduct = shoppingCartItems.getJSONObject(i).getJSONArray("ShoppingCartItems");

                    for(int j=0; j<virtualProduct.length(); j++){
                        totalPrice +=  virtualProduct.getJSONObject(j).getDouble("Price") * virtualProduct.getJSONObject(j).getInt("Quantity");
//                        totalDiscount += virtualProduct.getJSONObject(j).getDouble("Discount") * virtualProduct.getJSONObject(j).getInt("Quantity");
                        totalDiscount += virtualProduct.getJSONObject(j).getDouble("Discount");
                        totalPriceWithDiscount += virtualProduct.getJSONObject(j).getDouble("PriceWithDiscount") * virtualProduct.getJSONObject(j).getInt("Quantity");
                    }
                } else if(shoppingCartItems.getJSONObject(i).getString("RootCategoryName").toLowerCase().equals("non-grocery")){
                    nonStoreProduct = shoppingCartItems.getJSONObject(i).getJSONArray("ShoppingCartItems");

                    for(int j=0; j<nonStoreProduct.length(); j++){
                        totalPrice += nonStoreProduct.getJSONObject(j).getDouble("Price") * nonStoreProduct.getJSONObject(j).getInt("Quantity");
//                        totalDiscount += nonStoreProduct.getJSONObject(j).getDouble("Discount") * nonStoreProduct.getJSONObject(j).getInt("Quantity");
                        totalDiscount += nonStoreProduct.getJSONObject(j).getDouble("Discount") ;
                        totalPriceWithDiscount += nonStoreProduct.getJSONObject(j).getDouble("PriceWithDiscount") * nonStoreProduct.getJSONObject(j).getInt("Quantity");
                    }
                } else if(shoppingCartItems.getJSONObject(i).getString("RootCategoryName").toLowerCase().equals("grocery")){
                    storeProduct = shoppingCartItems.getJSONObject(i).getJSONArray("ShoppingCartItems");

                    for(int j=0; j<storeProduct.length(); j++){
                        totalPrice += storeProduct.getJSONObject(j).getDouble("Price") * storeProduct.getJSONObject(j).getInt("Quantity");
//                        totalDiscount += storeProduct.getJSONObject(j).getDouble("Discount") * storeProduct.getJSONObject(j).getInt("Quantity");
                        totalDiscount += storeProduct.getJSONObject(j).getDouble("Discount");
                        totalPriceWithDiscount += storeProduct.getJSONObject(j).getDouble("PriceWithDiscount") * storeProduct.getJSONObject(j).getInt("Quantity");
                    }
                } else {
                    promoProduct = shoppingCartItems.getJSONObject(i).getJSONArray("ShoppingCartItems");

                    for(int j=0; j<promoProduct.length(); j++){
                        if(promoProduct.getJSONObject(j).getBoolean("IsSelectedPromo")){
                            if(promoProduct.getJSONObject(j).getBoolean("IsQuantityUpdate")){
                                qtys = promoProduct.getJSONObject(j).getInt("QuantityUpdate");
                            } else {
                                qtys = promoProduct.getJSONObject(j).getInt("Quantity");
                            }

                            totalPrice += promoProduct.getJSONObject(j).getDouble("Price") * qtys;
                            totalPriceWithDiscount += promoProduct.getJSONObject(j).getDouble("PriceWithDiscount") * qtys;
//                            totalDiscount += promoProduct.getJSONObject(j).getDouble("Discount");
                            totalDiscount = totalPrice - totalPriceWithDiscount;
                        }
                    }
                }
            }
        } catch (Exception e){
            e.printStackTrace();
        }

//        totalBelanja.setText("Rp " + df.format(totalPrice).replace(",","."));

        if(totalDiscount > 0.0){
            totalDiscountTransaction.setText("Rp " + df.format(totalDiscount).replace(",","."));
        } else {
            totalDiscountTransaction.setText("Rp 0");
        }

        shippingCost = Double.valueOf(sessionManager.getKeyTotalShippingCost());

        totalTransaction.setText("Rp " + df.format(totalPrice - totalDiscount  - voucher).replace(",","."));
        totalShippingCost.setText("Rp " + df.format(shippingCost).replace(",","."));

        sessionManager.setKeyTotalPrice(Integer.valueOf(totalPrice.toString().split("\\.")[0]));
        sessionManager.setKeyTotalDiscount(Integer.valueOf(String.valueOf(totalDiscount).split("\\.")[0]));
        sessionManager.setKeyTotalVoucher(Integer.valueOf(voucher.toString().split("\\.")[0]));
        stopLoader();
    }

    @Override
    @SuppressWarnings("deprecation")
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.cart_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.icon_close) {
            finish();
            overridePendingTransition(R.anim.right_in, R.anim.back_top_out);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.back_top_in, R.anim.back_top_out);
    }

    @Override
    public void onResume(){
        super.onResume();

        totalPrice = Double.valueOf(sessionManager.getKeyTotalPrice());
        totalDiscount = Double.valueOf(sessionManager.getKeyTotalDiscount());
        voucher = Double.valueOf(sessionManager.getKeyTotalVoucher());
        shippingCost = Double.valueOf(sessionManager.getKeyTotalShippingCost());

        totalTransaction.setText("Rp " + df.format(totalPrice - totalDiscount  - voucher).replace(",","."));
        totalShippingCost.setText("Rp " + df.format(shippingCost).replace(",","."));

        if (sessionManager.getKeyCouponList().length() > 0){
            couponList.setVisibility(View.VISIBLE);
            List<String> listCoupon = new ArrayList<>(Arrays.asList(sessionManager.getKeyCouponList().split(",")));
            couponAdapter = new CouponAdapter(CartActivity.this, listCoupon, "cart");
            couponList.setAdapter(couponAdapter);
        }else{
            couponList.setVisibility(View.GONE);
        }

        checkStockProduct = false;
        promoBool = false;
    }

    public void makeJsonObjectGet(String url, final String type){
        System.out.println("'mfp " + url);
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                url, null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        System.out.println("'mfp " + response);
                        try {
                            if (response == null || response.length() == 0){
                                stopLoader();
                                Toast.makeText(CartActivity.this, "Gagal terkoneksi server", Toast.LENGTH_LONG).show();
                            }else{
                                if (type.equalsIgnoreCase("getMap")) {
                                    JSONArray results = response.getJSONArray("results");
                                    JSONObject geometry = results.getJSONObject(0).getJSONObject("geometry");
                                    JSONObject location = geometry.getJSONObject("location");

                                    addressLatLng = new LatLng(location.getDouble("lat"), location.getDouble("lng"));
                                    if (addressLatLng != null) {
                                        Helper.moveMapCamera(mMap, addressLatLng, 15);
                                    }
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(CartActivity.this, "Gagal terkoneksi server", Toast.LENGTH_LONG).show();
            }
        });

        AppController.getInstance().addToRequestQueue(jsonObjReq);
    }

    public void countDownStart(JSONObject jsonObject, String type) {
        stopLoader();
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View convertView = null;

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.dialog_info_countdown, null);
        }

        final TextView hourText = (TextView) convertView.findViewById(R.id.hour_text);
        final TextView minuteText = (TextView) convertView.findViewById(R.id.minute_text);
        final TextView secondText = (TextView) convertView.findViewById(R.id.second_text);
        Button btnClose = (Button) convertView.findViewById(R.id.btn_close);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(CartActivity.this);
        alertDialogBuilder.setView(convertView);
        final AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.show();

        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                voucherText.setText("");
                alertDialog.dismiss();
            }
        });

        final Handler handler = new Handler();
        String newTime = "";

        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String myTime = df.format(new Date());
        try {
            if (type.equals("payment")){
                Date d = df.parse(myTime);

                Calendar cal = Calendar.getInstance();
                cal.setTime(d);

                cal.add(Calendar.HOUR, Integer.valueOf(jsonObject.getString("Message").split(" ")[1].split(":")[0]));
                cal.add(Calendar.MINUTE, Integer.valueOf(jsonObject.getString("Message").split(" ")[1].split(":")[1]));
                cal.add(Calendar.SECOND, Integer.valueOf(jsonObject.getString("Message").split(" ")[1].split(":")[2]));

                newTime = df.format(cal.getTime());
            }else{
                newTime = jsonObject.getString("TenggangWaktu").split("\\.")[0].replace("T", " ");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        final String finalHour = newTime;
        runnable = new Runnable() {
            @Override
            public void run() {
                handler.postDelayed(this, 1000);
                try {
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    Date eventDate = dateFormat.parse(finalHour);
                    Date currentDate = new Date();

                    if (!currentDate.after(eventDate)) {
                        long diff = eventDate.getTime() - currentDate.getTime();
                        int seconds = (int) (diff / 1000) % 60 ;
                        int minutes = (int) ((diff / (1000*60)) % 60);
                        int hours   = (int) ((diff / (1000*60*60)) % 24);

                        hourText.setText(String.format("%02d", hours));
                        minuteText.setText(String.format("%02d", minutes));
                        secondText.setText(String.format("%02d", seconds));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        handler.postDelayed(runnable, 0);
    }

    public void setButtonEnabled(boolean isEdit){
        if (isEdit){
            nextToPromo.setEnabled(false);
            nextToPromo.setBackgroundResource(R.drawable.button_style_4);
        }else{
            nextToPromo.setEnabled(true);
            nextToPromo.setBackgroundResource(R.drawable.button_style_1);
        }
    }
    public void runLoader(){
        preloader.setVisibility(View.VISIBLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }

    public void stopLoader(){
        preloader.setVisibility(View.GONE);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }
}