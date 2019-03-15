package com.example.allakumarreddy.moneybook.utils;

import java.util.Date;

/**
 * Created by alla.kumarreddy on 7/20/2017.
 */

public class MBRecord {

    private String Description;
    private int amount;
    private Date date;

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    private String category;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    private int type;

    public int getBalLeft() {
        return balLeft;
    }

    public void setBalLeft(int balLeft) {
        this.balLeft = balLeft;
    }

    private int balLeft=0;

    public MBRecord(String description, int amount, Date date, String category) {
        this.Description = description;
        this.amount = amount;
        this.date = date;
        this.type = -1;
        this.category = category;
        //LoggerCus.d("MBRecord",description+":"+amount+":"+date);
    }

    public MBRecord() {
    }

    public MBRecord(String description, int amount, Date date, int type, String category) {
        this.Description = description;
        this.amount = amount;
        this.date = date;
        this.type = type;
        this.category = category;
        //LoggerCus.d("MBRecord",description+":"+amount+":"+date+":"+category);
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
