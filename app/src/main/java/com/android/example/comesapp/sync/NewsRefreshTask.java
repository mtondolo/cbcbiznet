package com.android.example.comesapp.sync;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;

import com.android.example.comesapp.data.NewsContract;
import com.android.example.comesapp.utils.JsonUtils;
import com.android.example.comesapp.utils.NetworkUtils;

import java.net.URL;

public class NewsRefreshTask {

    // Performs the network request to refresh news, parses the JSON from that request.
    synchronized public static void refreshNews(Context context) {

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

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
