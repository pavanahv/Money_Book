package com.pavanahv.allakumarreddy.moneybook.Services;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;

import com.pavanahv.allakumarreddy.moneybook.Activities.LoginActivity;
import com.pavanahv.allakumarreddy.moneybook.R;
import com.pavanahv.allakumarreddy.moneybook.storage.db.DbHandler;
import com.pavanahv.allakumarreddy.moneybook.utils.GlobalConstants;
import com.pavanahv.allakumarreddy.moneybook.utils.LoggerCus;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

public class SmartRemainderIntentService extends Service {

    private static final String TAG = "SmartRemainderIntentService";

    @Override
    public void onDestroy() {
        LoggerCus.d(TAG, "stopped...");
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            putNotification();
            final String action = intent.getAction();
            if (GlobalConstants.ACTION_SMART_REMAINDER_NOTIFICATION.equals(action)) {
                handleActionSmartRemainder();
            } else if (GlobalConstants.ACTION_REPORT_NOTIFICATION.equals(action)) {
                handleActionReports();
            }
            stopForeground(true);
            stopSelf();
        }
        return super.onStartCommand(intent, flags, startId);
    }

    private void putNotification() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Context context = getApplicationContext();
            NotificationCompat.Builder builder = new NotificationCompat.Builder(context, GlobalConstants.NOTIFICATION_CHANNLE_ID)
                    .setSmallIcon(R.drawable.ic_smart_remainder)
                    .setContentTitle("Money Book")
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT);

            NotificationManager notificationManager = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                notificationManager = context.getSystemService(NotificationManager.class);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    int importance = NotificationManager.IMPORTANCE_DEFAULT;
                    NotificationChannel channel = new NotificationChannel(GlobalConstants.NOTIFICATION_CHANNLE_ID, GlobalConstants.NOTIFICATION_CHANNLE_NAME, importance);
                    channel.setDescription(GlobalConstants.NOTIFICATION_CHANNLE_DESCRIPTION);
                    notificationManager.createNotificationChannel(channel);
                }
            }
            startForeground((int) System.currentTimeMillis(), builder.build());
        }
        LoggerCus.d(TAG, "started...");
    }

    private void handleActionReports() {
        /*new MessageParserBase(getApplicationContext()).handleActionMsgParse();
        performPendingTasks();*/
        showReportNotification();
    }

    private void showReportNotification() {
        int reqCode = (int) System.currentTimeMillis();
        Context context = getApplicationContext();
        Intent intent = new Intent(context, LoginActivity.class);
        intent.putExtra(GlobalConstants.REPORTS_NOTI, true);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, reqCode, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, GlobalConstants.NOTIFICATION_CHANNLE_ID)
                .setSmallIcon(R.drawable.ic_smart_remainder)
                .setContentTitle("Reports")
                .setContentText("It's Time To Have A Look At Reports !")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            notificationManager = context.getSystemService(NotificationManager.class);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                int importance = NotificationManager.IMPORTANCE_DEFAULT;
                NotificationChannel channel = new NotificationChannel(GlobalConstants.NOTIFICATION_CHANNLE_ID, GlobalConstants.NOTIFICATION_CHANNLE_NAME, importance);
                channel.setDescription(GlobalConstants.NOTIFICATION_CHANNLE_DESCRIPTION);
                notificationManager.createNotificationChannel(channel);
            }
            notificationManager.notify(GlobalConstants.TYPE_NOTIFICATION_ID[0], builder.build());
        }
    }

    private void performPendingTasks() {
        int[] res = getTotal();
        LoggerCus.d(TAG, Arrays.toString(res));
        int icons[] = new int[]{R.drawable.spent, R.drawable.earn, R.drawable.due, R.drawable.ic_loan};
        String titles[] = new String[]{"Spent", "Earn", "Due", "Loan"};
        int colors[] = new int[]{Color.parseColor("#d9534f"),
                Color.parseColor("#5bc0de"),
                Color.parseColor("#f0ad4e"),
                Color.parseColor("#8f8f00")};
        for (int i = 0; i < res.length; i++) {
            if (res[i] > 0) {
                createNotification(res[i], icons[i], titles[i], i, colors[i]);
                break;
            }
        }
    }

    private void createNotification(int countValue, int icon, String title, int index, int color) {
        int reqCode = (int) System.currentTimeMillis();
        Context mContext = getApplicationContext();
        Intent intent = new Intent(mContext, LoginActivity.class);
        //intent.putExtra(GlobalConstants.REPORTS_NOTI[index], true);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(mContext, reqCode, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.BigTextStyle bigText = new NotificationCompat.BigTextStyle();
        String text = title + " : " + countValue;
        Spannable spanString = new SpannableString(text);
        spanString.setSpan(new ForegroundColorSpan(color), 0, text.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        spanString.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), 0, text.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        bigText.bigText(spanString);
        bigText.setSummaryText(title + " Report");

        NotificationCompat.Builder builder = new NotificationCompat.Builder(mContext, "channelid")
                .setSmallIcon(R.drawable.ic_report)
                .setContentTitle("Daily Report")
                .setContentText(spanString)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setLargeIcon(BitmapFactory.decodeResource(mContext.getResources(), icon))
                .setStyle(bigText)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            notificationManager = mContext.getSystemService(NotificationManager.class);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(GlobalConstants.NOTIFICATION_CHANNLE_ID, GlobalConstants.NOTIFICATION_CHANNLE_NAME, importance);
            channel.setDescription(GlobalConstants.NOTIFICATION_CHANNLE_DESCRIPTION);
            notificationManager.createNotificationChannel(channel);
        }
        notificationManager.notify(GlobalConstants.TYPE_NOTIFICATION_ID[index], builder.build());
    }

    private int[] getTotal() {
        int res[] = new int[4];
        Date curDate = new Date();
        SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd");
        String curDateStr = format.format(curDate);

        Context mContext = getApplicationContext();
        DbHandler db = new DbHandler(mContext);
        for (int i = 0; i < 4; i++) {
            res[i] = db.getTotalOfType(curDateStr, i);
        }
        return res;
    }

    private void handleActionSmartRemainder() {
        showNotification();
    }

    private void showNotification() {
        int reqCode = (int) System.currentTimeMillis();
        Context context = getApplicationContext();
        Intent intent = new Intent(context, LoginActivity.class);
        intent.putExtra(GlobalConstants.SMART_REMAINDER_NOTI, true);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, reqCode, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, GlobalConstants.NOTIFICATION_CHANNLE_ID)
                .setSmallIcon(R.drawable.ic_smart_remainder)
                .setContentTitle("Smart Remainder")
                .setContentText("It's Time To Log Expenses")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            notificationManager = context.getSystemService(NotificationManager.class);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                int importance = NotificationManager.IMPORTANCE_DEFAULT;
                NotificationChannel channel = new NotificationChannel(GlobalConstants.NOTIFICATION_CHANNLE_ID, GlobalConstants.NOTIFICATION_CHANNLE_NAME, importance);
                channel.setDescription(GlobalConstants.NOTIFICATION_CHANNLE_DESCRIPTION);
                notificationManager.createNotificationChannel(channel);
            }
            notificationManager.notify(GlobalConstants.SMART_REMAINDER_NOTIFICATION_ID, builder.build());
        }
    }

}
