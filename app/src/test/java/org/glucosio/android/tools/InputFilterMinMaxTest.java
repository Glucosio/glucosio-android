package org.glucosio.android.tools;


import android.text.SpannableString;

import org.junit.Test;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class InputFilterMinMaxTest {

    private InputFilterMinMax ifmm = new InputFilterMinMax(3, 5);

    @Test
    public void checkIfRACCA1ValueIsInRangeTrue() {
        assertTrue(ifmm.isInRange(1, 7, 3));
    }

    @Test
    public void checkIfRACCA2ValueIsInRangeFalse() {
        assertFalse(ifmm.isInRange(5, 7, 3));
    }

    @Test
    public void checkIfRACCA3ValueIsInRangeTrue() {
        assertTrue(ifmm.isInRange(5, 1, 4));
    }

    @Test
    public void checkIfRACCA4ValueIsInRangeFalse() {
        assertFalse(ifmm.isInRange(3, 1, 4));
    }

    @Test
    public void checkIfRACCB1ValueIsInRangeTrue() {
        assertTrue(ifmm.isInRange(1, 7, 5));
    }

    @Test
    public void checkIfRACCB2ValueIsInRangeFalse() {
        assertFalse(ifmm.isInRange(1, 2, 5));
    }

    @Test
    public void checkIfRACCB3ValueIsInRangeTrue() {
        assertTrue(ifmm.isInRange(8, 4, 5));
    }

    @Test
    public void checkIfRACCB4ValueIsInRangeFalse() {
        assertFalse(ifmm.isInRange(8, 6, 5));
    }


    @Test
    public void checkIfRACCC1ValueIsInRangeTrue() {
        assertTrue(ifmm.isInRange(3, 7, 4));
    }

    @Test
    public void checkIfRACCC2ValueIsInRangeFalse() {
        assertFalse(ifmm.isInRange(3, 7, 2));
    }

    @Test
    public void checkIfRACCC3ValueIsInRangeTrue() {
        assertTrue(ifmm.isInRange(6, 4, 5));
    }

    @Test
    public void checkIfRACCC4ValueIsInRange4False() {
        assertFalse(ifmm.isInRange(6, 4, 7));
    }

    @Test
    public void shouldReturnEmptyStringWhenNoDestAndSourceValues() {
        ifmm = new InputFilterMinMax("4", "4");
        assertEquals("", ifmm.filter("", 0, 0, new SpannableString(""), 0, 0));
    }

}
