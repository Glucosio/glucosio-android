package org.glucosio.android;

import android.support.annotation.NonNull;

import org.glucosio.android.analytics.Analytics;
import org.glucosio.android.backup.Backup;
import org.glucosio.android.db.DatabaseHandler;
import org.glucosio.android.invitations.Invitation;
import org.mockito.Mock;

import static org.mockito.MockitoAnnotations.initMocks;

public class TestGlucosioApplication extends GlucosioApplication {
    @Mock
    private Backup backupMock;

    @Mock
    private Analytics analyticsMock;

    @Mock
    private Invitation invitationMock;

    @Mock
    private DatabaseHandler dbHandlerMock;

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
    public Invitation getInvitation() {
        return invitationMock;
    }

    @Override
    protected void initInstabug() {
    }

    @NonNull
    @Override
    public DatabaseHandler getDBHandler() {
        return dbHandlerMock;
    }
}
