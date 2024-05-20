package com.massoftwareengineering.triptracker.network;

public class TripRequest {
    private String notes;

    public TripRequest(String notes) {
        this.notes = notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
