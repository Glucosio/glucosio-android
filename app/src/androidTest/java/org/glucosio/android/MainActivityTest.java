package org.glucosio.android;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.glucosio.android.activity.MainActivity;
import org.junit.FixMethodOrder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

/**
 * @author piotr on 29/10/15.
 */
@RunWith(AndroidJUnit4.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class MainActivityTest {

    @Rule
    public ActivityTestRule<MainActivity> mRule = new ActivityTestRule<>(MainActivity.class);

    HelloActivityTest previousTest = new HelloActivityTest();

    private void goThroughHelloActivity() throws InterruptedException {
        previousTest.check_004_IfICanEnterMyAgeUsingHelloAgeEditText();
        previousTest.check_009_IfICanSubmitAnyData();
    }

    @Test
    public void check_001_checkIfToolbarIsDisplayed() throws InterruptedException {
        goThroughHelloActivity();
        onView(withId(R.id.toolbar)).check(matches(isDisplayed()));
    }
}