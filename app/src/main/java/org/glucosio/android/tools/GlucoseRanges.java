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

    private DatabaseHandler dB;
    private Context mContext;
    private String preferredRange;
    private int customMin;
    private int customMax;
    private final static int hyperLimit = 200;
    private final static int hypoLimit = 70;

    public GlucoseRanges(Context context) {
        this.mContext = context;
        dB = new DatabaseHandler(mContext);
        this.preferredRange = dB.getUser(1).getPreferred_range();
        this.customMin = dB.getUser(1).getCustom_range_min();
        this.customMax = dB.getUser(1).getCustom_range_max();
    }

    @VisibleForTesting
    void setPreferredRange(String preferredRange) {
        this.preferredRange = preferredRange;
    }

    @VisibleForTesting
    void setCustomMin(int customMin) {
        this.customMin = customMin;
    }

    @VisibleForTesting
    void setCustomMax(int customMax) {
        this.customMax = customMax;
    }

    public static int rangePresetToMin(String preset) {
        switch (preset) {
            case "ADA":
                return 70;
            case "AACE":
                return 110;
            case "UK NICE":
                return 72;
            default:
                return 70;
        }
    }

    public static int rangePresetToMax(String preset) {
        switch (preset) {
            case "ADA":
                return 180;
            case "AACE":
                return 140;
            case "UK NICE":
                return 153;
            default:
                return 180;
        }
    }

    public String colorFromReading(int reading) {
        if (reading < hypoLimit) {
            // hypo limit 70
            return "purple";
        } else if (reading > hyperLimit) {
            //  hyper limit 200
            return "red";
        } else if (reading < customMin) {
            // low limit
            return "blue";
        } else if (reading > customMax) {
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
