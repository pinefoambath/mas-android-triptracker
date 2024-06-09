package com.massoftwareengineering.triptracker;

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
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.massoftwareengineering.triptracker.network.GPSData;
import com.massoftwareengineering.triptracker.network.TripRepository;

import java.util.ArrayList;
import java.util.List;

public class TrackingFragment extends Fragment {

    private Button startTrackingButton, submitButton;
    private EditText tripNotes;
    private TextView welcomeText, formInstructions;
    private boolean isTracking = false;
    private TripRepository tripRepository;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_tracking, container, false);
        initViews(rootView);
        setListeners();
        tripRepository = new TripRepository();
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
            if (!isTracking) {
                startTracking();
            } else {
                finishTracking();
            }
        });

        submitButton.setOnClickListener(v -> submitTrip());
    }

    private void startTracking() {
        // TODO: start GPS tracking service
        isTracking = true;
        updateUIForTracking();
    }

    private void finishTracking() {
        // TODO: stop GPS tracking service
        isTracking = false;
        updateUIForTrackingStopped();
        showToast(getString(R.string.trip_finished));
    }

    private void submitTrip() {
        String notes = tripNotes.getText().toString().trim();
        if (notes.isEmpty()) {
            showToast(getString(R.string.enter_notes));
            return;
        }

        // Create dummy GPS data
        List<GPSData> gpsData = new ArrayList<>();
        gpsData.add(new GPSData(37.7749, -122.4194, "2024-06-08T19:36:21.82"));
        gpsData.add(new GPSData(34.0522, -118.2437, "2024-06-08T19:36:21.82"));

        tripRepository.submitTrip(notes, gpsData, new TripRepository.TripCallback() {
            @Override
            public void onSuccess() {
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
}
