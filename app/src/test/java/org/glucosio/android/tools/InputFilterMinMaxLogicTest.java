package org.glucosio.android.tools;


import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class InputFilterMinMaxLogicTest {

    // Test Fixtures
    private InputFilterMinMaxExt ifmm = new InputFilterMinMaxExt(3, 5);


    // Tests for logic coverage of isInRange
    @Test
    public void RACC_A1() {
        assertTrue(ifmm.isInRange(1, 7, 3));
    }

    @Test
    public void RACC_A2() {
        assertFalse(ifmm.isInRange(5, 7, 3));
    }

    @Test
    public void RACC_A3() {
        assertTrue(ifmm.isInRange(5, 1, 4));
    }

    @Test
    public void RACC_A4() {
        assertFalse(ifmm.isInRange(3, 1, 4));
    }

    @Test
    public void RACC_B1() {
        assertTrue(ifmm.isInRange(1, 7, 5));
    }

    @Test
    public void RACC_B2() {
        assertFalse(ifmm.isInRange(1, 2, 5));
    }

    @Test
    public void RACC_B3() {
        assertTrue(ifmm.isInRange(8, 4, 5));
    }

    @Test
    public void RACC_B4() {
        assertFalse(ifmm.isInRange(8, 6, 5));
    }


    @Test
    public void RACC_C1() {
        assertTrue(ifmm.isInRange(3, 7, 4));
    }

    @Test
    public void RACC_C2() {
        assertFalse(ifmm.isInRange(3, 7, 2));
    }

    @Test
    public void RACC_C3() {
        assertTrue(ifmm.isInRange(6, 4, 5));
    }

    @Test
    public void RACC_C4() {
        assertFalse(ifmm.isInRange(6, 4, 7));
    }

}
