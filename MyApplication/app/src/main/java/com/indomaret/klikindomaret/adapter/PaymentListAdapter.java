package com.indomaret.klikindomaret.adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.NetworkImageView;
import com.indomaret.klikindomaret.API;
import com.indomaret.klikindomaret.R;
import com.indomaret.klikindomaret.activity.CartActivity;
import com.indomaret.klikindomaret.activity.PaymentActivity;
import com.indomaret.klikindomaret.activity.PaymentKAIActivity;
import com.indomaret.klikindomaret.activity.PromoItemActivity;
import com.indomaret.klikindomaret.app.AppController;
import com.indomaret.klikindomaret.helper.SessionManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Created by indomaretitsd7 on 6/14/16.
 */
public class PaymentListAdapter extends BaseAdapter{
    private SessionManager sessionManager;
    private Intent intent;
    private LayoutInflater inflater;
    private Activity activity;
    private JSONArray promoProductCart, promoProduct;
    private ImageLoader imageLoader = AppController.getInstance().getImageLoader();
    private Double price, monthInstallment;
    private int tenor;
    private JSONObject payment, paymentObject;
    private String from = "";
    private List<String> paymentKrediCIMBtList = new ArrayList<>();
    private List<String> paymentKreditBCAList = new ArrayList<>();
    private List<JSONObject> paymentList = new ArrayList<>();
    private DecimalFormat df = new DecimalFormat("#,###");
    private JSONArray radioName = new JSONArray();
    private int status = 0;

    public PaymentListAdapter(Activity activity, List<JSONObject>  paymentList, JSONObject paymentObject, String from, JSONArray promoProduct, String codePayment){
        this.activity = activity;
        this.paymentList = paymentList;
        this.paymentObject = paymentObject;
        this.from = from;
        this.promoProduct = promoProduct;
    }

    @Override
    public int getCount() {
        return paymentList.size();
    }

    @Override
    public Object getItem(int position) {
        return paymentList.get(position);
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
            convertView = inflater.inflate(R.layout.list_payment, null);

        sessionManager = new SessionManager(activity);
        intent = activity.getIntent();

        final RadioButton radioButton = (RadioButton) convertView.findViewById(R.id.radio_button);
        TextView infoPromo = (TextView) convertView.findViewById(R.id.promo_info);
        ImageView paymentImage = (ImageView) convertView.findViewById(R.id.payment_image);
        NetworkImageView cardViewNetwork = (NetworkImageView) convertView.findViewById(R.id.card_view_payment_network);
        RecyclerView promoList = (RecyclerView) convertView.findViewById(R.id.promo_list);
        LinearLayout linearLayout = (LinearLayout) convertView.findViewById(R.id.linearLayout);
        LinearLayout linearPromo = (LinearLayout) convertView.findViewById(R.id.linear_promo);
        final Spinner spinnerPayment = (Spinner) convertView.findViewById(R.id.spinner_payment);
        LinearLayout linearInstallment = (LinearLayout) convertView.findViewById(R.id.linear_installment);
        TextView installmentText = (TextView) convertView.findViewById(R.id.installment_text);

        try {
            payment = paymentList.get(position);

            if (promoProduct != null && promoProduct.length() > 0){
                if (promoProduct.getJSONObject(0).getString("PaymentCode").equals(paymentList.get(position).getString("Code"))){
                    promoList.setVisibility(View.VISIBLE);
                    promoList.setHasFixedSize(true);
                    promoList.setLayoutManager(new LinearLayoutManager(activity));

                    JSONArray promoselected = new JSONArray();
                    for (int i=0; i<promoProduct.length(); i++){
                        try {
                            if (promoProduct.getJSONObject(i).getBoolean("IsSelectedPromo")){
                                promoselected.put(promoProduct.getJSONObject(i));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    PromoPaymentItemAdapter promoPaymentItemAdapter = new PromoPaymentItemAdapter(activity, promoselected);
                    promoList.setAdapter(promoPaymentItemAdapter);
                }else{
                    promoList.setVisibility(View.GONE);
                }
            }else{
                promoList.setVisibility(View.GONE);
            }

            if (from.equals("klikindomaret") && !paymentList.get(position).getString("Code").equals("message_installment")){
                if (payment.getBoolean("IsPromoPayment")){
                    infoPromo.setVisibility(View.VISIBLE);
                    infoPromo.setText("Promo "+payment.getString("Name"));
                }else{
                    infoPromo.setVisibility(View.GONE);
                }
            }else{
                infoPromo.setVisibility(View.GONE);
            }

            radioButton.setTag(position);

            if (paymentList.get(position).getString("Code").equals("message_installment")){
                linearInstallment.setVisibility(View.VISIBLE);
                radioButton.setVisibility(View.GONE);
                paymentImage.setVisibility(View.GONE);
                cardViewNetwork.setVisibility(View.GONE);
                spinnerPayment.setVisibility(View.GONE);

                linearLayout.setPadding(5,0,0,0);

                if (paymentList.get(position).getString("message") == null ||
                        paymentList.get(position).getString("message").equals("") ||
                        paymentList.get(position).getString("message").toLowerCase().equals("null")){
                    linearInstallment.setVisibility(View.GONE);
                }else{
                    installmentText.setText(paymentList.get(position).getString("message"));
                }
            }else{
                if (payment.getString("selected").equals("1")){
                    radioButton.setChecked(true);
                    if (paymentList.get(position).getString("Code").contains("500C")){
                        spinnerPayment.setVisibility(View.VISIBLE);
                    }else{
                        spinnerPayment.setVisibility(View.GONE);
                    }
                }else{
                    spinnerPayment.setVisibility(View.GONE);
                    radioButton.setChecked(false);
                }

                if(payment.getInt("PaymentPlan") == 01){
                    radioButton.setText(payment.getString("Name"));
                    linearLayout.setEnabled(true);
                    radioButton.setEnabled(true);
                    radioButton.setAlpha((float) 1.0);
                    linearInstallment.setVisibility(View.GONE);
                    radioButton.setVisibility(View.VISIBLE);
                    linearLayout.setPadding(5,5,5,5);

                    if (payment.getString("Code").equals("402")){
                        paymentImage.setVisibility(View.VISIBLE);
                        cardViewNetwork.setVisibility(View.GONE);
                        paymentImage.setImageResource(R.drawable.transfer);
                    } else if (payment.getString("Code").equals("405")){
                        paymentImage.setVisibility(View.VISIBLE);
                        cardViewNetwork.setVisibility(View.GONE);
                        paymentImage.setImageResource(R.drawable.bcaklikpay);
                    } else if (payment.getString("Code").equals("406")){
                        paymentImage.setVisibility(View.VISIBLE);
                        cardViewNetwork.setVisibility(View.GONE);
                        paymentImage.setImageResource(R.drawable.mandiriklikpay);
                    } else if (payment.getString("Code").equals("500")){
                        paymentImage.setVisibility(View.VISIBLE);
                        cardViewNetwork.setVisibility(View.GONE);
                        paymentImage.setImageResource(R.drawable.kredit);
                    } else if (payment.getString("Code").equals("BPPID")){
                        paymentImage.setVisibility(View.VISIBLE);
                        cardViewNetwork.setVisibility(View.GONE);
                        paymentImage.setImageResource(R.drawable.bppid);
                    } else if (payment.getString("Code").equals("RKPON")){
                        paymentImage.setVisibility(View.VISIBLE);
                        cardViewNetwork.setVisibility(View.GONE);
                        paymentImage.setImageResource(R.drawable.rkpon);
                    } else if (payment.getString("Code").equals("BPISAKU")){
                        paymentImage.setVisibility(View.VISIBLE);
                        cardViewNetwork.setVisibility(View.GONE);
                        paymentImage.setImageResource(R.drawable.isaku_logo);
                    }else if (payment.getString("Code").equals("COD")){
                        paymentImage.setVisibility(View.VISIBLE);
                        cardViewNetwork.setVisibility(View.GONE);
                        paymentImage.setImageResource(R.drawable.cod);
                    } else if (payment.getString("Code").equals("702")){
                        paymentImage.setVisibility(View.VISIBLE);
                        cardViewNetwork.setVisibility(View.GONE);
                        paymentImage.setImageResource(R.drawable.bca_virtual);
                    } else if (payment.getString("Code").equals("302")){
                        paymentImage.setVisibility(View.VISIBLE);
                        cardViewNetwork.setVisibility(View.GONE);
                        paymentImage.setImageResource(R.drawable.t_cash);
                    } else if (payment.getString("Code").equals("303")){
                        paymentImage.setVisibility(View.VISIBLE);
                        cardViewNetwork.setVisibility(View.GONE);
                        paymentImage.setImageResource(R.drawable.xl_tunai);
                    } else{
                        paymentImage.setVisibility(View.GONE);
                        cardViewNetwork.setVisibility(View.VISIBLE);
                        cardViewNetwork.setImageUrl("https://payment.klikindomaret.com/Asset/image/"+payment.getString("Code")+".jpg", imageLoader);
                    }
                } else if(payment.getInt("PaymentPlan") == 02){
                    cardViewNetwork.setVisibility(View.GONE);
                    paymentImage.setVisibility(View.VISIBLE);
                    paymentImage.setImageResource(R.drawable.bkkcn);
                    linearInstallment.setVisibility(View.GONE);
                    radioButton.setVisibility(View.VISIBLE);

                    if (status < position){
                        status = position;
                    }else{
                        status = position;
                        radioName = new JSONArray();
                    }

                    if (payment.getString("Name").split("-")[0].toLowerCase().contains("bca")){
                        if (!radioName.toString().contains("bca")){
                            radioButton.setVisibility(View.VISIBLE);
                            radioButton.setText(payment.getString("Name").split("-")[0]);
                            radioName.put("bca");
                        }else{
                            radioButton.setVisibility(View.GONE);
                        }
                    }else if (payment.getString("Name").split("-")[0].toLowerCase().contains("cimb")){
                        if (!radioName.toString().contains("cimb")){
                            radioButton.setVisibility(View.VISIBLE);
                            radioButton.setText(payment.getString("Name").split("-")[0]);
                            radioName.put("cimb");
                        }else{
                            radioButton.setVisibility(View.GONE);
                        }
                    }

                    if (paymentList.get(position).getString("Code").contains("500C")){
                        paymentImage.setVisibility(View.VISIBLE);

                        if (from.equals("kai")){
                            price = paymentObject.getDouble("GrandTotalBeforeVoucher");

                            if (paymentObject.getInt("PaymentStatus") == 1){
                                linearLayout.setEnabled(true);
                                radioButton.setEnabled(true);
                                radioButton.setAlpha((float) 1.0);
                            }else{
                                linearLayout.setEnabled(false);
                                radioButton.setEnabled(false);
                                radioButton.setAlpha((float) 0.3);
                            }
                        }else{
                            price = paymentObject.getDouble("TotalOrder");
                        }

                        tenor = payment.getInt("Tenor");
                        monthInstallment = price/tenor;

                        if (paymentList.get(position).getString("Code").contains("BCA")){
                            paymentImage.setImageResource(R.drawable.logo_bca);

                            if (!paymentKreditBCAList.toString().contains("Pilih Cicilan")){
                                paymentKreditBCAList.add("Pilih Cicilan");
                            }

                            if (!paymentKreditBCAList.toString().contains(payment.getString("Name")+ " (Rp " + df.format(monthInstallment).replace(",",".") +" / bulan)"))
                                paymentKreditBCAList.add(payment.getString("Name")+ " (Rp " + df.format(monthInstallment).replace(",",".") +" / bulan)");

                            ArrayAdapter<String> adapter = new ArrayAdapter<>(activity, android.R.layout.simple_spinner_item, paymentKreditBCAList);
                            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            spinnerPayment.setAdapter(adapter);

                            spinnerPayment.setSelection(sessionManager.getKeyCuurentSpinner());
                        }else {
                            paymentImage.setImageResource(R.drawable.logo_cimb);

                            if (!paymentKrediCIMBtList.toString().contains("Pilih Cicilan")){
                                paymentKrediCIMBtList.add("Pilih Cicilan");
                            }

                            if (!paymentKrediCIMBtList.toString().contains(payment.getString("Name")+ " (Rp " + df.format(monthInstallment).replace(",",".") +" / bulan)"))
                                paymentKrediCIMBtList.add(payment.getString("Name")+ " (Rp " + df.format(monthInstallment).replace(",",".") +" / bulan)");

                            ArrayAdapter<String> adapter = new ArrayAdapter<>(activity, android.R.layout.simple_spinner_item, paymentKrediCIMBtList);
                            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            spinnerPayment.setAdapter(adapter);

                            spinnerPayment.setSelection(sessionManager.getKeyCuurentSpinner());
                        }
                    }else{
                        radioButton.setVisibility(View.VISIBLE);
                        linearLayout.setEnabled(true);
                        radioButton.setEnabled(true);
                        radioButton.setAlpha((float) 1.0);
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        spinnerPayment.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int positionSpinner, long id) {
                try {
                    if (spinnerPayment.getSelectedItemPosition() != 0 && sessionManager.getKeyEditSpinner()){
                        if (from.equals("kai")){
                            ((PaymentKAIActivity) activity).setRadioButton(paymentList.get(position), paymentList.get(position).getString("Code"));
                            ((PaymentKAIActivity) activity).setButtonBuy();
                        }else{
                            sessionManager.setKeyCuurentSpinner(positionSpinner);

                            if (paymentList.get(position).getBoolean("IsPromoPayment")){
                                jsonArrayRequest(API.getInstance().getApiPaymentPromo()
                                        +"?CartID="+sessionManager.getCartId()
                                        +"&CustomerId="+sessionManager.getUserID()
                                        +"&RegionID="+sessionManager.getRegionID()
                                        +"&WarehouseAddressID=00000000-0000-0000-0000-000000000000"
                                        +"&PaymentTypeID="+paymentList.get(position).getString("ID"), position);
                                ((PaymentActivity)activity).runLoader();
                            }else{
                                ((PaymentActivity) activity).setSpinner(paymentList.get(position));
                            }
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        infoPromo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View convertView = null;

                if (convertView == null) {
                    convertView = inflater.inflate(R.layout.dialog_info, null);
                }

                TextView inputArea = (TextView) convertView.findViewById(R.id.info);
                Button btnClose = (Button) convertView.findViewById(R.id.btn_close);

                StringBuilder infoPromo = new StringBuilder();

                try {
                    for (int i=0; i<paymentObject.getJSONArray("PromoProducts").length(); i++){
                        if (paymentObject.getJSONArray("PromoProducts").getJSONObject(i).getString("KodeBin").equals(paymentList.get(position).getString("KodeBIN"))){
                            infoPromo.append(paymentObject.getJSONArray("PromoProducts").getJSONObject(i).getString("PromoDesc") + "\n\n");
                        }
                    }

                    inputArea.setText(infoPromo);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(activity);
                alertDialogBuilder.setView(convertView);
                final AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                alertDialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
                alertDialog.setCanceledOnTouchOutside(false);
                alertDialog.show();

                btnClose.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        alertDialog.dismiss();
                    }
                });
            }
        });

        linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (!paymentList.get(position).getString("Code").contains("message_installment")){
                        if (from.equals("kai")){
                            radioButton.setChecked(true);
                            ((PaymentKAIActivity) activity).setRadioButton(paymentList.get(position), paymentList.get(position).getString("Code"));
                            ((PaymentKAIActivity) activity).setButtonBuy();
                        }else{
                            sessionManager.setKeyCuurentSpinner(0);

                            if (paymentList.get(position).getString("Code").contains("500C")){
                                sessionManager.setKeyEditSpinner(true);
                            }else{
                                sessionManager.setKeyEditSpinner(false);
                            }

                            if (paymentList.get(position).getBoolean("IsPromoPayment")  && !paymentList.get(position).getString("Code").contains("500C")){
                                jsonArrayRequest(API.getInstance().getApiPaymentPromo()
                                        +"?CartID="+sessionManager.getCartId()
                                        +"&CustomerId="+sessionManager.getUserID()
                                        +"&RegionID="+sessionManager.getRegionID()
                                        +"&WarehouseAddressID=00000000-0000-0000-0000-000000000000"
                                        +"&PaymentTypeID="+paymentList.get(position).getString("ID"), position);
                                ((PaymentActivity)activity).runLoader();
                            }else{
                                ((PaymentActivity) activity).setRadioButton(paymentList.get(position), "select");
                            }
                        }
                        notifyDataSetChanged();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        radioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (!paymentList.get(position).getString("Code").contains("message_installment")){
                        if (from.equals("kai")){
                            radioButton.setChecked(true);
                            ((PaymentKAIActivity) activity).setRadioButton(paymentList.get(position), paymentList.get(position).getString("Code"));
                            ((PaymentKAIActivity) activity).setButtonBuy();
                        }else{
                            sessionManager.setKeyCuurentSpinner(0);

                            if (paymentList.get(position).getString("Code").contains("500C")){
                                sessionManager.setKeyEditSpinner(true);
                            }else{
                                sessionManager.setKeyEditSpinner(false);
                            }

                            if (paymentList.get(position).getBoolean("IsPromoPayment") && !paymentList.get(position).getString("Code").contains("500C")){
                                if (promoProduct.length() > 0){
                                    if (!promoProduct.getJSONObject(0).getString("PaymentCode").equals(paymentList.get(position).getString("Code"))){
                                        promoProduct = new JSONArray();
                                    }
                                }

                                jsonArrayRequest(API.getInstance().getApiPaymentPromo()
                                        +"?CartID="+sessionManager.getCartId()
                                        +"&CustomerId="+sessionManager.getUserID()
                                        +"&RegionID="+sessionManager.getRegionID()
                                        +"&WarehouseAddressID=00000000-0000-0000-0000-000000000000"
                                        +"&PaymentTypeID="+paymentList.get(position).getString("ID"), position);
                                ((PaymentActivity)activity).runLoader();
                            }else{
                                ((PaymentActivity) activity).setRadioButton(paymentList.get(position), "select");
                            }
                        }
                        notifyDataSetChanged();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        try {
            if (payment.toString().toLowerCase().contains("IsDisabled")){
                if (payment.getBoolean("IsDisabled") && !paymentList.get(position).getString("Code").equals("message_installment")){
                    linearLayout.setEnabled(false);
                    radioButton.setEnabled(false);
                    radioButton.setAlpha((float) 0.3);
                } else if (!paymentObject.getBoolean("IsInstallment") && paymentList.get(position).getString("Code").contains("500C")){
                    linearLayout.setEnabled(false);
                    radioButton.setEnabled(false);
                    radioButton.setAlpha((float) 0.3);
                } else{
                    linearLayout.setEnabled(true);
                    radioButton.setEnabled(true);
                    radioButton.setAlpha((float) 1.0);
                }
            }else{
                if (!paymentObject.getBoolean("IsInstallment") && paymentList.get(position).getString("Code").contains("500C")){
                    linearLayout.setEnabled(false);
                    radioButton.setEnabled(false);
                    radioButton.setAlpha((float) 0.3);
                }else{
                    linearLayout.setEnabled(true);
                    radioButton.setEnabled(true);
                    radioButton.setAlpha((float) 1.0);
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return convertView;
    }

    public void jsonArrayRequest(String url, final int position){
        System.out.println("--- url payment promo : "+url);
        JsonArrayRequest jsonArrayReq = new JsonArrayRequest(url,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        if (response == null || response.length() == 0){
                            ((PaymentActivity)activity).stopLoader();
                            Toast.makeText(activity, "Gagal terkoneksi server", Toast.LENGTH_LONG).show();
                        }else{
                            try {
                                promoProductCart = response.getJSONObject(0).getJSONObject("ShoppingCartItemPromo").getJSONArray("Items");

                                intent = new Intent(activity, PromoItemActivity.class);
                                intent.putExtra("promo", promoProductCart.toString());
                                intent.putExtra("objectPayment", paymentList.get(position).toString());
                                intent.putExtra("codePayment", paymentList.get(position).getString("Code"));
                                intent.putExtra("promoProduct", promoProduct.toString());
                                activity.startActivityForResult(intent, 3);

                                ((PaymentActivity)activity).stopLoader();
                            } catch (JSONException e) {
                                e.printStackTrace();
                                ((PaymentActivity)activity).stopLoader();
                            }
                        }
                    }
                },new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(activity, "Gagal terkoneksi server", Toast.LENGTH_LONG).show();
                ((PaymentActivity)activity).stopLoader();
            }
        }, activity);

        jsonArrayReq.setRetryPolicy(new DefaultRetryPolicy(60000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppController.getInstance().addToRequestQueue(jsonArrayReq);
    }
}