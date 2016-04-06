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

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import org.glucosio.android.activity.AddGlucoseActivity;
import org.glucosio.android.db.DatabaseHandler;
import org.glucosio.android.db.GlucoseReading;
import org.glucosio.android.tools.GlucosioConverter;
import org.glucosio.android.tools.ReadingTools;
import org.glucosio.android.tools.SplitDateTime;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class AddGlucosePresenter {
    private DatabaseHandler dB;
    private AddGlucoseActivity activity;
    private ReadingTools rTools;
    private GlucosioConverter converter;
    private String readingYear;
    private String readingMonth;
    private String readingDay;
    private String readingHour;
    private String readingMinute;


    public AddGlucosePresenter(AddGlucoseActivity addGlucoseActivity) {
        this.activity = addGlucoseActivity;
        dB = new DatabaseHandler(addGlucoseActivity.getApplicationContext());
    }

    public void updateSpinnerTypeTime() {
        getCurrentTime();
        activity.updateSpinnerTypeTime(timeToSpinnerType());
    }

    public void getCurrentTime() {
        DateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Date formatted = Calendar.getInstance().getTime();

        SplitDateTime addSplitDateTime = new SplitDateTime(formatted, inputFormat);

        this.readingYear = addSplitDateTime.getYear();
        this.readingMonth = addSplitDateTime.getMonth();
        this.readingDay = addSplitDateTime.getDay();
        this.readingHour = addSplitDateTime.getHour();
        this.readingMinute = addSplitDateTime.getMinute();
    }

    public int timeToSpinnerType() {
        DateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Date formatted = Calendar.getInstance().getTime();

        SplitDateTime addSplitDateTime = new SplitDateTime(formatted, inputFormat);
        int hour = Integer.parseInt(addSplitDateTime.getHour());

        return hourToSpinnerType(hour);
    }

    public int hourToSpinnerType(int hour) {
        rTools = new ReadingTools();
        return rTools.hourToSpinnerType(hour);
    }

    public void dialogOnAddButtonPressed(String time, String date, String reading, String type, String notes) {
        if (validateDate(date) && validateTime(time) && validateReading(reading) && validateType(type)) {
            Calendar cal = Calendar.getInstance();
            cal.set(Integer.parseInt(readingYear), Integer.parseInt(readingMonth) - 1, Integer.parseInt(readingDay), Integer.parseInt(readingHour), Integer.parseInt(readingMinute));
            Date finalDateTime = cal.getTime();
            boolean isReadingAdded;
            if ("mg/dL".equals(getUnitMeasuerement())) {
                int finalReading = Integer.parseInt(reading);
                GlucoseReading gReading = new GlucoseReading(finalReading, type, finalDateTime, notes);
                isReadingAdded = dB.addGlucoseReading(gReading);
            } else {
                converter = new GlucosioConverter();
                int convertedReading = converter.glucoseToMgDl(Double.parseDouble(reading));
                GlucoseReading gReading = new GlucoseReading(convertedReading, type, finalDateTime, notes);

                isReadingAdded = dB.addGlucoseReading(gReading);
            }
            if (!isReadingAdded) {
                activity.showDuplicateErrorMessage();
            } else {
                activity.finishActivity();
            }
        } else {
            activity.showErrorMessage();
        }
    }

    private boolean validateTime(String time) {
        return !"".equals(time);
    }

    private boolean validateDate(String date) {
        return !"".equals(date);
    }

    private boolean validateType(String type) {
        return !"".equals(type);
    }

    private boolean validateReading(String reading) {
        if (!reading.equals("")) {
            if ("mg/dL".equals(getUnitMeasuerement())) {
                // We store data in db in mg/dl
                try {
                    Integer readingValue = Integer.parseInt(reading);
                    if (readingValue > 19 && readingValue < 601) {
                        //TODO: Add custom ranges
                        // TODO: Convert range in mmol/L
                        return true;
                    } else {
                        return false;
                    }
                } catch (Exception e) {
                    return false;
                }
            } else {
/*            try {
                //TODO: Add custom ranges for mmol/L
                Integer readingValue = Integer.parseInt(reading);
                if (readingValue > 19 && readingValue < 601) {
                    // TODO: Convert range in mmol/L
                    return true;
                } else {
                    return false;
                }
            } catch (Exception e) {
                return false;
            }*/
                // IT return always true: we don't have ranges yet.
                return true;
            }
        } else {
            return false;
        }
    }

    // Getters and Setters

    public boolean isFreeStyleLibreEnabled() {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(activity.getApplicationContext());
        return sharedPref.getBoolean("pref_freestyle_libre", false);
    }

    public String getUnitMeasuerement() {
        return dB.getUser(1).getPreferred_unit();
    }

    public String getReadingYear() {
        return readingYear;
    }

    public void setReadingYear(String readingYear) {
        this.readingYear = readingYear;
    }

    public String getReadingMonth() {
        return readingMonth;
    }

    public void setReadingMonth(String readingMonth) {
        this.readingMonth = readingMonth;
    }

    public String getReadingDay() {
        return readingDay;
    }

    public void setReadingDay(String readingDay) {
        this.readingDay = readingDay;
    }

    public void setReadingHour(String readingHour) {
        this.readingHour = readingHour;
    }

    public void setReadingMinute(String readingMinute) {
        this.readingMinute = readingMinute;
    }

}
