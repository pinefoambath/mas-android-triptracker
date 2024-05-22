package com.massoftwareengineering.triptracker.network;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface TripService {
    @POST("api/tripBaseInformation")
    Call<Void> submitTrip(@Body TripRequest tripRequest);
}
