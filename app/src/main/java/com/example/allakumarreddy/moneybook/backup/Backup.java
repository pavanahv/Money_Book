package com.example.allakumarreddy.moneybook.backup;

import android.content.Context;

import com.example.allakumarreddy.moneybook.db.DbHandler;
import com.example.allakumarreddy.moneybook.storage.FileStore;
import com.example.allakumarreddy.moneybook.utils.LoggerCus;

import java.io.File;
import java.io.IOException;

/**
 * Created by alla.kumarreddy on 17-Mar-18.
 */

public class Backup {
    private final DbHandler db;
    private final Context context;

    public Backup(Context context) {
        this.context = context;
        this.db = new DbHandler(this.context);
    }

    public boolean send() {
        String s = db.getRecords();
        boolean res = false;
        try {
            final File f = new FileStore().writeFile(s);
            String response = "Backup Successfull !";
            LoggerCus.d("Backup", response);
            //context.mBackupFile = f;
            //context.signIn();
            res = true;
        } catch (IOException e) {
            LoggerCus.d("Backup", e.getMessage());
            res = false;
        }
        return res;
    }
}
