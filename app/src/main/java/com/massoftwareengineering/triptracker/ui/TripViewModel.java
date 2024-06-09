package com.massoftwareengineering.triptracker.ui;

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

    public LiveData<List<GPSData>> getGpsDataList() {
        return gpsDataList;
    }

    public void startTracking() {
        isTracking.setValue(true);
        // Start GPS tracking logic
    }

    public void stopTracking() {
        isTracking.setValue(false);
        // Stop GPS tracking logic
    }

    public void addGPSData(GPSData gpsData) {
        List<GPSData> currentData = gpsDataList.getValue();
        if (currentData != null) {
            currentData.add(gpsData);
            gpsDataList.setValue(currentData);
        }
    }

    public void submitTrip(String notes, TripRepository.TripCallback callback) {
        tripRepository.submitTrip(notes, gpsDataList.getValue(), callback);
    }
}
