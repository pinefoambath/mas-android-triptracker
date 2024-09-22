package com.massoftwareengineering.triptracker;

import android.content.Context;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.uiautomator.By;
import androidx.test.uiautomator.UiDevice;
import androidx.test.uiautomator.Until;

import com.massoftwareengineering.triptracker.ui.MainActivity;
import com.massoftwareengineering.triptracker.utils.PermissionUtils;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;
import static org.junit.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class PermissionUtilsTest {

    @Rule
    public ActivityScenarioRule<MainActivity> activityRule = new ActivityScenarioRule<>(MainActivity.class);

    private Context context;
    private UiDevice device;

    @Before
    public void setUp() {
        context = ApplicationProvider.getApplicationContext();
        device = UiDevice.getInstance(getInstrumentation());
    }

    @Test
    public void testRequestLocationPermission() throws Exception {
        boolean hasPermission = PermissionUtils.hasLocationPermission(context);
        if (!hasPermission) {
            activityRule.getScenario().onActivity(activity ->
                    PermissionUtils.requestLocationPermission(activity, 100)
            );

            device.wait(Until.hasObject(By.text("Allow")), 5000);

            if (device.hasObject(By.text("Allow"))) {
                device.findObject(By.text("Allow")).click();
            } else {
                System.out.println("Permission dialog did not appear or 'Allow' button not found.");
            }

            Thread.sleep(3000);

            assertTrue("Location permission should be granted", PermissionUtils.hasLocationPermission(context));
        }
    }
}



