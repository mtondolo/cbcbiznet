package com.android.example.comesapp.data;

import android.net.Uri;
import android.provider.BaseColumns;

public class NewsContract {

    /*
     * The "Content authority" is a name for the entire content provider, similar to the
     * relationship between a domain name and its website.
     */
    public static final String CONTENT_AUTHORITY = "com.android.example.comesapp";

    /*
     * Use CONTENT_AUTHORITY to create the base of all URI's which apps will use to contact
     * the content provider.
     */
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    /*
     * Possible paths that can be appended to BASE_CONTENT_URI to form valid URI's that item
     * can handle. For instance.
     */
    public static final String PATH_NEWS = "news";

    /* Inner class that defines the table contents of the news table */
    public static final class NewsEntry implements BaseColumns {

        /* The base CONTENT_URI used to query the table from the content provider */
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_NEWS)
                .build();

        /* Used internally as the name of our news table. */
        public static final String TABLE_NAME = "news";

        // headline, storyUrl, date  and imageUrl are stored as string representing news
        public static final String COLUMN_DATE = "date";
        public static final String COLUMN_HEADLINE = "headline";
        public static final String COLUMN_STORY = "story";
        public static final String COLUMN_STORY_URL = "storyUrl";
        public static final String COLUMN_IMAGE_URL = "imageUrl";
        public static final String COLUMN_IMAGE_DESCRIPTION = "image_description";

        public static final String SQL_CREATE_NEWS_TABLE =
                "CREATE TABLE " + TABLE_NAME + " (" +
                        _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        COLUMN_DATE + " INTEGER NOT NULL, " +
                        COLUMN_HEADLINE + " TEXT NOT NULL, " +
                        COLUMN_STORY + " TEXT NOT NULL, " +
                        COLUMN_STORY_URL + " TEXT NOT NULL, " +
                        COLUMN_IMAGE_URL + " TEXT NOT NULL, " +
                        COLUMN_IMAGE_DESCRIPTION + " TEXT, " +

                        // To ensure this table can only contain one date per row.
                        " UNIQUE (" + COLUMN_DATE + ") ON CONFLICT REPLACE);";

        // Builds a URI that adds the news date to the end of the news content URI path.
        public static Uri buildNewsUriWithDate(long date) {
            return CONTENT_URI.buildUpon()
                    .appendPath(Long.toString(date))
                    .build();
        }
    }
}
