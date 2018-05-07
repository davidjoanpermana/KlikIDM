package com.indomaret.klikindomaret.fragment;

import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
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
public class PaketDataFragment extends Fragment {
    private View view;
    private int totalItemInCart;
    private TextView price;
    private EditText phoneNumberPaket;
    private Spinner paketInternetNominal;
    private LinearLayout buyPaketInternet;
    private SessionManager sessionManager;
    private JSONArray paketList = new JSONArray();
    private JSONArray paketArray;
    DecimalFormat df = new DecimalFormat("#,###");

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_paket_data, container, false);

        sessionManager = new SessionManager(getActivity());
        price = (TextView) view.findViewById(R.id.price);
        phoneNumberPaket = (EditText) view.findViewById(R.id.phone_number_paket);
        paketInternetNominal = (Spinner) view.findViewById(R.id.nominal_paket);
        buyPaketInternet = (LinearLayout) view.findViewById(R.id.btn_buy_paket);

        buyPaketInternet.setEnabled(false);

        paketInternetNominal.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position > 0){
                    buyPaketInternet.setEnabled(true);
                    buyPaketInternet.setBackgroundResource(R.drawable.button_style_1);

                    try {
                        price.setVisibility(View.VISIBLE);
                        price.setText(Html.fromHtml("Harga Jual : <b>Rp." + df.format(paketArray.getJSONObject(position-1).getDouble("HargaWebsite")).replace(",",".") + "</b>"));
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

        phoneNumberPaket.addTextChangedListener(
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
                            makeJsonArrayGetVoucherGame(API.getInstance().getApiGetPaketInternet() + "/" + s + "?mfp_id=" + sessionManager.getKeyMfpId(), "paket");
                        } else {
                            setEmptyPaketList();
                        }
                    }
                }
        );

        buyPaketInternet.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int index = paketInternetNominal.getSelectedItemPosition();

                        if(phoneNumberPaket.getText().toString().length() < 9){
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
                                JSONObject paket = paketList.getJSONObject(index-1);

                                makeJsonArrayGetVoucherGame(API.getInstance().getApiModifyCart()
                                        +"?cartRef="
                                        +"&pId=" + paket.getString("ID")+";"+phoneNumberPaket.getText().toString()
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
                }
        );

        setEmptyPaketList();
        return view;
    }

    public void setEmptyPaketList(){
        List<String> paketNominal = new ArrayList<>();
        paketNominal.add("-- Pilih Nominal --");
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(getActivity(),android.R.layout.simple_spinner_item, paketNominal);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        paketInternetNominal.setAdapter(dataAdapter);
    }

    public void makeJsonArrayGetVoucherGame(String url, final String check) {
        System.out.println("Url Virtual = "+url);
        JsonArrayRequest jsonArrayReq = new JsonArrayRequest(url,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        if(check.equals("paket")){
                            setPaketList(response);
                        } else if(check.equals("modify")){
                            addToCart(response);
                        }else if(check.equals("cart")){
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

    public  void setPaketList(JSONArray response){
        paketList = response;
        List<String> paketNominal = new ArrayList<>();
        paketArray = new JSONArray();

        if (response.length() == 0) paketNominal.add(" Kode provider anda tidak terdaftar ");
        else paketNominal.add("-- Pilih Nominal --");

        try {
            for (int i=0; i<response.length(); i++){
                paketNominal.add(response.getJSONObject(i).getString("Title"));
                paketArray.put(response.getJSONObject(i));
            }
        } catch(Exception e){
            e.printStackTrace();
        }

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, paketNominal);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        paketInternetNominal.setAdapter(dataAdapter);
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

                phoneNumberPaket.setText("");
                buyPaketInternet.setEnabled(false);
                buyPaketInternet.setBackgroundResource(R.drawable.button_style_4);

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
}
