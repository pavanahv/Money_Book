package com.pavanahv.allakumarreddy.moneybook.broadcastreceivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;

import com.pavanahv.allakumarreddy.moneybook.Services.BackupToGoogleDriveService;
import com.pavanahv.allakumarreddy.moneybook.utils.GlobalConstants;
import com.pavanahv.allakumarreddy.moneybook.utils.LoggerCus;

public class NetworkConnectionReceiver extends BroadcastReceiver {

    private static final String TAG = NetworkConnectionReceiver.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        LoggerCus.d(TAG, "on receive");
        if (intent.getAction().equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
            boolean noConnectivity = intent.getBooleanExtra(
                    ConnectivityManager.EXTRA_NO_CONNECTIVITY, false);

            if (!noConnectivity) {
                LoggerCus.d(TAG, "CONNECTIVITY CHANGED");
                Intent lIntent = new Intent(context, BackupToGoogleDriveService.class);
                lIntent.setAction(GlobalConstants.ACTION_INTERNET_CONNECTED);
                context.startService(lIntent);
            }
        }

    }
}
