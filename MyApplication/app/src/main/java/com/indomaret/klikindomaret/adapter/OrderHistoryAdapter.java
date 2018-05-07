package com.indomaret.klikindomaret.adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.indomaret.klikindomaret.API;
import com.indomaret.klikindomaret.R;
import com.indomaret.klikindomaret.app.AppController;
import com.indomaret.klikindomaret.fragment.OrderHistoryTabbedFragment;
import com.indomaret.klikindomaret.helper.SessionManager;
import com.indomaret.klikindomaret.views.HeightAdjustableListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by indomaretitsd7 on 6/14/16.
 */
public class OrderHistoryAdapter extends BaseAdapter{
    private LayoutInflater inflater;
    private Activity activity;
    private SessionManager sessionManager;
    private List<JSONObject> orderObjectList;
    private JSONObject orderSummary, salesOrder;

    private DecimalFormat df = new DecimalFormat("#,###");
    private Double price, totalPrice;
    private String[] dateSplit;
    private String[] dateString;
    private String status = "";
    private String mode = "";
    private Date date = new Date();
    private SimpleDateFormat dt = new SimpleDateFormat("dd-mm-yyyy");
    private SimpleDateFormat dt2 = new SimpleDateFormat("dd-mm-yyyy");

    private String minDate = "";
    private String maxDate = "";
    private LinearLayout storeContainer, nonStoreContainer, linearLayoutPaai, virtualContainer, imageIPP, linearDelivery, linearPriceInfo, linearStoreTelp, linearNonstoreTelp;
    private HeightAdjustableListView virtualProductList;
    private RecyclerView statusDelivery, listIPP, listStore;
    private TextView nonStoreShipping, storeShipping, summaryDateNonStore, datePaai, metodePayment, metodePaymentIpp, metodePaymentStore,
            salesOrderCode, salesOrderDate, salesOrderTotalPrice, salesOrderStatus, freeText, nonstoreName, nonstoreAddress, nonstoreTelp, nonstorePIN,
            locationText, storeAddressStore, storeTelp, storeText, nonStoreText, nonStoreinfoText;
    private ImageView imageJNE;
    private OrderHistoryTabbedFragment fragment;
    private Button reOrder;

    public OrderHistoryAdapter(Activity activity, List<JSONObject> orderObjectList, String mode, OrderHistoryTabbedFragment fragment){
        this.activity = activity;
        this.orderObjectList = orderObjectList;
        this.mode = mode;
        this.fragment = fragment;
    }

    @Override
    public int getCount() {
        return orderObjectList.size();
    }

    @Override
    public Object getItem(int position) {
        return orderObjectList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @SuppressWarnings("deprecation")
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (inflater == null)
            inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (convertView == null)
            convertView = inflater.inflate(R.layout.fragment_order_history_single, null);

        salesOrder = orderObjectList.get(position);
        sessionManager = new SessionManager(activity);

        salesOrderCode = (TextView) convertView.findViewById(R.id.sales_order_code);
        salesOrderDate = (TextView) convertView.findViewById(R.id.sales_order_date);
        salesOrderTotalPrice = (TextView) convertView.findViewById(R.id.order_total_price);
        salesOrderStatus = (TextView) convertView.findViewById(R.id.order_status);
        reOrder = (Button) convertView.findViewById(R.id.reorder);
        freeText = (TextView) convertView.findViewById(R.id.free_text);
        nonstoreName = (TextView) convertView.findViewById(R.id.nonstore_name);
        nonstoreAddress = (TextView) convertView.findViewById(R.id.nonstore_address);
        nonstorePIN = (TextView) convertView.findViewById(R.id.nonstore_pin);
        locationText = (TextView) convertView.findViewById(R.id.location_text);
        storeAddressStore = (TextView) convertView.findViewById(R.id.store_address_store);
        storeTelp = (TextView) convertView.findViewById(R.id.store_telp);
        storeText = (TextView) convertView.findViewById(R.id.so_toko_text);
        nonStoreText = (TextView) convertView.findViewById(R.id.so_nontoko_text);
        nonStoreinfoText = (TextView) convertView.findViewById(R.id.nonstore_text);
        final TextView detail = (TextView) convertView.findViewById(R.id.detail);

        nonStoreContainer = (LinearLayout) convertView.findViewById(R.id.non_store_container);
        linearLayoutPaai = (LinearLayout) convertView.findViewById(R.id.linearLayout_paai);
        storeContainer = (LinearLayout) convertView.findViewById(R.id.store_container);
        virtualContainer = (LinearLayout) convertView.findViewById(R.id.virtual_container);
        imageIPP = (LinearLayout) convertView.findViewById(R.id.image_ipp);
        linearDelivery = (LinearLayout) convertView.findViewById(R.id.linear_delivery);
        linearPriceInfo = (LinearLayout) convertView.findViewById(R.id.linear_price_info);
        linearNonstoreTelp = (LinearLayout) convertView.findViewById(R.id.linear_nonstore_telp);
        linearStoreTelp = (LinearLayout) convertView.findViewById(R.id.linear_store_telp);
        final LinearLayout linearDetail = (LinearLayout) convertView.findViewById(R.id.linear_detail);

        virtualProductList = (HeightAdjustableListView) convertView.findViewById(R.id.virtual_product_list);
        statusDelivery = (RecyclerView) convertView.findViewById(R.id.status_delivery);
        listIPP = (RecyclerView) convertView.findViewById(R.id.list_ipp);
        listStore = (RecyclerView) convertView.findViewById(R.id.list_store);

        nonStoreShipping = (TextView) convertView.findViewById(R.id.nonstore_shipping);
        storeShipping = (TextView) convertView.findViewById(R.id.store_shipping);
        summaryDateNonStore = (TextView) convertView.findViewById(R.id.summary_date_non_store);
        datePaai = (TextView) convertView.findViewById(R.id.date_paai);
        metodePayment = (TextView) convertView.findViewById(R.id.metode_payment);
        metodePaymentIpp = (TextView) convertView.findViewById(R.id.metode_payment_ipp);
        metodePaymentStore = (TextView) convertView.findViewById(R.id.metode_payment_store);
        nonstoreTelp = (TextView) convertView.findViewById(R.id.nonstore_telp);

        imageJNE = (ImageView) convertView.findViewById(R.id.image_jne);

        try {
            if (mode.equals("4")){
                reOrder.setVisibility(View.GONE);
            }else{
                reOrder.setVisibility(View.VISIBLE);
            }

            detail.setText(Html.fromHtml("<u>Lihat Detail Pesanan</u>"));
            orderSummary = salesOrder.getJSONArray("detailPayment").getJSONObject(0);
            String plazaOrderNumber = "";
            Double plazaCostWarehouse = 0.0;
            Double plazaCostSupplier = 0.0;
            String timePAAI = "";

            List<JSONObject> storeGoods = new ArrayList<>();
            List<JSONObject> plazaGoods = new ArrayList<>();
            List<JSONObject> plazaGoodsIPP = new ArrayList<>();
            List<JSONObject> plazaGoodsPAAI = new ArrayList<>();
            List<JSONObject> virtualGoods = new ArrayList<>();

            dateSplit = salesOrder.getString("SalesOrderDate").split("T");
            dateString = dateSplit[0].split("-");

            salesOrderCode.setText(orderSummary.getJSONObject("Payment").getString("TransactionCode"));
            metodePayment.setText(orderSummary.getString("PaymentTypeName"));

            try {
                date = dt.parse(dateString[2]+"-"+dateString[1]+"-"+dateString[0]);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            if(salesOrder.getString("PaymentStatus") != null && !salesOrder.getString("PaymentStatus").equals("")){
                if (salesOrder.getString("PaymentStatus").equals("0")){
                    if (salesOrder.getJSONArray("detailPayment").getJSONObject(0).getString("PaymentTypeCode").contains("500")){
                        status = "Menunggu Verifikasi";
                    }else{
                        status = "Menunggu Pembayaran";
                    }
                } else if (salesOrder.getString("PaymentStatus").equals("8")){
                    status = "Dibatalkan";
                } else if (salesOrder.getString("PaymentStatus").equals("3")){
                    JSONObject object = salesOrder.getJSONArray("detailPayment").getJSONObject(0).getJSONObject("Payment");
                    if (object.getString("ResponseMapping") == null
                            || object.getString("ResponseMapping").equals("")
                            || object.getString("ResponseMapping").toLowerCase().equals("null")){
                        status = "Gagal";
                    }else{
                        status = object.getString("ResponseMapping");
                    }
                } else if (salesOrder.getString("PaymentStatus").equals("2")){
                    status = "Lunas";
                }
            }

            salesOrderStatus.setText(status);
            salesOrderDate.setText(dt2.format(date));

            price = Double.parseDouble(salesOrder.getString("Total"));

            totalPrice = price;
            salesOrderTotalPrice.setText("Rp " + df.format(totalPrice).replace(",","."));

            if(!orderSummary.getString("IStore").equals("null")){
                JSONArray productGoodArray = orderSummary.getJSONObject("IStore").getJSONArray("ItemDetail");
                for (int i=0; i<productGoodArray.length(); i++){
                    if(productGoodArray.getJSONObject(i).getString("PLU").equals("10036492")){
                        System.out.println("Sub Total = "+productGoodArray.getJSONObject(i));
                    } else {
                        storeGoods.add(productGoodArray.getJSONObject(i));
                    }
                }
            }

            if(!orderSummary.getString("IPlaza").equals("null")){
                JSONArray productGoodArray = orderSummary.getJSONObject("IPlaza").getJSONArray("ItemDetail");
                plazaOrderNumber = orderSummary.getJSONObject("IPlaza").getString("SalesOrderNo");
                plazaCostWarehouse = orderSummary.getJSONObject("IPlaza").getDouble("ShippingCostWarehouse");
                plazaCostSupplier = orderSummary.getJSONObject("IPlaza").getDouble("ShippingCostSupplier");
                minDate = orderSummary.getJSONObject("IPlaza").getString("PlazaMinETD");
                maxDate = orderSummary.getJSONObject("IPlaza").getString("PlazaMaxETD");

                for (int i=0; i<productGoodArray.length(); i++){
                    if (productGoodArray.getJSONObject(i).getString("ShippingPartner").toLowerCase().equals("ipp")){
                        plazaGoodsIPP.add(productGoodArray.getJSONObject(i));
                    }else{
                        if (productGoodArray.getJSONObject(i).getBoolean("IsUseNote")){
                            plazaGoodsPAAI.add(productGoodArray.getJSONObject(i));
                            timePAAI = orderSummary.getJSONObject("IPlaza").getString("WaktuPengirimanSupplier");
                        }else {
                            plazaGoods.add(productGoodArray.getJSONObject(i));
                        }
                    }
                }
            }

            if(!orderSummary.getString("IVirtual").equals("null")){
                JSONArray productGoodArray = orderSummary.getJSONObject("IVirtual").getJSONArray("ItemDetail");
                for (int i=0; i<productGoodArray.length(); i++){
                    virtualGoods.add(productGoodArray.getJSONObject(i));
                }
            }

            if(storeGoods.size() > 0) {
                storeContainer.setVisibility(View.VISIBLE);
                String pengiriman = "";
                boolean isApka = false;
                double subTotal = 0.0;

                storeText.setText("Pesanan Produk Toko (" + orderSummary.getJSONObject("IStore").getString("SalesOrderNo") + ")");
                if (orderSummary.getJSONObject("IStore").getBoolean("IsDelivery")){
                    linearStoreTelp.setVisibility(View.VISIBLE);
                    pengiriman = "Kirim ke Alamat";

                    locationText.setText("Alamat Kirim : ");
                    storeAddressStore.setText(orderSummary.getJSONObject("IStore").getString("CustomerAddress") + "\n"
                            + orderSummary.getJSONObject("IStore").getString("CustomerAddress2") + "\n"
                            + orderSummary.getJSONObject("IStore").getString("CustomerAddress3"));
                    storeTelp.setText(orderSummary.getJSONObject("IStore").getString("ReceiverPhone"));
                }else{
                    linearStoreTelp.setVisibility(View.GONE);
                    pengiriman = "Ambil di Toko";

                    locationText.setText("Lokasi dan Alamat Pengambilan : ");
                    storeAddressStore.setText(orderSummary.getJSONObject("IStore").getString("AddressFull"));
                }

                for (int i=0; i<orderSummary.getJSONObject("IStore").getJSONArray("ItemDetail").length(); i++){
                    if (orderSummary.getJSONObject("IStore").getJSONArray("ItemDetail").getJSONObject(i).getString("PLU").equals("10036492")){
                        isApka = true;
                        subTotal = orderSummary.getJSONObject("IStore").getJSONArray("ItemDetail").getJSONObject(i).getDouble("SubTotal");
                    }
                }

                if (isApka){
                    storeShipping.setText("Rp " + df.format(subTotal).replace(",", "."));
                }else{
                    storeShipping.setText("Rp 0");
                }

                listStore.setHasFixedSize(true);
                listStore.setLayoutManager(new LinearLayoutManager(activity));
                ListIPPAdapter listIPPAdapter = new ListIPPAdapter(activity, orderSummary.getJSONObject("IStore").getJSONArray("ItemDetail"));
                listStore.setAdapter(listIPPAdapter);

                metodePaymentStore.setText(pengiriman);
            } else {
                storeContainer.setVisibility(View.GONE);
            }

            if (plazaGoodsIPP.size() > 0 || plazaGoods.size() > 0){
                nonStoreContainer.setVisibility(View.VISIBLE);
                String pengiriman = "";

                if (orderSummary.getJSONObject("IPlaza").getBoolean("IsDelivery")){
                    pengiriman = "Kirim ke Alamat";
                }else{
                    pengiriman = "Ambil di Toko";
                }

                metodePaymentIpp.setText(pengiriman);

                listIPP.setHasFixedSize(true);
                listIPP.setLayoutManager(new LinearLayoutManager(activity));
                ListIPPAdapter listIPPAdapter = new ListIPPAdapter(activity, orderSummary.getJSONObject("IPlaza").getJSONArray("ItemDetail"));
                listIPP.setAdapter(listIPPAdapter);

                if(plazaGoodsIPP.size() > 0){
                    imageJNE.setVisibility(View.GONE);
                    imageIPP.setVisibility(View.VISIBLE);
                    linearDelivery.setVisibility(View.VISIBLE);
                    freeText.setVisibility(View.VISIBLE);

                    nonStoreText.setText("Pesanan Produk Non Toko (" + orderSummary.getJSONObject("IPlaza").getString("SalesOrderNo") + ")");
                    if (orderSummary.getJSONObject("IPlaza").getJSONArray("HistoryStatus").length() > 0){
                        linearDelivery.setVisibility(View.VISIBLE);
                        statusDelivery.setHasFixedSize(true);
                        statusDelivery.setLayoutManager(new LinearLayoutManager(activity));
                        InfoDeliveryAdapter infoDeliveryAdapter = new InfoDeliveryAdapter(activity, orderSummary.getJSONObject("IPlaza").getJSONArray("HistoryStatus"));
                        statusDelivery.setAdapter(infoDeliveryAdapter);
                    }else{
                        linearDelivery.setVisibility(View.GONE);
                    }

                    if (orderSummary.getJSONObject("IPlaza").getBoolean("IsDelivery")){
                        nonstoreName.setVisibility(View.GONE);
                        nonstorePIN.setVisibility(View.GONE);
                        linearNonstoreTelp.setVisibility(View.VISIBLE);
                        nonStoreinfoText.setText("Alamat Kirim : ");

                        nonstoreAddress.setText(orderSummary.getJSONObject("IPlaza").getString("CustomerAddress") + "\n"
                                + orderSummary.getJSONObject("IPlaza").getString("CustomerAddress2") + "\n"
                                + orderSummary.getJSONObject("IPlaza").getString("CustomerAddress3"));
                        nonstoreTelp.setText(orderSummary.getJSONObject("IPlaza").getString("ReceiverPhone"));
                    }else{
                        nonstoreName.setVisibility(View.VISIBLE);
                        nonstorePIN.setVisibility(View.VISIBLE);
                        linearNonstoreTelp.setVisibility(View.GONE);
                        nonStoreinfoText.setText("Lokasi dan Alamat Pengambilan : ");

                        nonstoreName.setText(orderSummary.getJSONObject("IPlaza").getString("IPPStoreName"));
                        nonstoreAddress.setText(orderSummary.getJSONObject("IPlaza").getString("IPPStoreStreet"));

                        if (orderSummary.getJSONObject("IPlaza").getString("PINIndoPaket") != null && orderSummary.getJSONObject("IPlaza").getString("PINIndoPaket").length() > 0
                                && !orderSummary.getJSONObject("IPlaza").getString("PINIndoPaket").toLowerCase().equals("null")){
                            nonstorePIN.setVisibility(View.VISIBLE);
                            nonstorePIN.setText("Kode PIN Pengambilan : " + orderSummary.getJSONObject("IPlaza").getString("PINIndoPaket"));
                        }else{
                            nonstorePIN.setVisibility(View.GONE);
                        }
                    }

                    nonStoreShipping.setText("Rp 0");
                    summaryDateNonStore.setText("2 - 5 hari");
                }else if(plazaGoods.size() > 0){
                    imageJNE.setVisibility(View.VISIBLE);
                    imageIPP.setVisibility(View.GONE);
                    linearDelivery.setVisibility(View.GONE);
                    freeText.setVisibility(View.GONE);

                    nonStoreText.setText("Pesanan Produk Non Toko (" + orderSummary.getJSONObject("IPlaza").getString("SalesOrderNo") + ")");

                    if (orderSummary.getJSONObject("IPlaza").getBoolean("IsDelivery")){
                        nonstoreName.setVisibility(View.GONE);
                        nonstorePIN.setVisibility(View.GONE);
                        linearNonstoreTelp.setVisibility(View.VISIBLE);
                        nonStoreinfoText.setText("Alamat Kirim : ");

                        nonstoreName.setText(orderSummary.getJSONObject("IPlaza").getString("CustomerAddress") + "\n"
                                + orderSummary.getJSONObject("IPlaza").getString("CustomerAddress2") + "\n"
                                + orderSummary.getJSONObject("IPlaza").getString("CustomerAddress3"));
                        nonstoreTelp.setText(orderSummary.getJSONObject("IPlaza").getString("ReceiverPhone"));
                    }else{
                        nonstoreName.setVisibility(View.VISIBLE);
                        nonstorePIN.setVisibility(View.VISIBLE);
                        linearNonstoreTelp.setVisibility(View.GONE);
                        nonStoreinfoText.setText("Lokasi dan Alamat Pengambilan : ");

                        nonstoreName.setText(orderSummary.getJSONObject("IPlaza").getString("AddressFull"));
                        nonstoreAddress.setText("");
                        nonstorePIN.setText("");
                    }

                    nonStoreShipping.setText("Rp " + df.format(plazaCostWarehouse + plazaCostSupplier).replace(",", "."));
                    summaryDateNonStore.setText(minDate + " - " + maxDate + " hari");
                }
            }  else {
                nonStoreContainer.setVisibility(View.GONE);
            }

            if(plazaGoodsPAAI.size() > 0) {
                nonStoreContainer.setVisibility(View.VISIBLE);
                linearLayoutPaai.setVisibility(View.VISIBLE);
                linearDelivery.setVisibility(View.GONE);

                nonStoreShipping.setText("Rp " + df.format(plazaCostWarehouse + plazaCostSupplier).replace(",", "."));
                datePaai.setText(timePAAI.split("T")[0]);
            } else {
                linearLayoutPaai.setVisibility(View.GONE);
            }

            if(virtualGoods.size() > 0) {
                virtualProductList.setAdapter(new SummaryProductAdapter(activity, virtualGoods));
                virtualContainer.setVisibility(View.VISIBLE);
            }else{
                virtualContainer.setVisibility(View.GONE);
            }

        } catch (JSONException e) {
            e.printStackTrace();
            fragment.stopLoading();
        }

        linearPriceInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View convertView = null;

                if (convertView == null) {
                    convertView = inflater.inflate(R.layout.dialog_info, null);
                }

                TextView inputArea = (TextView) convertView.findViewById(R.id.info);
                Button btnClose = (Button) convertView.findViewById(R.id.btn_close);
                StringBuilder infoPrice = new StringBuilder();

                try {
                    if (salesOrder.getString("PaymentStatus").equals("8")){
                        if (salesOrder.getString("TotalRefund") != null &&
                                !salesOrder.getString("TotalRefund").toLowerCase().equals("null") &&
                                salesOrder.getDouble("TotalRefund") > 0.0){
                            infoPrice.append("Total Refund : Rp " + df.format(salesOrder.getDouble("TotalRefund")).replace(",", "."));
                        }else{
                            infoPrice.append("Total Refund : Rp 0");
                        }

                        System.out.println("--- infoPrice : "+infoPrice);
                        if (salesOrder.getString("TotalVoucher") != null &&
                                !salesOrder.getString("TotalVoucher").toLowerCase().equals("null") &&
                                salesOrder.getDouble("TotalVoucher") > 0.0){
                            infoPrice.append("\nVoucher Belanja : Rp " + df.format(salesOrder.getDouble("TotalVoucher")).replace(",", "."));
                        }else{
                            infoPrice.append("\nVoucher Belanja : Rp 0");
                        }
                    }else{
                        if (salesOrder.getString("TotalOngkir") != null &&
                                !salesOrder.getString("TotalOngkir").toLowerCase().equals("null") &&
                                salesOrder.getDouble("TotalOngkir") > 0.0){
                            infoPrice.append("Total Ongkos Kirim : Rp " + df.format(salesOrder.getDouble("TotalOngkir")).replace(",", "."));
                        }else{
                            infoPrice.append("Total Ongkos Kirim : Rp 0");
                        }

                        System.out.println("--- infoPrice : "+infoPrice);
                        if (salesOrder.getString("TotalVoucher") != null &&
                                !salesOrder.getString("TotalVoucher").toLowerCase().equals("null") &&
                                salesOrder.getDouble("TotalVoucher") > 0.0){
                            infoPrice.append("\nVoucher Belanja : Rp " + df.format(salesOrder.getDouble("TotalVoucher")).replace(",", "."));
                        }else{
                            infoPrice.append("\nVoucher Belanja : Rp 0");
                        }

                        System.out.println("--- infoPrice : "+infoPrice);
                        if (salesOrder.getString("TotalDiscount") != null &&
                                !salesOrder.getString("TotalDiscount").toLowerCase().equals("null") &&
                                salesOrder.getDouble("TotalDiscount") > 0.0){
                            infoPrice.append("\nAnda Menghemat : Rp " + df.format(salesOrder.getDouble("TotalDiscount")).replace(",", "."));
                        }else{
                            infoPrice.append("\nAnda Menghemat : Rp 0");
                        }
                        System.out.println("--- infoPrice : "+infoPrice);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                System.out.println("--- infoPrice : "+infoPrice);
                inputArea.setText(infoPrice);

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(activity);
                alertDialogBuilder.setView(convertView);
                final AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                alertDialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
                alertDialog.setCanceledOnTouchOutside(false);
                alertDialog.show();

                btnClose.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        alertDialog.dismiss();
                    }
                });
            }
        });

        detail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (linearDetail.getVisibility() == View.VISIBLE){
                    linearDetail.setVisibility(View.GONE);
                    detail.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.arrow_down, 0);
                }else{
                    linearDetail.setVisibility(View.VISIBLE);
                    detail.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.arrow_up, 0);
                }
            }
        });

        reOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    fragment.runLoading();
                    jsonArrayGet(API.getInstance().getApiReorder()
                            + "?SalesOrderId=" + orderSummary.getJSONObject("Payment").getJSONArray("SalesOrders").getJSONObject(0).getString("ID")
                            + "&RegionId=" + sessionManager.getRegionID() + "&mfp_id=" + sessionManager.getKeyMfpId(), "reorder");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        return convertView;
    }

    public void jsonArrayGet(String urlJsonObj, final String type) {
        System.out.println("summary = " + urlJsonObj);

        JsonArrayRequest jsonArrayReq = new JsonArrayRequest(urlJsonObj,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        if (response == null || response.length() == 0){
                            Toast.makeText(activity, "Gagal terkoneksi server", Toast.LENGTH_LONG).show();
                        }else{
                            if (type.equals("reorder")) {
                                processReorder(response);
                            }
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(activity, "Gagal terkoneksi server", Toast.LENGTH_LONG).show();
            }
        }, activity);

        AppController.getInstance().addToRequestQueue(jsonArrayReq);
    }

    public void processReorder(JSONArray response) {
        fragment.stopLoading();
        System.out.println("reorder = " + response);

        try {
            sessionManager.setCartId(response.getJSONObject(0).getString("ResponseID"));
            android.support.v7.app.AlertDialog.Builder alertDialogBuilder = new android.support.v7.app.AlertDialog.Builder(activity);
            alertDialogBuilder.setTitle("KlikIndomaret");
            alertDialogBuilder.setMessage(response.getJSONObject(0).getString("Message"));
            alertDialogBuilder.setNegativeButton("Tutup", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                }
            });

            android.support.v7.app.AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}