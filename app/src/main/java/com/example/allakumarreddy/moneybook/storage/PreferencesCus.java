package com.example.allakumarreddy.moneybook.storage;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by alla.kumarreddy on 12-Apr-18.
 */

public class PreferencesCus {

    private SharedPreferences mSharedPreference;
    private Context context;
    private final String mDefaultStringIfNothingFound = null;

    public PreferencesCus(Context context) {
        this.context = context;
        mSharedPreference = PreferenceManager.getDefaultSharedPreferences(this.context);
    }

    public String getData(String label) {
        return mSharedPreference.getString(label, mDefaultStringIfNothingFound);
    }

    public void setData(String label, String data) {
        mSharedPreference.edit().putString(label, data).apply();
    }
}
