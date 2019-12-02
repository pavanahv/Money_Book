package com.example.allakumarreddy.moneybook.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.example.allakumarreddy.moneybook.Activities.AnalyticsItemDetail;
import com.example.allakumarreddy.moneybook.Activities.RePaymentActivity;
import com.example.allakumarreddy.moneybook.Adapter.MyAdapter;
import com.example.allakumarreddy.moneybook.R;
import com.example.allakumarreddy.moneybook.interfaces.HomeAdapterInterface;
import com.example.allakumarreddy.moneybook.storage.db.DbHandler;
import com.example.allakumarreddy.moneybook.utils.GlobalConstants;
import com.example.allakumarreddy.moneybook.utils.MBRecord;
import com.example.allakumarreddy.moneybook.utils.Utils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import static com.example.allakumarreddy.moneybook.utils.AnimationUtils.revealAnimation;


public class HomeInnerFragment extends Fragment {

    private static final String TAG = HomeInnerFragment.class.getSimpleName();
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
    int mType = -1;
    private View mainView;
    private View progress;
    private View noData;

    public HomeInnerFragment() {
        // Required empty public constructor
    }

    public static HomeInnerFragment newInstance(int type) {
        HomeInnerFragment fragment = new HomeInnerFragment();
        Bundle args = new Bundle();
        args.putInt(GlobalConstants.FRAGMENT_ACTIVATE_TYPE, type);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        readBundle(getArguments());
        Date curDate = new Date();
        format = new SimpleDateFormat("yyyy/MM/dd");
        curDateStr = format.format(curDate);
        mDbHandler = new DbHandler(getContext());
        mbr = new ArrayList<>();
        mAdapter = new MyAdapter(mbr, getContext(), mType, mHomeAdapterInterface);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View layout = inflater.inflate(R.layout.fragment_home_inner, container, false);
        mainView = layout.findViewById(R.id.mainView);
        mainView.setVisibility(View.GONE);
        progress = layout.findViewById(R.id.progress);
        progress.setVisibility(View.VISIBLE);

        noData = layout.findViewById(R.id.no_data);
        mListView = layout.findViewById(R.id.lv);
        mListView.setAdapter(mAdapter);
        mTotalTextView = layout.findViewById(R.id.total_tv);
        TextView dateTv = (TextView) layout.findViewById(R.id.date_text);
        dateTv.setText(new SimpleDateFormat("dd MMM yyyy").format(new Date()));
        dateTv.setTextColor(Utils.getColor(mType, getContext()));
        return layout;
    }

    private void readBundle(Bundle args) {
        if (args != null) {
            mType = args.getInt(GlobalConstants.FRAGMENT_ACTIVATE_TYPE);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        updataUI();
    }

    public void updataUI() {
        new Thread(() -> {
            mbr = mDbHandler.getRecords(curDateStr, mType);
            getActivity().runOnUiThread(() -> {
                mAdapter.clear();
                mAdapter.addAll(mbr);
                mAdapter.notifyDataSetChanged();
                mTotalTextView.setText(Utils.getFormattedNumber(getTotalAmount()));
                if (mAdapter.getCount() <= 0) {
                    noData.setVisibility(View.VISIBLE);
                    mListView.setVisibility(View.GONE);
                } else {
                    noData.setVisibility(View.GONE);
                    mListView.setVisibility(View.VISIBLE);
                }
                showMainView();
            });
        }).start();
    }

    private void showMainView() {
        revealAnimation(progress, mainView, getView());
    }

    private int getTotalAmount() {
        int total = 0;
        for (MBRecord n : mbr)
            total += n.getAmount();
        return total;
    }

    public void startDetailActivity(MBRecord mbr) {
        if (mType == GlobalConstants.TYPE_DUE || mType == GlobalConstants.TYPE_LOAN) {
            Intent intent = new Intent(getActivity(), RePaymentActivity.class);
            intent.putExtra("MBRecord", mbr);
            startActivity(intent);
        } else {
            Intent intent = new Intent(getActivity(), AnalyticsItemDetail.class);
            mbr.setType(mType);
            intent.putExtra("MBRecord", mbr);
            startActivity(intent);
        }
    }

}
