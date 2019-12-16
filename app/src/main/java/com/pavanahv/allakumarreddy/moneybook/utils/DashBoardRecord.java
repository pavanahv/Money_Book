package com.pavanahv.allakumarreddy.moneybook.utils;

/**
 * Created by alla.kumarreddy on 09-Apr-18.
 */

public class DashBoardRecord {

    private String text;

    private int totald;
    private int totalm;
    private int totaly;

    private int day;
    private int month;
    private int year;

    private int balanceLeft;

    public DashBoardRecord(String text, int totald, int totalm, int totaly, int day, int month, int year, int balanceLeft) {
        this.text = text;
        this.totald = totald;
        this.totalm = totalm;
        this.totaly = totaly;
        this.day = day;
        this.month = month;
        this.year = year;
        this.balanceLeft = balanceLeft;
    }

    public int getBalanceLeft() {
        return balanceLeft;
    }

    public void setBalanceLeft(int balanceLeft) {
        this.balanceLeft = balanceLeft;
    }

    public DashBoardRecord() {
        this.text = "";
        this.totald = 0;
        this.totalm = 0;
        this.totaly = 0;
        this.day = 0;
        this.month = 0;
        this.year = 0;
        this.balanceLeft = 0;
    }

    public int getPercentD() {
        return (int) (((float) this.day / this.totald) * 100);
    }

    public int getPercentM() {
        return (int) (((float) this.month / this.totalm) * 100);
    }

    public int getPercentY() {
        return (int) (((float) this.year / this.totaly) * 100);
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getTotald() {
        return totald;
    }

    public void setTotald(int totald) {
        this.totald = totald;
    }

    public int getTotalm() {
        return totalm;
    }

    public void setTotalm(int totalm) {
        this.totalm = totalm;
    }

    public int getTotaly() {
        return totaly;
    }

    public void setTotaly(int totaly) {
        this.totaly = totaly;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }
}

