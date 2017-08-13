package org.glucosio.android.tools;


import org.junit.Before;
import org.junit.Test;

import static junit.framework.Assert.assertTrue;

public class GlucoseRangesLogicTest {

    // Test Fixtures
    private int reading;
    private GlucoseRangesExt gr;
    private String preferredRange;

    @Before
    public void setup() {

        gr = new GlucoseRangesExt(0, 0);
        reading = 65;
    }

    /*
     * Testing colorFromReading for Clause Coverage
     */
    @Test
    public void clause1() {
        reading = 65;
        preferredRange = "";
        assertTrue(gr.colorFromReading(reading, preferredRange).equals("purple"));
    }

    @Test
    public void clause2() {
        reading = 210;
        preferredRange = "";
        assertTrue(gr.colorFromReading(reading, preferredRange).equals("red"));
    }

    @Test
    public void clauseADAGreen() {
        reading = 100;
        preferredRange = "ADA";
        assertTrue(gr.colorFromReading(reading, preferredRange).equals("green"));
    }

    @Test
    public void clauseADABlue() {
        // FIXME: 13.08.2017 FIX ADA range in primary and extension classes
        reading = 72;
        preferredRange = "ADA";
        assertTrue(gr.colorFromReading(reading, preferredRange).equals("blue"));
    }

    @Test
    public void clauseADAOrange() {
        reading = 185;
        preferredRange = "ADA";
        assertTrue(gr.colorFromReading(reading, preferredRange).equals("orange"));
    }

    @Test
    public void clauseAACEGreen() {
        reading = 130;
        preferredRange = "AACE";
        assertTrue(gr.colorFromReading(reading, preferredRange).equals("green"));
    }

    @Test
    public void clauseAACEBlue() {
        reading = 90;
        preferredRange = "AACE";
        assertTrue(gr.colorFromReading(reading, preferredRange).equals("blue"));
    }

    @Test
    public void clauseAACEOrange() {
        reading = 150;
        preferredRange = "AACE";
        assertTrue(gr.colorFromReading(reading, preferredRange).equals("orange"));
    }

    @Test
    public void clauseUKNICEGreen() {
        reading = 100;
        preferredRange = "UK NICE";
        assertTrue(gr.colorFromReading(reading, preferredRange).equals("green"));
    }

    @Test
    public void clauseUKNICEBlue() {
        reading = 71;
        preferredRange = "UK NICE";
        assertTrue(gr.colorFromReading(reading, preferredRange).equals("blue"));
    }

    @Test
    public void clauseUKNICEOrange() {
        reading = 155;
        preferredRange = "UK NICE";
        assertTrue(gr.colorFromReading(reading, preferredRange).equals("orange"));
    }

    @Test
    public void clauseCustomGreen() {
        reading = 100;
        preferredRange = "";
        GlucoseRangesExt temp = new GlucoseRangesExt(75, 140);
        assertTrue(temp.colorFromReading(reading, preferredRange).equals("green"));
    }

    @Test
    public void clauseCustomBlue() {
        reading = 72;
        preferredRange = "";
        GlucoseRangesExt temp = new GlucoseRangesExt(75, 140);
        assertTrue(temp.colorFromReading(reading, preferredRange).equals("blue"));
    }

    @Test
    public void clauseCustomOrange() {
        reading = 145;
        preferredRange = "";
        GlucoseRangesExt temp = new GlucoseRangesExt(75, 140);
        assertTrue(temp.colorFromReading(reading, preferredRange).equals("orange"));
    }
}

