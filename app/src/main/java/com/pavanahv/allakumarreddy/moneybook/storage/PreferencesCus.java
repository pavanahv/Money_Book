package com.pavanahv.allakumarreddy.moneybook.storage;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.pavanahv.allakumarreddy.moneybook.R;
import com.pavanahv.allakumarreddy.moneybook.utils.GlobalConstants;
import com.pavanahv.allakumarreddy.moneybook.utils.LoggerCus;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
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

    public void saveGoogleDriveBackypFileName(String data) {
        mSharedPreference.edit().putString(GlobalConstants.PREF_GOOGLE_DRIVE_BACKUP_FILE_NAME, data).apply();
    }

    public String getGoogleDriveBackypFileName() {
        return mSharedPreference.getString(GlobalConstants.PREF_GOOGLE_DRIVE_BACKUP_FILE_NAME, mDefaultStringIfNothingFound);
    }

    public void saveGoogleDriveBackypFileSize(String data) {
        mSharedPreference.edit().putString(GlobalConstants.PREF_GOOGLE_DRIVE_BACKUP_FILE_SIZE, data).apply();
    }

    public String getGoogleDriveBackypFileSize() {
        return mSharedPreference.getString(GlobalConstants.PREF_GOOGLE_DRIVE_BACKUP_FILE_SIZE, mDefaultStringIfNothingFound);
    }

    public void saveGoogleDriveBackypFileDate() {

        mSharedPreference.edit().putString(GlobalConstants.PREF_GOOGLE_DRIVE_BACKUP_FILE_DATE, new SimpleDateFormat(" dd / MM / yyyy  HH:mm:ss ").format(new Date())).apply();
    }

    public String getGoogleDriveBackypFileDate() {
        return mSharedPreference.getString(GlobalConstants.PREF_GOOGLE_DRIVE_BACKUP_FILE_DATE, mDefaultStringIfNothingFound);
    }

    public int getGoogleDriveBackupFrequency() {
        try {
            return Integer.parseInt(mSharedPreference.getString(GlobalConstants.PREF_BACKUP_FREQUENCY, mDefaultStringIfNothingFound));
        } catch (Exception e) {
            LoggerCus.d(TAG, e.getMessage());
            return -1;
        }
    }

    public void setLockPinData(int pin) {
        mSharedPreference.edit().putInt(GlobalConstants.PREF_LOCK_PIN, pin).apply();
    }

    public int getLockPinData() {
        return mSharedPreference.getInt(GlobalConstants.PREF_LOCK_PIN, -1);
    }

    public void setCurrentDate(long val) {
        mSharedPreference.edit().putLong(GlobalConstants.PREF_CURRENT_DATE, val).apply();
    }

    public Date getCurrentDate() {
        long val = mSharedPreference.getLong(GlobalConstants.PREF_CURRENT_DATE, -1);
        if (val == -1)
            return null;
        else {
            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(val);
            return cal.getTime();
        }
    }

    public boolean getRestoreStatus() {
        return mSharedPreference.getBoolean(GlobalConstants.RESTORE_DONE, false);
    }

    public void setRestoreStatus() {
        mSharedPreference.edit().putBoolean(GlobalConstants.RESTORE_DONE, true).apply();
    }

    public void setLockSmartPinData(String lockData) {
        mSharedPreference.edit().putString(GlobalConstants.PREF_LOCK_SMART_PIN, lockData).apply();
    }

    public String getLockSmartPinData() {
        return mSharedPreference.getString(GlobalConstants.PREF_LOCK_SMART_PIN, null);
    }

    public boolean getMessageParserNotificationStatus() {
        return mSharedPreference.getBoolean(GlobalConstants.PREF_MSG_PARSER_SWITCH, false);
    }

    public int getTheme() {
        return Integer.parseInt(mSharedPreference.getString(GlobalConstants.PREF_DISPLAY_THEME, "0"));
    }

    public int getColor(String pref) {
        String res = mSharedPreference.getString(pref, null);
        if (res == null) {
            switch (pref) {
                case GlobalConstants.PREF_DISPLAY_SPENT:
                case GlobalConstants.PREF_DISPLAY_DUE_PAYMENT:
                case GlobalConstants.PREF_DISPLAY_LOAN_PAYMENT:
                    return context.getResources().getColor(R.color.spent);
                case GlobalConstants.PREF_DISPLAY_EARN:
                    return context.getResources().getColor(R.color.earn);
                case GlobalConstants.PREF_DISPLAY_DUE:
                    return context.getResources().getColor(R.color.due);
                case GlobalConstants.PREF_DISPLAY_LOAN:
                    return context.getResources().getColor(R.color.loan);
                case GlobalConstants.PREF_DISPLAY_MONEY_TRANSFER:
                    return context.getResources().getColor(R.color.money_transfer);
                case GlobalConstants.PREF_DISPLAY_DAY_WISE:
                    return context.getResources().getColor(R.color.day_wise);
                case GlobalConstants.PREF_DISPLAY_MONTH_WISE:
                    return context.getResources().getColor(R.color.month_wise);
                case GlobalConstants.PREF_DISPLAY_YEAR_WISE:
                    return context.getResources().getColor(R.color.year_wise);
            }
        }
        return Integer.parseInt(res);
    }

    public void setColor(String pref, int color) {
        mSharedPreference.edit().putString(pref, color + "").apply();
    }
}
