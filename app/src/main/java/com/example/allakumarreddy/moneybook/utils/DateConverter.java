package com.example.allakumarreddy.moneybook.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by alla.kumarreddy on 08-Apr-18.
 */

public class DateConverter {
    String date = "";

    public Date getmDate() {
        return mDate;
    }

    public void setmDate(Date mDate) {
        this.mDate = mDate;
    }

    public Date getyDate() {
        return yDate;
    }

    public void setyDate(Date yDate) {
        this.yDate = yDate;
    }

    Date mDate = null;
    Date yDate = null;
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");

    public DateConverter(Date dDate) {
        this.dDate = dDate;
        this.dateToString();
    }

    public DateConverter(String date) {
        this.date = date;
        this.stringToDate();
    }

    private void dateToString() {
        date = sdf.format(dDate);
        Calendar cal = Calendar.getInstance();
        cal.setTime(dDate);
        month = cal.get(Calendar.MONTH) + 1;
        day = cal.get(Calendar.DATE);
        year = cal.get(Calendar.YEAR);
        cal = Calendar.getInstance();
        cal.setTime(dDate);
        cal.set(Calendar.DAY_OF_MONTH, 1);
        mDate = cal.getTime();
        cal.set(Calendar.MONTH, 0);
        yDate = cal.getTime();
    }

    Date dDate = null;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Date getdDate() {
        return dDate;
    }

    public void setdDate(Date dDate) {
        this.dDate = dDate;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    int month = 0, day = 0, year = 0;


    public void stringToDate() {
        try {
            dDate = sdf.parse(date);
            Calendar cal = Calendar.getInstance();
            cal.setTime(dDate);
            month = cal.get(Calendar.MONTH) + 1;
            day = cal.get(Calendar.DATE);
            year = cal.get(Calendar.YEAR);
            cal = Calendar.getInstance();
            cal.setTime(dDate);
            cal.set(Calendar.DAY_OF_MONTH, 1);
            mDate = cal.getTime();
            cal.set(Calendar.MONTH, 0);
            yDate = cal.getTime();
        } catch (Exception e) {
            LoggerCus.d("DateConverter",e.getMessage());
        }
    }
}
