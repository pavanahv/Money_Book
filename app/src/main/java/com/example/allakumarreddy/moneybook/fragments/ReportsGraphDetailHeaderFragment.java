package com.example.allakumarreddy.moneybook.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.example.allakumarreddy.moneybook.Adapter.ReportHeaderAdapter;
import com.example.allakumarreddy.moneybook.R;
import com.example.allakumarreddy.moneybook.utils.LoggerCus;

import java.util.ArrayList;

public class ReportsGraphDetailHeaderFragment extends Fragment {

    private static final String TAG = ReportsGraphDetailHeaderFragment.class.getSimpleName();
    private OnFragmentInteractionListener mListener;
    private String[][] data = null;
    private ListView mListView;
    private ArrayList<String[]> list;
    private ReportHeaderAdapter mAdapter;

    public ReportsGraphDetailHeaderFragment() {
        // Required empty public constructor
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mListener != null) {
            data = mListener.getData();
            initViewData();
        }
    }

    private void initViewData() {
        if (data != null) {
            LoggerCus.d(TAG, "" + data.length);
            list.clear();
            final int len = data.length;
            for (int i = 0; i < len; i++) {
                list.add(data[i]);
            }
            mAdapter.notifyDataSetChanged();
        }
    }

    public static ReportsGraphDetailHeaderFragment newInstance() {
        ReportsGraphDetailHeaderFragment fragment = new ReportsGraphDetailHeaderFragment();
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
        View root = inflater.inflate(R.layout.fragment_reports_graph_detail_header, container, false);
        init(root);
        return root;
    }

    private void init(View root) {
        mListView = root.findViewById(R.id.lv);
        list = new ArrayList<>();
        mAdapter = new ReportHeaderAdapter(list, getContext(),
                position -> mListener.itemClicked(position));
        mListView.setAdapter(mAdapter);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        String[][] getData();

        void itemClicked(int position);
    }
}
