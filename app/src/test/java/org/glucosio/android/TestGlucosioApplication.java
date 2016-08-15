package org.glucosio.android;

import android.support.annotation.NonNull;

import org.glucosio.android.activity.A1cCalculatorActivity;
import org.glucosio.android.activity.HelloActivity;
import org.glucosio.android.analytics.Analytics;
import org.glucosio.android.backup.Backup;
import org.glucosio.android.db.DatabaseHandler;
import org.glucosio.android.presenter.A1CCalculatorPresenter;
import org.glucosio.android.presenter.HelloPresenter;
import org.glucosio.android.tools.LocaleHelper;
import org.mockito.Mock;

import static org.mockito.MockitoAnnotations.initMocks;

public class TestGlucosioApplication extends GlucosioApplication {
    @Mock
    private Backup backupMock;

    @Mock
    private Analytics analyticsMock;

    @Mock
    private DatabaseHandler dbHandlerMock;

    @Mock
    private A1CCalculatorPresenter a1CCalculatorPresenterMock;

    @Mock
    private HelloPresenter helloPresenterMock;

    @Mock
    private LocaleHelper localeHelperMock;

    @Override
    public void onCreate() {
        super.onCreate();

        initMocks(this);
    }

    @NonNull
    @Override
    public Backup getBackup() {
        return backupMock;
    }

    @NonNull
    @Override
    public Analytics getAnalytics() {
        return analyticsMock;
    }

    @NonNull
    @Override
    public DatabaseHandler getDBHandler() {
        return dbHandlerMock;
    }

    @NonNull
    @Override
    public A1CCalculatorPresenter createA1cCalculatorPresenter(@NonNull A1cCalculatorActivity activity) {
        return a1CCalculatorPresenterMock;
    }

    @Override
    protected void initLanguage() {
        //nothing
    }

    @NonNull
    @Override
    public HelloPresenter createHelloPresenter(@NonNull final HelloActivity activity) {
        return helloPresenterMock;
    }

    @NonNull
    @Override
    public LocaleHelper getLocaleHelper() {
        return localeHelperMock;
    }
}
