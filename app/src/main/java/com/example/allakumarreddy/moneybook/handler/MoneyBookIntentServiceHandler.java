package com.example.allakumarreddy.moneybook.handler;


import android.os.Handler;
import android.os.Message;

import com.example.allakumarreddy.moneybook.interfaces.MoneyBookIntentServiceHandlerInterface;

public class MoneyBookIntentServiceHandler extends Handler {

    private final MoneyBookIntentServiceHandlerInterface mMoneyBookIntentServiceHandlerInterface;

    public MoneyBookIntentServiceHandler(MoneyBookIntentServiceHandlerInterface callback) {
        this.mMoneyBookIntentServiceHandlerInterface = callback;
    }

    @Override
    public void handleMessage(Message msg) {
        super.handleMessage(msg);
        mMoneyBookIntentServiceHandlerInterface.onResultReceived(msg);
    }
}
