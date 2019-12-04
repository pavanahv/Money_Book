package com.example.allakumarreddy.moneybook.utils;

public class AutoAddRecord {
    private String name;
    private int freq;
    private String freqData;
    private long date;
    private String desciption;
    private int type;
    private int amount;
    private String category;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getFreq() {
        return freq;
    }

    public void setFreq(int freq) {
        this.freq = freq;
    }

    public String getFreqData() {
        return freqData;
    }

    public void setFreqData(String freqData) {
        this.freqData = freqData;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public String getDesciption() {
        return desciption;
    }

    public void setDesciption(String desciption) {
        this.desciption = desciption;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    private String paymentMethod;

    public AutoAddRecord(String name, int freq, String freqData, long date, String desciption, int type, int amount, String category, String paymentMethod) {
        this.name = name;
        this.freq = freq;
        this.freqData = freqData;
        this.date = date;
        this.desciption = desciption;
        this.type = type;
        this.amount = amount;
        this.category = category;
        this.paymentMethod = paymentMethod;
    }

    public AutoAddRecord() {
    }

    @Override
    public String toString() {
        return "AutoAddRecord{" +
                "name='" + name + '\'' +
                ", freq=" + freq +
                ", freqData='" + freqData + '\'' +
                ", date=" + date +
                ", desciption='" + desciption + '\'' +
                ", type=" + type +
                ", amount=" + amount +
                ", category='" + category + '\'' +
                ", paymentMethod='" + paymentMethod + '\'' +
                '}';
    }
}
