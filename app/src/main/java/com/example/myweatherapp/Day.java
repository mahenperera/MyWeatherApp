package com.example.myweatherapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;


public class Day extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_day);

        Intent intent = getIntent();
        String day = intent.getStringExtra("day");
        String date = intent.getStringExtra("date");
        String cityCountry = intent.getStringExtra("cityCountry");
        int weatherIconResId = intent.getIntExtra("weatherIconResId", 0);
        String temperature = intent.getStringExtra("temperature");
        String weatherState = intent.getStringExtra("weatherState");
        int humidity = intent.getIntExtra("humidity", 0);

        TextView textViewDay = findViewById(R.id.day);
        TextView textViewDate = findViewById(R.id.date);
        TextView textViewCityCountry = findViewById(R.id.city_country);
        ImageView imageViewWeather = findViewById(R.id.weather_image);
        TextView textViewTemperature = findViewById(R.id.temperature);
        TextView textViewWeatherState = findViewById(R.id.weather_state);
        TextView textViewHumidity = findViewById(R.id.humidity);

        textViewDay.setText(day);
        textViewDate.setText(date);
        textViewCityCountry.setText(cityCountry);
        imageViewWeather.setImageResource(weatherIconResId);
        textViewTemperature.setText(temperature);
        textViewWeatherState.setText(weatherState);
        textViewHumidity.setText("Humidity: " + humidity + "%");
    }
}


