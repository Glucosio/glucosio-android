package org.glucosio.android.tools;


import android.test.mock.MockContext;

import org.junit.Test;

import static junit.framework.Assert.assertTrue;

public class GlucoseRangesLogicTest {

    private GlucoseRangesExt glucoseRangesExt = new GlucoseRangesExt(new MockContext());
    private String preferredRange = "";
    private int reading = 65;


    @Test
    public void expectPurpleWhenReadingValueIs65WithNoPreferredRange() {
        assertTrue(glucoseRangesExt.colorFromReading(reading, preferredRange).equals("purple"));
    }

    @Test
    public void expectRedWhenReadingValueIs210WithNoPreferredRange() {
        reading = 210;

        assertTrue(glucoseRangesExt.colorFromReading(reading, preferredRange).equals("red"));
    }

    @Test
    public void expectGreenWhenReadingValueIs100WithADAPreferredRange() {
        reading = 100;
        preferredRange = "ADA";

        assertTrue(glucoseRangesExt.colorFromReading(reading, preferredRange).equals("green"));
    }

    @Test
    public void expectBlueWhenReadingValueIs72WithADAPreferredRange() {
        // FIXME: 13.08.2017 FIX ADA range in primary and extension classes
        reading = 72;
        preferredRange = "ADA";

        assertTrue(glucoseRangesExt.colorFromReading(reading, preferredRange).equals("blue"));
    }

    @Test
    public void expectOrangeWhenReadingValueIs185WithADAPreferredRange() {
        reading = 185;
        preferredRange = "ADA";

        assertTrue(glucoseRangesExt.colorFromReading(reading, preferredRange).equals("orange"));
    }

    @Test
    public void expectGreenWhenReadingValueIs130WithAACEPreferredRange() {
        reading = 130;
        preferredRange = "AACE";

        assertTrue(glucoseRangesExt.colorFromReading(reading, preferredRange).equals("green"));
    }

    @Test
    public void expectBlueWhenReadingValueIs90WithAACEPreferredRange() {
        reading = 90;
        preferredRange = "AACE";

        assertTrue(glucoseRangesExt.colorFromReading(reading, preferredRange).equals("blue"));
    }

    @Test
    public void expectOrangeWhenReadingValueIs150WithAACEPreferredRange() {
        reading = 150;
        preferredRange = "AACE";

        assertTrue(glucoseRangesExt.colorFromReading(reading, preferredRange).equals("orange"));
    }

    @Test
    public void expectGreenWhenReadingValueIs100WithUK_NICEPreferredRange() {
        reading = 100;
        preferredRange = "UK NICE";

        assertTrue(glucoseRangesExt.colorFromReading(reading, preferredRange).equals("green"));
    }

    @Test
    public void expectBlueWhenReadingValueIs71WithUK_NICEPreferredRange() {
        reading = 71;
        preferredRange = "UK NICE";

        assertTrue(glucoseRangesExt.colorFromReading(reading, preferredRange).equals("blue"));
    }

    @Test
    public void expectOrangeWhenReadingValueIs155WithUK_NICEPreferredRange() {
        reading = 155;
        preferredRange = "UK NICE";

        assertTrue(glucoseRangesExt.colorFromReading(reading, preferredRange).equals("orange"));
    }

    @Test
    public void expectGreenWhenReadingValueIs100WithNoPreferredRangeAndExtValuesRange75_140() {
        glucoseRangesExt = new GlucoseRangesExt(75, 140);
        reading = 100;

        assertTrue(glucoseRangesExt.colorFromReading(reading, preferredRange).equals("green"));
    }

    @Test
    public void expectBlueWhenReadingValueIs72WithNoPreferredRangeAndExtValuesRange75_140() {
        glucoseRangesExt = new GlucoseRangesExt(75, 140);
        reading = 72;

        assertTrue(glucoseRangesExt.colorFromReading(reading, preferredRange).equals("blue"));
    }

    @Test
    public void expectOrangeWhenReadingValueIs145WithNoPreferredRangeAndExtValuesRange75_140() {
        glucoseRangesExt = new GlucoseRangesExt(75, 140);
        reading = 145;

        assertTrue(glucoseRangesExt.colorFromReading(reading, preferredRange).equals("orange"));
    }
}

