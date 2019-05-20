package com.android.example.comesapp.sync;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.NonNull;

import com.android.example.comesapp.data.NewsContract;

public class EventSyncUtils {

    private static boolean sInitialized;

    // Creates periodic sync tasks and checks to see if an immediate sync is required.
    synchronized public static void initialize(@NonNull final Context context) {

        // Only perform initialization once per app lifetime.
        if (sInitialized) return;

        // If the method body is executed, set sInitialized to true
        sInitialized = true;

        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... voids) {

                // URI for every row of events data in our policy table
                Uri eventsQueryUri = NewsContract.NewsEntry.EVENT_URI;

                // Since this query is going to be used only as a check to see if we have any
                // data (rather than to display data), we just need to PROJECT the ID of each row.
                String[] projectionColumns = {NewsContract.NewsEntry._ID};

                // Here, we perform the query to check to see if we have any events data
                Cursor cursor = context.getContentResolver().query(
                        eventsQueryUri,
                        projectionColumns,
                        null,
                        null,
                        null);

                // If the Cursor was null OR if it was empty, we need to sync immediately to
                // be able to display data to the user.
                if (null == cursor || cursor.getCount() == 0) {
                    startImmediateSync(context);
                }

                // Close the Cursor to avoid memory leaks!
                cursor.close();
                return null;
            }
        }.execute();

    }

    public static void startImmediateSync(@NonNull final Context context) {
        Intent intentToSyncImmediately = new Intent(context, EventSyncIntentService.class);
        context.startService(intentToSyncImmediately);
    }
}
