package com.example.allakumarreddy.moneybook.fragments;


import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.example.allakumarreddy.moneybook.R;

public class ReportsFragment extends Fragment {

    private LinearLayout mMainLayout;
    private ViewPager mViewPager;
    private ReportsViewPagerAdapter mReportsViewPagerAdapter;
    private TabLayout tabLayout;

    public ReportsFragment() {
        // Required empty public constructor
    }

    public static ReportsFragment newInstance() {
        ReportsFragment fragment = new ReportsFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_reports, container, false);
        getActivity().findViewById(R.id.fab).setVisibility(View.GONE);
        init(root);
        return root;
    }

    private void init(View root) {
        mViewPager = (ViewPager) root.findViewById(R.id.pager);
        mReportsViewPagerAdapter = new ReportsViewPagerAdapter(getChildFragmentManager());
        mViewPager.setAdapter(mReportsViewPagerAdapter);

        tabLayout = (TabLayout) root.findViewById(R.id.tab_layout);
        tabLayout.setupWithViewPager(mViewPager);
    }

}
