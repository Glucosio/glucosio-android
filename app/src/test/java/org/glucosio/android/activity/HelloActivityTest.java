package org.glucosio.android.activity;

import android.content.res.Resources;

import org.assertj.core.util.Lists;
import org.glucosio.android.RobolectricTest;
import org.junit.Before;
import org.junit.Test;
import org.robolectric.Robolectric;

import java.util.Locale;

import static org.assertj.android.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.isNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class HelloActivityTest extends RobolectricTest {

    private HelloActivity activity;

    @Before
    public void setUp() throws Exception {
        when(getLocaleHelper().getDeviceLocale()).thenReturn(new Locale("en"));
        activity = Robolectric.buildActivity(HelloActivity.class).create().get();
    }

    @Test
    public void ShouldReportAnalytics_WhenCreated() throws Exception {
        verify(getAnalytics()).reportScreen("Hello Activity");
    }

    @Test
    public void ShouldSetDefaultLanguageToNull_WhenNextPressed() throws Exception {
        //with mocked language helper spinner doesn't have any selection
        activity.countrySpinner.getSpinner().setSelection(0);

        activity.onStartClicked();

        verify(getHelloPresenter()).onNextClicked(anyString(), anyString(), isNull(String.class), anyString(), anyInt(), anyString());
    }

    @Test
    public void ShouldBindView_WhenCreated() throws Exception {
        assertThat(activity.languageSpinner).isNotNull();
        assertThat(activity.countrySpinner).isNotNull();
        assertThat(activity.ageTextView).isNotNull();
        assertThat(activity.genderSpinner).isNotNull();
        assertThat(activity.startButton).isNotNull();
    }

    @Test
    public void ShouldInitLanguageSpinner_WhenCreated() throws Exception {
        when(getLocaleHelper().getLocalesWithTranslation(any(Resources.class))).
                thenReturn(Lists.newArrayList("nl", "ru", "ua"));
        when(getLocaleHelper().getDisplayLanguage("nl")).thenReturn("Nederlandse");
        when(getLocaleHelper().getDisplayLanguage("ru")).thenReturn("Русский");
        when(getLocaleHelper().getDisplayLanguage("ua")).thenReturn("Українська");

        activity = Robolectric.buildActivity(HelloActivity.class).create().get();

        assertThat(activity.languageSpinner.getSpinner()).hasCount(4);
        assertThat(activity.languageSpinner.getSpinner()).hasItemAtPosition(1, "Nederlandse");
        assertThat(activity.languageSpinner.getSpinner()).hasItemAtPosition(2, "Русский");
        assertThat(activity.languageSpinner.getSpinner()).hasItemAtPosition(3, "Українська");
    }
}