package com.indomaret.klikindomaret.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.multidex.MultiDex;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.indomaret.klikindomaret.API;
import com.indomaret.klikindomaret.R;
import com.indomaret.klikindomaret.helper.Encode2;
import com.indomaret.klikindomaret.helper.SessionManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class NewProfileKAIActivity extends AppCompatActivity {
    private Intent intent;
    private SessionManager sessionManager;
    private EditText inputName, inputIdentitas, inputTlp;
    private Button btnSave;
    private Spinner inputGender;
    private LinearLayout linearIdHp;
    private JSONObject passengerObaject = new JSONObject();
    private ArrayList<String> genderList = new ArrayList<>();
    private String type = "";
    private String maturity = "";
    private Encode2 encode = new Encode2();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        MultiDex.install(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_profile_kai);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        intent = getIntent();
        sessionManager = new SessionManager(NewProfileKAIActivity.this);

        inputGender = (Spinner) findViewById(R.id.input_gender);
        inputName = (EditText) findViewById(R.id.input_name);
        inputIdentitas = (EditText) findViewById(R.id.input_identitas);
        inputTlp = (EditText) findViewById(R.id.input_tlp);
        btnSave = (Button) findViewById(R.id.btn_save);
        linearIdHp = (LinearLayout) findViewById(R.id.linear_id_hp);

        inputGender.setSelection(0);
        inputName.setImeOptions(EditorInfo. IME_ACTION_NEXT);

        type = intent.getStringExtra("type");
        if (type.contains("edit")){
            try {
                passengerObaject = new JSONObject(intent.getStringExtra("profileObject"));

                if (passengerObaject.getInt("Maturity") == 0){
                    genderList.add("Tuan");
                    genderList.add("Nyonya");

                    linearIdHp.setVisibility(View.VISIBLE);
                }else{
                    genderList.add("Tuan");
                    genderList.add("Nona");

                    linearIdHp.setVisibility(View.GONE);
                }

                ArrayAdapter<String> dataAdapterGender = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, genderList);
                dataAdapterGender.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                inputGender.setAdapter(dataAdapterGender);

                inputGender.setSelection(passengerObaject.getInt("Salutation"));
                inputName.setText(passengerObaject.getString("FullName"));
                inputIdentitas.setText(passengerObaject.getString("Identity"));
                inputTlp.setText(passengerObaject.getString("Phone"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }else if(type.equals("newAdult")){
            genderList.add("Tuan");
            genderList.add("Nyonya");
            maturity = "0";
            linearIdHp.setVisibility(View.VISIBLE);

            ArrayAdapter<String> dataAdapterGender = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, genderList);
            dataAdapterGender.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            inputGender.setAdapter(dataAdapterGender);
        }else if(type.equals("newInfant")){
            genderList.add("Tuan");
            genderList.add("Nona");
            maturity = "1";
            linearIdHp.setVisibility(View.GONE);

            ArrayAdapter<String> dataAdapterGender = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, genderList);
            dataAdapterGender.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            inputGender.setAdapter(dataAdapterGender);
        }

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JSONObject jsonObject = new JSONObject();
                try {
                    if (inputName.getText().toString().equals("")){
                        Toast.makeText(NewProfileKAIActivity.this, "Nama harus diisi.", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (inputIdentitas.getText().toString().equals("") && !type.equals("newInfant") && !type.equals("editInfant")){
                        Toast.makeText(NewProfileKAIActivity.this, "Nomor identitas/tanggal lahir harus diisi.", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (inputIdentitas.getText().toString().length() < 8 && !type.equals("newInfant") && !type.equals("editInfant")){
                        Toast.makeText(NewProfileKAIActivity.this, "Nomor identitas/tanggal lahir minimal 8 karakter.", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (inputTlp.getText().toString().equals("") && !type.equals("newInfant") && !type.equals("editInfant")){
                        Toast.makeText(NewProfileKAIActivity.this, "Nomor telp pemesan harus diisi.", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (inputTlp.getText().toString().length() > 16 && !type.equals("newInfant") && !type.equals("editInfant")){
                        Toast.makeText(NewProfileKAIActivity.this, "Nomor telp pemesan maksimal 16 karakter.", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (type.contains("edit")){
                        passengerObaject = new JSONObject(intent.getStringExtra("profileObject"));

                        jsonObject.put("ID", passengerObaject.getString("ID"));
                        jsonObject.put("CustomerID", passengerObaject.getString("CustomerID"));
                        jsonObject.put("FullName", inputName.getText().toString());
                        jsonObject.put("Phone", inputTlp.getText().toString());
                        jsonObject.put("Identity", inputIdentitas.getText().toString());
                        jsonObject.put("Salutation", inputGender.getSelectedItemId());
                        jsonObject.put("Maturity", passengerObaject.getString("Maturity"));

                        jsonPost(API.getInstance().getApiEditPassenger(), jsonObject, "");
                    }else {
                        jsonObject.put("ID", "");
                        jsonObject.put("CustomerID", sessionManager.getUserID());
                        jsonObject.put("FullName", inputName.getText().toString());

                        if (!type.equals("newInfant") && !type.equals("editInfant")){
                            jsonObject.put("Phone", inputTlp.getText().toString());
                            jsonObject.put("Identity", inputIdentitas.getText().toString());
                        }

                        jsonObject.put("Salutation", inputGender.getSelectedItemId());
                        jsonObject.put("Maturity", maturity);

                        jsonPost(API.getInstance().getApiAddPassenger(), jsonObject, "");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void jsonPost(String urlJsonObj, JSONObject jsonObject, final String type) {
        RequestQueue queue = Volley.newRequestQueue(this);
        System.out.println("--- passenger url : "+urlJsonObj);
        System.out.println("--- passenger obj : "+jsonObject);
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                urlJsonObj, jsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if (response == null || response.length() == 0){
                                Toast.makeText(NewProfileKAIActivity.this, "Gagal terkoneksi server", Toast.LENGTH_LONG).show();
                            }else{
                                if (response.getString("Message").equals("Success")){
                                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(NewProfileKAIActivity.this);
                                    alertDialogBuilder.setMessage("Data penumpang berhasil disimpan");
                                    alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface arg0, int arg1) {
                                            finish();
                                        }
                                    });

                                    AlertDialog alertDialog = alertDialogBuilder.create();
                                    alertDialog.setCanceledOnTouchOutside(false);
                                    alertDialog.show();
                                }else{
                                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(NewProfileKAIActivity.this);
                                    alertDialogBuilder.setMessage(response.getString("Message"));

                                    alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface arg0, int arg1) {
                                        }
                                    });

                                    AlertDialog alertDialog = alertDialogBuilder.create();
                                    alertDialog.setCanceledOnTouchOutside(false);
                                    alertDialog.show();
                                }
                            }
                        } catch (JSONException e) {
                            Toast.makeText(NewProfileKAIActivity.this, "Gagal terkoneksi server", Toast.LENGTH_LONG).show();
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(NewProfileKAIActivity.this, "Gagal terkoneksi server", Toast.LENGTH_LONG).show();
                    }
                }
        ) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String>  params = new HashMap<String, String>();
                String token = "";
                try {
                    token = encode.SHA1(encode.md5("66E2C13840534C139D85CEE1B433C1FX"));
                    System.out.println("--- token : "+token);
                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }

                params.put("ApplicationKey", "indomaret");
                params.put("Authorization", "bearer "+token+"#"+sessionManager.getUserID());

                return params;
            }
        };
        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(50000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(jsonObjReq);
    }

    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.right_in, R.anim.right_out);
    }
}
