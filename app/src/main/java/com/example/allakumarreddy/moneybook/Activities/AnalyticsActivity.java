package com.example.allakumarreddy.moneybook.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.allakumarreddy.moneybook.Adapter.AnalyticsAdapter;
import com.example.allakumarreddy.moneybook.utils.DatePickerCus;
import com.example.allakumarreddy.moneybook.db.DbHandler;
import com.example.allakumarreddy.moneybook.utils.IDate;
import com.example.allakumarreddy.moneybook.utils.LoggerCus;
import com.example.allakumarreddy.moneybook.utils.MBRecord;
import com.example.allakumarreddy.moneybook.R;
import com.example.allakumarreddy.moneybook.utils.Utils;
import com.example.allakumarreddy.moneybook.storage.XLStore;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class AnalyticsActivity extends AppCompatActivity implements IDate {

    private LinearLayout main;
    private View prevView;
    private DbHandler db;
    private ArrayList<MBRecord> list;
    private boolean currentDateSorE, dateAll = true;
    private SimpleDateFormat format;
    private Date sDate = new Date();
    private Date eDate = new Date();
    private AnalyticsAdapter analyticsAdapter;
    private TextView totalTv;
    private String queryText = "";
    private int MENU_TYPE_SPENT = 121;
    private int MENU_TYPE = 120;
    String menuDate[] = new String[]{"Start Date", "End Date", "All"};
    String menuType[] = new String[]{"All", "Spent", "Earn", "Due", "Loan","Money Transfer"};
    String menuInterval[] = new String[]{"Day", "Month", "Year"};
    String menuGOTType[] = new String[]{"List", "Graph"};
    String menuGraphType[] = new String[]{"Line", "Bar", "Pie", "Radar", "Scatter"};
    String menuGroupType[] = new String[]{"Item", "Date", "None"};
    String menuSortType[] = new String[]{"Date", "Price", "Item"};
    boolean moneyTypeAll = true;
    int moneyTypeInstance = -1;
    private int dateInterval = 0;
    private SearchView searchView;
    private ArrayList<MBRecord> dataList;
    private int graphType = 0;
    private boolean groupByNone = true;
    private int groupBy;
    private int sortBy = 0;
    private String[] cols = null;
    private String category = null;
    private boolean categoryNone = true;

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
        processTabs();
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.analytics_clear_all:
                clearAllFilters();
                updateData();
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

        SubMenu menuDateSubM = menu.addSubMenu("Date");
        for (int i = 0; i < menuDate.length; i++) {
            final int itemNum = i;
            menuDateSubM.add(menuDate[i]).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    setDate(itemNum);
                    return false;
                }
            });
        }

        SubMenu menuTypeSubM = menu.addSubMenu("Money Type");
        for (int i = 0; i < menuType.length; i++) {
            final int itemNum = i;
            menuTypeSubM.add(menuType[i]).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    setMoneyType(itemNum);
                    return false;
                }
            });
        }

        SubMenu menuIntervalSubM = menu.addSubMenu("Interval");
        for (int i = 0; i < menuInterval.length; i++) {
            final int itemNum = i;
            menuIntervalSubM.add(menuInterval[i]).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    setDateInterval(itemNum);
                    return false;
                }
            });
        }

        SubMenu menuGOTSubM = menu.addSubMenu("View By");
        for (int i = 0; i < menuGOTType.length; i++) {
            final int itemNum = i;
            menuGOTSubM.add(menuGOTType[i]).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    showGraph();
                    return false;
                }
            });
        }

        SubMenu menuGraphTypeSubM = menu.addSubMenu("Graph Type");
        for (int i = 0; i < menuGraphType.length; i++) {
            final int itemNum = i;
            menuGraphTypeSubM.add(menuGraphType[i]).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    setGraphType(itemNum);
                    return false;
                }
            });
        }

        SubMenu menuGroupBySubM = menu.addSubMenu("Group By");
        for (int i = 0; i < menuGroupType.length; i++) {
            final int itemNum = i;
            menuGroupBySubM.add(menuGroupType[i]).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    setGroupBy(itemNum);
                    return false;
                }
            });
        }

        SubMenu menuSortBySubM = menu.addSubMenu("Sort By");
        for (int i = 0; i < menuSortType.length; i++) {
            final int itemNum = i;
            menuSortBySubM.add(menuSortType[i]).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    setSortBy(itemNum);
                    return false;
                }
            });
        }

        cols = db.getCategeories();
        SubMenu menuCategoriesSubM = menu.addSubMenu("Category");
        for (int i = 0; i < cols.length; i++) {
            final int itemNum = i;
            menuCategoriesSubM.add(cols[i]).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    setCategory(itemNum);
                    return false;
                }
            });
        }
        menuCategoriesSubM.add("None").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                setCategory(cols.length);
                return false;
            }
        });

        return true;
    }

    public void startDetailActivity(int pos) {
        MBRecord mbr = dataList.get(pos);
        Intent intent = new Intent(AnalyticsActivity.this, AnalyticsItemDetail.class);
        intent.putExtra("desc", mbr.getDescription());
        intent.putExtra("amount", mbr.getAmount());
        intent.putExtra("date", format.format(mbr.getDate()));
        intent.putExtra("type", mbr.getType());
        intent.putExtra("category", mbr.getCategory());
        startActivity(intent);
    }

    private void setCategory(int itemNum) {
        if (itemNum == cols.length) {
            this.category = null;
            this.categoryNone = true;
        } else {
            this.category = cols[itemNum];
            this.categoryNone = false;
        }
        updateData();
    }

    private void setSortBy(int itemNum) {
        this.sortBy = itemNum;
        updateData();
    }

    private void setGroupBy(int itemNum) {
        if (itemNum == 2) {
            this.groupByNone = true;
            this.groupBy = -1;
        } else {
            this.groupByNone = false;
            this.groupBy = itemNum;
        }
        updateData();
    }

    private void setGraphType(int itemNum) {
        this.graphType = itemNum;
        showGraph();
    }

    private void showGraph() {
        final int size = dataList.size();
        String[] label = new String[size];
        String[] data = new String[size];
        for (int i = 0; i < size; i++) {
            MBRecord mbr = dataList.get(i);
            label[i] = mbr.getDescription();
            data[i] = mbr.getAmount() + "";
        }
        Intent in = new Intent(this, GraphActivity.class);
        in.putExtra("label", label);
        in.putExtra("data", data);
        in.putExtra("type", graphType);
        startActivity(in);
    }

    private void setDate(int itemNum) {
        switch (itemNum) {
            case 0:
                currentDateSorE = false;
                dateAll = false;
                new DatePickerCus(this, this, sDate).show();
                break;
            case 1:
                currentDateSorE = true;
                dateAll = false;
                new DatePickerCus(this, this, eDate).show();
                break;
            case 2:
                dateAll = true;
                updateData();
                break;
        }
    }

    private void clearAllFilters() {
        searchView.setQuery("", false);
        searchView.clearFocus();
        queryText = "";
        dateAll = true;
        moneyTypeAll = true;
        moneyTypeInstance = -1;
        dateInterval = 0;
        graphType = 0;
        groupByNone = true;
        groupBy = -1;
        sortBy = 0;
        this.category = null;
        this.categoryNone = true;
    }

    private void setDateInterval(int itemNum) {
        this.dateInterval = itemNum;
        updateData();
    }

    private void setMoneyType(int itemNum) {
        if (itemNum == 0) {
            this.moneyTypeAll = true;
            this.moneyTypeInstance = -1;
        } else {
            this.moneyTypeInstance = itemNum - 1;
            this.moneyTypeAll = false;
        }
        updateData();
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
                queryText = qText;
                updateData();
                return false;
            }
        });
        clearAllFilters();
        Intent intent = getIntent();
        String tempCat = intent.getStringExtra("name");
        if (tempCat.compareToIgnoreCase("0") != 0) {
            this.categoryNone = false;
            this.category = tempCat.toLowerCase();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateData();
    }

    @Override
    public void afterDateSelection(String date) {
        try {
            if (currentDateSorE)
                eDate = format.parse(date);
            else
                sDate = format.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        LoggerCus.d("analytics activity", format.format(sDate) + " " + format.format(eDate));
        updateData();
    }

    private void updateData() {
        list.clear();
        dataList = db.getRecordsAsList(queryText, dateAll, sDate, eDate, moneyTypeAll, moneyTypeInstance, dateInterval, groupByNone, groupBy, sortBy, categoryNone, category);
        list.addAll(dataList);
        analyticsAdapter.notifyDataSetChanged();
        upDateTotal();
    }

    private void upDateTotal() {
        int value = db.getTotal();
        totalTv.setText(Utils.getFormattedNumber(value));
    }
}
