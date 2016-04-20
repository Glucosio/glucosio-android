/*
 * Copyright (C) 2016 Glucosio Foundation
 *
 * This file is part of Glucosio.
 *
 * Glucosio is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, version 3.
 *
 * Glucosio is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Glucosio.  If not, see <http://www.gnu.org/licenses/>.
 *
 *
 */

package org.glucosio.android.activity;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveApi;
import com.google.android.gms.drive.DriveContents;
import com.google.android.gms.drive.DriveFile;
import com.google.android.gms.drive.DriveFolder;
import com.google.android.gms.drive.DriveId;
import com.google.android.gms.drive.MetadataChangeSet;
import com.google.android.gms.drive.query.Filters;
import com.google.android.gms.drive.query.Query;
import com.google.android.gms.drive.query.SearchableField;

import org.glucosio.android.GlucosioApplication;
import org.glucosio.android.R;
import org.glucosio.android.backup.Backup;
import org.glucosio.android.db.DatabaseHandler;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;

import io.realm.Realm;

public class BackupActivity extends AppCompatActivity {

    private Backup backup;
    private GoogleApiClient mGoogleApiClient;
    private String TAG = "glucosio_drive_backup";
    private Button backupButton;
    private Button restoreButton;
    private DriveFile restoredFile;
    private Realm realm;


    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.backup_drive_activity);

        realm = new DatabaseHandler(getApplicationContext()).getRealmIstance();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getResources().getString(R.string.title_activity_backup));

        backup = ((GlucosioApplication) getApplicationContext()).getBackup();
        backup.init(this);
        connectClient();
        mGoogleApiClient = backup.getClient();

        backupButton = (Button) findViewById(R.id.activity_backup_drive_button_backup);
        restoreButton = (Button) findViewById(R.id.activity_backup_drive_button_restore);

        backupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadToDrive(new File(realm.getPath()));
            }
        });

        restoreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                restoreFromDrive();
            }
        });
    }

    private void uploadToDrive(final File file) {
        Drive.DriveApi.newDriveContents(mGoogleApiClient)
                .setResultCallback(new ResultCallback<DriveApi.DriveContentsResult>() {

                    @Override
                    public void onResult(DriveApi.DriveContentsResult result) {
                        if (!result.getStatus().isSuccess()) {
                            Log.e(TAG, "Error while trying to create new file contents");
                            return;
                        }
                        final DriveContents driveContents = result.getDriveContents();

                        // Perform I/O off the UI thread.
                        new Thread() {
                            @Override
                            public void run() {
                                // write content to DriveContents
                                OutputStream outputStream = driveContents.getOutputStream();

                                FileInputStream inputStream = null;
                                try {
                                    inputStream = new FileInputStream(file);
                                } catch (FileNotFoundException e) {
                                    e.printStackTrace();
                                }

                                byte[] buf = new byte[1024];
                                int bytesRead;
                                try {
                                    if (inputStream != null) {
                                        while ((bytesRead = inputStream.read(buf)) > 0) {
                                                outputStream.write(buf, 0, bytesRead);
                                        }
                                    }
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }


                                MetadataChangeSet changeSet = new MetadataChangeSet.Builder()
                                        .setTitle("glucosio.realm")
                                        .setMimeType("text/plain")
                                        .build();

                                // create a file on app folder
                                Drive.DriveApi.getAppFolder(mGoogleApiClient)
                                        .createFile(mGoogleApiClient, changeSet, driveContents)
                                        .setResultCallback(fileCallback);
                            }
                        }.start();
                    }
                });
    }

    private void restoreFromDrive(){
        // First we search for plain text file named glucosio.realm
        Query query = new Query.Builder()
                .addFilter(Filters.eq(SearchableField.TITLE, "glucosio.realm"))
                .addFilter(Filters.eq(SearchableField.MIME_TYPE, "text/plain"))
                .build();

        Drive.DriveApi.query(mGoogleApiClient, query)
                .setResultCallback(new ResultCallback<DriveApi.MetadataBufferResult>() {

                    @Override
                    public void onResult(DriveApi.MetadataBufferResult result) {
                        if (!result.getStatus().isSuccess()) {
                            Log.e(TAG, "Problem while retrieving results");
                            return;
                        }
                        Log.e(TAG,result.toString());
                        Log.e(TAG, result.getMetadataBuffer().getCount()+"");
                        // Pick the first file in App Folder
                        if (result.getMetadataBuffer().get(0).isInAppFolder()){
                            restoredFile = result.getMetadataBuffer().get(0).getDriveId().asDriveFile();
                            result.release();
                        }

                        if (restoredFile != null) {
                            // Now get the file from the id
                            restoredFile.open(mGoogleApiClient, DriveFile.MODE_READ_ONLY, null)
                                    .setResultCallback(new ResultCallback<DriveApi.DriveContentsResult>() {
                                        @Override
                                        public void onResult(DriveApi.DriveContentsResult result) {
                                            if (!result.getStatus().isSuccess()) {
                                                showErrorDialog();
                                                return;
                                            }

                                            // DriveContents object contains pointers
                                            // to the actual byte stream
                                            DriveContents contents = result.getDriveContents();
                                            InputStream input = contents.getInputStream();

                                            try {
                                                File file = new File(realm.getPath(), "default.realm");
                                                OutputStream output = new FileOutputStream(file);
                                                try {
                                                    try {
                                                        byte[] buffer = new byte[4 * 1024]; // or other buffer size
                                                        int read;

                                                        while ((read = input.read(buffer)) != -1) {
                                                            output.write(buffer, 0, read);
                                                        }
                                                        output.flush();
                                                    } finally {
                                                        output.close();
                                                    }
                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                }
                                            } catch (FileNotFoundException e) {
                                                e.printStackTrace();
                                            } finally {
                                                try {
                                                    input.close();
                                                } catch (IOException e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                        }
                                    });

                            // Reboot app
                            Intent mStartActivity = new Intent(getApplicationContext(), MainActivity.class);
                            int mPendingIntentId = 123456;
                            PendingIntent mPendingIntent = PendingIntent.getActivity(getApplicationContext(), mPendingIntentId, mStartActivity, PendingIntent.FLAG_CANCEL_CURRENT);
                            AlarmManager mgr = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
                            mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 100, mPendingIntent);
                            System.exit(0);
                        } else {
                            showErrorDialogRestore();
                        }
                    }
                });

    }

    private void showErrorDialogRestore() {
        Toast.makeText(getApplicationContext(), R.string.activity_backup_drive_error_restore,Toast.LENGTH_SHORT).show();
    }

    final private ResultCallback<DriveFolder.DriveFileResult> fileCallback = new
            ResultCallback<DriveFolder.DriveFileResult>() {
                @Override
                public void onResult(DriveFolder.DriveFileResult result) {
                    if (!result.getStatus().isSuccess()) {
                        Log.d(TAG, "Error while trying to create the file");
                        showErrorDialog();
                        finish();
                        return;
                    }
                    showSuccessDialog();
                    finish();
                }
            };


    private void showSuccessDialog() {
        Toast.makeText(getApplicationContext(), R.string.activity_backup_drive_success, Toast.LENGTH_SHORT).show();
    }

    private void showErrorDialog() {
        Toast.makeText(getApplicationContext(), R.string.activity_backup_drive_failed, Toast.LENGTH_SHORT).show();
    }

    public void connectClient() {
        backup.start();
    }

    public void disconnectClient() {
        backup.stop();
    }


    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        switch (requestCode) {
            case 1:
                if (resultCode == RESULT_OK) {
                    backup.start();
                }
                break;
        }
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return true;
    }
}