package com.pavanahv.allakumarreddy.moneybook.interfaces;

import android.view.View;

import com.pavanahv.allakumarreddy.moneybook.utils.ReportData;

public interface ReportsAdapterInterface {
    void onGraphClicked(int position, View childItem, ReportData item);
}
