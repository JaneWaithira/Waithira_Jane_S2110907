package com.example.waithira_jane_s2110907;

// Name                 Jane Waithira
// Student ID           S2110907
// Programme of Study   Mobile Platform Development

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class WeatherAdapter extends RecyclerView.Adapter<WeatherAdapter.ViewHolder> {
    private List<Weather> weatherList;
    private OnItemClickListener listener;
    private String temperatureUnit = "Celsius";

    public void setTemperatureUnit(String selectedTemperatureUnit) {
        this.temperatureUnit = selectedTemperatureUnit;
        notifyDataSetChanged();
    }

    public String getTemperatureUnit(){
        return temperatureUnit;
    }

    public interface OnItemClickListener {
        void onItemClick(Weather weather);

    }
    public WeatherAdapter(List<Weather> weatherList) {
        this.weatherList = weatherList;
    }



    public WeatherAdapter(List<Weather> weatherList, OnItemClickListener listener) {
        this.weatherList = weatherList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_weather, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Weather weather = weatherList.get(position);
        // Bind data to views in item_weather.xml
        holder.bind(weather);

        holder.itemView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                listener.onItemClick(weather);
            }
        });
        Log.d("WeatherAdapter", "Binding weather for location: " + weather.getCityName());
    }

    @Override
    public int getItemCount() {
        return weatherList != null ? weatherList.size(): 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final TextView temperatureText;
        private final TextView locationText;
        private final TextView windHumidityText;
        private final TextView weatherDescriptionText;
        private final ImageView weatherIconImage;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            temperatureText = itemView.findViewById(R.id.temperatureText);
            locationText = itemView.findViewById(R.id.locationText);
            windHumidityText = itemView.findViewById(R.id.windHumidityText);
            weatherDescriptionText = itemView.findViewById(R.id.weatherDescriptionText);
            weatherIconImage = itemView.findViewById(R.id.weatherIcon);
            itemView.setOnClickListener(this);
        }


        public void onClick(View v) {
            int position = getAdapterPosition();
            if (position != RecyclerView.NO_POSITION) {
                Weather selectedWeather = weatherList.get(position);
                listener.onItemClick(selectedWeather);
            }
        }
        public void bind(Weather weather) {
            // Extracting the numeric part of the temperature string
            String temperatureString = weather.getTemperature();
            String numericTemperature = temperatureString.replaceAll("[^0-9.]", "");
            double temperatureValue = Double.parseDouble(numericTemperature);

            // Convert temperature to the selected unit
            if (temperatureUnit.equals("Fahrenheit")) {
                temperatureValue = (temperatureValue * 9 / 5) + 32;
            }

            Log.d("WeatherAdapter", "Original Temperature: " + temperatureString);
            Log.d("WeatherAdapter", "Converted Temperature (" + temperatureUnit + "): " + temperatureValue);

            // Set the temperature text
            temperatureText.setText(String.format(Locale.getDefault(), "%.1f°%s", temperatureValue, temperatureUnit.charAt(0)));

            // Bind data to views
            //temperatureText.setText(weather.getTemperature());
            locationText.setText(weather.getCityName());
            windHumidityText.setText(String.format("%s, %s", weather.getWindSpeed(), weather.getHumidity()));
            //weatherDescriptionText.setText(weather.getWeatherDescription());

            String weatherDescription = weather.getWeatherDescription();
            if (weatherDescription != null && !weatherDescription.isEmpty()) {
                weatherDescriptionText.setText(weatherDescription);
                Log.d("WeatherAdapter", "Weather Description text" + weatherDescriptionText.getText());

                // Set weather icon
                String iconName = "ic_weather_" + weather.getWeatherDescription().toLowerCase().replace(" ", "_");
                int iconResource = getResourceId(iconName, "drawable");


                if (iconResource != 0) {
                    weatherIconImage.setImageResource(iconResource);
                } else {
                    //set default if no icon found
                    weatherIconImage.setImageResource(R.drawable.default_weather);
                }
            }else{
                    weatherDescriptionText.setText("Not Available");
                    weatherIconImage.setImageResource(R.drawable.default_weather);
                }

            Log.d("WeatherAdapter", "Temperature: " + weather.getTemperature());
            Log.d("WeatherAdapter", "Location: " + weather.getCityName());
            Log.d("WeatherAdapter", "Wind Speed: " + weather.getWindSpeed());
            Log.d("WeatherAdapter", "Humidity: " + weather.getHumidity());
            Log.d("WeatherAdapter", "Weather Description: " + weather.getWeatherDescription());

        }
        private int getResourceId(String resourceName, String resourceType) {
            return itemView.getContext().getResources().getIdentifier(resourceName, resourceType, itemView.getContext().getPackageName());
        }
    }
}
