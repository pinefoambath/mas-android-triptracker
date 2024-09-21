package com.massoftwareengineering.triptracker.ui;

import android.app.Notification;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import android.app.NotificationManager;

import androidx.core.app.NotificationCompat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.massoftwareengineering.triptracker.R;
import com.massoftwareengineering.triptracker.data.repository.TripRepository;
import com.massoftwareengineering.triptracker.data.service.LocationReceiver;
import com.massoftwareengineering.triptracker.data.service.TrackingService;
import com.massoftwareengineering.triptracker.utils.NotificationUtils;
import com.massoftwareengineering.triptracker.utils.PermissionUtils;
import com.massoftwareengineering.triptracker.utils.TrackingUiUtils;

public class TrackingFragment extends Fragment {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private static final int NOTIFICATION_PERMISSION_REQUEST_CODE = 2;
    private static final int TRACKING_NOTIFICATION_ID = 1001;
    private static final String CHANNEL_ID = "TrackingNotificationChannel";
    private Button startTrackingButton, submitButton;
    private EditText tripNotes;
    private TextView welcomeText, formInstructions;
    private TripViewModel tripViewModel;
    private BroadcastReceiver locationBroadcastReceiver;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_tracking, container, false);
        initViews(rootView);
        resetForm();
        tripViewModel = new ViewModelProvider(this).get(TripViewModel.class);
        setListeners();

        tripViewModel.getIsTracking().observe(getViewLifecycleOwner(), isTracking -> {
            if (isTracking != null && isTracking) {
                updateUIForTracking();
            } else {
                updateUIForTrackingStopped();
            }
        });

        locationBroadcastReceiver = new LocationReceiver(tripViewModel);
        LocalBroadcastManager.getInstance(requireContext()).registerReceiver(
                locationBroadcastReceiver, new IntentFilter(TrackingService.ACTION_LOCATION_BROADCAST)
        );

        LocalBroadcastManager.getInstance(requireContext()).registerReceiver(locationBroadcastReceiver,
                new IntentFilter(TrackingService.ACTION_LOCATION_BROADCAST));

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();

        Boolean isTracking = tripViewModel.getIsTracking().getValue();
        if (Boolean.TRUE.equals(isTracking)) {
            updateUIForTracking();
        } else if (tripViewModel.hasGPSData()) {
            updateUIForTrackingStopped();
        } else {
            resetForm();
        }
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
        if (!PermissionUtils.hasNotificationPermission(requireContext())) {
            PermissionUtils.requestNotificationPermission(requireActivity(), NOTIFICATION_PERMISSION_REQUEST_CODE);
        }

        if (!PermissionUtils.hasLocationPermission(requireContext())) {
            PermissionUtils.requestLocationPermission(requireActivity(), LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            tripViewModel.startTracking();
            startTrackingService();
            showTrackingNotification();
        }
    }

    private void showTrackingNotification() {
        NotificationUtils.createNotificationChannel(
                requireContext(),
                CHANNEL_ID,
                getString(R.string.tracking_notification),
                getString(R.string.notification_description),
                NotificationManager.IMPORTANCE_HIGH
        );

        Notification notification = NotificationUtils.buildNotification(
                requireContext(),
                CHANNEL_ID,
                getString(R.string.tracking_notification),
                getString(R.string.tracking_started),
                R.drawable.notification_white,
                NotificationCompat.PRIORITY_HIGH,
                true
        );

        NotificationUtils.showNotification(requireContext(), TRACKING_NOTIFICATION_ID, notification);
    }

    private void stopTracking() {
        tripViewModel.stopTracking();
        stopTrackingService();
        showToast(getString(R.string.trip_finished));
        cancelTrackingNotification();
    }

    private void cancelTrackingNotification() {
        NotificationUtils.cancelNotification(requireContext(), TRACKING_NOTIFICATION_ID);
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

        TrackingUiUtils.disableSubmitButton(submitButton);

        tripViewModel.submitTrip(notes, new TripRepository.TripCallback() {
            @Override
            public void onSuccess() {
                tripViewModel.clearGPSData();
                resetForm();

                TrackingUiUtils.enableSubmitButton(submitButton);

                showToast(getString(R.string.trip_submitted));
            }

            @Override
            public void onError(int code, String message) {
                TrackingUiUtils.enableSubmitButton(submitButton);

                String errorMessage = getString(R.string.submit_failed, code, message);
                showToast(errorMessage);
            }

            @Override
            public void onError(Throwable t) {
                TrackingUiUtils.enableSubmitButton(submitButton);

                String errorMessage = getString(R.string.error_occurred, t.getMessage());
                showToast(errorMessage);
            }
        });
    }


    private void resetForm() {
        TrackingUiUtils.resetForm(startTrackingButton, tripNotes, welcomeText, formInstructions, submitButton, requireContext());
    }

    private void updateUIForTracking() {
        TrackingUiUtils.updateUIForTracking(startTrackingButton, welcomeText, formInstructions, tripNotes, submitButton, requireContext());
    }

    private void updateUIForTrackingStopped() {
        TrackingUiUtils.updateUIForTrackingStopped(startTrackingButton, welcomeText, formInstructions, tripNotes, submitButton);
    }


    private void showToast(String message) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (PermissionUtils.isPermissionGranted(grantResults)) {
                startTracking();
            } else {
                showToast(getString(R.string.permission_denied));
            }
        }

        if (requestCode == NOTIFICATION_PERMISSION_REQUEST_CODE) {
            if (!PermissionUtils.isPermissionGranted(grantResults)) {
                showToast("Notifications are required for tracking.");
            }
        }
    }
}


