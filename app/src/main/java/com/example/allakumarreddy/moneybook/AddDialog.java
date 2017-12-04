package com.example.allakumarreddy.moneybook;

import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;

/**
 * Created by alla.kumarreddy on 7/20/2017.
 */

public class AddDialog extends Dialog implements android.view.View.OnClickListener {

    final static String TAG="AddDialog";
    private final MainActivity activity;
    private Button add, cancel;

    public AddDialog(MainActivity activity) {
        super(activity);
        this.activity = activity;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.add_dialog);
        add = (Button) findViewById(R.id.addCus);
        cancel = (Button) findViewById(R.id.cancel);
        add.setOnClickListener(this);
        cancel.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.addCus:
                String des=((EditText)findViewById(R.id.des)).getText().toString();
                String amount=((EditText)findViewById(R.id.amount)).getText().toString();
                LoggerCus.d(TAG,des+" : "+amount);
                activity.setAddDialogDetails(new String[]{des,amount});
                activity.afterCallingAddDialog();
                break;
            default:
                break;
        }
        dismiss();
    }
}
