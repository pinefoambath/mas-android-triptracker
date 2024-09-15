package com.massoftwareengineering.triptracker.data.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.massoftwareengineering.triptracker.data.model.GPSData;
import com.massoftwareengineering.triptracker.ui.TripViewModel;

public class LocationReceiver extends BroadcastReceiver {
    private final TripViewModel tripViewModel;

    public LocationReceiver(TripViewModel tripViewModel) {
        this.tripViewModel = tripViewModel;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        double latitude = intent.getDoubleExtra(TrackingService.EXTRA_LATITUDE, 0);
        double longitude = intent.getDoubleExtra(TrackingService.EXTRA_LONGITUDE, 0);
        long timestamp = intent.getLongExtra(TrackingService.EXTRA_TIMESTAMP, 0);

        GPSData gpsData = new GPSData(latitude, longitude, java.time.Instant.ofEpochMilli(timestamp).toString());
        tripViewModel.addGPSData(gpsData);
    }
}
