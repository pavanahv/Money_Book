package com.pavanahv.allakumarreddy.moneybook.storage;

import android.os.Environment;

import com.pavanahv.allakumarreddy.moneybook.utils.GlobalConstants;
import com.pavanahv.allakumarreddy.moneybook.utils.LoggerCus;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * Created by alla.kumarreddy on 7/20/2017.
 */

public class FileStore {

    final static String TAG = "FileStore";
    private String restoredFileName;
    private String fileName;

    public FileStore(int type) {
        this.fileName = GlobalConstants.type[type];
    }

    public FileStore() {
        this.fileName = "Backup";
        this.restoredFileName = "Restore";
    }

    public String readLocalFile(boolean isBackup) {
        String outs = null;
        try {
            RandomAccessFile rs = new RandomAccessFile(getMediaFile(isBackup), "rw");
            for (String s = ""; s != null; s = rs.readLine()) {
                if (outs == null)
                    outs = "";
                outs += s;
            }
            rs.close();
        } catch (FileNotFoundException e) {
            LoggerCus.d(TAG, e.getMessage());
        } catch (IOException e) {
            LoggerCus.d(TAG, e.getMessage());
        }
        return outs;
    }

    public File writeFile(String s, boolean isBackup) throws IOException {
        File f = getMediaFile(isBackup);
        RandomAccessFile rs = new RandomAccessFile(f, "rw");
        rs.setLength(0);
        rs.writeBytes(s);
        rs.close();
        return f;
    }

    private File getMediaFile(boolean isBackup) {
        File mediaStorageDir = new File(Environment.getExternalStorageDirectory(), "MBStore");
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                LoggerCus.d(TAG, "failed to create or open directory");
                return null;
            }
        }

        File mediaFile;
        String filename = "";
        if (isBackup)
            filename += this.fileName + ".mb";
        else
            filename += this.restoredFileName + ".mb";
        mediaFile = new File(mediaStorageDir.getPath() + File.separator + filename);
        /*if (mediaFile.exists())
            mediaFile.delete();*/
        return mediaFile;
    }

    public File checkAndGetBackupMediaFile() {
        File mediaStorageDir = new File(Environment.getExternalStorageDirectory(), "MBStore");
        if (!mediaStorageDir.exists()) {
            LoggerCus.d(TAG, "Local Backup Not Found to restore..");
            return null;
        }

        File mediaFile;
        String filename = "";
        filename += this.fileName + ".mb";
        mediaFile = new File(mediaStorageDir.getPath() + File.separator + filename);
        if (mediaFile.exists())
            return mediaFile;
        else
            return null;
    }
}
