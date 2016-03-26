package org.glucosio.android.activity;

import org.glucosio.android.RobolectricTest;
import org.glucosio.android.db.User;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mock;
import org.robolectric.Robolectric;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

@Ignore
public class PreferencesActivityTest extends RobolectricTest {
    private PreferencesActivity activity;

    @Mock
    private User mockedUser;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        when(getDBHandler().getUser(1)).thenReturn(mockedUser);
        when(mockedUser.getPreferred_range()).thenReturn("Test");

        activity = Robolectric.buildActivity(PreferencesActivity.class).create().get();
    }

    @Test
    public void ShouldReportAnalytics_WhenCreated() throws Exception {

        verify(getAnalytics()).reportScreen("Preferences");
    }
}