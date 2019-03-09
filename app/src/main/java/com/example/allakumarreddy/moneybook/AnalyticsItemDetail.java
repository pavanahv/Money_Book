package com.example.allakumarreddy.moneybook;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;

public class AnalyticsItemDetail extends AppCompatActivity {

    private MBRecord mbrOld;
    private SimpleDateFormat format;
    private EditText des;
    private EditText amount;
    private EditText date;
    private Spinner type;
    private DbHandler db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_analytics_item_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        format = new SimpleDateFormat("yyyy/MM/dd");
        init();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.analytics_detail_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch (id) {
            case R.id.action_analytics_detail_delete:
                int result = db.deleteRecord(mbrOld);
                if (result > 0)
                    Toast.makeText(AnalyticsItemDetail.this, "Deleted Successfully " + result + " !", Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(AnalyticsItemDetail.this, "Deletion Error !", Toast.LENGTH_SHORT).show();
                break;

            case android.R.id.home:
                finish();
                break;

            case R.id.action_analytics_detail_save:
                save();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void init() {
        Intent intent = getIntent();
        db = new DbHandler(this);
        try {
            mbrOld = new MBRecord(intent.getStringExtra("desc"), intent.getIntExtra("amount", -1), format.parse(intent.getStringExtra("date")), intent.getIntExtra("type", -1));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        des = (EditText) findViewById(R.id.descitem);
        amount = (EditText) findViewById(R.id.amountitem);
        date = (EditText) findViewById(R.id.dateitem);
        type = (Spinner) findViewById(R.id.typeitem);
        ArrayAdapter aa = new ArrayAdapter(this, android.R.layout.simple_spinner_item, new String[]{"Spent", "Earn", "Due", "Loan"});
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        type.setAdapter(aa);
        type.setSelection(mbrOld.getType());
        des.setText(mbrOld.getDescription());
        amount.setText(mbrOld.getAmount() + "");
        date.setText(format.format(mbrOld.getDate()));
    }


    public void save(View view) {
        save();
    }

    private void save()
    {
        try {
            MBRecord mbrNew = new MBRecord(des.getText().toString(), Integer.parseInt(amount.getText().toString()), format.parse(date.getText().toString()), type.getSelectedItemPosition());
            LoggerCus.d("analyticsitemdetail", mbrNew.getDescription());
            LoggerCus.d("analyticsitemdetail", mbrNew.getAmount() + "");
            LoggerCus.d("analyticsitemdetail", mbrNew.getDate().toString());
            LoggerCus.d("analyticsitemdetail", mbrNew.getType() + "");
            boolean result = db.updateRecord(mbrOld, mbrNew);
            if (result)
                Toast.makeText(this, "Updated Successfully !", Toast.LENGTH_SHORT).show();
            else
                Toast.makeText(this, "Updation Error !", Toast.LENGTH_SHORT).show();
            finish();
        } catch (ParseException e) {
            LoggerCus.d("analyticsdetailactivity", e.getMessage());
        }
    }
}
