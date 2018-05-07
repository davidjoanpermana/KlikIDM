package com.indomaret.klikindomaret.adapter;

import android.app.Activity;
import android.content.Context;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.indomaret.klikindomaret.R;
import com.indomaret.klikindomaret.activity.ChooseSeatActivity;
import com.indomaret.klikindomaret.views.HeightAdjustableListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by indomaretitsd7 on 6/14/16.
 */
public class ListItemAdapter extends BaseAdapter {
    private Activity activity;
    private JSONArray itemList;
    private JSONArray passengerList;
    private JSONObject jsonObject;
    private JSONObject seatObject;
    private LayoutInflater inflater;
    private List<String> passengerName = new ArrayList<>();
    private String wagonNo;
    private ListSeatKAIAdapter listSeatKAIAdapter;

    public ListItemAdapter(Activity activity, JSONArray itemList, JSONArray passengerList, JSONObject seatObject, ListSeatKAIAdapter listSeatKAIAdapter){
        this.activity = activity;
        this.itemList = itemList;
        this.passengerList = passengerList;
        this.seatObject = seatObject;
        this.listSeatKAIAdapter = listSeatKAIAdapter;
    }

    @Override
    public int getCount() {
        return itemList.length();
    }

    @Override
    public Object getItem(int position) {
        try {
            return itemList.getJSONObject(position);
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
            convertView = inflater.inflate(R.layout.list_item, null);

        final Button seat = (Button) convertView.findViewById(R.id.seat);

        try {
            jsonObject = new JSONObject();
            passengerName = new ArrayList<>();
            jsonObject = itemList.getJSONObject(position);
            wagonNo = seatObject.getString("WagonNo");

            seat.setText(itemList.getJSONObject(position).getString("RowSeat")
                    + itemList.getJSONObject(position).getString("ColumnSeat"));

            if (jsonObject.getString("Status").equals("1")){
                seat.setVisibility(View.VISIBLE);
                seat.setBackgroundResource(R.drawable.checkbox_grey);
                seat.setEnabled(false);
            }else if (jsonObject.getString("Status").equals("0")){
                seat.setVisibility(View.VISIBLE);
                seat.setBackgroundResource(R.drawable.checkbox_null);
                seat.setEnabled(true);
            } else{
                seat.setVisibility(View.INVISIBLE);
                seat.setBackgroundResource(R.drawable.checkbox_filled);
                seat.setEnabled(false);
            }

            for (int i=0; i<passengerList.length(); i++){
                if (passengerList.getJSONObject(i).getString("SeatRow").equals(jsonObject.getString("RowSeat")) &&
                        passengerList.getJSONObject(i).getString("SeatPotition").equals(jsonObject.getString("ColumnSeat")) &&
                        passengerList.getJSONObject(i).getString("wagonNumber").equals(wagonNo)){
                    seat.setVisibility(View.VISIBLE);
                    seat.setEnabled(true);
                    seat.setBackgroundResource(R.drawable.checkbox_filled);
                }

                passengerName.add(passengerList.getJSONObject(i).getString("Name") +" ("+ passengerList.getJSONObject(i).getString("SeatRow")
                        + passengerList.getJSONObject(i).getString("SeatPotition") +")");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        seat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    if (itemList.getJSONObject(position).getString("Status").equals("0")){
                        LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                        View listReview = null;

                        if (listReview == null) {
                            listReview = inflater.inflate(R.layout.list_passenger_dialog, null);
                        }

                        TextView title = (TextView) listReview.findViewById(R.id.title) ;
                        HeightAdjustableListView listPassenger = (HeightAdjustableListView) listReview.findViewById(R.id.list_passenger);

                        title.setText("Pilih penumpang untuk mengisi kursi " + itemList.getJSONObject(position).getString("RowSeat")
                                + itemList.getJSONObject(position).getString("ColumnSeat"));

                        ArrayAdapter<String> adapter = new ArrayAdapter<>(activity, android.R.layout.simple_spinner_item, passengerName);
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        listPassenger.setAdapter(adapter);

                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(activity);
                        alertDialogBuilder.setView(listReview);
                        final AlertDialog alertDialog = alertDialogBuilder.create();
                        alertDialog.setCanceledOnTouchOutside(false);

                        listPassenger.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position2, long id) {
                                try {
                                    JSONObject jsonObject = passengerList.getJSONObject(position2);
                                    JSONObject retrunPassenger = new JSONObject();
                                    JSONArray seatDetail = seatObject.getJSONArray("SeatDetails");

                                    for (int i=0; i<seatDetail.length(); i++){
                                        if (passengerList.getJSONObject(position2).getString("SeatRow").equals(seatDetail.getJSONObject(i).getString("RowSeat")) &&
                                                passengerList.getJSONObject(position2).getString("SeatPotition").equals(seatDetail.getJSONObject(i).getString("ColumnSeat"))){
//                                            seatDetail.getJSONObject(i).put("Status", "0");
                                            retrunPassenger = seatDetail.getJSONObject(i);
                                        }
                                    }

                                    String firstWagonNo = passengerList.getJSONObject(position2).getString("wagonNumber");
                                    passengerList.getJSONObject(position2).put("SeatRow", itemList.getJSONObject(position).getString("RowSeat"));
                                    passengerList.getJSONObject(position2).put("SeatPotition", itemList.getJSONObject(position).getString("ColumnSeat"));
                                    passengerList.getJSONObject(position2).put("wagonNumber", wagonNo);

                                    listSeatKAIAdapter.refrehGridView(itemList.getJSONObject(position).getString("Column"), passengerList, jsonObject, itemList, wagonNo);

                                    seat.setBackgroundResource(R.drawable.checkbox_filled);
                                    ((ChooseSeatActivity)activity).setSeat(passengerList, retrunPassenger, firstWagonNo, wagonNo);

                                    alertDialog.dismiss();
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        });

                        alertDialog.show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        return convertView;
    }
}