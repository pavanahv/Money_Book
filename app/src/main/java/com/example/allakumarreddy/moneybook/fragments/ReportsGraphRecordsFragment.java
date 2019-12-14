package com.example.allakumarreddy.moneybook.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.example.allakumarreddy.moneybook.Activities.AnalyticsItemDetail;
import com.example.allakumarreddy.moneybook.Activities.RePaymentActivity;
import com.example.allakumarreddy.moneybook.Adapter.AnalyticsAdapter;
import com.example.allakumarreddy.moneybook.R;
import com.example.allakumarreddy.moneybook.storage.db.DbHandler;
import com.example.allakumarreddy.moneybook.utils.AnalyticsFilterData;
import com.example.allakumarreddy.moneybook.utils.GlobalConstants;
import com.example.allakumarreddy.moneybook.utils.MBRecord;

import java.util.ArrayList;

import static com.example.allakumarreddy.moneybook.utils.AnimationUtils.revealAnimation;


public class ReportsGraphRecordsFragment extends Fragment {
    private static final String TAG = ReportsGraphRecordsFragment.class.getSimpleName();
    private OnFragmentInteractionListener mListener;
    private AnalyticsFilterData mAnalyticsFilterData;
    private ListView mListView;
    private View mProgress;
    private ArrayList<MBRecord> list;
    private AnalyticsAdapter mAdapter;
    private DbHandler db;
    private View noData;
    private View mMainView;

    public ReportsGraphRecordsFragment() {
        // Required empty public constructor
    }


    public static ReportsGraphRecordsFragment newInstance() {
        ReportsGraphRecordsFragment fragment = new ReportsGraphRecordsFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
        db = new DbHandler(getContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_reports_graph_records, container, false);
        init(root);
        return root;
    }

    private void init(View root) {
        mMainView = root.findViewById(R.id.main_view);
        mMainView.setVisibility(View.GONE);
        mListView = (ListView) root.findViewById(R.id.lv);
        mProgress = root.findViewById(R.id.progress);
        mProgress.setVisibility(View.VISIBLE);
        noData = root.findViewById(R.id.no_data);
        noData.setVisibility(View.VISIBLE);
        mListView.setVisibility(View.GONE);

        list = new ArrayList<>();
        mAdapter = new AnalyticsAdapter(list, getContext(), (pos, v) -> {
            MBRecord mbr = list.get(pos);
            Intent intent = null;
            if (mbr.getType() == GlobalConstants.TYPE_DUE ||
                    mbr.getType() == GlobalConstants.TYPE_LOAN ||
                    mbr.getType() == GlobalConstants.TYPE_DUE_PAYMENT ||
                    mbr.getType() == GlobalConstants.TYPE_LOAN_PAYMENT) {
                intent = new Intent(getActivity(), RePaymentActivity.class);
                intent.putExtra("MBRecord", mbr);
            } else {
                intent = new Intent(getActivity(), AnalyticsItemDetail.class);
                intent.putExtra("MBRecord", mbr);
            }
            intent.putExtra("MBRecord", mbr);
            startActivity(intent);
            getActivity().overridePendingTransition(R.anim.move_right_in_activity, R.anim.move_left_out_activity);
        });
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

    public void init(AnalyticsFilterData analyticsFilterData) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                mAnalyticsFilterData = analyticsFilterData;
                list.clear();
                list.addAll(db.getRecordsAsList(mAnalyticsFilterData));
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mAdapter.notifyDataSetChanged();
                        mListView.setVisibility(View.VISIBLE);
                        noData.setVisibility(View.GONE);
                        showMainView();
                    }
                });
            }
        }).start();
    }

    @Override
    public void onResume() {
        super.onResume();
        showMainView();
    }

    private void showMainView() {
        revealAnimation(mProgress, mMainView, getView());
    }

    public interface OnFragmentInteractionListener {
    }
}
