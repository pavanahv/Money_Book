package com.pavanahv.allakumarreddy.moneybook.Activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.pavanahv.allakumarreddy.moneybook.Adapter.DescriptionAdapter;
import com.pavanahv.allakumarreddy.moneybook.R;
import com.pavanahv.allakumarreddy.moneybook.storage.db.DbHandler;
import com.pavanahv.allakumarreddy.moneybook.utils.GlobalConstants;
import com.pavanahv.allakumarreddy.moneybook.utils.LoggerCus;
import com.pavanahv.allakumarreddy.moneybook.utils.MBRecord;

import java.util.ArrayList;

public class AddActivity extends BaseActivity implements android.view.View.OnClickListener {

    private static final String TAG = "AddActivity";
    private int type;
    private String tcategory;
    private AutoCompleteTextView autoCompleteTextView;
    private Spinner categoryView;
    private DbHandler db;
    private String[] catArr;
    private DescriptionAdapter adapter;
    private Button add;
    private Button cancel;
    private ArrayList<MBRecord> records = new ArrayList<>();
    private EditText amtv;
    private Spinner paymentMethodView;
    private String[] payMethArr;
    ArrayList<MBRecord> prevRecords = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Add Item");
        db = new DbHandler(this);
        type = getIntent().getIntExtra("type", -1);
        if (type == GlobalConstants.PAYMENT_METHOD_MONEY_TRANSFER_SCREEN)
            tcategory = getIntent().getStringExtra("tcategory");
        init();
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

    private void init() {
        autoCompleteTextView = (AutoCompleteTextView) findViewById(R.id.autoCompleteTextViewDes);
        categoryView = (Spinner) findViewById(R.id.addcategory);
        paymentMethodView = (Spinner) findViewById(R.id.payment_method);
        amtv = ((EditText) findViewById(R.id.amount));
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        if (type == GlobalConstants.HOME_SCREEN ||
                type == GlobalConstants.PAYMENT_METHOD_MONEY_TRANSFER_SCREEN ||
                type == GlobalConstants.TYPE_LOAN_REPAYMENT ||
                type == GlobalConstants.TYPE_DUE_REPAYMENT) {
            int catType = getIntent().getIntExtra(GlobalConstants.CATEGORY_TYPE, -1);
            if (catType != -1)
                catArr = db.getCategeories(catType);
            else
                catArr = db.getCategeories(GlobalConstants.TYPE_SPENT);
            payMethArr = db.getPaymentMethods();
            int curInd = -1, pCurInd = -1;
            for (int i = 0; i < catArr.length; i++) {
                if ("others".compareToIgnoreCase(catArr[i]) == 0) {
                    curInd = i;
                    break;
                }
            }
            for (int i = 0; i < payMethArr.length; i++) {
                if ("others".compareToIgnoreCase(payMethArr[i]) == 0) {
                    pCurInd = i;
                    break;
                }
            }
            ArrayAdapter aa = new ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, catArr);
            aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            categoryView.setAdapter(aa);
            categoryView.setSelection(curInd);

            ArrayAdapter paa = new ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, payMethArr);
            paa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            paymentMethodView.setAdapter(paa);
            paymentMethodView.setSelection(pCurInd);

            if (type == GlobalConstants.HOME_SCREEN) {
                autoCompleteTextView.setHint("Description");
                autoCompleteTextView.addTextChangedListener(new TextWatcher() {

                    @Override
                    public void afterTextChanged(Editable s) {
                    }

                    @Override
                    public void beforeTextChanged(CharSequence s, int start,
                                                  int count, int after) {
                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start,
                                              int before, int count) {
                        if (s.length() != 0)
                            updateAdapter(s.toString(), catType);
                    }
                });

                adapter = new DescriptionAdapter(new ArrayList<>(), this);
                updateAdapter("", catType);
                autoCompleteTextView.setAdapter(adapter);
                autoCompleteTextView.setThreshold(1);
                autoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        MBRecord mbr = prevRecords.get(position);
                        autoCompleteTextView.setText(mbr.getDescription());
                        amtv.setText(mbr.getAmount() + "");

                        // category selection
                        String catStr = mbr.getCategory();
                        int catSel = -1;
                        for (int i = 0; i < catArr.length; i++) {
                            String ss = catArr[i];
                            if (ss.compareToIgnoreCase(catStr) == 0) {
                                catSel = i;
                                break;
                            }
                        }
                        if (catSel != -1)
                            categoryView.setSelection(catSel, true);

                        // payment selection
                        catStr = mbr.getPaymentMethod();
                        catSel = -1;
                        for (int i = 0; i < payMethArr.length; i++) {
                            String ss = payMethArr[i];
                            if (ss.compareToIgnoreCase(catStr) == 0) {
                                catSel = i;
                                break;
                            }
                        }
                        if (catSel != -1)
                            paymentMethodView.setSelection(catSel, true);

                        add.setFocusable(true);
                    }
                });
            } else if (type == GlobalConstants.TYPE_LOAN_REPAYMENT ||
                    type == GlobalConstants.TYPE_DUE_REPAYMENT) {
                autoCompleteTextView.setVisibility(View.GONE);
            }
        } else if (type == GlobalConstants.SAVE_FILTER_SCREEN) {
            autoCompleteTextView.setHint("Name Of Filter");
            findViewById(R.id.amount).setVisibility(View.GONE);
            categoryView.setVisibility(View.GONE);
            paymentMethodView.setVisibility(View.GONE);
        } else if (type == GlobalConstants.CATERGORY_SCREEN || type == GlobalConstants.PAYMENT_METHOD_SCREEN) {
            autoCompleteTextView.setHint("Name Of Category");
            findViewById(R.id.amount).setVisibility(View.GONE);
            categoryView.setVisibility(View.GONE);
            paymentMethodView.setVisibility(View.GONE);
        }
        add = (Button) findViewById(R.id.addCus);
        cancel = (Button) findViewById(R.id.cancel);
        add.setOnClickListener(this);
        cancel.setOnClickListener(this);
    }

    private void updateAdapter(String s, int type) {
        prevRecords.clear();
        prevRecords.addAll(records);
        adapter.clear();
        records = db.getDesForAutoComplete(s, type);
        ArrayList<String> alist = new ArrayList<>();
        for (MBRecord rec : records)
            alist.add(rec.getDescription() + "\nAmount : "
                    + rec.getAmount() + "\nCategory : " + rec.getCategory()
                    + "\nPaymentMethod : " + rec.getPaymentMethod());
        adapter.addAll(alist);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.addCus:
                String des = autoCompleteTextView.getText().toString();
                String amount = amtv.getText().toString();
                String category = "", paymentMethod = "";
                if (type == GlobalConstants.HOME_SCREEN ||
                        type == GlobalConstants.PAYMENT_METHOD_MONEY_TRANSFER_SCREEN ||
                        type == GlobalConstants.TYPE_LOAN_REPAYMENT ||
                        type == GlobalConstants.TYPE_DUE_REPAYMENT) {
                    category = catArr[categoryView.getSelectedItemPosition()];
                    paymentMethod = payMethArr[paymentMethodView.getSelectedItemPosition()];
                    if (amount == null)
                        amount = "0";
                    if (des == null)
                        des = "";
                    LoggerCus.d(TAG, des + " : " + amount);
                }

                Intent resultIntent = new Intent();
                resultIntent.putExtra("fdes", des);
                resultIntent.putExtra("famount", amount);
                resultIntent.putExtra("fcategory", category);
                resultIntent.putExtra("tcategory", tcategory);
                resultIntent.putExtra("type", type);
                resultIntent.putExtra("payment_method", paymentMethod);
                setResult(Activity.RESULT_OK, resultIntent);

                break;
            case R.id.cancel:
                setResult(Activity.RESULT_CANCELED, null);
                break;
            default:
                break;
        }
        finish();
    }
}
