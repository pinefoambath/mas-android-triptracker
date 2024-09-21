package com.massoftwareengineering.triptracker;

import android.content.Context;
import android.content.res.Resources;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.massoftwareengineering.triptracker.R;
import com.massoftwareengineering.triptracker.utils.TrackingUiUtils;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class TrackingUiUtilsTest {

    private Button startTrackingButton;
    private EditText tripNotes;
    private TextView welcomeText, formInstructions;
    private Button submitButton;
    private Context context;
    private Resources resources;

    @Before
    public void setUp() {
        startTrackingButton = Mockito.mock(Button.class);
        tripNotes = Mockito.mock(EditText.class);
        welcomeText = Mockito.mock(TextView.class);
        formInstructions = Mockito.mock(TextView.class);
        submitButton = Mockito.mock(Button.class);
        context = Mockito.mock(Context.class);
        resources = Mockito.mock(Resources.class);

        when(context.getResources()).thenReturn(resources);

        when(ContextCompat.getColor(context, R.color.primaryColor)).thenReturn(0xFF0000);
        when(ContextCompat.getColor(context, R.color.secondaryColor)).thenReturn(0x00FF00);
    }

    @Test
    public void testResetForm() {
        TrackingUiUtils.resetForm(startTrackingButton, tripNotes, welcomeText, formInstructions, submitButton, context);

        verify(tripNotes).setText("");
        verify(formInstructions).setVisibility(View.GONE);
        verify(tripNotes).setVisibility(View.GONE);
        verify(submitButton).setVisibility(View.GONE);
        verify(welcomeText).setVisibility(View.VISIBLE);
        verify(startTrackingButton).setVisibility(View.VISIBLE);
        verify(startTrackingButton).setText(R.string.start_tracking);
        verify(startTrackingButton).setBackgroundColor(0xFF0000);  // Verifying primaryColor (mocked to red)
    }

    @Test
    public void testUpdateUIForTracking() {
        TrackingUiUtils.updateUIForTracking(startTrackingButton, welcomeText, formInstructions, tripNotes, submitButton, context);

        verify(startTrackingButton).setText(R.string.stop_tracking);
        verify(startTrackingButton).setBackgroundColor(0x00FF00);
        verify(welcomeText).setVisibility(View.GONE);
        verify(formInstructions).setVisibility(View.GONE);
        verify(tripNotes).setVisibility(View.GONE);
        verify(submitButton).setVisibility(View.GONE);
    }

    @Test
    public void testUpdateUIForTrackingStopped() {
        TrackingUiUtils.updateUIForTrackingStopped(startTrackingButton, formInstructions, tripNotes, submitButton);
        
        verify(startTrackingButton).setVisibility(View.GONE);
        verify(formInstructions).setVisibility(View.VISIBLE);
        verify(tripNotes).setVisibility(View.VISIBLE);
        verify(submitButton).setVisibility(View.VISIBLE);
    }
}


