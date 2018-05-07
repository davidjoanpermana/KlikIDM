package com.indomaret.klikindomaret.fragment;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.indomaret.klikindomaret.R;
import com.indomaret.klikindomaret.helper.SessionManager;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class OrderHistoryFragment extends Fragment {
    private View view;
    SessionManager sessionManager;
    private TabLayout riwayatTabLayout;
    private ViewPager riwayatViewPager;

    private String[] tabTitle = {
            "Konfirmasi",
            "Proses",
            "Gagal",
            "Diterima",
            "Dibatalkan"
    };

    public OrderHistoryFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_order_history, container, false);
        sessionManager = new SessionManager(getActivity());
        riwayatViewPager = (ViewPager) view.findViewById(R.id.riwayat_viewpager);
        setupViewPager(riwayatViewPager);

        riwayatTabLayout = (TabLayout) view.findViewById(R.id.riwayat_tabs);
        riwayatTabLayout.setupWithViewPager(riwayatViewPager);

        selectedPage(0);

        return view;
    }

    public void selectedPage(int pageIndex){
        riwayatTabLayout.setScrollPosition(pageIndex, 0f, true);
        riwayatViewPager.setCurrentItem(pageIndex);
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getActivity().getSupportFragmentManager());
        adapter.addFragment(new OrderHistoryTabbedFragment("4"), "Konfirmasi");
        adapter.addFragment(new OrderHistoryTabbedFragment("1"), "Proses");
        adapter.addFragment(new OrderHistoryTabbedFragment("3"), "Gagal");
        adapter.addFragment(new OrderHistoryTabbedFragment("2"), "Diterima");
        adapter.addFragment(new OrderHistoryTabbedFragment("5"), "Dibatalkan");
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

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }
}
