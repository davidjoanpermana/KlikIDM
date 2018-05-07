package com.indomaret.klikindomaret.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.indomaret.klikindomaret.API;
import com.indomaret.klikindomaret.R;
import com.indomaret.klikindomaret.activity.HomeKAIActivity;
import com.indomaret.klikindomaret.activity.ListTicketKAIActivity;
import com.indomaret.klikindomaret.helper.Encode2;
import com.indomaret.klikindomaret.helper.SessionManager;
import com.indomaret.klikindomaret.views.HeightAdjustableListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by indomaretitsd7 on 6/14/16.
 */
public class MyTicketAdapter extends BaseAdapter{
    private Intent intent;
    private SessionManager sessionManager;
    private LayoutInflater inflater;
    private Activity activity;
    private JSONArray dataList;
    private JSONObject dataObejct;
    private PassengerPaymentKAIAdapter passengerPaymentKAIAdapter;
    private String status, date, month, year, hour, minute;
    private Encode2 encode = new Encode2();

    public MyTicketAdapter(Activity activity, JSONArray dataList){
        this.activity = activity;
        this.dataList = dataList;
    }

    @Override
    public int getCount() {
        return dataList.length();
    }

    @Override
    public Object getItem(int position) {
        try {
            return dataList.getString(position);
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
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
            convertView = inflater.inflate(R.layout.my_ticket, null);

        try {
            dataObejct = dataList.getJSONObject(position);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        TextView originalCity = (TextView) convertView.findViewById(R.id.original_city);
        TextView destinationCity = (TextView) convertView.findViewById(R.id.destination_city);
        TextView trainName = (TextView) convertView.findViewById(R.id.train_name);
        TextView orderingCode = (TextView) convertView.findViewById(R.id.ordering_code);
        TextView bookingCode = (TextView) convertView.findViewById(R.id.booking_code);
        TextView originalDate = (TextView) convertView.findViewById(R.id.original_date);
        TextView originalStation = (TextView) convertView.findViewById(R.id.original_station);
        TextView statusPayment = (TextView) convertView.findViewById(R.id.status_payment);
        TextView shippingDate = (TextView) convertView.findViewById(R.id.shipping_date);
        TextView errorMessage = (TextView) convertView.findViewById(R.id.error_message);

        final HeightAdjustableListView listPassenger = (HeightAdjustableListView) convertView.findViewById(R.id.list_passenger);

        final Button btnResend = (Button) convertView.findViewById(R.id.btn_resend);
        final ImageView btnPassenger = (ImageView) convertView.findViewById(R.id.btn_passeger);
        final LinearLayout linearPassenger = (LinearLayout) convertView.findViewById(R.id.linear_passenger);
        final LinearLayout linearLayout = (LinearLayout) convertView.findViewById(R.id.linearLayout);
        LinearLayout linearBooking = (LinearLayout) convertView.findViewById(R.id.linear_booking);

        sessionManager = new SessionManager(activity);
        try {
            if (dataObejct.getString("PaymentStatus").equals("0")){
                status = "Menunggu Pembayaran";
                errorMessage.setVisibility(View.GONE);
                statusPayment.setTextColor(Color.RED);
                btnResend.setVisibility(View.GONE);
                btnResend.setBackgroundColor(Color.parseColor("#ffc423"));
                linearBooking.setVisibility(View.GONE);
            }else if (dataObejct.getString("PaymentStatus").equals("2")){
                status = "Sukses";
                errorMessage.setVisibility(View.GONE);
                statusPayment.setTextColor(Color.GREEN);
                btnResend.setVisibility(View.VISIBLE);
                btnResend.setText("Kirim Ulang e-Tiket");
                btnResend.setBackgroundColor(Color.parseColor("#cceeff"));

                linearBooking.setVisibility(View.VISIBLE);
                bookingCode.setText(dataObejct.getJSONObject("BookingKAI").getString("BookingNumber"));
            } else if (dataObejct.getString("PaymentStatus").equals("8")){
                status = "Dibatalkan";
                errorMessage.setVisibility(View.GONE);
                statusPayment.setTextColor(Color.RED);
                btnResend.setVisibility(View.VISIBLE);
                btnResend.setText("Coba Lagi");
                linearBooking.setVisibility(View.GONE);
            } else if (dataObejct.getString("PaymentStatus").equals("3")){
                errorMessage.setVisibility(View.VISIBLE);
                errorMessage.setText(dataObejct.getJSONObject("Payment").getString("ResponseMapping"));
                status = "Pembayaran tidak berhasil";
                statusPayment.setTextColor(Color.RED);
                btnResend.setVisibility(View.VISIBLE);
                btnResend.setText("Coba Lagi");
            }

            originalCity.setText(dataObejct.getJSONObject("BookingKAI").getJSONObject("originStation").getString("NamaStasiun"));
            destinationCity.setText(dataObejct.getJSONObject("BookingKAI").getJSONObject("destinationStation").getString("NamaStasiun"));
            trainName.setText(dataObejct.getJSONObject("BookingKAI").getString("TrainName") + " " + dataObejct.getJSONObject("BookingKAI").getString("TrainNo"));

            if (dataObejct.getString("Payment") == null || dataObejct.getString("Payment").equals("null")){
                linearLayout.setVisibility(View.GONE);
            }else{
                linearLayout.setVisibility(View.VISIBLE);
                orderingCode.setText(dataObejct.getJSONObject("Payment").getString("TransactionCode"));
            }

//            2017-11-11T15:00:00
            date = dataObejct.getJSONObject("BookingKAI").getString("DepartureDate").split("T")[0].substring(8, 10);
            month = dataObejct.getJSONObject("BookingKAI").getString("DepartureDate").split("T")[0].substring(5, 7);
            year = dataObejct.getJSONObject("BookingKAI").getString("DepartureDate").split("T")[0].substring(0, 4);
            hour = dataObejct.getJSONObject("BookingKAI").getString("DepartureDate").split("T")[1].substring(0, 2);
            minute = dataObejct.getJSONObject("BookingKAI").getString("DepartureDate").split("T")[1].substring(3, 5);

            originalDate.setText(date + "-" + month + "-" + year + " " + hour + ":" + minute + " WIB");
            originalStation.setText(dataObejct.getJSONObject("BookingKAI").getJSONObject("originStation").getString("NamaStasiun"));
            statusPayment.setText(status);

            date = dataObejct.getString("Created").split("T")[0].substring(8, 10);
            month = dataObejct.getString("Created").split("T")[0].substring(5, 7);
            year = dataObejct.getString("Created").split("T")[0].substring(0, 4);
            hour = dataObejct.getString("Created").split("T")[1].substring(0, 2);
            minute = dataObejct.getString("Created").split("T")[1].substring(3, 5);

            shippingDate.setText(date + "-" + month + "-" + year + " " + hour + ":" + minute + " WIB");

            if (dataObejct.getJSONObject("BookingKAI").getJSONArray("Passenger").length() > 0){
                passengerPaymentKAIAdapter = new PassengerPaymentKAIAdapter(activity, dataObejct.getJSONObject("BookingKAI").getJSONArray("Passenger"));
                listPassenger.setAdapter(passengerPaymentKAIAdapter);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        btnPassenger.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listPassenger.setVisibility(View.VISIBLE);

                if (linearPassenger.getVisibility() == View.GONE){
                    btnPassenger.setImageResource(R.drawable.up_arr_colp);
                    linearPassenger.setVisibility(View.VISIBLE);
                } else {
                    btnPassenger.setImageResource(R.drawable.down_arr_colp);
                    linearPassenger.setVisibility(View.GONE);
                }
            }
        });

        btnResend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JSONObject jsonObject = new JSONObject();
                Date date = new Date();
                SimpleDateFormat curFormater = new SimpleDateFormat("yyyy-MM-dd");
                btnResend.setEnabled(false);

                if (btnResend.getText().equals("Kirim Ulang e-Tiket")){
                    try {
                        SimpleDateFormat df = new SimpleDateFormat("ddMMyyyy");
                        String TimeStamp = df.format(new Date());

                        jsonObject.put("BookingID", dataList.getJSONObject(position).getJSONObject("BookingKAI").getString("ID"));
                        jsonObject.put("TimeStamp", TimeStamp);

                        jsonPost(API.getInstance().getApiSendTicket() + "?mfp_id=" + sessionManager.getKeyMfpId(), jsonObject, "passenger");
                    } catch (JSONException e) {
                        e.printStackTrace();
                        btnResend.setEnabled(true);
                    }
                }else if (btnResend.getText().equals("Coba Lagi")){
                    try {
                        if (date.after(curFormater.parse(dataList.getJSONObject(position).getJSONObject("BookingKAI").getString("DepartureDate").split("T")[0]))){
                            intent = new Intent(activity, HomeKAIActivity.class);
                            intent.putExtra("from", "klikindomaret");
                            activity.finish();
                            activity.startActivity(intent);
                            activity.overridePendingTransition(R.anim.left_in, R.anim.left_out);
                        }else{
                            getListTicket(dataList.getJSONObject(position).getJSONObject("BookingKAI"));
                        }
                    } catch (ParseException e) {
                        e.printStackTrace();
                        btnResend.setEnabled(true);
                    } catch (JSONException e) {
                        e.printStackTrace();
                        btnResend.setEnabled(true);
                    }
                }
            }
        });
        return convertView;
    }

    public void  getListTicket(JSONObject bookingObject){
        try {
            JSONObject userData = new JSONObject();
            JSONArray adultArray = new JSONArray();
            JSONArray babyArray = new JSONArray();

            for (int i=0; i<bookingObject.getJSONArray("Passenger").length(); i++){
                if (bookingObject.getJSONArray("Passenger").getJSONObject(i).getString("Maturity").equals("0")){
                    adultArray.put(bookingObject.getJSONArray("Passenger").getJSONObject(i));
                }else{
                    babyArray.put(bookingObject.getJSONArray("Passenger").getJSONObject(i));
                }
            }

            userData.put("originalStation", bookingObject.getJSONObject("originStation").getString("NamaStasiun") + ", " +
                    bookingObject.getJSONObject("originStation").getString("City"));
            userData.put("originalCode", bookingObject.getJSONObject("originStation").getString("KodeStasiun"));
            userData.put("destinationStation", bookingObject.getJSONObject("destinationStation").getString("NamaStasiun") + ", " +
                    bookingObject.getJSONObject("destinationStation").getString("City"));
            userData.put("destinationCode", bookingObject.getJSONObject("destinationStation").getString("KodeStasiun"));

            date = bookingObject.getString("DepartureDate").split("T")[0].substring(8, 10);
            month = bookingObject.getString("DepartureDate").split("T")[0].substring(5, 7);
            year = bookingObject.getString("DepartureDate").split("T")[0].substring(0, 4);

            userData.put("originalDate", date+month+year);
            userData.put("originalDateText", date+"-"+month+"-"+year);

            date = bookingObject.getString("ArrivalDate").split("T")[0].substring(8, 10);
            month = bookingObject.getString("ArrivalDate").split("T")[0].substring(5, 7);
            year = bookingObject.getString("ArrivalDate").split("T")[0].substring(0, 4);

            userData.put("destinationDate", date+month+year);
            userData.put("destinationDateText", date+"-"+month+"-"+year);
            userData.put("countAdult", adultArray.length());
            userData.put("countBaby", babyArray.length());
            userData.put("indexAdult", adultArray.length() - 1);
            userData.put("indexBaby", babyArray.length());
            userData.put("destinationStatus", false);

            intent = new Intent(activity, ListTicketKAIActivity.class);
            intent.putExtra("userData", userData.toString());
            intent.putExtra("destinationStatus", false);
            intent.putExtra("getDestination", false);
            intent.putExtra("scheduleNow", "pergi");
            intent.putExtra("from", "home");
            activity.finish();
            activity.startActivity(intent);
            activity.overridePendingTransition(R.anim.left_in, R.anim.left_out);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void jsonPost(String urlJsonObj, final JSONObject jsonObject, final String type) {
        RequestQueue queue = Volley.newRequestQueue(activity);
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                urlJsonObj, jsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if (response == null || response.length() == 0){
                                Toast.makeText(activity, "Gagal terkoneksi server", Toast.LENGTH_LONG).show();
                            }else{
                                if (response.getString("Code").equals("00")){
                                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(activity);
                                    alertDialogBuilder.setMessage(response.getString("Message"));
                                    alertDialogBuilder.setNegativeButton("Ok", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface arg0, int arg1) {
                                            intent = new Intent(activity, HomeKAIActivity.class);
                                            intent.putExtra("from", "");
                                            activity.finish();
                                            activity.startActivity(intent);
                                            activity.overridePendingTransition(R.anim.left_in, R.anim.left_out);
                                        }
                                    });

                                    AlertDialog alertDialog = alertDialogBuilder.create();
                                    alertDialog.show();
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(activity, "Gagal terkoneksi server", Toast.LENGTH_LONG).show();
                        }
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(activity, "Gagal terkoneksi server", Toast.LENGTH_LONG).show();
                    }
                }
        ) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String>  params = new HashMap<String, String>();
                String token = "";
                try {
                    token = encode.SHA1(encode.md5("66E2C13840534C139D85CEE1B433C1FX"));
                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }

                params.put("ApplicationKey", "indomaret");
                params.put("Authorization", "bearer "+token);

                return params;
            }
        };
        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(50000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(jsonObjReq);
    }
}