package org.glucosio.android.activity;

import org.glucosio.android.RobolectricTest;
import org.junit.Before;
import org.junit.Test;
import org.robolectric.Robolectric;

import static org.mockito.Mockito.verify;

public class MainActivityTest extends RobolectricTest {
    private MainActivity activity;

    @Before
    public void setUp() throws Exception {
        activity = Robolectric.buildActivity(MainActivity.class).create().get();
    }

    @Test
    public void ShouldReportAnalytics_WhenCreated() throws Exception {

        verify(getAnalytics()).reportScreen("Main Activity");
    }
}
