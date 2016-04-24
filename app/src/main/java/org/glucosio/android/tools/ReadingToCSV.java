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

package org.glucosio.android.tools;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.util.Log;
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
import com.google.android.gms.drive.OpenFileActivityBuilder;

import org.glucosio.android.GlucosioApplication;
import org.glucosio.android.R;
import org.glucosio.android.backup.Backup;
import org.glucosio.android.db.GlucoseReading;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;

public class ReadingToCSV {

    private int REQUEST_CODE_PICKER = 2;

    private Activity activity;
    private Backup backup;
    private GoogleApiClient mGoogleApiClient;
    private String TAG = "glucosio_drive_backup";
    private IntentSender intentPicker;
    private ArrayList<GlucoseReading> readings;
    private String um;

    private File EXPORT_PATH = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);

    // Storage Permissions
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    public ReadingToCSV(Activity activity) {
        this.activity = activity;
    }

    public void shareCsv(final ArrayList<GlucoseReading> readings, final String um) {
        this.readings = readings;
        this.um = um;
        GlucosioApplication glucosioApplication = (GlucosioApplication) activity.getApplicationContext();

        backup = glucosioApplication.getBackup();
        mGoogleApiClient = backup.getClient();
        backup.init(activity);
        backup.start();

        openFolderPicker();
    }

    public void uploadToDrive(DriveId mFolderDriveId) {
        if (mFolderDriveId != null) {
            //Create the file on GDrive
            final DriveFolder folder = mFolderDriveId.asDriveFolder();
            Drive.DriveApi.newDriveContents(mGoogleApiClient)
                    .setResultCallback(new ResultCallback<DriveApi.DriveContentsResult>() {
                        @Override
                        public void onResult(DriveApi.DriveContentsResult result) {
                            if (!result.getStatus().isSuccess()) {
                                Log.e(TAG, "Error while trying to create new file contents");
                                showErrorDialog();
                                return;
                            }
                            final DriveContents driveContents = result.getDriveContents();

                            // Perform I/O off the UI thread.
                            new Thread() {
                                @Override
                                public void run() {
                                    // write content to DriveContents
                                    OutputStream outputStream = driveContents.getOutputStream();

                                    // Make csv file
                                    File file = new File(EXPORT_PATH, "glucosio_exported_data.csv");
                                    try {
                                        FileOutputStream f = new FileOutputStream(file);
                                        PrintWriter writer = new PrintWriter(f);
                                        writer.append(activity.getResources().getString(R.string.dialog_add_concentration));
                                        writer.append(',');

                                        writer.append(activity.getResources().getString(R.string.dialog_add_measured));
                                        writer.append(',');

                                        writer.append(activity.getResources().getString(R.string.dialog_add_date));
                                        writer.append(',');

                                        writer.append(activity.getResources().getString(R.string.dialog_add_time));
                                        writer.append('\n');

                                        FormatDateTime dateTool = new FormatDateTime(activity);

                                        if ("mg/dL".equals(um)) {
                                            for (int i = 0; i < readings.size(); i++) {

                                                writer.append(readings.get(i).getReading() + "mg/dL");
                                                writer.append(',');

                                                writer.append(readings.get(i).getReading_type() + "");
                                                writer.append(',');

                                                writer.append(dateTool.convertRawDate(readings.get(i).getCreated() + ""));
                                                writer.append(',');

                                                writer.append(dateTool.convertRawTime(readings.get(i).getCreated() + ""));
                                                writer.append('\n');
                                            }
                                        } else {
                                            GlucosioConverter converter = new GlucosioConverter();

                                            for (int i = 0; i < readings.size(); i++) {

                                                writer.append(converter.glucoseToMmolL(readings.get(i).getReading()) + "mmol/L");
                                                writer.append(',');

                                                writer.append(dateTool.convertRawDate(readings.get(i).getCreated() + ""));
                                                writer.append(',');

                                                writer.append(dateTool.convertRawTime(readings.get(i).getCreated() + ""));
                                                writer.append('\n');
                                            }
                                        }
                                        writer.flush();
                                        writer.close();
                                        f.close();
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }

                                    FileInputStream inputStream = null;
                                    try {
                                        inputStream = new FileInputStream(file);
                                    } catch (FileNotFoundException e) {
                                        showErrorDialog();
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
                                        showErrorDialog();
                                        e.printStackTrace();
                                    }


                                    MetadataChangeSet changeSet = new MetadataChangeSet.Builder()
                                            .setTitle("glucosio_exported.csv")
                                            .setMimeType("text/plain")
                                            .build();

                                    // create a file in selected folder
                                    folder.createFile(mGoogleApiClient, changeSet, driveContents)
                                            .setResultCallback(new ResultCallback<DriveFolder.DriveFileResult>() {
                                                @Override
                                                public void onResult(DriveFolder.DriveFileResult result) {
                                                    if (!result.getStatus().isSuccess()) {
                                                        Log.d(TAG, "Error while trying to create the file");
                                                        showErrorDialog();
                                                        return;
                                                    }
                                                    showSuccessDialog();
                                                }
                                            });
                                }
                            }.start();
                        }
                    });
        }
    }

    private void showSuccessDialog() {
        Toast.makeText(activity.getApplicationContext(), R.string.activity_csv_export_success, Toast.LENGTH_SHORT).show();
    }

    private void openFolderPicker() {
        try {
            if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
                    intentPicker = buildIntent();
                //Start the picker to choose a folder
                activity.startIntentSenderForResult(
                        intentPicker, REQUEST_CODE_PICKER, null, 0, 0, 0);
            }
        } catch (IntentSender.SendIntentException e) {
            Log.e(TAG, "Unable to send intent", e);
            showErrorDialog();
        }
    }

    private IntentSender buildIntent() {
        return Drive.DriveApi
                .newOpenFileActivityBuilder()
                .setMimeType(new String[]{DriveFolder.MIME_TYPE})
                .build(mGoogleApiClient);
    }

    public void showShareDialog(String path) {
        Intent shareIntent = new Intent(android.content.Intent.ACTION_SEND);
        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        shareIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse(path));
        shareIntent.setType("*/*");
        activity.startActivity(Intent.createChooser(shareIntent, activity.getResources().getString(R.string.share_using)));
    }

    private void showErrorDialog() {
        Toast.makeText(activity.getApplicationContext(), R.string.activity_backup_drive_failed, Toast.LENGTH_SHORT).show();
    }

    private void checkStoragePermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if(permission != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }
}
