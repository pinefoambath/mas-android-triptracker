package com.massoftwareengineering.triptracker.network;

public class GPSData {
    private double latitude;
    private double longitude;
    private String timestamp;

    public GPSData(double latitude, double longitude, String timestamp) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.timestamp = timestamp;
    }
}
