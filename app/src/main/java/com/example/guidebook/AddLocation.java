package com.example.guidebook;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class AddLocation extends AppCompatActivity {
    EditText etName;
    EditText etAddress;
    EditText etRating;
    ImageView setCamera;
    Button btSubmit;
    Boulder newBoulder;
    ActivityResultLauncher<Intent> arSmall;
    DatabaseHelper dbHelper = new DatabaseHelper(this);
    byte[] imageBytes; // To store the image as byte array      *******


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_location);

        etName = findViewById(R.id.etName);
        etAddress = findViewById(R.id.etAddress);
        etRating = findViewById(R.id.etRating);
        setCamera = findViewById(R.id.ivCamera);
        btSubmit = findViewById(R.id.bt);

        ActivityCompat.requestPermissions(this,
                new String[]{android.Manifest.permission.CAMERA,
                        android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                },
                1);

        arSmall = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
//                        Intent data = result.getData();
//                        Bitmap bitmap = (Bitmap) data.getExtras().get("data");
//                        setCamera.setImageBitmap(bitmap);
//                        setCamera.setImageBitmap(result.getData().getParcelableExtra("data"));

                        if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                            Bitmap bitmap = (Bitmap) result.getData().getExtras().get("data");
                            setCamera.setImageBitmap(bitmap);

                            // Convert the Bitmap to a Byte Array
                            imageBytes = convertBitmapToByteArray(bitmap);
                        }
                    }
                });

        setCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                arSmall.launch(intent);
            }
        });
        btSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveBoulderData();
                CharSequence text = "Data submitted successfully";
                int duration = Toast.LENGTH_SHORT;
                Toast.makeText (AddLocation.this, text, duration).show();
                Intent intent = new Intent(AddLocation.this, Locations.class);
                startActivity(intent);
            }

        });
    }
    private byte[] convertBitmapToByteArray(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        return stream.toByteArray();
    }

    private void saveBoulderData() {
        String name = etName.getText().toString();
        String address = etAddress.getText().toString();
        String rating = etRating.getText().toString();

        if (name.isEmpty() || address.isEmpty() || rating.isEmpty() || imageBytes == null) {
            Toast.makeText(this, "Please fill all fields and capture an image", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create the Boulder object
        newBoulder = new Boulder(name, address, rating, "no", imageBytes);

        // Save to database
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_NAME, newBoulder.getName());
        values.put(DatabaseHelper.COLUMN_ADDRESS, newBoulder.getAddress());
        values.put(DatabaseHelper.COLUMN_RATING, newBoulder.getRating());
        values.put(DatabaseHelper.IS_ACTIVE, newBoulder.getIsActive());
        values.put(DatabaseHelper.COLUMN_IMAGE, newBoulder.getImageBytes()); // Save as byte array (BLOB)
        db.insert(DatabaseHelper.TABLE_LOCATIONS, null, values);

        db.close();
    }
}