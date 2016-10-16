package org.glucosio.android.tools;

import android.content.res.Resources;

import org.glucosio.android.RobolectricTest;
import org.junit.Test;
import org.robolectric.RuntimeEnvironment;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class LocaleHelperTest extends RobolectricTest {
    private LocaleHelper helper = new LocaleHelper();

    @Test
    public void ShouldReturnAtLeastEnglish_WhenAsked() throws Exception {
        final Resources resources = RuntimeEnvironment.application.getResources();

        final List<String> localesWithTranslation = helper.getLocalesWithTranslation(resources);

        assertThat(localesWithTranslation.size()).isGreaterThanOrEqualTo(1);
        assertThat(localesWithTranslation).contains("en");
    }
}