package com.example.allakumarreddy.moneybook.test;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.webkit.WebView;

import com.example.allakumarreddy.moneybook.R;
import com.example.allakumarreddy.moneybook.storage.db.DbHandler;
import com.example.allakumarreddy.moneybook.utils.DateConverter;
import com.example.allakumarreddy.moneybook.utils.LoggerCus;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;

import static com.example.allakumarreddy.moneybook.storage.db.DbHandler.CAT_TABLE_NAME;
import static com.example.allakumarreddy.moneybook.storage.db.DbHandler.KEY_AMOUNT;
import static com.example.allakumarreddy.moneybook.storage.db.DbHandler.KEY_CAT;
import static com.example.allakumarreddy.moneybook.storage.db.DbHandler.KEY_CAT_BAL;
import static com.example.allakumarreddy.moneybook.storage.db.DbHandler.KEY_CAT_ID;
import static com.example.allakumarreddy.moneybook.storage.db.DbHandler.KEY_DATE;
import static com.example.allakumarreddy.moneybook.storage.db.DbHandler.KEY_DATE_MONTH;
import static com.example.allakumarreddy.moneybook.storage.db.DbHandler.KEY_DATE_YEAR;
import static com.example.allakumarreddy.moneybook.storage.db.DbHandler.KEY_DESCRIPTION;
import static com.example.allakumarreddy.moneybook.storage.db.DbHandler.KEY_NAME;
import static com.example.allakumarreddy.moneybook.storage.db.DbHandler.KEY_TYPE;
import static com.example.allakumarreddy.moneybook.storage.db.DbHandler.TABLE_NAME;

public class DataBaseActivity extends AppCompatActivity {

    private WebView mWebView;
    private DbHandler db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_base);

        mWebView = (WebView) findViewById(R.id.webview);
        mWebView.getSettings().setBuiltInZoomControls(true);
        init();
    }

    private void init() {
        db = new DbHandler(this);
        String s = getDbData();
        mWebView.loadData(s, "text/html", "utf-8");
    }

    private String getDbData() {
        StringBuilder sb = new StringBuilder();
        sb.append("<html><body>");
        String s = db.getRecords();
        try {
            JSONObject obj = new JSONObject(s);
            JSONArray jsonArray = obj.getJSONArray(TABLE_NAME);
            JSONArray jsonArrayc = obj.getJSONArray(CAT_TABLE_NAME);

            final int len = jsonArray.length();
            sb.append("<table style=\"border:1px solid black;border-collapse:collapse;\">");
            sb.append("<tr style=\"border:1px solid red;\">");
            sb.append("<td>" + KEY_TYPE + "</td>");
            sb.append("<td>" + KEY_DESCRIPTION + "</td>");
            sb.append("<td>" + KEY_AMOUNT + "</td>");
            sb.append("<td>" + KEY_DATE + "</td>");
            sb.append("<td>" + KEY_DATE_MONTH + "</td>");
            sb.append("<td>" + KEY_DATE_YEAR + "</td>");
            sb.append("<td>" + KEY_CAT + "</td>");
            sb.append("</tr>");
            for (int i = 0; i < len; i++) {
                sb.append("<tr>");
                JSONObject jsonObj = jsonArray.getJSONObject(i);
                sb.append("<td>" + Integer.parseInt(jsonObj.getString(KEY_TYPE)) + "</td>");
                sb.append("<td>" + jsonObj.getString(KEY_DESCRIPTION) + "</td>");
                sb.append("<td>" + Integer.parseInt(jsonObj.getString(KEY_AMOUNT)) + "</td>");
                DateConverter dc = new DateConverter(jsonObj.getString(KEY_DATE));
                dc.initialize();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd-H:m:s:S");
                sb.append("<td>" + sdf.format(dc.getdDate().getTime()) + "</br>" + jsonObj.getLong("test_day") + "</td>");
                sb.append("<td>" + sdf.format(dc.getmDate().getTime()) + "</br>" + jsonObj.getLong("test_month") + "</td>");
                sb.append("<td>" + sdf.format(dc.getyDate().getTime()) + "</br>" + jsonObj.getLong("test_year") + "</td>");
                sb.append("<td>" + jsonObj.getLong(KEY_CAT) + "</td>");
                sb.append("</tr>");
            }
            sb.append("</table>");

            sb.append("<table style=\"border:1px solid black;border-collapse:collapse;\">");
            sb.append("<tr style=\"border:1px solid red;\">");
            sb.append("<td>" + KEY_NAME + "</td>");
            sb.append("<td>" + KEY_CAT_ID + "</td>");
            sb.append("<td>" + KEY_CAT_BAL + "</td>");
            sb.append("</tr>");
            final int len1 = jsonArrayc.length();
            for (int i = 0; i < len1; i++) {
                sb.append("<tr>");
                JSONObject jsonObj = jsonArrayc.getJSONObject(i);
                sb.append("<td>" + jsonObj.getString(KEY_NAME) + "</td>");
                sb.append("<td>" + jsonObj.getLong(KEY_CAT_ID) + "</td>");
                sb.append("<td>" + jsonObj.getInt(KEY_CAT_BAL) + "</td>");
                sb.append("</tr>");
            }
            sb.append("</table>");
        } catch (JSONException e) {
            LoggerCus.d("DataBaseActivity", e.getMessage());
        }
        sb.append("</body></html>");
        LoggerCus.d("DataBaseActivity", sb.toString());
        return sb.toString();
    }
}
