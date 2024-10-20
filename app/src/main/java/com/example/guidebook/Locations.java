package com.example.guidebook;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class Locations extends AppCompatActivity {

    ListView lvLocations;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_locations);

        lvLocations = findViewById(R.id.lvLocations);
        ArrayList<String> locationList = new ArrayList<>();
        locationList.add("Helium");
        locationList.add("Hydrogen");
        locationList.add("Oxygen");
        locationList.add("Iron");
        locationList.add("Gold");
        locationList.add("Nitrogen");

        ArrayAdapter lList = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, locationList);
        lvLocations.setAdapter(lList);
    }
}