package com.indomaret.klikindomaret.activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.indomaret.klikindomaret.R;
import com.indomaret.klikindomaret.fragment.PromoPaymentFragment;
import com.indomaret.klikindomaret.helper.SessionManager;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

public class PromoItemActivity extends AppCompatActivity implements ViewPager.OnPageChangeListener {
    private Intent intent;
    private TabLayout promoTabLayout;
    private ViewPager promoViewPager;
    private Button btnOk;
    private SessionManager sessionManager;
    private boolean isEdit;
    private JSONArray promo = new JSONArray();
    private JSONArray promoProductCart = new JSONArray();
    private JSONArray promoselected = new JSONArray();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_promo_item);

        sessionManager = new SessionManager(this);
        intent = getIntent();

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int widthDm = dm.widthPixels;
        int heightDm = dm.heightPixels;

        getWindow().setLayout((int) (widthDm*.9), (int) (heightDm*.9));

        try {
            promoProductCart = new JSONArray(intent.getStringExtra("promo"));
            promo = new JSONArray(intent.getStringExtra("promoProduct"));
            JSONArray shoppingCartItems = new JSONArray();

            for (int i=0; i<promoProductCart.length(); i++){
                shoppingCartItems = promoProductCart.getJSONObject(i).getJSONArray("ShoppingCartItems");
                for (int j=0; j<shoppingCartItems.length(); j++){
                    for (int k=0; k<promo.length(); k++){
                        if (shoppingCartItems.getJSONObject(j).getString("ProductID").equals(promo.getJSONObject(k).getString("ProductID"))){
                            shoppingCartItems.getJSONObject(j).put("IsSelectedPromo", promo.getJSONObject(k).getBoolean("IsSelectedPromo"));
                            shoppingCartItems.getJSONObject(j).put("QuantityUpdate", promo.getJSONObject(k).getInt("QuantityUpdate"));
                        }
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        promoTabLayout = (TabLayout) findViewById(R.id.promo_tabs);
        promoViewPager = (ViewPager) findViewById(R.id.promo_viewpager);
        btnOk = (Button) findViewById(R.id.btn_ok);

        setupViewPager(promoViewPager);
        promoTabLayout.setupWithViewPager(promoViewPager);
        selectedPage(0);

        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sessionManager.setKeyEditSpinner(false);
                if (promo.length() > 0){
                    if (!isEdit){
                        intent.putExtra("promo", promo.toString());
                    }else{
                        intent.putExtra("promo", promoselected.toString());
                    }
                }else{
                    intent.putExtra("promo", promoselected.toString());
                }

                intent.putExtra("objectPayment", intent.getStringExtra("objectPayment"));
                intent.putExtra("codePayment", intent.getStringExtra("codePayment"));
                promoProductCart = new JSONArray();
                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapterVirtual adapter = new ViewPagerAdapterVirtual(PromoItemActivity.this.getSupportFragmentManager());
        String qty = "";
        JSONArray shoppingCartItems = new JSONArray();
        int countNewLine = 0;
        boolean isNewLine = true;

        for (int i=0; i<promoProductCart.length(); i++){
            try {
                if (promoProductCart.getJSONObject(i).getJSONArray("ShoppingCartItems").getJSONObject(0).getInt("QuantityMaxStruk") == 0){
                    qty =promoProductCart.getJSONObject(i).getJSONArray("ShoppingCartItems").getJSONObject(0).getString("Quantity");
                }else{
                    qty = promoProductCart.getJSONObject(i).getJSONArray("ShoppingCartItems").getJSONObject(0).getString("QuantityMaxStruk");
                }

//                qty = promoProductCart.getJSONObject(i).getJSONArray("ShoppingCartItems").getJSONObject(0).getString("Quantity");
                shoppingCartItems = promoProductCart.getJSONObject(i).getJSONArray("ShoppingCartItems");

                for (int j=0; j<shoppingCartItems.length(); j++){
                    if (!shoppingCartItems.getJSONObject(j).getBoolean("BonusIsNewLine")){
                        countNewLine += 1;
                        isNewLine = false;
                        shoppingCartItems.getJSONObject(j).put("IsSelectedPromo", true);
                        shoppingCartItems.getJSONObject(j).put("PaymentCode", intent.getStringExtra("codePayment"));

                        if (!promo.toString().contains(shoppingCartItems.getJSONObject(j).getString("ProductID"))){
                            promo.put(shoppingCartItems.getJSONObject(j));
                        }

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                            shoppingCartItems.remove(j);
                        }
                    }else{
                        isNewLine = true;
                    }
                }

                if (isNewLine){
                    adapter.addFragment(new PromoPaymentFragment(promoProductCart.getJSONObject(i), i-countNewLine), "promo " + ((i-countNewLine)+1) + " (maks " + qty + ")");
                }else if (promoProductCart.length() == 1){
                    sessionManager.setKeyEditSpinner(false);
                    if (promo.length() > 0){
                        if (!isEdit){
                            intent.putExtra("promo", promo.toString());
                        }else{
                            intent.putExtra("promo", promoselected.toString());
                        }
                    }else{
                        intent.putExtra("promo", promoselected.toString());
                    }

                    intent.putExtra("objectPayment", intent.getStringExtra("objectPayment"));
                    intent.putExtra("codePayment", intent.getStringExtra("codePayment"));
                    promoProductCart = new JSONArray();
                    setResult(RESULT_OK, intent);
                    finish();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        viewPager.setAdapter(adapter);
    }

    public void setTabView(final int position, int value) {
        TabLayout.Tab tab = promoTabLayout.getTabAt(position);

        if (tab.getCustomView() == null) tab.setCustomView(LayoutInflater.from(PromoItemActivity.this).inflate(R.layout.tab_item, null));

        View view=tab.getCustomView();
        TextView tabName= (TextView) view.findViewById(R.id.tab_name);
        TextView notifCount= (TextView) view.findViewById(R.id.notifText);

        tabName.setText(tab.getText());
        if (value > 0){
            notifCount.setVisibility(View.VISIBLE);
            notifCount.setText(value+"");
        }else{
            notifCount.setVisibility(View.GONE);
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    class ViewPagerAdapterVirtual extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapterVirtual(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

    public void selectedPage(int pageIndex) {
        promoTabLayout.setScrollPosition(pageIndex, 0f, true);
        promoViewPager.setCurrentItem(pageIndex);
    }

    public void promoSelectedUpdate(JSONArray promoList){
        isEdit = true;
        try {
            for (int i=0; i<promoList.length(); i++){
                promoList.getJSONObject(i).put("PaymentCode", intent.getStringExtra("codePayment"));
                if (!promoselected.toString().contains(promoList.getJSONObject(i).getString("ProductID")) && promoList.getJSONObject(i).getBoolean("IsSelectedPromo")){
                    promoselected.put(promoList.getJSONObject(i));
                }
            }

            if (promo.length() > 0){
                for (int i=0; i<promo.length(); i++){
                    if (!promoselected.toString().contains(promo.getJSONObject(i).getString("ProductID")) && promo.getJSONObject(i).getBoolean("IsSelectedPromo")){
                        promoselected.put(promo.getJSONObject(i));
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void promoUnSelectedUpdate(JSONArray promoList){
        isEdit = true;
        try {
            for (int i=0; i<promoList.length(); i++){
                promoList.getJSONObject(i).put("PaymentCode", intent.getStringExtra("codePayment"));

                if (promoselected.length() > 0){
                    for (int j=0; j<promoselected.length(); j++){
                        if (promoselected.getJSONObject(j).getString("ProductID").equals(promoList.getJSONObject(i).getString("ProductID"))
                                && !promoList.getJSONObject(i).getBoolean("IsSelectedPromo")){
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                                promoselected.remove(j);
                            }
                        }
                    }
                }else if (promo.length() > 0){
                    for (int j=0; j<promo.length(); j++){
                            if (promo.getJSONObject(j).getString("ProductID").equals(promoList.getJSONObject(i).getString("ProductID"))
                                    && !promoList.getJSONObject(i).getBoolean("IsSelectedPromo")){
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                                    promo.remove(j);
                                }
                            }
                    }

                    for (int j=0; j<promo.length(); j++){
                        promoselected.put(promo.getJSONObject(j));
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
