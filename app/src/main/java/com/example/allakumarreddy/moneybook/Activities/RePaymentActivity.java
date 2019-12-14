package com.example.allakumarreddy.moneybook.Activities;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.allakumarreddy.moneybook.Adapter.RePaymentAdapter;
import com.example.allakumarreddy.moneybook.R;
import com.example.allakumarreddy.moneybook.storage.db.DbHandler;
import com.example.allakumarreddy.moneybook.utils.GlobalConstants;
import com.example.allakumarreddy.moneybook.utils.MBRecord;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class RePaymentActivity extends BaseActivity {

    private static final int ADD_ACTIVITY = 1001;
    private static final String TAG = RePaymentActivity.class.getSimpleName();
    private MBRecord mbr;
    private SimpleDateFormat format;
    private TextView leftBal;
    private DbHandler db;
    private ArrayList<MBRecord> dataList;
    private RePaymentAdapter adapter;
    private int mMainType;
    private int mType;
    private ListView lv;
    private View noData;
    private View statusView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_re_payment);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mbr = (MBRecord) getIntent().getSerializableExtra("MBRecord");
        initType();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        if (mMainType == GlobalConstants.TYPE_DUE)
            getSupportActionBar().setTitle("DUE RePayments");
        else if (mMainType == GlobalConstants.TYPE_LOAN)
            getSupportActionBar().setTitle("LOAN RePayments");

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(view -> {
            startAddActivity();
        });

        initViews();
    }

    private void startAddActivity() {
        Intent intent = new Intent(this, AddActivity.class);
        intent.putExtra("type", GlobalConstants.TYPE_LOAN_REPAYMENT);
        intent.putExtra(GlobalConstants.CATEGORY_TYPE, mMainType);
        startActivityForResult(intent, ADD_ACTIVITY);
        overridePendingTransition(R.anim.move_right_in_activity, R.anim.move_left_out_activity);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.repayment_menu, menu);
        return true;
    }

    private int getType() {
        int temp = mbr.getType();
        if (temp == GlobalConstants.TYPE_DUE_PAYMENT)
            temp = GlobalConstants.TYPE_DUE;
        else if (temp == GlobalConstants.TYPE_LOAN_PAYMENT) {
            temp = GlobalConstants.TYPE_LOAN;
        }
        return temp;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case (ADD_ACTIVITY): {
                if (resultCode == Activity.RESULT_OK) {
                    String s[] = new String[]{data.getStringExtra("fdes"),
                            data.getStringExtra("famount"),
                            data.getStringExtra("fcategory"),
                            data.getStringExtra("tcategory"),
                            data.getStringExtra("payment_method")};
                    if ((s[1] != "") && (s[2] != "")) {
                        if (mbr.getmRefIdForLoanDue() != null) {
                            db.addRePaymentRecord(new MBRecord(mbr.getmRefIdForLoanDue(),
                                    Integer.parseInt(s[1]), new Date(), mType, s[2], s[4]), mMainType, mbr);
                        } else {
                            Toast.makeText(this, "Cant Add RePayment Record!. Something went wrong!", Toast.LENGTH_LONG).show();
                        }
                    }
                }
                break;
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_analytics_detail_edit:
                startDetailActivity(mbr, findViewById(R.id.desc));
                finish();
                break;
            case android.R.id.home:
                finish();
                overridePendingTransition(R.anim.move_left_in_activity, R.anim.move_right_out_activity);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void startDetailActivity(MBRecord mbRecord, View view) {
        Intent intent = new Intent(this, AnalyticsItemDetail.class);
        intent.putExtra("MBRecord", mbRecord);
        startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this, view,
                getResources().getString(R.string.shared_anim_analytics_item)).toBundle());
    }

    private void initViews() {
        db = new DbHandler(this);
        ((TextView) findViewById(R.id.desc)).setText(mbr.getDescription());
        ((TextView) findViewById(R.id.amount)).setText("" + mbr.getAmount());
        format = new SimpleDateFormat("yyyy/MM/dd");
        ((TextView) findViewById(R.id.date)).setText(format.format(mbr.getDate()));
        ((TextView) findViewById(R.id.category)).setText(mbr.getCategory());
        ((TextView) findViewById(R.id.payment_method)).setText(mbr.getPaymentMethod());
        leftBal = (TextView) findViewById(R.id.left_bal);
        noData = findViewById(R.id.no_data);
        statusView = findViewById(R.id.status_view);

        lv = (ListView) findViewById(R.id.rv);
        dataList = new ArrayList<>();

        adapter = new RePaymentAdapter(this, dataList, (mbRecord, finalConvertView) -> {
            startDetailActivity(mbRecord, finalConvertView);
        });
        lv.setAdapter(adapter);

        updateUI();
    }

    private void initType() {
        mMainType = getType();
        int typeTemp = -1;
        if (mMainType == GlobalConstants.TYPE_DUE) {
            typeTemp = GlobalConstants.TYPE_DUE_REPAYMENT;
        } else if (mMainType == GlobalConstants.TYPE_LOAN) {
            typeTemp = GlobalConstants.TYPE_LOAN_REPAYMENT;
        }
        mType = typeTemp;
    }

    private void updateUI() {
        // update list
        dataList.clear();
        dataList.addAll(db.getRePaymentRecords(mType, mbr.getmRefIdForLoanDue()));
        adapter.notifyDataSetChanged();

        // update left out balance
        int lBal = db.getRePaymentAmount(mType, mbr.getmRefIdForLoanDue());
        if (lBal != -1) {
            int temp = mbr.getAmount() - lBal;
            if (temp > 0) {
                leftBal.setText("" + temp);
                leftBal.setVisibility(View.VISIBLE);
                statusView.setVisibility(View.GONE);
            } else {
                leftBal.setText("0");
                leftBal.setVisibility(View.GONE);
                statusView.setVisibility(View.VISIBLE);
            }
        } else {
            leftBal.setText(mbr.getAmount());
        }

        // update no data visibility
        if (dataList.size() <= 0) {
            lv.setVisibility(View.GONE);
            noData.setVisibility(View.VISIBLE);
        } else {
            noData.setVisibility(View.GONE);
            lv.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onResume() {
        updateUI();
        super.onResume();
    }
}
