package com.indomaret.klikindomaret.helper;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.analytics.Tracker;
import com.indomaret.klikindomaret.adapter.PromoAdapter;
import com.indomaret.klikindomaret.views.HeightAdjustableListView;

import org.json.JSONArray;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import pl.droidsonroids.gif.GifImageView;

/**
 * Created by indomaretitsd7 on 6/10/16.
 */
public class Promo {
    private Toolbar toolbar;
    private HeightAdjustableListView cartPromoListview;
    private PromoAdapter promoAdapter;
    private SessionManager sessionManager;
    private Intent intent;

    private JSONArray cartProduct;
    private JSONArray shoppingCartItems, shoppingCartItemsCashback;
    private JSONArray promoProduct;
    private String apiGetCart;
    private JSONArray virtualProduct = new JSONArray();
    private JSONArray nonStoreProduct = new JSONArray();
    private JSONArray storeProduct = new JSONArray();
    boolean onlyVirtual = false;

    private Button nextToCart;
    private LinearLayout linearLayout, discountContainer;
    private GifImageView preloader;
    private RelativeLayout preloaderShadow;
    private Tracker mTracker;

    private DecimalFormat df = new DecimalFormat("#,###");
    private int is = 0;
    private int qty = 0;
    private int qtys, id;
    private String promoName = "";
    private String message = "";
    private String[] listProduct, listProductPlu;
    private AlertDialog.Builder alertDialogBuilder;
    private AlertDialog alertDialog;
    private List<String> emptyStock = new ArrayList<String>();
    private Double totalPrice = 0.0;
    private Double totalDiscount = 0.0;
    private TextView discountCart, totalCart;

    public void onCreates(Activity activity){

    }

}
