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
        this.tripRepository = new TripRepository(); 
    }

    public LiveData<Boolean> getIsTracking() {
        return isTracking;
    }

    public LiveData<List<GPSData>> getGpsDataList() {
        return gpsDataList;
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
}
