package com.example.allakumarreddy.moneybook.broadcastreceivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.example.allakumarreddy.moneybook.Services.BackupToGoogleDriveService;
import com.example.allakumarreddy.moneybook.utils.GlobalConstants;
import com.example.allakumarreddy.moneybook.utils.LoggerCus;
import com.example.allakumarreddy.moneybook.utils.Utils;

public class NetworkConnectionReceiver extends BroadcastReceiver {

    private static final String TAG = "phonechargereceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        LoggerCus.d(TAG, "on receive");
        if(Utils.isOnline(context)){
            Intent lIntent = new Intent(context, BackupToGoogleDriveService.class);
            lIntent.setAction(GlobalConstants.ACTION_INTERNET_CONNECTED);
            context.startService(lIntent);
        }
    }
}
