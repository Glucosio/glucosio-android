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
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.util.Log;
import io.realm.Realm;
import org.glucosio.android.db.DatabaseHandler;
import org.glucosio.android.db.GlucoseReading;
import org.glucosio.android.tools.ReadingToCSV;
import org.glucosio.android.view.ExportView;
import org.joda.time.DateTime;

import java.io.Closeable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.List;

public class ExportPresenter {
    private static final String EMPTY = "Empty Readings while exporting";

    private DateTime fromDate;
    private DateTime toDate;

    private final ExportView view;
    private final Context context;
    private final DatabaseHandler databaseHandler;

    public ExportPresenter(ExportView exportView, Context context, DatabaseHandler databaseHandler) {
        view = exportView;
        this.context = context;
        this.databaseHandler = databaseHandler;
    }

    public void onExportClicked(final boolean isExportAll) {

        final String preferredUnit = databaseHandler.getUser(1).getPreferred_unit();

        new AsyncTask<Void, Integer, String>() {
            @Override
            protected String doInBackground(Void... params) {

                Realm realm = databaseHandler.getNewRealmInstance();
                final List<GlucoseReading> readings;
                if (isExportAll) {
                    readings = databaseHandler.getGlucoseReadings(realm);
                } else {
                    readings = databaseHandler.getGlucoseReadings(realm, fromDate.toDate(), toDate.toDate());
                }

                if (readings.isEmpty()) {
                    realm.close();
                    return EMPTY;
                }

                if (dirExistsAndCanWrite()) {
                    Log.i("glucosio", "Dir exists");
                    String csvFile = exportReadings(readings, preferredUnit);
                    realm.close();
                    return csvFile;
                } else {
                    Log.i("glucosio", "Dir NOT exists");
                    realm.close();
                    return null;
                }
            }

            @Override
            protected void onProgressUpdate(Integer... values) {
                super.onProgressUpdate(values);
                view.onExportStarted(values[0]);
            }

            @Override
            protected void onPostExecute(String filename) {
                super.onPostExecute(filename);
                if (EMPTY.equals(filename)) {
                    view.onNoItemsToExport();
                } else if (filename == null) {
                    view.onExportError();
                } else {
                    Context applicationContext = context.getApplicationContext();
                    Uri uri = FileProvider.getUriForFile(applicationContext,
                            applicationContext.getPackageName() + ".provider.fileprovider", new File(filename));
                    view.onExportFinish(uri);
                }
            }
        }.execute();
    }

    @Nullable
    private String exportReadings(List<GlucoseReading> readings, String preferredUnit) {
        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/glucosio", "glucosio_export_" + System.currentTimeMillis() / 1000 + ".csv");

        FileOutputStream fileOutputStream = null;
        OutputStreamWriter osw = null;

        try {
            fileOutputStream = new FileOutputStream(file);
            osw = new OutputStreamWriter(fileOutputStream);

            new ReadingToCSV(context, preferredUnit).createCSVFile(readings, osw);

            return file.getPath();
        } catch (IOException e) {
            Log.e("ExportPresenter", "Exporting CSV", e);
            return null;
        } finally {
            closeSafe(osw);
            closeSafe(fileOutputStream);
        }
    }

    private void closeSafe(@Nullable Closeable closeable) {
        if (closeable == null) return;
        try {
            closeable.close();
        } catch (IOException ignored) {
            //ignored
        }
    }

    private boolean dirExistsAndCanWrite() {
        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/glucosio");
        return (file.exists() || file.mkdirs()) && file.canWrite();
    }

    public void setTo(int year, int monthOfYear, int dayOfMonth) {
        toDate = new DateTime(year, monthOfYear, dayOfMonth, 0, 0);
    }

    public void setFrom(int year, int monthOfYear, int dayOfMonth) {
        fromDate = new DateTime(year, monthOfYear, dayOfMonth, 0, 0);
    }
}
