package com.example.waithira_jane_s2110907;

// Name                 Jane Waithira
// Student ID           S2110907
// Programme of Study   Mobile Platform Development

import android.util.Log;
import android.widget.Toast;

import java.util.Collections;
import java.util.List;

import androidx.appcompat.widget.SearchView;

public class SearchHandler implements androidx.appcompat.widget.SearchView.OnQueryTextListener {

    private MainActivity activity;
    private List<Weather> originalWeatherList;


    public SearchHandler(MainActivity activity, List<Weather> originalWeatherList){
        this.activity = activity;
        this.originalWeatherList= originalWeatherList;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        Log.d("SearchHandler", "User searched for city: " + query);
        String locationId = CityIdResolver.getLocationIdFromCitiesFile(activity, query);

        if (locationId != null) {
            String weatherUrl = "https://weather-broker-cdn.api.bbci.co.uk/en/observation/rss/" + locationId;
            Log.d("SearchHandler", "Weather URL: " + weatherUrl);
            // Notify MainActivity with the weather URL
            activity.fetchSearchedWeather(weatherUrl, query);
        } else {
            Log.d("SearchHandler", "City not found: " + query);
            Toast.makeText(activity, "City not found", Toast.LENGTH_SHORT).show();
            if (query.isEmpty()) {
                // If the search query is empty, restore the original weather data
                activity.updateWeatherData(originalWeatherList);
                Log.d("SearchHandler", "Original Weather data: " + originalWeatherList);
            } else {
                activity.updateWeatherData(Collections.emptyList());
            }
        }

        return true;

    }

    void clearSearchQuery() {
        // Clear the search query and cancel the search
        SearchView searchView = activity.findViewById(R.id.searchView);
        if (searchView != null) {
            searchView.setQuery("", false); // Set empty query
            searchView.clearFocus(); // Clear focus
        }
        // Restore the original weather data in the UI
        activity.updateWeatherData(originalWeatherList);
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
    }

}
