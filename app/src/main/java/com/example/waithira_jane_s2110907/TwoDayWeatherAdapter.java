package com.example.waithira_jane_s2110907;

// Name                 Jane Waithira
// Student ID           S2110907
// Programme of Study   Mobile Platform Development

import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.lang.reflect.Field;
import java.util.List;

public class TwoDayWeatherAdapter extends RecyclerView.Adapter<TwoDayWeatherAdapter.ViewHolder> {

    private List<Weather> weatherList;

    public TwoDayWeatherAdapter(List<Weather> weatherList) {
        this.weatherList = weatherList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.two_day_weather, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Weather weather = weatherList.get(position);
        // Bind data to views in the item layout
        holder.bind(weather);
    }

    @Override
    public int getItemCount() {
        return weatherList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView dayTextView;
        private TextView minTempTextView;
        private TextView maxTempTextView;
        private TextView humidityTextView;
        
        private ImageView weatherIcon;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            dayTextView = itemView.findViewById(R.id.day);
            minTempTextView = itemView.findViewById(R.id.minTemperature);
            maxTempTextView = itemView.findViewById(R.id.maxTemperature);
            humidityTextView = itemView.findViewById(R.id.humidityText);
            weatherIcon = itemView.findViewById(R.id.weatherIcon);
        }

        public void bind(Weather weather) {
            // Bind weather data to views
            dayTextView.setText(weather.getDay());
            minTempTextView.setText(String.valueOf(weather.getMinTemperature()));
            maxTempTextView.setText(String.valueOf(weather.getMaxTemperature()));
            humidityTextView.setText(weather.getHumidity());
            int iconResource = getWeatherIconResource(weather.getWeatherDescription());
            weatherIcon.setImageResource(iconResource);
        }


    }

    private int getWeatherIconResource(String weatherDescription) {
        Field[] drawableFields = R.drawable.class.getFields();
        for (Field field: drawableFields) {
            try {String drawableName = field.getName();
            if (drawableName.toLowerCase().contains(weatherDescription.toLowerCase())) {
                return field.getInt(null);
            }
        } catch (Exception e) {
              e.printStackTrace();  
        }
        }
            
       return R.drawable.default_weather;

     }
}

