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
    Button btLocations;
    Button btTips;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btLocations = findViewById(R.id.btLocations);
        btTips = findViewById(R.id.btTips);

        btLocations.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Opens the Location page
                Intent intent = new Intent(MainActivity.this, Locations.class);
                startActivity(intent);
            }
        });

        btTips.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Opens the Tips page
                Intent intent = new Intent(MainActivity.this, TipsAndInfo.class);
                startActivity(intent);
            }
        });

        // Delay checking internet connection by 1.5 seconds using CountDownTimer
        new CountDownTimer(1500, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
            }

            @Override
            public void onFinish() {
                // After 1.5 seconds, check if there's an internet connection
                checkInternetConnection();
            }
        }.start();
    }

    // Check if the device is connected to the internet
    private void checkInternetConnection() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm != null ? cm.getActiveNetworkInfo() : null;

        // If not connected to the internet
        if (activeNetwork == null || !activeNetwork.isConnected()) {
            Toast.makeText(MainActivity.this, "Some features may not be available without an internet connection.", Toast.LENGTH_SHORT).show();
        }
    }
}