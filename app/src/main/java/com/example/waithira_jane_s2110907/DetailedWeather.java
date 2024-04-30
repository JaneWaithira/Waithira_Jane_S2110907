package com.example.waithira_jane_s2110907;

// Name                 Jane Waithira
// Student ID           S2110907
// Programme of Study   Mobile Platform Development


import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.airbnb.lottie.LottieCompositionFactory;
import com.airbnb.lottie.LottieAnimationView;
import com.google.gson.Gson;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.net.HttpURLConnection;
import java.net.URL;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class DetailedWeather extends AppCompatActivity {

    private TextView dateTextView;
    private TextView  locationName;

    private TextView  weatherDescription;

    private TextView minTemperature;

    private TextView maxTemperature;
    private TextView humidity;
    private TextView wind;
    private TextView uvRisk;
    private TextView visibility;
    private TextView sunrise;
    private TextView sunset;

    private TextView windDirection;
    private TextView pressure;
    private List<Weather> weatherList;
    private ImageView focusedWeatherIcon;

    private String threeDayForecastUrl;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.focused_weather);


        // Retrieve data from the Intent
        String cityName = getIntent().getStringExtra("CityName");
        threeDayForecastUrl = getIntent().getStringExtra("ThreeDayForecastUrl");
        Log.d("DetailedWeather", "City Name: " + cityName);
        Log.d("DetailedWeather", "Three Day Forecast URL: " + threeDayForecastUrl);


        weatherList = new ArrayList<>();
        fetchWeatherData(cityName, threeDayForecastUrl);

        ImageView backArrow = findViewById(R.id.backArrow);
        backArrow.setOnClickListener(v -> {
            finish();
        });

        ImageView mapIcon = findViewById(R.id.map);

        mapIcon.setOnClickListener(v -> {
            if (weatherList != null) {
                String weatherJson = new Gson().toJson(weatherList);
                Intent intent = new Intent(DetailedWeather.this, MapActivity.class);
                intent.putExtra("CityName", cityName);
                intent.putExtra("weatherData", weatherJson);
                startActivity(intent);
                Log.d("DetailedWeather", "Weather data: " + weatherJson);
            }else {
                Log.e("DetailedWeather", "Weather data is null");
            }

        });

        // Fetch and display weather data in a background thread
        dateTextView = findViewById(R.id.date);
        locationName = findViewById(R.id.locationName);
        weatherDescription = findViewById(R.id.weatherDescriptionText);
        minTemperature = findViewById(R.id.minimumTemperature);
        maxTemperature = findViewById(R.id.maximumTemperature);
        humidity = findViewById(R.id.humidityDescription);
        wind = findViewById(R.id.windValue);
        uvRisk = findViewById(R.id.uvRiskValue);
        visibility = findViewById(R.id.visibilityValue);
        sunrise = findViewById(R.id.sunriseValue);
        sunset = findViewById(R.id.sunsetValue);
        windDirection = findViewById(R.id.windDirectionValue);
        pressure = findViewById(R.id.airPressureValue);
        focusedWeatherIcon = findViewById(R.id.focusedWeatherIcon);

        setCurrentDate();
    }


    private void setCurrentDate() {
        // Get the current date
        Date currentDate = new Date();

        // Format the date
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMMM yyyy", Locale.getDefault());
        String formattedDate = dateFormat.format(currentDate);

        // Set the formatted date to TextView
        dateTextView.setText(formattedDate);

    }

    public void fetchWeatherData(String cityName, String threeDayForecastUrl) {
        new Thread(() -> {
            try {
                URL url = new URL(threeDayForecastUrl);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                Log.d("DetailedWeather", "Opening connection to URL: " + threeDayForecastUrl);

                InputStream inputStream = connection.getInputStream();
                String xmlData = convertInputStreamToString(inputStream);
                Log.d("DetailedWeather", "XML Data: " + xmlData);

                weatherList = FocusedWeatherParser.parseXML(new ByteArrayInputStream(xmlData.getBytes()));
                Log.d("DetailedWeather", "Weather data fetched successfully");
                Log.d("DetailedWeather", "Weather data fetched successfully"+ weatherList);

                runOnUiThread(() -> {

                    if (!weatherList.isEmpty()) {
                        Weather currentWeather = null;
                        List<Weather> nextTwoDaysWeather = new ArrayList<>();
                        boolean foundToday = false;

                        for (Weather weather : weatherList) {
                            if ("Today".equals(weather.getDay()) || "Tonight".equals(weather.getDay())) {
                                // Current weather
                                currentWeather = weather;
                                foundToday = true;
                                Log.d("DetailedWeather", "Today's weather found.");
                            } else if(foundToday && nextTwoDaysWeather.size() < 2) {
                                // Subsequent days' weather
                                nextTwoDaysWeather.add(weather);
                                Log.d("DetailedWeather", "Subsequent day's weather added: " + weather.getDay());
                            }
                        }

                        // Update UI with current weather
                        if (currentWeather != null) {
                            try {
                                updateCurrentWeatherUI(currentWeather, cityName);
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        }

                        // Populate RecyclerView with next two days' weather
                        populateRecyclerView(nextTwoDaysWeather);

                    } else {
                        Log.e("DetailedWeather", "Weather data is empty");
                    }
                });

                connection.disconnect();
            } catch (IOException e) {
                e.printStackTrace();
                Log.e("DetailedWeather", "Error fetching weather data: " + e.getMessage());
            }
        }).start();


    }

    private String convertInputStreamToString(InputStream inputStream) throws IOException {
        StringBuilder stringBuilder = new StringBuilder();
        byte[] buffer = new byte[1024];
        int bytesRead;
        while ((bytesRead = inputStream.read(buffer)) != -1) {
            stringBuilder.append(new String(buffer, 0, bytesRead));
        }
        return stringBuilder.toString();
    }

    private void updateCurrentWeatherUI(Weather currentWeather, String cityName) throws IOException {

        // Update UI components with current weather data
        locationName.setText(cityName);
        weatherDescription.setText(currentWeather.getWeatherDescription());
        minTemperature.setText(getString(R.string.temperature_format,currentWeather.getMinTemperature()));
        maxTemperature.setText(getString(R.string.temperature_format,currentWeather.getMaxTemperature()));
        humidity.setText(currentWeather.getHumidity());
        wind.setText(currentWeather.getWindSpeed());
        uvRisk.setText(getString(R.string.uv_risk_format, currentWeather.getUvRisk()));
        visibility.setText(currentWeather.getVisibility());
        sunrise.setText(currentWeather.getSunrise());
        sunset.setText(currentWeather.getSunset());
        windDirection.setText(currentWeather.getWindDirection());
        pressure.setText(currentWeather.getPressure());
        LottieAnimationView focusedWeatherIcon = findViewById(R.id.focusedWeatherIcon);

        // Define a map to map weather descriptions to resource IDs
        Map<String, Integer> animationResourceMap = new HashMap<>();
        animationResourceMap.put("light_rain", R.raw.light_rain);
        animationResourceMap.put("light_rain_showers", R.raw.light_rain_showers);
        animationResourceMap.put("sunny_intervals", R.raw.sunny_intervals);
        animationResourceMap.put("clear_sky", R.raw.clear_sky);
        animationResourceMap.put("sunny", R.raw.sunny);
        animationResourceMap.put("light_cloud", R.raw.light_cloud);
        animationResourceMap.put("thundery_showers", R.raw.thundery_showers);


        // Retrieve the resource ID based on the current weather description
        int animationResId = animationResourceMap.getOrDefault(currentWeather.getWeatherDescription().toLowerCase().replace(" ", "_"), R.raw.default_weather);

        Log.d("AnimationResId", "Animation Resource ID: " + animationResId);

        LottieCompositionFactory.fromRawRes(getApplicationContext(), animationResId)
                .addListener(composition -> {
                    focusedWeatherIcon.setComposition(composition);
                    focusedWeatherIcon.playAnimation();
                });


        Log.d("CurrentWeatherUI", "City Name: " + currentWeather.getCityName());
        Log.d("CurrentWeatherUI", "Temperature: " + currentWeather.getTemperature());
        Log.d("CurrentWeatherUI", "Weather Description: " + currentWeather.getWeatherDescription());
        Log.d("CurrentWeatherUI", "Min Temperature: " + currentWeather.getMinTemperature());
        Log.d("CurrentWeatherUI", "Max Temperature: " + currentWeather.getMaxTemperature());
        Log.d("CurrentWeatherUI", "Humidity: " + currentWeather.getHumidity());
        Log.d("CurrentWeatherUI", "Wind Speed: " + currentWeather.getWindSpeed());
        Log.d("CurrentWeatherUI", "UV Risk: " + currentWeather.getUvRisk());
        Log.d("CurrentWeatherUI", "Visibility: " + currentWeather.getVisibility());
        Log.d("CurrentWeatherUI", "Sunrise: " + currentWeather.getSunrise());
        Log.d("CurrentWeatherUI", "Sunset: " + currentWeather.getSunset());
        Log.d("CurrentWeatherUI", "Wind Direction: " + currentWeather.getWindDirection());
        Log.d("CurrentWeatherUI", "Pressure: " + currentWeather.getPressure());

    }

    private void populateRecyclerView(List<Weather> nextTwoDaysWeather) {
        // Create and set up RecyclerView adapter with next two days' weather data
        RecyclerView recyclerView = findViewById(R.id.threeDayWeather);
        TwoDayWeatherAdapter adapter = new TwoDayWeatherAdapter(nextTwoDaysWeather);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }


}


