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

    final static String TAG = "AddDialog";
    private final MainActivity activity;
    private final int type;
    private Button add, cancel;
    private EditText desEd;

    public AddDialog(MainActivity activity, int type) {
        super(activity);
        this.activity = activity;
        this.type = type;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.add_dialog);
        desEd = (EditText) findViewById(R.id.des);
        if (type == 0) {
            desEd.setHint("Description");
        } else {
            desEd.setHint("Name Of Category");
            findViewById(R.id.amount).setVisibility(View.GONE);
        }
        add = (Button) findViewById(R.id.addCus);
        cancel = (Button) findViewById(R.id.cancel);
        add.setOnClickListener(this);
        cancel.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.addCus:
                String des = ((EditText) findViewById(R.id.des)).getText().toString();
                String amount = ((EditText) findViewById(R.id.amount)).getText().toString();
                if(amount==null)
                    amount="0";
                if(des==null)
                    des="";
                LoggerCus.d(TAG, des + " : " + amount);
                activity.setAddDialogDetails(new String[]{des, amount});
                activity.afterCallingAddDialog();
                break;
            default:
                break;
        }
        dismiss();
    }
}
