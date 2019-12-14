package com.example.allakumarreddy.moneybook.utils;

import android.view.View;

import java.io.Serializable;
import java.util.ArrayList;

public class ReportData implements Serializable {

    private View graphView;

    private boolean isPie;

    public boolean isPie() {
        return isPie;
    }

    public void setPie(boolean pie) {
        isPie = pie;
    }

    public ArrayList<AnalyticsFilterData> getAnalyticsFilterDataArrayList() {
        return analyticsFilterDataArrayList;
    }

    public void setAnalyticsFilterDataArrayList(ArrayList<AnalyticsFilterData> analyticsFilterDataArrayList) {
        this.analyticsFilterDataArrayList = analyticsFilterDataArrayList;
    }

    private ArrayList<AnalyticsFilterData> analyticsFilterDataArrayList;

    public ReportData(View graphView, String title, AnalyticsFilterData mAnalyticsFilterData) {
        this.graphView = graphView;
        this.title = title;
        this.mAnalyticsFilterData = mAnalyticsFilterData;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    private String title;

    public View getGraphView() {
        return graphView;
    }

    public void setGraphView(View graphView) {
        this.graphView = graphView;
    }

    public AnalyticsFilterData getmAnalyticsFilterData() {
        return mAnalyticsFilterData;
    }

    public void setmAnalyticsFilterData(AnalyticsFilterData mAnalyticsFilterData) {
        this.mAnalyticsFilterData = mAnalyticsFilterData;
    }

    private AnalyticsFilterData mAnalyticsFilterData;
}
