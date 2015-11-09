package org.glucosio.android;

import android.support.test.InstrumentationRegistry;
import android.test.ActivityInstrumentationTestCase2;

import org.glucosio.android.activity.MainActivity;
import org.junit.Before;

/**
 * @author amouly on 10/29/15.
 */
public class FirstEspressoTest extends ActivityInstrumentationTestCase2<MainActivity> {

    private MainActivity mActivity;

    public FirstEspressoTest() {
        super(MainActivity.class);
    }

    @Before
    public void setUp() throws Exception {
        super.setUp();
        injectInstrumentation(InstrumentationRegistry.getInstrumentation());
        mActivity = getActivity();
    }
}
