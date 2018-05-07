package com.indomaret.klikindomaret.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.content.IntentCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.android.gms.analytics.ecommerce.Product;
import com.google.android.gms.analytics.ecommerce.ProductAction;
import com.indomaret.klikindomaret.API;
import com.indomaret.klikindomaret.MainActivity;
import com.indomaret.klikindomaret.R;
import com.indomaret.klikindomaret.adapter.SummaryProductAdapter;
import com.indomaret.klikindomaret.adapter.TrackingHistoryAdapter;
import com.indomaret.klikindomaret.app.AppController;
import com.indomaret.klikindomaret.helper.Month;
import com.indomaret.klikindomaret.helper.SessionManager;
import com.indomaret.klikindomaret.helper.UncaughtExceptionHandler;
import com.indomaret.klikindomaret.views.HeightAdjustableListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import pl.droidsonroids.gif.GifImageView;

public class OrderSummaryActivity extends AppCompatActivity {
    private Toolbar toolbar;
    SessionManager sessionManager;
    SummaryProductAdapter summaryProductAdapter;
    Month month = new Month();

    Intent intent;
    String salesOrderid, shippingDate, shippingTime, summaryFrom, transactionCode;
    JSONObject orderSummary;
    Boolean isDelivery;
    String storeOrderNumber, plazaOrderNumber, virtualOrderNumber, combinedOrderNumber;
    String addressFull;
    String sendDatePaai;
    String minDate = "";
    String maxDate = "";
    String[] waktuPengiriman;

    DecimalFormat df = new DecimalFormat("#,###");

    ScrollView summaryContainer;
    RelativeLayout preloader;
    Button reorder;
    TextView summaryOrderNumber, summarytTransactionCode, summaryTotalTransaction, summaryShippingCost, summaryTotalPrice,
            summarySavingCost, summaryStatusPayment, labelTransactionCode;
    TextView summarySavingCostInfo, topNotif, topDate, topPayment;
    TextView datePaai, sendText, voucherInfo, voucherNominal;

    TextView summaryFirstNameText, summaryFirstName, summaryLastName, summaryEmail, summaryPhone, infoDeliver;
    TextView summaryReceiverName, summaryReceiverPhone, summaryReceiverAddress, summaryReceiverNameNonToko, summaryReceiverPhoneNonToko, summaryReceiverAddressNonToko;
    HeightAdjustableListView storeProductList, nonstoreProductList, nonstoreProductPaaiList, virtualProductList;
    LinearLayout storeContainer, nonStoreContainer, linearLayoutPaai, linearLayoutNonPaai, linearLayoutStoreShipping, virtualContainer,
            transactionCodeContainer, paymentMethodContainer, topInfoContainer, linearSummaryLastName, orderNumberContainer, linVoucherInfo;
    TextView paymentMethod, nonstoreShipping, nonstorePaaiShipping, storeDeliver, storeDeliverAddress, storeAddress, summaryDateNonStore,
            soTokoText, soNonTokoText, soVirtualText, storeDateTimeShipping, timeDeliver, storeShipping;
    TextView OrderNumberTop, totalTransactionTop, totalShippingTop, totalTop, savingCostTop;
    LinearLayout savingCostTopInfo, linearIdentitas;
    private Tracker mTracker;

    private RelativeLayout relIpp;
    private LinearLayout linNotIPP;
    private HeightAdjustableListView ippList, trackingList;
    private boolean mIsDeliveryIPP = true;
    private boolean mIsIPP = false;
    private String pinIndoPaket, ippStoreName, ippStoreStreet;
    private TextView tvFieldPenerima,tvFieldPhoneNo,tvFieldKeterangan,titikPenerima,
            tvValuePenerima,tvValuePhoneNo,tvValueAlamat,titikKeterangan,tvValueKeterangan, tvFieldStatus;
    private TextView tvFieldLogistikName,titikLogistikName,tvValueLogistikNo,
            tvValueDeliveryTime,tvValueDeliveryCost, tvHeaderInfo;
    private JSONArray mHistoryList = new JSONArray();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_summary);

        UncaughtExceptionHandler uncaughtExceptionHandler = new UncaughtExceptionHandler();
        uncaughtExceptionHandler.uncaughtException(this);

        AppController application = (AppController) getApplication();
        mTracker = application.getDefaultTracker();

        mTracker.setScreenName("Order Summary Page");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());

        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(OrderSummaryActivity.this);

        toolbar = (Toolbar) findViewById(R.id.app_toolbar);
        TextView mTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
        toolbar.setTitle("");
        mTitle.setText("Detail Pesanan");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        sessionManager = new SessionManager(OrderSummaryActivity.this);
        intent = getIntent();

        summaryFrom = intent.getStringExtra("from");

        if (summaryFrom.equals("prod")) {
            salesOrderid = intent.getStringExtra("salesOrderId");
        } else {
            salesOrderid = intent.getStringExtra("salesOrderId");
            transactionCode = intent.getStringExtra("transactionCode");
        }

        summaryContainer = (ScrollView) findViewById(R.id.summary_container);
        preloader = (RelativeLayout) findViewById(R.id.preloader);
        reorder = (Button) findViewById(R.id.btn_reorder);

        summaryOrderNumber = (TextView) findViewById(R.id.summary_order_number);
        summarytTransactionCode = (TextView) findViewById(R.id.summary_transaction_code);
        summaryTotalTransaction = (TextView) findViewById(R.id.summary_total_transaction);
        summaryShippingCost = (TextView) findViewById(R.id.summary_shipping_cost);
        summaryTotalPrice = (TextView) findViewById(R.id.summary_total_price);
        summarySavingCost = (TextView) findViewById(R.id.summary_saving_cost);
        summarySavingCostInfo = (TextView) findViewById(R.id.summary_saving_cost_info);
        storeShipping = (TextView) findViewById(R.id.store_shipping_cost);
        storeDeliverAddress = (TextView) findViewById(R.id.store_deliver_address);
        storeAddress = (TextView) findViewById(R.id.store_address);
        summaryDateNonStore = (TextView) findViewById(R.id.summary_date_non_store);
        sendText = (TextView) findViewById(R.id.send_text);

        datePaai = (TextView) findViewById(R.id.date_paai);

        summaryFirstNameText = (TextView) findViewById(R.id.summary_firstname_text);
        summaryFirstName = (TextView) findViewById(R.id.summary_firstname);
        summaryLastName = (TextView) findViewById(R.id.summary_lastname);
        summaryEmail = (TextView) findViewById(R.id.summary_email);
        summaryPhone = (TextView) findViewById(R.id.summary_phone);
        infoDeliver = (TextView) findViewById(R.id.info_deliver);

        summaryReceiverName = (TextView) findViewById(R.id.summary_receiver_name);
        summaryReceiverPhone = (TextView) findViewById(R.id.summary_receiver_phone);
        summaryReceiverAddress = (TextView) findViewById(R.id.summary_receiver_address);
        summaryReceiverNameNonToko = (TextView) findViewById(R.id.summary_receiver_name_nontoko);
        summaryReceiverPhoneNonToko = (TextView) findViewById(R.id.summary_receiver_phone_nontoko);
        summaryReceiverAddressNonToko = (TextView) findViewById(R.id.summary_receiver_address_nontoko);
        summaryStatusPayment = (TextView) findViewById(R.id.summary_status_payment);
        labelTransactionCode = (TextView) findViewById(R.id.label_transaction_code);

        storeProductList = (HeightAdjustableListView) findViewById(R.id.store_product_list);
        nonstoreProductList = (HeightAdjustableListView) findViewById(R.id.nonstore_product_list);
        nonstoreProductPaaiList = (HeightAdjustableListView) findViewById(R.id.nonstore_paai_product_list);
        virtualProductList = (HeightAdjustableListView) findViewById(R.id.virtual_product_list);

        nonStoreContainer = (LinearLayout) findViewById(R.id.non_store_container);
        linearLayoutPaai = (LinearLayout) findViewById(R.id.linearLayout_paai);
        linearLayoutNonPaai = (LinearLayout) findViewById(R.id.linearLayout_non_paai);
        linearLayoutStoreShipping = (LinearLayout) findViewById(R.id.LinearLayout_store_shipping);
        storeContainer = (LinearLayout) findViewById(R.id.store_container);
        virtualContainer = (LinearLayout) findViewById(R.id.virtual_container);
        transactionCodeContainer = (LinearLayout) findViewById(R.id.transaction_code_container);
        paymentMethodContainer = (LinearLayout) findViewById(R.id.payment_method_container);
        topInfoContainer = (LinearLayout) findViewById(R.id.top_information_container);
        linearSummaryLastName = (LinearLayout) findViewById(R.id.linear_summary_lastname);
        orderNumberContainer = (LinearLayout) findViewById(R.id.order_number_container);
        savingCostTopInfo = (LinearLayout) findViewById(R.id.savingCostTopInfo);
        linVoucherInfo = (LinearLayout) findViewById(R.id.linVoucherInfo);
        linearIdentitas = (LinearLayout) findViewById(R.id.linear_identitas);

        paymentMethod = (TextView) findViewById(R.id.payment_method);
        nonstoreShipping = (TextView) findViewById(R.id.nonstore_shipping);
        nonstorePaaiShipping = (TextView) findViewById(R.id.nonstore_paai_shipping);
        storeDeliver = (TextView) findViewById(R.id.store_deliver);
        soTokoText = (TextView) findViewById(R.id.so_toko_text);
        soNonTokoText = (TextView) findViewById(R.id.so_nontoko_text);
        soVirtualText = (TextView) findViewById(R.id.so_virtual_text);
        storeDateTimeShipping = (TextView) findViewById(R.id.date_time_deliver);
        timeDeliver = (TextView) findViewById(R.id.time_deliver);
        voucherInfo = (TextView) findViewById(R.id.voucherInfo);
        voucherNominal = (TextView) findViewById(R.id.voucherNominal);

        topNotif = (TextView) findViewById(R.id.top_notif);
        topPayment = (TextView) findViewById(R.id.top_payment);
        topDate = (TextView) findViewById(R.id.top_date);

        relIpp = (RelativeLayout) findViewById(R.id.relIPP);
        linNotIPP = (LinearLayout) findViewById(R.id.linNotIPP);
        ippList = (HeightAdjustableListView) findViewById(R.id.ippList);
        trackingList = (HeightAdjustableListView) findViewById(R.id.trackingList);
        tvFieldPenerima = (TextView) findViewById(R.id.tvFieldPenerima);
        tvFieldPhoneNo = (TextView) findViewById(R.id.tvFieldPhoneNo);
        tvFieldKeterangan = (TextView) findViewById(R.id.tvFieldKeterangan);
        titikPenerima = (TextView) findViewById(R.id.titikPenerima);
        tvValuePenerima = (TextView) findViewById(R.id.tvValuePenerima);
        tvValuePhoneNo = (TextView) findViewById(R.id.tvValuePhoneNo);
        tvValueAlamat = (TextView) findViewById(R.id.tvValueAlamat);
        titikKeterangan = (TextView) findViewById(R.id.titikKeterangan);
        tvValueKeterangan = (TextView) findViewById(R.id.tvValueKeterangan);
        tvFieldLogistikName = (TextView) findViewById(R.id.tvFieldLogistikName);
        titikLogistikName = (TextView) findViewById(R.id.titikLogistikName);
        tvValueLogistikNo = (TextView) findViewById(R.id.tvValueLogistikNo);
        tvValueDeliveryTime = (TextView) findViewById(R.id.tvValueDeliveryTime);
        tvValueDeliveryCost = (TextView) findViewById(R.id.tvValueDeliveryCost);
        tvHeaderInfo = (TextView) findViewById(R.id.tvHeaderInfo);
        tvFieldStatus = (TextView) findViewById(R.id.tvFieldStatus);

        OrderNumberTop = (TextView) findViewById(R.id.OrderNumberTop);
        totalTransactionTop = (TextView) findViewById(R.id.totalTransactionTop);
        totalShippingTop = (TextView) findViewById(R.id.totalShippingTop);
        totalTop = (TextView) findViewById(R.id.totalTop);
        savingCostTop = (TextView) findViewById(R.id.savingCostTop);

        setDefaultIPP();
//        trackingList.setAdapter(new TrackingHistoryAdapter(this,getDummy()));

        if (summaryFrom.equals("order")) {
            reorder.setVisibility(View.VISIBLE);
        } else {
            reorder.setVisibility(View.GONE);
        }

        if (summaryFrom.equals("prod")) {
            jsonArrayGet(API.getInstance().getApiPayment() + "?SalesOrderHeaderId=" + salesOrderid + "&mfp_id=" + sessionManager.getKeyMfpId(), "sales");
        } else {
            jsonArrayGet(API.getInstance().getApiDetailPayment() + "?TransactionCode=" + transactionCode + "&TypeCode=&RegionID=" + sessionManager.getRegionID() + "&mfp_id=" + sessionManager.getKeyMfpId(), "sales");
        }

        topPayment.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        intent = new Intent(OrderSummaryActivity.this, PaymentListActivity.class);
                        startActivity(intent);
                        overridePendingTransition(R.anim.left_in, R.anim.left_out);
                    }
                }
        );

        reorder.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        jsonArrayGet(API.getInstance().getApiReorder() + "?SalesOrderId=" + salesOrderid + "&RegionId=" + sessionManager.getRegionID() + "&mfp_id=" + sessionManager.getKeyMfpId(), "reorder");
                    }
                }
        );
    }

    public void jsonArrayGet(String urlJsonObj, final String type) {
        runLoader();

        System.out.println("summary = " + urlJsonObj);

        JsonArrayRequest jsonArrayReq = new JsonArrayRequest(urlJsonObj,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        if (response == null || response.length() == 0){
                            stopLoader();
                            Toast.makeText(OrderSummaryActivity.this, "Gagal terkoneksi server", Toast.LENGTH_LONG).show();
                        }else{
                            if (type.equals("sales")) {
                                processOrderSummary(response);
                            } else if (type.equals("store")) {
                                processStore(response);
                            } else if (type.equals("reorder")) {
                                processReorder(response);
                            }
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(OrderSummaryActivity.this, "Gagal terkoneksi server", Toast.LENGTH_LONG).show();
                stopLoader();

            }
        }, this);

        AppController.getInstance().addToRequestQueue(jsonArrayReq);
    }

    public void processReorder(JSONArray response) {
        stopLoader();
        System.out.println("reorder = " + response);

        try {
            sessionManager.setCartId(response.getJSONObject(0).getString("ResponseID"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(OrderSummaryActivity.this);
        alertDialogBuilder.setTitle("KlikIndomaret");

        try {
            alertDialogBuilder.setMessage(response.getJSONObject(0).getString("Message"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        alertDialogBuilder.setNegativeButton("Tutup", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    public void processOrderSummary(JSONArray response) {
        try {
            orderSummary = response.getJSONObject(0);
            System.out.println("orderSummary : " + orderSummary);

            List<JSONObject> storeGoods = new ArrayList<>();
            List<JSONObject> plazaGoods = new ArrayList<>();
            List<JSONObject> plazaGoodsPAAI = new ArrayList<>();
            List<JSONObject> virtualGoods = new ArrayList<>();
            List<JSONObject> plazaGoodsIPP = new ArrayList<>();
            relIpp.setVisibility(View.GONE);

            Double plazaCostWarehouse = 0.0;
            Double plazaCostSupplier = 0.0;
            Double plazaCostBookingDirect = 0.0;
            Double storeShippingCost = 0.0;

            if (!orderSummary.getString("IStore").equals("null")) {
                isDelivery = orderSummary.getJSONObject("IStore").getBoolean("IsDelivery");
                waktuPengiriman = orderSummary.getJSONObject("IStore").getString("WaktuPengiriman").split("T");
                addressFull = orderSummary.getJSONObject("IStore").getString("AddressFull");
                storeOrderNumber = orderSummary.getJSONObject("IStore").getString("SalesOrderNo");
                JSONArray productGoodArray = orderSummary.getJSONObject("IStore").getJSONArray("ItemDetail");

                for (int i = 0; i < productGoodArray.length(); i++) {
                    if (productGoodArray.getJSONObject(i).getString("PLU").equals("10036492")) {
                        System.out.println("Sub Total = " + productGoodArray.getJSONObject(i));
                        storeShippingCost = productGoodArray.getJSONObject(i).getDouble("SubTotal");
                    } else {
                        storeGoods.add(productGoodArray.getJSONObject(i));
                    }
                }
            } else {
                storeOrderNumber = "";
            }

            if (!orderSummary.getString("IPlaza").equals("null")) {
                plazaOrderNumber = orderSummary.getJSONObject("IPlaza").getString("SalesOrderNo");
                plazaCostWarehouse = orderSummary.getJSONObject("IPlaza").getDouble("ShippingCostWarehouse");
                plazaCostSupplier = orderSummary.getJSONObject("IPlaza").getDouble("ShippingCostSupplier");


                if (!orderSummary.getJSONObject("IPlaza").getString("ShippingCostBookingDirect").equals("null")){
                    plazaCostBookingDirect = orderSummary.getJSONObject("IPlaza").getDouble("ShippingCostBookingDirect");
                }else{
                    plazaCostBookingDirect = 0.0;
                }


                if (plazaGoodsPAAI.size() > 0)
                    plazaCostBookingDirect = orderSummary.getJSONObject("IPlaza").getDouble("ShippingCostBookingDirect");

                JSONArray productGoodArray = orderSummary.getJSONObject("IPlaza").getJSONArray("ItemDetail");
                minDate = orderSummary.getJSONObject("IPlaza").getString("PlazaMinETD");
                maxDate = orderSummary.getJSONObject("IPlaza").getString("PlazaMaxETD");

                //ipp
                mIsIPP = false;
                if (orderSummary.getJSONObject("IPlaza").getBoolean("IsIPP")) {
                    mIsIPP = true;
                    pinIndoPaket = ippStoreName = ippStoreStreet = "";

                    mIsDeliveryIPP = true;

                    if (orderSummary.getJSONObject("IPlaza").getString("PINIndoPaket") != null &&
                            !TextUtils.isEmpty(orderSummary.getJSONObject("IPlaza").getString("PINIndoPaket"))) {
                        pinIndoPaket = orderSummary.getJSONObject("IPlaza").getString("PINIndoPaket");
                        mIsDeliveryIPP = false;
                    }

                    if (orderSummary.getJSONObject("IPlaza").getString("IPPStoreName") != null &&
                            !TextUtils.isEmpty(orderSummary.getJSONObject("IPlaza").getString("IPPStoreName"))) {
                        ippStoreName = orderSummary.getJSONObject("IPlaza").getString("IPPStoreName");
                        mIsDeliveryIPP = false;
                    }

                    if (orderSummary.getJSONObject("IPlaza").getString("IPPStoreStreet") != null &&
                            !TextUtils.isEmpty(orderSummary.getJSONObject("IPlaza").getString("IPPStoreStreet"))) {
                        ippStoreStreet = orderSummary.getJSONObject("IPlaza").getString("IPPStoreStreet");
                        mIsDeliveryIPP = false;
                    }

                    if (orderSummary.getJSONObject("IPlaza").getJSONArray("HistoryStatus") != null) {
                        if (orderSummary.getJSONObject("IPlaza").getJSONArray("HistoryStatus").length() > 0) {
                            mHistoryList = orderSummary.getJSONObject("IPlaza").getJSONArray("HistoryStatus");
                            trackingList.setAdapter(new TrackingHistoryAdapter(this, mHistoryList));
                            tvFieldStatus.setVisibility(View.VISIBLE);
                        }

                    }


                }

                for (int i = 0; i < productGoodArray.length(); i++) {
                    if (productGoodArray.getJSONObject(i).getString("ShippingPartner").equalsIgnoreCase("IPP")) {
                        mIsIPP = true;
                        if (productGoodArray.getJSONObject(i).getBoolean("IsDelivery")) {
                            mIsDeliveryIPP = true;
                        }else {
                            mIsDeliveryIPP = false;
                            if (orderSummary.getJSONObject("IPlaza").getString("PINIndoPaket") != null &&
                                    !TextUtils.isEmpty(orderSummary.getJSONObject("IPlaza").getString("PINIndoPaket"))) {
                                pinIndoPaket = orderSummary.getJSONObject("IPlaza").getString("PINIndoPaket");
                            }

                            if (orderSummary.getJSONObject("IPlaza").getString("IPPStoreName") != null &&
                                    !TextUtils.isEmpty(orderSummary.getJSONObject("IPlaza").getString("IPPStoreName"))) {
                                ippStoreName = orderSummary.getJSONObject("IPlaza").getString("IPPStoreName");
                            }

                            if (orderSummary.getJSONObject("IPlaza").getString("IPPStoreStreet") != null &&
                                    !TextUtils.isEmpty(orderSummary.getJSONObject("IPlaza").getString("IPPStoreStreet"))) {
                                ippStoreStreet = orderSummary.getJSONObject("IPlaza").getString("IPPStoreStreet");
                            }

//                            if (orderSummary.getJSONObject("IPlaza").getJSONObject("HistorySatus") != null) {
//                                if (orderSummary.getJSONObject("IPlaza").getJSONArray("HistorySatus").length() > 0) {
//                                    mHistoryList = orderSummary.getJSONObject("IPlaza").getJSONArray("HistorySatus");
//                                    trackingList.setAdapter(new TrackingHistoryAdapter(this, mHistoryList));
//                                }
//
//                            }
                        }
                        plazaGoodsIPP.add(productGoodArray.getJSONObject(i));
                    } else {
                        if (productGoodArray.getJSONObject(i).getBoolean("IsUseNote")) {
                            plazaGoodsPAAI.add(productGoodArray.getJSONObject(i));
                            sendDatePaai = orderSummary.getJSONObject("IPlaza").getString("WaktuPengirimanSupplier");
                        } else {
                            plazaGoods.add(productGoodArray.getJSONObject(i));
                        }

                    }

                }

            } else {
                plazaOrderNumber = "";
            }

            if (!orderSummary.getString("IVirtual").equals("null")) {
                virtualOrderNumber = orderSummary.getJSONObject("IVirtual").getString("SalesOrderNo");
                JSONArray productGoodArray = orderSummary.getJSONObject("IVirtual").getJSONArray("ItemDetail");

                for (int i = 0; i < productGoodArray.length(); i++) {
                    virtualGoods.add(productGoodArray.getJSONObject(i));
                }
            } else {
                virtualOrderNumber = "";
            }

            if (!orderSummary.getString("ICombine").equals("null")) {
                combinedOrderNumber = orderSummary.getJSONObject("ICombine").getString("SalesOrderNo");
                JSONArray productGoodArray = orderSummary.getJSONObject("ICombine").getJSONArray("ItemDetail");

                for (int i = 0; i < productGoodArray.length(); i++) {
                    if (productGoodArray.getJSONObject(i).getBoolean("IsVirtual")) {
                        virtualGoods.add(productGoodArray.getJSONObject(i));
                    } else if (productGoodArray.getJSONObject(i).getBoolean("IsPlaza")) {
                        if (productGoodArray.getJSONObject(i).getBoolean("IsUseNote")) {
                            plazaGoodsPAAI.add(productGoodArray.getJSONObject(i));
                            sendDatePaai = orderSummary.getJSONObject("IPlaza").getString("WaktuPengirimanSupplier");
                        } else {
                            plazaGoods.add(productGoodArray.getJSONObject(i));
                        }
                    } else {
                        isDelivery = orderSummary.getJSONObject("ICombine").getBoolean("IsDelivery");
                        waktuPengiriman = orderSummary.getJSONObject("ICombine").getString("WaktuPengiriman").split("T");
                        addressFull = orderSummary.getJSONObject("ICombine").getString("AddressFull");
                        if (productGoodArray.getJSONObject(i).getString("PLU").equals("10036492")) {
                            storeShippingCost = productGoodArray.getJSONObject(i).getDouble("SubTotal");
                        } else {
                            storeGoods.add(productGoodArray.getJSONObject(i));
                        }
                    }
                }
            } else {
                combinedOrderNumber = "";
            }

            Double totalShippingCost = 0.0;
            if (plazaGoodsPAAI.size() > 0) {
                totalShippingCost = (plazaCostWarehouse + plazaCostSupplier + plazaCostBookingDirect) + storeShippingCost;
            } else {
                totalShippingCost = (plazaCostWarehouse + plazaCostSupplier) + storeShippingCost;
            }

            if (summaryFrom.equals("prod")) {
                transactionCodeContainer.setVisibility(View.GONE);
                topInfoContainer.setVisibility(View.VISIBLE);
            } else {
                if (orderSummary.getJSONObject("Payment").getString("PaymentStatus").equals("2")) {
                    if (orderSummary.getString("PaymentTypeCode").toLowerCase().equals("cod")) {
                        topNotif.setText("Terima kasih telah berbelanja di KlikIndomaret, pembayaran anda sudah kami terima. Email konfirmassi akan dikirimkan setelah pemesanan anda siap dikirim.");
                    } else {
                        topNotif.setText("Terima kasih telah berbelanja di KlikIndomaret, pembayaran anda sudah kami terima. Email konfirmassi akan dikirimkan setelah pemesanan anda siap dikirim.");
                    }
                } else if (orderSummary.getJSONObject("Payment").getString("PaymentStatus").equals("9") ||
                        orderSummary.getJSONObject("Payment").getString("PaymentStatus").equals("3") ||
                        orderSummary.getJSONObject("Payment").getString("PaymentStatus").equals("8")) {

                    if (orderSummary.getJSONObject("Payment").getString("PaymentStatus").equals("9") || orderSummary.getJSONObject("Payment").getString("PaymentStatus").equals("3")) {
                        topNotif.setText("Mohon maaf, \n pesanan anda tidak dapat kami proses karena ada kendala teknis pada sistem pembayaran.");
                    } else {
                        topNotif.setText("Mohon maaf, pesanan anda tidak dapat kami proses karena anda telah melebihi batas waktu pembayaran.");
                    }
                } else {
                    if (orderSummary.getString("PaymentTypeCode").toLowerCase().equals("cod")) {
                        topNotif.setText("Silahkan lakukan pembayaran dengan detail sebagai berikut. \n Email konfirmasi akan dikirimkan setelah pembayaran berhasil diproses.");
                    } else {
                        topNotif.setText("Silahkan lakukan pembayaran dengan detail sebagai berikut. \n Email konfirmasi akan dikirimkan setelah pembayaran berhasil diproses.");

                        String limitPayment = orderSummary.getString("LimitPayment");
                        String[] limitDates = limitPayment.split("T");
                        String[] limitDate = limitDates[0].split("-");
                        String[] limitTime = limitDates[1].split(":");
                    }
                }

                summarytTransactionCode.setText(transactionCode);
                transactionCodeContainer.setVisibility(View.VISIBLE);

//                ProductAction productAction = new ProductAction(ProductAction.ACTION_CHECKOUT)
//                        .setCheckoutStep(6)
//                        .setCheckoutOptions(orderSummary.getJSONObject("Payment").getString("ID"));
//
//                HitBuilders.ScreenViewBuilder builder = new HitBuilders.ScreenViewBuilder()
//                        .setProductAction(productAction);
//
//                AppController application = (AppController) getApplication();
//                Tracker t = application.getDefaultTracker();
//                t.setScreenName("Summary Order");
//                t.send(builder.build());
            }
            StringBuilder stringBuilder = new StringBuilder();

            if (storeOrderNumber == "null") storeOrderNumber = "";
            if (plazaOrderNumber == "null") plazaOrderNumber = "";
            if (virtualOrderNumber == "null") virtualOrderNumber = "";
            if (combinedOrderNumber == null || combinedOrderNumber == "") {
                if (!storeOrderNumber.equals("")) {
                    if (stringBuilder.length() == 0) stringBuilder.append(storeOrderNumber);
                    else stringBuilder.append(", " + storeOrderNumber);
                }

                if (!plazaOrderNumber.equals("")) {
                    if (stringBuilder.length() == 0) stringBuilder.append(plazaOrderNumber);
                    else stringBuilder.append(", " + plazaOrderNumber);
                }

                if (!virtualOrderNumber.equals("")) {
                    if (stringBuilder.length() == 0) stringBuilder.append(virtualOrderNumber);
                    else stringBuilder.append(", " + virtualOrderNumber);
                }

                summaryOrderNumber.setText(stringBuilder);
                OrderNumberTop.setText(stringBuilder);
            } else {
                summaryOrderNumber.setText(combinedOrderNumber);
                OrderNumberTop.setText(combinedOrderNumber);
            }

            Double discount = orderSummary.getDouble("TotalDiscount");
            if (discount.equals(0.0)) {
                summarySavingCost.setVisibility(View.GONE);
                summarySavingCostInfo.setVisibility(View.GONE);
                savingCostTopInfo.setVisibility(View.GONE);
            } else {
                summarySavingCost.setText("Rp " + df.format(discount).replace(",", "."));
                summarySavingCost.setVisibility(View.VISIBLE);
                summarySavingCostInfo.setVisibility(View.VISIBLE);

                savingCostTop.setText("Rp " + df.format(discount).replace(",", "."));
                savingCostTopInfo.setVisibility(View.VISIBLE);
            }

            Double total = 0.0;
            if (summaryFrom.equals("prod")) {
                total = orderSummary.getDouble("TotalOrder");
            } else {
                total = orderSummary.getDouble("Total");
            }

            summaryTotalPrice.setText("Rp " + df.format(total).replace(",", "."));
            summaryShippingCost.setText("Rp " + df.format(totalShippingCost).replace(",", "."));
            summaryTotalTransaction.setText("Rp " + df.format(total - totalShippingCost).replace(",", "."));

            Double voucherDisc = orderSummary.getDouble("VBKNominal");
            if(voucherDisc > 0){
                linVoucherInfo.setVisibility(View.VISIBLE);
                voucherNominal.setText("Rp (" + df.format(voucherDisc).replace(",", ".") + ")");
            }

            totalTop.setText("Rp " + df.format(total).replace(",", "."));
            totalShippingTop.setText("Rp " + df.format(totalShippingCost).replace(",", "."));
            totalTransactionTop.setText("Rp " + df.format(total - totalShippingCost).replace(",", "."));

            if (summaryFrom.equals("prod")) {
                summaryFirstNameText.setText("Nama : ");
                summaryFirstName.setText(orderSummary.getString("CustomerName"));
                linearSummaryLastName.setVisibility(View.GONE);
                summaryEmail.setText(orderSummary.getString("CustomerEmail"));
                summaryPhone.setText(orderSummary.getString("CustomerMobile"));
            } else {
                summaryFirstNameText.setText("Nama Depan : ");
                summaryFirstName.setText(orderSummary.getString("CustomerFName"));
                summaryLastName.setText(orderSummary.getString("CustomerLName"));
                summaryEmail.setText(orderSummary.getString("CustomerEmail"));
                summaryPhone.setText(orderSummary.getString("CustomerMobile"));

                linearSummaryLastName.setVisibility(View.VISIBLE);
            }

            if (storeGoods.size() > 0) {
                summaryProductAdapter = new SummaryProductAdapter(OrderSummaryActivity.this, storeGoods);
                storeProductList.setAdapter(summaryProductAdapter);

                soTokoText.setText(soTokoText.getText() + " (" + storeOrderNumber + ")");

                if (orderSummary.getJSONObject("IStore").getString("Cabang").equals("")
                        || orderSummary.getJSONObject("IStore").getString("Cabang").equals("null")
                        || orderSummary.getJSONObject("IStore").getString("Cabang") == null) {
                    storeAddress.setText(addressFull);
                } else {
                    storeAddress.setText(orderSummary.getJSONObject("IStore").getString("Cabang") + "\n " + addressFull);
                }

                if (isDelivery) {
                    linearLayoutStoreShipping.setVisibility(View.VISIBLE);
                    linearIdentitas.setVisibility(View.VISIBLE);
                    infoDeliver.setVisibility(View.GONE);

                    summaryReceiverName.setText(orderSummary.getJSONObject("IStore").getString("ReceiverName"));
                    summaryReceiverPhone.setText(orderSummary.getJSONObject("IStore").getString("ReceiverPhone"));

                    if ((orderSummary.getJSONObject("IStore").getString("CustomerAddress2") == null || orderSummary.getJSONObject("IStore").getString("CustomerAddress2").equals("null") || orderSummary.getJSONObject("IStore").getString("CustomerAddress2").equals(""))
                            && (orderSummary.getJSONObject("IStore").getString("CustomerAddress3") == null || orderSummary.getJSONObject("IStore").getString("CustomerAddress3").equals("null") || orderSummary.getJSONObject("IStore").getString("CustomerAddress3").equals(""))) {
                        summaryReceiverAddress.setText(orderSummary.getJSONObject("IStore").getString("CustomerAddress"));
                    } else if (orderSummary.getJSONObject("IStore").getString("CustomerAddress2") == null || orderSummary.getJSONObject("IStore").getString("CustomerAddress2").equals("null") || orderSummary.getJSONObject("IStore").getString("CustomerAddress2").equals("")) {
                        summaryReceiverAddress.setText(orderSummary.getJSONObject("IStore").getString("CustomerAddress") + "\n"
                                + orderSummary.getJSONObject("IStore").getString("CustomerAddress3"));
                    } else if (orderSummary.getJSONObject("IStore").getString("CustomerAddress3") == null || orderSummary.getJSONObject("IStore").getString("CustomerAddress3").equals("null") || orderSummary.getJSONObject("IStore").getString("CustomerAddress3").equals("")) {
                        summaryReceiverAddress.setText(orderSummary.getJSONObject("IStore").getString("CustomerAddress") + "\n"
                                + orderSummary.getJSONObject("IStore").getString("CustomerAddress2"));
                    } else {
                        summaryReceiverAddress.setText(orderSummary.getJSONObject("IStore").getString("CustomerAddress") + "\n"
                                + orderSummary.getJSONObject("IStore").getString("CustomerAddress2") + "\n"
                                + orderSummary.getJSONObject("IStore").getString("CustomerAddress3"));
                    }

                    storeShipping.setText("Rp " + df.format(storeShippingCost).replace(",", "."));
                    storeDeliver.setText("\nPesanan akan diantarkan pada : ");
                } else {
                    linearIdentitas.setVisibility(View.GONE);

                    infoDeliver.setText("Pesanan bisa diambil di gerai Indomaret :");
                    storeDeliver.setText("\nPesanan bisa diambil pada : ");
                }

                String[] dateShipping = waktuPengiriman;
                String[] shipping = dateShipping[0].split("-");
                String[] timeShipping = dateShipping[1].split(":");

                if (timeShipping[0].contains("10")) {
                    storeDateTimeShipping.setText(shipping[2] + " " + month.getMonth(shipping[1]) + " " + shipping[0] + ", Pukul 10:00 - 12:00 WIB");
                } else if (timeShipping[0].contains("12")) {
                    storeDateTimeShipping.setText(shipping[2] + "-" + month.getMonth(shipping[1]) + "-" + shipping[0] + ", Pukul 12:00 - 15:00 WIB");
                } else if (timeShipping[0].contains("15")) {
                    storeDateTimeShipping.setText(shipping[2] + "-" + month.getMonth(shipping[1]) + "-" + shipping[0] + ", Pukul 15:00 - 18:00 WIB");
                } else if (timeShipping[0].contains("18")) {
                    storeDateTimeShipping.setText(shipping[2] + "-" + month.getMonth(shipping[1]) + "-" + shipping[0] + ", Pukul 18:00 - 20:00 WIB");
                }

                storeContainer.setVisibility(View.VISIBLE);
            } else {
                storeContainer.setVisibility(View.GONE);
            }

            //ipp
            linNotIPP.setVisibility(View.GONE);
            if (plazaGoodsIPP.size() > 0) {
                summaryProductAdapter = new SummaryProductAdapter(OrderSummaryActivity.this, plazaGoodsIPP);
                ippList.setAdapter(summaryProductAdapter);

//                soNonTokoText.setText(soNonTokoText.getText() + " (" + plazaOrderNumber + ")");
                tvValueDeliveryCost.setText("Rp " + df.format(plazaCostWarehouse + plazaCostSupplier).replace(",", "."));
//                tvValueDeliveryTime.setText(minDate + " - " + maxDate + " hari kerja");
                tvValueDeliveryTime.setText(2 + " - " + 5 + " hari kerja");

                tvFieldLogistikName.setVisibility(View.GONE);
                titikLogistikName.setVisibility(View.GONE);
                tvValueLogistikNo.setVisibility(View.GONE);

                if(mIsDeliveryIPP){
                    tvHeaderInfo.setText("Pesanan dikirim ke :");
                    tvFieldPenerima.setVisibility(View.VISIBLE);
                    titikPenerima.setVisibility(View.VISIBLE);
                    tvValuePenerima.setVisibility(View.VISIBLE);
                    tvFieldKeterangan.setVisibility(View.VISIBLE);
                    titikKeterangan.setVisibility(View.VISIBLE);
                    tvValueKeterangan.setVisibility(View.VISIBLE);
                    tvFieldLogistikName.setVisibility(View.VISIBLE);
                    titikLogistikName.setVisibility(View.VISIBLE);
                    tvValueLogistikNo.setVisibility(View.VISIBLE);

                    tvFieldPenerima.setText("Penerima");
                    tvFieldPhoneNo.setText("Telp");

                    tvValuePenerima.setText(orderSummary.getJSONObject("IPlaza").getString("ReceiverName"));
                    tvValuePhoneNo.setText(orderSummary.getJSONObject("IPlaza").getString("ReceiverPhone"));

                    if(orderSummary.getJSONObject("IPlaza").getString("KetAlamat") != "null") {
                        tvValueKeterangan.setText(orderSummary.getJSONObject("IPlaza").getString("KetAlamat"));
                    }
                    else {
                        tvValueKeterangan.setText("");
                    }


                    if (orderSummary.getJSONObject("IPlaza").toString().contains("NoAWB")){
                        if(!TextUtils.isEmpty(orderSummary.getJSONObject("IPlaza").getString("NoAWB")) &&
                                !orderSummary.getJSONObject("IPlaza").getString("NoAWB").equalsIgnoreCase("null")){

                            tvFieldLogistikName.setVisibility(View.VISIBLE);
                            titikLogistikName.setVisibility(View.VISIBLE);
                            tvValueLogistikNo.setVisibility(View.VISIBLE);

                            tvValueLogistikNo.setText(orderSummary.getJSONObject("IPlaza").getString("NoAWB"));
                        }else {
                            tvFieldLogistikName.setVisibility(View.GONE);
                            titikLogistikName.setVisibility(View.GONE);
                            tvValueLogistikNo.setVisibility(View.GONE);
                        }
                    }else{
                        tvFieldLogistikName.setVisibility(View.GONE);
                        titikLogistikName.setVisibility(View.GONE);
                        tvValueLogistikNo.setVisibility(View.GONE);
                    }

                    if ((orderSummary.getJSONObject("IPlaza").getString("CustomerAddress2") == null || orderSummary.getJSONObject("IPlaza").getString("CustomerAddress2").equals("null") || orderSummary.getJSONObject("IPlaza").getString("CustomerAddress2").equals(""))
                            && (orderSummary.getJSONObject("IPlaza").getString("CustomerAddress3") == null || orderSummary.getJSONObject("IPlaza").getString("CustomerAddress3").equals("null") || orderSummary.getJSONObject("IPlaza").getString("CustomerAddress3").equals(""))) {
                        tvValueAlamat.setText(orderSummary.getJSONObject("IPlaza").getString("CustomerAddress"));
                    } else if (orderSummary.getJSONObject("IPlaza").getString("CustomerAddress2") == null || orderSummary.getJSONObject("IPlaza").getString("CustomerAddress2").equals("null") || orderSummary.getJSONObject("IPlaza").getString("CustomerAddress2").equals("")) {
                        tvValueAlamat.setText(orderSummary.getJSONObject("IPlaza").getString("CustomerAddress") + "\n"
                                + orderSummary.getJSONObject("IPlaza").getString("CustomerAddress3"));
                    } else if (orderSummary.getJSONObject("IPlaza").getString("CustomerAddress3") == null || orderSummary.getJSONObject("IPlaza").getString("CustomerAddress3").equals("null") || orderSummary.getJSONObject("IPlaza").getString("CustomerAddress3").equals("")) {
                        tvValueAlamat.setText(orderSummary.getJSONObject("IPlaza").getString("CustomerAddress") + "\n"
                                + orderSummary.getJSONObject("IPlaza").getString("CustomerAddress2"));
                    } else {
                        tvValueAlamat.setText(orderSummary.getJSONObject("IPlaza").getString("CustomerAddress") + "\n"
                                + orderSummary.getJSONObject("IPlaza").getString("CustomerAddress2") + "\n"
                                + orderSummary.getJSONObject("IPlaza").getString("CustomerAddress3"));
                    }

                }else {
                    tvHeaderInfo.setText("Pesanan bisa diambil di gerai Indomaret :");
                    tvFieldLogistikName.setVisibility(View.GONE);
                    titikLogistikName.setVisibility(View.GONE);
                    tvValueLogistikNo.setVisibility(View.GONE);
                    tvFieldPenerima.setVisibility(View.GONE);
                    titikPenerima.setVisibility(View.GONE);
                    tvValuePenerima.setVisibility(View.GONE);
                    if(pinIndoPaket!=null){
                        if(!TextUtils.isEmpty(pinIndoPaket) && !pinIndoPaket.equalsIgnoreCase("null")){
                            tvFieldPenerima.setVisibility(View.VISIBLE);
                            titikPenerima.setVisibility(View.VISIBLE);
                            tvValuePenerima.setVisibility(View.VISIBLE);

                            tvFieldPenerima.setText("PIN");
                            tvValuePenerima.setText(pinIndoPaket);//pin

                        }
                    }

                    tvFieldPhoneNo.setText("Toko");
                    tvFieldKeterangan.setVisibility(View.INVISIBLE);
                    titikKeterangan.setVisibility(View.INVISIBLE);
                    tvValueKeterangan.setVisibility(View.GONE);

                    tvValuePhoneNo.setText(ippStoreName);//tokoName
                    tvValueAlamat.setText(ippStoreStreet);//alamat

                }

                nonStoreContainer.setVisibility(View.VISIBLE);
                relIpp.setVisibility(View.VISIBLE);
            } else {
                nonStoreContainer.setVisibility(View.GONE);
                relIpp.setVisibility(View.GONE);
            }



            if (plazaGoods.size() > 0) {
                summaryProductAdapter = new SummaryProductAdapter(OrderSummaryActivity.this, plazaGoods);
                nonstoreProductList.setAdapter(summaryProductAdapter);

                soNonTokoText.setText(soNonTokoText.getText() + " (" + plazaOrderNumber + ")");
                nonstoreShipping.setText("Rp " + df.format(plazaCostWarehouse + plazaCostSupplier).replace(",", "."));
                summaryDateNonStore.setText(minDate + " - " + maxDate + " hari kerja");

                summaryReceiverNameNonToko.setText(orderSummary.getJSONObject("IPlaza").getString("ReceiverName"));
                summaryReceiverPhoneNonToko.setText(orderSummary.getJSONObject("IPlaza").getString("ReceiverPhone"));

                if ((orderSummary.getJSONObject("IPlaza").getString("CustomerAddress2") == null || orderSummary.getJSONObject("IPlaza").getString("CustomerAddress2").equals("null") || orderSummary.getJSONObject("IPlaza").getString("CustomerAddress2").equals(""))
                        && (orderSummary.getJSONObject("IPlaza").getString("CustomerAddress3") == null || orderSummary.getJSONObject("IPlaza").getString("CustomerAddress3").equals("null") || orderSummary.getJSONObject("IPlaza").getString("CustomerAddress3").equals(""))) {
                    summaryReceiverAddressNonToko.setText(orderSummary.getJSONObject("IPlaza").getString("CustomerAddress"));
                } else if (orderSummary.getJSONObject("IPlaza").getString("CustomerAddress2") == null || orderSummary.getJSONObject("IPlaza").getString("CustomerAddress2").equals("null") || orderSummary.getJSONObject("IPlaza").getString("CustomerAddress2").equals("")) {
                    summaryReceiverAddressNonToko.setText(orderSummary.getJSONObject("IPlaza").getString("CustomerAddress") + "\n"
                            + orderSummary.getJSONObject("IPlaza").getString("CustomerAddress3"));
                } else if (orderSummary.getJSONObject("IPlaza").getString("CustomerAddress3") == null || orderSummary.getJSONObject("IPlaza").getString("CustomerAddress3").equals("null") || orderSummary.getJSONObject("IPlaza").getString("CustomerAddress3").equals("")) {
                    summaryReceiverAddressNonToko.setText(orderSummary.getJSONObject("IPlaza").getString("CustomerAddress") + "\n"
                            + orderSummary.getJSONObject("IPlaza").getString("CustomerAddress2"));
                } else {
                    summaryReceiverAddressNonToko.setText(orderSummary.getJSONObject("IPlaza").getString("CustomerAddress") + "\n"
                            + orderSummary.getJSONObject("IPlaza").getString("CustomerAddress2") + "\n"
                            + orderSummary.getJSONObject("IPlaza").getString("CustomerAddress3"));
                }

                nonStoreContainer.setVisibility(View.VISIBLE);
                linearLayoutNonPaai.setVisibility(View.VISIBLE);

                linNotIPP.setVisibility(View.VISIBLE);
            } else {
                soNonTokoText.setText(soNonTokoText.getText() + " (" + plazaOrderNumber + ")");
                if(!mIsIPP)
                    nonStoreContainer.setVisibility(View.GONE);
                linearLayoutNonPaai.setVisibility(View.GONE);
            }

            if (plazaGoodsPAAI.size() > 0) {
                summaryProductAdapter = new SummaryProductAdapter(OrderSummaryActivity.this, plazaGoodsPAAI);
                nonstoreProductPaaiList.setAdapter(summaryProductAdapter);

                String[] paymentDateSpilt = sendDatePaai.split("T");
                String[] paymentDate = paymentDateSpilt[0].split("-");

                datePaai.setText(paymentDate[2] + "-" + month.getMonth(paymentDate[1]) + "-" + paymentDate[0]);
                nonstorePaaiShipping.setText("Rp " + df.format(plazaCostBookingDirect).replace(",", "."));

                summaryReceiverNameNonToko.setText(orderSummary.getJSONObject("IPlaza").getString("ReceiverName"));
                summaryReceiverPhoneNonToko.setText(orderSummary.getJSONObject("IPlaza").getString("ReceiverPhone"));

                if ((orderSummary.getJSONObject("IPlaza").getString("CustomerAddress2") == null || orderSummary.getJSONObject("IPlaza").getString("CustomerAddress2").equals("null") || orderSummary.getJSONObject("IPlaza").getString("CustomerAddress2").equals(""))
                        && (orderSummary.getJSONObject("IPlaza").getString("CustomerAddress3") == null || orderSummary.getJSONObject("IPlaza").getString("CustomerAddress3").equals("null") || orderSummary.getJSONObject("IPlaza").getString("CustomerAddress3").equals(""))) {
                    summaryReceiverAddressNonToko.setText(orderSummary.getJSONObject("IPlaza").getString("CustomerAddress"));
                } else if (orderSummary.getJSONObject("IPlaza").getString("CustomerAddress2") == null || orderSummary.getJSONObject("IPlaza").getString("CustomerAddress2").equals("null") || orderSummary.getJSONObject("IPlaza").getString("CustomerAddress2").equals("")) {
                    summaryReceiverAddressNonToko.setText(orderSummary.getJSONObject("IPlaza").getString("CustomerAddress") + "\n"
                            + orderSummary.getJSONObject("IPlaza").getString("CustomerAddress3"));
                } else if (orderSummary.getJSONObject("IPlaza").getString("CustomerAddress3") == null || orderSummary.getJSONObject("IPlaza").getString("CustomerAddress3").equals("null") || orderSummary.getJSONObject("IPlaza").getString("CustomerAddress3").equals("")) {
                    summaryReceiverAddressNonToko.setText(orderSummary.getJSONObject("IPlaza").getString("CustomerAddress") + "\n"
                            + orderSummary.getJSONObject("IPlaza").getString("CustomerAddress2"));
                } else {
                    summaryReceiverAddressNonToko.setText(orderSummary.getJSONObject("IPlaza").getString("CustomerAddress") + "\n"
                            + orderSummary.getJSONObject("IPlaza").getString("CustomerAddress2") + "\n"
                            + orderSummary.getJSONObject("IPlaza").getString("CustomerAddress3"));
                }

                nonStoreContainer.setVisibility(View.VISIBLE);
                linearLayoutPaai.setVisibility(View.VISIBLE);

                linNotIPP.setVisibility(View.VISIBLE);
            } else {
                linearLayoutPaai.setVisibility(View.GONE);
            }

            if(virtualGoods.size() > 0){
                boolean isTicket = false;
                for (int i=0; i<virtualGoods.size(); i++){
                    if (virtualGoods.get(i).getBoolean("IsTicket")){
                        isTicket = true;
                    }
                }

                if (isTicket){
                    sendText.setText("Waktu pengiriman maksimal:");
                }else{
                    sendText.setText("Waktu pengiriman pulsa maksimal:");
                }

                System.out.println("--- virtualGoods : "+virtualGoods);
                summaryProductAdapter = new SummaryProductAdapter(OrderSummaryActivity.this, virtualGoods);
                virtualProductList.setAdapter(summaryProductAdapter);
                virtualContainer.setVisibility(View.VISIBLE);
                soVirtualText.setText(soVirtualText.getText() + " (" + virtualOrderNumber + ")");
            } else {
                virtualContainer.setVisibility(View.GONE);
                soVirtualText.setText(soVirtualText.getText() + " (" + virtualOrderNumber + ")");
            }

            if (summaryFrom.equals("pay")) {
                HitBuilders.ScreenViewBuilder builder = new HitBuilders.ScreenViewBuilder();

                for (int i = 0; i < storeGoods.size(); i++) {
                    Product product = new Product()
                            .setId(storeGoods.get(i).getString("SoID"))
                            .setName(storeGoods.get(i).getString("Nama"))
                            .setCategory("Produk Toko")
                            .setPrice(storeGoods.get(i).getInt("HargaSatuan"))
                            .setQuantity(storeGoods.get(i).getInt("Qty"));

                    builder.addProduct(product);
                }

                for (int i = 0; i < plazaGoods.size(); i++) {
                    Product product = new Product()
                            .setId(plazaGoods.get(i).getString("SoID"))
                            .setName(plazaGoods.get(i).getString("Nama"))
                            .setCategory("Produk Plaza")
                            .setPrice(plazaGoods.get(i).getInt("HargaSatuan"))
                            .setQuantity(plazaGoods.get(i).getInt("Qty"));

                    builder.addProduct(product);
                }

                for (int i = 0; i < virtualGoods.size(); i++) {
                    Product product = new Product()
                            .setId(virtualGoods.get(i).getString("SoID"))
                            .setName(virtualGoods.get(i).getString("Nama"))
                            .setCategory("Produk Virtual")
                            .setPrice(virtualGoods.get(i).getInt("HargaSatuan"))
                            .setQuantity(virtualGoods.get(i).getInt("Qty"));

                    builder.addProduct(product);
                }

                ProductAction productAction = new ProductAction(ProductAction.ACTION_PURCHASE)
                        .setTransactionId(summaryOrderNumber.getText().toString())
                        .setTransactionAffiliation("Klikindomaret")
                        .setTransactionRevenue(Double.valueOf(String.valueOf(total - totalShippingCost).replace(",", "")))
                        .setTransactionShipping(Double.valueOf(String.valueOf(totalShippingCost).replace(",", "")));

                builder.setProductAction(productAction);

                AppController application = (AppController) getApplication();
                Tracker t = application.getDefaultTracker();
                t.setScreenName("Summary Order");
                t.send(builder.build());
            }

            String paymentMethodTxt = "";
            if (summaryFrom.equals("prod")) {
                LinearLayout paymentContainer = (LinearLayout) findViewById(R.id.payment_container);
                paymentContainer.setVisibility(View.GONE);
            } else {
                if(orderSummary.getJSONObject("Payment").getString("PaymentStatus").equals("0"))
                {
                    String limitPayment = orderSummary.getString("LimitPayment");
                    String[] limitDates = limitPayment.split("T");
                    String[] limitDate = limitDates[0].split("-");
                    String[] limitTime = limitDates[1].split(":");

                    if (orderSummary.getString("PaymentTypeCode").toLowerCase().equals("cod")) {
                        summaryStatusPayment.setText("Menunggu Pembayaran");
                    }else{
                        summaryStatusPayment.setText("Menunggu Pembayaran (Bayar sebelum " + limitDate[2] + " " + month.getMonth(limitDate[1]) + " " + limitDate[0] + ", " + limitTime[0] + ":" + limitTime[1] + " WIB)");
                    }

                    summaryStatusPayment.setTextColor(Color.parseColor("#d10000"));
                }
                else if(orderSummary.getJSONObject("Payment").getString("PaymentStatus").equals("2")) {
                    summaryStatusPayment.setText("Lunas");
                    summaryStatusPayment.setTextColor(Color.parseColor("#00b718"));
                }
                else if(orderSummary.getJSONObject("Payment").getString("PaymentStatus").equals("3") || orderSummary.getJSONObject("Payment").getString("PaymentStatus").equals("9")) {
                    summaryStatusPayment.setText("Gagal");
                    summaryStatusPayment.setTextColor(Color.parseColor("#d10000"));
                }
                else if(orderSummary.getJSONObject("Payment").getString("PaymentStatus").equals("8")) {
                    summaryStatusPayment.setText("Pembatalan Otomatis (melewati batas waktu pembayaran)");
                    summaryStatusPayment.setTextColor(Color.parseColor("#d10000"));
                }

                String paymentType = orderSummary.getString("PaymentTypeCode");
                paymentMethod.setText(orderSummary.getString("PaymentTypeName"));
                if(paymentType.toLowerCase().equals("702"))
                {
                    labelTransactionCode.setText("Nomor Rekening Virtual");
                } else if (paymentType.toLowerCase().equals("402")) {
                    labelTransactionCode.setText("Nomor Rekening Virtual");
                    orderNumberContainer.setVisibility(View.VISIBLE);
                }

                LinearLayout paymentContainer = (LinearLayout) findViewById(R.id.payment_container);
                paymentContainer.setVisibility(View.VISIBLE);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        summaryContainer.setVisibility(View.VISIBLE);
        stopLoader();
    }

    public void processStore(JSONArray response) {
        String[] dateShipping = shippingDate.split("T");
        String[] shipping = dateShipping[0].split("-");
        String[] timeShipping = shippingTime.split(":");

        try {
            storeDeliver.setText("Pesanan bisa diambil di gerai Indomaret : ");
            storeDateTimeShipping.setText(response.getJSONObject(0).getString("Name") + "\n" + response.getJSONObject(0).getString("Street"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (timeShipping[0].equals("10")) {
            timeDeliver.setVisibility(View.VISIBLE);
            timeDeliver.setText("Pesanan dapat diambil pada : \n" + shipping[2] + " " + month.getMonth(shipping[1]) + " " + shipping[0] + ", Pukul 10:00 - 12:00 WIB");
        } else if (timeShipping[0].equals("12")) {
            timeDeliver.setVisibility(View.VISIBLE);
            timeDeliver.setText("Pesanan dapat diambil pada : \n" + shipping[2] + " " + month.getMonth(shipping[1]) + " " + shipping[0] + ", Pukul 12:00 - 15:00 WIB");
        } else if (timeShipping[0].equals("15")) {
            timeDeliver.setVisibility(View.VISIBLE);
            timeDeliver.setText("Pesanan dapat diambil pada : \n" + shipping[2] + " " + month.getMonth(shipping[1]) + " " + shipping[0] + ", Pukul 15:00 - 18:00 WIB");
        } else if (timeShipping[0].equals("18")) {
            timeDeliver.setVisibility(View.VISIBLE);
            timeDeliver.setText("Pesanan dapat diambil pada : \n" + shipping[2] + " " + month.getMonth(shipping[1]) + " " + shipping[0] + ", Pukul 18:00 - 20:00 WIB");
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
            if (summaryFrom.equals("pay")) {
                intent = new Intent(OrderSummaryActivity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | IntentCompat.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
                overridePendingTransition(R.anim.right_in, R.anim.right_out);
                return true;
            } else {
                finish();
                overridePendingTransition(R.anim.right_in, R.anim.right_out);
            }
        }

        return super.onOptionsItemSelected(item);
    }

    public void onBackPressed() {
        if (summaryFrom.equals("pay")) {
            intent = new Intent(OrderSummaryActivity.this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | IntentCompat.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
            overridePendingTransition(R.anim.right_in, R.anim.right_out);
        } else {
            finish();
            overridePendingTransition(R.anim.right_in, R.anim.right_out);
        }
    }

    private void setDefaultIPP() {
        pinIndoPaket = ippStoreName = ippStoreStreet = "";
        relIpp.setVisibility(View.GONE);
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
