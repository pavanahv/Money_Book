package com.pavanahv.allakumarreddy.moneybook.Activities;

import android.os.Bundle;

import com.pavanahv.allakumarreddy.moneybook.R;
import com.pavanahv.allakumarreddy.moneybook.utils.ThemeUtils;

public class GoogleDriveActivity extends BaseActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(ThemeUtils.getTheme(getClass().getSimpleName(), this));
        setContentView(R.layout.activity_google_drive);
    }
}
