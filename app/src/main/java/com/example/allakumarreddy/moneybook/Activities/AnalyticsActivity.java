package com.example.allakumarreddy.moneybook.Activities;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.allakumarreddy.moneybook.Adapter.AnalyticsAdapter;
import com.example.allakumarreddy.moneybook.R;
import com.example.allakumarreddy.moneybook.storage.XLStore;
import com.example.allakumarreddy.moneybook.storage.db.DbHandler;
import com.example.allakumarreddy.moneybook.utils.AnalyticsFilterData;
import com.example.allakumarreddy.moneybook.utils.GlobalConstants;
import com.example.allakumarreddy.moneybook.utils.LoggerCus;
import com.example.allakumarreddy.moneybook.utils.MBRecord;
import com.example.allakumarreddy.moneybook.utils.Utils;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import static com.example.allakumarreddy.moneybook.utils.FilterUtils.initCategories;
import static com.example.allakumarreddy.moneybook.utils.FilterUtils.initFilters;
import static com.example.allakumarreddy.moneybook.utils.FilterUtils.initPaymentMethods;
import static com.example.allakumarreddy.moneybook.utils.FilterUtils.initType;

public class AnalyticsActivity extends BaseActivity {

    private static final String TAG = "AnalyticsActivity";
    private static final int ANALYTICS_FILTER_ACTIVITY = 1001;
    private static final int ADD_ACTIVITY = 1002;
    private LinearLayout main;
    private View prevView;
    private DbHandler db;
    private ArrayList<MBRecord> list;
    private SimpleDateFormat format;
    private AnalyticsAdapter analyticsAdapter;
    private TextView totalTv;
    private SearchView searchView;
    private ArrayList<MBRecord> dataList;
    private int graphType = 0;
    private AnalyticsFilterData mAnalyticsFilterData;
    private boolean mStartItemDetailActivity = true;
    private ListView analyticsListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_analytics);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        main = (LinearLayout) findViewById(R.id.analyticsview);
        db = new DbHandler(this);
        format = new SimpleDateFormat("yyyy/MM/dd");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Analytics");
        processTabs();
        init();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case (ANALYTICS_FILTER_ACTIVITY):
                if (resultCode == Activity.RESULT_OK) {
                    mAnalyticsFilterData = (AnalyticsFilterData) data.getSerializableExtra(GlobalConstants.ANALYTICS_FILTER_ACTIVITY);
                    break;
                }
                break;
            case ADD_ACTIVITY:
                if (resultCode == Activity.RESULT_OK) {
                    String name = data.getExtras().getString("fdes");
                    String jsonStrFiltr = mAnalyticsFilterData.getParcelableJSONStringForFilter();
                    boolean res = db.insertFilterRecord(name, jsonStrFiltr, false);
                    if (res) {
                        initFilters(db, mAnalyticsFilterData);
                        Toast.makeText(this, "Filter Saved Successfully!", Toast.LENGTH_LONG).show();
                    }
                }
                break;
        }
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.analytics_filter:
                Intent intent = new Intent(this, FiltersAnalyticsActivity.class);
                intent.putExtra(GlobalConstants.ANALYTICS_FILTER_ACTIVITY, mAnalyticsFilterData);
                startActivityForResult(intent, ANALYTICS_FILTER_ACTIVITY);
                overridePendingTransition(R.anim.move_right_in_activity, R.anim.move_left_out_activity);
                break;
            case R.id.save_filter:
                Intent lIntent = new Intent(AnalyticsActivity.this, AddActivity.class);
                lIntent.putExtra("type", GlobalConstants.SAVE_FILTER_SCREEN);
                startActivityForResult(lIntent, ADD_ACTIVITY);
                overridePendingTransition(R.anim.move_right_in_activity, R.anim.move_left_out_activity);
                break;

            case R.id.analytics_save_xl:
                try {
                    new XLStore("MoneyBook", dataList);
                    Toast.makeText(AnalyticsActivity.this, "Saved!", Toast.LENGTH_SHORT).show();
                } catch (IOException e) {
                    LoggerCus.d("analyticsactivity", e.getMessage());
                    Toast.makeText(AnalyticsActivity.this, "Error in Saving ! " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
                break;

            case android.R.id.home:
                finish();
                overridePendingTransition(R.anim.move_left_in_activity, R.anim.move_right_out_activity);
                break;

            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.analytics_menu, menu);
        return true;
    }

    public void startDetailActivity(int pos, View view) {
        if (mStartItemDetailActivity) {
            MBRecord mbr = dataList.get(pos);
            LoggerCus.d(TAG, mbr.toString());
            Intent intent = null;
            if (mbr.getType() == GlobalConstants.TYPE_DUE ||
                    mbr.getType() == GlobalConstants.TYPE_LOAN ||
                    mbr.getType() == GlobalConstants.TYPE_DUE_PAYMENT ||
                    mbr.getType() == GlobalConstants.TYPE_LOAN_PAYMENT) {
                intent = new Intent(AnalyticsActivity.this, RePaymentActivity.class);
                intent.putExtra("MBRecord", mbr);
            } else {
                intent = new Intent(AnalyticsActivity.this, AnalyticsItemDetail.class);
                intent.putExtra("MBRecord", mbr);
            }
            startActivity(intent,
                    ActivityOptions.makeSceneTransitionAnimation(this, view,
                            getResources().getString(R.string.shared_anim_analytics_item)).toBundle());
        } else {
            Toast.makeText(this, "Record can't be edited when GROUPBY filter is selected", Toast.LENGTH_LONG).show();
        }
    }

    private void showGraph() {
        // reset to view list after returning from graph activity
        mAnalyticsFilterData.subMenuViewByDataBool[0] = false;
        mAnalyticsFilterData.subMenuViewByDataBool[1] = true;

        final int size = dataList.size();
        String[] label = new String[size];
        String[] data = new String[size];
        for (int i = 0; i < size; i++) {
            MBRecord mbr = dataList.get(i);
            label[i] = mbr.getDescription();
            data[i] = mbr.getAmount() + "";
        }
        String jsonStrFiltr = mAnalyticsFilterData.getParcelableJSONStringForFilter();

        Intent in = new Intent(this, GraphActivity.class);
        in.putExtra("label", label);
        in.putExtra("data", data);
        in.putExtra("type", graphType);
        in.putExtra("jsonStrFiltr", jsonStrFiltr);
        in.putExtra("activateType", GlobalConstants.ACTIVATE_GRAPH_ACTIVITY_WITH_ADD_TO_SCREEN_MENU);
        startActivity(in);
        overridePendingTransition(R.anim.move_right_in_activity, R.anim.move_left_out_activity);
    }

    public void processTabs() {
        LayoutInflater inflater = LayoutInflater.from(this);
        View view = inflater.inflate(R.layout.item, null);
        main.removeView(prevView);
        prevView = view;
        main.addView(view);
        analyticsListView = (ListView) view.findViewById(R.id.analyticslv);
        searchView = (SearchView) view.findViewById(R.id.sv);
        list = new ArrayList<>();
        dataList = (ArrayList<MBRecord>) list.clone();
        analyticsAdapter = new AnalyticsAdapter(list, this, (pos, v) -> AnalyticsActivity.this.startDetailActivity(pos, v));
        analyticsListView.setAdapter(analyticsAdapter);
        totalTv = (TextView) view.findViewById(R.id.total);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String qText) {
                mAnalyticsFilterData.queryText = qText;
                updateData();
                return false;
            }
        });
    }

    private void init() {
        mAnalyticsFilterData = new AnalyticsFilterData();
        Intent intent = getIntent();
        String temp = intent.getStringExtra("name");
        int catType = intent.getIntExtra(GlobalConstants.CATEGORY_TYPE, -1);
        initType(mAnalyticsFilterData, catType);
        initCategories(temp, db, mAnalyticsFilterData, catType);
        temp = intent.getStringExtra("paymentMethod");
        initPaymentMethods(temp, db, mAnalyticsFilterData);
        initFilters(db, mAnalyticsFilterData);
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateData();
    }

    private void updateData() {
        if (mAnalyticsFilterData.subMenuGroupByDataBool[4])
            mStartItemDetailActivity = true;
        else
            mStartItemDetailActivity = false;
        list.clear();
        dataList = db.getRecordsAsList(mAnalyticsFilterData);
        list.addAll(dataList);
        analyticsAdapter.setGroupBy(!mStartItemDetailActivity);
        analyticsAdapter.notifyDataSetChanged();
        upDateTotal();
        if (mAnalyticsFilterData.subMenuViewByDataBool[0]) {
            for (int i = 0; i < mAnalyticsFilterData.subMenuGraphTypeDataBool.length; i++) {
                if (mAnalyticsFilterData.subMenuGraphTypeDataBool[i]) {
                    graphType = i;
                    break;
                }
            }
            showGraph();
        }
    }

    private void upDateTotal() {
        int value = db.getTotal();
        totalTv.setText(Utils.getFormattedNumber(value));
    }
}
