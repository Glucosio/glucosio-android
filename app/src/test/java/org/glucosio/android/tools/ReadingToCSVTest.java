package org.glucosio.android.tools;

import org.glucosio.android.Constants;
import org.glucosio.android.R;
import org.glucosio.android.RobolectricTest;
import org.glucosio.android.db.GlucoseReading;
import org.junit.Assert;
import org.junit.Test;
import org.robolectric.RuntimeEnvironment;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by david on 30/10/16.
 */

public class ReadingToCSVTest extends RobolectricTest {

    private final FormatDateTime dateTool = new FormatDateTime(RuntimeEnvironment.application);
    private final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    private final OutputStreamWriter osw = new OutputStreamWriter(outputStream);
    private final NumberFormat numberFormat = NumberFormatUtils.createDefaultNumberFormat();

    @Test
    public void GeneratesEmptyCSVWithHeader_WhenNoData() throws IOException {
        ReadingToCSV r = new ReadingToCSV(RuntimeEnvironment.application, Constants.Units.MG_DL);
        r.createCSVFile(new ArrayList<GlucoseReading>(), osw);

        assertFileContentEqualsToString(outputStream.toString(), headerAsString());
    }

    @Test
    public void GeneratesCSVWithHeaderAndOneLine_WhenOneReading() throws IOException {
        final Date created = new Date();

        List<GlucoseReading> values = new ArrayList<>();
        values.add(new GlucoseReading(80, "type", created, "notes"));

        ReadingToCSV r = new ReadingToCSV(RuntimeEnvironment.application, Constants.Units.MG_DL);
        r.createCSVFile(values, osw);

        assertFileContentEqualsToString(outputStream.toString(), headerAsString(), valuesAsString(values.get(0), Constants.Units.MG_DL));
    }

    @Test
    public void GeneratesCSVWithHeaderAndOneLine_WhenOneReadingInMMol() throws IOException {
        final Date created = new Date();

        List<GlucoseReading> values = new ArrayList<>();
        values.add(new GlucoseReading(80, "type", created, "notes"));

        ReadingToCSV r = new ReadingToCSV(RuntimeEnvironment.application, Constants.Units.MMOL_L);
        r.createCSVFile(values, osw);

        assertFileContentEqualsToString(outputStream.toString(), headerAsString(), valuesAsString(values.get(0), Constants.Units.MMOL_L));
    }

    @Test
    public void GeneratesCSVWithoutNulls_WhenOneReadingWithNulls() throws IOException {
        final Date created = new Date();

        List<GlucoseReading> values = new ArrayList<>();
        values.add(new GlucoseReading(80, null, created, null));

        ReadingToCSV r = new ReadingToCSV(RuntimeEnvironment.application, Constants.Units.MG_DL);
        r.createCSVFile(values, osw);

        assertFileContentEqualsToString(outputStream.toString(), headerAsString(), valuesAsString(values.get(0), Constants.Units.MG_DL));
    }

    @Test
    public void GeneratesCSVWithHeaderAndLinesEscaped_WhenDataWithWrongChars() throws IOException {
        final Date created = new Date();

        List<GlucoseReading> values = new ArrayList<>();
        values.add(new GlucoseReading(80, "type", created, ","));
        values.add(new GlucoseReading(81, "type", created, "\"E\""));
        values.add(new GlucoseReading(82, "type", created, "\r"));

        ReadingToCSV r = new ReadingToCSV(RuntimeEnvironment.application, Constants.Units.MG_DL);
        r.createCSVFile(values, osw);

        assertFileContentEqualsToString(
                outputStream.toString(),
                headerAsString(),
                valuesAsString(values.get(0), Constants.Units.MG_DL),
                valuesAsString(values.get(1), Constants.Units.MG_DL),
                valuesAsString(values.get(2), Constants.Units.MG_DL)
        );
    }

    @Test
    public void EscapeNewLine_WhenDataWithIt() throws IOException {
        final Date created = new Date();

        List<GlucoseReading> values = new ArrayList<>();
        values.add(new GlucoseReading(82, "type", created, "\n"));

        ReadingToCSV r = new ReadingToCSV(RuntimeEnvironment.application, Constants.Units.MG_DL);
        r.createCSVFile(values, osw);

        String result = outputStream.toString();
        String header = headerAsString();
        assertThat(result).contains(header);
        String resultWithoutHeader = result.replace(header, "");
        resultWithoutHeader = resultWithoutHeader.trim();

        assertThat(resultWithoutHeader).isEqualTo(valuesAsString(values.get(0), Constants.Units.MG_DL));
    }

    private String headerAsString() {
        return getString(R.string.dialog_add_date) +
                ',' +
                getString(R.string.dialog_add_time) +
                ',' +
                getString(R.string.dialog_add_concentration) +
                ',' +
                getString(R.string.helloactivity_spinner_preferred_glucose_unit) +
                ',' +
                getString(R.string.dialog_add_measured) +
                ',' +
                getString(R.string.dialog_add_notes);
    }

    private String getString(int stringId) {
        return RuntimeEnvironment.application.getString(stringId);
    }

    private String valuesAsString(GlucoseReading reading, String units) {
        double readingValue = Constants.Units.MG_DL.equals(units) ?
                reading.getReading() :
                GlucosioConverter.glucoseToMmolL(reading.getReading());

        String reading_type = reading.getReading_type();
        String notes = reading.getNotes();

        return dateTool.convertRawDate(reading.getCreated()) +
                ',' +
                dateTool.convertRawTime(reading.getCreated()) +
                ',' +
                numberFormat.format(readingValue) +
                ',' +
                units +
                ',' +
                (reading_type == null ? "" : reading_type) +
                ',' +
                escapedCSVString(notes == null ? "" : notes);
    }

    private String escapedCSVString(String s) {
        if (s.contains(",") || s.contains("\"") || s.contains("\n") || s.contains("\r"))
            return "\"" + s.replace("\"", "\"\"") + "\"";
        else
            return s;
    }

    private void assertFileContentEqualsToString(String output, String... expectedValues) {
        String[] lines = output.split("\n");
        Assert.assertEquals(expectedValues.length, lines.length);

        for (int i = 0; i < lines.length; i++) {
            Assert.assertEquals(expectedValues[i], lines[i]);
        }
    }
}
