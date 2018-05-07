package com.indomaret.klikindomaret.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.indomaret.klikindomaret.R;
import com.indomaret.klikindomaret.activity.ListTicketKAIActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;

/**
 * Created by indomaretitsd7 on 6/14/16.
 */
public class ListTicketKAIAdapter extends BaseAdapter{
    private LayoutInflater inflater;
    private Activity activity;
    private JSONArray ticketList;
    private DecimalFormat df = new DecimalFormat("#,###");
    private JSONObject objectDataTicket = new JSONObject();
    private int countPassenger = 0;
    private JSONArray ticketZeroList = new JSONArray();
    private JSONArray ticketNonZeroList = new JSONArray();

    public ListTicketKAIAdapter(Activity activity, JSONArray ticketList, int countPassenger){
        this.activity = activity;
        this.ticketList = ticketList;
        this.countPassenger = countPassenger;
    }

    @Override
    public int getCount() {
        return ticketList.length();
    }

    @Override
    public Object getItem(int position) {
        try {
            return ticketList.getJSONObject(position);
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
            convertView = inflater.inflate(R.layout.list_ticket_kai, null);

        try {
            ticketZeroList = new JSONArray();
            ticketNonZeroList = new JSONArray();

            for (int i = 0; i < ticketList.length(); i++) {
                if (ticketList.getJSONObject(i).getInt("TotalRemainingSeats") == 0){
                    ticketZeroList.put(ticketList.getJSONObject(i));
                }else{
                    ticketNonZeroList.put(ticketList.getJSONObject(i));
                }
            }

            if (ticketZeroList.length() > 0){
                for (int i = 0; i < ticketZeroList.length(); i++) {
                    ticketNonZeroList.put(ticketZeroList.getJSONObject(i));
                }
            }

            objectDataTicket = ticketNonZeroList.getJSONObject(position);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        TextView trainName = (TextView) convertView.findViewById(R.id.train_name);
        TextView train_info = (TextView) convertView.findViewById(R.id.info_train);
        TextView originTime = (TextView) convertView.findViewById(R.id.origin_time);
        TextView destinationTime = (TextView) convertView.findViewById(R.id.destination_time);
        TextView classTrain = (TextView) convertView.findViewById(R.id.class_train);
        TextView countSit = (TextView) convertView.findViewById(R.id.count_sit);
        TextView priceTrain = (TextView) convertView.findViewById(R.id.price_train);
        RelativeLayout relativeLayout = (RelativeLayout) convertView.findViewById(R.id.relative_list_ticket);

        try {
            String price = df.format(Double.parseDouble(objectDataTicket.getString("AdultPrice")));
            if (price.equals("0")){
                System.out.println(objectDataTicket);
            }
            trainName.setText(objectDataTicket.getString("TrainName") + " " + objectDataTicket.getString("TrainNumber"));
            train_info.setText(objectDataTicket.getString("Original") + "   "
                    + objectDataTicket.getString("DurationInHour").split("\\.")[0] + "J "
                    + objectDataTicket.getString("DurationInMinutes").split("\\.")[0] + "M   "
                    + objectDataTicket.getString("Destination"));
            originTime.setText(objectDataTicket.getString("DepartureDate").split("T")[1].substring(0,5));
            destinationTime.setText(objectDataTicket.getString("ArrivalDate").split("T")[1].substring(0,5));
            classTrain.setText(objectDataTicket.getString("TrainClassName") + " ("
                    + objectDataTicket.getString("TrainSubClassCode") + ")");
            countSit.setText("Sisa " + objectDataTicket.getString("TotalRemainingSeats") + " Kursi");
            priceTrain.setText("Rp " + price.replace(",", "."));

            if (objectDataTicket.getInt("TotalRemainingSeats") <= 20 && objectDataTicket.getInt("TotalRemainingSeats") > 0){
                countSit.setVisibility(View.VISIBLE);
            }else{
                countSit.setVisibility(View.GONE);
            }

            relativeLayout.setAlpha((float) 0.5);
            if (objectDataTicket.getInt("TotalRemainingSeats") == 0){
                relativeLayout.setBackgroundColor(Color.GRAY);
                relativeLayout.setEnabled(false);
            }else if(objectDataTicket.getInt("TotalRemainingSeats") < countPassenger){
                relativeLayout.setBackgroundColor(Color.GRAY);
                relativeLayout.setEnabled(false);
            } else{
                relativeLayout.setBackgroundColor(Color.WHITE);
                relativeLayout.setEnabled(true);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    ((ListTicketKAIActivity)activity).clickTicket(ticketNonZeroList.getJSONObject(position));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        return convertView;
    }
}