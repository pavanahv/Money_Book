package com.example.allakumarreddy.moneybook.interfaces;

import android.view.View;

import com.example.allakumarreddy.moneybook.utils.ReportData;

public interface ReportsAdapterInterface {
    void onGraphClicked(int position, View childItem, ReportData item);
}
