package com.example.allakumarreddy.moneybook.Activities;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import com.example.allakumarreddy.moneybook.MessageParser.ChunksFragment;
import com.example.allakumarreddy.moneybook.MessageParser.ChunksFragmentInteractionListener;
import com.example.allakumarreddy.moneybook.MessageParser.ItemDetailFragment;
import com.example.allakumarreddy.moneybook.MessageParser.MessageListFragment;
import com.example.allakumarreddy.moneybook.R;

public class MessageParseActivity extends AppCompatActivity implements ChunksFragmentInteractionListener {

    private String mChunkData;
    private String des;
    private String amount;
    private int type;
    private String cate;
    private String balLeft;
    private String selectedMsg;

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
    public void onParse(String str) {
        ItemDetailFragment itemDetailFragment = new ItemDetailFragment();
        this.mChunkData=str;
        itemDetailFragment.setChunkText(this.mChunkData);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, itemDetailFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    public void saveFields(String desStr, String amountStr, int typeInt, String cateStr, String balLeftStr) {
        this.des=desStr;
        this.amount=amountStr;
        this.type=typeInt;
        this.cate=cateStr;
        this.balLeft=balLeftStr;

        FragmentTransaction transaction=getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container,new MessageListFragment());
        transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    public void seelctedMsg(String s) {
        this.selectedMsg=s;

        //next fragment
    }
}