package com.massoftwareengineering.triptracker.data.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.os.IBinder;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.massoftwareengineering.triptracker.R;
import com.massoftwareengineering.triptracker.ui.MainActivity;
import com.massoftwareengineering.triptracker.utils.NotificationUtils;
import com.massoftwareengineering.triptracker.utils.PermissionUtils;

public class TrackingService extends Service {

    private static final String CHANNEL_ID = "TrackingServiceChannel";
    private static final int LOCATION_UPDATE_INTERVAL = 600000; // 10 minutes
    public static final String ACTION_LOCATION_BROADCAST = "TrackingServiceLocationBroadcast";
    public static final String EXTRA_LATITUDE = "extra_latitude";
    public static final String EXTRA_LONGITUDE = "extra_longitude";
    public static final String EXTRA_TIMESTAMP = "extra_timestamp";

    private FusedLocationProviderClient fusedLocationClient;
    private LocationCallback locationCallback;

    @Override
    public void onCreate() {
        super.onCreate();

        NotificationUtils.createNotificationChannel(
                this,
                CHANNEL_ID,
                getString(R.string.tracking_service_channel_name),
                getString(R.string.tracking_service_channel_description),
                NotificationManager.IMPORTANCE_HIGH
        );

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                for (Location location : locationResult.getLocations()) {
                    double latitude = location.getLatitude();
                    double longitude = location.getLongitude();
                    long timestamp = location.getTime();
                    sendLocationBroadcast(latitude, longitude, timestamp);
                }
            }
        };
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String input = intent.getStringExtra("inputExtra");

        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE);

        Notification notification = NotificationUtils.buildNotification(
                this,
                CHANNEL_ID,
                getString(R.string.tracking_service_notification_title),
                input,
                R.drawable.notification_white,
                NotificationCompat.PRIORITY_MAX,
                true
        );

        startForeground(1, notification);

        startLocationUpdates();

        return START_STICKY;
    }

    private void startLocationUpdates() {
        if (PermissionUtils.hasLocationPermission(this)) {
            LocationRequest locationRequest = LocationRequest.create();
            locationRequest.setInterval(LOCATION_UPDATE_INTERVAL);
            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

            try {
                fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);
            } catch (SecurityException e) {
                stopSelf();
            }
        } else {
            stopSelf();
        }
    }


    private void stopLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(locationCallback);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopLocationUpdates();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void sendLocationBroadcast(double latitude, double longitude, long timestamp) {
        Intent intent = new Intent(ACTION_LOCATION_BROADCAST);
        intent.putExtra(EXTRA_LATITUDE, latitude);
        intent.putExtra(EXTRA_LONGITUDE, longitude);
        intent.putExtra(EXTRA_TIMESTAMP, timestamp);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }
}
