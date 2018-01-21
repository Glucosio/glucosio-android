package org.glucosio.android.tools;

import org.glucosio.android.Constants;
import org.glucosio.android.R;
import org.glucosio.android.RobolectricTest;
import org.glucosio.android.db.GlucoseReading;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.robolectric.RuntimeEnvironment;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by david on 30/10/16.
 */

public class ReadingToCSVTest extends RobolectricTest {

    private FormatDateTime dateTool;
    private final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    private OutputStreamWriter osw = new OutputStreamWriter(outputStream);

    @Before
    public void setUp() {
        dateTool = new FormatDateTime(RuntimeEnvironment.application);
    }

    @Test
    public void whenNoDataGeneratesEmptyCSVWithHeader() throws IOException {
        ReadingToCSV r = new ReadingToCSV(RuntimeEnvironment.application, Constants.Units.MG_DL);
        r.createCSVFile(new ArrayList<GlucoseReading>(), osw);

        assertFileContentEqualsToString(outputStream.toString(), headerAsString());
    }

    @Test
    public void whenOneDataGeneratesCSVWithHeaderAndOneLine() throws IOException {
        final Date created = new Date();

        List<GlucoseReading> values = new ArrayList<>();
        values.add(new GlucoseReading(80, "type", created, "notes"));

        ReadingToCSV r = new ReadingToCSV(RuntimeEnvironment.application, Constants.Units.MG_DL);
        r.createCSVFile(values, osw);

        assertFileContentEqualsToString(outputStream.toString(), headerAsString(), valuesAsString(values.get(0), Constants.Units.MG_DL));
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
        return dateTool.convertRawDate(reading.getCreated()) +
                ',' +
                dateTool.convertRawTime(reading.getCreated()) +
                ',' +
                reading.getReading() +
                ',' +
                units +
                ',' +
                reading.getReading_type() +
                ',' +
                reading.getNotes();
    }

    private void assertFileContentEqualsToString(String output, String... expectedValues) {
        String[] lines = output.split("\n");
        Assert.assertEquals(expectedValues.length, lines.length);

        for (int i = 0; i < lines.length; i++) {
            Assert.assertEquals(expectedValues[i], lines[i]);
        }
    }
}
