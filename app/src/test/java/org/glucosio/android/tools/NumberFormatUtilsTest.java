package org.glucosio.android.tools;

import org.junit.Test;

import java.text.NumberFormat;

import static org.assertj.core.api.Assertions.assertThat;

public class NumberFormatUtilsTest {
    @Test
    public void SetMinimumAndMaximumNumberOfDigits() {
        NumberFormat numberFormat = NumberFormatUtils.createDefaultNumberFormat();

        assertThat(numberFormat.getMinimumFractionDigits()).isZero();
        assertThat(numberFormat.getMaximumFractionDigits()).isEqualTo(3);
    }
}
