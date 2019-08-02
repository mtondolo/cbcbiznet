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
}
