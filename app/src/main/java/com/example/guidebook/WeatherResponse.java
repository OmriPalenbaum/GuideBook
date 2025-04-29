package com.example.guidebook;

import java.util.List;

public class WeatherResponse {
    private String name; // City name
    private Main main; // Main weather data (temperature, humidity)
    private List<Weather> weather; // List of weather conditions (e.g., description)

    // Getter for city name
    public String getName() {
        return name;
    }

    // Getter for main weather data
    public Main getMain() {
        return main;
    }

    // Getter for weather conditions list
    public List<Weather> getWeather() {
        return weather;
    }

    // Inner class for main weather data (temperature, humidity)
    public static class Main {
        private double temp; // Temperature
        private int humidity; // Humidity percentage

        // Getter for temperature
        public double getTemp() {
            return temp;
        }

        // Getter for humidity
        public int getHumidity() {
            return humidity;
        }
    }

    // Inner class for weather condition description
    public static class Weather {
        private String description; // Weather condition description (e.g., sunny, cloudy)

        // Getter for weather description
        public String getDescription() {
            return description;
        }
    }
}