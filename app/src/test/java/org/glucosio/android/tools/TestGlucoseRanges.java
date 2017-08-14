package org.glucosio.android.tools;


import android.content.Context;

class TestGlucoseRanges extends GlucoseRanges {
    TestGlucoseRanges(Context context) {
        super(context);
    }

    protected boolean isInUnitTests() {
        return true;
    }
}
