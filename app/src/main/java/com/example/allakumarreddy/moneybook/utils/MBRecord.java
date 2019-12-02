package com.example.allakumarreddy.moneybook.utils;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by alla.kumarreddy on 7/20/2017.
 */

public class MBRecord implements Serializable {

    private String mRefIdForLoanDue = null;

    public String getPaymentMethod() {
        return paymentMethod;
    }

    private String paymentMethod;
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
    private String toCategory;

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

    private int balLeft = 0;

    public MBRecord(String description, int amount, Date date, String category, String paymentMethod) {
        this.Description = description;
        this.amount = amount;
        this.date = date;
        this.type = -1;
        this.category = category;
        this.paymentMethod = paymentMethod;
        LoggerCus.d("MBRecord", description + ":" + amount + ":" + date + ":" + category + ";" + type + ";" + paymentMethod);
    }

    public MBRecord() {
    }

    public MBRecord(String description, int amount, Date date, int type, String category, String paymentMethod) {
        this.Description = description;
        this.amount = amount;
        this.date = date;
        this.type = type;
        this.category = category;
        this.paymentMethod = paymentMethod;
        //LoggerCus.d("MBRecord",description+":"+amount+":"+date+":"+category+";"+type);
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

    public String getToCategory() {
        return toCategory;
    }

    public void setToCategory(String toCategory) {
        this.toCategory = toCategory;
    }

    @Override
    public String toString() {
        return "MBRecord{" +
                "paymentMethod='" + paymentMethod + '\'' +
                ", Description='" + Description + '\'' +
                ", amount=" + amount +
                ", date=" + date +
                ", category='" + category + '\'' +
                ", toCategory='" + toCategory + '\'' +
                ", type=" + type +
                ", balLeft=" + balLeft +
                '}';
    }

    public void setRefIdForLoanDue(String refIdForLoanDue) {
        mRefIdForLoanDue = refIdForLoanDue;
    }

    public String getmRefIdForLoanDue() {
        return mRefIdForLoanDue;
    }
}
