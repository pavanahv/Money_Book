package com.pavanahv.allakumarreddy.moneybook.Activities;

import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.pavanahv.allakumarreddy.moneybook.R;
import com.pavanahv.allakumarreddy.moneybook.storage.PreferencesCus;
import com.pavanahv.allakumarreddy.moneybook.utils.GlobalConstants;
import com.pavanahv.allakumarreddy.moneybook.utils.ThemeUtils;

public class CreateSmartPinActivity extends BaseActivity {

    private static final String TAG = "CreateSmartPinActivity";
    private String str = "";
    private boolean isLastOp = false;
    private TextView mStatus;
    private TextView mPinView;
    private boolean isVar = false;
    private int opCount = 0;
    private int varCount = 0;
    private boolean isLastNum = false;
    private String numChar = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(ThemeUtils.getTheme(getClass().getSimpleName(), this));
        setContentView(R.layout.activity_create_smart_pin);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Smart Pin Lock");

        mStatus = (TextView) findViewById(R.id.status);
        mPinView = (TextView) findViewById(R.id.pin);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBack();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        onBack();
        super.onBackPressed();
    }

    private void onBack() {
        PreferenceManager.getDefaultSharedPreferences(this).edit().putString(GlobalConstants.PREF_LOCK_TYPE, "2").apply();
        Toast.makeText(this, "Lock Disabled!\nPlease select lock from settings again.", Toast.LENGTH_LONG).show();
        finish();
        overridePendingTransition(R.anim.move_left_in_activity, R.anim.move_right_out_activity);
    }

    public void onCancel(View view) {
        onBack();
    }

    public void create(View view) {

        boolean res = checkFormula();
        if (res) {
            PreferencesCus pref = new PreferencesCus(this);
            pref.setLockSmartPinData(str);
            finish();
            overridePendingTransition(R.anim.move_left_in_activity, R.anim.move_right_out_activity);
        }
    }

    private boolean checkFormula() {
        String s = str;
        int numC = 0, varC = 0, opC = 0;
        boolean lastNum = false;
        for (char c : s.toCharArray()) {
            if (c == '+' || c == '*') {
                opC++;
                if (lastNum) {
                    numC++;
                    lastNum = false;
                }
            } else if (c == 'X') {
                varC++;
                if (lastNum) {
                    numC++;
                    lastNum = false;
                }
            } else {
                lastNum = true;
            }
        }
        if (lastNum) {
            numC++;
        }

        if (numC < 1) {
            error("Atleast one number should be used in formula");
            return false;
        }

        if (opC < 1) {
            error("Atleast one Operator should be used in formula");
            return false;
        }

        if (varC < 1) {
            error("Variable should be used in formula");
            return false;
        }
        return true;
    }

    public void buttonFire(View view) {
        switch (view.getId()) {
            case R.id.plus:
                calc('+');
                break;
            case R.id.mul:
                calc('*');
                break;
            case R.id.del:
                del();
                break;
            case R.id.one:
                calc('1');
                break;
            case R.id.two:
                calc('2');
                break;
            case R.id.three:
                calc('3');
                break;
            case R.id.four:
                calc('4');
                break;
            case R.id.five:
                calc('5');
                break;
            case R.id.six:
                calc('6');
                break;
            case R.id.seven:
                calc('7');
                break;
            case R.id.eight:
                calc('8');
                break;
            case R.id.nine:
                calc('9');
                break;
            case R.id.zero:
                calc('0');
                break;
            case R.id.variable:
                calc('X');
                break;
            default:
                break;
        }
    }

    private void del() {
        final int len = str.length();
        if (len > 0) {
            String temp = str.substring(0, len - 1);
            reset();
            for (char cc : temp.toCharArray()) {
                calc(cc);
            }
        }
    }

    private void reset() {
        isLastOp = false;
        isVar = false;
        isLastNum = false;
        varCount = 0;
        opCount = 0;
        str = "";
        numChar = "";
        updateTextView();
    }

    private void calc(char c) {

        // checking for operands
        boolean op = false;
        if (c == '*' || c == '+')
            op = true;

        boolean varb = false;
        if (c == 'X') {
            varb = true;
        }

        boolean isNum = false;
        if (!varb && !op)
            isNum = true;

        if (op) {
            if (isLastOp) {
                error("Cannot Put Two Operators Side By Side");
                return;
            }

            if (str.length() == 0) {
                error("You Cannot start with operator");
                return;
            }

            opCount++;

            if (opCount > 2) {
                opCount--;
                error("Cannot use more than two operators");
                return;
            }
        }

        if (varb) {
            if (isVar || (varCount == 1)) {
                error("Only One Operand Is allowed");
                return;
            }

            if (!isLastOp && isLastNum) {
                error("Cannot Add Variable After Number. Please Add Operator In Between\n Ex. 2*x+19");
                return;
            }

            varCount++;
        }

        if (isNum) {
            if (isVar) {
                error("Cannot User Number After Variable. Use Operator In Middle");
                return;
            }

            if (isLastNum) {
                int tempNum = Integer.parseInt(numChar + c);
                if (tempNum >= 100) {
                    error("Number Cannot be more than 100");
                    return;
                }
                numChar += c;
            } else {
                numChar = "" + c;
            }
        } else {
            numChar = "";
        }

        isVar = varb;
        isLastOp = op;
        isLastNum = isNum;
        hideStatus();
        str = str + c;
        updateTextView();
        //LoggerCus.d(TAG, "varb:" + varb + " isVar:" + isVar + " op:" + op + " isLastop:" + isLastOp + " varcount:" + varCount + " opcount" + opCount);
    }

    private void updateTextView() {
        mPinView.setText(str);
    }

    private void error(String s) {
        mStatus.setText(s);
        showStatus();
    }

    private void showStatus() {
        mStatus.setVisibility(View.VISIBLE);
    }

    private void hideStatus() {
        mStatus.setVisibility(View.INVISIBLE);
    }
}
