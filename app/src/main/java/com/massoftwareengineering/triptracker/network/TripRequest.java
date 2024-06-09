package com.massoftwareengineering.triptracker.network;

import java.util.List;

public class TripRequest {
    private String notes;
    private List<GPSData> gpsData;

    public TripRequest(String notes, List<GPSData> gpsData) {
        this.notes = notes;
        this.gpsData = gpsData;
    }
}
