package com.example.allakumarreddy.moneybook.Services;

import android.app.IntentService;
import android.content.Intent;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;

import com.example.allakumarreddy.moneybook.utils.AutoAddManager;
import com.example.allakumarreddy.moneybook.utils.Backup;
import com.example.allakumarreddy.moneybook.utils.GlobalConstants;
import com.example.allakumarreddy.moneybook.utils.LoggerCus;
import com.example.allakumarreddy.moneybook.utils.MessageParserBase;

import static com.example.allakumarreddy.moneybook.utils.GlobalConstants.ACTION_BACKUP;
import static com.example.allakumarreddy.moneybook.utils.GlobalConstants.ACTION_BACKUP_MAIN_ACTIVITY_OPEN;
import static com.example.allakumarreddy.moneybook.utils.GlobalConstants.ACTION_MSG_PARSE_BY_DATE;

public class MoneyBookIntentService extends IntentService {

    private static final String TAG = "MoneyBookIntentService";
    private Messenger handler;

    public MoneyBookIntentService() {
        super("MoneyBookIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_MSG_PARSE_BY_DATE.equals(action)) {
                handler = intent.getParcelableExtra(GlobalConstants.HANDLER_NAME);
                new MessageParserBase(getApplicationContext()).handleActionMsgParse();
                new AutoAddManager(getApplicationContext()).process();
                Message msg = new Message();
                msg.what = GlobalConstants.MSG_PARSING_COMPLETED;
                try {
                    handler.send(msg);
                } catch (RemoteException e) {
                    LoggerCus.d(TAG, e.getMessage());
                }
            } else if (ACTION_BACKUP.equals(action)) {
                boolean res = new Backup(this).send();
                LoggerCus.d(TAG, "Backup : " + res);
            } else if (ACTION_BACKUP_MAIN_ACTIVITY_OPEN.equals(action)) {
                handler = intent.getParcelableExtra(GlobalConstants.HANDLER_NAME);
                boolean res = new Backup(this).send();
                Message msg = new Message();
                msg.what = GlobalConstants.BACKUP_COMPLETED;
                if (res)
                    msg.arg1 = 1;
                else
                    msg.arg1 = 0;
                try {
                    handler.send(msg);
                } catch (RemoteException e) {
                    LoggerCus.d(TAG, e.getMessage());
                }
            }
        }
    }

}
