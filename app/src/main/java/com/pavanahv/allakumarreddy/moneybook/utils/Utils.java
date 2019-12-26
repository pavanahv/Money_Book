package com.pavanahv.allakumarreddy.moneybook.utils;

import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;
import android.util.TypedValue;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.charts.RadarChart;
import com.github.mikephil.charting.charts.ScatterChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.data.RadarData;
import com.github.mikephil.charting.data.RadarDataSet;
import com.github.mikephil.charting.data.RadarEntry;
import com.github.mikephil.charting.data.ScatterData;
import com.github.mikephil.charting.data.ScatterDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.interfaces.datasets.IRadarDataSet;
import com.github.mikephil.charting.interfaces.datasets.IScatterDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.pavanahv.allakumarreddy.moneybook.R;
import com.pavanahv.allakumarreddy.moneybook.Services.BackupToGoogleDriveService;
import com.pavanahv.allakumarreddy.moneybook.broadcastreceivers.AlarmReceiver;
import com.pavanahv.allakumarreddy.moneybook.broadcastreceivers.ReportsAlarmReceiver;
import com.pavanahv.allakumarreddy.moneybook.broadcastreceivers.SmartRemainderAlarmReceiver;
import com.pavanahv.allakumarreddy.moneybook.storage.PreferencesCus;
import com.pavanahv.allakumarreddy.moneybook.storage.db.DbHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.TreeSet;

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

    public static void isOnline(Context context, BackupToGoogleDriveService.Callback callback) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager
                .getActiveNetworkInfo();

        boolean isThreadStarted = false;
        // Check internet connection and accrding to state change the
        if (networkInfo != null) {
            if (networkInfo.isConnected()) {
                isThreadStarted = true;
                new Thread(() -> {
                    boolean status = isInternetAvailable();
                    if (status)
                        LoggerCus.d(TAG, "Connected to internet");
                    else
                        LoggerCus.d(TAG, "Not Connected to internet");
                    callback.isInternetAvilable(status);
                }).start();

            } else {
                LoggerCus.d(TAG, "Network is not connected");
            }
        } else {
            LoggerCus.d(TAG, "Network Info is null");
        }
        if (!isThreadStarted) {
            callback.isInternetAvilable(false);
        }
    }

    public static boolean isInternetAvailable() {
        try {
            InetAddress address = InetAddress.getByName("www.google.com");
            return !address.equals("");
        } catch (UnknownHostException e) {
            // Log error
        }
        return false;
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

            int moneyType = jobj.getInt("moneyType");
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

            JSONArray jarrPayMethBool = jobj.getJSONArray("PayMethBool");
            boolean[] payMetheBool = new boolean[jarrPayMethBool.length()];
            for (int i = 0; i < jarrPayMethBool.length(); i++) {
                payMetheBool[i] = jarrPayMethBool.getJSONObject(i).getBoolean("element" + i);
            }

            JSONArray jarrpays = jobj.getJSONArray("pays");
            String[] pays = new String[jarrpays.length()];
            for (int i = 0; i < jarrpays.length(); i++) {
                pays[i] = jarrpays.getJSONObject(i).getString("element" + i);
            }

            DbHandler db = new DbHandler(context);
            return db.getRecordsAsList(queryText, dateDataBool, sDate, eDate,
                    moneyType, dateInterval, groupByNone, groupBy,
                    sortBy, catTypeBool, cols,
                    sortingOrder,
                    payMetheBool, pays);
        } catch (JSONException e) {
            LoggerCus.d(TAG, e.getMessage());
        } catch (ParseException e) {
            LoggerCus.d(TAG, e.getMessage());
        }
        return new ArrayList<MBRecord>();
    }

    public static View drawLineGraph(int len, String[] label, String[] data, Context context) {
        LineChart chart = new LineChart(context);

        chart.getXAxis().setDrawLabels(true);
        chart.getXAxis().setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                int val = (int) (value * 100);
                val = val % 100;
                if (val == 0)
                    return label[(int) value];
                else
                    return "";
            }
        });

        ArrayList<Entry> entry = new ArrayList<>();
        for (int i = 0; i < len; i++) {
            entry.add(new Entry(Float.parseFloat(i + ""),
                    Float.parseFloat(data[i]),
                    label[i]));
        }
        LineDataSet dataSet = new LineDataSet(entry, "Amount");
        dataSet.setColor(ColorTemplate.getHoloBlue());
        dataSet.setDrawFilled(true);
        dataSet.setFillColor(ColorTemplate.getHoloBlue());
        dataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        ArrayList<ILineDataSet> list = new ArrayList<>();
        list.add(dataSet);
        LineData lineData = new LineData(list);
        chart.setData(lineData);

        Legend l = chart.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
        l.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        l.setDrawInside(false);
        l.setForm(Legend.LegendForm.CIRCLE);
        l.setFormSize(9f);
        l.setTextSize(11f);
        l.setXEntrySpace(4f);

        chart.animateXY(GlobalConstants.graph_animate_time, GlobalConstants.graph_animate_time);
        chart.getDescription().setEnabled(false);
        //dataSet.setColors(ColorTemplate.JOYFUL_COLORS);
        return chart;
    }

    public static View drawBarGraph(int len, String[] label, String[] data, Context context) {
        BarChart chart = new BarChart(context);

        Legend l = chart.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
        l.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        l.setDrawInside(false);
        l.setForm(Legend.LegendForm.CIRCLE);
        l.setFormSize(9f);
        l.setTextSize(11f);
        l.setXEntrySpace(4f);

        ArrayList<BarEntry> entry = new ArrayList<>();
        for (int i = 0; i < len; i++) {
            entry.add(new BarEntry(i, Float.parseFloat(data[i]), label[i]));
        }
        chart.getXAxis().setDrawLabels(true);
        chart.getXAxis().setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                int val = (int) (value * 100);
                val = val % 100;
                if (val == 0)
                    return label[(int) value];
                else
                    return "";
            }
        });
        BarDataSet dataSet = new BarDataSet(entry, "Amount");
        ArrayList<IBarDataSet> list = new ArrayList<>();
        list.add(dataSet);
        BarData barData = new BarData(list);
        chart.setData(barData);

        chart.animateXY(GlobalConstants.graph_animate_time, GlobalConstants.graph_animate_time);
        chart.getDescription().setEnabled(false);
        dataSet.setColors(getColors());
        return chart;
    }

    public static View drawPieGraph(int len, String[] label, String[] data, Context context) {
        PieChart chart = new PieChart(context);

        Legend l = chart.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        l.setOrientation(Legend.LegendOrientation.VERTICAL);
        l.setDrawInside(false);
        l.setForm(Legend.LegendForm.CIRCLE);
        l.setFormSize(9f);
        l.setTextSize(11f);
        l.setXEntrySpace(4f);

        ArrayList<PieEntry> entry = new ArrayList<>();
        for (int i = 0; i < len; i++) {
            entry.add(new PieEntry(Float.parseFloat(data[i]), label[i]));
        }
        PieDataSet dataSet = new PieDataSet(entry, "Amount");
        PieData barData = new PieData(dataSet);
        chart.setData(barData);
        chart.setEntryLabelColor(Color.BLACK);

        chart.animateXY(GlobalConstants.graph_animate_time, GlobalConstants.graph_animate_time);
        dataSet.setColors(getColors());
        chart.getDescription().setEnabled(false);
        return chart;
    }

    public static ArrayList<Integer> getColors() {
        ArrayList<Integer> colors = new ArrayList<>();

        for (int c : ColorTemplate.VORDIPLOM_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.JOYFUL_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.COLORFUL_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.LIBERTY_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.PASTEL_COLORS)
            colors.add(c);
        return colors;
    }

    public static View drawRadarGraph(int len, String[] label, String[] data, Context context) {
        RadarChart chart = new RadarChart(context);

        ArrayList<RadarEntry> entry = new ArrayList<>();
        for (int i = 0; i < len; i++) {
            entry.add(new RadarEntry(Float.parseFloat(data[i]), label[i]));
        }
        RadarDataSet dataSet = new RadarDataSet(entry, "Amount");
        ArrayList<IRadarDataSet> list = new ArrayList<>();
        list.add(dataSet);
        RadarData barData = new RadarData(list);
        chart.setData(barData);

        Legend l = chart.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
        l.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        l.setDrawInside(false);
        l.setForm(Legend.LegendForm.CIRCLE);
        l.setFormSize(9f);
        l.setTextSize(11f);
        l.setXEntrySpace(4f);

        chart.getXAxis().setDrawLabels(true);
        chart.getXAxis().setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                if ((value - 1) >= 0)
                    value--;
                int val = (int) (value * 100);
                val = val % 100;
                if (val == 0)
                    return label[(int) (value)];
                else
                    return "";
            }
        });

        chart.animateXY(GlobalConstants.graph_animate_time, GlobalConstants.graph_animate_time);
        chart.getDescription().setEnabled(false);
        dataSet.setColors(getColors());
        return chart;
    }

    public static View drawScatterGraph(int len, String[] label, String[] data, Context context) {
        ScatterChart chart = new ScatterChart(context);

        ArrayList<Entry> entry = new ArrayList<>();
        for (int i = 0; i < len; i++) {
            entry.add(new Entry(i, Float.parseFloat(data[i]), label[i]));
        }
        ScatterDataSet dataSet = new ScatterDataSet(entry, "Amount");
        ArrayList<IScatterDataSet> list = new ArrayList<>();
        list.add(dataSet);
        ScatterData barData = new ScatterData(list);
        chart.setData(barData);

        Legend l = chart.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
        l.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        l.setDrawInside(false);
        l.setForm(Legend.LegendForm.CIRCLE);
        l.setFormSize(9f);
        l.setTextSize(11f);
        l.setXEntrySpace(4f);

        chart.getXAxis().setDrawLabels(true);
        chart.getXAxis().setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                int val = (int) (value * 100);
                val = val % 100;
                if (val == 0 && (((int) value) < label.length))
                    return label[(int) value];
                else
                    return "";
            }
        });

        chart.animateXY(GlobalConstants.graph_animate_time, GlobalConstants.graph_animate_time);
        chart.getDescription().setEnabled(false);
        dataSet.setColors(getColors());
        return chart;
    }

    public static File getGraphShareMediaFile() {
        File mediaStorageDir = new File(Environment.getExternalStorageDirectory(), Environment.DIRECTORY_DCIM);
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                LoggerCus.d(TAG, "failed to create or open directory");
                return null;
            }
        }

        File mediaFile;
        String filename = "";
        filename += "shared_graph" + ".png";
        mediaFile = new File(mediaStorageDir.getPath() + File.separator + filename);
        return mediaFile;
    }

    public static void deleteGraphShareMediaFile() {
        File mediaStorageDir = new File(Environment.getExternalStorageDirectory(), Environment.DIRECTORY_DCIM);
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                LoggerCus.d(TAG, "failed to create or open directory");
            }
        }

        File mediaFile;
        String filename = "";
        filename += "shared_graph" + ".png";
        mediaFile = new File(mediaStorageDir.getPath() + File.separator + filename);
        if (mediaFile.exists())
            mediaFile.delete();
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
        LoggerCus.d(TAG, time + "");

        if (time == -1) {
            time = System.currentTimeMillis();
        }

        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(time);
        Calendar calOrg = Calendar.getInstance();
        calOrg.setTimeInMillis(System.currentTimeMillis());
        calOrg.set(Calendar.HOUR, cal.get(Calendar.HOUR));
        calOrg.set(Calendar.MINUTE, cal.get(Calendar.MINUTE));
        calOrg.set(Calendar.SECOND, 0);
        calOrg.set(Calendar.AM_PM, cal.get(Calendar.AM_PM));
        LoggerCus.d(TAG, calOrg.getTimeInMillis() + "");
        LoggerCus.d(TAG, cal.get(Calendar.HOUR) + " " + cal.get(Calendar.MINUTE) + " " + cal.get(Calendar.SECOND) + " " + cal.get(Calendar.AM_PM));
        LoggerCus.d(TAG, calOrg.get(Calendar.HOUR) + " " + calOrg.get(Calendar.MINUTE) + " " + calOrg.get(Calendar.SECOND) + " " + calOrg.get(Calendar.AM_PM));

        Intent i = new Intent(context, SmartRemainderAlarmReceiver.class);
        PendingIntent pi = PendingIntent.getBroadcast(context, GlobalConstants.REQ_CODE_PENDING_INTENT_SMART_REMAINDER, i, 0);

        AlarmManager alarm = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarm.setRepeating(AlarmManager.RTC_WAKEUP, calOrg.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pi);
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

        if (time == -1) {
            time = System.currentTimeMillis();
        }

        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(time);
        Calendar calOrg = Calendar.getInstance();
        calOrg.setTimeInMillis(System.currentTimeMillis());
        calOrg.set(Calendar.HOUR, cal.get(Calendar.HOUR));
        calOrg.set(Calendar.MINUTE, cal.get(Calendar.MINUTE));
        calOrg.set(Calendar.SECOND, 0);
        calOrg.set(Calendar.AM_PM, cal.get(Calendar.AM_PM));
        LoggerCus.d(TAG, calOrg.getTimeInMillis() + "");
        LoggerCus.d(TAG, cal.get(Calendar.HOUR) + " " + cal.get(Calendar.MINUTE) + " " + cal.get(Calendar.SECOND) + " " + cal.get(Calendar.AM_PM));
        LoggerCus.d(TAG, calOrg.get(Calendar.HOUR) + " " + calOrg.get(Calendar.MINUTE) + " " + calOrg.get(Calendar.SECOND) + " " + calOrg.get(Calendar.AM_PM));
        Intent i = new Intent(context, ReportsAlarmReceiver.class);
        PendingIntent pi = PendingIntent.getBroadcast(context, GlobalConstants.REQ_CODE_PENDING_INTENT_REPORTS, i, 0);

        AlarmManager alarm = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarm.setRepeating(AlarmManager.RTC_WAKEUP, calOrg.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pi);
        Toast.makeText(context, "Reports Set Successfully!", Toast.LENGTH_LONG).show();
    }

    public static int getColor(int type, Context context) {
        switch (type) {
            case 0:
                return context.getResources().getColor(R.color.spent);
            case 1:
                return context.getResources().getColor(R.color.earn);
            case 2:
                return context.getResources().getColor(R.color.due);
            case 3:
                return context.getResources().getColor(R.color.loan);
        }
        return R.color.colorPrimary;
    }

    public static void addRefIdForLoanDueFromDes(MBRecord mbr) {
        String des = mbr.getDescription();
        int count = 0;
        for (int i = 0; true; i++) {
            i = des.indexOf("{{", i);
            if (i == -1) {
                break;
            } else {
                count++;
            }
        }
        if (count == 1) {
            int inda = des.indexOf("{{");
            int indb = des.indexOf("}}", inda);
            String temp = des.substring(inda + 2, indb);
            LoggerCus.d(TAG, temp);
            String desc = des.substring(0, inda) + des.substring(indb + 2, des.length());
            mbr.setDescription(desc);
            mbr.setRefIdForLoanDue(temp);
        }
    }

    public static String getNameFromEntryData(Object data) {
        try {
            String temp = ((String[]) data)[0];
            int ind = temp.indexOf("(");
            return temp.substring(0, ind).trim();
        } catch (Exception e) {
            LoggerCus.d(TAG, "Error while parsing entry data for getting name -> " + e.getMessage());
            return null;
        }
    }

    public static void parseToGraphData(ArrayList<String[]> datas, ArrayList<String[]> labels) {

        final int len = labels.size();
        SimpleDateFormat sdf = null;
        boolean isMonth = false;
        if (len > 0 && labels.get(0).length > 0) {
            String temp = labels.get(0)[0];
            temp = getNameFromEntry(temp);
            int strLen = temp.split("-").length;
            if (strLen > 2) {
                sdf = new SimpleDateFormat("dd - MM - yyyy");
            } else {
                isMonth = true;
                sdf = new SimpleDateFormat("MM - yyyy");
            }

            TreeSet<Long> set = new TreeSet<>();
            HashMap<String, String> maps[] = new HashMap[4];
            int ind = 0;
            for (String[] labelArr : labels) {
                LoggerCus.d(TAG, Arrays.toString(labelArr));
                maps[ind] = new HashMap<>();
                int i = 0;
                String[] tempDataList = datas.get(ind);
                for (String label : labelArr) {
                    String tempLabel = getNameFromEntry(label);
                    if (tempLabel != null) {
                        maps[ind].put(tempLabel, tempDataList[i]);
                        i++;
                        try {
                            Date d = sdf.parse(tempLabel);
                            d = intializeSDateForDay(d);
                            if (isMonth)
                                d = intializeSDateForMonth(d);
                            set.add(d.getTime());
                        } catch (ParseException e) {
                            LoggerCus.d(TAG, "error : " + e.getMessage());
                        }
                    }
                }
                ind++;
            }

            LoggerCus.d(TAG, set.toString());

            labels.clear();
            datas.clear();

            for (int i = 0; i < len; i++) {
                HashMap<String, String> map = maps[i];
                String[] label = new String[set.size()];
                String[] data = new String[set.size()];
                int j = 0;
                for (Long date : set) {
                    String dateStr = sdf.format(date);
                    label[j] = dateStr;
                    if (map.containsKey(dateStr)) {
                        data[j] = map.get(dateStr);
                    } else {
                        data[j] = "0";
                    }
                    j++;
                }
                LoggerCus.d(TAG, "final label " + i + " -> " + Arrays.toString(label));
                LoggerCus.d(TAG, "final data " + i + " -> " + Arrays.toString(data));
                labels.add(label);
                datas.add(data);
            }
        } else
            LoggerCus.d(TAG, "labels size is 0");

    }

    private static String getNameFromEntry(String temp) {
        try {
            int ind = temp.indexOf("(");
            return temp.substring(0, ind).trim();
        } catch (Exception e) {
            LoggerCus.d(TAG, "Error while parsing entry data for getting name -> " + e.getMessage());
            return null;
        }
    }

    public static Date intializeSDateForMonth(Date sDate) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(sDate);
        cal.set(Calendar.DATE, cal.getMinimum(Calendar.DATE));
        return cal.getTime();
    }

    public static Date intializeSDateForDay(Date sDate) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(sDate);
        cal.set(Calendar.HOUR_OF_DAY, cal.getMinimum(Calendar.HOUR_OF_DAY));
        cal.set(Calendar.MINUTE, cal.getMinimum(Calendar.MINUTE));
        cal.set(Calendar.SECOND, cal.getMinimum(Calendar.SECOND));
        cal.set(Calendar.MILLISECOND, cal.getMinimum(Calendar.MILLISECOND));
        return cal.getTime();
    }
}
