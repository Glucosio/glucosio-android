package org.glucosio.android.tools;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import org.glucosio.android.RobolectricTest;
import org.junit.Before;
import org.junit.Test;
import org.robolectric.RuntimeEnvironment;

import static org.assertj.android.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThat;

public class PreferencesTest extends RobolectricTest {
    private Preferences preferences;

    @Before
    public void setUp() throws Exception {
        preferences = new Preferences(RuntimeEnvironment.application);
    }

    @Test
    public void ShouldReturnFalseAsDefault_WhenAskedIfLocaleWasCleared() throws Exception {
        assertThat(preferences.isLocaleCleaned()).isFalse();
    }

    @Test
    public void ShouldReturnSavedValue_WhenLocaleIsClearedAsked() throws Exception {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(RuntimeEnvironment.application);
        sharedPreferences.edit().putBoolean(Preferences.LOCALE_CLEANED, true).apply();

        assertThat(preferences.isLocaleCleaned()).isTrue();
    }

    @Test
    public void ShouldSaveLocaleIsCleaned_WhenAsked() throws Exception {
        preferences.saveLocaleCleaned();

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(RuntimeEnvironment.application);
        assertThat(sharedPreferences).contains(Preferences.LOCALE_CLEANED, true);
    }
}