package com.example.guidebook;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    Button btLocations; // Button to navigate to Locations page
    Button btTips; // Button to navigate to Tips page

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main); // Set the layout for the activity

        // Initialize the buttons
        btLocations = findViewById(R.id.btLocations);
        btTips = findViewById(R.id.btTips);

        // Set up an OnClickListener for the Locations button
        btLocations.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // When clicked, open the Locations page
                Intent intent = new Intent(MainActivity.this, Locations.class);
                startActivity(intent);
            }
        });

        // Set up an OnClickListener for the Tips button
        btTips.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // When clicked, open the Tips page
                Intent intent = new Intent(MainActivity.this, TipsAndInfo.class);
                startActivity(intent);
            }
        });

        // Delay the internet connection check by 1.5 seconds using CountDownTimer
        new CountDownTimer(1500, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                // No action needed for each tick
            }

            @Override
            public void onFinish() {
                // After 1.5 seconds, check for an internet connection
                checkInternetConnection();
            }
        }.start();
    }

    // Method to check if the device has an active internet connection
    private void checkInternetConnection() {
        // Get the ConnectivityManager system service
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        // Get the active network info
        NetworkInfo activeNetwork = cm != null ? cm.getActiveNetworkInfo() : null;

        // If there's no active network or if it's not connected, show a toast
        if (activeNetwork == null || !activeNetwork.isConnected()) {
            Toast.makeText(MainActivity.this, "Some features may not be available without internet", Toast.LENGTH_SHORT).show();
        }
    }
}
