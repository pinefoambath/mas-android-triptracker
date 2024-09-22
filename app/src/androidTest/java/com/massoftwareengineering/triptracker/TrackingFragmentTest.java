package com.massoftwareengineering.triptracker;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import android.util.Log;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.not;

import com.massoftwareengineering.triptracker.ui.MainActivity;

@RunWith(AndroidJUnit4.class)
public class TrackingFragmentTest {

    private static final String TAG = "TrackingFragmentTest";

    @Rule
    public ActivityScenarioRule<MainActivity> activityRule = new ActivityScenarioRule<>(MainActivity.class);

    @Test
    public void startAndStopTracking() {
        onView(withId(R.id.startButton)).check(matches(isDisplayed()));
        onView(withId(R.id.startButton)).check(matches(withText(R.string.start_tracking)));
        onView(withId(R.id.formInstructions)).check(matches(not(isDisplayed())));

        onView(withId(R.id.startButton)).perform(click());
        onView(withId(R.id.startButton)).perform(click());

        onView(withId(R.id.submitButton)).check(matches(withText(R.string.submit_trip)));
    }

    @Test
    public void submitTripWithoutNotes() {
        onView(withId(R.id.startButton)).perform(click());
        waitForUIUpdate();
        onView(withId(R.id.startButton)).perform(click());
        waitForUIUpdate();

        onView(withId(R.id.submitButton)).perform(click());
        onView(withId(R.id.tripNotes)).check(matches(isDisplayed()));
    }


    private void waitForUIUpdate() {
        try {
            Log.d(TAG, "Sleeping for 1 second to wait for UI update");
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

