package org.glucosio.android.tools;

import android.support.annotation.NonNull;

import java.text.NumberFormat;

import static org.glucosio.android.Constants.Units.KG;
import static org.glucosio.android.Constants.Units.LBS;
import static org.glucosio.android.Constants.Units.MG_DL;
import static org.glucosio.android.Constants.Units.MMOL_L;
import static org.glucosio.android.Constants.Units.MM_HG;
import static org.glucosio.android.Constants.Units.PERCENTAGE;

public class NumberFormatUtils {

    private static final int MINIMUM_FRACTION_DIGITS = 0;
    private static final int MAXIMUM_FRACTION_DIGITS = 0;

    private NumberFormatUtils() {
    }

    /*
     * These methods are added so that the number of digits used
     * for formatting can vary depending on the units chosen.
     *
     * ref, Slack convo:
     *
     * "Everything I can find shows only one decimal used in mmol and European measurements
     *  And for US measurements we only use decimal in Hba1c not in Blood Glucose or weight,
     *  so can we just make sure it’s decimal support for the mmol and European metrics and
     *  only one decimal? then we should be good"
     */
    private static int minFractionDigitsForUnit(String unit) {
        int retVal = MINIMUM_FRACTION_DIGITS;
        if (unit != null) {
            if (unit.equalsIgnoreCase(MG_DL)) {
                retVal = 0;
            }
            if (unit.equalsIgnoreCase(MMOL_L)) {
                retVal = 1;
            }
        }
        return retVal;
    }

    private static int maxFractionDigitsForUnit(String unit) {
        int retVal = MAXIMUM_FRACTION_DIGITS;
        if (unit != null) {
            if (unit.equalsIgnoreCase(MG_DL)) {
                retVal = 0;
            }
            if (unit.equalsIgnoreCase(MMOL_L)) {
                retVal = 1;
            }
            if (unit.equalsIgnoreCase(PERCENTAGE)) {
                retVal = 1;
            }
            if (unit.equalsIgnoreCase(MM_HG)) {
                retVal = 1;
            }
            if (unit.equalsIgnoreCase(LBS)) {
                retVal = 2;
            }
            if (unit.equalsIgnoreCase(KG)) {
                retVal = 1;
            }

        }
        return retVal;
    }

    @NonNull
    public static NumberFormat createDefaultNumberFormat(String units) {
        NumberFormat numberFormat = NumberFormat.getNumberInstance();
        numberFormat.setMaximumFractionDigits(maxFractionDigitsForUnit(units));
        numberFormat.setMinimumFractionDigits(minFractionDigitsForUnit(units));
        return numberFormat;
    }
}
