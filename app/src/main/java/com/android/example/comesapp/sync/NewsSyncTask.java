package com.android.example.comesapp.sync;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.text.format.DateUtils;

import com.android.example.comesapp.data.NewsContract;
import com.android.example.comesapp.data.NewsPreferences;
import com.android.example.comesapp.utils.JsonUtils;
import com.android.example.comesapp.utils.NetworkUtils;
import com.android.example.comesapp.utils.NotificationUtils;


import java.net.URL;

public class NewsSyncTask {

    // Performs the network request for updated news, parses the JSON from that request.
    synchronized public static void syncNews(Context context) {

        try {

            // The getUrl method will return the URL that we need to get the JSON for the news.
            URL newsRequestUrl = NetworkUtils.buildUrl(context);

            // Use the URL to retrieve the JSON
            String jsonNewsResponse = NetworkUtils.getResponseFromHttpUrl(newsRequestUrl);

            // Parse the JSON into a list of news values
            ContentValues[] newsValues = JsonUtils.getNewsFromJsonStr(context, jsonNewsResponse);

            // In cases where our JSON contained an error code, getNewsFromJsonStr would have returned null.
            if (newsValues != null && newsValues.length != 0) {

                // Get a handle on the ContentResolver to delete and insert data
                ContentResolver newsContentResolver = context.getContentResolver();

                // Delete old news data because we don't need to keep multiple days' data
                newsContentResolver.delete(
                        NewsContract.NewsEntry.CONTENT_URI,
                        null,
                        null);

                // Insert our new news data into news's ContentProvider.
                newsContentResolver.bulkInsert(
                        NewsContract.NewsEntry.CONTENT_URI,
                        newsValues);

                // Determine whether to notify the user that the news has been refreshed.
                boolean notificationsEnabled = NewsPreferences.areNotificationsEnabled(context);

                // If last notification is more than 1 day,
                // Send another notification to the user.
                long timeSinceLastNotification = NewsPreferences
                        .getEllapsedTimeSinceLastNotification(context);

                boolean oneDayPassedSinceLastNotification = false;

                if (timeSinceLastNotification >= DateUtils.DAY_IN_MILLIS) {
                    oneDayPassedSinceLastNotification = true;
                }

                // Show the notification if the user wants them shown and
                // we haven't shown a notification in the past day.
                if (notificationsEnabled && oneDayPassedSinceLastNotification) {
                    NotificationUtils.notifyUserOfLatestNews(context);
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
