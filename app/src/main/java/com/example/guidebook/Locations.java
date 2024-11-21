package com.example.guidebook;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class Locations extends AppCompatActivity {
    Button btAdd;

    ListView lvLocations;
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
            arrBoulder[0] = new Boulder("Timna", "Park Timna", "4.6/5", "yes", convertResourceToByteArray(R.drawable.empty_camera));
            arrBoulder[1] = new Boulder("Cabara cliff", "Zichron Yaakov", "3.4/5", "yes", convertResourceToByteArray(R.drawable.ilcclogo1));
            arrBoulder[2] = new Boulder("Beit Arie", "Beit Arie", "4/5", "yes", convertResourceToByteArray(R.drawable.ilcclogo1));
            arrBoulder[3] = new Boulder("Zanuah cliff", "Zanuah river", "3.7/5", "yes", convertResourceToByteArray(R.drawable.ilcclogo1));
            arrBoulder[4] = new Boulder("Beit Oren", "Oren river", "4.6/5", "yes", convertResourceToByteArray(R.drawable.ilcclogo1));
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
        String name, address, rating, isActive;
        byte[] imageBytes; // For storing image as byte array
        db = dbHelper.getReadableDatabase();
        ArrayList<Boulder> list = new ArrayList<>();
        ArrayList<byte[]> imageList = new ArrayList<>();
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
                    isActive = cursor.getString(indexIsActive);
                    // Retrieve image as byte array
                    imageBytes = cursor.getBlob(indexImage);

                    //puts the data in a new Boulder object
                    Boulder record = new Boulder(name, address, rating, isActive, imageBytes);
                    //adds the new object to the list
                    list.add(record);
                    imageList.add(record.getImageBytes());
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

        btAdd = findViewById(R.id.btAdd);
        lvLocations = findViewById(R.id.lvLocations);
        insertTable();
        final ArrayList<Boulder> boulderList = getAllRecords();

        //takes all of the values from the boulderList and put them in a list as strings
        ArrayList<String> BoulderList = new ArrayList<>();
        for (Boulder item : boulderList){
            if (item.getIsActive().equals("yes")){
                BoulderList.add(item.getName() +":   "+ item.getAddress() +"\uD83D\uDCCD   "+ item.getRating()+"⭐");
            }
        }

        //puts the strings in the list view
        ArrayAdapter<String> bList = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, BoulderList);
        lvLocations.setAdapter(bList);

        btAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //opens the AddLocation page
                Intent intent = new Intent(Locations.this, AddLocation.class);
                startActivity(intent);
            }
        });

        lvLocations.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(Locations.this, Boulder_Page.class);
                intent.putExtra("boulderObject", boulderList.get(position));
                startActivity(intent);
            }
        });
    }
}