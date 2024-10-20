package com.example.guidebook;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;

import java.util.ArrayList;
import java.util.jar.Attributes;

public class MainActivity extends AppCompatActivity {
    Button btLocations;
    Button btTips;
    DBHelper dbHelper = new DBHelper(this);
    SQLiteDatabase db;
    ContentValues cv = new ContentValues();
    public void insertTable(){
        Boulder[] arrBoulder = new Boulder[5];
        arrBoulder[0] = new Boulder("Timna", "Park Timna", "4.6/5", "yes");
        arrBoulder[1] = new Boulder("Cabara cliff", "Zichron Yaakov", "3.4/5", "yes");
        arrBoulder[2] = new Boulder("Beit Arie", "Beit Arie", "4/5", "yes");
        arrBoulder[3] = new Boulder("Zanuah cliff", "Zanuah river", "3.7/5", "yes");
        arrBoulder[4] = new Boulder("Beit Oren", "Oren river", "4.6/5", "yes");
        db = dbHelper.getWritableDatabase();
        for (int i=0; i<arrBoulder.length; i++){
            cv.put(DBHelper.TABLE_NAME, arrBoulder[i].getName());
            cv.put(DBHelper.TABLE_NAME, arrBoulder[i].getAddress());
            cv.put(DBHelper.TABLE_NAME, arrBoulder[i].getRating());
            cv.put(DBHelper.TABLE_NAME, arrBoulder[i].getIsActive());
            db.insert(DBHelper.TABLE_NAME,null,cv);
        }
        db.close();
    }
    public ArrayList<Boulder> getAllRecords(){
        int index;
        String name, address, rating, isActive;
        db = dbHelper.getReadableDatabase();
        ArrayList<Boulder> list = new ArrayList<>();
        Cursor cursor = db.query(DBHelper.TABLE_NAME,null,null,null,null,null,null);
        while (cursor.moveToNext()){
            index = cursor.getColumnIndex("NAME");
            name = cursor.getString(index);
            index = cursor.getColumnIndex("ADDRESS");
            address = cursor.getString(index);
            index = cursor.getColumnIndex("RATING");
            rating = cursor.getString(index);
            index = cursor.getColumnIndex("IS_ACTIVE");
            isActive = cursor.getString(index);
            Boulder record = new Boulder(name, address, rating, isActive);
            list.add(record);
        }
        db.close();
        return list;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        insertTable();
        btLocations = findViewById(R.id.btLocations);
        btTips = findViewById(R.id.btTips);

        btLocations.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, Locations.class);
                startActivity(intent);
            }
        });
        btTips.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }
}