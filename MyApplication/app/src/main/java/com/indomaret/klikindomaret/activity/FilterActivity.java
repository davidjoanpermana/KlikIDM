package com.indomaret.klikindomaret.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.indomaret.klikindomaret.R;
import com.indomaret.klikindomaret.adapter.BrandFilterAdapter;
import com.indomaret.klikindomaret.adapter.MultiLevelAdapter;
import com.indomaret.klikindomaret.data.DataProvider;
import com.indomaret.klikindomaret.helper.SessionManager;
import com.indomaret.klikindomaret.helper.UncaughtExceptionHandler;
import com.indomaret.klikindomaret.views.HeightAdjustableListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import pl.openrnd.multilevellistview.MultiLevelListView;

public class FilterActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private TextView filterCategory, filterBrand, filterPrince;
    private RelativeLayout scrollView;
    private Button btnSearch, btnReset;
    private View view = null;
    private EditText maxText, minText;
    private HeightAdjustableListView heightAdjustableListView;
    private BrandFilterAdapter brandFilterAdapter;
    private LayoutInflater inflater;

    private Intent intent;
    private JSONArray sortedJsonArray = new JSONArray();
    private String[] brandArray;
    private String[] brandIDArray;
    private String brandString;
    private JSONArray brandJsonArray;
    private int brandIndex = 0;
    private ArrayList<Integer> brandIDArraySelected = new ArrayList<Integer>();

    private JSONArray sortedCatJsonArray = new JSONArray();
    private String[] catArray;
    private String[] catIDArray;
    private String catString;
    private JSONArray catJsonArray;
    private int catIndex = 0;
    private ArrayList<Integer> catIDArraySelected = new ArrayList<Integer>();

    private String categoryId = "";
    private String stringStart, stringEnd;
    private String startPrice = "";
    private String endPrice = "";
    private String categoryList, from;
    private MultiLevelListView mListView;
    private JSONArray categoryJsonArray;

    private DataProvider dataProvider;
    private MultiLevelAdapter multiLevelAdapter;
    private SessionManager sessionManager;
    private JSONObject categoryJsonObject;
    private String permalink = "";
    private String brandSelected = "";
    private String categorySelected = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter);

        UncaughtExceptionHandler uncaughtExceptionHandler = new UncaughtExceptionHandler();
        uncaughtExceptionHandler.uncaughtException(this);

        scrollView = (RelativeLayout) findViewById(R.id.scroll_view);
        filterCategory = (TextView) findViewById(R.id.category);
        filterBrand = (TextView) findViewById(R.id.brand);
        filterPrince = (TextView) findViewById(R.id.price);

        btnSearch = (Button) findViewById(R.id.search);
        btnReset = (Button) findViewById(R.id.reset);

        toolbar = (Toolbar) findViewById(R.id.app_toolbar);
        TextView mTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
        mTitle.setText("Filter");
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        sessionManager = new SessionManager(FilterActivity.this);
        intent = getIntent();
        categoryList = intent.getStringExtra("category");
        brandIndex = intent.getIntExtra("merekIndex", 0);
        brandString = intent.getStringExtra("brands");
        startPrice = intent.getStringExtra("startprice");
        endPrice = intent.getStringExtra("endprice");
        from = intent.getStringExtra("from");
        brandSelected = intent.getStringExtra("brandSelected");
        categorySelected = intent.getStringExtra("categorySelected");

        if (intent.getStringExtra("cat").toLowerCase().contains("category")){
            filterCategory.setVisibility(View.GONE);
        }else if (from.equals("brand")){
            filterBrand.setVisibility(View.GONE);
        }

        try {
            brandJsonArray = new JSONArray(brandString);
            categoryJsonArray = new JSONArray(categoryList);
            dataProvider = new DataProvider(categoryJsonArray, "filter");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        btnSearch.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(minText == null) stringStart = "";
                        else stringStart = minText.getText().toString();

                        if(maxText == null) stringEnd = "";
                        else stringEnd = maxText.getText().toString();

                        String merekID = "";

                        if (brandIDArraySelected.size() > 0){
                            for (int i = 0; i < brandIDArraySelected.size(); i++) {
                                merekID = brandIDArray[brandIDArraySelected.get(0)];
                            }
                        }else{
                            merekID = brandSelected;
                        }

                        String catID = "";
                        for (int i = 0; i < catIDArraySelected.size(); i++) {
                            catID += catIDArray[catIDArraySelected.get(i)] + ";";
                        }

                        Intent filterIntent = new Intent();
                        filterIntent.putExtra("filt", "nocat");
                        filterIntent.putExtra("merekIndex", brandIndex);
                        filterIntent.putExtra("merekID", merekID);
                        filterIntent.putExtra("maxPrice", stringStart);
                        filterIntent.putExtra("minPrice", stringEnd);
                        filterIntent.putExtra("categoryId", catID);
                        setResult(RESULT_OK, filterIntent);
                        finish();
                        overridePendingTransition(R.anim.right_in, R.anim.right_out);
                    }
                }
        );

        btnReset.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(minText != null && maxText != null){
                            maxText.setText("");
                            minText.setText("");
                        }

                        if (brandIndex > 0) setBrandIndex(0);
                        if (catIndex > 0) setCategoryId(0);
                    }
                }
        );

        setFilter();
        setCategory();
    }

    public void setCategory(){
//        scrollView.removeView(view);
//        view  = null;
//        LayoutInflater inflater = (LayoutInflater) FilterActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//
//        if (view == null) {
//            view = inflater.inflate(R.layout.activity_filter_category, null);
//            scrollView.addView(view);
//        }
//
//        TextView allCat = (TextView) view.findViewById(R.id.semua_kategori);
//
//        mListView = (MultiLevelListView) view.findViewById(R.id.listViewFilter);
//        mListView.mListView.setDivider(null);
//        mListView.mListView.setDividerHeight(0);
//
//        multiLevelAdapter = new MultiLevelAdapter(FilterActivity.this, dataProvider);
//        mListView.setAdapter(multiLevelAdapter);
//        multiLevelAdapter.setDataItems(dataProvider.getInitialItems());
//
//        allCat.setOnClickListener(
//                new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        setCategoryId("");
//                    }
//                }
//        );

        brandFilterAdapter = new BrandFilterAdapter(FilterActivity.this, catArray, catIDArraySelected, "", "category");

        scrollView.removeView(view);
        view = null;
        inflater = (LayoutInflater) FilterActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (view == null) {
            view = inflater.inflate(R.layout.activity_filter_brand, null);
            scrollView.addView(view);
        }

//        final EditText searchText = (EditText) view.findViewById(R.id.search_text);
        heightAdjustableListView = (HeightAdjustableListView) view.findViewById(R.id.brand_text);
//
//        searchText.addTextChangedListener(
//                new TextWatcher() {
//                    @Override
//                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
//
//                    @Override
//                    public void onTextChanged(CharSequence s, int start, int before, int count) {}
//
//                    @Override
//                    public void afterTextChanged(Editable s) {
//                        if(s.length() > 0){
//                            brandFilterAdapter = new BrandFilterAdapter(FilterActivity.this, catArray, catIDArraySelected, s.toString(), "category");
//                        }else if(s.length() == 0){
//                            brandFilterAdapter = new BrandFilterAdapter(FilterActivity.this, catArray, catIDArraySelected, "", "category");
//                        }
//
//                        heightAdjustableListView.setAdapter(brandFilterAdapter);
//                    }
//                }
//        );

        heightAdjustableListView.setAdapter(brandFilterAdapter);
    }

    public void setFilter(){

        //brand
        List<JSONObject> jsonList = new ArrayList<JSONObject>();
        for (int i = 0; i < brandJsonArray.length(); i++) {
            try {
                jsonList.add(brandJsonArray.getJSONObject(i));
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

        for (int i = 0; i < brandJsonArray.length(); i++) {
            sortedJsonArray.put(jsonList.get(i));
        }

        brandArray = new String[sortedJsonArray.length()];
        brandIDArray = new String[sortedJsonArray.length()];
        //brandIDArray[0] = null;
        //brandArray[0] = "Semua Merek";

        for (int i = 0; i < sortedJsonArray.length(); i++) {
            try {
                brandIDArray[i] = sortedJsonArray.getJSONObject(i).getString("ID");
                brandArray[i] = sortedJsonArray.getJSONObject(i).getString("Name");

                if(brandSelected.contains(brandIDArray[i])){
                    brandIDArraySelected.add(i);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        //category
        List<JSONObject> jsonCatList = new ArrayList<JSONObject>();
        for (int i = 0; i < categoryJsonArray.length(); i++) {
            try {
                jsonCatList.add(categoryJsonArray.getJSONObject(i));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        Collections.sort(jsonCatList, new Comparator<JSONObject>() {

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

        sortedCatJsonArray = new JSONArray();

        for (int i = 0; i < categoryJsonArray.length(); i++) {
            sortedCatJsonArray.put(jsonCatList.get(i));
        }

        catArray = new String[sortedCatJsonArray.length()];
        catIDArray = new String[sortedCatJsonArray.length()];
        //catIDArray[0] = null;
        //catArray[0] = "Semua Kategori";

        for (int i = 0; i < sortedCatJsonArray.length(); i++) {
            try {
                catIDArray[i] = sortedCatJsonArray.getJSONObject(i).getString("ID");
                catArray[i] = sortedCatJsonArray.getJSONObject(i).getString("Name");

                if(categorySelected.contains(catIDArray[i])){
                    catIDArraySelected.add(i);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }






        filterCategory.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        filterCategory.setBackgroundColor(Color.parseColor("#ffffff"));
                        filterBrand.setBackgroundColor(Color.parseColor("#e0e0e0"));
                        filterPrince.setBackgroundColor(Color.parseColor("#e0e0e0"));

                        setCategory();
                    }
                }
        );

        filterBrand.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        filterCategory.setBackgroundColor(Color.parseColor("#e0e0e0"));
                        filterBrand.setBackgroundColor(Color.parseColor("#ffffff"));
                        filterPrince.setBackgroundColor(Color.parseColor("#e0e0e0"));

                        setBranch();
                    }
                }
        );

        filterPrince.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        filterCategory.setBackgroundColor(Color.parseColor("#e0e0e0"));
                        filterBrand.setBackgroundColor(Color.parseColor("#e0e0e0"));
                        filterPrince.setBackgroundColor(Color.parseColor("#ffffff"));

                        scrollView.removeView(view);
                        view  = null;
                        LayoutInflater inflater = (LayoutInflater) FilterActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

                        if (view == null) {
                            view = inflater.inflate(R.layout.activity_filter_price, null);
                            scrollView.addView(view);
                        }

                        maxText = (EditText) view.findViewById(R.id.max_text);
                        minText = (EditText) view.findViewById(R.id.min_text);

                        if (endPrice != null) maxText.setText(endPrice);

                        if (!startPrice.equals("")) minText.setText(startPrice);
                    }
                }
        );
    }

    public void setBranch(){
        brandFilterAdapter = new BrandFilterAdapter(FilterActivity.this, brandArray, brandIDArraySelected, "", "brand");

        scrollView.removeView(view);
        view = null;
        inflater = (LayoutInflater) FilterActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (view == null) {
            view = inflater.inflate(R.layout.activity_filter_brand, null);
            scrollView.addView(view);
        }

//        final EditText searchText = (EditText) view.findViewById(R.id.search_text);
        heightAdjustableListView = (HeightAdjustableListView) view.findViewById(R.id.brand_text);
//
//        searchText.addTextChangedListener(
//                new TextWatcher() {
//                    @Override
//                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
//
//                    @Override
//                    public void onTextChanged(CharSequence s, int start, int before, int count) {}
//
//                    @Override
//                    public void afterTextChanged(Editable s) {
//                        if(s.length() > 0){
//                            brandFilterAdapter = new BrandFilterAdapter(FilterActivity.this, brandArray, brandIDArraySelected, s.toString(), "brand");
//                        }else if(s.length() == 0){
//                            brandFilterAdapter = new BrandFilterAdapter(FilterActivity.this, brandArray, brandIDArraySelected, "", "brand");
//                        }
//
//                        heightAdjustableListView.setAdapter(brandFilterAdapter);
//                    }
//                }
//        );

        heightAdjustableListView.setAdapter(brandFilterAdapter);
    }

    public void setBrandIndex(int passBrandIndex){
        scrollView.removeView(view);
        view = null;
        inflater = (LayoutInflater) FilterActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (view == null) {
            view = inflater.inflate(R.layout.activity_filter_brand, null);
            scrollView.addView(view);
        }

        heightAdjustableListView = (HeightAdjustableListView) view.findViewById(R.id.brand_text);

        brandIndex = passBrandIndex;
        brandIDArraySelected = new ArrayList<Integer>();
        brandIDArraySelected.add(passBrandIndex);
        brandFilterAdapter = new BrandFilterAdapter(FilterActivity.this, brandArray, brandIDArraySelected, "", "brand");
        heightAdjustableListView.setAdapter(brandFilterAdapter);
    }

    public void setCategoryId(int passCategoryIndex){
//        this.categoryId = categoryId;
//        try {
//            categoryJsonObject = new JSONObject(sessionManager.getSingleCategory());
//            permalink = categoryJsonObject.getString("Permalink");
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//
//        Intent filterIntent = new Intent();
//        filterIntent.putExtra("filt", "cat");
//        filterIntent.putExtra("categoryId", categoryId);
//
//        if (brandIndex == 0){
//            filterIntent.putExtra("permalink", permalink);
//        } else {
//            filterIntent.putExtra("permalink", permalink);
//            filterIntent.putExtra("merekID", brandIDArray[0]);
//        }
//
//        setResult(RESULT_OK, filterIntent);
//        finish();
//        overridePendingTransition(R.anim.right_in, R.anim.right_out);

        scrollView.removeView(view);
        view = null;
        inflater = (LayoutInflater) FilterActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (view == null) {
            view = inflater.inflate(R.layout.activity_filter_brand, null);
            scrollView.addView(view);
        }

        heightAdjustableListView = (HeightAdjustableListView) view.findViewById(R.id.brand_text);

        catIndex = passCategoryIndex;

        if(catIDArraySelected.contains(passCategoryIndex)){
            for (int i = 0; i < catIDArraySelected.size(); i++) {
                if(catIDArraySelected.get(i) == passCategoryIndex){
                    catIDArraySelected.remove(i);
                }
            }
        }else {
            catIDArraySelected.add(passCategoryIndex);
        }

        brandFilterAdapter = new BrandFilterAdapter(FilterActivity.this, catArray, catIDArraySelected, "", "category");
        heightAdjustableListView.setAdapter(brandFilterAdapter);
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
        }

        return super.onOptionsItemSelected(item);
    }
}
