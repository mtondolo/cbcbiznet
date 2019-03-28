package com.android.example.comesapp.utils;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;

import com.android.example.comesapp.DetailNewsActivity;
import com.android.example.comesapp.R;
import com.android.example.comesapp.data.NewsContract;


import static android.content.Context.NOTIFICATION_SERVICE;

public class NotificationUtils {

    // The columns of data that we are interested in displaying within our notification.
    public static final String[] NEWS_NOTIFICATION_PROJECTION = {
            NewsContract.NewsEntry.COLUMN_HEADLINE,
    };

    // Store value indices in the Strings array to quickly access the data from our query.
    public static final int INDEX_HEADLINE = 0;

    // This notification ID can be used to access our notification after we've displayed it.
    private static final int NOTIFICATION_ID = 0;

    private static final String PRIMARY_CHANNEL_ID = "primary_notification_channel";

    private static NotificationManager mNotifyManager;

    // Constructs and displays a notification for the latest news.
    public static void notifyUserOfLatestNews(Context context) {

        // Build the URI for the our news
        Uri latestNewsUri = NewsContract.NewsEntry.CONTENT_URI;
        Cursor latestNewsCursor = context.getContentResolver().query(
                latestNewsUri,

                //This used to limit the columns returned in our cursor.
                NEWS_NOTIFICATION_PROJECTION,

                null,
                null,
                null);

        // If our cursor is not empty, we want to show the notification.
        if (latestNewsCursor.moveToFirst()) {

            // News headline as returned by API, used as notification content text.
            String headline = latestNewsCursor.getString(INDEX_HEADLINE);

            // Create a notification manager object.
            mNotifyManager = (NotificationManager)
                    context.getSystemService(NOTIFICATION_SERVICE);

            // Notification channels are only available in OREO and higher.
            if (android.os.Build.VERSION.SDK_INT >=
                    android.os.Build.VERSION_CODES.O) {

                // Create a NotificationChannel
                NotificationChannel notificationChannel = new NotificationChannel(PRIMARY_CHANNEL_ID,
                        context.getString(R.string.app_name), NotificationManager
                        .IMPORTANCE_HIGH);
                notificationChannel.setDescription(headline);
                mNotifyManager.createNotificationChannel(notificationChannel);
            }

            // Build the notification with all of the parameters.
            NotificationCompat.Builder notifyBuilder = new NotificationCompat.Builder(context, PRIMARY_CHANNEL_ID)
                    .setContentTitle(context.getString(R.string.app_name))
                    .setContentText(headline)
                    .setSmallIcon(R.drawable.ic_android)

                    // Notify user with default sound
                    .setDefaults(Notification.DEFAULT_SOUND)
                    .setPriority(NotificationCompat.PRIORITY_HIGH)

                    // Removes the notification when the user taps it
                    .setAutoCancel(true);

            // Open COMESApp DetailNewsActivity to display the newly updated news.
            Intent detailIntentForLatestNews = new Intent(context, DetailNewsActivity.class);
            detailIntentForLatestNews.setData(latestNewsUri);

            TaskStackBuilder taskStackBuilder = TaskStackBuilder.create(context);
            taskStackBuilder.addNextIntentWithParentStack(detailIntentForLatestNews);
            PendingIntent resultPendingIntent = taskStackBuilder
                    .getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

            notifyBuilder.setContentIntent(resultPendingIntent);

            // NotificationId is a unique int for each notification that you must define
            mNotifyManager.notify(NOTIFICATION_ID, notifyBuilder.build());
        }

        // Close the cursor to avoid wasting resources.
        latestNewsCursor.close();
    }
}






