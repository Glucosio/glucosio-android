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

import android.content.Context;
import android.content.res.Resources;
import android.os.Environment;
import android.util.Log;

import org.glucosio.android.R;
import org.glucosio.android.db.GlucoseReading;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.List;

import io.realm.Realm;

public final class ReadingToCSV {

    private final Context context;
    private final String um;
    private final FormatDateTime dateTool;

    public ReadingToCSV(Context context, String um) {
        this.context = context;
        this.um = um;

        this.dateTool = new FormatDateTime(context);
    }

    public String createCSVFile(Realm realm, final List<GlucoseReading> readings) {
        try {
            File file = null;
            final File sd = Environment.getExternalStorageDirectory();
            if (sd.canWrite()) {
                file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/glucosio", "glucosio_export_" + System.currentTimeMillis() / 1000 + ".csv");

                FileOutputStream fileOutputStream = null;
                OutputStreamWriter osw = null;

                try {
                    fileOutputStream = new FileOutputStream(file);
                    osw = new OutputStreamWriter(fileOutputStream);

                    // CSV Structure
                    // Date | Time | Concentration | Unit | Measured | Notes
                    final Resources resources = this.context.getResources();
                    writeLine(osw,
                            resources.getString(R.string.dialog_add_date),
                            resources.getString(R.string.dialog_add_time),
                            resources.getString(R.string.dialog_add_concentration),
                            resources.getString(R.string.helloactivity_spinner_preferred_glucose_unit),
                            resources.getString(R.string.dialog_add_measured),
                            resources.getString(R.string.dialog_add_notes)
                    );

                    // Concentration | Measured | Date | Time | Notes | Unit of Measurement
                    if ("mg/dL".equals(um)) {
                        for (int i = 0; i < readings.size(); i++) {
                            GlucoseReading reading = readings.get(i);

                            writeLine(osw,
                                    this.dateTool.convertRawDate(reading.getCreated()),
                                    this.dateTool.convertRawTime(reading.getCreated()),
                                    String.valueOf(reading.getReading()),
                                    "mg/dL",
                                    String.valueOf(reading.getReading_type()),
                                    reading.getNotes()
                            );

                        }
                    } else {
                        for (int i = 0; i < readings.size(); i++) {
                            GlucoseReading reading = readings.get(i);

                            writeLine(osw,
                                    this.dateTool.convertRawDate(reading.getCreated()),
                                    this.dateTool.convertRawTime(reading.getCreated()),
                                    GlucosioConverter.glucoseToMmolL(reading.getReading()) + "",
                                    "mmol/L",
                                    reading.getReading_type() + "",
                                    reading.getNotes()
                            );
                        }

                    }
                    osw.flush();
                } catch (Exception e) {
                    Log.e("Glucosio", "Error exporting readings", e);
                } finally {
                    if (osw != null) osw.close();
                    if (fileOutputStream != null) fileOutputStream.close();
                }
                Log.i("Glucosio", "Done exporting readings");
            }
            realm.close();
            return file == null ? null : file.getPath();
        } catch (Exception e) {
            realm.close();
            e.printStackTrace();
            return null;
        }
    }

    private void writeLine(OutputStreamWriter osw, String... values) throws IOException {
        for (int i = 0; i < values.length; i++) {
            osw.append(values[i]);
            osw.append(i == values.length - 1 ? '\n' : ',');
        }
    }
}
