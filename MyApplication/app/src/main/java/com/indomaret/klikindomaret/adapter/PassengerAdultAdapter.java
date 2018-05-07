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
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.indomaret.klikindomaret.R;
import com.indomaret.klikindomaret.activity.PassengerActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by indomaretitsd7 on 6/14/16.
 */
public class PassengerAdultAdapter extends BaseAdapter{
    private LayoutInflater inflater;
    private JSONArray countAdult;
    private JSONArray passengerList;
    private Activity activity;
    private List<String> listGender = new ArrayList<String>();
    private List<String> listPassenger = new ArrayList<String>();
    private JSONObject passengerAdult;

    public PassengerAdultAdapter(Activity activity, JSONArray countAdult, JSONArray passengerList){
        this.activity = activity;
        this.countAdult = countAdult;
        this.passengerList = passengerList;
    }

    @Override
    public int getCount() {
        return countAdult.length();
    }

    @Override
    public Object getItem(int position) {
        try {
            return countAdult.getString(position);
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

        String titleAdult = null;
        try {
            titleAdult = countAdult.getString(position);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (convertView == null)
            convertView = inflater.inflate(R.layout.list_passenger_adult, null);

        TextView titlePassenger = (TextView) convertView.findViewById(R.id.title_passenger);
        final Spinner inputGender = (Spinner) convertView.findViewById(R.id.input_gender);
        final AutoCompleteTextView inputName = (AutoCompleteTextView) convertView.findViewById(R.id.input_name);
        final EditText inputIdentitas = (EditText) convertView.findViewById(R.id.input_identitas);
        final CheckBox btnCheck = (CheckBox) convertView.findViewById(R.id.btn_check);
        LinearLayout linearCheck = (LinearLayout) convertView.findViewById(R.id.linear_check);

        listGender = new ArrayList<>();
        listGender.add("Tuan");
        listGender.add("Nyonya");

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(activity, android.R.layout.simple_spinner_item, listGender);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        inputGender.setAdapter(dataAdapter);

        try {
            listPassenger = new ArrayList<String>();
            for (int i=0; i<passengerList.length(); i++){
                if (passengerList.getJSONObject(i).getString("Maturity").equals("0")){
                    listPassenger.add(passengerList.getJSONObject(i).getString("FullName"));
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        ArrayAdapter<String> adapterOriginStasiun = new ArrayAdapter<>(activity,android.R.layout.simple_spinner_dropdown_item, listPassenger);
        inputName.setThreshold(1);
        inputName.setAdapter(adapterOriginStasiun);

        titlePassenger.setText(titleAdult);

        inputName.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                try {
                    for (int i=0; i<passengerList.length(); i++){
                        if (passengerList.getJSONObject(i).getString("FullName").equals(inputName.getText().toString())){
                            inputGender.setSelection(passengerList.getJSONObject(i).getInt("Salutation"));
                            inputIdentitas.setText(passengerList.getJSONObject(i).getString("Identity"));
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        if (position == 0){
            linearCheck.setVisibility(View.VISIBLE);
        }else{
            linearCheck.setVisibility(View.GONE);
        }

        btnCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                passengerAdult = new JSONObject();
                passengerAdult =  ((PassengerActivity) activity).getData();

                if (btnCheck.isChecked()){
                    try {
                        if (passengerAdult.getInt("Salutation") == 2){
                            inputGender.setSelection(1);
                        }else{
                            inputGender.setSelection(passengerAdult.getInt("Salutation"));
                        }

                        inputName.setText(passengerAdult.getString("PassengerName"));

                        if (passengerList.length() > 0){
                            for (int i=0; i<passengerList.length(); i++){
                                if (passengerList.getJSONObject(i).getString("FullName").toLowerCase().equals(inputName.getText().toString().toLowerCase())){
                                    inputIdentitas.setText(passengerList.getJSONObject(i).getString("Identity"));
                                }
                            }
                        }else{
                            inputIdentitas.setText("");
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }else{
                    inputGender.setSelection(0);
                    inputName.setText("");
                    inputIdentitas.setText("");
                }
            }
        });

        return convertView;
    }
}