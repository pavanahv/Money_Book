package com.example.allakumarreddy.moneybook.broadcastreceivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import com.example.allakumarreddy.moneybook.Services.SmartRemainderIntentService;
import com.example.allakumarreddy.moneybook.utils.GlobalConstants;
import com.example.allakumarreddy.moneybook.utils.LoggerCus;

public class ReportsAlarmReceiver extends BroadcastReceiver {

    private static final String TAG = "ReportsAlarmReceiver";
    private static final String CHANNEL_ID = "MoneyBookNotification";
    private static final int NOTIFICATION_ID = 1001;
    private Context mContext;

    @Override
    public void onReceive(Context context, Intent intent) {
        mContext = context;
        LoggerCus.d(TAG, "onreceive");
        Intent sIntent = new Intent(context, SmartRemainderIntentService.class);
        sIntent.setAction(GlobalConstants.ACTION_REPORT_NOTIFICATION);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(sIntent);
        } else {
            context.startService(sIntent);
        }
    }
}
