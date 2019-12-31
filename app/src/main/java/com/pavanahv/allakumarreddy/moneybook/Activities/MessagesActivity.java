package com.pavanahv.allakumarreddy.moneybook.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

import com.pavanahv.allakumarreddy.moneybook.Adapter.MessageParserAdapter;
import com.pavanahv.allakumarreddy.moneybook.R;
import com.pavanahv.allakumarreddy.moneybook.storage.db.DbHandler;
import com.pavanahv.allakumarreddy.moneybook.utils.ThemeUtils;

import java.util.ArrayList;
import java.util.HashMap;

public class MessagesActivity extends BaseActivity {

    private DbHandler db;
    private ListView mListView;
    private ArrayList<HashMap<String, String>> list;
    private MessageParserAdapter mArrayAdapter;
    private View noData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(ThemeUtils.getTheme(getClass().getSimpleName(), this));
        setContentView(R.layout.activity_messages);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Message Parser");
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(v -> {
            startActivity(new Intent(MessagesActivity.this, MessageParseActivity.class));
            overridePendingTransition(R.anim.move_right_in_activity, R.anim.move_left_out_activity);
        });
        init();
    }

    private void init() {
        db = new DbHandler(this);
        mListView = (ListView) findViewById(R.id.msg_list_view);
        noData = findViewById(R.id.no_data);
        noData.setVisibility(View.GONE);
        list = new ArrayList<>();
        mArrayAdapter = new MessageParserAdapter(list, this, name -> {
            Intent intent = new Intent(MessagesActivity.this, MessageDetailActivity.class);
            intent.putExtra("name", name);
            startActivity(intent);
            overridePendingTransition(R.anim.move_right_in_activity, R.anim.move_left_out_activity);
        });
        mListView.setAdapter(mArrayAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        new Thread(() -> {
            list = db.getMsgRecordsAsList();
            runOnUiThread(() -> {
                mArrayAdapter.clear();
                mArrayAdapter.addAll(list);
                mArrayAdapter.notifyDataSetChanged();
                if (mArrayAdapter.getCount() <= 0) {
                    noData.setVisibility(View.VISIBLE);
                    mListView.setVisibility(View.GONE);
                } else {
                    noData.setVisibility(View.GONE);
                    mListView.setVisibility(View.VISIBLE);
                }
            });
        }).start();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                overridePendingTransition(R.anim.move_left_in_activity, R.anim.move_right_out_activity);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
