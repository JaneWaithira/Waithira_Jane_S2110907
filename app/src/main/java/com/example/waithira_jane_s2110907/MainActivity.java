package com.example.waithira_jane_s2110907;

// Name                 Jane Waithira
// Student ID           S2110907
// Programme of Study   Mobile Platform Development

import static okhttp3.internal.Internal.instance;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.widget.SearchView;

import android.widget.ImageView;
import android.widget.Toast;

import org.xmlpull.v1.XmlPullParserException;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import okhttp3.internal.Internal;

public class MainActivity extends AppCompatActivity implements WeatherAdapter.OnItemClickListener{
    private Button startButton;
    private WeatherAdapter weatherAdapter;
    private FrameLayout container;
    private List<Weather> weatherList = new ArrayList<>();
    private List<Weather> originalWeatherList = new ArrayList<>();

    private String temperatureUnit;



    private String cityName;

    private final String[] dailyWeatherUrls = {
            "https://weather-broker-cdn.api.bbci.co.uk/en/observation/rss/5128581",
            "https://weather-broker-cdn.api.bbci.co.uk/en/observation/rss/2643743",
            "https://weather-broker-cdn.api.bbci.co.uk/en/observation/rss/2648579",
            "https://weather-broker-cdn.api.bbci.co.uk/en/observation/rss/287286",
            "https://weather-broker-cdn.api.bbci.co.uk/en/observation/rss/934154",
            "https://weather-broker-cdn.api.bbci.co.uk/en/observation/rss/1185241"
    };

    private final String[] threeDayForecastUrls = {
            "https://weather-broker-cdn.api.bbci.co.uk/en/forecast/rss/3day/5128581",
            "https://weather-broker-cdn.api.bbci.co.uk/en/forecast/rss/3day/2648579"

    };

    private boolean isNetworkAvailable(){
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
            return activeNetworkInfo != null && activeNetworkInfo.isConnected();
        }
        return false;
    }

    private void showNetworkConnection() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("No Internet Connection");
        builder.setMessage("Please enable WiFi or mobile data to access weather information.");
        builder.setPositiveButton("Settings", new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //Redirect user to Settings page
                Intent intent  = new Intent(Settings.ACTION_WIFI_SETTINGS);startActivity(intent);

            }
        });
        builder.setNegativeButton("Cancel", null);
        builder.setCancelable(false);
        builder.show();
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        container = findViewById(R.id.container);
        startButton = findViewById(R.id.startButton);
        startButton.setOnClickListener(this::startProgress);


    }


    private void startProgress(View view) {
        // Check for internet connectivity
        if (isNetworkAvailable()) {
            // Hide or remove the views you want to hide or remove
            findViewById(R.id.salutation).setVisibility(View.GONE);
            findViewById(R.id.startButton).setVisibility(View.GONE);
            findViewById(R.id.homeIcon).setVisibility(View.GONE);

            // Fetch and process daily weather data
            for (int i = 0; i < dailyWeatherUrls.length; i++) {
                new Thread(new Task(dailyWeatherUrls[i], cityName)).start(); // Pass URL and cityName
            }
        } else {
            // Show network connection advice as a dialog box
            showNetworkConnection();
        }

    }


    @Override
    public void onItemClick(Weather weather) {
        if (weather != null) {
            String cityName = weather.getCityName();

            String locationId = CityIdResolver.getLocationIdFromCitiesFile(MainActivity.this, cityName);

            if (locationId != null && !locationId.isEmpty()) {
                String threeDayForecastUrl = WeatherParser.constructThreeDayForecastUrl(locationId);

                if (threeDayForecastUrl != null) {
                    Intent intent = new Intent(MainActivity.this, DetailedWeather.class);
                    intent.putExtra("CityName", cityName);
                    intent.putExtra("ThreeDayForecastUrl", threeDayForecastUrl);
                    startActivity(intent);
                } else {
                    Log.e("MainActivity", "Failed to construct 3-day forecast URL");
                    Toast.makeText(MainActivity.this, "Error occurred. Please try again later.", Toast.LENGTH_SHORT).show();
                }
            } else {
                Log.e("MainActivity", "Location ID not found for cityName: " + cityName);
                Toast.makeText(MainActivity.this, "Error occurred. Please try again later.", Toast.LENGTH_SHORT).show();
            }
        } else {
            Log.e("MainActivity", "Invalid weather object");
            Toast.makeText(MainActivity.this, "Error occurred. Please try again later.", Toast.LENGTH_SHORT).show();
        }
    }


    public void fetchSearchedWeather(String weatherUrl, String query) {

        Log.d("MainActivity", "Search function called for weather URL: " + weatherUrl);
        String cityName = " ";
        new Thread(() -> {
            try {
                Log.d("MainActivity", "Fetching weather data from URL: " + weatherUrl);
                URL url = new URL(weatherUrl);
                URLConnection connection = url.openConnection();
                InputStream inputStream = connection.getInputStream();
                List<Weather> searchedWeatherList= WeatherParser.parseWeatherData(inputStream, query);
                inputStream.close(); // Close the stream

                runOnUiThread(() -> {
                    // Update UI with the fetched weather data
                    if (searchedWeatherList != null && !searchedWeatherList.isEmpty()) {
                        weatherList.clear();
                        weatherList.addAll(searchedWeatherList);

                        // Notify adapter of data change
                        weatherAdapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(MainActivity.this, "No weather data available for the searched city", Toast.LENGTH_SHORT).show();
                    }

                });
            } catch (IOException e) {
                Log.e("MainActivity", "Error fetching weather data: " + e.getMessage());
                runOnUiThread(() -> {
                    Toast.makeText(MainActivity.this, "Error fetching weather data", Toast.LENGTH_SHORT).show();
                    // Hide progress indicator or loading state
                    // (e.g., hide ProgressBar)
                });
            } catch (XmlPullParserException e) {
                Log.e("MainActivity", "Error parsing weather data: " + e.getMessage());
                runOnUiThread(() -> {
                    Toast.makeText(MainActivity.this, "Error parsing weather data", Toast.LENGTH_SHORT).show();
                });
            }
        }).start();

    }


    private class Task implements Runnable {
        private final String url;

        private final String cityName;
        private final StringBuilder result;

        public Task(String aurl, String cityName) {
            url = aurl;
            this.cityName = cityName;
            result = new StringBuilder();
        }

        @Override
        public void run() {
            Log.d("MyTag", "Task run method called");
            StringBuilder result = new StringBuilder();
            URL aurl;
            URLConnection yc;
            BufferedReader in = null;
            String inputLine;

            Log.e("MyTag", "in run");

            try {
                Log.e("MyTag", "in try");
                aurl = new URL(url);
                yc = aurl.openConnection();
                in = new BufferedReader(new InputStreamReader(yc.getInputStream()));
                while ((inputLine = in.readLine()) != null) {
                    result.append(inputLine);
                    Log.d("MyTag","Received URL: " + url);
                }
            } catch (IOException ae) {
                Log.e("MyTag", "IOException occurred while processing URL: " + url +  ": " + ae.getMessage());
                runOnUiThread(() -> Toast.makeText(MainActivity.this, "Error occurred. Please try again later.", Toast.LENGTH_SHORT).show());
            } finally {
                try {
                    if (in != null) {
                        in.close();
                    }
                } catch (IOException e) {
                    Log.e("MyTag", "Error closing BufferedReader: " + e.getMessage());
                }
            }
            String cityName = WeatherParser.extractcityName(MainActivity.this, url);
            MainActivity.this.cityName = cityName;
            Log.d("MyTag", "City Name: " + cityName);
            processData(result.toString(), cityName);

        }

        private void processData(String data, String cityName) {
            try {
                // Parse weather data
                ByteArrayInputStream inputStream = new ByteArrayInputStream(data.getBytes());
                List<Weather> newWeatherList = WeatherParser.parseWeatherData(inputStream, cityName);
                Log.d("MainActivity", "WeatherList: " + newWeatherList);

                if (newWeatherList != null && !newWeatherList.isEmpty()) {
                    // Update weather list
                    weatherList.addAll(newWeatherList);
                    originalWeatherList = new ArrayList<>(weatherList);
                    Log.d("MainActivity", "Original WeatherList: " + originalWeatherList);



                    // Display weather data in RecyclerView
                    runOnUiThread(() -> {
                        // Remove all views from container
                        container.removeAllViews();

                        // Inflate home_weather.xml layout
                        View homeWeatherView = LayoutInflater.from(MainActivity.this).inflate(R.layout.home_weather, container, false);

                        // Debugging: Verify if homeWeatherView is inflated correctly
                        if (homeWeatherView == null) {
                            Log.e("MainActivity", "homeWeatherView is null");
                            return;
                        }

                        SearchView searchView = homeWeatherView.findViewById(R.id.searchView);

                        SearchHandler searchHandler = new SearchHandler(MainActivity.this, originalWeatherList);
                        searchView.setOnQueryTextListener(searchHandler);

                        searchView.setOnCloseListener(() -> {
                            searchHandler.clearSearchQuery();
                            return false;
                        });

                        // Debugging: Log a message to ensure setOnClickListener is applied
                        Log.d("MainActivity", "setOnClickListener applied to settingsIcon");


                        ImageView settingsIcon = homeWeatherView.findViewById(R.id.settingsIcon);
                        settingsIcon.setOnClickListener(v -> {
                            // Create an intent to start the SettingsActivity
                            Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                            intent.putExtra("weatherList", new ArrayList<>(weatherList));
                            startActivity(intent);
                        });

                        // Find RecyclerView in home_weather.xml layout
                        RecyclerView weatherRecyclerView = homeWeatherView.findViewById(R.id.weatherRecyclerView);

                        // Set up RecyclerView
                        weatherAdapter = new WeatherAdapter(weatherList, MainActivity.this);
                        weatherRecyclerView.setAdapter(weatherAdapter);
                        weatherRecyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));

                        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                        // Add homeWeatherView to the container
                        container.addView(homeWeatherView, layoutParams);
                        // Find the settings icon view

                    });
                } else {
                    runOnUiThread(() -> Toast.makeText(MainActivity.this, "No weather data available", Toast.LENGTH_SHORT).show());
                }
            } catch (Exception e) {
                Log.e("MainActivity", "Error processing weather data: " + e.getMessage());
                runOnUiThread(() -> Toast.makeText(MainActivity.this, "Error processing weather data", Toast.LENGTH_SHORT).show());
            }
        }
    }

    public void updateWeatherData(List<Weather> newWeatherList) {
        // Clear existing weather list
        weatherList.clear();
        weatherList.addAll(newWeatherList);
        Log.d("MainActivity", "Weather List after clearing Search Query: " + weatherList);
        // Notify adapter of data change
        weatherAdapter.notifyDataSetChanged();
    }



}

