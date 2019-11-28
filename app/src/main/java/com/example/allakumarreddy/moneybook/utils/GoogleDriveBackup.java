package com.example.allakumarreddy.moneybook.utils;

import android.content.Context;
import android.content.Intent;

import com.example.allakumarreddy.moneybook.Services.BackupToGoogleDriveService;
import com.example.allakumarreddy.moneybook.storage.db.DbHandler;
import com.example.allakumarreddy.moneybook.storage.PreferencesCus;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.googleapis.media.MediaHttpUploaderProgressListener;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;

import java.util.Collections;


/**
 * Created by alla.kumarreddy on 12-Apr-18.
 */

public class GoogleDriveBackup {


    private static final String TAG = "GoogleDriveBackup";
    private final Context mContext;
    private final DbHandler db;
    private final PreferencesCus mPref;
    private DriveServiceHelper mDriveServiceHelper;

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

        mDriveServiceHelper = new DriveServiceHelper(googleDriveService);
    }

    public void backup(MediaHttpUploaderProgressListener uploadProgressCallback) {
        String openFileId = mPref.getGoogleDriveBackypFileName();
        if (mDriveServiceHelper != null && openFileId != null) {
            LoggerCus.d(TAG, "Saving " + openFileId);

            String fileName = GlobalConstants.GOOGLE_DRIVE_BACKUP_FILE_NAME;
            String fileContent = db.getRecords();

            mDriveServiceHelper.saveFile(openFileId, fileName, fileContent, uploadProgressCallback)
                    .addOnCompleteListener((OnCompleteListener) task -> {
                        mPref.saveGoogleDriveBackypFileDate();
                        mContext.stopService(new Intent(mContext, BackupToGoogleDriveService.class));
                    })
                    .addOnFailureListener(exception ->
                            LoggerCus.d(TAG, "Unable to save file via REST. " + exception.getMessage()));
        }
    }
}
