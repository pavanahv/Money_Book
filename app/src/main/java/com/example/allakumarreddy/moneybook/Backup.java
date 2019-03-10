package com.example.allakumarreddy.moneybook;

import android.widget.Toast;

import java.io.File;
import java.io.IOException;

/**
 * Created by alla.kumarreddy on 17-Mar-18.
 */

public class Backup implements Runnable {
    private final DbHandler db;
    private final MainActivity context;

    public Backup(DbHandler db, MainActivity context) {
        this.db = db;
        this.context = context;
    }

    public void send() {
        new Thread(this).start();
    }

    @Override
    public void run() {
        String s = db.getRecords();
        try {
            final File f = new FileStore().writeFile(s);
            String response = "Backup Successfull !";
            LoggerCus.d("Backup", response);
            context.runOnUiThread(() -> {
                Toast.makeText(context, response, Toast.LENGTH_LONG).show();
                context.mBackupFile = f;
                context.signIn();
            });
        } catch (IOException e) {
            LoggerCus.d("Backup", e.getMessage());
            context.runOnUiThread(() -> Toast.makeText(context, "Error in Backup !" + e.getMessage(), Toast.LENGTH_LONG).show());
        }

    }
}
