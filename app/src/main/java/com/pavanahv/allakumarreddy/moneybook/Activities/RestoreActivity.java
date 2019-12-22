package com.pavanahv.allakumarreddy.moneybook.Activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.api.client.googleapis.media.MediaHttpDownloaderProgressListener;
import com.pavanahv.allakumarreddy.moneybook.R;
import com.pavanahv.allakumarreddy.moneybook.storage.FileStore;
import com.pavanahv.allakumarreddy.moneybook.storage.PreferencesCus;
import com.pavanahv.allakumarreddy.moneybook.storage.db.DbHandler;
import com.pavanahv.allakumarreddy.moneybook.utils.GlobalConstants;
import com.pavanahv.allakumarreddy.moneybook.utils.GoogleDriveBackup;
import com.pavanahv.allakumarreddy.moneybook.utils.LoggerCus;
import com.pavanahv.allakumarreddy.moneybook.utils.Utils;

import java.io.File;
import java.io.IOException;

public class RestoreActivity extends BaseActivity {

    private static final String TAG = RestoreActivity.class.getSimpleName();
    private static final int MY_PERMISSIONS_REQUEST_READ_WRITE_STORAGE = 10010;
    private GoogldDriveBackupCheck mGoogldDriveBackupCheck;
    private GoogleDriveBackup mGoogleDriveBackup;
    private PreferencesCus mPref;
    private String mContent = null;
    private File localBackupFile = null;
    private boolean isBackupSuccess = false;
    private File mRestoredFile = null;
    private boolean firstSkip = false;

    public void skip(View view) {
        skip();
    }

    public interface GoogldDriveBackupCheck {
        void onCheckFile(boolean isSuccess);

        void onFileDownloaded(boolean isSuccess, String content);
    }

    private TextView mDriveStatus;
    private TextView mLocalStatus;
    private TextView mProgressText;
    private ProgressBar mProgress;
    private FileStore mFileStore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restore);
        LoggerCus.d(TAG, "onCreate");
        init();
        initViews();
        if (Utils.checkReadWriteStoragePermissions(this)) {
            afterStoragePermission();
        } else {
            LoggerCus.d(TAG, "No permission for file storage");
            requestPermissionForReadWriteStorage();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_WRITE_STORAGE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    afterStoragePermission();
                } else {
                    requestPermissionForReadWriteStorage();
                }
            }
            break;
        }
    }

    private void afterStoragePermission() {
        LoggerCus.d(TAG, "got permission for filesystem");
        initLocalCheck();
        initDriveCheck();
    }

    private void init() {
        mPref = new PreferencesCus(this);
    }

    private void initDriveCheck() {
        mGoogldDriveBackupCheck = new GoogldDriveBackupCheck() {
            @Override
            public void onCheckFile(boolean isSuccess) {
                LoggerCus.d(TAG, "checkfile -> " + isSuccess);
                if (isSuccess) {
                    LoggerCus.d(TAG, "File found in drive");
                    mDriveStatus.setText("Backup File Found");
                    mProgressText.setText("Downloading File From Google Drive");
                    downloadBackupFile();
                } else {
                    LoggerCus.d(TAG, "File not found in drive");
                    mProgress.setIndeterminate(false);
                    mDriveStatus.setText("No File Found in Drive");
                    performFinishingTasks();
                }
            }

            @Override
            public void onFileDownloaded(boolean isSuccess, String content) {
                if (isSuccess) {
                    new Thread(() -> {
                        LoggerCus.d(TAG, "File downloaded from drive");
                        LoggerCus.d(TAG, content + "");
                        try {
                            mRestoredFile = mFileStore.writeFile(content, false);
                        } catch (IOException e) {
                            LoggerCus.d(TAG, "Error in storing file in local storage");
                        }
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mProgressText.setText("File Download successfully!");
                                performFinishingTasks();
                            }
                        });
                    }).start();
                } else {
                    LoggerCus.d(TAG, "File not downloaded from drive");
                    mProgress.setIndeterminate(true);
                    mDriveStatus.setText("Error in downloading file");
                    mProgressText.setText(content);
                    performFinishingTasks();
                }
            }
        };
        mGoogleDriveBackup = new GoogleDriveBackup(this);
        mProgressText.setText("Getting File From Drive");
        LoggerCus.d(TAG, "checking file is there in drive");
        mGoogleDriveBackup.checkFile(mGoogldDriveBackupCheck);
    }

    private void downloadBackupFile() {
        mProgress.setIndeterminate(false);
        MediaHttpDownloaderProgressListener mediaDownloader = downloader -> {
            mProgress.setProgress((int) downloader.getProgress() * 100);
            mProgressText.setText(downloader.getNumBytesDownloaded() + " Bytes");
        };
        LoggerCus.d(TAG, "Downloading file from drive");
        mGoogleDriveBackup.readFile(mediaDownloader, mGoogldDriveBackupCheck);
    }

    private void performFinishingTasks() {
        LoggerCus.d(TAG, "Checking the way to restore data");
        if (localBackupFile == null) {
            LoggerCus.d(TAG, "local backup is null");
            if (mRestoredFile == null) {
                LoggerCus.d(TAG, "drive backup is null");
                skip();
            } else {
                LoggerCus.d(TAG, "" + mRestoredFile.length());
                LoggerCus.d(TAG, "drive backup found and local backup is null so restoring data from there");
                importContent();
            }
        } else {
            LoggerCus.d(TAG, "local backup is not null");
            if (mRestoredFile == null) {
                LoggerCus.d(TAG, "drive backup is null so going with local backup which is not null");
                importWithLocalBackup();
            } else {
                LoggerCus.d(TAG, "both drive and local backup found");
                if (mRestoredFile.length() >= localBackupFile.length()) {
                    LoggerCus.d(TAG, "" + mRestoredFile.length() + " " + localBackupFile.length());
                    LoggerCus.d(TAG, "drive file length is more going with dirve file");
                    importContent();
                } else {
                    LoggerCus.d(TAG, "local file length is more going with local file");
                    importWithLocalBackup();
                }
            }
        }
    }

    private void importContent() {
        clearScreen();
        new Thread(() -> {
            String content = mFileStore.readLocalFile(false);
            LoggerCus.d(TAG, content);
            try {
                if (content != null) {
                    importData(content);
                    isBackupSuccess = true;
                } else {
                    LoggerCus.d(TAG, "no content in drive file");
                }
            } catch (Exception e) {
                LoggerCus.d(TAG, "Exception in importing");
            }
            skip();
        }).start();
    }

    private void importData(String content) {
        DbHandler db = new DbHandler(this);
        db.addRecords(content);
    }

    private void requestPermissionForReadWriteStorage() {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                MY_PERMISSIONS_REQUEST_READ_WRITE_STORAGE);
    }

    private void clearScreen() {
        findViewById(R.id.local).setVisibility(View.GONE);
        findViewById(R.id.drive).setVisibility(View.GONE);
        mProgress.setIndeterminate(true);
        mProgressText.setText("Restoring ......");
    }

    private void importWithLocalBackup() {
        clearScreen();
        new Thread(() -> {
            String content = mFileStore.readLocalFile(true);
            if (content != null) {
                importData(content);
                isBackupSuccess = true;
            } else {
                LoggerCus.d(TAG, "no content in local backup file");
            }
            skip();
        }).start();
    }

    private synchronized void skip() {
        if (!firstSkip) {
            firstSkip = true;
        } else {
            return;
        }
        LoggerCus.d(TAG, "skipping");
        DbHandler db = new DbHandler(this);
        new Thread(() -> {
            if (!isBackupSuccess) {
                for (int i = 0; i < 4; i++)
                    db.addCategory(GlobalConstants.OTHERS_CAT, i);
                db.addPaymentMethod(GlobalConstants.OTHERS_CAT);
            }
            runOnUiThread(() -> {
                mPref.setRestoreStatus();

                // go to login activity
                Intent intent = new Intent(RestoreActivity.this, MainActivity.class);
                intent.putExtra(GlobalConstants.LOGIN_CHECK, true);
                startActivity(intent);
                overridePendingTransition(R.anim.move_right_in_activity, R.anim.move_left_out_activity);
                finish();
            });

        }).start();
    }

    private void initLocalCheck() {
        LoggerCus.d(TAG, "Checking for local backup");
        mFileStore = new FileStore();
        localBackupFile = mFileStore.checkAndGetBackupMediaFile();
        if (localBackupFile == null) {
            LoggerCus.d(TAG, "no backup file found");
            mLocalStatus.setText("No Local Backup Found");
        } else {
            mLocalStatus.setText("Backup File Found. " + localBackupFile.length() + " Bytes");
            LoggerCus.d(TAG, "backup file found");
        }
    }

    private void initViews() {
        mDriveStatus = (TextView) findViewById(R.id.google_drive_status);
        mLocalStatus = (TextView) findViewById(R.id.local_status);
        mProgressText = (TextView) findViewById(R.id.restorestatus);
        mProgress = (ProgressBar) findViewById(R.id.progress);
    }
}
