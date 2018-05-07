package com.indomaret.klikindomaret.fragment;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.widget.DatePicker;
import android.widget.EditText;

import com.indomaret.klikindomaret.R;

import java.util.Calendar;

/**
 * A simple {@link Fragment} subclass.
 */
public class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState){
        //Use the current date as the default date in the date picker
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH) + 1;
        int day = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dpDialog = new DatePickerDialog(getActivity(), this, year, month, day);
        DatePicker datePicker = dpDialog.getDatePicker();

        Calendar calendar = Calendar.getInstance();//get the current day
        datePicker.setMaxDate(calendar.getTimeInMillis());//set the current day as the max date
        datePicker.setCalendarViewShown(false);
        datePicker.setSpinnersShown(true);
        return dpDialog;

//        return new DatePickerDialog(getActivity(), this, year, month, day);
    }

    public void onDateSet(DatePicker view, int year, int month, int day) {
        String days= null;
        String months = null;
        EditText tv = (EditText) getActivity().findViewById(R.id.edittext_profile_birthdate);

        if(day < 10){
            days = "0" + day;
        } else{
            days = "" + day;
        }

        if((month+1) < 10){
            months = "0" + (month+1);
        } else {
            months = "" + (month+1);
        }

        String stringOfDate = days + "-" + months + "-" + year;
        tv.setText(stringOfDate);
    }
}