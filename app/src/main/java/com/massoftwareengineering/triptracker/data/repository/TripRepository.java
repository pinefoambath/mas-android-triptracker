package com.massoftwareengineering.triptracker.data.repository;

import com.massoftwareengineering.triptracker.data.model.GPSData;
import com.massoftwareengineering.triptracker.data.model.TripRequest;
import com.massoftwareengineering.triptracker.data.network.RetrofitClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.util.List;

public class TripRepository {

    private final TripService tripService;

    public TripRepository() {
        tripService = RetrofitClient.getTripService();
    }
    
    public TripRepository(TripService tripService) {
        this.tripService = tripService;
    }

    public void submitTrip(String notes, List<GPSData> gpsData, final TripCallback callback) {
        TripRequest tripRequest = new TripRequest(notes, gpsData);
        Call<Void> call = tripService.submitTrip(tripRequest);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    callback.onSuccess();
                } else {
                    callback.onError(response.code(), response.message());
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                callback.onError(t);
            }
        });
    }

    public interface TripCallback {
        void onSuccess();

        void onError(int code, String message);

        void onError(Throwable t);
    }
}
