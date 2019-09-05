package com.example.allakumarreddy.moneybook.broadcastreceivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;

import com.example.allakumarreddy.moneybook.Services.SmartRemainderIntentService;
import com.example.allakumarreddy.moneybook.utils.GlobalConstants;
import com.example.allakumarreddy.moneybook.utils.LoggerCus;

public class SmartRemainderAlarmReceiver extends BroadcastReceiver {

    private static final String TAG = "SmartRemainderAlarmReceiver";

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onReceive(Context context, Intent intent) {
        LoggerCus.d(TAG, "onreceive");
        Intent sIntent = new Intent(context, SmartRemainderIntentService.class);
        sIntent.setAction(GlobalConstants.ACTION_SMART_REMAINDER_NOTIFICATION);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(sIntent);
        } else {
            context.startService(sIntent);
        }
    }

}
