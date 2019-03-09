package com.example.allakumarreddy.moneybook;

import android.os.Environment;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * Created by alla.kumarreddy on 7/20/2017.
 */

public class FileStore {

    final static String TAG = "FileStore";
    private String fileName;

    public FileStore(int type) {
        this.fileName = GlobalConstants.type[type];
    }

    public FileStore() {
        this.fileName = "Backup";
    }

    public String readFile() {
        String outs = "";
        try {
            RandomAccessFile rs = new RandomAccessFile(getMediaFile(), "rw");
            for (String s = ""; s != null; s = rs.readLine())
                outs += s;
            rs.close();
        } catch (FileNotFoundException e) {
            LoggerCus.d(TAG, e.getMessage());
        } catch (IOException e) {
            LoggerCus.d(TAG, e.getMessage());
        }
        return outs;
    }

    public File writeFile(String s) throws IOException {
        File f=getMediaFile();
        RandomAccessFile rs = new RandomAccessFile(f, "rw");
        rs.setLength(0);
        rs.writeBytes(s);
        rs.close();
        return f;
    }

    private File getMediaFile() {
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), "MBStore");
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                LoggerCus.d(TAG, "failed to create or open directory");
                return null;
            }
        }

        File mediaFile;
        String filename = this.fileName + ".JSON";
        mediaFile = new File(mediaStorageDir.getPath() + File.separator + filename);
        if (mediaFile.exists())
            mediaFile.delete();
        return mediaFile;
    }
}
