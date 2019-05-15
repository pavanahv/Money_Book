package com.example.allakumarreddy.moneybook.Activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import com.example.allakumarreddy.moneybook.Adapter.FilterMainMenuAdapter;
import com.example.allakumarreddy.moneybook.R;

import java.util.ArrayList;

public class FiltersAnalyticsActivity extends AppCompatActivity {

    private RecyclerView mainMenu;
    private RecyclerView subMenu;
    private String[] mainMenuData = new String[]{"Date", "Date Interval", "Money Type", "Group By", "Sort By", "Sorting Order", "Category", "Graph Type", "View By", "Saved Filters"};
    private ArrayList<String> mMainMenuArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filters_analytics);

        init();
        initMainMenu();
    }

    private void initMainMenu() {
        mainMenu.setLayoutManager(new LinearLayoutManager(this));
        mainMenu.setHasFixedSize(true);
        mMainMenuArrayList = new ArrayList<String>(mainMenuData.length);
        for (String s : mainMenuData)
            mMainMenuArrayList.add(s);
        mainMenu.setAdapter(new FilterMainMenuAdapter(this, mMainMenuArrayList));
        mainMenu.addItemDecoration(new DividerItemDecoration(mainMenu.getContext(), DividerItemDecoration.VERTICAL));
    }

    private void init() {
        mainMenu = (RecyclerView) findViewById(R.id.mainmenu);
        subMenu = (RecyclerView) findViewById(R.id.submenu);
    }

    public void mainMenuItemSelected(int position) {
        Toast.makeText(this, "clicked on " + mMainMenuArrayList.get(position), Toast.LENGTH_LONG).show();
    }
}
