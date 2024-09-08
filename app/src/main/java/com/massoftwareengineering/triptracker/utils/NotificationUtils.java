package com.massoftwareengineering.triptracker.utils;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;

import androidx.core.app.NotificationCompat;

public class NotificationUtils {

    public static void createNotificationChannel(Context context, String channelId, String channelName, String channelDescription, int importance) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId, channelName, importance);
            channel.setDescription(channelDescription);
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }
    }

    public static Notification buildNotification(Context context, String channelId, String title, String contentText, int iconResId, int priority, boolean autoCancel) {
        return new NotificationCompat.Builder(context, channelId)
                .setContentTitle(title)
                .setContentText(contentText)
                .setSmallIcon(iconResId)
                .setPriority(priority)
                .setAutoCancel(autoCancel)
                .build();
    }
}
