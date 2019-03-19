package com.example.allakumarreddy.moneybook.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.allakumarreddy.moneybook.utils.DashBoardRecord;
import com.example.allakumarreddy.moneybook.utils.DateConverter;
import com.example.allakumarreddy.moneybook.utils.LoggerCus;
import com.example.allakumarreddy.moneybook.utils.MBRecord;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;


/**
 * Created by alla.kumarreddy on 7/25/2017.
 */

public class DbHandler extends SQLiteOpenHelper {
    // All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "MoneyBookManager";

    // Contacts table name
    public static final String TABLE_NAME = "MoneyBook";

    // Contacts Table Columns names
    public static final String KEY_TYPE = "type";
    public static final String KEY_DESCRIPTION = "description";
    public static final String KEY_AMOUNT = "amount";
    public static final String KEY_DATE = "day";
    public static final String KEY_DATE_MONTH = "month";
    public static final String KEY_DATE_YEAR = "year";
    public static final String KEY_CAT = "category";

    public static final String CAT_TABLE_NAME = "Catageories";
    public static final String KEY_NAME = "name";
    public static final String KEY_CAT_ID = "id";
    public static final String KEY_CAT_BAL = "balance_left";

    // Contacts Table Columns names
    public static final String MSG_TABLE_NAME = "Message";
    public static final String MSG_KEY_TYPE = "type";
    public static final String MSG_KEY_DESCRIPTION = "description";
    public static final String MSG_KEY_AMOUNT = "amount";
    public static final String MSG_KEY_CAT = "category";
    public static final String MSG_KEY_LEFT_BAL = "balance_left";
    public static final String MSG_KEY_NAME = "name";
    public static final String MSG_KEY_MSG = "msg";

    private SimpleDateFormat format;
    private final static String TAG = "DbHandler";
    private int total;
    private int mTotalMoneySpentInCurrentMonth;
    private int mTotalMoneySpentInCurrentDay;
    private int mTotalMoneySpentInCurrentYear;

    public DbHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        format = new SimpleDateFormat("yyyy/MM/dd");
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_CONTACTS_TABLE = "CREATE TABLE " + TABLE_NAME + "("
                + KEY_TYPE + " INTEGER," + KEY_DESCRIPTION + " TEXT,"
                + KEY_AMOUNT + " INTEGER," + KEY_DATE + " INTEGER," + KEY_DATE_MONTH + " INTEGER," + KEY_DATE_YEAR + " INTEGER," + KEY_CAT + " INTEGER)";
        db.execSQL(CREATE_CONTACTS_TABLE);

        String CREATE_CAT_TABLE = "CREATE TABLE " + CAT_TABLE_NAME + "("
                + KEY_CAT_ID + " INTEGER PRIMARY KEY,"
                + KEY_NAME + " TEXT," + KEY_CAT_BAL + " INTEGER)";
        db.execSQL(CREATE_CAT_TABLE);

        String CREATE_MESSAGE_TABLE = "CREATE TABLE " + MSG_TABLE_NAME + "("
                + MSG_KEY_TYPE + " INTEGER," + MSG_KEY_DESCRIPTION + " TEXT,"
                + MSG_KEY_AMOUNT + " TEXT," + MSG_KEY_CAT + " INTEGER,"
                + MSG_KEY_LEFT_BAL + " TEXT," + MSG_KEY_NAME + " TEXT,"
                + MSG_KEY_MSG + " TEXT)";
        db.execSQL(CREATE_MESSAGE_TABLE);
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + CAT_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + MSG_TABLE_NAME);
        // Create tables again
        onCreate(db);
    }

    public long getIdOfCategory(String name) {
        SQLiteDatabase db = this.getReadableDatabase();
        long result = -1L;
        Cursor cursor = db.rawQuery("SELECT " + KEY_CAT_ID + " FROM " + CAT_TABLE_NAME + " WHERE " + KEY_NAME + " = '" + name + "'", null);
        if (cursor != null) {
            final int len = cursor.getCount();
            for (int i = 0; i < len; i++) {
                cursor.moveToPosition(i);
                result = cursor.getLong(0);
            }
            cursor.close();
        }

        db.close();
        return result;
    }

    public String getNameOfCategory(long id) {
        SQLiteDatabase db = this.getReadableDatabase();
        String result = "";
        Cursor cursor = db.rawQuery("SELECT " + KEY_NAME + " FROM " + CAT_TABLE_NAME + " WHERE " + KEY_CAT_ID + " = " + id + "", null);
        if (cursor != null) {
            final int len = cursor.getCount();
            for (int i = 0; i < len; i++) {
                cursor.moveToPosition(i);
                result = cursor.getString(0);
            }
            cursor.close();
        }

        db.close();
        return result;
    }

    public boolean checkCatNameExists(String name) {
        SQLiteDatabase db = this.getReadableDatabase();
        boolean result = false;
        Cursor cursor = db.rawQuery("SELECT " + KEY_CAT_ID + " FROM " + CAT_TABLE_NAME + " WHERE " + KEY_NAME + " = '" + name + "'", null);
        if (cursor != null) {
            final int len = cursor.getCount();
            if (len > 0)
                result = false;
            else
                result = true;
            cursor.close();
        }

        return result;
    }

    public void addCategory(String name) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_NAME, name.toLowerCase());
        db.insert(CAT_TABLE_NAME, null, values);
        db.close();
    }

    public boolean addRecord(MBRecord mbr, int type) {
        DateConverter dc = new DateConverter(mbr.getDate());
        ContentValues values = new ContentValues();
        values.put(KEY_TYPE, type);
        values.put(KEY_DESCRIPTION, mbr.getDescription());
        values.put(KEY_AMOUNT, mbr.getAmount());
        values.put(KEY_DATE, dc.getdDate().getTime());
        values.put(KEY_DATE_MONTH, dc.getmDate().getTime());
        values.put(KEY_DATE_YEAR, dc.getyDate().getTime());
        values.put(KEY_CAT, getIdOfCategory(mbr.getCategory()));

        SQLiteDatabase db = this.getWritableDatabase();
        // Inserting Row
        long result = db.insert(TABLE_NAME, null, values);
        db.close();
        if (result != -1)
            return true;
        else
            return false;
    }

    public boolean addRecordWithCatAsID(MBRecord mbr, int type) {
        DateConverter dc = new DateConverter(mbr.getDate());
        ContentValues values = new ContentValues();
        values.put(KEY_TYPE, type);
        values.put(KEY_DESCRIPTION, mbr.getDescription());
        values.put(KEY_AMOUNT, mbr.getAmount());
        values.put(KEY_DATE, dc.getdDate().getTime());
        values.put(KEY_DATE_MONTH, dc.getmDate().getTime());
        values.put(KEY_DATE_YEAR, dc.getyDate().getTime());
        values.put(KEY_CAT, Long.parseLong(mbr.getCategory()));

        SQLiteDatabase db = this.getWritableDatabase();
        // Inserting Row
        long result = db.insert(TABLE_NAME, null, values);
        db.close();
        if (result != -1)
            return true;
        else
            return false;
    }

    public boolean updateBalLeft(long catId, int bal) {
        ContentValues values = new ContentValues();
        values.put(KEY_CAT_BAL, bal);

        SQLiteDatabase db = this.getWritableDatabase();
        int result = db.update(CAT_TABLE_NAME, values, KEY_CAT_ID + "=" + catId, null);
        db.close();
        if (result > 0)
            return true;
        else
            return false;
    }

    public void addRecords(String s) {
        deleteAllData();

        SQLiteDatabase db = this.getWritableDatabase();

        try {
            JSONObject obj = new JSONObject(s);
            JSONArray jsonArray = obj.getJSONArray(TABLE_NAME);
            JSONArray jsonArrayc = obj.getJSONArray(CAT_TABLE_NAME);
            JSONArray jsonArraym = obj.getJSONArray(MSG_TABLE_NAME);

            final int len = jsonArray.length();
            for (int i = 0; i < len; i++) {
                JSONObject jsonObj = jsonArray.getJSONObject(i);
                ContentValues values = new ContentValues();
                values.put(KEY_TYPE, Integer.parseInt(jsonObj.getString(KEY_TYPE)));
                values.put(KEY_DESCRIPTION, jsonObj.getString(KEY_DESCRIPTION));
                values.put(KEY_AMOUNT, Integer.parseInt(jsonObj.getString(KEY_AMOUNT)));
                DateConverter dc = new DateConverter(jsonObj.getString(KEY_DATE));

                values.put(KEY_DATE, dc.getdDate().getTime());
                values.put(KEY_DATE_MONTH, dc.getmDate().getTime());
                values.put(KEY_DATE_YEAR, dc.getyDate().getTime());
                values.put(KEY_CAT, jsonObj.getLong(KEY_CAT));

                // Inserting Row
                db.insert(TABLE_NAME, null, values);
            }

            final int len1 = jsonArrayc.length();
            for (int i = 0; i < len1; i++) {
                JSONObject jsonObj = jsonArrayc.getJSONObject(i);
                ContentValues values = new ContentValues();
                values.put(KEY_NAME, jsonObj.getString(KEY_NAME));
                values.put(KEY_CAT_ID, jsonObj.getLong(KEY_CAT_ID));
                values.put(KEY_CAT_BAL, jsonObj.getLong(KEY_CAT_BAL));
                // Inserting Row
                db.insert(CAT_TABLE_NAME, null, values);
            }

            final int lenm = jsonArraym.length();
            for (int i = 0; i < lenm; i++) {
                JSONObject jsonObj = jsonArraym.getJSONObject(i);
                ContentValues values = new ContentValues();
                values.put(MSG_KEY_NAME, jsonObj.getString(KEY_NAME));
                values.put(MSG_KEY_DESCRIPTION, jsonObj.getString(MSG_KEY_DESCRIPTION));
                values.put(MSG_KEY_AMOUNT, jsonObj.getString(MSG_KEY_AMOUNT));
                values.put(MSG_KEY_TYPE, jsonObj.getInt(MSG_KEY_TYPE));
                values.put(MSG_KEY_CAT, jsonObj.getLong(MSG_KEY_CAT));
                values.put(MSG_KEY_LEFT_BAL, jsonObj.getString(MSG_KEY_LEFT_BAL));
                values.put(MSG_KEY_MSG, jsonObj.getString(MSG_KEY_MSG));
                // Inserting Row
                db.insert(MSG_TABLE_NAME, null, values);
            }

            db.close(); // Closing database connection
        } catch (JSONException e) {
            LoggerCus.d(TAG, e.getMessage());
        }
    }

    private void deleteAllData() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME, null, null);
        db.delete(CAT_TABLE_NAME, null, null);
        db.delete(MSG_TABLE_NAME, null, null);
        db.close();
    }

    public boolean updateRecord(MBRecord mbrOld, MBRecord mbrNew) {
        if (deleteRecord(mbrOld) > 0) {
            return addRecord(mbrNew, mbrNew.getType());
        } else
            return false;
    }

    public int deleteRecord(MBRecord mbr) {
        DateConverter dc = new DateConverter(mbr.getDate());
        Date sDate = dc.getdDate();
        Date eDate = (Date) sDate.clone();
        sDate = intializeSDateForDay(sDate);
        eDate = intializeEDateForDay(eDate);

        SQLiteDatabase db = this.getWritableDatabase();
        LoggerCus.d(TAG, KEY_DESCRIPTION + " = '" + mbr.getDescription() + "' AND " + KEY_AMOUNT + " = " + mbr.getAmount() + " AND " + KEY_TYPE + " = " + mbr.getType() + " AND " + KEY_DATE + " >= " + sDate.getTime() + " AND " + KEY_DATE + " <= " + eDate.getTime());
        int n = db.delete(TABLE_NAME, KEY_DESCRIPTION + " = '" + mbr.getDescription() + "' AND " + KEY_AMOUNT + " = " + mbr.getAmount() + " AND " + KEY_TYPE + " = " + mbr.getType() + " AND " + KEY_DATE + " >= " + sDate.getTime() + " AND " + KEY_DATE + " <= " + eDate.getTime(), null);
        db.close();
        return n;
    }


    public ArrayList<MBRecord> getRecords(String date, int type) {
        LoggerCus.d(TAG, date);
        DateConverter dc = null;
        dc = new DateConverter(date);
        Calendar cal = Calendar.getInstance();
        cal.setTime(dc.getdDate());
        cal.set(Calendar.AM_PM, Calendar.AM);
        cal.set(Calendar.HOUR, 0);
        Date temp1 = cal.getTime();
        cal.set(Calendar.AM_PM, Calendar.PM);
        cal.set(Calendar.HOUR, 11);
        Date temp2 = cal.getTime();

        ArrayList<MBRecord> mbr = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = null;
        cursor = db.rawQuery("SELECT " + KEY_DESCRIPTION + "," + KEY_AMOUNT + "," + KEY_DATE + "," + getSQLQueryForCat() + " FROM " + TABLE_NAME + " where " + KEY_DATE + " >= " + temp1.getTime() + " AND " + KEY_DATE + " <= " + temp2.getTime() + " AND " + KEY_TYPE + "=" + type, null);
        if (cursor != null) {
            final int len = cursor.getCount();
            for (int i = 0; i < len; i++) {
                cursor.moveToPosition(i);
                mbr.add(new MBRecord(cursor.getString(0), Integer.parseInt(cursor.getString(1)), new Date(cursor.getLong(2)), cursor.getString(3)));
            }
            cursor.close();
        }
        // return contact
        return mbr;
    }

    public String getRecords() {
        ArrayList<MBRecord> mbr = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        JSONArray jsonArray = new JSONArray();

        Cursor cursor = db.rawQuery("SELECT " + KEY_DESCRIPTION + "," + KEY_AMOUNT + "," + KEY_DATE + "," + KEY_TYPE + "," + KEY_CAT + " FROM " + TABLE_NAME, null);
        if (cursor != null) {
            final int len = cursor.getCount();
            for (int i = 0; i < len; i++) {
                cursor.moveToPosition(i);
                try {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put(KEY_DESCRIPTION, cursor.getString(0));
                    jsonObject.put(KEY_AMOUNT, cursor.getString(1));
                    jsonObject.put(KEY_DATE, format.format(new Date(cursor.getLong(2))));
                    jsonObject.put(KEY_TYPE, cursor.getString(3));
                    jsonObject.put(KEY_CAT, cursor.getLong(4));
                    jsonArray.put(jsonObject);
                } catch (JSONException e) {
                    LoggerCus.d(TAG, e.getMessage());
                }
            }
            cursor.close();
        }

        JSONArray jsonArrayc = new JSONArray();
        cursor = db.rawQuery("SELECT " + KEY_NAME + "," + KEY_CAT_ID + "," + KEY_CAT_BAL + " FROM " + CAT_TABLE_NAME, null);
        if (cursor != null) {
            final int len = cursor.getCount();
            for (int i = 0; i < len; i++) {
                cursor.moveToPosition(i);
                try {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put(KEY_NAME, cursor.getString(0));
                    jsonObject.put(KEY_CAT_ID, cursor.getLong(1));
                    jsonObject.put(KEY_CAT_BAL, cursor.getLong(2));
                    jsonArrayc.put(jsonObject);
                } catch (JSONException e) {
                    LoggerCus.d(TAG, e.getMessage());
                }
            }
            cursor.close();
        }

        JSONArray jsonArraym = new JSONArray();
        cursor = db.rawQuery("SELECT " + MSG_KEY_NAME + "," + MSG_KEY_DESCRIPTION + "," + MSG_KEY_AMOUNT + ","
                + MSG_KEY_TYPE + "," + MSG_KEY_CAT + "," + MSG_KEY_LEFT_BAL + "," + MSG_KEY_MSG
                + " FROM " + MSG_TABLE_NAME, null);
        if (cursor != null) {
            final int len = cursor.getCount();
            for (int i = 0; i < len; i++) {
                cursor.moveToPosition(i);
                try {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put(MSG_KEY_NAME, cursor.getString(0));
                    jsonObject.put(MSG_KEY_DESCRIPTION, cursor.getString(1));
                    jsonObject.put(MSG_KEY_AMOUNT, cursor.getString(2));
                    jsonObject.put(MSG_KEY_TYPE, cursor.getInt(3));
                    jsonObject.put(MSG_KEY_CAT, cursor.getLong(4));
                    jsonObject.put(MSG_KEY_LEFT_BAL, cursor.getString(5));
                    jsonObject.put(MSG_KEY_MSG, cursor.getString(6));
                    jsonArraym.put(jsonObject);
                } catch (JSONException e) {
                    LoggerCus.d(TAG, e.getMessage());
                }
            }
            cursor.close();
        }

        JSONObject obj = new JSONObject();
        try {
            obj.put(TABLE_NAME, jsonArray);
            obj.put(CAT_TABLE_NAME, jsonArrayc);
            obj.put(MSG_TABLE_NAME, jsonArraym);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        db.close();
        // return JSON string
        return obj.toString();
    }

    public ArrayList<MBRecord> getRecordsAsList() {
        ArrayList<MBRecord> mbr = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        this.total = 0;
        JSONArray jsonArray = new JSONArray();

        Cursor cursor = db.rawQuery("SELECT " + KEY_DESCRIPTION + "," + KEY_AMOUNT + "," + KEY_DATE + "," + KEY_TYPE + "," + getSQLQueryForCat() + " FROM " + TABLE_NAME, null);
        if (cursor != null) {
            final int len = cursor.getCount();
            for (int i = 0; i < len; i++) {
                cursor.moveToPosition(i);
                int numSpent = Integer.parseInt(cursor.getString(1));
                this.total += numSpent;
                mbr.add(new MBRecord(cursor.getString(0), numSpent, new Date(cursor.getLong(2)), cursor.getInt(3), cursor.getString(4)));
            }
            cursor.close();
        }
        // return contact
        return mbr;
    }

    private String getSQLQueryForCat() {
        return "(SELECT " + KEY_NAME + " FROM " + CAT_TABLE_NAME + " WHERE " + KEY_CAT_ID + " = " + KEY_CAT + ") AS " + KEY_CAT;
    }

    public ArrayList<MBRecord> getRecordsAsList(String query, boolean isAllDate, Date sDate, Date eDate, boolean isAllMoneyType, int moneyType, int dateInterval, boolean groupByNone, int groupBy, int sortBy, boolean categoryNone, String category) {
        this.total = 0;

        DateConverter dcs = new DateConverter(intializeSDateForDay(sDate));
        DateConverter dce = new DateConverter(intializeSDateForDay(eDate));
        switch (dateInterval) {
            case 1:
                sDate.setDate(1);
                int tempMon = eDate.getMonth() + 1;
                eDate.setMonth(tempMon);
                eDate.setDate(0);
                LoggerCus.d(TAG, sDate.toString() + " " + eDate.toString());
                break;

            case 2:
                sDate.setMonth(0);
                sDate.setDate(1);
                eDate.setYear(eDate.getYear() + 1);
                eDate.setMonth(0);
                eDate.setDate(0);
                LoggerCus.d(TAG, sDate.toString() + " " + eDate.toString());
                break;

            default:
                break;
        }
        ArrayList<MBRecord> mbr = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String eQuery = "";
        if (groupByNone)
            //todo
            eQuery = "SELECT " + KEY_DESCRIPTION + "," + KEY_AMOUNT + "," + KEY_DATE + "," + KEY_TYPE + "," + KEY_DATE_MONTH + "," + KEY_DATE_YEAR + "," + getSQLQueryForCat() + " FROM " + TABLE_NAME + " WHERE ";
        else {
            switch (groupBy) {
                case 0:
                    //todo
                    eQuery = "SELECT " + KEY_DESCRIPTION + ",sum(" + KEY_AMOUNT + ")," + KEY_DATE + "," + KEY_DATE_MONTH + "," + KEY_DATE_YEAR + "," + KEY_TYPE + ",count(" + KEY_DESCRIPTION + ") as ckd," + getSQLQueryForCat() + " FROM " + TABLE_NAME + " WHERE ";
                    break;

                case 1:
                    switch (dateInterval) {
                        case 0:
                            eQuery = "SELECT " + KEY_DESCRIPTION + ",sum(" + KEY_AMOUNT + ")," + KEY_DATE + "," + KEY_DATE_MONTH + "," + KEY_DATE_YEAR + "," + KEY_TYPE + ",count(" + KEY_DATE + ") as ckd," + getSQLQueryForCat() + " FROM " + TABLE_NAME + " WHERE ";
                            break;

                        case 1:
                            eQuery = "SELECT " + KEY_DESCRIPTION + ",sum(" + KEY_AMOUNT + ")," + KEY_DATE + "," + KEY_DATE_MONTH + "," + KEY_DATE_YEAR + "," + KEY_TYPE + ",count(" + KEY_DATE_MONTH + ") as ckd," + getSQLQueryForCat() + " FROM " + TABLE_NAME + " WHERE ";
                            break;

                        case 2:
                            eQuery = "SELECT " + KEY_DESCRIPTION + ",sum(" + KEY_AMOUNT + ")," + KEY_DATE + "," + KEY_DATE_MONTH + "," + KEY_DATE_YEAR + "," + KEY_TYPE + ",count(" + KEY_DATE_YEAR + ") as ckd," + getSQLQueryForCat() + " FROM " + TABLE_NAME + " WHERE ";
                            break;
                    }
                    break;
            }
        }
        eQuery += KEY_DESCRIPTION + " LIKE '%" + query + "%'";
        if (!isAllDate) {
            eQuery += " AND " + KEY_DATE + " >= " + dcs.getdDate().getTime() + " AND " + KEY_DATE + " <= " + dce.getdDate().getTime();
        }
        if (!isAllMoneyType)
            eQuery += " AND " + KEY_TYPE + " = " + moneyType;

        if (!categoryNone)
            eQuery += getCategoryQueryString(category);

        if (!groupByNone) {
            switch (groupBy) {
                case 0:
                    eQuery += " GROUP BY " + KEY_DESCRIPTION;
                    break;
                case 1:
                    //todo
                    switch (dateInterval) {
                        case 0:
                            eQuery += " GROUP BY " + KEY_DATE;
                            break;

                        case 1:
                            eQuery += " GROUP BY " + KEY_DATE_MONTH;
                            break;

                        case 2:
                            eQuery += " GROUP BY " + KEY_DATE_YEAR;
                            break;
                    }
                    break;
            }
        }
        switch (sortBy) {
            case 0:
                //todo
                eQuery += " ORDER BY " + KEY_DATE + " DESC";
                break;
            case 1:
                eQuery += " ORDER BY " + KEY_AMOUNT;
                break;
            case 2:
                eQuery += " ORDER BY " + KEY_DESCRIPTION;
                break;
        }
        Log.d(TAG, eQuery);
        Cursor cursor = db.rawQuery(eQuery, null);
        if (cursor != null) {
            final int len = cursor.getCount();
            for (int i = 0; i < len; i++) {
                cursor.moveToPosition(i);
                int numSpent = Integer.parseInt(cursor.getString(1));
                this.total += numSpent;
                if (!groupByNone) {
                    switch (groupBy) {
                        case 0:
                            mbr.add(new MBRecord(cursor.getString(0) + " (" + cursor.getLong(6) + ")", numSpent, new Date(cursor.getLong(2)), cursor.getInt(3), cursor.getString(7)));
                            break;
                        case 1:
                            switch (dateInterval) {
                                case 0:
                                    mbr.add(new MBRecord(new SimpleDateFormat("dd - MM - yyyy").format(new Date(cursor.getLong(2))) + " (" + cursor.getLong(6) + ")", numSpent, new Date(cursor.getLong(2)), cursor.getInt(3), cursor.getString(7)));
                                    break;

                                case 1:
                                    mbr.add(new MBRecord(new SimpleDateFormat("MM - yyyy").format(new Date(cursor.getLong(2))) + " (" + cursor.getLong(6) + ")", numSpent, new Date(cursor.getLong(2)), cursor.getInt(3), cursor.getString(7)));
                                    break;

                                case 2:
                                    mbr.add(new MBRecord(new SimpleDateFormat("yyyy").format(new Date(cursor.getLong(2))) + " (" + cursor.getLong(6) + ")", numSpent, new Date(cursor.getLong(2)), cursor.getInt(3), cursor.getString(7)));
                                    break;
                            }
                            break;
                    }
                } else {
                    mbr.add(new MBRecord(cursor.getString(0), numSpent, new Date(cursor.getLong(2)), cursor.getInt(3), cursor.getString(6)));
                }
            }
            cursor.close();
        }
        // return contact
        return mbr;
    }

    public int getTotal() {
        return this.total;
    }

    private Date intializeSDateForDay(Date sDate) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(sDate);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }

    private Date intializeEDateForDay(Date eDate) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(eDate);
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        return cal.getTime();
    }

    private String getCategoryQueryString(String s) {
        return " AND (" + KEY_CAT + " = (SELECT " + KEY_CAT_ID + " FROM " + CAT_TABLE_NAME + " WHERE " + KEY_NAME + " = '" + s + "'))";
    }

    public DashBoardRecord getDashBoardRecord(String s) {
        DashBoardRecord dbr = new DashBoardRecord();
        Date sDate = new Date();
        Date eDate = new Date();
        sDate = intializeSDateForDay(sDate);
        eDate = intializeEDateForDay(eDate);
        int type = 0;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;

        // getting day
        LoggerCus.d(TAG, sDate.toString() + " " + eDate.toString());
        cursor = db.rawQuery("SELECT sum(" + KEY_AMOUNT + ") FROM " + TABLE_NAME + " where " + KEY_DATE + " >= " + sDate.getTime() + " AND " + KEY_DATE + " <= " + eDate.getTime() + " AND " + KEY_TYPE + "=" + type + getCategoryQueryString(s), null);
        if ((cursor != null) && (cursor.getCount() > 0)) {
            cursor.moveToPosition(0);
            String temp = cursor.getString(0);
            if (temp != null)
                dbr.setDay(Integer.parseInt(temp));
            else
                dbr.setDay(0);
            cursor.close();
        }

        // getting month
        sDate.setDate(1);
        int tempMon = eDate.getMonth() + 1;
        eDate.setMonth(tempMon);
        eDate.setDate(0);
        LoggerCus.d(TAG, sDate.toString() + " " + eDate.toString());

        cursor = db.rawQuery("SELECT sum(" + KEY_AMOUNT + ") FROM " + TABLE_NAME + " where " + KEY_DATE + " >= " + sDate.getTime() + " AND " + KEY_DATE + " <= " + eDate.getTime() + " AND " + KEY_TYPE + "=" + type + getCategoryQueryString(s), null);
        if ((cursor != null) && (cursor.getCount() > 0)) {
            cursor.moveToPosition(0);
            String temp = cursor.getString(0);
            if (temp != null)
                dbr.setMonth(Integer.parseInt(temp));
            else
                dbr.setMonth(0);
            cursor.close();
        }

        // getting year
        sDate.setMonth(0);
        eDate.setYear(eDate.getYear() + 1);
        eDate.setMonth(0);
        eDate.setDate(0);

        LoggerCus.d(TAG, sDate.toString() + " " + eDate.toString());

        cursor = db.rawQuery("SELECT sum(" + KEY_AMOUNT + ") FROM " + TABLE_NAME + " where " + KEY_DATE + " >= " + sDate.getTime() + " AND " + KEY_DATE + " <= " + eDate.getTime() + " AND " + KEY_TYPE + "=" + type + getCategoryQueryString(s), null);
        if ((cursor != null) && (cursor.getCount() > 0)) {
            cursor.moveToPosition(0);
            String temp = cursor.getString(0);
            if (temp != null)
                dbr.setYear(Integer.parseInt(temp));
            else
                dbr.setYear(0);
            cursor.close();
        }
        dbr.setTotald(getTotalMoneySpentInCurrentDay());
        dbr.setTotalm(getTotalMoneySpentInCurrentMonth());
        dbr.setTotaly(getTotalMoneySpentInCurrentYear());
        dbr.setBalanceLeft(getBalanceLeft(s));
        dbr.setText(s.toUpperCase());
        db.close();
        return dbr;
    }

    private int getBalanceLeft(String s) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        int balLeft = -1;
        cursor = db.rawQuery("SELECT " + KEY_CAT_BAL + " FROM " + CAT_TABLE_NAME + " WHERE " + KEY_NAME + " = '" + s + "'", null);
        if ((cursor != null) && (cursor.getCount() > 0)) {
            cursor.moveToPosition(0);
            balLeft = cursor.getInt(0);
            cursor.close();
        }
        db.close();
        return balLeft;
    }

    public int getTotalMoneySpentInCurrentMonth() {
        Date sDate = new Date();
        Date eDate = new Date();

        sDate = intializeSDateForDay(sDate);
        eDate = intializeEDateForDay(eDate);

        sDate.setDate(1);
        int tempMon = eDate.getMonth() + 1;
        eDate.setMonth(tempMon);
        eDate.setDate(0);
        //LoggerCus.d(TAG, sDate.toString() + " " + eDate.toString());
        int type = 0;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        cursor = db.rawQuery("SELECT sum(" + KEY_AMOUNT + ") FROM " + TABLE_NAME + " where " + KEY_DATE + " >= " + sDate.getTime() + " AND " + KEY_DATE + " <= " + eDate.getTime() + " AND " + KEY_TYPE + "=" + type, null);
        int result = 0;
        if ((cursor != null) && (cursor.getCount() > 0)) {
            cursor.moveToPosition(0);
            String temp = cursor.getString(0);
            if (temp != null)
                result = Integer.parseInt(temp);
            else
                result = 0;
            cursor.close();
        }
        db.close();
        return result;
    }

    public int getTotalMoneySpentInCurrentDay() {

        Date sDate = new Date();
        Date eDate = new Date();

        sDate = intializeSDateForDay(sDate);
        eDate = intializeEDateForDay(eDate);
        //LoggerCus.d(TAG, sDate.toString() + " " + eDate.toString());
        int type = 0;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        cursor = db.rawQuery("SELECT sum(" + KEY_AMOUNT + ") FROM " + TABLE_NAME + " where " + KEY_DATE + " >= " + sDate.getTime() + " AND " + KEY_DATE + " <= " + eDate.getTime() + " AND " + KEY_TYPE + "=" + type, null);
        int result = 0;
        if ((cursor != null) && (cursor.getCount() > 0)) {
            cursor.moveToPosition(0);
            String temp = cursor.getString(0);
            if (temp != null)
                result = Integer.parseInt(temp);
            else
                result = 0;
            cursor.close();
        }
        db.close();
        return result;
    }

    public int getTotalMoneySpentInCurrentYear() {
        Date sDate = new Date();
        Date eDate = new Date();

        sDate = intializeSDateForDay(sDate);
        eDate = intializeEDateForDay(eDate);

        sDate.setDate(1);
        int tempMon = eDate.getMonth() + 1;
        eDate.setMonth(tempMon);
        eDate.setDate(0);

        sDate.setMonth(0);
        eDate.setYear(eDate.getYear() + 1);
        eDate.setMonth(0);
        eDate.setDate(0);
        //LoggerCus.d(TAG, sDate.toString() + " " + eDate.toString());
        int type = 0;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        cursor = db.rawQuery("SELECT sum(" + KEY_AMOUNT + ") FROM " + TABLE_NAME + " where " + KEY_DATE + " >= " + sDate.getTime() + " AND " + KEY_DATE + " <= " + eDate.getTime() + " AND " + KEY_TYPE + "=" + type, null);
        int result = 0;
        if ((cursor != null) && (cursor.getCount() > 0)) {
            cursor.moveToPosition(0);
            String temp = cursor.getString(0);
            if (temp != null)
                result = Integer.parseInt(temp);
            else
                result = 0;
            cursor.close();
        }
        db.close();
        return result;
    }

    public ArrayList<DashBoardRecord> getDashBoardRecords() {
        ArrayList<DashBoardRecord> dbr = new ArrayList<>();
        String cols[] = getCategeories();
        final int len = cols.length;
        for (int i = 0; i < len; i++) {
            dbr.add(getDashBoardRecord(cols[i]));
        }
        return dbr;
    }

    public String[] getCategeories() {
        SQLiteDatabase db = this.getReadableDatabase();
        int type = 0;
        Cursor cursor = null;
        cursor = db.rawQuery("SELECT " + KEY_NAME + " FROM " + CAT_TABLE_NAME + " ORDER BY " + KEY_CAT_ID, null);
        String cols[] = null;
        int len = 0;
        if (cursor != null) {
            len = cursor.getCount();
            cols = new String[len];
            for (int i = 0; i < len; i++) {
                cursor.moveToPosition(i);
                cols[i] = cursor.getString(0);
            }
            cursor.close();
        }
        db.close();

        LoggerCus.d(TAG, Arrays.toString(cols));
        return cols;
    }

    public void deleteCategory(String s) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("UPDATE " + TABLE_NAME + " SET " + KEY_CAT + " = (SELECT " + KEY_CAT_ID + " FROM " + CAT_TABLE_NAME + " WHERE " + KEY_NAME + " = 'others') WHERE " + KEY_CAT + " = (SELECT " + KEY_CAT_ID + " FROM " + CAT_TABLE_NAME + " WHERE " + KEY_NAME + " = '" + s + "')");
        db.delete(CAT_TABLE_NAME, KEY_NAME + " = '" + s + "'", null);
        db.close();
    }

    public void exec() {

    }

    public boolean insertMsgRecord(String des, String amount, int type, String cate, String balLeft, String msgStr, String name) {
        ContentValues values = new ContentValues();
        values.put(MSG_KEY_NAME, name);
        values.put(MSG_KEY_DESCRIPTION, des);
        values.put(MSG_KEY_AMOUNT, amount);
        values.put(MSG_KEY_TYPE, type);
        values.put(MSG_KEY_CAT, getIdOfCategory(cate));
        values.put(MSG_KEY_LEFT_BAL, balLeft);
        values.put(MSG_KEY_MSG, encode(msgStr));

        SQLiteDatabase db = this.getWritableDatabase();
        long result = db.insert(MSG_TABLE_NAME, null, values);
        db.close();
        if (result != -1)
            return true;
        else
            return false;
    }

    private String encode(String msgStr) {
        String s = "";
        try {
            s = URLEncoder.encode(msgStr, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            LoggerCus.d(TAG, e.getMessage());
        }
        return s;
    }

    private String decode(String msgStr) {
        String s = "";
        try {
            s = URLDecoder.decode(msgStr, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            LoggerCus.d(TAG, e.getMessage());
        }
        return s;
    }

    public ArrayList<String> getMsgRecordsAsList() {
        ArrayList<String> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT " + MSG_KEY_NAME + " FROM " + MSG_TABLE_NAME, null);
        if (cursor != null) {
            final int len = cursor.getCount();
            for (int i = 0; i < len; i++) {
                cursor.moveToPosition(i);
                list.add(cursor.getString(0));
            }
            cursor.close();
        }

        db.close();
        return list;
    }

    public ArrayList getMsgDetails(String msgName) {
        SQLiteDatabase db = this.getReadableDatabase();

        ArrayList<String> list = new ArrayList<>();
        Cursor cursor = db.rawQuery("SELECT " + MSG_KEY_DESCRIPTION + "," + MSG_KEY_AMOUNT + "," + MSG_KEY_TYPE + "," + MSG_KEY_CAT + "," + MSG_KEY_LEFT_BAL + "," + MSG_KEY_MSG + " FROM " + MSG_TABLE_NAME + " WHERE " + MSG_KEY_NAME + " = '" + msgName + "'", null);
        if (cursor != null) {
            final int len = cursor.getCount();
            for (int i = 0; i < len; i++) {
                cursor.moveToPosition(i);
                list.add(cursor.getString(0));
                list.add(cursor.getString(1));
                list.add("" + cursor.getInt(2));
                list.add("" + getNameOfCategory(cursor.getInt(3)));
                list.add(cursor.getString(4));
                list.add(decode(cursor.getString(5)));
            }
            cursor.close();
        }

        db.close();
        return list;
    }

    public boolean deleteMessage(String msgName) {
        SQLiteDatabase db = this.getReadableDatabase();
        int n = db.delete(MSG_TABLE_NAME, MSG_KEY_NAME + " = '" + msgName + "'", null);
        db.close();
        if (n > 0)
            return true;
        else
            return false;
    }

    public ArrayList getMsgRecordsAsMap() {
        ArrayList<HashMap> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT " + MSG_KEY_NAME + "," + MSG_KEY_DESCRIPTION + ","
                + MSG_KEY_AMOUNT + "," + MSG_KEY_LEFT_BAL + "," + MSG_KEY_TYPE + "," + MSG_KEY_CAT
                + "," + MSG_KEY_MSG + " FROM " + MSG_TABLE_NAME, null);
        if (cursor != null) {
            final int len = cursor.getCount();
            for (int i = 0; i < len; i++) {
                cursor.moveToPosition(i);
                HashMap<String, String> map = new HashMap<>();
                map.put(MSG_KEY_NAME, cursor.getString(0));
                map.put(MSG_KEY_DESCRIPTION, cursor.getString(1));
                map.put(MSG_KEY_AMOUNT, cursor.getString(2));
                map.put(MSG_KEY_LEFT_BAL, cursor.getString(3));
                map.put(MSG_KEY_TYPE, "" + cursor.getInt(4));
                map.put(MSG_KEY_CAT, "" + cursor.getLong(5));
                map.put(MSG_KEY_MSG, decode(cursor.getString(6)));
                list.add(map);
            }
            cursor.close();
        }

        db.close();
        return list;
    }
}
