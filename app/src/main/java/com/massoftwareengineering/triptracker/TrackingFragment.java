package com.massoftwareengineering.triptracker;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;


public class TrackingFragment extends Fragment {

    private Button startTrackingButton;
    private LinearLayout tripFinishedForm;
    private EditText tripNotes;
    private boolean isTracking = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_tracking, container, false);
        startTrackingButton = rootView.findViewById(R.id.startButton);
        startTrackingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startTracking();
            }
        });

        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tripFinishedForm = view.findViewById(R.id.tripFinishedForm);
        tripNotes = view.findViewById(R.id.tripNotes);
        startTrackingButton = view.findViewById(R.id.startButton);

        startTrackingButton.setOnClickListener(v -> {
            if (isTracking) {
                finishTracking();
            } else {
                startTracking();
            }
        });

        view.findViewById(R.id.submitButton).setOnClickListener(v -> submitTrip());
    }

    private void startTracking() {
        // todo: actually start the gps service
        isTracking = true;
        startTrackingButton.setText(R.string.tracking);
        startTrackingButton.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.tracking_active));
    }


    private void finishTracking() {
        isTracking = false;
        startTrackingButton.setText(R.string.start_tracking);
        startTrackingButton.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.default_button));

        tripFinishedForm.setVisibility(View.VISIBLE);
        Toast.makeText(requireContext(), "Trip Finished", Toast.LENGTH_SHORT).show();
    }

    private void submitTrip() {
        String notes = tripNotes.getText().toString();
        // TODO: Handle trip submission (post call to backend.)

        tripFinishedForm.setVisibility(View.GONE);
        tripNotes.setText("");
    }
}
