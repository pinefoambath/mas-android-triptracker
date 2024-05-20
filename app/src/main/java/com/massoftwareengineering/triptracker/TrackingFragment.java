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

import com.massoftwareengineering.triptracker.network.TripBaseInformation;
import com.massoftwareengineering.triptracker.utils.JsonUtils;

public class TrackingFragment extends Fragment {

    private Button startTrackingButton, submitButton;
    private EditText tripNotes;
    private TextView welcomeText, formInstructions;
    private boolean isTracking = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_tracking, container, false);

        startTrackingButton = rootView.findViewById(R.id.startButton);
        submitButton = rootView.findViewById(R.id.submitButton);
        welcomeText = rootView.findViewById(R.id.welcomeText);
        formInstructions = rootView.findViewById(R.id.formInstructions);
        tripNotes = rootView.findViewById(R.id.tripNotes);
        String jsonRequestBody = JsonUtils.createJsonRequestBody(String.valueOf(tripNotes));


        startTrackingButton.setOnClickListener(v -> {
            if (!isTracking) {
                startTracking();
            } else {
                finishTracking();
            }
        });

        submitButton.setOnClickListener(v -> submitTrip());

        return rootView;
    }

    private void startTracking() {
        // TODO: start GPS tracking service
        isTracking = true;
        startTrackingButton.setText(R.string.stop_tracking);
        startTrackingButton.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.tracking_active));

        welcomeText.setVisibility(View.GONE);
        formInstructions.setVisibility(View.GONE);
        tripNotes.setVisibility(View.GONE);
        submitButton.setVisibility(View.GONE);
    }

    private void finishTracking() {
        // TODO: stop GPS tracking service
        isTracking = false;

        startTrackingButton.setText(R.string.start_tracking);
        startTrackingButton.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.default_button));

        formInstructions.setVisibility(View.VISIBLE);
        tripNotes.setVisibility(View.VISIBLE);
        submitButton.setVisibility(View.VISIBLE);
        Toast.makeText(requireContext(), getString(R.string.trip_finished), Toast.LENGTH_SHORT).show();
    }

    private void submitTrip() {
        String notes = tripNotes.getText().toString();
        TripBaseInformation tripInfo = new TripBaseInformation(notes);

        ApiService apiService = RetrofitClient.getClient("http://192.168.x.x:5281/").create(ApiService.class); // Replace with your actual IP address
        Call<Void> call = apiService.postTripBaseInformation(tripInfo);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, retrofit2.Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(requireContext(), getString(R.string.trip_submitted), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(requireContext(), "Submission failed: " + response.code(), Toast.LENGTH_SHORT).show();
                }

                resetUI();
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(requireContext(), "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                resetUI();
            }
        });
    }

    private void resetUI() {
        tripNotes.setText("");
        formInstructions.setVisibility(View.VISIBLE);
        tripNotes.setVisibility(View.VISIBLE);
        submitButton.setVisibility(View.VISIBLE);
        welcomeText.setVisibility(View.GONE);
        startTrackingButton.setVisibility(View.VISIBLE);
    }

}
