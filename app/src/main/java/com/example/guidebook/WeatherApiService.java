package com.example.guidebook;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

// Interface for API
public interface WeatherApiService {
    @GET("data/2.5/weather")
    Call<WeatherResponse> getWeather(
            @Query("q") String city,
            @Query("appid") String apiKey,
            @Query("units") String units // Metric for Celsius
    );
}

