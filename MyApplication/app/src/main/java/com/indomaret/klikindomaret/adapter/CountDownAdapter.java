package com.indomaret.klikindomaret.adapter;

import android.app.Activity;
import android.content.DialogInterface;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.indomaret.klikindomaret.API;
import com.indomaret.klikindomaret.R;
import com.indomaret.klikindomaret.activity.CartActivity;
import com.indomaret.klikindomaret.app.AppController;
import com.indomaret.klikindomaret.helper.SessionManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by USER on 4/26/2016.
 */
public class CountDownAdapter extends RecyclerView.Adapter<CountDownAdapter.ViewHolder> {
    private SessionManager sessionManager;
    private Activity activity;
    private String time, idProduct;
    private Handler handler = new Handler();

    public CountDownAdapter(Activity activity, String time, String idProduct){
        this.activity = activity;
        this.time = time;
        this.idProduct = idProduct;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        public TextView messageErrorPromo;

        public ViewHolder(View view) {
            super(view);
            messageErrorPromo = (TextView) view.findViewById(R.id.message_error_promo);
        }
    }

    @Override
    public CountDownAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.count_down, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public int getItemCount() {
        return 1;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        try {
            sessionManager = new SessionManager(activity);
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            final Date eventDate = dateFormat.parse(time);
            final Date currentDate = new Date();

            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    try {
                        long diff = eventDate.getTime() - currentDate.getTime();
                        long minutes = ((diff / (1000*60)) % 60);
                        long seconds = (diff / 1000) % 60;
                        int hours   = (int) ((diff / (1000*60*60)) % 24);

                        holder.messageErrorPromo.setText("Batas Waktu : "
                                + String.format("%02d", hours) + "Jam "
                                + String.format("%02d", minutes) + "Menit "
                                + String.format("%02d", seconds) + "Detik ");
                        notifyDataSetChanged();

                        if (String.format("%02d", hours).equals("00") && String.format("%02d", minutes).equals("00") && String.format("%02d", seconds).equals("00")){
                            ((CartActivity)activity).updateCart();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            },1000);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void jsonPost(String urlJsonObj, JSONObject jsonObject, final String type){
        System.out.println("cart adapter url = " + urlJsonObj);
        System.out.println("cart adapter object = " + jsonObject);

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                urlJsonObj, jsonObject,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        if (response == null || response.length() == 0){
                            Toast.makeText(activity, "Gagal terkoneksi server", Toast.LENGTH_LONG).show();
                        }else{
                            if (type.equals("delete")){
                                try {
                                    if(response.getString("Message").equals("success")){
                                        String url = API.getInstance().getCartTotal()
                                                +"?cartId=" + sessionManager.getCartId()
                                                +"&customerId=" + sessionManager.getUserID()
                                                +"&mfp_id=" + sessionManager.getKeyMfpId();

                                        jsonArrayGet(url, "cart");
                                    }else{
                                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(activity);
                                        alertDialogBuilder.setMessage(response.getString("Message"));
                                        alertDialogBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface arg0, int arg1) {
                                            }
                                        });

                                        AlertDialog alertDialog = alertDialogBuilder.create();
                                        alertDialog.setCanceledOnTouchOutside(false);
                                        alertDialog.show();
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }else if (type.equals("update")){

                            }
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(activity, "Gagal terkoneksi server", Toast.LENGTH_LONG).show();
            }
        });

        AppController.getInstance().addToRequestQueue(jsonObjReq);
    }

    public void jsonArrayGet(String urlJsonObj, final String type){
        System.out.println("region " + urlJsonObj);

        JsonArrayRequest jsonArrayReq = new JsonArrayRequest(urlJsonObj,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            if(response.getInt(0) > 0){
                                ((CartActivity) activity).deleteItemCartResponse();
                            } else {
                                sessionManager.setCartId("00000000-0000-0000-0000-000000000000");

                                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(activity);
                                alertDialogBuilder.setMessage("Keranjang Belanja Anda kosong.");
                                alertDialogBuilder.setNegativeButton("Mulai Belanja", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface arg0, int arg1) {
                                        activity.finish();
                                        activity.overridePendingTransition(R.anim.right_in, R.anim.right_out);
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
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(activity, "Gagal terkoneksi server", Toast.LENGTH_LONG).show();
            }
        }, activity);

        AppController.getInstance().addToRequestQueue(jsonArrayReq);
    }
}
