package org.glucosio.android.activity;

import org.glucosio.android.RobolectricTest;
import org.junit.Before;
import org.junit.Test;
import org.robolectric.Robolectric;

import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.isNull;
import static org.mockito.Mockito.verify;

public class HelloActivityTest extends RobolectricTest {

    private HelloActivity activity;

    @Before
    public void setUp() throws Exception {
        activity = Robolectric.buildActivity(HelloActivity.class).create().get();
    }

    @Test
    public void ShouldReportAnalytics_WhenCreated() throws Exception {
        verify(getAnalytics()).reportScreen("Hello Activity");
    }

    @Test
    public void ShouldSetDefaultLanguageToNull_WhenNextPressed() throws Exception {
        activity.onStartClicked();

        verify(getHelloPresenter()).onNextClicked(anyString(), anyString(), isNull(String.class), anyString(), anyInt(), anyString());
    }
}