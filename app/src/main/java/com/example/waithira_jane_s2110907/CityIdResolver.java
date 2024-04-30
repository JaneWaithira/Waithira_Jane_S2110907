package com.example.waithira_jane_s2110907;

// Name                 Jane Waithira
// Student ID           S2110907
// Programme of Study   Mobile Platform Development

import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class CityIdResolver {

    public static String getLocationIdFromCitiesFile(Context context, String cityName) {
        try {
            InputStream inputStream = context.getAssets().open("cities.txt");
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            int lineCount = 0; // Track the line number
            while ((line = bufferedReader.readLine()) != null) {
                lineCount++; // Increment line count for each iteration
                //Log.d("CityIdResolver", "Read line " + lineCount + " from cities.txt: " + line); // Log the current line
                String[] parts = line.split("\t"); // Split by tab character
                if (parts.length >= 2) {
                    String cityFromFile = parts[1].trim();
                    if (cityFromFile.equalsIgnoreCase(cityName)) {
                        String locationId = parts[0].trim();
                        Log.d("CityIdResolver", "Location ID found in cities.txt: " + locationId + " for city: " + cityName);
                        return locationId; // Return location ID if cityName matches
                    }
                }
            }
            bufferedReader.close();
        } catch (IOException e) {
            Log.e("CityIdResolver", "Error reading cities.txt file: " + e.getMessage());
        }
        Log.e("CityIdResolver", "Location ID not found in cities.txt for city: " + cityName);
        return null; // Return null if cityName not found
    }

}
