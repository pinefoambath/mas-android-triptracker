package com.massoftwareengineering.triptracker.ui;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.massoftwareengineering.triptracker.R;
import com.massoftwareengineering.triptracker.data.model.GPSData;
import com.massoftwareengineering.triptracker.data.repository.TripRepository;

public class TrackingFragment extends Fragment {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;

    private Button startTrackingButton, submitButton;
    private EditText tripNotes;
    private TextView welcomeText, formInstructions;
    private TripViewModel tripViewModel;
    private FusedLocationProviderClient fusedLocationClient;
    private LocationCallback locationCallback;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_tracking, container, false);
        initViews(rootView);
        tripViewModel = new ViewModelProvider(this).get(TripViewModel.class);
        setListeners();
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext());

        tripViewModel.getIsTracking().observe(getViewLifecycleOwner(), isTracking -> {
            if (isTracking) {
                updateUIForTracking();
            } else {
                updateUIForTrackingStopped();
            }
        });

        return rootView;
    }

    private void initViews(View rootView) {
        startTrackingButton = rootView.findViewById(R.id.startButton);
        submitButton = rootView.findViewById(R.id.submitButton);
        welcomeText = rootView.findViewById(R.id.welcomeText);
        formInstructions = rootView.findViewById(R.id.formInstructions);
        tripNotes = rootView.findViewById(R.id.tripNotes);
    }

    private void setListeners() {
        startTrackingButton.setOnClickListener(v -> {
            if (tripViewModel.getIsTracking().getValue() == Boolean.FALSE) {
                startTracking();
            } else {
                stopTracking();
            }
        });

        submitButton.setOnClickListener(v -> submitTrip());
    }

    private void startTracking() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            tripViewModel.startTracking();
            startLocationUpdates();
        }
    }

    private void stopTracking() {
        tripViewModel.stopTracking();
        stopLocationUpdates();
        showToast(getString(R.string.trip_finished));
    }

    private void startLocationUpdates() {
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setInterval(10000); // 10 seconds
        locationRequest.setFastestInterval(5000); // 5 seconds
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                for (android.location.Location location : locationResult.getLocations()) {
                    GPSData gpsData = new GPSData(location.getLatitude(), location.getLongitude(), java.time.Instant.ofEpochMilli(location.getTime()).toString());
                    tripViewModel.addGPSData(gpsData);
                }
            }
        };

        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);
    }

    private void stopLocationUpdates() {
        if (locationCallback != null) {
            fusedLocationClient.removeLocationUpdates(locationCallback);
        }
    }

    private void submitTrip() {
        String notes = tripNotes.getText().toString().trim();
        if (notes.isEmpty()) {
            showToast(getString(R.string.enter_notes));
            return;
        }

        tripViewModel.submitTrip(notes, new TripRepository.TripCallback() {
            @Override
            public void onSuccess() {
                tripViewModel.clearGPSData();
                resetForm();
                showToast(getString(R.string.trip_submitted));
            }

            @Override
            public void onError(int code, String message) {
                String errorMessage = getString(R.string.submit_failed, code, message);
                showToast(errorMessage);
            }

            @Override
            public void onError(Throwable t) {
                String errorMessage = getString(R.string.error_occurred, t.getMessage());
                showToast(errorMessage);
            }
        });
    }

    private void resetForm() {
        tripNotes.setText("");
        formInstructions.setVisibility(View.GONE);
        tripNotes.setVisibility(View.GONE);
        submitButton.setVisibility(View.GONE);
        welcomeText.setVisibility(View.VISIBLE);
        startTrackingButton.setVisibility(View.VISIBLE);
    }

    private void updateUIForTracking() {
        startTrackingButton.setText(R.string.stop_tracking);
        startTrackingButton.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.tracking_active));
        welcomeText.setVisibility(View.GONE);
        formInstructions.setVisibility(View.GONE);
        tripNotes.setVisibility(View.GONE);
        submitButton.setVisibility(View.GONE);
    }

    private void updateUIForTrackingStopped() {
        startTrackingButton.setText(R.string.start_tracking);
        startTrackingButton.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.default_button));
        formInstructions.setVisibility(View.VISIBLE);
        tripNotes.setVisibility(View.VISIBLE);
        submitButton.setVisibility(View.VISIBLE);
    }

    private void showToast(String message) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startTracking();
            } else {
                showToast(getString(R.string.permission_denied));
            }
        }
    }
}

