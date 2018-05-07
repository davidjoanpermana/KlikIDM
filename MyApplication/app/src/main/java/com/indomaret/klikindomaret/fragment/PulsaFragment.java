package com.indomaret.klikindomaret.fragment;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.indomaret.klikindomaret.API;
import com.indomaret.klikindomaret.MainActivity;
import com.indomaret.klikindomaret.R;
import com.indomaret.klikindomaret.app.AppController;
import com.indomaret.klikindomaret.helper.SessionManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class PulsaFragment extends Fragment {
    private View view;
    private EditText phoneNumber;
    private TextView price;
    private ImageView contact;
    private Spinner pulsaVoucherNominal;
    private LinearLayout btnBuyFacebook;
    private JSONArray pulsaList, contactArray, pulsaArray;
    private JSONObject contactObject;
    private SessionManager sessionManager;
    private static final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 100;
    public android.support.v7.app.AlertDialog alertDialog;
    DecimalFormat df = new DecimalFormat("#,###");

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_pulsa, container, false);

        sessionManager = new SessionManager(getActivity());
        pulsaVoucherNominal = (Spinner) view.findViewById(R.id.nominal_pulsa);
        btnBuyFacebook = (LinearLayout)  view.findViewById(R.id.btn_buy_pulsa);
        price = (TextView) view.findViewById(R.id.price);
        phoneNumber = (EditText) view.findViewById(R.id.phone_number);
        contact = (ImageView) view.findViewById(R.id.contact);

        phoneNumber.setFocusable(false);
        phoneNumber.setFocusableInTouchMode(true);
        btnBuyFacebook.setEnabled(false);

        contact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED ) {
                    ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_CONTACTS}, 1);
                }else{
                    contactArray = new JSONArray();
                    Cursor phones = getActivity().getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,null,null, null);

                    while (phones.moveToNext()){
                        contactObject = new JSONObject();
                        String name=phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                        String phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));

                        try {
                            contactObject.put("Name", name);
                            contactObject.put("PhoneNumber", phoneNumber);

                            contactArray.put(contactObject);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    Intent intent = new Intent(Intent.ACTION_PICK,  ContactsContract.Contacts.CONTENT_URI);
                    startActivityForResult(intent, 1);
                }
//                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_CONTACTS}, MY_PERMISSIONS_REQUEST_READ_CONTACTS);
            }
        });

        pulsaVoucherNominal.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position > 0){
                    btnBuyFacebook.setEnabled(true);
                    btnBuyFacebook.setBackgroundResource(R.drawable.button_style_1);

                    try {
                        price.setVisibility(View.VISIBLE);
                        price.setText(Html.fromHtml("Harga Jual : <b>Rp." + df.format(pulsaArray.getJSONObject(position-1).getDouble("HargaWebsite")).replace(",",".") + "</b>"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }else{
                    price.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        btnBuyFacebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int index = pulsaVoucherNominal.getSelectedItemPosition();

                if(phoneNumber.getText().toString().length() < 9){
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
                    alertDialogBuilder.setMessage("Nomor Hanphone tidak boleh kurang dari 9");
                    alertDialogBuilder.setNegativeButton("Tutup", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });

                    AlertDialog alertDialog = alertDialogBuilder.create();
                    alertDialog.show();
                } else if(index > 0){
                    try {
                        JSONObject pulsa = pulsaList.getJSONObject(index-1);

                        makeJsonArrayGetVoucherGame(API.getInstance().getApiModifyCart()
                                +"?cartRef="
                                +"&pId=" + pulsa.getString("ID")+";"+phoneNumber.getText().toString()
                                +"&mod=add"
                                +"&qty=1"
                                +"&regionID=" + sessionManager.getRegionID()
                                +"&id="
                                +"&scId=" + sessionManager.getCartId()
                                +"&cId=" + sessionManager.getUserID()
                                +"&isPair=false"
                                +"&mfp_id=" + sessionManager.getKeyMfpId(), "modify");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        checkPulsaNumber();
        setEmptyPulsaList();
        return view;
    }

//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        if (requestCode == 1) {
//            if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
//                boolean showRationale = false;
//                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
//                    showRationale = shouldShowRequestPermissionRationale( permissions[0] );
//                }
//
//                if (!showRationale) {
//                    contentFrame.setVisibility(View.GONE);
//                    linear.setVisibility(View.VISIBLE);
//                    btnSetting.setVisibility(View.VISIBLE);
//                    btnRequest.setVisibility(View.GONE);
//                    infoText.setText("Fitur Pencarian Barcode tidak bisa dijalankan. Apabila Anda ingin menggunakan fitur Pencarian Barcode mohon mengaktifkan permission camera dengan menekan tombol pengaturan dibawah." +
//                            "\n\nMasuk <App permissions> kemudian aktifkan <Camera>.");
//                } else if (Manifest.permission.CAMERA.equals(permissions[0])) {
//                    contentFrame.setVisibility(View.GONE);
//                    linear.setVisibility(View.VISIBLE);
//                    btnSetting.setVisibility(View.GONE);
//                    btnRequest.setVisibility(View.VISIBLE);
//                    infoText.setText("Fitur Pencarian Barcode tidak bisa dijalankan. Apabila Anda ingin menggunakan fitur Pencarian Barcode mohon mengaktifkan permission camera dengan menekan tombol pengaturan dibawah.");
//                }
//            }
//        }
//    }

    public void setPhoneNumber(String number){
        phoneNumber.setText(number);
    }

    public void checkPulsaNumber(){
        phoneNumber.addTextChangedListener(
                new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {

                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        if (s.length() > 3) {
                            makeJsonArrayGetVoucherGame(API.getInstance().getApiCheckPulsaNumber() + "/" + s + "?mfp_id=" + sessionManager.getKeyMfpId(), "pulsa");
                        } else {
                            setEmptyPulsaList();
                    }
                    }
                }
        );
    }

    public void setEmptyPulsaList(){
        List<String> pulsaNominal = new ArrayList<>();
        pulsaNominal.add("-- Pilih Nominal --");
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(getActivity(),android.R.layout.simple_spinner_item, pulsaNominal);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        pulsaVoucherNominal.setAdapter(dataAdapter);
    }

    public void setPulsaList(JSONArray response){
        pulsaList = response;
        List<String> pulsaNominal = new ArrayList<>();
        pulsaArray = new JSONArray();

        if (response.length() == 0) pulsaNominal.add(" Kode provider anda tidak terdaftar ");
        else pulsaNominal.add(" -- Pilih Nominal -- ");

        try {
            for (int i=0; i<response.length(); i++){
                pulsaNominal.add(response.getJSONObject(i).getString("Title"));
                pulsaArray.put(response.getJSONObject(i));
            }
        } catch(Exception e){
            e.printStackTrace();
        }

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, pulsaNominal);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        pulsaVoucherNominal.setAdapter(dataAdapter);
    }

    public void makeJsonArrayGetVoucherGame(String url, final String check) {
        System.out.println("Url Virtual = "+url);
        JsonArrayRequest jsonArrayReq = new JsonArrayRequest(url,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        if(check.equals("pulsa")){
                            setPulsaList(response);
                        } else if(check.equals("cart")){
                            try {
                                ((MainActivity)getActivity()).updateCartTotal(response.getInt(0));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        } else if(check.equals("modify")){
                            addToCart(response);
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
//                Toast.makeText(getActivity(), "Gagal terhubung, geser kebawah untuk refresh", Toast.LENGTH_LONG).show();
            }
        }, getActivity());

        AppController.getInstance().addToRequestQueue(jsonArrayReq);
    }

    public void addToCart(JSONArray response){
        try {
            if (sessionManager.getCartId() == null || sessionManager.getCartId().equals("00000000-0000-0000-0000-000000000000")){
                sessionManager.setCartId(response.getJSONObject(0).getString("ResponseID"));
            }

            JSONObject cart = response.getJSONObject(0);
            System.out.println(response);

            if(sessionManager.getCartId() == null){
                if (cart.getString("ResponseID")!=null){
                    sessionManager.setCartId(cart.getString("ResponseID"));
                }
            }

            if(cart.getBoolean("Success")){
                makeJsonArrayGetVoucherGame(API.getInstance().getCartTotal()
                        +"?cartId=" + sessionManager.getCartId()
                        +"&customerId=" + sessionManager.getUserID()
                        +"&mfp_id=" + sessionManager.getKeyMfpId() , "cart");

                phoneNumber.setText("");
                btnBuyFacebook.setEnabled(false);
                btnBuyFacebook.setBackgroundResource(R.drawable.button_style_4);

                final Toast toast = Toast.makeText(getActivity(), "Produk masuk ke keranjang", Toast.LENGTH_SHORT);
                toast.show();

                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        toast.cancel();
                    }
                }, 500);
            } else{
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
                alertDialogBuilder.setMessage(cart.getString("ErrorMessage"));
                alertDialogBuilder.setNegativeButton("Tutup", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == getActivity().RESULT_OK) {
                Uri emailUri = data.getData();
                Cursor emailCursor = getContext().getContentResolver().query(emailUri, null, null, null, null);
                if (emailCursor != null) {
                    if (emailCursor.moveToFirst()) {
                        String name = emailCursor.getString(emailCursor.getColumnIndexOrThrow(ContactsContract.Contacts.DISPLAY_NAME));

                        for (int i=0; i<contactArray.length(); i++){
                            try {
                                if (name.equals(contactArray.getJSONObject(i).getString("Name"))){
                                    setPhoneNumber(contactArray.getJSONObject(i).getString("PhoneNumber").replace("+62", "0").replace("-", "").replace(" ", ""));
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    emailCursor.close();
                }
            }

        }
    }
}
