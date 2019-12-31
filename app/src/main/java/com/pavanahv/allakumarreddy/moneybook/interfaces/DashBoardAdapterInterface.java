package com.pavanahv.allakumarreddy.moneybook.interfaces;

import android.view.View;

public interface DashBoardAdapterInterface {
    void viewOnClick(String dataText, View finalConvertView);

    void deleteCategoryPopMenu(String categoryName);

    void moneyTrasferPopMenu(String categoryName);
}
