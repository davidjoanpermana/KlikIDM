package com.indomaret.klikindomaret.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.JsonArrayRequest;
import com.indomaret.klikindomaret.API;
import com.indomaret.klikindomaret.R;
import com.indomaret.klikindomaret.activity.CartActivity;
import com.indomaret.klikindomaret.app.AppController;
import com.indomaret.klikindomaret.fragment.PromoFragment;
import com.indomaret.klikindomaret.helper.SessionManager;
import com.indomaret.klikindomaret.views.NetworkImagesView;

import org.json.JSONArray;
import org.json.JSONException;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by USER on 4/18/2016.
 */
public class PromoAdapter extends BaseAdapter {
    private LayoutInflater inflater;
    private Activity activity;
    private SessionManager sessionManager;

    private JSONArray productsPromo;
    private int maxQty;
    private int thisQty = 0;
    private boolean isEdit = false;
    private boolean isSelected = false;
    private Map<Integer, Boolean> mapSelected = new HashMap<>();
    private List<String> stock = new ArrayList<>();
    private int is;
    private boolean status;
    private List<Integer> addPosition = new ArrayList<>();
    private PromoFragment promoFragment;

    private ImageLoader imageLoader = AppController.getInstance().getImageLoader();
    private DecimalFormat df = new DecimalFormat("#,###");

    public PromoAdapter(Activity activity, JSONArray productsPromo, int maxQty, int is, PromoFragment promoFragment){
        this.activity = activity;
        this.productsPromo = productsPromo;
        this.maxQty = maxQty;
        thisQty = 0;
        sessionManager = new SessionManager(activity);
        this.is = is;
        this.promoFragment = promoFragment;
    }

    public PromoAdapter(Activity activity, JSONArray productsPromo, int maxQty, List<String> stock, int is, PromoFragment promoFragment){
        this.activity = activity;
        this.productsPromo = productsPromo;
        this.maxQty = maxQty;
        thisQty = 0;
        this.stock = stock;
        sessionManager = new SessionManager(activity);
        this.is = is;
        this.promoFragment = promoFragment;
    }

    @Override
    public int getCount() {
        return productsPromo.length();
    }

    @Override
    public Object getItem(int position) {
        try {
            return productsPromo.getJSONObject(position);
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
    public View getView(final int position, View convertView, ViewGroup parent) {

        if (inflater == null)
            inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (convertView == null)
            convertView = inflater.inflate(R.layout.activity_cart_promo_single_list_item, null);

        final RelativeLayout promoContainer = (RelativeLayout) convertView.findViewById(R.id.promo_product_container);
        NetworkImagesView imageProduct = (NetworkImagesView) convertView.findViewById(R.id.image_product);
        final LinearLayout imageCheck = (LinearLayout) convertView.findViewById(R.id.image_check);
        RelativeLayout qtyMinus = (RelativeLayout) convertView.findViewById(R.id.qty_minus);
        RelativeLayout qtyPlus = (RelativeLayout) convertView.findViewById(R.id.qty_plus);
        LinearLayout linearPromo = (LinearLayout) convertView.findViewById(R.id.Linear_promo);
        LinearLayout linearGkupon = (LinearLayout) convertView.findViewById(R.id.Linear_gkupon);

        final EditText productQty = (EditText) convertView.findViewById(R.id.product_quantity);

        TextView productName  = (TextView) convertView.findViewById(R.id.cart_single_product_name);
        TextView sendFrom = (TextView) convertView.findViewById(R.id.cart_single_product_send_from);
        TextView productPrice = (TextView) convertView.findViewById(R.id.cart_single_product_price);
        TextView initialPrice = (TextView) convertView.findViewById(R.id.cart_single_product_initial_price);
        TextView titleGkupon = (TextView) convertView.findViewById(R.id.title_gkupon);
        TextView descGkupon = (TextView) convertView.findViewById(R.id.desc_gkupon);
        TextView btnDetail = (TextView) convertView.findViewById(R.id.btn_detail);
        final TextView totalPrice = (TextView) convertView.findViewById(R.id.total_price);
        TextView promoInitialPrice = (TextView) convertView.findViewById(R.id.cart_single_product_initial_price);
        TextView productStock = (TextView) convertView.findViewById(R.id.product_qty_stock);
        final TextView checkList = (CheckBox) convertView.findViewById(R.id.checklist);
        RecyclerView messageCountdown = (RecyclerView) convertView.findViewById(R.id.message_countdown);
        promoInitialPrice.setPaintFlags(promoInitialPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

        int qty = 0;
        try {
            try {
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                final Date eventDate = dateFormat.parse(productsPromo.getJSONObject(position).getString("ProductExpired").split("\\.")[0].replace("T", " "));
                final Date currentDate = new Date();

                if (!currentDate.after(eventDate)) {
                    messageCountdown.setVisibility(View.VISIBLE);

                    messageCountdown.setHasFixedSize(true);
                    messageCountdown.setLayoutManager(new LinearLayoutManager(activity));
                    messageCountdown.setAdapter(new CountDownAdapter(activity, productsPromo.getJSONObject(position).getString("ProductExpired").split("\\.")[0].replace("T", " "), ""));
                }else{
                    messageCountdown.setVisibility(View.GONE);
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }


            isSelected = productsPromo.getJSONObject(position).getBoolean("IsSelectedPromo");
            mapSelected.put(position, isSelected);
            btnDetail.setText(Html.fromHtml("<u>Selengkapnya</u>"));

            if (productsPromo.getJSONObject(position).getBoolean("IsQuantityUpdate")){
                qty = productsPromo.getJSONObject(position).getInt("QuantityUpdate");
            } else {
                qty = productsPromo.getJSONObject(position).getInt("Quantity");
            }

            if (!isEdit) productQty.setText(String.valueOf(qty));

            if (productsPromo.getJSONObject(position).toString().contains("IsGKuponPromo") &&
                     productsPromo.getJSONObject(position).getBoolean("IsGKuponPromo")){
                linearPromo.setVisibility(View.GONE);
                totalPrice.setVisibility(View.GONE);
                linearGkupon.setVisibility(View.VISIBLE);

                if (productsPromo.getJSONObject(position).getString("CouponPrizeTitle") == null ||
                        productsPromo.getJSONObject(position).getString("CouponPrizeTitle").toLowerCase().equals("null") ||
                        productsPromo.getJSONObject(position).getString("CouponPrizeTitle").length() == 0){
                    titleGkupon.setText("");
                }else{
                    titleGkupon.setText(productsPromo.getJSONObject(position).getString("CouponPrizeTitle"));
                }

                if (productsPromo.getJSONObject(position).getString("CouponPrizeMechanism") == null ||
                        productsPromo.getJSONObject(position).getString("CouponPrizeMechanism").toLowerCase().equals("null") ||
                        productsPromo.getJSONObject(position).getString("CouponPrizeMechanism").length() == 0){
                    descGkupon.setText("");
                }else{
                    descGkupon.setText(productsPromo.getJSONObject(position).getString("CouponPrizeMechanism"));
                }
            } else {
                linearPromo.setVisibility(View.VISIBLE);
                totalPrice.setVisibility(View.GONE);
                linearGkupon.setVisibility(View.GONE);

                imageProduct.setImageUrl(productsPromo.getJSONObject(position).getJSONObject("Product").getString("ImageThumb"), imageLoader);
                productName.setText(productsPromo.getJSONObject(position).getJSONObject("Product").getString("Description"));

                if(productsPromo.getJSONObject(position).getJSONObject("Product").getString("ProductFlag").toLowerCase().equals("store")){
                    sendFrom.setText("Dikirim dari Toko");
                } else if(productsPromo.getJSONObject(position).getJSONObject("Product").getString("ProductFlag").toLowerCase().equals("plaza")){
                    if(productsPromo.getJSONObject(position).getJSONObject("Product").getString("Flag_Produk").substring(0,1).equals("B")){
                        sendFrom.setText("Dikirim dari Penjual");
                    } else if(productsPromo.getJSONObject(position).getJSONObject("Product").getString("Flag_Produk").substring(0,1).equals("D")){
                        sendFrom.setText("Dikirim dari Gudang");
                    }
                }
            }

            btnDetail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(activity);
                        alertDialogBuilder.setMessage(productsPromo.getJSONObject(position).getString("CouponPrizeMechanism"));
                        alertDialogBuilder.setNegativeButton("Tutup", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface arg0, int arg1) {}
                        });

                        AlertDialog alertDialog = alertDialogBuilder.create();
                        alertDialog.setCanceledOnTouchOutside(false);
                        alertDialog.show();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });

            final Double priceWithDiscount = productsPromo.getJSONObject(position).getDouble("PriceWithDiscount");
            Double price = productsPromo.getJSONObject(position).getDouble("Price");
            Double discount = productsPromo.getJSONObject(position).getDouble("Discount");

            if(mapSelected.get(position) && !isEdit){
                promoContainer.setBackgroundResource(R.drawable.card_product_style_2);
                imageCheck.setBackgroundResource(R.color.colorPrimary);
                status = false;

                for (int i=0; i<addPosition.size(); i++){
                    if (position == addPosition.get(i)) status = true;
                }

                if (!status){
                    thisQty += qty;
                    addPosition.add(position);
                    promoFragment.setSisaPromo(thisQty);
                }

                promoContainer.setAlpha((float) 1);
                promoContainer.setEnabled(true);
            } else if(!mapSelected.get(position) && !isEdit) {
                promoContainer.setBackgroundResource(R.drawable.card_product_style_1);
                imageCheck.setBackgroundResource(R.color.backgroundGrey);

                if (thisQty == maxQty){
                    promoContainer.setAlpha((float) 0.2);
                    promoContainer.setEnabled(false);
                }else{
                    promoContainer.setAlpha((float) 1);
                    promoContainer.setEnabled(true);
                }
            }

            ((CartActivity)activity).setTabView(is, thisQty, maxQty);

            if(!mapSelected.get(position)) {
                if (thisQty == maxQty){
                    promoContainer.setAlpha((float) 0.2);
                    promoContainer.setEnabled(false);
                }else{
                    promoContainer.setAlpha((float) 1);
                    promoContainer.setEnabled(true);
                }
            }

//            if (!isSelected && position == 0 && index == 0 && !isEdit){
//                isEdit = true;
//
//                if (priceWithDiscount <= 0.0){
//                    jsonArrayRequest(API.getInstance().getAPiSetSelectPromo()
//                            + "?isSelect=" + true
//                            + "&mfp_id=" + sessionManager.getKeyMfpId()
//                            + "&plu=" + productsPromo.getJSONObject(position).getJSONObject("Product").getString("PLU")
//                            + "&promoCode=" + productsPromo.getJSONObject(position).getString("PromoCode")
//                            + "&shoppingCartID=" + sessionManager.getCartId()
//                            + "&quantity=" + qty, position, "");
//
//                    if (productsPromo.getJSONObject(position).getBoolean("IsQuantityUpdate")){
//                        qty = productsPromo.getJSONObject(position).getInt("QuantityUpdate");
//                    } else {
//                        qty = productsPromo.getJSONObject(position).getInt("Quantity");
//                    }
//
//                    promoContainer.setBackgroundResource(R.drawable.card_product_style_2);
//                    imageCheck.setBackgroundResource(R.color.colorPrimary);
//
//                    mapSelected.put(position, true);
//                    productsPromo.getJSONObject(position).put("IsSelectedPromo", true);
//
//                    thisQty = qty;
//                    productQty.setText(""+thisQty);
//                    promoFragment.setSisaPromo(thisQty);
//                    ((CartActivity)activity).setTabView(is, thisQty);
//                }
//            }

            if (!mapSelected.toString().contains("true")){
                promoFragment.setSisaPromo(0);
            }

            productPrice.setText("Rp " + df.format(priceWithDiscount).replace(",","."));

            if (discount == 0.0){
                initialPrice.setVisibility(View.GONE);
            } else {
                initialPrice.setText("Rp " + df.format(price).replace(",","."));
                initialPrice.setVisibility(View.VISIBLE);
            }

            totalPrice.setText("Rp " + df.format(priceWithDiscount * Integer.parseInt(String.valueOf(productQty.getText()))).replace(",","."));

            if (stock.size() > 0){
                String plus = productsPromo.getJSONObject(position).getJSONObject("Product").getString("PLU");
                int qtyBuy = productsPromo.getJSONObject(position).getInt("Quantity");
                productStock.setVisibility(View.GONE);

                for (int i=0; i<stock.size(); i++){
                    String[] pluStock = stock.get(i).replace("}", "").split("\\|");

                    if(pluStock[0].equals(plus)){
                        String sisaStock = pluStock[1].replace("\"","");

                        if(Integer.parseInt(sisaStock) == 0 || Integer.parseInt(sisaStock) < qtyBuy || pluStock[2].equalsIgnoreCase("false")){
                            if(Integer.parseInt(sisaStock) <= 0){
                                productStock.setText("Persediaan Kosong");
                                productStock.setVisibility(View.VISIBLE);
                            } else {
                                productStock.setText("Sisa stok " + sisaStock);
                                productStock.setVisibility(View.VISIBLE);
                            }
                        }
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        qtyPlus.setOnClickListener(
            new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(mapSelected.get(position)) {
                        int qtyText = Integer.parseInt(productQty.getText().toString());

                        Double priceWithDiscount = null;
                        try {
                            priceWithDiscount = productsPromo.getJSONObject(position).getDouble("PriceWithDiscount");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        if (thisQty < maxQty){
                            isEdit = true;
                            thisQty += 1;
                            qtyText += 1;
                            productQty.setText(qtyText + "");
                            promoFragment.setSisaPromo(thisQty);
                            ((CartActivity)activity).setTabView(is, thisQty, maxQty);

                            try {
                                jsonArrayRequest(API.getInstance().getAPiSetSelectPromo()
                                        +"?isSelect=" + true
                                        +"&mfp_id=" + sessionManager.getKeyMfpId()
                                        +"&plu=" + productsPromo.getJSONObject(position).getJSONObject("Product").getString("PLU")
                                        +"&promoCode=" + productsPromo.getJSONObject(position).getString("PromoCode")
                                        +"&shoppingCartID=" + sessionManager.getCartId()
                                        +"&quantity=" + qtyText,"plus");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }else {
                            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(activity);
                            alertDialogBuilder.setMessage("Jumlah produk promo ini tidak boleh lebih dari " + maxQty);
                            alertDialogBuilder.setNegativeButton("Tutup", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface arg0, int arg1) {}
                            });

                            AlertDialog alertDialog = alertDialogBuilder.create();
                            alertDialog.setCanceledOnTouchOutside(false);
                            alertDialog.show();
                        }

                        totalPrice.setText("Rp " + df.format(priceWithDiscount * Integer.parseInt(String.valueOf(productQty.getText()))).replace(",","."));
                    }
                }
            }
        );

        qtyMinus.setOnClickListener(
            new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(mapSelected.get(position)) {
                        int qtyText = Integer.parseInt(productQty.getText().toString());

                        Double priceWithDiscount = null;
                        try {
                            priceWithDiscount = productsPromo.getJSONObject(position).getDouble("PriceWithDiscount");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        if(qtyText != 1){
                            isEdit = true;
                            thisQty -= 1;
                            qtyText -= 1;
                            productQty.setText(qtyText + "");
                            promoFragment.setSisaPromo(thisQty);
                            ((CartActivity)activity).setTabView(is, thisQty, maxQty);

                            try {
                                jsonArrayRequest(API.getInstance().getAPiSetSelectPromo()
                                        +"?isSelect=" + true
                                        +"&mfp_id=" + sessionManager.getKeyMfpId()
                                        +"&plu=" + productsPromo.getJSONObject(position).getJSONObject("Product").getString("PLU")
                                        +"&promoCode=" + productsPromo.getJSONObject(position).getString("PromoCode")
                                        +"&shoppingCartID=" + sessionManager.getCartId()
                                        +"&quantity=" + qtyText,"minus");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        totalPrice.setText("Rp " + df.format(priceWithDiscount * Integer.parseInt(String.valueOf(productQty.getText()))).replace(",","."));
                    }
                }
            }
        );

        promoContainer.setOnClickListener(
            new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    isEdit = true;

                    try {
                        Double priceWithDiscount = productsPromo.getJSONObject(position).getDouble("PriceWithDiscount");

                        if(mapSelected.get(position)) {
                            int qtyText = Integer.parseInt(productQty.getText().toString());
                            thisQty -= qtyText;
                            productQty.setText("1");
                            promoFragment.setSisaPromo(thisQty);

                            String url =API.getInstance().getAPiSetSelectPromo()
                                    + "?isSelect=" + false
                                    + "&mfp_id=" + sessionManager.getKeyMfpId()
                                    + "&plu=" + productsPromo.getJSONObject(position).getJSONObject("Product").getString("PLU")
                                    + "&promoCode=" + productsPromo.getJSONObject(position).getString("PromoCode")
                                    + "&shoppingCartID=" + sessionManager.getCartId()
                                    + "&quantity=" + 1;

                            JsonArrayRequest jsonArrayReq = new JsonArrayRequest(url,
                                    new Response.Listener<JSONArray>() {
                                        @Override
                                        public void onResponse(JSONArray response) {
                                            try {
                                                if (response == null || response.length() == 0){
                                                    Toast.makeText(activity, "Gagal terkoneksi server", Toast.LENGTH_LONG).show();
                                                }else{
                                                    if (response.getJSONObject(0).getString("Message").equals("success")){
                                                        promoContainer.setBackgroundResource(R.drawable.card_product_style_1);
                                                        imageCheck.setBackgroundResource(R.color.backgroundGrey);
                                                        mapSelected.put(position, false);
                                                        productsPromo.getJSONObject(position).put("IsSelectedPromo", false);

                                                        ((CartActivity)activity).setTabView(is, thisQty, maxQty);
                                                        ((CartActivity) activity).loadPromo("select");
                                                        notifyDataSetChanged();
                                                    }else{
                                                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(activity);
                                                        alertDialogBuilder.setMessage(response.getJSONObject(0).getString("Message"));
                                                        alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                                            @Override
                                                            public void onClick(DialogInterface arg0, int arg1) {
                                                            }
                                                        });

                                                        AlertDialog alertDialog = alertDialogBuilder.create();
                                                        alertDialog.setCanceledOnTouchOutside(false);
                                                        alertDialog.show();
                                                    }
                                                }
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    }, new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    Log.i("url error", error.toString());
                                    Toast.makeText(activity, "Gagal terkoneksi server", Toast.LENGTH_LONG).show();
                                }
                            }, activity);

                            jsonArrayReq.setRetryPolicy(new DefaultRetryPolicy(60000,
                                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                            AppController.getInstance().addToRequestQueue(jsonArrayReq);
                        } else {
                            if (priceWithDiscount <= 0.0){
                                productQty.setText("1");
                            }else if(thisQty == 0 && position != 0){
                                productQty.setText(productQty.getText().toString());
                            }else if(thisQty > 0 && thisQty < maxQty){
                                productQty.setText("1");
                            }

                            int qtyText = Integer.parseInt(productQty.getText().toString());
                            int tempQTY = thisQty + qtyText;

                            if(tempQTY <= maxQty){
                                thisQty += qtyText;
                                promoFragment.setSisaPromo(thisQty);
                                String url = API.getInstance().getAPiSetSelectPromo()
                                        + "?isSelect=" + true
                                        + "&mfp_id=" + sessionManager.getKeyMfpId()
                                        + "&plu=" + productsPromo.getJSONObject(position).getJSONObject("Product").getString("PLU")
                                        + "&promoCode=" + productsPromo.getJSONObject(position).getString("PromoCode")
                                        + "&shoppingCartID=" + sessionManager.getCartId()
                                        + "&quantity=" + qtyText;

                                JsonArrayRequest jsonArrayReq = new JsonArrayRequest(url,
                                        new Response.Listener<JSONArray>() {
                                            @Override
                                            public void onResponse(JSONArray response) {
                                                try {
                                                    if (response == null || response.length() == 0){
                                                        Toast.makeText(activity, "Gagal terkoneksi server", Toast.LENGTH_LONG).show();
                                                    }else{
                                                        if (response.getJSONObject(0).getString("Message").equals("success")
                                                                || response.getJSONObject(0).getString("Message").equals("true")){
                                                            promoContainer.setBackgroundResource(R.drawable.card_product_style_2);
                                                            imageCheck.setBackgroundResource(R.color.colorPrimary);
                                                            mapSelected.put(position, true);
                                                            productsPromo.getJSONObject(position).put("IsSelectedPromo", true);

                                                            ((CartActivity)activity).setTabView(is, thisQty, maxQty);
                                                            ((CartActivity) activity).loadPromo("select");
                                                            notifyDataSetChanged();
                                                        }else{
                                                            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(activity);
                                                            alertDialogBuilder.setMessage(response.getJSONObject(0).getString("Message"));
                                                            alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                                                @Override
                                                                public void onClick(DialogInterface arg0, int arg1) {
                                                                }
                                                            });

                                                            AlertDialog alertDialog = alertDialogBuilder.create();
                                                            alertDialog.setCanceledOnTouchOutside(false);
                                                            alertDialog.show();
                                                        }
                                                    }
                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                        }, new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        Log.i("url error", error.toString());
                                        Toast.makeText(activity, "Gagal terkoneksi server", Toast.LENGTH_LONG).show();
                                    }
                                }, activity);

                                jsonArrayReq.setRetryPolicy(new DefaultRetryPolicy(60000,
                                        DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                                AppController.getInstance().addToRequestQueue(jsonArrayReq);

                            }
                        }

                        totalPrice.setText("Rp " + df.format(priceWithDiscount * Integer.parseInt(String.valueOf(productQty.getText()))).replace(",","."));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        );

        checkList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isEdit = true;

                try {
                    Double priceWithDiscount = productsPromo.getJSONObject(position).getDouble("PriceWithDiscount");

                    if(mapSelected.get(position)) {
                        int qtyText = Integer.parseInt(productQty.getText().toString());
                        thisQty -= qtyText;
                        productQty.setText("1");
                        promoFragment.setSisaPromo(thisQty);

                        String url =API.getInstance().getAPiSetSelectPromo()
                                + "?isSelect=" + false
                                + "&mfp_id=" + sessionManager.getKeyMfpId()
                                + "&plu=" + productsPromo.getJSONObject(position).getJSONObject("Product").getString("PLU")
                                + "&promoCode=" + productsPromo.getJSONObject(position).getString("PromoCode")
                                + "&shoppingCartID=" + sessionManager.getCartId()
                                + "&quantity=" + 1;

                        JsonArrayRequest jsonArrayReq = new JsonArrayRequest(url,
                                new Response.Listener<JSONArray>() {
                                    @Override
                                    public void onResponse(JSONArray response) {
                                        try {
                                            if (response == null || response.length() == 0){
                                                Toast.makeText(activity, "Gagal terkoneksi server", Toast.LENGTH_LONG).show();
                                            }else{
                                                if (response.getJSONObject(0).getString("Message").equals("success")){
                                                    promoContainer.setBackgroundResource(R.drawable.card_product_style_1);
                                                    imageCheck.setBackgroundResource(R.color.backgroundGrey);
                                                    mapSelected.put(position, false);
                                                    productsPromo.getJSONObject(position).put("IsSelectedPromo", false);

                                                    ((CartActivity)activity).setTabView(is, thisQty, maxQty);
                                                    ((CartActivity) activity).loadPromo("select");
                                                    notifyDataSetChanged();
                                                }else{
                                                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(activity);
                                                    alertDialogBuilder.setMessage(response.getJSONObject(0).getString("Message"));
                                                    alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface arg0, int arg1) {
                                                        }
                                                    });

                                                    AlertDialog alertDialog = alertDialogBuilder.create();
                                                    alertDialog.setCanceledOnTouchOutside(false);
                                                    alertDialog.show();
                                                }
                                            }
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Log.i("url error", error.toString());
                                Toast.makeText(activity, "Gagal terkoneksi server", Toast.LENGTH_LONG).show();
                            }
                        }, activity);

                        jsonArrayReq.setRetryPolicy(new DefaultRetryPolicy(60000,
                                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                        AppController.getInstance().addToRequestQueue(jsonArrayReq);
                    } else {
                        if (priceWithDiscount <= 0.0){
                            productQty.setText("1");
                        }else if(thisQty == 0 && position != 0){
                            productQty.setText(productQty.getText().toString());
                        }else if(thisQty > 0 && thisQty < maxQty){
                            productQty.setText("1");
                        }

                        int qtyText = Integer.parseInt(productQty.getText().toString());
                        int tempQTY = thisQty + qtyText;

                        if(tempQTY <= maxQty){
                            thisQty += qtyText;
                            promoFragment.setSisaPromo(thisQty);
                            String url = API.getInstance().getAPiSetSelectPromo()
                                    + "?isSelect=" + true
                                    + "&mfp_id=" + sessionManager.getKeyMfpId()
                                    + "&plu=" + productsPromo.getJSONObject(position).getJSONObject("Product").getString("PLU")
                                    + "&promoCode=" + productsPromo.getJSONObject(position).getString("PromoCode")
                                    + "&shoppingCartID=" + sessionManager.getCartId()
                                    + "&quantity=" + qtyText;

                            JsonArrayRequest jsonArrayReq = new JsonArrayRequest(url,
                                    new Response.Listener<JSONArray>() {
                                        @Override
                                        public void onResponse(JSONArray response) {
                                            try {
                                                if (response == null || response.length() == 0){
                                                    Toast.makeText(activity, "Gagal terkoneksi server", Toast.LENGTH_LONG).show();
                                                }else{
                                                    if (response.getJSONObject(0).getString("Message").equals("success")
                                                            || response.getJSONObject(0).getString("Message").equals("true")){
                                                        promoContainer.setBackgroundResource(R.drawable.card_product_style_2);
                                                        imageCheck.setBackgroundResource(R.color.colorPrimary);
                                                        mapSelected.put(position, true);
                                                        productsPromo.getJSONObject(position).put("IsSelectedPromo", true);

                                                        ((CartActivity)activity).setTabView(is, thisQty, maxQty);
                                                        ((CartActivity) activity).loadPromo("select");
                                                        notifyDataSetChanged();
                                                    }else{
                                                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(activity);
                                                        alertDialogBuilder.setMessage(response.getJSONObject(0).getString("Message"));
                                                        alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                                            @Override
                                                            public void onClick(DialogInterface arg0, int arg1) {
                                                            }
                                                        });

                                                        AlertDialog alertDialog = alertDialogBuilder.create();
                                                        alertDialog.setCanceledOnTouchOutside(false);
                                                        alertDialog.show();
                                                    }
                                                }
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    }, new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    Log.i("url error", error.toString());
                                    Toast.makeText(activity, "Gagal terkoneksi server", Toast.LENGTH_LONG).show();
                                }
                            }, activity);

                            jsonArrayReq.setRetryPolicy(new DefaultRetryPolicy(60000,
                                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                            AppController.getInstance().addToRequestQueue(jsonArrayReq);

                        }
                    }

                    totalPrice.setText("Rp " + df.format(priceWithDiscount * Integer.parseInt(String.valueOf(productQty.getText()))).replace(",","."));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        imageCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isEdit = true;

                try {
                    Double priceWithDiscount = productsPromo.getJSONObject(position).getDouble("PriceWithDiscount");

                    if(mapSelected.get(position)) {
                        int qtyText = Integer.parseInt(productQty.getText().toString());
                        thisQty -= qtyText;
                        productQty.setText("1");
                        promoFragment.setSisaPromo(thisQty);

                        String url =API.getInstance().getAPiSetSelectPromo()
                                + "?isSelect=" + false
                                + "&mfp_id=" + sessionManager.getKeyMfpId()
                                + "&plu=" + productsPromo.getJSONObject(position).getJSONObject("Product").getString("PLU")
                                + "&promoCode=" + productsPromo.getJSONObject(position).getString("PromoCode")
                                + "&shoppingCartID=" + sessionManager.getCartId()
                                + "&quantity=" + 1;

                        JsonArrayRequest jsonArrayReq = new JsonArrayRequest(url,
                                new Response.Listener<JSONArray>() {
                                    @Override
                                    public void onResponse(JSONArray response) {
                                        try {
                                            if (response == null || response.length() == 0){
                                                Toast.makeText(activity, "Gagal terkoneksi server", Toast.LENGTH_LONG).show();
                                            }else{
                                                if (response.getJSONObject(0).getString("Message").equals("success")){
                                                    promoContainer.setBackgroundResource(R.drawable.card_product_style_1);
                                                    imageCheck.setBackgroundResource(R.color.backgroundGrey);
                                                    mapSelected.put(position, false);
                                                    productsPromo.getJSONObject(position).put("IsSelectedPromo", false);

                                                    ((CartActivity)activity).setTabView(is, thisQty, maxQty);
                                                    ((CartActivity) activity).loadPromo("select");
                                                    notifyDataSetChanged();
                                                }else{
                                                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(activity);
                                                    alertDialogBuilder.setMessage(response.getJSONObject(0).getString("Message"));
                                                    alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface arg0, int arg1) {
                                                        }
                                                    });

                                                    AlertDialog alertDialog = alertDialogBuilder.create();
                                                    alertDialog.setCanceledOnTouchOutside(false);
                                                    alertDialog.show();
                                                }
                                            }
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Log.i("url error", error.toString());
                                Toast.makeText(activity, "Gagal terkoneksi server", Toast.LENGTH_LONG).show();
                            }
                        }, activity);

                        jsonArrayReq.setRetryPolicy(new DefaultRetryPolicy(60000,
                                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                        AppController.getInstance().addToRequestQueue(jsonArrayReq);
                    } else {
                        if (priceWithDiscount <= 0.0){
                            productQty.setText("1");
                        }else if(thisQty == 0 && position != 0){
                            productQty.setText(productQty.getText().toString());
                        }else if(thisQty > 0 && thisQty < maxQty){
                            productQty.setText("1");
                        }

                        int qtyText = Integer.parseInt(productQty.getText().toString());
                        int tempQTY = thisQty + qtyText;

                        if(tempQTY <= maxQty){
                            thisQty += qtyText;
                            promoFragment.setSisaPromo(thisQty);
                            String url = API.getInstance().getAPiSetSelectPromo()
                                    + "?isSelect=" + true
                                    + "&mfp_id=" + sessionManager.getKeyMfpId()
                                    + "&plu=" + productsPromo.getJSONObject(position).getJSONObject("Product").getString("PLU")
                                    + "&promoCode=" + productsPromo.getJSONObject(position).getString("PromoCode")
                                    + "&shoppingCartID=" + sessionManager.getCartId()
                                    + "&quantity=" + qtyText;

                            JsonArrayRequest jsonArrayReq = new JsonArrayRequest(url,
                                    new Response.Listener<JSONArray>() {
                                        @Override
                                        public void onResponse(JSONArray response) {
                                            try {
                                                if (response == null || response.length() == 0){
                                                    Toast.makeText(activity, "Gagal terkoneksi server", Toast.LENGTH_LONG).show();
                                                }else{
                                                    if (response.getJSONObject(0).getString("Message").equals("success")
                                                            || response.getJSONObject(0).getString("Message").equals("true")){
                                                        promoContainer.setBackgroundResource(R.drawable.card_product_style_2);
                                                        imageCheck.setBackgroundResource(R.color.colorPrimary);
                                                        mapSelected.put(position, true);
                                                        productsPromo.getJSONObject(position).put("IsSelectedPromo", true);

                                                        ((CartActivity)activity).setTabView(is, thisQty, maxQty);
                                                        ((CartActivity) activity).loadPromo("select");
                                                        notifyDataSetChanged();
                                                    }else{
                                                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(activity);
                                                        alertDialogBuilder.setMessage(response.getJSONObject(0).getString("Message"));
                                                        alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                                            @Override
                                                            public void onClick(DialogInterface arg0, int arg1) {
                                                            }
                                                        });

                                                        AlertDialog alertDialog = alertDialogBuilder.create();
                                                        alertDialog.setCanceledOnTouchOutside(false);
                                                        alertDialog.show();
                                                    }
                                                }
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    }, new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    Log.i("url error", error.toString());
                                    Toast.makeText(activity, "Gagal terkoneksi server", Toast.LENGTH_LONG).show();
                                }
                            }, activity);

                            jsonArrayReq.setRetryPolicy(new DefaultRetryPolicy(60000,
                                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                            AppController.getInstance().addToRequestQueue(jsonArrayReq);

                        }
                    }

                    totalPrice.setText("Rp " + df.format(priceWithDiscount * Integer.parseInt(String.valueOf(productQty.getText()))).replace(",","."));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        imageProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isEdit = true;

                try {
                    Double priceWithDiscount = productsPromo.getJSONObject(position).getDouble("PriceWithDiscount");

                    if(mapSelected.get(position)) {
                        int qtyText = Integer.parseInt(productQty.getText().toString());
                        thisQty -= qtyText;
                        productQty.setText("1");
                        promoFragment.setSisaPromo(thisQty);

                        String url =API.getInstance().getAPiSetSelectPromo()
                                + "?isSelect=" + false
                                + "&mfp_id=" + sessionManager.getKeyMfpId()
                                + "&plu=" + productsPromo.getJSONObject(position).getJSONObject("Product").getString("PLU")
                                + "&promoCode=" + productsPromo.getJSONObject(position).getString("PromoCode")
                                + "&shoppingCartID=" + sessionManager.getCartId()
                                + "&quantity=" + 1;

                        JsonArrayRequest jsonArrayReq = new JsonArrayRequest(url,
                                new Response.Listener<JSONArray>() {
                                    @Override
                                    public void onResponse(JSONArray response) {
                                        try {
                                            if (response == null || response.length() == 0){
                                                Toast.makeText(activity, "Gagal terkoneksi server", Toast.LENGTH_LONG).show();
                                            }else{
                                                if (response.getJSONObject(0).getString("Message").equals("success")){
                                                    promoContainer.setBackgroundResource(R.drawable.card_product_style_1);
                                                    imageCheck.setBackgroundResource(R.color.backgroundGrey);
                                                    mapSelected.put(position, false);
                                                    productsPromo.getJSONObject(position).put("IsSelectedPromo", false);

                                                    ((CartActivity)activity).setTabView(is, thisQty, maxQty);
                                                    ((CartActivity) activity).loadPromo("select");
                                                    notifyDataSetChanged();
                                                }else{
                                                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(activity);
                                                    alertDialogBuilder.setMessage(response.getJSONObject(0).getString("Message"));
                                                    alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface arg0, int arg1) {
                                                        }
                                                    });

                                                    AlertDialog alertDialog = alertDialogBuilder.create();
                                                    alertDialog.setCanceledOnTouchOutside(false);
                                                    alertDialog.show();
                                                }
                                            }
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Log.i("url error", error.toString());
                                Toast.makeText(activity, "Gagal terkoneksi server", Toast.LENGTH_LONG).show();
                            }
                        }, activity);

                        jsonArrayReq.setRetryPolicy(new DefaultRetryPolicy(60000,
                                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                        AppController.getInstance().addToRequestQueue(jsonArrayReq);
                    } else {
                        if (priceWithDiscount <= 0.0){
                            productQty.setText("1");
                        }else if(thisQty == 0 && position != 0){
                            productQty.setText(productQty.getText().toString());
                        }else if(thisQty > 0 && thisQty < maxQty){
                            productQty.setText("1");
                        }

                        int qtyText = Integer.parseInt(productQty.getText().toString());
                        int tempQTY = thisQty + qtyText;

                        if(tempQTY <= maxQty){
                            thisQty += qtyText;
                            promoFragment.setSisaPromo(thisQty);
                            String url = API.getInstance().getAPiSetSelectPromo()
                                    + "?isSelect=" + true
                                    + "&mfp_id=" + sessionManager.getKeyMfpId()
                                    + "&plu=" + productsPromo.getJSONObject(position).getJSONObject("Product").getString("PLU")
                                    + "&promoCode=" + productsPromo.getJSONObject(position).getString("PromoCode")
                                    + "&shoppingCartID=" + sessionManager.getCartId()
                                    + "&quantity=" + qtyText;

                            JsonArrayRequest jsonArrayReq = new JsonArrayRequest(url,
                                    new Response.Listener<JSONArray>() {
                                        @Override
                                        public void onResponse(JSONArray response) {
                                            try {
                                                if (response == null || response.length() == 0){
                                                    Toast.makeText(activity, "Gagal terkoneksi server", Toast.LENGTH_LONG).show();
                                                }else{
                                                    if (response.getJSONObject(0).getString("Message").equals("success")
                                                            || response.getJSONObject(0).getString("Message").equals("true")){
                                                        promoContainer.setBackgroundResource(R.drawable.card_product_style_2);
                                                        imageCheck.setBackgroundResource(R.color.colorPrimary);
                                                        mapSelected.put(position, true);
                                                        productsPromo.getJSONObject(position).put("IsSelectedPromo", true);

                                                        ((CartActivity)activity).setTabView(is, thisQty, maxQty);
                                                        ((CartActivity) activity).loadPromo("select");
                                                        notifyDataSetChanged();
                                                    }else{
                                                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(activity);
                                                        alertDialogBuilder.setMessage(response.getJSONObject(0).getString("Message"));
                                                        alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                                            @Override
                                                            public void onClick(DialogInterface arg0, int arg1) {
                                                            }
                                                        });

                                                        AlertDialog alertDialog = alertDialogBuilder.create();
                                                        alertDialog.setCanceledOnTouchOutside(false);
                                                        alertDialog.show();
                                                    }
                                                }
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    }, new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    Log.i("url error", error.toString());
                                    Toast.makeText(activity, "Gagal terkoneksi server", Toast.LENGTH_LONG).show();
                                }
                            }, activity);

                            jsonArrayReq.setRetryPolicy(new DefaultRetryPolicy(60000,
                                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                            AppController.getInstance().addToRequestQueue(jsonArrayReq);

                        }
                    }

                    totalPrice.setText("Rp " + df.format(priceWithDiscount * Integer.parseInt(String.valueOf(productQty.getText()))).replace(",","."));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        return convertView;
    }

    public void jsonArrayRequest(String url, final String status){
        Log.i("url", url);

        JsonArrayRequest jsonArrayReq = new JsonArrayRequest(url,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        if (response == null || response.length() == 0){
                            Toast.makeText(activity, "Gagal terkoneksi server", Toast.LENGTH_LONG).show();
                        }else{
                            ((CartActivity) activity).loadPromo(status);
                            notifyDataSetChanged();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i("url error", error.toString());
                Toast.makeText(activity, "Gagal terkoneksi server", Toast.LENGTH_LONG).show();
            }
        }, activity);

        jsonArrayReq.setRetryPolicy(new DefaultRetryPolicy(60000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppController.getInstance().addToRequestQueue(jsonArrayReq);
    }
}

