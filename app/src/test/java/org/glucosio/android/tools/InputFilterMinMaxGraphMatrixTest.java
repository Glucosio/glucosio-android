package org.glucosio.android.tools;


import org.junit.Test;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNull;
import static junit.framework.Assert.assertTrue;

public class InputFilterMinMaxGraphMatrixTest {

    //GraphCoverageTests
    private InputFilterMinMaxExt ifmm = new InputFilterMinMaxExt("5", "3");


    @Test
    public void filterNull() {
        assertNull(ifmm.filter("4", 0, 0, "", 0, 0));
    }

    @Test
    public void filterEmpty() {
        assertEquals("", ifmm.filter("2", 0, 0, "", 0, 0));
    }

    @Test
    public void filterNull2() {
        assertNull(ifmm.filter("4", 0, 0, "", 0, 0));
    }

    @Test
    public void filterEmpty2() {
        assertEquals("", ifmm.filter("2", 0, 0, "", 0, 0));
    }


    //Mutation Tests
    @Test
    public void isInRangeMutant() {
        assertTrue(ifmm.isInRange(5, 3, 4));
        assertTrue(ifmm.isInRangeMutant(5, 3, 4));
    }
}
