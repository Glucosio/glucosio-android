package org.glucosio.android.tools;


import android.content.Context;
import android.support.v4.content.ContextCompat;

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
    public void shouldReturnOkColorIfGreenStringIsGiven() {
        int expected = ContextCompat.getColor(contextMock, R.color.glucosio_reading_ok);
        int actual = glucoseRangesExt.stringToColor("green");

        assertEquals(expected, actual);
    }

    @Test
    public void shouldReturnHyperColorIfRedStringIsGiven() {
        int expected = ContextCompat.getColor(contextMock, R.color.glucosio_reading_hyper);
        int actual = glucoseRangesExt.stringToColor("red");

        assertEquals(expected, actual);
    }

    @Test
    public void shouldReturnLowColorIfBlueStringGiven() {
        int expected = ContextCompat.getColor(contextMock, R.color.glucosio_reading_low);
        int actual = glucoseRangesExt.stringToColor("blue");

        assertEquals(expected, actual);
    }

    @Test
    public void shouldReturnHighColorIfOrangeStringIsGiven() {
        int expected = ContextCompat.getColor(contextMock, R.color.glucosio_reading_high);
        int actual = glucoseRangesExt.stringToColor("orange");

        assertEquals(expected, actual);
    }

    @Test
    public void shouldReturnHypoColorIfPurpleStringIsGiven() {
        int expected = ContextCompat.getColor(contextMock, R.color.glucosio_reading_hypo);
        int actual = glucoseRangesExt.stringToColor("purple");

        assertEquals(expected, actual);
    }

}
