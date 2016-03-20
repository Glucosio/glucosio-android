package org.glucosio.android;

import org.glucosio.android.analytics.Analytics;
import org.junit.Ignore;
import org.junit.runner.RunWith;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

@Ignore
@RunWith(GlucosioTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21, packageName = "org.glucosio.android")
public abstract class RobolectricTest {
    protected Analytics getAnalytics() {
        return ((TestGlucosioApplication) RuntimeEnvironment.application).getAnalytics();
    }
}
