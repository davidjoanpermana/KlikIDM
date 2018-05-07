package com.indomaret.klikindomaret.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.indomaret.klikindomaret.API;
import com.indomaret.klikindomaret.R;
import com.indomaret.klikindomaret.activity.OrderSummaryActivity;
import com.indomaret.klikindomaret.adapter.OrderHistoryAdapter;
import com.indomaret.klikindomaret.app.AppController;
import com.indomaret.klikindomaret.helper.SessionManager;
import com.indomaret.klikindomaret.views.HeightAdjustableListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import pl.droidsonroids.gif.GifImageView;

/**
 * A simple {@link Fragment} subclass.
 */
public class OrderHistoryTabbedFragment extends Fragment {
    private View view;
    private Intent intent;
    private SessionManager sessionManager;
    private OrderHistoryAdapter orderHistoryAdapter;

    private int currentPage = 1;
    private int pageSize = 30;
    private List<JSONObject> orderObjectList;

    private HeightAdjustableListView orderList;
    private RelativeLayout preloader;

    private String mode = "";
    private String sortType = "";
    private String url;
    private JSONArray historys = new JSONArray();
    int count = 0;

    public OrderHistoryTabbedFragment(String mode) {
        this.mode = mode;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_order_history_tabbed, container, false);
        sessionManager = new SessionManager(getActivity());
        orderList = (HeightAdjustableListView) view.findViewById(R.id.order_history);

        preloader = (RelativeLayout) view.findViewById(R.id.preloader);

        runLoader();

        System.out.println("mode : "+mode);
        orderObjectList = new ArrayList<>();
        currentPage = 1;
        url = API.getInstance().getApiSalesOrderHeader()+sessionManager.getUserID()+"?no=&currPage="+currentPage
                +"&pageSize="+pageSize
                +"&mode="+mode
                +"&sortmode="
                +"&mfp_id="+sessionManager.getKeyMfpId();
        jsonArrayGet(url, "new", 0);

        orderList.setOnScrollListener(
                new AbsListView.OnScrollListener() {
                    @Override
                    public void onScrollStateChanged(AbsListView view, int scrollState) {
                        int threshold = 1;
                        int count = orderList.getCount();

                        if (scrollState == SCROLL_STATE_IDLE) {
                            if(((orderList.getLastVisiblePosition()) >= (count - threshold))){
                                preloader.setVisibility(View.VISIBLE);

                                currentPage = currentPage + 1;
                                url = API.getInstance().getApiSalesOrderHeader()+sessionManager.getUserID()+"?no=&currPage="+currentPage
                                        +"&pageSize="+pageSize
                                        +"&mode="+mode
                                        +"&sortmode="
                                        +"&mfp_id="+sessionManager.getKeyMfpId();
                                jsonArrayGet(url, "update", 0);
                            }
                        }
                    }

                    @Override
                    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {}
                }
        );

        return view;
    }

    public void jsonArrayGet(String urlJsonObj, final String type, final int index){
        System.out.println("--- ORDER HISTORY URL : "+urlJsonObj);
        System.out.println("--- ORDER HISTORY TYPE : "+type);

        JsonArrayRequest jsonArrayReq = new JsonArrayRequest(urlJsonObj,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        if (type.contains("sales")){
                            try {
                                historys.getJSONObject(index).put("detailPayment", response);
                                count += 1;

                                if (count == historys.length()) processOrderHistory(historys, type);
                            } catch (JSONException e) {
                                e.printStackTrace();
                                preloader.setVisibility(View.GONE);
                            }

                        }else{
                            historys = response;
                            count = 0;
                            for (int i=0; i<historys.length(); i++){
                                try {
                                    if (type.equals("new")){
                                        jsonArrayGet(API.getInstance().getApiDetailPayment() + "?TransactionCode=" + historys.getJSONObject(i).getString("TransactionCode") + "&TypeCode=&RegionID="+ sessionManager.getRegionID() +"&mfp_id=" + sessionManager.getKeyMfpId(), "salesNew", i);
                                    }else if(type.equals("update")){
                                        jsonArrayGet(API.getInstance().getApiDetailPayment() + "?TransactionCode=" + historys.getJSONObject(i).getString("TransactionCode") + "&TypeCode=&RegionID="+ sessionManager.getRegionID() +"&mfp_id=" + sessionManager.getKeyMfpId(), "salesUpdate", i);
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                    preloader.setVisibility(View.GONE);
                                }
                            }

                            if (historys.length() == 0){
                                preloader.setVisibility(View.GONE);
                            }
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println("Error" + error);
                Toast.makeText(getActivity(), "Gagal terkoneksi server", Toast.LENGTH_LONG).show();
                preloader.setVisibility(View.GONE);
            }
        }, getActivity());

        jsonArrayReq.setRetryPolicy(new DefaultRetryPolicy(60000, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppController.getInstance().addToRequestQueue(jsonArrayReq);
    }

    public void stopLoading(){
        preloader.setVisibility(View.GONE);
    }

    public void runLoading(){
        preloader.setVisibility(View.VISIBLE);
    }

    public void processOrderHistory(JSONArray response, String type){
        if(type.equals("salesNew")){
            orderObjectList = new ArrayList<>();
        }

        for (int i=0; i<response.length(); i++){
            try{
                orderObjectList.add(response.getJSONObject(i));
            } catch (Exception e){
                e.printStackTrace();
            }
        }

        if(type.equals("salesUpdate")){
            orderHistoryAdapter.notifyDataSetChanged();
        } else {
            orderHistoryAdapter = new OrderHistoryAdapter(getActivity(), orderObjectList, mode, OrderHistoryTabbedFragment.this);
            orderList.setAdapter(orderHistoryAdapter);
        }

        orderList.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                        intent = new Intent(getActivity(), OrderSummaryActivity.class);
//                        try {
//                            intent.putExtra("salesOrderId", orderObjectList.get(position).getString("ID"));
//                            intent.putExtra("transactionCode", orderObjectList.get(position).getString("TransactionCode"));
//                            intent.putExtra("from", "order");
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }
//
//                        getActivity().startActivity(intent);
//                        getActivity().overridePendingTransition(R.anim.left_in, R.anim.left_out);
                    }
                }
        );

        preloader.setVisibility(View.GONE);
    }

    public void runLoader(){
        preloader.setVisibility(View.VISIBLE);
    }

    public void stopLoader(){
        preloader.setVisibility(View.GONE);
    }
}
