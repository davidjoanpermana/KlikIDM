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

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.indomaret.klikindomaret.API;
import com.indomaret.klikindomaret.R;
import com.indomaret.klikindomaret.activity.AddAdressActivity;
import com.indomaret.klikindomaret.activity.AddressActivity;
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
public class AddressItemAdapter extends BaseAdapter {
    Intent intent;
    private Activity activity;
    private LayoutInflater inflater;
    private JSONObject address;
    private JSONArray addressList;
    private SessionManager sessionManager;
    private SQLiteHandler sqLiteHandler;

    private TextView name, alamat, city, zipcode, title, infoPlace, iconCheck;
    private LinearLayout addressBackground, btnEdit, linearPlace;
    private ImageView iconPlace;
    private AddressBookFragment addressBookFragment;

    public AddressItemAdapter(Activity activity, JSONArray addressList){
        this.activity = activity;
        this.addressList = addressList;
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
            convertView = inflater.inflate(R.layout.address_layout, null);

        name = (TextView) convertView.findViewById(R.id.receiver_name);
        alamat = (TextView) convertView.findViewById(R.id.street);
        city = (TextView) convertView.findViewById(R.id.city);
        zipcode = (TextView) convertView.findViewById(R.id.zipcode);
        title = (TextView) convertView.findViewById(R.id.title_address);
        btnEdit = (LinearLayout) convertView.findViewById(R.id.btn_edit);
        infoPlace = (TextView) convertView.findViewById(R.id.info_place);

        iconPlace = (ImageView) convertView.findViewById(R.id.icon_place);
        iconCheck = (TextView) convertView.findViewById(R.id.icon_check);

        addressBackground = (LinearLayout) convertView.findViewById(R.id.address_item_background);
        linearPlace = (LinearLayout) convertView.findViewById(R.id.linear_place);

        try {
            address = addressList.getJSONObject(position);

            name.setText(address.getString("ReceiverName"));

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
                iconCheck.setVisibility(View.VISIBLE);
            } else {
                addressBackground.setBackgroundResource(R.drawable.card_product_style_1_grey);
                iconCheck.setVisibility(View.GONE);
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

        linearPlace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (addressList.getJSONObject(position).getString("GoogleAddress") == null ||
                            addressList.getJSONObject(position).getString("GoogleAddress").toLowerCase().equals("null") ||
                            addressList.getJSONObject(position).getString("GoogleAddress").equals("")){
                        LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                        View view = inflater.inflate(R.layout.popup_validate_map, null);
                        Button btnOk = (Button) view.findViewById(R.id.btn_ok);
                        Button btnCancel = (Button) view.findViewById(R.id.btn_cancel);

                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(activity);
                        alertDialogBuilder.setView(view);
                        final AlertDialog alertDialog = alertDialogBuilder.create();
                        alertDialog.setCanceledOnTouchOutside(false);
                        alertDialog.show();

                        btnOk.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                try {
                                    intent = new Intent(activity, AddAdressActivity.class);
                                    intent.putExtra("type", "updateShipping");
                                    intent.putExtra("data", addressList.getJSONObject(position).toString());
                                    activity.startActivity(intent);
                                    activity.overridePendingTransition(R.anim.left_in, R.anim.left_out);
                                    alertDialog.dismiss();
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        });

                        btnCancel.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                alertDialog.dismiss();
                                try {
                                    ((AddressActivity)activity).setData(addressList.getJSONObject(position),
                                            addressList.getJSONObject(position).getString("GoogleAddress"),
                                            addressList.getJSONObject(position).getString("Latitude"),
                                            addressList.getJSONObject(position).getString("Longitude"));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        addressBackground.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    ((AddressActivity)activity).setData(addressList.getJSONObject(position),
                            addressList.getJSONObject(position).getString("GoogleAddress"),
                            addressList.getJSONObject(position).getString("Latitude"),
                            addressList.getJSONObject(position).getString("Longitude"));
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
                        intent.putExtra("type", "updateShipping");
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

        return convertView;
    }
}
