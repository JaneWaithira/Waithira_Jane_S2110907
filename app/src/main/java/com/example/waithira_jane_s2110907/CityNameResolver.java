package com.example.waithira_jane_s2110907;

// Name                 Jane Waithira
// Student ID           S2110907
// Programme of Study   Mobile Platform Development

import android.content.Context;
import android.util.Log;

import java.io.IOException;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

public class CityNameResolver {
    public static String resolveCityName(Context context, String locationId) {
        String cityName = null;

        try{
            InputStream inputStream = context.getAssets().open("cities.txt");
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            // Iterate through each line of the file
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\t");
                if (parts.length >= 2 && parts[0].equals(locationId)) {
                    cityName = parts[1];
                    break;
                }

                // Log.d("CityNameResolver", "Line: " + line);
                //Log.d("CityNameResolver", "Location ID: " + parts[0]);
            }
            reader.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return cityName;
    }

}
