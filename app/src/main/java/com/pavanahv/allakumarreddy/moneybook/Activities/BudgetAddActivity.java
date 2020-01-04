package com.pavanahv.allakumarreddy.moneybook.Activities;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.pavanahv.allakumarreddy.moneybook.R;
import com.pavanahv.allakumarreddy.moneybook.storage.db.DbHandler;
import com.pavanahv.allakumarreddy.moneybook.utils.GlobalConstants;
import com.pavanahv.allakumarreddy.moneybook.utils.LoggerCus;
import com.pavanahv.allakumarreddy.moneybook.utils.ThemeUtils;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.HashMap;
import java.util.HashSet;

public class BudgetAddActivity extends BaseActivity {

    private static final String TAG = BudgetAddActivity.class.getSimpleName();
    private EditText name;
    private Spinner intervalSpinner;
    private LinearLayout catLayout;
    private EditText limtAmt;
    private DbHandler db;
    private String[] catArr;
    private boolean[] catCheck;
    private String intervalData[];
    private HashMap<String, String> map = null;
    private boolean isEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(ThemeUtils.getTheme(getClass().getSimpleName(), this));
        setContentView(R.layout.activity_budget_add);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Add Budget");
        db = new DbHandler(this);
        if (getIntent().getExtras() != null)
            map = (HashMap<String, String>) getIntent().getExtras().get("map");
        if (map != null) {
            isEdit = true;
        }
        initViews();
    }

    private void initViews() {
        name = (EditText) findViewById(R.id.nameitem);
        intervalSpinner = (Spinner) findViewById(R.id.timeitem);
        catLayout = (LinearLayout) findViewById(R.id.cat);
        limtAmt = (EditText) findViewById(R.id.limititem);

        catArr = db.getCategeories(GlobalConstants.TYPE_SPENT);
        catCheck = new boolean[catArr.length];
        if (catLayout.getChildCount() > 0) {
            catLayout.removeAllViews();
        }

        intervalData = GlobalConstants.BUDGET_INTERVAL;
        ArrayAdapter aa = new ArrayAdapter(this,
                android.R.layout.simple_spinner_dropdown_item, intervalData);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        intervalSpinner.setAdapter(aa);
        intervalSpinner.setSelection(1);

        HashSet<String> catSet = new HashSet<>();
        if (isEdit) {
            JSONArray arr = null;
            try {
                arr = new JSONArray(map.get("cat"));
            } catch (JSONException e) {
                LoggerCus.d(TAG, "Error while parsing json array of cat -> " + e.getMessage());
            }

            if (arr != null) {
                final int len = arr.length();
                for (int i = 0; i < len; i++) {
                    try {
                        String item = (String) arr.get(i);
                        catSet.add(item);
                    } catch (JSONException e) {
                        LoggerCus.d(TAG, "error : " + e.getMessage());
                    }
                }

            }
        }
        final int catLen = catArr.length;
        LinearLayout.LayoutParams llp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        for (int i = 0; i < catLen; i++) {
            String cat = catArr[i];
            CheckBox cb = new CheckBox(this);
            cb.setText(cat);
            final int ind = i;
            if (!isEdit) {
                cb.setOnCheckedChangeListener((buttonView, isChecked) -> catCheck[ind] = isChecked);
            } else {
                cb.setEnabled(false);
                if (catSet.contains(cat)) {
                    cb.setChecked(true);
                }
            }
            catLayout.addView(cb, llp);
        }

        if (isEdit) {
            name.setText(map.get("name"));
            intervalSpinner.setSelection(Integer.parseInt(map.get("interval")));
            limtAmt.setText(map.get("limit"));
            name.setEnabled(false);
            intervalSpinner.setEnabled(false);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                finish();
                overridePendingTransition(R.anim.move_left_in_activity, R.anim.move_right_out_activity);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void cancel(View view) {
        super.onBackPressed();
    }

    public void save(View view) {
        boolean res = checkFields();
        if (res) {

            JSONArray arr = new JSONArray();
            for (int i = 0; i < catCheck.length; i++) {
                if (catCheck[i]) {
                    arr.put(catArr[i]);
                }
            }
            if (!isEdit) {
                res = db.addBudgetRecord(name.getText().toString(),
                        intervalSpinner.getSelectedItemPosition(),
                        arr.toString(),
                        Integer.parseInt(limtAmt.getText().toString()));
            } else {
                res = db.updateBudgetRecord(map.get("name"), Integer.parseInt(limtAmt.getText().toString()));
            }
            if (res) {
                error("Success");
                cancel(null);
            } else {
                error("Something Went Wrong. Please Try Again");
            }
        }
    }

    private boolean checkFields() {
        String temp = "";
        if (!isEdit) {
            // checking name
            temp = name.getText().toString().trim();
            if (!(temp != null && temp.length() > 0)) {
                error("Name Should Not Be Null");
                return false;
            }

            // checking category
            boolean res = false;
            for (int i = 0; i < catCheck.length; i++) {
                if (catCheck[i]) {
                    res = true;
                    break;
                }
            }
            if (!res) {
                error("Atleast One Category Nedds To Be Checked");
                return false;
            }
        }

        // checking limit amount
        temp = limtAmt.getText().toString().trim();
        if (!(temp != null && temp.length() > 0 && Integer.parseInt(temp) > 0)) {
            error("Limit Should Not Be Null And Should Be Greater Than 0");
            return false;
        }

        return true;
    }

    private void error(String s) {
        Toast.makeText(this, s, Toast.LENGTH_LONG).show();
    }
}
