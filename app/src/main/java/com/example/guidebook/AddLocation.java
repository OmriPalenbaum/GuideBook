package com.example.guidebook;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class AddLocation extends AppCompatActivity {
    EditText etName;
    EditText etAddress;
    EditText etRating;
    ImageView setCamera;
    ImageButton ibBack2;
    Button btSubmit;
    Boulder newBoulder;
    ActivityResultLauncher<Intent> arCamera;
    ActivityResultLauncher<Intent> arGallery;
    DatabaseHelper dbHelper = new DatabaseHelper(this);
    byte[] imageBytes; // To store the image as byte array

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_location);

        ibBack2 = findViewById(R.id.imageButton2);
        etName = findViewById(R.id.etName);
        etAddress = findViewById(R.id.etAddress);
        etRating = findViewById(R.id.etRating);
        setCamera = findViewById(R.id.ivCamera);
        btSubmit = findViewById(R.id.bt);

        ActivityCompat.requestPermissions(this,
                new String[]{android.Manifest.permission.CAMERA,
                        android.Manifest.permission.WRITE_EXTERNAL_STORAGE},
                1);

        // Register for Camera Result
        arCamera = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                            Bitmap bitmap = (Bitmap) result.getData().getExtras().get("data");
                            setCamera.setImageBitmap(bitmap);

                            // Convert Bitmap to Byte Array
                            imageBytes = convertBitmapToByteArray(bitmap);
                        }
                    }
                });

        // Register for Gallery Result
        arGallery = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                            Uri selectedImageUri = result.getData().getData();
                            try {
                                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImageUri);
                                setCamera.setImageBitmap(bitmap);

                                // Convert Bitmap to Byte Array
                                imageBytes = convertBitmapToByteArray(bitmap);
                            } catch (IOException e) {
                                e.printStackTrace();
                                Toast.makeText(AddLocation.this, "Failed to load image!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });

        // Show dialog to choose between Camera or Gallery
        setCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showImageSourceDialog();
            }
        });

        ibBack2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AddLocation.this, Locations.class);
                startActivity(intent);
            }
        });

        btSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Save the Boulder to the database
                int test = saveBoulderData();
                if (test == 1) {
                    Toast.makeText(AddLocation.this, "Data submitted successfully", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(AddLocation.this, Locations.class);
                    startActivity(intent);
                }
            }
        });
    }

    // Show a dialog to choose between Camera or Gallery
    private void showImageSourceDialog() {
        String[] options = {"Take Photo", "Choose from Gallery"};
        new AlertDialog.Builder(this)
                .setTitle("Select Image Source")
                .setItems(options, (dialog, which) -> {
                    if (which == 0) {
                        // Launch Camera
                        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        arCamera.launch(cameraIntent);
                    } else if (which == 1) {
                        // Launch Gallery
                        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        arGallery.launch(galleryIntent);
                    }
                })
                .show();
    }

    // Convert Bitmap to Byte Array
    private byte[] convertBitmapToByteArray(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        return stream.toByteArray();
    }

    // Save the Boulder to the database
    private int saveBoulderData() {
        String name = etName.getText().toString();
        String address = etAddress.getText().toString();
        String rating = etRating.getText().toString();


        boolean hasError = false;

        // Validate name
        if (name.isEmpty()) {
            etName.setError("Name is required");
            hasError = true;
        } else {
            etName.setError(null);
        }

        // Validate address
        if (address.isEmpty()) {
            etAddress.setError("Address is required");
            hasError = true;
        } else {
            etAddress.setError(null);
        }

        // Validate rating
        double ratingValue = -1;
        try {
            ratingValue = Double.parseDouble(rating);
            if (ratingValue < 0 || ratingValue > 5) {
                etRating.setError("Rating must be between 0 and 5");
                hasError = true;
            } else {
                etRating.setError(null);
            }
        } catch (NumberFormatException e) {
            etRating.setError("Invalid rating format");
            hasError = true;
        }

        if (hasError) {
            Toast.makeText(this, "Please correct the errors", Toast.LENGTH_SHORT).show();
            return -1;
        }

        // Check if the picture was captured
        if (imageBytes == null) {
            Toast.makeText(this, "Please capture or select an image", Toast.LENGTH_SHORT).show();
            return -1;
        }

        // Create the Boulder object
        newBoulder = new Boulder(name, address, rating + "/5", 0, imageBytes);

        // Save to database
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_NAME, newBoulder.getName());
        values.put(DatabaseHelper.COLUMN_ADDRESS, newBoulder.getAddress());
        values.put(DatabaseHelper.COLUMN_RATING, newBoulder.getRating());
        values.put(DatabaseHelper.IS_ACTIVE, newBoulder.getIsActive());
        values.put(DatabaseHelper.COLUMN_IMAGE, newBoulder.getImageBytes());
        db.insert(DatabaseHelper.TABLE_LOCATIONS, null, values);

        db.close();
        return 1;
    }
}
