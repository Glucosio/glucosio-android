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

import org.glucosio.android.activity.AddKetoneActivity;
import org.glucosio.android.db.DatabaseHandler;
import org.glucosio.android.db.KetoneReading;

import java.util.Date;

public class AddKetonePresenter extends AddReadingPresenter {
    private DatabaseHandler dB;
    private AddKetoneActivity activity;

    public AddKetonePresenter(AddKetoneActivity addKetoneActivity) {
        this.activity = addKetoneActivity;
        dB = new DatabaseHandler(addKetoneActivity.getApplicationContext());
    }

    public void dialogOnAddButtonPressed(String time, String date, String reading) {
        if (validateDate(date) && validateTime(time) && validateKetone(reading)) {

            KetoneReading kReading = generateKetoneReading(reading);
            dB.addKetoneReading(kReading);

            activity.finishActivity();
        } else {
            activity.showErrorMessage();
        }
    }

    public void dialogOnAddButtonPressed(String time, String date, String reading, long oldId) {
        if (validateDate(date) && validateTime(time) && validateText(reading)) {

            KetoneReading kReading = generateKetoneReading(reading);
            dB.editKetoneReading(oldId, kReading);

            activity.finishActivity();
        } else {
            activity.showErrorMessage();
        }
    }

    private KetoneReading generateKetoneReading(String reading) {
        Date finalDateTime = getReadingTime();
        double finalReading = Double.parseDouble(reading);
        return new KetoneReading(finalReading, finalDateTime);
    }

    public String getUnitMeasuerement() {
        return dB.getUser(1).getPreferred_unit();
    }

    public KetoneReading getKetoneReadingById(Long id) {
        return dB.getKetoneReadingById(id);
    }

    // Validator
    private boolean validateKetone(String reading) {
        return validateText(reading);
    }
}
