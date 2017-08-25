package org.glucosio.android;

import org.glucosio.android.analytics.Analytics;
import org.glucosio.android.backup.Backup;
import org.glucosio.android.db.DatabaseHandler;
import org.glucosio.android.presenter.HelloPresenter;
import org.glucosio.android.tools.LocaleHelper;
import org.junit.Ignore;
import org.junit.runner.RunWith;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;
import org.robolectric.RobolectricTestRunner;

@Ignore
@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21)
public abstract class RobolectricTest {
    protected Analytics getAnalytics() {
        return getTestApplication().getAnalytics();
    }

    protected Backup getBackup() {
        return getTestApplication().getBackup();
    }

    private TestGlucosioApplication getTestApplication() {
        return (TestGlucosioApplication) RuntimeEnvironment.application;
    }

    protected DatabaseHandler getDBHandler() {
        return getTestApplication().getDBHandler();
    }

    protected HelloPresenter getHelloPresenter() {
        //noinspection ConstantConditions
        return getTestApplication().createHelloPresenter(null);
    }

    protected LocaleHelper getLocaleHelper() {
        //noinspection ConstantConditions
        return getTestApplication().getLocaleHelper();
    }
}
