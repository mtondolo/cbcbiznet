package com.android.example.comesapp.utils;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;

import com.android.example.comesapp.DetailNewsActivity;
import com.android.example.comesapp.R;
import com.android.example.comesapp.data.NewsContract;
import com.android.example.comesapp.data.NewsPreferences;

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

    // Construct and display a notification for the latest news.
    public static void notifyUserOfLatestNews(Context context) {

        // Build the content URI for the our news
        Uri contentUri = NewsContract.NewsEntry.CONTENT_URI;

        Cursor contentCursor = context.getContentResolver().query(
                contentUri,

                // This used to limit the columns returned in our cursor.
                NEWS_NOTIFICATION_PROJECTION,
                null,
                null,
                null);

        // Move cursor to first row
        contentCursor.moveToFirst();

        // News headline as returned by API, used as notification content text.
        String headline = contentCursor.getString(INDEX_HEADLINE);

        //  Get a handle to shared preferences to set the notification flag.
        SharedPreferences preferences = context.getSharedPreferences("app", 0);
        String notificationFlag = preferences.getString("notification", "");

        // If the headline is not the same as the the one the notification flag
        // send the notification.
        if (!notificationFlag.equalsIgnoreCase(headline)) {

            // Create notification channel for OREO and higher.
            if (android.os.Build.VERSION.SDK_INT >=
                    android.os.Build.VERSION_CODES.O) {

                NotificationChannel notificationChannel = new NotificationChannel(PRIMARY_CHANNEL_ID,
                        context.getString(R.string.app_name), NotificationManager
                        .IMPORTANCE_HIGH);
                notificationChannel.setDescription(headline);
                mNotifyManager.createNotificationChannel(notificationChannel);
            }

            // Intent to open DetailNewsActivity to display the latest news.
            Intent detailIntentForLatestNews = new Intent(context, DetailNewsActivity.class);
            detailIntentForLatestNews.setData(contentUri);

            TaskStackBuilder taskStackBuilder = TaskStackBuilder.create(context);
            taskStackBuilder.addNextIntentWithParentStack(detailIntentForLatestNews);
            PendingIntent resultPendingIntent = taskStackBuilder
                    .getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

            // Build the notification with all of the parameters.
            NotificationCompat.Builder notifyBuilder = new NotificationCompat.Builder(context, PRIMARY_CHANNEL_ID)
                    .setContentTitle(context.getString(R.string.app_name))
                    .setContentText(headline)
                    .setSmallIcon(R.drawable.ic_android)
                    .setContentIntent(resultPendingIntent)
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setDefaults(NotificationCompat.DEFAULT_ALL)
                    .setAutoCancel(true);

            // Create a notification manager object.
            mNotifyManager = (NotificationManager)
                    context.getSystemService(NOTIFICATION_SERVICE);

            // NotificationId is a unique int for each notification that you must define
            mNotifyManager.notify(NOTIFICATION_ID, notifyBuilder.build());

            // Store the headline in the notification flag.
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString("notification", headline);
            editor.commit();

            // Save the current time of the notification so we can check
            // next time the news is refreshed if we should show another notification.
            NewsPreferences.saveLastNotificationTime(context, System.currentTimeMillis());
        }

        // Close the cursor to avoid wasting resources.
        contentCursor.close();
    }
}








