package com.example.allakumarreddy.moneybook.MessageParser;

import android.app.IntentService;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;

import com.example.allakumarreddy.moneybook.db.DbHandler;
import com.example.allakumarreddy.moneybook.storage.PreferencesCus;
import com.example.allakumarreddy.moneybook.utils.LoggerCus;
import com.example.allakumarreddy.moneybook.utils.MBRecord;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import static com.example.allakumarreddy.moneybook.utils.GlobalConstants.ACTION_MSG_PARSE_BY_DATE;

public class MessagesParserService extends IntentService {

    private static final String TAG = "MessagesParserService";

    public MessagesParserService() {
        super("MessagesParserService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_MSG_PARSE_BY_DATE.equals(action)) {
                handleActionMsgParse();
            }
        }
    }

    private void handleActionMsgParse() {
        DbHandler db = new DbHandler(this);
        PreferencesCus pref = new PreferencesCus(this);
        Date date = pref.getMsgParsedDate();
        LoggerCus.d(TAG, date.toString());


        ArrayList<HashMap> msgList = getMsgsFromProvider(date);
        ArrayList<HashMap> list = getMsgParseDataFromDb(db);

        for (HashMap<String, String> map : list) {
            LoggerCus.d(TAG, map.values().toString());
            LoggerCus.d(TAG, map.keySet().toString());
        }

        for (HashMap<String, String> map : msgList) {
            LoggerCus.d(TAG, map.values().toString());
            LoggerCus.d(TAG, map.keySet().toString());
        }

        for (HashMap<String, String> map : msgList) {
            String rawMsg = map.get("body");
            for (HashMap<String, String> mapDbMsg : list) {
                String msg = mapDbMsg.get(DbHandler.MSG_KEY_MSG);
                if (compareParseData(rawMsg, msg, map, mapDbMsg, db))
                    break;
            }
        }
    }

    private boolean compareParseData(String orgStr, String savStr, HashMap<String, String> rawMsgMap, HashMap<String, String> dbMsgMap, DbHandler db) {
        boolean result = false;
        ArrayList<String> constList = getChunksList(savStr);

        // check its a valid message or not i.e to compare both messages
        int count = 0;
        for (String s : constList) {
            if (orgStr.indexOf(s) == -1)
                break;
            count++;
        }
        if (count == constList.size()) {
            // yes selected message is valid

            // extract required details into list
            ArrayList<String> flist = new ArrayList<>();
            final int len = constList.size();
            int sind = orgStr.indexOf(constList.get(0));
            if (sind != 0) {
                flist.add(orgStr.substring(0, sind));
            }
            sind += constList.get(0).length();
            for (int j = 1; j < len; j++) {
                String s = constList.get(j);
                int ind = orgStr.indexOf(s, sind);
                flist.add(orgStr.substring(sind, ind));
                sind = ind + s.length();
            }
            parseUpdateAllFields(flist, rawMsgMap, dbMsgMap, db);
            result = true;
        } else {
            // selected message is not valid
            result = false;
        }
        return result;
    }

    private void parseUpdateAllFields(ArrayList<String> flist, HashMap<String, String> rawMsgMap, HashMap<String, String> dbMsgMap, DbHandler db) {
        String desc = dbMsgMap.get(DbHandler.MSG_KEY_DESCRIPTION);
        String bal = dbMsgMap.get(DbHandler.MSG_KEY_LEFT_BAL);

        if ((desc.compareToIgnoreCase("") == 0) && (bal.compareToIgnoreCase("") == 0))
            return;
        else if ((desc.compareToIgnoreCase("") != 0) && (bal.compareToIgnoreCase("") != 0)) {
            String des = parseUpdateField(flist, desc);
            int amount = (int) Float.parseFloat(parseUpdateField(flist, dbMsgMap.get(DbHandler.MSG_KEY_AMOUNT)));
            int balLeft = (int) Float.parseFloat(parseUpdateField(flist, bal));
            LoggerCus.d(TAG, des + " -> " + amount + " -> " + balLeft + " -> " + rawMsgMap.get("date") + " -> " + new Date(Long.parseLong(rawMsgMap.get("date"))).toString());

            MBRecord mbRecord = new MBRecord(des, amount, new Date(Long.parseLong(rawMsgMap.get("date"))), dbMsgMap.get(DbHandler.MSG_KEY_CAT));
            boolean res = db.addRecordWithCatAsID(mbRecord, Integer.parseInt(dbMsgMap.get(DbHandler.MSG_KEY_TYPE)));
            if (res) {
                res = db.updateBalLeft(Long.parseLong(dbMsgMap.get(DbHandler.MSG_KEY_CAT)), balLeft);
            }

        } else if ((desc.compareToIgnoreCase("") != 0) && (bal.compareToIgnoreCase("") == 0)) {
            String des = parseUpdateField(flist, desc);
            int amount = Integer.parseInt(parseUpdateField(flist, dbMsgMap.get(DbHandler.MSG_KEY_AMOUNT)));
            LoggerCus.d(TAG, des + " -> " + amount);
        } else if ((desc.compareToIgnoreCase("") == 0) && (bal.compareToIgnoreCase("") != 0)) {
            int balLeft = Integer.parseInt(parseUpdateField(flist, bal));
            LoggerCus.d(TAG, "" + balLeft);
        }
    }

    private String parseUpdateField(ArrayList<String> flist, String str) {
        StringBuilder strBuilder = new StringBuilder();
        final int len = str.length();
        int pind = 0;
        for (int i = 0; i < len; ) {
            int sind = str.indexOf("{{", i);
            int eind = -1;
            if (sind != -1) {
                eind = str.indexOf("}}", sind);
                if (eind != -1) {
                    int arg = Integer.parseInt(str.substring(sind + 2, eind));
                    String prefixStr = str.substring(pind, sind);
                    strBuilder.append(prefixStr + flist.get(arg - 1));
                }
            } else
                break;
            i = eind + 2;
            pind = i;
        }
        if (pind < len)
            strBuilder.append(str.substring(pind, len));
        return strBuilder.toString();
    }

    private ArrayList<String> getChunksList(String savStr) {
        ArrayList<String> constList = new ArrayList<>();
        int i;
        for (i = 0; i < savStr.length(); ) {
            int sind = savStr.indexOf("{{", i);
            int eind = -1;
            if (sind != -1) {
                eind = savStr.indexOf("}}", sind);
                if (eind != -1) {
                    constList.add(savStr.substring(i, sind));
                }
            } else
                break;
            i = eind + 2;
        }
        if (i != savStr.length())
            constList.add(savStr.substring(i));
        return constList;
    }

    private ArrayList getMsgsFromProvider(Date date) {
        ArrayList<HashMap> msgList = new ArrayList<>();
        String filter = "date>=" + date.getTime();
        final Uri SMS_INBOX = Uri.parse("content://sms/inbox");
        Cursor cursor = getContentResolver().query(SMS_INBOX, null, filter, null, null);
        int totalSmsCount = cursor.getCount();
        LoggerCus.d(TAG, "Total sms count : " + totalSmsCount);
        while (cursor.moveToNext()) {
            HashMap<String, String> map = new HashMap<>();
            map.put("body", cursor.getString(cursor.getColumnIndex("body")));
            map.put("date", cursor.getString(cursor.getColumnIndex("date")));
            msgList.add(map);
        }
        cursor.close();
        return msgList;
    }

    private ArrayList<HashMap> getMsgParseDataFromDb(DbHandler db) {
        return db.getMsgRecordsAsMap();
    }
}
