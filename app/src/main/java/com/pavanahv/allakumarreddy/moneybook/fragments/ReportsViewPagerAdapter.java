package com.pavanahv.allakumarreddy.moneybook.fragments;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.pavanahv.allakumarreddy.moneybook.utils.GlobalConstants;

import java.text.SimpleDateFormat;
import java.util.Date;

class ReportsViewPagerAdapter extends FragmentPagerAdapter {

    public ReportsViewPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return ReportsInnerFragment.newInstance(GlobalConstants.TYPE_SPENT);
            case 1:
                return ReportsInnerFragment.newInstance(GlobalConstants.TYPE_EARN);
            case 2:
                return ReportsInnerFragment.newInstance(GlobalConstants.TYPE_DUE);
        }
        return null; //does not happen
    }

    @Override
    public int getCount() {
        return 3;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return new SimpleDateFormat("dd MMM yyyy").format(new Date());
            case 1:
                return new SimpleDateFormat("MMM yyyy").format(new Date());
            case 2:
                return new SimpleDateFormat("yyyy").format(new Date());
        }
        return "";
    }

}
