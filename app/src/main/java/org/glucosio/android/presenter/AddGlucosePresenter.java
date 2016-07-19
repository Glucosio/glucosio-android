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
    private DatabaseHandler dB;
    private AddGlucoseActivity activity;
    private ReadingTools rTools;
    private GlucosioConverter converter;

    public AddGlucosePresenter(AddGlucoseActivity addGlucoseActivity) {
        this.activity = addGlucoseActivity;
        dB = new DatabaseHandler(addGlucoseActivity.getApplicationContext());
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
        rTools = new ReadingTools();
        return rTools.hourToSpinnerType(hour);
    }

    public void dialogOnAddButtonPressed(String time, String date, String reading, String type, String notes) {
        if (validateDate(date) && validateTime(time) && validateGlucose(reading) && validateType(type)) {
            Date finalDateTime = getReadingTime();
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

    public void dialogOnAddButtonPressed(String time, String date, String reading, String type, String notes, long oldId) {
        if (validateDate(date) && validateTime(time) && validateGlucose(reading) && validateType(type)) {
            Date finalDateTime = getReadingTime();
            boolean isReadingAdded;
            if ("mg/dL".equals(getUnitMeasuerement())) {
                int finalReading = Integer.parseInt(reading);
                GlucoseReading gReading = new GlucoseReading(finalReading, type, finalDateTime, notes);
                isReadingAdded = dB.editGlucoseReading(oldId, gReading);
            } else {
                converter = new GlucosioConverter();
                int convertedReading = converter.glucoseToMgDl(Double.parseDouble(reading));
                GlucoseReading gReading = new GlucoseReading(convertedReading, type, finalDateTime, notes);

                isReadingAdded = dB.editGlucoseReading(oldId, gReading);
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

    public int retriveSpinnerID (String measuredTypeText, List<String> measuredTypelist) {
        int mesuredId = 0;
        boolean isCustomType = true;
        for (String measuredType : measuredTypelist) {
            if (measuredType.equals(measuredTypeText)) {
                isCustomType = false;
                break;
            }
            mesuredId++;
        }
        // if it's a custom type, type = other(12)
        return isCustomType ? 11 : mesuredId;
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
                    if (readingValue > 19 && readingValue < 601) {
                        //TODO: Add custom ranges
                        // TODO: Convert range in mmol/L
                        return true;
                    } else {
                        return false;
                    }
                } catch (Exception e) {
                    FirebaseCrash.log("Exception during reading validation");
                    FirebaseCrash.report(e);
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

    private boolean validateType(String type) {
        return validateText(type);
    }
}
