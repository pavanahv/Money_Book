package com.example.allakumarreddy.moneybook.test;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.webkit.WebView;

import com.example.allakumarreddy.moneybook.R;
import com.example.allakumarreddy.moneybook.db.DbHandler;
import com.example.allakumarreddy.moneybook.utils.DateConverter;
import com.example.allakumarreddy.moneybook.utils.LoggerCus;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import static com.example.allakumarreddy.moneybook.db.DbHandler.KEY_AMOUNT;
import static com.example.allakumarreddy.moneybook.db.DbHandler.KEY_CAT;
import static com.example.allakumarreddy.moneybook.db.DbHandler.KEY_CAT_BAL;
import static com.example.allakumarreddy.moneybook.db.DbHandler.KEY_CAT_ID;
import static com.example.allakumarreddy.moneybook.db.DbHandler.KEY_DATE;
import static com.example.allakumarreddy.moneybook.db.DbHandler.KEY_DATE_MONTH;
import static com.example.allakumarreddy.moneybook.db.DbHandler.KEY_DATE_YEAR;
import static com.example.allakumarreddy.moneybook.db.DbHandler.KEY_DESCRIPTION;
import static com.example.allakumarreddy.moneybook.db.DbHandler.KEY_NAME;
import static com.example.allakumarreddy.moneybook.db.DbHandler.KEY_TYPE;

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
            JSONArray jsonArray = obj.getJSONArray("records");
            JSONArray jsonArrayc = obj.getJSONArray("cats");

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
                sb.append("<td>" + Integer.parseInt(jsonObj.getString("Type")) + "</td>");
                sb.append("<td>" + jsonObj.getString("Description") + "</td>");
                sb.append("<td>" + Integer.parseInt(jsonObj.getString("Amount")) + "</td>");
                DateConverter dc = new DateConverter(jsonObj.getString("Date"));
                sb.append("<td>" + dc.getdDate().getTime() + "</td>");
                sb.append("<td>" + dc.getmDate().getTime() + "</td>");
                sb.append("<td>" + dc.getyDate().getTime() + "</td>");
                sb.append("<td>" + jsonObj.getLong("Category") + "</td>");
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
                sb.append("<td>" + jsonObj.getString("Name") + "</td>");
                sb.append("<td>" + jsonObj.getLong("ID") + "</td>");
                sb.append("<td>" + jsonObj.getInt("BalLeft") + "</td>");
                sb.append("</tr>");
            }
            sb.append("</table>");
        } catch (JSONException e) {
            LoggerCus.d("DataBaseActivity", e.getMessage());
        }
        sb.append("</body></html>");
        return sb.toString();
    }
}
