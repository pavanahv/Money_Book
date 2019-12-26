package com.pavanahv.allakumarreddy.moneybook.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.pavanahv.allakumarreddy.moneybook.Activities.ReportGraphDetailActivity;
import com.pavanahv.allakumarreddy.moneybook.Adapter.ReportsGraphAdapter;
import com.pavanahv.allakumarreddy.moneybook.R;
import com.pavanahv.allakumarreddy.moneybook.storage.db.DbHandler;
import com.pavanahv.allakumarreddy.moneybook.utils.AnalyticsFilterData;
import com.pavanahv.allakumarreddy.moneybook.utils.GlobalConstants;
import com.pavanahv.allakumarreddy.moneybook.utils.GraphUtils;
import com.pavanahv.allakumarreddy.moneybook.utils.MBRecord;
import com.pavanahv.allakumarreddy.moneybook.utils.ReportData;
import com.pavanahv.allakumarreddy.moneybook.utils.Utils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import static com.pavanahv.allakumarreddy.moneybook.utils.AnimationUtils.revealAnimation;
import static com.pavanahv.allakumarreddy.moneybook.utils.FilterUtils.initCategories;
import static com.pavanahv.allakumarreddy.moneybook.utils.FilterUtils.initFilters;
import static com.pavanahv.allakumarreddy.moneybook.utils.FilterUtils.initPaymentMethods;
import static com.pavanahv.allakumarreddy.moneybook.utils.FilterUtils.initType;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ReportsInnerFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ReportsInnerFragment extends Fragment {

    private static final String ACTIVATE_TYPE = "ACTIVATE_TYPE";
    private static final int DAY = 0;
    private static final int MONTH = 1;
    private static final int YEAR = 2;
    private int type;
    private ArrayList<ReportData> list;
    private ReportsGraphAdapter mReportsGraphAdapter;
    private AnalyticsFilterData mAnalyticsFilterData;
    private DbHandler db;
    private View mainView;
    private View progress;
    private String day;
    private String month;
    private String year;
    private ListView listView;
    private View noData;
    private ArrayList<String> mTitleList;
    private ArrayList<String[]> labelList;
    private ArrayList<String[]> dataList;
    private ArrayList<AnalyticsFilterData> mAnalyticsFilterDataList;


    public ReportsInnerFragment() {
        // Required empty public constructor
    }

    public static ReportsInnerFragment newInstance(int type) {
        ReportsInnerFragment fragment = new ReportsInnerFragment();
        Bundle args = new Bundle();
        args.putInt(ACTIVATE_TYPE, type);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            this.type = getArguments().getInt(ACTIVATE_TYPE);
        }
        db = new DbHandler(getContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_reports_inner, container, false);
        init(root);
        return root;
    }

    private void init(View root) {
        mainView = root.findViewById(R.id.main_layout);
        mainView.setVisibility(View.GONE);
        progress = root.findViewById(R.id.progress);
        progress.setVisibility(View.VISIBLE);

        noData = root.findViewById(R.id.no_data);
        noData.setVisibility(View.GONE);

        listView = root.findViewById(R.id.graph_list);
        list = new ArrayList<>();
        mReportsGraphAdapter = new ReportsGraphAdapter(list, getContext(),
                (position, childItem, item) -> {
                    itemClicked(position, childItem, item);
                });
        listView.setAdapter(mReportsGraphAdapter);
    }

    private void itemClicked(int position, View childItem, ReportData item) {
        Intent intent = new Intent(getActivity(), ReportGraphDetailActivity.class);
        intent.putExtra("title", item.getTitle());
        intent.putExtra("isPie", item.isPie());
        if (item.isPie()) {
            intent.putExtra("AnalyticsFilterData", item.getmAnalyticsFilterData());
        } else {
            ArrayList<AnalyticsFilterData> tempList = item.getAnalyticsFilterDataArrayList();
            for (int i = 0; i < 4; i++) {
                intent.putExtra("AnalyticsFilterData" + i, tempList.get(i));
            }
        }
        startActivity(intent);
        getActivity().overridePendingTransition(R.anim.move_right_in_activity, R.anim.move_left_out_activity);
    }

    private void showMainView() {
        revealAnimation(progress, mainView, getView());
    }

    @Override
    public void onResume() {
        super.onResume();
        updateUI();
    }

    private void updateUI() {

        new Thread(new Runnable() {
            @Override
            public void run() {
                day = new SimpleDateFormat("dd MMM yyyy").format(new Date());
                month = new SimpleDateFormat("MMM yyyy").format(new Date());
                year = new SimpleDateFormat("yyyy").format(new Date());
                mTitleList = new ArrayList<>();
                mAnalyticsFilterDataList = new ArrayList<>();
                labelList = new ArrayList<>();
                dataList = new ArrayList<>();

                switch (type) {
                    case 0:
                        init(DAY);
                        break;
                    case 1:
                        init(MONTH);
                        break;
                    case 2:
                        init(YEAR);
                        break;
                    default:
                        break;
                }
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        addViews();
                        mReportsGraphAdapter.notifyDataSetChanged();
                        if (list.size() <= 0) {
                            listView.setVisibility(View.GONE);
                            noData.setVisibility(View.VISIBLE);
                        } else {
                            listView.setVisibility(View.VISIBLE);
                            noData.setVisibility(View.GONE);
                        }
                        showMainView();
                    }
                });
            }
        }).start();
    }

    private void init(int type) {
        spent(type);
        earn(type);
        due(type);
        loan(type);
        paymentMethod(type);
        if (type != DAY)
            lineGraph(type);
    }

    private void lineGraph(int type) {
        ArrayList<String[]> labels = new ArrayList<>(4);
        ArrayList<String[]> datas = new ArrayList<>(4);
        ArrayList<AnalyticsFilterData> analyticsFilterData = new ArrayList<>(4);

        int types[] = new int[]{GlobalConstants.TYPE_SPENT,
                GlobalConstants.TYPE_EARN,
                GlobalConstants.TYPE_DUE,
                GlobalConstants.TYPE_LOAN};

        for (int i = 0; i < 4; i++) {
            mAnalyticsFilterData = new AnalyticsFilterData();
            initPaymentMethods(null, db, mAnalyticsFilterData);
            initFilters(db, mAnalyticsFilterData);
            initType(mAnalyticsFilterData, types[i]);

            // making date as required and none to false
            mAnalyticsFilterData.subMenuDateDataBool[type] = true;
            mAnalyticsFilterData.subMenuDateDataBool[4] = false;

            initCategories(null, db, mAnalyticsFilterData, types[i]);
            // making group by filter with date as true
            mAnalyticsFilterData.subMenuGroupByDataBool[1] = true;
            mAnalyticsFilterData.subMenuGroupByDataBool[4] = false;

            // making date interval with other than day as true
            mAnalyticsFilterData.subMenuDateIntervalDataBool[type - 1] = true;
            if (type == YEAR) {
                // making date interval with day as false
                mAnalyticsFilterData.subMenuDateIntervalDataBool[0] = false;
            }

            // making sorting order increasing
            mAnalyticsFilterData.subMenuSortingOrderDataBool[0] = true;
            mAnalyticsFilterData.subMenuSortingOrderDataBool[1] = false;

            ArrayList<MBRecord> mbrList = db.getRecordsAsList(mAnalyticsFilterData);
            final int size = mbrList.size();
            String[] label = new String[size];
            String[] data = new String[size];
            for (int j = 0; j < size; j++) {
                MBRecord mbr = mbrList.get(j);
                label[j] = mbr.getDescription();
                data[j] = mbr.getAmount() + "";
            }

            labels.add(label);
            datas.add(data);
            analyticsFilterData.add(mAnalyticsFilterData);
        }

        Utils.parseToGraphData(datas, labels);
        String title = "Date Wise Analysis For " + getDateStr(type);
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                View view = (View) GraphUtils.drawLineGraph(labels, datas, getContext(), title);
                ReportData rp = new ReportData(view, title, null);
                rp.setPie(false);
                rp.setAnalyticsFilterDataArrayList(analyticsFilterData);
                list.add(rp);
            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
        mReportsGraphAdapter.clear();
        mReportsGraphAdapter.notifyDataSetChanged();
    }

    private void paymentMethod(int type) {
        initObj(type, false);
        initType(mAnalyticsFilterData, GlobalConstants.TYPE_SPENT);
        initCategories(null, db, mAnalyticsFilterData, GlobalConstants.TYPE_SPENT);
        update("Usage Of Payment Methods For " + getDateStr(type));
    }

    private void earn(int type) {
        initObj(type, true);
        // making type as earn
        initType(mAnalyticsFilterData, GlobalConstants.TYPE_EARN);
        initCategories(null, db, mAnalyticsFilterData, GlobalConstants.TYPE_EARN);
        update("Income For " + getDateStr(type));
    }

    private void due(int type) {
        initObj(type, true);
        // making type as Due
        initType(mAnalyticsFilterData, GlobalConstants.TYPE_DUE);
        initCategories(null, db, mAnalyticsFilterData, GlobalConstants.TYPE_DUE);
        update("Dues For " + getDateStr(type));
    }

    private void loan(int type) {
        initObj(type, true);
        // making type as loan
        initType(mAnalyticsFilterData, GlobalConstants.TYPE_LOAN);
        initCategories(null, db, mAnalyticsFilterData, GlobalConstants.TYPE_LOAN);
        update("Loans For " + getDateStr(type));
    }

    private void spent(int type) {
        initObj(type, true);
        // making type as spent
        initType(mAnalyticsFilterData, GlobalConstants.TYPE_SPENT);
        initCategories(null, db, mAnalyticsFilterData, GlobalConstants.TYPE_SPENT);
        update("Expenses For " + getDateStr(type));
    }

    private String getDateStr(int type) {
        switch (type) {
            case DAY:
                return day;
            case MONTH:
                return month;
            case YEAR:
                return year;
            default:
                return "";
        }
    }

    private void initObj(int type, boolean isCat) {
        mAnalyticsFilterData = new AnalyticsFilterData();
        initPaymentMethods(null, db, mAnalyticsFilterData);
        initFilters(db, mAnalyticsFilterData);

        setGroupBy(isCat);

        switch (type) {
            case DAY:
                // making date as "current day"
                mAnalyticsFilterData.subMenuDateDataBool[0] = true;
                break;
            case MONTH:
                // making date as "current month"
                mAnalyticsFilterData.subMenuDateDataBool[1] = true;
                break;
            case YEAR:
                // making date as "current year"
                mAnalyticsFilterData.subMenuDateDataBool[2] = true;
                break;
            default:
                break;
        }
        // making date all as false
        mAnalyticsFilterData.subMenuDateDataBool[4] = false;
    }

    private void setGroupBy(boolean isCat) {
        if (isCat) {
            // making group by filter with categories true
            mAnalyticsFilterData.subMenuGroupByDataBool[2] = true;
        } else {
            // making group by filter with payment method true
            mAnalyticsFilterData.subMenuGroupByDataBool[3] = true;
        }
        // making group by filter with none as false
        mAnalyticsFilterData.subMenuGroupByDataBool[4] = false;
    }

    private void update(String title) {
        mTitleList.add(title);
        mAnalyticsFilterDataList.add(mAnalyticsFilterData);

        ArrayList<MBRecord> mbrList = db.getRecordsAsList(mAnalyticsFilterData);
        final int size = mbrList.size();
        String[] label = new String[size];
        String[] data = new String[size];
        for (int i = 0; i < size; i++) {
            MBRecord mbr = mbrList.get(i);
            label[i] = mbr.getDescription();
            data[i] = mbr.getAmount() + "";
        }

        labelList.add(label);
        dataList.add(data);
    }

    private void addViews() {
        final int len = labelList.size();
        for (int j = 0; j < len; j++) {
            AnalyticsFilterData tempAnalyticsFilterData = mAnalyticsFilterDataList.get(j);
            String title = mTitleList.get(j);
            String label[] = labelList.get(j);
            String[] data = dataList.get(j);
            int size = label.length;
            if (size > 0) {
                View view = (View) GraphUtils.drawPieGraph(size, label, data, getContext(), title);
                ReportData rd = new ReportData(view, title, tempAnalyticsFilterData);
                rd.setPie(true);
                list.add(rd);
            }
        }
    }

}