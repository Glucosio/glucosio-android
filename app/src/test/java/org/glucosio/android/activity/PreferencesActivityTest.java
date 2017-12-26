package org.glucosio.android.activity;

import org.glucosio.android.Constants;
import org.glucosio.android.RobolectricTest;
import org.glucosio.android.db.User;
import org.glucosio.android.db.UserBuilder;
import org.junit.Before;
import org.junit.Test;
import org.robolectric.Robolectric;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class PreferencesActivityTest extends RobolectricTest {
    private PreferencesActivity activity;

    private final User user = new UserBuilder()
            .setId(1)
            .setName("test")
            .setPreferredLanguage("en")
            .setCountry("en")
            .setAge(23)
            .setGender("M")
            .setDiabetesType(1)
            .setPreferredUnit(Constants.Units.MG_DL)
            .setPreferredA1CUnit("")
            .setPreferredWeightUnit("")
            .setPreferredRange("Test")
            .setMinRange(0)
            .setMaxRange(100)
            .createUser();

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
