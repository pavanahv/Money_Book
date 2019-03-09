package com.example.allakumarreddy.moneybook;

import android.content.Context;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;

/**
 * Created by alla.kumarreddy on 17-Mar-18.
 */

public class Backup {
    private final DbHandler db;
    private final Context context;

    public Backup(DbHandler db, Context context) {
        this.db = db;
        this.context = context;
    }

    public File send() {
        String s = db.getRecords();
        File f=null;
        try {
            f=new FileStore().writeFile(s);
            String response = "Backup Successfull !";
            LoggerCus.d("Backup", response);
            Toast.makeText(context, response, Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            LoggerCus.d("Backup", e.getMessage());
            Toast.makeText(context, "Error in Backup !" + e.getMessage(), Toast.LENGTH_LONG).show();
        }
        return f;
    }

    /*@Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(String... params) {
        return this.send();
    }

    private String send() {
        String s = db.getRecords();
        new FileStore().writeFile(s);
        return "Saved!";
    }

    @Override
    protected void onPostExecute(String response) {
        super.onPostExecute(response);
        LoggerCus.d("Backup",response);
        Toast.makeText(context,response,Toast.LENGTH_LONG).show();
    }*/
}
