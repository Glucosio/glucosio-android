package org.glucosio.android.tools;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Locale;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.offset;

public class ReadingToolsTest {

    private Locale defaultLocale;

    @Before
    public void setUp() {
        defaultLocale = Locale.getDefault();
    }

    @After
    public void after() {
        Locale.setDefault(defaultLocale);
    }

    @Test
    public void shouldReturnNull_whenNullStringPassed() {
        assertThat(ReadingTools.parseReading(null)).isNull();
    }

    @Test
    public void shouldReturnNull_whenInvalidStringPassed() {
        assertThat(ReadingTools.parseReading("abc")).isNull();
    }

    @Test
    public void shouldReturnCorrectNumber_whenDotIsDecimalSeparator() {
        Locale.setDefault(new Locale("en"));
        assertThat(ReadingTools.parseReading("1.23").doubleValue()).isEqualTo(1.23);
    }

    @Test
    public void shouldReturnCorrectNumber_whenCommaIsDecimalSeparator() {
        Locale.setDefault(new Locale("fr"));
        assertThat(ReadingTools.parseReading("1,23").doubleValue()).isEqualTo(1.23);
    }

    @Test
    public void parseDoubleCorrectly() {
        double doubleValue = ReadingTools.safeParseDouble("1.01");

        assertThat(doubleValue).isCloseTo(1.01, offset(0.000000000000001));
    }

    @Test
    public void parseToZeroWhenException() {
        double doubleValue = ReadingTools.safeParseDouble("b");

        assertThat(doubleValue).isZero();
    }
}
