package com.pavanahv.allakumarreddy.moneybook.Services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;

import com.google.api.client.googleapis.media.MediaHttpUploaderProgressListener;
import com.pavanahv.allakumarreddy.moneybook.R;
import com.pavanahv.allakumarreddy.moneybook.broadcastreceivers.NetworkConnectionReceiver;
import com.pavanahv.allakumarreddy.moneybook.storage.PreferencesCus;
import com.pavanahv.allakumarreddy.moneybook.utils.Backup;
import com.pavanahv.allakumarreddy.moneybook.utils.GlobalConstants;
import com.pavanahv.allakumarreddy.moneybook.utils.GoogleDriveBackup;
import com.pavanahv.allakumarreddy.moneybook.utils.LoggerCus;
import com.pavanahv.allakumarreddy.moneybook.utils.Utils;

public class BackupToGoogleDriveService extends Service {

    private static final String TAG = "BackupToGoogleDriveService";
    private static final String NOTIFICATION_CHANNEL_ID = "NOTIFICATION_CHANNEL_ID_1";
    private static final String NOTIFICATION_CHANNEL_NAME = "NOTIFICATION_CHANNEL_NAME_BACKUP";
    private static final int NOTIFICATION_ID = 1;
    private NotificationManager notificationManager;
    private PreferencesCus mPref;
    private boolean isWaitingForInternet = false;
    private NotificationCompat.Builder builder;
    private NetworkConnectionReceiver receiver;
    private boolean isRegesteredForInternet = false;
    private String mAction;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            String action = intent.getAction();
            mPref = new PreferencesCus(this);
            switch (action) {
                case GlobalConstants.BACKUP_TO_GOOGLE_DRIVE:
                    mAction = GlobalConstants.BACKUP_TO_GOOGLE_DRIVE;
                    LoggerCus.d(TAG, "foreground service started for backup to google drive");
                    startForegroundServiceForBackupToGoogleDrive();
                    break;
                case GlobalConstants.ACTION_INTERNET_CONNECTED:
                    mAction = GlobalConstants.ACTION_INTERNET_CONNECTED;
                    resumeBackup();
                    break;
                case GlobalConstants.ACTION_STOP_BACKUP_SERVICE:
                    stopForegroundService();
                    break;
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }

    private void resumeBackup() {
        if (isWaitingForInternet) {
            isWaitingForInternet = false;
            startBackupToDriveIfInternetAvailable();
        }
    }

    private void startForegroundServiceForBackupToGoogleDrive() {
        LoggerCus.d(TAG, "Starting foreground service.");

        Intent notificationIntent = new Intent();
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
        builder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID);
        builder.setSmallIcon(R.drawable.icon_backup);
        builder.setContentTitle("Backup To GoogleDrive");
        //builder.setContentText("Uploading To Google Drive");
        builder.setContentIntent(pendingIntent);

        Notification notification = builder.build();
        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= 26) {
            NotificationChannel channel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, NOTIFICATION_CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription("NOTIFICATION_CHANNEL_DESCRIPTION");
            notificationManager.createNotificationChannel(channel);
        }
        builder.setOngoing(true);
        builder.setPriority(NotificationCompat.PRIORITY_LOW);
        builder.setProgress(100, 0, true);
        startForeground(NOTIFICATION_ID, notification);
        startBackupToDriveIfInternetAvailable();
    }

    private void startBackupToDriveIfInternetAvailable() {
        LoggerCus.d(TAG, "Checking for Internet");
        Callback callback = isOn -> {
            if (isOn) {
                unRegisterBroadCast();
                backupToDrive();
            } else {
                if (mAction.compareToIgnoreCase(GlobalConstants.BACKUP_TO_GOOGLE_DRIVE) == 0) {
                    LoggerCus.d(TAG, "No internet so waiting for network");
                    builder.setContentText("Waiting for Network!");
                    notificationManager.notify(NOTIFICATION_ID, builder.build());
                    initBroadCast();
                    isWaitingForInternet = true;
                }
            }
        };

        Utils.isOnline(BackupToGoogleDriveService.this, callback);
    }

    private void unRegisterBroadCast() {
        if (isRegesteredForInternet) {
            try {
                unregisterReceiver(receiver);
            } catch (Exception exception) {
                LoggerCus.d(TAG, exception.getMessage());
            }
        }
    }


    private void initBroadCast() {
        isRegesteredForInternet = true;
        receiver = new NetworkConnectionReceiver();
        IntentFilter ifilter = new IntentFilter();
        ifilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(receiver, ifilter);
    }

    private void backupToDrive() {
        new Thread(() -> {
            Backup backup = new Backup(BackupToGoogleDriveService.this);
            backup.send();
            GoogleDriveBackup gdb = new GoogleDriveBackup(BackupToGoogleDriveService.this);
            MediaHttpUploaderProgressListener uploadProgressCallback = uploader -> {
                LoggerCus.d(TAG, uploader.getNumBytesUploaded() + "");
                int percent = (int) (uploader.getProgress() * 100);
                builder.setProgress(100, percent, false);
                builder.setContentText("" + uploader.getNumBytesUploaded() + " Bytes " + " " + percent + "%");
                notificationManager.notify(NOTIFICATION_ID, builder.build());
            };
            mPref.saveGoogleDriveBackypFileSize(backup.getBackupFile().length() + " Bytes");
            gdb.backup(uploadProgressCallback);
        }).start();
    }

    private void stopForegroundService() {
        LoggerCus.d(TAG, "Stop foreground service.");

        // Stop foreground service and remove the notification.
        stopForeground(true);

        // Stop the foreground service.
        stopSelf();
    }

    public interface Callback {
        void isInternetAvilable(boolean isOn);
    }
}
