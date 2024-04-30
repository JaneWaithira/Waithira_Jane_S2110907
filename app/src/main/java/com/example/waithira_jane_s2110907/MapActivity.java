package com.example.waithira_jane_s2110907;

// Name                 Jane Waithira
// Student ID           S2110907
// Programme of Study   Mobile Platform Development

import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback{
    private GoogleMap mMap;
    private String cityName;
    private List<Weather> weatherJson;

    private Button closeButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.maps);

        cityName = getIntent().getStringExtra("CityName");

        String weatherJsonString = getIntent().getStringExtra("weatherData");
        if (weatherJsonString != null) {
            // Deserialize the JSON string to a list of Weather objects
            weatherJson = new Gson().fromJson(weatherJsonString, new TypeToken<List<Weather>>(){}.getType());
        } else {
            Log.e("MapActivity", "Weather data is null");
        }

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.maps);
        assert mapFragment != null;
        mapFragment.getMapAsync(this);

        ImageView backArrow = findViewById(R.id.backArrow);
        backArrow.setOnClickListener(v -> {
            finish();
        });

        ImageView weatherDetailIcon = findViewById(R.id.weatherDetailsIcon);
        weatherDetailIcon.setOnClickListener(v -> showWeatherDetails());
    }

    private void showWeatherDetails() {
        View weatherDetailsContainer = findViewById(R.id.weatherDetailsContainer);
        if (weatherDetailsContainer.getVisibility() == View.VISIBLE) {
            // If weather details are already visible, hide them
            weatherDetailsContainer.setVisibility(View.GONE);
        } else {
            // If weather details are not visible, populate and show them
            TextView locationNameMap = findViewById(R.id.locationNameMap);
            TextView minimumTemperatureMap = findViewById(R.id.minimumTemperatureMap);
            TextView maximumTemperatureMap = findViewById(R.id.maximumTemperatureMap);
            RecyclerView threeDayWeather = findViewById(R.id.threeDayWeather);
            ImageView mapWeatherIcon = findViewById(R.id.mapWeatherIcon);

            locationNameMap.setText(cityName);
            if (weatherJson != null && !weatherJson.isEmpty()) {
                Weather currentWeather = weatherJson.get(0);

                // Update UI components with current weather data
                minimumTemperatureMap.setText(String.valueOf(currentWeather.getMinTemperature()));
                maximumTemperatureMap.setText(String.valueOf(currentWeather.getMaxTemperature()));

                // Set weather icon
                String iconName = "ic_weather_" + currentWeather.getWeatherDescription().toLowerCase().replace(" ", "_");
                int iconResource = getResources().getIdentifier(iconName, "drawable", getPackageName());
                if (iconResource != 0) {
                    mapWeatherIcon.setImageResource(iconResource);
                } else {
                    // Set default icon if no icon found
                    mapWeatherIcon.setImageResource(R.drawable.default_weather);
                }

                // Get next two days' weather
                List<Weather> twoDayForecast = weatherJson.subList(1, Math.min(3, weatherJson.size()));

                // Create and set up RecyclerView adapter with next two days' weather data
                TwoDayWeatherAdapter adapter = new TwoDayWeatherAdapter(twoDayForecast);
                threeDayWeather.setAdapter(adapter);
                threeDayWeather.setLayoutManager(new LinearLayoutManager(this));
            } else {
                Log.e("MapActivity", "Weather data is null or empty");
            }

            // Show weather details container
            weatherDetailsContainer.setVisibility(View.VISIBLE);
        }

        closeButton = findViewById(R.id.mapCloseButton);
        if (closeButton != null) {
            closeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
        } else {
            Log.e("MapActivity", "closeButton is null");
        }
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in the current location and move the camera
        LatLng currentLocation = getCityLocation(cityName);
        mMap.addMarker(new MarkerOptions().position(currentLocation).title(cityName));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 10f));
    }

    private LatLng getCityLocation(String cityName) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<android.location.Address> addresses = geocoder.getFromLocationName(cityName, 1);
            if (addresses != null && !addresses.isEmpty()) {
                double latitude = addresses.get(0).getLatitude();
                double longitude = addresses.get(0).getLongitude();
                return new LatLng(latitude, longitude);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        // Default to a generic location if geocoding fails
        return new LatLng(0, 0);
    }
}
