package com.indomaret.klikindomaret.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Build;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.indomaret.klikindomaret.API;
import com.indomaret.klikindomaret.R;
import com.indomaret.klikindomaret.activity.AddAdressActivity;
import com.indomaret.klikindomaret.app.AppController;
import com.indomaret.klikindomaret.fragment.AddressBookFragment;
import com.indomaret.klikindomaret.helper.SQLiteHandler;
import com.indomaret.klikindomaret.helper.SessionManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by USER on 4/6/2016.
 */
public class AddressAdapter extends BaseAdapter {
    Intent intent;
    private Activity activity;
    private LayoutInflater inflater;
    private JSONObject address;
    private JSONArray addressList;
    private SessionManager sessionManager;
    private SQLiteHandler sqLiteHandler;

    private TextView name, mobile, alamat, city, zipcode, title, btnEdit, btnDelete, infoPlace;
    private LinearLayout addressBackground, titleBackground;
    private ImageView iconPlace;
    private CheckBox iconCheckBlue;
    private AddressBookFragment addressBookFragment;

    public AddressAdapter(Activity activity, JSONArray addressList, AddressBookFragment addressBookFragment){
        this.activity = activity;
        this.addressList = addressList;
        this.addressBookFragment = addressBookFragment;
    }

    @Override
    public int getCount() {
        return addressList.length();
    }

    @Override
    public Object getItem(int position) {
        try {
            return addressList.getJSONObject(position);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {
        sessionManager = new SessionManager(activity);
        sqLiteHandler = new SQLiteHandler(activity);

        if (inflater == null)
            inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (convertView == null)
            convertView = inflater.inflate(R.layout.address_item_layout, null);

        name = (TextView) convertView.findViewById(R.id.receiver_name);
        mobile = (TextView) convertView.findViewById(R.id.mobile_number);
        alamat = (TextView) convertView.findViewById(R.id.street);
        city = (TextView) convertView.findViewById(R.id.city);
        zipcode = (TextView) convertView.findViewById(R.id.zipcode);
        title = (TextView) convertView.findViewById(R.id.title_address);
        btnEdit = (TextView) convertView.findViewById(R.id.btn_edit);
        btnDelete = (TextView) convertView.findViewById(R.id.btn_delete);
        infoPlace = (TextView) convertView.findViewById(R.id.info_place);

        iconCheckBlue = (CheckBox) convertView.findViewById(R.id.icon_check_blue);
        iconPlace = (ImageView) convertView.findViewById(R.id.icon_place);

        addressBackground = (LinearLayout) convertView.findViewById(R.id.address_item_background);
        titleBackground = (LinearLayout) convertView.findViewById(R.id.title_item_background);

        try {
            address = addressList.getJSONObject(position);

            name.setText(address.getString("ReceiverName"));
            mobile.setText(address.getString("ReceiverPhone"));

            if((address.getString("Street2") == null || address.getString("Street2").equals("null") || address.getString("Street2").equals(""))
                    && (address.getString("Street3") == null || address.getString("Street3").equals("null") || address.getString("Street3").equals(""))){
                alamat.setText(address.getString("Street"));
            } else if(address.getString("Street2") == null || address.getString("Street2").equals("null") || address.getString("Street2").equals("")){
                alamat.setText(address.getString("Street")+"\n"
                        +address.getString("Street3"));
            }else if(address.getString("Street3") == null || address.getString("Street3").equals("null") || address.getString("Street3").equals("")){
                alamat.setText(address.getString("Street")+"\n"
                        +address.getString("Street2"));
            }else{
                alamat.setText(address.getString("Street")+"\n"
                        +address.getString("Street2")+"\n"
                        +address.getString("Street3"));
            }

            city.setText(address.getString("District")+", "
                    + address.getString("CityLabel"));
            zipcode.setText(address.getString("RegionName")+", "
                    + address.getString("ProvinceName")+", "
                    + address.getString("ZipCode"));

            title.setText(address.getString("AddressTitle"));

            if(address.getString("IsDefault").equals("true")){
                addressBackground.setBackgroundResource(R.drawable.card_product_style_2_grey);
                titleBackground.setBackgroundResource(R.drawable.card_product_style_2_grey);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    iconCheckBlue.setButtonTintList(ColorStateList.valueOf(Color.parseColor("#0079C2")));
                }
            } else {
                addressBackground.setBackgroundResource(R.drawable.card_product_style_1_grey);
                titleBackground.setBackgroundResource(R.drawable.card_product_style_1_grey);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    iconCheckBlue.setButtonTintList(ColorStateList.valueOf(Color.parseColor("#F2F2F2")));
                }
            }

            if (address.getString("GoogleAddress") == null || address.getString("GoogleAddress").equals("null") ||
                    address.getString("GoogleAddress").equals("")){
                iconPlace.setBackgroundResource(R.drawable.icon_place_red);
                infoPlace.setText("Mohon tandai lokasi pada peta");
                infoPlace.setTextColor(Color.parseColor("#f78409"));
            }else{
                iconPlace.setBackgroundResource(R.drawable.icon_place_black);
                infoPlace.setText(address.getString("GoogleAddress"));
                infoPlace.setTextColor(Color.parseColor("#000000"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        titleBackground.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    addressBookFragment.setDefault(position);
                    address.put("IsDefault", true);
                    notifyDataSetChanged();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        addressBackground.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    addressBookFragment.setDefault(position);
                    address.put("IsDefault", true);
                    notifyDataSetChanged();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        btnEdit.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        intent = new Intent(activity, AddAdressActivity.class);
                        intent.putExtra("type", "update");
                        try {
                            intent.putExtra("data", addressList.getJSONObject(position).toString());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        activity.startActivity(intent);
                        activity.overridePendingTransition(R.anim.left_in, R.anim.left_out);
                    }
                }
        );

        btnDelete.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(activity);
                        alertDialogBuilder.setMessage("Yakin akan menghapus alamat?");
                        alertDialogBuilder.setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface arg0, int arg1) {
                                try {
                                    makeJsonObjectGet(API.getInstance().getApiDeleteAddress()+"?mfp_id="+sessionManager.getKeyMfpId()+"&ID="+addressList.getJSONObject(position).getString("ID")+"&CustID="+addressList.getJSONObject(position).getString("CustomerID"), "del");
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        });

                        alertDialogBuilder.setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface arg0, int arg1) {
                            }
                        });

                        AlertDialog alertDialog = alertDialogBuilder.create();
                        alertDialog.show();
                    }
                }
        );

        System.out.println("--- position : "+position);
        return convertView;
    }

    public void makeJsonObjectGet(String url, final String Type){
        System.out.println("address url = " + url);

        JsonArrayRequest jsonArrayReq = new JsonArrayRequest(url,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        if (response == null || response.length() == 0){
                            Toast.makeText(activity, "Gagal terkoneksi server", Toast.LENGTH_LONG).show();
                        }else{
                            if(Type.equals("del")){
                                processDelete(response);
                            } else if(Type.equals("prof")){
                                processProfile(response);
                            }
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

    public void processDelete(JSONArray response){
        makeJsonObjectGet(API.getInstance().getApiGetProfile()+"?access_token="+sessionManager.getResponseId()+"&mfp_id="+sessionManager.getResponseId(), "prof");
    }

    public void processProfile(final JSONArray response){
        sqLiteHandler.insertProfile(response.toString());

        try {
            sessionManager.setUserID(response.getJSONObject(0).getString("ID"));
            JSONArray address = response.getJSONObject(0).getJSONArray("Address");

            for (int i=0; i<address.length(); i++){
                if(address.getJSONObject(i).getString("IsDefault").equals("true")){
                    sqLiteHandler.insertDefaultAddress(address.getJSONObject(i).toString());
                }
            }

            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(activity);
            alertDialogBuilder.setPositiveButton("Tutup", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface arg0, int arg1) {
                    JSONArray userProfile = null;

                    try {
                        userProfile = new JSONArray(sqLiteHandler.getProfile());
                        JSONArray addressArray = userProfile.getJSONObject(0).getJSONArray("Address");
                        boolean addressSelect = false;

                        for (int i=0; i<addressArray.length(); i++){
                            if (addressArray.getJSONObject(i).getString("IsDefault").equals("true")){
                                addressSelect = true;
                            }
                        }

                        if (!addressSelect && userProfile.getJSONObject(0).getJSONArray("Address").length() != 0){
                            JSONObject address = addressArray.getJSONObject(0);
                            sessionManager.setRegionID(addressArray.getJSONObject(0).getString("Region"));
                            sessionManager.setRegionName(addressArray.getJSONObject(0).getString("RegionName"));
                            address.put("IsDefault", true);
                            JSONObject cartObject = new JSONObject();

                            try {
                                cartObject.put("CartId", sessionManager.getCartId());
                                cartObject.put("RegionId", sessionManager.getRegionID());
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            addressBookFragment.makeJsonPost(API.getInstance().getApiSetDefaultAddress()+"?isChangeAddress=false&mfp_id="+sessionManager.getKeyMfpId(), address, "update address");
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    addressBookFragment.updateDataAdapter();
                }
            });

            alertDialogBuilder.setMessage("Berhasil menghapus alamat");
            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
