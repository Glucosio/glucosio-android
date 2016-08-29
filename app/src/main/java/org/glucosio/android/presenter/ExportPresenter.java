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

import org.glucosio.android.activity.MainActivity;
import org.glucosio.android.db.DatabaseHandler;
import org.glucosio.android.db.GlucoseReading;
import org.glucosio.android.tools.ReadingToCSV;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import io.realm.Realm;

public class ExportPresenter {

    // Storage Permissions
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    private int fromDay;
    private int fromMonth;
    private int fromYear;
    private int toDay;
    private int toMonth;
    private int toYear;
    private DatabaseHandler dB;
    private MainActivity activity;

    public ExportPresenter(MainActivity exportActivity, DatabaseHandler dbHandler) {
        this.activity = exportActivity;
        dB = dbHandler;
    }

    public void onExportClicked(final Boolean all) {
        final List<GlucoseReading> readings;

        if (all) {
            readings = dB.getGlucoseReadings();
        } else {
            Calendar fromDate = Calendar.getInstance();
            fromDate.set(Calendar.YEAR, fromYear);
            fromDate.set(Calendar.MONTH, fromMonth);
            fromDate.set(Calendar.DAY_OF_MONTH, fromDay);

            Calendar toDate = Calendar.getInstance();
            toDate.set(Calendar.YEAR, toYear);
            toDate.set(Calendar.MONTH, toMonth);
            toDate.set(Calendar.DAY_OF_MONTH, toDay);
            readings = dB.getGlucoseReadings(fromDate.getTime(), toDate.getTime());
        }

        if (readings.size() != 0) {
            if (hasStoragePermissions(activity)) {
                activity.showExportedSnackBar(readings.size());
                final ReadingToCSV csv = new ReadingToCSV(activity.getApplicationContext());
                final String preferredUnit = dB.getUser(1).getPreferred_unit();

                new AsyncTask<Void, Void, String>() {
                    @Override
                    protected String doInBackground(Void... params) {
                        final ArrayList<GlucoseReading> readingsToExport;
                        Realm realm = dB.getNewRealmInstance();

                        if (all) {
                            readingsToExport = dB.getGlucoseReadings(realm);
                        } else {
                            Calendar fromDate = Calendar.getInstance();
                            fromDate.set(Calendar.YEAR, fromYear);
                            fromDate.set(Calendar.MONTH, fromMonth);
                            fromDate.set(Calendar.DAY_OF_MONTH, fromDay);

                            Calendar toDate = Calendar.getInstance();
                            toDate.set(Calendar.YEAR, toYear);
                            toDate.set(Calendar.MONTH, toMonth);
                            toDate.set(Calendar.DAY_OF_MONTH, toDay);
                            readingsToExport = dB.getGlucoseReadings(realm, fromDate.getTime(), toDate.getTime());
                        }

                        if (dirExists()) {
                            Log.i("glucosio", "Dir exists");
                            return csv.createCSVFile(realm, readingsToExport, preferredUnit);
                        } else {
                            Log.i("glucosio", "Dir NOT exists");
                            return null;
                        }
                    }

                    @Override
                    protected void onPostExecute(String filename) {
                        super.onPostExecute(filename);
                        if (filename != null) {
                            Uri uri = FileProvider.getUriForFile(activity.getApplicationContext(),
                                    activity.getApplicationContext().getPackageName() + ".provider.fileprovider", new File(filename));
                            activity.showShareDialog(uri);
                        } else {
                            //TODO: Show error SnackBar
                            activity.showExportError();
                        }
                    }
                }.execute();
            }
        } else {
            activity.showNoReadingsSnackBar();
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

    public int getFromDay() {
        return fromDay;
    }

    public void setFromDay(int fromDay) {
        this.fromDay = fromDay;
    }

    public int getFromMonth() {
        return fromMonth;
    }

    public void setFromMonth(int fromMonth) {
        this.fromMonth = fromMonth;
    }

    public int getFromYear() {
        return fromYear;
    }

    public void setFromYear(int fromYear) {
        this.fromYear = fromYear;
    }

    public int getToDay() {
        return toDay;
    }

    public void setToDay(int toDay) {
        this.toDay = toDay;
    }

    public int getToMonth() {
        return toMonth;
    }

    public void setToMonth(int toMonth) {
        this.toMonth = toMonth;
    }

    public int getToYear() {
        return toYear;
    }

    public void setToYear(int toYear) {
        this.toYear = toYear;
    }
}
