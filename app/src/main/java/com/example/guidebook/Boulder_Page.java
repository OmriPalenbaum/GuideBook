package com.example.guidebook;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class Boulder_Page extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_boulder_page);

        Boulder boulder = (Boulder) getIntent().getSerializableExtra("boulderObject");
    }
}