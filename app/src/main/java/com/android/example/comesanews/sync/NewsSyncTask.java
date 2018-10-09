package com.android.example.comesanews.sync;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;

import com.android.example.comesanews.data.NewsContract;
import com.android.example.comesanews.utils.LatestNewsJSONUtils;
import com.android.example.comesanews.utils.NetworkUtils;

import java.net.URL;

public class NewsSyncTask {

    /**
     * Performs the network request for updated news, parses the JSON from that request, and
     * inserts the latest news information into our ContentProvider.
     */
    synchronized public static void syncNews(Context context) {

        try {

            URL newsRequestUrl = NetworkUtils.buildLatestNewsUrl();
            String jsonNewsResponse = NetworkUtils.getResponseFromHttpUrl(newsRequestUrl);

            ContentValues[] newsValues = LatestNewsJSONUtils.getSimpleNewsStringsFromJson(context, jsonNewsResponse);

            if (newsValues != null && newsValues.length != 0) {
                ContentResolver newsContentResolver = context.getContentResolver();
                newsContentResolver.delete(
                        NewsContract.LatestNewsEntry.CONTENT_URI,
                        null,
                        null);

                newsContentResolver.bulkInsert(
                        NewsContract.LatestNewsEntry.CONTENT_URI,
                        newsValues);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
