package com.example.allakumarreddy.moneybook;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;


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
    private static final String TABLE_NAME = "MoneyBook";

    // Contacts Table Columns names
    private static final String KEY_TYPE = "type";
    private static final String KEY_DESCRIPTION = "description";
    private static final String KEY_AMOUNT = "amount";
    private static final String KEY_DATE = "date";

    private SimpleDateFormat format;
    private final static String TAG = "DbHandler";

    public DbHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        format = new SimpleDateFormat("yyyy/MM/dd");
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_CONTACTS_TABLE = "CREATE TABLE " + TABLE_NAME + "("
                + KEY_TYPE + " INTEGER," + KEY_DESCRIPTION + " TEXT,"
                + KEY_AMOUNT + " INTEGER," + KEY_DATE + " TEXT)";
        db.execSQL(CREATE_CONTACTS_TABLE);
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);

        // Create tables again
        onCreate(db);
    }

    public void addRecord(MBRecord mbr, int type) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_TYPE, type);
        values.put(KEY_DESCRIPTION, mbr.getDescription());
        values.put(KEY_AMOUNT, mbr.getAmount());
        values.put(KEY_DATE, format.format(mbr.getDate()));

        // Inserting Row
        db.insert(TABLE_NAME, null, values);
        db.close(); // Closing database connection
    }

    public ArrayList<MBRecord> getRecords(String date, int type) {
        LoggerCus.d(TAG,date);
        ArrayList<MBRecord> mbr = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT "+KEY_DESCRIPTION+","+KEY_AMOUNT+","+KEY_DATE+" FROM "+TABLE_NAME+" where "+KEY_DATE+" like '"+date+"%' AND "+KEY_TYPE+"="+type,null);
        if (cursor != null) {
            final int len=cursor.getCount();
            for(int i=0;i<len;i++) {
                cursor.moveToPosition(i);
                try {
                    mbr.add(new MBRecord(cursor.getString(0), Integer.parseInt(cursor.getString(1)), format.parse(cursor.getString(2))));
                } catch (ParseException e) {
                    LoggerCus.d(TAG, e.getMessage());
                }
            }
            cursor.close();
        }
        // return contact
        return mbr;
    }
}
