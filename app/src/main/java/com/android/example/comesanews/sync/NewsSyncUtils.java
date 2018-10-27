package com.android.example.comesanews.sync;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;

import com.android.example.comesanews.data.NewsContract;
import com.firebase.jobdispatcher.Constraint;
import com.firebase.jobdispatcher.Driver;
import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Job;
import com.firebase.jobdispatcher.Lifetime;
import com.firebase.jobdispatcher.Trigger;

import java.util.concurrent.TimeUnit;

public class NewsSyncUtils {

    /*// Interval at which to sync with the news.
    private static final int SYNC_INTERVAL_HOURS = 3;
    private static final int SYNC_INTERVAL_SECONDS = (int) TimeUnit.HOURS.toSeconds(SYNC_INTERVAL_HOURS);
    private static final int SYNC_FLEXTIME_SECONDS = SYNC_INTERVAL_SECONDS / 3;*/

    // Interval at which to sync with the news for test purposes only
    private static final int REMINDER_INTERVAL_MINUTES = 1;
    private static final int REMINDER_INTERVAL_SECONDS = (int) (TimeUnit.MINUTES.toSeconds(REMINDER_INTERVAL_MINUTES));
    private static final int SYNC_FLEXTIME_SECONDS = REMINDER_INTERVAL_SECONDS;

    private static boolean sInitialized;

    // Sync tag to identify our sync job
    private static final String NEWS_SYNC_TAG = "news-sync";

    // Schedules a repeating sync of COMESA's news data using FirebaseJobDispatcher.
    static void scheduleFirebaseJobDispatcherSync(@NonNull final Context context) {

        Driver driver = new GooglePlayDriver(context);
        FirebaseJobDispatcher dispatcher = new FirebaseJobDispatcher(driver);

        Job syncNewsJob = dispatcher.newJobBuilder()
                .setService(NewsFirebaseJobService.class)
                .setTag(NEWS_SYNC_TAG)
                .setConstraints(Constraint.ON_ANY_NETWORK)
                .setLifetime(Lifetime.FOREVER)
                .setRecurring(true)
                .setTrigger(Trigger.executionWindow(
                        REMINDER_INTERVAL_SECONDS,
                        REMINDER_INTERVAL_SECONDS + SYNC_FLEXTIME_SECONDS))
                .setReplaceCurrent(true)
                .build();

        // Schedule the Job with the dispatcher
        dispatcher.schedule(syncNewsJob);
    }

    // Creates periodic sync tasks and checks to see if an immediate sync is required.
    synchronized public static void initialize(@NonNull final Context context) {

        // Only perform initialization once per app lifetime.
        if (sInitialized) return;

        // If the method body is executed, set sInitialized to true
        sInitialized = true;

        // This method call triggers News to create its task to synchronize COMESA data periodically.
        scheduleFirebaseJobDispatcherSync(context);

        Thread checkForEmpty = new Thread(new Runnable() {
            @Override
            public void run() {

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

                /* Close the Cursor to avoid memory leaks! */
                cursor.close();
            }
        });

        // Finally, once the thread is prepared, fire it off to perform our checks.
        checkForEmpty.start();
    }

    public static void startImmediateSync(@NonNull final Context context) {
        Intent intentToSyncImmediately = new Intent(context, NewsSyncIntentService.class);
        context.startService(intentToSyncImmediately);
    }
}
