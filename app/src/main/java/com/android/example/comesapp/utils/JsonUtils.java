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
        final String KEY_IMAGE_DESCRIPTION = "imageDescription";

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
            String imageDescription;

            /* Get the JSON object representing the news item */
            JSONObject news = newsArray.getJSONObject(i);

            // Extract the value for the key called "headline",
            // "story", "storyUrl, "dateTimeMillis", "imageUrl"
            // and "imageDescription"
            dateTimeMillis = news.getLong(KEY_DATE);
            headline = news.getString(KEY_HEADLINE);
            story = news.getString(KEY_STORY);
            storyUrl = news.getString(KEY_STORY_URL);
            imageUrl = news.getString(KEY_IMAGE_URL);
            imageDescription = news.getString(KEY_IMAGE_DESCRIPTION);

            ContentValues newsValues = new ContentValues();
            newsValues.put(NewsContract.NewsEntry.COLUMN_DATE, dateTimeMillis);
            newsValues.put(NewsContract.NewsEntry.COLUMN_HEADLINE, headline);
            newsValues.put(NewsContract.NewsEntry.COLUMN_STORY, story);
            newsValues.put(NewsContract.NewsEntry.COLUMN_STORY_URL, storyUrl);
            newsValues.put(NewsContract.NewsEntry.COLUMN_IMAGE_URL, imageUrl);
            newsValues.put(NewsContract.NewsEntry.COLUMN_IMAGE_DESCRIPTION, imageDescription);

            newsContentValues[i] = newsValues;

        }
        return newsContentValues;
    }

    // This method parses JSON from a web response and returns an array of Strings
    public static String getEventFromJsonStr(Context context, String eventJsonStr)
            throws JSONException {

        // headline, storyUrl, date and imageUrl are keys for the news item
        final String KEY_TITLE = "title";
        final String KEY_VENUE = "venue";

        // If the JSON string is empty or null, then return early.
        if (TextUtils.isEmpty(eventJsonStr)) {
            return null;
        }

        JSONArray newsArray = new JSONArray(eventJsonStr);

        final StringBuilder parsedNewsData = new StringBuilder();

        for (int i = 0; i < newsArray.length(); i++) {

            // These are the values that will be collected
            String title;
            String venue;

            // Get the JSON object representing the event
            JSONObject event = newsArray.getJSONObject(i);

            // Extract the value for the key called "title",
            // and "venue"
            title = event.getString(KEY_TITLE);
            venue = event.getString(KEY_VENUE);

            parsedNewsData.append(title).append(venue).append("\n");

        }
        return parsedNewsData.toString();
    }
}

