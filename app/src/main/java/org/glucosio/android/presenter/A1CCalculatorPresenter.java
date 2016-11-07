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

import android.support.annotation.NonNull;

import org.glucosio.android.activity.A1cCalculatorActivity;
import org.glucosio.android.db.DatabaseHandler;
import org.glucosio.android.db.HB1ACReading;
import org.glucosio.android.db.User;
import org.glucosio.android.tools.GlucosioConverter;

import java.util.Date;

public class A1CCalculatorPresenter {
    private final DatabaseHandler dbHandler;
    private final A1cCalculatorActivity activity;

    public A1CCalculatorPresenter(@NonNull final A1cCalculatorActivity activity,
                                  @NonNull final DatabaseHandler dbHandler) {
        this.activity = activity;
        this.dbHandler = dbHandler;
    }

    public double calculateA1C(String glucose) {
        if (isInvalidDouble(glucose)) {
            return 0;
        }

        double convertedA1C;
        User user = dbHandler.getUser(1);

        if ("mg/dL".equals(user.getPreferred_unit())) {
            convertedA1C = GlucosioConverter.glucoseToA1C(Double.parseDouble(glucose));
        } else {
            convertedA1C = GlucosioConverter.glucoseToA1C(GlucosioConverter.glucoseToMgDl(Double.parseDouble(glucose)));
        }
        if ("percentage".equals(user.getPreferred_unit_a1c())) {
            return convertedA1C;
        } else {
            return GlucosioConverter.a1cNgspToIfcc(convertedA1C);
        }
    }

    private boolean isInvalidDouble(String value) {
        return value == null || value.length() == 0 || (value.length() == 1 && !Character.isDigit(value.charAt(0)));
    }

    public void checkGlucoseUnit() {
        if (!dbHandler.getUser(1).getPreferred_unit().equals("mg/dL")) {
            activity.setMmol();
        }
    }

    public void saveA1C(double a1c) {
        User user = dbHandler.getUser(1);
        double finalA1c = a1c;
        if (!"percentage".equals(user.getPreferred_unit_a1c())) {
            finalA1c = GlucosioConverter.a1cIfccToNgsp(a1c);
        }

        HB1ACReading a1cReading = new HB1ACReading(finalA1c, new Date());
        dbHandler.addHB1ACReading(a1cReading);
        activity.finish();
    }

    public String getA1cUnit() {
        return dbHandler.getUser(1).getPreferred_unit_a1c();
    }

}
