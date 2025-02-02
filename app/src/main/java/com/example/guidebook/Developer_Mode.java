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
import android.widget.Toast;

import java.util.ArrayList;

public class Developer_Mode extends AppCompatActivity {
    ImageButton ibBack;
    Dev_Adapter adapter;
    RecyclerView recyclerView;
    DatabaseHelper dbHelper = new DatabaseHelper(this);
    SQLiteDatabase db;

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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_developer_mode);

        ibBack = findViewById(R.id.imageButtonBack);
        recyclerView = findViewById(R.id.recyclerViewDev);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        //creates a list of all the boulders
        final ArrayList<Boulder> boulderList = getAllRecords();
        //creates a list of all the NOT active boulders
        ArrayList<Boulder> inActiveBoulders = new ArrayList<>();
        for (Boulder item: boulderList){
            if (!item.getIsActive()){
                inActiveBoulders.add(item);
            }
        }
        //sends the data to the recycler view adapter
        adapter = new Dev_Adapter(inActiveBoulders, new Dev_Adapter.OnItemClickListener() {
            @Override
            public void onItemClick(Boulder boulder, int position) {
                adapter.notifyDataSetChanged();
            }
        });
        recyclerView.setAdapter(adapter);
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