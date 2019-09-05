package com.example.allakumarreddy.moneybook.utils;

import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;
import android.util.TypedValue;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.allakumarreddy.moneybook.broadcastreceivers.AlarmReceiver;
import com.example.allakumarreddy.moneybook.broadcastreceivers.ReportsAlarmReceiver;
import com.example.allakumarreddy.moneybook.broadcastreceivers.SmartRemainderAlarmReceiver;
import com.example.allakumarreddy.moneybook.db.DbHandler;
import com.example.allakumarreddy.moneybook.storage.PreferencesCus;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.charts.RadarChart;
import com.github.mikephil.charting.charts.ScatterChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.RadarData;
import com.github.mikephil.charting.data.RadarDataSet;
import com.github.mikephil.charting.data.ScatterData;
import com.github.mikephil.charting.data.ScatterDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by alla.kumarreddy on 09-Apr-18.
 */

public class Utils {

    private static final String TAG = "Utils";
    private static String email = "EMAIL";
    private static String appFolder = "APP_FOLDER";

    public static String getBackupFile() {
        return backupFile;
    }

    public static void setBackupFile(String backupFile) {
        Utils.backupFile = backupFile;
    }

    private static String backupFile = "backup.JSON";

    public static String getEmail() {
        return email;
    }

    public static void setEmail(String email) {
        Utils.email = email;
    }

    public static String getAppFolder() {
        return appFolder;
    }

    public static void setAppFolder(String appFolder) {
        Utils.appFolder = appFolder;
    }


    public static float getDp(Context context, int px) {
        Resources r = context.getResources();
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, px, r.getDisplayMetrics());
    }

    public static String getFormattedNumber(int value) {
        String formattedString = "";
        if (value <= 999)
            formattedString = Long.toString(value);
        else {
            String thousandsPart = (value % 1000) + "";
            int len = thousandsPart.length();
            if (len < 3) {
                switch (len) {
                    case 2:
                        thousandsPart = "0" + thousandsPart;
                        break;

                    case 1:
                        thousandsPart = "00" + thousandsPart;
                        break;
                }
            }
            long rest = value / 1000;
            NumberFormat format = new DecimalFormat("##,##");
            formattedString = format.format(rest);
            formattedString += "," + thousandsPart;
        }
        return formattedString;
    }

    public static String getTrimmedString(String s, int len) {
        if (s.length() > len)
            return s.substring(0, len);
        else
            return s;
    }

    public static int castFloat2IntRemovingCommas(String s) {
        return (int) Float.parseFloat(s.replaceAll(",", ""));
    }

    public static boolean checkReadWriteStoragePermissions(Context context) {
        if ((ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED) &&
                (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE)
                        == PackageManager.PERMISSION_GRANTED)) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean checkReadSMSPermissions(Context context) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_SMS)
                == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean isOnline(Context context) {
        boolean status = false;
        ConnectivityManager connectivityManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager
                .getActiveNetworkInfo();

        // Check internet connection and accrding to state change the
        if (networkInfo != null && networkInfo.isConnected()) {
            status = true;
            LoggerCus.d(TAG, "Connected to internet");
            Toast.makeText(context, "MoneyBook : Connected to Internet", Toast.LENGTH_SHORT).show();
        }
        return status;
    }

    public static void setAlarmForGoogleDriveBackup(Context context) {
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent i = new Intent(context, AlarmReceiver.class);
        PendingIntent pi = PendingIntent.getBroadcast(context, (int) System.currentTimeMillis(), i, 0);
        int freq = new PreferencesCus(context).getGoogleDriveBackupFrequency();
        if (freq != -1) {
            int millsec = 1000 * 60 * 60;
            switch (freq) {
                case 0:
                    millsec *= 6;
                    break;
                case 1:
                    millsec *= 12;
                    break;
                case 2:
                    millsec *= 24;
                    break;
                case 3:
                    millsec *= 24 * 7;
                    break;
            }
            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(System.currentTimeMillis());
            cal.set(Calendar.HOUR, 6);
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.SECOND, 0);
            am.setRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), millsec, pi); // Millisec * Second * Minute
            Toast.makeText(context, "Backup Frequency Set Successfully!", Toast.LENGTH_LONG).show();
        }
    }

    public static void cancelAlarmForGoogleDriveBackup(Context context) {
        Intent intent = new Intent(context, AlarmReceiver.class);
        PendingIntent sender = PendingIntent.getBroadcast(context, 0, intent, 0);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(sender);
    }

    public static View getGraphFromParelableJSONString(String s, Context context) {

        ArrayList<MBRecord> dataList = getFilterRecordsFromParelableJSONString(s, context);
        int graphType = getFilterGraphType(s);

        final int size = dataList.size();
        String[] label = new String[size];
        String[] data = new String[size];
        for (int i = 0; i < size; i++) {
            MBRecord mbr = dataList.get(i);
            label[i] = mbr.getDescription();
            data[i] = mbr.getAmount() + "";
        }
        View view = null;
        switch (graphType) {
            case 0:
                view = drawLineGraph(data.length, label, data, context);
                break;
            case 1:
                view = drawBarGraph(data.length, label, data, context);
                break;
            case 2:
                view = drawPieGraph(data.length, label, data, context);
                break;
            case 3:
                view = drawRadarGraph(data.length, label, data, context);
                break;
            case 4:
                view = drawScatterGraph(data.length, label, data, context);
                break;
        }
        if (view != null)
            return view;

        TextView tv = new TextView(context);
        tv.setText("No Filters Yet");
        return tv;
    }

    public static ArrayList<MBRecord> getFilterRecordsFromParelableJSONString(String s, Context context) {
        JSONObject jobj = null;
        try {
            jobj = new JSONObject(s);
            String queryText = jobj.getString("queryText");
            boolean dateAll = jobj.getBoolean("dateAll");
            SimpleDateFormat jformat = new SimpleDateFormat("yyyy/MM/dd");
            Date sDate = jformat.parse(jobj.getString("sDate"));
            Date eDate = jformat.parse(jobj.getString("eDate"));

            JSONArray jarrDataDataBool = jobj.getJSONArray("dateDataBool");
            boolean dateDataBool[] = new boolean[jarrDataDataBool.length()];
            for (int i = 0; i < jarrDataDataBool.length(); i++) {
                dateDataBool[i] = jarrDataDataBool.getJSONObject(i).getBoolean("element" + i);
            }

            JSONArray jarrmenuTypeBool = jobj.getJSONArray("moneyTypeBool");
            boolean[] menuTypeBool = new boolean[jarrmenuTypeBool.length()];
            for (int i = 0; i < jarrmenuTypeBool.length(); i++) {
                menuTypeBool[i] = jarrmenuTypeBool.getJSONObject(i).getBoolean("element" + i);
            }

            int dateInterval = jobj.getInt("dateInterval");
            boolean groupByNone = jobj.getBoolean("groupByNone");
            int groupBy = jobj.getInt("groupBy");
            int sortBy = jobj.getInt("sortBy");
            int sortingOrder = jobj.getInt("sortingOrder");

            JSONArray jarrCatTypeBool = jobj.getJSONArray("CatTypeBool");
            boolean[] catTypeBool = new boolean[jarrCatTypeBool.length()];
            for (int i = 0; i < jarrCatTypeBool.length(); i++) {
                catTypeBool[i] = jarrCatTypeBool.getJSONObject(i).getBoolean("element" + i);
            }

            JSONArray jarrcols = jobj.getJSONArray("cols");
            String[] cols = new String[jarrcols.length()];
            for (int i = 0; i < jarrcols.length(); i++) {
                cols[i] = jarrcols.getJSONObject(i).getString("element" + i);
            }
            DbHandler db = new DbHandler(context);
            return db.getRecordsAsList(queryText, dateDataBool, sDate, eDate,
                    menuTypeBool, dateInterval, groupByNone, groupBy, sortBy, catTypeBool, cols,
                    sortingOrder);
        } catch (JSONException e) {
            LoggerCus.d(TAG, e.getMessage());
        } catch (ParseException e) {
            LoggerCus.d(TAG, e.getMessage());
        }
        return new ArrayList<MBRecord>();
    }

    public static View drawLineGraph(int len, String[] label, String[] data, Context context) {
        LineChart chart = new LineChart(context);

        ArrayList<Entry> entry = new ArrayList<>();
        ArrayList<String> labels = new ArrayList<>();
        for (int i = 0; i < len; i++) {
            entry.add(new Entry(Float.parseFloat(data[i]), i));
            labels.add(label[i]);
        }
        LineDataSet dataSet = new LineDataSet(entry, "Amount");
        LineData lineData = new LineData(labels, dataSet);
        chart.setData(lineData);

        chart.animateXY(2000, 2000);
        chart.setDescription("");
        dataSet.setColors(ColorTemplate.JOYFUL_COLORS);
        return chart;
    }

    public static View drawBarGraph(int len, String[] label, String[] data, Context context) {
        BarChart chart = new BarChart(context);

        ArrayList<BarEntry> entry = new ArrayList<>();
        ArrayList<String> labels = new ArrayList<String>();
        for (int i = 0; i < len; i++) {
            entry.add(new BarEntry(Float.parseFloat(data[i]), i));
            labels.add(label[i]);
        }
        BarDataSet dataSet = new BarDataSet(entry, "Amount");
        BarData barData = new BarData(labels, dataSet);
        chart.setData(barData);

        chart.animateXY(2000, 2000);
        chart.setDescription("");
        dataSet.setColors(ColorTemplate.JOYFUL_COLORS);
        return chart;
    }

    public static View drawPieGraph(int len, String[] label, String[] data, Context context) {
        PieChart chart = new PieChart(context);

        ArrayList<Entry> entry = new ArrayList<>();
        ArrayList<String> labels = new ArrayList<>();
        for (int i = 0; i < len; i++) {
            entry.add(new Entry(Float.parseFloat(data[i]), i));
            labels.add(label[i]);
        }
        PieDataSet dataSet = new PieDataSet(entry, "Amount");
        PieData barData = new PieData(labels, dataSet);
        chart.setData(barData);

        chart.animateXY(2000, 2000);
        chart.setDescription("");
        dataSet.setColors(ColorTemplate.JOYFUL_COLORS);
        return chart;
    }

    public static View drawRadarGraph(int len, String[] label, String[] data, Context context) {
        RadarChart chart = new RadarChart(context);

        ArrayList<Entry> entry = new ArrayList<>();
        ArrayList<String> labels = new ArrayList<>();
        for (int i = 0; i < len; i++) {
            entry.add(new Entry(Float.parseFloat(data[i]), i));
            labels.add(label[i]);
        }
        RadarDataSet dataSet = new RadarDataSet(entry, "Amount");
        RadarData barData = new RadarData(labels, dataSet);
        chart.setData(barData);

        chart.animateXY(2000, 2000);
        chart.setDescription("");
        dataSet.setColors(ColorTemplate.JOYFUL_COLORS);
        return chart;
    }

    public static View drawScatterGraph(int len, String[] label, String[] data, Context context) {
        ScatterChart chart = new ScatterChart(context);

        ArrayList<Entry> entry = new ArrayList<>();
        ArrayList<String> labels = new ArrayList<>();
        for (int i = 0; i < len; i++) {
            entry.add(new Entry(Float.parseFloat(data[i]), i));
            labels.add(label[i]);
        }
        ScatterDataSet dataSet = new ScatterDataSet(entry, "Amount");
        ScatterData barData = new ScatterData(labels, dataSet);
        chart.setData(barData);

        chart.animateXY(2000, 2000);
        chart.setDescription("");
        dataSet.setColors(ColorTemplate.JOYFUL_COLORS);
        return chart;
    }

    public static int getFilterGraphType(String s) {
        JSONObject jobj = null;
        int graphType = 0;
        try {
            jobj = new JSONObject(s);
            graphType = jobj.getInt("graphType");
        } catch (JSONException e) {
            LoggerCus.d(TAG, e.getMessage());
        }
        return graphType;
    }

    public static void cancelAlarmForReportsRemainder(Context context) {
        Intent i = new Intent(context, SmartRemainderAlarmReceiver.class);
        PendingIntent pi = PendingIntent.getBroadcast(context, GlobalConstants.REQ_CODE_PENDING_INTENT_SMART_REMAINDER, i, 0);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(pi);
    }

    public static void setAlarmForReportsRemainder(Context context) {
        long time = PreferenceManager.getDefaultSharedPreferences(context).getLong(GlobalConstants.PREF_REPORTS_REMAINDER_TIME, -1);
        if (time != -1) {
            time = System.currentTimeMillis();
        }
        Intent i = new Intent(context, SmartRemainderAlarmReceiver.class);
        PendingIntent pi = PendingIntent.getBroadcast(context, GlobalConstants.REQ_CODE_PENDING_INTENT_SMART_REMAINDER, i, 0);
        int millsec = 1000 * 60 * 60;
        millsec *= 24;

        AlarmManager alarm = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarm.setRepeating(AlarmManager.RTC_WAKEUP, time, millsec, pi);
        Toast.makeText(context, "Smart Remainder Set Successfully!", Toast.LENGTH_LONG).show();
    }

    public static void cancelAlarmForReportsNotification(Context context) {
        Intent i = new Intent(context, ReportsAlarmReceiver.class);
        PendingIntent pi = PendingIntent.getBroadcast(context, GlobalConstants.REQ_CODE_PENDING_INTENT_REPORTS, i, 0);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(pi);
    }

    public static void setAlarmForReportsNotification(Context context) {
        long time = PreferenceManager.getDefaultSharedPreferences(context).getLong(GlobalConstants.PREF_REPORTS_TIME, -1);
        if (time != -1) {
            time = System.currentTimeMillis();
        }
        Intent i = new Intent(context, ReportsAlarmReceiver.class);
        PendingIntent pi = PendingIntent.getBroadcast(context, GlobalConstants.REQ_CODE_PENDING_INTENT_REPORTS, i, 0);
        int millsec = 1000 * 60 * 60;
        millsec *= 24;

        AlarmManager alarm = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarm.setRepeating(AlarmManager.RTC_WAKEUP, time, millsec, pi);
        Toast.makeText(context, "Reports Set Successfully!", Toast.LENGTH_LONG).show();
    }
}
