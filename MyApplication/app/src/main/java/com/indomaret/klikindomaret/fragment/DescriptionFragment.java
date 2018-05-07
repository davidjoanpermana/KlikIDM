package com.indomaret.klikindomaret.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.indomaret.klikindomaret.R;
import com.indomaret.klikindomaret.adapter.ProductDescriptionAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * A simple {@link Fragment} subclass.
 */
public class DescriptionFragment extends Fragment {
    private View view;
    private JSONObject product;
    private JSONArray productAttribut;

    public DescriptionFragment(JSONObject product) {
        this.product = product;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_description, container, false);

        productAttribut = new JSONArray();
        RecyclerView descriptionList = (RecyclerView) view.findViewById(R.id.product_description_list);

        try {
            productAttribut = product.getJSONArray("ProductAttributes");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        ProductDescriptionAdapter productDescriptionAdapter = new ProductDescriptionAdapter(productAttribut);
        descriptionList.setAdapter(productDescriptionAdapter);

        descriptionList.setLayoutManager(new LinearLayoutManager(this.getActivity()));

        return view;
    }
}
