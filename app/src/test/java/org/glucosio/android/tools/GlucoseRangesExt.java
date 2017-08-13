package org.glucosio.android.tools;


import android.content.Context;
import android.support.v4.content.ContextCompat;

import org.glucosio.android.R;

public class GlucoseRangesExt extends GlucoseRanges {
    private Context context;
    private int customMin;
    private int customMax;

    public GlucoseRangesExt(int customMin, int customMax) {
        this.customMin = customMin;
        this.customMax = customMax;
    }

    public GlucoseRangesExt(Context context) {
        this.context = context;
    }

    public String colorFromReading(int reading, String preferredRange) {
        // Check for Hypo/Hyperglycemia
        if (reading < 70) {
            return "purple";
        } else if (reading > 200) {
            return "red";
        } else if (reading > 70 | reading < 200) {
            // if not check with custom ranges
            switch (preferredRange) {
                case "ADA":
                    //// FIXME: 13.08.2017 FIX ADA range
                    if (reading >= 75 & reading <= 180) {
                        return "green";
                    } else if (reading < 75) {
                        return "blue";
                    } else if (reading > 180) {
                        return "orange";
                    }
                case "AACE":
                    if (reading >= 110 & reading <= 140) {
                        return "green";
                    } else if (reading < 110) {
                        return "blue";
                    } else if (reading > 140) {
                        return "orange";
                    }
                case "UK NICE":
                    if (reading >= 72 & reading <= 153) {
                        return "green";
                    } else if (reading < 72) {
                        return "blue";
                    } else if (reading > 153) {
                        return "orange";
                    }
                default:
                    if (reading >= customMin & reading <= customMax) {
                        return "green";
                    } else if (reading < customMin) {
                        return "blue";
                    } else if (reading > customMax) {
                        return "orange";
                    }
            }
        }
        return "red";
    }

    /**
     * This it the same method like in original class, but used with different context
     * given by GlucoseRanges extension class.
     * <p>
     * params @color
     */
    @Override
    public int stringToColor(String color) {
        switch (color) {
            case "green":
                return ContextCompat.getColor(context, R.color.glucosio_reading_ok);
            case "red":
                return ContextCompat.getColor(context, R.color.glucosio_reading_hyper);
            case "blue":
                return ContextCompat.getColor(context, R.color.glucosio_reading_low);
            case "orange":
                return ContextCompat.getColor(context, R.color.glucosio_reading_high);
            default:
                return ContextCompat.getColor(context, R.color.glucosio_reading_hypo);
        }
    }
}