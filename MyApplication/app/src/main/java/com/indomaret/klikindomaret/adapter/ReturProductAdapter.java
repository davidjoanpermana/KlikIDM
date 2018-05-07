package com.indomaret.klikindomaret.adapter;

import android.app.Activity;
import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.indomaret.klikindomaret.R;
import com.indomaret.klikindomaret.activity.ReturActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.List;

/**
 * Created by indomaretitsd7 on 6/16/16.
 */
public class ReturProductAdapter extends BaseAdapter {
    private LayoutInflater inflater;
    private Activity activity;
    private List<JSONObject> productList;
    private int status;
    private String type;
    private EditText etReason;

    public ReturProductAdapter(Activity activity, List<JSONObject> productList, int status, String type){
        this.activity = activity;
        this.productList = productList;
        this.status = status;
        this.type = type;
    }

    @Override
    public int getCount() {
        return productList.size();
    }

    @Override
    public Object getItem(int position) {
        return productList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @SuppressWarnings("deprecation")
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (inflater == null)
            inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (convertView == null)
            convertView = inflater.inflate(R.layout.activity_retur_product, null);

        final JSONObject productObject = productList.get(position);

        TextView sku = (TextView) convertView.findViewById(R.id.product_sku);
        TextView productName = (TextView) convertView.findViewById(R.id.product_name);
        TextView productQuantity = (TextView) convertView.findViewById(R.id.product_quantity);
        TextView productStatus = (TextView) convertView.findViewById(R.id.product_status);

        LinearLayout linStatus = (LinearLayout) convertView.findViewById(R.id.linStatus);
        LinearLayout linReason = (LinearLayout) convertView.findViewById(R.id.linReason);
        etReason = (EditText) convertView.findViewById(R.id.etReason);

        if(type == "retur")
        {
            linReason.setVisibility(View.VISIBLE);
        }

        try {
            sku.setText(productObject.getString("PLU"));
            productName.setText(productObject.getString("ProductName"));
            productQuantity.setText(productObject.getString("QuantityRetur"));

            if(type == "retur") {
                if (status == 0) {
                    productStatus.setText("Belum dikonfirmasi");
                    etReason.setEnabled(true);
                } else {
                    productStatus.setText("Sudah dikonfirmasi");
                    etReason.setEnabled(false);
                    etReason.setText(productObject.getString("ReasonRetur"));
                }
            }
            else{
                linStatus.setVisibility(View.GONE);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        etReason.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                ((ReturActivity) activity).saveReason(editable.toString(), position);
            }
        });

        return convertView;
    }
}
