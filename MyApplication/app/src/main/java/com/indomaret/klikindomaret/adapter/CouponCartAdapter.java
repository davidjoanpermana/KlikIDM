package com.indomaret.klikindomaret.adapter;

import android.app.Activity;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.indomaret.klikindomaret.API;
import com.indomaret.klikindomaret.R;
import com.indomaret.klikindomaret.activity.CartActivity;
import com.indomaret.klikindomaret.app.AppController;
import com.indomaret.klikindomaret.helper.SessionManager;

import org.json.JSONArray;
import org.json.JSONException;

/**
 * Created by indomaretitsd7 on 6/23/16.
 */
public class CouponCartAdapter extends RecyclerView.Adapter<CouponCartAdapter.ViewHolder> {
    private Activity activity;
    private LayoutInflater inflater;
    private JSONArray coupons;
    private SessionManager sessionManager;

    public CouponCartAdapter(Activity activity, JSONArray coupons){
        this.activity = activity;
        this.coupons = coupons;
        this.sessionManager = new SessionManager(activity);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView coupon;
        public ImageView removeCoupon;

        public ViewHolder(View view) {
            super(view);
            coupon = (TextView) view.findViewById(R.id.coupon);
            removeCoupon = (ImageView) view.findViewById(R.id.remove_coupon);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_cart_coupon, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        try {
            if (!coupons.get(position).toString().toLowerCase().equals("null") && !coupons.get(position).equals(""))
                holder.coupon.setText("i-Kupon : " + coupons.get(position).toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        holder.removeCoupon.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(activity);
                        alertDialogBuilder.setMessage("Yakin menghapus i-Kupon");
                        alertDialogBuilder.setNegativeButton("Batal", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface arg0, int arg1) {}
                        });

                        alertDialogBuilder.setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface arg0, int arg1) {
                                try {
                                    jsonArrayRequest(API.getInstance().getApiCancelCoupon()+"?customerId="+sessionManager.getUserID()+"&couponCode="+coupons.get(position).toString()+"&mfp_id="+sessionManager.getKeyMfpId());
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        });

                        AlertDialog alertDialog = alertDialogBuilder.create();
                        alertDialog.setCanceledOnTouchOutside(false);
                        alertDialog.show();
                    }
                }
        );
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return coupons.length();
    }

    public void jsonArrayRequest(String url){
        System.out.println("cart = " + url);

        JsonArrayRequest jsonArrayReq = new JsonArrayRequest(url,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        if (response == null || response.length() == 0){
                            Toast.makeText(activity, "Gagal terkoneksi server", Toast.LENGTH_LONG).show();
                        }else {
                            try {
                                if (response.getJSONObject(0).getString("Pesan").equals("Unbook kupon berhasil")){
                                    sessionManager.setKeyCouponList("");
                                    ((CartActivity) activity).reloadCart();
                                }else{
                                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(activity);
                                    alertDialogBuilder.setMessage(response.getJSONObject(0).getString("Pesan"));

                                    alertDialogBuilder.setPositiveButton("Ya", new DialogInterface.OnClickListener() {
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
                        }
//                        try {
//                            if (response == null || response.length() == 0){
//                                Toast.makeText(activity, "Gagal terkoneksi server", Toast.LENGTH_LONG).show();
//                            }else{
//                                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(activity);
//                                alertDialogBuilder.setMessage(response.getJSONObject(0).getString("Pesan"));
//                                alertDialogBuilder.setNegativeButton("Batal", new DialogInterface.OnClickListener() {
//                                    @Override
//                                    public void onClick(DialogInterface arg0, int arg1) {}
//                                });
//
//                                alertDialogBuilder.setPositiveButton("Ya", new DialogInterface.OnClickListener() {
//                                    @Override
//                                    public void onClick(DialogInterface arg0, int arg1) {
//                                        ((CartActivity) activity).reloadCart();
//                                    }
//                                });
//
//                                AlertDialog alertDialog = alertDialogBuilder.create();
//                                alertDialog.setCanceledOnTouchOutside(false);
//                                alertDialog.show();
//                            }
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(activity, "Gagal terkoneksi server", Toast.LENGTH_LONG).show();
            }
        }, activity);

        jsonArrayReq.setRetryPolicy(new DefaultRetryPolicy(60000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppController.getInstance().addToRequestQueue(jsonArrayReq);
    }
}
