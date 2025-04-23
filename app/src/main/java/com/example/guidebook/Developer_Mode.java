package com.example.guidebook;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class Developer_Mode extends AppCompatActivity {
    ImageButton ibBack;
    TextView tvInActive;
    Dev_Adapter adapter;
    Dev_Adapter adapter2;
    RecyclerView recyclerViewInActive;
    RecyclerView recyclerViewActive;
    DatabaseHelper dbHelper = new DatabaseHelper(this);
    SQLiteDatabase db;

    //method to retrieve all data from the database
    public ArrayList<Boulder> getAllRecords() {
        String name, address, rating;
        int isActive, isDone;
        byte[] imageBytes; // For storing image as byte array
        db = dbHelper.getReadableDatabase();
        ArrayList<Boulder> list = new ArrayList<>();
        Cursor cursor = db.query(DatabaseHelper.TABLE_LOCATIONS, null, null, null, null, null, null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                //gets the index for each column
                int indexName = cursor.getColumnIndex(DatabaseHelper.COLUMN_NAME);
                int indexAddress = cursor.getColumnIndex(DatabaseHelper.COLUMN_ADDRESS);
                int indexRating = cursor.getColumnIndex(DatabaseHelper.COLUMN_RATING);
                int indexIsActive = cursor.getColumnIndex(DatabaseHelper.COLUMN_IS_ACTIVE);
                int indexIsDone = cursor.getColumnIndex(DatabaseHelper.COLUMN_IS_DONE);
                int indexImage = cursor.getColumnIndex(DatabaseHelper.COLUMN_IMAGE); // Now a TEXT column
                //checks if all the indexes are valid
                if (indexName != -1 && indexAddress != -1 && indexRating != -1 && indexIsActive != -1 && indexImage != -1) {
                    name = cursor.getString(indexName);
                    address = cursor.getString(indexAddress);
                    rating = cursor.getString(indexRating);
                    isActive = cursor.getInt(indexIsActive);
                    isDone = cursor.getInt(indexIsDone);
                    // Retrieve image as byte array
                    imageBytes = cursor.getBlob(indexImage);

                    //puts the data in a new Boulder object
                    Boulder record = new Boulder(name, address, rating, isActive, isDone, imageBytes);
                    //adds the new object to the list
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

        ibBack = findViewById(R.id.imageButton1);
        tvInActive = findViewById(R.id.textView);
        recyclerViewInActive = findViewById(R.id.recyclerViewInActive);
        recyclerViewInActive.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewActive = findViewById(R.id.recyclerViewActive);
        recyclerViewActive.setLayoutManager(new LinearLayoutManager(this));

        //creates a list of all the boulders
        final ArrayList<Boulder> boulderList = getAllRecords();
        //creates a list of all the NOT active boulders
        ArrayList<Boulder> inActiveBoulders = new ArrayList<>();
        for (Boulder item: boulderList){
            if (!item.getIsActive()){
                inActiveBoulders.add(item);
            }
        }
        if (inActiveBoulders.isEmpty()){
            tvInActive.setVisibility(View.GONE);
        }
        //creates a list of all the active boulders
        ArrayList<Boulder> activeBoulders = new ArrayList<>();
        for (Boulder item: boulderList){
            if (item.getIsActive()){
                activeBoulders.add(item);
            }
        }
        //sends the data to the recycler view adapter
        adapter = new Dev_Adapter(inActiveBoulders) {
            public void onItemClick(Boulder boulder, int position) {
                adapter.notifyDataSetChanged();
            }
        };
        recyclerViewInActive.setAdapter(adapter);

        //sends the data to the recycler view adapter
        adapter2 = new Dev_Adapter(activeBoulders) {
            public void onItemClick(Boulder boulder, int position) {
                adapter.notifyDataSetChanged();
            }
        };
        recyclerViewActive.setAdapter(adapter2);

        // sets listener for the image button
        ibBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent  = new Intent(Developer_Mode.this, Locations.class);
                startActivity(intent);
            }
        });
    }
}