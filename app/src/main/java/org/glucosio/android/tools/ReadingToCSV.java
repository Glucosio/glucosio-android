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
import android.support.annotation.NonNull;
import android.util.Log;
import org.glucosio.android.Constants;
import org.glucosio.android.R;
import org.glucosio.android.db.GlucoseReading;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.List;

public final class ReadingToCSV {

    private final Context context;
    private final String userMeasurements;
    private final FormatDateTime dateTool;

    public ReadingToCSV(@NonNull Context context, @NonNull String userMeasurements) {
        this.context = context;
        this.userMeasurements = userMeasurements;
        this.dateTool = new FormatDateTime(context);
    }

    public void createCSVFile(@NonNull List<GlucoseReading> readings, @NonNull OutputStreamWriter osw) throws IOException {
        // CSV Structure
        // Date | Time | Concentration | Unit | Measured | Notes
        Resources resources = context.getResources();
        writeLine(osw,
                resources.getString(R.string.dialog_add_date),
                resources.getString(R.string.dialog_add_time),
                resources.getString(R.string.dialog_add_concentration),
                resources.getString(R.string.helloactivity_spinner_preferred_glucose_unit),
                resources.getString(R.string.dialog_add_measured),
                resources.getString(R.string.dialog_add_notes)
        );

        // Concentration | Measured | Date | Time | Notes | Unit of Measurement
        if (Constants.Units.MG_DL.equals(userMeasurements)) {
            for (GlucoseReading reading : readings) {
                writeLine(osw,
                        dateTool.convertRawDate(reading.getCreated()),
                        dateTool.convertRawTime(reading.getCreated()),
                        String.valueOf(reading.getReading()),
                        Constants.Units.MG_DL,
                        String.valueOf(reading.getReading_type()),
                        reading.getNotes()
                );

            }
        } else {
            for (GlucoseReading reading : readings) {
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
        Log.i("Glucosio", "Done exporting readings");
    }

    private void writeLine(@NonNull OutputStreamWriter osw, @NonNull String... values) throws IOException {
        for (int i = 0; i < values.length; i++) {
            osw.append(values[i]);
            osw.append(i == values.length - 1 ? '\n' : ',');
        }
    }
}
