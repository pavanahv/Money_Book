package com.example.allakumarreddy.moneybook.Activities;

import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.allakumarreddy.moneybook.R;
import com.example.allakumarreddy.moneybook.storage.PreferencesCus;
import com.example.allakumarreddy.moneybook.utils.GlobalConstants;

public class CreatePinActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_pin);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Pin Lock");
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
        EditText pint = (EditText) findViewById(R.id.pin);
        EditText cpint = (EditText) findViewById(R.id.cpin);
        int pin = Integer.parseInt(pint.getText().toString());
        int cpin = Integer.parseInt(cpint.getText().toString());
        if (pin == cpin) {
            PreferencesCus preferenceCus = new PreferencesCus(this);
            preferenceCus.setLockPinData(pin);
            Toast.makeText(this, "Lock Pin Set Successfully!", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            TextView tv = ((TextView) findViewById(R.id.status));
            tv.setText("Pin & Confirm Pin are not equal");
            pint.setText("");
            cpint.setText("");
            pint.setFocusable(true);
            tv.setVisibility(View.VISIBLE);
        }
    }

}
