package com.indomaret.klikindomaret.activity;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.android.gms.analytics.ecommerce.Product;
import com.google.android.gms.analytics.ecommerce.ProductAction;
import com.indomaret.klikindomaret.API;
import com.indomaret.klikindomaret.R;
import com.indomaret.klikindomaret.adapter.ProductCategoryAdapter;
import com.indomaret.klikindomaret.app.AppController;
import com.indomaret.klikindomaret.helper.SessionManager;
import com.indomaret.klikindomaret.helper.UncaughtExceptionHandler;
import com.indomaret.klikindomaret.views.BadgeDrawable;
import com.indomaret.klikindomaret.views.HeaderGridView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import pl.droidsonroids.gif.GifImageView;

public class CategoryActivity extends AppCompatActivity {
    public static Activity categoryActivity;
    private ProductCategoryAdapter productCategoryAdapter;
    private HeaderGridView productGridView;
    private FloatingActionButton scrollToTop;
    private Toolbar toolbar;
    private RelativeLayout preloader;
    private LinearLayout filter, sorting;
    private SwipeRefreshLayout pullToRefreshContainer;
    private TextView sortingCategory, sortingCategory2, mTitle;
    private EditText editStartPrice = null;
    private EditText editEndPrice = null;
    private RadioGroup radioPrice;
    private RadioButton allPrice, low, lower, high;

    private String api;
    private String categoryName = "";
    private String categoryNamePromo = "";
    private String categoryNameHero = "";
    private String categoryID = "";
    private String permalink = "";
    private Integer sortType = 0;
    private String url = "";
    private String sortDir = "DESC";
    private String productbrandid= "";
    private String sortCol = "Latest";
    private String pagesize = "24";
    private String startprice = "";
    private String endprice = "";
    private String bannerid = "";
    private String searchKey = "";
    private String cat, from;
    private int totalItemInCart;
    private int totalRecords;
    private int page = 1;
    private int identity = 0;
    private double categoryMaxPrice = 0.0;
    private int merekIndex = 0;

    private List<JSONObject> categoryObjectList;
    private JSONObject category;
    private JSONObject object = null;
    private JSONObject objectMaxPrice = null;
    private JSONArray brands = new JSONArray();
    private JSONArray sortedJsonArray = new JSONArray();

    private SharedPreferences prefs;
    private DecimalFormat df = new DecimalFormat("#,###");
    private SessionManager sessionManager;
    private Intent intent;
    private Tracker mTracker;
    private String[] brandArray;
    private String filterCategoriURL;
    private String filterCategory = (new JSONArray()).toString();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);

        UncaughtExceptionHandler uncaughtExceptionHandler = new UncaughtExceptionHandler();
        uncaughtExceptionHandler.uncaughtException(this);

        categoryActivity = this;
        AppController application = (AppController) getApplication();
        mTracker = application.getDefaultTracker();
        mTracker.setScreenName("Category Product Page");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());

        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(CategoryActivity.this);

        productGridView = (HeaderGridView) findViewById(R.id.first_home_gridview_category);
        preloader = (RelativeLayout) findViewById(R.id.preloader);
        sortingCategory = (TextView) findViewById(R.id.sorting_category);
        sortingCategory2 = (TextView) findViewById(R.id.sorting_category2);
        filter = (LinearLayout) findViewById(R.id.filtering);
        sorting = (LinearLayout) findViewById(R.id.sorting2);
        sessionManager = new SessionManager(CategoryActivity.this);

        api = API.getInstance().getApiCategories()+"?mfp_id="+sessionManager.getKeyMfpId()+"&regionID="+sessionManager.getRegionID();

        if(sessionManager.getCartId() != null){
            String cartUrl = API.getInstance().getCartTotal()+"?cartId=" + sessionManager.getCartId()+"&customerId=" + sessionManager.getUserID()+"&mfp_id=" + sessionManager.getKeyMfpId();
            arrayRequest(cartUrl, "cart");
        }

        intent = getIntent();
        from = "";
        cat = intent.getStringExtra("cat");
        Log.i("cat", cat);

        if (cat.equals("category")){
            from = "category";
            prefs = this.getSharedPreferences(getString(R.string.pref_key), Context.MODE_PRIVATE);

            try {
                category = new JSONObject(prefs.getString(getString(R.string.single_category), null));
                categoryID = category.getString("ID");
                categoryName = category.getString("Name");

                if (category.getString("SortType").equals("null") || category.getString("SortType").equals(""))
                    sortType = 0;
                else
                    sortType = category.getInt("SortType");

                if (sortType == 1) {
                    sortCol = "ProductBrand.Name";
                    sortDir = "ASC";
                } else if (sortType == 2) {
                    sortCol = "ProductBrand.Name";
                    sortDir = "DESC";
                } else if (sortType == 3) {
                    sortCol = "HargaWebsite";
                    sortDir = "ASC";
                } else if (sortType == 4) {
                    sortCol = "HargaWebsite";
                    sortDir = "DESC";
                } else if (sortType == 5) {
                    sortCol = "Promo";
                    sortDir = "ASC";
                } else if (sortType == 6) {
                    sortCol = "OverallRating";
                    sortDir = "DESC";
                } else {
                    sortCol = "Latest";
                    sortDir = "DESC";
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            getBrandProduct();
            getCategoryProduct();
        } else if(cat.equals("brand")){
            from = "brand";
            JSONObject brand = null;

            try {
                brand = new JSONObject(intent.getStringExtra("brand"));

                productbrandid = brand.getString("ID");
                categoryName = brand.getString("Name");
            } catch (JSONException e) {
                e.printStackTrace();
            }

            getCategoryProduct();
            JSONObject searchObject = new JSONObject();

            try {
                searchObject.put("Permalink",brand.getString("Permalink"));

                getBrandProduct();
                makeJsonPost(API.getInstance().getSearchByCategoryByBrand()+"?mfp_id="+sessionManager.getKeyMfpId(), searchObject, "brand");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else if(cat.equals("promo")){
            try {
                JSONObject promo = new JSONObject(intent.getStringExtra("promo"));
                bannerid = promo.getString("ID");

                if (promo.getString("MetaTitle").contains("null") || promo.getString("MetaTitle").length() == 0){
                    if (promo.toString().contains("ProductGroupDescription"))
                        categoryNamePromo = promo.getString("ProductGroupDescription");
                    else
                        categoryNamePromo = promo.getString("Description");
                }else {
                    categoryNamePromo = promo.getString("MetaTitle");
                }

                toolbar = (Toolbar) findViewById(R.id.app_toolbar);
                toolbar.setTitle("");
                mTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
                mTitle.setText(categoryNamePromo);
                setSupportActionBar(toolbar);
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);

                if(!promo.toString().contains("ProductGroupDetail")){
                    filterCategoriURL = API.getInstance().getApiFilterCategoryByBanner()+"?url="+promo.getString("TargetUrl")+"&startPrice=&endPrice=&productBrandID";
                }else{
                    filterCategoriURL = API.getInstance().getApiFilterCategoryByProductGroup()+"?url="+promo.getString("TargetUrl")+"&startPrice=&endPrice=&productBrandID";
                }

                arrayRequest(filterCategoriURL, "filterCat");
            } catch (JSONException e) {
                e.printStackTrace();
            }

            getCategoryProduct();
            getBrandProduct();
        } else if(cat.equals("search")){
            searchKey = intent.getStringExtra("searchKey");
            categoryName = intent.getStringExtra("searchKey");
            categoryName = categoryName.replace("%20", " ");

            JSONObject searchObject = new JSONObject();
            JSONObject searchModel = new JSONObject();
            try {
                searchObject.put("ID","");
                searchObject.put("ParentID","");

                if (intent.getStringExtra("permalink") != null){
                    searchObject.put("Permalink",intent.getStringExtra("permalink"));
                } else{
                    searchObject.put("Permalink","");
                };

                if (intent.getStringExtra("name") != null){
                    searchObject.put("Name",intent.getStringExtra("name"));
                } else{
                    searchObject.put("Name","");
                }


                searchModel.put("Categories",category);
                searchModel.put("ProductBrandID",productbrandid);
                searchModel.put("StartPrice",startprice);
                searchModel.put("EndPrice",endprice);

                searchObject.put("SearchModel",searchModel);

                makeJsonPost(API.getInstance().getSearchByCategory()+"?regionID="+sessionManager.getRegionID()+"&mfp_id="+sessionManager.getKeyMfpId(), searchObject, "search");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else if(cat.equals("hero")){
            JSONObject promo = null;

            try {
                promo = new JSONObject(intent.getStringExtra("hero"));
                bannerid = promo.getString("ID");

                if (promo.getString("MetaTitle").contains("null") || promo.getString("MetaTitle").length() == 0){
                    categoryNameHero = promo.getString("ProductGroupDescription");
                }else{
                    categoryNameHero = promo.getString("MetaTitle");
                }

                getBrandProduct();
                makeRequestHero(API.getInstance().getApiProductGroupByUrl()+"?regionID="+sessionManager.getRegionID()+"&mfp_id="+sessionManager.getKeyMfpId()+"&url="+intent.getStringExtra("url"), "hero");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else if(cat.equals("searchCategory")){
            JSONObject searchObject = new JSONObject();
            JSONObject searchModel = new JSONObject();

            try {
                searchObject.put("ID","");
                searchObject.put("ParentID","");
                searchObject.put("Permalink",intent.getStringExtra("permalink"));
                searchObject.put("Name","");

                searchModel.put("Categories",category);
                searchModel.put("ProductBrandID",productbrandid);
                searchModel.put("StartPrice",startprice);
                searchModel.put("EndPrice",endprice);

                searchObject.put("SearchModel",searchModel);

                makeJsonPost(API.getInstance().getSearchByCategory()+"?regionID="+sessionManager.getRegionID()+"&mfp_id="+sessionManager.getKeyMfpId(), searchObject, "search");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        makeJsonArrayGetMax();

        if (!cat.equals("promo") && !cat.equals("hero")){
            toolbar = (Toolbar) findViewById(R.id.app_toolbar);
            toolbar.setTitle("");
            mTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
            mTitle.setText(categoryName);
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        setFooterBar();
        pullToRefresh();

        scrollToTop = (FloatingActionButton) findViewById(R.id.scroll_to_top);
        scrollToTop.animate().cancel();
        scrollToTop.animate().translationYBy(350);

        scrollToTop.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        productGridView.smoothScrollToPosition(0);
                    }
                }
        );

        filter.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent filterIntent = new Intent(CategoryActivity.this, FilterActivity.class);
                        filterIntent.putExtra("cat", cat);
                        filterIntent.putExtra("brands", brands.toString());
                        filterIntent.putExtra("merekIndex", merekIndex);
                        filterIntent.putExtra("startprice", startprice);
                        filterIntent.putExtra("endprice", endprice);
                        filterIntent.putExtra("category", filterCategory);
                        filterIntent.putExtra("from", from);
                        filterIntent.putExtra("brandSelected", productbrandid);
                        filterIntent.putExtra("categorySelected", categoryID);
                        startActivityForResult(filterIntent, 1);
                        overridePendingTransition(R.anim.left_in, R.anim.left_out);
                    }
                }
        );

        sorting.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(CategoryActivity.this);
                        alertDialogBuilder.setTitle("Pilih Urutan");
                        alertDialogBuilder.setItems(R.array.sorting_array, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (which == 0) {
                                    sortCol = "ProductBrand.Name";
                                    sortDir = "ASC";
                                    sortingCategory2.setText("Alfabet (A-Z)");
                                } else if (which == 1) {
                                    sortCol = "ProductBrand.Name";
                                    sortDir = "DESC";
                                    sortingCategory2.setText("Alfabet (Z-A)");
                                } else if (which == 2) {
                                    sortCol = "HargaWebsite";
                                    sortDir = "ASC";
                                    sortingCategory2.setText("Harga Termurah");
                                } else if (which == 3) {
                                    sortCol = "HargaWebsite";
                                    sortDir = "DESC";
                                    sortingCategory2.setText("Harga Termahal");
                                } else if (which == 4) {
                                    sortCol = "Promo";
                                    sortDir = "ASC";
                                    sortingCategory2.setText("Promo");
                                } else if (which == 5) {
                                    sortCol = "OverallRating";
                                    sortDir = "DESC";
                                    sortingCategory.setText("Populer");
                                } else {
                                    sortCol = "Latest";
                                    sortDir = "DESC";
                                    sortingCategory2.setText("Terbaru");
                                }

                                runPreloader();

                                page = 1;
                                getCategoryProduct();
                            }
                        });

                        AlertDialog alertDialog = alertDialogBuilder.create();
                        alertDialog.show();
                    }
                }
        );
    }

    public void pullToRefresh(){
        pullToRefreshContainer = (SwipeRefreshLayout) findViewById(R.id.category_swipe_container);

        pullToRefreshContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                page = 1;
                runPreloader();

                pullToRefreshContainer.setRefreshing(false);

                getCategoryProduct();
            }
        });

        pullToRefreshContainer.setColorSchemeResources(R.color.colorPrimary,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
    }

    public void getCategoryProduct(){
        runPreloader();
        makeJsonArrayGet();
    }

    //get categories data
    public void makeJsonArrayGet() {
        if(cat.equals("brand")){
            url = api
                    +"&SortDir=" + sortDir
                    +"&ProductBrandID=" + productbrandid
                    +"&SortCol=" + sortCol
                    +"&Page=" + page
                    +"&PageSize=" + pagesize
                    +"&StartPrice=" + startprice
                    +"&EndPrice=" + endprice;
        } else if(cat.toLowerCase().contains("category")){
            url = api
                    +"&SortDir=" + sortDir
                    +"&ProductBrandID=" + productbrandid
                    +"&SortCol=" + sortCol
                    +"&Page=" + page
                    +"&PageSize=" + pagesize
                    +"&StartPrice=" + startprice
                    +"&EndPrice=" + endprice;
        } else if(cat.equals("promo")){
            url = api
                    +"&SortDir=" + sortDir
                    +"&BannerID=" + bannerid
                    +"&ProductBrandID=" + productbrandid
                    +"&SortCol=" + sortCol
                    +"&Page=" + page
                    +"&PageSize=" + pagesize
                    +"&StartPrice=" + startprice
                    +"&EndPrice=" + endprice;
        } else if(cat.equals("search")){
            url = api
                    +"&SortDir=" + sortDir
                    +"&SortCol=" + sortCol
                    +"&Page=" + page
                    +"&PageSize=" + pagesize
                    +"&StartPrice=" + startprice
                    +"&EndPrice=" + endprice
                    +"&SearchKey=" + searchKey.replace(" ", "%20");
        } else if(cat.equals("hero")){
            url = api
                    +"&SortDir=" + sortDir
                    +"&BannerID=" + bannerid
                    +"&ProductBrandID=" + productbrandid
                    +"&SortCol=" + sortCol
                    +"&Page=" + page
                    +"&PageSize=" + pagesize
                    +"&StartPrice=" + startprice
                    +"&EndPrice=" + endprice;
            System.out.println("Url Cat Hero = "+url);
        }

        StringBuilder urls = new StringBuilder();
        urls.append(url);

        if(!categoryID.equals("")){
            String[] catSplitArray = categoryID.split(";");
            if(catSplitArray.length > 0) {
                for (int i = 0; i < catSplitArray.length; i++) {
                    if (catSplitArray[i] != "") {
                        urls.append("&CategoryID=" + catSplitArray[i]);
                    }
                }
            }else{
                urls.append("&CategoryID=" + categoryID);
            }
        }

        if (sessionManager.isLoggedIn() && cat.equals("search")){
            urls.append("&USERID=" + sessionManager.getUserID());
        }

            url = urls.toString();
        System.out.println("url = " + url);

        JsonArrayRequest jsonArrayReq = new JsonArrayRequest(url,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        if (response == null || response.length() == 0){
                            stopPreloader();
                            Toast.makeText(CategoryActivity.this, "Gagal terkoneksi server", Toast.LENGTH_LONG).show();
                        }else{
                            processResponse(response);
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(CategoryActivity.this, "Gagal terkoneksi server", Toast.LENGTH_LONG).show();
                stopPreloader();
            }
        }, this);

        jsonArrayReq.setRetryPolicy(new DefaultRetryPolicy(60000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppController.getInstance().addToRequestQueue(jsonArrayReq);
    }

    //process response data
    public void processResponse(JSONArray response) {
        JSONArray jsonArray = null;
        categoryObjectList = new ArrayList<>();

        try {
            object = response.getJSONObject(0);
            totalRecords = Integer.parseInt(response.getJSONObject(0).getString("TotalRecord"));
            jsonArray = object.getJSONArray("ProductList");

            for (int i=0; i<jsonArray.length(); i++){
                categoryObjectList.add(jsonArray.getJSONObject(i));
            }

            if(cat.equals("search") || cat.equals("searchCategory")) filterCategory = object.getJSONArray("Categories").toString();
            if(cat.equals("search") || cat.equals("searchCategory")){
                JSONObject jsonObject = new JSONObject();

                for (int i=0; i<object.getJSONArray("Brands").length(); i++){
                    jsonObject.put("ID", object.getJSONArray("Brands").getJSONObject(i).getString("ID"));
                    jsonObject.put("Name", object.getJSONArray("Brands").getJSONObject(i).getString("Name"));

                    brands.put(jsonObject);
                    jsonObject = new JSONObject();
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        setCategoryProduct();

        if (categoryObjectList.size() == 0){
            TextView description = (TextView) findViewById(R.id.category_text);
            description.setVisibility(View.VISIBLE);
        } else {
            TextView description = (TextView) findViewById(R.id.category_text);
            description.setVisibility(View.GONE);
        }
    }


    //method set category product
    public void setCategoryProduct(){
        productCategoryAdapter = new ProductCategoryAdapter(CategoryActivity.this, categoryObjectList, "cat");
        productGridView.setAdapter(productCategoryAdapter);

        productGridView.setOnScrollListener(
                new AbsListView.OnScrollListener() {
                    @Override
                    public void onScrollStateChanged(AbsListView view, int scrollState) {
                        int threshold = 1;
                        int count = productGridView.getCount();

                        int btn_initPosY = scrollToTop.getScrollY();
                        if (scrollState == SCROLL_STATE_IDLE) {

                            if((count != totalRecords) && ((productGridView.getLastVisiblePosition()) >= (count - threshold))){
                                loadMore();
                            }

                            if (productGridView.getFirstVisiblePosition() <= 1) {
                                scrollToTop.animate().cancel();
                                scrollToTop.animate().translationYBy(350);
                            } else {
                                scrollToTop.animate().cancel();
                                scrollToTop.animate().translationY(btn_initPosY);
                            }
                        }
                    }

                    @Override
                    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

                    }
                }
        );

        productGridView.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        intent = new Intent(CategoryActivity.this, ProductActivity.class);
                        intent.putExtra("data", categoryObjectList.get(position).toString());
                        intent.putExtra("cat", "category");
                        startActivity(intent);
                        overridePendingTransition(R.anim.left_in, R.anim.left_out);

                        try {
                            String variant = "";

                            if (categoryObjectList.get(position).getString("Color").equals(null) ||
                                    categoryObjectList.get(position).getString("Color").equals("null")){
                                variant = categoryObjectList.get(position).getString("Flavour");
                            }else{
                                variant = categoryObjectList.get(position).getString("Color");
                            }

                            Product productModel =  new Product()
                                    .setId(categoryObjectList.get(position).getString("ID"))
                                    .setName(categoryObjectList.get(position).getString("Description"))
                                    .setBrand(categoryObjectList.get(position).getJSONObject("ProductBrand").getString("Name"))
                                    .setVariant(variant)
                                    .setPosition(1)
                                    .setCustomDimension(1, "Produk");

                            ProductAction productAction = new ProductAction(ProductAction.ACTION_CLICK)
                                    .setProductActionList("Kategori Produk");

                            HitBuilders.ScreenViewBuilder builder = new HitBuilders.ScreenViewBuilder()
                                    .addProduct(productModel)
                                    .setProductAction(productAction);

                            AppController application = (AppController) getApplication();
                            Tracker t = application.getDefaultTracker();
                            t.setScreenName("SearchResults Product");
                            t.send(builder.build());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
        );

        stopPreloader();
    }

    //method set footer bar function
    public void setFooterBar(){
        LinearLayout sortingPrice = (LinearLayout) findViewById(R.id.sorting_price);
        LinearLayout sortingMerk = (LinearLayout) findViewById(R.id.sorting_merk);
        LinearLayout sorting = (LinearLayout) findViewById(R.id.sorting);

        if(intent.getStringExtra("cat").equals("brand") || intent.getStringExtra("cat").equals("search")){
            sortingMerk.setVisibility(View.GONE);
        }

        sortingPrice.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        LayoutInflater inflater = (LayoutInflater) CategoryActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                        View popupView = null;

                        if (popupView == null) {
                            popupView = inflater.inflate(R.layout.popup_price, null);
                        }

                        editStartPrice = (EditText) popupView.findViewById(R.id.startPrice);
                        editEndPrice = (EditText) popupView.findViewById(R.id.endPrice);
                        radioPrice = (RadioGroup) popupView.findViewById(R.id.radioPrice);
                        allPrice = (RadioButton) popupView.findViewById(R.id.all_price);
                        lower = (RadioButton) popupView.findViewById(R.id.lower);
                        low = (RadioButton) popupView.findViewById(R.id.low);
                        high = (RadioButton) popupView.findViewById(R.id.high);

                        if (identity == 0) {
                            allPrice.setChecked(true);
                        } else if (identity == 1) {
                            lower.setChecked(true);
                        } else if (identity == 2) {
                            low.setChecked(true);
                        } else if (identity == 3) {
                            high.setChecked(true);
                        } else {
                            editStartPrice.setText(startprice);
                            editEndPrice.setText(endprice);
                        }

                        Double maxPrice = null;

                        try {
                            System.out.println("max = " + objectMaxPrice.getString("MaxPrice"));
                            if(categoryMaxPrice==0.0){
                                maxPrice = Double.valueOf(objectMaxPrice.getString("MaxPrice"));
                            } else {
                                maxPrice = categoryMaxPrice;
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        int intMaxPrice = maxPrice.intValue();
                        final int dividedPrice = intMaxPrice / 3;

                        lower.setText(" < Rp " + df.format(dividedPrice).replace(",", "."));
                        low.setText(" Rp " + df.format(dividedPrice).replace(",", ".") + " - " + df.format(dividedPrice * 2).replace(",", "."));
                        high.setText(" > Rp " + df.format(dividedPrice * 2).replace(",", "."));

                        editStartPrice.addTextChangedListener(
                                new TextWatcher() {
                                    @Override
                                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                                    }

                                    @Override
                                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                                    }

                                    @Override
                                    public void afterTextChanged(Editable s) {

                                    }
                                }
                        );

                        editEndPrice.addTextChangedListener(
                                new TextWatcher() {
                                    @Override
                                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                                    }

                                    @Override
                                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                                    }

                                    @Override
                                    public void afterTextChanged(Editable s) {

                                    }
                                }
                        );

                        radioPrice.setOnCheckedChangeListener(
                                new RadioGroup.OnCheckedChangeListener() {
                                    @Override
                                    public void onCheckedChanged(RadioGroup group, int checkedId) {
                                        switch (checkedId) {
                                            case R.id.all_price:
                                                editStartPrice.getText().clear();
                                                editEndPrice.getText().clear();
                                                allPrice.setChecked(true);
                                                break;

                                            case R.id.lower:
                                                editStartPrice.getText().clear();
                                                editEndPrice.getText().clear();
                                                lower.setChecked(true);
                                                break;

                                            case R.id.low:
                                                editStartPrice.getText().clear();
                                                editEndPrice.getText().clear();
                                                low.setChecked(true);
                                                break;

                                            case R.id.high:
                                                editStartPrice.getText().clear();
                                                editEndPrice.getText().clear();
                                                high.setChecked(true);
                                                break;
                                        }
                                    }
                                }
                        );

                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(CategoryActivity.this);
                        alertDialogBuilder.setTitle("Pilih Harga");
                        alertDialogBuilder.setView(popupView);

                        alertDialogBuilder.setPositiveButton("Pilih", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface arg0, int arg1) {
                                if (editStartPrice.getText().toString().length() > 0 || editEndPrice.getText().toString().length() > 0) {
                                    startprice = "" + editStartPrice.getText().toString();
                                    endprice = "" + editEndPrice.getText().toString();
                                    identity = 4;
                                } else if (allPrice.isChecked()) {
                                    startprice = "";
                                    endprice = "";
                                    identity = 0;
                                } else if (lower.isChecked()) {
                                    startprice = "0";
                                    endprice = "" + dividedPrice;
                                    identity = 1;
                                } else if (low.isChecked()) {
                                    startprice = "" + dividedPrice;
                                    endprice = "" + dividedPrice * 2;
                                    identity = 2;
                                } else if (high.isChecked()) {
                                    startprice = "" + dividedPrice * 2;
                                    endprice = "";
                                    identity = 3;
                                }

                                page = 1;
                                getCategoryProduct();
                            }
                        });

                        alertDialogBuilder.setNegativeButton("Batal", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });

                        AlertDialog alertDialog = alertDialogBuilder.create();
                        alertDialog.show();
                    }
                }
        );

        sortingMerk.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final TextView categoryBrandName = (TextView) findViewById(R.id.category_brand);
                        List<JSONObject> jsonList = new ArrayList<JSONObject>();
                        for (int i = 0; i < brands.length(); i++) {
                            try {
                                jsonList.add(brands.getJSONObject(i));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        Collections.sort(jsonList, new Comparator<JSONObject>() {

                            public int compare(JSONObject a, JSONObject b) {
                                String valA = new String();
                                String valB = new String();

                                try {
                                    valA = (String) a.get("Name");
                                    valB = (String) b.get("Name");
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                return valA.compareTo(valB);
                            }
                        });


                        sortedJsonArray = new JSONArray();
                        for (int i = 0; i < brands.length(); i++) {
                            sortedJsonArray.put(jsonList.get(i));
                        }

                        brandArray = new String[sortedJsonArray.length() + 1];
                        brandArray[0] = "Semua Merek";
                        for (int i = 0; i < sortedJsonArray.length(); i++) {
                            try {
                                brandArray[i + 1] = sortedJsonArray.getJSONObject(i).getString("Name");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(CategoryActivity.this);
                        alertDialogBuilder.setTitle("Pilih Merek");
                        alertDialogBuilder.setItems(brandArray, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (which == 0) {
                                    productbrandid = "";
                                    categoryBrandName.setText("Semua Merek");
                                } else {
                                    try {
                                        productbrandid = sortedJsonArray.getJSONObject(which - 1).getString("ID");
                                        categoryBrandName.setText(sortedJsonArray.getJSONObject(which - 1).getString("Name"));
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }

                                getCategoryProduct();
                            }
                        });

                        AlertDialog alertDialog = alertDialogBuilder.create();
                        alertDialog.show();
                    }
                }
        );

        sorting.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(CategoryActivity.this);
                        alertDialogBuilder.setTitle("Pilih Urutan");
                        alertDialogBuilder.setItems(R.array.sorting_array, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (which == 0) {
                                    sortCol = "ProductBrand.Name";
                                    sortDir = "ASC";
                                    sortingCategory.setText("Alfabet (A-Z)");
                                } else if (which == 1) {
                                    sortCol = "ProductBrand.Name";
                                    sortDir = "DESC";
                                    sortingCategory.setText("Alfabet (Z-A)");
                                } else if (which == 2) {
                                    sortCol = "HargaWebsite";
                                    sortDir = "ASC";
                                    sortingCategory.setText("Harga Termurah");
                                } else if (which == 3) {
                                    sortCol = "HargaWebsite";
                                    sortDir = "DESC";
                                    sortingCategory.setText("Harga Termahal");
                                } else if (which == 4) {
                                    sortCol = "Promo";
                                    sortDir = "ASC";
                                    sortingCategory.setText("Promo");
                                } else if (which == 5) {
                                    sortCol = "OverallRating";
                                    sortDir = "DESC";
                                    sortingCategory.setText("Populer");
                                } else {
                                    sortCol = "Latest";
                                    sortDir = "DESC";
                                    sortingCategory.setText("Terbaru");
                                }

                                runPreloader();
                                page = 1;
                                getCategoryProduct();
                            }
                        });

                        AlertDialog alertDialog = alertDialogBuilder.create();
                        alertDialog.show();
                    }
                }
        );
    }

    //method for load more - lazy load data
    public void loadMore(){
        runPreloader();
        page = page + 1;
        makeJsonArrayGetUpdate();
    }

    //get categories data
    public void makeJsonArrayGetUpdate() {
        if(cat.equals("brand")){
            url = api
                    +"&SortDir=" + sortDir
                    +"&ProductBrandID=" + productbrandid
                    +"&SortCol=" + sortCol
                    +"&Page=" + page
                    +"&PageSize=" + pagesize
                    +"&StartPrice=" + startprice
                    +"&EndPrice=" + endprice;
        } else if(cat.equals("category")){
            url = api
                    +"&SortDir=" + sortDir
                    +"&CategoryID=" + categoryID
                    +"&ProductBrandID=" + productbrandid
                    +"&SortCol=" + sortCol
                    +"&Page=" + page
                    +"&PageSize=" + pagesize
                    +"&StartPrice=" + startprice
                    +"&EndPrice=" + endprice;
        } else if(cat.equals("promo")){
            url = api
                    +"&SortDir=" + sortDir
                    +"&BannerID=" + bannerid
                    +"&ProductBrandID=" + productbrandid
                    +"&SortCol=" + sortCol
                    +"&Page=" + page
                    +"&PageSize=" + pagesize
                    +"&StartPrice=" + startprice
                    +"&EndPrice=" + endprice;
        } else if(cat.equals("search")){
            url = api
                    +"&SortDir=" + sortDir
                    +"&SortCol=" + sortCol
                    +"&Page=" + page
                    +"&PageSize=" + pagesize
                    +"&StartPrice=" + startprice
                    +"&EndPrice=" + endprice
                    +"&SearchKey=" + searchKey.replace(" ","%20");
        }  else if(cat.equals("hero")){
            url = api
                    +"&SortDir=" + sortDir
                    +"&BannerID=" + bannerid
                    +"&ProductBrandID=" + productbrandid
                    +"&SortCol=" + sortCol
                    +"&Page=" + page
                    +"&PageSize=" + pagesize
                    +"&StartPrice=" + startprice
                    +"&EndPrice=" + endprice;
        } else if(cat.equals("searchCategory")){
            url = api
                    +"&SortDir=" + sortDir
                    +"&CategoryID=" + categoryID
                    +"&ProductBrandID=" + productbrandid
                    +"&SortCol=" + sortCol
                    +"&Page=" + page
                    +"&PageSize=" + pagesize
                    +"&StartPrice=" + startprice
                    +"&EndPrice=" + endprice;
        }

        StringBuilder urls = new StringBuilder();
        urls.append(url);

        if(!categoryID.equals("")){
            urls.append("&CategoryID=" + categoryID);
        }

        url = urls.toString();
        System.out.println("loadmore = " + cat);

        JsonArrayRequest jsonArrayReq = new JsonArrayRequest(url,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        if (response == null || response.length() == 0){
                            stopPreloader();
                            Toast.makeText(CategoryActivity.this, "Gagal terkoneksi server", Toast.LENGTH_LONG).show();
                        }else{
                            processResponseUpdate(response);
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(CategoryActivity.this, "Gagal terkoneksi server", Toast.LENGTH_LONG).show();
                stopPreloader();
            }
        }, this);

        jsonArrayReq.setRetryPolicy(new DefaultRetryPolicy(60000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppController.getInstance().addToRequestQueue(jsonArrayReq);
    }

    //process response data
    public void processResponseUpdate(JSONArray response) {
        JSONArray jsonArray = null;

        try {
            object = response.getJSONObject(0);
            jsonArray = object.getJSONArray("ProductList");

            for (int i=0; i<jsonArray.length(); i++){
                categoryObjectList.add(jsonArray.getJSONObject(i));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        productCategoryAdapter.notifyDataSetChanged();
        stopPreloader();
    }

    //get product group data
    public void makeRequestHero(String url, final String type) {
        System.out.println("Url Hero = "+url);
        JsonArrayRequest jsonArrayReq = new JsonArrayRequest(url,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        if (response == null || response.length() == 0){
                            stopPreloader();
                            Toast.makeText(CategoryActivity.this, "Gagal terkoneksi server", Toast.LENGTH_LONG).show();
                        }else{
                            if(type.equals("hero")){
                                processRequestHero(response);
                            } else if(type.equals("cate")){

                            }
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(CategoryActivity.this, "Gagal terkoneksi server", Toast.LENGTH_LONG).show();
            }
        }, this);

        jsonArrayReq.setRetryPolicy(new DefaultRetryPolicy(60000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppController.getInstance().addToRequestQueue(jsonArrayReq);
    }

    public void processRequestHero(JSONArray response){
        try {
            JSONObject object = response.getJSONObject(0);
            bannerid = object.getString("ID");
            categoryNameHero = object.getString("ProductGroupDescription");
            mTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
            mTitle.setText(categoryNameHero);

            System.out.println("categoryName hero : "+categoryName);

            getCategoryProduct();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void makeJsonPost(String urlJsonObj, JSONObject jsonObject, final String type){
        System.out.println(urlJsonObj);
        System.out.println(jsonObject);
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                urlJsonObj, jsonObject,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if (response == null || response.length() == 0){
                                stopPreloader();
                                Toast.makeText(CategoryActivity.this, "Gagal terkoneksi server", Toast.LENGTH_LONG).show();
                            }else{
                                if (type.equals("brand")){
                                    filterCategory =response.getJSONArray("ResponseObject").getJSONObject(0).getJSONArray("Categories").toString();
                                }else{
                                    if (response.getJSONArray("ResponseObject").length() > 0){
                                        categoryID = response.getJSONArray("ResponseObject").getJSONObject(0).getString("ID");
                                        categoryName = response.getJSONArray("ResponseObject").getJSONObject(0).getString("Name");
                                        filterCategory = response.getJSONArray("ResponseObject").getJSONObject(0).getJSONArray("SubCategories").toString();
                                    }

                                    mTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
                                    mTitle.setText(categoryName);
                                    getCategoryProduct();
                                    if (cat.equals("searchCategory")) getBrandProduct();
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(CategoryActivity.this, "Gagal terkoneksi server", Toast.LENGTH_LONG).show();
            }
        });

        AppController.getInstance().addToRequestQueue(jsonObjReq);
    }

    public void updateCartTotal(int total) {
        totalItemInCart = total;
        this.invalidateOptionsMenu();
    }

    public void arrayRequest(final String url, final String type) {
        System.out.println("type cat : "+type);
        System.out.println("url cat : "+url);
        JsonArrayRequest jsonArrayReq = new JsonArrayRequest(url,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        if (response == null || response.length() == 0){
                            stopPreloader();
                            Toast.makeText(CategoryActivity.this, "Gagal terkoneksi server", Toast.LENGTH_LONG).show();
                        }else{
                            if (type.equals("cart")) {
                                try {
                                    updateCartTotal(response.getInt(0));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            } else if(type.equals("filterCat")){
                                try {
                                    filterCategory = response.getJSONObject(0).getJSONArray("Categories").toString();
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
//                Toast.makeText(CategoryActivity.this, "Gagal terkoneksi server", Toast.LENGTH_LONG).show();sdffsdfsdfsdfdsfsdfsdfsdf
            }
        }, this);

        AppController.getInstance().addToRequestQueue(jsonArrayReq);
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
        }
        else {
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
            if(totalItemInCart==0){
                android.support.v7.app.AlertDialog.Builder alertDialogBuilder = new android.support.v7.app.AlertDialog.Builder(CategoryActivity.this);
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
                intent = new Intent(CategoryActivity.this, CartActivity.class);
                finish();
                startActivity(intent);
                overridePendingTransition(R.anim.top_in, R.anim.top_out);
            } else {
                intent = new Intent(CategoryActivity.this, LoginActivity.class);
                intent.putExtra("from", "klikindomaret");
                finish();
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

        if(sessionManager.getCartId() != null){
            String cartUrl = API.getInstance().getCartTotal()+"?cartId=" + sessionManager.getCartId()+"&customerId=" + sessionManager.getUserID()+"&mfp_id=" + sessionManager.getKeyMfpId();
            arrayRequest(cartUrl, "cart");
        }
    }

    //get categories data
    public void makeJsonArrayGetMax() {
        if(bannerid != ""){
            url = api
                    +"&SortDir=DESC"
                    +"&BannerID=" + bannerid
                    +"&ProductBrandID=" + productbrandid
                    +"&SortCol=HargaWebsite"
                    +"&Page=1"
                    +"&PageSize=1";
        } else if (searchKey != ""){
            url = api
                    +"&SortDir=DESC"
                    +"&ProductBrandID=" + productbrandid
                    +"&SortCol=HargaWebsite"
                    +"&Page=1"
                    +"&PageSize=1"
                    +"&SearchKey=" + searchKey.replace(" ","%20");
        } else if(cat.equals("brand")){
            url = api
                    +"&SortDir=DESC"
                    +"&ProductBrandID=" + productbrandid
                    +"&SortCol=HargaWebsite"
                    +"&Page=1"
                    +"&PageSize=1";
        } else {
            url = api
                    +"&SortDir=DESC"
                    +"&CategoryID=" + categoryID
                    +"&ProductBrandID=" + productbrandid
                    +"&SortCol=HargaWebsite"
                    +"&Page=1"
                    +"&PageSize=1";
        }

        System.out.println("url max price = " + url);

        JsonArrayRequest jsonArrayReq = new JsonArrayRequest(url,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            if (response == null || response.length() == 0){
                                stopPreloader();
                                Toast.makeText(CategoryActivity.this, "Gagal terkoneksi server", Toast.LENGTH_LONG).show();
                            }else{
                                objectMaxPrice = response.getJSONObject(0);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(CategoryActivity.this, "Gagal terkoneksi server", Toast.LENGTH_LONG).show();
                stopPreloader();
            }
        }, this);

        jsonArrayReq.setRetryPolicy(new DefaultRetryPolicy(60000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppController.getInstance().addToRequestQueue(jsonArrayReq);
    }

    public void getBrandProduct() {
        if (categoryID != null && categoryID.length() > 0){
            url = API.getInstance().getApiBrandByCategory()+"?&categoryID=" + categoryID;

            JsonArrayRequest jsonArrayReq = new JsonArrayRequest(url,
                    new Response.Listener<JSONArray>() {
                        @Override
                        public void onResponse(JSONArray response) {
                            brands = response;
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.i("url error", error.toString());
                    Toast.makeText(CategoryActivity.this, "Gagal terkoneksi server", Toast.LENGTH_LONG).show();
                }
            }, this);

            jsonArrayReq.setRetryPolicy(new DefaultRetryPolicy(60000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            AppController.getInstance().addToRequestQueue(jsonArrayReq);
        }else if (bannerid != null && bannerid.length() > 0){
            url = API.getInstance().getApiBrandByBannerId()+"?bannerID=" + bannerid + "&IsBanner=true";

            JsonArrayRequest jsonArrayReq = new JsonArrayRequest(url,
                    new Response.Listener<JSONArray>() {
                        @Override
                        public void onResponse(JSONArray response) {
                            brands = response;
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.i("url error", error.toString());
                    Toast.makeText(CategoryActivity.this, "Gagal terkoneksi server", Toast.LENGTH_LONG).show();
                }
            }, this);

            jsonArrayReq.setRetryPolicy(new DefaultRetryPolicy(60000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            AppController.getInstance().addToRequestQueue(jsonArrayReq);
        }

//        if(cat.equals("category") || cat.equals("searchCategory")){
//            if (permalink != null){
//                url = API.getInstance().getApiBrandByCategory()+"?&categoryID=" + categoryID + "&qsCategory=" + permalink;
//            } else {
//                url = API.getInstance().getApiBrandByCategory()+"?&categoryID=" + categoryID + "&qsCategory=" + "";
//            }
//        } else if(cat.equals("search")){
//            if (permalink != null){
//                url = API.getInstance().getApiBrandBySearchKey()+"?&key=" + searchKey + "&qsCategory=" + permalink;
//            } else {
//                url = API.getInstance().getApiBrandBySearchKey()+"?&key=" + searchKey + "&qsCategory=" + "";
//            }
//        } else if(cat.equals("promo") || cat.equals("hero")){
//            if (permalink != null){
//                url = API.getInstance().getApiBrandByBannerId()+"?bannerID=" + bannerid + "&IsBanner=true"+"&qsCategory=" + permalink;
//            } else {
//                url = API.getInstance().getApiBrandByBannerId()+"?bannerID=" + bannerid + "&IsBanner=true"+"&qsCategory=" + "";
//            }
//        }else if(cat.equals("promo hero") || cat.equals("hero")){
//            if (permalink != null){
//                url = API.getInstance().getApiBrandByBannerId()+"?bannerID=" + bannerid + "&IsBanner=false"+"&qsCategory=" + permalink;
//            } else {
//                url = API.getInstance().getApiBrandByBannerId()+"?bannerID=" + bannerid + "&IsBanner=false"+"&qsCategory=" + "";
//            }
//        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                if(data.getStringExtra("filt").equals("cat")){
                    categoryID = data.getStringExtra("categoryId");
                    permalink = data.getStringExtra("permalink");

                    merekIndex = 0;
                    productbrandid = data.getStringExtra("merekID");
                    startprice = "";
                    endprice = "";
                } else {
                    merekIndex = data.getIntExtra("merekIndex", 0);
                    productbrandid = data.getStringExtra("merekID");
                    categoryID = data.getStringExtra("categoryId");
                    startprice = data.getStringExtra("maxPrice");
                    endprice = data.getStringExtra("minPrice");
                }

                getCategoryProduct();
            }
        }
    }

    public void runPreloader(){
        preloader.setVisibility(View.VISIBLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }

    public void stopPreloader(){
        preloader.setVisibility(View.GONE);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }

    public void setEnable(boolean value){
        productGridView.setEnabled(value);
    }
}
