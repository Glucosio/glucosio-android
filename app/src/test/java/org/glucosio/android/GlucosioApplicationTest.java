package org.glucosio.android;

import android.support.annotation.NonNull;

import org.glucosio.android.db.DatabaseHandler;
import org.glucosio.android.db.User;
import org.glucosio.android.tools.Preferences;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class GlucosioApplicationTest {
    @InjectMocks
    private GlucosioApplicationForTest application;

    @Mock
    private Preferences preferencesMock;

    @Mock
    private DatabaseHandler databaseHandlerMock;

    @Mock
    private User userMock;

    @Captor
    private ArgumentCaptor<User> userCaptor;

    @Test
    public void ShouldClearLanguage_WhenSetFromHelloActivityAndNotFixedYet() throws Exception {
        when(databaseHandlerMock.getUser(1)).thenReturn(userMock);

        application.onCreate();

        verify(databaseHandlerMock).updateUser(userCaptor.capture());
        assertThat(userCaptor.getValue().getPreferred_language()).isNull();
    }

    @Test
    public void ShouldSaveLanguageClearedToPreferences_WhenItIsDone() throws Exception {
        when(databaseHandlerMock.getUser(1)).thenReturn(userMock);

        application.onCreate();


        verify(preferencesMock).saveLocaleCleaned();
    }

    @Test
    public void ShouldNotClearLanguage_WhenAlreadyDone() throws Exception {
        when(preferencesMock.isLocaleCleaned()).thenReturn(true);

        application.onCreate();

        verify(userMock, never()).setPreferred_language(null);
        verify(databaseHandlerMock, never()).updateUser(userMock);
    }

    static class GlucosioApplicationForTest extends GlucosioApplication {

        private final DatabaseHandler dbHandler;
        private final Preferences preferences;

        private GlucosioApplicationForTest(DatabaseHandler dbHandler, Preferences preferences) {
            this.dbHandler = dbHandler;
            this.preferences = preferences;
        }

        @NonNull
        @Override
        public DatabaseHandler getDBHandler() {
            return dbHandler;
        }

        @NonNull
        @Override
        public Preferences getPreferences() {
            return preferences;
        }

        @Override
        protected void initFont() {
            //don't do anything for now
        }
    }
}