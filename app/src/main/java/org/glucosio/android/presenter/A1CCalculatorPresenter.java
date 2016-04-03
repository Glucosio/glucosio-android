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

import org.glucosio.android.activity.A1Calculator;
import org.glucosio.android.db.DatabaseHandler;
import org.glucosio.android.db.HB1ACReading;
import org.glucosio.android.tools.GlucosioConverter;

import java.util.Calendar;

public class A1CCalculatorPresenter {
    private DatabaseHandler dB;
    private A1Calculator activity;


    public A1CCalculatorPresenter(A1Calculator a1Calculator) {
        this.activity = a1Calculator;
        dB = new DatabaseHandler(a1Calculator.getApplicationContext());
    }

    public double calculateA1C(String glucose) {
        GlucosioConverter converter = new GlucosioConverter();
        if (dB.getUser(1).getPreferred_unit().equals("mg/dL")) {
            return converter.glucoseToA1C(Double.parseDouble(glucose));
        } else {
            return converter.glucoseToA1C(converter.glucoseToMgDl(Double.parseDouble(glucose)));
        }
    }

    public void checkUnit() {
        if (!dB.getUser(1).getPreferred_unit().equals("mg/dL")) {
            activity.setMmol();
        }
    }

    public void saveA1C(double a1c) {
        HB1ACReading a1cReading = new HB1ACReading(a1c, Calendar.getInstance().getTime());
        dB.addHB1ACReading(a1cReading);
        activity.finish();
    }

}
