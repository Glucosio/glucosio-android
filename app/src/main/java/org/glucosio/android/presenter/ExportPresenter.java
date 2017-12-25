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

package org.glucosio.android.presenter;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.util.Log;
import io.realm.Realm;
import org.glucosio.android.db.DatabaseHandler;
import org.glucosio.android.db.GlucoseReading;
import org.glucosio.android.tools.ReadingToCSV;
import org.glucosio.android.view.ExportView;
import org.joda.time.DateTime;

import java.io.File;
import java.util.List;

public class ExportPresenter {

    // Storage Permissions
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static final String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    private int fromDay;
    private int fromMonth;
    private int fromYear;
    private int toDay;
    private int toMonth;
    private int toYear;

    private Activity mActivity;
    private ExportView mExportView;
    private DatabaseHandler dB;

    public ExportPresenter(Activity activity, DatabaseHandler databaseHandler) {
        mActivity = activity;
        if (activity instanceof ExportView) {
            mExportView = (ExportView) activity;
        } else {
            throw new IllegalStateException("ExportPresenter Activity must implement ExportView interface");
        }
        dB = databaseHandler;
    }

    public void onExportClicked(final boolean isExportAll) {

        if (hasStoragePermissions(mActivity)) {
            final String preferredUnit = dB.getUser(1).getPreferred_unit();
            final boolean[] isEmpty = {false};

            new AsyncTask<Void, Void, String>() {
                @Override
                protected String doInBackground(Void... params) {

                    Realm realm = dB.getNewRealmInstance();
                    final List<GlucoseReading> readings;
                    if (isExportAll) {
                        readings = dB.getGlucoseReadings(realm);
                    } else {
                        DateTime fromDate = new DateTime(fromYear, fromMonth, fromDay, 0, 0);
                        DateTime toDate = new DateTime(toYear, toMonth, toDay, 0, 0);

                        readings = dB.getGlucoseReadings(realm, fromDate.toDate(), toDate.toDate());
                    }

                    mExportView.onExportStarted(readings.size());
                    if (readings.isEmpty()) {
                        isEmpty[0] = true;
                        return null;
                    }

                    if (dirExists()) {
                        Log.i("glucosio", "Dir exists");
                        return new ReadingToCSV(mActivity, preferredUnit).createCSVFile(realm, readings);
                    } else {
                        Log.i("glucosio", "Dir NOT exists");
                        return null;
                    }
                }

                @Override
                protected void onPostExecute(String filename) {
                    super.onPostExecute(filename);
                    if (filename != null) {
                        Uri uri = FileProvider.getUriForFile(mActivity.getApplicationContext(),
                                mActivity.getApplicationContext().getPackageName() + ".provider.fileprovider", new File(filename));
                        mExportView.onExportFinish(uri);
                    } else if (isEmpty[0]) {
                        mExportView.onNoItemsToExport();
                    } else {
                        mExportView.onExportError();
                    }
                }
            }.execute();
        }
    }

    private boolean dirExists() {
        final File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/glucosio");
        return file.exists() || file.mkdirs();
    }

    private boolean hasStoragePermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        Log.i("Glucosio", "Storage permissions granted.");

        if (permission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
            return false;
        } else {
            return true;
        }
    }

    public void setFromDay(int fromDay) {
        this.fromDay = fromDay;
    }

    public void setFromMonth(int fromMonth) {
        this.fromMonth = fromMonth;
    }

    public void setFromYear(int fromYear) {
        this.fromYear = fromYear;
    }

    public void setToDay(int toDay) {
        this.toDay = toDay;
    }

    public void setToMonth(int toMonth) {
        this.toMonth = toMonth;
    }

    public void setToYear(int toYear) {
        this.toYear = toYear;
    }
}
