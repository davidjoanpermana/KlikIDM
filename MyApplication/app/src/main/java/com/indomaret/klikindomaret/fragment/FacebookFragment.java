package com.indomaret.klikindomaret.fragment;

import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
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

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class FacebookFragment extends Fragment {
    private View view;
    private int totalItemInCart;
    private Spinner facebookCreditNominal;
    private LinearLayout buyFacbookCredit;
    private SessionManager sessionManager;
    private JSONArray facebokCreditList = new JSONArray();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_facebook, container, false);

        sessionManager = new SessionManager(getActivity());
        facebookCreditNominal = (Spinner) view.findViewById(R.id.nominal_facebookCredit);
        buyFacbookCredit = (LinearLayout) view.findViewById(R.id.btn_buy_facebookCredit);

        buyFacbookCredit.setEnabled(false);
        makeJsonArrayGetVoucherGame(API.getInstance().getApiGetVoucherFacebook()+"?mfp_id="+sessionManager.getKeyMfpId(), "facebook");

        facebookCreditNominal.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position > 0){
                    buyFacbookCredit.setEnabled(true);
                    buyFacbookCredit.setBackgroundResource(R.drawable.button_style_1);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        buyFacbookCredit.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int index = facebookCreditNominal.getSelectedItemPosition();
                        if(index > 0){
                            try {
                                JSONObject voucher = facebokCreditList.getJSONObject(index-1);

                                makeJsonArrayGetVoucherGame(API.getInstance().getApiModifyCart()
                                        +"?cartRef="
                                        +"&pId=" + voucher.getString("ID")
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

    public void makeJsonArrayGetVoucherGame(String url, final String check) {
        System.out.println("Url Virtual = "+url);
        JsonArrayRequest jsonArrayReq = new JsonArrayRequest(url,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        if (response == null || response.length() == 0){
                            Toast.makeText(getActivity(), "Gagal terkoneksi server", Toast.LENGTH_LONG).show();
                        }else{
                            if(check.equals("facebook")){
                                setFacebookCreditList(response);
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
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
//                Toast.makeText(getActivity(), "Gagal terhubung, geser kebawah untuk refresh", Toast.LENGTH_LONG).show();
            }
        }, getActivity());

        AppController.getInstance().addToRequestQueue(jsonArrayReq);
    }

    public  void setFacebookCreditList(JSONArray response){
        facebokCreditList = response;

        List<String> facebookCredit = new ArrayList<>();
        facebookCredit.add(" -- Pilih Voucher -- ");
        try {
            for (int i = 0; i < response.length(); i++){
                facebookCredit.add(response.getJSONObject(i).getString("Title"));
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(getActivity(),android.R.layout.simple_spinner_item, facebookCredit);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        facebookCreditNominal.setAdapter(dataAdapter);
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
