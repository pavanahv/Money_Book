package com.example.allakumarreddy.moneybook;

import android.util.Log;

/**
 * Created by alla.kumarreddy on 7/20/2017.
 */

public class LoggerCus {
    final static String TAG="pavanlog";

    public static void d(String tag,String s)
    {
        Log.d(TAG,tag+" : "+s);
    }
}
