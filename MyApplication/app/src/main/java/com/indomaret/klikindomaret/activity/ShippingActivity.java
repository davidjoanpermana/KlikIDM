package com.indomaret.klikindomaret.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.InputFilter;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
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
import com.indomaret.klikindomaret.adapter.CouponAdapter;
import com.indomaret.klikindomaret.app.AppController;
import com.indomaret.klikindomaret.helper.Month;
import com.indomaret.klikindomaret.helper.SQLiteHandler;
import com.indomaret.klikindomaret.helper.SessionManager;
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
import java.util.List;

public class ShippingActivity extends AppCompatActivity {
    public static Activity shippingActivity;
    private SessionManager sessionManager;
    private Toolbar toolbar;
    private Intent intent;
    private SQLiteHandler sqLiteHandler;

    private String minShipping, maxShipping, store, mCustomerAddressId, serverTime, days, stringDays, latitude, longitude;
    private String date = "";
    private String time = "";
    private String expiredDate = "";
    private String isIPPCover = "";
    private String shippingType = "";
    private String storeCode = "";
    private String infoStore = "";
    private String addressTextValue = "";
    private String addressSelected = "";
    private String[] stringSplit;
    private List<String> listCoupon = new ArrayList<>();
    private Integer leadTime = 0;
    private Integer d, m, y;
    private Double plazaShippingCost = 0.0;
    private Double storeShippingCost = 0.0;
    private Double shippingCost = 0.0;
    private Double totalPrice = 0.0;
    private Double totalDiscount = 0.0;
    private Double voucher = 0.0;
    private Double coupon = 0.0;
    private Double storeProductTotal, nonstoreProductTotal, paaiProductTotal, virtualProductTotal, ippProductTotal;
    private boolean isSend, isDeliveryOrder, isPaai, isIPP, status;
    private boolean isStart = true;
    private DecimalFormat df = new DecimalFormat("#,###");
    private JSONObject storeObject, storePlazaObject;
    private JSONObject defaultAddressObject = new JSONObject();
    private JSONObject defaultAddressStoreObject = new JSONObject();
    private JSONObject lastStoreObject = new JSONObject();
    private JSONObject lastStorePlazaObject = new JSONObject();
    private JSONArray specialDatePAAIs = new JSONArray();
    private JSONArray normalDatePAAIs = new JSONArray();
    private JSONArray shoppingCartItems = new JSONArray();
    private JSONArray ippCityList = new JSONArray();
    private JSONArray storeProductObject = new JSONArray();
    private JSONArray listAddressPAAI = new JSONArray();
    private List<JSONObject> virtualProductObject = new ArrayList<>();
    private List<String> coupons, addressList;
    private JSONArray nonstoreProductObject = new JSONArray();
    private JSONArray paaiProductObject = new JSONArray();
    private JSONArray nonPaaiProductObject = new JSONArray();
    private Month dateName = new Month();
    private Runnable runnable;

    private View underLinePick, underLineSend, underLinePickPlaza, underLineSendPlaza;
    private RelativeLayout preloader, pickOrder, sendOrder, relPesananDiambil, relPesananDiantar;
    private LinearLayout linearBtnStore, storeProductContainer, sendAddressContainer, pickingStoreContainer,  linearBtnNonstore, linIpp, sendAddressContainerIpp,
            pickStoreContainerIpp, linearPaai, virtualProductContainer, linearTotal, linearRingkasan, sendAddress, pickAddress, sendAddressPlaza,
            pickAddressPlaza, linearStoreShippingCost;
    private TextView btnStore, pickOrderText, sendOrderText, receiverName, receiverAddress1, receiverCity, receiverZipCode, dateTimeSending, storeName, storeAddress,
            storeZipcode, dateTimePicking, shippingCostStore, totalPriceStore, btnNonStore, relPesananDiambilText, relPesananDiantarText, receiverNameIpp, receiverAddressIpp,
            storeNameIpp, storeAddressIpp, gratisText, estimasiTime, tvTotalPriceIPP, totalPriceVirtual, mTitle, totalVoucher, totalPricePAAI, btnPAAI, infoAddressPAAI,
            paaiShippingCost;
    private TextView totalTransaction, totalDiscountText, totalShippingCost, sendAddressText, pickAddressText, sendAddressPlazaText, pickAddressPlazaText, deliveryName, infoVoucher;
    private EditText sendDate, voucherText;
    private Spinner spAddressPAAI;
    private ImageView imagePlaza, imageArrow, btnAdd;
    private Button nextToPayment, btnVoucher;
    private HeightAdjustableListView couponList;
    private CouponAdapter couponAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shipping);

        sessionManager = new SessionManager(this);
        intent = getIntent();
        shippingActivity = this;
        sqLiteHandler = new SQLiteHandler(ShippingActivity.this);

        toolbar = (Toolbar) findViewById(R.id.app_toolbar);
        mTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
        toolbar.setTitle("");
        mTitle.setText("Pengiriman");
        setSupportActionBar(toolbar);

        underLinePick = (View) findViewById(R.id.underline_pick);
        underLineSend = (View) findViewById(R.id.underline_send);
        underLinePickPlaza = (View) findViewById(R.id.underline_pick_plaza);
        underLineSendPlaza = (View) findViewById(R.id.underline_send_plaza);

        preloader = (RelativeLayout) findViewById(R.id.preloader);
        linearBtnStore = (LinearLayout) findViewById(R.id.linear_btn_store);
        storeProductContainer = (LinearLayout) findViewById(R.id.store_product_container);
        pickOrder = (RelativeLayout) findViewById(R.id.pick_order);
        sendOrder = (RelativeLayout) findViewById(R.id.send_order);
        sendAddressContainer = (LinearLayout) findViewById(R.id.send_address_container);
        pickingStoreContainer = (LinearLayout) findViewById(R.id.pick_store_container);
        linearBtnNonstore = (LinearLayout) findViewById(R.id.linear_btn_nonstore);
        linIpp = (LinearLayout) findViewById(R.id.linIpp);
        relPesananDiambil = (RelativeLayout) findViewById(R.id.relPesananDiambil);
        relPesananDiantar = (RelativeLayout) findViewById(R.id.relPesananDiantar);
        sendAddressContainerIpp = (LinearLayout) findViewById(R.id.send_address_container_ipp);
        pickStoreContainerIpp = (LinearLayout) findViewById(R.id.pick_store_container_ipp);
        linearPaai = (LinearLayout) findViewById(R.id.linear_paai);
        virtualProductContainer = (LinearLayout) findViewById(R.id.virtual_product_container);
        linearTotal = (LinearLayout) findViewById(R.id.footer);
        linearRingkasan = (LinearLayout) findViewById(R.id.discount_container);
        sendAddress = (LinearLayout) findViewById(R.id.send_address);
        pickAddress = (LinearLayout) findViewById(R.id.pick_address);
        sendAddressPlaza = (LinearLayout) findViewById(R.id.send_address_plaza);
        pickAddressPlaza = (LinearLayout) findViewById(R.id.pick_address_plaza);
        linearStoreShippingCost = (LinearLayout) findViewById(R.id.linear_store_shipping_cost);

        btnStore = (TextView) findViewById(R.id.btn_store);
        pickOrderText = (TextView) findViewById(R.id.pick_order_text);
        sendOrderText = (TextView) findViewById(R.id.send_order_text);
        receiverName = (TextView) findViewById(R.id.receiver_name);
        receiverAddress1 = (TextView) findViewById(R.id.receiver_address1);
        receiverCity = (TextView) findViewById(R.id.receiver_city);
        receiverZipCode = (TextView) findViewById(R.id.receiver_pos_code);
        dateTimeSending = (TextView) findViewById(R.id.datetime_sending);
        storeName = (TextView) findViewById(R.id.store_name);
        storeAddress = (TextView) findViewById(R.id.store_address);
        storeZipcode = (TextView) findViewById(R.id.store_zipcode);
        dateTimePicking = (TextView) findViewById(R.id.datetime_picking);
        shippingCostStore = (TextView) findViewById(R.id.store_shipping_cost);
        totalPriceStore = (TextView) findViewById(R.id.total_price_store);
        btnNonStore = (TextView) findViewById(R.id.btn_nonstore);
        relPesananDiambilText = (TextView) findViewById(R.id.relPesananDikirim_text);
        relPesananDiantarText = (TextView) findViewById(R.id.relPesananDiantar_text);
        receiverNameIpp = (TextView) findViewById(R.id.receiver_name_ipp);
        receiverAddressIpp = (TextView) findViewById(R.id.receiver_address_ipp);
        storeNameIpp = (TextView) findViewById(R.id.store_name_ipp);
        storeAddressIpp = (TextView) findViewById(R.id.store_address_ipp);
        gratisText = (TextView) findViewById(R.id.gratis_text);
        estimasiTime = (TextView) findViewById(R.id.estimasi_time);
        tvTotalPriceIPP = (TextView) findViewById(R.id.nonstore_total_price_ipp);
        totalPriceVirtual = (TextView) findViewById(R.id.virtual_total_price);
        totalVoucher = (TextView) findViewById(R.id.total_voucher);
        totalPricePAAI = (TextView) findViewById(R.id.total_price_paai);
        btnPAAI= (TextView) findViewById(R.id.btn_paai);
        infoAddressPAAI= (TextView) findViewById(R.id.info_address_paai);
        paaiShippingCost= (TextView) findViewById(R.id.paai_shipping_cost);
        totalTransaction = (TextView) findViewById(R.id.total_transaction);
        totalDiscountText = (TextView) findViewById(R.id.total_discount);
        totalShippingCost = (TextView) findViewById(R.id.total_ongkir);
        sendAddressText = (TextView) findViewById(R.id.send_address_text);
        pickAddressText = (TextView) findViewById(R.id.pick_address_text);
        sendAddressPlazaText = (TextView) findViewById(R.id.send_address_plaza_text);
        pickAddressPlazaText = (TextView) findViewById(R.id.pick_address_plaza_text);
        deliveryName = (TextView) findViewById(R.id.delivery_name);
        infoVoucher = (TextView) findViewById(R.id.info_voucher);
        infoVoucher = (TextView) findViewById(R.id.info_voucher);

        sendDate = (EditText) findViewById(R.id.edittext_date);
        voucherText = (EditText) findViewById(R.id.voucher_text);
        spAddressPAAI = (Spinner) findViewById(R.id.spinner_address_paai);
        imagePlaza = (ImageView) findViewById(R.id.Image_plaza);
        imageArrow = (ImageView) findViewById(R.id.image_arrow);
        btnAdd = (ImageView) findViewById(R.id.btn_add);
        nextToPayment = (Button) findViewById(R.id.next_to_payment);
        btnVoucher = (Button) findViewById(R.id.btn_voucher);
        couponList = (HeightAdjustableListView) findViewById(R.id.coupon_list);

        voucherText.setFilters(new InputFilter[]{new InputFilter.AllCaps()});

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(ShippingActivity.this, AddAdressActivity.class);
                intent.putExtra("type", "addShipping");
                startActivityForResult(intent, 8);
                overridePendingTransition(R.anim.left_in, R.anim.left_out);
            }
        });

        spAddressPAAI.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position > 0) {
                    try {
                        mCustomerAddressId = listAddressPAAI.getJSONObject(position -1).getString("ID");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

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

        linearTotal.setOnClickListener(new View.OnClickListener() {
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

        sendOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendOrder.setBackgroundResource(R.color.colorWhite);
                sendOrderText.setTextColor(Color.parseColor("#0079C2"));
                underLineSend.setBackgroundResource(R.color.colorPrimary);

                pickOrder.setBackgroundResource(R.color.backgroundGrey);
                pickOrderText.setTextColor(Color.parseColor("#000000"));
                underLinePick.setBackgroundResource(R.color.backgroundGrey);

                sendAddressContainer.setVisibility(View.VISIBLE);
                pickingStoreContainer.setVisibility(View.GONE);

                isSend = true;
                shippingType = "IsDelivery";
                sessionManager.setkeyStore(null);

                if (sqLiteHandler.getDefaultAddress() != null && sqLiteHandler.getDefaultAddress().length() > 0) {
                    sendAddress.setVisibility(View.VISIBLE);
                    sendAddressText.setVisibility(View.GONE);

                    sessionManager.setKeyShipping(true);

                    if (defaultAddressStoreObject != null && defaultAddressStoreObject.length() > 0){
                        setDefaultAddress(defaultAddressStoreObject.toString());
                    }else{
                        setDefaultAddress(sqLiteHandler.getDefaultAddress());
                    }

                    if (date.length() > 0 && time.length() > 0){
                        dateTimeSending.setText(date + " | " + time);
                    }else{
                        dateTimeSending.setText("");
                    }
                }else{
                    sendAddress.setVisibility(View.GONE);
                    sendAddressText.setVisibility(View.VISIBLE);
                }
            }
        });

        pickOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickOrder.setBackgroundResource(R.color.colorWhite);
                pickOrderText.setTextColor(Color.parseColor("#0079C2"));
                underLinePick.setBackgroundResource(R.color.colorPrimary);

                sendOrder.setBackgroundResource(R.color.backgroundGrey);
                sendOrderText.setTextColor(Color.parseColor("#000000"));
                underLineSend.setBackgroundResource(R.color.backgroundGrey);

                pickingStoreContainer.setVisibility(View.VISIBLE);
                sendAddressContainer.setVisibility(View.GONE);
                isSend = false;
                shippingType = "";

                totalPrice = Double.valueOf(sessionManager.getKeyTotalPrice());
                totalDiscount = Double.valueOf(sessionManager.getKeyTotalDiscount());
                voucher = Double.valueOf(sessionManager.getKeyTotalVoucher());
                coupon = Double.valueOf(sessionManager.getKeyTotalCoupon());

                totalTransaction.setText("Rp " + df.format(totalPrice - totalDiscount + - voucher).replace(",","."));
                totalDiscountText.setText("Rp " + df.format(totalDiscount).replace(",","."));
                totalShippingCost.setText("Rp " + df.format(shippingCost).replace(",","."));
                totalVoucher.setText("Rp " + df.format(coupon + voucher).replace(",","."));

                defaultStore();
            }
        });

        sendAddress.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        intent = new Intent(ShippingActivity.this, AddressActivity.class);
                        startActivityForResult(intent, 1);
                        overridePendingTransition(R.anim.left_in, R.anim.left_out);
                        isSend = true;
                        isStart = true;
                    }
                }
        );

        sendAddressText.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        intent = new Intent(ShippingActivity.this, AddressActivity.class);
                        startActivityForResult(intent, 1);
                        overridePendingTransition(R.anim.left_in, R.anim.left_out);
                        isSend = true;
                        isStart = true;
                    }
                }
        );

        pickAddress.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        intent = new Intent(ShippingActivity.this, MapActivity.class);
                        intent.putExtra("serverTime", serverTime);
                        intent.putExtra("type", "storePick");
                        startActivityForResult(intent, 2);
                        overridePendingTransition(R.anim.left_in, R.anim.left_out);
                        isSend = false;
                    }
                }
        );

        pickAddressText.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        intent = new Intent(ShippingActivity.this, MapActivity.class);
                        intent.putExtra("serverTime", serverTime);
                        intent.putExtra("type", "storePick");
                        startActivityForResult(intent, 2);
                        overridePendingTransition(R.anim.left_in, R.anim.left_out);
                        isSend = false;
                    }
                }
        );

        dateTimeSending.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (defaultAddressStoreObject != null && defaultAddressStoreObject.length() > 0){
                    try {
                        intent = new Intent(ShippingActivity.this, ShippingTimeActivity.class);
                        intent.putExtra("time", serverTime);
                        intent.putExtra("customerAddressID", defaultAddressStoreObject.getString("ID"));
                        startActivityForResult(intent, 6);
                        overridePendingTransition(R.anim.left_in, R.anim.left_out);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }else{
                    Toast toast = Toast.makeText(getApplicationContext(), "Alamat Pengiriman belum ada.", Toast.LENGTH_SHORT);
                    toast.show();
                }
            }
        });

        dateTimePicking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            try {
                if (storeObject != null && storeObject.length() > 0){
                    intent = new Intent(ShippingActivity.this, ShippingTimeActivity.class);
                    intent.putExtra("time", serverTime);
                    if (storeObject.toString().contains("StoreId")) intent.putExtra("storeId", storeObject.getString("StoreId"));
                    else if (storeObject.toString().contains("ID")) intent.putExtra("storeId", storeObject.getString("ID"));
                    startActivityForResult(intent, 7);
                    overridePendingTransition(R.anim.left_in, R.anim.left_out);
                }else{
                    Toast toast = Toast.makeText(getApplicationContext(), "Alamat toko belum ada.", Toast.LENGTH_SHORT);
                    toast.show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            }
        });

        relPesananDiantar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                relPesananDiantar.setBackgroundResource(R.color.colorWhite);
                relPesananDiantarText.setTextColor(Color.parseColor("#0079C2"));
                underLineSendPlaza.setBackgroundResource(R.color.colorPrimary);

                relPesananDiambil.setBackgroundResource(R.color.backgroundGrey);
                relPesananDiambilText.setTextColor(Color.parseColor("#000000"));
                underLinePickPlaza.setBackgroundResource(R.color.backgroundGrey);

                sendAddressContainerIpp.setVisibility(View.VISIBLE);
                pickStoreContainerIpp.setVisibility(View.GONE);
                isDeliveryOrder = true;
                sessionManager.setKeyStorePlaza(null);
                spAddressPAAI.setEnabled(false);
                infoAddressPAAI.setVisibility(View.VISIBLE);

                if (defaultAddressObject != null && defaultAddressObject.length() > 0){
                    sendAddressPlaza.setVisibility(View.VISIBLE);
                    sendAddressPlazaText.setVisibility(View.GONE);

                    setDefaultAddressPlaza(defaultAddressObject.toString());
                } else if (sqLiteHandler.getDefaultAddress() != null && sqLiteHandler.getDefaultAddress().length() > 0) {
                    sendAddressPlaza.setVisibility(View.VISIBLE);
                    sendAddressPlazaText.setVisibility(View.GONE);

                    setDefaultAddressPlaza(sqLiteHandler.getDefaultAddress());
                }else{
                    sendAddressPlaza.setVisibility(View.GONE);
                    sendAddressPlazaText.setVisibility(View.VISIBLE);
                }
            }
        });

        relPesananDiambil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                relPesananDiambil.setBackgroundResource(R.color.colorWhite);
                relPesananDiambilText.setTextColor(Color.parseColor("#0079C2"));
                underLinePickPlaza.setBackgroundResource(R.color.colorPrimary);

                relPesananDiantar.setBackgroundResource(R.color.backgroundGrey);
                relPesananDiantarText.setTextColor(Color.parseColor("#000000"));
                underLineSendPlaza.setBackgroundResource(R.color.backgroundGrey);

                pickStoreContainerIpp.setVisibility(View.VISIBLE);
                sendAddressContainerIpp.setVisibility(View.GONE);
                isDeliveryOrder = false;
                spAddressPAAI.setEnabled(true);
                infoAddressPAAI.setVisibility(View.GONE);

                defaultStorePlaza();
            }
        });

        pickAddressPlaza.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent = new Intent(ShippingActivity.this, MapPlazaActivity.class);
                intent.putExtra("isIPP", isIPP);
                startActivityForResult(intent, 4);
                overridePendingTransition(R.anim.left_in, R.anim.left_out);
                isDeliveryOrder = false;
                mCustomerAddressId = null;
            }
        });

        pickAddressPlazaText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent = new Intent(ShippingActivity.this, MapPlazaActivity.class);
                intent.putExtra("isIPP", isIPP);
                startActivityForResult(intent, 4);
                overridePendingTransition(R.anim.left_in, R.anim.left_out);
                isDeliveryOrder = false;
                mCustomerAddressId = null;
            }
        });


        sendAddressPlaza.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent = new Intent(ShippingActivity.this, AddressActivity.class);
                startActivityForResult(intent, 5);
                overridePendingTransition(R.anim.left_in, R.anim.left_out);
                isDeliveryOrder = true;
                storeCode = null;
            }
        });

        sendAddressPlazaText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent = new Intent(ShippingActivity.this, AddressActivity.class);
                startActivityForResult(intent, 5);
                overridePendingTransition(R.anim.left_in, R.anim.left_out);
                isDeliveryOrder = true;
                storeCode = null;
            }
        });

        sendDate.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        LayoutInflater inflater = (LayoutInflater) ShippingActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                        View writeReview = inflater.inflate(R.layout.activity_shipping_date, null);

                        final Calendar calendar = Calendar.getInstance();
                        final StringBuilder builder=new StringBuilder();
                        final DatePicker datePicker = (DatePicker) writeReview.findViewById(R.id.date_picker);
                        Button cancelReview = (Button) writeReview.findViewById(R.id.cancel_review);
                        final Button sendReview = (Button) writeReview.findViewById(R.id.send_review);

                        Integer year, month, day;
                        SimpleDateFormat simpleDate = new SimpleDateFormat("yyyy-MM-dd");
                        String[] splitDate1 = serverTime.split("T");
                        final String[] splitDate2 = splitDate1[0].split("-");

                        try {
                            calendar.setTime(simpleDate.parse(splitDate1[0]));
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }

                        sendReview.setEnabled(true);
                        sendReview.setBackgroundResource(R.drawable.button_style_3);

                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ShippingActivity.this);
                        alertDialogBuilder.setView(writeReview);
                        final AlertDialog alertDialog = alertDialogBuilder.create();
                        alertDialog.setCanceledOnTouchOutside(false);
                        alertDialog.show();

                        datePicker.init(datePicker.getYear(), datePicker.getMonth(), datePicker.getDayOfMonth(), new DatePicker.OnDateChangedListener() {
                            @Override
                            public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                status = false;
                                Calendar cal = Calendar.getInstance();
                                cal.set(Calendar.YEAR, year);
                                cal.set(Calendar.MONTH, monthOfYear);
                                cal.set(Calendar.DATE, dayOfMonth);

                                days = dateName.getDay3(String.valueOf(cal.get(Calendar.DAY_OF_WEEK)));
                                stringDays = dateName.getDay2(String.valueOf(cal.get(Calendar.DAY_OF_WEEK)));

                                System.out.println("on click");
                                if ((year <= Integer.valueOf(splitDate2[0])) && (monthOfYear+1) < Integer.valueOf(splitDate2[1])){
                                    final Toast toast = Toast.makeText(getApplicationContext(), "Tidak dapat memilih tanggal tersebut", Toast.LENGTH_SHORT);
                                    toast.show();

                                    Handler handler = new Handler();
                                    handler.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            toast.cancel();
                                        }
                                    }, 500);

                                    sendReview.setEnabled(false);
                                    sendReview.setBackgroundColor(Color.LTGRAY);
                                    status = true;
                                }else if (dayOfMonth < (Integer.valueOf(splitDate2[2]) + leadTime) && (monthOfYear+1) <= Integer.valueOf(splitDate2[1])){
                                    final Toast toast = Toast.makeText(getApplicationContext(), "Tidak dapat memilih tanggal tersebut", Toast.LENGTH_SHORT);
                                    toast.show();

                                    Handler handler = new Handler();
                                    handler.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            toast.cancel();
                                        }
                                    }, 500);

                                    sendReview.setEnabled(false);
                                    sendReview.setBackgroundColor(Color.LTGRAY);
                                    status = true;
                                }else{
                                    sendReview.setEnabled(true);
                                    sendReview.setBackgroundResource(R.drawable.button_style_3);
                                }

                                try {
                                    for (int i=0; i<specialDatePAAIs.length();i++){
                                        stringSplit = specialDatePAAIs.getString(i).split("-");
                                        d = Integer.valueOf(stringSplit[1]);
                                        m = Integer.valueOf(stringSplit[0]);
                                        y = Integer.valueOf(stringSplit[2]);

                                        if(dayOfMonth == d && monthOfYear+1 == m && year == y){
                                            if (!status) {
                                                final Toast toast = Toast.makeText(getApplicationContext(), "Tidak ada pengiriman pada tanggal " + d + "-" + m + "-" + y, Toast.LENGTH_SHORT);
                                                toast.show();

                                                Handler handler = new Handler();
                                                handler.postDelayed(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        toast.cancel();
                                                    }
                                                }, 500);

                                                sendReview.setEnabled(false);
                                                sendReview.setBackgroundColor(Color.LTGRAY);
                                                status = true;
                                            }
                                        }else{
                                            if (!status){
                                                sendReview.setEnabled(true);
                                                sendReview.setBackgroundResource(R.drawable.button_style_3);
                                            }
                                        }
                                    }

                                    for (int i=0; i<normalDatePAAIs.length();i++){
                                        if(normalDatePAAIs.getString(i).equals(days)){
                                            if (!status) {
                                                final Toast toast = Toast.makeText(getApplicationContext(), "Tidak ada pengiriman pada hari" + stringDays, Toast.LENGTH_SHORT);
                                                toast.show();

                                                Handler handler = new Handler();
                                                handler.postDelayed(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        toast.cancel();
                                                    }
                                                }, 500);

                                                sendReview.setEnabled(false);
                                                sendReview.setBackgroundColor(Color.LTGRAY);
                                                status = true;
                                            }
                                        }else{
                                            if (!status){
                                                sendReview.setEnabled(true);
                                                sendReview.setBackgroundResource(R.drawable.button_style_3);
                                            }
                                        }
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        });

                        if(sendDate.getText().toString().contains("/")){
                            String[] splitString = sendDate.getText().toString().split("/");
                            month = Integer.valueOf(splitString[1]);
                            day = Integer.valueOf(splitString[0]);
                            year = Integer.valueOf(splitString[2]);

                            datePicker.updateDate(year, month-1, day);
                        }

                        cancelReview.setOnClickListener(
                                new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        alertDialog.hide();
                                    }
                                }
                        );

                        final JSONArray specialDatePAAIs2 = specialDatePAAIs;
                        sendReview.setOnClickListener(
                                new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        status = false;
                                        Calendar cal = Calendar.getInstance();
                                        cal.set(Calendar.YEAR, datePicker.getYear());
                                        cal.set(Calendar.MONTH, datePicker.getMonth());
                                        cal.set(Calendar.DATE, datePicker.getDayOfMonth());

                                        days = dateName.getDay3(String.valueOf(cal.get(Calendar.DAY_OF_WEEK)));
                                        stringDays = dateName.getDay2(String.valueOf(cal.get(Calendar.DAY_OF_WEEK)));

                                        try {
                                            if ((datePicker.getYear() < Integer.valueOf(splitDate2[0]) || datePicker.getYear() == Integer.valueOf(splitDate2[0])) &&
                                                    (datePicker.getMonth() + 1) < Integer.valueOf(splitDate2[1])){
                                                Toast.makeText(getApplicationContext(), "Tidak dapat memilih tanggal tersebut", Toast.LENGTH_LONG).show();
                                                status = true;
                                            }else if (datePicker.getDayOfMonth() < (Integer.valueOf(splitDate2[2]) + leadTime) && (datePicker.getMonth() + 1) == Integer.valueOf(splitDate2[1])){
                                                Toast.makeText(getApplicationContext(), "Tidak dapat memilih tanggal tersebut", Toast.LENGTH_LONG).show();
                                                status = true;
                                            }

                                            for (int i=0; i<specialDatePAAIs.length();i++){
                                                stringSplit = specialDatePAAIs.getString(i).split("-");
                                                d = Integer.valueOf(stringSplit[1]);
                                                m = Integer.valueOf(stringSplit[0]);
                                                y = Integer.valueOf(stringSplit[2]);

                                                if(datePicker.getDayOfMonth() == d && (datePicker.getMonth() + 1) == m && datePicker.getYear() == y){
                                                    if (!status) {
                                                        Toast.makeText(getApplicationContext(), "Tidak ada pengiriman pada tanggal " + d + "-" + m + "-" + y, Toast.LENGTH_LONG).show();
                                                        status = true;
                                                    }
                                                }
                                            }

                                            for (int i=0; i<normalDatePAAIs.length();i++){
                                                if(normalDatePAAIs.getString(i).equals(days)){
                                                    if (!status) {
                                                        Toast.makeText(getApplicationContext(), "Tidak ada pengiriman pada hari " + stringDays, Toast.LENGTH_LONG).show();
                                                        status = true;
                                                    }
                                                }
                                            }
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }

                                        if (!status){
                                            builder.append(datePicker.getDayOfMonth()).append("/");
                                            builder.append(datePicker.getMonth() + 1).append("/");
                                            builder.append(datePicker.getYear());

                                            sendDate.setText(builder.toString());
                                        }

                                        alertDialog.hide();
                                    }
                                }
                        );
                    }
                }
        );

        nextToPayment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoPayment();
            }
        });

        jsonArrayRequest(API.getInstance().getApiServerDate() + "?mfp_id=" + sessionManager.getKeyMfpId(), "time");
        jsonArrayRequest(API.getInstance().getApiServerTime(), "preparation");

        jsonArrayRequest(API.getInstance().getApiShippingMethod()+"?id="+sessionManager.getCartId()
                +"&customerID="+sessionManager.getUserID()
                +"&isVirtual=false&isParcelView=false&regionId="+sessionManager.getRegionID()
                +"&mfp_id="+sessionManager.getKeyMfpId(), "shipping");

        jsonArrayRequest(API.getInstance().getApiGetCart()+"?id=&customerId="+sessionManager.getUserID()
                +"&ShoppingCartID="+sessionManager.getCartId()
                +"&regionID="+sessionManager.getRegionID()
                +"&mfp_id="+sessionManager.getKeyMfpId(), "cart");
    }

    public void reloadCart(){
        jsonArrayRequest(API.getInstance().getApiGetCart()+"?id=&customerId="+sessionManager.getUserID()
                +"&ShoppingCartID="+sessionManager.getCartId()
                +"&regionID="+sessionManager.getRegionID()
                +"&mfp_id="+sessionManager.getKeyMfpId(), "cart");
    }

    public void defaultStore(){
        try {
            if (!isSend){
                if (lastStoreObject.getString("Name") == null || lastStoreObject.getString("Name").toLowerCase().equals("null")
                        || lastStoreObject.getString("Name").toLowerCase().equals("")){
                    pickAddress.setVisibility(View.GONE);
                    pickAddressText.setVisibility(View.VISIBLE);
                }else{
                    pickAddress.setVisibility(View.VISIBLE);
                    pickAddressText.setVisibility(View.GONE);

                    sessionManager.setkeyStore(lastStoreObject.toString());
                    sessionManager.setKeyShipping(true);

                    try {
                        storeObject = lastStoreObject;

                        if (storeObject.toString().contains("Zipcode")){
                            storeObject.put("ZipCode", storeObject.getString("Zipcode"));
                        }else if(storeObject.toString().contains("ZipCode")){
                            storeObject.put("Zipcode", storeObject.getString("ZipCode"));
                        }

                        storeName.setText(storeObject.getString("Name") + "(" + storeObject.getString("Code") + ")");
                        storeAddress.setText(storeObject.getString("Street"));
                        storeZipcode.setText(storeObject.getString("Zipcode"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    linearStoreShippingCost.setVisibility(View.GONE);
                }

                if (date.length() > 0 && time.length() > 0){
                    dateTimePicking.setText(date + " | " + time);
                }else{
                    dateTimePicking.setText("");
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void defaultStorePlaza(){
        try {
            if (!isDeliveryOrder){
                if (lastStorePlazaObject.getString("StoreCode") == null || lastStorePlazaObject.getString("StoreCode").toLowerCase().equals("null")
                        || lastStorePlazaObject.getString("StoreCode").toLowerCase().equals("")){
                    pickAddressPlaza.setVisibility(View.GONE);
                    pickAddressPlazaText.setVisibility(View.VISIBLE);
                }else {
                    pickAddressPlaza.setVisibility(View.VISIBLE);
                    pickAddressPlazaText.setVisibility(View.GONE);

                    storeCode = lastStorePlazaObject.getString("StoreCode");
                    sessionManager.setKeyStoreCodePlaza(storeCode);
                    pickStoreContainerIpp.setVisibility(View.VISIBLE);
                    sendAddressContainerIpp.setVisibility(View.GONE);

                    try {
                        JSONObject storeObject = new JSONObject(lastStorePlazaObject.toString());
                        sessionManager.setKeyStorePlaza(storeObject.toString());
                        storeNameIpp.setText(storeObject.getString("StoreName") + "(" + storeObject.getString("StoreCode") + ")");
                        storeAddressIpp.setText(storeObject.getString("Address"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            jsonArrayRequest(API.getInstance().getApiShippingMethod()+"?id="+sessionManager.getCartId()
                    +"&customerID="+sessionManager.getUserID()
                    +"&isVirtual=false&isParcelView=false&regionId="+sessionManager.getRegionID()
                    +"&mfp_id="+sessionManager.getKeyMfpId(), "shipping");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void gotoPayment() {
        String storeAddressId = "00000000-0000-0000-0000-000000000000";
        String plazaAddressId = "00000000-0000-0000-0000-000000000000";
        sessionManager.setKeyCuurentSpinner(0);

        try {
            if (defaultAddressStoreObject == null || defaultAddressStoreObject.length() == 0)
                storeAddressId = "00000000-0000-0000-0000-000000000000";
            else
                storeAddressId = defaultAddressStoreObject.getString("ID");

            if (defaultAddressObject == null || defaultAddressObject.length() == 0)
                plazaAddressId = "00000000-0000-0000-0000-000000000000";
            else
                plazaAddressId = defaultAddressObject.getString("ID");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (storeProductObject.length() > 0 && isSend && (defaultAddressStoreObject == null || defaultAddressStoreObject.length() == 0)) {
            Toast.makeText(ShippingActivity.this, "Alamat pengiriman barang belum ada.", Toast.LENGTH_LONG).show();
        } else if (storeProductObject.length() > 0 && !isSend && (storeObject == null || storeObject.length() == 0)) {
            Toast.makeText(ShippingActivity.this, "Alamat toko untuk barang toko belum ada.", Toast.LENGTH_LONG).show();
        } else if (storeProductObject.length() > 0 && (!isSend || isSend) && (date == null || date.length() == 0)) {
            Toast.makeText(ShippingActivity.this, "Jadwal pengiriman barang belum ada.", Toast.LENGTH_LONG).show();
        } else if (storeProductObject.length() > 0 && storeAddressId.equals("00000000-0000-0000-0000-000000000000") && isSend) {
            Toast.makeText(ShippingActivity.this, "Alamat pengiriman barang toko belum ada.", Toast.LENGTH_LONG).show();
        } else if (nonstoreProductObject.length() > 0 && (defaultAddressObject.length() == 0 || defaultAddressObject == null) && isDeliveryOrder && !isPaai) {
            Toast.makeText(ShippingActivity.this, "Alamat pengiriman barang plaza belum ada.", Toast.LENGTH_LONG).show();
        } else if (nonstoreProductObject.length() > 0 && (storeCode == "" || storeCode == null) && !isDeliveryOrder && !isPaai) {
            Toast.makeText(ShippingActivity.this, "Alamat toko untuk barang non toko belum ada.", Toast.LENGTH_LONG).show();
        } else if (nonstoreProductObject.length() > 0 && !isDeliveryOrder && isPaai && mCustomerAddressId.length() == 0) {
            Toast.makeText(ShippingActivity.this, "Alamat toko untuk barang non toko belum ada.", Toast.LENGTH_LONG).show();
        } else if (nonstoreProductObject.length() > 0 && plazaAddressId.equals("00000000-0000-0000-0000-000000000000") && isDeliveryOrder && !isPaai) {
            Toast.makeText(ShippingActivity.this, "Alamat pengiriman barang plaza belum ada.", Toast.LENGTH_LONG).show();
        } else {
            if (isPaai){
                if (sendDate.getText().toString().length() == 0){
                    Toast.makeText(ShippingActivity.this, "Tanggal pengiriman belum ada.", Toast.LENGTH_LONG).show();
                    nextToPayment.setEnabled(true);
                    return;
                }
            }

            intent = new Intent(ShippingActivity.this, PaymentActivity.class);
            intent.putExtra("plazaAddressId", plazaAddressId);

            if (storeProductObject != null && storeProductObject.length() > 0){
                intent.putExtra("storeProductObject", storeProductObject.toString());
                intent.putExtra("isSend", isSend);
                intent.putExtra("time", time);
                intent.putExtra("date", date);
                intent.putExtra("shippingType", shippingType);
                intent.putExtra("expiredDate", expiredDate);

                if (!isSend){
                    intent.putExtra("storeObject", storeObject.toString());
                }else{
                    intent.putExtra("address", defaultAddressStoreObject.toString());
                }
            }

            if (nonstoreProductObject != null && nonstoreProductObject.length() > 0){
                intent.putExtra("nonstoreProductObject", nonstoreProductObject.toString());
                intent.putExtra("isDeliveryOrder", isDeliveryOrder);
                intent.putExtra("isPaai", isPaai);
                intent.putExtra("isIPP", isIPP);
                intent.putExtra("sendDate", sendDate.getText().toString());

                if (isDeliveryOrder){
                    intent.putExtra("defaultAddressObject", defaultAddressObject.toString());
                    intent.putExtra("infoStore", infoStore);
                    intent.putExtra("latitude", latitude);
                    intent.putExtra("longitude", longitude);
                    intent.putExtra("addressTextValue", addressTextValue);
                    intent.putExtra("mCustomerAddressId", mCustomerAddressId);
                }else{
                    intent.putExtra("storeCode", storeCode);
                }

                if(!isDeliveryOrder && isPaai){
                    intent.putExtra("mCustomerAddressId", mCustomerAddressId);
                }
            }

            startActivity(intent);
            overridePendingTransition(R.anim.left_in, R.anim.left_out);
        }
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
                            Toast.makeText(ShippingActivity.this, "Gagal terkoneksi server", Toast.LENGTH_LONG).show();
                        }else{
                            if (type.equals("shipping")){
                                try {
                                    plazaShippingCost = 0.0;
                                    storeShippingCost = 0.0;

                                    minShipping = response.getJSONObject(0).getString("PlazaMinETD");
                                    maxShipping = response.getJSONObject(0).getString("PlazaMaxETD");
                                    isIPPCover = response.getJSONObject(0).getString("IsIPPCoverageRegion");
                                    sessionManager.setKeyIppCover(isIPPCover);

                                    if (response.getJSONObject(0).getJSONObject("CustomerAddress") == null ||
                                            response.getJSONObject(0).getJSONObject("CustomerAddress").length() == 0){

                                    }else{
                                        mCustomerAddressId = response.getJSONObject(0).getJSONObject("CustomerAddress").getString("ID");
                                    }

                                    sessionManager.setKeyAddressIdPlaza(mCustomerAddressId);

                                    if(sessionManager.getKeyShipping() || sessionManager.getKeyPlazaShipping()){
                                        if(sessionManager.getKeyStore() == null && isSend){
                                            storeShippingCost = response.getJSONObject(0).getDouble("OngkosKirimStore");
                                            shippingCostStore.setText("Rp " + df.format(storeShippingCost).replace(",", "."));
//                                            totalOngkir.setText("Rp " + df.format(ongkir + storeShippingCost).replace(",","."));
//                                        }
                                        } else {
                                            shippingCostStore.setText("(Gratis)");
//                                            totalOngkir.setText("Rp 0");
                                        }

                                        if (sessionManager.getKeyStorePlaza() == null && isDeliveryOrder){
                                            plazaShippingCost = response.getJSONObject(0).getDouble("OngkosKirimPlaza");
//                                            double ongkir = Double.valueOf(totalOngkir.getText().toString().toLowerCase().replace("rp", "").replace(" ", "").replace(".", ""));

                                            if(nonstoreProductObject.length() > 0 && isIPPCover.equals("false")){
                                                gratisText.setText("Rp " + df.format(plazaShippingCost).replace(",","."));
                                            }
//                                        }
                                        }else {
                                            gratisText.setText("(Gratis)");
//                                            totalOngkir.setText("Rp 0");
                                        }

                                        getKeyShipping();
                                    }

                                    cartResponse(response);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            } else if(type.equals("time")){
                                try {
                                    serverTime = response.get(0).toString();
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }else if(type.equals("preparation")){
                                try {
                                    sessionManager.setKeyPreparationTime(response.getJSONObject(0).getInt("SettingValue"));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }else if(type.equals("cart")){
                                showCart(response);
                            }else if(type.equals("address")){
                                if (isStart) setListAddres(response);
                                preloader.setVisibility(View.GONE);
                            }
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(ShippingActivity.this, "Gagal terkoneksi server", Toast.LENGTH_LONG).show();
                stopLoader();
            }
        }, this);

        jsonArrayReq.setRetryPolicy(new DefaultRetryPolicy(60000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppController.getInstance().addToRequestQueue(jsonArrayReq);
    }

    public void setListAddres(JSONArray response){
        addressList = new ArrayList<>();
        addressList.add(String.valueOf(Html.fromHtml("Pilih Alamat")));

        if (response.length() > 0){
            try {
                for (int i=0; i<response.length();i++){
                    addressList.add(response.getJSONObject(i).getString("AddressTitle"));
                    listAddressPAAI.put(response.getJSONObject(i));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            final ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, addressList);
            dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spAddressPAAI.setAdapter(dataAdapter);

            if (!isDeliveryOrder){
                spAddressPAAI.setEnabled(true);
                infoAddressPAAI.setVisibility(View.GONE);
            }else{
                spAddressPAAI.setEnabled(false);
                infoAddressPAAI.setVisibility(View.VISIBLE);
            }

            if (sqLiteHandler.getDefaultAddress().length() > 0){
                JSONObject addressObject;
                try {
                    addressObject = new JSONObject(sqLiteHandler.getDefaultAddress());
                    for (int i=0; i<addressList.size(); i++){
                        if (addressList.get(i).equals(addressObject.getString("AddressTitle"))){
                            spAddressPAAI.setSelection(i);
                            mCustomerAddressId = listAddressPAAI.getJSONObject(i -1).getString("ID");
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

        isStart = false;
    }

    public void showCart(JSONArray response){
        double totalCoupon = 0.0;
        coupons = new ArrayList<>();
        JSONArray shoppingCartItems;

        try {
            if (response.getJSONObject(0).getJSONObject("CartItemNotification").getJSONArray("Items").length() > 0){
                shoppingCartItems = response.getJSONObject(0).getJSONObject("CartItemNotification").getJSONArray("Items");

                if (shoppingCartItems.getJSONObject(0).getString("VBKNominal") == null ||
                        shoppingCartItems.getJSONObject(0).getString("VBKNominal").equals("null") ||
                        shoppingCartItems.getJSONObject(0).getString("VBKNominal").equals("")){
                    voucher = 0.0;
                    totalVoucher.setText("Rp " + df.format(voucher).replace(",","."));
                }else if (shoppingCartItems.getJSONObject(0).getDouble("VBKNominal") > 0.0){
                    voucher = shoppingCartItems.getJSONObject(0).getDouble("VBKNominal");
                    totalVoucher.setText("Rp " + df.format(voucher).replace(",","."));
                }

                for (int i=0; i<shoppingCartItems.length(); i++){
                    if(shoppingCartItems.getJSONObject(i).getString("RootCategoryName").toLowerCase().equals("virtual")){
                        if (shoppingCartItems.getJSONObject(i).getString("VBKCode") != null && !shoppingCartItems.getJSONObject(i).getString("VBKCode").equals("")
                                && !shoppingCartItems.getJSONObject(i).getString("VBKCode").contains("null")){
                            if (coupons.size() > 0){
                                if (!coupons.toString().contains("Voucher : " + shoppingCartItems.getJSONObject(i).getString("VBKCode"))){
                                    coupons.add("Voucher : " + shoppingCartItems.getJSONObject(i).getString("VBKCode"));
                                }
                            }else{
                                coupons.add("Voucher : " + shoppingCartItems.getJSONObject(i).getString("VBKCode"));
                            }
                        }

                        JSONArray virtualProduct = shoppingCartItems.getJSONObject(i).getJSONArray("ShoppingCartItems");

                        for (int j=0; j<virtualProduct.length(); j++){
                            JSONArray couponArray = virtualProduct.getJSONObject(j).getJSONArray("Coupons");
                            for (int k=0; k<couponArray.length(); k++){
                                if (!couponArray.get(k).toString().toLowerCase().equals("null") && !couponArray.get(k).equals(""))
                                    if (coupons.size() > 0){
                                        if (!coupons.toString().contains("i-Kupon : " + couponArray.get(k).toString())){
                                            coupons.add("i-Kupon : " + couponArray.get(k).toString());
                                        }
                                    }else{
                                        coupons.add("i-Kupon : " + couponArray.get(k).toString());
                                    }
                            }
                        }

                        for(int j=0; j<virtualProduct.length(); j++){
//                            totalDiscount += virtualProduct.getJSONObject(j).getDouble("Discount");
                            totalCoupon += virtualProduct.getJSONObject(j).getDouble("KuponNominal");
                        }
                    } else if(shoppingCartItems.getJSONObject(i).getString("RootCategoryName").toLowerCase().equals("non-grocery")){
                        if (shoppingCartItems.getJSONObject(i).getString("VBKCode") != null && !shoppingCartItems.getJSONObject(i).getString("VBKCode").equals("")
                                && !shoppingCartItems.getJSONObject(i).getString("VBKCode").contains("null")){
                            if (coupons.size() > 0){
                                if (!coupons.toString().contains("Voucher : " + shoppingCartItems.getJSONObject(i).getString("VBKCode"))){
                                    coupons.add("Voucher : " + shoppingCartItems.getJSONObject(i).getString("VBKCode"));
                                }
                            }else{
                                coupons.add("Voucher : " + shoppingCartItems.getJSONObject(i).getString("VBKCode"));
                            }
                        }

                        JSONArray nonStoreProduct = shoppingCartItems.getJSONObject(i).getJSONArray("ShoppingCartItems");

                        for (int j=0; j<nonStoreProduct.length(); j++){
                            JSONArray couponArray = nonStoreProduct.getJSONObject(j).getJSONArray("Coupons");
                            for (int k=0; k<couponArray.length(); k++){
                                if (!couponArray.get(k).toString().toLowerCase().equals("null") && !couponArray.get(k).equals(""))
                                    if (coupons.size() > 0){
                                        if (!coupons.toString().contains("i-Kupon : " + couponArray.get(k).toString())){
                                            coupons.add("i-Kupon : " + couponArray.get(k).toString());
                                        }
                                    }else{
                                        coupons.add("i-Kupon : " + couponArray.get(k).toString());
                                    }
                            }
                        }

                        for(int j=0; j<nonStoreProduct.length(); j++){
//                            totalDiscount += nonStoreProduct.getJSONObject(j).getDouble("Discount");
                            totalCoupon += nonStoreProduct.getJSONObject(j).getDouble("KuponNominal");
                        }
                    } else if(shoppingCartItems.getJSONObject(i).getString("RootCategoryName").toLowerCase().equals("grocery")){
                        if (shoppingCartItems.getJSONObject(i).getString("VBKCode") != null && !shoppingCartItems.getJSONObject(i).getString("VBKCode").equals("")
                                && !shoppingCartItems.getJSONObject(i).getString("VBKCode").contains("null")){
                            if (coupons.size() > 0){
                                if (!coupons.toString().contains("Voucher : " + shoppingCartItems.getJSONObject(i).getString("VBKCode"))){
                                    coupons.add("Voucher : " + shoppingCartItems.getJSONObject(i).getString("VBKCode"));
                                }
                            }else{
                                coupons.add("Voucher : " + shoppingCartItems.getJSONObject(i).getString("VBKCode"));
                            }
                        }

                        JSONArray storeProduct = new JSONArray();
                        for(int j=0; j<shoppingCartItems.getJSONObject(i).getJSONArray("ShoppingCartItems").length(); j++){
                            storeProduct.put(shoppingCartItems.getJSONObject(i).getJSONArray("ShoppingCartItems").getJSONObject(j));
//                            totalDiscount += storeProduct.getJSONObject(j).getDouble("Discount");
                            totalCoupon += storeProduct.getJSONObject(j).getDouble("KuponNominal");
                        }

                        for (int j=0; j<storeProduct.length(); j++){
                            JSONArray couponArray = storeProduct.getJSONObject(j).getJSONArray("Coupons");
                            for (int k=0; k<couponArray.length(); k++){
                                if (!couponArray.get(k).toString().toLowerCase().equals("null") && !couponArray.get(k).equals(""))
                                    if (coupons.size() > 0){
                                        if (!coupons.toString().contains("i-Kupon : " + couponArray.get(k).toString())){
                                            coupons.add("i-Kupon : " + couponArray.get(k).toString());
                                        }
                                    }else{
                                        coupons.add("i-Kupon : " + couponArray.get(k).toString());
                                    }
                            }
                        }
                    }
                }

                if(totalCoupon > 0.0 || voucher > 0.0){
                    couponList.setVisibility(View.VISIBLE);
                    totalVoucher.setText("Rp " + df.format(totalCoupon + voucher).replace(",","."));
                    couponAdapter = new CouponAdapter(ShippingActivity.this, coupons, "shipping");
                    couponList.setAdapter(couponAdapter);
                    sessionManager.setKeyTotalCoupon(Integer.valueOf(String.valueOf(totalCoupon).split("\\.")[0]));
                    sessionManager.setKeyCouponList(coupons.toString());
                }else{
                    couponList.setVisibility(View.GONE);
                }
            }
        } catch (Exception e){
            e.printStackTrace();
        }

        stopLoader();
    }

    public void getKeyShipping(){
        if (sessionManager.getKeyShipping()) {
            if (sessionManager.getKeyStore() == null) {
                if (sessionManager.getKeyDate().length() > 0) date = sessionManager.getKeyDate();
                if (sessionManager.getKeyTime().length() > 0) time = sessionManager.getKeyTime();
                if (sessionManager.getKeyExpiredDate().length() > 0) expiredDate = sessionManager.getKeyExpiredDate();

                try {
                    if (addressSelected != null && addressSelected.length() > 0){
                        defaultAddressStoreObject = new JSONObject(addressSelected);
                    }else{
                        defaultAddressStoreObject = new JSONObject(sqLiteHandler.getDefaultAddress());
                    }

                    receiverName.setText(": " + defaultAddressStoreObject.getString("ReceiverName"));

                    receiverAddress1.setText(defaultAddressStoreObject.getString("Street"));
                    receiverCity.setText(defaultAddressStoreObject.getString("RegionName"));
                    receiverZipCode.setText(defaultAddressStoreObject.getString("ZipCode"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                if (date.length() > 0 && time.length() > 0){
                    dateTimeSending.setText(date + " | " + time);
                }else{
                    dateTimeSending.setText("");
                }

                if (storeShippingCost == 0.0){
                    shippingCostStore.setText("(Gratis)");
                }else{
                    shippingCostStore.setText("Rp " + df.format(storeShippingCost).replace(",", "."));
                }

                if (isIPPCover.equals("true")){
//                    totalOngkir.setText("Rp " + df.format(storeShippingCost).replace(",","."));
                }else{
//                    totalOngkir.setText("Rp " + df.format(plazaShippingCost + storeShippingCost).replace(",","."));
                }

                pickOrder.setBackgroundResource(R.color.backgroundGrey);
                pickOrderText.setTextColor(Color.parseColor("#000000"));
                underLinePick.setBackgroundResource(R.color.backgroundGrey);
                sendOrder.setBackgroundResource(R.color.colorWhite);
                sendOrderText.setTextColor(Color.parseColor("#0079C2"));
                underLineSend.setBackgroundResource(R.color.colorPrimary);
                sendAddressContainer.setVisibility(View.VISIBLE);
                pickingStoreContainer.setVisibility(View.GONE);
            } else {
                if (sessionManager.getKeyDate().length() > 0) date = sessionManager.getKeyDate();
                if (sessionManager.getKeyTime().length() > 0) time = sessionManager.getKeyTime();
                if (sessionManager.getKeyExpiredDate().length() > 0) expiredDate = sessionManager.getKeyExpiredDate();
                store = sessionManager.getKeyStore();

                try {
                    storeObject = new JSONObject(store);

                    if (storeObject.toString().contains("Zipcode")){
                        storeObject.put("ZipCode", storeObject.getString("Zipcode"));
                    } else if (storeObject.toString().contains("ZipCode")){
                        storeObject.put("Zipcode", storeObject.getString("ZipCode"));
                    }

                    storeName.setText(storeObject.getString("store") + "(" + storeObject.getString("ID") + ")");
                    storeAddress.setText(storeObject.getString("address"));
                    storeZipcode.setText(storeObject.getString("Zipcode"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                if (date.length() > 0 && time.length() > 0){
                    dateTimePicking.setText(date + " | " + time);
                }else{
                    dateTimePicking.setText("");
                }

                shippingCostStore.setText("(Gratis)");

//                if (isIPPCover.equals("false")) totalOngkir.setText("Rp " + df.format(plazaShippingCost).replace(",","."));

                sendOrder.setBackgroundResource(R.color.backgroundGrey);
                sendOrderText.setTextColor(Color.parseColor("#000000"));
                underLineSend.setBackgroundResource(R.color.backgroundGrey);
                pickOrder.setBackgroundResource(R.color.colorWhite);
                pickOrderText.setTextColor(Color.parseColor("#0079C2"));
                underLinePick.setBackgroundResource(R.color.colorPrimary);
                sendAddressContainer.setVisibility(View.GONE);
                pickingStoreContainer.setVisibility(View.VISIBLE);
            }
        }

        if(sessionManager.getKeyPlazaShipping()){
            if(sessionManager.getKeyStorePlaza() == null){
                isDeliveryOrder = true;
                isIPPCover = sessionManager.getKeyIppCover();
                mCustomerAddressId = sessionManager.getKeyAddressIdPlaza();
                try {
                    relPesananDiantar.setBackgroundResource(R.color.colorWhite);
                    relPesananDiantarText.setTextColor(Color.parseColor("#0079C2"));
                    underLineSendPlaza.setBackgroundResource(R.color.colorPrimary);

                    relPesananDiambil.setBackgroundResource(R.color.backgroundGrey);
                    relPesananDiambilText.setTextColor(Color.parseColor("#000000"));
                    underLinePickPlaza.setBackgroundResource(R.color.backgroundGrey);
                    pickStoreContainerIpp.setVisibility(View.GONE);
                    sendAddressContainerIpp.setVisibility(View.VISIBLE);

                    if (defaultAddressObject == null && defaultAddressObject.length() == 0){
                        defaultAddressObject = new JSONObject(sqLiteHandler.getDefaultAddress());
                    }

                    receiverNameIpp.setText(defaultAddressObject.getString("ReceiverName"));
                    StringBuilder addressBuilder = new StringBuilder();

                    if((defaultAddressObject.getString("Street2") == null || defaultAddressObject.getString("Street2").equals("null") || defaultAddressObject.getString("Street2").equals(""))
                            && (defaultAddressObject.getString("Street3") == null || defaultAddressObject.getString("Street3").equals("null") || defaultAddressObject.getString("Street3").equals(""))){
                        addressBuilder.append(defaultAddressObject.getString("Street")).append("\n");
                    } else if(defaultAddressObject.getString("Street2") == null || defaultAddressObject.getString("Street2").equals("null") || defaultAddressObject.getString("Street2").equals("")){
                        addressBuilder.append(defaultAddressObject.getString("Street")).append("\n")
                                .append(defaultAddressObject.getString("Street3")).append("\n");
                    } else if(defaultAddressObject.getString("Street3") == null || defaultAddressObject.getString("Street3").equals("null") || defaultAddressObject.getString("Street3").equals("")){
                        addressBuilder.append(defaultAddressObject.getString("Street")).append("\n")
                                .append(defaultAddressObject.getString("Street2")).append("\n");
                    } else {
                        addressBuilder.append(defaultAddressObject.getString("Street")).append("\n").append(defaultAddressObject.getString("Street2")).append("\n")
                                .append(defaultAddressObject.getString("Street3")).append("\n");
                    }

                    addressBuilder.append(defaultAddressObject.getString("District")).append(", ")
                            .append(defaultAddressObject.getString("CityLabel")).append("\n")
                            .append(defaultAddressObject.getString("RegionName")).append(", ")
                            .append(defaultAddressObject.getString("ProvinceName")).append(", ")
                            .append(defaultAddressObject.getString("ZipCode"));

                    receiverAddressIpp.setText(addressBuilder);

                    if (isIPPCover.equals("true") && isIPP){
                        imagePlaza.setBackgroundResource(R.drawable.logoindopaket);
                        deliveryName.setText("Indopaket");
                        if (plazaShippingCost == 0.0){
                            gratisText.setText("(Gratis)");
                        }else{
                            gratisText.setText("Rp " + df.format(plazaShippingCost).replace(",","."));
                        }
                    }else{
                        imagePlaza.setBackgroundResource(R.drawable.icon_jne);
                        deliveryName.setText("JNE");
                        if (plazaShippingCost == 0.0){
                            gratisText.setText("(Gratis)");
                        }else{
                            gratisText.setText("Rp " + df.format(plazaShippingCost).replace(",","."));
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }else {
                isDeliveryOrder = false;
                storeCode = sessionManager.getKeyStoreCodePlaza();

                relPesananDiambil.setBackgroundResource(R.drawable.rounded_blue_background);
                relPesananDiambilText.setTextColor(Color.parseColor("#0079C2"));
                relPesananDiantar.setBackgroundResource(R.drawable.rounded_full_grey_background);
                relPesananDiantarText.setTextColor(Color.parseColor("#000000"));
                pickStoreContainerIpp.setVisibility(View.VISIBLE);
                sendAddressContainerIpp.setVisibility(View.GONE);

                try {
                    JSONObject storeObject = new JSONObject(sessionManager.getKeyStorePlaza());
                    if (storeObject.toString().contains("StoreName")){
                        storeNameIpp.setText(storeObject.getString("StoreName") + "(" + storeObject.getString("StoreCode") + ")");
                        storeAddressIpp.setText(storeObject.getString("Address"));
                    }else{
                        storeNameIpp.setText(storeObject.getString("store") + "(" + storeObject.getString("ID") + ")");
                        storeAddressIpp.setText(storeObject.getString("address"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                imagePlaza.setBackgroundResource(R.drawable.logoindopaket);
                deliveryName.setText("Indopaket");
                gratisText.setText("(Gratis)");
                estimasiTime.setText("Estimasi pengiriman : " +minShipping + " - " + maxShipping + " hari kerja");
            }
        }
    }

    public void cartResponse(JSONArray response){
        storeProductTotal = 0.0;
        nonstoreProductTotal = 0.0;
        paaiProductTotal = 0.0;
        ippProductTotal = 0.0;
        virtualProductTotal = 0.0;

        storeProductObject = new JSONArray();
        nonstoreProductObject = new JSONArray();
        virtualProductObject = new ArrayList<>();

        try {
            if (sessionManager.getKeyLastStore().equals("")){
                lastStoreObject = response.getJSONObject(0).getJSONObject("LastIStore");
            }else{
                lastStoreObject = new JSONObject(sessionManager.getKeyLastStore());
            }

            specialDatePAAIs = response.getJSONObject(0).getJSONObject("ShoppingCart").getJSONArray("DaysOffSpecial");
            normalDatePAAIs = response.getJSONObject(0).getJSONObject("ShoppingCart").getJSONArray("DaysOffNormal");
            leadTime = response.getJSONObject(0).getJSONObject("ShoppingCart").getInt("Leadtime");

            shoppingCartItems = response.getJSONObject(0).getJSONObject("ShoppingCart").getJSONArray("ShoppingCartItems");
            for (int i=0; i<shoppingCartItems.length(); i++){
                if(shoppingCartItems.getJSONObject(i).getBoolean("IsVirtual")){
                    virtualProductObject.add(shoppingCartItems.getJSONObject(i));
                    virtualProductTotal = virtualProductTotal + shoppingCartItems.getJSONObject(i).getDouble("SubTotal");
                } else if(shoppingCartItems.getJSONObject(i).getJSONObject("Product").getString("ProductFlag").toLowerCase().equals("plaza")){
                    nonstoreProductObject.put(shoppingCartItems.getJSONObject(i));

                    if (shoppingCartItems.getJSONObject(i).getJSONObject("Product").getBoolean("IsPAAI") && shoppingCartItems.getJSONObject(i).getBoolean("IsUseNote")) {
                        isPaai = true;
                        paaiProductObject.put(shoppingCartItems.getJSONObject(i));
                        paaiProductTotal = paaiProductTotal + shoppingCartItems.getJSONObject(i).getDouble("SubTotal");
                    }else{
                        nonPaaiProductObject.put(shoppingCartItems.getJSONObject(i));
                        nonstoreProductTotal = nonstoreProductTotal + shoppingCartItems.getJSONObject(i).getDouble("SubTotal");
                    }
                } else if(shoppingCartItems.getJSONObject(i).getJSONObject("Product").getString("ProductFlag").toLowerCase().equals("store")){
                    if (!shoppingCartItems.getJSONObject(i).getJSONObject("Product").getString("Name").toLowerCase().equals("jasa kirim")){
                        storeProductObject.put(shoppingCartItems.getJSONObject(i));
                        storeProductTotal = storeProductTotal + shoppingCartItems.getJSONObject(i).getDouble("SubTotal");
                    }
                }
            }

            if (sessionManager.getKeyLastStorePlaza().equals("")){
                lastStorePlazaObject = response.getJSONObject(0).getJSONObject("LastStoreIPP");
            }else {
                lastStorePlazaObject = new JSONObject(sessionManager.getKeyLastStorePlaza());
            }

            if (response.getJSONObject(0).toString().contains("IsIPP")){
                isIPP = response.getJSONObject(0).getBoolean("IsIPP");
                if (isIPP) {
                    if (ippCityList == null || ippCityList.length() <= 0) {
                        ippCityList = new JSONArray();

                        if (response.getJSONObject(0).getJSONArray("ListIPPCities") != null &&
                                response.getJSONObject(0).getJSONArray("ListIPPCities").length() > 0) {
                            ippCityList = response.getJSONObject(0).getJSONArray("ListIPPCities");
                        }
                    }
                }
            }

            if (response.getJSONObject(0).isNull("CustomerAddress")){
                // do nothing
            }else{
                if (response.getJSONObject(0).getJSONObject("CustomerAddress").getString("ID") != null &&
                        !TextUtils.isEmpty(response.getJSONObject(0).getJSONObject("CustomerAddress").getString("ID"))) {
                    mCustomerAddressId = response.getJSONObject(0).getJSONObject("CustomerAddress").getString("ID");
                }
            }
        } catch (Exception e){
            e.printStackTrace();
        }

        if(storeProductObject.length() > 0){
            linearBtnStore.setVisibility(View.VISIBLE);
            storeProductContainer.setVisibility(View.VISIBLE);
            totalPriceStore.setText("Rp " + df.format(storeProductTotal).replace(",","."));

            int countProduct = 0;
            for (int i=0; i<storeProductObject.length(); i++){
                try {
                    countProduct += storeProductObject.getJSONObject(i).getInt("Quantity");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            btnStore.setText("Produk Toko (" + countProduct + " items)");
        } else {
            linearBtnStore.setVisibility(View.GONE);
            storeProductContainer.setVisibility(View.GONE);
        }

        if(nonPaaiProductObject.length() > 0){
            linearBtnNonstore.setVisibility(View.VISIBLE);
            linIpp.setVisibility(View.VISIBLE);

            if (isIPP){
                int countProduct = 0;
                for (int i=0; i<nonPaaiProductObject.length(); i++){
                    try {
                        countProduct += nonPaaiProductObject.getJSONObject(i).getInt("Quantity");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                btnNonStore.setText("Pesanan Non Toko (" + countProduct + " items)");
                tvTotalPriceIPP.setText("Rp " + df.format(ippProductTotal).replace(",", "."));

                if (isIPPCover.equals("") || isIPPCover.equals("true") || !isDeliveryOrder){
                    imagePlaza.setBackgroundResource(R.drawable.logoindopaket);
                    deliveryName.setText("Indopaket");
                    if (plazaShippingCost == 0.0){
                        gratisText.setText("(Gratis)");
                    }else{
                        gratisText.setText("Rp " + df.format(plazaShippingCost).replace(",","."));
                    }
                    estimasiTime.setText("Estimasi pengiriman : " +minShipping + " - " + maxShipping + " hari kerja");
                }else{
                    try {
                        plazaShippingCost = response.getJSONObject(0).getDouble("OngkosKirimPlaza");
                        minShipping = response.getJSONObject(0).getString("PlazaMinETD");
                        maxShipping = response.getJSONObject(0).getString("PlazaMaxETD");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    imagePlaza.setBackgroundResource(R.drawable.icon_jne);
                    deliveryName.setText("JNE");
                    if (plazaShippingCost == 0.0){
                        gratisText.setText("(Gratis)");
                    }else{
                        gratisText.setText("Rp " + df.format(plazaShippingCost).replace(",","."));
                    }

                    estimasiTime.setText("Estimasi pengiriman : " +minShipping + " - " + maxShipping + " hari kerja");
                }
            }else{
                int countProduct = 0;
                for (int i=0; i<nonstoreProductObject.length(); i++){
                    try {
                        countProduct += nonstoreProductObject.getJSONObject(i).getInt("Quantity");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                btnNonStore.setText("Pesanan Non Toko (" + countProduct + " items)");

                imagePlaza.setBackgroundResource(R.drawable.icon_jne);
                deliveryName.setText("JNE");
                gratisText.setText("Rp " + df.format(plazaShippingCost).replace(",","."));
                estimasiTime.setText("Estimasi pengiriman : " +minShipping + " - " + maxShipping + " hari kerja");
                tvTotalPriceIPP.setText("Rp " + df.format(nonstoreProductTotal).replace(",","."));
            }
        } else {
            linearBtnNonstore.setVisibility(View.GONE);
            linIpp.setVisibility(View.GONE);
        }

        if(paaiProductObject.length() > 0){
            jsonArrayRequest(API.getInstance().getApiGetInitialAddress()+"?custId="+sessionManager.getUserID()+"&mfp_id="+sessionManager.getKeyMfpId(), "address");
            linearPaai.setVisibility(View.VISIBLE);

            int countProduct = 0;
            for (int i=0; i<paaiProductObject.length(); i++){
                try {
                    countProduct += paaiProductObject.getJSONObject(i).getInt("Quantity");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            btnPAAI.setText("Produk Florist/Ice cake/Cake (" + countProduct + " items)");
            totalPricePAAI.setText("Rp " + df.format(paaiProductTotal).replace(",","."));
        }else{
            linearPaai.setVisibility(View.GONE);
        }

        if(virtualProductObject.size() > 0){
            virtualProductContainer.setVisibility(View.VISIBLE);
            totalPriceVirtual.setText("Rp " + df.format(virtualProductTotal).replace(",","."));
        } else {
            virtualProductContainer.setVisibility(View.GONE);
        }

        if(storeProductObject.length() > 0 && nonstoreProductObject.length() > 0 && isIPPCover.equals("false")){
            shippingCost = plazaShippingCost + storeShippingCost;
        } else if(nonstoreProductObject.length() > 0 && isIPPCover.equals("false")){
            shippingCost = plazaShippingCost;
        } else if(storeProductObject.length() > 0 ){
            shippingCost = storeShippingCost;
        }

        totalPrice = Double.valueOf(sessionManager.getKeyTotalPrice());
        totalDiscount = Double.valueOf(sessionManager.getKeyTotalDiscount());
        voucher = Double.valueOf(sessionManager.getKeyTotalVoucher());
        coupon = Double.valueOf(sessionManager.getKeyTotalCoupon());
//        shippingCost = Double.valueOf(sessionManager.getKeyTotalShippingCost());

        totalTransaction.setText("Rp " + df.format(totalPrice - totalDiscount + shippingCost - voucher).replace(",","."));
        totalDiscountText.setText("Rp " + df.format(totalDiscount).replace(",","."));
        totalShippingCost.setText("Rp " + df.format(shippingCost).replace(",","."));
        totalVoucher.setText("Rp " + df.format(coupon + voucher).replace(",","."));
        stopLoader();

        if (!isDeliveryOrder){
            try {
                if (lastStorePlazaObject.getString("StoreCode") == null || lastStorePlazaObject.getString("StoreCode").toLowerCase().equals("null")
                        || lastStorePlazaObject.getString("StoreCode").toLowerCase().equals("")){
                    pickAddressPlaza.setVisibility(View.GONE);
                    pickAddressPlazaText.setVisibility(View.VISIBLE);
                }else {
                    pickAddressPlaza.setVisibility(View.VISIBLE);
                    pickAddressPlazaText.setVisibility(View.GONE);

                    storeCode = lastStorePlazaObject.getString("StoreCode");
                    sessionManager.setKeyStoreCodePlaza(storeCode);
                    pickStoreContainerIpp.setVisibility(View.VISIBLE);
                    sendAddressContainerIpp.setVisibility(View.GONE);

                    try {
                        JSONObject storeObject = new JSONObject(lastStorePlazaObject.toString());
                        sessionManager.setKeyStorePlaza(storeObject.toString());
                        storeNameIpp.setText(storeObject.getString("StoreName") + "(" + storeObject.getString("StoreCode") + ")");
                        storeAddressIpp.setText(storeObject.getString("Address"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        defaultStore();
    }

    public void setDefaultAddress(String defaultAddress){
        try {
            defaultAddressStoreObject = new JSONObject(defaultAddress);
            receiverName.setText(": " + defaultAddressStoreObject.getString("ReceiverName"));

            receiverAddress1.setText(defaultAddressStoreObject.getString("Street"));
            receiverCity.setText(defaultAddressStoreObject.getString("RegionName"));
            receiverZipCode.setText(defaultAddressStoreObject.getString("ZipCode"));

            JSONObject object = new JSONObject();
            try {
                object.put("IsDelivery", isSend);
                object.put("ShoppingCartID", sessionManager.getCartId());
                if (defaultAddressStoreObject.getString("ID").length() > 0){
                    object.put("CustomerAddressID", defaultAddressStoreObject.getString("ID"));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            jsonPost(API.getInstance().getApiStoreZoneSlot()+"?mfp_id="+sessionManager.getKeyMfpId(), object, "shippingcost");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void setDefaultAddressPlaza(String defaultAddress){
        try {
            defaultAddressObject = new JSONObject(defaultAddress);
            sendAddressContainerIpp.setVisibility(View.VISIBLE);
            pickStoreContainerIpp.setVisibility(View.GONE);

            receiverNameIpp.setText(defaultAddressObject.getString("ReceiverName"));
            StringBuilder addressBuilder = new StringBuilder();

            if((defaultAddressObject.getString("Street2") == null || defaultAddressObject.getString("Street2").equals("null") || defaultAddressObject.getString("Street2").equals(""))
                    && (defaultAddressObject.getString("Street3") == null || defaultAddressObject.getString("Street3").equals("null") || defaultAddressObject.getString("Street3").equals(""))){
                addressBuilder.append(defaultAddressObject.getString("Street")).append("\n");
            } else if(defaultAddressObject.getString("Street2") == null || defaultAddressObject.getString("Street2").equals("null") || defaultAddressObject.getString("Street2").equals("")){
                addressBuilder.append(defaultAddressObject.getString("Street")).append("\n")
                        .append(defaultAddressObject.getString("Street3")).append("\n");
            } else if(defaultAddressObject.getString("Street3") == null || defaultAddressObject.getString("Street3").equals("null") || defaultAddressObject.getString("Street3").equals("")){
                addressBuilder.append(defaultAddressObject.getString("Street")).append("\n")
                        .append(defaultAddressObject.getString("Street2")).append("\n");
            } else {
                addressBuilder.append(defaultAddressObject.getString("Street")).append("\n")
                        .append(defaultAddressObject.getString("Street2")).append("\n")
                        .append(defaultAddressObject.getString("Street3")).append("\n");
            }

            addressBuilder.append(defaultAddressObject.getString("District")).append(", ")
                    .append(defaultAddressObject.getString("CityLabel")).append("\n")
                    .append(defaultAddressObject.getString("RegionName")).append(", ")
                    .append(defaultAddressObject.getString("ProvinceName")).append(", ")
                    .append(defaultAddressObject.getString("ZipCode"));

            receiverAddressIpp.setText(addressBuilder);

            if (plazaShippingCost == 0.0){
                gratisText.setText("(Gratis)");
            }else{
                gratisText.setText("Rp " + df.format(plazaShippingCost).replace(",", "."));
            }

            jsonArrayRequest(API.getInstance().getApiShippingMethod()+"/"+sessionManager.getCartId()
                    + "?customerId=" + sessionManager.getUserID()
                    + "&CustomerAddressID="+mCustomerAddressId
                    + "&IsIPP="+isIPP
                    + "&IPPServiceType=3"
                    + "&isVIrtual=false&isParcelView=false&regionID="+sessionManager.getRegionID() + "&mfp_id="
                    + sessionManager.getKeyMfpId(), "shipping");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

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
                                                voucherText.setText("");
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
                                case "shippingcost":
                                    totalPrice = Double.valueOf(sessionManager.getKeyTotalPrice());
                                    totalDiscount = Double.valueOf(sessionManager.getKeyTotalDiscount());
                                    voucher = Double.valueOf(sessionManager.getKeyTotalVoucher());
                                    coupon = Double.valueOf(sessionManager.getKeyTotalCoupon());
                                    sessionManager.setKeyTotalShippingCost(response.getJSONObject("ResponseObject").getInt("CostDelivery"));

                                    linearStoreShippingCost.setVisibility(View.VISIBLE);
                                    if (response.getJSONObject("ResponseObject").getInt("CostDelivery") == 0){
                                        shippingCostStore.setText("(Gratis)");
                                    }else{
                                        shippingCostStore.setText("Rp " + df.format(response.getJSONObject("ResponseObject").getInt("CostDelivery")).replace(",", "."));
                                    }

                                    totalTransaction.setText("Rp " + df.format(totalPrice - totalDiscount + response.getJSONObject("ResponseObject").getDouble("CostDelivery") - voucher).replace(",","."));
                                    totalDiscountText.setText("Rp " + df.format(totalDiscount).replace(",","."));
                                    totalShippingCost.setText("Rp " + df.format(response.getJSONObject("ResponseObject").getDouble("CostDelivery")).replace(",","."));
                                    totalVoucher.setText("Rp " + df.format(coupon + voucher).replace(",","."));

                                    stopLoader();
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
                Toast.makeText(ShippingActivity.this, "Gagal terkoneksi server", Toast.LENGTH_LONG).show();
                stopLoader();
            }
        });

        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(60000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
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

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ShippingActivity.this);
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

    public void runLoader(){
        preloader.setVisibility(View.VISIBLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }

    public void stopLoader(){
        preloader.setVisibility(View.GONE);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if(resultCode == RESULT_OK){
                sendAddress.setVisibility(View.VISIBLE);
                sendAddressText.setVisibility(View.GONE);

                shippingType = "IsDelivery";
                nextToPayment.setText("Lanjut");
                nextToPayment.setBackgroundResource(R.drawable.button_style_1);
                nextToPayment.setEnabled(true);

                pickOrder.setBackgroundResource(R.color.backgroundGrey);
                pickOrderText.setTextColor(Color.parseColor("#000000"));
                underLinePick.setBackgroundResource(R.color.backgroundGrey);
                sendOrder.setBackgroundResource(R.color.colorWhite);
                sendOrderText.setTextColor(Color.parseColor("#0079C2"));
                underLineSend.setBackgroundResource(R.color.colorPrimary);

                sessionManager.setkeyStore(null);
                sessionManager.setKeyShipping(true);

                isSend = true;

                addressSelected = data.getStringExtra("address");
                try {
                    defaultAddressStoreObject = new JSONObject(addressSelected);
                    receiverName.setText(": " + defaultAddressStoreObject.getString("ReceiverName"));

                    receiverAddress1.setText(defaultAddressStoreObject.getString("Street"));
                    receiverCity.setText(defaultAddressStoreObject.getString("RegionName"));
                    receiverZipCode.setText(defaultAddressStoreObject.getString("ZipCode"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                if (storeShippingCost == 0.0){
                    shippingCostStore.setText("(Gratis)");
                }else{
                    shippingCostStore.setText("Rp " + df.format(storeShippingCost).replace(",", "."));
                }

                sendAddressContainer.setVisibility(View.VISIBLE);
                pickingStoreContainer.setVisibility(View.GONE);

                totalPrice = Double.valueOf(sessionManager.getKeyTotalPrice());
                totalDiscount = Double.valueOf(sessionManager.getKeyTotalDiscount());
                voucher = Double.valueOf(sessionManager.getKeyTotalVoucher());
                coupon = Double.valueOf(sessionManager.getKeyTotalCoupon());
                sessionManager.setKeyTotalShippingCost(Integer.valueOf(data.getStringExtra("shippingcost").split("\\.")[0]));

                linearStoreShippingCost.setVisibility(View.VISIBLE);
                if (Integer.valueOf(data.getStringExtra("shippingcost").split("\\.")[0]) == 0){
                    shippingCostStore.setText("(Gratis)");
                }else{
                    shippingCostStore.setText("Rp " + df.format(Integer.valueOf(data.getStringExtra("shippingcost").split("\\.")[0])).replace(",", "."));
                }

                totalTransaction.setText("Rp " + df.format(totalPrice - totalDiscount + Double.valueOf(data.getStringExtra("shippingcost").split("\\.")[0]) - voucher).replace(",","."));
                totalDiscountText.setText("Rp " + df.format(totalDiscount).replace(",","."));
                totalShippingCost.setText("Rp " + df.format(Double.valueOf(data.getStringExtra("shippingcost").split("\\.")[0])).replace(",","."));
                totalVoucher.setText("Rp " + df.format(coupon + voucher).replace(",","."));
            }
        } else if(requestCode == 2){
            if(resultCode == RESULT_OK){
                shippingType = "";
                nextToPayment.setText("Lanjut");
                nextToPayment.setBackgroundResource(R.drawable.button_style_1);
                nextToPayment.setEnabled(true);

                pickOrder.setBackgroundResource(R.color.colorWhite);
                pickOrderText.setTextColor(Color.parseColor("#0079C2"));
                underLinePick.setBackgroundResource(R.color.colorPrimary);
                sendOrder.setBackgroundResource(R.color.backgroundGrey);
                sendOrderText.setTextColor(Color.parseColor("#000000"));
                underLineSend.setBackgroundResource(R.color.backgroundGrey);

                store = data.getStringExtra("store");
                sessionManager.setkeyStore(store);
                sessionManager.setKeyShipping(true);

                isSend = false;

                try {
                    storeObject = new JSONObject(store);

                    if (storeObject.toString().contains("Zipcode")){
                        storeObject.put("ZipCode", storeObject.getString("Zipcode"));
                    }else if(storeObject.toString().contains("ZipCode")){
                        storeObject.put("Zipcode", storeObject.getString("ZipCode"));
                    }

                    storeName.setText(storeObject.getString("Name") + "(" + storeObject.getString("Code") + ")");
                    storeAddress.setText(storeObject.getString("Street"));
                    storeZipcode.setText(storeObject.getString("Zipcode"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                shippingCostStore.setText("(Gratis)");

                sendAddressContainer.setVisibility(View.GONE);
                pickingStoreContainer.setVisibility(View.VISIBLE);
            }

            jsonArrayRequest(API.getInstance().getApiShippingMethod()+"?id="+sessionManager.getCartId()
                    +"&customerID="+sessionManager.getUserID()
                    +"&isVirtual=false&isParcelView=false&regionId="+sessionManager.getRegionID()
                    +"&mfp_id="+sessionManager.getKeyMfpId(), "shipping");
        } else if(requestCode == 4){
            if(resultCode == RESULT_OK){
                nextToPayment.setText("Lanjut");
                nextToPayment.setBackgroundResource(R.drawable.button_style_1);
                nextToPayment.setEnabled(true);
                sessionManager.setKeyPlazaShipping(true);

                relPesananDiambil.setBackgroundResource(R.color.colorWhite);
                relPesananDiambilText.setTextColor(Color.parseColor("#0079C2"));
                underLinePickPlaza.setBackgroundResource(R.color.colorPrimary);

                relPesananDiantar.setBackgroundResource(R.color.backgroundGrey);
                relPesananDiantarText.setTextColor(Color.parseColor("#000000"));
                underLineSendPlaza.setBackgroundResource(R.color.backgroundGrey);
                pickStoreContainerIpp.setVisibility(View.VISIBLE);
                sendAddressContainerIpp.setVisibility(View.GONE);

                storeCode = data.getStringExtra("storeCode");
                sessionManager.setKeyStoreCodePlaza(storeCode);

                try {
                    storePlazaObject = new JSONObject(data.getStringExtra("store"));
                    sessionManager.setKeyStorePlaza(storePlazaObject.toString());
                    storeNameIpp.setText(storePlazaObject.getString("store") + "(" + storePlazaObject.getString("ID") + ")");
                    storeAddressIpp.setText(storePlazaObject.getString("address"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                jsonArrayRequest(API.getInstance().getApiShippingMethod()+"/"+sessionManager.getCartId()
                        +"?customerId="+sessionManager.getUserID()
                        +"&IsIPP="+isIPP
                        +"&IPPServiceType=1"
                        +"&IPPStoreCode="+storeCode
                        +"&isVirtual=false&isParcelView=false&regionID="+sessionManager.getRegionID()
                        +"&mfp_id="+sessionManager.getKeyMfpId(), "shipping");
            }
        } else if(requestCode == 5){
            if(resultCode == RESULT_OK){
                nextToPayment.setText("Lanjut");
                nextToPayment.setBackgroundResource(R.drawable.button_style_1);
                nextToPayment.setEnabled(true);
                sessionManager.setKeyPlazaShipping(true);

                relPesananDiantar.setBackgroundResource(R.color.colorWhite);
                relPesananDiantarText.setTextColor(Color.parseColor("#0079C2"));
                underLineSendPlaza.setBackgroundResource(R.color.colorPrimary);

                relPesananDiambil.setBackgroundResource(R.color.backgroundGrey);
                relPesananDiambilText.setTextColor(Color.parseColor("#000000"));
                underLinePickPlaza.setBackgroundResource(R.color.backgroundGrey);
                pickStoreContainerIpp.setVisibility(View.GONE);
                sendAddressContainerIpp.setVisibility(View.VISIBLE);

                infoStore = data.getStringExtra("infoStore");
                addressTextValue = data.getStringExtra("infoStore");
                latitude = data.getStringExtra("latitude");
                longitude = data.getStringExtra("longitude");
                mCustomerAddressId = data.getStringExtra("mCustomerAddressId");
                sessionManager.setKeyIppCover(isIPPCover);
                sessionManager.setKeyAddressIdPlaza(mCustomerAddressId);
                sessionManager.setKeyStorePlaza(null);

                try {
                    defaultAddressObject = new JSONObject(data.getStringExtra("address"));
                    sendAddressContainerIpp.setVisibility(View.VISIBLE);
                    pickStoreContainerIpp.setVisibility(View.GONE);

                    receiverNameIpp.setText(defaultAddressObject.getString("ReceiverName"));
                    StringBuilder addressBuilder = new StringBuilder();

                    if((defaultAddressObject.getString("Street2") == null || defaultAddressObject.getString("Street2").equals("null") || defaultAddressObject.getString("Street2").equals(""))
                            && (defaultAddressObject.getString("Street3") == null || defaultAddressObject.getString("Street3").equals("null") || defaultAddressObject.getString("Street3").equals(""))){
                        addressBuilder.append(defaultAddressObject.getString("Street")).append("\n");
                    } else if(defaultAddressObject.getString("Street2") == null || defaultAddressObject.getString("Street2").equals("null") || defaultAddressObject.getString("Street2").equals("")){
                        addressBuilder.append(defaultAddressObject.getString("Street")).append("\n")
                                .append(defaultAddressObject.getString("Street3")).append("\n");
                    } else if(defaultAddressObject.getString("Street3") == null || defaultAddressObject.getString("Street3").equals("null") || defaultAddressObject.getString("Street3").equals("")){
                        addressBuilder.append(defaultAddressObject.getString("Street")).append("\n")
                                .append(defaultAddressObject.getString("Street2")).append("\n");
                    } else {
                        addressBuilder.append(defaultAddressObject.getString("Street")).append("\n")
                                .append(defaultAddressObject.getString("Street2")).append("\n")
                                .append(defaultAddressObject.getString("Street3")).append("\n");
                    }

                    addressBuilder.append(defaultAddressObject.getString("District")).append(", ")
                            .append(defaultAddressObject.getString("CityLabel")).append("\n")
                            .append(defaultAddressObject.getString("RegionName")).append(", ")
                            .append(defaultAddressObject.getString("ProvinceName")).append(", ")
                            .append(defaultAddressObject.getString("ZipCode"));

                    receiverAddressIpp.setText(addressBuilder);

                    if (addressList != null && addressList.size() > 0){
                        for (int i=0; i<addressList.size(); i++){
                            if (addressList.get(i).equals(defaultAddressObject.getString("AddressTitle"))){
                                spAddressPAAI.setSelection(i);
                                mCustomerAddressId = listAddressPAAI.getJSONObject(i -1).getString("ID");
                            }
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                jsonArrayRequest(API.getInstance().getApiShippingMethod()+"/"+sessionManager.getCartId()
                        + "?customerId=" + sessionManager.getUserID()
                        + "&CustomerAddressID="+mCustomerAddressId
                        + "&IsIPP="+isIPP
                        +"&IPPServiceType=3"
                        + "&isVIrtual=false&isParcelView=false&regionID="+sessionManager.getRegionID() + "&mfp_id="
                        + sessionManager.getKeyMfpId(), "shipping");
            }
        } else if(requestCode == 6){
            if(resultCode == RESULT_OK){
                date = data.getStringExtra("date");
                time = data.getStringExtra("time");
                expiredDate = data.getStringExtra("expiredDate");
                sessionManager.setkeyDate(date);
                sessionManager.setKeyTime(time);
                sessionManager.setKeyExpiredDate(expiredDate);

                dateTimeSending.setText(date + " | " + time);
            }
        }else if(requestCode == 7){
            if(resultCode == RESULT_OK){
                date = data.getStringExtra("date");
                time = data.getStringExtra("time");
                expiredDate = data.getStringExtra("expiredDate");
                sessionManager.setkeyDate(date);
                sessionManager.setKeyTime(time);
                sessionManager.setKeyExpiredDate(expiredDate);
                sessionManager.setKeyExpiredDate(expiredDate);

                dateTimePicking.setText(date + " | " + time);
            }
        }else if (requestCode == 8){
            isStart = true;
            try {
                setListAddres(new JSONArray(sqLiteHandler.getProfile()).getJSONObject(0).getJSONArray("Address"));

                if (isDeliveryOrder) setDefaultAddressPlaza(sqLiteHandler.getDefaultAddress());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        totalPrice = Double.valueOf(sessionManager.getKeyTotalPrice());
        totalDiscount = Double.valueOf(sessionManager.getKeyTotalDiscount());
        voucher = Double.valueOf(sessionManager.getKeyTotalVoucher());
        coupon = Double.valueOf(sessionManager.getKeyTotalCoupon());
        shippingCost = Double.valueOf(sessionManager.getKeyTotalShippingCost());

        totalTransaction.setText("Rp " + df.format(totalPrice - totalDiscount + shippingCost - voucher).replace(",","."));
        totalDiscountText.setText("Rp " + df.format(totalDiscount).replace(",","."));
        totalShippingCost.setText("Rp " + df.format(shippingCost).replace(",","."));
        totalVoucher.setText("Rp " + df.format(coupon + voucher).replace(",","."));

        if (sessionManager.getKeyCouponList().length() > 0){
            couponList.setVisibility(View.VISIBLE);
            listCoupon = new ArrayList<>(Arrays.asList(sessionManager.getKeyCouponList().split(",")));
            couponAdapter = new CouponAdapter(ShippingActivity.this, listCoupon, "shipping");
            couponList.setAdapter(couponAdapter);
        }else{
            couponList.setVisibility(View.GONE);
        }
    }
}
