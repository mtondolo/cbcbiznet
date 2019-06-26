package com.android.example.comesapp.utils;

import android.content.Context;
import android.text.TextUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class JsonRefreshUtils {

    public static String getNewDataJsonStr
            (Context context, String newsJsonStr) throws JSONException {

        // headline is the key for the news item
        final String KEY_HEADLINE = "headline";

        // If the JSON string is empty or null, then return early.
        if (TextUtils.isEmpty(newsJsonStr)) {
            return null;
        }

        JSONArray newsArray = new JSONArray(newsJsonStr);

        final StringBuilder parsedNewsData = new StringBuilder();

        // This is the value that will be collected
        String headline;

        // Get the JSON object representing the news item
        JSONObject news = newsArray.getJSONObject(0);

        // Extract the value for the key called "headline".
        headline = news.getString(KEY_HEADLINE);

        // Stick the data to our string builder
        parsedNewsData.append(headline);

        //return parsedNewsData;
        return parsedNewsData.toString();
    }

    public static String getEventsDataJsonStr
            (Context context, String eventsJsonStr) throws JSONException {

        // title is the key for the events item
        final String KEY_TITLE = "title";

        // If the JSON string is empty or null, then return early.
        if (TextUtils.isEmpty(eventsJsonStr)) {
            return null;
        }

        JSONArray eventsArray = new JSONArray(eventsJsonStr);

        final StringBuilder parsedEventData = new StringBuilder();

        // This is the value that will be collected
        String title;

        // Get the JSON object representing the event item
        JSONObject event = eventsArray.getJSONObject(0);

        // Extract the value for the key called "headline".
        title = event.getString(KEY_TITLE);

        // Stick the data to our string builder
        parsedEventData.append(title);

        //return parsedEventData;
        return parsedEventData.toString();
    }
}
