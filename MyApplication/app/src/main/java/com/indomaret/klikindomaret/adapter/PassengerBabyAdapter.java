package com.indomaret.klikindomaret.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.BaseAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.indomaret.klikindomaret.R;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by indomaretitsd7 on 6/14/16.
 */
public class PassengerBabyAdapter extends BaseAdapter{
    private LayoutInflater inflater;
    private Activity activity;
    private JSONArray countBaby;
    private JSONArray passengerList;
    private List<String> listGender = new ArrayList<String>();
    private List<String> listPassenger = new ArrayList<String>();

    public PassengerBabyAdapter(Activity activity, JSONArray countBaby, JSONArray passengerList){
        this.activity = activity;
        this.countBaby = countBaby;
        this.passengerList = passengerList;
    }

    @Override
    public int getCount() {
        return countBaby.length();
    }

    @Override
    public Object getItem(int position) {
        try {
            return countBaby.getString(position);
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
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
            convertView = inflater.inflate(R.layout.list_passenger_baby, null);

        String titleBaby = null;
        try {
            titleBaby = countBaby.getString(position);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        TextView titlePassenger = (TextView) convertView.findViewById(R.id.title_passenger);
        final Spinner inputGender = (Spinner) convertView.findViewById(R.id.input_gender);
        final AutoCompleteTextView inputName = (AutoCompleteTextView) convertView.findViewById(R.id.input_name);

        titlePassenger.setText(titleBaby);
        listGender = new ArrayList<>();
        listGender.add("Tuan");
        listGender.add("Nona");

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(activity, android.R.layout.simple_spinner_item, listGender);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        inputGender.setAdapter(dataAdapter);

        try {
            listPassenger = new ArrayList<String>();
            for (int i=0; i<passengerList.length(); i++){
                if (passengerList.getJSONObject(i).getString("Maturity").equals("1")){
                    listPassenger.add(passengerList.getJSONObject(i).getString("FullName"));
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        ArrayAdapter<String> adapterOriginStasiun = new ArrayAdapter<>(activity,android.R.layout.simple_spinner_dropdown_item, listPassenger);
        inputName.setThreshold(1);
        inputName.setAdapter(adapterOriginStasiun);

        inputName.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                try {
                    for (int i=0; i<passengerList.length(); i++){
                        if (passengerList.getJSONObject(i).getString("FullName").equals(inputName.getText().toString())){
                            inputGender.setSelection(passengerList.getJSONObject(i).getInt("Salutation"));
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        return convertView;
    }
}