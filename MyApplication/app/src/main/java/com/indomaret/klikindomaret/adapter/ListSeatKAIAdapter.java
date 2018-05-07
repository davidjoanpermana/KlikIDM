package com.indomaret.klikindomaret.adapter;

import android.app.Activity;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.LinearLayout;

import com.indomaret.klikindomaret.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by indomaretitsd7 on 6/14/16.
 */
public class ListSeatKAIAdapter extends PagerAdapter {
    private Activity activity;
    private int row;
    private int countGerbong = 0;
    private JSONArray seatList;
    private JSONArray passengerList;
    private JSONObject seatObject, tempObject;
    private ListItemAdapter  listItemAdapter;

    private JSONArray itemList = new JSONArray();
    private JSONArray itemList2 = new JSONArray();
    private JSONArray itemList3 = new JSONArray();
    private JSONArray itemList4 = new JSONArray();
    private JSONArray itemList5 = new JSONArray();
    private JSONArray itemList6 = new JSONArray();

    private JSONArray itemListTemp = new JSONArray();
    private JSONArray itemList2Temp = new JSONArray();
    private JSONArray itemList3Temp = new JSONArray();
    private JSONArray itemList4Temp = new JSONArray();
    private JSONArray itemList5Temp = new JSONArray();
    private JSONArray itemList6Temp = new JSONArray();

    private GridView listView1, listView2, listView3, listView4, listView5, listView6;

    public ListSeatKAIAdapter(Activity activity, JSONArray seatList, JSONArray passengerList){
        this.activity = activity;
        this.seatList = seatList;
        this.passengerList = passengerList;
    }


    @Override
    public int getCount() {
        return seatList.length();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((LinearLayout) object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position) {
        View itemView = LayoutInflater.from(activity).inflate(R.layout.list_seat_kai, container, false);

        listView1 = (GridView) itemView.findViewById(R.id.listview1);
        listView2 = (GridView) itemView.findViewById(R.id.listview2);
        listView3 = (GridView) itemView.findViewById(R.id.listview3);
        listView4 = (GridView) itemView.findViewById(R.id.listview4);
        listView5 = (GridView) itemView.findViewById(R.id.listview5);
        listView6 = (GridView) itemView.findViewById(R.id.listview6);

        itemList = new JSONArray();
        itemList2 = new JSONArray();
        itemList3 = new JSONArray();
        itemList4 = new JSONArray();
        itemList5 = new JSONArray();
        itemList6 = new JSONArray();

        itemListTemp = new JSONArray();
        itemList2Temp = new JSONArray();
        itemList3Temp = new JSONArray();
        itemList4Temp = new JSONArray();
        itemList5Temp = new JSONArray();
        itemList6Temp = new JSONArray();

        listView1.setVerticalScrollBarEnabled(false);
        listView2.setVerticalScrollBarEnabled(false);
        listView3.setVerticalScrollBarEnabled(false);
        listView4.setVerticalScrollBarEnabled(false);
        listView5.setVerticalScrollBarEnabled(false);
        listView6.setVerticalScrollBarEnabled(false);

        tempObject = new JSONObject();
        try {
            seatObject = seatList.getJSONObject(position);
            for (int i=0; i<seatObject.getJSONArray("SeatDetails").length(); i++){
                if (seatObject.getJSONArray("SeatDetails").getJSONObject(i).getInt("Column") > countGerbong){
                    countGerbong = seatObject.getJSONArray("SeatDetails").getJSONObject(i).getInt("Column");
                }

                if (seatObject.getJSONArray("SeatDetails").getJSONObject(i).getString("Column").equals("1")){
                    itemListTemp.put(seatObject.getJSONArray("SeatDetails").getJSONObject(i));
                }else if (seatObject.getJSONArray("SeatDetails").getJSONObject(i).getString("Column").equals("2")){
                    itemList2Temp.put(seatObject.getJSONArray("SeatDetails").getJSONObject(i));
                }else if (seatObject.getJSONArray("SeatDetails").getJSONObject(i).getString("Column").equals("3")){
                    itemList3Temp.put(seatObject.getJSONArray("SeatDetails").getJSONObject(i));
                }else if (seatObject.getJSONArray("SeatDetails").getJSONObject(i).getString("Column").equals("4")){
                    itemList4Temp.put(seatObject.getJSONArray("SeatDetails").getJSONObject(i));
                }else if (seatObject.getJSONArray("SeatDetails").getJSONObject(i).getString("Column").equals("5")){
                    itemList5Temp.put(seatObject.getJSONArray("SeatDetails").getJSONObject(i));
                }else if (seatObject.getJSONArray("SeatDetails").getJSONObject(i).getString("Column").equals("6")){
                    itemList6Temp.put(seatObject.getJSONArray("SeatDetails").getJSONObject(i));
                }
            }

            row = 1;
            for (int i=0; i< itemListTemp.length(); i++){
                if(itemListTemp.getJSONObject(i).getInt("RowSeat") != row){
                    for (int j=row; j<itemListTemp.getJSONObject(i).getInt("RowSeat"); j++){
                        tempObject = new JSONObject();
                        tempObject.put("Row", j);
                        tempObject.put("Column", itemListTemp.getJSONObject(i).getString("Column"));
                        tempObject.put("RowSeat", j);
                        tempObject.put("ColumnSeat", itemListTemp.getJSONObject(i).getString("ColumnSeat"));
                        tempObject.put("SubClass", itemListTemp.getJSONObject(i).getString("SubClass"));
                        tempObject.put("Status", "2");

                        itemList.put(tempObject);
                    }

                    itemList.put(itemListTemp.getJSONObject(i));
                    row = itemListTemp.getJSONObject(i).getInt("RowSeat") +1;
                }else {
                    row += 1;
                    itemList.put(itemListTemp.getJSONObject(i));
                }
            }

            row = 1;
            for (int i=0; i< itemList2Temp.length(); i++){
                if(itemList2Temp.getJSONObject(i).getInt("RowSeat") != row){
                    for (int j=row; j<itemList2Temp.getJSONObject(i).getInt("RowSeat"); j++){
                        tempObject = new JSONObject();
                        tempObject.put("Row", j);
                        tempObject.put("Column", itemList2Temp.getJSONObject(i).getString("Column"));
                        tempObject.put("RowSeat", j);
                        tempObject.put("ColumnSeat", itemList2Temp.getJSONObject(i).getString("ColumnSeat"));
                        tempObject.put("SubClass", itemList2Temp.getJSONObject(i).getString("SubClass"));
                        tempObject.put("Status", "2");

                        itemList2.put(tempObject);
                    }

                    itemList2.put(itemList2Temp.getJSONObject(i));
                    row = itemList2Temp.getJSONObject(i).getInt("RowSeat") +1;
                }else {
                    row += 1;
                    itemList2.put(itemList2Temp.getJSONObject(i));
                }
            }

            row = 1;
            for (int i=0; i< itemList3Temp.length(); i++){
                if(itemList3Temp.getJSONObject(i).getInt("RowSeat") != row){
                    for (int j=row; j<itemList3Temp.getJSONObject(i).getInt("RowSeat"); j++){
                        tempObject = new JSONObject();
                        tempObject.put("Row", j);
                        tempObject.put("Column", itemList3Temp.getJSONObject(i).getString("Column"));
                        tempObject.put("RowSeat", j);
                        tempObject.put("ColumnSeat", itemList3Temp.getJSONObject(i).getString("ColumnSeat"));
                        tempObject.put("SubClass", itemList3Temp.getJSONObject(i).getString("SubClass"));
                        tempObject.put("Status", "2");

                        itemList3.put(tempObject);
                    }

                    itemList3.put(itemList3Temp.getJSONObject(i));
                    row = itemList3Temp.getJSONObject(i).getInt("RowSeat") +1;
                }else {
                    row += 1;
                    itemList3.put(itemList3Temp.getJSONObject(i));
                }
            }

            row = 1;
            for (int i=0; i< itemList4Temp.length(); i++){
                tempObject = new JSONObject();
                if(itemList4Temp.getJSONObject(i).getInt("RowSeat") != row){
                    for (int j=row; j<itemList4Temp.getJSONObject(i).getInt("RowSeat"); j++){
                        tempObject = new JSONObject();
                        tempObject.put("Row", j);
                        tempObject.put("Column", itemList4Temp.getJSONObject(i).getString("Column"));
                        tempObject.put("RowSeat", j);
                        tempObject.put("ColumnSeat", itemList4Temp.getJSONObject(i).getString("ColumnSeat"));
                        tempObject.put("SubClass", itemList4Temp.getJSONObject(i).getString("SubClass"));
                        tempObject.put("Status", "2");

                        itemList4.put(tempObject);
                    }

                    itemList4.put(itemList4Temp.getJSONObject(i));
                    row = itemList4Temp.getJSONObject(i).getInt("RowSeat") +1;
                }else {
                    row +=  1;
                    itemList4.put(itemList4Temp.getJSONObject(i));
                }
            }

            row = 1;
            for (int i=0; i< itemList5Temp.length(); i++){
                tempObject = new JSONObject();
                if(itemList5Temp.getJSONObject(i).getInt("RowSeat") != row){
                    for (int j=row; j<itemList5Temp.getJSONObject(i).getInt("RowSeat"); j++){
                        tempObject = new JSONObject();
                        tempObject.put("Row", j);
                        tempObject.put("Column", itemList5Temp.getJSONObject(i).getString("Column"));
                        tempObject.put("RowSeat", j);
                        tempObject.put("ColumnSeat", itemList5Temp.getJSONObject(i).getString("ColumnSeat"));
                        tempObject.put("SubClass", itemList5Temp.getJSONObject(i).getString("SubClass"));
                        tempObject.put("Status", "2");

                        itemList5.put(tempObject);
                    }

                    itemList5.put(itemList5Temp.getJSONObject(i));
                    row = itemList5Temp.getJSONObject(i).getInt("RowSeat") +1;
                }else {
                    row += 1;
                    itemList5.put(itemList5Temp.getJSONObject(i));
                }
            }

            row = 1;
            for (int i=0; i< itemList6Temp.length(); i++){
                tempObject = new JSONObject();
                if(itemList6Temp.getJSONObject(i).getInt("RowSeat") != row){
                    for (int j=row; j<itemList6Temp.getJSONObject(i).getInt("RowSeat"); j++){
                        tempObject = new JSONObject();
                        tempObject.put("Row", j);
                        tempObject.put("Column", itemList6Temp.getJSONObject(i).getString("Column"));
                        tempObject.put("RowSeat", j);
                        tempObject.put("ColumnSeat", itemList6Temp.getJSONObject(i).getString("ColumnSeat"));
                        tempObject.put("SubClass", itemList6Temp.getJSONObject(i).getString("SubClass"));
                        tempObject.put("Status", "2");

                        itemList6.put(tempObject);
                    }

                    itemList6.put(itemList6Temp.getJSONObject(i));
                    row = itemList6Temp.getJSONObject(i).getInt("RowSeat") +1;
                }else {
                    row += 1;
                    itemList6.put(itemList6Temp.getJSONObject(i));
                }
            }

            if (itemList.length() > 0){
                listItemAdapter = new ListItemAdapter(activity, itemList, passengerList, seatObject, this);
                listView1.setAdapter(listItemAdapter);
            }else{
                listView1.setVisibility(View.INVISIBLE);
            }
            if (itemList2.length() > 0){
                listItemAdapter = new ListItemAdapter(activity, itemList2, passengerList, seatObject, this);
                listView2.setAdapter(listItemAdapter);
            }else{
                listView2.setVisibility(View.INVISIBLE);
            }
            if (itemList3.length() > 0){
                listItemAdapter = new ListItemAdapter(activity, itemList3, passengerList, seatObject, this);
                listView3.setAdapter(listItemAdapter);
            }else{
                listView3.setVisibility(View.INVISIBLE);
            }
            if (itemList4.length() > 0){
                listItemAdapter = new ListItemAdapter(activity, itemList4, passengerList, seatObject, this);
                listView4.setAdapter(listItemAdapter);
            }else{
                listView4.setVisibility(View.INVISIBLE);
            }
            if (itemList5.length() > 0){
                listItemAdapter = new ListItemAdapter(activity, itemList5, passengerList, seatObject, this);
                listView5.setAdapter(listItemAdapter);
            }else{
                listView5.setVisibility(View.INVISIBLE);
            }
            if (itemList6.length() > 0){
                listItemAdapter = new ListItemAdapter(activity, itemList6, passengerList, seatObject, this);
                listView6.setAdapter(listItemAdapter);
            }else{
                listView6.setVisibility(View.INVISIBLE);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        container.addView(itemView);
        return itemView;
    }

    public void refrehGridView(String column, JSONArray passengerArray, JSONObject passengerObject, JSONArray seatList, String wagonNo){
        for (int i=0; i<seatList.length(); i++){
            try {
                if (passengerObject.getString("SeatRow").equals(seatList.getJSONObject(i).getString("RowSeat")) &&
                        passengerObject.getString("SeatPotition").equals(seatList.getJSONObject(i).getString("ColumnSeat")) &&
                        passengerObject.getString("wagonNumber").equals(wagonNo)){
                    seatList.getJSONObject(i).put("RowSeat", passengerObject.getString("SeatRow"));
                    seatList.getJSONObject(i).put("ColumnSeat", passengerObject.getString("SeatPotition"));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}