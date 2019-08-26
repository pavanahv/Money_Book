package com.example.allakumarreddy.moneybook.broadcastreceivers;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.PowerManager;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;

import com.example.allakumarreddy.moneybook.Activities.FingerPrintLoginActivity;
import com.example.allakumarreddy.moneybook.R;
import com.example.allakumarreddy.moneybook.utils.GlobalConstants;
import com.example.allakumarreddy.moneybook.utils.LoggerCus;

public class SmartRemainderAlarmReceiver extends BroadcastReceiver {

    private static final String TAG = "SmartRemainderAlarmReceiver";
    private static final String CHANNEL_ID = "MoneyBookNotification";
    private static final int NOTIFICATION_ID = 1001;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onReceive(Context context, Intent intent) {
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock wl = null;
        if (pm != null) {
            wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "MoneyBook:SmartRemainderAlarmReceiver");
            wl.acquire();
        }

        // Put here YOUR code.
        LoggerCus.d(TAG, "onreceive");
        showNotification(context);

        wl.release();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void showNotification(Context context) {

        Intent intent = new Intent(context, FingerPrintLoginActivity.class);
        intent.putExtra(GlobalConstants.SMART_REMAINDER_NOTI, true);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context,CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_smart_remainder)
                .setContentTitle("Smart Remainder")
                .setContentText("It's Time To Log Expenses")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "MoneyBook Channel Name";
            String description = "MoneyBook Channel Description";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            notificationManager.createNotificationChannel(channel);
        }
        notificationManager.notify(NOTIFICATION_ID,builder.build());

    }

}
