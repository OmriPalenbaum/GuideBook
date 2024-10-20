package com.example.guidebook;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class DBHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "BoulderingGuide.db";
    public static final String TABLE_NAME = "table";
    public static final String NAME = "name";
    public static final String ADDRESS = "address";
    public static final String RATING = "rating";
    public static final String IS_ACTIVE = "is_active";

    public String buildTable(){
        String st = "CREATE TABLE IF NOT EXISTS" + TABLE_NAME;
        st+= "("+ NAME+ "TEXT, ";
        st+= ADDRESS+ "TEXT, ";
        st+= RATING+ "TEXT, ";
        st+= IS_ACTIVE+ "TEXT )";
        return st;
    }

    public DBHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, 1);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(buildTable());
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }


}