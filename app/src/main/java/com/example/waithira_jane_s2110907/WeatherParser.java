package com.example.waithira_jane_s2110907;

import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;


import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;

import android.util.Log;

import java.io.IOException;

// Name                 Jane Waithira
// Student ID           S2110907
// Programme of Study   Mobile Platform Development
public class WeatherParser {
    private static final String LOG_TAG = "WeatherParser";

    public static List<Weather> parseWeatherData(InputStream inputStream, String cityName) throws XmlPullParserException, IOException {
        List<Weather> weatherList = new ArrayList<>();

        XmlPullParser parser = XmlPullParserFactory.newInstance().newPullParser();
        parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
        parser.setInput(inputStream, null);

        int eventType = parser.getEventType();
        Weather currentWeather = null;

        while (eventType != XmlPullParser.END_DOCUMENT) {
            String tagName = parser.getName();
            switch (eventType) {
                case XmlPullParser.START_TAG:
                    if ("item".equals(tagName)) {
                        currentWeather = new Weather("", "", "", "", 0.0, 0.0, "", "", 0, "", "", "", "", "");
                        currentWeather.setCityName(cityName);
                    } else if (currentWeather != null) {
                        extractWeatherDetails(currentWeather, tagName, parser.nextText());
                    }
                    break;
                case XmlPullParser.END_TAG:
                    if ("item".equals(tagName) && currentWeather != null) {
                        weatherList.add(currentWeather);
                        currentWeather = null;
                    }
                    break;
            }
            eventType = parser.next();
        }

        inputStream.close();
        return weatherList;
    }


    private static void extractWeatherDetails(Weather weather, String tagName, String value) {
        switch (tagName) {
            case "title":
                String weatherDescription = extractWeatherDescription(value);
                weather.setWeatherDescription(weatherDescription);
                break;
            case "description":
                String[] minMaxTemperature = extractminTemperature(value);
                double minTemperature = Double.parseDouble(minMaxTemperature[0]);
                double maxTemperature = Double.parseDouble(minMaxTemperature[1]);
                String windSpeed = extractWindSpeed(value);
                String temperature = extractTemperature(value);

                String humidity = extractHumidity(value);
                int uvRisk = extractUvRisk(value);
                String visibility = extractVisibility(value);
                String sunrise = extractSunrise(value);
                String sunset = extractSunset(value);

                weather.setMinTemperature(minTemperature);
                weather.setMaxTemperature(maxTemperature);
                weather.setWindSpeed(windSpeed);
                weather.setTemperature(temperature);
                weather.setHumidity(humidity);
                weather.setUvRisk(uvRisk);
                weather.setVisibility(visibility);
                weather.setSunrise(sunrise);
                weather.setSunset(sunset);
                break;
        }
    }


    public static boolean isDailyWeather(String title) {
        return !title.contains("3-day");
    }

    private static String readText(XmlPullParser parser) throws IOException, XmlPullParserException {
        String result = "";
        if (parser.next() == XmlPullParser.TEXT) {
            result = parser.getText();
            parser.nextTag();
        }
        return result;
    }




    public static String extractcityName(Context context, String url) {
        // Log the method call
        Log.d("WeatherParser", "extract cityName method called with URL: " + url);

        // Split the URL to extract the location ID
        String[] parts = url.split("/");
        String lastPart = parts[parts.length - 1];
        String locationId = lastPart.replaceAll("[^\\d]", "");

        // Logging the extracted location ID
        Log.d("WeatherParser", "Extracted location ID: " + locationId);

        // Resolve the city name using the location ID
        String cityName = CityNameResolver.resolveCityName(context, locationId);

        // Logging the extracted city name
        Log.d("WeatherParser", "Extracted city name from URL: " + cityName);

        return cityName;
    }



    public static String constructThreeDayForecastUrl(String locationId) {
        Log.d("WeatherParser", "construct three day forecast url method called with : " + locationId);

        // Use the location ID directly instead of resolving it to a city name
        if (locationId != null && !locationId.isEmpty()) {
            String url = "https://weather-broker-cdn.api.bbci.co.uk/en/forecast/rss/3day/" + locationId;
            Log.d("WeatherParser", "Constructed URL for three-day forecast: " + url);
            return url;
        } else {
            Log.e("WeatherParser", "Location ID is null or empty. Unable to construct three-day forecast URL.");
            return null; // Handle the case where locationId is null or empty
        }
    }


    private static String extractTemperature(String description) {
        String[] parts = description.split(",");
        for (String part : parts) {
            if (part.trim().startsWith("Temperature:")) {
                // Find the index of the opening bracket
                int bracketIndex = part.indexOf("(");
                if (bracketIndex != -1) {
                    // Extract the substring before the opening bracket
                    String temperature = part.substring("Temperature:".length(), bracketIndex).trim();
                    Log.d("MyTag", "Extracted temperature: " + temperature);
                    return temperature;
                }
            }
        }
        return null;
    }


    private static String[] extractminTemperature(String description) {
        String[] temperatureArray = new String[2];
        String[] parts = description.split(",");

        for (String part : parts) {
            if (part.trim().startsWith("Minimum Temperature:") || part.trim().startsWith("Maximum Temperature:")) {
                String temperature = part.trim().substring(part.indexOf(":") + 1).trim();
                // Remove unwanted characters and trim
                temperature = temperature.replaceAll("[^\\d.-]", "").trim();
                Log.d("MyTag", "Extracted temperature: " + temperature);

                if (!temperature.isEmpty()) {
                    if (part.trim().startsWith("Minimum Temperature:")) {
                        temperatureArray[0] = temperature;
                    } else {
                        temperatureArray[1] = temperature;
                    }
                }
            }
        }

        // Ensure both minimum and maximum temperatures are initialized
        if (temperatureArray[0] == null) {
            temperatureArray[0] = "0.0"; // Default value for minimum temperature
        }
        if (temperatureArray[1] == null) {
            temperatureArray[1] = "0.0"; // Default value for maximum temperature
        }

        return temperatureArray;
    }

    private static String extractWindSpeed(String description) {
        String windSpeed = "";
        String[] parts = description.split(",");
        for (String part : parts) {
            if (part.trim().startsWith("Wind Speed:")) {
                windSpeed = part.trim().substring("Wind Speed:".length()).trim();

                // Append "WS" prefix to wind speed value
                windSpeed = "WS: " + windSpeed;
                Log.d("MyTag", "Extracted windSpeed: " + windSpeed);

                break;
            }
        }
        return windSpeed;
    }



    private static String extractWeatherDescription1(String description) {
        String weatherDescription = "";
        int todayIndex = description.indexOf("Today:");
        if (todayIndex != -1){
            String afterToday = description.substring(todayIndex + "Today:".length());
            afterToday = afterToday.trim();
            int commaIndex = afterToday.indexOf(',');
            if (commaIndex != -1) {
                weatherDescription = afterToday.substring(0, commaIndex).trim();
            } else {
                weatherDescription = afterToday;
            }
        }
        return weatherDescription;
    }


    private static String extractWeatherDescription(String title) {
        // Find the index of the colon followed by a space in the title
        int colonIndex = title.indexOf(": ");

        // If colon is found
        if (colonIndex != -1) {
            // Find the index of the first comma after the colon
            int commaIndex = title.indexOf(",", colonIndex);

            // If comma is found
            if (commaIndex != -1) {
                // Extract the substring between the colon and the comma
                String description = title.substring(colonIndex + 2, commaIndex).trim();
                return description;
            }
        }

        // If colon is not found or comma is not found, return an empty string
        return "";
    }




    private static String extractHumidity(String description) {
        String humidity = "";
        String[] parts = description.split(",");
        for (String part : parts) {
            if (part.trim().startsWith("Humidity:")) {
                String humidtyStr = part.trim().substring("Humidity:".length()).trim();
                // Remove non-numeric characters
                humidity = "H: " + humidtyStr;

                Log.d("MyTag", "Extracted Humidity: " + humidity);
                break;
            }
        }
        return humidity;
    }

    private static int extractUvRisk(String description) {
        int UvRisk = 0;
        String[] parts = description.split(",");
        for (String part : parts) {
            if (part.trim().startsWith("UV Risk:")) {
                UvRisk = Integer.parseInt(part.trim().substring("UV Risk:".length()).trim());
                Log.d("MyTag", "Extracted UvRisk: " + UvRisk);
                break;
            }
        }
        return UvRisk;
    }

    private static String extractVisibility(String description) {
        String visibility = "";
        String[] parts = description.split(",");
        for (String part : parts) {
            if (part.trim().startsWith("Visibility:")) {
                visibility = part.trim().substring("Visibility:".length()).trim();
                Log.d("MyTag", "Extracted visibility: " + visibility);
                break;
            }
        }
        return visibility;
    }


    private static String extractSunrise(String description) {
        String sunrise = "";
        String[] parts = description.split(",");
        for (String part : parts) {
            if (part.trim().startsWith("Sunrise: ")) {
                sunrise = part.trim().substring("Sunrise: ".length()).trim();
                Log.d("MyTag", "Extracted sunrise: " + sunrise);
                break;
            }
        }
        return sunrise;
    }

    private static String extractSunset(String description) {
        String sunset = "";
        String[] parts = description.split(",");
        for (String part : parts) {
            if (part.trim().startsWith("Sunset: ")) {
                sunset = part.trim().substring("Sunset: ".length()).trim();
                Log.d("MyTag", "Extracted sunset: " + sunset);
                break;
            }
        }
        return sunset;
    }




    private static void skip(XmlPullParser parser) throws XmlPullParserException, IOException {
        if (parser.getEventType() != XmlPullParser.START_TAG) {
            throw new IllegalStateException();
        }
        int depth = 1;
        while (depth != 0) {
            switch (parser.next()) {
                case XmlPullParser.END_TAG:
                    depth--;
                    break;
                case XmlPullParser.START_TAG:
                    depth++;
                    break;
            }
        }
    }



}

