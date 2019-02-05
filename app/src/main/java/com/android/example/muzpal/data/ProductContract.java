package com.android.example.muzpal.data;

import android.net.Uri;
import android.provider.BaseColumns;

public class ProductContract {

    /*
     * The "Content authority" is a name for the entire content provider, similar to the
     * relationship between a domain name and its website.
     */
    public static final String CONTENT_AUTHORITY = "com.android.example.muzpal";

    /*
     * Use CONTENT_AUTHORITY to create the base of all URI's which apps will use to contact
     * the content provider.
     */
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    /*
     * Possible paths that can be appended to BASE_CONTENT_URI to form valid URI's that Product
     * can handle. For instance.
     */
    public static final String PATH_PRODUCT = "product";

    /* Inner class that defines the table contents of the product table */
    public static final class ProductEntry implements BaseColumns {

        /* The base CONTENT_URI used to query the table from the content provider */
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_PRODUCT)
                .build();

        /* Used internally as the name of our product table. */
        public static final String TABLE_NAME = "product";

        // company, description and image are stored as string representing a product
        public static final String COLUMN_COMPANY = "company";
        public static final String COLUMN_DESCRIPTION = "description";
        public static final String COLUMN_IMAGE = "image";

    }
}
