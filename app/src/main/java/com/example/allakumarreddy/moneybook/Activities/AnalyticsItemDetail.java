package com.example.allakumarreddy.moneybook.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.allakumarreddy.moneybook.R;
import com.example.allakumarreddy.moneybook.storage.db.DbHandler;
import com.example.allakumarreddy.moneybook.utils.GlobalConstants;
import com.example.allakumarreddy.moneybook.utils.LoggerCus;
import com.example.allakumarreddy.moneybook.utils.MBRecord;

import java.text.ParseException;
import java.text.SimpleDateFormat;

public class AnalyticsItemDetail extends AppCompatActivity {

    private static final String TAG = AnalyticsItemDetail.class.getSimpleName();
    private MBRecord mbrOld;
    private SimpleDateFormat format;
    private EditText des;
    private EditText amount;
    private EditText date;
    private Spinner type;
    private DbHandler db;
    private Spinner cate;
    private String[] catArr;
    private int recType;
    private Spinner tcate;
    private Spinner paymentMethodView;
    private String[] payMethArr;
    private boolean firstTypeSelection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_analytics_item_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Item Detail");
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
                break;

            case R.id.action_analytics_detail_save:
                save();
                break;
            default:
                break;
        }
        finish();
        return super.onOptionsItemSelected(item);
    }

    private void init() {
        Intent intent = getIntent();
        db = new DbHandler(this);
        mbrOld = (MBRecord) intent.getSerializableExtra("MBRecord");
        recType = mbrOld.getType();

        des = (EditText) findViewById(R.id.descitem);
        amount = (EditText) findViewById(R.id.amountitem);
        date = (EditText) findViewById(R.id.dateitem);
        type = (Spinner) findViewById(R.id.typeitem);

        cate = (Spinner) findViewById(R.id.catitem);
        initCategory(mbrOld.getType(), mbrOld.getCategory());

        paymentMethodView = (Spinner) findViewById(R.id.payment_method);
        payMethArr = db.getPaymentMethods();
        int pCurind = -1;

        // initialize with others payment method
        for (int i = 0; i < payMethArr.length; i++) {
            if (payMethArr[i].compareToIgnoreCase(GlobalConstants.OTHERS_CAT) == 0) {
                pCurind = i;
                break;
            }
        }

        if (mbrOld.getPaymentMethod() != null) {
            for (int i = 0; i < payMethArr.length; i++) {
                if (payMethArr[i].compareToIgnoreCase(mbrOld.getPaymentMethod()) == 0) {
                    pCurind = i;
                    break;
                }
            }
        }

        ArrayAdapter paa = new ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, payMethArr);
        paymentMethodView.setAdapter(paa);
        paymentMethodView.setSelection(pCurind);

        if (recType == 4) {
            des.setVisibility(View.GONE);
            findViewById(R.id.descitemtext).setVisibility(View.GONE);
            type.setVisibility(View.GONE);
            findViewById(R.id.typeitemtext).setVisibility(View.GONE);
            ((TextView) findViewById(R.id.catfromitemtext)).setText("From Category");

            tcate = (Spinner) findViewById(R.id.cattoitem);
            int curind = -1;
            for (int i = 0; i < catArr.length; i++) {
                if (catArr[i].compareToIgnoreCase(mbrOld.getDescription()) == 0) {
                    curind = i;
                    break;
                }
            }
            ArrayAdapter tcaa = new ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, catArr);
            tcaa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            tcate.setAdapter(tcaa);
            tcate.setSelection(curind);
        } else {
            findViewById(R.id.cattoitem).setVisibility(View.GONE);
            findViewById(R.id.cattoitemtext).setVisibility(View.GONE);
            des.setText(mbrOld.getDescription());
            ArrayAdapter aa = new ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, new String[]{"Spent", "Earn", "Due", "Loan"});
            aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            type.setAdapter(aa);
            type.setSelection(mbrOld.getType());
            firstTypeSelection = true;
            type.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    if (!firstTypeSelection) {
                        initCategory(position, GlobalConstants.OTHERS_CAT);
                    } else {
                        firstTypeSelection = false;
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
        }
        amount.setText(mbrOld.getAmount() + "");
        date.setText(format.format(mbrOld.getDate()));
    }

    private void initCategory(int type, String cat) {
        LoggerCus.d(TAG, "type : " + type + " cat : " + cat);
        catArr = db.getCategeories(type);
        int curind = -1;
        // initialize with others category
        for (int i = 0; i < catArr.length; i++) {
            if (catArr[i].compareToIgnoreCase(GlobalConstants.OTHERS_CAT) == 0) {
                curind = i;
                break;
            }
        }

        if (!(cat.compareToIgnoreCase(GlobalConstants.OTHERS_CAT) == 0)) {
            if (cat != null) {
                for (int i = 0; i < catArr.length; i++) {
                    if (catArr[i].compareToIgnoreCase(cat) == 0) {
                        curind = i;
                        break;
                    }
                }
            }
        }

        ArrayAdapter caa = new ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, catArr);
        caa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        cate.setAdapter(caa);
        cate.setSelection(curind);
        caa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    }


    public void save(View view) {
        save();
        finish();
    }

    private void save() {
        try {
            boolean result = false;
            if (recType == 4) {
                MBRecord mbrNew = new MBRecord(catArr[tcate.getSelectedItemPosition()],
                        Integer.parseInt(amount.getText().toString()),
                        format.parse(date.getText().toString()), mbrOld.getType(),
                        catArr[cate.getSelectedItemPosition()], payMethArr[paymentMethodView.getSelectedItemPosition()]);
                result = db.updateMTRecord(mbrOld, mbrNew);
            } else {
                MBRecord mbrNew = new MBRecord(des.getText().toString(),
                        Integer.parseInt(amount.getText().toString()),
                        format.parse(date.getText().toString()), type.getSelectedItemPosition(),
                        catArr[cate.getSelectedItemPosition()], payMethArr[paymentMethodView.getSelectedItemPosition()]);
                result = db.updateRecord(mbrOld, mbrNew);
            }
            if (result)
                Toast.makeText(this, "Updated Successfully !", Toast.LENGTH_SHORT).show();
            else
                Toast.makeText(this, "Updation Error !", Toast.LENGTH_SHORT).show();
        } catch (ParseException e) {
            LoggerCus.d("analyticsdetailactivity", e.getMessage());
        }
    }
}
