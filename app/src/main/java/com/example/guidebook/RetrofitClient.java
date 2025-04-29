package com.example.guidebook;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {
    private static final String BASE_URL = "https://api.openweathermap.org/"; // Base URL for the API
    private static Retrofit retrofit = null; // Retrofit instance

    // Method to get the Retrofit client
    public static Retrofit getClient() {
        if (retrofit == null) {
            // If no existing Retrofit instance, create a new one
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL) // Set the base URL
                    .addConverterFactory(GsonConverterFactory.create()) // Add Gson converter for JSON parsing
                    .build(); // Build the Retrofit instance
        }
        return retrofit; // Return the Retrofit instance
    }
}