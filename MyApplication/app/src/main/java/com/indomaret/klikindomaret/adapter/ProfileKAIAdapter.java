package com.indomaret.klikindomaret.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.indomaret.klikindomaret.API;
import com.indomaret.klikindomaret.R;
import com.indomaret.klikindomaret.activity.NewProfileKAIActivity;
import com.indomaret.klikindomaret.activity.ProfileKAIActivity;
import com.indomaret.klikindomaret.helper.Encode2;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by indomaretitsd7 on 6/14/16.
 */
public class ProfileKAIAdapter extends BaseAdapter{
    private LayoutInflater inflater;
    private Intent intent;
    private Activity activity;
    private JSONArray passengerList;
    private String maturity;
    private Encode2 encode = new Encode2();

    public ProfileKAIAdapter(Activity activity, JSONArray passengerList){
        this.activity = activity;
        this.passengerList = passengerList;
    }

    @Override
    public int getCount() {
        return passengerList.length();
    }

    @Override
    public Object getItem(int position) {
        try {
            return passengerList.getJSONObject(position);
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
            convertView = inflater.inflate(R.layout.profile_kai_single, null);

        JSONObject profileObject = null;
        try {
            profileObject = passengerList.getJSONObject(position);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        final TextView nameUser = (TextView) convertView.findViewById(R.id.name);
        TextView ageUser = (TextView) convertView.findViewById(R.id.age);
        final TextView idUser = (TextView) convertView.findViewById(R.id.id_profile);
        Button btnUbah = (Button) convertView.findViewById(R.id.btn_ubah);
        final Button btnHapus = (Button) convertView.findViewById(R.id.btn_hapus);

        try {
            if (profileObject.getString("Maturity").equals("0")){
                maturity = "Dewasa";
            }else{
                maturity = "Bayi";
            }

            nameUser.setText(profileObject.getString("FullName"));
            ageUser.setText(maturity);
            idUser.setText(profileObject.getString("Identity"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        final JSONObject finalProfileObject = profileObject;
        btnUbah.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    intent = new Intent(activity, NewProfileKAIActivity.class);
                    intent.putExtra("profileObject", finalProfileObject.toString());

                    if (passengerList.getJSONObject(position).getString("Maturity").equals("0")){
                        intent.putExtra("type", "editAdult");
                    }else{
                        intent.putExtra("type", "editInfant");
                    }

                    activity.startActivity(intent);
                    activity.overridePendingTransition(R.anim.left_in, R.anim.left_out);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        final JSONObject finalProfileObject1 = profileObject;
        btnHapus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(activity);
                alertDialogBuilder.setMessage("Apakah Anda yakin ingin menghapus?");
                alertDialogBuilder.setNegativeButton("Batal", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {}
                });

                alertDialogBuilder.setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        try {
                            requestWithSomeHttpHeaders(API.getInstance().getApiDeletePassenger()
                                    +"?ID="+ finalProfileObject1.getString("ID")+"&CustomerID=2f415dba-885a-4297-8b6e-3260f8cc2247", "passenger");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });

                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.setCanceledOnTouchOutside(false);
                alertDialog.show();
            }
        });

        return convertView;
    }

    public void requestWithSomeHttpHeaders(String url, final String type) {
        RequestQueue queue = Volley.newRequestQueue(activity);
        System.out.println("--- passenger url : "+url);
        StringRequest postRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {
                        try {
                            if (response == null || response.length() == 0){
                                Toast.makeText(activity, "Gagal terkoneksi server", Toast.LENGTH_LONG).show();
                            }else{
                                JSONObject jObject = new JSONObject(response);
                                if (jObject.getBoolean("IsSuccess")) {
                                    ((ProfileKAIActivity) activity).showPassenger();
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
                    System.out.println("--- token : "+token);
                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }

                params.put("ApplicationKey", "indomaret");
                params.put("Authorization", "bearer "+token+"#2f415dba-885a-4297-8b6e-3260f8cc2247");

                return params;
            }
        };
        queue.add(postRequest);
    }
}