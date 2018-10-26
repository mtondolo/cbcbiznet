package com.android.example.comesanews.sync;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.NonNull;

import com.android.example.comesanews.data.NewsContract;

public class NewsSyncUtils {

     private static boolean sInitialized;

     // Creates periodic sync tasks and checks to see if an immediate sync is required.
      synchronized public static void initialize(@NonNull final Context context) {

         // Only perform initialization once per app lifetime.
          if (sInitialized) return;

          // If the method body is executed, set sInitialized to true
          sInitialized = true;

          // We need to check to see if our ContentProvider has data to display in our news list.
        new AsyncTask<Void, Void, Void>() {
            @Override
            public Void doInBackground( Void... voids ) {

                // URI for every row of  data in our news table.
                 Uri newsQueryUri = NewsContract.LatestNewsEntry.CONTENT_URI;

                // We need to PROJECT more columns to determine what news details need to be displayed.
                String[] projectionColumns = {NewsContract.LatestNewsEntry._ID};

                // Here, we perform the query to check to see if we have any news data */
               Cursor cursor = context.getContentResolver().query(
                        newsQueryUri,
                        projectionColumns,
                        null,
                        null,
                        null);

                // If the Cursor was null, we need to sync immediately to be able to display data to the user.
                if (null == cursor || cursor.getCount() == 0) {
                    startImmediateSync(context);
                }

                /* Make sure we close the Cursor to avoid memory leaks! */
                cursor.close();
                return null;
            }
        }.execute();
      }

    public static void startImmediateSync(@NonNull final Context context) {
        Intent intentToSyncImmediately = new Intent(context, NewsSyncIntentService.class);
        context.startService(intentToSyncImmediately);
    }
}
