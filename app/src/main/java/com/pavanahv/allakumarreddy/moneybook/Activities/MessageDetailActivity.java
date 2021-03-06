package com.pavanahv.allakumarreddy.moneybook.Activities;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.pavanahv.allakumarreddy.moneybook.R;
import com.pavanahv.allakumarreddy.moneybook.storage.db.DbHandler;
import com.pavanahv.allakumarreddy.moneybook.utils.ThemeUtils;

import java.util.ArrayList;

public class MessageDetailActivity extends BaseActivity {

    private String msgName;
    private DbHandler db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(ThemeUtils.getTheme(getClass().getSimpleName(), this));
        setContentView(R.layout.activity_message_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Message Detail");
        init();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.message_detail_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_detail_delete:
                if (db.deleteMessage(msgName)) {
                    Toast.makeText(this, "Successfully Deleted !", Toast.LENGTH_LONG).show();
                    finish();
                } else {
                    Toast.makeText(this, "Error !\nSomething Went Wrong", Toast.LENGTH_LONG).show();
                }
                break;
            case android.R.id.home:
                finish();
                overridePendingTransition(R.anim.move_left_in_activity, R.anim.move_right_out_activity);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void init() {
        msgName = getIntent().getStringExtra("name");
        db = new DbHandler(this);
        ArrayList<String> list = db.getMsgDetails(msgName);
        ((TextView) findViewById(R.id.nameitem)).setText(msgName);
        ((TextView) findViewById(R.id.msgitem)).setText(list.get(5));
        ((TextView) findViewById(R.id.descitem)).setText(list.get(0));
        ((TextView) findViewById(R.id.amountitem)).setText(list.get(1));
        ((TextView) findViewById(R.id.leftOutBalance)).setText(list.get(4));
        ((TextView) findViewById(R.id.catitem)).setText(list.get(3));
        ((TextView) findViewById(R.id.payitem)).setText(list.get(6));

        Spinner type = (Spinner) findViewById(R.id.typeitem);
        ArrayAdapter aa = new ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, new String[]{"Spent", "Earn", "Due", "Loan"});
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        type.setAdapter(aa);
        type.setSelection(Integer.parseInt(list.get(2)));
        type.setEnabled(false);

    }

}
