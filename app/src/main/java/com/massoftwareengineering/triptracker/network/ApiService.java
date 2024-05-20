package com.massoftwareengineering.triptracker.network;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ApiService {
    @POST("api/tripBaseInformation")
    Call<Void> postTripBaseInformation(@Body TripBaseInformation tripBaseInformation);
}

class TripBaseInformation {
    private String notes;

    public TripBaseInformation(String notes) {
        this.notes = notes;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
