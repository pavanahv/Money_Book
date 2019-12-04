package com.example.allakumarreddy.moneybook.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.example.allakumarreddy.moneybook.Activities.AnalyticsActivity;
import com.example.allakumarreddy.moneybook.Adapter.DashBoardAdapter;
import com.example.allakumarreddy.moneybook.interfaces.DashBoardAdapterInterface;
import com.example.allakumarreddy.moneybook.R;
import com.example.allakumarreddy.moneybook.storage.db.DbHandler;
import com.example.allakumarreddy.moneybook.utils.DashBoardRecord;
import com.example.allakumarreddy.moneybook.utils.GlobalConstants;
import com.example.allakumarreddy.moneybook.utils.Utils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import static com.example.allakumarreddy.moneybook.utils.AnimationUtils.revealAnimation;

public class DashBoardInnerFragment extends Fragment implements DashBoardAdapterInterface {

    private static final String TAG = DashBoardInnerFragment.class.getSimpleName();
    private ListView dashBoardList;
    private TextView totalD;
    private TextView totalM;
    private TextView totalY;
    private ArrayList<DashBoardRecord> dbr;
    private DashBoardAdapter dashBoardAdapter;
    private TextView dataD;
    private TextView dateM;
    private TextView dateY;
    private DbHandler db;
    private View mainView;
    private View progress;
    private int mType = -1;

    public DashBoardInnerFragment() {

    }

    public static DashBoardInnerFragment newInstance(int type) {
        DashBoardInnerFragment fragment = new DashBoardInnerFragment();
        Bundle args = new Bundle();
        args.putInt(GlobalConstants.FRAGMENT_ACTIVATE_TYPE, type);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = new DbHandler(getContext());
        setHasOptionsMenu(true);
        readBundle(getArguments());
    }

    private void readBundle(Bundle args) {
        mType = args.getInt(GlobalConstants.FRAGMENT_ACTIVATE_TYPE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_dashboard_inner, container, false);
        init(view);
        return view;
    }

    private void init(View view) {
        mainView = view.findViewById(R.id.mainView);
        mainView.setVisibility(View.GONE);
        progress = view.findViewById(R.id.progress);
        progress.setVisibility(View.VISIBLE);

        dashBoardList = (ListView) view.findViewById(R.id.dashboardlist);

        totalD = (TextView) view.findViewById(R.id.dashboardtotald);
        totalM = (TextView) view.findViewById(R.id.dashboardtotalm);
        totalY = (TextView) view.findViewById(R.id.dashboardtotaly);

        dataD = ((TextView) view.findViewById(R.id.dashday));
        dateM = ((TextView) view.findViewById(R.id.dashmonth));
        dateY = ((TextView) view.findViewById(R.id.dashyear));
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initViewData();
    }

    private void initViewData() {
        dataD.setText(new SimpleDateFormat("dd").format(new Date()));
        dateM.setText(new SimpleDateFormat("MMM").format(new Date()));
        dateY.setText(new SimpleDateFormat("yyyy").format(new Date()));

        dbr = new ArrayList<>();
        dashBoardAdapter = new DashBoardAdapter(dbr, getContext(), this, mType);
        dashBoardList.setAdapter(dashBoardAdapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        dashBoardUIData();
    }

    public void dashBoardUIData() {
        new Thread(() -> {
            String dayCountTotalHeadText = Utils.getFormattedNumber(db.getTotalMoneyInCurrentDay(mType));
            String monthCountTotalHeadText = Utils.getFormattedNumber(db.getTotalMoneyInCurrentMonth(mType));
            String yearCountTotalHeadText = Utils.getFormattedNumber(db.getTotalMoneyInCurrentYear(mType));
            dbr = db.getDashBoardRecords(mType);
            getActivity().runOnUiThread(() -> {
                totalD.setText(dayCountTotalHeadText);
                totalM.setText(monthCountTotalHeadText);
                totalY.setText(yearCountTotalHeadText);
                dashBoardAdapter.clear();
                dashBoardAdapter.addAll(dbr);
                dashBoardAdapter.notifyDataSetChanged();
                showMainView();
            });
        }).start();
    }

    private void showMainView() {
        revealAnimation(progress, mainView, getView());
    }

    @Override
    public void viewOnClick(String dataText) {
        Intent intent = new Intent(getContext(), AnalyticsActivity.class);
        intent.putExtra("name", dataText);
        intent.putExtra(GlobalConstants.CATEGORY_TYPE, mType);
        startActivity(intent);
    }

    @Override
    public void deleteCategoryPopMenu(String categoryName) {
        db.deleteCategory(categoryName.toLowerCase());
        dashBoardUIData();
    }

    @Override
    public void moneyTrasferPopMenu(String categoryName) {
        // no need to implement
    }

}