package com.indomaret.klikindomaret.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.IntentCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.InputFilter;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
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
import com.indomaret.klikindomaret.MainActivity;
import com.indomaret.klikindomaret.R;
import com.indomaret.klikindomaret.adapter.CouponAdapter;
import com.indomaret.klikindomaret.adapter.PaymentCategoryAdapter;
import com.indomaret.klikindomaret.app.AppController;
import com.indomaret.klikindomaret.helper.Month;
import com.indomaret.klikindomaret.helper.SQLiteHandler;
import com.indomaret.klikindomaret.helper.SessionManager;
import com.indomaret.klikindomaret.views.HeightAdjustableListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class PaymentActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private SessionManager sessionManager;
    private Intent intent;
    private SQLiteHandler sqLiteHandler;

    private String plazaAddressId, expiredDate, shippingType, salesOrderid, message, paymentId, time, date,mCustomerAddressId, sendDate, latitude, longitude;
    private String stringDate = "";
    private String stringClock = "";
    private String gatewayCode = "";
    private String shippingTime = "";
    private String noHPEMoney = "";
    public String tokenEMoney = "";
    private String storeCode = "";
    private String infoStore = "";
    private String addressTextValue = "";
    private String[] listProductArray,arrayDate, clock;
    private List<String> listCoupon = new ArrayList<>();
    private Double totalPrice = 0.0;
    private Double totalDiscount = 0.0;
    private Double voucher = 0.0;
    private Double coupon = 0.0;
    private Double shippingCost = 0.0;
    private Double totalPricePromo = 0.0;
    private double pricePromo = 0.0;
    private double priceWithDiscountPromo = 0.0;
    private boolean isCOD,isSend, isDeliveryOrder, isPaai, isIPP;
    private JSONObject paymentListObejct = new JSONObject();
    private JSONObject paymentObject = new JSONObject();
    private JSONObject defaultAddressObject = new JSONObject();
    private JSONObject defaultAddressStoreObject = new JSONObject();
    private JSONObject storeObject = new JSONObject();
    private JSONArray paymentArrayList = new JSONArray();
    private JSONArray promoPaymentSelected = new JSONArray();
    private JSONArray promoProducts = new JSONArray();
    private JSONArray storeProductObject = new JSONArray();
    private JSONArray nonstoreProductObject = new JSONArray();
    private List<String> listProductDetail;
    private JSONArray list = new JSONArray();
    private JSONArray promoPaymentProductCart = new JSONArray();
    private DecimalFormat df = new DecimalFormat("#,###");
    private PaymentCategoryAdapter paymentCategoryAdapter;
    private AlertDialog.Builder alertDialogBuilder;
    private AlertDialog alertDialog;
    private Runnable runnable;
    private Month dateName = new Month();
    private DateFormat dateFormat = new SimpleDateFormat("dd MMMM yyyy");

    private LinearLayout linearTotal, linearRingkasan;
    private RelativeLayout preloader;
    private TextView totalTransaction, totalDiscountText, totalShippingCost, totalVoucher, infoVoucher, mTitle;
    private EditText voucherText;
    private Button btnVoucher, btnBuy;
    private ImageView imageArrow;
    private HeightAdjustableListView couponList;
    private CouponAdapter couponAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        sessionManager = new SessionManager(this);
        intent = getIntent();
        sqLiteHandler = new SQLiteHandler(this);

        toolbar = (Toolbar) findViewById(R.id.app_toolbar);
        mTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
        toolbar.setTitle("");
        mTitle.setText("Pembayaran");
        setSupportActionBar(toolbar);

        plazaAddressId = intent.getStringExtra("plazaAddressId");

        try {
            if (intent.getStringExtra("storeProductObject") != null && intent.getStringExtra("storeProductObject").length() > 0) storeProductObject = new JSONArray(intent.getStringExtra("storeProductObject"));
            if (intent.getStringExtra("nonstoreProductObject") != null && intent.getStringExtra("nonstoreProductObject").length() > 0)nonstoreProductObject = new JSONArray(intent.getStringExtra("nonstoreProductObject"));

            if (storeProductObject != null && storeProductObject.length() > 0){
                isSend = intent.getBooleanExtra("isSend", false);
                time = intent.getStringExtra("time");
                date = intent.getStringExtra("date");
                shippingType = intent.getStringExtra("shippingType");
                expiredDate = intent.getStringExtra("expiredDate");

                if (isSend){
                    defaultAddressStoreObject = new JSONObject(intent.getStringExtra("address"));
                }else{
                    storeObject = new JSONObject(intent.getStringExtra("storeObject"));
                }
            }

            if (nonstoreProductObject != null && nonstoreProductObject.length() > 0){
                isDeliveryOrder = intent.getBooleanExtra("isDeliveryOrder", false);
                isPaai = intent.getBooleanExtra("isPaai", false);
                isIPP = intent.getBooleanExtra("isIPP", false);
                sendDate = intent.getStringExtra("sendDate");

                if (isDeliveryOrder){
                    defaultAddressObject = new JSONObject(intent.getStringExtra("defaultAddressObject"));
                    infoStore = intent.getStringExtra("infoStore");
                    addressTextValue = intent.getStringExtra("addressTextValue");
                    mCustomerAddressId = intent.getStringExtra("mCustomerAddressId");
                    latitude = intent.getStringExtra("latitude");
                    longitude = intent.getStringExtra("longitude");
                }else{
                    storeCode = intent.getStringExtra("storeCode");
                }

                if(!isDeliveryOrder && isPaai){
                    mCustomerAddressId = intent.getStringExtra("mCustomerAddressId");
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        linearTotal = (LinearLayout) findViewById(R.id.footer);
        linearRingkasan = (LinearLayout) findViewById(R.id.discount_container);
        preloader = (RelativeLayout) findViewById(R.id.preloader);

        totalTransaction = (TextView) findViewById(R.id.total_transaction);
        totalDiscountText = (TextView) findViewById(R.id.total_discount);
        totalShippingCost = (TextView) findViewById(R.id.total_ongkir);
        totalVoucher = (TextView) findViewById(R.id.total_voucher);
        infoVoucher = (TextView) findViewById(R.id.info_voucher);

        voucherText = (EditText) findViewById(R.id.voucher_text);
        btnVoucher = (Button) findViewById(R.id.btn_voucher);
        btnBuy= (Button) findViewById(R.id.btn_buy);
        imageArrow= (ImageView) findViewById(R.id.image_arrow);
        couponList = (HeightAdjustableListView) findViewById(R.id.coupon_list);

        voucherText.setFilters(new InputFilter[]{new InputFilter.AllCaps()});

        totalPrice = Double.valueOf(sessionManager.getKeyTotalPrice());
        totalDiscount = Double.valueOf(sessionManager.getKeyTotalDiscount());
        voucher = Double.valueOf(sessionManager.getKeyTotalVoucher());
        shippingCost = Double.valueOf(sessionManager.getKeyTotalShippingCost());
        coupon = Double.valueOf(sessionManager.getKeyTotalCoupon());

        totalDiscountText.setText("Rp " + df.format(totalDiscount).replace(",","."));
        totalTransaction.setText("Rp " + df.format(totalPrice - totalDiscount + shippingCost - voucher).replace(",","."));
        totalShippingCost.setText("Rp " + df.format(shippingCost).replace(",","."));
        totalVoucher.setText("Rp " + df.format(coupon + voucher).replace(",","."));

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

        btnBuy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getBuy(gatewayCode);
            }
        });

        try {
            if (storeProductObject != null && storeProductObject.length() > 0){
                clock  = time.split("-");
                stringClock = clock[0].replace(" ","");

                arrayDate = date.replace(",", " ").split(" ");
                stringDate = arrayDate[3]+"-"+dateName.getMonthNumber(arrayDate[2])+"-"+arrayDate[1]+"T00:00:00";
            }else{
                SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd");
                SimpleDateFormat sdfTime = new SimpleDateFormat("HH:mm");
                stringDate = sdfDate.format(new Date())+"T"+sdfTime.format(new Date());
            }

            URL url = new URL(API.getInstance().getApiPaymentSection()
                    + "?CartID=" + sessionManager.getCartId()
                    + "&WaktuPengiriman=" + stringDate
                    + "&CustomerId=" + sessionManager.getUserID()
                    + "&RegionID=" + sessionManager.getRegionID()
                    + "&WarehouseAddressID=" + plazaAddressId
                    + "&ShippingType=" + shippingType
                    + "&mfp_id=" + sessionManager.getKeyMfpId());
            URI uri = new URI(url.getProtocol(), url.getUserInfo(), url.getHost(), url.getPort(), url.getPath(), url.getQuery(), url.getRef());
            url = uri.toURL();
            jsonArrayRequest(url.toString(), "payment");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

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

    public void insertPayment(JSONArray response){
        try {
            JSONObject paymentObject = response.getJSONObject(0);
            JSONObject insertPaymentObject = new JSONObject();
            salesOrderid = paymentObject.getString("SalesOrderHeaderId");

            insertPaymentObject.put("CreateBy", sessionManager.getUserID());
            insertPaymentObject.put("SalesOrderHeaderId", salesOrderid);
            insertPaymentObject.put("GatewayCode", gatewayCode);
            insertPaymentObject.put("CODValue", "");
            insertPaymentObject.put("IsCODTimeValid", true);
            if (isCOD){
                insertPaymentObject.put("ShippingTime", stringDate);
            }else{
                insertPaymentObject.put("ShippingTime", paymentObject.getString("ShippingTime"));
            }
            insertPaymentObject.put("instalmentCredit", "");
            insertPaymentObject.put("vouchercode", "");
            insertPaymentObject.put("regionID", sessionManager.getRegionID());
            insertPaymentObject.put("NoHPEMoney", noHPEMoney);
            insertPaymentObject.put("TokenEMoney", tokenEMoney);

            jsonPost(API.getInstance().getApiPostInsertPayment() + "?mfp_id=" + sessionManager.getKeyMfpId(), insertPaymentObject, "insertPayment");
        } catch (JSONException e) {
            e.printStackTrace();
            stopLoader();
        }
    }

    public void setRadioPayment(JSONObject paymentAdapterObject){
        try {
            LinearLayout linearListPayment = (LinearLayout) findViewById(R.id.linear_list_payment);
            linearListPayment.removeAllViews();

            for (int j=0; j<paymentAdapterObject.getJSONArray("PaymentType").length(); j++){
                paymentAdapterObject.getJSONArray("PaymentType").getJSONObject(j).put("selected", "0");

                if (paymentAdapterObject.getJSONArray("PaymentType").getJSONObject(j).getString("Code").contains("500C")){
                    paymentAdapterObject.getJSONArray("PaymentType").getJSONObject(j).put("PaymentMethod", "9");
                }
            }

            paymentListObejct = paymentAdapterObject;

            for (int i=0; i<paymentAdapterObject.getJSONArray("PaymentMethod").length(); i++){
                RecyclerView listPayment = new RecyclerView(PaymentActivity.this);
                JSONObject paymentMethodObject = new JSONObject();
                list = new JSONArray();

                paymentMethodObject.put("paymentMethod", paymentAdapterObject.getJSONArray("PaymentMethod").get(i));
                list.put(paymentMethodObject);

                listPayment.setHasFixedSize(true);
                listPayment.setLayoutManager(new LinearLayoutManager(PaymentActivity.this));

                if (storeProductObject != null && storeProductObject.length() > 0){
                    paymentCategoryAdapter = new PaymentCategoryAdapter(PaymentActivity.this, list, paymentAdapterObject, "klikindomaret", promoPaymentProductCart, "", storeProductObject.length(), expiredDate);
                }else{
                    paymentCategoryAdapter = new PaymentCategoryAdapter(PaymentActivity.this, list, paymentAdapterObject, "klikindomaret", promoPaymentProductCart, "");
                }

                listPayment.setAdapter(paymentCategoryAdapter);
                linearListPayment.addView(listPayment);

                if (paymentAdapterObject.toString().contains("500C")){
                    if (paymentAdapterObject.getJSONArray("PaymentMethod").get(i).toString().split(",")[2].contains("Kartu Kredit")){
                        listPayment = new RecyclerView(PaymentActivity.this);
                        list = new JSONArray();
                        paymentMethodObject = new JSONObject();
                        String paymentCicilan = "9,9,Cicilan,http://klikindomaret.com/content/index/cara-pembayaran";

                        paymentMethodObject.put("paymentMethod", paymentCicilan);
                        list.put(paymentMethodObject);

                        listPayment.setHasFixedSize(true);
                        listPayment.setLayoutManager(new LinearLayoutManager(PaymentActivity.this));
                        if (storeProductObject != null && storeProductObject.length() > 0){
                            paymentCategoryAdapter = new PaymentCategoryAdapter(PaymentActivity.this, list, paymentAdapterObject, "klikindomaret", promoPaymentProductCart, "", storeProductObject.length(), expiredDate);
                        }else{
                            paymentCategoryAdapter = new PaymentCategoryAdapter(PaymentActivity.this, list, paymentAdapterObject, "klikindomaret", promoPaymentProductCart, "");
                        }
                        listPayment.setAdapter(paymentCategoryAdapter);
                        linearListPayment.addView(listPayment);
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void setRadioButton(JSONObject paymentObject, String type){
        this.paymentObject = paymentObject;

        totalPrice = Double.valueOf(sessionManager.getKeyTotalPrice());
        totalDiscount = Double.valueOf(sessionManager.getKeyTotalDiscount());
        voucher = Double.valueOf(sessionManager.getKeyTotalVoucher());
        shippingCost = Double.valueOf(sessionManager.getKeyTotalShippingCost());
        coupon = Double.valueOf(sessionManager.getKeyTotalCoupon());

        if (type.equals("select")){
            promoPaymentProductCart = new JSONArray();
            totalDiscountText.setText("Rp " + df.format(totalDiscount).replace(",","."));
            totalTransaction.setText("Rp " + df.format(totalPrice - totalDiscount + shippingCost - voucher).replace(",","."));
            totalShippingCost.setText("Rp " + df.format(shippingCost).replace(",","."));
            totalVoucher.setText("Rp " + df.format(coupon + voucher).replace(",","."));
        }

        try {
            gatewayCode = paymentObject.getString("Code");
            paymentId = paymentObject.getString("ID");

            for (int j=0; j<paymentListObejct.getJSONArray("PaymentType").length(); j++){
                if (paymentListObejct.getJSONArray("PaymentType").getJSONObject(j).getString("Code").equals(gatewayCode)){
                    paymentListObejct.getJSONArray("PaymentType").getJSONObject(j).put("selected", "1");
                }else{
                    paymentListObejct.getJSONArray("PaymentType").getJSONObject(j).put("selected", "0");
                }
            }

            LinearLayout linearListPayment = (LinearLayout) findViewById(R.id.linear_list_payment);
            linearListPayment.removeAllViews();

            for (int i=0; i<paymentListObejct.getJSONArray("PaymentMethod").length(); i++) {
                RecyclerView listPayment = new RecyclerView(PaymentActivity.this);
                JSONObject paymentMethodObject = new JSONObject();
                list = new JSONArray();

                paymentMethodObject.put("paymentMethod", paymentListObejct.getJSONArray("PaymentMethod").get(i));
                list.put(paymentMethodObject);

                listPayment.setHasFixedSize(true);
                listPayment.setLayoutManager(new LinearLayoutManager(PaymentActivity.this));
                if (storeProductObject != null && storeProductObject.length() > 0){
                    paymentCategoryAdapter = new PaymentCategoryAdapter(PaymentActivity.this, list, paymentListObejct, "klikindomaret", promoPaymentProductCart, "", storeProductObject.length(), expiredDate);
                }else{
                    paymentCategoryAdapter = new PaymentCategoryAdapter(PaymentActivity.this, list, paymentListObejct, "klikindomaret", promoPaymentProductCart, "");
                }
                listPayment.setAdapter(paymentCategoryAdapter);
                linearListPayment.addView(listPayment);

                if (paymentListObejct.toString().contains("500C")){
                    if (paymentListObejct.getJSONArray("PaymentMethod").get(i).toString().split(",")[2].contains("Kartu Kredit")){
                        listPayment = new RecyclerView(PaymentActivity.this);
                        list = new JSONArray();
                        paymentMethodObject = new JSONObject();
                        String paymentCicilan = "9,9,Cicilan,http://klikindomaret.com/content/index/cara-pembayaran";

                        paymentMethodObject.put("paymentMethod", paymentCicilan);
                        list.put(paymentMethodObject);

                        listPayment.setHasFixedSize(true);
                        listPayment.setLayoutManager(new LinearLayoutManager(PaymentActivity.this));
                        if (storeProductObject != null && storeProductObject.length() > 0){
                            paymentCategoryAdapter = new PaymentCategoryAdapter(PaymentActivity.this, list, paymentListObejct, "klikindomaret", promoPaymentProductCart, "", storeProductObject.length(), expiredDate);
                        }else{
                            paymentCategoryAdapter = new PaymentCategoryAdapter(PaymentActivity.this, list, paymentListObejct, "klikindomaret", promoPaymentProductCart, "");
                        }
                        listPayment.setAdapter(paymentCategoryAdapter);
                        linearListPayment.addView(listPayment);
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void updatePromoPayment(JSONArray promoPayment){
        promoPaymentSelected = new JSONArray();
        try {
            for (int i=0; i<promoPayment.length(); i++){
                if (promoPayment.getJSONObject(i).getBoolean("IsSelectedPromo")){
                    promoPaymentSelected.put(promoPayment.getJSONObject(i));
                }
            }

            double pricePromo = 0.0;
            double priceWithDiscountPromo = 0.0;
            totalPricePromo = 0.0;
            for (int i=0; i<promoPaymentSelected.length(); i++){
                pricePromo += promoPaymentSelected.getJSONObject(i).getDouble("Price") * promoPaymentSelected.getJSONObject(i).getInt("QuantityUpdate");
                priceWithDiscountPromo += promoPaymentSelected.getJSONObject(i).getDouble("PriceWithDiscount") * promoPaymentSelected.getJSONObject(i).getInt("QuantityUpdate");
//                        totalPricePromo += promoPaymentSelected.getJSONObject(i).getDouble("Discount");
            }

            totalPrice = Double.valueOf(sessionManager.getKeyTotalPrice());
            totalDiscount = Double.valueOf(sessionManager.getKeyTotalDiscount());
            voucher = Double.valueOf(sessionManager.getKeyTotalVoucher());
            shippingCost = Double.valueOf(sessionManager.getKeyTotalShippingCost());
            coupon = Double.valueOf(sessionManager.getKeyTotalCoupon());

            totalPricePromo = pricePromo - priceWithDiscountPromo;
            totalDiscountText.setText("Rp " + df.format(totalDiscount + totalPricePromo).replace(",","."));
            totalTransaction.setText("Rp " + df.format(totalPrice - totalDiscount + shippingCost + (pricePromo - totalPricePromo)  - voucher).replace(",","."));
            totalShippingCost.setText("Rp " + df.format(shippingCost).replace(",","."));
            totalVoucher.setText("Rp " + df.format(coupon + voucher).replace(",","."));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void setSpinner(JSONObject paymentObject){
        try {
            gatewayCode = paymentObject.getString("Code");
            paymentId = paymentObject.getString("ID");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void getBuy(String code){
        try {
            sessionManager.setKeyStock(false);
            if(code.equals("RKPON") || code.equals("BPISAKU")){
                isCOD = false;

                intent = new Intent(PaymentActivity.this, PaymentElectronicActivity.class);
                intent.putExtra("paymentCode", gatewayCode);
                intent.putExtra("objectResponse", paymentObject.toString());
                intent.putExtra("defaultAddress", sqLiteHandler.getDefaultAddress());

                if (storeProductObject.length() > 0){
                    JSONArray storeArray = new JSONArray();
                    for (int i=0; i<storeProductObject.length(); i++){
                        storeArray.put(storeProductObject.get(i));
                    }

                    intent.putExtra("storeProductString", storeArray.toString());
                }

                if (nonstoreProductObject.length() > 0){
                    JSONArray nonStoreArray = new JSONArray();
                    for (int i=0; i<nonstoreProductObject.length(); i++){
                        nonStoreArray.put(nonstoreProductObject.get(i));
                    }

                    intent.putExtra("nonstoreProductString", nonStoreArray.toString());
                }

                intent.putExtra("isSend", isSend);
                intent.putExtra("isDeliveryOrder", isDeliveryOrder);

                if (defaultAddressStoreObject != null && defaultAddressStoreObject.length() > 0) intent.putExtra("defaultAddressStoreObject", defaultAddressStoreObject.toString());
                if (defaultAddressObject != null && defaultAddressObject.length() > 0) intent.putExtra("defaultAddressObject", defaultAddressObject.toString());
                if (storeObject != null) intent.putExtra("storeObject", storeObject.toString());

                intent.putExtra("time", time);
                intent.putExtra("date", date);
                intent.putExtra("isPaai", isPaai);
                intent.putExtra("from", "klikindomaret");

                if (promoProducts != null) intent.putExtra("promoProducts", promoProducts.toString());

                intent.putExtra("salesOrderId", salesOrderid);
                startActivity(intent);
                overridePendingTransition(R.anim.left_in, R.anim.left_out);
            }else if (code.equalsIgnoreCase("disat") || code.equalsIgnoreCase("xltun")){
                isCOD = false;
                LayoutInflater inflater = (LayoutInflater) PaymentActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View addXlIndLayout = null;

                if (addXlIndLayout == null) {
                    addXlIndLayout = inflater.inflate(R.layout.indosat_xl_emoney_layout, null);
                }

                final EditText noHpTxt = (EditText) addXlIndLayout.findViewById(R.id.phone_number_emoney);
                final EditText tokenCode = (EditText)  addXlIndLayout.findViewById(R.id.token_code);
                Button btnTutup = (Button) addXlIndLayout.findViewById(R.id.cancel_xl_indPay);
                Button btnBayar = (Button) addXlIndLayout.findViewById(R.id.submit_xl_indPay);

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(PaymentActivity.this);
                alertDialogBuilder.setView(addXlIndLayout);
                final AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.setCanceledOnTouchOutside(false);
                alertDialog.show();

                btnTutup.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog.hide();
                        stopLoader();
                    }
                });

                btnBayar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (tokenCode.getText().length() < 4){
                            Toast.makeText(PaymentActivity.this, "Kode Token tidak boleh kurang dari 4 digit", Toast.LENGTH_SHORT).show();
                        } else if (noHpTxt.getText().length() < 9){
                            Toast.makeText(PaymentActivity.this, "Nomor Handphone tidak boleh kurang dari 9 digit", Toast.LENGTH_SHORT).show();
                        } else if (!noHpTxt.getText().toString().substring(0,1).equals("0")){
                            Toast.makeText(PaymentActivity.this, "Nomor Handphone harus diawali dengan 0", Toast.LENGTH_SHORT).show();
                        }else {
                            noHPEMoney = noHpTxt.getText().toString();
                            tokenEMoney = tokenCode.getText().toString();
                            checkout();
                        }
                    }
                });
            } else if(code.equals("COD")){
                isCOD = true;
                checkout();
//
//                List<Integer> clockList = new ArrayList<>();
//                String SoDate = paymentArrayList.getJSONObject(0).getString("SoDate");
//                String[] soDateSplit = SoDate.split("T");
//                int[] clocks = {10, 12, 15, 18, 34, 36, 39, 42};
//                int clock = Integer.parseInt(soDateSplit[1].substring(0,2));
//
//                for(int i=0; i<clocks.length; i++){
//                    if (clocks[i]-clock < 24 && clocks[i]-clock > 6){
//                        clockList.add(clocks[i]);
//                    }
//                }
//
//                String[] codPayment = new String[clockList.size()+1];
//                codPayment[0] = "Pilih waktu pengantaran COD";
//
//                for (int i=0; i<clockList.size(); i++){
//                    if (clockList.get(i) <= 18){
//                        Calendar calendar = Calendar.getInstance();
//                        calendar.add(Calendar.DATE, 0);
//                        Date today = calendar.getTime();
//                        String dates = dateFormat.format(today);
//                        String time = null;
//
//                        if (clockList.get(i) == 10){
//                            time = "Pukul 10:00 - 12:00 WIB";
//                        } else if(clockList.get(i) == 12){
//                            time = "Pukul 12:00 - 15:00 WIB";
//                        } else if(clockList.get(i) == 15){
//                            time = "Pukul 15:00 - 18:00 WIB";
//                        } else if(clockList.get(i) == 18){
//                            time = "Pukul 18:00 - 20:00 WIB";
//                        }
//
//                        codPayment[i+1] = dates + ", " + time;
//                    } else {
//                        Calendar calendar = Calendar.getInstance();
//                        calendar.add(Calendar.DATE, 1);
//                        Date today = calendar.getTime();
//                        String dates = dateFormat.format(today);
//                        String time = null;
//
//                        if (clockList.get(i) == 34){
//                            time = "Pukul 10:00 - 12:00 WIB";
//                        } else if(clockList.get(i) == 36){
//                            time = "Pukul 12:00 - 15:00 WIB";
//                        } else if(clockList.get(i) == 39){
//                            time = "Pukul 15:00 - 18:00 WIB";
//                        } else if(clockList.get(i) == 42){
//                            time = "Pukul 18:00 - 20:00 WIB";
//                        }
//
//                        codPayment[i+1] = dates + ", " + time;
//                    }
//                }
//
//                LayoutInflater inflater = (LayoutInflater) PaymentActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//                View addXlIndLayout = null;
//
//                if (addXlIndLayout == null) {
//                    addXlIndLayout = inflater.inflate(R.layout.cod_layout, null);
//                }
//
//                final TextView textPilih = (TextView) addXlIndLayout.findViewById(R.id.text_pilih);
//                final Spinner sendTime = (Spinner)  addXlIndLayout.findViewById(R.id.installment_spinner);
//                Button btnTutup = (Button) addXlIndLayout.findViewById(R.id.cancel_xl_indPay);
//                Button btnBayar = (Button) addXlIndLayout.findViewById(R.id.submit_xl_indPay);
//
//                textPilih.setText("Pilih jam pengiriman");
//                ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, codPayment);
//                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//                sendTime.setAdapter(adapter);
//
//                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(PaymentActivity.this);
//                alertDialogBuilder.setView(addXlIndLayout);
//                final AlertDialog alertDialog = alertDialogBuilder.create();
//                alertDialog.setCanceledOnTouchOutside(false);
//                alertDialog.show();
//
//                btnTutup.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        alertDialog.dismiss();
//                        stopLoader();
//                    }
//                });
//
//                btnBayar.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        if (sendTime.getSelectedItemPosition() == 0){
//                            Toast.makeText(PaymentActivity.this, "Pilih Waktu Pengantaran COD", Toast.LENGTH_SHORT).show();
//                        }else{
//                            String spinnerContent = sendTime.getSelectedItem().toString();
//                            String[] format = spinnerContent.split(",");
//                            DateFormat dateFormat = new SimpleDateFormat("dd MMMM dddd");
//                            DateFormat formatDate = new SimpleDateFormat("yyyy-MM-dd");
//                            Date a = null;
//
//                            try {
//                                a = dateFormat.parse(format[0]);
//                            } catch (ParseException e) {
//                                e.printStackTrace();
//                            }
//
//                            String fd = formatDate.format(a);
//                            String aClock = null;
//
//                            if(format[1].contains("10")){
//                                aClock = "10:00:00";
//                            } else if(format[1].contains("12")){
//                                aClock = "12:00:00";
//                            } else if(format[1].contains("15")){
//                                aClock = "15:00:00";
//                            } else if(format[1].contains("18")){
//                                aClock = "18:00:00";
//                            }
//
//                            shippingTime = fd+"T"+aClock;
//                            checkout();
//
//                            alertDialog.dismiss();
//                        }
//                    }
//                });
            } else{
                checkout();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void checkout(){
        final JSONObject checkOutObject = new JSONObject();

        try {
            JSONObject defaultAddress = new JSONObject(sqLiteHandler.getDefaultAddress());
            System.out.println("Buyer = " + defaultAddress);
            final String regionId = defaultAddress.getString("Region");
            final String regionName = defaultAddress.getString("RegionName");

            if (!isSend){
                if (storeObject.toString().contains("Zipcode")){
                    storeObject.put("ZipCode", storeObject.getString("Zipcode"));
                } else if (storeObject.toString().contains("ZipCode")){
                    storeObject.put("Zipcode", storeObject.getString("ZipCode"));
                }
            }

            if (storeProductObject.length() > 0){
                if(isSend){
                    checkOutObject.put("StoreId", "00000000-0000-0000-0000-000000000000");
                    checkOutObject.put("IsDelivery", true);
                    checkOutObject.put("ShopZipCode", "");
                    checkOutObject.put("DistrictId", defaultAddressStoreObject.getString("DistrictId"));
                    checkOutObject.put("CustomerAddressId", defaultAddressStoreObject.getString("ID"));
                } else {
                    if (storeObject.toString().contains("StoreId")){
                        checkOutObject.put("StoreId", storeObject.getString("StoreId"));
                    }else if (storeObject.toString().contains("ID")){
                        checkOutObject.put("StoreId", storeObject.getString("ID"));
                    }

                    checkOutObject.put("IsDelivery", false);
                    checkOutObject.put("ShopZipCode", storeObject.getString("Zipcode"));
                    checkOutObject.put("DistrictId", storeObject.getString("DistrictId"));
                    checkOutObject.put("CustomerAddressId", "00000000-0000-0000-0000-000000000000");
                }

                clock  = time.split("-");
                stringClock = clock[0].replace(" ","");

                arrayDate = date.replace(",", " ").split(" ");
                stringDate = arrayDate[3]+"-"+dateName.getMonthNumber(arrayDate[2])+"-"+arrayDate[1]+"T00:00:00";
            } else {
                stringClock = "00:00";
                stringDate = "0000-00-00T00:00:00";
            }

            if(isPaai){
                SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
                SimpleDateFormat formatterString = new SimpleDateFormat("yyyy-MM-dd");

                try {
                    if (sendDate.length() > 0 && sendDate != null){
                        Date date = formatter.parse(sendDate);
                        String stringDate2 = formatterString.format(date);

                        checkOutObject.put("IPlazaShippingDate",stringDate2+"T00:00:00");
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }else{
                checkOutObject.put("IStoreShippingDate", "");
            }

            JSONObject promoObject = new JSONObject();
            JSONArray promoList = new JSONArray();
            for (int i=0; i<promoPaymentSelected.length(); i++){
                promoObject.put("promo", promoPaymentSelected.getJSONObject(i).getString("PromoCode"));
                promoObject.put("plu", promoPaymentSelected.getJSONObject(i).getJSONObject("Product").getString("PLU"));
                promoObject.put("qty", promoPaymentSelected.getJSONObject(i).getString("QuantityUpdate"));

                promoList.put(promoObject);
                promoObject = new JSONObject();
            }

            checkOutObject.put("CityId", "00000000-0000-0000-0000-000000000000");
            checkOutObject.put("IStoreShippingDate", stringDate);
            checkOutObject.put("IStoreShippingDateDelivery", stringDate);
            checkOutObject.put("IStoreShippingDateShipping", stringDate);
            checkOutObject.put("IStorePreferTime", stringClock+":00");
            checkOutObject.put("IStorePreferTimeDelivery", stringClock+":00");
            checkOutObject.put("IStorePreferTimeShipping", stringClock+":00");
            checkOutObject.put("ShippingTariff", 0);
            checkOutObject.put("IsHaveInsurance", false);
            checkOutObject.put("PlazaStartEtd", 0);
            checkOutObject.put("PlazaEndEtd", 0);
            checkOutObject.put("WarehouseShippingPartner", "JNE");
            checkOutObject.put("JneTariff", 0);
            checkOutObject.put("RpxTariff", 0);
            checkOutObject.put("IsDeliveryStoreActivated", false);
            checkOutObject.put("IsVirtual", false);
            checkOutObject.put("Address", null);
            checkOutObject.put("CustomerID", sessionManager.getUserID());
            checkOutObject.put("ShoppingCartID", sessionManager.getCartId());
            checkOutObject.put("RegionID", sessionManager.getRegionID());
            checkOutObject.put("PaymentPromoSelected", promoList);
            checkOutObject.put("PaymentType", paymentId);

            if (isIPP){
                checkOutObject.put("IsIPP", true);
                checkOutObject.put("IPPServiceType", isDeliveryOrder ? 3 : 1);

                if (isDeliveryOrder) {
                    if (latitude != null && latitude.length() > 0 && longitude != null && longitude.length() > 0) {
                        checkOutObject.put("IPPCustomerAddressLongitude", longitude);
                        checkOutObject.put("IPPCustomerAddressLatitude", latitude);
                    }

                    checkOutObject.put("IPPCustomerGoogleAddress", infoStore);
                    checkOutObject.put("IPPCustomerKetAlamat", addressTextValue);
                    checkOutObject.put("AddressIDPlazaIPP", mCustomerAddressId);
                    checkOutObject.put("AddressIDPlaza", mCustomerAddressId);
                }else{
                    if(!storeCode.isEmpty()) {
                        checkOutObject.put("IPPStoreCode", storeCode);
                    }
                }
            }

            if(storeProductObject.length() > 0 && isSend) checkOutObject.put("AddressIDStore", defaultAddressStoreObject.getString("ID"));
            else checkOutObject.put("AddressIDStore", "00000000-0000-0000-0000-000000000000");

            if (isIPP && isDeliveryOrder) {
                checkOutObject.put("AddressIDPlaza", mCustomerAddressId);

                if (defaultAddressObject != null && defaultAddressObject.length() > 0) {
                    checkOutObject.put("AddressIDPlazaIPP", defaultAddressObject.getString("ID"));
                    checkOutObject.put("AddressIDPlaza", defaultAddressObject.getString("ID"));
                }
            } else {
                if (nonstoreProductObject.length() > 0){
                    if (isPaai){
                        checkOutObject.put("AddressIDPlaza", mCustomerAddressId);
                        checkOutObject.put("AddressIDPlazaIPP", mCustomerAddressId);
                    }else{
                        if (defaultAddressObject != null && defaultAddressObject.length() > 0)
                            checkOutObject.put("AddressIDPlaza", defaultAddressObject.getString("ID"));
                        else
                            checkOutObject.put("AddressIDPlaza", "00000000-0000-0000-0000-000000000000");
                    }
                }else {
                    checkOutObject.put("AddressIDPlaza", "00000000-0000-0000-0000-000000000000");
                }
            }

            if(!isDeliveryOrder && isPaai){
                checkOutObject.put("AddressIDPlazaIPP", mCustomerAddressId);
            }

            if(regionId.equals(sessionManager.getRegionID())){
                jsonPost(API.getInstance().getApiCheckout() + "?mfp_id=" + sessionManager.getKeyMfpId(), checkOutObject, "checkout");
            } else {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(PaymentActivity.this);
                alertDialogBuilder.setMessage("Perbedaan Kota Tujuan dapat mempengaruhi harga dan ketersediaan produk. Periksa kembali keranjang belanja Anda.");
                alertDialogBuilder.setNegativeButton("Batal", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        stopLoader();
                    }
                });

                alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        sessionManager.setRegionID(regionId);
                        sessionManager.setRegionName(regionName);

                        jsonPost(API.getInstance().getApiCheckout() + "?mfp_id=" + sessionManager.getKeyMfpId(), checkOutObject, "checkout");

                    }
                });

                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.setCanceledOnTouchOutside(false);
                alertDialog.show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
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
                            Toast.makeText(PaymentActivity.this, "Gagal terkoneksi server", Toast.LENGTH_LONG).show();
                        }else{
                            if (type.equals("payment")){
                                try {
                                    setRadioPayment(response.getJSONObject(0));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                paymentArrayList = response;

                                stopLoader();
                            }else if (type.equals("getPayment")){
                                insertPayment(response);
                            }else if (type.equals("cart")){
                                showCart(response);
                            }
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(PaymentActivity.this, "Gagal terkoneksi server", Toast.LENGTH_LONG).show();
                stopLoader();
            }
        }, this);

        jsonArrayReq.setRetryPolicy(new DefaultRetryPolicy(60000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppController.getInstance().addToRequestQueue(jsonArrayReq);
    }

    public void showCart(JSONArray response){
//        totalDiscount = 0.0;
        double totalCoupon = 0.0;
        List<String> coupons = new ArrayList<>();
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
                    couponAdapter = new CouponAdapter(PaymentActivity.this, coupons, "payment");
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
                                case "checkout":
                                    message = response.getString("Message");

                                    if (response.getBoolean("IsSuccess")) {
                                        jsonArrayRequest(API.getInstance().getApiPayment() + "?SalesOrderHeaderId=" + response.getJSONObject("ResponseObject").getString("SalesOrderHeaderId"), "getPayment");
                                    } else {
                                        alertDialogBuilder = new AlertDialog.Builder(PaymentActivity.this);

                                        if (response.getString("Message").contains("belum lunas")) {
                                            alertDialogBuilder.setMessage(message);

                                            alertDialogBuilder.setPositiveButton("Lihat Riwayat Pesanan", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface arg0, int arg1) {
                                                    intent = new Intent(PaymentActivity.this, ProfileActivity.class);
                                                    intent.putExtra("pageindex", "2");
                                                    startActivity(intent);

                                                    finish();
                                                    overridePendingTransition(R.anim.right_out, R.anim.right_in);
                                                }
                                            });
                                            stopLoader();
                                        } else if (response.getString("Message").contains("kesalahan saat cek stok")) {
                                            alertDialogBuilder.setMessage(response.getString("Message"));
                                            alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface arg0, int arg1) {
                                                }
                                            });
                                            stopLoader();
                                        } else if (response.getString("Message").toLowerCase().contains("terverifikasi")) {
                                            alertDialogBuilder.setMessage("Nomor handphone belum terverifikasi, silahkan verifikasi nomor handphone Anda terlebih dahulu.");
                                            alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface arg0, int arg1) {
                                                    finish();
                                                    intent = new Intent(PaymentActivity.this, VerificationPhoneActivity.class);
                                                    intent.putExtra("type", "otpUser");
                                                    startActivity(intent);
                                                    overridePendingTransition(R.anim.left_in, R.anim.left_out);
                                                }
                                            });
                                            stopLoader();
                                        } else if (response.getString("Message").toLowerCase().contains("persediaan")) {
                                            sessionManager.setEmptyProd(response.getJSONObject("ResponseObject").toString());
                                            if (sessionManager.getEmptyProd() != null) {
                                                listProductDetail = new ArrayList<>();
                                                listProductArray = sessionManager.getEmptyProd().split(",");

                                                for (int i = 0; i < listProductArray.length; i++) {
                                                    if (listProductArray[i].contains("false")) {
                                                        String[] listProductPlu = listProductArray[i].split(":");
                                                        listProductDetail.add(listProductPlu[1] + "|" + listProductPlu[2].replace("\"", ""));
                                                    }
                                                }
                                            }

                                            alertDialogBuilder.setMessage(response.getString("Message"));
                                            alertDialogBuilder.setPositiveButton("Tutup", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface arg0, int arg1) {
                                                    intent = new Intent(PaymentActivity.this, CartActivity.class);
                                                    intent.putExtra("listProductDetail", listProductDetail.toString());
                                                    intent.putExtra("checkStok", true);
                                                    if (CartActivity.cartActivity != null) CartActivity.cartActivity.finish();
                                                    if (ShippingActivity.shippingActivity != null) ShippingActivity.shippingActivity.finish();
                                                    finish();
                                                    startActivity(intent);
                                                    overridePendingTransition(R.anim.left_in, R.anim.left_out);
                                                }
                                            });
                                            stopLoader();
                                        } else if (response.getString("Message").contains("TENGGANGWAKTU")) {
                                            stopLoader();
                                            countDownStart(response, "payment");
                                        } else {
                                            alertDialogBuilder.setMessage(response.getString("Message"));
                                            alertDialogBuilder.setPositiveButton("Tutup", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface arg0, int arg1) {
                                                }
                                            });
                                            stopLoader();
                                        }

                                        alertDialog = alertDialogBuilder.create();
                                        alertDialog.show();
                                    }

                                    break;
                                case "insertPayment":
                                    if (response.getString("Message").contains("redirect")) {
                                        String callbackUrl = response.getJSONObject("ResponseObject").getString("CallBackURL");
                                        String transactionCode = response.getJSONObject("ResponseObject").getString("TransactionCode");
                                        sessionManager.setKeyShippingPlaza(0);
                                        sessionManager.setKeyTotalPrice(0);
                                        sessionManager.setKeyTotalVoucher(0);
                                        sessionManager.setKeyTotalDiscount(0);
                                        sessionManager.setKeyTotalShippingCost(0);
                                        sessionManager.setKeyTotalCoupon(0);
                                        sessionManager.setKeyCouponList("");
                                        sessionManager.setkeyDate("");
                                        sessionManager.setKeyTime("");

                                        sessionManager.setKeyShipping(false);
                                        sessionManager.setKeyPlazaShipping(false);

                                        if (callbackUrl == null || callbackUrl.toLowerCase().equals("null") || callbackUrl.equals("")) {
                                            sessionManager.setCartId("00000000-0000-0000-0000-000000000000");

                                            System.out.println("payment transactionCode = " + transactionCode);

                                            intent = new Intent(PaymentActivity.this, ThankyouPageActivity.class);
                                            intent.putExtra("salesOrderId", salesOrderid);
                                            intent.putExtra("transactionCode", transactionCode);
                                            intent.putExtra("from", "pay");
                                            if (CartActivity.cartActivity != null) CartActivity.cartActivity.finish();
                                            if (ShippingActivity.shippingActivity != null) ShippingActivity.shippingActivity.finish();
                                            finish();
                                            startActivity(intent);
                                            overridePendingTransition(R.anim.left_in, R.anim.left_out);
                                        } else {
                                            sessionManager.setCartId("00000000-0000-0000-0000-000000000000");
                                            sessionManager.setkeyStore(null);

                                            System.out.println("callbackUrl = " + callbackUrl);

                                            intent = new Intent(PaymentActivity.this, PaymentThirdPartyActivity.class);
                                            intent.putExtra("callbackUrl", callbackUrl);
                                            if (CartActivity.cartActivity != null) CartActivity.cartActivity.finish();
                                            if (ShippingActivity.shippingActivity != null) ShippingActivity.shippingActivity.finish();
                                            finish();
                                            startActivity(intent);
                                            overridePendingTransition(R.anim.left_in, R.anim.left_out);
                                        }

                                        if (callbackUrl.toLowerCase().contains("faspay") || callbackUrl.toLowerCase().contains("klikindomaret.com")
                                                || callbackUrl.toLowerCase().contains("klikbca.com")) {

                                        } else {

                                        }
                                    } else if (response.getString("Message").contains("DuplicateTrxID")) {
                                        Toast.makeText(PaymentActivity.this, response.getString("Message"), Toast.LENGTH_SHORT).show();
                                        stopLoader();
                                    } else {
                                        if (response.getString("Message").toLowerCase().contains("sudah di proses") || response.getString("Message").toLowerCase().contains("didalam sistem")) {
                                            Toast.makeText(PaymentActivity.this, response.getString("Message"), Toast.LENGTH_SHORT).show();
                                            sessionManager.setCartId("00000000-0000-0000-0000-000000000000");

                                            intent = new Intent(PaymentActivity.this, MainActivity.class);
                                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | IntentCompat.FLAG_ACTIVITY_CLEAR_TASK);
                                            startActivity(intent);
                                            finish();
                                            overridePendingTransition(R.anim.left_in, R.anim.left_out);
                                        } else {
                                            Toast.makeText(PaymentActivity.this, response.getString("Message"), Toast.LENGTH_SHORT).show();
                                        }

                                        stopLoader();
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
                                                    stopLoader();
                                                }
                                            } else {
                                                infoVoucher.setVisibility(View.VISIBLE);
                                                infoVoucher.setText(response.getJSONObject("ResponseObject").getString("keterangan"));
                                                infoVoucher.setTextColor(Color.RED);
                                                voucherText.setText("");
                                                stopLoader();
                                            }
                                        } else {
                                            infoVoucher.setVisibility(View.VISIBLE);
                                            infoVoucher.setText(response.getJSONObject("ResponseObject").getString("keterangan"));
                                            infoVoucher.setTextColor(Color.GREEN);
                                            voucherText.setText("");
                                            reloadCart();
                                            stopLoader();
                                        }
                                    }  catch (ParseException e) {
                                        e.printStackTrace();
                                        stopLoader();
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
                Toast.makeText(PaymentActivity.this, "Gagal terkoneksi server", Toast.LENGTH_LONG).show();
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

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(PaymentActivity.this);
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
        if(requestCode == 3){
            if(resultCode == RESULT_OK){
                try {
                    promoPaymentSelected = new JSONArray();
                    promoPaymentProductCart = new JSONArray();
                    promoPaymentProductCart = new JSONArray(data.getStringExtra("promo"));

                    for (int i=0; i<promoPaymentProductCart.length(); i++){
                        if (promoPaymentProductCart.getJSONObject(i).getBoolean("IsSelectedPromo")){
                            promoPaymentSelected.put(promoPaymentProductCart.getJSONObject(i));
                        }
                    }

                    pricePromo = 0.0;
                    priceWithDiscountPromo = 0.0;
                    totalPricePromo = 0.0;
                    for (int i=0; i<promoPaymentSelected.length(); i++){
                        pricePromo += promoPaymentSelected.getJSONObject(i).getDouble("Price") * promoPaymentSelected.getJSONObject(i).getInt("QuantityUpdate");
                        priceWithDiscountPromo += promoPaymentSelected.getJSONObject(i).getDouble("PriceWithDiscount") * promoPaymentSelected.getJSONObject(i).getInt("QuantityUpdate");
                    }

                    totalPrice = Double.valueOf(sessionManager.getKeyTotalPrice());
                    totalDiscount = Double.valueOf(sessionManager.getKeyTotalDiscount());
                    voucher = Double.valueOf(sessionManager.getKeyTotalVoucher());
                    shippingCost = Double.valueOf(sessionManager.getKeyTotalShippingCost());
                    coupon = Double.valueOf(sessionManager.getKeyTotalCoupon());

                    totalPricePromo = pricePromo - priceWithDiscountPromo;
                    totalDiscountText.setText("Rp " + df.format(totalDiscount + totalPricePromo).replace(",","."));
                    totalTransaction.setText("Rp " + df.format(totalPrice - totalDiscount + shippingCost + (pricePromo - totalPricePromo)  - voucher).replace(",","."));
                    totalShippingCost.setText("Rp " + df.format(shippingCost).replace(",","."));
                    totalVoucher.setText("Rp " + df.format(coupon + voucher).replace(",","."));
                    JSONObject objectPayment = new JSONObject(data.getStringExtra("objectPayment"));

                    setRadioButton(objectPayment, "update");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
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

    @Override
    public void onResume() {
        super.onResume();

        totalPrice = Double.valueOf(sessionManager.getKeyTotalPrice());
        totalDiscount = Double.valueOf(sessionManager.getKeyTotalDiscount());
        voucher = Double.valueOf(sessionManager.getKeyTotalVoucher());
        shippingCost = Double.valueOf(sessionManager.getKeyTotalShippingCost());
        coupon = Double.valueOf(sessionManager.getKeyTotalCoupon());

        totalPricePromo = pricePromo - priceWithDiscountPromo;
        totalDiscountText.setText("Rp " + df.format(totalDiscount + totalPricePromo).replace(",","."));
        totalTransaction.setText("Rp " + df.format(totalPrice - totalDiscount + shippingCost + (pricePromo - totalPricePromo)  - voucher).replace(",","."));
        totalShippingCost.setText("Rp " + df.format(shippingCost).replace(",","."));
        totalVoucher.setText("Rp " + df.format(coupon + voucher).replace(",","."));

        if (sessionManager.getKeyCouponList().length() > 0){
            couponList.setVisibility(View.VISIBLE);
            listCoupon = new ArrayList<>(Arrays.asList(sessionManager.getKeyCouponList().split(",")));
            couponAdapter = new CouponAdapter(PaymentActivity.this, listCoupon, "payment");
            couponList.setAdapter(couponAdapter);
        }else{
            couponList.setVisibility(View.GONE);
        }
    }
}
