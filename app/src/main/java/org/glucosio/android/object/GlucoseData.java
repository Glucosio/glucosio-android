package org.glucosio.android.object;

import java.text.DecimalFormat;

public class GlucoseData implements Comparable<GlucoseData> {

    public long realDate;
    public String sensorId;
    public long sensorTime;
    public int glucoseLevel = -1;
    public long phoneDatabaseId;

    public GlucoseData(){}

    public String glucose(boolean mmol) {
        return glucose(glucoseLevel, mmol);
    }

    public static String glucose(int mgdl, boolean mmol) {
        return mmol ? new DecimalFormat("##.0").format(mgdl/18f) : String.valueOf(mgdl);
    }

    @Override
    public int compareTo(GlucoseData another) {
        return (int) (realDate - another.realDate);
    }
}
