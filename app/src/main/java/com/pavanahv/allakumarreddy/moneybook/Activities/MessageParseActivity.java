package com.pavanahv.allakumarreddy.moneybook.Activities;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.MenuItem;
import android.widget.Toast;

import com.pavanahv.allakumarreddy.moneybook.R;
import com.pavanahv.allakumarreddy.moneybook.fragments.ChunksFragment;
import com.pavanahv.allakumarreddy.moneybook.fragments.ItemDetailFragment;
import com.pavanahv.allakumarreddy.moneybook.fragments.MessageListFragment;
import com.pavanahv.allakumarreddy.moneybook.interfaces.ChunksFragmentInteractionListener;
import com.pavanahv.allakumarreddy.moneybook.storage.db.DbHandler;
import com.pavanahv.allakumarreddy.moneybook.utils.ThemeUtils;

public class MessageParseActivity extends BaseActivity implements ChunksFragmentInteractionListener {

    private String mChunkData;
    private String des;
    private String amount;
    private int type;
    private String cate;
    private String balLeft;
    private String selectedMsg;
    private String msgStr;
    private String nameStr;
    private boolean firstTime = true;
    private String payStr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(ThemeUtils.getTheme(getClass().getSimpleName(), this));
        setContentView(R.layout.activity_message_parse);
        getSupportActionBar().setTitle("Message Parser");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.fragment_container, new MessageListFragment());
        transaction.commit();
        //seelctedMsg("You have made a purchase for an amount of Rs. 24.00  on your Sodexo Card 637513-xxxxxx-6056 on 10:33,11 Mar at TULASI JUICE JUNCTION. The available balance on your Sodexo Card is INR 1000.10.Download the Zeta App to track your spends http://bit.ly/2RySHSA.");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                finish();
                overridePendingTransition(R.anim.move_left_in_activity, R.anim.move_right_out_activity);
                break;
        }
        return super.onOptionsItemSelected(item);
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
    public void saveFields(String desStr, String amountStr, int typeInt, String cateStr, String balLeftStr, String paymStr) {
        this.des = desStr;
        this.amount = amountStr;
        this.type = typeInt;
        this.cate = cateStr;
        this.balLeft = balLeftStr;
        this.payStr = paymStr;

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, new MessageListFragment());
        transaction.addToBackStack(null);
        transaction.commit();
        //seelctedMsg("You have made a purchase for an amount of Rs. 24.00  on your Sodexo Card 637513-xxxxxx-6056 on 10:33,11 Mar at TULASI JUICE JUNCTION. The available balance on your Sodexo Card is INR 1000.10.Download the Zeta App to track your spends http://bit.ly/2RySHSA.");
    }

    @Override
    public void seelctedMsg(String s) {
        this.selectedMsg = s;

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        if (firstTime) {
            firstTime = false;
            ChunksFragment chunkFragment = new ChunksFragment();
            chunkFragment.setMessage(s);

            transaction.replace(R.id.fragment_container, chunkFragment);
        } else {
            //next fragment
            ItemDetailFragment itemDetailFragment = new ItemDetailFragment();

            itemDetailFragment.setTypeActivate(1);
            itemDetailFragment.setAmountStr(this.amount);
            itemDetailFragment.setBalLeftStr(this.balLeft);
            itemDetailFragment.setCateStr(this.cate);
            itemDetailFragment.setDesStr(this.des);
            itemDetailFragment.setTypeStr(this.type);
            itemDetailFragment.setMsgStr(this.msgStr);
            itemDetailFragment.setPayStr(this.payStr);
            itemDetailFragment.setSelectedMsgStr(this.selectedMsg);

            transaction.replace(R.id.fragment_container, itemDetailFragment);
        }
        transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    public void saveEverything() {
        DbHandler db = new DbHandler(this);
        boolean res = db.insertMsgRecord(des, amount, type, cate, balLeft, msgStr, nameStr, payStr);
        if (res) {
            Toast.makeText(this, "Saved!", Toast.LENGTH_LONG).show();
            finish();
        } else
            Toast.makeText(this, "Failed To Create !\nSomething Went Wrong", Toast.LENGTH_LONG).show();
    }
}