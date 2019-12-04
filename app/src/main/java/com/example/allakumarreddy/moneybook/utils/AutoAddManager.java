package com.example.allakumarreddy.moneybook.utils;

import android.content.Context;

import com.example.allakumarreddy.moneybook.storage.db.DbHandler;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class AutoAddManager {

    private static final String TAG = AutoAddManager.class.getSimpleName();
    private final Context mContext;
    private final DbHandler db;

    public AutoAddManager(Context context) {
        this.mContext = context;
        db = new DbHandler(context);
    }

    public void process() {
        long currentTime = new Date().getTime();
        ArrayList<AutoAddRecord> list = db.getAutoAddRecords();
        for (AutoAddRecord record : list) {
            long time = record.getDate();
            boolean isAdded = false;
            while (time <= currentTime) {
                if (!isAdded)
                    isAdded = true;
                addRecordToDB(time, record);
                time = updateTime(time, record);
            }
            if (isAdded) {
                boolean res = db.updateAutoAddRecordTime(time, record.getName());
                if (!res) {
                    LoggerCus.d(TAG, "Error : Got error in updating time in date field in autoadd table");
                }
            }
        }
    }

    private void log(long time) {
        LoggerCus.d(TAG, "Time : " + time + " -> Date : " + new Date(time));
    }

    private long updateTime(long time, AutoAddRecord record) {
        int freq = record.getFreq();
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(time);
        switch (freq) {

            /*
            0 -> Daily
            so Just adding a day in a year will go to next day
             */
            case 0:
                cal.add(Calendar.DAY_OF_YEAR, 1);
                break;

            /*
             1 -> Weekly
             so Just add one more week so it will go to next week
             And set the day which user selected
             Since user selects in array which starts from sunday that is 0
             but in calender API sunday is 1. So just add 1 to it
             */
            case 1:
                cal.add(Calendar.WEEK_OF_MONTH, 1);
                cal.set(Calendar.DAY_OF_WEEK,
                        Integer.parseInt(record.getFreqData()) + 1);
                break;

            /*
             2 -> Monthly
             so Just add 1 to month so that it will go to next month
             then set specific day from 0 to 31 based on month. Since user
             is not shown with month minimum case should be taken so 28 is taken
             and user selects from array of numbers from 1 to 28 so since index starts
             from 0 plus 1 is added
             */
            case 2:
                cal.add(Calendar.MONTH, 1);
                cal.set(Calendar.DAY_OF_MONTH,
                        Integer.parseInt(record.getFreqData()) + 1);
                break;

            /*
             3 -> Yearly
             so Just add 1 to the year field it will go to next year
             then set month and day which is stored as long TimeInMills in db
             create calender from that and set to original and return
             */
            case 3:
                Calendar tempCal = Calendar.getInstance();
                tempCal.setTimeInMillis(Long.parseLong(record.getFreqData()));
                cal.add(Calendar.YEAR, 1);
                cal.set(Calendar.MONTH, tempCal.get(Calendar.MONTH));
                cal.set(Calendar.DAY_OF_MONTH, tempCal.get(Calendar.DAY_OF_MONTH));
                break;
        }
        return cal.getTimeInMillis();
    }

    private void addRecordToDB(long time, AutoAddRecord record) {
        LoggerCus.d(TAG, record.toString());
        MBRecord mbRecord = new MBRecord(record.getDesciption(),
                record.getAmount(),
                new Date(time),
                record.getType(),
                record.getCategory(),
                record.getPaymentMethod());
        db.addRecord(mbRecord, record.getType());
    }

}
