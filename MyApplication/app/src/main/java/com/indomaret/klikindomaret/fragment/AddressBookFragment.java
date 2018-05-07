package com.indomaret.klikindomaret.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.indomaret.klikindomaret.API;
import com.indomaret.klikindomaret.R;
import com.indomaret.klikindomaret.activity.AddAdressActivity;
import com.indomaret.klikindomaret.adapter.AddressAdapter;
import com.indomaret.klikindomaret.app.AppController;
import com.indomaret.klikindomaret.helper.SQLiteHandler;
import com.indomaret.klikindomaret.helper.SessionManager;
import com.indomaret.klikindomaret.helper.UncaughtExceptionHandler;
import com.indomaret.klikindomaret.views.HeightAdjustableListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * A simple {@link Fragment} subclass.
 */
public class AddressBookFragment extends Fragment {
    private View view;
    private Intent intent;
    private SQLiteHandler sqLiteHandler;
    private SessionManager sessionManager;

    private HeightAdjustableListView addressListView;
    private LinearLayout addAddress;
    private ScrollView scrollView;

    private AddressAdapter addressAdapter;
    private JSONArray userProfile, addressArray;
    private Tracker mTracker;

    boolean edit = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_address_list, container, false);
        UncaughtExceptionHandler uncaughtExceptionHandler = new UncaughtExceptionHandler();
        uncaughtExceptionHandler.uncaughtException(getActivity());

        AppController application = (AppController) getActivity().getApplication();
        mTracker = application.getDefaultTracker();

        sqLiteHandler = new SQLiteHandler(getActivity());
        sessionManager = new SessionManager(getActivity());

        addressListView = (HeightAdjustableListView) view.findViewById(R.id.address_list);
        addAddress = (LinearLayout) view.findViewById(R.id.add_address);
        scrollView = (ScrollView) view.findViewById(R.id.scroll_view);

        scrollView.fullScroll(ScrollView.FOCUS_UP);

        try {
            intent = getActivity().getIntent();
            userProfile = new JSONArray(sqLiteHandler.getProfile());
            addressArray = userProfile.getJSONObject(0).getJSONArray("Address");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        addressAdapter = new AddressAdapter(getActivity(), addressArray, this);
        addressListView.setAdapter(addressAdapter);

        addAddress.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        intent = new Intent(getActivity(), AddAdressActivity.class);
                        intent.putExtra("type", "add");
                        startActivityForResult(intent, 1);
                        getActivity().overridePendingTransition(R.anim.left_in, R.anim.left_out);
                    }
                }
        );

        addressListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
           @Override
           public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
               try {
                   JSONObject address = addressArray.getJSONObject(position);
                   sessionManager.setRegionID(addressArray.getJSONObject(position).getString("Region"));
                   sessionManager.setRegionName(addressArray.getJSONObject(position).getString("RegionName"));
                   address.put("IsDefault", true);
                   Log.d("Alamat Address list",addressArray.getJSONObject(position).toString());

                   JSONObject cartObject = new JSONObject();
                       try {
                           cartObject.put("CartId", sessionManager.getCartId());
                           cartObject.put("RegionId", sessionManager.getRegionID());
                       } catch (JSONException e) {
                           e.printStackTrace();
                       }

                       makeJsonPost(API.getInstance().getApiSetDefaultAddress()+"?isChangeAddress=false&mfp_id="+sessionManager.getKeyMfpId(), address, "update address");
                       makeJsonPost(API.getInstance().updateCartItemByRegion()+"?mfp_id=" + sessionManager.getKeyMfpId(),cartObject, "update cart");
                   } catch (JSONException e) {
                       e.printStackTrace();
                   }
               }
           }
        );
        return view;
    }

    public void setDefault(int position){
        try {
            JSONObject address = addressArray.getJSONObject(position);
            sessionManager.setRegionID(addressArray.getJSONObject(position).getString("Region"));
            sessionManager.setRegionName(addressArray.getJSONObject(position).getString("RegionName"));
            address.put("IsDefault", true);

            JSONObject cartObject = new JSONObject();

            cartObject.put("CartId", sessionManager.getCartId());
            cartObject.put("RegionId", sessionManager.getRegionID());

            makeJsonPost(API.getInstance().getApiSetDefaultAddress()+"?isChangeAddress=false&mfp_id="+sessionManager.getKeyMfpId(), address, "update address");
            makeJsonPost(API.getInstance().updateCartItemByRegion()+"?mfp_id=" + sessionManager.getKeyMfpId(),cartObject, "update cart");
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
                        if (type.equals("update address")){
                            processSetDefaultAddress(response);
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getActivity(), "Gagal terkoneksi server", Toast.LENGTH_LONG).show();
            }
        });

        AppController.getInstance().addToRequestQueue(jsonObjReq);
    }

    public void makeJsonObjectGet(String url, final String Type){
        System.out.println("address url = " + url);

        JsonArrayRequest jsonArrayReq = new JsonArrayRequest(url,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        processProfile(response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getActivity(), "Gagal terkoneksi server", Toast.LENGTH_LONG).show();
            }
        }, getActivity());

        jsonArrayReq.setRetryPolicy(new DefaultRetryPolicy(60000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppController.getInstance().addToRequestQueue(jsonArrayReq);
    }

    public void processProfile(final JSONArray response){
        sqLiteHandler.insertProfile(response.toString());
        Log.d("Response Alamat",response.toString());

        try {
            sessionManager.setUserID(response.getJSONObject(0).getString("ID"));
            JSONArray address = response.getJSONObject(0).getJSONArray("Address");

            for (int i=0; i<address.length(); i++){
                if(address.getJSONObject(i).getString("IsDefault").equals("true")){
                    sqLiteHandler.insertDefaultAddress(address.getJSONObject(i).toString());
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            userProfile = new JSONArray(sqLiteHandler.getProfile());
            addressArray = userProfile.getJSONObject(0).getJSONArray("Address");
//            edit = false;
            addressAdapter = new AddressAdapter(getActivity(), addressArray, this);
            addressListView.setAdapter(addressAdapter);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void processSetDefaultAddress(JSONObject response){
        try {
            if(response.getString("Message").equals("success")){
                makeJsonObjectGet(API.getInstance().getApiGetProfile()+"?access_token="+sessionManager.getResponseId()+"&mfp_id="+sessionManager.getResponseId(), "prof");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void updateDataAdapter(){
        try {
            userProfile = new JSONArray(sqLiteHandler.getProfile());
            addressArray = userProfile.getJSONObject(0).getJSONArray("Address");

            addressAdapter = new AddressAdapter(getActivity(), addressArray, this);
            addressListView.setAdapter(addressAdapter);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
