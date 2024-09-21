package com.massoftwareengineering.triptracker.utils;

import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.massoftwareengineering.triptracker.R;

public class TrackingUiUtils {

    public static void resetForm(Button startTrackingButton, EditText tripNotes, TextView welcomeText, TextView formInstructions, Button submitButton, Context context) {
        tripNotes.setText("");
        formInstructions.setVisibility(View.GONE);
        tripNotes.setVisibility(View.GONE);
        submitButton.setVisibility(View.GONE);
        welcomeText.setVisibility(View.VISIBLE);
        startTrackingButton.setVisibility(View.VISIBLE);
        startTrackingButton.setText(R.string.start_tracking);
        startTrackingButton.setBackgroundColor(ContextCompat.getColor(context, R.color.primaryColor));
    }

    public static void updateUIForTracking(Button startTrackingButton, TextView welcomeText, TextView formInstructions, EditText tripNotes, Button submitButton, Context context) {
        startTrackingButton.setText(R.string.stop_tracking);
        startTrackingButton.setBackgroundColor(ContextCompat.getColor(context, R.color.secondaryColor));
        welcomeText.setVisibility(View.GONE);
        formInstructions.setVisibility(View.GONE);
        tripNotes.setVisibility(View.GONE);
        submitButton.setVisibility(View.GONE);
    }

    public static void updateUIForTrackingStopped(Button startTrackingButton, TextView welcomeText, TextView formInstructions, EditText tripNotes, Button submitButton) {
        startTrackingButton.setVisibility(View.GONE);
        welcomeText.setVisibility(View.GONE);
        formInstructions.setVisibility(View.VISIBLE);
        tripNotes.setVisibility(View.VISIBLE);
        submitButton.setVisibility(View.VISIBLE);
    }
}


