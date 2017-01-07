package org.glucosio.android.tools;

import android.content.Context;
import android.content.res.Resources;
import android.os.Environment;

import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;


import org.glucosio.android.R;
import org.glucosio.android.db.GlucoseReading;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.internal.util.io.IOUtil;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import io.realm.Realm;

/**
 * Created by david on 30/10/16.
 */

@RunWith(PowerMockRunner.class)
@PrepareForTest({ReadingToCSV.class, Realm.class, Environment.class})
public class ReadingToCSVTest {

    private Context context;

    private Realm realm;

    private File tmpFolderForTesting;
    private File glucosioFolder;

    private FormatDateTime dateTool;

    @Before
    public void setUp() {
        realm = mock(Realm.class);
        context = mock(Context.class);

        dateTool = new FormatDateTime(context);
        
        final Resources resources = mock(Resources.class);
        when(resources.getString(Matchers.anyInt())).thenAnswer(new Answer<String>() {
            @Override
            public String answer(InvocationOnMock invocation) throws Throwable {
                return String.valueOf(invocation.getArguments()[0]);
            }
        });
        
        when(context.getResources()).thenReturn(resources);

        File tmpDir = new File(System.getProperty("java.io.tmpdir"));

        tmpFolderForTesting = new File(tmpDir, UUID.randomUUID().toString());

        // Create the folder for glucosio
        glucosioFolder = new File(tmpFolderForTesting, "glucosio");
        glucosioFolder.mkdirs();

        Assert.assertTrue(glucosioFolder.exists() && glucosioFolder.isDirectory());

        mockStatic(Environment.class);
        when(Environment.getExternalStorageDirectory()).thenReturn(tmpDir);
        when(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)).thenReturn(tmpFolderForTesting);
    }

    @After
    public void clean() {
        removeFolder(tmpFolderForTesting);
    }

    private void removeFolder(File folder) {
        if (!folder.exists() || !folder.isDirectory()) {
            return;
        }
        final File[] files = folder.listFiles();
        if (files != null) {
            for (int i = 0; i < files.length; i++) {
                File f = files[i];
                if (f.isDirectory()) {
                    removeFolder(f);
                } else {
                    files[i].delete();
                }
            }
        }
        folder.delete();
    }

    @Test
    public void whenNoDataGeneratesEmptyCSVWithHeader() throws IOException {
        final ReadingToCSV r = createReadingToCSV("mg/dL");
        final String path = r.createCSVFile(realm, new ArrayList<GlucoseReading>());

        assertFileContentEqualsToString(path, headerAsString());
    }

    @Test
    public void whenOneDataGeneratesCSVWithHeaderAndOneLine() throws IOException {
        final Date created = new Date();

        List<GlucoseReading> values = new ArrayList<>();
        values.add(new GlucoseReading(80, "type", created, "notes"));

        final ReadingToCSV r = createReadingToCSV("mg/dL");
        final String path = r.createCSVFile(realm, values);

        assertFileContentEqualsToString(path, headerAsString(), valuesAsString(values.get(0), "mg/dL"));
    }

    private ReadingToCSV createReadingToCSV(String um) {
        return new ReadingToCSV(context, um);
    }

    private String headerAsString() {
        return new StringBuilder().append(R.string.dialog_add_date)
                .append(',')
                .append(R.string.dialog_add_time)
                .append(',')
                .append(R.string.dialog_add_concentration)
                .append(',')
                .append(R.string.helloactivity_spinner_preferred_glucose_unit)
                .append(',')
                .append(R.string.dialog_add_measured)
                .append(',')
                .append(R.string.dialog_add_notes)
                .toString();
    }

    private String valuesAsString(GlucoseReading reading, String units) {
        return new StringBuilder().append(dateTool.convertRawDate(reading.getCreated()))
                .append(',')
                .append(dateTool.convertRawTime(reading.getCreated()))
                .append(',')
                .append(reading.getReading())
                .append(',')
                .append(units)
                .append(',')
                .append(reading.getReading_type())
                .append(',')
                .append(reading.getNotes())
                .toString();
    }

    private void assertFileContentEqualsToString(String path, String... expectedValues) throws IOException {
        Assert.assertNotNull(path);

        final File file = new File(path);
        Assert.assertTrue(file.exists() && file.isFile());

        InputStream is = new FileInputStream(file);
        try {
            Collection<String> lines = IOUtil.readLines(is);

            Assert.assertEquals(expectedValues.length, lines.size());

            Iterator<String> iterator = lines.iterator();

            for (int i = 0; i < lines.size(); i++) {
                Assert.assertTrue(iterator.hasNext());
                Assert.assertEquals(expectedValues[i], iterator.next());
            }

        } finally {
            IOUtil.closeQuietly(is);
        }
    }

}
