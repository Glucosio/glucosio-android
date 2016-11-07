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

import com.google.firebase.crash.FirebaseCrash;

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
import java.util.List;

public class AddGlucosePresenter extends AddReadingPresenter {
    private static final int UNKNOWN_ID = -1;
    private DatabaseHandler dB;
    private AddGlucoseActivity activity;
    private ReadingTools rTools;

    public AddGlucosePresenter(AddGlucoseActivity addGlucoseActivity) {
        this.activity = addGlucoseActivity;
        dB = new DatabaseHandler(addGlucoseActivity.getApplicationContext());
        rTools = new ReadingTools();
    }

    public void updateSpinnerTypeTime() {
        setReadingTimeNow();
        activity.updateSpinnerTypeTime(timeToSpinnerType());
    }

    private int timeToSpinnerType() {
        DateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Date formatted = Calendar.getInstance().getTime();

        SplitDateTime addSplitDateTime = new SplitDateTime(formatted, inputFormat);
        int hour = Integer.parseInt(addSplitDateTime.getHour());

        return hourToSpinnerType(hour);
    }

    public int hourToSpinnerType(int hour) {
        return rTools.hourToSpinnerType(hour);
    }

    public void dialogOnAddButtonPressed(String time, String date, String reading, String type, String notes) {
        dialogOnAddButtonPressed(time, date, reading, type, notes, UNKNOWN_ID);
    }

    public void dialogOnAddButtonPressed(String time, String date, String reading, String type, String notes, long oldId) {
        if (validateDate(date) && validateTime(time) && validateGlucose(reading) && validateType(type)) {
            Date finalDateTime = getReadingTime();
            Number number = ReadingTools.parseReading(reading);
            if (number == null) {
                activity.showErrorMessage();
            } else {
                boolean isReadingAdded = createReading(type, notes, oldId, finalDateTime, number);
                if (!isReadingAdded) {
                    activity.showDuplicateErrorMessage();
                } else {
                    activity.finishActivity();
                }
            }
        } else {
            activity.showErrorMessage();
        }
    }

    private boolean createReading(String type, String notes, long oldId, Date finalDateTime, Number number) {
        boolean isReadingAdded;
        int readingValue;
        if ("mg/dL".equals(getUnitMeasuerement())) {
            readingValue = number.intValue();
        } else {
            readingValue = GlucosioConverter.glucoseToMgDl(number.doubleValue());
        }
        GlucoseReading gReading = new GlucoseReading(readingValue, type, finalDateTime, notes);
        if (oldId == UNKNOWN_ID) {
            isReadingAdded = dB.addGlucoseReading(gReading);
        } else {
            isReadingAdded = dB.editGlucoseReading(oldId, gReading);
        }
        return isReadingAdded;
    }

    public Integer retrieveSpinnerID(String measuredTypeText, List<String> measuredTypelist) {
        int measuredId = 0;
        boolean isFound = false;
        for (String measuredType : measuredTypelist) {
            if (measuredType.equals(measuredTypeText)) {
                isFound = true;
                break;
            }
            measuredId++;
        }
        // if type is not found, it's return null
        return isFound ? measuredId : null;
    }

    public String getUnitMeasuerement() {
        return dB.getUser(1).getPreferred_unit();
    }

    public GlucoseReading getGlucoseReadingById(Long id) {
        return dB.getGlucoseReadingById(id);
    }

    // Validator
    private boolean validateGlucose(String reading) {
        if (validateText(reading)) {
            if ("mg/dL".equals(getUnitMeasuerement())) {
                // We store data in db in mg/dl
                try {
                    Integer readingValue = Integer.parseInt(reading);
                    //TODO: Add custom ranges
                    return readingValue > 19 && readingValue < 601;
                } catch (Exception e) {
                    FirebaseCrash.log("Exception during reading validation");
                    FirebaseCrash.report(e);
                    return false;
                }
            } else if ("mmol/L".equals(getUnitMeasuerement())) {
                // Convert mmol/L Unit
                try {
                    Double readingValue = Double.parseDouble(reading);
                    return readingValue > 1.0545 && readingValue < 33.3555;
                } catch (Exception e) {
                    FirebaseCrash.log("Exception during reading validation");
                    FirebaseCrash.report(e);
                    return false;
                }
            } else {
                // IT return always true: we don't have ranges yet.
                return true;
            }
        } else {
            return false;
        }
    }

    public boolean isFreeStyleLibreEnabled() {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(activity.getApplicationContext());
        return sharedPref.getBoolean("pref_freestyle_libre", false);
    }

    private boolean validateType(String type) {
        return validateText(type);
    }
}
