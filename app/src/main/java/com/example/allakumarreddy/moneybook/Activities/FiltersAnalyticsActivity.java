package com.example.allakumarreddy.moneybook.Activities;

import android.app.Activity;
import android.content.Intent;
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
import com.example.allakumarreddy.moneybook.utils.AnalyticsFilterData;
import com.example.allakumarreddy.moneybook.utils.DatePickerCus;
import com.example.allakumarreddy.moneybook.utils.GlobalConstants;
import com.example.allakumarreddy.moneybook.utils.IDate;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class FiltersAnalyticsActivity extends AppCompatActivity implements IDate {

    private static final String TAG = FiltersAnalyticsActivity.class.getSimpleName();
    private RecyclerView mainMenu;
    private RecyclerView subMenu;
    private ArrayList<String> mMainMenuArrayList;

    ArrayList<String[]> subMenuListData = new ArrayList<>();
    private boolean mIsRadioOrCheck;
    private ArrayList<String> mSubMenuArrayList;
    private ArrayList<boolean[]> subMenuListDataSelection = new ArrayList<>();
    private ArrayList<Boolean> mSubMenuArrayListSelection;
    private int mMainMenuSelectedItemPosition;
    private DbHandler db;
    private AnalyticsFilterData mAnalyticsFilterData;
    private boolean isSdateOrEdate = false;
    private SimpleDateFormat format;
    private FilterSubMenuAdapter mSubMenuAdapter;

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

        mAnalyticsFilterData.initDataAgainAfterClear();
        initMainMenuData();
        initSubMenuData();
        initSubMenu();
    }

    private void initMainMenuData() {
        mMainMenuArrayList = new ArrayList<String>(mAnalyticsFilterData.mainMenuData.length);
        for (String s : mAnalyticsFilterData.mainMenuData)
            mMainMenuArrayList.add(s);
        mainMenu.setAdapter(new FilterMainMenuAdapter(this, mMainMenuArrayList));
    }

    private void initSubMenu() {
        subMenu.setLayoutManager(new LinearLayoutManager(this));
        subMenu.setHasFixedSize(true);
        subMenu.addItemDecoration(new DividerItemDecoration(mainMenu.getContext(), DividerItemDecoration.VERTICAL));

        setSubMenu(0);
    }

    private void initSubMenuData() {

        subMenuListData.add(mAnalyticsFilterData.subMenuDateData);
        subMenuListData.add(mAnalyticsFilterData.subMenuDateIntervalData);
        subMenuListData.add(mAnalyticsFilterData.subMenuMoneyTypeData);
        subMenuListData.add(mAnalyticsFilterData.subMenuGroupByData);
        subMenuListData.add(mAnalyticsFilterData.subMenuSortByData);
        subMenuListData.add(mAnalyticsFilterData.subMenuSortingOrderData);
        subMenuListData.add(mAnalyticsFilterData.subMenuCatogeoryData);
        subMenuListData.add(mAnalyticsFilterData.subMenuViewByData);
        subMenuListData.add(mAnalyticsFilterData.subMenuGraphTypeData);
        subMenuListData.add(mAnalyticsFilterData.subMenuFilterData);

        subMenuListDataSelection.add(mAnalyticsFilterData.subMenuDateDataBool);
        subMenuListDataSelection.add(mAnalyticsFilterData.subMenuDateIntervalDataBool);
        subMenuListDataSelection.add(mAnalyticsFilterData.subMenuMoneyTypeDataBool);
        subMenuListDataSelection.add(mAnalyticsFilterData.subMenuGroupByDataBool);
        subMenuListDataSelection.add(mAnalyticsFilterData.subMenuSortByDataBool);
        subMenuListDataSelection.add(mAnalyticsFilterData.subMenuSortingOrderDataBool);
        subMenuListDataSelection.add(mAnalyticsFilterData.subMenuCatogeoryDataBool);
        subMenuListDataSelection.add(mAnalyticsFilterData.subMenuViewByDataBool);
        subMenuListDataSelection.add(mAnalyticsFilterData.subMenuGraphTypeDataBool);
        subMenuListDataSelection.add(mAnalyticsFilterData.subMenuFilterDataBool);
    }

    private void initMainMenu() {
        mainMenu.setLayoutManager(new LinearLayoutManager(this));
        mainMenu.setHasFixedSize(true);
        mainMenu.addItemDecoration(new DividerItemDecoration(mainMenu.getContext(), DividerItemDecoration.VERTICAL));
    }

    private void init() {
        format = new SimpleDateFormat("yyyy/MM/dd");
        mainMenu = (RecyclerView) findViewById(R.id.mainmenu);
        subMenu = (RecyclerView) findViewById(R.id.submenu);
        db = new DbHandler(this);

        Intent intent = getIntent();
        mAnalyticsFilterData = (AnalyticsFilterData) intent.getSerializableExtra(GlobalConstants.ANALYTICS_FILTER_ACTIVITY);
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
        mSubMenuAdapter = new FilterSubMenuAdapter(this, mSubMenuArrayList, mIsRadioOrCheck, mSubMenuArrayListSelection);
        subMenu.setAdapter(mSubMenuAdapter);
    }

    private void setAllMainMenuItemsNormalStyle() {
        final int len = mAnalyticsFilterData.mainMenuData.length;
        for (int i = 0; i < len; i++)
            ((TextView) mainMenu.getChildAt(i).findViewById(R.id.menutitle)).setTypeface(null, Typeface.NORMAL);
    }

    public void cancel(View view) {
        Intent resultIntent = new Intent();
        setResult(Activity.RESULT_CANCELED, resultIntent);
        finish();
    }

    public void apply(View view) {
        Intent resultIntent = new Intent();
        resultIntent.putExtra(GlobalConstants.ANALYTICS_FILTER_ACTIVITY, mAnalyticsFilterData);
        setResult(Activity.RESULT_OK, resultIntent);
        finish();
    }

    public void subMenuItemSelected(int position) {
        if (mIsRadioOrCheck) {
            setAllSubMenuItemsNotChecked(mIsRadioOrCheck);
            ((RadioButton) subMenu.getChildAt(position).findViewById(R.id.radioButton)).setChecked(true);
            subMenuListDataSelection.get(mMainMenuSelectedItemPosition)[position] = true;
            checkDateCustomItem(position);
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

    private void checkDateCustomItem(int position) {
        if ((mMainMenuSelectedItemPosition == 0) && (position == 3)) {
            new DatePickerCus(this, this, mAnalyticsFilterData.sDate).show();
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

    @Override
    public void afterDateSelection(String date) {
        try {
            if (isSdateOrEdate) {
                isSdateOrEdate = false;
                mAnalyticsFilterData.eDate = format.parse(date);
                updateDateInCustomItem();
            } else {
                isSdateOrEdate = true;
                mAnalyticsFilterData.sDate = format.parse(date);
                new DatePickerCus(this, this, mAnalyticsFilterData.eDate).show();
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    private void updateDateInCustomItem() {
        mAnalyticsFilterData.subMenuDateData[3] = "Custom ( " + format.format(mAnalyticsFilterData.sDate) + " - " + format.format(mAnalyticsFilterData.eDate) + " )";
        mainMenuItemSelected(0);
    }
}