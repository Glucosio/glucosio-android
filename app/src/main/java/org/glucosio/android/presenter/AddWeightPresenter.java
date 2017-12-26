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

import org.glucosio.android.activity.AddWeightActivity;
import org.glucosio.android.db.DatabaseHandler;
import org.glucosio.android.db.WeightReading;
import org.glucosio.android.tools.GlucosioConverter;
import org.glucosio.android.tools.ReadingTools;

import java.util.Date;

public class AddWeightPresenter extends AddReadingPresenter {
    private DatabaseHandler dB;
    private AddWeightActivity activity;


    public AddWeightPresenter(AddWeightActivity addWeightActivity) {
        this.activity = addWeightActivity;
        dB = new DatabaseHandler(addWeightActivity.getApplicationContext());
    }

    public void dialogOnAddButtonPressed(String time, String date, String reading) {
        if (validateDate(date) && validateTime(time) && validateWeight(reading)) {

            WeightReading wReading = generateWeightReading(reading);
            dB.addWeightReading(wReading);

            activity.finishActivity();
        } else {
            activity.showErrorMessage();
        }
    }

    public void dialogOnAddButtonPressed(String time, String date, String reading, long oldId) {
        if (validateDate(date) && validateTime(time) && validateWeight(reading)) {

            WeightReading wReading = generateWeightReading(reading);
            dB.editWeightReading(oldId, wReading);

            activity.finishActivity();
        } else {
            activity.showErrorMessage();
        }
    }

    private WeightReading generateWeightReading(String reading) {
        Date finalDateTime = getReadingTime();

        double finalReading;

        if ("kilograms".equals(getWeightUnitMeasuerement())) {
            finalReading = ReadingTools.safeParseDouble(reading);
        } else {
            finalReading = GlucosioConverter.lbToKg(ReadingTools.safeParseDouble(reading));
        }

        return new WeightReading(finalReading, finalDateTime);
    }

    // Getters and Setters

    public String getWeightUnitMeasuerement() {
        return dB.getUser(1).getPreferred_unit_weight();
    }

    public WeightReading getWeightReadingById(Long id) {
        return dB.getWeightReadingById(id);
    }

    // Validator
    private boolean validateWeight(String reading) {
        return validateText(reading);
    }

}
