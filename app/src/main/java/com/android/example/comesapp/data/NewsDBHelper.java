package com.android.example.comesapp.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class NewsDBHelper extends SQLiteOpenHelper {

    //This is the name of our database.
    public static final String DATABASE_NAME = "news.db";

    /*
     * If we change the database schema, we must increment the database version or the onUpgrade
     * method will not be called.
     */
    private static final int DATABASE_VERSION = 4;

    public NewsDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Called when the database is created for the first time.
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        final String SQL_CREATE_NEWS_TABLE =
                "CREATE TABLE " + NewsContract.NewsEntry.TABLE_NAME + " (" +
                        NewsContract.NewsEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        NewsContract.NewsEntry.COLUMN_HEADLINE + " TEXT NOT NULL, " +
                        NewsContract.NewsEntry.COLUMN_STORY + " TEXT NOT NULL, " +
                        NewsContract.NewsEntry.COLUMN_STORY_URL + " TEXT NOT NULL, " +
                        NewsContract.NewsEntry.COLUMN_CREATED_AT + " INTEGER NOT NULL, " +
                        NewsContract.NewsEntry.COLUMN_IMAGE_URL + " TEXT NOT NULL, " +

                        // To ensure this table can only contain one headline per row.
                        " UNIQUE (" + NewsContract.NewsEntry.COLUMN_HEADLINE + ") ON CONFLICT REPLACE);";

        sqLiteDatabase.execSQL(SQL_CREATE_NEWS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + NewsContract.NewsEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
