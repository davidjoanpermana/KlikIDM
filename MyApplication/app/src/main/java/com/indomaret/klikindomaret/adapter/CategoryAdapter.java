package com.indomaret.klikindomaret.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.LinearLayout;

import com.indomaret.klikindomaret.R;
import com.indomaret.klikindomaret.activity.CategoryActivity;
import com.indomaret.klikindomaret.activity.ProductActivity;
import com.indomaret.klikindomaret.helper.SessionManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by indomaretitsd7 on 6/22/16.
 */
public class CategoryAdapter extends BaseAdapter {
    private Intent intent;
    private SessionManager sessionManager;
    private Activity activity;
    private LayoutInflater inflater;
    private JSONArray productList;
    private JSONObject product;
    private List<JSONObject> products;

    public CategoryAdapter(Activity activity, JSONArray productList){
        this.activity = activity;
        this.productList = productList;
    }

    @Override
    public int getCount() {
        return productList.length();
    }

    @Override
    public Object getItem(int position) {
        try {
            return productList.getJSONObject(position);
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
            convertView = inflater.inflate(R.layout.activity_category_item, null);

        intent = activity.getIntent();
        sessionManager = new SessionManager(activity);

        Button btnCategory = (Button) convertView.findViewById(R.id.btn_category);
        GridView gridProductList = (GridView) convertView.findViewById(R.id.grid_product);
        LinearLayout linearGridd = (LinearLayout) convertView.findViewById(R.id.linear_grid);

        try {
            products = new ArrayList<>();
            product = productList.getJSONObject(position);

            for (int i=0; i<product.getJSONArray("ProductRecommendation").length(); i++){
                products.add(product.getJSONArray("ProductRecommendation").getJSONObject(i));
            }

            DisplayMetrics displayMetrics = new DisplayMetrics();
            activity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
            int width = displayMetrics.widthPixels;

            ViewGroup.LayoutParams params = linearGridd.getLayoutParams();
            params.width = ((width / 3) + 30) * products.size();
//            params.width = ((width / 4)) * products.size();
            System.out.println("--- width : "+width);
            System.out.println("--- params.width : "+params.width);

            linearGridd.setLayoutParams(params);

            btnCategory.setText(product.getString("Name"));

            ProductCategoryAdapter productCategoryAdapter = new ProductCategoryAdapter(activity, products, "catByLevel");
            gridProductList.setAdapter(productCategoryAdapter);

            gridProductList.setOnItemClickListener(
                    new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int index, long id) {
                            try {
                                intent = new Intent(activity, ProductActivity.class);
                                intent.putExtra("data", productList.getJSONObject(position).getJSONArray("ProductRecommendation").get(index).toString());
                                intent.putExtra("cat", "category");
                                activity.startActivity(intent);
                                activity.overridePendingTransition(R.anim.left_in, R.anim.left_out);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
            );
        } catch (JSONException e) {
            e.printStackTrace();
        }

        btnCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    sessionManager.saveSingleCategory(productList.getJSONObject(position).toString());
                    intent = new Intent(activity, CategoryActivity.class);
                    intent.putExtra("cat", "category");
                    activity.startActivity(intent);
                    activity.overridePendingTransition(R.anim.left_in, R.anim.left_out);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        return convertView;
    }
}
