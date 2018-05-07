package com.indomaret.klikindomaret.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.indomaret.klikindomaret.R;
import com.indomaret.klikindomaret.app.AppController;
import com.indomaret.klikindomaret.fragment.AddressBookFragment;
import com.indomaret.klikindomaret.fragment.NotificationFragment;
import com.indomaret.klikindomaret.fragment.OrderHistoryFragment;
import com.indomaret.klikindomaret.fragment.ProfileFragment;
import com.indomaret.klikindomaret.fragment.WhistlistFragment;
import com.indomaret.klikindomaret.helper.SQLiteHandler;
import com.indomaret.klikindomaret.helper.UncaughtExceptionHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ProfileActivity extends AppCompatActivity {
    private ViewPager profileViewPager;
    private Spinner spinnerMenu;
    private Tracker mTracker;
    private boolean isAddress;
    private JSONArray userProfileObjectArray;
    private JSONObject userProfileObject;
    private SQLiteHandler sqLiteHandler;

    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        UncaughtExceptionHandler uncaughtExceptionHandler = new UncaughtExceptionHandler();
        uncaughtExceptionHandler.uncaughtException(this);

        AppController application = (AppController) getApplication();
        mTracker = application.getDefaultTracker();
        sqLiteHandler = new SQLiteHandler(ProfileActivity.this);

        intent = getIntent();
        isAddress = false;
        int pageIndex = Integer.parseInt(intent.getStringExtra("pageindex"));

        if (isAddress){
            getProfile(sqLiteHandler.getProfile());
        }

        profileViewPager = (ViewPager) findViewById(R.id.profile_viewpager);
        spinnerMenu = (Spinner) findViewById(R.id.spinner_menu);

        setupViewPager(profileViewPager);
        spinnerMenu.setSelection(pageIndex);

        spinnerMenu.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                profileViewPager.setCurrentItem(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    public void getProfile(String userProfile){
        try {
            userProfileObjectArray = new JSONArray(userProfile);
            userProfileObject = userProfileObjectArray.getJSONObject(0);

            if (userProfileObject.getString("MobileVerified").equals("false")){
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ProfileActivity.this);
                alertDialogBuilder.setMessage("Anda belum melakukan verifikasi nomor telp.");
                alertDialogBuilder.setNegativeButton("Tutup", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {}
                });
                final AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.right_in, R.anim.right_out);
    }

    @Override
    public void onResume(){
        super.onResume();
        mTracker.setScreenName("Profile Page");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new ProfileFragment());
        adapter.addFragment(new AddressBookFragment());
        adapter.addFragment(new OrderHistoryFragment());
        adapter.addFragment(new WhistlistFragment());
        adapter.addFragment(new NotificationFragment());
        viewPager.setAdapter(adapter);
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
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

        public void addFragment(Fragment fragment) {
            mFragmentList.add(fragment);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return null;
        }
    }

    @Override
    public void onDestroy(){
        super.onDestroy();

        Intent intent = new Intent("test");
        sendBroadcast(intent);
    }
}
