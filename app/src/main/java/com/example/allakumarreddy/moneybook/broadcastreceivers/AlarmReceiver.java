package com.example.allakumarreddy.moneybook.broadcastreceivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.PowerManager;

import com.example.allakumarreddy.moneybook.Services.BackupToGoogleDriveService;
import com.example.allakumarreddy.moneybook.utils.GlobalConstants;
import com.example.allakumarreddy.moneybook.utils.LoggerCus;

public class AlarmReceiver extends BroadcastReceiver {

    private static final String TAG = "alarmreceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock wl = null;
        if (pm != null) {
            wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "");
            wl.acquire();
        }

        // Put here YOUR code.
        LoggerCus.d(TAG, "onreceive");
        backupToGoogleDrive(context);

        wl.release();
    }

    private void backupToGoogleDrive(Context context) {
        Intent intent = new Intent(context, BackupToGoogleDriveService.class);
        intent.setAction(GlobalConstants.BACKUP_TO_GOOGLE_DRIVE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(intent);
        } else {
            context.startService(intent);
        }
    }
}
