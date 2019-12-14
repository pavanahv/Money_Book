package com.example.allakumarreddy.moneybook.Activities;

import android.support.v7.app.AppCompatActivity;

import com.example.allakumarreddy.moneybook.R;

public class BaseActivity extends AppCompatActivity {

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.move_left_in_activity, R.anim.move_right_out_activity);
    }

}
