package com.example.allakumarreddy.moneybook.Activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
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
import com.example.allakumarreddy.moneybook.db.DbHandler;
import com.example.allakumarreddy.moneybook.storage.XLStore;
import com.example.allakumarreddy.moneybook.utils.AnalyticsFilterData;
import com.example.allakumarreddy.moneybook.utils.GlobalConstants;
import com.example.allakumarreddy.moneybook.utils.LoggerCus;
import com.example.allakumarreddy.moneybook.utils.MBRecord;
import com.example.allakumarreddy.moneybook.utils.Utils;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;

public class AnalyticsActivity extends AppCompatActivity {

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_analytics);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mAnalyticsFilterData = new AnalyticsFilterData();
        main = (LinearLayout) findViewById(R.id.analyticsview);
        db = new DbHandler(this);
        format = new SimpleDateFormat("yyyy/MM/dd");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
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
                        initFilters();
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
                break;
            case R.id.save_filter:
                Intent lIntent = new Intent(AnalyticsActivity.this, AddActivity.class);
                lIntent.putExtra("type", 3);
                startActivityForResult(lIntent, ADD_ACTIVITY);
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

    public void startDetailActivity(int pos) {
        if (mStartItemDetailActivity) {
            MBRecord mbr = dataList.get(pos);
            Intent intent = new Intent(AnalyticsActivity.this, AnalyticsItemDetail.class);
            intent.putExtra("MBRecord", mbr);
            startActivity(intent);
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
    }

    public void processTabs() {
        LayoutInflater inflater = LayoutInflater.from(this);
        View view = inflater.inflate(R.layout.item, null);
        main.removeView(prevView);
        prevView = view;
        main.addView(view);
        ListView analyticsListView = (ListView) view.findViewById(R.id.analyticslv);
        searchView = (SearchView) view.findViewById(R.id.sv);
        list = new ArrayList<>();
        dataList = (ArrayList<MBRecord>) list.clone();
        analyticsAdapter = new AnalyticsAdapter(list, this);
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
        // categories initialization
        String[] cat = db.getCategeories();
        mAnalyticsFilterData.subMenuCatogeoryData = new String[cat.length + 1];
        mAnalyticsFilterData.subMenuCatogeoryData[0] = "All";
        for (int i = 1; i < mAnalyticsFilterData.subMenuCatogeoryData.length; i++)
            mAnalyticsFilterData.subMenuCatogeoryData[i] = cat[i - 1];

        mAnalyticsFilterData.subMenuCatogeoryDataBool = new boolean[mAnalyticsFilterData.subMenuCatogeoryData.length];

        Intent intent = getIntent();
        String tempCat = intent.getStringExtra("name");
        if (tempCat != null) {
            Arrays.fill(mAnalyticsFilterData.subMenuCatogeoryDataBool, false);
            for (int i = 1; i < mAnalyticsFilterData.subMenuCatogeoryData.length; i++) {
                if (mAnalyticsFilterData.subMenuCatogeoryData[i].compareToIgnoreCase(tempCat) == 0) {
                    mAnalyticsFilterData.subMenuCatogeoryDataBool[i] = true;
                    break;
                }
            }
        } else {
            Arrays.fill(mAnalyticsFilterData.subMenuCatogeoryDataBool, true);
        }

        initFilters();
    }

    private void initFilters() {
        mAnalyticsFilterData.filters = db.getFilterRecords();
        mAnalyticsFilterData.subMenuFilterData = new String[mAnalyticsFilterData.filters.length + 1];
        mAnalyticsFilterData.subMenuFilterData[0] = "None";
        for (int i = 0; i < mAnalyticsFilterData.subMenuFilterData.length - 1; i++) {
            mAnalyticsFilterData.subMenuFilterData[i + 1] = mAnalyticsFilterData.filters[i][0];
        }
        mAnalyticsFilterData.subMenuFilterDataBool = new boolean[mAnalyticsFilterData.subMenuFilterData.length];
        Arrays.fill(mAnalyticsFilterData.subMenuFilterDataBool, false);
        mAnalyticsFilterData.subMenuFilterDataBool[0] = true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateData();
    }

    private void updateData() {
        list.clear();
        dataList = db.getRecordsAsList(mAnalyticsFilterData);
        list.addAll(dataList);
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
        if (mAnalyticsFilterData.subMenuGroupByDataBool[2])
            mStartItemDetailActivity = true;
        else
            mStartItemDetailActivity = false;
    }

    private void upDateTotal() {
        int value = db.getTotal();
        totalTv.setText(Utils.getFormattedNumber(value));
    }
}
