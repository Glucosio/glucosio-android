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
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, packageName = "org.glucosio.android", sdk = 19)
public class GlucoseRangesTest {

    private final Context contextMock = RuntimeEnvironment.application.getApplicationContext();
    private TestGlucoseRanges glucoseRanges = new TestGlucoseRanges(contextMock);
    private int reading = 65;

    @Test
    public void expectIsInUnitTestsReturnTrue() {
        assertTrue(glucoseRanges.isInUnitTests());
    }

    @Test
    public void expectIsInUnitTestsReturnFalseByDefault() {
        assertFalse(new GlucoseRanges().isInUnitTests());
    }

    @Test
    public void expectPurpleWhenReadingValueIs65WithNoPreferredRange() {
        assertTrue(glucoseRanges.colorFromReading(reading).equals("purple"));
    }

    @Test
    public void expectRedWhenReadingValueIs210WithNoPreferredRange() {
        reading = 410;

        assertEquals(glucoseRanges.colorFromReading(reading), "red");
    }

    @Test
    public void expectGreenWhenReadingValueIs100WithADAPreferredRange() {
        glucoseRanges.setPreferredRange("ADA");
        reading = 100;

        assertTrue(glucoseRanges.colorFromReading(reading).equals("green"));
    }

    @Test
    public void expectBlueWhenReadingValueIs72WithADAPreferredRange() {
        // FIXME: 13.08.2017 FIX ADA range in primary and extension classes
        glucoseRanges.setPreferredRange("ADA");
        reading = 67;

        assertFalse(glucoseRanges.colorFromReading(reading).equals("blue"));
    }

    @Test
    public void expectOrangeWhenReadingValueIs185WithADAPreferredRange() {
        glucoseRanges.setPreferredRange("ADA");
        reading = 185;

        assertTrue(glucoseRanges.colorFromReading(reading).equals("orange"));
    }

    @Test
    public void expectGreenWhenReadingValueIs130WithAACEPreferredRange() {
        glucoseRanges.setPreferredRange("AACE");
        reading = 130;

        assertTrue(glucoseRanges.colorFromReading(reading).equals("green"));
    }

    @Test
    public void expectBlueWhenReadingValueIs90WithAACEPreferredRange() {
        glucoseRanges.setPreferredRange("AACE");
        reading = 90;

        assertTrue(glucoseRanges.colorFromReading(reading).equals("blue"));
    }

    @Test
    public void expectOrangeWhenReadingValueIs150WithAACEPreferredRange() {
        glucoseRanges.setPreferredRange("AACE");
        reading = 150;

        assertTrue(glucoseRanges.colorFromReading(reading).equals("orange"));
    }

    @Test
    public void expectGreenWhenReadingValueIs100WithUKNICEPreferredRange() {
        glucoseRanges.setPreferredRange("UK NICE");
        reading = 100;

        assertTrue(glucoseRanges.colorFromReading(reading).equals("green"));
    }

    @Test
    public void expectBlueWhenReadingValueIs71WithUKNICEPreferredRange() {
        glucoseRanges.setPreferredRange("UK NICE");
        reading = 71;

        assertTrue(glucoseRanges.colorFromReading(reading).equals("blue"));
    }

    @Test
    public void expectOrangeWhenReadingValueIs155WithUKNICEPreferredRange() {
        glucoseRanges.setPreferredRange("UK NICE");
        reading = 155;

        assertTrue(glucoseRanges.colorFromReading(reading).equals("orange"));
    }

    @Test
    public void expectGreenWhenReadingValueIs100WithNoPreferredRangeAndExtValuesRange75140() {
        setPreferredRange(75, 140);
        reading = 100;

        assertTrue(glucoseRanges.colorFromReading(reading).equals("green"));
    }

    @Test
    public void expectBlueWhenReadingValueIs72WithNoPreferredRangeAndExtValuesRange75140() {
        setPreferredRange(75, 140);
        reading = 72;

        assertTrue(glucoseRanges.colorFromReading(reading).equals("blue"));
    }

    @Test
    public void expectOrangeWhenReadingValueIs145WithNoPreferredRangeAndExtValuesRange75140() {
        setPreferredRange(75, 140);
        reading = 145;

        assertTrue(glucoseRanges.colorFromReading(reading).equals("orange"));
    }

    @Test
    public void shouldReturnOkColorIfGreenStringIsGiven() {
        int expected = ContextCompat.getColor(contextMock, R.color.glucosio_reading_ok);
        int actual = glucoseRanges.stringToColor("green");

        assertEquals(expected, actual);
    }

    @Test
    public void shouldReturnHyperColorIfRedStringIsGiven() {
        int expected = ContextCompat.getColor(contextMock, R.color.glucosio_reading_hyper);
        int actual = glucoseRanges.stringToColor("red");

        assertEquals(expected, actual);
    }

    @Test
    public void shouldReturnLowColorIfBlueStringGiven() {
        int expected = ContextCompat.getColor(contextMock, R.color.glucosio_reading_low);
        int actual = glucoseRanges.stringToColor("blue");

        assertEquals(expected, actual);
    }

    @Test
    public void shouldReturnHighColorIfOrangeStringIsGiven() {
        int expected = ContextCompat.getColor(contextMock, R.color.glucosio_reading_high);
        int actual = glucoseRanges.stringToColor("orange");

        assertEquals(expected, actual);
    }

    @Test
    public void shouldReturnHypoColorIfPurpleStringIsGiven() {
        int expected = ContextCompat.getColor(contextMock, R.color.glucosio_reading_hypo);
        int actual = glucoseRanges.stringToColor("purple");

        assertEquals(expected, actual);
    }

    private void setPreferredRange(int min, int max) {
        glucoseRanges.setPreferredRange("Custom");
        glucoseRanges.setCustomMin(min);
        glucoseRanges.setCustomMax(max);
    }
}