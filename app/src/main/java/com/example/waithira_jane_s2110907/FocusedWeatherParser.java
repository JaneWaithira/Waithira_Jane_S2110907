package com.example.waithira_jane_s2110907;
// Name                 Jane Waithira
// Student ID           S2110907
// Programme of Study   Mobile Platform Development

import android.util.Log;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class FocusedWeatherParser {

    public static List<Weather> parseXML(InputStream inputStream) {
        List<Weather> weatherList = new ArrayList<>();

        try {
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            XmlPullParser parser = factory.newPullParser();
            parser.setInput(inputStream, null);

            int eventType = parser.getEventType();
            Weather currentWeather = null;

            while (eventType != XmlPullParser.END_DOCUMENT) {
                if (eventType == XmlPullParser.START_TAG && "item".equals(parser.getName())) {
                    // Initialize currentWeather object when encountering the <item> tag
                    currentWeather = new Weather("", "", "", "", 0.0, 0.0, "", "", 0, "", "", "","", "" );
                } else if (eventType == XmlPullParser.START_TAG && "title".equals(parser.getName())) {
                    // Only set attributes of currentWeather if it's not null
                    if (currentWeather != null) {
                        String title = parser.nextText();
                        currentWeather.setDay(parseDay(title));
                        currentWeather.setWeatherDescription(parseWeatherDescription(title));
                    }
                } else if (eventType == XmlPullParser.START_TAG && "description".equals(parser.getName())) {
                    // Only parse description if currentWeather is not null
                    if (currentWeather != null) {
                        String description = parser.nextText();
                        parseDescription(description, currentWeather);
                    }
                } else if (eventType == XmlPullParser.END_TAG && "item".equals(parser.getName()) && currentWeather != null) {
                    // Add currentWeather to the list when encountering the closing </item> tag
                    weatherList.add(currentWeather);
                    currentWeather = null; // Reset currentWeather for the next iteration
                }
                eventType = parser.next();
            }
            Log.d("FocusedWeatherParser", "XML parsing successful");
        } catch (XmlPullParserException | IOException e) {
            e.printStackTrace();
            Log.e("FocusedWeatherParser", "Error parsing XML: " + e.getMessage());
        }
        Log.d("FocusedWeatherParser", "Weather list: " + weatherList);
        return weatherList;
    }


    private static String parseDay(String title) {
        return title.split(":")[0].trim();
    }

    private static String parseWeatherDescription(String title) {
        // Split the title by comma
        String[] parts = title.split(",");
        // Extract the first part as the weather description
        String description = parts[0].trim();
        // Check if the weather description contains the word "Minimum Temperature"
        if (description.contains("Minimum Temperature")) {
            // Remove "Minimum Temperature" and its following part from the description
            description = description.replace("Minimum Temperature", "").trim();
        }
        if (description.contains("Today:")) {
            // Remove "Tonight" from the description
            description = description.replace("Today:", "").trim();
        }
        // Check if the description contains the word "Tonight"
        if (description.contains("Tonight:")) {
            // Remove "Tonight" from the description
            description = description.replace("Tonight:", "").trim();
        }
        return description;
    }




    private static void parseDescription(String description, Weather weather) {
        String[] parts = description.split(", ");
        for (String part : parts) {
            if (part.contains("Minimum Temperature")) {
                // Extract numerical value from the string and parse it into double
                String minTempStr = part.split(": ")[1].split(" ")[0]; // Extract "21°C"
                double minTemp = Double.parseDouble(minTempStr.replaceAll("[^\\d.]", ""));
                weather.setMinTemperature(minTemp);
            } else if (part.contains("Maximum Temperature")) {
                // Extract numerical value from the string and parse it into double
                String maxTempStr = part.split(": ")[1].split(" ")[0]; // Extract "30°C"
                double maxTemp = Double.parseDouble(maxTempStr.replaceAll("[^\\d.]", ""));
                weather.setMaxTemperature(maxTemp);
            }  else if (part.contains("Wind Speed")) {
                weather.setWindSpeed(part.split(": ")[1]);
            } else if (part.contains("Visibility")) {
                String visibility = part.split(": ")[1].trim();
                weather.setVisibility(visibility);
            } else if (part.contains("Humidity")) {
                weather.setHumidity(part.split(": ")[1]);
            } else if (part.contains("UV Risk")) {
                weather.setUvRisk(Integer.parseInt(part.split(": ")[1]));
            } else if (part.contains("Sunrise")) {
                weather.setSunrise(part.split(": ")[1]);
            } else if (part.contains("Sunset")) {
                weather.setSunset(part.split(": ")[1]);
            }  else if (part.contains("Wind Direction")) {
            weather.setWindDirection(part.split(": ")[1]);
            } else if (part.contains("Pressure")) {
                weather.setPressure(part.split(": ")[1]);
            }
        }
    }
}
