package com.pavanahv.allakumarreddy.moneybook.Activities;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.pavanahv.allakumarreddy.moneybook.R;
import com.pavanahv.allakumarreddy.moneybook.fragments.AutoAddAddFragment;
import com.pavanahv.allakumarreddy.moneybook.fragments.AutoAddListFragment;
import com.pavanahv.allakumarreddy.moneybook.utils.AutoAddRecord;

public class AutoAddActivity extends BaseActivity implements
        AutoAddListFragment.OnAutoAddListFragmentInteractionListener,
        AutoAddAddFragment.OnAutoAddAddFragmentInteractionListener {

    private View mainView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auto_add);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Auto Add");

        init();
        initFragments();
    }

    private void initFragments() {
        showListFragment();
    }

    private void init() {
        mainView = findViewById(R.id.main_view);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
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
    public void addButtonClicked() {
        showAddFragment();
    }

    @Override
    public void itemClicked(AutoAddRecord record) {
        showAddFragment(record);
    }

    private void showAddFragment(AutoAddRecord record) {
        String name = "";
        if (record != null)
            name = record.getName();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.main_view, AutoAddAddFragment.newInstance(name));
        transaction.addToBackStack(null);
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        transaction.commit();
    }

    private void showAddFragment() {
        showAddFragment(null);
    }

    @Override
    public void onSave() {
        showListFragment();
    }

    @Override
    public void onCancel() {
        showListFragment();
    }

    private void showListFragment() {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.main_view, new AutoAddListFragment());
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        transaction.commit();
    }


}
