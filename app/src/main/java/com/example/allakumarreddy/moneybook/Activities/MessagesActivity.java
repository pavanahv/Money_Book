package com.example.allakumarreddy.moneybook.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.allakumarreddy.moneybook.R;
import com.example.allakumarreddy.moneybook.db.DbHandler;

import java.util.ArrayList;

public class MessagesActivity extends AppCompatActivity {

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
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(view -> startActivity(new Intent(MessagesActivity.this, MessageParseActivity.class)));
        init();
    }

    private void init() {
        db = new DbHandler(this);
        mListView = (ListView) findViewById(R.id.msg_list_view);
        mArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);
        mListView.setAdapter(mArrayAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(MessagesActivity.this, MessageDetailActivity.class);
                intent.putExtra("name", mArrayAdapter.getItem(position));
                startActivity(intent);
            }
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

}
