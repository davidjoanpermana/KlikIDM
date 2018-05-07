package com.indomaret.klikindomaret.adapter;

import android.app.Activity;
import android.content.Context;
import android.location.Address;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.indomaret.klikindomaret.R;
import com.indomaret.klikindomaret.activity.AddAdressActivity;
import com.indomaret.klikindomaret.activity.CartActivity;
import com.indomaret.klikindomaret.helper.Month;

import java.util.List;

/**
 * Created by USER on 8/30/2017.
 */

public class SearchAddressAdapter extends BaseAdapter {
    private Activity activity;
    private LayoutInflater inflater;
    private List<Address> addressList;

    public SearchAddressAdapter(Activity activity, List<Address> addressList) {
        this.activity = activity;
        this.addressList = addressList;
    }

    @Override
    public int getCount() {
        return addressList.size();
    }

    @Override
    public Object getItem(int position) {
        return addressList.get(position);

    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (inflater == null)
            inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (convertView == null)
            convertView = inflater.inflate(R.layout.item_search_address, null);

        TextView tvAddress = (TextView) convertView.findViewById(R.id.tvAddress);
        RelativeLayout item = (RelativeLayout) convertView.findViewById(R.id.linItem);

//        try {
            final Address address = addressList.get(position);
            tvAddress.setText(address.getAddressLine(0));
//        } catch (Exception e) {
//
//        }

        item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(activity != null){
                    if(activity instanceof AddAdressActivity){
                        ((AddAdressActivity) activity).selectLocation(address);
                    }
                }
            }
        });



        return convertView;
    }
}