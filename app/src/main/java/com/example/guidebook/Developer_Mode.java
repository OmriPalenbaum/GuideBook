package com.example.guidebook;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;

public class Developer_Mode extends AppCompatActivity {
    ImageButton ibBack;
    TextView tvInActive;
    Dev_Adapter adapter;
    Dev_Adapter adapter2;
    RecyclerView recyclerViewInActive;
    RecyclerView recyclerViewActive;
    DatabaseHelper dbHelper;
    SQLiteDatabase db;

    // Retrieves all boulders from the database into a list
    public ArrayList<Boulder> getAllRecords() {
        String name, address, rating;
        int isActive, isDone;
        byte[] imageBytes;

        db = dbHelper.getReadableDatabase();
        ArrayList<Boulder> list = new ArrayList<>();
        Cursor cursor = db.query(DatabaseHelper.TABLE_LOCATIONS, null, null, null, null, null, null);

        if (cursor != null) {
            while (cursor.moveToNext()) {
                // Get column indexes
                int indexName = cursor.getColumnIndex(DatabaseHelper.COLUMN_NAME);
                int indexAddress = cursor.getColumnIndex(DatabaseHelper.COLUMN_ADDRESS);
                int indexRating = cursor.getColumnIndex(DatabaseHelper.COLUMN_RATING);
                int indexIsActive = cursor.getColumnIndex(DatabaseHelper.COLUMN_IS_ACTIVE);
                int indexIsDone = cursor.getColumnIndex(DatabaseHelper.COLUMN_IS_DONE);
                int indexImage = cursor.getColumnIndex(DatabaseHelper.COLUMN_IMAGE);

                // Only proceed if all required columns exist
                if (indexName != -1 && indexAddress != -1 && indexRating != -1 && indexIsActive != -1 && indexImage != -1) {
                    name = cursor.getString(indexName);
                    address = cursor.getString(indexAddress);
                    rating = cursor.getString(indexRating);
                    isActive = cursor.getInt(indexIsActive);
                    isDone = cursor.getInt(indexIsDone);
                    imageBytes = cursor.getBlob(indexImage);

                    // Create new Boulder object and add to list
                    Boulder record = new Boulder(name, address, rating, isActive, isDone, imageBytes);
                    list.add(record);
                }
            }
            cursor.close();
        }

        db.close();
        return list;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_developer_mode);

        dbHelper = new DatabaseHelper(this);

        // Initialize views
        ibBack = findViewById(R.id.imageButton1);
        tvInActive = findViewById(R.id.textView);
        recyclerViewInActive = findViewById(R.id.recyclerViewInActive);
        recyclerViewInActive.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewActive = findViewById(R.id.recyclerViewActive);
        recyclerViewActive.setLayoutManager(new LinearLayoutManager(this));

        // Load data into both RecyclerViews
        loadRecyclerViews();

        // Set click listener for the back button
        ibBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate back to Locations activity
                Intent intent = new Intent(Developer_Mode.this, Locations.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh RecyclerViews when returning to this activity
        loadRecyclerViews();
    }

    private void loadRecyclerViews() {
        // Get all boulders from the database
        final ArrayList<Boulder> boulderList = getAllRecords();

        // Filter inactive boulders
        ArrayList<Boulder> inActiveBoulders = new ArrayList<>();
        for (Boulder item : boulderList) {
            if (!item.getIsActive()) {
                inActiveBoulders.add(item);
            }
        }

        // Show or hide the "Inactive" title based on data
        if (inActiveBoulders.isEmpty()) {
            tvInActive.setVisibility(View.GONE);
        } else {
            tvInActive.setVisibility(View.VISIBLE);
        }

        // Filter active boulders
        ArrayList<Boulder> activeBoulders = new ArrayList<>();
        for (Boulder item : boulderList) {
            if (item.getIsActive()) {
                activeBoulders.add(item);
            }
        }

        // Set adapters for each RecyclerView
        adapter = new Dev_Adapter(inActiveBoulders);
        recyclerViewInActive.setAdapter(adapter);

        adapter2 = new Dev_Adapter(activeBoulders);
        recyclerViewActive.setAdapter(adapter2);
    }
}
