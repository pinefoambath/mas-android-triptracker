package com.massoftwareengineering.triptracker.data.model;

import com.massoftwareengineering.triptracker.data.model.GPSData;

import java.util.List;

public class TripRequest {
    private String notes;
    private List<GPSData> gpsData;

    public TripRequest(String notes, List<GPSData> gpsData) {
        this.notes = notes;
        this.gpsData = gpsData;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public List<GPSData> getGpsData() {
        return gpsData;
    }

    public void setGpsData(List<GPSData> gpsData) {
        this.gpsData = gpsData;
    }
}
