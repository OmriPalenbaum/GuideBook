package com.example.guidebook;


import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Switch;
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
import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.app.ProgressDialog;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;

import java.util.List;
import java.util.Locale;
public class AddLocation extends AppCompatActivity {
    EditText etName;
    EditText etAddress;
    EditText etRating;
    ImageView setCamera;
    ImageButton ibBack2;
    ImageButton ibCurrentLocation; // Current location button
    Switch switchIsDone;
    Button btSubmit;
    Boulder newBoulder;
    ActivityResultLauncher<Intent> arCamera;
    ActivityResultLauncher<Intent> arGallery;
    DatabaseHelper dbHelper = new DatabaseHelper(this);
    byte[] imageBytes; // To store the image as byte array

    // For location services
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1001;
    private FusedLocationProviderClient fusedLocationClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_location);

        // Initialize view elements
        ibBack2 = findViewById(R.id.imageButton2);
        etName = findViewById(R.id.etName);
        etAddress = findViewById(R.id.etAddress);
        etRating = findViewById(R.id.etRating);
        setCamera = findViewById(R.id.ivCamera);
        switchIsDone = findViewById(R.id.switchIsDone);
        btSubmit = findViewById(R.id.bt);
        ibCurrentLocation = findViewById(R.id.ibCurrentLocation); // Initialize current location button

        // Initialize the location client
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // Setup current location button click listener
        ibCurrentLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get current location when button is clicked
                getCurrentLocation();
            }
        });

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
                    //shows a dialog to explain how the submission works
                    new AlertDialog.Builder(AddLocation.this)
                            .setTitle("Submission Received")
                            .setMessage("Your boulder information has been submitted and is now awaiting approval from our team. \nThis process typically takes about one week. \nIf more than a week has passed, feel free to contact us via the Tips & Info page.")
                            .setCancelable(false)
                            .setPositiveButton("OK", (dialog, which) -> {
                                // opens the locations page after clicking OK
                                Intent intent = new Intent(AddLocation.this, Locations.class);
                                startActivity(intent);
                            })
                            .show();
                }
            }
        });
    }

    //Gets the user's current location if permissions are granted or requests permissions if they aren't already granted
    private void getCurrentLocation() {
        // Check if location permissions are granted
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Request permissions if not granted
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
            return;
        }

        // Show loading indicator
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Getting your location...");
        progressDialog.show();

        // Request current location
        fusedLocationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null)
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        progressDialog.dismiss();
                        if (location != null) {
                            // Use Geocoder to get address from coordinates
                            getAddressFromLocation(location);
                        } else {
                            Toast.makeText(AddLocation.this, "Couldn't get location. Please try again.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(AddLocation.this, "Location error: " + e.getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    //Converts latitude and longitude to a readable address in English
    private void getAddressFromLocation(Location location) {
        // Use English locale specifically for the Geocoder
        Geocoder geocoder = new Geocoder(this, Locale.ENGLISH);

        try {
            // Get address from coordinates
            List<Address> addresses = geocoder.getFromLocation(
                    location.getLatitude(), location.getLongitude(), 1);

            if (addresses != null && addresses.size() > 0) {
                Address address = addresses.get(0);
                StringBuilder addressText = new StringBuilder();

                // Try to get street address
                String street = address.getThoroughfare();
                String streetNumber = address.getSubThoroughfare();

                if (streetNumber != null && !streetNumber.isEmpty()) {
                    addressText.append(streetNumber).append(" ");
                }

                if (street != null && !street.isEmpty()) {
                    addressText.append(street).append(", ");
                }

                // Always include city name
                String city = address.getLocality();
                if (city != null && !city.isEmpty()) {
                    addressText.append(city);
                } else {
                    // Fallback to subadmin area if city is not available
                    String area = address.getSubAdminArea();
                    if (area != null && !area.isEmpty()) {
                        addressText.append(area);
                    }
                }

                // Don't need to add Israel as country for local addresses
                String country = address.getCountryName();
                if (country != null && !country.isEmpty() && !country.equals("Israel")) {
                    addressText.append(", ").append(country);
                }

                // Set the address in the EditText
                etAddress.setText(addressText.toString());

                // Log the full address details for debugging
                Log.d("AddressInfo", "Full address: " + address.getAddressLine(0));
            } else {
                // If no address found, use coordinates
                String coords = String.format(Locale.US, "%.6f, %.6f",
                        location.getLatitude(), location.getLongitude());
                etAddress.setText(coords);
            }
        } catch (IOException e) {
            e.printStackTrace();
            // If geocoding fails, use coordinates
            String coords = String.format(Locale.US, "%.6f, %.6f",
                    location.getLatitude(), location.getLongitude());
            etAddress.setText(coords);
            Toast.makeText(this, "Couldn't get address, using coordinates instead",
                    Toast.LENGTH_SHORT).show();
        }
    }

    //Handles the permission request result
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, get location
                getCurrentLocation();
            } else {
                // Permission denied
                Toast.makeText(this, "Location permission required to use this feature",
                        Toast.LENGTH_SHORT).show();
            }
        }
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

        boolean isDone = switchIsDone.isChecked();
        int intIsDone = 0;
        if (isDone) {intIsDone = 1;}

        // Create the Boulder object
        newBoulder = new Boulder(name, address, rating + "/5", 0, intIsDone, imageBytes);

        // Save to database
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_NAME, newBoulder.getName());
        values.put(DatabaseHelper.COLUMN_ADDRESS, newBoulder.getAddress());
        values.put(DatabaseHelper.COLUMN_RATING, newBoulder.getRating());
        values.put(DatabaseHelper.COLUMN_IS_ACTIVE, newBoulder.getIsActive());
        values.put(DatabaseHelper.COLUMN_IS_DONE, newBoulder.getIsDone());
        values.put(DatabaseHelper.COLUMN_IMAGE, newBoulder.getImageBytes());
        db.insert(DatabaseHelper.TABLE_LOCATIONS, null, values);

        db.close();
        return 1;
    }
}