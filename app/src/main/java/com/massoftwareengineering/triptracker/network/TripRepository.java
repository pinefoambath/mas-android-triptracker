package com.massoftwareengineering.triptracker.network;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TripRepository {

    private TripService tripService;

    public TripRepository() {
        tripService = RetrofitClient.getTripService();
    }

    // Constructor for testing
    public TripRepository(TripService tripService) {
        this.tripService = tripService;
    }

    public void submitTrip(String notes, final TripCallback callback) {
        TripRequest tripRequest = new TripRequest(notes);
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


