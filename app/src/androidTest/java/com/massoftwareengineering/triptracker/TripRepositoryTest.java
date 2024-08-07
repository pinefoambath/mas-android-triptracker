package com.massoftwareengineering.triptracker;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import okhttp3.MediaType;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import com.massoftwareengineering.triptracker.data.model.GPSData;
import com.massoftwareengineering.triptracker.data.repository.TripRepository;
import com.massoftwareengineering.triptracker.data.model.TripRequest;
import com.massoftwareengineering.triptracker.data.repository.TripService;

import java.util.ArrayList;
import java.util.List;

public class TripRepositoryTest {

    @Mock
    private TripService tripService;

    @Mock
    private Call<Void> mockCall;

    @Mock
    private TripRepository.TripCallback mockCallback;

    private TripRepository tripRepository;
    private List<GPSData> dummyGpsData;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        tripRepository = new TripRepository(tripService);

        dummyGpsData = new ArrayList<>();
        dummyGpsData.add(new GPSData(47.2245, 8.88184, "2024-06-08T19:36:21.82"));
        dummyGpsData.add(new GPSData(47.2200, 8.88000, "2024-06-08T19:36:21.82"));
    }

    @Test
    public void submitTrip_success() {
        when(tripService.submitTrip(any(TripRequest.class))).thenReturn(mockCall);
        doAnswer(invocation -> {
            Callback<Void> callback = invocation.getArgument(0);
            callback.onResponse(mockCall, Response.success(null));
            return null;
        }).when(mockCall).enqueue(any());

        tripRepository.submitTrip("Test notes", dummyGpsData, mockCallback);
        // Verify we're returning the onSuccess callback when we had a success response
        verify(mockCallback).onSuccess();
    }

    @Test
    public void submitTrip_failure() {
        when(tripService.submitTrip(any(TripRequest.class))).thenReturn(mockCall);
        ResponseBody responseBody = ResponseBody.create(MediaType.parse("application/json"), "Error");
        doAnswer(invocation -> {
            Callback<Void> callback = invocation.getArgument(0);
            callback.onResponse(mockCall, Response.error(400, responseBody));
            return null;
        }).when(mockCall).enqueue(any());

        tripRepository.submitTrip("Test notes", dummyGpsData, mockCallback);
        // Verify we're returning the onError callback when we had an error response
        verify(mockCallback).onError(eq(400), anyString());
    }

    @Test
    public void submitTrip_networkError() {
        when(tripService.submitTrip(any(TripRequest.class))).thenReturn(mockCall);
        doAnswer(invocation -> {
            Callback<Void> callback = invocation.getArgument(0);
            callback.onFailure(mockCall, new Throwable("Network error"));
            return null;
        }).when(mockCall).enqueue(any());

        tripRepository.submitTrip("Test notes", dummyGpsData, mockCallback);
        // Verify we're returning the onError callback with a Throwable for the network error
        verify(mockCallback).onError(any(Throwable.class));
    }
}

