package org.glucosio.android.tools;

import org.glucosio.android.db.DatabaseHandler;

public class GlucoseRanges {

    private DatabaseHandler dB;
    private String preferredRange;
    private int customMin;
    private int customMax;

    public GlucoseRanges(){
        dB = new DatabaseHandler();
        this.preferredRange = dB.getUser(1).get_preferred_range();
        if (preferredRange.equals("Custom range")){
            this.customMin = dB.getUser(1).get_custom_range_min();
            this.customMax = dB.getUser(1).get_custom_range_max();
        }
    }

    public String colorFromRange(int reading) {
        // Check for Hypo/Hyperglycemia
        if (reading < 70 | reading > 200) {
            return "purple";
        } else {
            // if not check with custom ranges
            switch (preferredRange) {
                case "ADA":
                    if (reading >= 70 & reading <= 180) {
                        return "green";
                    } else {
                        return "red";
                    }
                case "AACE":
                    if (reading >= 110 & reading <= 140) {
                        return "green";
                    } else {
                        return "red";
                    }
                case "UK NICE":
                    if (reading >= 72 & reading <= 153) {
                        return "green";
                    } else {
                        return "red";
                    }
                default:
                    if (reading >= customMin & reading <= customMax) {
                        return "green";
                    } else {
                        return "red";
                    }
            }
        }
    }
}
