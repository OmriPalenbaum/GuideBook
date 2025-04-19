package com.example.guidebook;

import java.util.List;

public class WeatherResponse {
    private String name;
    private Main main;
    private List<Weather> weather;

    //City
    public String getName() {
        return name;
    }

    public Main getMain() {
        return main;
    }

    public List<Weather> getWeather() {
        return weather;
    }

    public static class Main {
        private double temp;
        private int humidity;

        //Temperature
        public double getTemp() {
            return temp;
        }
        //humidity
        public int getHumidity() {
            return humidity;
        }
    }

    public static class Weather {
        private String description;
        //Description
        public String getDescription() {
            return description;
        }
    }
}
