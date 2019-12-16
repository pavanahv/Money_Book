package com.pavanahv.allakumarreddy.moneybook.broadcastreceivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;

import com.pavanahv.allakumarreddy.moneybook.utils.LoggerCus;
import com.pavanahv.allakumarreddy.moneybook.utils.Utils;

public class BootUpCompleteReceiver extends BroadcastReceiver {

    private static final String TAG = "bootupcompletereceiver";

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onReceive(Context context, Intent intent) {
        LoggerCus.d(TAG, "bootup completed");
        Utils.setAlarmForGoogleDriveBackup(context);
        Utils.setAlarmForReportsRemainder(context);
        Utils.setAlarmForReportsNotification(context);
    }
}
