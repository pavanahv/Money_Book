package com.example.allakumarreddy.moneybook.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.allakumarreddy.moneybook.R;
import com.example.allakumarreddy.moneybook.storage.db.DbHandler;

import java.util.ArrayList;

public class MessagesActivity extends BaseActivity {

    private DbHandler db;
    private ListView mListView;
    private ArrayAdapter<String> mArrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        mArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);
        mListView.setAdapter(mArrayAdapter);
        mListView.setOnItemClickListener((parent, view, position, id) -> {
            Intent intent = new Intent(MessagesActivity.this, MessageDetailActivity.class);
            intent.putExtra("name", mArrayAdapter.getItem(position));
            startActivity(intent);
            overridePendingTransition(R.anim.move_right_in_activity, R.anim.move_left_out_activity);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        new Thread(() -> {
            ArrayList<String> list = db.getMsgRecordsAsList();
            runOnUiThread(() -> {
                mArrayAdapter.clear();
                mArrayAdapter.addAll(list);
                mArrayAdapter.notifyDataSetChanged();
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
