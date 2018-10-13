package com.android.example.comesanews.data;

import android.net.Uri;
import android.provider.BaseColumns;

public class NewsContract {

    /*
     * The "Content authority" is a name for the entire content provider, similar to the
     * relationship between a domain name and its website.
     */
    public static final String CONTENT_AUTHORITY = "com.android.example.comesanews";

    /*
     * Use CONTENT_AUTHORITY to create the base of all URI's which apps will use to contact
     * the content provider.
     */
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    /*
     * Possible paths that can be appended to BASE_CONTENT_URI to form valid URI's that Latest News
     * can handle. For instance.
     */
    public static final String PATH_NEWS = "news";

    /* Inner class that defines the table contents of the latest news table */
    public static final class LatestNewsEntry implements BaseColumns {

        /* The base CONTENT_URI used to query the table from the content provider */
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_NEWS)
                .build();

        /* Used internally as the name of our latest news table. */
        public static final String TABLE_NAME = "latest_news";

        /* title, date, author, imageUrl and webUrl are
        stored as string representing latest news head line */
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_DATE = "date";
        public static final String COLUMN_IMAGE = "imageUrl";
        public static final String COLUMN_WEB = "webUrl";

    }
}
