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

import android.net.Uri;

import org.glucosio.android.activity.MainActivity;
import org.glucosio.android.db.DatabaseHandler;
import org.glucosio.android.db.GlucoseReading;
import org.glucosio.android.tools.ReadingToCSV;

import java.util.ArrayList;
import java.util.Calendar;

public class ExportPresenter {

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

    public void onExportClicked(Boolean all) {
        ArrayList<GlucoseReading> readings;

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
            activity.showExportedSnackBar(readings.size());
            ReadingToCSV csv = new ReadingToCSV(activity.getApplicationContext());
            Uri csvUri = csv.createCSV(readings, dB.getUser(1).getPreferred_unit());
            activity.showShareDialog(csvUri);
        } else {
            activity.showNoReadingsSnackBar();
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
