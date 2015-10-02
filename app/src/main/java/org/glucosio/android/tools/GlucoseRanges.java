package org.glucosio.android.tools;

import org.glucosio.android.db.DatabaseHandler;

public class GlucoseRanges {

    DatabaseHandler dB;
    String preferredRange;
    int customMin;
    int customMax;

    public GlucoseRanges(){
        dB = new DatabaseHandler();
        this.preferredRange = dB.getUser(1).get_preferred_range();
        if (preferredRange.equals("custom")){
            customMin = dB.getUser(1).get_custom_range_min();
            customMax = dB.getUser(1).get_custom_range_max();
        }
    }

    public String colorFromRange(int reading) {
        if (preferredRange.equals("ADA")){
            if (reading < 70  & reading > 180){
                return "green";
            } else {
                return "red";
            }
        } else if (preferredRange.equals("AACE")){
            if (reading < 110 & reading > 140){
                return "green";
            } else {
                return "red";
            }
        } else if (preferredRange.equals("UK NICE")) {
            if (reading < 72 & reading > 153){
                return "green";
            } else {
                return "read";
            }
        } else {
            if (reading < customMin & reading > customMax){
                return "green";
            } else {
                return "red";
            }
        }
    }
}
