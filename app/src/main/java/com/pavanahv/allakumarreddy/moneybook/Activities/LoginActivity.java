package com.pavanahv.allakumarreddy.moneybook.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.pavanahv.allakumarreddy.moneybook.R;
import com.pavanahv.allakumarreddy.moneybook.interfaces.FingerPrintInterface;
import com.pavanahv.allakumarreddy.moneybook.storage.PreferencesCus;
import com.pavanahv.allakumarreddy.moneybook.utils.FingerPrintManager;
import com.pavanahv.allakumarreddy.moneybook.utils.GlobalConstants;
import com.pavanahv.allakumarreddy.moneybook.utils.LoggerCus;
import com.pavanahv.allakumarreddy.moneybook.utils.Utils;

import java.util.Arrays;
import java.util.Random;

public class LoginActivity extends BaseActivity {

    private static final String TAG = "LoginActivity";
    private int n1 = 2;
    private int n2 = 9;
    private char op1 = '*';
    private char op2 = '+';
    private int input;
    private int orgResult;
    private TextView numberView;
    private EditText inputText;
    private boolean isFingerPrintEnabled;
    private int mPinLoginData = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        PreferencesCus sp = new PreferencesCus(this);
        LoggerCus.d(TAG, sp.getData(Utils.getEmail()) + " login data");
        if (sp.getData(Utils.getEmail()) == null || !sp.getRestoreStatus()) {
            startActivity(new Intent(this, WelcomeActivity.class));
            overridePendingTransition(R.anim.move_right_in_activity, R.anim.move_left_out_activity);
            finish();
            return;
        }

        init();
    }

    private void init() {
        SharedPreferences prefereneManager = PreferenceManager.getDefaultSharedPreferences(this);
        String lockType = prefereneManager.getString(GlobalConstants.PREF_LOCK_TYPE, null);
        isFingerPrintEnabled = prefereneManager.getBoolean(GlobalConstants.PREF_LOCK_FINGERPRINT, false);
        if (isFingerPrintEnabled) {
            loadFingerPrint();
        }

        if (lockType != null) {
            switch (Integer.parseInt(lockType)) {
                case 2:
                    if (!isFingerPrintEnabled) {
                        loginSuccess();
                    }
                    break;
                case 0:
                    enablePinLock();
                    break;
                case 1:
                    enableSmartLock();
                    break;
            }
        } else {
            loginSuccess();
        }
    }

    private void loadFingerPrint() {
        FingerPrintInterface mFingerPrintInterfaceCallback = new FingerPrintInterface() {
            @Override
            public void update(boolean isSuccess, String message) {
                ImageView fingerPringImage = (ImageView) findViewById(R.id.finger_print_image);
                TextView mStatusView = (TextView) findViewById(R.id.status_text);
                if (isSuccess) {
                    fingerPringImage.setImageResource(R.drawable.ic_finger_print_success);
                    loginSuccess();
                } else {
                    mStatusView.setText(message);
                    fingerPringImage.setImageResource(R.drawable.ic_finger_print_error);
                    mStatusView.setVisibility(View.VISIBLE);
                }

            }
        };

        FingerPrintManager fm = new FingerPrintManager(this, mFingerPrintInterfaceCallback);
        if (fm.main()) {
            findViewById(R.id.finger_print).setVisibility(View.VISIBLE);
        } else {
            findViewById(R.id.finger_print).setVisibility(View.GONE);
            Toast.makeText(this, fm.getErrorText(), Toast.LENGTH_LONG).show();
        }
    }

    private void enableSmartLock() {
        findViewById(R.id.smart_lock).setVisibility(View.VISIBLE);

        inputText = (EditText) findViewById(R.id.inputnumber);
        numberView = (TextView) findViewById(R.id.number);
        inputText.setOnKeyListener((v, keyCode, event) -> {
            if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                    (keyCode == KeyEvent.KEYCODE_ENTER)) {
                login(null);
                return true;
            }
            return false;
        });

        generateRandNumber();
    }

    private void enablePinLock() {
        findViewById(R.id.pin_lock).setVisibility(View.VISIBLE);
        mPinLoginData = new PreferencesCus(this).getLockPinData();
        ((EditText) findViewById(R.id.input_pin_number)).setOnKeyListener((v, keyCode, event) -> {
            if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                    (keyCode == KeyEvent.KEYCODE_ENTER)) {
                pinLogin(null);
                return true;
            }
            return false;
        });
    }

    private void loginSuccess() {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        intent.putExtra(GlobalConstants.LOGIN_CHECK, true);
        if (getIntent().getBooleanExtra(GlobalConstants.SMART_REMAINDER_NOTI, false)) {
            intent.putExtra(GlobalConstants.SMART_REMAINDER_NOTI, true);
        }
        if (getIntent().getBooleanExtra(GlobalConstants.REPORTS_NOTI, false)) {
            intent.putExtra(GlobalConstants.REPORTS_NOTI, true);
        }
        startActivity(intent);
        overridePendingTransition(R.anim.move_right_in_activity, R.anim.move_left_out_activity);
        finish();
    }

    private void generateRandNumber() {
        Random randomGenerator = new Random();
        input = randomGenerator.nextInt(10) + 1;
        numberView.setText("" + input);
        calResult();
        LoggerCus.d(TAG, "orgresult : " + orgResult);
    }

    public void login(View view) {
        //2*x+9
        try {
            input = Integer.parseInt(inputText.getText().toString());
        } catch (Exception e) {
            Log.e(TAG, e.getMessage() + "  caught exception some error while converting string to input number");
            Toast.makeText(this, "Please Enter Only Number That To In Range", Toast.LENGTH_LONG).show();
            return;
        }
        if (orgResult == input) {
            loginSuccess();
        } else {
            Toast.makeText(this, "Login Failed ! \n Please Enter Correct Code", Toast.LENGTH_LONG).show();
            generateRandNumber();
        }

    }

    private void calResult() {
        String pin = new PreferencesCus(this).getLockSmartPinData();
        if (pin == null) {
            Toast.makeText(this, "Something Went Wrong With SmartPin\nTry Another Way To Login", Toast.LENGTH_LONG).show();
            orgResult = 9999;
            return;
        }
        LoggerCus.d(TAG, pin);
        int opCount = 0;
        for (char c : pin.toCharArray()) {
            if (c == '*' || c == '+') {
                opCount++;
            }
        }

        LoggerCus.d(TAG, "opcount : " + opCount);
        if (opCount == 2) {
            // There are two operators , so first calculate where * is there according to BODMAS rule
            // So getting two things other sides of *
            String vals[] = new String[]{
                    "", "", ""
            };
            char ops[] = new char[]{
                    '+', '+'
            };

            int ind = 0;
            boolean isMulFirst = false;
            boolean isOpFixed = false;
            for (char c : pin.toCharArray()) {
                if (c == '*' || c == '+') {
                    ops[ind] = c;
                    if (!isOpFixed) {
                        isOpFixed = true;
                        if (c == '*') {
                            isMulFirst = true;
                        } else {
                            isMulFirst = false;
                        }
                    }
                    ind++;
                    continue;
                }
                vals[ind] += c;
            }

            LoggerCus.d(TAG, "vals array : " + Arrays.toString(vals));

            if (isMulFirst) {
                orgResult = calculateResultOnIndex(vals, 0, 1, 2, ops[0], ops[1]);
            } else {
                orgResult = calculateResultOnIndex(vals, 1, 2, 0, ops[0], ops[1]);
            }
        } else {
            String vals[] = new String[]{
                    "", ""
            };

            int ind = 0;
            char op = '+';
            for (char c : pin.toCharArray()) {
                if (c == '*' || c == '+') {
                    op = c;
                    ind++;
                    continue;
                }
                vals[ind] += c;
            }

            LoggerCus.d(TAG, "vals array : " + Arrays.toString(vals));

            int val1 = getIntOfVarb(vals[0]);
            int val2 = getIntOfVarb(vals[1]);
            if (val1 == -1) {
                orgResult = getResOnOp(input, val2, op);
            } else if (val2 == -1) {
                orgResult = getResOnOp(val1, input, op);
            }
        }
    }

    private int calculateResultOnIndex(String vals[], int a, int b, int c, char op1, char op2) {
        int result = 0;
        int val1 = getIntOfVarb(vals[a]);
        int val2 = getIntOfVarb(vals[b]);
        if (val1 == -1) {
            result = getResOnOp(input, val2, op1);
        } else if (val2 == -1) {
            result = getResOnOp(val1, input, op1);
        } else {
            result = getResOnOp(val1, val2, op1);
        }
        // rest thing
        val1 = getIntOfVarb(vals[c]);
        if (val1 == -1) {
            result = getResOnOp(result, input, op2);
        } else {
            result = getResOnOp(result, val1, op2);
        }
        return result;
    }

    private int getIntOfVarb(String s) {
        if (s.charAt(0) == 'X')
            return -1;
        else
            return Integer.parseInt(s);
    }

    private int getResOnOp(int a, int b, char op) {
        switch (op) {
            case '+':
                return a + b;
            case '*':
                return a * b;
        }
        return 0;
    }

    public void pinLogin(View view) {
        int pin = Integer.parseInt(((EditText) findViewById(R.id.input_pin_number)).getText().toString());
        if (mPinLoginData == pin) {
            loginSuccess();
        } else {
            TextView tv = (TextView) findViewById(R.id.pin_status_text);
            tv.setText("PIN INCORRECT");
            tv.setVisibility(View.VISIBLE);
        }
    }
}
