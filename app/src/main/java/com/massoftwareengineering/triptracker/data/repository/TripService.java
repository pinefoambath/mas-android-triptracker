package com.massoftwareengineering.triptracker.data.repository;

import com.massoftwareengineering.triptracker.data.model.TripRequest;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface TripService {
    @POST("api/tripBaseInformation")
    Call<Void> submitTrip(@Body TripRequest tripRequest);
}
