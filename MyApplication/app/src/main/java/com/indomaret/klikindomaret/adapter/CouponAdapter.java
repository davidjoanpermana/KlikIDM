package com.indomaret.klikindomaret.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
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
import com.indomaret.klikindomaret.activity.CartActivity;
import com.indomaret.klikindomaret.activity.PaymentActivity;
import com.indomaret.klikindomaret.activity.ShippingActivity;
import com.indomaret.klikindomaret.app.AppController;
import com.indomaret.klikindomaret.helper.SessionManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * Created by indomaretitsd7 on 6/23/16.
 */
public class CouponAdapter extends BaseAdapter {
    private Activity activity;
    private LayoutInflater inflater;
    private String from = "";
    private List<String> coupons;
    private SessionManager sessionManager;

    public CouponAdapter (Activity activity, List<String> coupons, String from){
        this.activity = activity;
        this.coupons = coupons;
        this.from = from;
        this.sessionManager = new SessionManager(activity);
    }

    @Override
    public int getCount() {
        return coupons.size();
    }

    @Override
    public Object getItem(int position) {
        return coupons.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (inflater == null)
            inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (convertView == null)
            convertView = inflater.inflate(R.layout.activity_cart_coupon, null);

        final TextView coupon = (TextView) convertView.findViewById(R.id.coupon);
        ImageView removeCoupon = (ImageView) convertView.findViewById(R.id.remove_coupon);

        if (!coupons.get(position).toLowerCase().equals("null") && !coupons.get(position).equals(""))
            coupon.setText(coupons.get(position).replace("[", "").replace("]", ""));

        removeCoupon.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(activity);
                        alertDialogBuilder.setMessage("Yakin menghapus i-Kupon/Voucher");
                        alertDialogBuilder.setNegativeButton("Batal", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface arg0, int arg1) {}
                        });

                        alertDialogBuilder.setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface arg0, int arg1) {
                                try {
                                    if (coupons.get(position).toLowerCase().contains("kupon")){
                                        jsonArrayRequest(API.getInstance().getApiCancelCoupon()+"?customerId="+sessionManager.getUserID()+"&couponCode="+coupons.get(position).toString().split(" ")[2]+"&mfp_id="+sessionManager.getKeyMfpId());
                                    }else{
                                        JSONObject object = new JSONObject();
                                        object.put("Code", coupons.get(position).toString().split(" ")[2]);
                                        object.put("ShoppingCartID", sessionManager.getCartId());
                                        object.put("CustomerID", sessionManager.getUserID());
                                        object.put("RegionID", sessionManager.getRegionID());
                                        object.put("SalesOrderNo", "");
                                        object.put("Nominal", "");

                                        jsonPost(API.getInstance().getApiCancelVoucher()+"?mfp_id="+sessionManager.getKeyMfpId(), object);
                                    }
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

        return convertView;
    }

    public void jsonPost(String urlJsonObj, JSONObject jsonObject){
        System.out.println("--- update url= " + urlJsonObj);
        System.out.println("--- update = object" + jsonObject);

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                urlJsonObj, jsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(final JSONObject response) {
                        try {
                            if (response == null || response.length() == 0){
                                Toast.makeText(activity, "Gagal terkoneksi server", Toast.LENGTH_LONG).show();
                            }else{
                                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(activity);
                                alertDialogBuilder.setMessage(response.getJSONObject("ResponseObject").getString("keterangan"));

                                alertDialogBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface arg0, int arg1) {
                                        sessionManager.setKeyCouponList("");
                                        if (from.equals("cart")){
                                            ((CartActivity) activity).reloadCart();
                                        }else if (from.equals("shipping")){
                                            ((ShippingActivity) activity).reloadCart();
                                        }else if (from.equals("payment")){
                                            ((PaymentActivity) activity).reloadCart();
                                        }
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
        });

        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(60000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppController.getInstance().addToRequestQueue(jsonObjReq);
    }

    public void jsonArrayRequest(String url){
        System.out.println("cart = " + url);

        JsonArrayRequest jsonArrayReq = new JsonArrayRequest(url,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            if (response == null || response.length() == 0){
                                Toast.makeText(activity, "Gagal terkoneksi server", Toast.LENGTH_LONG).show();
                            }else{
                                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(activity);
                                alertDialogBuilder.setMessage(response.getJSONObject(0).getString("Pesan"));
                                alertDialogBuilder.setNegativeButton("Batal", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface arg0, int arg1) {}
                                });

                                alertDialogBuilder.setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface arg0, int arg1) {
                                        sessionManager.setKeyCouponList("");
                                        if (from.equals("cart")){
                                            ((CartActivity) activity).reloadCart();
                                        }else if (from.equals("shipping")){
                                            ((ShippingActivity) activity).reloadCart();
                                        }else if (from.equals("payment")){
                                            ((PaymentActivity) activity).reloadCart();
                                        }
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

        jsonArrayReq.setRetryPolicy(new DefaultRetryPolicy(60000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppController.getInstance().addToRequestQueue(jsonArrayReq);
    }
}
