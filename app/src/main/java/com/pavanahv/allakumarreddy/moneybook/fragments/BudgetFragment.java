package com.pavanahv.allakumarreddy.moneybook.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.pavanahv.allakumarreddy.moneybook.Activities.BudgetAddActivity;
import com.pavanahv.allakumarreddy.moneybook.Activities.BudgetDetailActivity;
import com.pavanahv.allakumarreddy.moneybook.Adapter.BudgetAdapter;
import com.pavanahv.allakumarreddy.moneybook.R;
import com.pavanahv.allakumarreddy.moneybook.interfaces.BudgetAdapterInterface;
import com.pavanahv.allakumarreddy.moneybook.storage.db.DbHandler;
import com.pavanahv.allakumarreddy.moneybook.utils.AnalyticsFilterData;
import com.pavanahv.allakumarreddy.moneybook.utils.GlobalConstants;
import com.pavanahv.allakumarreddy.moneybook.utils.LoggerCus;
import com.pavanahv.allakumarreddy.moneybook.utils.MBRecord;
import com.pavanahv.allakumarreddy.moneybook.utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import static com.pavanahv.allakumarreddy.moneybook.utils.AnimationUtils.revealAnimation;
import static com.pavanahv.allakumarreddy.moneybook.utils.FilterUtils.initCategories;
import static com.pavanahv.allakumarreddy.moneybook.utils.FilterUtils.initFilters;
import static com.pavanahv.allakumarreddy.moneybook.utils.FilterUtils.initPaymentMethods;
import static com.pavanahv.allakumarreddy.moneybook.utils.FilterUtils.initType;


public class BudgetFragment extends Fragment {

    private static final String TAG = BudgetFragment.class.getSimpleName();
    private View mainView;
    private View progress;
    private ListView mListView;
    private ArrayList<HashMap<String, String>> list;
    private BudgetAdapter mBudgetAdapter;
    private DbHandler db;
    private SimpleDateFormat sdf;
    private View noData;

    public BudgetFragment() {
        // Required empty public constructor
    }

    public static BudgetFragment newInstance(String param1, String param2) {
        BudgetFragment fragment = new BudgetFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = new DbHandler(getContext());
        sdf = new SimpleDateFormat("dd/MM/yyyy");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_budget, container, false);
        initViews(root);
        FloatingActionButton fab = getActivity().findViewById(R.id.fab);
        fab.setVisibility(View.VISIBLE);
        fab.setOnClickListener(view1 -> startAddActivity());
        return root;
    }

    private void startAddActivity() {
        startActivity(new Intent(getContext(), BudgetAddActivity.class));
        getActivity().overridePendingTransition(R.anim.move_right_in_activity, R.anim.move_left_out_activity);
    }

    private void showMainView() {
        revealAnimation(progress, mainView, getView());
    }

    private void initViews(View root) {
        mainView = root.findViewById(R.id.main_view);
        progress = root.findViewById(R.id.progress);
        mainView.setVisibility(View.GONE);
        progress.setVisibility(View.VISIBLE);

        mListView = root.findViewById(R.id.lv);
        list = new ArrayList<>();
        mBudgetAdapter = new BudgetAdapter(list, getContext(), new BudgetAdapterInterface() {
            @Override
            public void onClickItem(String name) {
                startDetailActivity(name);
            }
        });
        mListView.setAdapter(mBudgetAdapter);
        noData = root.findViewById(R.id.no_data);
        noData.setVisibility(View.GONE);
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
                list = db.getBudgetRecordsAsList();
                ArrayList<HashMap<String, String>> tempList = new ArrayList<>();
                for (HashMap<String, String> item : list) {
                    HashMap<String, String> map = new HashMap<>();
                    map.put("name", item.get("name"));
                    int limit = Integer.parseInt(item.get("limit"));
                    map.put("limit", "" + limit);
                    int interval = Integer.parseInt(item.get("interval"));
                    map.put("interval", GlobalConstants.BUDGET_INTERVAL[interval]);

                    String tempDate = "";
                    String avg = "";
                    AnalyticsFilterData analyticsFilterData = new AnalyticsFilterData();
                    initType(analyticsFilterData, GlobalConstants.TYPE_SPENT);
                    initCategories(null, db, analyticsFilterData, GlobalConstants.TYPE_SPENT);
                    initPaymentMethods(null, db, analyticsFilterData);
                    initFilters(db, analyticsFilterData);
                    JSONArray arr = null;
                    try {
                        arr = new JSONArray(item.get("cat"));
                    } catch (JSONException e) {
                        LoggerCus.d(TAG, "Error while parsing json array of cat -> " + e.getMessage());
                    }
                    Date sDate = new Date();
                    Date eDate = new Date();
                    switch (interval) {
                        case 0:
                            // making current day to true
                            Arrays.fill(analyticsFilterData.subMenuDateDataBool, false);
                            analyticsFilterData.subMenuDateDataBool[0] = true;

                            // date for current day
                            tempDate = sdf.format(new Date());
                            tempDate = tempDate + " - " + tempDate;
                            break;
                        case 1:
                            // making current month to true
                            Arrays.fill(analyticsFilterData.subMenuDateDataBool, false);
                            analyticsFilterData.subMenuDateDataBool[1] = true;

                            // date for current month
                            sDate = Utils.intializeSDateForMonth(sDate);
                            eDate = Utils.intializeEDateForMonth(eDate);
                            tempDate = sdf.format(sDate) + " - " + sdf.format(eDate);
                            break;
                        case 2:
                            // making current year to true
                            Arrays.fill(analyticsFilterData.subMenuDateDataBool, false);
                            analyticsFilterData.subMenuDateDataBool[2] = true;

                            // making date interval to month
                            Arrays.fill(analyticsFilterData.subMenuDateIntervalDataBool, false);
                            analyticsFilterData.subMenuDateIntervalDataBool[1] = true;

                            // date for current month
                            sDate = new Date();
                            eDate = new Date();
                            sDate = Utils.intializeSDateForMonth(sDate);
                            eDate = Utils.intializeEDateForMonth(eDate);
                            sDate = Utils.intializeSDateForYear(sDate);
                            eDate = Utils.intializeEDateForYear(eDate);
                            tempDate = sdf.format(sDate) + " - " + sdf.format(eDate);
                            break;
                        default:
                            break;
                    }
                    // setting group by to date
                    Arrays.fill(analyticsFilterData.subMenuGroupByDataBool, false);
                    analyticsFilterData.subMenuGroupByDataBool[1] = true;

                    // setting sorting order
                    Arrays.fill(analyticsFilterData.subMenuSortingOrderDataBool, false);
                    analyticsFilterData.subMenuSortingOrderDataBool[0] = true;

                    // setting category
                    Arrays.fill(analyticsFilterData.subMenuCatogeoryDataBool, false);
                    if (arr != null) {
                        final int catLen = arr.length();
                        for (int i = 0; i < catLen; i++) {
                            try {
                                String catName = arr.getString(i);
                                final int len = analyticsFilterData.subMenuCatogeoryData.length;
                                for (int j = 1; j < len; j++) {
                                    if (analyticsFilterData.subMenuCatogeoryData[j].compareToIgnoreCase(catName) == 0) {
                                        analyticsFilterData.subMenuCatogeoryDataBool[j] = true;
                                        break;
                                    }
                                }
                            } catch (JSONException e) {
                                LoggerCus.d(TAG, "error in getting array name -> " + e.getMessage());
                            }
                        }
                    }

                    int total = 0;
                    ArrayList<MBRecord> tempListRecs = db.getRecordsAsList(analyticsFilterData);
                    for (MBRecord mbr : tempListRecs) {
                        total += mbr.getAmount();
                    }

                    map.put("spent", Utils.getFormattedNumber(total));
                    map.put("balLeft", "" + (limit - total));
                    map.put("progress", "" + (((float) total / limit) * 100));
                    map.put("date", tempDate);
                    switch (interval) {
                        case 0:
                            avg = limit + " per Day";
                            break;

                        case 1:
                            long diff = eDate.getTime() - sDate.getTime();
                            long days = TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
                            avg = String.format("%.2f", (float) limit / days) + " per day";
                            break;
                        case 2:
                            days = 0;
                            Calendar cal = Calendar.getInstance();
                            cal.setTime(sDate);
                            int tempYear = cal.get(Calendar.YEAR);
                            while (true) {
                                int tempYearD = cal.get(Calendar.YEAR);
                                if (tempYear != tempYearD)
                                    break;
                                days++;
                                cal.add(Calendar.MONTH, 1);
                            }
                            avg = String.format("%.2f", ((float) limit / days)) + " per month";
                            break;
                    }
                    map.put("avg", avg);
                    tempList.add(map);
                }
                list.clear();
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mBudgetAdapter.clear();
                        mBudgetAdapter.addAll(tempList);
                        mBudgetAdapter.notifyDataSetChanged();
                        if (mBudgetAdapter.getCount() <= 0) {
                            noData.setVisibility(View.VISIBLE);
                            mListView.setVisibility(View.GONE);
                        } else {
                            noData.setVisibility(View.GONE);
                            mListView.setVisibility(View.VISIBLE);
                        }
                        showMainView();
                    }
                });
            }
        }).start();
    }

    private void startDetailActivity(String name) {
        Intent intent = new Intent(getContext(), BudgetDetailActivity.class);
        intent.putExtra("name", name);
        startActivity(intent);
        getActivity().overridePendingTransition(R.anim.move_right_in_activity, R.anim.move_left_out_activity);
    }

}
