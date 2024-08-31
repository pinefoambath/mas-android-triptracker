package com.massoftwareengineering.triptracker.ui;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
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
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.massoftwareengineering.triptracker.R;
import com.massoftwareengineering.triptracker.data.model.GPSData;
import com.massoftwareengineering.triptracker.data.repository.TripRepository;
import com.massoftwareengineering.triptracker.data.service.TrackingService;

public class TrackingFragment extends Fragment {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;

    private Button startTrackingButton, submitButton;
    private EditText tripNotes;
    private TextView welcomeText, formInstructions;
    private TripViewModel tripViewModel;
    private FusedLocationProviderClient fusedLocationClient;
    private BroadcastReceiver locationBroadcastReceiver;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_tracking, container, false);
        initViews(rootView);
        resetForm();
        tripViewModel = new ViewModelProvider(this).get(TripViewModel.class);
        setListeners();
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext());

        tripViewModel.getIsTracking().observe(getViewLifecycleOwner(), isTracking -> {
            if (isTracking != null && isTracking) {
                updateUIForTracking();
            } else {
                updateUIForTrackingStopped();
            }
        });

        locationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                double latitude = intent.getDoubleExtra(TrackingService.EXTRA_LATITUDE, 0);
                double longitude = intent.getDoubleExtra(TrackingService.EXTRA_LONGITUDE, 0);
                long timestamp = intent.getLongExtra(TrackingService.EXTRA_TIMESTAMP, 0);
                GPSData gpsData = new GPSData(latitude, longitude, java.time.Instant.ofEpochMilli(timestamp).toString());
                tripViewModel.addGPSData(gpsData);
            }
        };

        LocalBroadcastManager.getInstance(requireContext()).registerReceiver(locationBroadcastReceiver,
                new IntentFilter(TrackingService.ACTION_LOCATION_BROADCAST));

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        resetForm();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        LocalBroadcastManager.getInstance(requireContext()).unregisterReceiver(locationBroadcastReceiver);
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
            if (Boolean.FALSE.equals(tripViewModel.getIsTracking().getValue())) {
                startTracking();
            } else {
                stopTracking();
            }
        });

        submitButton.setOnClickListener(v -> {
            submitTrip();
        });
    }

    private void startTracking() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.FOREGROUND_SERVICE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(), new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.FOREGROUND_SERVICE,
                    Manifest.permission.ACCESS_COARSE_LOCATION
            }, LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            tripViewModel.startTracking();
            startTrackingService();
        }
    }

    private void stopTracking() {
        tripViewModel.stopTracking();
        stopTrackingService();
        showToast(getString(R.string.trip_finished));
    }

    private void startTrackingService() {
        Intent serviceIntent = new Intent(getActivity(), TrackingService.class);
        serviceIntent.putExtra("inputExtra", "Tracking your trip");
        ContextCompat.startForegroundService(getActivity(), serviceIntent);
    }

    private void stopTrackingService() {
        Intent serviceIntent = new Intent(getActivity(), TrackingService.class);
        getActivity().stopService(serviceIntent);
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
        startTrackingButton.setText(R.string.start_tracking);
        startTrackingButton.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.primaryColor));

    }

    private void updateUIForTracking() {
        startTrackingButton.setText(R.string.stop_tracking);
        startTrackingButton.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.secondaryColor));
        welcomeText.setVisibility(View.GONE);
        formInstructions.setVisibility(View.GONE);
        tripNotes.setVisibility(View.GONE);
        submitButton.setVisibility(View.GONE);
    }

    private void updateUIForTrackingStopped() {
        startTrackingButton.setVisibility(View.GONE);
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


