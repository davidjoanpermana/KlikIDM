package com.indomaret.klikindomaret.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.indomaret.klikindomaret.R;
import com.indomaret.klikindomaret.fragment.PulsaFragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by indomaretitsd7 on 6/14/16.
 */
public class ContactItemAdapter extends BaseAdapter {
    private Activity activity;
    private JSONArray contactList;
    protected JSONObject contactObject;
    private LayoutInflater inflater;
    private LinearLayout linearContact;
    private PulsaFragment pulsaFragment;

    public ContactItemAdapter(Activity activity, JSONArray contactList, PulsaFragment pulsaFragment) {
        this.activity = activity;
        this.contactList = contactList;
        this.pulsaFragment = pulsaFragment;
    }

    @Override
    public int getCount() {
        return contactList.length();
    }

    @Override
    public Object getItem(int position) {
        try {
            return contactList.getJSONObject(position);
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
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
            convertView = inflater.inflate(R.layout.contact_item, null);

        TextView contactName = (TextView) convertView.findViewById(R.id.contact_name);
        TextView contactPhone = (TextView) convertView.findViewById(R.id.contact_phone);
        linearContact = (LinearLayout) convertView.findViewById(R.id.linear_contact);

        try {
            contactObject = contactList.getJSONObject(position);

            contactName.setText(contactObject.getString("Name"));
            contactPhone.setText(contactObject.getString("PhoneNumber"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        linearContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    pulsaFragment.setPhoneNumber(contactList.getJSONObject(position).getString("PhoneNumber")
                            .replace("+62", "0").replace("-", "").replace(" ", ""));

                    pulsaFragment.alertDialog.dismiss();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        return convertView;
    }

}