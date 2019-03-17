package com.example.allakumarreddy.moneybook.storage;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.example.allakumarreddy.moneybook.utils.LoggerCus;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by alla.kumarreddy on 12-Apr-18.
 */

public class PreferencesCus {

    private static final String MsgParsedDate = "MsgParsedDate";
    private static final String TAG = "PreferenceCus";
    private final SimpleDateFormat sdf;
    private SharedPreferences mSharedPreference;
    private Context context;
    private final String mDefaultStringIfNothingFound = null;

    public PreferencesCus(Context context) {
        this.context = context;
        mSharedPreference = PreferenceManager.getDefaultSharedPreferences(this.context);
        sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
    }

    public String getData(String label) {
        return mSharedPreference.getString(label, mDefaultStringIfNothingFound);
    }

    public void setData(String label, String data) {
        mSharedPreference.edit().putString(label, data).apply();
    }

    public void saveMsgParsedDate(String date) {
        LoggerCus.d(TAG, "New Date Saving : " + date);
        mSharedPreference.edit().putString(MsgParsedDate, date).apply();
    }

    public Date getMsgParsedDate() {
        String dateStr = mSharedPreference.getString(MsgParsedDate, sdf.format(new Date()));
        Date date = new Date();
        try {
            date = sdf.parse(dateStr);
        } catch (ParseException e) {
            LoggerCus.d(TAG, e.getMessage());
        }
        LoggerCus.d(TAG, "Old Date : " + date);
        saveMsgParsedDate(sdf.format(new Date()));
        return date;
    }
}
