package com.massoftwareengineering.triptracker;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (shouldBeBackArrowVisible()) {
            final Drawable upArrow = ContextCompat.getDrawable(this, R.drawable.pfeil);
            if (upArrow != null) {
                Objects.requireNonNull(getSupportActionBar()).setHomeAsUpIndicator(upArrow);
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            }
        }

        getSupportFragmentManager().addOnBackStackChangedListener(this::updateBackArrowVisibility);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_container, new TrackingFragment())
                    .commit();
        }
    }

    private void updateBackArrowVisibility() {
        boolean shouldShowBackArrow = shouldBeBackArrowVisible();
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(shouldShowBackArrow);
            if (shouldShowBackArrow) {
                final Drawable upArrow = ContextCompat.getDrawable(this, R.drawable.pfeil);
                getSupportActionBar().setHomeAsUpIndicator(upArrow);
            }
        }
    }

    private boolean shouldBeBackArrowVisible() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            String lastBackStackEntryName = getSupportFragmentManager().getBackStackEntryAt(
                    getSupportFragmentManager().getBackStackEntryCount() - 1).getName();
            return "AboutFragment".equals(lastBackStackEntryName);
        }
        return false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        } else if (id == R.id.action_about) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new AboutFragment())
                    .addToBackStack("AboutFragment")
                    .commit();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
            super.onBackPressed();
        } else {
            getSupportFragmentManager().popBackStack();
        }
    }
}
