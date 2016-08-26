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
import android.os.Environment;
import android.util.Log;

import org.glucosio.android.R;
import org.glucosio.android.db.GlucoseReading;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

import io.realm.Realm;

public class ReadingToCSV {

    private Context context;

    public ReadingToCSV(Context mContext) {
        this.context = mContext;
    }


    public String createCSVFile(Realm realm, final ArrayList<GlucoseReading> readings, String um) {
        try {
            final File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/glucosio", "glucosio_export_ " + System.currentTimeMillis()/1000 + ".csv");
            final File sd = Environment.getExternalStorageDirectory();
            if (sd.canWrite()) {
                FileOutputStream fileOutputStream = new FileOutputStream(file);
                OutputStreamWriter osw = new OutputStreamWriter(fileOutputStream);

                // Export Structure
                // Concentration | Measured | Date | Time | Notes | Unit of Measurement

                osw.append(context.getResources().getString(R.string.dialog_add_concentration));
                osw.append(',');

                osw.append(context.getResources().getString(R.string.dialog_add_measured));
                osw.append(',');

                osw.append(context.getResources().getString(R.string.dialog_add_date));
                osw.append(',');

                osw.append(context.getResources().getString(R.string.dialog_add_time));
                osw.append(',');

                osw.append(context.getResources().getString(R.string.dialog_add_notes));
                osw.append(',');

                osw.append(context.getResources().getString(R.string.helloactivity_spinner_preferred_glucose_unit));
                osw.append('\n');

                FormatDateTime dateTool = new FormatDateTime(context);

                // Concentration | Measured | Date | Time | Notes | Unit of Measurement
                if ("mg/dL".equals(um)) {
                    for (int i = 0; i < readings.size(); i++) {
                        GlucoseReading reading = readings.get(i);

                        osw.append(reading.getReading() + "");
                        osw.append(',');

                        osw.append(reading.getReading_type() + "");
                        osw.append(',');

                        osw.append(dateTool.convertRawDate(reading.getCreated() + ""));
                        osw.append(',');

                        osw.append(dateTool.convertRawTime(reading.getCreated() + ""));
                        osw.append(',');

                        osw.append(reading.getNotes());
                        osw.append(',');

                        osw.append("mg/dL");
                        osw.append('\n');
                    }
                } else {
                    GlucosioConverter converter = new GlucosioConverter();

                    for (int i = 0; i < readings.size(); i++) {
                        GlucoseReading reading = readings.get(i);

                        osw.append(converter.glucoseToMmolL(reading.getReading()) + "");
                        osw.append(',');

                        osw.append(reading.getReading_type() + "");
                        osw.append(',');

                        osw.append(dateTool.convertRawDate(reading.getCreated() + ""));
                        osw.append(',');

                        osw.append(dateTool.convertRawTime(reading.getCreated() + ""));
                        osw.append(',');

                        osw.append(reading.getNotes());
                        osw.append(',');

                        osw.append("mmol/L");
                        osw.append('\n');
                    }
                }

                osw.flush();
                osw.close();
                Log.i("Glucosio", "Done exporting readings");
            }
            realm.close();
            return file.getPath();
        } catch (Exception e) {
            realm.close();
            e.printStackTrace();
            return null;
        }
    }
}
