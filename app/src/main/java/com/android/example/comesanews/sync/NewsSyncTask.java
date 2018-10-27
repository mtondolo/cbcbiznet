package com.android.example.comesanews.sync;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;

import com.android.example.comesanews.data.NewsContract;
import com.android.example.comesanews.utils.NewsJSONUtils;
import com.android.example.comesanews.utils.NetworkUtils;

import java.net.URL;

public class NewsSyncTask {

    /**
     * Performs the network request for updated news, parses the JSON from that request, and
     * inserts the latest news information into our ContentProvider.
     */
    synchronized public static void syncNews(Context context) {

        try {

            /*
             * The getUrl method will return the URL that we need to get the policy JSON for the
             * policy.
             *
             */
            URL newsRequestUrl = NetworkUtils.buildLatestNewsUrl();

            /* Use the URL to retrieve the JSON */
            String jsonNewsResponse = NetworkUtils.getResponseFromHttpUrl(newsRequestUrl);

            /* Parse the JSON into a list of policy values */
            ContentValues[] newsValues = NewsJSONUtils.getSimpleNewsStringsFromJson(jsonNewsResponse);

            /*
             * In cases where our JSON contained an error code, getSimpleYCPolicyStringFromJson
             * would have returned null.
             */

            if (newsValues != null && newsValues.length != 0) {
                /* Get a handle on the ContentResolver to delete and insert data */
                ContentResolver newsContentResolver = context.getContentResolver();

                /* Delete old policy data because we don't need to keep multiple days' data */
                newsContentResolver.delete(
                        NewsContract.LatestNewsEntry.CONTENT_URI,
                        null,
                        null);

                /* Insert our new policy data into Policy's ContentProvider */
                newsContentResolver.bulkInsert(
                        NewsContract.LatestNewsEntry.CONTENT_URI,
                        newsValues);
            }


        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
