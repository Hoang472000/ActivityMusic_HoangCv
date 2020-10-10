package com.out.activitymusic.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class FavoriteSongsHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "MusicDB";
    private static final int DATABASE_VERSION = 1;

    FavoriteSongsHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        MusicDatabase.onCreate(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        MusicDatabase.onUpgrade(db, oldVersion, newVersion);
    }
}