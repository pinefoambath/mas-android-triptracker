package com.massoftwareengineering.triptracker.ui;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.massoftwareengineering.triptracker.data.model.GPSData;
import com.massoftwareengineering.triptracker.data.repository.TripRepository;

import java.util.ArrayList;
import java.util.List;

public class TripViewModel extends ViewModel {
    private TripRepository tripRepository;
    private MutableLiveData<Boolean> isTracking = new MutableLiveData<>(false);
    private MutableLiveData<List<GPSData>> gpsDataList = new MutableLiveData<>(new ArrayList<>());

    public TripViewModel() {
        tripRepository = new TripRepository();
    }

    public LiveData<Boolean> getIsTracking() {
        return isTracking;
    }

    public void startTracking() {
        isTracking.setValue(true);
    }

    public void stopTracking() {
        isTracking.setValue(false);
    }

    public void addGPSData(GPSData gpsData) {
        List<GPSData> currentData = gpsDataList.getValue();
        if (currentData != null) {
            currentData.add(gpsData);
            gpsDataList.setValue(currentData);
        } else {
            Log.e("TripViewModel", "GPS data list is null");
        }
    }

    public void clearGPSData() {
        gpsDataList.setValue(new ArrayList<>());
    }

    public void submitTrip(String notes, TripRepository.TripCallback callback) {
        List<GPSData> gpsData = gpsDataList.getValue();
        if (gpsData != null && tripRepository != null) {
            tripRepository.submitTrip(notes, gpsData, callback);
        } else {
            callback.onError(new Throwable("GPS data or TripRepository is null"));
        }
    }

    public boolean hasGPSData() {
        List<GPSData> currentData = gpsDataList.getValue();
        return currentData != null && !currentData.isEmpty();
    }
}
