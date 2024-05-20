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

import com.massoftwareengineering.triptracker.network.TripRepository;
import com.massoftwareengineering.triptracker.network.TripRequest;
import com.massoftwareengineering.triptracker.network.TripService;

public class TripRepositoryTest {

    @Mock
    private TripService tripService;

    @Mock
    private Call<Void> mockCall;

    @Mock
    private TripRepository.TripCallback mockCallback;

    private TripRepository tripRepository;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        tripRepository = new TripRepository(tripService); 
    }

    @Test
    public void submitTrip_success() {
        when(tripService.submitTrip(any(TripRequest.class))).thenReturn(mockCall);
        doAnswer(invocation -> {
            Callback<Void> callback = invocation.getArgument(0);
            callback.onResponse(mockCall, Response.success(null));
            return null;
        }).when(mockCall).enqueue(any());

        tripRepository.submitTrip("Test notes", mockCallback);
        // verify we're returning the onSuccess callback when we had a success response
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

        tripRepository.submitTrip("Test notes", mockCallback);
        // verify we're returning the onError callback when we had a an error response
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

        tripRepository.submitTrip("Test notes", mockCallback);
        // verify we're returning the onError callback with a Throwable for the network error
        verify(mockCallback).onError(any(Throwable.class));
    }
}


