package com.android.example.comesapp.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;

// This class serves as the ContentProvider for all of news's data.
public class NewsProvider extends ContentProvider {

    // These constant will be used to match URIs with the data they are looking for.
    public static final int CODE_NEWS = 100;
    public static final int CODE_NEWS_WITH_DATE = 101;

    // The URI Matcher used by this content provider.
    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private NewsDBHelper mOpenHelper;

    // Creates the UriMatcher that will match each URI to the CODE_NEWS constant defined above.
    public static UriMatcher buildUriMatcher() {

        // All paths added to the UriMatcher have a corresponding code to return when a match is found.
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = NewsContract.CONTENT_AUTHORITY;

        // This URI is content://com.android.example.comesapp/news
        matcher.addURI(authority, NewsContract.PATH_NEWS, CODE_NEWS);

        // This URI would look something like content://com.android.example.comesapp/news/1472214172
        matcher.addURI(authority, NewsContract.PATH_NEWS + "/#", CODE_NEWS_WITH_DATE);

        return matcher;
    }

    //In onCreate, we initialize our content provider on startup.
    @Override
    public boolean onCreate() {
        mOpenHelper = new NewsDBHelper(getContext());
        return true;
    }

    // Handles requests to insert a set of new rows.
    @Override
    public int bulkInsert(@NonNull Uri uri, @NonNull ContentValues[] values) {

        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();

        switch (sUriMatcher.match(uri)) {

            case CODE_NEWS:
                db.beginTransaction();
                int rowsInserted = 0;
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(NewsContract.NewsEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            rowsInserted++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }

                if (rowsInserted > 0) {
                    getContext().getContentResolver().notifyChange(uri, null);
                }

                // Return the number of rows inserted from our implementation of bulkInsert
                return rowsInserted;

            // If the URI does match match CODE_NEWS, return the super implementation of bulkInsert
            default:
                return super.bulkInsert(uri, values);
        }
    }

    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {

        Cursor cursor;

        // Given a URI, will determine what kind of request is being made and query the database accordingly.
        switch (sUriMatcher.match(uri)) {

            // We want to return a cursor that contains one row of weather data for a particular date.
            case CODE_NEWS_WITH_DATE: {

                String dateString = uri.getLastPathSegment();

                String[] selectionArguments = new String[]{dateString};

                cursor = mOpenHelper.getReadableDatabase().query(
                        /* Table we are going to query */
                        NewsContract.NewsEntry.TABLE_NAME,

                        //A projection designates the columns we want returned in our Cursor.
                        projection,

                        // The URI that matches CODE_NEWS_WITH_DATE contains a date at the end of it.
                        NewsContract.NewsEntry.COLUMN_DATE + " = ? ",
                        selectionArguments,
                        null,
                        null,
                        sortOrder);
                break;
            }

            // we want to return a cursor that contains every row of news data in our news table
            case CODE_NEWS: {
                cursor = mOpenHelper.getReadableDatabase().query(
                        NewsContract.NewsEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            }

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    // Deletes data at a given URI with optional arguments for more fine tuned deletions.
    @Override
    public int delete(Uri uri, String selection,
                      String[] selectionArgs) {
        int numRowsDeleted;
        if (null == selection) selection = "1";
        switch (sUriMatcher.match(uri)) {
            case CODE_NEWS:
                numRowsDeleted = mOpenHelper.getWritableDatabase().delete(
                        NewsContract.NewsEntry.TABLE_NAME,
                        selection,
                        selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if (numRowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return numRowsDeleted;
    }

    // We aren't going to do anything with this method. However, we are required to override it as WeatherProvider extends ContentProvider.
    @Override
    public String getType(Uri uri) {
        throw new RuntimeException("We are not implementing getType.");
    }

    // We aren't going to do anything with this method. However, we are required to override it as NewsProvider extends ContentProvider.
    @Override
    public Uri insert(Uri uri, ContentValues values) {

        return null;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        throw new RuntimeException("We will implement the update method!");
    }
}
