package com.example.allakumarreddy.moneybook.Activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.example.allakumarreddy.moneybook.R;
import com.example.allakumarreddy.moneybook.db.DbHandler;
import com.example.allakumarreddy.moneybook.utils.LoggerCus;

public class AddActivity extends AppCompatActivity implements android.view.View.OnClickListener {

    private static final String TAG = "AddActivity";
    private int type;
    private String tcategory;
    private AutoCompleteTextView autoCompleteTextView;
    private Spinner categoryView;
    private DbHandler db;
    private String[] catArr;
    private ArrayAdapter<String> adapter;
    private Button add;
    private Button cancel;

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
            if (type == 0) {
                //desEd.setHint("Description");
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

                adapter = new ArrayAdapter<String>(this, android.R.layout.select_dialog_item);
                updateAdapter("");
                autoCompleteTextView.setAdapter(adapter);
                autoCompleteTextView.setThreshold(1);
            } else if (type == 2)
                autoCompleteTextView.setHint("Description For MoneyTransfer");
            ArrayAdapter aa = new ArrayAdapter(this, android.R.layout.simple_spinner_item, catArr);
            aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            categoryView.setAdapter(aa);
            categoryView.setSelection(curInd);
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
        adapter.addAll(db.getDesForAutoComplete(s));
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.addCus:
                String des = autoCompleteTextView.getText().toString();
                String amount = ((EditText) findViewById(R.id.amount)).getText().toString();
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
