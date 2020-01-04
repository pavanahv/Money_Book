package com.pavanahv.allakumarreddy.moneybook.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pavanahv.allakumarreddy.moneybook.Activities.AddActivity;
import com.pavanahv.allakumarreddy.moneybook.Adapter.HomeViewPagerAdapter;
import com.pavanahv.allakumarreddy.moneybook.R;
import com.pavanahv.allakumarreddy.moneybook.storage.PreferencesCus;
import com.pavanahv.allakumarreddy.moneybook.storage.db.DbHandler;
import com.pavanahv.allakumarreddy.moneybook.utils.GlobalConstants;
import com.pavanahv.allakumarreddy.moneybook.utils.MBRecord;

import java.util.Date;

public class HomeFragment extends Fragment {
    private static final int ADD_ACTIVITY = 1001;

    private String mParam1;
    private String mParam2;

    private ViewPager mHomeViewPager;
    private HomeViewPagerAdapter mHomeViewPagerAdapter;
    private TabLayout tabLayout;

    public HomeFragment() {
        // Required empty public constructor
    }

    public static HomeFragment newInstance() {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        mHomeViewPager = (ViewPager) view.findViewById(R.id.pager);
        mHomeViewPagerAdapter = new HomeViewPagerAdapter(getChildFragmentManager());
        mHomeViewPager.setAdapter(mHomeViewPagerAdapter);

        tabLayout = (TabLayout) view.findViewById(R.id.tab_layout);
        tabLayout.setupWithViewPager(mHomeViewPager);

        FloatingActionButton fab = getActivity().findViewById(R.id.fab);
        fab.setVisibility(View.VISIBLE);
        fab.setOnClickListener(view1 -> startAddActivity());

        return view;
    }

    private void startAddActivity() {
        Intent intent = new Intent(getContext(), AddActivity.class);
        intent.putExtra("type", GlobalConstants.HOME_SCREEN);
        intent.putExtra(GlobalConstants.CATEGORY_TYPE, mHomeViewPager.getCurrentItem());
        startActivityForResult(intent, ADD_ACTIVITY);
        getActivity().overridePendingTransition(R.anim.move_right_in_activity, R.anim.move_left_out_activity);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case (ADD_ACTIVITY): {
                if (resultCode == Activity.RESULT_OK) {
                    String s[] = new String[]{data.getStringExtra("fdes"),
                            data.getStringExtra("famount"),
                            data.getStringExtra("fcategory"),
                            data.getStringExtra("tcategory"),
                            data.getStringExtra("payment_method")};
                    if ((s[0] != "") && (s[1] != "") && (s[2] != "")) {
                        final int pos = mHomeViewPager.getCurrentItem();
                        MBRecord mbr = new MBRecord(s[0], Integer.parseInt(s[1]), new Date(), s[2], s[4]);
                        DbHandler db = new DbHandler(getContext());
                        Date tempDate = getDatePref();
                        if (tempDate != null) {
                            mbr.setDate(tempDate);
                        }
                        db.addRecord(mbr, pos);
                    }
                }
                break;
            }
        }
    }

    private Date getDatePref() {
        return new PreferencesCus(getContext()).getCurrentDate();
    }

}