package com.example.guidebook;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

// Interface for making weather API requests
public interface WeatherApiService {

    // GET request to fetch weather data for a specific city
    @GET("data/2.5/weather")
    Call<WeatherResponse> getWeather(
            @Query("q") String city, // The name of the city for which the weather is requested
            @Query("appid") String apiKey, // API key for authorization
            @Query("units") String units // Unit of measurement (Metric for Celsius)
    );
}