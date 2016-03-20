package org.glucosio.android.activity;

import android.app.Activity;

import org.glucosio.android.RobolectricTest;
import org.junit.Before;
import org.junit.Test;
import org.robolectric.Robolectric;

import static org.mockito.Mockito.verify;

public class BackupActivityTest extends RobolectricTest {

    private BackupActivity activity;

    @Before
    public void setUp() throws Exception {
        activity = Robolectric.buildActivity(BackupActivity.class).create().get();
    }

    @Test
    public void ShouldInitBack_WhenCreated() throws Exception {

        verify(getBackup()).init(activity);
    }

    @Test
    public void ShouldDelegateToBackup_WhenConnectIsCalled() throws Exception {

        activity.connectClient();

        verify(getBackup()).start();
    }

    @Test
    public void ShouldDelegateToBackup_WhenDisconnectIsCalled() throws Exception {

        activity.disconnectClient();

        verify(getBackup()).stop();
    }

    @Test
    public void ShouldDelegateToBackup_WhenActivityResultReceived() throws Exception {

        activity.onActivityResult(1, Activity.RESULT_OK, null);

        verify(getBackup()).start();
    }
}