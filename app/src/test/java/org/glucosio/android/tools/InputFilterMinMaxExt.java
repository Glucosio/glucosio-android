package org.glucosio.android.tools;


import android.util.Log;

class InputFilterMinMaxExt extends InputFilterMinMax {
    private static final String TAG = "InputFilterMinMaxExt";

    private final int min;
    private final int max;

    InputFilterMinMaxExt(int min, int max) {
        super(min, max);
        this.min = min;
        this.max = max;
    }

    InputFilterMinMaxExt(String min, String max) {
        super(min, max);
        this.min = Integer.parseInt(min);
        this.max = Integer.parseInt(max);
    }

    public CharSequence filter(CharSequence source, int start, int end, String dest, int dstart, int dend) { //Spanned dest,
        try {
            int input = Integer.parseInt(dest + source.toString());
            if (this.isInRange(min, max, input))
                return null;
        } catch (NumberFormatException nfe) {
            Log.e(TAG, "filter: ", nfe);
        }
        return "";
    }

    public boolean isInRangeMutant(int a, int b, int c) {
        return b > a ? c >= a || c <= b : c >= b || c <= a;
    }
}
