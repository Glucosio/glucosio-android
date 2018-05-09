package org.glucosio.android.tools;

import org.junit.Test;

import java.text.NumberFormat;

import static org.assertj.core.api.Assertions.assertThat;
import static org.glucosio.android.Constants.Units.MG_DL;

public class NumberFormatUtilsTest {
    @Test
    public void SetMinimumAndMaximumNumberOfDigits() {
        NumberFormat numberFormat = NumberFormatUtils.createDefaultNumberFormat(MG_DL);

        assertThat(numberFormat.getMinimumFractionDigits()).isZero();
        assertThat(numberFormat.getMaximumFractionDigits()).isEqualTo(0);
    }
}
