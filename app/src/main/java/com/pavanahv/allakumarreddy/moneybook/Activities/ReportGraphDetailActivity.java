package com.pavanahv.allakumarreddy.moneybook.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.IPieDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.pavanahv.allakumarreddy.moneybook.Adapter.ReportsViewPagerAdapter;
import com.pavanahv.allakumarreddy.moneybook.R;
import com.pavanahv.allakumarreddy.moneybook.fragments.ReportsGraphDetailHeaderFragment;
import com.pavanahv.allakumarreddy.moneybook.fragments.ReportsGraphRecordsFragment;
import com.pavanahv.allakumarreddy.moneybook.storage.db.DbHandler;
import com.pavanahv.allakumarreddy.moneybook.utils.AnalyticsFilterData;
import com.pavanahv.allakumarreddy.moneybook.utils.GraphUtils;
import com.pavanahv.allakumarreddy.moneybook.utils.LoggerCus;
import com.pavanahv.allakumarreddy.moneybook.utils.MBRecord;
import com.pavanahv.allakumarreddy.moneybook.utils.ReportData;
import com.pavanahv.allakumarreddy.moneybook.utils.Utils;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static com.pavanahv.allakumarreddy.moneybook.utils.FilterUtils.initCategories;
import static com.pavanahv.allakumarreddy.moneybook.utils.FilterUtils.initPaymentMethods;

public class ReportGraphDetailActivity extends BaseActivity implements
        ReportsGraphDetailHeaderFragment.OnFragmentInteractionListener,
        ReportsGraphRecordsFragment.OnFragmentInteractionListener {

    private static final String TAG = ReportGraphDetailActivity.class.getSimpleName();
    private DbHandler db;
    private LinearLayout graphViewParent;
    private View noData;
    private ReportData mReportData;
    private String mTitle;
    private boolean isPie;
    private AnalyticsFilterData mAnalyticsFilterData;
    ArrayList<AnalyticsFilterData> mAnalyticsFilterDataList = new ArrayList<>();
    private PieChart pieChart;
    private LineChart lineChart;
    private ViewPager mViewPager;
    private ReportsViewPagerAdapter mReportsViewPagerAdapter;
    private ArrayList<Entry> pieEntryValues;
    private ReportsGraphRecordsFragment mReportsGraphRecordsFragment;
    private boolean isCat = false;
    private boolean pieFirst = true;
    private int ttype = -1;
    private int lineGraphCurrentPos = -1;
    private boolean lineFirst = true;
    private ArrayList<String[]> labels;
    private ArrayList<String[]> datas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_graph_detail);

        db = new DbHandler(this);
        initViews();
        initData();
    }


    private void initData() {
        Intent intent = getIntent();
        if (intent != null) {
            mTitle = intent.getStringExtra("title");
            updateActionBar();
            isPie = intent.getBooleanExtra("isPie", true);
            if (isPie) {
                mAnalyticsFilterData = (AnalyticsFilterData) intent.getSerializableExtra("AnalyticsFilterData");
                if (mAnalyticsFilterDataList != null)
                    initGraph();
            } else {
                for (int i = 0; i < 4; i++) {
                    mAnalyticsFilterDataList.add((AnalyticsFilterData)
                            intent.getSerializableExtra("AnalyticsFilterData" + i));
                }
                if (mAnalyticsFilterDataList.size() > 0)
                    initGraph();
            }
        }
    }

    private void updateActionBar() {
        getSupportActionBar().setTitle(mTitle);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_share:
                Utils.deleteGraphShareMediaFile();
                if (isPie) {
                    pieChart.saveToGallery("shared_graph");
                } else {
                    lineChart.saveToGallery("shared_graph");
                }
                File file = Utils.getGraphShareMediaFile();
                if (file != null) {
                    Toast.makeText(this, "Saved To Gallery |\n" + file.getPath(), Toast.LENGTH_LONG).show();
                    break;
                }
                Toast.makeText(this, "Something Went Wrong!", Toast.LENGTH_SHORT).show();
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
        getMenuInflater().inflate(R.menu.reports_detail_activity, menu);
        return true;
    }

    private void initGraph() {
        if (isPie) {
            drawPieGraph();
        } else {
            lineGraphCurrentPos = mAnalyticsFilterDataList.size();
            drawLineGraphFull();
        }
    }

    private void drawPieGraph() {
        ArrayList<MBRecord> mbrList = db.getRecordsAsList(mAnalyticsFilterData);
        final int size = mbrList.size();
        String[] label = new String[size];
        String[] data = new String[size];
        for (int i = 0; i < size; i++) {
            MBRecord mbr = mbrList.get(i);
            label[i] = mbr.getDescription();
            data[i] = mbr.getAmount() + "";
        }

        pieChart = GraphUtils.drawPieGraph(size, label,
                data, this, mTitle);
        View view = (View) pieChart;

        pieChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {
                pieValueClicked(e, h);
            }

            @Override
            public void onNothingSelected() {

            }
        });
        renderGraph(view, true);
    }

    private void drawLineGraphFull() {

        labels = new ArrayList<>();
        datas = new ArrayList<>();
        for (AnalyticsFilterData tempData : mAnalyticsFilterDataList) {
            ArrayList<MBRecord> mbrList = db.getRecordsAsList(tempData);
            final int size = mbrList.size();
            String[] label = new String[size];
            String[] data = new String[size];
            for (int i = 0; i < size; i++) {
                MBRecord mbr = mbrList.get(i);
                label[i] = mbr.getDescription();
                data[i] = mbr.getAmount() + "";
            }
            labels.add(label);
            datas.add(data);
        }
        Utils.parseToGraphData(datas, labels);
        lineChart = GraphUtils.drawLineGraph(labels, datas, this, mTitle);
        View view = (View) lineChart;
        lineChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {
                lineValueClicked(e, h);
            }

            @Override
            public void onNothingSelected() {

            }
        });
        renderGraph(view, true);
    }

    private void renderGraph(View view, boolean initViewPager) {
        if (view != null) {
            if (graphViewParent.getChildCount() > 0)
                graphViewParent.removeAllViews();

            graphViewParent.addView(view, new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT, 1f));
            if (initViewPager)
                initViewPager();
        }
    }

    private void lineValueClicked(Entry entry, Highlight highlight) {
        if (lineFirst) {
            lineFirst = false;
            mReportsGraphRecordsFragment =
                    (ReportsGraphRecordsFragment) mReportsViewPagerAdapter.getRegisteredFragment(1);
        }
        int type = highlight.getDataSetIndex();
        if (lineGraphCurrentPos != 4) {
            type = lineGraphCurrentPos;
        }
        AnalyticsFilterData tempData = mAnalyticsFilterDataList.get(type).clone();
        Arrays.fill(tempData.subMenuMoneyTypeDataBool, false);
        tempData.subMenuMoneyTypeDataBool[type] = true;

        // setting group by from date to none
        tempData.subMenuGroupByDataBool[1] = false;
        tempData.subMenuGroupByDataBool[4] = true;

        // setting current day or month to custom to take below given date
        Arrays.fill(tempData.subMenuDateDataBool, false);
        tempData.subMenuDateDataBool[3] = true;

        // setting date
        try {
            String[] tempArr = (String[]) entry.getData();
            String temps = tempArr[0];
            LoggerCus.d(TAG, "date:" + temps);

            SimpleDateFormat formater = null;
            if (tempData.subMenuDateIntervalDataBool[0])
                formater = new SimpleDateFormat("dd - MM - yyyy");
            else if (tempData.subMenuDateIntervalDataBool[1])
                formater = new SimpleDateFormat("MM - yyyy");

            Date tDate = formater.parse(temps);
            tempData.sDate = tDate;
            tempData.eDate = tDate;

            LoggerCus.d(TAG, "sdate : " + tempData.sDate.getTime());
            LoggerCus.d(TAG, "edate : " + tempData.eDate.getTime());

            if (tempData.subMenuDateIntervalDataBool[0]) {
                tempData.sDate = db.intializeSDateForDay(tempData.sDate);
                tempData.eDate = db.intializeEDateForDay(tempData.eDate);
            } else if (tempData.subMenuDateIntervalDataBool[1]) {
                tempData.sDate = db.intializeSDateForMonth(tempData.sDate);
                tempData.eDate = db.intializeEDateForMonth(tempData.eDate);
            }

            LoggerCus.d(TAG, "sdate : " + tempData.sDate.getTime());
            LoggerCus.d(TAG, "edate : " + tempData.eDate.getTime());

        } catch (ParseException e) {
            LoggerCus.d(TAG, "Error while parsing date for line value clicked -> " + e.getMessage());
        }

        mReportsGraphRecordsFragment.init(tempData);
        if (mViewPager.getCurrentItem() == 0)
            moveToRecordsFragment();
    }

    private void pieValueClicked(Entry entry, Highlight highlight) {

        if (pieFirst) {
            pieFirst = false;
            for (int i = 0; i < mAnalyticsFilterData.subMenuMoneyTypeDataBool.length; i++) {
                if (mAnalyticsFilterData.subMenuMoneyTypeDataBool[i]) {
                    ttype = i;
                    break;
                }
            }

            // set group by cat false
            if (mAnalyticsFilterData.subMenuGroupByDataBool[2]) {
                isCat = true;
                mAnalyticsFilterData.subMenuGroupByDataBool[2] = false;
                initCategories(Utils.getNameFromEntryData(entry.getData()).trim(), db, mAnalyticsFilterData, ttype);
            } else {
                isCat = false;
                mAnalyticsFilterData.subMenuGroupByDataBool[3] = false;
                initPaymentMethods(Utils.getNameFromEntryData(entry.getData()).trim(), db, mAnalyticsFilterData);
            }
            mAnalyticsFilterData.subMenuGroupByDataBool[4] = true;

            mReportsGraphRecordsFragment =
                    (ReportsGraphRecordsFragment) mReportsViewPagerAdapter.getRegisteredFragment(1);
        } else {
            if (isCat) {
                initCategories(Utils.getNameFromEntryData(entry.getData()).trim(), db, mAnalyticsFilterData, ttype);
            } else {
                initPaymentMethods(Utils.getNameFromEntryData(entry.getData()).trim(), db, mAnalyticsFilterData);
            }
        }
        mReportsGraphRecordsFragment.init(mAnalyticsFilterData);
        if (mViewPager.getCurrentItem() == 0)
            moveToRecordsFragment();
    }

    private void moveToRecordsFragment() {
        mViewPager.setCurrentItem(1, true);
    }

    private void initViews() {
        graphViewParent = (LinearLayout) findViewById(R.id.graph_view);
        mViewPager = (ViewPager) findViewById(R.id.view_pager);
        noData = findViewById(R.id.no_data);

        mViewPager.setVisibility(View.VISIBLE);
        noData.setVisibility(View.GONE);
    }

    private void initViewPager() {
        mReportsViewPagerAdapter = new ReportsViewPagerAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mReportsViewPagerAdapter);
    }

    @Override
    public String[][] getData() {
        if (isPie) {
            pieEntryValues = new ArrayList<>();
            IPieDataSet dataset = pieChart.getData().getDataSet();
            List<Integer> colors = dataset.getColors();
            final int len = dataset.getEntryCount();
            String s[][] = new String[len][3];
            for (int i = 0; i < len; i++) {
                PieEntry entry = dataset.getEntryForIndex(i);
                pieEntryValues.add(entry);
                s[i][0] = Utils.getNameFromEntryData(entry.getData());
                s[i][1] = "" + (int) entry.getValue();
                s[i][2] = "" + colors.get(i);
            }
            return s;
        } else {
            String[] labels = lineChart.getData().getDataSetLabels();
            int[] colors = lineChart.getData().getColors();
            final int len = labels.length;
            String s[][] = new String[len + 1][3];
            for (int i = 0; i < len; i++) {
                s[i][0] = labels[i];
                s[i][1] = null;
                s[i][2] = colors[i] + "";
            }
            s[len][0] = "All";
            s[len][1] = null;
            s[len][2] = null;
            return s;
        }
    }

    @Override
    public void itemClicked(int position) {
        if (isPie) {
            Entry entry = pieEntryValues.get(position);
            Highlight h = new Highlight(entry.getX(), entry.getY(), 0);
            pieChart.highlightValue(h, true);
        } else {
            lineGraphCurrentPos = position;
            if (position == mAnalyticsFilterDataList.size()) {
                drawLineGraphFull();
            } else {
                drawGraph(position);
            }
            LoggerCus.d(TAG, "type : " + position);
        }
    }

    private void drawGraph(int pos) {
        /*ArrayList<MBRecord> mbrList = db.getRecordsAsList(mAnalyticsFilterDataList.get(pos));
        final int size = mbrList.size();
        String[] label = new String[size];
        String[] data = new String[size];
        for (int i = 0; i < size; i++) {
            MBRecord mbr = mbrList.get(i);
            label[i] = mbr.getDescription();
            data[i] = mbr.getAmount() + "";
        }*/
        lineChart = GraphUtils.drawLineGraph(labels.get(pos), datas.get(pos), this, mTitle, pos);
        View view = (View) lineChart;
        lineChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {
                lineValueClicked(e, h);
            }

            @Override
            public void onNothingSelected() {

            }
        });
        renderGraph(view, false);
    }


}
