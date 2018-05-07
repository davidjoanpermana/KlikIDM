package com.indomaret.klikindomaret.fragment;

import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.TextView;

import com.indomaret.klikindomaret.R;
import com.indomaret.klikindomaret.activity.CartActivity;
import com.indomaret.klikindomaret.activity.ProductActivity;
import com.indomaret.klikindomaret.adapter.ProductDescriptionAdapter;
import com.indomaret.klikindomaret.adapter.ProductPromoAdapter;
import com.indomaret.klikindomaret.adapter.PromoAdapter;
import com.indomaret.klikindomaret.helper.SessionManager;
import com.indomaret.klikindomaret.views.HeightAdjustableListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class PromoFragment extends Fragment {
    private View view;
    private SessionManager sessionManager;
    private int qty = 0;
    public AlertDialog alertDialog;
    private String[] listProduct, listProductPlu;
    private PromoAdapter promoAdapter;
    private List<String> emptyStock = new ArrayList<String>();
    private JSONObject promoProduct = new JSONObject();
    private JSONArray promoArray = new JSONArray();
    private int position;
    private TextView sisaPromo;

    public PromoFragment (JSONObject promoProduct, int position){
        this.promoProduct = promoProduct;
        this.position = position;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.activity_cart_promo_single_grid_item, container, false);

        GridView cartPromoGridview = (GridView) view.findViewById(R.id.grid_product);
        TextView promoDescrption = (TextView) view.findViewById(R.id.promo_description);
        TextView btnDetail = (TextView) view.findViewById(R.id.btn_detail);
        TextView maxPromo = (TextView) view.findViewById(R.id.maks_promo);
        sisaPromo = (TextView) view.findViewById(R.id.sisa_promo);

        sessionManager = new SessionManager(getActivity());

        try {
            promoArray = ((CartActivity)getActivity()).promoArrays;
            promoProduct = promoArray.getJSONObject(position);
            qty = promoProduct.getJSONArray("ShoppingCartItems").getJSONObject(0).getInt("Quantity");

            btnDetail.setText(Html.fromHtml("<u>Baca Selengkapnya</u>"));
            maxPromo.setText(" Anda hanya dapat memilih maksimal ("+ qty+")");
            promoDescrption.setText(promoProduct.getJSONArray("ShoppingCartItems").getJSONObject(0).getString("CartRefValue"));

            if (sessionManager.getEmptyProd() != null){
                listProduct = sessionManager.getEmptyProd().split(",");

                for (int i=0; i<listProduct.length; i++){
                    if (listProduct[i].contains("false")){
                        listProductPlu = listProduct[i].split(":");
                        emptyStock.add(listProductPlu[1]+"|"+listProductPlu[2].replace("\"",""));
                    }
                }

                promoAdapter = new PromoAdapter(getActivity(), promoProduct.getJSONArray("ShoppingCartItems"), qty, emptyStock, position, this);
            }else {
                promoAdapter = new PromoAdapter(getActivity(), promoProduct.getJSONArray("ShoppingCartItems"), qty, position, this);
            }

            cartPromoGridview.setAdapter(promoAdapter);

            ViewGroup.LayoutParams layoutParams = cartPromoGridview.getLayoutParams();
            layoutParams.width = (cartPromoGridview.getRequestedColumnWidth() + 10) * promoProduct.getJSONArray("ShoppingCartItems").length(); //this is in pixels
            cartPromoGridview.setLayoutParams(layoutParams);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        btnDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
                    alertDialogBuilder.setMessage(promoProduct.getJSONArray("ShoppingCartItems").getJSONObject(0).getString("CartRefValue"));
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

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//            promoArray.remove(0);
//        }
        return view;
    }

    public void setSisaPromo(int value){
        sisaPromo.setText(String.valueOf(qty-value));
    }
}
