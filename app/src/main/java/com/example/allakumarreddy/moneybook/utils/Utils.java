package com.example.allakumarreddy.moneybook.utils;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.content.ContextCompat;
import android.util.TypedValue;
import android.widget.Toast;

import java.text.DecimalFormat;
import java.text.NumberFormat;

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
}
