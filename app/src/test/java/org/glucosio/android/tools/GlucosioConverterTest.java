package org.glucosio.android.tools;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.offset;

public class GlucosioConverterTest {
    @Test
    public void ShouldRoundToFloor_WhenLowerValueOfExactPointIsGiven() {
        assertThat(GlucosioConverter.round(10.123, 2)).isEqualTo(10.12);
    }

    @Test
    public void ShouldRoundToCeil_WhenHigherValueOfExactPointIsGiven() {
        assertThat(GlucosioConverter.round(10.126, 2)).isEqualTo(10.13);
    }

    @Test
    public void ShouldConvertMmolToMgDlExactly_WhenMgDlNeeded() {
        assertThat(GlucosioConverter.glucoseToMgDl(1)).isEqualTo(18);
    }

    @Test
    public void ShouldConvertMgDlToMmolExactly_WhenMmolNeeded() {
        assertThat(GlucosioConverter.glucoseToMmolL(18)).isEqualTo(1);
    }

    @Test
    public void ShouldConvertMgDlToA1cExactly_WhenA1CNeeded() {
        assertThat(GlucosioConverter.glucoseToA1C(18)).isEqualTo(2.25);
    }

    @Test
    public void ShouldConvertA1CToGlucoseExactly_WhenGlucoseNeeded() {
        assertThat(GlucosioConverter.a1cToGlucose(2.25)).isEqualTo(17.88);
    }

    @Test
    public void ShouldConvertKgToLbExactly_WhenLbNeeded() {
        assertThat(GlucosioConverter.kgToLb(60)).isEqualTo(132.27, offset(0.01));
    }

    @Test
    public void ShouldConvertLbToKgExactly_WhenKgNeeded() {
        assertThat(GlucosioConverter.lbToKg(132)).isEqualTo(59.87, offset(0.01));
    }

    @Test
    public void ShouldConvertPercentageToA1cExactly_WhenPercentageIsUsed() {
        assertThat(GlucosioConverter.a1cNgspToIfcc(5)).isEqualTo(31.13);
    }

    @Test
    public void ShouldConvertA1cToPercentageExactly_WhenPercentageNeeded() {
        assertThat(GlucosioConverter.a1cIfccToNgsp(31.13)).isEqualTo(5);
    }
}
