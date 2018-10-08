package com.android.example.comesanews.data;

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
    private static final int DATABASE_VERSION = 1;

    public NewsDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Called when the database is created for the first time.
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        final String SQL_CREATE_LATEST_NEWS_TABLE =
                "CREATE TABLE " + NewsContract.LatestNewsEntry.TABLE_NAME + " (" +
                        NewsContract.LatestNewsEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        NewsContract.LatestNewsEntry.COLUMN_TITLE + " TEXT, " +
                        NewsContract.LatestNewsEntry.COLUMN_DATE + " TEXT, " +
                        NewsContract.LatestNewsEntry.COLUMN_AUTHOR + " TEXT" + ");";
        sqLiteDatabase.execSQL(SQL_CREATE_LATEST_NEWS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + NewsContract.LatestNewsEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
