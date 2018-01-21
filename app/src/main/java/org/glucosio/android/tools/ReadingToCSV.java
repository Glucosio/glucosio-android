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
import android.support.annotation.Nullable;
import android.util.Log;
import org.glucosio.android.Constants;
import org.glucosio.android.R;
import org.glucosio.android.db.GlucoseReading;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.NumberFormat;
import java.util.List;

public final class ReadingToCSV {

    private static final char[] SYMBOLS_TO_ESCAPE = new char[]{',', '"', '\n', '\r'};

    private final Context context;
    private final String userMeasurements;
    private final FormatDateTime dateTool;

    private final NumberFormat formatter = NumberFormatUtils.createDefaultNumberFormat();

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
                        formatter.format(reading.getReading()),
                        Constants.Units.MG_DL,
                        valueOrEmptyString(reading.getReading_type()),
                        valueOrEmptyString(reading.getNotes())
                );

            }
        } else {
            for (GlucoseReading reading : readings) {
                writeLine(osw,
                        this.dateTool.convertRawDate(reading.getCreated()),
                        this.dateTool.convertRawTime(reading.getCreated()),
                        formatter.format(GlucosioConverter.glucoseToMmolL(reading.getReading())),
                        Constants.Units.MMOL_L,
                        valueOrEmptyString(reading.getReading_type()),
                        valueOrEmptyString(reading.getNotes())
                );
            }

        }
        osw.flush();
        Log.i("Glucosio", "Done exporting readings");
    }

    @NonNull
    private String valueOrEmptyString(@Nullable String value) {
        return value == null ? "" : value;
    }

    private void writeLine(@NonNull OutputStreamWriter osw, @NonNull String... values) throws IOException {
        for (int i = 0; i < values.length; i++) {
            osw.append(escapeCSV(values[i]));
            osw.append(i == values.length - 1 ? '\n' : ',');
        }
    }

    private String escapeCSV(String value) {
        if (containsSymbolsToEscape(value)) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        } else {
            return value;
        }
    }

    private boolean containsSymbolsToEscape(String value) {
        for (char c : SYMBOLS_TO_ESCAPE) {
            if (value.indexOf(c) > -1) {
                return true;
            }
        }

        return false;
    }
}
