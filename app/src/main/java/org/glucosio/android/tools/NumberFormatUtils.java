package org.glucosio.android.tools;

import android.support.annotation.NonNull;

import java.text.NumberFormat;

public class NumberFormatUtils {

    private static final int MINIMUM_FRACTION_DIGITS = 0;
    private static final int MAXIMUM_FRACTION_DIGITS = 3;

    private NumberFormatUtils() {
    }

    @NonNull
    public static NumberFormat createDefaultNumberFormat() {
        NumberFormat numberFormat = NumberFormat.getNumberInstance();
        numberFormat.setMaximumFractionDigits(MAXIMUM_FRACTION_DIGITS);
        numberFormat.setMinimumFractionDigits(MINIMUM_FRACTION_DIGITS);
        return numberFormat;
    }
}
