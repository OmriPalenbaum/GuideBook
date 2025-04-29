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
import android.widget.CheckBox;
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
    CheckBox checkBoxShowDone;
    RecyclerView recyclerView;
    FrameLayout fragmentContainer;
    Item_Adapter adapter;
    DatabaseHelper dbHelper = new DatabaseHelper(this);
    SQLiteDatabase db;
    ContentValues cv = new ContentValues();

    // Inserts hardcoded data if the table is empty
    public void insertTable() {
        db = dbHelper.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + DatabaseHelper.TABLE_LOCATIONS, null);
        cursor.moveToFirst();
        int count = cursor.getInt(0);
        cursor.close();

        // Insert sample data if the table is empty
        if (count == 0) {
            Boulder[] arrBoulder = new Boulder[5];
            arrBoulder[0] = new Boulder("Timna", "Eilat", "4.6/5", 1, 0, convertResourceToByteArray(R.drawable.cliff_image_timna));
            arrBoulder[1] = new Boulder("Cabara cliff", "Zichron Ya'akov", "3.4/5", 1, 0, convertResourceToByteArray(R.drawable.cliff_image_cabara));
            arrBoulder[2] = new Boulder("Beit Arie", "Ofarim", "4/5", 1, 0, convertResourceToByteArray(R.drawable.cliff_image_arie));
            arrBoulder[3] = new Boulder("Zanoah cliff", "Zanoah", "3.7/5", 1, 0, convertResourceToByteArray(R.drawable.cliff_image_zanoah));
            arrBoulder[4] = new Boulder("Beit Oren", "Beit Oren", "4.6/5", 1, 0, convertResourceToByteArray(R.drawable.cliff_image_oren));

            // Insert boulders into the database
            for (Boulder boulder : arrBoulder) {
                cv.clear();
                cv.put(DatabaseHelper.COLUMN_NAME, boulder.getName());
                cv.put(DatabaseHelper.COLUMN_ADDRESS, boulder.getAddress());
                cv.put(DatabaseHelper.COLUMN_RATING, boulder.getRating());
                cv.put(DatabaseHelper.COLUMN_IS_ACTIVE, boulder.getIsActive());
                cv.put(DatabaseHelper.COLUMN_IS_DONE, boulder.getIsDone());
                cv.put(DatabaseHelper.COLUMN_IMAGE, boulder.getImageBytes());
                db.insert(DatabaseHelper.TABLE_LOCATIONS, null, cv);
            }
        }
        db.close();
    }

    // Retrieves all records from the database
    public ArrayList<Boulder> getAllRecords() {
        String name, address, rating;
        int isActive, isDone;
        byte[] imageBytes;
        db = dbHelper.getReadableDatabase();
        ArrayList<Boulder> list = new ArrayList<>();
        Cursor cursor = db.query(DatabaseHelper.TABLE_LOCATIONS, null, null, null, null, null, null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                // Extracts data from cursor
                int indexName = cursor.getColumnIndex(DatabaseHelper.COLUMN_NAME);
                int indexAddress = cursor.getColumnIndex(DatabaseHelper.COLUMN_ADDRESS);
                int indexRating = cursor.getColumnIndex(DatabaseHelper.COLUMN_RATING);
                int indexIsActive = cursor.getColumnIndex(DatabaseHelper.COLUMN_IS_ACTIVE);
                int indexIsDone = cursor.getColumnIndex(DatabaseHelper.COLUMN_IS_DONE);
                int indexImage = cursor.getColumnIndex(DatabaseHelper.COLUMN_IMAGE);
                if (indexName != -1 && indexAddress != -1 && indexRating != -1 && indexIsActive != -1 && indexImage != -1) {
                    name = cursor.getString(indexName);
                    address = cursor.getString(indexAddress);
                    rating = cursor.getString(indexRating);
                    isActive = cursor.getInt(indexIsActive);
                    isDone = cursor.getInt(indexIsDone);
                    imageBytes = cursor.getBlob(indexImage);
                    // Adds new Boulder object to list
                    Boulder record = new Boulder(name, address, rating, isActive, isDone, imageBytes);
                    list.add(record);
                }
            }
            cursor.close();
        }
        db.close();
        return list;
    }

    // Converts a drawable resource to a compressed byte array
    private byte[] convertResourceToByteArray(int resourceId) {
        try {
            // Decode image dimensions first
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeResource(getResources(), resourceId, options);
            int originalWidth = options.outWidth;
            int originalHeight = options.outHeight;

            // Calculate scale factor to downsample image
            int desiredWidth = 800, desiredHeight = 800, scale = 1;
            while ((originalWidth / scale > desiredWidth) || (originalHeight / scale > desiredHeight)) {
                scale *= 2;
            }

            // Decode image with downsample
            options.inJustDecodeBounds = false;
            options.inSampleSize = scale;
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), resourceId, options);

            // Compress and return as byte array
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

        // Initialize views and components
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        fragmentContainer = findViewById(R.id.fragmentContainer);
        btAdd = findViewById(R.id.btAdd);
        ibBack = findViewById(R.id.imageButton1);
        tvTitle = findViewById(R.id.tvLocations);
        spinnerSort = findViewById(R.id.spinnerSort);
        checkBoxShowDone = findViewById(R.id.checkboxShowDone);

        // Set up spinner for sorting options
        String[] sortOptions = getResources().getStringArray(R.array.sort_options);
        int[] sortIcons = new int[] { R.drawable.icon_a_to_z, R.drawable.icon_star };
        CustomSpinnerAdapter spin_adapter = new CustomSpinnerAdapter(this, sortOptions, sortIcons);
        spinnerSort.setAdapter(spin_adapter);

        // Insert data into DB if necessary
        insertTable();

        // Create list of active boulders
        final ArrayList<Boulder> boulderList = getAllRecords();
        ArrayList<Boulder> activeBoulders = new ArrayList<>();
        for (Boulder item : boulderList) {
            if (item.getIsActive()) {
                activeBoulders.add(item);
            }
        }

        // Initialize adapter and handle item clicks
        adapter = new Item_Adapter(activeBoulders) {
            public void onItemClick(Boulder boulder, int position) {
                String name = boulder.getName();
            }
        };
        recyclerView.setAdapter(adapter);

        // Handle checkbox to filter done items
        checkBoxShowDone.setOnCheckedChangeListener((buttonView, isChecked) -> {
            ArrayList<Boulder> filteredList = new ArrayList<>();
            for (Boulder b : boulderList) {
                if (b.getIsActive() && (isChecked || !b.getIsDone())) {
                    filteredList.add(b);
                }
            }
            adapter.updateList(filteredList);
        });

        // Handle Add button click
        btAdd.setOnClickListener(v -> {
            Intent intent = new Intent(Locations.this, AddLocation.class);
            startActivity(intent);
        });

        // Handle Back button click
        ibBack.setOnClickListener(v -> {
            Intent intent = new Intent(Locations.this, MainActivity.class);
            startActivity(intent);
        });

        // Handle title click for developer password dialog
        tvTitle.setOnClickListener(new MultiClickListener() {
            @Override
            public void onMultipleClicks(View view) {
                showPasswordDialog();
            }
        });

        // Handle sorting
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

    // Displays the developer password dialog
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
                    hideKeyboard(passwordInput);

                    if (password.isEmpty()) {
                        showErrorMessage("Password cannot be empty");
                    } else if (password.equals("PASS")) {
                        Intent intent = new Intent(Locations.this, Developer_Mode.class);
                        startActivity(intent);
                        Toast.makeText(Locations.this, "Developer mode activated!", Toast.LENGTH_SHORT).show();
                    } else {
                        showErrorMessage("Incorrect password!");
                    }
                })
                .setNegativeButton("Cancel", (dialog, which) -> {
                    hideKeyboard(passwordInput);
                    dialog.dismiss();
                })
                .setOnDismissListener(dialog -> hideKeyboard(passwordInput))
                .show();

        showKeyboard(passwordInput);
    }

    // Displays error message dialog
    private void showErrorMessage(String message) {
        new AlertDialog.Builder(this)
                .setTitle("Error")
                .setMessage(message)
                .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                .show();
    }

    // Hides the keyboard
    private void hideKeyboard(EditText editText) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
        }
    }

    // Shows the keyboard
    private void showKeyboard(EditText editText) {
        editText.requestFocus();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);
        }
    }
}
