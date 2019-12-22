package com.pavanahv.allakumarreddy.moneybook.utils;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.googleapis.media.MediaHttpDownloaderProgressListener;
import com.google.api.client.googleapis.media.MediaHttpUploaderProgressListener;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.File;
import com.pavanahv.allakumarreddy.moneybook.Activities.RestoreActivity;
import com.pavanahv.allakumarreddy.moneybook.Services.BackupToGoogleDriveService;
import com.pavanahv.allakumarreddy.moneybook.storage.PreferencesCus;
import com.pavanahv.allakumarreddy.moneybook.storage.db.DbHandler;

import java.util.Collections;
import java.util.List;


/**
 * Created by alla.kumarreddy on 12-Apr-18.
 */

public class GoogleDriveBackup {


    private static final String TAG = "GoogleDriveBackup";
    private final Context mContext;
    private final DbHandler db;
    private final PreferencesCus mPref;
    private DriveServiceHelper mDriveServiceHelper;
    private MediaHttpUploaderProgressListener mUploadProgressCallback;

    public GoogleDriveBackup(Context context) {
        this.mContext = context;
        db = new DbHandler(context);
        mPref = new PreferencesCus(mContext);
        initGoogleDriveClint();
    }

    private void initGoogleDriveClint() {
        GoogleSignInAccount googleAccount = GoogleSignIn.getLastSignedInAccount(mContext);
        // Use the authenticated account to sign in to the Drive service.
        GoogleAccountCredential credential =
                GoogleAccountCredential.usingOAuth2(
                        mContext, Collections.singleton(DriveScopes.DRIVE_FILE));
        credential.setSelectedAccount(googleAccount.getAccount());
        Drive googleDriveService =
                new Drive.Builder(
                        AndroidHttp.newCompatibleTransport(),
                        new GsonFactory(),
                        credential)
                        .setApplicationName("MoneyBook")
                        .build();
        LoggerCus.d(TAG, "Google driver service created");
        mDriveServiceHelper = new DriveServiceHelper(googleDriveService);

    }

    public void backup(MediaHttpUploaderProgressListener uploadProgressCallback) {
        LoggerCus.d(TAG, "backing up to google drive");
        String openFileId = mPref.getGoogleDriveBackypFileName();
        mUploadProgressCallback = uploadProgressCallback;
        if (openFileId == null) {
            LoggerCus.d(TAG, "OpenFileId is not found in preferences. So trying to create file in google drive");
            createFile();
            return;
        } else {
            saveFile(openFileId);
        }
    }

    private void saveFile(String openFileId) {
        if (openFileId == null) {
            failedToBackup(false, "File not created in drive");
            return;
        }

        if (mDriveServiceHelper != null) {

            String fileName = GlobalConstants.GOOGLE_DRIVE_BACKUP_FILE_NAME;
            String fileContent = db.getRecords();

            mDriveServiceHelper.saveFile(openFileId, fileName, fileContent, mUploadProgressCallback)
                    .addOnCompleteListener((OnCompleteListener) task -> {
                        mPref.saveGoogleDriveBackypFileDate();
                        failedToBackup(true, "Google Drive Backup Success");
                    })
                    .addOnFailureListener(exception ->
                            failedToBackup(false, "Not able to write to file in drive -> " + exception.getMessage()));
        } else {
            failedToBackup(false, "Drive service helper not created");
        }
    }

    private void createFile() {
        if (mDriveServiceHelper != null) {
            Log.d(TAG, "Creating a file.");

            mDriveServiceHelper.createFile()
                    .addOnSuccessListener(fileId -> {
                        mPref.saveGoogleDriveBackypFileName(fileId);
                        saveFile(fileId);
                    })
                    .addOnFailureListener(exception ->
                            failedToBackup(false, "Failed to create file -> " + exception.getMessage()));
        }
    }

    public void checkFile(RestoreActivity.GoogldDriveBackupCheck googldDriveBackupCheck) {
        if (mDriveServiceHelper != null) {
            mDriveServiceHelper.queryFiles().addOnSuccessListener(fileList -> {
                List<File> list = fileList.getFiles();
                String temp = GlobalConstants.GOOGLE_DRIVE_BACKUP_FILE_NAME;
                boolean found = false;
                for (File f : list) {
                    if (f.getName().compareToIgnoreCase(temp) == 0) {
                        LoggerCus.d(TAG, f.getName());
                        found = true;
                        mPref.saveGoogleDriveBackypFileName(f.getId());
                        break;
                    }
                }
                if (found)
                    googldDriveBackupCheck.onCheckFile(true);
                else
                    googldDriveBackupCheck.onCheckFile(false);
            }).addOnFailureListener(e -> {
                googldDriveBackupCheck.onCheckFile(false);
            });
        }
    }

    public void readFile(MediaHttpDownloaderProgressListener listener,
                         RestoreActivity.GoogldDriveBackupCheck googldDriveBackupCheck) {
        if (mDriveServiceHelper != null) {
            String openFileId = mPref.getGoogleDriveBackypFileName();
            if (openFileId != null) {
                Log.d(TAG, "Reading file from google drive");
                mDriveServiceHelper.readFile(openFileId, listener)
                        .addOnSuccessListener(nameAndContent -> {
                            String name = nameAndContent.first;
                            String content = nameAndContent.second;
                            googldDriveBackupCheck.onFileDownloaded(true, content);
                        })
                        .addOnFailureListener(exception -> {
                            Log.e(TAG, "Couldn't read file.", exception);

                        });

            }
        }
    }

    private void failedToBackup(boolean success, String str) {
        if (success) {
            LoggerCus.d(TAG, "Backup successful via REST. ");
        } else {
            LoggerCus.d(TAG, "Unable to save file via REST. " + str);
        }
        mContext.stopService(new Intent(mContext, BackupToGoogleDriveService.class));
    }
}
