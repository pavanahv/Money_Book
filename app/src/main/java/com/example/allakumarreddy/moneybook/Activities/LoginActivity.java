package com.example.allakumarreddy.moneybook.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.allakumarreddy.moneybook.R;
import com.example.allakumarreddy.moneybook.storage.PreferencesCus;
import com.example.allakumarreddy.moneybook.utils.Utils;

import java.util.Random;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";
    private int n1 = 2;
    private int n2 = 9;
    private char op1 = '*';
    private char op2 = '+';
    private int input;
    private int orgResult;
    private TextView numberView;
    private EditText inputText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        PreferencesCus sp = new PreferencesCus(this);
        if (sp.getData(Utils.getEmail()) == null) {
            startActivity(new Intent(this, WelcomeActivity.class));
            finish();
        } else {
            Toast.makeText(this, "Logged in as " + sp.getData(Utils.getEmail()), Toast.LENGTH_SHORT).show();
        }

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

    private void generateRandNumber() {
        Random randomGenerator = new Random();
        orgResult = randomGenerator.nextInt(10) + 1;
        numberView.setText("" + orgResult);
        input = orgResult;
        orgResult = calResult();
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
            startActivity(new Intent(this, MainActivity.class).putExtra("login", true));
            finish();
        } else {
            Toast.makeText(this, "Login Failed ! \n Please Enter Correct Code", Toast.LENGTH_LONG).show();
            generateRandNumber();
        }

    }

    private int calResult() {
        int result = getResOnOp(n1, input, op1);
        result = getResOnOp(result, n2, op2);
        return result;
    }

    private int getResOnOp(int a, int b, char op) {
        switch (op) {
            case '+':
                return a + b;
            case '-':
                return a - b;
            case '*':
                return a * b;
        }
        return 0;
    }
}
