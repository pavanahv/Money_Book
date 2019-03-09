package com.example.allakumarreddy.moneybook;

import android.content.Context;
import android.support.annotation.NonNull;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.drive.DriveContents;
import com.google.android.gms.drive.DriveFile;
import com.google.android.gms.drive.DriveFolder;
import com.google.android.gms.drive.DriveResourceClient;
import com.google.android.gms.drive.Metadata;
import com.google.android.gms.drive.MetadataBuffer;
import com.google.android.gms.drive.MetadataChangeSet;
import com.google.android.gms.drive.events.OpenFileCallback;
import com.google.android.gms.drive.query.Filters;
import com.google.android.gms.drive.query.Query;
import com.google.android.gms.drive.query.SearchableField;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;

/**
 * Created by alla.kumarreddy on 12-Apr-18.
 */

public class GoogleDriveBackup {

    private final Context mContext;
    private final PreferencesCus sp;
    private final File mFile;
    private final String text;
    private DriveResourceClient mDriveResourceClient;
    Query query;
    private final String TAG = "GoogleDriveBackup";
    private GoogleApiClient mDriveClient;
    private DriveFolder mDriveFolder;
    private boolean mBackup = true;

    public GoogleDriveBackup(File file, Context context, DriveResourceClient driveResourceClient, String text) {
        this.mContext = context;
        sp = new PreferencesCus(mContext);
        this.mFile = file;
        mDriveResourceClient = driveResourceClient;
        this.text = text;
        mBackup = false;
    }

    public void backup() {
        getAppFolder();
    }

    private void getAppFolder() {
        Task<DriveFolder> getAppFolderTask = mDriveResourceClient.getAppFolder();
        getAppFolderTask.addOnSuccessListener(new OnSuccessListener<DriveFolder>() {
            @Override
            public void onSuccess(DriveFolder driveFolder) {
                LoggerCus.d(TAG, "Got app folder");
                GoogleDriveBackup.this.mDriveFolder = driveFolder;
                showAllFiles();
            }
        });
        getAppFolderTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                LoggerCus.d(TAG, "Error in getting app folder " + e.getMessage());
            }
        });
    }

    private void createFile() {
        final Task<DriveFolder> appFolderTask = mDriveResourceClient.getAppFolder();
        final Task<DriveContents> createContentsTask = mDriveResourceClient.createContents();
        Tasks.whenAll(appFolderTask, createContentsTask)
                .continueWithTask(task -> {
                    DriveFolder parent = appFolderTask.getResult();
                    DriveContents contents = createContentsTask.getResult();
                    OutputStream outputStream = contents.getOutputStream();
                    try (Writer writer = new OutputStreamWriter(outputStream)) {
                        writer.write(text);
                    }

                    MetadataChangeSet changeSet = new MetadataChangeSet.Builder()
                            .setTitle(Utils.getBackupFile())
                            .setMimeType("text/plain")
                            .setStarred(true)
                            .build();

                    return mDriveResourceClient.createFile(parent, changeSet, contents);
                })
                .addOnSuccessListener(new OnSuccessListener<DriveFile>() {
                    @Override
                    public void onSuccess(DriveFile driveFile) {
                        LoggerCus.d(TAG, "File creation request accepted");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        LoggerCus.d(TAG, "Unable to create file " + e.getMessage());
                    }
                })
                .addOnCompleteListener(new OnCompleteListener<DriveFile>() {
                    @Override
                    public void onComplete(@NonNull Task<DriveFile> task) {
                        LoggerCus.d(TAG, "File created successfully");
                        Toast.makeText(mContext, "backed up to google drive", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void showAllFiles() {
        Query query = new Query.Builder()
                .addFilter(Filters.eq(SearchableField.MIME_TYPE, "text/plain"))
                .build();
        // [START query_children]
        Task<MetadataBuffer> queryTask = mDriveResourceClient.queryChildren(mDriveFolder, query);
        // END query_children]
        queryTask.addOnSuccessListener(new OnSuccessListener<MetadataBuffer>() {
            @Override
            public void onSuccess(MetadataBuffer metadata) {
                LoggerCus.d(TAG, "Got all the files in folder");
                int len = metadata.getCount();
                boolean found = false;
                int file = 0;
                for (int i = 0; i < len; i++) {
                    Metadata md = metadata.get(i);
                    LoggerCus.d(TAG, md.getTitle());
                    if (md.getTitle().compareToIgnoreCase(Utils.getBackupFile()) == 0) {
                        found = true;
                        file = i;
                        break;
                    }
                }
                if (found) {
                    LoggerCus.d(TAG, "Backup File found");
                    DriveFile temp = metadata.get(file).getDriveId().asDriveFile();
                    if (mBackup) {
                        rewriteContents(temp);
                    } else {
                        retrieveContents(temp);
                    }
                } else {
                    createFile();
                }
            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        LoggerCus.d(TAG, "Error in getting list of files " + e.getMessage());
                    }
                });
    }


    private void rewriteContents(DriveFile file) {
        // [START open_for_write]
        Task<DriveContents> openTask =
                mDriveResourceClient.openFile(file, DriveFile.MODE_WRITE_ONLY);
        // [END open_for_write]
        // [START rewrite_contents]
        openTask.continueWithTask(task -> {
            DriveContents driveContents = task.getResult();
            try (OutputStream out = driveContents.getOutputStream()) {
                out.write(text.getBytes());
            }
            // [START commit_content]
            Task<Void> commitTask =
                    mDriveResourceClient.commitContents(driveContents, null);
            // [END commit_content]
            return commitTask;
        })
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        LoggerCus.d(TAG, "Backuped to Drive Sucessfully !");
                        Toast.makeText(mContext, "Backuped to Drive Sucessfully !", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        LoggerCus.d(TAG, "Backup request success !");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        LoggerCus.d(TAG, "Error in Backuping to Drive " + e.getMessage());
                        Toast.makeText(mContext, "Error in Backuping to Drive " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
        // [END rewrite_contents]
    }

    private void retrieveContents(DriveFile file) {
        OpenFileCallback openCallback = new OpenFileCallback() {
            @Override
            public void onProgress(long bytesDownloaded, long bytesExpected) {
                int progress = (int) (bytesDownloaded * 100 / bytesExpected);
                LoggerCus.d(TAG, String.format("Loading progress: %d percent", progress));
                //mProgressBar.setProgress(progress);
            }

            @Override
            public void onContents(@NonNull DriveContents driveContents) {
                //mProgressBar.setProgress(100);
                try {
                    try (BufferedReader reader = new BufferedReader(
                            new InputStreamReader(driveContents.getInputStream()))) {
                        StringBuilder builder = new StringBuilder();
                        String line;
                        while ((line = reader.readLine()) != null) {
                            builder.append(line);
                        }
                        LoggerCus.d(TAG, builder.toString());
                        mDriveResourceClient.discardContents(driveContents);
                        Toast.makeText(mContext, "Restored Successufully !", Toast.LENGTH_SHORT).show();
                    }
                } catch (IOException e) {
                    onError(e);
                }
            }

            @Override
            public void onError(@NonNull Exception e) {
                LoggerCus.d(TAG, "Unable to Restore " + e.getMessage());
                Toast.makeText(mContext, "Unable to Restore " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        };

        mDriveResourceClient.openFile(file, DriveFile.MODE_READ_ONLY, openCallback);
    }
}
