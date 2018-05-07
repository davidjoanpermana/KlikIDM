package com.indomaret.klikindomaret.activity;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.indomaret.klikindomaret.API;
import com.indomaret.klikindomaret.R;
import com.indomaret.klikindomaret.adapter.CategoryAdapter;
import com.indomaret.klikindomaret.adapter.SlideBrandAdapter;
import com.indomaret.klikindomaret.adapter.ViewPagerAdapter;
import com.indomaret.klikindomaret.app.AppController;
import com.indomaret.klikindomaret.helper.SessionManager;
import com.indomaret.klikindomaret.views.BadgeDrawable;
import com.indomaret.klikindomaret.views.HeightAdjustableListView;
import com.indomaret.klikindomaret.views.WrapContentViewPager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import pl.droidsonroids.gif.GifImageView;

public class CategoryLevel1Activity extends AppCompatActivity implements ViewPager.OnPageChangeListener {
    public static Activity categoryLevel1Activity;
    private Intent intent;
    private Tracker mTracker;
    private SessionManager sessionManager;
    private Toolbar toolbar;

    private TextView mTitle, textBrand;
    private RelativeLayout preloader;
    private LinearLayout linearParent;

    private ViewPagerAdapter mAdapter;
    private CategoryAdapter categoryAdapter;
    private WrapContentViewPager slideBanner;
    private HeightAdjustableListView productList;

    private int totalItemInCart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_level1);

        intent = getIntent();
        sessionManager = new SessionManager(CategoryLevel1Activity.this);
        categoryLevel1Activity = this;

        AppController application = (AppController) getApplication();
        mTracker = application.getDefaultTracker();
        mTracker.setScreenName("Category Product Page");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());

        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(CategoryLevel1Activity.this);

        toolbar = (Toolbar) findViewById(R.id.app_toolbar);
        toolbar.setTitle("");
        mTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
        mTitle.setText(intent.getStringExtra("name"));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        textBrand = (TextView) findViewById(R.id.text_brand);
        productList = (HeightAdjustableListView) findViewById(R.id.category_listview);
        preloader = (RelativeLayout) findViewById(R.id.preloader);
        linearParent = (LinearLayout) findViewById(R.id.linear_parent);

        JSONObject searchObject = new JSONObject();
        JSONObject searchModel = new JSONObject();

        if(sessionManager.getCartId() != null){
            String cartUrl = API.getInstance().getCartTotal()+"?cartId=" + sessionManager.getCartId()+"&customerId=" + sessionManager.getUserID()+"&mfp_id=" + sessionManager.getKeyMfpId();
            arrayRequest(cartUrl, "cart");
        }

        try {
            searchObject.put("ID","");
            searchObject.put("ParentID","");
            searchObject.put("Permalink",intent.getStringExtra("permalink"));
            searchObject.put("Name","");

            searchModel.put("Categories","");
            searchModel.put("ProductBrandID","");
            searchModel.put("StartPrice","");
            searchModel.put("EndPrice","");

            searchObject.put("SearchModel",searchModel);

            makeJsonPost(API.getInstance().getSearchByCategory()+"?regionID="+sessionManager.getRegionID()+"&mfp_id="+sessionManager.getKeyMfpId(), searchObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    //method set slide banner
    public void setSlideBanner(JSONObject response) {
        try {
            JSONArray banners = response.getJSONArray("ResponseObject").getJSONObject(0).getJSONArray("Banners");
            slideBanner = (WrapContentViewPager) findViewById(R.id.pager_banner);

            if (banners.length() > 0){
                slideBanner.setVisibility(View.VISIBLE);
                mAdapter = new ViewPagerAdapter(CategoryLevel1Activity.this, banners);

                slideBanner.setAdapter(mAdapter);
                slideBanner.setCurrentItem(0);
                slideBanner.addOnPageChangeListener(this);
            }else{
                slideBanner.setVisibility(View.GONE);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    //method set slide brand
    public void setSlideBrand(JSONObject response) {
        try {
            final JSONArray brands = response.getJSONArray("ResponseObject").getJSONObject(0).getJSONArray("Brands");

            GridView slideBrand = (GridView) findViewById(R.id.grid_brand);
            SlideBrandAdapter slideBrandAdapter = new SlideBrandAdapter(this, brands, "cat");
            slideBrand.setAdapter(slideBrandAdapter);
            textBrand.setVisibility(View.VISIBLE);

            slideBrand.setOnItemClickListener(
                    new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            try {
                                JSONObject brandObject = brands.getJSONObject(position);

                                if (brandObject.getString("Name").length() > 1) {
                                    intent = new Intent(CategoryLevel1Activity.this, CategoryActivity.class);
                                    intent.putExtra("cat", "brand");
                                    intent.putExtra("brand", brandObject.toString());
                                    finish();
                                    startActivity(intent);
                                    overridePendingTransition(R.anim.right_in, R.anim.right_out);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
            );
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    //method set product list
    public void setProductList(final JSONObject response){
        try {
            categoryAdapter = new CategoryAdapter(this, response.getJSONArray("ResponseObject").getJSONObject(0).getJSONArray("SubCategories"));
            productList.setAdapter(categoryAdapter);

            preloader.setVisibility(View.GONE);
        } catch (JSONException e) {
            e.printStackTrace();

            preloader.setVisibility(View.GONE);
        }
    }

    public void arrayRequest(final String url, final String type) {
        System.out.println("type cat : "+type);
        System.out.println("url cat : "+url);
        JsonArrayRequest jsonArrayReq = new JsonArrayRequest(url,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        if (response == null || response.length() == 0){
                            preloader.setVisibility(View.GONE);
                            Toast.makeText(CategoryLevel1Activity.this, "Gagal terkoneksi server", Toast.LENGTH_LONG).show();
                        }else{
                            if (type.equals("cart")) {
                                try {
                                    updateCartTotal(response.getInt(0));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(CategoryLevel1Activity.this, "Gagal terkoneksi server", Toast.LENGTH_LONG).show();
                preloader.setVisibility(View.GONE);
            }
        }, this);

        AppController.getInstance().addToRequestQueue(jsonArrayReq);
    }

    public void makeJsonPost(String urlJsonObj, JSONObject jsonObject){
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                urlJsonObj, jsonObject,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        if (response == null || response.length() == 0){
                            preloader.setVisibility(View.GONE);
                            Toast.makeText(CategoryLevel1Activity.this, "Gagal terkoneksi server", Toast.LENGTH_LONG).show();
                        }else{
                            preloader.setVisibility(View.VISIBLE);

                            setSlideBanner(response);
                            setProductList(response);
                            setSlideBrand(response);
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(CategoryLevel1Activity.this, "Gagal terkoneksi server", Toast.LENGTH_LONG).show();
                preloader.setVisibility(View.GONE);
            }
        });

        AppController.getInstance().addToRequestQueue(jsonObjReq);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {

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

    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.right_in, R.anim.right_out);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            finish();
            overridePendingTransition(R.anim.right_in, R.anim.right_out);
            return true;
        } else if (id == R.id.keranjang_top_btn) {
            if(totalItemInCart==0){
                android.support.v7.app.AlertDialog.Builder alertDialogBuilder = new android.support.v7.app.AlertDialog.Builder(CategoryLevel1Activity.this);
                alertDialogBuilder.setMessage("Keranjang belanja Anda kosong.");
                alertDialogBuilder.setNegativeButton("Mulai Belanja", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                    }
                });

                android.support.v7.app.AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
            } else if(sessionManager.isLoggedIn()){
                sessionManager.setEmptyProd(null);
                intent = new Intent(CategoryLevel1Activity.this, CartActivity.class);
                finish();
                startActivity(intent);
                overridePendingTransition(R.anim.top_in, R.anim.top_out);
            } else {
                intent = new Intent(CategoryLevel1Activity.this, LoginActivity.class);
                intent.putExtra("from", "klikindomaret");
                finish();
                startActivity(intent);
                overridePendingTransition(R.anim.left_in, R.anim.left_out);
            }

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void runPreloader(){
        preloader.setVisibility(View.VISIBLE);
        setEnable(false);
    }

    public void stopPreloader(){
        preloader.setVisibility(View.GONE);
        setEnable(true);
    }

    public void setEnable(boolean value){
        for ( int i = 0; i < linearParent.getChildCount();  i++ ){
            View view = linearParent.getChildAt(i);
            view.setEnabled(value); // Or whatever you want to do with the view.
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}
