package com.massoftwareengineering.triptracker;

import android.content.Context;
import android.os.Build;
import android.os.Process;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.uiautomator.UiDevice;
import androidx.test.uiautomator.Until;

import com.massoftwareengineering.triptracker.ui.MainActivity;
import com.massoftwareengineering.triptracker.utils.PermissionUtils;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import androidx.test.uiautomator.By;

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
    public void testLocationPermissionRequestAndGrant() throws Exception {
        revokePermissions(new String[]{
                android.Manifest.permission.ACCESS_FINE_LOCATION,
                android.Manifest.permission.ACCESS_COARSE_LOCATION
        });

        assertFalse("Location permission should not be granted initially", PermissionUtils.hasLocationPermission(context));
        activityRule.getScenario().onActivity(activity ->
                PermissionUtils.requestLocationPermission(activity, 100)
        );

        grantPermissionThroughUi("While using the app");

        Thread.sleep(2000);

        assertTrue("Location permission should be granted", PermissionUtils.hasLocationPermission(context));
    }

    private void revokePermissions(String[] permissions) throws Exception {
        String packageName = context.getPackageName();
        for (String permission : permissions) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                getInstrumentation().getUiAutomation().revokeRuntimePermission(packageName, permission);
            } else {
                System.out.println("Cannot revoke permissions on SDK versions below 23");
            }
        }
        Thread.sleep(1000);
    }

    private void grantPermissionThroughUi(String buttonText) throws Exception {
        device.wait(Until.hasObject(By.text(buttonText)), 5000);

        if (device.hasObject(By.text(buttonText))) {
            device.findObject(By.text(buttonText)).click();
        } else {
            device.wait(Until.hasObject(By.res("android:id/button1")), 5000);
            if (device.hasObject(By.res("android:id/button1"))) {
                device.findObject(By.res("android:id/button1")).click();
            } else {
                System.out.println("Permission dialog did not appear or button not found.");
            }
        }
    }
}


