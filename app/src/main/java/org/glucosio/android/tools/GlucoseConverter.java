package org.glucosio.android.tools;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class GlucoseConverter {
    public int toMgDl(double mmolL){
        double converted = mmolL * 18;
        int finalInteger = (int) converted;
        return finalInteger;
    }

    public double toMmolL(double mgDl){
        return round(mgDl / 18.0, 1);
    }

    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }
}
