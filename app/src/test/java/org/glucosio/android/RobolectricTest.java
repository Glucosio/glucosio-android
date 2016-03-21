package org.glucosio.android;

import com.instabug.library.InstabugActivityDelegate;

import org.glucosio.android.analytics.Analytics;
import org.glucosio.android.backup.Backup;
import org.glucosio.android.db.DatabaseHandler;
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

    protected Backup getBackup() {
        return ((TestGlucosioApplication) RuntimeEnvironment.application).getBackup();
    }

    protected InstabugActivityDelegate getInstaDelegate() {
        //noinspection ConstantConditions
        return ((TestGlucosioApplication) RuntimeEnvironment.application).createInstabugDelegate(null);
    }

    protected DatabaseHandler getDBHandler() {
        return ((TestGlucosioApplication) RuntimeEnvironment.application).getDBHandler();
    }
}
