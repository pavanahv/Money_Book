package com.example.allakumarreddy.moneybook.storage.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.allakumarreddy.moneybook.utils.AnalyticsFilterData;
import com.example.allakumarreddy.moneybook.utils.DashBoardRecord;
import com.example.allakumarreddy.moneybook.utils.DateConverter;
import com.example.allakumarreddy.moneybook.utils.GlobalConstants;
import com.example.allakumarreddy.moneybook.utils.LoggerCus;
import com.example.allakumarreddy.moneybook.utils.MBRecord;
import com.example.allakumarreddy.moneybook.utils.Utils;

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

    public static final String TABLE_NAME = "MoneyBook";
    public static final String KEY_TYPE = "type";
    public static final String KEY_DESCRIPTION = "description";
    public static final String KEY_AMOUNT = "amount";
    public static final String KEY_DATE = "day";
    public static final String KEY_DATE_MONTH = "month";
    public static final String KEY_DATE_YEAR = "year";
    public static final String KEY_CAT = "category";
    private static final String KEY_PAYMENT_METHOD = "payment_method";

    public static final String CAT_TABLE_NAME = "Catageories";
    public static final String KEY_NAME = "name";
    public static final String KEY_CAT_ID = "id";
    public static final String KEY_CAT_TYPE = "type";

    public static final String PAY_METH_TABLE_NAME = "PaymentMethod";
    public static final String KEY_PAY_METH_NAME = "name";
    public static final String KEY_PAY_METH_ID = "id";
    public static final String KEY_PAY_METH_BAL = "balance_left";

    public static final String MSG_TABLE_NAME = "Message";
    public static final String MSG_KEY_TYPE = "type";
    public static final String MSG_KEY_DESCRIPTION = "description";
    public static final String MSG_KEY_AMOUNT = "amount";
    public static final String MSG_KEY_CAT = "category";
    public static final String MSG_KEY_LEFT_BAL = "balance_left";
    public static final String MSG_KEY_NAME = "name";
    public static final String MSG_KEY_MSG = "msg";

    public static final String F_TABLE_NAME = "Filter";
    public static final String F_KEY_NAME = "name";
    public static final String F_KEY_FILTER = "filter";
    public static final String F_KEY_SHOW_DASH = "showOnDash";

    public static final String MT_TABLE_NAME = "MoneyTransfer";
    public static final String MT_KEY_DESCRIPTION = "description";
    public static final String MT_KEY_AMOUNT = "amount";
    public static final String MT_KEY_DATE = "day";
    public static final String MT_KEY_DATE_MONTH = "month";
    public static final String MT_KEY_DATE_YEAR = "year";
    public static final String MT_KEY_CAT_FROM = "from_category";
    public static final String MT_KEY_CAT_TO = "to_category";
    private static final String MT_KEY_TYPE = "4";
    private static final int MT_TYPE = 4;
    private static final String SORTING_ORDER[] = new String[]{"ASC", "DESC"};
    public static final String KEY_CAT_BAL = "balance_left";
    public static final String MSG_KEY_PAYMENT_METHOD = "payment_method";

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
                + KEY_AMOUNT + " INTEGER," + KEY_DATE + " INTEGER," + KEY_DATE_MONTH + " INTEGER,"
                + KEY_DATE_YEAR + " INTEGER," + KEY_CAT + " INTEGER, " + KEY_PAYMENT_METHOD + " INTEGER)";
        db.execSQL(CREATE_CONTACTS_TABLE);

        String CREATE_CAT_TABLE = "CREATE TABLE " + CAT_TABLE_NAME + "("
                + KEY_CAT_ID + " INTEGER PRIMARY KEY,"
                + KEY_NAME + " TEXT," + KEY_CAT_TYPE + " INTEGER)";
        db.execSQL(CREATE_CAT_TABLE);

        String CREATE_PAY_METH_TABLE = "CREATE TABLE " + PAY_METH_TABLE_NAME + "("
                + KEY_PAY_METH_ID + " INTEGER PRIMARY KEY,"
                + KEY_PAY_METH_NAME + " TEXT," + KEY_PAY_METH_BAL + " INTEGER)";
        db.execSQL(CREATE_PAY_METH_TABLE);

        String CREATE_MESSAGE_TABLE = "CREATE TABLE " + MSG_TABLE_NAME + "("
                + MSG_KEY_TYPE + " INTEGER," + MSG_KEY_DESCRIPTION + " TEXT,"
                + MSG_KEY_AMOUNT + " TEXT," + MSG_KEY_CAT + " INTEGER,"
                + MSG_KEY_PAYMENT_METHOD + " INTEGER,"
                + MSG_KEY_LEFT_BAL + " TEXT," + MSG_KEY_NAME + " TEXT,"
                + MSG_KEY_MSG + " TEXT)";
        db.execSQL(CREATE_MESSAGE_TABLE);

        String CREATE_MT_TABLE = "CREATE TABLE " + MT_TABLE_NAME + "("
                + MT_KEY_DESCRIPTION + " TEXT,"
                + MT_KEY_AMOUNT + " INTEGER," + MT_KEY_DATE + " INTEGER,"
                + MT_KEY_DATE_MONTH + " INTEGER," + MT_KEY_DATE_YEAR
                + " INTEGER," + MT_KEY_CAT_FROM + " INTEGER,"
                + MT_KEY_CAT_TO + " INTEGER)";
        db.execSQL(CREATE_MT_TABLE);

        String CREATE_FILTER_TABLE = "CREATE TABLE " + F_TABLE_NAME + "("
                + F_KEY_NAME + " TEXT PRIMARY KEY,"
                + F_KEY_FILTER + " TEXT," + F_KEY_SHOW_DASH + " INTEGER)";
        db.execSQL(CREATE_FILTER_TABLE);
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + CAT_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + MSG_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + MT_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + PAY_METH_TABLE_NAME);
        // Create tables again
        onCreate(db);
    }

    public long getIdOfCategory(String name, int type) {
        if (type == GlobalConstants.TYPE_DUE_REPAYMENT
                || type == GlobalConstants.TYPE_DUE_PAYMENT)
            type = GlobalConstants.TYPE_DUE;
        else if (type == GlobalConstants.TYPE_LOAN_REPAYMENT
                || type == GlobalConstants.TYPE_LOAN_PAYMENT)
            type = GlobalConstants.TYPE_LOAN;

        SQLiteDatabase db = this.getReadableDatabase();
        long result = -1L;
        Cursor cursor = db.rawQuery("SELECT " + KEY_CAT_ID + " FROM " + CAT_TABLE_NAME + " WHERE " + KEY_NAME + " = '" + name + "' AND " + KEY_CAT_TYPE + " = " + type, null);
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

    public String getNameOfPaymentMethod(long id) {
        SQLiteDatabase db = this.getReadableDatabase();
        String result = "";
        Cursor cursor = db.rawQuery("SELECT " + KEY_PAY_METH_NAME + " FROM " + PAY_METH_TABLE_NAME + " WHERE " + KEY_PAY_METH_ID + " = " + id + "", null);
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

    public void addCategory(String name, int type) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_NAME, name.toLowerCase());
        values.put(KEY_CAT_TYPE, type);
        db.insert(CAT_TABLE_NAME, null, values);
        db.close();
    }

    public boolean addRecord(MBRecord mbr, int type) {
        String des = mbr.getDescription();
        if (type == GlobalConstants.TYPE_DUE ||
                type == GlobalConstants.TYPE_LOAN ||
                type == GlobalConstants.TYPE_DUE_PAYMENT ||
                type == GlobalConstants.TYPE_LOAN_PAYMENT) {
            des += "{{" + System.currentTimeMillis() + "}}";
        }
        DateConverter dc = new DateConverter(intializeSDateForDay(mbr.getDate()));
        ContentValues values = new ContentValues();
        values.put(KEY_TYPE, type);
        values.put(KEY_DESCRIPTION, des);
        values.put(KEY_AMOUNT, mbr.getAmount());
        values.put(KEY_DATE, dc.getdDate().getTime());
        values.put(KEY_DATE_MONTH, dc.getmDate().getTime());
        values.put(KEY_DATE_YEAR, dc.getyDate().getTime());
        values.put(KEY_CAT, getIdOfCategory(mbr.getCategory(), type));
        values.put(KEY_PAYMENT_METHOD, getIdOfPaymentMethod(mbr.getPaymentMethod()));
        LoggerCus.d(TAG, values.toString());

        SQLiteDatabase db = this.getWritableDatabase();
        // Inserting Row
        long result = db.insert(TABLE_NAME, null, values);
        db.close();
        if (result != -1)
            return true;
        else
            return false;
    }

    public void addRePaymentRecord(MBRecord mbr, int type, MBRecord mainMbr) {
        DateConverter dc = new DateConverter(intializeSDateForDay(mbr.getDate()));
        ContentValues values = new ContentValues();
        values.put(KEY_TYPE, mbr.getType());
        values.put(KEY_DESCRIPTION, mbr.getDescription());
        values.put(KEY_AMOUNT, mbr.getAmount());
        values.put(KEY_DATE, dc.getdDate().getTime());
        values.put(KEY_DATE_MONTH, dc.getmDate().getTime());
        values.put(KEY_DATE_YEAR, dc.getyDate().getTime());
        values.put(KEY_CAT, getIdOfCategory(mbr.getCategory(), type));
        values.put(KEY_PAYMENT_METHOD, getIdOfPaymentMethod(mbr.getPaymentMethod()));
        LoggerCus.d(TAG, values.toString());

        SQLiteDatabase db = this.getWritableDatabase();
        // Inserting Row
        db.insert(TABLE_NAME, null, values);
        db.close();

        if (mainMbr.getType() == GlobalConstants.TYPE_DUE_PAYMENT ||
                mainMbr.getType() == GlobalConstants.TYPE_LOAN_PAYMENT) {
            return;
        }

        int lBal = getRePaymentAmount(mbr.getType(), mainMbr.getmRefIdForLoanDue());
        if (lBal != -1) {
            int temp = mainMbr.getAmount() - lBal;
            if (temp <= 0) {
                updateRePaymentRecord(mainMbr);
            }
        }
    }

    private void updateRePaymentRecord(MBRecord mbr) {
        ContentValues values = new ContentValues();
        int type = -1;
        if (mbr.getType() == GlobalConstants.TYPE_DUE)
            type = GlobalConstants.TYPE_DUE_PAYMENT;
        else if (mbr.getType() == GlobalConstants.TYPE_LOAN)
            type = GlobalConstants.TYPE_LOAN_PAYMENT;
        else
            return;
        values.put(KEY_TYPE, type);
        SQLiteDatabase db = this.getWritableDatabase();
        int res = db.update(TABLE_NAME, values,
                KEY_DESCRIPTION + " = '" + mbr.getDescription() + "{{" + mbr.getmRefIdForLoanDue() + "}}'", null);
        LoggerCus.d(TAG, mbr.toString() + " " + values.toString() + " updateRePaymentRecord() -> " + res);
        db.close();
    }

    private long getIdOfPaymentMethod(String name) {
        SQLiteDatabase db = this.getReadableDatabase();
        long result = -1L;
        Cursor cursor = db.rawQuery("SELECT " + KEY_PAY_METH_ID + " FROM " + PAY_METH_TABLE_NAME + " WHERE " + KEY_PAY_METH_NAME + " = '" + name + "'", null);
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

    public boolean addRecordWithCatAsID(MBRecord mbr, int type) {
        DateConverter dc = new DateConverter(intializeSDateForDay(mbr.getDate()));
        ContentValues values = new ContentValues();
        values.put(KEY_TYPE, type);
        values.put(KEY_DESCRIPTION, mbr.getDescription());
        values.put(KEY_AMOUNT, mbr.getAmount());
        values.put(KEY_DATE, dc.getdDate().getTime());
        values.put(KEY_DATE_MONTH, dc.getmDate().getTime());
        values.put(KEY_DATE_YEAR, dc.getyDate().getTime());
        values.put(KEY_CAT, Long.parseLong(mbr.getCategory()));
        values.put(KEY_PAYMENT_METHOD, Long.parseLong(mbr.getPaymentMethod()));

        SQLiteDatabase db = this.getWritableDatabase();
        // Inserting Row
        long result = db.insert(TABLE_NAME, null, values);
        db.close();
        if (result != -1)
            return true;
        else
            return false;
    }

    public boolean updateBalLeft(long payId, int bal) {
        ContentValues values = new ContentValues();
        values.put(KEY_PAY_METH_BAL, bal);

        SQLiteDatabase db = this.getWritableDatabase();
        int result = db.update(PAY_METH_TABLE_NAME, values, KEY_PAY_METH_ID + "=" + payId, null);
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
            JSONArray jsonArrayf = obj.getJSONArray(F_TABLE_NAME);
            JSONArray jsonArrayp = obj.getJSONArray(PAY_METH_TABLE_NAME);

            final int len = jsonArray.length();
            for (int i = 0; i < len; i++) {
                JSONObject jsonObj = jsonArray.getJSONObject(i);
                ContentValues values = new ContentValues();
                values.put(KEY_TYPE, Integer.parseInt(jsonObj.getString(KEY_TYPE)));
                values.put(KEY_DESCRIPTION, jsonObj.getString(KEY_DESCRIPTION));
                values.put(KEY_AMOUNT, Integer.parseInt(jsonObj.getString(KEY_AMOUNT)));

                DateConverter dc = new DateConverter(jsonObj.getString(KEY_DATE));
                dc.setdDate(intializeSDateForDay(dc.getdDate()));
                dc.initialize();

                values.put(KEY_DATE, dc.getdDate().getTime());
                values.put(KEY_DATE_MONTH, dc.getmDate().getTime());
                values.put(KEY_DATE_YEAR, dc.getyDate().getTime());
                values.put(KEY_CAT, jsonObj.getLong(KEY_CAT));
                values.put(KEY_PAYMENT_METHOD, jsonObj.getLong(KEY_PAYMENT_METHOD));

                // Inserting Row
                db.insert(TABLE_NAME, null, values);
            }

            final int len1 = jsonArrayp.length();
            for (int i = 0; i < len1; i++) {
                JSONObject jsonObj = jsonArrayp.getJSONObject(i);
                ContentValues values = new ContentValues();
                values.put(KEY_PAY_METH_NAME, jsonObj.getString(KEY_PAY_METH_NAME));
                values.put(KEY_PAY_METH_ID, jsonObj.getLong(KEY_PAY_METH_ID));
                values.put(KEY_PAY_METH_BAL, jsonObj.getLong(KEY_PAY_METH_BAL));
                // Inserting Row
                db.insert(PAY_METH_TABLE_NAME, null, values);
            }

            final int len2 = jsonArrayc.length();
            for (int i = 0; i < len2; i++) {
                JSONObject jsonObj = jsonArrayc.getJSONObject(i);
                ContentValues values = new ContentValues();
                values.put(KEY_NAME, jsonObj.getString(KEY_NAME));
                values.put(KEY_CAT_ID, jsonObj.getLong(KEY_CAT_ID));
                values.put(KEY_CAT_TYPE, jsonObj.getLong(KEY_CAT_TYPE));
                // Inserting Row
                db.insert(CAT_TABLE_NAME, null, values);
            }

            final int lenm = jsonArraym.length();
            for (int i = 0; i < lenm; i++) {
                JSONObject jsonObj = jsonArraym.getJSONObject(i);
                ContentValues values = new ContentValues();
                values.put(MSG_KEY_NAME, jsonObj.getString(MSG_KEY_NAME));
                values.put(MSG_KEY_DESCRIPTION, jsonObj.getString(MSG_KEY_DESCRIPTION));
                values.put(MSG_KEY_AMOUNT, jsonObj.getString(MSG_KEY_AMOUNT));
                values.put(MSG_KEY_TYPE, jsonObj.getInt(MSG_KEY_TYPE));
                values.put(MSG_KEY_CAT, jsonObj.getLong(MSG_KEY_CAT));
                values.put(MSG_KEY_LEFT_BAL, jsonObj.getString(MSG_KEY_LEFT_BAL));
                values.put(MSG_KEY_MSG, jsonObj.getString(MSG_KEY_MSG));
                values.put(MSG_KEY_PAYMENT_METHOD, jsonObj.getLong(MSG_KEY_PAYMENT_METHOD));
                // Inserting Row
                db.insert(MSG_TABLE_NAME, null, values);
            }

            final int lenf = jsonArrayf.length();
            for (int i = 0; i < lenf; i++) {
                JSONObject jsonObj = jsonArrayf.getJSONObject(i);
                ContentValues values = new ContentValues();
                values.put(F_KEY_NAME, jsonObj.getString(F_KEY_NAME));
                values.put(F_KEY_FILTER, jsonObj.getString(F_KEY_FILTER));
                values.put(F_KEY_SHOW_DASH, jsonObj.getString(F_KEY_SHOW_DASH));
                // Inserting Row
                db.insert(F_TABLE_NAME, null, values);
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
        db.delete(F_TABLE_NAME, null, null);
        db.delete(PAY_METH_TABLE_NAME, null, null);
        db.close();
    }

    public boolean updateRecord(MBRecord mbrOld, MBRecord mbrNew) {
        int type = mbrOld.getType();

        if (type == GlobalConstants.TYPE_DUE ||
                type == GlobalConstants.TYPE_LOAN ||
                type == GlobalConstants.TYPE_DUE_PAYMENT ||
                type == GlobalConstants.TYPE_LOAN_PAYMENT) {
            if (mbrOld.getmRefIdForLoanDue() == null) {
                if (deleteRecord(mbrOld) > 0) {
                    return addRecord(mbrNew, mbrNew.getType());
                } else
                    return false;
            } else {
                boolean isUpdated = false;
                if (mbrOld.getAmount() != mbrNew.getAmount()) {
                    if (mbrOld.getType() == GlobalConstants.TYPE_DUE_PAYMENT ||
                            mbrOld.getType() == GlobalConstants.TYPE_LOAN_PAYMENT) {
                        if (mbrNew.getAmount() > mbrOld.getAmount()) {
                            int tempType = -1;
                            if (mbrOld.getType() == GlobalConstants.TYPE_DUE_PAYMENT)
                                tempType = GlobalConstants.TYPE_DUE_REPAYMENT;
                            else if (mbrOld.getType() == GlobalConstants.TYPE_LOAN_PAYMENT)
                                tempType = GlobalConstants.TYPE_LOAN_REPAYMENT;

                            int lBal = getRePaymentAmount(tempType, mbrOld.getmRefIdForLoanDue());
                            if (lBal != -1) {
                                int temp = mbrNew.getAmount() - lBal;
                                if (temp <= 0) {
                                    updateRePaymentRecord(mbrOld);
                                } else {
                                    isUpdated = updateRePaymentRecordToNormal(mbrOld);
                                }
                            }
                        }
                    }
                }

                if (isUpdated) {
                    if (mbrOld.getType() == GlobalConstants.TYPE_DUE_PAYMENT)
                        type = GlobalConstants.TYPE_DUE;
                    else if (mbrOld.getType() == GlobalConstants.TYPE_LOAN_PAYMENT)
                        type = GlobalConstants.TYPE_LOAN;
                }

                DateConverter dc = new DateConverter(intializeSDateForDay(mbrNew.getDate()));
                ContentValues values = new ContentValues();
                values.put(KEY_TYPE, type);
                values.put(KEY_DESCRIPTION, mbrNew.getDescription() + "{{" + mbrOld.getmRefIdForLoanDue() + "}}");
                values.put(KEY_AMOUNT, mbrNew.getAmount());
                values.put(KEY_DATE, dc.getdDate().getTime());
                values.put(KEY_DATE_MONTH, dc.getmDate().getTime());
                values.put(KEY_DATE_YEAR, dc.getyDate().getTime());
                values.put(KEY_CAT, getIdOfCategory(mbrNew.getCategory(), type));
                values.put(KEY_PAYMENT_METHOD, getIdOfPaymentMethod(mbrNew.getPaymentMethod()));
                LoggerCus.d(TAG, values.toString());

                SQLiteDatabase db = this.getWritableDatabase();
                int res = db.update(TABLE_NAME, values,
                        KEY_DESCRIPTION + " = '" + mbrOld.getDescription()
                                + "{{" + mbrOld.getmRefIdForLoanDue() + "}}' AND "
                                + KEY_TYPE + " = " + type, null);
                LoggerCus.d(TAG, mbrNew.toString() + " " + values.toString() + " updateRecord() -> " + res);
                db.close();

                if (res > 0)
                    return true;
                else
                    return false;
            }
        } else {
            if (deleteRecord(mbrOld) > 0) {
                return addRecord(mbrNew, mbrNew.getType());
            } else
                return false;
        }
    }

    private boolean updateRePaymentRecordToNormal(MBRecord mbr) {
        ContentValues values = new ContentValues();
        int type = -1;
        if (mbr.getType() == GlobalConstants.TYPE_DUE_PAYMENT)
            type = GlobalConstants.TYPE_DUE;
        else if (mbr.getType() == GlobalConstants.TYPE_LOAN_PAYMENT)
            type = GlobalConstants.TYPE_LOAN;
        else
            return false;

        values.put(KEY_TYPE, type);
        SQLiteDatabase db = this.getWritableDatabase();
        int res = db.update(TABLE_NAME, values,
                KEY_DESCRIPTION + " = '" + mbr.getDescription() + "{{" + mbr.getmRefIdForLoanDue() + "}}'", null);
        LoggerCus.d(TAG, mbr.toString() + " " + values.toString() + " updateRePaymentRecordToNormal() -> " + res);
        db.close();
        if (res > 0)
            return true;
        else
            return false;
    }

    public int deleteRecord(MBRecord mbr) {
        DateConverter dc = new DateConverter(mbr.getDate());
        Date sDate = dc.getdDate();
        Date eDate = (Date) sDate.clone();
        sDate = intializeSDateForDay(sDate);
        eDate = intializeEDateForDay(eDate);

        String des = mbr.getDescription();
        if (mbr.getType() == GlobalConstants.TYPE_DUE_PAYMENT ||
                mbr.getType() == GlobalConstants.TYPE_DUE_PAYMENT) {
            if (mbr.getmRefIdForLoanDue() != null) {
                deleteRePaymentHistory(mbr);
                des += "{{" + mbr.getmRefIdForLoanDue() + "}}";
            }
        }
        String query = KEY_DESCRIPTION + " = '" + des + "' AND "
                + KEY_AMOUNT + " = " + mbr.getAmount() + " AND " + KEY_TYPE
                + " = " + mbr.getType() + " AND " + KEY_CAT + " = "
                + getIdOfCategory(mbr.getCategory(), mbr.getType()) + " AND " + KEY_PAYMENT_METHOD
                + " = " + getIdOfPaymentMethod(mbr.getPaymentMethod()) + " AND "
                + KEY_DATE + " >= " + sDate.getTime() + " AND " + KEY_DATE + " <= "
                + eDate.getTime();
        SQLiteDatabase db = this.getWritableDatabase();
        int n = db.delete(TABLE_NAME, query, null);
        db.close();
        LoggerCus.d(TAG, query);

        if (mbr.getType() == GlobalConstants.TYPE_DUE_REPAYMENT ||
                mbr.getType() == GlobalConstants.TYPE_LOAN_REPAYMENT) {

            int lBal = getRePaymentAmount(mbr.getType(), mbr.getDescription());
            MBRecord parentMbr = getRepaymentParentRecord(mbr);
            LoggerCus.d(TAG, parentMbr.toString());
            if (lBal != -1) {
                int temp = parentMbr.getAmount() - lBal;
                if (temp > 0) {
                    updateRePaymentRecordToNormal(parentMbr);
                }
            }
        }

        return n;
    }

    private MBRecord getRepaymentParentRecord(MBRecord mbr) {
        int type = -1;
        if (mbr.getType() == GlobalConstants.TYPE_DUE_REPAYMENT)
            type = GlobalConstants.TYPE_DUE_PAYMENT;
        else if (mbr.getType() == GlobalConstants.TYPE_LOAN_REPAYMENT)
            type = GlobalConstants.TYPE_LOAN_PAYMENT;

        String query = "SELECT " + KEY_DESCRIPTION + "," + KEY_AMOUNT + ","
                + KEY_DATE + "," + getSQLQueryForCat() + "," + getSQLQueryForPayMeth()
                + "," + KEY_TYPE
                + " FROM " + TABLE_NAME + " where " + KEY_DESCRIPTION + " LIKE '%" + mbr.getDescription()
                + "%' "
                + " AND " + KEY_TYPE + "=" + type;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        MBRecord temp = null;
        if (cursor != null) {
            final int len = cursor.getCount();
            for (int i = 0; i < len; i++) {
                cursor.moveToPosition(i);
                temp = new MBRecord(cursor.getString(0),
                        Integer.parseInt(cursor.getString(1)),
                        new Date(cursor.getLong(2)), cursor.getInt(5),
                        cursor.getString(3),
                        cursor.getString(4));
                if (type == GlobalConstants.TYPE_DUE ||
                        type == GlobalConstants.TYPE_LOAN ||
                        type == GlobalConstants.TYPE_DUE_PAYMENT ||
                        type == GlobalConstants.TYPE_LOAN_PAYMENT) {
                    Utils.addRefIdForLoanDueFromDes(temp);
                }
            }
        }
        db.close();
        return temp;
    }

    private void deleteRePaymentHistory(MBRecord mbr) {
        int type = -1;
        if (mbr.getType() == GlobalConstants.TYPE_DUE_PAYMENT) {
            type = GlobalConstants.TYPE_DUE_REPAYMENT;
        } else if (mbr.getType() == GlobalConstants.TYPE_LOAN_PAYMENT) {
            type = GlobalConstants.TYPE_LOAN_REPAYMENT;
        } else
            return;
        String query = KEY_DESCRIPTION + " = '" + mbr.getmRefIdForLoanDue() + "' AND " + KEY_TYPE + " = " + type;
        SQLiteDatabase db = this.getWritableDatabase();
        int n = db.delete(TABLE_NAME, query, null);
        db.close();
        LoggerCus.d(TAG, "Repayment History deleted! -> " + n);
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
        String query = "";
        if (type == GlobalConstants.TYPE_DUE) {
            query = "SELECT " + KEY_DESCRIPTION + "," + KEY_AMOUNT + ","
                    + KEY_DATE + "," + getSQLQueryForCat() + "," + getSQLQueryForPayMeth()
                    + "," + KEY_TYPE
                    + " FROM " + TABLE_NAME + " where " + KEY_DATE + " >= " + temp1.getTime()
                    + " AND " + KEY_DATE + " <= " + temp2.getTime()
                    + " AND (" + KEY_TYPE + "=" + type + " OR "
                    + KEY_TYPE + " = " + GlobalConstants.TYPE_DUE_PAYMENT + ")";
        } else if (type == GlobalConstants.TYPE_LOAN) {
            query = "SELECT " + KEY_DESCRIPTION + "," + KEY_AMOUNT + ","
                    + KEY_DATE + "," + getSQLQueryForCat() + "," + getSQLQueryForPayMeth()
                    + "," + KEY_TYPE
                    + " FROM " + TABLE_NAME + " where " + KEY_DATE + " >= " + temp1.getTime()
                    + " AND " + KEY_DATE + " <= " + temp2.getTime()
                    + " AND (" + KEY_TYPE + "=" + type + " OR "
                    + KEY_TYPE + " = " + GlobalConstants.TYPE_LOAN_PAYMENT + ")";
        } else {
            query = "SELECT " + KEY_DESCRIPTION + "," + KEY_AMOUNT + ","
                    + KEY_DATE + "," + getSQLQueryForCat() + "," + getSQLQueryForPayMeth()
                    + "," + KEY_TYPE
                    + " FROM " + TABLE_NAME + " where " + KEY_DATE + " >= " + temp1.getTime()
                    + " AND " + KEY_DATE + " <= " + temp2.getTime()
                    + " AND " + KEY_TYPE + "=" + type;
        }
        LoggerCus.d(TAG, query);
        cursor = db.rawQuery(query, null);
        if (cursor != null) {
            final int len = cursor.getCount();
            for (int i = 0; i < len; i++) {
                cursor.moveToPosition(i);
                MBRecord mbrTemp = new MBRecord(cursor.getString(0),
                        Integer.parseInt(cursor.getString(1)),
                        new Date(cursor.getLong(2)), cursor.getInt(5),
                        cursor.getString(3),
                        cursor.getString(4));
                if (type == GlobalConstants.TYPE_DUE ||
                        type == GlobalConstants.TYPE_LOAN ||
                        type == GlobalConstants.TYPE_DUE_PAYMENT ||
                        type == GlobalConstants.TYPE_LOAN_PAYMENT) {
                    Utils.addRefIdForLoanDueFromDes(mbrTemp);
                }
                LoggerCus.d(TAG, mbrTemp.toString());
                mbr.add(mbrTemp);
            }
            cursor.close();
        }
        return mbr;
    }

    public int getTotalOfType(String date, int type) {
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

        int total = -1;
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = null;
        cursor = db.rawQuery("SELECT SUM(" + KEY_AMOUNT + ") FROM " + TABLE_NAME + " where " + KEY_DATE + " >= " + temp1.getTime() + " AND " + KEY_DATE + " <= " + temp2.getTime() + " AND " + KEY_TYPE + "=" + type, null);
        if (cursor != null) {
            final int len = cursor.getCount();
            for (int i = 0; i < len; i++) {
                cursor.moveToPosition(i);
                total = cursor.getInt(0);
            }
            cursor.close();
        }
        // return contact
        return total;
    }

    public String getRecords() {
        ArrayList<MBRecord> mbr = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        JSONArray jsonArray = new JSONArray();

        Cursor cursor = db.rawQuery("SELECT " + KEY_DESCRIPTION + ","
                + KEY_AMOUNT + "," + KEY_DATE + "," + KEY_TYPE
                + "," + KEY_CAT + "," + KEY_DATE_MONTH + ","
                + KEY_DATE_YEAR + "," + KEY_PAYMENT_METHOD
                + " FROM " + TABLE_NAME, null);

        if (cursor != null) {
            final int len = cursor.getCount();
            for (int i = 0; i < len; i++) {
                cursor.moveToPosition(i);
                try {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put(KEY_DESCRIPTION, cursor.getString(0));
                    jsonObject.put(KEY_AMOUNT, cursor.getString(1));
                    jsonObject.put(KEY_DATE, format.format(new Date(cursor.getLong(2))));
                    //jsonObject.put("test_day", cursor.getLong(2));
                    //jsonObject.put("test_month", cursor.getLong(5));
                    //jsonObject.put("test_year", cursor.getLong(6));
                    jsonObject.put(KEY_TYPE, cursor.getString(3));
                    jsonObject.put(KEY_CAT, cursor.getLong(4));
                    jsonObject.put(KEY_PAYMENT_METHOD, cursor.getLong(7));
                    jsonArray.put(jsonObject);
                } catch (JSONException e) {
                    LoggerCus.d(TAG, e.getMessage());
                }
            }
            cursor.close();
        }

        JSONArray jsonArrayc = new JSONArray();
        cursor = db.rawQuery("SELECT " + KEY_NAME + "," + KEY_CAT_ID + ","
                + KEY_CAT_TYPE + " FROM " + CAT_TABLE_NAME, null);
        if (cursor != null) {
            final int len = cursor.getCount();
            for (int i = 0; i < len; i++) {
                cursor.moveToPosition(i);
                try {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put(KEY_NAME, cursor.getString(0));
                    jsonObject.put(KEY_CAT_ID, cursor.getLong(1));
                    jsonObject.put(KEY_CAT_TYPE, cursor.getLong(2));
                    jsonArrayc.put(jsonObject);
                } catch (JSONException e) {
                    LoggerCus.d(TAG, e.getMessage());
                }
            }
            cursor.close();
        }

        JSONArray jsonArraym = new JSONArray();
        cursor = db.rawQuery("SELECT " + MSG_KEY_NAME + "," + MSG_KEY_DESCRIPTION + "," + MSG_KEY_AMOUNT + ","
                + MSG_KEY_TYPE + "," + MSG_KEY_CAT + "," + MSG_KEY_LEFT_BAL
                + "," + MSG_KEY_MSG + "," + MSG_KEY_PAYMENT_METHOD
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
                    jsonObject.put(MSG_KEY_PAYMENT_METHOD, cursor.getLong(7));
                    jsonArraym.put(jsonObject);
                } catch (JSONException e) {
                    LoggerCus.d(TAG, e.getMessage());
                }
            }
            cursor.close();
        }

        JSONArray jsonArrayf = new JSONArray();
        cursor = db.rawQuery("SELECT " + F_KEY_NAME + "," + F_KEY_FILTER + "," + F_KEY_SHOW_DASH
                + " FROM " + F_TABLE_NAME, null);
        if (cursor != null) {
            final int len = cursor.getCount();
            for (int i = 0; i < len; i++) {
                cursor.moveToPosition(i);
                try {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put(F_KEY_NAME, cursor.getString(0));
                    jsonObject.put(F_KEY_FILTER, cursor.getString(1));
                    jsonObject.put(F_KEY_SHOW_DASH, cursor.getString(2));
                    jsonArrayf.put(jsonObject);
                } catch (JSONException e) {
                    LoggerCus.d(TAG, e.getMessage());
                }
            }
            cursor.close();
        }

        JSONArray jsonArrayp = new JSONArray();
        cursor = db.rawQuery("SELECT " + KEY_PAY_METH_ID + ","
                + KEY_PAY_METH_NAME + "," + KEY_PAY_METH_BAL
                + " FROM " + PAY_METH_TABLE_NAME, null);
        if (cursor != null) {
            final int len = cursor.getCount();
            for (int i = 0; i < len; i++) {
                cursor.moveToPosition(i);
                try {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put(KEY_PAY_METH_ID, cursor.getLong(0));
                    jsonObject.put(KEY_PAY_METH_NAME, cursor.getString(1));
                    jsonObject.put(KEY_PAY_METH_BAL, cursor.getInt(2));
                    jsonArrayp.put(jsonObject);
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
            obj.put(F_TABLE_NAME, jsonArrayf);
            obj.put(PAY_METH_TABLE_NAME, jsonArrayp);
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

        Cursor cursor = db.rawQuery("SELECT " + KEY_DESCRIPTION + "," + KEY_AMOUNT
                + "," + KEY_DATE + "," + KEY_TYPE + "," + getSQLQueryForCat() + ","
                + getSQLQueryForPayMeth() + " FROM " + TABLE_NAME, null);
        if (cursor != null) {
            final int len = cursor.getCount();
            for (int i = 0; i < len; i++) {
                cursor.moveToPosition(i);
                int numSpent = Integer.parseInt(cursor.getString(1));
                this.total += numSpent;
                mbr.add(new MBRecord(cursor.getString(0), numSpent, new Date(cursor.getLong(2)), cursor.getInt(3), cursor.getString(4), cursor.getString(5)));
            }
            cursor.close();
        }
        // return contact
        return mbr;
    }

    private String getSQLQueryForCat() {
        return "(SELECT " + KEY_NAME + " FROM " + CAT_TABLE_NAME
                + " WHERE " + KEY_CAT_ID + " = " + KEY_CAT + ") AS " + KEY_CAT;
    }

    private String getSQLQueryForPayMeth() {
        return "(SELECT " + KEY_PAY_METH_NAME + " FROM " + PAY_METH_TABLE_NAME
                + " WHERE " + KEY_PAY_METH_ID + " = " + KEY_PAYMENT_METHOD + ") AS " + KEY_PAYMENT_METHOD;
    }

    private String getSQLQueryForCatMT() {
        return "(SELECT " + KEY_NAME + " FROM " + CAT_TABLE_NAME + " WHERE " + KEY_CAT_ID + " = " + MT_KEY_CAT_FROM + ") AS " + MT_KEY_CAT_FROM;
    }

    public ArrayList<MBRecord> getRecordsAsList(String query, boolean[] dateBool, Date sDate, Date eDate,
                                                int moneyType, int dateInterval,
                                                boolean groupByNone, int groupBy, int sortBy,
                                                boolean[] categoryBool, String[] category,
                                                int sortingOrder,
                                                boolean[] payBool, String[] payMeth) {
        this.total = 0;

        ArrayList<MBRecord> mbr = new ArrayList<>();

        int date = -1;
        for (int i = 0; i < dateBool.length; i++) {
            if (dateBool[i]) {
                date = i;
                break;
            }
        }

        if (date != 3 && date != 4) {
            sDate = new Date();
            eDate = new Date();
            switch (date) {
                case 2:
                    sDate = intializeSDateForYear(sDate);
                    eDate = intializeEDateForYear(eDate);
                case 1:
                    sDate = intializeSDateForMonth(sDate);
                    eDate = intializeEDateForMonth(eDate);
                case 0:
                    sDate = intializeSDateForDay(sDate);
                    eDate = intializeEDateForDay(eDate);
                    break;
            }
        }

        DateConverter dcs = new DateConverter(intializeSDateForDay(sDate));
        DateConverter dce = new DateConverter(intializeSDateForDay(eDate));

        switch (dateInterval) {
            case 2:
                sDate = intializeSDateForYear(sDate);
                eDate = intializeEDateForYear(eDate);
            case 1:
                sDate = intializeSDateForMonth(intializeSDateForDay(sDate));
                eDate = intializeEDateForMonth(intializeEDateForDay(eDate));
                LoggerCus.d(TAG, sDate.toString() + " " + eDate.toString());
                break;

            default:
                break;
        }

        SQLiteDatabase db = this.getReadableDatabase();
        String eQuery = "";
        if (groupByNone)
            eQuery = "SELECT " + KEY_DESCRIPTION + "," + KEY_AMOUNT + "," + KEY_DATE + ","
                    + KEY_TYPE + "," + KEY_DATE_MONTH + "," + KEY_DATE_YEAR + ","
                    + getSQLQueryForCat() + "," + getSQLQueryForPayMeth()
                    + " FROM " + TABLE_NAME + " WHERE ";
        else {
            switch (groupBy) {
                case 0:
                    eQuery = "SELECT " + KEY_DESCRIPTION + ",sum(" + KEY_AMOUNT + ") AS " + KEY_AMOUNT + "," + KEY_DATE
                            + "," + KEY_DATE_MONTH + "," + KEY_DATE_YEAR + "," + KEY_TYPE
                            + ",count(" + KEY_DESCRIPTION + ") as ckd," + getSQLQueryForCat()
                            + "," + getSQLQueryForPayMeth() + " FROM " + TABLE_NAME + " WHERE ";
                    break;

                case 1:
                    switch (dateInterval) {
                        case 0:
                            eQuery = "SELECT " + KEY_DESCRIPTION + ",sum(" + KEY_AMOUNT + ") AS "
                                    + KEY_AMOUNT + "," + KEY_DATE + "," + KEY_DATE_MONTH + ","
                                    + KEY_DATE_YEAR + "," + KEY_TYPE + ",count(" + KEY_DATE + ") as ckd,"
                                    + getSQLQueryForCat() + "," + getSQLQueryForPayMeth()
                                    + " FROM " + TABLE_NAME + " WHERE ";
                            break;

                        case 1:
                            eQuery = "SELECT " + KEY_DESCRIPTION + ",sum(" + KEY_AMOUNT + ") AS " + KEY_AMOUNT
                                    + "," + KEY_DATE + "," + KEY_DATE_MONTH + "," + KEY_DATE_YEAR + ","
                                    + KEY_TYPE + ",count(" + KEY_DATE_MONTH + ") as ckd,"
                                    + getSQLQueryForCat() + "," + getSQLQueryForPayMeth()
                                    + " FROM " + TABLE_NAME + " WHERE ";
                            break;

                        case 2:
                            eQuery = "SELECT " + KEY_DESCRIPTION + ",sum(" + KEY_AMOUNT + ") AS " + KEY_AMOUNT
                                    + "," + KEY_DATE + "," + KEY_DATE_MONTH + "," + KEY_DATE_YEAR + ","
                                    + KEY_TYPE + ",count(" + KEY_DATE_YEAR + ") as ckd,"
                                    + getSQLQueryForCat() + "," + getSQLQueryForPayMeth()
                                    + " FROM " + TABLE_NAME + " WHERE ";
                            break;
                    }
                    break;
                case 2:
                    eQuery = "SELECT " + KEY_DESCRIPTION + ",sum(" + KEY_AMOUNT + ") AS " + KEY_AMOUNT + "," + KEY_DATE
                            + "," + KEY_DATE_MONTH + "," + KEY_DATE_YEAR + "," + KEY_TYPE
                            + ",count(" + KEY_CAT + ") as cat_count," + getSQLQueryForCat()
                            + "," + getSQLQueryForPayMeth() + " FROM " + TABLE_NAME + " WHERE ";
                    break;
                case 3:
                    eQuery = "SELECT " + KEY_DESCRIPTION + ",sum(" + KEY_AMOUNT + ") AS " + KEY_AMOUNT + "," + KEY_DATE
                            + "," + KEY_DATE_MONTH + "," + KEY_DATE_YEAR + "," + KEY_TYPE
                            + ",count(" + KEY_PAYMENT_METHOD + ") as payment_method_count," + getSQLQueryForCat()
                            + "," + getSQLQueryForPayMeth() + " FROM " + TABLE_NAME + " WHERE ";
                    break;
            }
        }
        eQuery += KEY_DESCRIPTION + " LIKE '%" + query + "%'";

        switch (date) {
            case 0:
            case 1:
            case 2:
            case 3:
                eQuery += " AND " + KEY_DATE + " >= " + dcs.getdDate().getTime() + " AND " + KEY_DATE + " <= " + dce.getdDate().getTime();
                break;
            case 4:
                // this is date all so everything should come
                break;
        }
        eQuery += getQueryForMoneyType(moneyType);

        eQuery += getCategoryQueryString(categoryBool, category, moneyType);

        eQuery += getPaymentMethodQueryString(payBool, payMeth);

        if (!groupByNone) {
            switch (groupBy) {
                case 0:
                    eQuery += " GROUP BY " + KEY_DESCRIPTION;
                    break;
                case 1:
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
                case 2:
                    eQuery += " GROUP BY " + KEY_CAT;
                    break;
                case 3:
                    eQuery += " GROUP BY " + KEY_PAYMENT_METHOD;
                    break;
            }
        }
        switch (sortBy) {
            case 0:
                eQuery += " ORDER BY " + KEY_DATE + " " + SORTING_ORDER[sortingOrder];
                break;
            case 1:
                eQuery += " ORDER BY " + KEY_AMOUNT + " " + SORTING_ORDER[sortingOrder];
                break;
            case 2:
                eQuery += " ORDER BY " + KEY_DESCRIPTION + " " + SORTING_ORDER[sortingOrder];
                break;
        }
        LoggerCus.d(TAG, eQuery);
        Cursor cursor = db.rawQuery(eQuery, null);
        if (cursor != null) {
            final int len = cursor.getCount();
            for (int i = 0; i < len; i++) {
                MBRecord mbrTemp = null;
                cursor.moveToPosition(i);
                int numSpent = Integer.parseInt(cursor.getString(1));
                this.total += numSpent;
                if (!groupByNone) {
                    switch (groupBy) {
                        case 0:
                            mbrTemp = new MBRecord(cursor.getString(0)
                                    + " (" + cursor.getLong(6) + ")", numSpent,
                                    new Date(cursor.getLong(2)), cursor.getInt(5),
                                    cursor.getString(7), cursor.getString(8));
                            break;
                        case 1:
                            switch (dateInterval) {
                                case 0:
                                    mbrTemp = new MBRecord(new SimpleDateFormat("dd - MM - yyyy")
                                            .format(new Date(cursor.getLong(2))) + " ("
                                            + cursor.getLong(6) + ")", numSpent,
                                            new Date(cursor.getLong(2)), cursor.getInt(5),
                                            cursor.getString(7), cursor.getString(8));
                                    break;

                                case 1:
                                    mbrTemp = new MBRecord(new SimpleDateFormat("MM - yyyy")
                                            .format(new Date(cursor.getLong(2))) + " ("
                                            + cursor.getLong(6) + ")", numSpent,
                                            new Date(cursor.getLong(2)), cursor.getInt(5),
                                            cursor.getString(7), cursor.getString(8));
                                    break;

                                case 2:
                                    mbrTemp = new MBRecord(new SimpleDateFormat("yyyy")
                                            .format(new Date(cursor.getLong(2))) + " ("
                                            + cursor.getLong(6) + ")", numSpent,
                                            new Date(cursor.getLong(2)), cursor.getInt(5),
                                            cursor.getString(7), cursor.getString(8));
                                    break;
                            }
                            break;
                        case 2:
                            mbrTemp = new MBRecord(cursor.getString(7)
                                    + " (" + cursor.getLong(6) + ")", numSpent,
                                    new Date(cursor.getLong(2)), cursor.getInt(5),
                                    cursor.getString(7), cursor.getString(8));
                            break;
                        case 3:
                            mbrTemp = new MBRecord(cursor.getString(8)
                                    + " (" + cursor.getLong(6) + ")", numSpent,
                                    new Date(cursor.getLong(2)), cursor.getInt(5),
                                    cursor.getString(7), cursor.getString(8));
                            break;
                    }
                } else {
                    mbrTemp = new MBRecord(cursor.getString(0), numSpent, new Date(cursor.getLong(2)),
                            cursor.getInt(3), cursor.getString(6), cursor.getString(7));
                }
                if (moneyType == GlobalConstants.TYPE_DUE ||
                        moneyType == GlobalConstants.TYPE_LOAN ||
                        moneyType == GlobalConstants.TYPE_DUE_PAYMENT ||
                        moneyType == GlobalConstants.TYPE_LOAN_PAYMENT) {
                    Utils.addRefIdForLoanDueFromDes(mbrTemp);
                }
                mbr.add(mbrTemp);
            }
            cursor.close();
        }
        // return contact
        return mbr;
    }

    private String getPaymentMethodQueryString(boolean[] payBool, String[] payMeth) {

        String eQuery = "";
        eQuery += " AND (";
        boolean started = false;
        boolean evenOneCatTrue = false;
        for (int i = 1; i < payBool.length; i++) {
            if (payBool[i]) {
                evenOneCatTrue = true;
                if (started)
                    eQuery += " OR ";
                else
                    started = true;
                eQuery += "(" + KEY_PAYMENT_METHOD + " = (SELECT " + KEY_PAY_METH_ID + " FROM " + PAY_METH_TABLE_NAME
                        + " WHERE " + KEY_PAY_METH_NAME + " = '" + payMeth[i] + "')) ";
            }
        }
        eQuery += ")";
        if (evenOneCatTrue)
            return eQuery;
        else
            return "";

    }

    private String getQueryForMoneyType(int moneyType) {
        String eQuery = "";
        eQuery += " AND (";
        if (moneyType == GlobalConstants.TYPE_DUE) {
            eQuery += "  " + KEY_TYPE + " = " + moneyType + " OR " + KEY_TYPE + " = " + GlobalConstants.TYPE_DUE_PAYMENT;
        } else if (moneyType == GlobalConstants.TYPE_LOAN) {
            eQuery += "  " + KEY_TYPE + " = " + moneyType + " OR " + KEY_TYPE + " = " + GlobalConstants.TYPE_LOAN_PAYMENT;
        } else {
            eQuery += "  " + KEY_TYPE + " = " + moneyType;
        }
        eQuery += " )";
        return eQuery;
    }

    public int getTotal() {
        return this.total;
    }

    private Date intializeSDateForDay(Date sDate) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(sDate);
        cal.set(Calendar.HOUR_OF_DAY, cal.getMinimum(Calendar.HOUR_OF_DAY));
        cal.set(Calendar.MINUTE, cal.getMinimum(Calendar.MINUTE));
        cal.set(Calendar.SECOND, cal.getMinimum(Calendar.SECOND));
        cal.set(Calendar.MILLISECOND, cal.getMinimum(Calendar.MILLISECOND));
        return cal.getTime();
    }

    private Date intializeEDateForDay(Date eDate) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(eDate);
        cal.set(Calendar.HOUR_OF_DAY, cal.getMaximum(Calendar.HOUR_OF_DAY));
        cal.set(Calendar.MINUTE, cal.getMaximum(Calendar.MINUTE));
        cal.set(Calendar.SECOND, cal.getMaximum(Calendar.SECOND));
        cal.set(Calendar.MILLISECOND, cal.getMaximum(Calendar.MILLISECOND));
        return cal.getTime();
    }

    private Date intializeSDateForMonth(Date sDate) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(sDate);
        cal.set(Calendar.DATE, cal.getMinimum(Calendar.DATE));
        return cal.getTime();
    }

    private Date intializeEDateForMonth(Date eDate) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(eDate);
        cal.set(Calendar.DATE, cal.getMaximum(Calendar.DATE));
        return cal.getTime();
    }

    private Date intializeSDateForYear(Date sDate) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(sDate);
        cal.set(Calendar.MONTH, cal.getMinimum(Calendar.MONTH));
        return cal.getTime();
    }

    private Date intializeEDateForYear(Date eDate) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(eDate);
        cal.set(Calendar.MONTH, cal.getMaximum(Calendar.MONTH));
        return cal.getTime();
    }

    private String getCategoryQueryString(boolean[] res, String[] s, int type) {
        String eQuery = "";
        eQuery += " AND (";
        boolean started = false;
        boolean evenOneCatTrue = false;
        for (int i = 1; i < res.length; i++) {
            if (res[i]) {
                evenOneCatTrue = true;
                if (started)
                    eQuery += " OR ";
                else
                    started = true;
                eQuery += "(" + KEY_CAT + " = (SELECT " + KEY_CAT_ID + " FROM " + CAT_TABLE_NAME
                        + " WHERE " + KEY_NAME + " = '" + s[i] + "' AND " +
                        KEY_CAT_TYPE + " = " + type + ")) ";
            }
        }
        eQuery += ")";
        if (evenOneCatTrue)
            return eQuery;
        else
            return "";
    }

    public DashBoardRecord getDashBoardRecord(String s, int type) {
        DashBoardRecord dbr = new DashBoardRecord();
        Date sDate = new Date();
        Date eDate = new Date();
        sDate = intializeSDateForDay(sDate);
        eDate = intializeEDateForDay(eDate);
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;

        // getting day
        LoggerCus.d(TAG, sDate.toString() + " " + eDate.toString());
        cursor = db.rawQuery("SELECT sum(" + KEY_AMOUNT + ") FROM " + TABLE_NAME + " where " + KEY_DATE + " >= " + sDate.getTime() + " AND " + KEY_DATE + " <= " + eDate.getTime() + " AND " + KEY_TYPE + "=" + type + getCategoryQueryString(s, type), null);
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

        cursor = db.rawQuery("SELECT sum(" + KEY_AMOUNT + ") FROM " + TABLE_NAME + " where " + KEY_DATE + " >= " + sDate.getTime() + " AND " + KEY_DATE + " <= " + eDate.getTime() + " AND " + KEY_TYPE + "=" + type + getCategoryQueryString(s, type), null);
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

        cursor = db.rawQuery("SELECT sum(" + KEY_AMOUNT + ") FROM " + TABLE_NAME + " where " + KEY_DATE + " >= " + sDate.getTime() + " AND " + KEY_DATE + " <= " + eDate.getTime() + " AND " + KEY_TYPE + "=" + type + getCategoryQueryString(s, type), null);
        if ((cursor != null) && (cursor.getCount() > 0)) {
            cursor.moveToPosition(0);
            String temp = cursor.getString(0);
            if (temp != null)
                dbr.setYear(Integer.parseInt(temp));
            else
                dbr.setYear(0);
            cursor.close();
        }
        dbr.setTotald(getTotalMoneyInCurrentDay(type));
        dbr.setTotalm(getTotalMoneyInCurrentMonth(type));
        dbr.setTotaly(getTotalMoneyInCurrentYear(type));
        //dbr.setBalanceLeft(getBalanceLeft(s));
        dbr.setBalanceLeft(0);
        dbr.setText(s.toUpperCase());
        db.close();
        return dbr;
    }

    private String getCategoryQueryString(String s, int type) {
        return " AND (" + KEY_CAT + " = (SELECT " + KEY_CAT_ID + " FROM " + CAT_TABLE_NAME + " WHERE " + KEY_NAME + " = '" + s + "' AND " + KEY_CAT_TYPE + " = " + type + "))";
    }

    private String getPaymentMethodQueryString(String s) {
        return " AND (" + KEY_PAYMENT_METHOD + " = (SELECT " + KEY_PAY_METH_ID + " FROM " + PAY_METH_TABLE_NAME + " WHERE " + KEY_PAY_METH_NAME + " = '" + s + "'))";
    }

    private int getBalanceLeft(String s) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        int balLeft = -1;
        cursor = db.rawQuery("SELECT " + KEY_PAY_METH_BAL + " FROM " + PAY_METH_TABLE_NAME + " WHERE " + KEY_PAY_METH_NAME + " = '" + s + "'", null);
        if ((cursor != null) && (cursor.getCount() > 0)) {
            cursor.moveToPosition(0);
            balLeft = cursor.getInt(0);
            cursor.close();
        }
        db.close();
        return balLeft;
    }

    public int getTotalMoneyInCurrentMonth(int type) {
        Date sDate = new Date();
        Date eDate = new Date();

        sDate = intializeSDateForDay(sDate);
        eDate = intializeEDateForDay(eDate);

        sDate.setDate(1);
        int tempMon = eDate.getMonth() + 1;
        eDate.setMonth(tempMon);
        eDate.setDate(0);
        //LoggerCus.d(TAG, sDate.toString() + " " + eDate.toString());

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

    public int getTotalMoneyInCurrentDay(int type) {

        Date sDate = new Date();
        Date eDate = new Date();

        sDate = intializeSDateForDay(sDate);
        eDate = intializeEDateForDay(eDate);
        //LoggerCus.d(TAG, sDate.toString() + " " + eDate.toString());

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

    public int getTotalMoneyInCurrentYear(int type) {
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

    public ArrayList<DashBoardRecord> getDashBoardRecords(int type) {
        ArrayList<DashBoardRecord> dbr = new ArrayList<>();
        String cols[] = getCategeories(type);
        final int len = cols.length;
        for (int i = 0; i < len; i++) {
            dbr.add(getDashBoardRecord(cols[i], type));
        }
        return dbr;
    }

    public ArrayList<DashBoardRecord> getPaymentMethodRecords() {
        ArrayList<DashBoardRecord> dbr = new ArrayList<>();
        String cols[] = getPaymentMethods();
        final int len = cols.length;
        for (int i = 0; i < len; i++) {
            dbr.add(getPaymentMethodRecord(cols[i]));
        }
        return dbr;
    }

    private DashBoardRecord getPaymentMethodRecord(String s) {
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
        cursor = db.rawQuery("SELECT sum(" + KEY_AMOUNT + ") FROM " + TABLE_NAME + " where " + KEY_DATE + " >= " + sDate.getTime() + " AND " + KEY_DATE + " <= " + eDate.getTime() + " AND " + KEY_TYPE + "=" + type + getPaymentMethodQueryString(s), null);
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

        cursor = db.rawQuery("SELECT sum(" + KEY_AMOUNT + ") FROM " + TABLE_NAME + " where " + KEY_DATE + " >= " + sDate.getTime() + " AND " + KEY_DATE + " <= " + eDate.getTime() + " AND " + KEY_TYPE + "=" + type + getPaymentMethodQueryString(s), null);
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

        cursor = db.rawQuery("SELECT sum(" + KEY_AMOUNT + ") FROM " + TABLE_NAME + " where " + KEY_DATE + " >= " + sDate.getTime() + " AND " + KEY_DATE + " <= " + eDate.getTime() + " AND " + KEY_TYPE + "=" + type + getPaymentMethodQueryString(s), null);
        if ((cursor != null) && (cursor.getCount() > 0)) {
            cursor.moveToPosition(0);
            String temp = cursor.getString(0);
            if (temp != null)
                dbr.setYear(Integer.parseInt(temp));
            else
                dbr.setYear(0);
            cursor.close();
        }
        dbr.setTotald(getTotalMoneyInCurrentDay(GlobalConstants.TYPE_SPENT));
        dbr.setTotalm(getTotalMoneyInCurrentMonth(GlobalConstants.TYPE_SPENT));
        dbr.setTotaly(getTotalMoneyInCurrentYear(GlobalConstants.TYPE_SPENT));
        dbr.setBalanceLeft(getBalanceLeft(s));
        dbr.setText(s.toUpperCase());
        db.close();
        return dbr;
    }

    public String[] getPaymentMethods() {
        SQLiteDatabase db = this.getReadableDatabase();
        int type = 0;
        Cursor cursor = null;
        cursor = db.rawQuery("SELECT " + KEY_PAY_METH_NAME + " FROM " + PAY_METH_TABLE_NAME + " ORDER BY " + KEY_PAY_METH_ID, null);
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

    public String[] getCategeories(int type) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        cursor = db.rawQuery("SELECT " + KEY_NAME + " FROM " + CAT_TABLE_NAME + " WHERE " + KEY_CAT_TYPE + " = " + type + " ORDER BY " + KEY_CAT_ID, null);
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

    public void deletePaymentMethod(String s) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("UPDATE " + TABLE_NAME + " SET " + KEY_PAYMENT_METHOD + " = (SELECT " + KEY_PAY_METH_ID + " FROM " + PAY_METH_TABLE_NAME + " WHERE " + KEY_PAY_METH_NAME + " = 'others') WHERE " + KEY_PAYMENT_METHOD + " = (SELECT " + KEY_PAY_METH_ID + " FROM " + PAY_METH_TABLE_NAME + " WHERE " + KEY_PAY_METH_NAME + " = '" + s + "')");
        db.delete(PAY_METH_TABLE_NAME, KEY_PAY_METH_NAME + " = '" + s + "'", null);
        db.close();
    }

    public void exec() {
    }

    public boolean insertMsgRecord(String des, String amount, int type, String cate, String balLeft, String msgStr, String name, String payStr) {
        ContentValues values = new ContentValues();
        values.put(MSG_KEY_NAME, name);
        values.put(MSG_KEY_DESCRIPTION, des);
        values.put(MSG_KEY_AMOUNT, amount);
        values.put(MSG_KEY_TYPE, type);
        values.put(MSG_KEY_CAT, getIdOfCategory(cate, type));
        values.put(MSG_KEY_PAYMENT_METHOD, getIdOfPaymentMethod(payStr));
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
        Cursor cursor = db.rawQuery("SELECT " + MSG_KEY_DESCRIPTION + "," + MSG_KEY_AMOUNT + "," + MSG_KEY_TYPE + "," + MSG_KEY_CAT + "," + MSG_KEY_LEFT_BAL + "," + MSG_KEY_MSG + "," + MSG_KEY_PAYMENT_METHOD + " FROM " + MSG_TABLE_NAME + " WHERE " + MSG_KEY_NAME + " = '" + msgName + "'", null);
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
                list.add("" + getNameOfPaymentMethod(cursor.getInt(6)));
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
                + "," + MSG_KEY_MSG + "," + MSG_KEY_PAYMENT_METHOD + " FROM " + MSG_TABLE_NAME, null);
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
                map.put(MSG_KEY_PAYMENT_METHOD, "" + cursor.getLong(7));
                list.add(map);
            }
            cursor.close();
        }

        db.close();
        return list;
    }

    public boolean addMTRecord(MBRecord mbr) {
        DateConverter dc = new DateConverter(intializeSDateForDay(mbr.getDate()));
        ContentValues values = new ContentValues();
        values.put(KEY_TYPE, MT_TYPE);
        values.put(KEY_DESCRIPTION, mbr.getToCategory());
        values.put(KEY_AMOUNT, mbr.getAmount());
        values.put(KEY_DATE, dc.getdDate().getTime());
        values.put(KEY_DATE_MONTH, dc.getmDate().getTime());
        values.put(KEY_DATE_YEAR, dc.getyDate().getTime());
        values.put(KEY_CAT, getIdOfCategory(mbr.getCategory(), GlobalConstants.TYPE_SPENT));
        values.put(KEY_PAYMENT_METHOD, getIdOfPaymentMethod(mbr.getPaymentMethod()));

        SQLiteDatabase db = this.getWritableDatabase();
        // Inserting Row
        long result = db.insert(TABLE_NAME, null, values);
        db.close();
        if (result != -1)
            return true;
        else
            return false;
    }

    public ArrayList<MBRecord> getDesForAutoComplete(String s, int type) {
        ArrayList<MBRecord> list = new ArrayList<>();

        if (type == GlobalConstants.TYPE_DUE_PAYMENT)
            type = GlobalConstants.TYPE_DUE;
        else if (type == GlobalConstants.TYPE_LOAN_PAYMENT)
            type = GlobalConstants.TYPE_LOAN;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT DISTINCT " + KEY_DESCRIPTION
                + "," + KEY_AMOUNT + "," + getSQLQueryForCat() + ","
                + getSQLQueryForPayMeth() + ", " + KEY_TYPE
                + " FROM " + TABLE_NAME + " WHERE "
                + KEY_TYPE + " = " + type
                + " AND (" + KEY_DESCRIPTION + " LIKE '%" + s + "%'"
                + " OR " + KEY_DESCRIPTION + " LIKE '" + s + "%'"
                + " OR " + KEY_DESCRIPTION + " LIKE '" + s + "'"
                + " OR " + KEY_DESCRIPTION + " LIKE '%" + s + "')"
                + " LIMIT 5", null);
        if (cursor != null) {
            final int len = cursor.getCount();
            for (int i = 0; i < len; i++) {
                cursor.moveToPosition(i);
                type = cursor.getInt(4);
                MBRecord mbrTemp = new MBRecord(cursor.getString(0),
                        Integer.parseInt(cursor.getString(1)),
                        null, type,
                        cursor.getString(2),
                        cursor.getString(3));
                if (type == GlobalConstants.TYPE_DUE ||
                        type == GlobalConstants.TYPE_LOAN ||
                        type == GlobalConstants.TYPE_DUE_PAYMENT ||
                        type == GlobalConstants.TYPE_LOAN_PAYMENT) {
                    Utils.addRefIdForLoanDueFromDes(mbrTemp);
                }
                LoggerCus.d(TAG, mbrTemp.toString());
                list.add(mbrTemp);
            }
            cursor.close();
        }

        db.close();
        //LoggerCus.d(TAG,list.toString());
        return list;
    }

    public boolean insertFilterRecord(String name, String filter, boolean showOnDash) {
        ContentValues values = new ContentValues();
        values.put(F_KEY_NAME, name);
        values.put(F_KEY_FILTER, encode(filter));
        values.put(F_KEY_SHOW_DASH, (showOnDash ? 1 : 0));

        SQLiteDatabase db = this.getWritableDatabase();
        long result = db.insert(F_TABLE_NAME, null, values);
        db.close();
        if (result != -1)
            return true;
        else
            return false;
    }

    public String[][] getDashBoardFilterRecords() {
        SQLiteDatabase db = this.getReadableDatabase();
        String[][] records = null;
        Cursor cursor = db.rawQuery("SELECT " + F_KEY_NAME + ", " + F_KEY_FILTER + " FROM " + F_TABLE_NAME + " WHERE " + F_KEY_SHOW_DASH + " = 1", null);
        if (cursor != null) {
            final int len = cursor.getCount();
            records = new String[len][2];
            for (int i = 0; i < len; i++) {
                cursor.moveToPosition(i);
                records[i][0] = cursor.getString(0);
                records[i][1] = decode(cursor.getString(1));
            }
            cursor.close();
        }
        return records;
    }

    public void deleteFilter(String s) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(F_TABLE_NAME, F_KEY_NAME + " = '" + s + "'", null);
        db.close();
    }

    public ArrayList<MBRecord> getRecordsAsList(AnalyticsFilterData mAnalyticsFilterData) {

        // creating dateinterval var
        int dateInterval = -1;
        for (int i = 0; i < mAnalyticsFilterData.subMenuDateIntervalDataBool.length; i++) {
            if (mAnalyticsFilterData.subMenuDateIntervalDataBool[i]) {
                dateInterval = i;
                break;
            }
        }

        // initializing group by var
        int groupBy = -1;
        if (!mAnalyticsFilterData.subMenuGroupByDataBool[4]) {
            for (int i = 0; i < mAnalyticsFilterData.subMenuGroupByDataBool.length - 1; i++) {
                if (mAnalyticsFilterData.subMenuGroupByDataBool[i]) {
                    groupBy = i;
                    break;
                }
            }
        }

        int sortBy = -1;
        for (int i = 0; i < mAnalyticsFilterData.subMenuSortByDataBool.length; i++) {
            if (mAnalyticsFilterData.subMenuSortByDataBool[i]) {
                sortBy = i;
                break;
            }
        }

        // intializing sorting order var
        int sortingOrder = -1;
        for (int i = 0; i < mAnalyticsFilterData.subMenuSortingOrderDataBool.length; i++) {
            if (mAnalyticsFilterData.subMenuSortingOrderDataBool[i]) {
                sortingOrder = i;
                break;
            }
        }

        // intializing sorting order var
        int moneyType = -1;
        for (int i = 0; i < mAnalyticsFilterData.subMenuMoneyTypeDataBool.length; i++) {
            if (mAnalyticsFilterData.subMenuMoneyTypeDataBool[i]) {
                moneyType = i;
                break;
            }
        }

        return getRecordsAsList(mAnalyticsFilterData.queryText,
                mAnalyticsFilterData.subMenuDateDataBool,
                mAnalyticsFilterData.sDate,
                mAnalyticsFilterData.eDate,
                moneyType,
                dateInterval,
                mAnalyticsFilterData.subMenuGroupByDataBool[4],
                groupBy,
                sortBy,
                mAnalyticsFilterData.subMenuCatogeoryDataBool,
                mAnalyticsFilterData.subMenuCatogeoryData,
                sortingOrder,
                mAnalyticsFilterData.subMenuPaymentMethodDataBool,
                mAnalyticsFilterData.subMenuPaymentMethodData);
    }

    public String[][] getFilterRecords() {
        SQLiteDatabase db = this.getReadableDatabase();
        String[][] records = null;
        Cursor cursor = db.rawQuery("SELECT " + F_KEY_NAME + ", " + F_KEY_FILTER + " FROM " + F_TABLE_NAME + " WHERE " + F_KEY_SHOW_DASH + " = 0", null);
        if (cursor != null) {
            final int len = cursor.getCount();
            records = new String[len][2];
            for (int i = 0; i < len; i++) {
                cursor.moveToPosition(i);
                records[i][0] = cursor.getString(0);
                records[i][1] = decode(cursor.getString(1));
            }
            cursor.close();
        }
        return records;
    }

    public boolean updateMTRecord(MBRecord mbrOld, MBRecord mbrNew) {
        if (deleteMTRecord(mbrOld) > 0) {
            return addRecord(mbrNew, mbrNew.getType());
        } else
            return false;
    }

    private int deleteMTRecord(MBRecord mbr) {
        DateConverter dc = new DateConverter(mbr.getDate());
        Date sDate = dc.getdDate();
        Date eDate = (Date) sDate.clone();
        sDate = intializeSDateForDay(sDate);
        eDate = intializeEDateForDay(eDate);

        SQLiteDatabase db = this.getWritableDatabase();
        int n = db.delete(TABLE_NAME, KEY_DESCRIPTION + " = '" + mbr.getDescription() + "' AND " + KEY_AMOUNT + " = " + mbr.getAmount() + " AND " + KEY_TYPE + " = " + mbr.getType() + " AND " + KEY_DATE + " >= " + sDate.getTime() + " AND " + KEY_DATE + " <= " + eDate.getTime(), null);
        db.close();
        return n;
    }

    public void addPaymentMethod(String name) {
        ContentValues values = new ContentValues();
        values.put(KEY_PAY_METH_NAME, name.toLowerCase());
        SQLiteDatabase db = this.getWritableDatabase();
        db.insert(PAY_METH_TABLE_NAME, null, values);
        db.close();
    }

    public ArrayList<MBRecord> getRePaymentRecords(int type, String refId) {
        ArrayList<MBRecord> mbr = new ArrayList<>();

        if (refId == null)
            return mbr;

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = null;
        String query = "SELECT " + KEY_AMOUNT + "," + KEY_DATE + "," + getSQLQueryForCat()
                + "," + getSQLQueryForPayMeth() + " FROM " + TABLE_NAME + " where "
                + KEY_TYPE + "=" + type + " AND " + KEY_DESCRIPTION + " = '" + refId + "'";
        LoggerCus.d(TAG, query);
        cursor = db.rawQuery(query, null);
        if (cursor != null) {
            final int len = cursor.getCount();
            int amount = cursor.getColumnIndex(KEY_AMOUNT);
            int date = cursor.getColumnIndex(KEY_DATE);
            int cat = cursor.getColumnIndex(KEY_CAT);
            int paym = cursor.getColumnIndex(KEY_PAYMENT_METHOD);

            for (int i = 0; i < len; i++) {
                cursor.moveToPosition(i);

                MBRecord mbrTemp = new MBRecord(refId,
                        cursor.getInt(amount),
                        new Date(cursor.getLong(date)),
                        type,
                        cursor.getString(cat),
                        cursor.getString(paym));

                mbr.add(mbrTemp);
            }
            cursor.close();
        }

        return mbr;
    }

    public int getRePaymentAmount(int type, String refId) {

        if (refId == null) {
            return 0;
        }

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        String query = "SELECT SUM(" + KEY_AMOUNT + ") FROM " + TABLE_NAME + " where "
                + KEY_TYPE + "=" + type + " AND " + KEY_DESCRIPTION + " = '" + refId + "'";
        LoggerCus.d(TAG, query);
        cursor = db.rawQuery(query, null);
        int res = -1;
        if (cursor != null) {
            final int len = cursor.getCount();
            for (int i = 0; i < len; i++) {
                cursor.moveToPosition(i);
                res = cursor.getInt(0);
            }
            cursor.close();
            return res;
        }
        return res;
    }
}
