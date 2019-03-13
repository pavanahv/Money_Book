package com.example.allakumarreddy.moneybook.utils;

import com.example.allakumarreddy.moneybook.storage.FileStore;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Date;

/**
 * Created by alla.kumarreddy on 7/20/2017.
 */

public class MBTransaction {

    final static String TAG = "MBTransaction";
    private int type;
    private FileStore fs;
    private String file = "";
    private JSONObject jsonObj = null;
    private MBRecord[] mbr = null;
    private JSONArray array;

    public MBTransaction(int type) {
        this.type = type;
        fs = new FileStore(type);
        load();
    }

    private void load() {
        this.file = fs.readFile();
        try {
            jsonObj = new JSONObject(this.file);

            // Getting JSON Array node
            array = jsonObj.getJSONArray(GlobalConstants.type[this.type]);

            final int len = array.length();
            mbr = new MBRecord[len];

            // looping through All array
            for (int i = 1; i < len; i++) {
                JSONObject c = array.getJSONObject(i);

                String des = c.getString(GlobalConstants.fields[0]);
                String amount = c.getString(GlobalConstants.fields[1]);
                String date = c.getString(GlobalConstants.fields[2]);
                LoggerCus.d(TAG, des + " : " + amount + " : " + date);
                mbr[i - 1] = new MBRecord(des, Integer.parseInt(amount), new Date(date),"");
            }
        } catch (JSONException e) {
            LoggerCus.d(TAG, e.getMessage());
        }
    }

    public void addRecord(MBRecord record) {
        JSONObject obj = new JSONObject();
        try {
            obj.put(GlobalConstants.fields[0], record.getDescription());
            obj.put(GlobalConstants.fields[1], record.getAmount() + "");
            obj.put(GlobalConstants.fields[2], record.getDate().toString());
        } catch (JSONException e) {
            LoggerCus.d(TAG, e.getMessage());
        }
        array.put(obj);
    }

    public MBRecord[] getRecords() {
        return this.mbr;
    }

    public void store() {
        final String s = jsonObj.toString();
        LoggerCus.d(TAG, s);
        try {
            fs.writeFile(s);
        } catch (IOException e) {
            LoggerCus.d(TAG, e.getMessage());
        }
    }

    public void createFile() {
        JSONObject finalobject = new JSONObject();
        try {
            JSONObject obj = new JSONObject();
            try {
                obj.put(GlobalConstants.fields[0], "dummy");
                obj.put(GlobalConstants.fields[1], 0);
                obj.put(GlobalConstants.fields[2], new Date().toString());
            } catch (JSONException e) {
                LoggerCus.d(TAG, e.getMessage());
            }
            JSONArray arraY = new JSONArray();
            arraY.put(obj);
            finalobject.put(GlobalConstants.type[this.type], arraY);
        } catch (JSONException e) {
            LoggerCus.d(TAG, e.getMessage());
        }
        LoggerCus.d(TAG, finalobject.toString());
        try {
            fs.writeFile(finalobject.toString());
        } catch (IOException e) {
            LoggerCus.d(TAG, e.getMessage());
        }
    }
}
