package com.indomaret.klikindomaret.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.indomaret.klikindomaret.API;
import com.indomaret.klikindomaret.R;
import com.indomaret.klikindomaret.adapter.OrderHistoryAdapter;
import com.indomaret.klikindomaret.adapter.OrderStatusAdapter;
import com.indomaret.klikindomaret.app.AppController;
import com.indomaret.klikindomaret.helper.Month;
import com.indomaret.klikindomaret.helper.SessionManager;
import com.indomaret.klikindomaret.views.HeightAdjustableListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class OrderStatusFragment extends Fragment {
    private View view;
    EditText orderCode;
    Button searchStatusOrder;
    LinearLayout threeContainer;
    HeightAdjustableListView statusListView;
    LinearLayout orderStatusBullet;

    TextView statusOrderCode, orderDate, orderTime, orderCancel;
    TextView orderPayment, paymentTransactionMethod, paymentTransactionCode, paymentDate, paymentTime, paymentTotal;
    TextView processedOrder, processNonstoreInfo, processShippingMethod, processShippingTimeVirtual, processShippingTime, processStoreInfo, processStoreShippingDate, processStoreShippingTime, processStoreSender, processStoreSenderAddress;
    TextView orderAccept, accepter, accepterPhone, accepterDate, awbNumber;
    ImageView bullet2, bullet3, bullet4;

    SessionManager sessionManager;
    Month month = new Month();
    OrderStatusAdapter orderStatusAdapter;
    OrderHistoryAdapter orderHistoryAdapter;
    Boolean isDelivery;
    String storeId;
    String shippingStatus = "";
    String[] shippingDatesSend;
    String shippingDateSend;

    SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy");
    List<JSONObject> statusOrder;
    JSONObject singleStatusOrder;
    int currentPage = 1;
    int pageSize = 20;

    DecimalFormat df = new DecimalFormat("#,###");

    public OrderStatusFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        view = inflater.inflate(R.layout.fragment_order_status, container, false);
        sessionManager = new SessionManager(getActivity());

        orderCode = (EditText) view.findViewById(R.id.order_code);
        threeContainer = (LinearLayout) view.findViewById(R.id.three_container);
        statusListView = (HeightAdjustableListView) view.findViewById(R.id.three_listview);
        searchStatusOrder = (Button) view.findViewById(R.id.search_order_status);
        orderStatusBullet = (LinearLayout) view.findViewById(R.id.order_status_bullet);

        statusOrderCode = (TextView) view.findViewById(R.id.order_number);
        orderDate = (TextView) view.findViewById(R.id.order_date);
        orderTime = (TextView) view.findViewById(R.id.order_time);
        orderCancel = (TextView) view.findViewById(R.id.order_cancel);

        orderPayment = (TextView) view.findViewById(R.id.order_payment);
        paymentTransactionMethod = (TextView) view.findViewById(R.id.payment_transaction_method);
        paymentTransactionCode = (TextView) view.findViewById(R.id.payment_transaction_code);
        paymentDate = (TextView) view.findViewById(R.id.payment_date);
        paymentTime = (TextView) view.findViewById(R.id.payment_time);
        paymentTotal = (TextView) view.findViewById(R.id.payment_total);

        processedOrder = (TextView) view.findViewById(R.id.processed_order);
        processNonstoreInfo = (TextView) view.findViewById(R.id.process_nonstore_info);
        processShippingMethod = (TextView) view.findViewById(R.id.process_shipping_method);
        processShippingTime = (TextView) view.findViewById(R.id.process_shipping_time);
        processShippingTimeVirtual = (TextView) view.findViewById(R.id.process_shipping_time_virtual);
        processStoreInfo = (TextView) view.findViewById(R.id.process_store_info);
        processStoreShippingDate =(TextView) view.findViewById(R.id.process_store_shipping_date);
        processStoreShippingTime = (TextView) view.findViewById(R.id.process_store_shipping_time);
        processStoreSender = (TextView) view.findViewById(R.id.process_store_sender);
        processStoreSenderAddress = (TextView) view.findViewById(R.id.process_store_sender_address);

        orderAccept = (TextView) view.findViewById(R.id.summary_order_accept);
        accepter = (TextView) view.findViewById(R.id.summary_order_accepter);
        accepterPhone = (TextView) view.findViewById(R.id.summary_order_accepter_phone);
        accepterDate = (TextView) view.findViewById(R.id.summary_order_accepter_date);
        awbNumber = (TextView) view.findViewById(R.id.awb_number);

        bullet2 = (ImageView) view.findViewById(R.id.bullet02);
        bullet3 = (ImageView) view.findViewById(R.id.bullet03);
        bullet4 = (ImageView) view.findViewById(R.id.bullet04);

        orderCode.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String code = orderCode.getText().toString();

                        if(code.length() == 0){
                            jsonArrayGet(API.getInstance().getApiThreeSalesOrder()+"?customerID=" + sessionManager.getUserID() +"&mfp_id="+sessionManager.getKeyMfpId(), "three");
                        }
                     }
                }
        );

        searchStatusOrder.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String code = orderCode.getText().toString();

                        if(code.length() > 0){
                            jsonArrayGet(API.getInstance().getApiSearchStatusOrder()+sessionManager.getUserID()+"?no="+code+"&currPage="+currentPage+"&pageSize="+pageSize+"&mode="+0+"&mfp_id="+sessionManager.getKeyMfpId(), "search");
                        }
                    }
                }
        );

        statusListView.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        try {
                            jsonArrayGet(API.getInstance().getApiStatusOrderById()+"?salesOrderId="+statusOrder.get(position).getString("ID")+"&mfp_id="+sessionManager.getKeyMfpId(), "single");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
        );

        return view;
    }

    public void jsonArrayGet(String urlJsonObj, final String type){
        System.out.println("Url No AWB = "+urlJsonObj);
        System.out.println("type order status : "+type);

        JsonArrayRequest jsonArrayReq = new JsonArrayRequest(urlJsonObj,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        if(type.equals("three")){
                            processThreeResponse(response);
                        } else if(type.equals("search")){
                            processSearch(response);
                        } else if(type.equals("single")){
                            processSingleOrderStatus(response);
                        } else if(type.equals("store")){
                            processStore(response);
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getActivity(), "Gagal terkoneksi server", Toast.LENGTH_LONG).show();
                threeContainer.setVisibility(View.GONE);
            }
        }, getActivity());
        AppController.getInstance().addToRequestQueue(jsonArrayReq);
    }

    public void processThreeResponse(JSONArray response){
        statusOrder = new ArrayList<>();

        if(response.length() > 0){
            for (int i=0; i<response.length(); i++){
                try {
                    statusOrder.add(response.getJSONObject(i));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            orderStatusAdapter = new OrderStatusAdapter(getActivity(), statusOrder);
            statusListView.setAdapter(orderStatusAdapter);

            threeContainer.setVisibility(View.VISIBLE);
            orderStatusBullet.setVisibility(View.GONE);
        } else {
            threeContainer.setVisibility(View.GONE);
        }
    }

    public void processSearch(JSONArray response){
        statusOrder = new ArrayList<>();

        if(response.length() > 0){
            for (int i=0; i<response.length(); i++){
                try {
                    if (response.getJSONObject(i).getInt("PaymentStatus") != 8){
                        statusOrder.add(response.getJSONObject(i));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            orderHistoryAdapter = new OrderHistoryAdapter (getActivity(), statusOrder, "", null);
            statusListView.setAdapter(orderHistoryAdapter);

            threeContainer.setVisibility(View.VISIBLE);
            orderStatusBullet.setVisibility(View.GONE);
        } else {
            threeContainer.setVisibility(View.GONE);
        }
    }

    public void processSingleOrderStatus(JSONArray response){
        threeContainer.setVisibility(View.GONE);
        orderStatusBullet.setVisibility(View.VISIBLE);

        try {
            singleStatusOrder = response.getJSONObject(0);

            //Bullet 1
            statusOrderCode.setText("Nomor Pesanan : " + singleStatusOrder.getString("SalesOrderNo"));

            String[] orderDates = singleStatusOrder.getString("SalesOrderDate").split("T");
            String[] orderSingleDates = orderDates[0].split("-");
            String[] orderSingleTime = orderDates[1].split(":");

            orderDate.setText("Tanggal : " + orderSingleDates[2]+" "+month.getMonth(orderSingleDates[1])+" "+orderSingleDates[0]);
            orderTime.setText("Waktu : " + orderSingleTime[0]+":"+orderSingleTime[1] + " WIB");

            String paymentStatus = singleStatusOrder.getString("PaymentStatus");
            if(paymentStatus.equals("8")){
                orderCancel.setVisibility(View.VISIBLE);
            } else {
                orderCancel.setVisibility(View.GONE);
            }

            //Bullet 2
            if(singleStatusOrder.getString("PaymentStatus").equals("2")){
                JSONObject payments = singleStatusOrder.getJSONArray("Payments").getJSONObject(0);

                bullet2.setImageResource(R.drawable.icon_circle_blue);
                orderPayment.setTextColor(Color.parseColor("#0079C2"));

                paymentTransactionMethod.setText("Metode Pembayaran : " + payments.getJSONObject("PaymentType").getString("Name"));
                paymentTransactionMethod.setVisibility(View.VISIBLE);
                paymentTransactionCode.setText("Kode Transaksi : " + payments.getString("TransactionCode"));
                paymentTransactionCode.setVisibility(View.VISIBLE);

                String[] paymentDates = payments.getString("PaymentDate").split("T");
                String[] paymentDateBreak = paymentDates[0].split("-");
                String[] paymentTimeBreak = paymentDates[1].split(":");

                paymentDate.setText("Tanggal : " + paymentDateBreak[2] + "-" + month.getMonth(paymentDateBreak[1]) + "-" + paymentDateBreak[0]);
                paymentDate.setVisibility(View.VISIBLE);
                paymentTime.setText("Waktu : " + paymentTimeBreak[0] + ":" + paymentTimeBreak[1] + "WIB");
                paymentTime.setVisibility(View.VISIBLE);

                Double totalPrice = Double.valueOf(singleStatusOrder.getString("Total"));

                paymentTotal.setText("Total : Rp " + df.format(totalPrice).replace(",","."));
                paymentTotal.setVisibility(View.VISIBLE);

                //Bullet 3
                JSONArray salesOrderDetails = singleStatusOrder.getJSONArray("SalesOrderDetails");

                bullet3.setImageResource(R.drawable.icon_circle_blue);
                processedOrder.setTextColor(Color.parseColor("#0079C2"));

                for (int i=0; i<salesOrderDetails.length(); i++){
                    if (salesOrderDetails.getJSONObject(i).getString("IsDelivery").equals("") ||
                            salesOrderDetails.getJSONObject(i).getString("IsDelivery").equals("null")){
                        isDelivery = false;
                    }else {
                        isDelivery = true;
                    }

                    if (shippingStatus.length() == 0 || shippingStatus.equals("null")){
                        shippingDatesSend = salesOrderDetails.getJSONObject(i).getString("ShippingStatusDate").split("T");
                        shippingDateSend = shippingDatesSend[0];

                        shippingStatus = salesOrderDetails.getJSONObject(i).getString("ShippingStatus");
                        orderSingleDates = shippingDateSend.split("-");
                    }

                    if(salesOrderDetails.getJSONObject(i).getJSONObject("Product").getString("ProductFlag").toLowerCase().equals("store")){
                        storeId = salesOrderDetails.getJSONObject(i).getString("StoreId");
                    }
                }

                for (int i=0; i<salesOrderDetails.length(); i++){
                    if(salesOrderDetails.getJSONObject(i).getJSONObject("Product").getString("ProductFlag").toLowerCase().equals("plaza")){
                        if(salesOrderDetails.getJSONObject(i).getJSONObject("Product").getBoolean("IsVirtual")){
                            processNonstoreInfo.setVisibility(View.GONE);
                            processShippingMethod.setVisibility(View.GONE);
                            processShippingTimeVirtual.setVisibility(View.VISIBLE);
                            awbNumber.setVisibility(View.GONE);
                        } else {
                            processNonstoreInfo.setVisibility(View.VISIBLE);
//                            processShippingMethod.setText("Metode Pengiriman : " + salesOrderDetails.getJSONObject(i).getString("ShippingPartner"));
                            processShippingMethod.setVisibility(View.VISIBLE);
                            processShippingMethod.setText("Metode Pengiriman : JNE");
                            processShippingTime.setVisibility(View.VISIBLE);

                            if (salesOrderDetails.getJSONObject(0).getString("NoAwb").length() > 0 &&
                                    !salesOrderDetails.getJSONObject(0).getString("NoAwb").equals("null")){
                                awbNumber.setText("No. AWB : "+salesOrderDetails.getJSONObject(0).getString("NoAwb"));
                                awbNumber.setVisibility(View.VISIBLE);
                            } else {
                                awbNumber.setVisibility(View.GONE);
                            }
                        }
                    } else if(salesOrderDetails.getJSONObject(i).getJSONObject("Product").getString("ProductFlag").toLowerCase().equals("store")) {
                        jsonArrayGet(API.getInstance().getApiStoreById()+storeId+"?mfp_id=" + sessionManager.getKeyMfpId(), "store");

                        processStoreInfo.setVisibility(View.VISIBLE);

                        String[] shippingDates = salesOrderDetails.getJSONObject(i).getString("ShippingDate").split("T");
                        String[] shippingDate = shippingDates[0].split("-");
                        String[] shippingTimes = salesOrderDetails.getJSONObject(i).getString("ShippingPreferTime").split(":");

                        if(isDelivery){
                            processNonstoreInfo.setText("Barang Dikirim dari Toko");
                            processStoreShippingDate.setText("Tanggal pengiriman : " + shippingDate[2]+"-"+shippingDate[1]+"-"+shippingDate[0]);
                            processStoreShippingDate.setVisibility(View.VISIBLE);
                            awbNumber.setVisibility(View.GONE);
                        } else {
                            processNonstoreInfo.setText("Barang Diproses di Toko");
                            processStoreShippingDate.setText("Tanggal pengambilan : " + shippingDate[2]+"-"+shippingDate[1]+"-"+shippingDate[0]);
                            processStoreShippingDate.setVisibility(View.VISIBLE);
                            awbNumber.setVisibility(View.GONE);
                        }

                        processStoreShippingTime.setText("Waktu : " + shippingTimes[0]+":"+shippingTimes[1]+" WIB");
                        processStoreShippingTime.setVisibility(View.VISIBLE);
                    }
                }

                //Bullet 4
                if(shippingStatus.equals("Completed") || shippingStatus.equals("KIRIM") || shippingStatus.equals("1.2.3.3") || shippingStatus.equals("1.2.2.3")){
                    bullet4.setImageResource(R.drawable.icon_circle_blue);
                    orderAccept.setTextColor(Color.parseColor("#0079C2"));

                    if(isDelivery){
                        accepter.setText("Nama Penerima : " + singleStatusOrder.getString("CustomerContactName"));
                        accepter.setVisibility(View.VISIBLE);
                    } else {
                        accepter.setText("Nama Pelanggan : " + singleStatusOrder.getString("CustomerContactName"));
                        accepter.setVisibility(View.VISIBLE);
                    }

                    accepterPhone.setText("No. Telp : " + singleStatusOrder.getString("CustomerPhone"));
                    accepterPhone.setVisibility(View.VISIBLE);
                    accepterDate.setText("Tanggal : " + orderSingleDates[2]+" "+month.getMonth(orderSingleDates[1])+" "+orderSingleDates[0]);
                    accepterDate.setVisibility(View.VISIBLE);
                }
            }else {
                bullet2.setImageResource(R.drawable.icon_circle_grey);
                bullet3.setImageResource(R.drawable.icon_circle_grey);
                bullet4.setImageResource(R.drawable.icon_circle_grey);

                orderPayment.setTextColor(Color.parseColor("#8d8d8d"));
                processedOrder.setTextColor(Color.parseColor("#8d8d8d"));
                orderAccept.setTextColor(Color.parseColor("#8d8d8d"));

                paymentTransactionMethod.setVisibility(View.GONE);
                paymentTransactionCode.setVisibility(View.GONE);
                paymentDate.setVisibility(View.GONE);
                paymentTime.setVisibility(View.GONE);
                paymentTotal.setVisibility(View.GONE);

                processNonstoreInfo.setVisibility(View.GONE);
                processShippingMethod.setVisibility(View.GONE);
                processShippingTime.setVisibility(View.GONE);
                processStoreInfo.setVisibility(View.GONE);
                processStoreShippingDate.setVisibility(View.GONE);
                processStoreShippingTime.setVisibility(View.GONE);
                processStoreSender.setVisibility(View.GONE);

                accepter.setVisibility(View.GONE);
                accepterPhone.setVisibility(View.GONE);
            }

            System.out.println("status = " + singleStatusOrder.getString("PaymentStatus"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void processStore(JSONArray response){
        try {
            if(isDelivery){
                processStoreSender.setVisibility(View.GONE);
            } else {
                processStoreSender.setText(response.getJSONObject(0).getString("Name"));
                processStoreSender.setVisibility(View.VISIBLE);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
