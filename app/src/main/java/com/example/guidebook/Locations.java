package com.example.guidebook;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.provider.MediaStore;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class Locations extends AppCompatActivity {
    Button btAdd;
    ImageButton ibBack;
    TextView tvTitle;
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
            // Hardcoded data, few locations
            Boulder[] arrBoulder = new Boulder[5];
            arrBoulder[0] = new Boulder("Timna", "Park Timna", "4.6/5", 1, convertResourceToByteArray(R.drawable.the_rock));
            arrBoulder[1] = new Boulder("Cabara cliff", "Zichron Yaakov", "3.4/5", 1, convertResourceToByteArray(R.drawable.the_rock));
            arrBoulder[2] = new Boulder("Beit Arie", "Beit Arie", "4/5", 1, convertResourceToByteArray(R.drawable.the_rock));
            arrBoulder[3] = new Boulder("Zanuah cliff", "Zanuah river", "3.7/5", 1, convertResourceToByteArray(R.drawable.the_rock));
            arrBoulder[4] = new Boulder("Beit Oren", "Oren river", "4.6/5", 1, convertResourceToByteArray(R.drawable.the_rock));
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

    private byte[] convertResourceToByteArray(int resourceId) {
        try {
            // Load the drawable resource as a Bitmap
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), resourceId);

            // Convert Bitmap to byte array
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
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
//    // Create a method to show the password dialog
//    private void showPasswordDialog() {
//        // Create an EditText for user input
//        final EditText passwordInput = new EditText(this);
//        passwordInput.setHint("Enter developer password");
//        passwordInput.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
//
//        // Create the dialog
//        AlertDialog.Builder builder = new AlertDialog.Builder(this);
//        builder.setTitle("Password Required")
//                .setMessage("Please enter developer password:")
//                .setView(passwordInput)
//                .setPositiveButton("OK", (dialog, which) -> {
//                    // Get the entered password
//                    String password = passwordInput.getText().toString();
//
//                    // Validate the password
//                    if (password.isEmpty()) {
//                        showErrorMessage("Password cannot be empty");
//                    } else if (password.equals("PASS")) {
//                        //opens developer page if 5 clicks detected
//                        Intent intent = new Intent(Locations.this, Developer_Mode.class);
//                        startActivity(intent);
//                        //shows a TOAST that says "Developer mode activated"
//                        Toast.makeText(Locations.this, "Developer mode activated!", Toast.LENGTH_SHORT).show();
//                        showSuccessMessage("Password is correct!");
//                    } else {
//                        // Handle incorrect password
//                        showErrorMessage("Incorrect password!");
//                    }
//                })
//                .setNegativeButton("Cancel", (dialog, which) -> {
//                    // Dismiss the dialog
//                    dialog.dismiss();
//                })
//                .show();
//    }
//
//    // Helper method to show error messages
//    private void showErrorMessage(String message) {
//        new AlertDialog.Builder(this)
//                .setTitle("Error")
//                .setMessage(message)
//                .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
//                .show();
//    }
//
//    // Helper method to show success messages
//    private void showSuccessMessage(String message) {
//        new AlertDialog.Builder(this)
//                .setTitle("Success")
//                .setMessage(message)
//                .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
//                .show();
//    }
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