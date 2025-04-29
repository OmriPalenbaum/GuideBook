package com.example.guidebook;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

public class TipsAndInfo extends AppCompatActivity {

    ImageButton ibBack; // Button to go back to the main activity

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tips_and_info); // Set the content view for this activity

        ibBack = findViewById(R.id.imageButtonBack); // Find the back button by its ID

        // Set an OnClickListener to handle the back button click
        ibBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create an Intent to go back to MainActivity
                Intent intent = new Intent(TipsAndInfo.this, MainActivity.class);
                startActivity(intent); // Start MainActivity
            }
        });
    }
}
