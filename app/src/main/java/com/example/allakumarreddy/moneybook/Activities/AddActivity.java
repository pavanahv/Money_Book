package com.example.allakumarreddy.moneybook.Activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.example.allakumarreddy.moneybook.Adapter.DescriptionAdapter;
import com.example.allakumarreddy.moneybook.R;
import com.example.allakumarreddy.moneybook.db.DbHandler;
import com.example.allakumarreddy.moneybook.utils.LoggerCus;
import com.example.allakumarreddy.moneybook.utils.MBRecord;

import java.util.ArrayList;

public class AddActivity extends AppCompatActivity implements android.view.View.OnClickListener {

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
    private ArrayList<MBRecord> records;
    private EditText amtv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        db = new DbHandler(this);
        type = getIntent().getIntExtra("type", -1);
        if (type == 2)
            tcategory = getIntent().getStringExtra("tcategory");
        init();
    }

    private void init() {
        autoCompleteTextView = (AutoCompleteTextView) findViewById(R.id.autoCompleteTextViewDes);
        categoryView = (Spinner) findViewById(R.id.addcategory);
        amtv = ((EditText) findViewById(R.id.amount));
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        if (type == 0 || type == 2) {
            catArr = db.getCategeories();
            int curInd = -1;
            for (int i = 0; i < catArr.length; i++) {
                if ("others".compareToIgnoreCase(catArr[i]) == 0) {
                    curInd = i;
                    break;
                }
            }
            ArrayAdapter aa = new ArrayAdapter(this, android.R.layout.simple_spinner_item, catArr);
            aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            categoryView.setAdapter(aa);
            categoryView.setSelection(curInd);
            if (type == 0) {
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
                            updateAdapter(s.toString());
                    }
                });

                adapter = new DescriptionAdapter(new ArrayList<>(), this);
                updateAdapter("");
                autoCompleteTextView.setAdapter(adapter);
                autoCompleteTextView.setThreshold(1);
                autoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        String s = (String) parent.getItemAtPosition(position);
                        MBRecord mbr = null;
                        for (MBRecord rec : records) {
                            String a = rec.getDescription();
                            if (a.compareToIgnoreCase(s) == 0) {
                                mbr = rec;
                                break;
                            }
                        }
                        amtv.setText(mbr.getAmount() + "");
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
                        add.setFocusable(true);
                    }
                });
            } else if (type == 2)
                autoCompleteTextView.setVisibility(View.GONE);
        } else {
            autoCompleteTextView.setHint("Name Of Category");
            findViewById(R.id.amount).setVisibility(View.GONE);
            categoryView.setVisibility(View.GONE);
        }
        add = (Button) findViewById(R.id.addCus);
        cancel = (Button) findViewById(R.id.cancel);
        add.setOnClickListener(this);
        cancel.setOnClickListener(this);
    }

    private void updateAdapter(String s) {
        adapter.clear();
        records = db.getDesForAutoComplete(s);
        ArrayList<String> alist = new ArrayList<>();
        for (MBRecord rec : records)
            alist.add(rec.getDescription());
        adapter.addAll(alist);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.addCus:
                String des = autoCompleteTextView.getText().toString();
                String amount = amtv.getText().toString();
                String category = "";
                if (type == 0 || type == 2) {
                    category = catArr[categoryView.getSelectedItemPosition()];
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
