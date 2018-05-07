package com.indomaret.klikindomaret.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.TabLayout;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsConstants;
import com.facebook.appevents.AppEventsLogger;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.android.gms.analytics.ecommerce.ProductAction;
import com.indomaret.klikindomaret.API;
import com.indomaret.klikindomaret.R;
import com.indomaret.klikindomaret.adapter.ProductDescriptionAdapter;
import com.indomaret.klikindomaret.adapter.ProductPromoAdapter;
import com.indomaret.klikindomaret.adapter.ProductViewPagerAdapter;
import com.indomaret.klikindomaret.adapter.RelatedProductAdapter;
import com.indomaret.klikindomaret.adapter.SpecificationAdapter;
import com.indomaret.klikindomaret.app.AppController;
import com.indomaret.klikindomaret.helper.SessionManager;
import com.indomaret.klikindomaret.helper.UncaughtExceptionHandler;
import com.indomaret.klikindomaret.views.BadgeDrawable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import pl.droidsonroids.gif.GifImageView;

public class ProductActivity extends AppCompatActivity implements ViewPager.OnPageChangeListener, View.OnClickListener{
    private Toolbar toolbar;
    private ImageView[] dots;
    private EditText qtyProduct;
    private TextView productName, productPrice, productInitialPrice, sendFrom, productInstallment,
            productDiscount, emptyText, mTitle, emptyPromo, emptyDesc, textColor, textSize;
    private ViewPager productImages;
    private TabLayout tabLayout;
    private LinearLayout pagerIndicator, linearDetailProduct;
    private RelativeLayout preloader;
    private RelativeLayout productPromotion, qtyPlus, qtyMinus;
    private LinearLayout shareButton, buyButton, emptyBuyButton, emptyBuyButtonPromo;
    private Spinner satuanBeli;
    private GridView productColor, productSize;
    private RecyclerView descriptionList;
    private ProductViewPagerAdapter mAdapter;

    private int dotsCount;
    private int indexColor = 0;
    private int indexSize = 0;
    private int totalItemInCart;
    private String url, permalink, cat, productID, urlHero;
    private Double validPrice, discount, price = 0.0, cartonPrice = 0.0;
    private List<Integer> tenor;
    private List<String> satuanList;
    private JSONObject productFromCategory = new JSONObject();
    private JSONObject productFromHome = new JSONObject();
    private JSONObject product;
    private JSONArray productavailableColor, productAvailableSizeFromColor;
    private Tracker mTracker;

    private SessionManager sessionManager;
    private Intent intent;
    private DecimalFormat df = new DecimalFormat("#,###");

    private List<String> colorArray = new ArrayList<>();
    private List<String> sizeArray = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product);

        UncaughtExceptionHandler uncaughtExceptionHandler = new UncaughtExceptionHandler();
        uncaughtExceptionHandler.uncaughtException(this);

        AppController application = (AppController) getApplication();
        mTracker = application.getDefaultTracker();
        mTracker.setScreenName("Product Detail Page");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());

        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);

        satuanList = new ArrayList<String>();
        satuanList.add("Satuan");

        sessionManager = new SessionManager(ProductActivity.this);

        if(sessionManager.getCartId() != null){
            url = API.getInstance().getCartTotal()+"?cartId=" + sessionManager.getCartId() +"&customerId=" + sessionManager.getUserID()+"&mfp_id=" + sessionManager.getKeyMfpId();
            cat = "cart";
            makeRequestproduct(url, "cart");
        } else {
            updateCartTotal(0);
        }

        intent = getIntent();
        String data = intent.getStringExtra("data");
        cat = intent.getStringExtra("cat");
        urlHero = intent.getStringExtra("url");

        toolbar = (Toolbar) findViewById(R.id.app_toolbar);
        mTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);

        try {
            if(cat.equals("home")){
                productFromHome = new JSONObject(data);
                Uri uri = Uri.parse(productFromHome.getString("TargetUrl"));
                permalink = uri.getLastPathSegment();
                productID = productFromHome.getString("ID");
            }else if(cat.equals("deep")){
                Uri uri = Uri.parse(data);
                permalink = uri.getLastPathSegment();
            } else if(cat.equals("hero")){
                Uri uri = Uri.parse(urlHero);
                permalink = uri.getLastPathSegment();
            } else {
                productFromCategory = new JSONObject(data);

                if(productFromCategory.getString("Title").length() > 0 && !productFromCategory.getString("Title").equals("null")){
                    mTitle.setText(productFromCategory.getString("Title"));
                } else {
                    mTitle.setText(productFromCategory.getString("Name"));
                }

                permalink = productFromCategory.getString("Permalink");
                productID = productFromCategory.getString("ID");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        makeRequestproduct(API.getInstance().getApiPaymentTypeInstallment()+"?mfp_id="+sessionManager.getKeyMfpId(), "payment");

        productName = (TextView) findViewById(R.id.product_name);
        productPrice = (TextView) findViewById(R.id.single_product_price);
        productInitialPrice = (TextView) findViewById(R.id.single_product_initial_price);
        sendFrom = (TextView) findViewById(R.id.product_send_from);
        productInstallment = (TextView) findViewById(R.id.product_installment);
        productDiscount = (TextView) findViewById(R.id.product_discount);
        emptyText = (TextView) findViewById(R.id.empty_text);
        productPromotion = (RelativeLayout) findViewById(R.id.product_promotion);
        qtyPlus = (RelativeLayout) findViewById(R.id.qty_plus);
        qtyMinus = (RelativeLayout) findViewById(R.id.qty_minus);
        qtyProduct = (EditText) findViewById(R.id.product_quantity);
        shareButton = (LinearLayout) findViewById(R.id.share_button);
        productColor = (GridView) findViewById(R.id.product_color);
        productSize = (GridView) findViewById(R.id.product_size);
        buyButton = (LinearLayout) findViewById(R.id.btn_single_category_buy);
        emptyBuyButton = (LinearLayout) findViewById(R.id.btn_single_category_empty);
        emptyBuyButtonPromo = (LinearLayout) findViewById(R.id.btn_single_category_empty_promo);
        satuanBeli = (Spinner) findViewById(R.id.satuan_spinner);

        preloader = (RelativeLayout) findViewById(R.id.preloader);
        productImages = (ViewPager) findViewById(R.id.pager_image_product);
        pagerIndicator = (LinearLayout) findViewById(R.id.viewPagerCountDotsProduct);
        linearDetailProduct = (LinearLayout) findViewById(R.id.linear_detail_product);
        emptyPromo = (TextView) findViewById(R.id.empty_promo);
        emptyDesc = (TextView) findViewById(R.id.empty_desc);
        textColor = (TextView) findViewById(R.id.text_color);
        textSize = (TextView) findViewById(R.id.text_size);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, satuanList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        satuanBeli.setAdapter(adapter);

        descriptionList = (RecyclerView) findViewById(R.id.product_description_list);
        tabLayout = (TabLayout) findViewById(R.id.tabs);

        //set page tab
        tabLayout.addTab(tabLayout.newTab().setText("Deskripsi"));
        tabLayout.addTab(tabLayout.newTab().setText("Promo"));

        tabLayout.setOnTabSelectedListener(
                new TabLayout.OnTabSelectedListener() {
                    @Override
                    public void onTabSelected(TabLayout.Tab tab) {
                        switch (tab.getPosition()) {
                            case 0:
                                try {
                                    if (product.getJSONArray("ProductAttributes").length() > 0){
                                        descriptionList.setVisibility(View.VISIBLE);
                                        emptyDesc.setVisibility(View.GONE);
                                        emptyPromo.setVisibility(View.GONE);

                                        descriptionList.setLayoutManager(new LinearLayoutManager(ProductActivity.this));
                                        ProductDescriptionAdapter productDescriptionAdapter = new ProductDescriptionAdapter(product.getJSONArray("ProductAttributes"));
                                        descriptionList.setAdapter(productDescriptionAdapter);
                                    }else{
                                        descriptionList.setVisibility(View.GONE);
                                        emptyDesc.setVisibility(View.VISIBLE);
                                        emptyPromo.setVisibility(View.GONE);
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                                break;
                            case 1:
                                try {
                                    if (product.getJSONArray("ProductBadge").length() > 0){
                                        descriptionList.setVisibility(View.VISIBLE);
                                        emptyDesc.setVisibility(View.GONE);
                                        emptyPromo.setVisibility(View.GONE);

                                        descriptionList.setLayoutManager(new LinearLayoutManager(ProductActivity.this));
                                        ProductPromoAdapter productPromoAdapter = new ProductPromoAdapter(product.getJSONArray("ProductBadge"));
                                        descriptionList.setAdapter(productPromoAdapter);
                                    }else{
                                        descriptionList.setVisibility(View.GONE);
                                        emptyDesc.setVisibility(View.GONE);
                                        emptyPromo.setVisibility(View.VISIBLE);
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                                break;
                            case 2:
                                break;
                        }
                    }

                    @Override
                    public void onTabUnselected(TabLayout.Tab tab) {
                    }

                    @Override
                    public void onTabReselected(TabLayout.Tab tab) {
                    }
                }
        );

        //add quantity product
        qtyPlus.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int qty = Integer.parseInt(qtyProduct.getText().toString());
                        qty = qty + 1;
                        qtyProduct.setText(qty + "");
                    }
                }
        );

        //decrease quantity product
        qtyMinus.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int qty = Integer.parseInt(qtyProduct.getText().toString());

                        if (qty != 1) {
                            qty = qty - 1;
                            qtyProduct.setText(qty + "");
                        }
                    }
                }
        );

        shareButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(Intent.ACTION_SEND);
                        i.setType("text/plain");
                        i.putExtra(Intent.EXTRA_TEXT, API.getInstance().getWebBaseUrl()+"product/"+permalink);
                        startActivity(Intent.createChooser(i, "Share URL"));
                    }
                }
        );

        satuanBeli.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (satuanBeli.getSelectedItemPosition() == 0){
                    productPrice.setText("Rp " + df.format(price).replace(",", "."));
                }else {
                    productPrice.setText("Rp " + df.format(cartonPrice).replace(",", "."));
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        productColor.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                System.out.println("--- color position : "+position);
                System.out.println("--- color : "+((TextView) view.findViewById(R.id.item)).getText());
                try {
                    if(position != indexColor){
                        String dataColor = productavailableColor.getJSONObject(position).getString("Product");
                        intent = new Intent(ProductActivity.this, ProductActivity.class);
                        intent.putExtra("data", dataColor);
                        intent.putExtra("cat", "product");
                        startActivity(intent);
                        finish();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });


        productSize.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                System.out.println("--- size position : "+position);
                System.out.println("--- size : "+((TextView) view.findViewById(R.id.item)).getText());

                try{
                    if(position != indexSize){
                        String dataSize = productAvailableSizeFromColor.getJSONObject(position).getString("Product");
                        intent = new Intent(ProductActivity.this, ProductActivity.class);
                        System.out.println("--- dataSize : "+dataSize);
                        intent.putExtra("data", dataSize);
                        intent.putExtra("cat", "product");
                        startActivity(intent);
                        finish();
                    }
                } catch (JSONException e){
                    e.printStackTrace();
                }
            }
        });

        buyButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String quantity;
                        try {
                            String variant = "";

                            if (product.getString("Color").equals(null) || product.getString("Color").equals("null")){
                                variant = product.getString("Flavour");
                            }else{
                                variant = product.getString("Color");
                            }

                            com.google.android.gms.analytics.ecommerce.Product productModel =  new com.google.android.gms.analytics.ecommerce.Product()
                                    .setId(product.getString("ID"))
                                    .setName(product.getString("Description"))
                                    .setBrand(product.getJSONObject("ProductBrand").getString("Name"))
                                    .setVariant(variant)
                                    .setPosition(1)
                                    .setCustomDimension(1, "Produk");

                            ProductAction productAction = new ProductAction(ProductAction.ACTION_ADD)
                                    .setProductActionList("Produk Detail");

                            HitBuilders.ScreenViewBuilder builder = new HitBuilders.ScreenViewBuilder()
                                    .addProduct(productModel)
                                    .setProductAction(productAction);

                            AppController application = (AppController) getApplication();
                            Tracker t = application.getDefaultTracker();
                            t.setScreenName("Add Product");
                            t.send(builder.build());

                            Bundle parameters = new Bundle();
                            parameters.putString(AppEventsConstants.EVENT_PARAM_DESCRIPTION, product.getString("Description"));
                            parameters.putString(AppEventsConstants.EVENT_PARAM_CONTENT_ID, product.getString("ID"));

                            AppEventsLogger logger = AppEventsLogger.newLogger(ProductActivity.this);
                            logger.logEvent(AppEventsConstants.EVENT_NAME_ADDED_TO_CART, parameters);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        if (satuanBeli.getSelectedItemPosition() == 0){
                            quantity = qtyProduct.getText().toString();
                        }else {
                            int frac = 0;
                            int qty = Integer.parseInt(qtyProduct.getText().toString());

                            try {
                                frac = Integer.parseInt(product.getString("FracCarton"));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            int finalQty = qty*frac;
                            quantity = String.valueOf(finalQty);
                        }

                        preloader.setVisibility(View.VISIBLE);

                        try {
                            makeRequestproduct(API.getInstance().getApiModifyCart()
                                    +"?cartRef=mobile"
                                    +"&pId=" + product.getString("ID")
                                    +"&mod=add"
                                    +"&qty=" + quantity
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
        );
    }

    @Override
    public void onClick(View v) {}

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}

    @Override
    public void onPageSelected(int position) {
        for (int i = 0; i < dotsCount; i++) {
            dots[i].setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.nonselecteditem_dot, null));
        }

        dots[position].setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.selecteditem_dot, null));
    }

    @Override
    public void onPageScrollStateChanged(int state) {}

    public void selectedPage(int pageIndex) {
        tabLayout.setScrollPosition(pageIndex, 0f, true);
    }

    private void setUiPageViewController() {
        dotsCount = mAdapter.getCount();
        dots = new ImageView[dotsCount];

        pagerIndicator.removeAllViews();

        for (int i = 0; i < dotsCount; i++) {
            dots[i] = new ImageView(ProductActivity.this);
            dots[i].setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.nonselecteditem_dot, null));

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );

            params.setMargins(4, 0, 4, 0);
            pagerIndicator.addView(dots[i], params);
        }

        dots[0].setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.selecteditem_dot, null));
    }

    //get product
    public void makeRequestproduct(String url, final String type) {
        System.out.println("act URL Product = "+url);
        JsonArrayRequest jsonArrayReq = new JsonArrayRequest(url,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        System.out.println("product response = " + response);

                        if(type.equals("product")){
                            processProduct(response);
                        } else if(type.equals("relatedProduct")){
                            relatedProduct(response);
                        } else if(type.equals("color")){
                            availableColor(response);
                        } else if(type.equals("size")){
                            availableSize(response);
                        } else if(type.equals("modify")){
                            addToCart(response);
                        } else if(type.equals("cart")){
                            try {
                                updateCartTotal(response.getInt(0));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        } else if(type.equals("payment")){
                            paymentResponse(response);
                        }

                        preloader.setVisibility(View.GONE);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(ProductActivity.this, "Gagal terkoneksi server", Toast.LENGTH_LONG).show();
                preloader.setVisibility(View.GONE);
            }
        }, this);

        jsonArrayReq.setRetryPolicy(new DefaultRetryPolicy(60000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppController.getInstance().addToRequestQueue(jsonArrayReq);
    }

    //POST REQUEST
    public void jsonPost(String urlJsonObj, JSONObject jsonObject){
        System.out.println("wishlist = " + urlJsonObj);
        System.out.println("wishlist = " + jsonObject);

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                urlJsonObj, jsonObject,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        if (response == null || response.length() == 0){
                            preloader.setVisibility(View.GONE);
                            Toast.makeText(ProductActivity.this, "Gagal terkoneksi server", Toast.LENGTH_LONG).show();
                        }else{
                            addWishListResponse(response);
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(ProductActivity.this, "Gagal terkoneksi server", Toast.LENGTH_LONG).show();
                preloader.setVisibility(View.GONE);
            }
        });

        AppController.getInstance().addToRequestQueue(jsonObjReq);
    }

    public void paymentResponse(JSONArray response){
        tenor = new ArrayList<>();

        for(int i=0; i<response.length(); i++){
            try {
                tenor.add(response.getJSONObject(i).getInt("Tenor"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public void addWishListResponse(JSONObject response){
        preloader.setVisibility(View.GONE);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ProductActivity.this);

        try{
            if(response.getBoolean("IsSuccess")){
                if(response.getString("Message").equals("success")){
                    alertDialogBuilder.setMessage("Berhasil menambahkan produk ke Wishlist");
                } else if(response.getString("Message").equals("already")){
                    alertDialogBuilder.setMessage("Produk sudah ada di Daftar Wishlist");
                }
            } else {
                alertDialogBuilder.setMessage("Gagal menambahkan produk ke Daftar Wishlist");
            }
        } catch(Exception e) {
            alertDialogBuilder.setMessage("Gagal melakukan koneksi");
        }

        alertDialogBuilder.setNegativeButton("Tutup", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
            }
        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    public void processProduct(JSONArray response){
        try {
            product = response.getJSONObject(0);
            System.out.println("--- product act : "+product);
            String variant = "";

            System.out.println("Product = " + product);
            System.out.println("Product cat = " + cat);

            if (product.getString("Color").equals(null) || product.getString("Color").equals("null")){
                variant = product.getString("Flavour");
            }else{
                variant = product.getString("Color");
            }

            com.google.android.gms.analytics.ecommerce.Product productModel = null;
            productModel = new com.google.android.gms.analytics.ecommerce.Product()
                    .setId(product.getString("ID"))
                    .setName(product.getString("Description"))
                    .setBrand(product.getJSONObject("ProductBrand").getString("Name"))
                    .setVariant(variant)
                    .setPosition(1)
                    .setCustomDimension(1, "Produk");

            ProductAction productAction = new ProductAction(ProductAction.ACTION_DETAIL);
            HitBuilders.ScreenViewBuilder builder = new HitBuilders.ScreenViewBuilder()
                    .addImpression(productModel, "Produk Detail")
                    .addProduct(productModel)
                    .setProductAction(productAction);

            AppController application = (AppController) getApplication();
            Tracker t = application.getDefaultTracker();
            t.setScreenName("Product");
            t.send(builder.build());

            if(cat.equals("home") || cat.equals("cart")){
                if(product.getString("Title").length()>0 && !product.getString("Title").equals("null")){
                    productName.setText(product.getString("Title"));
                    mTitle.setText(product.getString("Title"));
                } else {
                    productName.setText(product.getString("Name"));
                    mTitle.setText(product.getString("Name"));
                }
            } else {
                if(productFromCategory.getString("Title").length() > 0 && !productFromCategory.getString("Title").equals("null")){
                    productName.setText(productFromCategory.getString("Title"));
                } else {
                    productName.setText(productFromCategory.getString("Name"));
                }
            }

            if (Integer.parseInt(product.getString("FracCarton")) > 0){
                if (!satuanList.toString().contains("Karton (" + product.getString("FracCarton") + " pcs)")){
                    satuanList.add("Karton (" + product.getString("FracCarton") + " pcs)");
                }

                satuanBeli.setVisibility(View.VISIBLE);
            }else {
                satuanBeli.setVisibility(View.GONE);
            }

            JSONArray productListImages = product.getJSONArray("ImageModels");
            JSONArray productListImageWithoutThumb = new JSONArray();

            for (int i=0; i<productListImages.length(); i++){
                if(!productListImages.getJSONObject(i).getString("AssetUrl").contains("thumb")){
                   productListImageWithoutThumb.put(productListImages.getJSONObject(i));
                }
            }

            mAdapter = new ProductViewPagerAdapter(ProductActivity.this, productListImageWithoutThumb);
            productImages.setAdapter(mAdapter);
            productImages.setCurrentItem(0);
            productImages.addOnPageChangeListener(this);
            setUiPageViewController();

            validPrice = Double.parseDouble(product.getString("HargaWebsite"));
            discount = Double.parseDouble(product.getString("Discount"));
            cartonPrice = Double.parseDouble(product.getString("PriceCarton"));
            price = validPrice - discount;
            productPrice.setText("Rp " + df.format(price).replace(",", "."));

            if(!discount.equals(0.0)){
                productInitialPrice.setText("Rp " + df.format(validPrice).replace(",","."));
                productInitialPrice.setVisibility(View.VISIBLE);
                productInitialPrice.setPaintFlags(productInitialPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            } else {
                productInitialPrice.setVisibility(View.GONE);
            }

            if (product.getString("ProductFlag").toLowerCase().equals("plaza")){
                if(product.getString("Flag_Produk").substring(0,1).equals("D")){
                    sendFrom.setText("Dikirim dari Gudang");
                } else {
                    if(product.getString("Flag_Produk").substring(0,1).equals("B")){
                        sendFrom.setText("Dikirim dari Penjual");
                    }
                }
            } else {
                sendFrom.setText("Dikirim dari Toko");
            }

            if(cat.equals("home") || cat.equals("cart")){
                if(product.getString("IsInstallment").equals("true") && price >= Double.valueOf(product.getString("MinimumOrderInstallment"))){
                    productInstallment.setText("Cicilan 0%");
                    productInstallment.setVisibility(View.VISIBLE);
                    productPromotion.setVisibility(View.VISIBLE);
                } else {
                    productInstallment.setVisibility(View.GONE);
                }
            } else {
                if(productFromCategory.getString("IsInstallment").equals("true") && price >= Double.valueOf(product.getString("MinimumOrderInstallment"))){
                    productInstallment.setText("Cicilan 0%");
                    productInstallment.setVisibility(View.VISIBLE);
                    productPromotion.setVisibility(View.VISIBLE);
                } else {
                    productInstallment.setVisibility(View.GONE);
                }
            }

            JSONArray badgeArray = product.getJSONArray("ProductBadge");

            if(badgeArray.length() != 0){
                int badgeType = 100;

                for(int i=0; i<badgeArray.length(); i++){
                    if (badgeArray.getJSONObject(i).getInt("BadgeType") < badgeType){
                        badgeType = badgeArray.getJSONObject(i).getInt("BadgeType");

                        if (badgeArray.getJSONObject(i).getString("BadgeDesc").toLowerCase().equals("tebus murah")){
                            productDiscount.setVisibility(View.GONE);
                        }else{
                            productDiscount.setVisibility(View.VISIBLE);
                            productDiscount.setText(badgeArray.getJSONObject(i).getString("BadgeDesc"));
                        }
                    }
                }

                productPromotion.setVisibility(View.VISIBLE);
            } else {
                productDiscount.setVisibility(View.GONE);
            }

            if (product.getString("IsSoldOut") == "false" && product.getString("IsHideAddToCart") == "true"){
                buyButton.setVisibility(View.GONE);
                emptyBuyButton.setVisibility(View.GONE);
                emptyBuyButtonPromo.setVisibility(View.VISIBLE);
            } else if (product.getString("IsSoldOut").equals("true")){
                buyButton.setVisibility(View.GONE);
                emptyBuyButton.setVisibility(View.VISIBLE);
                emptyBuyButtonPromo.setVisibility(View.GONE);
            } else {
                buyButton.setVisibility(View.VISIBLE);
                emptyBuyButton.setVisibility(View.GONE);
                emptyBuyButtonPromo.setVisibility(View.GONE);
            }

            if(product.getString("AverageReview").equals("0.0")){
            } else {
                Float rate = Float.valueOf(product.getString("AverageReview"));
                RatingBar ratingBar = (RatingBar) findViewById(R.id.product_rating);
                ratingBar.setRating(rate);
            }

            // set decription
            try {
                if (product.getJSONArray("ProductAttributes").length() > 0){
                    descriptionList.setVisibility(View.VISIBLE);
                    emptyDesc.setVisibility(View.GONE);
                    emptyPromo.setVisibility(View.GONE);

                    descriptionList.setLayoutManager(new LinearLayoutManager(ProductActivity.this));
                    ProductDescriptionAdapter productDescriptionAdapter = new ProductDescriptionAdapter(product.getJSONArray("ProductAttributes"));
                    descriptionList.setAdapter(productDescriptionAdapter);
                }else{
                    descriptionList.setVisibility(View.GONE);
                    emptyDesc.setVisibility(View.VISIBLE);
                    emptyPromo.setVisibility(View.GONE);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            selectedPage(0);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String productID = "";
        String categoryID = "";

        if (product != null){
            buyButton.setEnabled(true);
            linearDetailProduct.setVisibility(View.VISIBLE);
            try {
                productID = product.getString("ID");
                categoryID = product.getJSONArray("ProductCategoryMappings").getJSONObject(0).getString("CategoryID");
            } catch (JSONException e) {
                e.printStackTrace();
            }

            makeRequestproduct(API.getInstance().getAPiAvailableColor() + "?mfp_id=" + sessionManager.getKeyMfpId() + "&ProductID=" + productID, "color");
            makeRequestproduct(API.getInstance().getApiAvailableSize() + "?mfp_id=" + sessionManager.getKeyMfpId() + "&ProductID=" + productID, "size");
            makeRequestproduct(API.getInstance().getApiRelatedProduct() + "?mfp_id=" + sessionManager.getKeyMfpId() + "&categoryID=" + categoryID + "&ProductID=" + productID, "relatedProduct");
        }else{
            buyButton.setEnabled(false);
            emptyText.setVisibility(View.VISIBLE);
        }
    }

    public void relatedProduct(final JSONArray response){
        GridView relatedProductList = (GridView) findViewById(R.id.related_product);
        RelatedProductAdapter relatedProductAdapter = new RelatedProductAdapter(ProductActivity.this, response);
        relatedProductList.setAdapter(relatedProductAdapter);

        relatedProductList.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        try {
                            String variant = "";

                            if (response.getJSONObject(position).getString("Color").equals(null) ||
                                    response.getJSONObject(position).getString("Color").equals("null")){
                                variant = response.getJSONObject(position).getString("Flavour");
                            }else{
                                variant = response.getJSONObject(position).getString("Color");
                            }

                            com.google.android.gms.analytics.ecommerce.Product productModel =  new com.google.android.gms.analytics.ecommerce.Product()
                                    .setId(response.getJSONObject(position).getString("ID"))
                                    .setName(response.getJSONObject(position).getString("Description"))
                                    .setBrand(response.getJSONObject(position).getJSONObject("ProductBrand").getString("Name"))
                                    .setVariant(variant)
                                    .setPosition(1)
                                    .setCustomDimension(1, "Produk");

                            ProductAction productAction = new ProductAction(ProductAction.ACTION_ADD)
                                    .setProductActionList("Produk Terkait");

                            HitBuilders.ScreenViewBuilder builder = new HitBuilders.ScreenViewBuilder()
                                    .addProduct(productModel)
                                    .setProductAction(productAction);

                            AppController application = (AppController) getApplication();
                            Tracker t = application.getDefaultTracker();
                            t.setScreenName("Add Product");
                            t.send(builder.build());

                            intent = new Intent(ProductActivity.this, ProductActivity.class);
                            intent.putExtra("data", response.getJSONObject(position).toString());
                            intent.putExtra("cat", "category");
                            startActivity(intent);
                            overridePendingTransition(R.anim.left_in, R.anim.left_out);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
        );
    }

    public void availableColor(JSONArray response){
        productavailableColor = response;
        JSONObject colorObject;
        colorArray = new ArrayList<>();

        if(response.length() > 0){
            for (int i=0; i<response.length(); i++){
                try {
                    if (colorArray.size() > 0){
                        if (!colorArray.toString().contains(response.getJSONObject(i).getString("Description"))){
                            colorObject = new JSONObject();
                            colorObject.put("data", response.getJSONObject(i).getString("Description"));
                            colorObject.put("productID", response.getJSONObject(i).getString("ProductID"));

                            if(response.getJSONObject(i).getString("ProductID").equals(productID)){
                                indexSize = sizeArray.size();
                                colorObject.put("status", "1");
                            }else{
                                colorObject.put("status", "0");
                            }

                            colorArray.add(colorObject.toString());
                        }
                    }else{
                        colorObject = new JSONObject();
                        colorObject.put("data", response.getJSONObject(i).getString("Description"));
                        colorObject.put("productID", response.getJSONObject(i).getString("ProductID"));

                        if(response.getJSONObject(i).getString("ProductID").equals(productID)){
                            indexSize = sizeArray.size();
                            colorObject.put("status", "1");
                        }else{
                            colorObject.put("status", "0");
                        }

                        colorArray.add(colorObject.toString());
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            SpecificationAdapter relatedProductAdapter = new SpecificationAdapter(ProductActivity.this, colorArray, "color");
            productColor.setAdapter(relatedProductAdapter);
            productColor.setVisibility(View.VISIBLE);
            textColor.setVisibility(View.VISIBLE);
        }
    }

    public void availableSize(JSONArray response){
        productAvailableSizeFromColor = response;
        sizeArray = new ArrayList<>();

        if(response.length() > 0){
            JSONObject sizeObject = new JSONObject();

            for (int i=0; i<response.length(); i++){
                try {
                    sizeObject = new JSONObject();
                    sizeObject.put("data", response.getJSONObject(i).getString("Description"));
                    sizeObject.put("productID", response.getJSONObject(i).getString("ProductID"));

                    if(response.getJSONObject(i).getString("ProductID").equals(productID)){
                        indexSize = sizeArray.size();
                        sizeObject.put("status", "1");
                    }else{
                        sizeObject.put("status", "0");
                    }

                    sizeArray.add(sizeObject.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            SpecificationAdapter relatedProductAdapter = new SpecificationAdapter(ProductActivity.this, sizeArray, "size");
            productSize.setAdapter(relatedProductAdapter);
            productSize.setVisibility(View.VISIBLE);
            textSize.setVisibility(View.VISIBLE);
        }
    }

    public void addToCart(JSONArray response){
        try {
            JSONObject cart = response.getJSONObject(0);

            if(sessionManager.getCartId() == null || sessionManager.getCartId().equals("00000000-0000-0000-0000-000000000000")){
                sessionManager.setCartId(cart.getString("ResponseID"));
            }

            if(cart.getBoolean("Success")) {
                makeRequestproduct(API.getInstance().getCartTotal()
                        +"?cartId=" + sessionManager.getCartId()
                        +"&customerId=" + sessionManager.getUserID()
                        +"&mfp_id=" + sessionManager.getKeyMfpId() , "cart");


                final Toast toast = Toast.makeText(getApplicationContext(), "Produk masuk ke keranjang", Toast.LENGTH_SHORT);
                toast.show();

                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        toast.cancel();
                    }
                }, 500);
            }else {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ProductActivity.this);
                alertDialogBuilder.setMessage(cart.getString("ErrorMessage"));
                alertDialogBuilder.setNegativeButton("Tutup", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {}
                });

                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.setCanceledOnTouchOutside(false);
                alertDialog.show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void updateCartTotal(int total) {
        totalItemInCart = total;
        this.invalidateOptionsMenu();
    }

    @Override
    @SuppressWarnings("deprecation")
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.category_menu, menu);
        MenuItem itemCart = menu.findItem(R.id.keranjang_top_btn);
        LayerDrawable icon = (LayerDrawable) itemCart.getIcon();
        setBadgeCount(this, icon, ""+totalItemInCart);

        return true;
    }

    public void stopPreloader(){
        preloader.setVisibility(View.GONE);
    }

    public void runPreloader(){
        preloader.setVisibility(View.VISIBLE);
    }

    public static void setBadgeCount(Context context, LayerDrawable icon, String count) {
        BadgeDrawable badge;
        Drawable reuse = icon.findDrawableByLayerId(R.id.ic_badge);

        if (reuse != null && reuse instanceof BadgeDrawable) {
            badge = (BadgeDrawable) reuse;
        } else {
            badge = new BadgeDrawable(context);
        }

        badge.setCount(count);
        icon.mutate();
        icon.setDrawableByLayerId(R.id.ic_badge, badge);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            finish();
            overridePendingTransition(R.anim.right_in, R.anim.right_out);
            return true;
        } else if (id == R.id.keranjang_top_btn) {
            if(totalItemInCart == 0){
                android.support.v7.app.AlertDialog.Builder alertDialogBuilder = new android.support.v7.app.AlertDialog.Builder(ProductActivity.this);
                alertDialogBuilder.setMessage("Keranjang belanja Anda kosong.");
                alertDialogBuilder.setNegativeButton("Mulai Belanja", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                    }
                });

                android.support.v7.app.AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
            }
            else if(sessionManager.isLoggedIn()){
                sessionManager.setEmptyProd(null);
                intent = new Intent(ProductActivity.this, CartActivity.class);
                finish();
                if (CategoryActivity.categoryActivity != null) CategoryActivity.categoryActivity.finish();
                if (CategoryLevel1Activity.categoryLevel1Activity != null) CategoryLevel1Activity.categoryLevel1Activity.finish();
                startActivity(intent);
                overridePendingTransition(R.anim.top_in, R.anim.top_out);
            } else {
                intent = new Intent(ProductActivity.this, LoginActivity.class);
                finish();
                if (CategoryActivity.categoryActivity != null) CategoryActivity.categoryActivity.finish();
                if (CategoryLevel1Activity.categoryLevel1Activity != null) CategoryLevel1Activity.categoryLevel1Activity.finish();
                startActivity(intent);
                overridePendingTransition(R.anim.left_in, R.anim.left_out);
            }

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.right_in, R.anim.right_out);
    }

    @Override
    public void onResume(){
        super.onResume();

        satuanBeli.setSelection(0);

        if(sessionManager.getCartId() != null){
            url = API.getInstance().getCartTotal()+"?cartId=" + sessionManager.getCartId() +"&customerId=" + sessionManager.getUserID()+"&mfp_id=" + sessionManager.getKeyMfpId();
            cat = "cart";
            makeRequestproduct(url, "cart");
        } else {
            updateCartTotal(0);
        }

        url = API.getInstance().getApiProductByPermalink()+"?mfp_id="+sessionManager.getKeyMfpId()+"&regionID="+sessionManager.getRegionID() + "&permalink="+permalink;
        makeRequestproduct(url, "product");
    }
}