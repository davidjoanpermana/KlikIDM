package com.indomaret.klikindomaret.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Paint;
import android.support.v7.app.AlertDialog;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.indomaret.klikindomaret.R;
import com.indomaret.klikindomaret.activity.PromoItemActivity;
import com.indomaret.klikindomaret.app.AppController;
import com.indomaret.klikindomaret.fragment.PromoPaymentFragment;
import com.indomaret.klikindomaret.views.NetworkImagesView;

import org.json.JSONArray;
import org.json.JSONException;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by USER on 4/18/2016.
 */
public class PromoPaymentAdapter extends BaseAdapter {
    private LayoutInflater inflater;
    private Activity activity;

    private JSONArray productsPromo;
    private int maxQty;
    private int thisQty = 0;
    private int index = 0;
    private boolean isEdit = false;
    private boolean isSelected = false;
    private Map<Integer, Boolean> mapSelected = new HashMap<>();
    private List<String> stock = new ArrayList<>();
    private int is;
    private boolean status;
    private List<Integer> addPosition = new ArrayList<>();
    private PromoPaymentFragment promoFragment;

    private ImageLoader imageLoader = AppController.getInstance().getImageLoader();
    private DecimalFormat df = new DecimalFormat("#,###");

    public PromoPaymentAdapter(Activity activity, JSONArray productsPromo, int maxQty, int is, PromoPaymentFragment promoFragment){
        this.activity = activity;
        this.productsPromo = productsPromo;
        this.maxQty = maxQty;
        thisQty = 0;
        this.is = is;
        this.promoFragment = promoFragment;
    }

    public PromoPaymentAdapter(Activity activity, JSONArray productsPromo, int maxQty, List<String> stock, int is, PromoPaymentFragment promoFragment){
        this.activity = activity;
        this.productsPromo = productsPromo;
        this.maxQty = maxQty;
        thisQty = 0;
        this.stock = stock;
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
            convertView = inflater.inflate(R.layout.activity_cart_promo_payment_single_list_item, null);

        final RelativeLayout promoContainer = (RelativeLayout) convertView.findViewById(R.id.promo_product_container);
        NetworkImagesView imageProduct = (NetworkImagesView) convertView.findViewById(R.id.image_product);
        final ImageView imageCheck = (ImageView) convertView.findViewById(R.id.image_check);
        final ImageView imageCheck2 = (ImageView) convertView.findViewById(R.id.image_check2);
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
        promoInitialPrice.setPaintFlags(promoInitialPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

        int qty = 0;
        try {
            isSelected = productsPromo.getJSONObject(position).getBoolean("IsSelectedPromo");
            mapSelected.put(position, isSelected);
            btnDetail.setText(Html.fromHtml("<u>Selengkapnya</u>"));

            if (productsPromo.getJSONObject(position).getBoolean("IsSelectedPromo")){

            }
            if (productsPromo.getJSONObject(position).getBoolean("IsQuantityUpdate")){
                qty = productsPromo.getJSONObject(position).getInt("QuantityUpdate");
            } else {
                if (productsPromo.getJSONObject(position).getInt("QuantityMaxStruk") == 0){
                    qty = productsPromo.getJSONObject(position).getInt("Quantity");
                }else{
                    qty = productsPromo.getJSONObject(position).getInt("QuantityMaxStruk");
                    productsPromo.getJSONObject(position).put("QuantityUpdate", qty);
                }
            }

            productQty.setText(String.valueOf(qty));

            if (productsPromo.getJSONObject(position).toString().contains("IsGKuponPromo") &&
                    productsPromo.getJSONObject(position).getBoolean("IsGKuponPromo")){
                linearPromo.setVisibility(View.GONE);
                totalPrice.setVisibility(View.INVISIBLE);
                linearGkupon.setVisibility(View.VISIBLE);
                titleGkupon.setText(productsPromo.getJSONObject(position).getString("CouponPrizeTitle"));
                descGkupon.setText(productsPromo.getJSONObject(position).getString("CouponPrizeMechanism"));
            } else {
                linearPromo.setVisibility(View.VISIBLE);
                totalPrice.setVisibility(View.INVISIBLE);
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

            for (int i = 0; i < productsPromo.length(); i++){
                if (productsPromo.getJSONObject(i).getBoolean("IsSelectedPromo")){
                    index++;
                }
            }

            if(mapSelected.get(position) && !isEdit){
                promoContainer.setBackgroundResource(R.drawable.card_product_style_2);
                imageCheck.setVisibility(View.GONE);
                imageCheck2.setVisibility(View.VISIBLE);
                status = false;

                for (int i=0; i<addPosition.size(); i++){
                    if (position == addPosition.get(i)) status = true;
                }

                if (!status){
                    thisQty += qty;
                    addPosition.add(position);
                    promoFragment.setSisaPromo(thisQty);
                }
            } else if(!mapSelected.get(position) && !isEdit) {
                promoContainer.setBackgroundResource(R.drawable.card_product_style_1);
                imageCheck2.setVisibility(View.GONE);
                imageCheck.setVisibility(View.VISIBLE);
            }

            if (!isSelected && position == 0 && index == 0 && !isEdit){
                isEdit = true;

                if (priceWithDiscount <= 0.0){
                    if (productsPromo.getJSONObject(position).getBoolean("IsQuantityUpdate")){
                        qty = productsPromo.getJSONObject(position).getInt("QuantityUpdate");
                    } else {
                        if (productsPromo.getJSONObject(position).getInt("QuantityMaxStruk") == 0){
                            qty = productsPromo.getJSONObject(position).getInt("Quantity");
                        }else{
                            qty = productsPromo.getJSONObject(position).getInt("QuantityMaxStruk");
                            productsPromo.getJSONObject(position).put("QuantityUpdate", qty);
                        }
                    }

                    promoContainer.setBackgroundResource(R.drawable.card_product_style_2);
                    imageCheck.setVisibility(View.GONE);
                    imageCheck2.setVisibility(View.VISIBLE);

                    mapSelected.put(position, true);
                    productsPromo.getJSONObject(position).put("IsSelectedPromo", true);
                    ((PromoItemActivity)activity).promoSelectedUpdate(productsPromo);

                    thisQty = qty;
                    productQty.setText(""+thisQty);
                    promoFragment.setSisaPromo(thisQty);
                    ((PromoItemActivity)activity).setTabView(is, thisQty);
                }
            }

            ((PromoItemActivity)activity).setTabView(is, thisQty);

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
                int qtyBuy = 0;
                if (productsPromo.getJSONObject(position).getInt("QuantityMaxStruk") == 0){
                    qtyBuy = productsPromo.getJSONObject(position).getInt("Quantity");
                }else{
                    qtyBuy = productsPromo.getJSONObject(position).getInt("QuantityMaxStruk");
                }

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
                                thisQty += 1;
                                qtyText += 1;
                                productQty.setText(qtyText + "");
                                promoFragment.setSisaPromo(thisQty);
                                ((PromoItemActivity)activity).setTabView(is, thisQty);

                                try {
                                    productsPromo.getJSONObject(position).put("QuantityUpdate", thisQty);
                                    productsPromo.getJSONObject(position).put("IsQuantityUpdate", true);
                                    ((PromoItemActivity)activity).promoSelectedUpdate(productsPromo);
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
                                thisQty -= 1;
                                qtyText -= 1;
                                productQty.setText(qtyText + "");
                                promoFragment.setSisaPromo(thisQty);
                                ((PromoItemActivity)activity).setTabView(is, thisQty);

                                try {
                                    productsPromo.getJSONObject(position).put("QuantityUpdate", thisQty);
                                    productsPromo.getJSONObject(position).put("IsQuantityUpdate", true);
                                    ((PromoItemActivity)activity).promoSelectedUpdate(productsPromo);
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
                                ((PromoItemActivity)activity).setTabView(is, thisQty);

                                promoContainer.setBackgroundResource(R.drawable.card_product_style_1);
                                imageCheck2.setVisibility(View.GONE);
                                imageCheck.setVisibility(View.VISIBLE);
                                mapSelected.put(position, false);
                                productsPromo.getJSONObject(position).put("IsSelectedPromo", false);
                                productsPromo.getJSONObject(position).put("IsQuantityUpdate", false);
                                ((PromoItemActivity)activity).promoUnSelectedUpdate(productsPromo);
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
                                    ((PromoItemActivity)activity).setTabView(is, thisQty);

                                    promoContainer.setBackgroundResource(R.drawable.card_product_style_2);
                                    imageCheck.setVisibility(View.GONE);
                                    imageCheck2.setVisibility(View.VISIBLE);
                                    mapSelected.put(position, true);
                                    productsPromo.getJSONObject(position).put("IsSelectedPromo", true);
                                    productsPromo.getJSONObject(position).put("IsQuantityUpdate", true);
                                    if (productsPromo.getJSONObject(position).getInt("QuantityUpdate") == 0){
                                        productsPromo.getJSONObject(position).put("QuantityUpdate", maxQty);
                                    }else{
                                        productsPromo.getJSONObject(position).put("QuantityUpdate", productQty.getText().toString());
                                    }
                                    ((PromoItemActivity)activity).promoSelectedUpdate(productsPromo);
                                } else {
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
                            }

                            totalPrice.setText("Rp " + df.format(priceWithDiscount * Integer.parseInt(String.valueOf(productQty.getText()))).replace(",","."));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
        );

        return convertView;
    }
}

