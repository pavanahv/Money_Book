package com.example.allakumarreddy.moneybook.broadcastreceivers;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.os.Messenger;
import android.os.PowerManager;
import android.support.v4.app.NotificationCompat;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;

import com.example.allakumarreddy.moneybook.Activities.FingerPrintLoginActivity;
import com.example.allakumarreddy.moneybook.R;
import com.example.allakumarreddy.moneybook.Services.MoneyBookIntentService;
import com.example.allakumarreddy.moneybook.Services.MoneyBookIntentServiceHandler;
import com.example.allakumarreddy.moneybook.db.DbHandler;
import com.example.allakumarreddy.moneybook.utils.GlobalConstants;
import com.example.allakumarreddy.moneybook.utils.LoggerCus;
import com.example.allakumarreddy.moneybook.utils.Utils;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

import static com.example.allakumarreddy.moneybook.utils.GlobalConstants.ACTION_MSG_PARSE_BY_DATE;

public class ReportsAlarmReceiver extends BroadcastReceiver {

    private static final String TAG = "ReportsAlarmReceiver";
    private static final String CHANNEL_ID = "MoneyBookNotification";
    private static final int NOTIFICATION_ID = 1001;
    private Context mContext;

    @Override
    public void onReceive(Context context, Intent intent) {
        mContext = context;
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock wl = null;
        if (pm != null) {
            wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "MoneyBook:SmartRemainderAlarmReceiver");
            wl.acquire();
        }

        // Put here YOUR code.
        LoggerCus.d(TAG, "onreceive");
        startMessageParserService();

        wl.release();
    }

    private void startMessageParserService() {
        boolean perm = Utils.checkReadSMSPermissions(mContext);
        if (perm) {
            Intent intent = new Intent(mContext, MoneyBookIntentService.class);
            intent.setAction(ACTION_MSG_PARSE_BY_DATE);
            intent.putExtra(GlobalConstants.HANDLER_NAME, new Messenger(new MoneyBookIntentServiceHandler(msg -> {
                if (msg.what == GlobalConstants.MSG_PARSING_COMPLETED) {
                    performPendingTasks();
                }
            })));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                mContext.startForegroundService(intent);
            } else {
                mContext.startService(intent);
            }
        }
    }

    private void performPendingTasks() {
        int[] res = getTotal();
        LoggerCus.d(TAG, Arrays.toString(res));
        int icons[] = new int[]{R.drawable.spent, R.drawable.earn, R.drawable.due, R.drawable.loan};
        String titles[] = new String[]{"Spent", "Earn", "Due", "Loan"};
        int colors[] = new int[]{Color.parseColor("#d9534f"),
                Color.parseColor("#5bc0de"),
                Color.parseColor("#f0ad4e"),
                Color.parseColor("#8f8f00")};
        for (int i = 0; i < res.length; i++) {
            if (res[i] > 0) {
                createNotification(res[i], icons[i], titles[i], i, colors[i]);
            }
        }
    }

    private void createNotification(int countValue, int icon, String title, int index, int color) {
        int reqCode = (int) System.currentTimeMillis();
        Intent intent = new Intent(mContext, FingerPrintLoginActivity.class);
        intent.putExtra(GlobalConstants.REPORTS_NOTI[index], true);
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
        notificationManager.notify(reqCode, builder.build());
    }

    private int[] getTotal() {
        int res[] = new int[4];
        Date curDate = new Date();
        SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd");
        String curDateStr = format.format(curDate);

        DbHandler db = new DbHandler(mContext);
        for (int i = 0; i < 4; i++) {
            res[i] = db.getTotalOfType(curDateStr, i);
        }
        return res;
    }
}
