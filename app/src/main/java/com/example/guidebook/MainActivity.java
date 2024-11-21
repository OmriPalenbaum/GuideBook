package com.example.guidebook;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;

import java.util.ArrayList;
import java.util.jar.Attributes;

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
                //opens the Location page
                Intent intent = new Intent(MainActivity.this, Locations.class);
                startActivity(intent);
            }
        });
        btTips.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //opens the Tips page
                Intent intent = new Intent(MainActivity.this, TipsAndInfo.class);
                startActivity(intent);
            }
        });
    }
}