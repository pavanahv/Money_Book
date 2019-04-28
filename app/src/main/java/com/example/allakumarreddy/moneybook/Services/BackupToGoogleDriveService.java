package com.example.allakumarreddy.moneybook.Services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;

import com.example.allakumarreddy.moneybook.R;
import com.example.allakumarreddy.moneybook.backup.Backup;
import com.example.allakumarreddy.moneybook.backup.GoogleDriveBackup;
import com.example.allakumarreddy.moneybook.storage.PreferencesCus;
import com.example.allakumarreddy.moneybook.utils.GlobalConstants;
import com.example.allakumarreddy.moneybook.utils.LoggerCus;
import com.google.api.client.googleapis.media.MediaHttpUploaderProgressListener;

public class BackupToGoogleDriveService extends Service {

    private static final String TAG = "BackupToGoogleDriveService";
    private static final String NOTIFICATION_CHANNEL_ID = "NOTIFICATION_CHANNEL_ID_1";
    private static final String NOTIFICATION_CHANNEL_NAME = "NOTIFICATION_CHANNEL_NAME_BACKUP";
    private static final int NOTIFICATION_ID = 1;
    private NotificationManager notificationManager;
    private PreferencesCus mPref;

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
                    startForegroundServiceForBackupToGoogleDrive();
                    LoggerCus.d(TAG, "foreground service started for backup to google drive");
                    break;
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }

    private void startForegroundServiceForBackupToGoogleDrive() {
        LoggerCus.d(TAG, "Start foreground service.");

        Intent notificationIntent = new Intent();
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID);
        builder.setSmallIcon(R.drawable.icon_backup);
        builder.setContentTitle("Backup To GoogleDrive");
        builder.setContentText("Uploading To Google Drive");
        builder.setContentIntent(pendingIntent);
        builder.setOngoing(true);
        builder.setProgress(100, 0, true);

        Notification notification = builder.build();
        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= 26) {
            NotificationChannel channel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, NOTIFICATION_CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription("NOTIFICATION_CHANNEL_DESCRIPTION");
            notificationManager.createNotificationChannel(channel);
        }
        startForeground(NOTIFICATION_ID, notification);
        backupToDrive(builder);
    }

    private void backupToDrive(NotificationCompat.Builder builder) {
        new Thread(() -> {
            Backup backup = new Backup(BackupToGoogleDriveService.this);
            backup.send();
            GoogleDriveBackup gdb = new GoogleDriveBackup(BackupToGoogleDriveService.this);
            MediaHttpUploaderProgressListener uploadProgressCallback = uploader -> {
                LoggerCus.d(TAG, uploader.getNumBytesUploaded() + "");
                int percent = (int) uploader.getProgress();
                builder.setProgress(100, percent, false);
                builder.setContentText("" + uploader.getNumBytesUploaded() + " Bytes " + " " + percent + "%");
                notificationManager.notify(NOTIFICATION_ID, builder.build());
            };
            mPref.saveGoogleDriveBackypFileSize(backup.getBackupFile().length()+" Bytes");
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
}
