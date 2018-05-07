package com.indomaret.klikindomaret.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.indomaret.klikindomaret.R;
import com.indomaret.klikindomaret.adapter.ListFilterAdapter;
import com.indomaret.klikindomaret.helper.UncaughtExceptionHandler;
import com.indomaret.klikindomaret.views.HeightAdjustableListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class FilterKAIActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private Intent intent;
    private String values, from;
    private TextView filterTime, filterClass, filterTrain, filterPrince, mTitle;
    private Button btnSearch, btnReset;
    private HeightAdjustableListView itemList;
    private JSONObject dataObject = new JSONObject();
    private JSONObject userData = new JSONObject();
    private JSONArray dataArray = new JSONArray();
    private JSONArray dataReturnPriceArray = new JSONArray();
    private JSONArray timeArray = new JSONArray();
    private JSONArray classArray = new JSONArray();
    private JSONArray trainArray = new JSONArray();
    private JSONArray priceArray = new JSONArray();
    private ListFilterAdapter listFilterAdapter;
    private boolean destinationStatus = false;
    private String scheduleNow;
    private String currentPage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter_kai);

        UncaughtExceptionHandler uncaughtExceptionHandler = new UncaughtExceptionHandler();
        uncaughtExceptionHandler.uncaughtException(this);

        filterTime = (TextView) findViewById(R.id.time);
        filterClass = (TextView) findViewById(R.id.class_train);
        filterTrain = (TextView) findViewById(R.id.train);
        filterPrince = (TextView) findViewById(R.id.price);

        btnSearch = (Button) findViewById(R.id.search);
        btnReset = (Button) findViewById(R.id.reset);
        itemList = (HeightAdjustableListView) findViewById(R.id.list_item);

        toolbar = (Toolbar) findViewById(R.id.app_toolbar);
        mTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
        mTitle.setText("Filter");
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        from = "home";
        intent = getIntent();
        try {
            userData = new JSONObject(intent.getStringExtra("userData"));
            destinationStatus = intent.getBooleanExtra("destinationStatus", false);
            scheduleNow = intent.getStringExtra("scheduleNow");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            dataObject = new JSONObject();
            dataArray = new JSONArray(intent.getStringExtra("jsonObjects"));


            setTimeArray();

            for (int i = 0; i < dataArray.length(); i++) {
                dataObject = new JSONObject();
                if (!classArray.toString().contains(dataArray.getJSONObject(i).getString("TrainClassName"))) {
                    dataObject.put("TrainClassName", dataArray.getJSONObject(i).getString("TrainClassName"));
                    dataObject.put("status", "0");
                    classArray.put(dataObject);
                }

                dataObject = new JSONObject();
                if (!trainArray.toString().contains(dataArray.getJSONObject(i).getString("TrainName"))) {
                    dataObject.put("TrainName", dataArray.getJSONObject(i).getString("TrainName"));
                    dataObject.put("status", "0");
                    trainArray.put(dataObject);
                }
            }

            dataObject = new JSONObject();
            dataObject.put("position", "0");
            dataObject.put("AdultPrice", "< Rp 150.000");
            dataObject.put("status", "0");
            dataObject.put("minPrice", "0");
            dataObject.put("maxPrice", "150000");
            priceArray.put(dataObject);

            dataObject = new JSONObject();
            dataObject.put("position", "1");
            dataObject.put("AdultPrice", "Rp 150.000 - Rp 300.000");
            dataObject.put("status", "0");
            dataObject.put("minPrice", "150000");
            dataObject.put("maxPrice", "300000");
            priceArray.put(dataObject);

            dataObject = new JSONObject();
            dataObject.put("position", "2");
            dataObject.put("AdultPrice", "Rp 300.000 - Rp 450.000");
            dataObject.put("status", "0");
            dataObject.put("minPrice", "300000");
            dataObject.put("maxPrice", "450000");
            priceArray.put(dataObject);

            dataObject = new JSONObject();
            dataObject.put("position", "3");
            dataObject.put("AdultPrice", "Rp 450.000 >");
            dataObject.put("status", "0");
            dataObject.put("minPrice", "450000");
            dataObject.put("maxPrice", "0");
            priceArray.put(dataObject);

            listFilterAdapter = new ListFilterAdapter(FilterKAIActivity.this, timeArray, "time");
            itemList.setAdapter(listFilterAdapter);

            currentPage = "time";
        } catch (JSONException e) {
            e.printStackTrace();
        }

        filterTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                filterTime.setBackgroundColor(Color.parseColor("#ffffff"));
                filterClass.setBackgroundColor(Color.parseColor("#e0e0e0"));
                filterTrain.setBackgroundColor(Color.parseColor("#e0e0e0"));
                filterPrince.setBackgroundColor(Color.parseColor("#e0e0e0"));

                listFilterAdapter = new ListFilterAdapter(FilterKAIActivity.this, timeArray, "time");
                itemList.setAdapter(listFilterAdapter);
                currentPage = "time";
            }
        });

        filterClass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                filterTime.setBackgroundColor(Color.parseColor("#e0e0e0"));
                filterClass.setBackgroundColor(Color.parseColor("#ffffff"));
                filterTrain.setBackgroundColor(Color.parseColor("#e0e0e0"));
                filterPrince.setBackgroundColor(Color.parseColor("#e0e0e0"));

                listFilterAdapter = new ListFilterAdapter(FilterKAIActivity.this, classArray, "class");
                itemList.setAdapter(listFilterAdapter);
                currentPage = "class";
            }
        });

        filterTrain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                filterTime.setBackgroundColor(Color.parseColor("#e0e0e0"));
                filterClass.setBackgroundColor(Color.parseColor("#e0e0e0"));
                filterTrain.setBackgroundColor(Color.parseColor("#ffffff"));
                filterPrince.setBackgroundColor(Color.parseColor("#e0e0e0"));

                listFilterAdapter = new ListFilterAdapter(FilterKAIActivity.this, trainArray, "train");
                itemList.setAdapter(listFilterAdapter);
                currentPage = "train";
            }
        });

        filterPrince.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                filterTime.setBackgroundColor(Color.parseColor("#e0e0e0"));
                filterClass.setBackgroundColor(Color.parseColor("#e0e0e0"));
                filterTrain.setBackgroundColor(Color.parseColor("#e0e0e0"));
                filterPrince.setBackgroundColor(Color.parseColor("#ffffff"));

                listFilterAdapter = new ListFilterAdapter(FilterKAIActivity.this, priceArray, "price");
                itemList.setAdapter(listFilterAdapter);
                currentPage = "price";
            }
        });

        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                from = "home";

                try {
                    for (int i = 0; i < timeArray.length(); i++) {
                        timeArray.getJSONObject(i).put("status", "0");
                    }

                    for (int i = 0; i < classArray.length(); i++) {
                        classArray.getJSONObject(i).put("status", "0");
                    }

                    for (int i = 0; i < trainArray.length(); i++) {
                        trainArray.getJSONObject(i).put("status", "0");
                    }

                    for (int i = 0; i < priceArray.length(); i++) {
                        priceArray.getJSONObject(i).put("status", "0");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                switch (currentPage){
                    case "time" :
                        listFilterAdapter = new ListFilterAdapter(FilterKAIActivity.this, timeArray, "time");
                        itemList.setAdapter(listFilterAdapter);
                        break;
                    case "class" :
                        listFilterAdapter = new ListFilterAdapter(FilterKAIActivity.this, classArray, "class");
                        itemList.setAdapter(listFilterAdapter);
                        break;
                    case "train" :
                        listFilterAdapter = new ListFilterAdapter(FilterKAIActivity.this, trainArray, "train");
                        itemList.setAdapter(listFilterAdapter);
                        break;
                    case "price" :
                        listFilterAdapter = new ListFilterAdapter(FilterKAIActivity.this, priceArray, "price");
                        itemList.setAdapter(listFilterAdapter);
                        break;
                }
            }
        });

        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                intent = new Intent();

                JSONArray filterByTime = new JSONArray();
                JSONArray filterByClass = new JSONArray();
                JSONArray filterByTrain = new JSONArray();

                try {
                    for (int i = 0; i < timeArray.length(); i++) {
                        if (timeArray.getJSONObject(i).getString("status").equals("1")) {
                            filterByTime.put(timeArray.getJSONObject(i).getString("type"));
                        }
                    }

                    for (int i = 0; i < classArray.length(); i++) {
                        if (classArray.getJSONObject(i).getString("status").equals("1")) {
                            filterByClass.put(classArray.getJSONObject(i).getString("TrainClassName"));
                        }
                    }

                    for (int i = 0; i < trainArray.length(); i++) {
                        if (trainArray.getJSONObject(i).getString("status").equals("1")) {
                            filterByTrain.put(trainArray.getJSONObject(i).getString("TrainName"));
                        }
                    }

                    for (int i = 0; i < priceArray.length(); i++) {
                        dataObject = new JSONObject();
                        if (priceArray.getJSONObject(i).getString("status").equals("1")) {
                            if (priceArray.length() == 1) {
                                if (priceArray.getJSONObject(i).getString("position").equals("0")) {
                                    dataObject.put("minPrice", "0");
                                    dataObject.put("maxPrice", priceArray.getJSONObject(i).getString("maxPrice"));
                                    dataReturnPriceArray.put(dataObject);
                                } else if (priceArray.getJSONObject(i).getString("position").equals("3")) {
                                    dataObject.put("minPrice", priceArray.getJSONObject(i).getString("minPrice"));
                                    dataObject.put("maxPrice", "0");
                                    dataReturnPriceArray.put(dataObject);
                                } else {
                                    dataObject.put("minPrice", priceArray.getJSONObject(i).getString("minPrice"));
                                    dataObject.put("maxPrice", priceArray.getJSONObject(i).getString("maxPrice"));
                                    dataReturnPriceArray.put(dataObject);
                                }
                            } else {
                                if (i == priceArray.length() - 1) {
                                    if (priceArray.getJSONObject(i).getString("position").equals("3")) {
                                        dataObject.put("maxPrice", priceArray.getJSONObject(i).getString("minPrice"));
                                    } else {
                                        dataObject.put("maxPrice", priceArray.getJSONObject(i).getString("maxPrice"));
                                    }
                                } else {
                                    if (priceArray.getJSONObject(i).getString("position").equals("0")) {
                                        dataObject.put("minPrice", priceArray.getJSONObject(i).getString("minPrice"));
                                    } else {
                                        dataObject.put("minPrice", priceArray.getJSONObject(i).getString("maxPrice"));
                                    }
                                }
                            }
                            dataReturnPriceArray.put(priceArray.getJSONObject(i));
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                intent.putExtra("filterByTime", filterByTime.toString());
                intent.putExtra("filterByClass", filterByClass.toString());
                intent.putExtra("filterByTrain", filterByTrain.toString());
                intent.putExtra("filterByPrice", dataReturnPriceArray.toString());

                if (filterByTime.length() > 0 || filterByClass.length() > 0 ||
                        filterByTrain.length() > 0 || dataReturnPriceArray.length() > 0) {
                    intent.putExtra("showAll", false);
                } else {
                    intent.putExtra("showAll", true);
                }

                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }

    public void setList(JSONObject values, String type, int position) {
        try {
            if (type.equals("time")) {
                timeArray.put(position, values);
            } else if (type.equals("class")) {
                classArray.put(position, values);
            } else if (type.equals("train")) {
                trainArray.put(position, values);
            } else if (type.equals("price")) {
                priceArray.put(position, values);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void setTimeArray(){
        try{
            dataObject = new JSONObject();
            dataObject.put("DepartureDate", "00:00 - 05:59 WIB");
            dataObject.put("status", "0");
            dataObject.put("type", "1");
            timeArray.put(dataObject);

            dataObject = new JSONObject();
            dataObject.put("DepartureDate", "06:00 - 11:59 WIB");
            dataObject.put("status", "0");
            dataObject.put("type", "2");
            timeArray.put(dataObject);

            dataObject = new JSONObject();
            dataObject.put("DepartureDate", "12:00 - 17:59 WIB");
            dataObject.put("status", "0");
            dataObject.put("type", "3");
            timeArray.put(dataObject);

            dataObject = new JSONObject();
            dataObject.put("DepartureDate", "18:00 - 23.59 WIB");
            dataObject.put("status", "0");
            dataObject.put("type", "4");
            timeArray.put(dataObject);

        }catch (Exception e){

        }
    }

    @Override
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
