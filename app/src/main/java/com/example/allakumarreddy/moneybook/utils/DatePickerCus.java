package com.example.allakumarreddy.moneybook.utils;

/**
 * Created by alla.kumarreddy on 7/25/2017.
 */

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.DatePicker;

import com.example.allakumarreddy.moneybook.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DatePickerCus extends Dialog implements android.view.View.OnClickListener {

    final static String TAG = "DatePickerDialog";
    private IDate idate;
    private Date date;
    private DatePicker dp;
    private Button add, cancel;
    private Calendar cal = Calendar.getInstance();

    public DatePickerCus(Activity activity,IDate idate) {
        super(activity);
        this.idate=idate;
    }

    public DatePickerCus(Activity activity,IDate idate, Date date) {
        super(activity);
        this.idate = idate;
        this.date=date;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.date_picker_cus);
        dp=(DatePicker)findViewById(R.id.datePicker);
        LoggerCus.d(TAG," "+Integer.parseInt(new SimpleDateFormat("yyyy").format(date))+" "+date.getMonth()+" "+date.getDate());
        dp.init(Integer.parseInt(new SimpleDateFormat("yyyy").format(date)),date.getMonth(),date.getDate(),null);
        add = (Button) findViewById(R.id.addButtonDate);
        cancel = (Button) findViewById(R.id.cancelButtonDate);
        add.setOnClickListener(this);
        cancel.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.addButtonDate:
                String des = dp.getYear()+"/"+(dp.getMonth()+1)+"/"+dp.getDayOfMonth();
                LoggerCus.d(TAG, des);
                idate.afterDateSelection(des);
                break;
            default:
                break;
        }
        dismiss();
    }
}
