package org.glucosio.android.tools;


import android.content.Context;
import android.graphics.Color;

import org.glucosio.android.R;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class GlucoseRangesTest {
    private static final int READING_OK = Color.parseColor("#749756");
    private static final int READING_HYPER = Color.parseColor("#E86445");
    private static final int READING_LOW = Color.parseColor("#4B8DDB");
    private static final int READING_HIGH = Color.parseColor("#E7B85D");
    private static final int READING_HYPO = Color.parseColor("#6F4EAB");
    @Mock
    private Context context;
    @Mock
    private GlucoseRanges glucoseRanges;

    @Before
    public void setUp() throws Exception {
        when(context.getColor(R.color.glucosio_reading_hyper)).thenReturn(READING_HYPER);
        when(glucoseRanges.stringToColor("red")).thenReturn(READING_HYPER);

        when(context.getColor(R.color.glucosio_reading_low)).thenReturn(READING_LOW);
        when(glucoseRanges.stringToColor("blue")).thenReturn(READING_LOW);

        when(context.getColor(R.color.glucosio_reading_ok)).thenReturn(READING_OK);
        when(glucoseRanges.stringToColor("green")).thenReturn(READING_OK);

        when(context.getColor(R.color.glucosio_reading_hypo)).thenReturn(READING_HYPO);
        when(glucoseRanges.stringToColor("purple")).thenReturn(READING_HYPO);

        when(context.getColor(R.color.glucosio_reading_high)).thenReturn(READING_HIGH);
        when(glucoseRanges.stringToColor("orange")).thenReturn(READING_HIGH);

        when(glucoseRanges.colorFromReading(65)).thenReturn("purple");
        when(glucoseRanges.colorFromReading(410)).thenReturn("red");
        when(glucoseRanges.colorFromReading(100)).thenReturn("green");
        when(glucoseRanges.colorFromReading(130)).thenReturn("green");
        when(glucoseRanges.colorFromReading(72)).thenReturn("blue");
        when(glucoseRanges.colorFromReading(90)).thenReturn("blue");
        when(glucoseRanges.colorFromReading(145)).thenReturn("orange");
        when(glucoseRanges.colorFromReading(185)).thenReturn("orange");

    }

    @Test
    public void expectPurpleWhenReadingValueIs65WithNoPreferredRange() {
        assertTrue(glucoseRanges.colorFromReading(65).equals("purple"));
    }

    @Test
    public void expectRedWhenReadingValueIs210WithNoPreferredRange() {
        assertEquals(glucoseRanges.colorFromReading(410), "red");
    }

    @Test
    public void expectGreenWhenReadingValueIs100WithADAPreferredRange() {
        glucoseRanges.setPreferredRange("ADA");
        assertTrue(glucoseRanges.colorFromReading(100).equals("green"));
    }

    @Test
    public void expectBlueWhenReadingValueIs72WithADAPreferredRange() {
        glucoseRanges.setPreferredRange("ADA");
        assertTrue(glucoseRanges.colorFromReading(72).equals("blue"));
    }

    @Test
    public void expectOrangeWhenReadingValueIs185WithADAPreferredRange() {
        glucoseRanges.setPreferredRange("ADA");
        assertTrue(glucoseRanges.colorFromReading(185).equals("orange"));
    }

    @Test
    public void expectGreenWhenReadingValueIs130WithAACEPreferredRange() {
        glucoseRanges.setPreferredRange("AACE");
        assertTrue(glucoseRanges.colorFromReading(130).equals("green"));
    }

    @Test
    public void expectBlueWhenReadingValueIs90WithAACEPreferredRange() {
        glucoseRanges.setPreferredRange("AACE");
        assertTrue(glucoseRanges.colorFromReading(90).equals("blue"));
    }

    @Test
    public void expectOrangeWhenReadingValueIs145WithAACEPreferredRange() {
        glucoseRanges.setPreferredRange("AACE");
        assertTrue(glucoseRanges.colorFromReading(145).equals("orange"));
    }

    @Test
    public void expectGreenWhenReadingValueIs100WithUKNICEPreferredRange() {
        glucoseRanges.setPreferredRange("UK NICE");
        assertTrue(glucoseRanges.colorFromReading(100).equals("green"));
    }

    @Test
    public void expectBlueWhenReadingValueIs71WithUKNICEPreferredRange() {
        glucoseRanges.setPreferredRange("UK NICE");
        assertTrue(glucoseRanges.colorFromReading(72).equals("blue"));
    }

    @Test
    public void expectOrangeWhenReadingValueIs155WithUKNICEPreferredRange() {
        glucoseRanges.setPreferredRange("UK NICE");
        assertTrue(glucoseRanges.colorFromReading(145).equals("orange"));
    }

    @Test
    public void expectGreenWhenReadingValueIs100WithNoPreferredRangeAndExtValuesRange75140() {
        setPreferredRange(75, 140);
        assertTrue(glucoseRanges.colorFromReading(100).equals("green"));
    }

    @Test
    public void expectBlueWhenReadingValueIs72WithNoPreferredRangeAndExtValuesRange75140() {
        setPreferredRange(75, 140);
        assertTrue(glucoseRanges.colorFromReading(72).equals("blue"));
    }

    @Test
    public void expectOrangeWhenReadingValueIs145WithNoPreferredRangeAndExtValuesRange75140() {
        setPreferredRange(75, 140);
        assertTrue(glucoseRanges.colorFromReading(145).equals("orange"));
    }

    @Test
    public void shouldReturnOkColorIfGreenStringIsGiven() {
        int expected = context.getColor(R.color.glucosio_reading_ok);
        int actual = glucoseRanges.stringToColor("green");

        assertEquals(expected, actual);
    }

    @Test
    public void shouldReturnHyperColorIfRedStringIsGiven() {
        int expected = context.getColor(R.color.glucosio_reading_hyper);
        int actual = glucoseRanges.stringToColor("red");

        assertEquals(expected, actual);
    }

    @Test
    public void shouldReturnLowColorIfBlueStringGiven() {
        int expected = context.getColor(R.color.glucosio_reading_low);
        int actual = glucoseRanges.stringToColor("blue");

        assertEquals(expected, actual);
    }

    @Test
    public void shouldReturnHighColorIfOrangeStringIsGiven() {
        int expected = context.getColor(R.color.glucosio_reading_high);
        int actual = glucoseRanges.stringToColor("orange");

        assertEquals(expected, actual);
    }

    @Test
    public void shouldReturnHypoColorIfPurpleStringIsGiven() {
        int expected = context.getColor(R.color.glucosio_reading_hypo);
        int actual = glucoseRanges.stringToColor("purple");

        assertEquals(expected, actual);
    }

    private void setPreferredRange(int min, int max) {
        glucoseRanges.setPreferredRange("Custom");
        glucoseRanges.setCustomMin(min);
        glucoseRanges.setCustomMax(max);
    }
}