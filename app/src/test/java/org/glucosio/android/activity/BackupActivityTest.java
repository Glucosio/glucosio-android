package org.glucosio.android.activity;

import android.app.Activity;

import org.glucosio.android.RobolectricTest;
import org.junit.Before;
import org.junit.Test;
import org.robolectric.Robolectric;

import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

public class BackupActivityTest extends RobolectricTest {

    private BackupActivity activity;

    @Before
    public void setUp() throws Exception {
        initMocks(this);

        activity = Robolectric.buildActivity(BackupActivity.class).create().get();
    }

    @Test
    public void ShouldInitBack_WhenCreated() throws Exception {

        verify(getBackup()).init(activity);
    }

    @Test
    public void ShouldDelegateToBackup_WhenConnectIsCalled() throws Exception {
        reset(getBackup());

        activity.connectClient();

        verify(getBackup()).start();
    }

    @Test
    public void ShouldDelegateToBackup_WhenDisconnectIsCalled() throws Exception {
        reset(getBackup());

        activity.disconnectClient();

        verify(getBackup()).stop();
    }

    @Test
    public void ShouldDelegateToBackup_WhenActivityResultReceived() throws Exception {
        reset(getBackup());

        activity.onActivityResult(1, Activity.RESULT_OK, null);

        verify(getBackup()).start();
    }
}