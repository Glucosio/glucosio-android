package org.glucosio.android.activity;

import android.view.MotionEvent;

import org.glucosio.android.RobolectricTest;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.robolectric.Robolectric;
import org.robolectric.util.ActivityController;

import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

public class MainActivityTest extends RobolectricTest {
    private MainActivity activity;
    private ActivityController<MainActivity> activityController;

    @Mock
    private MotionEvent motionEventMock;

    @Before
    public void setUp() throws Exception {
        initMocks(this);

        activityController = Robolectric.buildActivity(MainActivity.class);
        activity = activityController.create().get();
    }

    @Test
    public void ShouldReportAnalytics_WhenCreated() throws Exception {

        verify(getAnalytics()).reportScreen("Main Activity");
    }

    @Test
    public void ShouldDelegateToInstabug_WhenOnResumeIsCalled() throws Exception {

        activityController.resume();

        verify(getInstaDelegate()).onResume();
    }

    @Test
    public void ShouldDelegateToInstabug_WhenOnPauseIsCalled() throws Exception {

        activityController.pause();

        verify(getInstaDelegate()).onPause();
    }

    @Test
    public void ShouldDelegateToInstabug_WhenOnDestroyIsCalled() throws Exception {

        activityController.destroy();

        verify(getInstaDelegate()).onDestroy();
    }

    @Test
    public void ShouldDelegateToInstabug_WhenDispatchTouchEventIsCalled() throws Exception {

        activity.dispatchTouchEvent(motionEventMock);

        verify(getInstaDelegate()).dispatchTouchEvent(motionEventMock);
    }
}
