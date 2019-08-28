package com.example.allakumarreddy.moneybook.SettingsLock;

import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.allakumarreddy.moneybook.R;
import com.example.allakumarreddy.moneybook.utils.GlobalConstants;
import com.example.allakumarreddy.moneybook.utils.LoggerCus;

public class CreateSmartPinActivity extends AppCompatActivity {

    private static final String TAG = "CreateSmartPinActivity";
    private String str = "";
    private boolean isLastOp;
    private TextView mStatus;
    private TextView mPinView;
    private boolean isVar;
    private int opCount = 0;
    private int varCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
    }

    public void onCancel(View view) {
        onBack();
    }

    public void create(View view) {


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
            reset();
            for (char cc : str.toCharArray()) {
                if (cc == '*' || cc == '+')
                    opCount++;
                else if (cc == 'X')
                    varCount++;
            }
            char lc = str.charAt(str.length() - 1);
            if (lc == '*' || lc == '+')
                isLastOp = true;
            else if (lc == 'X')
                isVar = true;
            str = str.substring(0, len - 1);
            updateTextView();
            LoggerCus.d(TAG, " isVar:" + isVar + " isLastop:" + isLastOp + " varcount:" + varCount + " opcount" + opCount);
        }
        hideStatus();
    }

    private void reset() {
        isLastOp = false;
        isVar = false;
        varCount = 0;
        opCount = 0;
    }

    private void calc(char c) {

        // checking for operands
        boolean op = false;
        if (c == '*' || c == '+')
            op = true;
        if (op && isLastOp) {
            error("Cannot Put Two Operands Side By Side");
            return;
        }
        if (op) {
            if (str.length() == 0) {
                error("You Cannot start with operand");
                return;
            }
            opCount++;
            if (opCount > 2) {
                opCount--;
                error("Cannot use more than two operands");
                return;
            }
        }

        boolean varb = false;
        if (c == 'X') {
            varb = true;
        }

        LoggerCus.d(TAG, "varb:" + varb + " isVar:" + isVar + " op:" + op + " isLastop:" + isLastOp + " varcount:" + varCount + " opcount" + opCount);
        if (isVar && varb || (varb && varCount == 1)) {
            error("Only One Operand Is allowed");
            return;
        }
        if (!isLastOp && varb) {
            error("Cannot Add Variable After Number. Please Add Operand In Between\n Ex. 2*x+19");
            return;
        }

        boolean isNum = false;
        if (!varb && !op)
            isNum = true;

        isVar = varb;
        if (varb)
            varCount++;
        isLastOp = op;
        hideStatus();
        str = str + c;
        updateTextView();
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
