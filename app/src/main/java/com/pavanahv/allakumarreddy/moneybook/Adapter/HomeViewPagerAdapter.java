package com.pavanahv.allakumarreddy.moneybook.Adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.SparseArray;
import android.view.ViewGroup;

import com.pavanahv.allakumarreddy.moneybook.fragments.HomeInnerFragment;
import com.pavanahv.allakumarreddy.moneybook.utils.GlobalConstants;

public class HomeViewPagerAdapter extends FragmentPagerAdapter {
    SparseArray<Fragment> registeredFragments = new SparseArray<>();

    public HomeViewPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return HomeInnerFragment.newInstance(GlobalConstants.TYPE_SPENT);
            case 1:
                return HomeInnerFragment.newInstance(GlobalConstants.TYPE_EARN);
            case 2:
                return HomeInnerFragment.newInstance(GlobalConstants.TYPE_DUE);
            case 3:
                return HomeInnerFragment.newInstance(GlobalConstants.TYPE_LOAN);
        }
        return null; //does not happen
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        Fragment fragment = (Fragment) super.instantiateItem(container, position);
        registeredFragments.put(position, fragment);
        return fragment;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        registeredFragments.remove(position);
        super.destroyItem(container, position, object);
    }

    public Fragment getRegisteredFragment(int position) {
        return registeredFragments.get(position);
    }

    @Override
    public int getCount() {
        return 4;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return "Spent";
            case 1:
                return "Earn";
            case 2:
                return "Due";
            case 3:
                return "Loan";
        }
        return "";
    }
}
