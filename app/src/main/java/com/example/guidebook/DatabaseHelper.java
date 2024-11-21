package com.example.guidebook;

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
    public static final String IS_ACTIVE = "is_active";
    public static final String COLUMN_IMAGE = "image";
    // Create Table SQL Statement
    private static final String CREATE_TABLE_LOCATIONS = "CREATE TABLE "
            + TABLE_LOCATIONS + "("
            + COLUMN_NAME + " TEXT, "
            + COLUMN_ADDRESS + " TEXT, "
            + COLUMN_RATING + " TEXT, "
            + IS_ACTIVE + " TEXT, "
            + COLUMN_IMAGE + " BLOB"
            + ")";


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

}
