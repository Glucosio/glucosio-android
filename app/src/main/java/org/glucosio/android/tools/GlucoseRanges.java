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

package org.glucosio.android.tools;

import android.content.Context;
import android.support.annotation.VisibleForTesting;
import android.support.v4.content.ContextCompat;
import org.glucosio.android.R;
import org.glucosio.android.db.DatabaseHandler;

public class GlucoseRanges {

    private Context mContext;
    private double userMin;
    private double userMax;
    private static final String ADA = "ADA";
    private static final String AACE = "AACE";
    private static final String UKNICE = "UK NICE";
    private static final int HYPER_LIMIT = 200;
    private static final int HYPO_LIMIT = 70;
    private static final int ADA_MIN = 80;
    private static final int ADA_MAX = 180;
    private static final int AACE_MIN = 110;
    private static final int AACE_MAX = 140;
    private static final int UKNICE_MIN = 81;
    private static final int UKNICE_MAX = 153;

    public GlucoseRanges(Context context) {
        this.mContext = context;
        DatabaseHandler dB = new DatabaseHandler(mContext);
        this.userMin = dB.getUser(1).getCustom_range_min();
        this.userMax = dB.getUser(1).getCustom_range_max();
    }

    @VisibleForTesting
    void setCustomMin(int customMin) {
        this.userMin = customMin;
    }

    @VisibleForTesting
    void setCustomMax(int customMax) {
        this.userMax = customMax;
    }

    public static int getPresetMin(String preset) {
        switch (preset) {
            case ADA:
                return ADA_MIN;
            case AACE:
                return AACE_MIN;
            case UKNICE:
                return UKNICE_MIN;
            default:
                return ADA_MIN;
        }
    }

    public static int getPresetMax(String preset) {
        switch (preset) {
            case ADA:
                return ADA_MAX;
            case AACE:
                return AACE_MAX;
            case UKNICE:
                return UKNICE_MAX;
            default:
                return ADA_MAX;
        }
    }

    public String colorFromReading(double reading) {
        if (reading < HYPO_LIMIT) {
            // hypo limit 70
            return "purple";
        } else if (reading > HYPER_LIMIT) {
            //  hyper limit 200
            return "red";
        } else if (reading < userMin) {
            // low limit
            return "blue";
        } else if (reading > userMax) {
            // high limit
            return "orange";
        } else {
            // in range
            return "green";
        }
    }

    public int stringToColor(String color) {
        switch (color) {
            case "green":
                return ContextCompat.getColor(mContext, R.color.glucosio_reading_ok);
            case "red":
                return ContextCompat.getColor(mContext, R.color.glucosio_reading_hyper);
            case "blue":
                return ContextCompat.getColor(mContext, R.color.glucosio_reading_low);
            case "orange":
                return ContextCompat.getColor(mContext, R.color.glucosio_reading_high);
            default:
                return ContextCompat.getColor(mContext, R.color.glucosio_reading_hypo);
        }
    }

}
