package com.example.allakumarreddy.moneybook;

/**
 * Created by alla.kumarreddy on 7/25/2017.
 */

import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.DatePicker;

public class DatePickerCus extends Dialog implements android.view.View.OnClickListener {

    final static String TAG = "DatePickerDialog";
    private final MainActivity activity;
    private DatePicker dp;
    private Button add, cancel;

    public DatePickerCus(MainActivity activity) {
        super(activity);
        this.activity = activity;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.date_picker_cus);
        dp=(DatePicker)findViewById(R.id.datePicker);
        add = (Button) findViewById(R.id.addButtonDate);
        cancel = (Button) findViewById(R.id.cancelButtonDate);
        add.setOnClickListener(this);
        cancel.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.addButtonDate:
                String des = dp.getYear()+"/"+dp.getMonth()+"/"+dp.getDayOfMonth();
                LoggerCus.d(TAG, des);
                activity.afterDate(des);
                break;
            default:
                break;
        }
        dismiss();
    }
}
