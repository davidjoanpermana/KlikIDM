package com.indomaret.klikindomaret.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;
import com.indomaret.klikindomaret.R;
import com.indomaret.klikindomaret.activity.FilterActivity;

import java.util.ArrayList;

/**
 * Created by indomaretitsd7 on 6/18/16.
 */
public class MarkerMapAdapter implements GoogleMap.InfoWindowAdapter {
    private LayoutInflater inflater;
    private Activity activity;
    private String title;
    private String description;

    public MarkerMapAdapter(Activity activity, String title, String description){
        this.activity = activity;
        this.title = title;
        this.description = description;
    }

    @Override
    public View getInfoWindow(Marker marker) {
//        if (inflater == null)
//            inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//
//        if (convertView == null)
//            convertView = inflater.inflate(R.layout.marker_map, null);
//
//        marker.setTitle();
//        final TextView titleName = (TextView) convertView.findViewById(R.id.title);
//        final TextView desName = (TextView) convertView.findViewById(R.id.description);
//        final Button btnPilih = (Button) convertView.findViewById(R.id.btn_pilih);
//
//        titleName.setText(title);
//        desName.setText(description);
//
//        btnPilih.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//            }
//        });
        return null;
    }

    @Override
    public View getInfoContents(Marker marker) {
        return null;
    }
}
