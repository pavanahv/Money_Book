package com.example.allakumarreddy.moneybook.Activities;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.example.allakumarreddy.moneybook.MessageParser.ChunksFragment;
import com.example.allakumarreddy.moneybook.MessageParser.ChunksFragmentInteractionListener;
import com.example.allakumarreddy.moneybook.MessageParser.ItemDetailFragment;
import com.example.allakumarreddy.moneybook.MessageParser.MessageListFragment;
import com.example.allakumarreddy.moneybook.R;
import com.example.allakumarreddy.moneybook.db.DbHandler;

public class MessageParseActivity extends AppCompatActivity implements ChunksFragmentInteractionListener {

    private String mChunkData;
    private String des;
    private String amount;
    private int type;
    private String cate;
    private String balLeft;
    private String selectedMsg;
    private String msgStr;
    private String nameStr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_parse);
        getSupportActionBar().setTitle("Message Parser");

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.fragment_container, new ChunksFragment());
        transaction.commit();
    }

    @Override
    public void onParse(String str, String msgStr, String nameStr) {
        ItemDetailFragment itemDetailFragment = new ItemDetailFragment();
        this.mChunkData = str;
        this.msgStr = msgStr;
        this.nameStr = nameStr;

        itemDetailFragment.setChunkText(this.mChunkData);
        itemDetailFragment.setTypeActivate(0);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, itemDetailFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    public void saveFields(String desStr, String amountStr, int typeInt, String cateStr, String balLeftStr) {
        this.des = desStr;
        this.amount = amountStr;
        this.type = typeInt;
        this.cate = cateStr;
        this.balLeft = balLeftStr;

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, new MessageListFragment());
        transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    public void seelctedMsg(String s) {
        this.selectedMsg = s;

        //next fragment
        ItemDetailFragment itemDetailFragment = new ItemDetailFragment();

        itemDetailFragment.setTypeActivate(1);
        itemDetailFragment.setAmountStr(this.amount);
        itemDetailFragment.setBalLeftStr(this.balLeft);
        itemDetailFragment.setCateStr(this.cate);
        itemDetailFragment.setDesStr(this.des);
        itemDetailFragment.setTypeStr(this.type);
        itemDetailFragment.setMsgStr(this.msgStr);
        itemDetailFragment.setSelectedMsgStr(this.selectedMsg);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, itemDetailFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    public void saveEverything() {
        DbHandler db = new DbHandler(this);
        boolean res = db.insertMsgRecord(des, amount, type, cate, balLeft, msgStr, nameStr);
        if (res) {
            Toast.makeText(this, "Saved!", Toast.LENGTH_LONG).show();
            finish();
        } else
            Toast.makeText(this, "Failed To Create !\nSomething Went Wrong", Toast.LENGTH_LONG).show();
    }
}