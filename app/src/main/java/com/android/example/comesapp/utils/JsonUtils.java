package com.android.example.comesapp.utils;

import android.content.ContentValues;
import android.content.Context;
import android.text.TextUtils;

import com.android.example.comesapp.data.NewsContract;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class JsonUtils {

    // This method parses JSON from a web response and returns an array of Strings
    public static ContentValues[] getNewsFromJsonStr(Context context, String newsJsonStr)
            throws JSONException {

        // headline, storyUrl, date and imageUrl are keys for the news item
        final String KEY_DATE = "created_At";
        final String KEY_HEADLINE = "headline";
        final String KEY_STORY = "story";
        final String KEY_STORY_URL = "storyUrl";
        final String KEY_IMAGE_URL = "imageUrl";

        // If the JSON string is empty or null, then return early.
        if (TextUtils.isEmpty(newsJsonStr)) {
            return null;
        }

        JSONArray newsArray = new JSONArray(newsJsonStr);

        ContentValues[] newsContentValues = new ContentValues[newsArray.length()];

        for (int i = 0; i < newsArray.length(); i++) {

            /* These are the values that will be collected */
            long dateTimeMillis;
            String headline;
            String story;
            String storyUrl;
            String imageUrl;

            /* Get the JSON object representing the news item */
            JSONObject news = newsArray.getJSONObject(i);

            // Extract the value for the key called "headline", "story", "storyUrl, "dateTimeMillis" and "imageUrl"
            dateTimeMillis = news.getLong(KEY_DATE);
            headline = news.getString(KEY_HEADLINE);
            story = news.getString(KEY_STORY);
            storyUrl = news.getString(KEY_STORY_URL);
            imageUrl = news.getString(KEY_IMAGE_URL);

            ContentValues newsValues = new ContentValues();
            newsValues.put(NewsContract.NewsEntry.COLUMN_DATE, dateTimeMillis);
            newsValues.put(NewsContract.NewsEntry.COLUMN_HEADLINE, headline);
            newsValues.put(NewsContract.NewsEntry.COLUMN_STORY, story);
            newsValues.put(NewsContract.NewsEntry.COLUMN_STORY_URL, storyUrl);
            newsValues.put(NewsContract.NewsEntry.COLUMN_IMAGE_URL, imageUrl);

            newsContentValues[i] = newsValues;

        }
        return newsContentValues;
    }
}

