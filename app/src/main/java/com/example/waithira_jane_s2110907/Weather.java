package com.example.waithira_jane_s2110907;

import java.io.Serializable;

// Name                 Jane Waithira
// Student ID           S2110907
// Programme of Study   Mobile Platform Development
public class Weather implements Serializable {
    private String cityName;

    private String day;

    private String weatherDescription;
    private String temperature;
    private double minTemperature;
    private double maxTemperature;
    private String windSpeed;

    private String humidity;
    private int uvRisk;
    private String visibility;
    private String sunrise;
    private String sunset;
    private String windDirection;
    private String pressure;



    public Weather(String cityName, String day, String weatherDescription, String temperature, double minTemperature, double maxTemperature, String windSpeed, String humidity, int uvRisk, String visibility, String sunrise, String sunset, String windDirection, String pressure) {
        this.cityName = cityName;
        this.day = day;
        this.weatherDescription = weatherDescription;
        this.temperature = temperature;
        this.minTemperature = minTemperature;
        this.maxTemperature = maxTemperature;
        this.windSpeed = windSpeed;
        this.humidity = humidity;
        this.uvRisk = uvRisk;
        this.visibility = visibility;
        this.sunrise = sunrise;
        this.sunset = sunset;
        this.windDirection = windDirection;
        this.pressure = pressure;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public String getWeatherDescription() {
        return weatherDescription;
    }

    public void setWeatherDescription(String weatherDescription) {
        this.weatherDescription = weatherDescription;
    }

    public String getTemperature() {
        return temperature;
    }

    public void setTemperature(String temperature) {
        this.temperature = temperature;
    }

    public double getMinTemperature() {
        return minTemperature;
    }

    public void setMinTemperature(double minTemperature) {
        this.minTemperature = minTemperature;
    }
    public double getMaxTemperature() {
        return maxTemperature;
    }

    public void setMaxTemperature(double maxTemperature) {
        this.maxTemperature = maxTemperature;
    }
    public String getWindSpeed() {
        return windSpeed;
    }

    public void setWindSpeed(String windSpeed) {
        this.windSpeed = windSpeed;
    }

    public String getHumidity() {
        return humidity;
    }

    public void setHumidity(String humidity) {
        this.humidity = humidity;
    }

    public int getUvRisk() {
        return uvRisk;
    }

    public void setUvRisk(int uvRisk) {
        this.uvRisk = uvRisk;
    }

    public String getVisibility() {
        return visibility;
    }

    public void setVisibility(String visibility) {
        this.visibility = visibility;
    }

    public String getSunrise() {
        return sunrise;
    }

    public void setSunrise(String sunrise) {
        this.sunrise = sunrise;
    }

    public String getSunset() {
        return sunset;
    }

    public void setSunset(String sunset) {
        this.sunset = sunset;
    }
    public String getWindDirection() {
        return windDirection;
    }

    public void setWindDirection(String windDirection) {
        this.windDirection = windDirection;
    }

    public String getPressure() {
        return pressure;
    }

    public void setPressure(String pressure) {
        this.pressure = pressure;
    }

    @Override
    public String toString() {
        return "Weather{" +
                "city Name='" + cityName + '\'' +
                "description='" + weatherDescription + '\'' +
                ", temperature='" + temperature + '\'' +
                ", minTemperature='" + minTemperature + '\'' +
                ", maxTemperature='" + temperature + '\'' +
                ", windSpeed='" + windSpeed + '\'' +
                ", humidity='" + humidity + '\'' +
                '}';
    }

}
