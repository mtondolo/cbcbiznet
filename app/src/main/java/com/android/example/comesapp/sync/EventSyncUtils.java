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

public class EventSyncUtils {

    // Interval at which to sync with the events. This is just for test purposes.
    private static final int REMINDER_INTERVAL_MINUTES = 1;
    private static final int REMINDER_INTERVAL_SECONDS = (int) (TimeUnit.MINUTES.toSeconds(REMINDER_INTERVAL_MINUTES));
    private static final int SYNC_FLEXTIME_SECONDS = REMINDER_INTERVAL_SECONDS;

    private static boolean sInitialized;

    //  Sync tag to identify our sync job
    private static final String EVENT_SYNC_TAG = "event-sync";

    // Schedules a repeating sync of events data using FirebaseJobDispatcher.
    static void scheduleFirebaseJobDispatcherSync(@NonNull final Context context) {

        Driver driver = new GooglePlayDriver(context);
        FirebaseJobDispatcher dispatcher = new FirebaseJobDispatcher(driver);

        // Create the Job to periodically sync Events
        Job syncEventJob = dispatcher.newJobBuilder()
                .setService(NewsFirebaseJobService.class)
                .setTag(EVENT_SYNC_TAG)
                .setConstraints(Constraint.ON_ANY_NETWORK)
                .setLifetime(Lifetime.FOREVER)
                .setRecurring(true)
                .setTrigger(Trigger.executionWindow(
                        REMINDER_INTERVAL_SECONDS,
                        REMINDER_INTERVAL_SECONDS + SYNC_FLEXTIME_SECONDS))
                .setReplaceCurrent(true)
                .build();

        // Schedule the Job with the dispatcher
        dispatcher.schedule(syncEventJob);
    }

    // Creates periodic sync tasks and checks to see if an immediate sync is required.
    synchronized public static void initialize(@NonNull final Context context) {

        // Only perform initialization once per app lifetime.
        if (sInitialized) return;

        // If the method body is executed, set sInitialized to true
        sInitialized = true;

        // This method call triggers Event to create its task to synchronize events data periodically.
        scheduleFirebaseJobDispatcherSync(context);

        // We need to check to see if our ContentProvider has data to display in our events list.
        Thread checkForEmpty = new Thread(new Runnable() {
            @Override
            public void run() {
                Uri eventQueryUri = NewsContract.NewsEntry.EVENT_URI;
                String[] projectionColumns = {NewsContract.NewsEntry._ID};
                Cursor cursor = context.getContentResolver().query(
                        eventQueryUri,
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
        Intent intentToSyncImmediately = new Intent(context, EventSyncIntentService.class);
        context.startService(intentToSyncImmediately);
    }
}
