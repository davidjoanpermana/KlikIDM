package com.indomaret.klikindomaret.adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.indomaret.klikindomaret.API;
import com.indomaret.klikindomaret.R;
import com.indomaret.klikindomaret.activity.HotelActivity;
import com.indomaret.klikindomaret.app.AppController;
import com.indomaret.klikindomaret.helper.SQLiteHandler;
import com.indomaret.klikindomaret.helper.SessionManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * Created by indomaretitsd7 on 6/14/16.
 */
public class ListMenuHotelAdapter extends BaseAdapter {
    private Intent intent;
    private SQLiteHandler sqLiteHandler;
    private SessionManager sessionManager;
    private Activity activity;
    private List<String> menuList;
    private String menu;
    private LayoutInflater inflater;

    public ListMenuHotelAdapter(Activity activity, List<String> menuList){
        this.activity = activity;
        this.menuList = menuList;
    }

    @Override
    public int getCount() {
        return menuList.size();
    }

    @Override
    public Object getItem(int position) {
        return menuList.get(position);
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
            convertView = inflater.inflate(R.layout.list_menu_hotel, null);

        TextView textMenu = (TextView) convertView.findViewById(R.id.text_menu);
        LinearLayout linearSignOut = (LinearLayout) convertView.findViewById(R.id.linear_signout);

        intent = activity.getIntent();
        sqLiteHandler = new SQLiteHandler(activity);

        sessionManager = new SessionManager(activity);

        if (sessionManager.isLoggedIn()){
            if (position == 6){
                linearSignOut.setVisibility(View.VISIBLE);
            }else{
                linearSignOut.setVisibility(View.GONE);
            }
        }else{
            linearSignOut.setVisibility(View.GONE);
        }

        menu = menuList.get(position);
        textMenu.setText(menu);

        linearSignOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((HotelActivity)activity).runLoder();

                sessionManager.setKeyShipping(false);
                sessionManager.setKeyPlazaShipping(false);
                getMFPToken(API.getInstance().getMfpId()+"?device_token=" + sessionManager.getDeviceToken());
                sqLiteHandler.deleteData();
                sessionManager.setKeyShippingPlaza(0);

                sessionManager.setKeyTotalPrice(0);
                sessionManager.setKeyTotalVoucher(0);
                sessionManager.setKeyTotalDiscount(0);
                sessionManager.setKeyTotalShippingCost(0);
                sessionManager.setKeyTotalCoupon(0);
                sessionManager.setKeyCouponList("");
                sessionManager.setKeyTokenCookie("");
                sessionManager.setkeyDate("");
                sessionManager.setKeyTime("");
            }
        });

        return convertView;
    }

    public void getMFPToken(String url){
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                url, new JSONObject(),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            ((HotelActivity)activity).stopLoder();
                            System.out.println("--- berhasil logout");
                            sessionManager.setKeyMfpId(response.getString("Message"));
                            sessionManager.setLogin(false);
                            sessionManager.setCartId(null);
                            sessionManager.setUserID("00000000-0000-0000-0000-000000000000");

                            intent = new Intent(activity, HotelActivity.class);
                            intent.putExtra("from", "klikindomaret");
                            activity.startActivity(intent);
                            activity.finish();
                        } catch (JSONException e) {
                            System.out.println("--- gagal logout");
                            e.printStackTrace();
                            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(activity);
                            alertDialogBuilder.setMessage("Gagal Logout.");

                            alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface arg0, int arg1) {

                                }
                            });

                            AlertDialog alertDialog = alertDialogBuilder.create();
                            alertDialog.setCanceledOnTouchOutside(false);
                            alertDialog.show();
                            ((HotelActivity)activity).stopLoder();
                        }
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(activity, "Gagal terkoneksi server", Toast.LENGTH_LONG).show();
                ((HotelActivity)activity).stopLoder();
            }
        });

        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(60000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppController.getInstance().addToRequestQueue(jsonObjReq);
    }
}