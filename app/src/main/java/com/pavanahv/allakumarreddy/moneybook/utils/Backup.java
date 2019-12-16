package com.pavanahv.allakumarreddy.moneybook.utils;

import android.content.Context;

import com.pavanahv.allakumarreddy.moneybook.storage.FileStore;
import com.pavanahv.allakumarreddy.moneybook.storage.db.DbHandler;

import java.io.File;
import java.io.IOException;

/**
 * Created by alla.kumarreddy on 17-Mar-18.
 */

public class Backup {
    private final DbHandler db;
    private final Context context;
    private File f;

    public Backup(Context context) {
        this.context = context;
        this.db = new DbHandler(this.context);
    }

    public boolean send() {
        String s = db.getRecords();
        boolean res = false;
        try {
            f = new FileStore().writeFile(s,true);
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

    public File getBackupFile() {
        return this.f;
    }
}
