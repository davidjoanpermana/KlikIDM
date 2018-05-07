package com.indomaret.klikindomaret.fragment;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.indomaret.klikindomaret.R;

/**
 * Created by USER on 1/25/2018.
 */

public class DFragment  extends DialogFragment {
    private View view;

    public DFragment (View view){
        this.view = view;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.dialog_fragment, container, false);
        getDialog().setTitle("DialogFragment Tutorial");

        LinearLayout linearLayout = (LinearLayout) rootView.findViewById(R.id.linear);
        System.out.println("--- view : "+view);
//        linearLayout.addView(view);
        return rootView;
    }
}
