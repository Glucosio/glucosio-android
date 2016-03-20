package org.glucosio.android.activity;

import org.glucosio.android.RobolectricTest;
import org.junit.Before;
import org.junit.Test;
import org.robolectric.Robolectric;

import static org.mockito.Mockito.verify;

public class BackupActivityTest extends RobolectricTest {

    private BackupActivity activity;

    @Before
    public void setUp() throws Exception {
        activity = Robolectric.buildActivity(BackupActivity.class).create().get();
    }

    @Test
    public void ShouldInitBack_WhenCreated() throws Exception {

        verify(getBackup()).init(activity);
    }
}