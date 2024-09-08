package com.massoftwareengineering.triptracker;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import com.massoftwareengineering.triptracker.ui.MainActivity;
import com.massoftwareengineering.triptracker.utils.NotificationUtils;

@RunWith(AndroidJUnit4.class)
public class NotificationUtilsTest {

    @Rule
    public ActivityScenarioRule<MainActivity> activityRule = new ActivityScenarioRule<>(MainActivity.class);

    private Context context;
    private NotificationManager notificationManager;

    @Before
    public void setUp() {
        context = ApplicationProvider.getApplicationContext();
        notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
    }

    @Test
    public void testCreateNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationUtils.createNotificationChannel(
                    context,
                    "test_channel_id",
                    "Test Channel",
                    "Test Channel Description",
                    NotificationManager.IMPORTANCE_HIGH
            );

            NotificationChannel channel = notificationManager.getNotificationChannel("test_channel_id");
            assertNotNull("Notification channel should be created", channel);
            assertEquals("Test Channel", channel.getName());
            assertEquals("Test Channel Description", channel.getDescription());
        }
    }

    @Test
    public void testBuildNotification() {
        Notification notification = NotificationUtils.buildNotification(
                context,
                "test_channel_id",
                "Test Notification Title",
                "Test Notification Content",
                android.R.drawable.ic_dialog_info,
                Notification.PRIORITY_HIGH,
                true
        );

        assertNotNull("Notification should not be null", notification);
        assertEquals("Test Notification Title", notification.extras.getString(Notification.EXTRA_TITLE));
        assertEquals("Test Notification Content", notification.extras.getString(Notification.EXTRA_TEXT));
        assertEquals(Notification.PRIORITY_HIGH, notification.priority);
    }
}
