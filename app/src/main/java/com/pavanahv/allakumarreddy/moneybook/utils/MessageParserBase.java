package com.pavanahv.allakumarreddy.moneybook.utils;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.StyleSpan;

import com.pavanahv.allakumarreddy.moneybook.Activities.LoginActivity;
import com.pavanahv.allakumarreddy.moneybook.R;
import com.pavanahv.allakumarreddy.moneybook.storage.PreferencesCus;
import com.pavanahv.allakumarreddy.moneybook.storage.db.DbHandler;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class MessageParserBase {
    private static final String TAG = "MessageParserBase";
    private final DbHandler db;
    private Context mContext;
    private ArrayList<MBRecord> mbrList;
    private SimpleDateFormat sdf;
    private boolean isMSGParserNoti;

    public MessageParserBase(Context mContext) {
        this.mContext = mContext;
        db = new DbHandler(mContext);
        isMSGParserNoti = new PreferencesCus(mContext).getMessageParserNotificationStatus();
    }

    public void handleActionMsgParse() {
        PreferencesCus pref = new PreferencesCus(mContext);
        Date date = pref.getMsgParsedDate();
        if (isMSGParserNoti)
            mbrList = new ArrayList<>();
        LoggerCus.d(TAG, date.toString());


        ArrayList<HashMap> msgList = getMsgsFromProvider(date);
        ArrayList<HashMap> list = getMsgParseDataFromDb();

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
                if (compareParseData(rawMsg, msg, map, mapDbMsg))
                    break;
            }
        }

        if (isMSGParserNoti && mbrList.size() > 0) {
            sdf = new SimpleDateFormat("dd / MM / yyyy");
            for (MBRecord mbr : mbrList) {
                if (mbr != null)
                    showNotifications(mbr);
            }
        }
    }

    private void showNotifications(MBRecord mbRecord) {
        int icons[] = new int[]{R.drawable.spent, R.drawable.earn, R.drawable.due, R.drawable.ic_loan};
        int reqCode = (int) System.currentTimeMillis();
        Context context = mContext;
        Intent intent = new Intent(context, LoginActivity.class);
        intent.putExtra(GlobalConstants.MSG_PARSER_NOTI, true);
        intent.putExtra(GlobalConstants.MBRECORD, mbRecord);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, reqCode, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        String text = "";
        String amount = "Amount : " + mbRecord.getAmount();
        String cat = "Category : " + mbRecord.getCategory();
        String paym = "PaymentMethod : " + mbRecord.getPaymentMethod();
        String date = "Date : " + sdf.format(mbRecord.getDate());
        text += amount + "\n" + cat + "\n" + paym + "\n" + date;

        Spannable spanString = new SpannableString(text);
        int txtLen = amount.length();
        spanString.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), 0, "amount".length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        spanString.setSpan(new StyleSpan(android.graphics.Typeface.ITALIC), "amount".length() + 3, txtLen, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        txtLen += 1;
        spanString.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), txtLen, txtLen + "category".length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        spanString.setSpan(new StyleSpan(android.graphics.Typeface.ITALIC), txtLen + "category".length() + 3, txtLen + cat.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        txtLen += cat.length() + 1;
        spanString.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), txtLen, txtLen + "paymentMethod".length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        spanString.setSpan(new StyleSpan(android.graphics.Typeface.ITALIC), txtLen + "paymentMethod".length() + 3, txtLen + paym.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        txtLen += paym.length() + 1;
        spanString.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), txtLen, txtLen + "date".length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        spanString.setSpan(new StyleSpan(android.graphics.Typeface.ITALIC), txtLen + "date".length() + 3, txtLen + date.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        NotificationCompat.BigTextStyle bigText = new NotificationCompat.BigTextStyle();
        bigText.bigText(spanString);
        bigText.setSummaryText("Message Parser");

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, GlobalConstants.NOTIFICATION_CHANNLE_MSG_PARSER_ID)
                .setSmallIcon(R.drawable.message_parser)
                .setContentTitle(mbRecord.getDescription())
                .setContentText(text)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true)
                .setLargeIcon(BitmapFactory.decodeResource(mContext.getResources(), icons[mbRecord.getType()]))
                .setStyle(bigText)
                .setContentIntent(pendingIntent);


        NotificationManager notificationManager = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            notificationManager = context.getSystemService(NotificationManager.class);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                int importance = NotificationManager.IMPORTANCE_DEFAULT;
                NotificationChannel channel = new NotificationChannel(GlobalConstants.NOTIFICATION_CHANNLE_MSG_PARSER_ID, GlobalConstants.NOTIFICATION_CHANNLE_MSG_PARSER_NAME, importance);
                channel.setDescription(GlobalConstants.NOTIFICATION_CHANNLE_MESSAGE_PARSER_DESCRIPTION);
                notificationManager.createNotificationChannel(channel);
            }
            notificationManager.notify(reqCode + 1, builder.build());
        }
    }

    private boolean compareParseData(String orgStr, String savStr, HashMap<String,
            String> rawMsgMap, HashMap<String, String> dbMsgMap) {
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
            if (sind != orgStr.length())
                flist.add(orgStr.substring(sind));
            parseUpdateAllFields(flist, rawMsgMap, dbMsgMap);
            result = true;
        } else {
            // selected message is not valid
            result = false;
        }
        return result;
    }

    private void parseUpdateAllFields(ArrayList<String> flist, HashMap<String,
            String> rawMsgMap, HashMap<String, String> dbMsgMap) {
        String desc = dbMsgMap.get(DbHandler.MSG_KEY_DESCRIPTION);
        String bal = dbMsgMap.get(DbHandler.MSG_KEY_LEFT_BAL);

        if ((desc.compareToIgnoreCase("") == 0) && (bal.compareToIgnoreCase("") == 0))
            return;
        else if ((desc.compareToIgnoreCase("") != 0) && (bal.compareToIgnoreCase("") != 0)) {
            String des = parseUpdateField(flist, desc);
            int amount = Utils.castFloat2IntRemovingCommas(parseUpdateField(flist, dbMsgMap.get(DbHandler.MSG_KEY_AMOUNT)));
            int balLeft = Utils.castFloat2IntRemovingCommas(parseUpdateField(flist, bal));
            LoggerCus.d(TAG, des + " -> " + amount + " -> " + balLeft + " -> " + rawMsgMap.get("date") + " -> " + new Date(Long.parseLong(rawMsgMap.get("date"))).toString());

            MBRecord mbRecord = new MBRecord(des, amount, new Date(Long.parseLong(rawMsgMap.get("date"))), dbMsgMap.get(DbHandler.MSG_KEY_CAT), dbMsgMap.get(DbHandler.MSG_KEY_PAYMENT_METHOD));
            MBRecord res = db.addRecordWithCatAsID(mbRecord, Integer.parseInt(dbMsgMap.get(DbHandler.MSG_KEY_TYPE)));
            if (res != null) {
                db.updateBalLeft(Long.parseLong(dbMsgMap.get(DbHandler.MSG_KEY_PAYMENT_METHOD)), balLeft);
                if (isMSGParserNoti)
                    mbrList.add(res);
            }

        } else if ((desc.compareToIgnoreCase("") != 0) && (bal.compareToIgnoreCase("") == 0)) {
            String des = parseUpdateField(flist, desc);
            int amount = Utils.castFloat2IntRemovingCommas(parseUpdateField(flist, dbMsgMap.get(DbHandler.MSG_KEY_AMOUNT)));
            LoggerCus.d(TAG, des + " -> " + amount);

            MBRecord mbRecord = new MBRecord(des, amount, new Date(Long.parseLong(rawMsgMap.get("date"))), dbMsgMap.get(DbHandler.MSG_KEY_CAT), dbMsgMap.get(DbHandler.MSG_KEY_PAYMENT_METHOD));
            MBRecord res = db.addRecordWithCatAsID(mbRecord, Integer.parseInt(dbMsgMap.get(DbHandler.MSG_KEY_TYPE)));
            if (res != null)
                if (isMSGParserNoti)
                    mbrList.add(res);
        } else if ((desc.compareToIgnoreCase("") == 0) && (bal.compareToIgnoreCase("") != 0)) {
            int balLeft = Utils.castFloat2IntRemovingCommas(parseUpdateField(flist, bal));
            LoggerCus.d(TAG, "" + balLeft);
            boolean res = db.updateBalLeft(Long.parseLong(dbMsgMap.get(DbHandler.MSG_KEY_PAYMENT_METHOD)), balLeft);
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
        Cursor cursor = mContext.getContentResolver().query(SMS_INBOX, null, filter, null, null);
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

    private ArrayList<HashMap> getMsgParseDataFromDb() {
        return db.getMsgRecordsAsMap();
    }

}
