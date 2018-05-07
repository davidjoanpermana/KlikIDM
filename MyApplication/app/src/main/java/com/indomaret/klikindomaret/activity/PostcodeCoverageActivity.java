package com.indomaret.klikindomaret.activity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.indomaret.klikindomaret.API;
import com.indomaret.klikindomaret.R;
import com.indomaret.klikindomaret.adapter.AreaAutoCompleteAdapter;
import com.indomaret.klikindomaret.adapter.StoreCoverageAdapter;
import com.indomaret.klikindomaret.app.AppController;
import com.indomaret.klikindomaret.helper.SessionManager;
import com.indomaret.klikindomaret.helper.UncaughtExceptionHandler;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

import pl.droidsonroids.gif.GifImageView;

public class PostcodeCoverageActivity extends AppCompatActivity {
    private static final int SPEECH_REQUEST_CODE = 0;
    private ListView postcodeListView, storeListView;
    private Toolbar toolbar;
    private SessionManager sessionManager;

    private ImageView clearText;
    private RelativeLayout preloader;

    private List<String> data2;
    private String key = "";
    private String url, postcode;

    private boolean voice = false;

    private EditText searchPostcode;
    private TextView notCoverage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_postcode_coverage);

        UncaughtExceptionHandler uncaughtExceptionHandler = new UncaughtExceptionHandler();
        uncaughtExceptionHandler.uncaughtException(this);
        sessionManager = new SessionManager(this);

        toolbar = (Toolbar) findViewById(R.id.app_toolbar);
        toolbar.setTitle("");
        TextView mTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
        mTitle.setText("Cangkupan Kode Pos");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        searchPostcode = (EditText) findViewById(R.id.search_postcode);
        notCoverage = (TextView) findViewById(R.id.not_coverage);
        postcodeListView = (ListView) findViewById(R.id.postcode_listview);
        storeListView = (ListView) findViewById(R.id.store_listview);
        clearText = (ImageView) findViewById(R.id.clear_text);
        preloader = (RelativeLayout) findViewById(R.id.preloader);

        searchPostcode.addTextChangedListener(
                new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {}

                    @Override
                    public void afterTextChanged(Editable s) {
                        if(s.length() > 0 && s.length() < 15){
                            clearText.setVisibility(View.VISIBLE);
                            key = s.toString();
                            url = API.getInstance().getApiAutoCompleteZipcode() + "?key=" + key.replace(" ", "%20")+"&mfp_id=" + sessionManager.getKeyMfpId();
                            makeJsonArrayGet(url, "auto");
                        } else if(s.length() == 0) {
                            clearText.setVisibility(View.GONE);
                            postcodeListView.setVisibility(View.GONE);
                            notCoverage.setVisibility(View.GONE);
                            storeListView.setVisibility(View.GONE);
                            storeListView.setVisibility(View.GONE);
                        }
                    }
                }
        );

        postcodeListView.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        preloader.setVisibility(View.VISIBLE);

                        if (data2.size() > 0){
                            searchPostcode.setText(data2.get(position));

                            postcode = data2.get(position).substring(data2.get(position).length() - 5);
                            url = API.getInstance().getApiStoreCoverage() + "?zipcode=" + postcode + "&CustId=00000000-0000-0000-0000-000000000000&mfp_id=" + sessionManager.getKeyMfpId() + "&regionID=" + sessionManager.getRegionID();
                            makeJsonArrayGet(url, "store");
                        }else{
                            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(PostcodeCoverageActivity.this);
                            alertDialogBuilder.setMessage("Kode pos tidak ditemukan");
                            alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface arg0, int arg1) {
                                }
                            });

                            AlertDialog alertDialog = alertDialogBuilder.create();
                            alertDialog.setCanceledOnTouchOutside(false);
                            alertDialog.show();
                            preloader.setVisibility(View.GONE);
                        }
                    }
                }
        );

        clearText.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        searchPostcode.getText().clear();
                    }
                }
        );

        /*searchView = (SearchView) findViewById(R.id.search_view);
        searchView.onActionViewExpanded();
        searchView.setFocusable(true);
        searchView.setIconified(false);
        searchView.findViewById(android.support.v7.appcompat.R.id.search_plate)
                .setBackgroundColor(ContextCompat.getColor(this, android.R.color.transparent));

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Toast.makeText(PostcodeCoverageActivity.this, "onQueryTextSubmit :: " + query, Toast.LENGTH_SHORT).show();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                if (voice) {
                } else if (newText.isEmpty()) {
                    data2 = new ArrayList<String>();
                    postcodeListView = (ListView) findViewById(R.id.postcode_listview);
                    AreaAutoCompleteAdapter areaAutoCompleteAdapter = new AreaAutoCompleteAdapter(PostcodeCoverageActivity.this, data2);
                    postcodeListView.setAdapter(areaAutoCompleteAdapter);
                } else {
                    data2 = new ArrayList<String>();
                    for (int i = 0; i < data.length; i++) {
                        if (data[i].toLowerCase().contains(newText.toLowerCase())) {
                            data2.add(data[i]);
                        }
                    }

                    postcodeListView = (ListView) findViewById(R.id.postcode_listview);
                    AreaAutoCompleteAdapter areaAutoCompleteAdapter = new AreaAutoCompleteAdapter(PostcodeCoverageActivity.this, data2);
                    postcodeListView.setAdapter(areaAutoCompleteAdapter);

                    postcodeListView.setOnItemClickListener(
                            new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                    searchView.setQuery(data2.get(position), true);
                                }
                            }
                    );
                }

                voice = false;

                return false;
            }
        });*/

    }

    //get coverage area autocomplete
    public void makeJsonArrayGet(String urlJsonObj, final String check){
        JsonArrayRequest jsonArrayReq = new JsonArrayRequest(urlJsonObj,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        if(check.equals("auto")){
                            setAutoComplete(response);
                        } else if(check.equals("store")){
                            setStoreList(response);
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(PostcodeCoverageActivity.this, "Gagal terkoneksi server", Toast.LENGTH_LONG).show();
                preloader.setVisibility(View.GONE);
            }
        }, this);

        jsonArrayReq.setRetryPolicy(new DefaultRetryPolicy(60000, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppController.getInstance().addToRequestQueue(jsonArrayReq);
    }

    public void setAutoComplete(JSONArray response){
        data2 = new ArrayList<>();

        for (int i=0; i<response.length(); i++){
            try {
                data2.add(response.getString(i));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        if (data2.size() > 0){
            postcodeListView.setVisibility(View.VISIBLE);
            storeListView.setVisibility(View.GONE);
            notCoverage.setVisibility(View.GONE);
            storeListView.setVisibility(View.GONE);
            AreaAutoCompleteAdapter areaAutoCompleteAdapter = new AreaAutoCompleteAdapter(PostcodeCoverageActivity.this, data2);
            postcodeListView.setAdapter(areaAutoCompleteAdapter);
        }
    }

    public void setStoreList(JSONArray response){
        if(response.length() > 0){
            JSONArray jsonArray = new JSONArray();
            for (int i=0; i<response.length(); i++){
                try {
                    if (jsonArray.length() > 0 && jsonArray != null){
                        if (!jsonArray.toString().contains(response.getJSONObject(i).getString("Code"))){
                            jsonArray.put(response.getJSONObject(i));
                        }
                    }else{
                        jsonArray.put(response.getJSONObject(i));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            StoreCoverageAdapter storeCoverageAdapter = new StoreCoverageAdapter(PostcodeCoverageActivity.this, jsonArray);
            storeListView.setAdapter(storeCoverageAdapter);

            postcodeListView.setVisibility(View.GONE);
            storeListView.setVisibility(View.GONE);
            notCoverage.setVisibility(View.GONE);
            storeListView.setVisibility(View.VISIBLE);
        } else {
            notCoverage.setVisibility(View.VISIBLE);
            postcodeListView.setVisibility(View.GONE);
            storeListView.setVisibility(View.GONE);
            storeListView.setVisibility(View.GONE);
        }

        preloader.setVisibility(View.GONE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.area_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            finish();
            overridePendingTransition(R.anim.right_in, R.anim.right_out);
            return true;
        }

        /*if (id == R.id.voice_search) {
            displaySpeechRecognizer();
            return true;
        }*/

        return super.onOptionsItemSelected(item);
    }

    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.right_in, R.anim.right_out);
    }

    /*private void displaySpeechRecognizer() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        startActivityForResult(intent, SPEECH_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == SPEECH_REQUEST_CODE && resultCode == RESULT_OK) {
            List<String> results = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            String spokenText = results.get(0);
            voice = true;
            searchView.setQuery(spokenText, false);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }*/
}
