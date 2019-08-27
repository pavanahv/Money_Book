package com.example.allakumarreddy.moneybook.home;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.example.allakumarreddy.moneybook.Activities.AnalyticsItemDetail;
import com.example.allakumarreddy.moneybook.Activities.HomeAdapterInterface;
import com.example.allakumarreddy.moneybook.Adapter.MyAdapter;
import com.example.allakumarreddy.moneybook.R;
import com.example.allakumarreddy.moneybook.db.DbHandler;
import com.example.allakumarreddy.moneybook.utils.GlobalConstants;
import com.example.allakumarreddy.moneybook.utils.MBRecord;
import com.example.allakumarreddy.moneybook.utils.Utils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


public class HomeLoanFragment extends Fragment {

    private SimpleDateFormat format;
    private String curDateStr;
    private DbHandler mDbHandler;
    private ListView mListView;
    private TextView mTotalTextView;
    private ArrayList<MBRecord> mbr;
    private HomeAdapterInterface mHomeAdapterInterface = new HomeAdapterInterface() {
        @Override
        public void onClickItem(MBRecord mbr) {
            startDetailActivity(mbr);
        }
    };
    private MyAdapter mAdapter;

    public HomeLoanFragment() {
        // Required empty public constructor
    }

    public static HomeSpentFragment newInstance(String param1, String param2) {
        HomeSpentFragment fragment = new HomeSpentFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Date curDate = new Date();
        format = new SimpleDateFormat("yyyy/MM/dd");
        curDateStr = format.format(curDate);
        mDbHandler = new DbHandler(getContext());
        mbr = new ArrayList<MBRecord>();
        mAdapter = new MyAdapter(mbr, getContext(), GlobalConstants.TYPE_LOAN, mHomeAdapterInterface);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View layout = inflater.inflate(R.layout.fragment_home_spent, container, false);
        mListView = (ListView) layout.findViewById(R.id.lv);
        mListView.setAdapter(mAdapter);
        mTotalTextView = (TextView) layout.findViewById(R.id.total_tv);
        return layout;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onResume() {
        super.onResume();
        updataUI();
    }

    public void updataUI(){
        new Thread(() -> {
            mbr = mDbHandler.getRecords(curDateStr, GlobalConstants.TYPE_LOAN);
            getActivity().runOnUiThread(() -> {
                mAdapter.clear();
                mAdapter.addAll(mbr);
                mAdapter.notifyDataSetChanged();
                mTotalTextView.setText(Utils.getFormattedNumber(getTotalAmount()));
            });
        }).start();
    }

    private int getTotalAmount() {
        int total = 0;
        for (MBRecord n : mbr)
            total += n.getAmount();
        return total;
    }

    public void startDetailActivity(MBRecord mbr) {
        Intent intent = new Intent(getActivity(), AnalyticsItemDetail.class);
        intent.putExtra("MBRecord", mbr);
        startActivity(intent);
    }

}
