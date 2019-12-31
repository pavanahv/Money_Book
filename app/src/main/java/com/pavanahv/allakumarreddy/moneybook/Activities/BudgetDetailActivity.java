package com.pavanahv.allakumarreddy.moneybook.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.BarChart;
import com.mikhaellopez.circularprogressbar.CircularProgressBar;
import com.pavanahv.allakumarreddy.moneybook.R;
import com.pavanahv.allakumarreddy.moneybook.storage.PreferencesCus;
import com.pavanahv.allakumarreddy.moneybook.storage.db.DbHandler;
import com.pavanahv.allakumarreddy.moneybook.utils.AnalyticsFilterData;
import com.pavanahv.allakumarreddy.moneybook.utils.GlobalConstants;
import com.pavanahv.allakumarreddy.moneybook.utils.GraphUtils;
import com.pavanahv.allakumarreddy.moneybook.utils.LoggerCus;
import com.pavanahv.allakumarreddy.moneybook.utils.MBRecord;
import com.pavanahv.allakumarreddy.moneybook.utils.ThemeUtils;
import com.pavanahv.allakumarreddy.moneybook.utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.TreeSet;
import java.util.concurrent.TimeUnit;

import static com.pavanahv.allakumarreddy.moneybook.utils.FilterUtils.initCategories;
import static com.pavanahv.allakumarreddy.moneybook.utils.FilterUtils.initFilters;
import static com.pavanahv.allakumarreddy.moneybook.utils.FilterUtils.initPaymentMethods;
import static com.pavanahv.allakumarreddy.moneybook.utils.FilterUtils.initType;

public class BudgetDetailActivity extends BaseActivity {

    private static final String TAG = BudgetDetailActivity.class.getSimpleName();
    private DbHandler db;
    private HashMap<String, String> map;
    private SimpleDateFormat sdf;
    private PreferencesCus mPref;
    private float average;
    private String avg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(ThemeUtils.getTheme(getClass().getSimpleName(), this));
        setContentView(R.layout.activity_budget_detail);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Budget Detail");
        db = new DbHandler(this);
        String name = getIntent().getExtras().getString("name");
        if (name == null)
            finish();
        map = db.getBudgetRecordsAsList(name);
        sdf = new SimpleDateFormat("dd/MM/yyyy");
        mPref = new PreferencesCus(this);
        if (map.size() > 0) {
            initViews();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.budget_detail, menu);
        return true;
    }

    private void initGraph() {

        new Thread(() -> {
            // getting all the dates from sdate to edate
            SimpleDateFormat sdfDb = null;
            int interval = Integer.parseInt(map.get("interval"));
            TreeSet<String> dateSet = new TreeSet<>();
            Date sDate = new Date();
            Date eDate = new Date();

            AnalyticsFilterData analyticsFilterData = new AnalyticsFilterData();
            initType(analyticsFilterData, GlobalConstants.TYPE_SPENT);
            initCategories(null, db, analyticsFilterData, GlobalConstants.TYPE_SPENT);
            initPaymentMethods(null, db, analyticsFilterData);
            initFilters(db, analyticsFilterData);

            switch (interval) {
                case 0:
                    // making current day to true
                    Arrays.fill(analyticsFilterData.subMenuDateDataBool, false);
                    analyticsFilterData.subMenuDateDataBool[0] = true;

                    sdfDb = new SimpleDateFormat("dd - MM - yyyy");
                    // date for current day
                    dateSet.add(sdfDb.format(sDate));
                    break;
                case 1:
                    // making current month to true
                    Arrays.fill(analyticsFilterData.subMenuDateDataBool, false);
                    analyticsFilterData.subMenuDateDataBool[1] = true;

                    sdfDb = new SimpleDateFormat("dd - MM - yyyy");
                    // date for current month
                    sDate = Utils.intializeSDateForMonth(sDate);
                    eDate = Utils.intializeEDateForMonth(eDate);
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(sDate);
                    int tempMonth = cal.get(Calendar.MONTH);
                    while (true) {
                        int tempMonthD = cal.get(Calendar.MONTH);
                        if (tempMonth != tempMonthD)
                            break;
                        dateSet.add(sdfDb.format(cal.getTime()));
                        cal.add(Calendar.DAY_OF_MONTH, 1);
                    }
                    break;
                case 2:
                    // making current year to true
                    Arrays.fill(analyticsFilterData.subMenuDateDataBool, false);
                    analyticsFilterData.subMenuDateDataBool[2] = true;

                    // making date interval to month
                    Arrays.fill(analyticsFilterData.subMenuDateIntervalDataBool, false);
                    analyticsFilterData.subMenuDateIntervalDataBool[1] = true;

                    sdfDb = new SimpleDateFormat("MM - yyyy");
                    // date for current month
                    sDate = new Date();
                    eDate = new Date();
                    sDate = Utils.intializeSDateForMonth(sDate);
                    eDate = Utils.intializeEDateForMonth(eDate);
                    sDate = Utils.intializeSDateForYear(sDate);
                    eDate = Utils.intializeEDateForYear(eDate);
                    cal = Calendar.getInstance();
                    cal.setTime(sDate);
                    int tempYear = cal.get(Calendar.YEAR);
                    while (true) {
                        int tempYearD = cal.get(Calendar.YEAR);
                        if (tempYear != tempYearD)
                            break;
                        dateSet.add(sdfDb.format(cal.getTime()));
                        cal.add(Calendar.MONTH, 1);
                    }
                    break;
                default:
                    break;
            }

            // getting all the categories data
            JSONArray arr = null;
            try {
                arr = new JSONArray(map.get("cat"));
            } catch (JSONException e) {
                LoggerCus.d(TAG, "Error while parsing json array of cat -> " + e.getMessage());
            }

            // setting group by to date
            Arrays.fill(analyticsFilterData.subMenuGroupByDataBool, false);
            analyticsFilterData.subMenuGroupByDataBool[1] = true;

            // setting sorting order
            Arrays.fill(analyticsFilterData.subMenuSortingOrderDataBool, false);
            analyticsFilterData.subMenuSortingOrderDataBool[0] = true;

            if (arr != null) {
                final int catLen = arr.length();
                HashMap<String, Integer> catMaps[] = new HashMap[catLen];
                ArrayList<String> catList = new ArrayList<>();
                for (int i = 0; i < catLen; i++) {
                    try {
                        String catName = arr.getString(i);
                        catList.add(catName);
                        catMaps[i] = new HashMap<>();
                        final int len = analyticsFilterData.subMenuCatogeoryData.length;
                        Arrays.fill(analyticsFilterData.subMenuCatogeoryDataBool, false);
                        for (int j = 1; j < len; j++) {
                            if (analyticsFilterData.subMenuCatogeoryData[j].compareToIgnoreCase(catName) == 0) {
                                analyticsFilterData.subMenuCatogeoryDataBool[j] = true;
                                break;
                            }
                        }

                        ArrayList<MBRecord> tempListRecs = db.getRecordsAsList(analyticsFilterData);
                        for (MBRecord mbr : tempListRecs) {
                            String tempName = Utils.getNameFromEntry(mbr.getDescription());
                            if (tempName != null) {
                                catMaps[i].put(tempName, mbr.getAmount());
                            }
                        }
                    } catch (JSONException e) {
                        LoggerCus.d(TAG, "error in getting array name -> " + e.getMessage());
                    }
                }
                ArrayList<Integer> catLists[] = new ArrayList[catMaps.length];
                for (int i = 0; i < catLists.length; i++) {
                    catLists[i] = new ArrayList<>();
                }
                for (String dateSetDateStr : dateSet) {
                    int i = 0;
                    for (HashMap<String, Integer> catMap : catMaps) {
                        if (!catMap.containsKey(dateSetDateStr)) {
                            catLists[i].add(0);
                        } else {
                            catLists[i].add(catMap.get(dateSetDateStr));
                        }
                        i++;
                    }
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        BarChart chart = GraphUtils.drawStackedBarGraph(catList, catLists,
                                new ArrayList<String>(dateSet), BudgetDetailActivity.this,
                                map.get("name") + " Analysis", average, avg);
                        FrameLayout graphView = (FrameLayout) findViewById(R.id.graph_view);
                        if (graphView.getChildCount() > 0)
                            graphView.removeAllViews();
                        graphView.addView(chart,
                                new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,
                                        FrameLayout.LayoutParams.MATCH_PARENT));
                    }
                });

            }
        }).start();


    }

    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                finish();
                overridePendingTransition(R.anim.move_left_in_activity, R.anim.move_right_out_activity);
                break;

            case R.id.action_edit:
                startAddActivity();
                break;
            case R.id.action_delete:
                boolean res = db.deleteBudgetRecord(map.get("name"));
                if (res) {
                    Toast.makeText(this, "Successfully Deleted !", Toast.LENGTH_SHORT).show();
                    finish();
                    overridePendingTransition(R.anim.move_left_in_activity, R.anim.move_right_out_activity);
                } else {
                    Toast.makeText(this, "Something went wrong. Please try again!", Toast.LENGTH_SHORT).show();
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void startAddActivity() {
        Intent intent = new Intent(this, BudgetAddActivity.class);
        intent.putExtra("map", map);
        startActivity(intent);
        overridePendingTransition(R.anim.move_right_in_activity, R.anim.move_left_out_activity);
        finish();
    }

    private void initViews() {
        TextView name = (TextView) findViewById(R.id.name);
        TextView limitt = (TextView) findViewById(R.id.limit);
        CircularProgressBar circularProgress = (CircularProgressBar) findViewById(R.id.cp);
        TextView intervalt = (TextView) findViewById(R.id.interval);
        TextView date = (TextView) findViewById(R.id.date);
        TextView balLeft = (TextView) findViewById(R.id.bal_left);
        TextView spent = (TextView) findViewById(R.id.spent);
        TextView avgt = (TextView) findViewById(R.id.avg);
        ImageView expired = (ImageView) findViewById(R.id.expired);

        new Thread(() -> {
            // initiating data
            int limit = Integer.parseInt(map.get("limit"));
            int interval = Integer.parseInt(map.get("interval"));

            String tempDate = "";

            avg = "";
            AnalyticsFilterData analyticsFilterData = new AnalyticsFilterData();
            initType(analyticsFilterData, GlobalConstants.TYPE_SPENT);
            initCategories(null, db, analyticsFilterData, GlobalConstants.TYPE_SPENT);
            initPaymentMethods(null, db, analyticsFilterData);
            initFilters(db, analyticsFilterData);
            JSONArray arr = null;
            try {
                arr = new JSONArray(map.get("cat"));
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

            int tempColor = Utils.getSpentColor(mPref);

            switch (interval) {
                case 0:
                    average = (float) limit;
                    avg = limit + " per Day";
                    break;

                case 1:
                    long diff = eDate.getTime() - sDate.getTime();
                    long days = TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
                    average = (float) limit / days;
                    avg = String.format("%.2f", average) + " per day";
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
                    average = (float) limit / days;
                    avg = String.format("%.2f", average) + " per month";
                    break;
            }


            final int tt = total;
            final String tempDatet = tempDate;
            final String avgtt = avg;
            initGraph();
            runOnUiThread(() -> {
                name.setText(map.get("name"));
                limitt.setText(limit + "");
                intervalt.setText(GlobalConstants.BUDGET_INTERVAL[interval]);
                spent.setText(Utils.getFormattedNumber(tt));
                balLeft.setText("" + (limit - tt));
                circularProgress.setProgress(((float) tt / limit) * 100);
                date.setText(tempDatet);
                circularProgress.setBackgroundColor(tempColor);
                circularProgress.setColor(tempColor);
                spent.setTextColor(tempColor);
                avgt.setText(avgtt);
                //expired.setColorFilter(Utils.getSpentColor(mPref), PorterDuff.Mode.MULTIPLY);
                if (tt > limit) {
                    expired.setVisibility(View.VISIBLE);
                } else {
                    expired.setVisibility(View.GONE);
                }
            });
        }).start();
    }
}
