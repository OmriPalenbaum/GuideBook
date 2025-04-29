package com.example.guidebook;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import androidx.annotation.Nullable;

// Helper class to manage SQLite database for the guidebook
public class DatabaseHelper extends SQLiteOpenHelper {
    // Database Name and Version
    private static final String DATABASE_NAME = "boulderingGuide.db";
    private static final int DATABASE_VERSION = 1;

    // Table Name
    public static final String TABLE_LOCATIONS = "Locations";

    // Column Names for the Locations table
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_ADDRESS = "address";
    public static final String COLUMN_RATING = "rating";
    public static final String COLUMN_IS_ACTIVE = "is_active";
    public static final String COLUMN_IS_DONE = "is_done";
    public static final String COLUMN_IMAGE = "image";

    // SQL statement to create the Locations table
    private static final String CREATE_TABLE_LOCATIONS = "CREATE TABLE " + TABLE_LOCATIONS + "(" +
            COLUMN_NAME + " TEXT, " +
            COLUMN_ADDRESS + " TEXT, " +
            COLUMN_RATING + " TEXT, " +
            COLUMN_IS_ACTIVE + " INTEGER, " +
            COLUMN_IS_DONE + " INTEGER, " +
            COLUMN_IMAGE + " BLOB" +
            ")";

    // Constructor
    public DatabaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create the Locations table when the database is first created
        db.execSQL(CREATE_TABLE_LOCATIONS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Upgrade the database if version number changes
        if (oldVersion != newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_LOCATIONS); // Drop existing table
            onCreate(db); // Create a fresh table
        }
    }

    //i know these two functions can be 1, but it is simpler this way
    // Update 'is_active' to true (1) for a specific boulder by name
    public void updateBoulderStatus1(String name) {
        SQLiteDatabase db = this.getWritableDatabase(); // Get writable database
        ContentValues cv = new ContentValues(); // Create key-value pairs
        cv.put(COLUMN_IS_ACTIVE, 1);
        db.update(TABLE_LOCATIONS, cv, "name = ?", new String[]{name}); // Update matching row
        db.close(); // Close database connection
    }

    // Update 'is_active' to false (0) for a specific boulder by name
    public void updateBoulderStatus0(String name) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_IS_ACTIVE, 0);
        db.update(TABLE_LOCATIONS, cv, "name = ?", new String[]{name});
        db.close();
    }

    // Update 'is_done' status (true or false) for a specific boulder
    public void updateIsDone(String name, boolean isDone) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_IS_DONE, isDone ? 1 : 0); // 1 if done, 0 if not
        db.update(TABLE_LOCATIONS, cv, COLUMN_NAME + " = ?", new String[]{name});
        db.close();
    }

    // Update the name of a boulder (use old name as identifier)
    public void updateBoulderName(String oldName, String newName) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_NAME, newName);
        db.update(TABLE_LOCATIONS, cv, COLUMN_NAME + " = ?", new String[]{oldName});
        db.close();
    }

    // Update the address of a boulder
    public void updateBoulderAddress(String name, String newAddress) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_ADDRESS, newAddress);
        db.update(TABLE_LOCATIONS, cv, COLUMN_NAME + " = ?", new String[]{name});
        db.close();
    }

    // Update the rating of a boulder
    public void updateBoulderRating(String name, String newRating) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_RATING, newRating);
        db.update(TABLE_LOCATIONS, cv, COLUMN_NAME + " = ?", new String[]{name});
        db.close();
    }
}