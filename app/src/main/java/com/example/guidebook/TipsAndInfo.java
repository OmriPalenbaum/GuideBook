package com.example.guidebook;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.graphics.Paint;
import android.widget.Toast;

public class TipsAndInfo extends AppCompatActivity {

    ImageButton ibBack; // Button to go back to the main activity
    TextView tvWhatToBring;
    TextView tvHowToPrepare;
    TextView tvWhatToExpect;
    TextView tvHowToStaySafe;
    TextView tvTypesOfGrips;
    TextView tvTypesOfClimbing;
    TextView whatToBringText;
    TextView howToPrepareText;
    TextView whatToExpectText;
    TextView howToStaySafeText;
    TextView typesOfGripsText;
    TextView typesOfClimbingText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tips_and_info); // Set the content view for this activity

        ibBack = findViewById(R.id.imageButtonBack); // Find the back button by its ID
        tvWhatToBring = findViewById(R.id.tvWhatToBring);
        tvHowToPrepare = findViewById(R.id.tvHowToPrepare);
        tvWhatToExpect = findViewById(R.id.tvWhatToExpect);
        tvHowToStaySafe = findViewById(R.id.tvHowToStaySafe);
        tvTypesOfGrips = findViewById(R.id.tvTypesOfGrips);
        tvTypesOfClimbing = findViewById(R.id.tvTypesOfClimbing);
        whatToBringText = findViewById(R.id.whatToBringText);
        howToPrepareText = findViewById(R.id.howToPrepareText);
        whatToExpectText = findViewById(R.id.whatToExpectText);
        howToStaySafeText = findViewById(R.id.howToStaySafeText);
        typesOfGripsText = findViewById(R.id.typesOfGripsText);
        typesOfClimbingText = findViewById(R.id.typesOfClimbingText);
        TextView federationLink = findViewById(R.id.tvFederationWebsite);
        TextView contactUs = findViewById(R.id.tvContactUs);
        TextView emailUs = findViewById(R.id.tvEmailUs);

        // Set an OnClickListener to handle the back button click
        ibBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create an Intent to go back to MainActivity
                Intent intent = new Intent(TipsAndInfo.this, MainActivity.class);
                startActivity(intent); // Start MainActivity
            }
        });

        // Set listeners for each TextView to toggle visibility of corresponding text
        tvWhatToBring.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Toggle the visibility of the 'What to bring' text
                if (whatToBringText.getVisibility() == View.GONE) {
                    whatToBringText.setVisibility(View.VISIBLE);
                    tvWhatToBring.setText("What to Bring▶");
                } else {
                    whatToBringText.setVisibility(View.GONE);
                    tvWhatToBring.setText("What to Bring▼");
                }
            }
        });

        tvHowToPrepare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Toggle the visibility of the 'How to prepare' text
                if (howToPrepareText.getVisibility() == View.GONE) {
                    howToPrepareText.setVisibility(View.VISIBLE);
                    tvHowToPrepare.setText("How to Prepare▶");
                } else {
                    howToPrepareText.setVisibility(View.GONE);
                    tvHowToPrepare.setText("How to Prepare▼");
                }
            }
        });

        tvWhatToExpect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Toggle the visibility of the 'What to expect' text
                if (whatToExpectText.getVisibility() == View.GONE) {
                    whatToExpectText.setVisibility(View.VISIBLE);
                    tvWhatToExpect.setText("What to Expect▶");
                } else {
                    whatToExpectText.setVisibility(View.GONE);
                    tvWhatToExpect.setText("What to Expect▼");
                }
            }
        });

        tvHowToStaySafe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Toggle the visibility of the 'How to Stay Safe' text
                if (howToStaySafeText.getVisibility() == View.GONE) {
                    howToStaySafeText.setVisibility(View.VISIBLE);
                    tvHowToStaySafe.setText("How to Stay Safe▶");
                } else {
                    howToStaySafeText.setVisibility(View.GONE);
                    tvHowToStaySafe.setText("How to Stay Safe▼");
                }
            }
        });

        tvTypesOfGrips.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Toggle the visibility of the 'Types of Grips' text
                if (typesOfGripsText.getVisibility() == View.GONE) {
                    typesOfGripsText.setVisibility(View.VISIBLE);
                    tvTypesOfGrips.setText("Types of Grips▶");
                } else {
                    typesOfGripsText.setVisibility(View.GONE);
                    tvTypesOfGrips.setText("Types of Grips▼");
                }
            }
        });

        tvTypesOfClimbing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Toggle the visibility of the 'Types of Climbing' text
                if (typesOfClimbingText.getVisibility() == View.GONE) {
                    typesOfClimbingText.setVisibility(View.VISIBLE);
                    tvTypesOfClimbing.setText("Types of Climbing▶");
                } else {
                    typesOfClimbingText.setVisibility(View.GONE);
                    tvTypesOfClimbing.setText("Types of Climbing▼");
                }
            }
        });

        federationLink.setPaintFlags(federationLink.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        emailUs.setPaintFlags(emailUs.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        contactUs.setPaintFlags(contactUs.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        // link to the ILCC site
        federationLink.setOnClickListener(v -> {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://ilca.org.il/rock-3/"));
            startActivity(browserIntent);
        });

        // open Email with the address and content
        emailUs.setOnClickListener(v -> {
            Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
            emailIntent.setData(Uri.parse("mailto:omri5455@gmail.com"));
            try {
                startActivity(Intent.createChooser(emailIntent, "Send email using..."));
            } catch (android.content.ActivityNotFoundException ex) {
                Toast.makeText(this, "No email clients installed on your device.", Toast.LENGTH_SHORT).show();
            }
        });

        // open phone app with the number
        contactUs.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse("tel:0509656522"));
            startActivity(intent);
        });
    }
}

