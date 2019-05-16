package com.example.allakumarreddy.moneybook.Activities;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.TextView;

import com.example.allakumarreddy.moneybook.Adapter.FilterMainMenuAdapter;
import com.example.allakumarreddy.moneybook.Adapter.FilterSubMenuAdapter;
import com.example.allakumarreddy.moneybook.R;
import com.example.allakumarreddy.moneybook.db.DbHandler;

import java.util.ArrayList;
import java.util.Arrays;

public class FiltersAnalyticsActivity extends AppCompatActivity {

    private RecyclerView mainMenu;
    private RecyclerView subMenu;
    private String[] mainMenuData = new String[]{"Date", "Date Interval", "Money Type", "Group By", "Sort By", "Sorting Order", "Category", "View By", "Graph Type", "Saved Filters"};
    private ArrayList<String> mMainMenuArrayList;
    private String[] subMenuDateData = new String[]{"Current Day", "Current Month", "Current Year", "Custom", "All"};
    private boolean[] subMenuDateDataBool = new boolean[]{false, false, false, false, true};
    private String[] subMenuMoneyTypeData = new String[]{"All", "Spent", "Earn", "Due", "Loan", "Money Transfer"};
    private boolean[] subMenuMoneyTypeDataBool = new boolean[]{true, true, true, true, true, true};
    private String[] subMenuDateIntervalData = new String[]{"Day", "Month", "Year"};
    private boolean[] subMenuDateIntervalDataBool = new boolean[]{true, false, false};
    private String[] subMenuViewByData = new String[]{"Graph", "List"};
    private boolean[] subMenuViewByDataBool = new boolean[]{false, true};
    private String[] subMenuGraphTypeData = new String[]{"Line", "Bar", "Pie", "Radar", "Scatter"};
    private boolean[] subMenuGraphTypeDataBool = new boolean[]{true, false, false, false, false};
    private String[] subMenuGroupByData = new String[]{"Item", "Date", "None"};
    private boolean[] subMenuGroupByDataBool = new boolean[]{false, false, true};
    private String[] subMenuSortByData = new String[]{"Date", "price", "Item"};
    private boolean[] subMenuSortByDataBool = new boolean[]{true, false, false};
    private String[] subMenuSortingOrderData = new String[]{"Increasing", "Decreasing"};
    private boolean[] subMenuSortingOrderDataBool = new boolean[]{true, false};
    private String[] subMenuCatogeoryData = new String[]{"All"};
    private boolean[] subMenuCatogeoryDataBool = new boolean[]{true};
    private String[] subMenuFilterData = new String[]{"No Filters"};
    private boolean[] subMenuFilterDataBool = new boolean[]{false};

    ArrayList<String[]> subMenuListData = new ArrayList<>();
    private boolean mIsRadioOrCheck;
    private ArrayList<String> mSubMenuArrayList;
    private ArrayList<boolean[]> subMenuListDataSelection = new ArrayList<>();
    private ArrayList<Boolean> mSubMenuArrayListSelection;
    private int mMainMenuSelectedItemPosition;
    private DbHandler db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filters_analytics);
        getSupportActionBar().setTitle("Analytics Filter");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        init();
        initMainMenu();
        initMainMenuData();
        initSubMenuData();
        initSubMenu();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.analytics_clear_all:
                clearAll();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.analytics_filter_menu, menu);
        return true;
    }

    private void clearAll() {
        subMenuListData.clear();
        subMenuListDataSelection.clear();
        mMainMenuArrayList.clear();
        mSubMenuArrayList.clear();

        initDataAgainAfterClear();
        initMainMenuData();
        initSubMenuData();
        initSubMenu();
    }

    private void initMainMenuData() {
        mMainMenuArrayList = new ArrayList<String>(mainMenuData.length);
        for (String s : mainMenuData)
            mMainMenuArrayList.add(s);
        mainMenu.setAdapter(new FilterMainMenuAdapter(this, mMainMenuArrayList));
    }

    private void initSubMenu() {
        subMenu.setLayoutManager(new LinearLayoutManager(this));
        subMenu.setHasFixedSize(true);
        subMenu.addItemDecoration(new DividerItemDecoration(mainMenu.getContext(), DividerItemDecoration.VERTICAL));

        setSubMenu(0);
    }

    private void initDataAgainAfterClear() {
        subMenuDateDataBool = new boolean[]{false, false, false, false, true};
        subMenuMoneyTypeDataBool = new boolean[]{true, true, true, true, true, true};
        subMenuDateIntervalDataBool = new boolean[]{true, false, false};
        subMenuViewByDataBool = new boolean[]{false, true};
        subMenuGraphTypeDataBool = new boolean[]{true, false, false, false, false};
        subMenuGroupByDataBool = new boolean[]{false, false, true};
        subMenuSortByDataBool = new boolean[]{true, false, false};
        subMenuSortingOrderDataBool = new boolean[]{true, false};
        subMenuCatogeoryDataBool = new boolean[]{true};
        subMenuFilterDataBool = new boolean[]{false};
    }

    private void initSubMenuData() {

        String[] cat = db.getCategeories();
        subMenuCatogeoryData = new String[cat.length + 1];
        subMenuCatogeoryData[0] = "All";
        for (int i = 1; i < subMenuCatogeoryData.length; i++)
            subMenuCatogeoryData[i] = cat[i - 1];

        subMenuCatogeoryDataBool = new boolean[subMenuCatogeoryData.length];
        Arrays.fill(subMenuCatogeoryDataBool, true);

        subMenuListData.add(subMenuDateData);
        subMenuListData.add(subMenuDateIntervalData);
        subMenuListData.add(subMenuMoneyTypeData);
        subMenuListData.add(subMenuGroupByData);
        subMenuListData.add(subMenuSortByData);
        subMenuListData.add(subMenuSortingOrderData);
        subMenuListData.add(subMenuCatogeoryData);
        subMenuListData.add(subMenuViewByData);
        subMenuListData.add(subMenuGraphTypeData);
        subMenuListData.add(subMenuFilterData);

        subMenuListDataSelection.add(subMenuDateDataBool);
        subMenuListDataSelection.add(subMenuDateIntervalDataBool);
        subMenuListDataSelection.add(subMenuMoneyTypeDataBool);
        subMenuListDataSelection.add(subMenuGroupByDataBool);
        subMenuListDataSelection.add(subMenuSortByDataBool);
        subMenuListDataSelection.add(subMenuSortingOrderDataBool);
        subMenuListDataSelection.add(subMenuCatogeoryDataBool);
        subMenuListDataSelection.add(subMenuViewByDataBool);
        subMenuListDataSelection.add(subMenuGraphTypeDataBool);
        subMenuListDataSelection.add(subMenuFilterDataBool);
    }

    private void initMainMenu() {
        mainMenu.setLayoutManager(new LinearLayoutManager(this));
        mainMenu.setHasFixedSize(true);
        mainMenu.addItemDecoration(new DividerItemDecoration(mainMenu.getContext(), DividerItemDecoration.VERTICAL));
    }

    private void init() {
        mainMenu = (RecyclerView) findViewById(R.id.mainmenu);
        subMenu = (RecyclerView) findViewById(R.id.submenu);
        db = new DbHandler(this);
    }

    public void mainMenuItemSelected(int position) {
        setAllMainMenuItemsNormalStyle();
        ((TextView) mainMenu.getChildAt(position).findViewById(R.id.menutitle)).setTypeface(null, Typeface.BOLD);
        setSubMenu(position);
    }

    private void setSubMenu(int position) {
        mMainMenuSelectedItemPosition = position;
        switch (position) {
            case 0:
            case 1:
            case 3:
            case 4:
            case 5:
            case 7:
            case 8:
            case 9:
                initSubMenu(subMenuListData.get(position), true, subMenuListDataSelection.get(position));
                break;
            case 2:
            case 6:
                initSubMenu(subMenuListData.get(position), false, subMenuListDataSelection.get(position));
                break;
        }
    }

    private void initSubMenu(String[] data, boolean isRadioOrCheck, boolean[] selection) {
        mIsRadioOrCheck = isRadioOrCheck;
        mSubMenuArrayList = new ArrayList<String>(data.length);
        for (String s : data)
            mSubMenuArrayList.add(s);
        mSubMenuArrayListSelection = new ArrayList<>(selection.length);
        for (Boolean b : selection)
            mSubMenuArrayListSelection.add(b);
        subMenu.setAdapter(new FilterSubMenuAdapter(this, mSubMenuArrayList, mIsRadioOrCheck, mSubMenuArrayListSelection));
    }

    private void setAllMainMenuItemsNormalStyle() {
        final int len = mainMenuData.length;
        for (int i = 0; i < len; i++)
            ((TextView) mainMenu.getChildAt(i).findViewById(R.id.menutitle)).setTypeface(null, Typeface.NORMAL);
    }

    public void cancel(View view) {

    }

    public void apply(View view) {

    }

    public void subMenuItemSelected(int position) {
        if (mIsRadioOrCheck) {
            setAllSubMenuItemsNotChecked(mIsRadioOrCheck);
            ((RadioButton) subMenu.getChildAt(position).findViewById(R.id.radioButton)).setChecked(true);
            subMenuListDataSelection.get(mMainMenuSelectedItemPosition)[position] = true;
        } else {
            CheckBox checkBox = (CheckBox) subMenu.getChildAt(position).findViewById(R.id.checkBox);
            if (!checkBox.isChecked()) {
                checkBox.setChecked(false);
                subMenuListDataSelection.get(mMainMenuSelectedItemPosition)[position] = false;
                if (position == 0) {
                    setAllSubMenuCheckBox(false);
                } else {
                    ((CheckBox) subMenu.getChildAt(0).findViewById(R.id.checkBox)).setChecked(false);
                    subMenuListDataSelection.get(mMainMenuSelectedItemPosition)[0] = false;
                }
            } else {
                checkBox.setChecked(true);
                subMenuListDataSelection.get(mMainMenuSelectedItemPosition)[position] = true;
                if (position == 0) {
                    setAllSubMenuCheckBox(true);
                } else {
                    ((CheckBox) subMenu.getChildAt(0).findViewById(R.id.checkBox)).setChecked(false);
                    subMenuListDataSelection.get(mMainMenuSelectedItemPosition)[0] = false;
                }
            }
        }
    }

    private void setAllSubMenuCheckBox(boolean b) {
        boolean[] values = subMenuListDataSelection.get(mMainMenuSelectedItemPosition);
        final int len = values.length;
        for (int i = 0; i < len; i++) {
            ((CheckBox) subMenu.getChildAt(i).findViewById(R.id.checkBox)).setChecked(b);
            values[i] = b;
        }
    }

    private void setAllSubMenuItemsNotChecked(boolean mIsRadioOrCheck) {
        boolean[] values = subMenuListDataSelection.get(mMainMenuSelectedItemPosition);
        final int len = values.length;
        for (int i = 0; i < len; i++) {
            ((RadioButton) subMenu.getChildAt(i).findViewById(R.id.radioButton)).setChecked(false);
            values[i] = false;
        }
    }
}
