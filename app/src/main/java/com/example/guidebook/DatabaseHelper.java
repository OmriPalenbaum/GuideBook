package com.example.guidebook;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import androidx.annotation.Nullable;

public class DatabaseHelper extends SQLiteOpenHelper {
    // Database Name
    private static final String DATABASE_NAME = "boulderingGuide.db";
    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Table Names
    public static final String TABLE_LOCATIONS = "Locations";

    // Column Names for Users Table
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_ADDRESS = "address";
    public static final String COLUMN_RATING = "rating";
    public static final String COLUMN_IS_ACTIVE = "is_active";
    public static final String COLUMN_IS_DONE = "is_done";
    public static final String COLUMN_IMAGE = "image";

    // Create Table SQL Statement
    private static final String CREATE_TABLE_LOCATIONS = "CREATE TABLE " + TABLE_LOCATIONS + "(" +
            COLUMN_NAME + " TEXT, " +
            COLUMN_ADDRESS + " TEXT, " +
            COLUMN_RATING + " TEXT, " +
            COLUMN_IS_ACTIVE + " INTEGER, " +
            COLUMN_IS_DONE + " INTEGER, " +
            COLUMN_IMAGE + " BLOB" +
            ")";

    public DatabaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Creating the tables
        db.execSQL(CREATE_TABLE_LOCATIONS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Dropping older table if exists and creating a new one
        if (oldVersion != newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_LOCATIONS);
            onCreate(db);
        }
    }

    // Method to update the 'is_active' to true(1) based on the Boulder name
    public void updateBoulderStatus1(String name) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_IS_ACTIVE, 1);
        db.update(TABLE_LOCATIONS, cv, "name = ?", new String[]{name});
        db.close();
    }

    // Method to update the 'is_active' to false(0) based on the Boulder name
    public void updateBoulderStatus0(String name) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_IS_ACTIVE, 0);
        db.update(TABLE_LOCATIONS, cv, "name = ?", new String[]{name});
        db.close();
    }

    // Method to update the 'is_done' based on the Boulder name
    public void updateIsDone(String name, boolean isDone) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_IS_DONE, isDone ? 1 : 0);
        db.update(TABLE_LOCATIONS, cv, COLUMN_NAME + " = ?", new String[]{name});
        db.close();
    }

    // Method to update the name of a boulder (need to use old name as identifier)
    public void updateBoulderName(String oldName, String newName) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_NAME, newName);
        db.update(TABLE_LOCATIONS, cv, COLUMN_NAME + " = ?", new String[]{oldName});
        db.close();
    }

    // Method to update the address of a boulder
    public void updateBoulderAddress(String name, String newAddress) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_ADDRESS, newAddress);
        db.update(TABLE_LOCATIONS, cv, COLUMN_NAME + " = ?", new String[]{name});
        db.close();
    }

    // Method to update the rating of a boulder
    public void updateBoulderRating(String name, String newRating) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_RATING, newRating);
        db.update(TABLE_LOCATIONS, cv, COLUMN_NAME + " = ?", new String[]{name});
        db.close();
    }
}