package com.example.allakumarreddy.moneybook.broadcastreceivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.example.allakumarreddy.moneybook.utils.LoggerCus;
import com.example.allakumarreddy.moneybook.utils.Utils;

public class BootUpCompleteReceiver extends BroadcastReceiver {

    private static final String TAG = "bootupcompletereceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        LoggerCus.d(TAG, "bootup completed");
        Utils.setAlarmForGoogleDriveBackup(context);
    }
}
