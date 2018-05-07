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
public class VoucherFragment extends Fragment {
    private View view;
    private TextView price;
    private Spinner gameVoucherNominal;
    private LinearLayout buyVoucher;
    private SessionManager sessionManager;
    private JSONArray voucherArray;
    private JSONArray voucherList = new JSONArray();
    DecimalFormat df = new DecimalFormat("#,###");

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_voucher, container, false);

        sessionManager = new SessionManager(getActivity());
        price = (TextView) view.findViewById(R.id.price);
        gameVoucherNominal = (Spinner) view.findViewById(R.id.nominal_voucher);
        buyVoucher = (LinearLayout) view.findViewById(R.id.btn_buy_voucher);

        buyVoucher.setEnabled(false);
        makeJsonArrayGetVoucherGame(API.getInstance().getApiVirtual()+"?mfp_id="+sessionManager.getKeyMfpId(), "virtual");

        gameVoucherNominal.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position > 0){
                    buyVoucher.setEnabled(true);
                    buyVoucher.setBackgroundResource(R.drawable.button_style_1);

                    try {
                        price.setVisibility(View.VISIBLE);
                        price.setText(Html.fromHtml("Harga Jual : <b>Rp." + df.format(voucherArray.getJSONObject(position-1).getDouble("Price")).replace(",",".") + "</b>"));
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

        buyVoucher.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ((MainActivity)getActivity()).runLoader();
                        int index = gameVoucherNominal.getSelectedItemPosition();
                        if(index > 0){
                            try {
                                JSONObject voucher = voucherList.getJSONObject(index-1);

                                makeJsonArrayGetVoucherGame(API.getInstance().getApiModifyCart()
                                        +"?cartRef="
                                        +"&pId=" + voucher.getString("ProductId")
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

        return view;
    }

    public void setEmptyPaketList(){
        List<String> paketNominal = new ArrayList<>();
        paketNominal.add("Isikan Nomor Handphone");
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(getActivity(),android.R.layout.simple_spinner_item, paketNominal);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        gameVoucherNominal.setAdapter(dataAdapter);
    }

    public void makeJsonArrayGetVoucherGame(String url, final String check) {
        System.out.println("Url Virtual = "+url);
        JsonArrayRequest jsonArrayReq = new JsonArrayRequest(url,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        if (response == null || response.length() == 0){
                            ((MainActivity)getActivity()).stopLoader();
                            Toast.makeText(getActivity(), "Gagal terkoneksi server", Toast.LENGTH_LONG).show();
                        }else{
                            if(check.equals("virtual")){
                                try {
                                    setGameVoucherList(response.getJSONObject(0).getJSONArray("ListVoucherGameOnline"));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            } else if(check.equals("modify")){
                                addToCart(response);
                            }else if(check.equals("cart")){
                                try {
                                    ((MainActivity)getActivity()).updateCartTotal(response.getInt(0));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
//                Toast.makeText(getActivity(), "Gagal terhubung, geser kebawah untuk refresh", Toast.LENGTH_LONG).show();
                ((MainActivity)getActivity()).stopLoader();
            }
        }, getActivity());

        AppController.getInstance().addToRequestQueue(jsonArrayReq);
    }

    public void setGameVoucherList(JSONArray response){
        voucherList = response;
        List<String> voucherName = new ArrayList<>();
        voucherArray = new JSONArray();

        voucherName.add(" -- Pilih Voucher -- ");
        try {
            for (int i=0; i<response.length(); i++){
                voucherName.add(response.getJSONObject(i).getString("Title"));
                voucherArray.put(response.getJSONObject(i));
            }
        } catch(Exception e){
            e.printStackTrace();
        }

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(getActivity(),android.R.layout.simple_spinner_item, voucherName);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        gameVoucherNominal.setAdapter(dataAdapter);
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

                gameVoucherNominal.setSelection(0);
                buyVoucher.setEnabled(false);
                buyVoucher.setBackgroundResource(R.drawable.button_style_4);

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

            ((MainActivity)getActivity()).stopLoader();
        } catch (JSONException e) {
            e.printStackTrace();
            ((MainActivity)getActivity()).stopLoader();
        }
    }
}
