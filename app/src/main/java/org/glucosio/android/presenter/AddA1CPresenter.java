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

import org.glucosio.android.activity.AddA1CActivity;
import org.glucosio.android.db.DatabaseHandler;
import org.glucosio.android.db.HB1ACReading;
import org.glucosio.android.tools.GlucosioConverter;

import java.util.Date;

public class AddA1CPresenter extends AddReadingPresenter {
    private DatabaseHandler dB;
    private AddA1CActivity activity;

    public AddA1CPresenter(AddA1CActivity addA1CActivity) {
        this.activity = addA1CActivity;
        dB = new DatabaseHandler(addA1CActivity.getApplicationContext());
    }

    public void dialogOnAddButtonPressed(String time, String date, String reading) {
        if (validateDate(date) && validateTime(time) && validateA1C(reading)) {

            HB1ACReading hReading = generateHB1ACReading(reading);
            dB.addHB1ACReading(hReading);

            activity.finishActivity();
        } else {
            activity.showErrorMessage();
        }
    }

    public void dialogOnAddButtonPressed(String time, String date, String reading, long oldId) {
        if (validateDate(date) && validateTime(time) && validateText(reading)) {

            HB1ACReading hReading = generateHB1ACReading(reading);
            dB.editHB1ACReading(oldId, hReading);

            activity.finishActivity();
        } else {
            activity.showErrorMessage();
        }
    }

    private HB1ACReading generateHB1ACReading(String reading) {
        Date finalDateTime = getReadingTime();

        double finalReading;
        if ("percentage".equals(getA1CUnitMeasuerement())) {
            finalReading = Double.parseDouble(reading);
        } else {
            finalReading = GlucosioConverter.a1cIfccToNgsp(Double.parseDouble(reading));
        }

        return new HB1ACReading(finalReading, finalDateTime);
    }

    public String getA1CUnitMeasuerement() {
        return dB.getUser(1).getPreferred_unit_a1c();
    }

    public HB1ACReading getHB1ACReadingById(Long id) {
        return dB.getHB1ACReadingById(id);
    }

    // Validator
    private boolean validateA1C(String reading) {
        return validateText(reading);
    }
}
