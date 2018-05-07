package com.indomaret.klikindomaret.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.indomaret.klikindomaret.API;
import com.indomaret.klikindomaret.R;
import com.indomaret.klikindomaret.adapter.NotificationAdapter;
import com.indomaret.klikindomaret.app.AppController;
import com.indomaret.klikindomaret.helper.SessionManager;
import com.indomaret.klikindomaret.views.HeightAdjustableListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * A simple {@link Fragment} subclass.
 */
public class NotificationFragment extends Fragment {
    private View view;
    private SessionManager sessionManager;
    private HeightAdjustableListView notifList;
    private RelativeLayout preloader, cover;

    private JSONObject notifObject;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragmen_notification, container, false);
        sessionManager = new SessionManager(getActivity());

        notifList = (HeightAdjustableListView) view.findViewById(R.id.list_notif);
        preloader = (RelativeLayout) view.findViewById(R.id.preloader);
        cover = (RelativeLayout) view.findViewById(R.id.cover);

        preloader.setVisibility(View.VISIBLE);
        cover.setVisibility(View.VISIBLE);

        makeJsonObjectGet(API.getInstance().getApiGetProfile()+"?access_token="+sessionManager.getResponseId()+"&mfp_id="+sessionManager.getResponseId());
        return view;
    }

    public void makeJsonObjectGet(String url){
        System.out.println("address url = " + url);

        JsonArrayRequest jsonArrayReq = new JsonArrayRequest(url,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        if (response == null || response.length() == 0){
                            Toast.makeText(getActivity(), "Gagal terkoneksi server", Toast.LENGTH_LONG).show();
                        }else{
                            prossessNotif(response);
                        }

                        preloader.setVisibility(View.GONE);
                        cover.setVisibility(View.GONE);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getActivity(), "Gagal terkoneksi server", Toast.LENGTH_LONG).show();
                preloader.setVisibility(View.GONE);
                cover.setVisibility(View.GONE);
            }
        }, getActivity());

        jsonArrayReq.setRetryPolicy(new DefaultRetryPolicy(60000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppController.getInstance().addToRequestQueue(jsonArrayReq);
    }

    public void jsonPost(String urlJsonObj, JSONObject jsonObject){
        System.out.println("update url= " + urlJsonObj);
        System.out.println("update = object" + jsonObject);

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                urlJsonObj, jsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(final JSONObject response) {
//                        if (response == null || response.length() == 0){
//                            preloader.setVisibility(View.GONE);
//                            cover.setVisibility(View.GONE);
//                            Toast.makeText(getActivity(), "Gagal terkoneksi server", Toast.LENGTH_LONG).show();
//                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getActivity(), "Gagal terkoneksi server", Toast.LENGTH_LONG).show();
                preloader.setVisibility(View.GONE);
                cover.setVisibility(View.GONE);
            }
        });

        AppController.getInstance().addToRequestQueue(jsonObjReq);
    }

    public void prossessNotif(JSONArray response){
        notifObject = new JSONObject();

        try {
            NotificationAdapter notificationAdapter = new NotificationAdapter(getActivity(), response.getJSONObject(0).getJSONArray("CustNotifications"));
            notifList.setAdapter(notificationAdapter);

            notifObject.put("ID", sessionManager.getUserID());
            jsonPost(API.getInstance().getApiSetNotif()+ "?mfp_id=" + sessionManager.getKeyMfpId(), notifObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
