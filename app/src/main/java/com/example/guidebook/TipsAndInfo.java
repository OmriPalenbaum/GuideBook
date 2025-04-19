package com.example.guidebook;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

public class TipsAndInfo extends AppCompatActivity {

    ImageButton ibBack;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tips_and_info);

        ibBack = findViewById(R.id.imageButtonBack);

        ibBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TipsAndInfo.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }
}