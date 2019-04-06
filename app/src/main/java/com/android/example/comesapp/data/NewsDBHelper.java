package com.android.example.comesapp.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.android.example.comesapp.data.NewsContract.NewsEntry;


public class NewsDBHelper extends SQLiteOpenHelper {

    //This is the name of our database.
    public static final String DATABASE_NAME = "news.db";

    /*
     * If we change the database schema, we must increment the database version or the onUpgrade
     * method will not be called.
     */
    private static final int DATABASE_VERSION = 11;

    public NewsDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Called when the database is created for the first time.
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        final String SQL_CREATE_NEWS_TABLE =
                "CREATE TABLE " + NewsContract.NewsEntry.TABLE_NAME + " (" +
                        NewsEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        NewsEntry.COLUMN_DATE + " INTEGER NOT NULL, " +
                        NewsEntry.COLUMN_HEADLINE + " TEXT NOT NULL, " +
                        NewsEntry.COLUMN_STORY + " TEXT NOT NULL, " +
                        NewsEntry.COLUMN_STORY_URL + " TEXT NOT NULL, " +
                        NewsEntry.COLUMN_IMAGE_URL + " TEXT NOT NULL, " +

                        // To ensure this table can only contain one headline per row.
                        " UNIQUE (" + NewsEntry.COLUMN_DATE + ") ON CONFLICT REPLACE);";

        sqLiteDatabase.execSQL(SQL_CREATE_NEWS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + NewsEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
