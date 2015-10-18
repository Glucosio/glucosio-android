package org.glucosio.android.tools;

import android.content.Context;
import android.graphics.Color;

import org.glucosio.android.db.DatabaseHandler;

public class GlucoseRanges {

    private DatabaseHandler dB;
    private Context mContext;
    private String preferredRange;
    private int customMin;
    private int customMax;

    public GlucoseRanges(Context context){
        this.mContext = context;
        dB = new DatabaseHandler(mContext);
        this.preferredRange = dB.getUser(1).getPreferred_range();
        if (preferredRange.equals("Custom range")){
            this.customMin = dB.getUser(1).getCustom_range_min();
            this.customMax = dB.getUser(1).getCustom_range_max();
        }
    }

    public String colorFromRange(int reading) {
        // Check for Hypo/Hyperglycemia
        if (reading < 70) {
            return "purple";
        } else if (reading > 200){
            return "red";
        } else if (reading > 70 | reading < 200 ) {
            // if not check with custom ranges
            switch (preferredRange) {
                case "ADA":
                    if (reading >= 70 & reading <= 180) {
                        return "green";
                    } else if (reading < 70) {
                        return "blue";
                    } else if (reading > 180){
                        return "orange";
                    }
                case "AACE":
                    if (reading >= 110 & reading <= 140) {
                        return "green";
                    } else if (reading < 110) {
                        return "blue";
                    } else if (reading > 140){
                        return "orange";
                    }
                case "UK NICE":
                    if (reading >= 72 & reading <= 153) {
                        return "green";
                    } else if (reading < 72) {
                        return "blue";
                    } else if (reading > 153){
                        return "orange";
                    }
                default:
                    if (reading >= customMin & reading <= customMax) {
                        return "green";
                    } else if (reading < customMin) {
                        return "blue";
                    } else if (reading > customMax){
                        return "orange";
                    }
            }
        }
    return "red";}

    public int stringToColor (String color){
        switch (color) {
            case "green":
                return Color.parseColor("#4CAF50");
            case "red":
                return Color.parseColor("#D32F2F");
            case "blue":
                return Color.parseColor("#448AFF");
            case "orange":
                return Color.parseColor("#FF5722");
            default:
                return Color.parseColor("#9C27B0");
        }
    };
}
