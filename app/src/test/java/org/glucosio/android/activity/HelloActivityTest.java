package org.glucosio.android.activity;

import android.content.res.Resources;

import org.assertj.core.util.Lists;
import org.glucosio.android.RobolectricTest;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.robolectric.Robolectric;

import java.util.Locale;

import static org.assertj.android.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.isNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class HelloActivityTest extends RobolectricTest {

    private HelloActivity activity;

    @Captor
    private ArgumentCaptor<String> stringCaptor;

    @Before
    public void setUp() throws Exception {
        initMocks(this);

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

    @Test
    public void ShouldAddDefaultSpinner_WhenCreated() throws Exception {
        assertThat(activity.languageSpinner.getSpinner()).hasCount(1);
        assertThat(activity.languageSpinner.getSpinner()).hasItemAtPosition(0, new Locale("en").getDisplayLanguage());
    }

    @Test
    public void ShouldPassNullAsLocale_WhenDefaultSelected() throws Exception {
        when(getLocaleHelper().getLocalesWithTranslation(any(Resources.class))).
                thenReturn(Lists.newArrayList("nl"));
        when(getLocaleHelper().getDisplayLanguage("nl")).thenReturn("Nederlandse");
        activity = Robolectric.buildActivity(HelloActivity.class).create().get();
        activity.countrySpinner.getSpinner().setSelection(0);

        activity.onStartClicked();

        verify(getHelloPresenter()).onNextClicked(anyString(), anyString(), stringCaptor.capture(),
                anyString(), anyInt(), anyString());
        assertThat(stringCaptor.getValue()).isNull();
    }

    @Test
    public void ShouldPassNLAsLocale_WhenNLSelected() throws Exception {
        when(getLocaleHelper().getLocalesWithTranslation(any(Resources.class))).
                thenReturn(Lists.newArrayList("nl"));
        when(getLocaleHelper().getDisplayLanguage("nl")).thenReturn("Nederlandse");
        activity = Robolectric.buildActivity(HelloActivity.class).create().get();
        activity.countrySpinner.getSpinner().setSelection(0);
        activity.languageSpinner.setSelection(1);

        activity.onStartClicked();

        verify(getHelloPresenter()).onNextClicked(anyString(), anyString(), stringCaptor.capture(),
                anyString(), anyInt(), anyString());
        assertThat(stringCaptor.getValue()).isEqualTo("nl");
    }
}