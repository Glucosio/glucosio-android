package org.glucosio.android.activity;

import org.glucosio.android.RobolectricTest;
import org.glucosio.android.db.User;
import org.junit.Before;
import org.junit.Test;
import org.robolectric.Robolectric;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class PreferencesActivityTest extends RobolectricTest {
    private PreferencesActivity activity;

    private User user = new User(1, "test", "en", "en", 23, "M", 1, "mg/dL", "", "", "Test", 0, 100);

    @Before
    public void setUp() {
        when(getDBHandler().getUser(1)).thenReturn(user);

        activity = Robolectric.buildActivity(PreferencesActivity.class).create().get();
    }

    @Test
    public void ShouldReportAnalytics_WhenCreated() {
        verify(getAnalytics()).reportScreen("Preferences");
    }
}
