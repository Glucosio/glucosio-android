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

import android.util.Log;

import org.glucosio.android.activity.MainActivity;
import org.glucosio.android.db.DatabaseHandler;
import org.glucosio.android.tools.GlucoseConverter;
import org.glucosio.android.tools.ReadingTools;

public class MainPresenter {

    private MainActivity mainActivity;

    private DatabaseHandler dB;
    private ReadingTools rTools;
    private GlucoseConverter converter;

    private String readingYear;
    private String readingMonth;
    private String readingDay;
    private String readingHour;
    private String readingMinute;

    public MainPresenter(MainActivity mainActivity, DatabaseHandler databaseHandler) {
        this.mainActivity = mainActivity;
        dB = databaseHandler;
        Log.i("msg::", "initiated db object");
        if (dB.getUser(1) == null) {
            // if user doesn't exists start hello activity
            mainActivity.startHelloActivity();
        } else {
            //creating  a new user
            rTools = new ReadingTools();
            converter = new GlucoseConverter();

            // DEBUG METHODS
            // dB.addNGlucoseReadings();
        }
    }

    public boolean isdbEmpty() {
        return dB.getGlucoseReadings().size() == 0;
    }
}
