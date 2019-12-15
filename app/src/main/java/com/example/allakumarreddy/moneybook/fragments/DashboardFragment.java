package com.example.allakumarreddy.moneybook.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.allakumarreddy.moneybook.Activities.AddActivity;
import com.example.allakumarreddy.moneybook.Adapter.DashBoardViewPagerAdapter;
import com.example.allakumarreddy.moneybook.R;
import com.example.allakumarreddy.moneybook.storage.db.DbHandler;
import com.example.allakumarreddy.moneybook.utils.GlobalConstants;

public class DashboardFragment extends Fragment {


    private static final int ADD_ACTIVITY = 1001;
    private static final String TAG = DashboardFragment.class.getSimpleName();

    private ViewPager mViewPager;
    private TabLayout tabLayout;

    public DashboardFragment() {
        // Required empty public constructor
    }

    public static DashboardFragment newInstance(String param1, String param2) {
        DashboardFragment fragment = new DashboardFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        mViewPager = (ViewPager) view.findViewById(R.id.pager);
        DashBoardViewPagerAdapter mDashBoardViewPagerAdapter = new DashBoardViewPagerAdapter(getChildFragmentManager());
        mViewPager.setAdapter(mDashBoardViewPagerAdapter);

        tabLayout = (TabLayout) view.findViewById(R.id.tab_layout);
        tabLayout.setupWithViewPager(mViewPager);

        FloatingActionButton fab = (FloatingActionButton) getActivity().findViewById(R.id.fab);
        fab.setVisibility(View.VISIBLE);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startAddActivity();
            }
        });
        init();
        return view;
    }

    private void init() {
        FloatingActionButton fab = (FloatingActionButton) getActivity().findViewById(R.id.fab);
        fab.setVisibility(View.VISIBLE);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startAddActivity();
            }
        });
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.findItem(R.id.action_calender).setVisible(false);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    public void addCategory(String name, int type) {
        DbHandler db = new DbHandler(getContext());
        db.addCategory(name, type);
    }

    private void startAddActivity() {
        Intent intent = new Intent(getContext(), AddActivity.class);
        intent.putExtra("type", GlobalConstants.CATERGORY_SCREEN);
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
                    addCategory(s[0], mViewPager.getCurrentItem());
                }
                break;
            }
        }
    }
}
