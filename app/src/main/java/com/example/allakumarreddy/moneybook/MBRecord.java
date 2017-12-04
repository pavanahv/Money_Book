package com.example.allakumarreddy.moneybook;

import java.util.Date;

/**
 * Created by alla.kumarreddy on 7/20/2017.
 */

public class MBRecord {

    private String Description;
    private int amount;
    private Date date;

    public MBRecord(String description, int amount, Date date) {
        this.Description = description;
        this.amount = amount;
        this.date = date;
        LoggerCus.d("MBRecord",description+":"+amount+":"+date);
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
