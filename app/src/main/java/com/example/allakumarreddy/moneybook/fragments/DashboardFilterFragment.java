package com.example.allakumarreddy.moneybook.fragments;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.allakumarreddy.moneybook.Activities.GraphActivity;
import com.example.allakumarreddy.moneybook.Adapter.DashBoardFilterAdapter;
import com.example.allakumarreddy.moneybook.R;
import com.example.allakumarreddy.moneybook.interfaces.DashBoardFilterAdapterInterface;
import com.example.allakumarreddy.moneybook.interfaces.DashUIUpdateInterface;
import com.example.allakumarreddy.moneybook.storage.db.DbHandler;
import com.example.allakumarreddy.moneybook.utils.GlobalConstants;
import com.example.allakumarreddy.moneybook.utils.MBRecord;
import com.example.allakumarreddy.moneybook.utils.Utils;

import java.util.ArrayList;

public class DashboardFilterFragment extends Fragment implements DashBoardFilterAdapterInterface {

    private RecyclerView dashBoardFilterList;
    private DbHandler db;
    private DashUIUpdateInterface mDashUIUpdateInterface;

    public DashboardFilterFragment() {
        // Required empty public constructor
    }


    public static DashboardFilterFragment newInstance(String param1, String param2) {
        DashboardFilterFragment fragment = new DashboardFilterFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = new DbHandler(getContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_dashboard_filter, container, false);
        init(view);
        return view;
    }

    private void init(View view) {
        dashBoardFilterList = (RecyclerView) view.findViewById(R.id.dash_filter_recycler);
        getActivity().findViewById(R.id.fab).setVisibility(View.GONE);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initViewData();
    }

    private void initViewData() {
        dashBoardFilterList.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        dashBoardFilterList.setLayoutManager(layoutManager);
    }

    @Override
    public void onResume() {
        super.onResume();
        dashBoardUIData();
    }

    public void dashBoardUIData() {
        new Thread(() -> {
            String[][] jsonDataTitle = db.getDashBoardFilterRecords();
            getActivity().runOnUiThread(() -> {

                DashBoardFilterAdapter mDashBoardFilterAdapter = new DashBoardFilterAdapter(jsonDataTitle, getContext(), this);
                dashBoardFilterList.setAdapter(mDashBoardFilterAdapter);
            });
        }).start();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mDashUIUpdateInterface = (DashUIUpdateInterface) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement DashUIUpdateInterface");
        }
    }

    @Override
    public void delteFilter(String filterName) {
        db.deleteFilter(filterName);
        dashBoardUIData();
    }

    @Override
    public void fullScreen(String filterName) {
        new Thread(() -> {

            ArrayList<MBRecord> dataList = Utils.getFilterRecordsFromParelableJSONString(filterName, getContext());
            final int size = dataList.size();
            String[] label = new String[size];
            String[] data = new String[size];
            for (int i = 0; i < size; i++) {
                MBRecord mbr = dataList.get(i);
                label[i] = mbr.getDescription();
                data[i] = mbr.getAmount() + "";
            }

            Intent in = new Intent(getContext(), GraphActivity.class);
            in.putExtra("label", label);
            in.putExtra("data", data);
            in.putExtra("type", Utils.getFilterGraphType(filterName));
            in.putExtra("activateType", GlobalConstants.ACTIVATE_GRAPH_ACTIVITY_WITHOUT_ADD_TO_SCREEN_MENU);

            getActivity().runOnUiThread(() -> startActivity(in));

        }).start();

    }
}
