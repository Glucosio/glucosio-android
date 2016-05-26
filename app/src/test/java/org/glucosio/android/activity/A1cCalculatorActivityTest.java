package org.glucosio.android.activity;

import org.glucosio.android.RobolectricTest;
import org.glucosio.android.TestGlucosioApplication;
import org.glucosio.android.presenter.A1CCalculatorPresenter;
import org.junit.Before;
import org.junit.Test;
import org.robolectric.Robolectric;
import org.robolectric.RuntimeEnvironment;

import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

public class A1cCalculatorActivityTest extends RobolectricTest {
    private A1cCalculatorActivity activity;

    @Before
    public void setUp() throws Exception {
        initMocks(this);

        activity = Robolectric.buildActivity(A1cCalculatorActivity.class).create().get();
    }

    @Test
    public void ShouldAskPresenterToCheckUnit_WhenCreated() throws Exception {
        verify(getA1cCalculatorPresenter()).checkGlucoseUnit();
    }

    private A1CCalculatorPresenter getA1cCalculatorPresenter() {
        //noinspection ConstantConditions
        return ((TestGlucosioApplication) RuntimeEnvironment.application).createA1cCalculatorPresenter(null);
    }
}