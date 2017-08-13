package org.glucosio.android.tools;


import android.content.Context;

import org.glucosio.android.BuildConfig;
import org.glucosio.android.R;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import static junit.framework.Assert.assertEquals;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, packageName = "org.glucosio.android", sdk = 19)
public class GlucoseRangesGraphMatrixTest {

    private final Context contextMock = RuntimeEnvironment.application.getApplicationContext();
    private final GlucoseRangesExt glucoseRangesExt = new GlucoseRangesExt(contextMock);


    @Test
    public void stringToColorGreen() {
        assertEquals(contextMock.getResources().getColor(R.color.glucosio_reading_ok),
                glucoseRangesExt.stringToColor("green"));
    }

    @Test
    public void stringToColorRed() {
        assertEquals(contextMock.getResources().getColor(R.color.glucosio_reading_hyper),
                glucoseRangesExt.stringToColor("red"));
    }

    @Test
    public void stringToColorBlue() {
        assertEquals(contextMock.getResources().getColor(R.color.glucosio_reading_low),
                glucoseRangesExt.stringToColor("blue"));
    }

    @Test
    public void stringToColorOrange() {
        assertEquals(contextMock.getResources().getColor(R.color.glucosio_reading_high),
                glucoseRangesExt.stringToColor("orange"));
    }

    @Test
    public void stringToColorPurple() {
        assertEquals(contextMock.getResources().getColor(R.color.glucosio_reading_hypo),
                glucoseRangesExt.stringToColor("purple"));
    }

}
