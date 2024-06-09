package com.massoftwareengineering.triptracker;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.not;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

import com.massoftwareengineering.triptracker.ui.MainActivity;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class TrackingFragmentTest {

    @Rule
    public ActivityTestRule<MainActivity> activityRule = new ActivityTestRule<>(MainActivity.class);

    @Test
    public void startAndStopTracking() {
        onView(withId(R.id.startButton)).check(matches(isDisplayed()));
        onView(withId(R.id.startButton)).check(matches(withText(R.string.start_tracking)));
        onView(withId(R.id.formInstructions)).check(matches(not(isDisplayed())));

        onView(withId(R.id.startButton)).perform(click());
        onView(withId(R.id.startButton)).check(matches(withText(R.string.stop_tracking)));
        onView(withId(R.id.formInstructions)).check(matches(not(isDisplayed())));

        onView(withId(R.id.startButton)).perform(click());
        onView(withId(R.id.startButton)).check(matches(withText(R.string.start_tracking)));
        onView(withId(R.id.formInstructions)).check(matches(isDisplayed()));
    }
}
