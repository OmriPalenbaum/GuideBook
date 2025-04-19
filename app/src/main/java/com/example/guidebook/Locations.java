package com.example.guidebook;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.InputType;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Comparator;

public class Locations extends AppCompatActivity {
    Button btAdd;
    ImageButton ibBack;
    TextView tvTitle;
    Spinner spinnerSort;
    RecyclerView recyclerView;
    FrameLayout fragmentContainer;
    Item_Adapter adapter;
    DatabaseHelper dbHelper = new DatabaseHelper(this);
    SQLiteDatabase db;
    ContentValues cv = new ContentValues();

    //method to insert hardcoded data
    public void insertTable() {
        db = dbHelper.getWritableDatabase();
        // Check if data is already inserted
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + DatabaseHelper.TABLE_LOCATIONS, null);
        cursor.moveToFirst();
        int count = cursor.getInt(0);
        cursor.close();
        // If no data, insert the new data
        if (count == 0) {
            // Hardcoded data, a few locations
            Boulder[] arrBoulder = new Boulder[5];
            arrBoulder[0] = new Boulder("Timna", "Eilat", "4.6/5", 1, convertResourceToByteArray(R.drawable.cliff_image_timna));
            arrBoulder[1] = new Boulder("Cabara cliff", "Zichron Ya'akov", "3.4/5", 1, convertResourceToByteArray(R.drawable.cliff_image_cabara));
            arrBoulder[2] = new Boulder("Beit Arie", "Ofarim", "4/5", 1, convertResourceToByteArray(R.drawable.cliff_image_arie));
            arrBoulder[3] = new Boulder("Zanoah cliff", "Zanoah", "3.7/5", 1, convertResourceToByteArray(R.drawable.cliff_image_zanoah));
            arrBoulder[4] = new Boulder("Beit Oren", "Beit Oren", "4.6/5", 1, convertResourceToByteArray(R.drawable.cliff_image_oren));
            // Loop through each boulder and insert it into the database
            for (Boulder boulder : arrBoulder) {
                cv.clear();
                cv.put(DatabaseHelper.COLUMN_NAME, boulder.getName());
                cv.put(DatabaseHelper.COLUMN_ADDRESS, boulder.getAddress());
                cv.put(DatabaseHelper.COLUMN_RATING, boulder.getRating());
                cv.put(DatabaseHelper.IS_ACTIVE, boulder.getIsActive());
                cv.put(DatabaseHelper.COLUMN_IMAGE, boulder.getImageBytes());
                db.insert(DatabaseHelper.TABLE_LOCATIONS, null, cv);
            }
        }
        db.close();
    }

    //method to retrieve all data from the database
    public ArrayList<Boulder> getAllRecords() {
        String name, address, rating;
        int isActive;
        byte[] imageBytes; // For storing image as byte array
        db = dbHelper.getReadableDatabase();
        ArrayList<Boulder> list = new ArrayList<>();
//        ArrayList<byte[]> imageList = new ArrayList<>();
        Cursor cursor = db.query(DatabaseHelper.TABLE_LOCATIONS, null, null, null, null, null, null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                //gets the index for each column
                int indexName = cursor.getColumnIndex(DatabaseHelper.COLUMN_NAME);
                int indexAddress = cursor.getColumnIndex(DatabaseHelper.COLUMN_ADDRESS);
                int indexRating = cursor.getColumnIndex(DatabaseHelper.COLUMN_RATING);
                int indexIsActive = cursor.getColumnIndex(DatabaseHelper.IS_ACTIVE);
                int indexImage = cursor.getColumnIndex(DatabaseHelper.COLUMN_IMAGE); // Now a TEXT column
                //checks if all the indexes are valid
                if (indexName != -1 && indexAddress != -1 && indexRating != -1 && indexIsActive != -1 && indexImage != -1) {
                    name = cursor.getString(indexName);
                    address = cursor.getString(indexAddress);
                    rating = cursor.getString(indexRating);
                    isActive = cursor.getInt(indexIsActive);
                    // Retrieve image as byte array
                    imageBytes = cursor.getBlob(indexImage);

                    //puts the data in a new Boulder object
                    Boulder record = new Boulder(name, address, rating, isActive, imageBytes);
                    //adds the new object to the list
                    list.add(record);
//                    imageList.add(record.getImageBytes());
                }
            }
            cursor.close();
        }
        db.close();
        return list;
    }

    //Converts a drawable resource to a compressed byte array, resizing it to reduce memory usage
    private byte[] convertResourceToByteArray(int resourceId) {
        try {
            // Step 1: Decode with inJustDecodeBounds=true to get original dimensions
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeResource(getResources(), resourceId, options);
            int originalWidth = options.outWidth;
            int originalHeight = options.outHeight;

            // Step 2: Calculate scale factor to downsample image
            int desiredWidth = 800; // You can change this based on needs
            int desiredHeight = 800;
            int scale = 1;
            while ((originalWidth / scale > desiredWidth) || (originalHeight / scale > desiredHeight)) {
                scale *= 2;
            }

            // Step 3: Decode the bitmap with downsampling
            options.inJustDecodeBounds = false;
            options.inSampleSize = scale;
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), resourceId, options);

            // Step 4: Compress to JPEG
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 50, stream);
            return stream.toByteArray();

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_locations);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        fragmentContainer = findViewById(R.id.fragmentContainer);
        btAdd = findViewById(R.id.btAdd);
        ibBack = findViewById(R.id.imageButton1);
        tvTitle = findViewById(R.id.tvLocations);
        spinnerSort = findViewById(R.id.spinnerSort);

        String[] sortOptions = getResources().getStringArray(R.array.sort_options);
        int[] sortIcons = new int[] {
                R.drawable.icon_a_to_z,
                R.drawable.icon_star
        };

        CustomSpinnerAdapter spin_adapter = new CustomSpinnerAdapter(this, sortOptions, sortIcons);
        spinnerSort.setAdapter(spin_adapter);


        insertTable();

        //creates a list if all the boulders
        final ArrayList<Boulder> boulderList = getAllRecords();
        //creates a list of all the active boulders
        ArrayList<Boulder> activeBoulders = new ArrayList<>();
        for (Boulder item: boulderList){
            if (item.getIsActive()){
                activeBoulders.add(item);
            }
        }

        // Initialize the adapter with the list of boulders and an OnItemClickListener
        adapter = new Item_Adapter(activeBoulders, new Item_Adapter.OnItemClickListener() {
            @Override
            public void onItemClick(Boulder boulder, int position) {
                // Handle the item click, fragment transaction will happen inside the adapter
                String name = boulder.getName();
                Toast.makeText(Locations.this, "Clicked: " + name, Toast.LENGTH_SHORT).show();
            }
        });

        // Set the adapter to the RecyclerView
        recyclerView.setAdapter(adapter);

        btAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //opens the AddLocation page
                Intent intent = new Intent(Locations.this, AddLocation.class);
                startActivity(intent);
            }
        });
        ibBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Locations.this, MainActivity.class);
                startActivity(intent);
            }
        });

        tvTitle.setOnClickListener(new MultiClickListener() {
            @Override
            public void onMultipleClicks(View view) {
                showPasswordDialog();
            }
        });

        // Sorting system logic for Spinner
        spinnerSort.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0: // Sort by name
                        activeBoulders.sort(Comparator.comparing(Boulder::getName));
                        break;
                    case 1: // Sort by rating
                        activeBoulders.sort((b1, b2) -> {
                            double r1 = Double.parseDouble(b1.getRating().replace("/5", ""));
                            double r2 = Double.parseDouble(b2.getRating().replace("/5", ""));
                            return Double.compare(r2, r1); // Descending
                        });
                        break;
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void showPasswordDialog() {
        final EditText passwordInput = new EditText(this);
        passwordInput.setHint("Enter developer password");
        passwordInput.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Password Required")
                .setMessage("Please enter developer password:")
                .setView(passwordInput)
                .setPositiveButton("OK", (dialog, which) -> {
                    String password = passwordInput.getText().toString();

                    // Hide the keyboard before proceeding
                    hideKeyboard(passwordInput);

                    if (password.isEmpty()) {
                        showErrorMessage("Password cannot be empty");
                    } else if (password.equals("PASS")) {
                        showCountdown();
                    } else {
                        showErrorMessage("Incorrect password!");
                    }
                })
                .setNegativeButton("Cancel", (dialog, which) -> {
                    // Hide the keyboard when the dialog is dismissed
                    hideKeyboard(passwordInput);
                    dialog.dismiss();
                })
                .setOnDismissListener(dialog -> {
                    // Additional safety: Ensure the keyboard is hidden when dialog is dismissed
                    hideKeyboard(passwordInput);
                })
                .show();

        // Show the keyboard when the dialog opens
        showKeyboard(passwordInput);
    }

    private void showCountdown() {
        final TextView countdownView = new TextView(this);
        countdownView.setTextSize(24); // Customize text size
        countdownView.setPadding(50, 50, 50, 50); // Add padding for better appearance

        AlertDialog countdownDialog = new AlertDialog.Builder(this)
                .setTitle("Launching Developer Mode")
                .setView(countdownView)
                .setCancelable(false) // Prevent user from dismissing the countdown
                .create();

        countdownDialog.show();

        // Start a 5-second countdown
        new CountDownTimer(5000, 1000) {
            public void onTick(long millisUntilFinished) {
                countdownView.setText("Opening in " + millisUntilFinished / 1000 + " seconds...");
            }

            public void onFinish() {
                countdownDialog.dismiss();
                Intent intent = new Intent(Locations.this, Developer_Mode.class);
                startActivity(intent);

                // Show a toast message
                Toast.makeText(Locations.this, "Developer mode activated!", Toast.LENGTH_SHORT).show();
            }
        }.start();
    }
    private void showErrorMessage(String message) {
        new AlertDialog.Builder(this)
                .setTitle("Error")
                .setMessage(message)
                .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                .show();
    }

    //had a problem with the IME callback so added this
    private void hideKeyboard(EditText editText) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
        }
    }
    private void showKeyboard(EditText editText) {
        editText.requestFocus();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);
        }
    }

}