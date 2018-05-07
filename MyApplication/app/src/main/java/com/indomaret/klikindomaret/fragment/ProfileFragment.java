package com.indomaret.klikindomaret.fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.indomaret.klikindomaret.API;
import com.indomaret.klikindomaret.R;
import com.indomaret.klikindomaret.activity.VerificationPhoneActivity;
import com.indomaret.klikindomaret.app.AppController;
import com.indomaret.klikindomaret.helper.SQLiteHandler;
import com.indomaret.klikindomaret.helper.SessionManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.Date;

import pl.droidsonroids.gif.GifImageView;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends Fragment {
    private Intent intent;
    private View view;
    private SessionManager sessionManager;
    private SQLiteHandler sqLiteHandler;

    private Button changePass, cancelPass, changeNomor, btnSaveProfile, btnSaveProfileBank;
    private TextView verivicationNumber, changePasswordButton, emailAddress;
    private EditText phoneNumber, oldPass, newPass, confNewPass, userNameBank, rekBank, bankName, branchBank;
    private EditText editFirstName, editLastName, editBirthDate;
    private RadioButton radioButtonMale, radioButtonFemale;
    private RelativeLayout preloader;

    private JSONArray userProfileObjectArray;
    private JSONObject userProfileObject;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_profile, container, false);
        sessionManager = new SessionManager(getActivity());
        sqLiteHandler = new SQLiteHandler(getActivity());

        changeNomor = (Button) view.findViewById(R.id.btn_change_nomor);
        btnSaveProfile = (Button) view.findViewById(R.id.btn_save_profile);
        btnSaveProfileBank = (Button) view.findViewById(R.id.btn_save_profile_bank);

        editFirstName = (EditText) view.findViewById(R.id.edittext_profile_first_name);
        editLastName = (EditText) view.findViewById(R.id.edittext_profile_last_name);
        editBirthDate = (EditText) view.findViewById(R.id.edittext_profile_birthdate);
        phoneNumber = (EditText) view.findViewById(R.id.edittext_phone_number);
        userNameBank = (EditText) view.findViewById(R.id.username_bank);
        rekBank = (EditText) view.findViewById(R.id.rek_bank);
        bankName = (EditText) view.findViewById(R.id.name_bank);
        branchBank = (EditText) view.findViewById(R.id.branch_bank);

        emailAddress = (TextView) view.findViewById(R.id.profile_email);
        verivicationNumber = (TextView) view.findViewById(R.id.verified_number);
        changePasswordButton = (TextView) view.findViewById(R.id.btn_profile_change_password);

        preloader = (RelativeLayout) view.findViewById(R.id.preloader);
        radioButtonMale = (RadioButton) view.findViewById(R.id.radioMale);
        radioButtonFemale = (RadioButton) view.findViewById(R.id.radioFemale);

        editFirstName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                btnSaveProfile.setEnabled(true);
                btnSaveProfile.setBackgroundResource(R.drawable.button_style_1);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        editLastName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                btnSaveProfile.setEnabled(true);
                btnSaveProfile.setBackgroundResource(R.drawable.button_style_1);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        editBirthDate.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        btnSaveProfile.setEnabled(true);
                        btnSaveProfile.setBackgroundResource(R.drawable.button_style_1);

                        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                        View writeReview = null;

                        if (writeReview == null) {
                            writeReview = inflater.inflate(R.layout.date_picker, null);
                        }

                        final Calendar calendar = Calendar.getInstance();
                        Integer year, month, day;
                        final StringBuilder builder=new StringBuilder();
                        final DatePicker datePicker = (DatePicker) writeReview.findViewById(R.id.date_picker);
                        final Button done = (Button) writeReview.findViewById(R.id.send_review);

                        datePicker.setMaxDate(calendar.getTimeInMillis());

                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
                        alertDialogBuilder.setView(writeReview);
                        final AlertDialog alertDialog = alertDialogBuilder.create();
                        alertDialog.setCanceledOnTouchOutside(false);
                        alertDialog.show();

                        if(editBirthDate.getText().toString().contains("/")){
                            String[] splitString = editBirthDate.getText().toString().split("/");
                            month = Integer.valueOf(splitString[1]);
                            day = Integer.valueOf(splitString[0]);
                            year = Integer.valueOf(splitString[2]);

                            datePicker.updateDate(year, month-1, day);
                        }

                        done.setOnClickListener(
                                new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {

                                        if (String.valueOf(datePicker.getDayOfMonth()).length() == 1){
                                            builder.append("0" + datePicker.getDayOfMonth() + "-");
                                        }else{
                                            builder.append(datePicker.getDayOfMonth()+"-");
                                        }

                                        if (String.valueOf(datePicker.getMonth()).length() == 1){
                                            builder.append("0" + (datePicker.getMonth() + 1) + "-");
                                        }else{
                                            builder.append((datePicker.getMonth() + 1)+"-");
                                        }

                                        builder.append(datePicker.getYear());
                                        editBirthDate.setText(builder.toString());
                                        alertDialog.dismiss();
                                    }
                                }
                        );
                    }
                }
        );

        radioButtonMale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnSaveProfile.setEnabled(true);
                btnSaveProfile.setBackgroundResource(R.drawable.button_style_1);
            }
        });

        radioButtonFemale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnSaveProfile.setEnabled(true);
                btnSaveProfile.setBackgroundResource(R.drawable.button_style_1);
            }
        });

        userNameBank.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                btnSaveProfileBank.setEnabled(true);
                btnSaveProfileBank.setBackgroundResource(R.drawable.button_style_1);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        bankName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                btnSaveProfileBank.setEnabled(true);
                btnSaveProfileBank.setBackgroundResource(R.drawable.button_style_1);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        rekBank.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                btnSaveProfileBank.setEnabled(true);
                btnSaveProfileBank.setBackgroundResource(R.drawable.button_style_1);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        branchBank.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                btnSaveProfileBank.setEnabled(true);
                btnSaveProfileBank.setBackgroundResource(R.drawable.button_style_1);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        btnSaveProfile.setOnClickListener(
                new View.OnClickListener() {
                    @SuppressWarnings("deprecation")
                    @Override
                    public void onClick(View v) {
                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
                        alertDialogBuilder.setNegativeButton("Tutup", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface arg0, int arg1) {

                            }
                        });

                        String[] birthdateSplit = editBirthDate.getText().toString().split("-");
                        int year = Integer.parseInt(birthdateSplit[2]);
                        int month = Integer.parseInt(birthdateSplit[1]);
                        int day = Integer.parseInt(birthdateSplit[0]);
                        Date cDate = new Date(year, month, day);

                        userProfileObject.remove("Address");
                        userProfileObject.remove("Password");

                        try {
                            userProfileObject.put("FName", editFirstName.getText().toString());
                            userProfileObject.put("LName", editLastName.getText().toString());
//                            userProfileObject.put("DateOfBirthStringFormatted", day+"-"+month+"-"+year);
                            userProfileObject.put("Mobile", phoneNumber.getText().toString());

                            if(radioButtonMale.isChecked()){
                                userProfileObject.put("Gender", "Pria");
                            } else if(radioButtonFemale.isChecked()){
                                userProfileObject.put("Gender", "Wanita");
                            }

                            userProfileObject.put("DateOfBirth", year + "-" + month + "-" + day + "T00:00:00");

                            runLoader();

                            makeJsonPost(API.getInstance().getApiUpdateProfile()+"?isMyAccount=true&mfp_id="+sessionManager.getKeyMfpId(), userProfileObject, "prof");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
        );

        btnSaveProfileBank.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {;
                intent = new Intent(getActivity(), VerificationPhoneActivity.class);
                JSONObject object = new JSONObject();
                try {
                    object.put("AccountName", userNameBank.getText().toString());
                    object.put("Bank", bankName.getText().toString());
                    object.put("AccountNumber", rekBank.getText().toString());
                    object.put("BranchBank", branchBank.getText().toString());

                    intent.putExtra("objectBank", object.toString());

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                intent.putExtra("type", "otpBank");
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.left_in, R.anim.left_out);
            }
        });

        changePasswordButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                        View passwordView = null;

                        if (passwordView == null) {
                            passwordView = inflater.inflate(R.layout.fragment_profile_change_password, null);
                        }

                        oldPass = (EditText) passwordView.findViewById(R.id.oldpass);
                        newPass = (EditText) passwordView.findViewById(R.id.newpass);
                        confNewPass = (EditText) passwordView.findViewById(R.id.confnewpass);

                        cancelPass = (Button) passwordView.findViewById(R.id.cancel_pass);
                        changePass = (Button) passwordView.findViewById(R.id.change_pass) ;

                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
                        alertDialogBuilder.setTitle("Ubah Kata Sandi");
                        alertDialogBuilder.setView(passwordView);
                        final AlertDialog alertDialog = alertDialogBuilder.create();
                        alertDialog.show();

                        cancelPass.setOnClickListener(
                                new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        alertDialog.hide();
                                    }
                                }
                        );

                        changePass.setOnClickListener(
                                new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        JSONObject password = new JSONObject();

                                        String stringOldPass = oldPass.getText().toString();
                                        String stringNewPass = newPass.getText().toString();
                                        String stringConfNewPass = confNewPass.getText().toString();

                                        if (TextUtils.isEmpty(stringOldPass)) {
                                            Snackbar snack = Snackbar.make(getActivity().findViewById(android.R.id.content), "Harap isi Kata Sandi lama", Snackbar.LENGTH_LONG);
                                            snack.show();
                                            return;
                                        }

                                        if (TextUtils.isEmpty(stringNewPass)) {
                                            Snackbar snack = Snackbar.make(getActivity().findViewById(android.R.id.content), "Harap isi Kata Sandi baru", Snackbar.LENGTH_LONG);
                                            snack.show();
                                            return;
                                        }

                                        if (TextUtils.isEmpty(stringConfNewPass)) {
                                            Snackbar snack = Snackbar.make(getActivity().findViewById(android.R.id.content), "Harap isi Konfirmasi Kata Sandi baru", Snackbar.LENGTH_LONG);
                                            snack.show();
                                            return;
                                        }

                                        if (!stringNewPass.equals(stringConfNewPass)) {
                                            Snackbar snack = Snackbar.make(getActivity().findViewById(android.R.id.content), "Kata Sandi baru tidak sama dengan Konfirmasi Kata Sandi baru", Snackbar.LENGTH_LONG);
                                            snack.show();
                                            return;
                                        }

                                        try {
                                            password.put("ID", userProfileObject.getString("ID"));
                                            password.put("Password", stringNewPass);
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }

                                        makeJsonPost(API.getInstance().getApiUpdatePasswordMobile()+"?mfp_id="+sessionManager.getKeyMfpId(), password, "pass");
                                        alertDialog.hide();
                                        runLoader();
                                    }
                                }
                        );
                    }
                }
        );

        changeNomor.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        intent = new Intent(getActivity(), VerificationPhoneActivity.class);
                        intent.putExtra("type", "otpUser");
                        startActivity(intent);
                        getActivity().overridePendingTransition(R.anim.left_in, R.anim.left_out);
                    }
                }
        );

        return view;
    }

    public void setProfile(String userProfile){
        try {
            userProfileObjectArray = new JSONArray(userProfile);
            userProfileObject = userProfileObjectArray.getJSONObject(0);

            if(userProfileObject.getString("Gender").equals("Pria")){
                radioButtonMale.setChecked(true);
                radioButtonFemale.setChecked(false);
            } else{
                radioButtonMale.setChecked(false);
                radioButtonFemale.setChecked(true);
            }

            if (userProfileObject.getString("FName") != null && !userProfileObject.getString("FName").equals("null")) editFirstName.setText(userProfileObject.getString("FName"));
            else editFirstName.setText("");
            if (userProfileObject.getString("LName") != null && !userProfileObject.getString("LName").equals("null")) editLastName.setText(userProfileObject.getString("LName"));
            else editLastName.setText("");

            emailAddress.setText(userProfileObject.getString("Email"));
            editBirthDate.setText(userProfileObject.getString("DateOfBirthStringFormatted"));
            phoneNumber.setText(userProfileObject.getString("Mobile"));

            if(userProfileObject.getString("MobileVerified").equals("true")){
                verivicationNumber.setText("(Sudah Terverifikasi)");
                verivicationNumber.setTextColor(Color.parseColor("#009933"));
            } else if(userProfileObject.getString("MobileVerified").equals("false")){
                verivicationNumber.setText("(Belum Terverifikasi)");
                verivicationNumber.setTextColor(Color.parseColor("#990000"));
            }

            if (userProfileObject.getString("AccountName") == null || userProfileObject.getString("AccountName").equals("null") ||
                    userProfileObject.getString("AccountName").equals("")){
                userNameBank.setText("");
            } else {
                userNameBank.setText(userProfileObject.getString("AccountName"));
            }

            if (userProfileObject.getString("Bank") == null || userProfileObject.getString("Bank").equals("null") ||
                    userProfileObject.getString("Bank").equals("")){
                bankName.setText("");
            } else{
                bankName.setText(userProfileObject.getString("Bank"));
            }

            if (userProfileObject.getString("AccountNumber") == null || userProfileObject.getString("AccountNumber").equals("null") ||
                    userProfileObject.getString("AccountNumber").equals("")) {
                rekBank.setText("");
            } else {
                rekBank.setText(userProfileObject.getString("AccountNumber"));
            }

            if (userProfileObject.getString("BranchBank") == null || userProfileObject.getString("BranchBank").equals("null") ||
                    userProfileObject.getString("BranchBank").equals("")) {
                branchBank.setText("");
            } else {
                branchBank.setText(userProfileObject.getString("BranchBank"));
            }

            btnSaveProfile.setEnabled(false);
            btnSaveProfile.setBackgroundResource(R.drawable.button_style_4);
            btnSaveProfileBank.setEnabled(false);
            btnSaveProfileBank.setBackgroundResource(R.drawable.button_style_4);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void makeJsonPost(String urlJsonObj, JSONObject jsonObject, final String type){
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                urlJsonObj, jsonObject,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        if(type.equals("pass")){
                            updatepasswordResponse(response);
                        } else if(type.equals("prof")){
                            updateProfileResponse(response);
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getActivity(), "Gagal terkoneksi server", Toast.LENGTH_LONG).show();
                stopLoader();
            }
        });

        AppController.getInstance().addToRequestQueue(jsonObjReq);
    }

    public void updatepasswordResponse(JSONObject response){
        stopLoader();

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        alertDialogBuilder.setNegativeButton("Tutup", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {

            }
        });

        try {
            if(response.getString("IsSuccess").equals("true")){
                alertDialogBuilder.setMessage("Kata Sandi Berhasil di ubah");
                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
            } else {
                alertDialogBuilder.setMessage("Gagal mengubah Kata Sandi");
                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void updateProfileResponse(JSONObject response){
        stopLoader();
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());

        try {
            if(response.getString("IsSuccess").equals("true")){
                sessionManager.setUsername(response.getJSONObject("ResponseObject").getString("FName"));
                sessionManager.setLastUsername(response.getJSONObject("ResponseObject").getString("LName"));
                makeJsonObjectGet(API.getInstance().getApiGetProfile()+"?access_token="+sessionManager.getResponseId()+"&mfp_id="+sessionManager.getResponseId());

                alertDialogBuilder.setPositiveButton("Tutup", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        btnSaveProfile.setEnabled(false);
                        btnSaveProfile.setBackgroundResource(R.drawable.button_style_4);
                    }
                });

                alertDialogBuilder.setMessage("Berhasil mengubah profil pengguna");
                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
            } else if(response.getString("IsSuccess").equals("false")){
                alertDialogBuilder.setNegativeButton("Tutup", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        btnSaveProfile.setEnabled(true);
                        btnSaveProfile.setBackgroundResource(R.drawable.button_style_1);
                        setProfile(sqLiteHandler.getProfile());
                    }
                });

                alertDialogBuilder.setMessage("Gagal mengubah profil pengguna");
                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void makeJsonObjectGet(String url){
        System.out.println("address url = " + url);

        JsonArrayRequest jsonArrayReq = new JsonArrayRequest(url,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        sqLiteHandler.insertProfile(response.toString());

                        try {
                            JSONArray address = response.getJSONObject(0).getJSONArray("Address");
                            for (int i=0; i<address.length(); i++){
                                if(address.getJSONObject(i).getString("IsDefault").equals("true")){
                                    sqLiteHandler.insertDefaultAddress(address.getJSONObject(i).toString());
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        stopLoader();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getActivity(), "Gagal terkoneksi server", Toast.LENGTH_LONG).show();
                stopLoader();
            }
        }, getActivity());

        jsonArrayReq.setRetryPolicy(new DefaultRetryPolicy(60000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppController.getInstance().addToRequestQueue(jsonArrayReq);
    }

    public void runLoader(){
        preloader.setVisibility(View.VISIBLE);
    }

    public void stopLoader(){
        preloader.setVisibility(View.GONE);
    }

    @Override
    public void onResume(){
        super.onResume();

        final String userProfile = sqLiteHandler.getProfile();
        setProfile(userProfile);
    }
}
