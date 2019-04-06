package com.android.example.comesapp.sync;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;

import com.android.example.comesapp.data.NewsContract;
import com.firebase.jobdispatcher.Constraint;
import com.firebase.jobdispatcher.Driver;
import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Job;
import com.firebase.jobdispatcher.Lifetime;
import com.firebase.jobdispatcher.Trigger;

import java.util.concurrent.TimeUnit;

public class NewsSyncUtils {

    // Interval at which to sync with the news.
    private static final int SYNC_INTERVAL_HOURS = 3;
    private static final int SYNC_INTERVAL_SECONDS = (int) (TimeUnit.HOURS.toSeconds(SYNC_INTERVAL_HOURS));
    private static final int SYNC_FLEXTIME_SECONDS = SYNC_INTERVAL_SECONDS / 3;

    private static boolean sInitialized;

    //  Sync tag to identify our sync job
    private static final String NEWS_SYNC_TAG = "news-sync";

    // Schedules a repeating sync of news data using FirebaseJobDispatcher.
    static void scheduleFirebaseJobDispatcherSync(@NonNull final Context context) {

        Driver driver = new GooglePlayDriver(context);
        FirebaseJobDispatcher dispatcher = new FirebaseJobDispatcher(driver);

        // Create the Job to periodically sync News.
        Job syncNewsJob = dispatcher.newJobBuilder()

                // The Service that will be used to sync news data.
                .setService(NewsFirebaseJobService.class)

                // Set the UNIQUE tag used to identify this Job
                .setTag(NEWS_SYNC_TAG)

                // Network constraints on which this Job should run.
                // We choose to run on any network.
                .setConstraints(Constraint.ON_ANY_NETWORK)

                // setLifetime sets how long this job should persist.
                // The options are to keep the Job "forever" or
                // to have it die the next time the device boots up.
                .setLifetime(Lifetime.FOREVER)

                //  We want news data to stay up to date, so we tell this Job to recur.
                .setRecurring(true)

                // We want the news data to be synced every 3 to 4 hours.
                .setTrigger(Trigger.executionWindow(
                        SYNC_INTERVAL_SECONDS,
                        SYNC_INTERVAL_SECONDS + SYNC_FLEXTIME_SECONDS))

                // If a Job with the tag with provided already exists,
                // this new job will replace the old one.
                .setReplaceCurrent(true)

                // Once the Job is ready, call the builder's build method to return the Job.
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

        // This method call triggers News to create its task to synchronize news data periodically.
        scheduleFirebaseJobDispatcherSync(context);

        // We need to check to see if our ContentProvider has data to display in our news list.
        Thread checkForEmpty = new Thread(new Runnable() {
            @Override
            public void run() {
                Uri newsQueryUri = NewsContract.NewsEntry.CONTENT_URI;
                String[] projectionColumns = {NewsContract.NewsEntry._ID};
                Cursor cursor = context.getContentResolver().query(
                        newsQueryUri,
                        projectionColumns,
                        null,
                        null,
                        null);

                if (null == cursor || cursor.getCount() == 0) {
                    startImmediateSync(context);
                }

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
