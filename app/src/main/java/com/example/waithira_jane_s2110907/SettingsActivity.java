package com.example.waithira_jane_s2110907;

// Name                 Jane Waithira
// Student ID           S2110907
// Programme of Study   Mobile Platform Development

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.io.Serializable;
import java.util.Calendar;
import java.util.List;


public class SettingsActivity extends AppCompatActivity {
    private Spinner spinnerTemperatureUnits;
    private Spinner spinnerRefreshTime;
    private List<Weather> weatherList;
    private WeatherAdapter weatherAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.weather_settings);

        // Initialize spinner
        spinnerTemperatureUnits = findViewById(R.id.spinnerTemperatureUnits);
        spinnerRefreshTime = findViewById(R.id.refreshTime);

        // Retrieve weather data from intent extras
        Intent intent = getIntent();
        weatherList = (List<Weather>) intent.getSerializableExtra("weatherList");

        weatherAdapter = new WeatherAdapter(weatherList);

        // Spinner adapters
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.temperature_units, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTemperatureUnits.setAdapter(adapter);

        ArrayAdapter<CharSequence> refreshAdapter = ArrayAdapter.createFromResource(this, R.array.refresh_settings, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerRefreshTime.setAdapter(refreshAdapter);

        // Retrieve the selected temperature unit from SharedPreferences and set it as the initial selection
        SharedPreferences prefs = getSharedPreferences("TemperatureUnitSettings", MODE_PRIVATE);
        String selectedTemperatureUnit = prefs.getString("temperature_unit", "Celsius"); // Default to Celsius if not found
        int index = adapter.getPosition(selectedTemperatureUnit);
        spinnerTemperatureUnits.setSelection(index);

        // Handle item selection
        spinnerTemperatureUnits.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedUnit = parent.getItemAtPosition(position).toString();
                Log.d(TAG, "Selected temperature unit: " + selectedUnit);

                // Save the selected temperature unit in SharedPreferences
                saveTemperatureUnit(selectedUnit);

                // Update temperature unit in the WeatherAdapter
                weatherAdapter.setTemperatureUnit(selectedUnit);

                Toast.makeText(SettingsActivity.this, "Selected temperature unit: " + selectedUnit, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });

        // Item selection for Refresh times
        spinnerRefreshTime.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedInterval = parent.getItemAtPosition(position).toString();
                Log.d("SettingsActivity", "Selected auto refresh interval: " + selectedInterval);
                saveRefreshInterval(selectedInterval);

                scheduleAlarm(selectedInterval);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {// Do nothing

            }
        });

        setInitialRefreshTimeSelection();

        // Handle back arrow click
        ImageView backArrow = findViewById(R.id.backArrow);
        backArrow.setOnClickListener(v -> {
            String selectedUnit = spinnerTemperatureUnits.getSelectedItem().toString();
            Log.d(TAG, "Back arrow clicked. Selected temperature unit: " + selectedTemperatureUnit);
            saveTemperatureUnit(selectedUnit);

            // Finish the SettingsActivity
            finish();
        });
    }

    private void saveTemperatureUnit(String selectedTemperatureUnit) {
        SharedPreferences.Editor editor = getSharedPreferences("TemperatureUnitSettings", MODE_PRIVATE).edit();
        editor.putString("temperature_unit", selectedTemperatureUnit);
        editor.apply();
    }

    private void setInitialRefreshTimeSelection() {
        SharedPreferences prefs = getSharedPreferences("AutoRefreshSettings", MODE_PRIVATE);
        String selectedInterval = prefs.getString("refresh_interval", "Default(08:00 and 20:00");
        int index = ArrayAdapter.createFromResource(this, R.array.refresh_settings, android.R.layout.simple_spinner_item).getPosition(selectedInterval);
        spinnerRefreshTime.setSelection(index);
    }



    private void scheduleAlarm(String selectedInterval) {
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        Intent intent = new Intent(this, WeatherUpdateReceiver.class);

        // Create an Intent for the WeatherUpdateReceiver
        PendingIntent alarmIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_IMMUTABLE);

        // Convert the selected interval to milliseconds
        long intervalMillis = convertIntervalToMills(selectedInterval);

        // Schedule repeating alarm with selected interval
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + intervalMillis, intervalMillis, alarmIntent);

        // Save selected interval in SharedPreferences
        saveRefreshInterval(selectedInterval);
    }

    private long convertIntervalToMills(String selectedInterval) {
        // Convert the selected interval to milliseconds
        long intervalMillis = 0;

        switch (selectedInterval) {
            case "Default(08:00 and 20:00)":
                // Calculate the time until the next default refresh (in milliseconds)
                intervalMillis = calculateTimeUntilNextDefaultRefresh();
                break;
            case "Every 3 hours":
                intervalMillis = 3 * 60 * 60 * 1000; // 3 hours in milliseconds
                break;
            case "Every 6 hours":
                intervalMillis = 6 * 60 * 60 * 1000; // 6 hours in milliseconds
                break;
        }

        return intervalMillis;

    }

    private long calculateTimeUntilNextDefaultRefresh() {
        Calendar calendar = Calendar.getInstance();
        // Check if current time is after 8 PM
        if (calendar.get(Calendar.HOUR_OF_DAY) >= 20) {
            // If it is after 8PM, set next refresh to 8 AM next day
            calendar.add(Calendar.DAY_OF_YEAR, 1);
            calendar.set(Calendar.HOUR_OF_DAY, 8);
        } else {
            // If before 8PM, set refresh time to 8 PM
            calendar.set(Calendar.HOUR_OF_DAY, 20);
        }
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        long currentTimeMillis = System.currentTimeMillis();
        long nextRefreshTimeMillis = calendar.getTimeInMillis();
        return nextRefreshTimeMillis - currentTimeMillis;

    }

    private void saveRefreshInterval(String selectedInterval) {
        SharedPreferences.Editor editor = getSharedPreferences("AutoRefreshSettings", MODE_PRIVATE).edit();
        editor.putString("refresh_interval", selectedInterval);
        editor.apply();
    }
}
