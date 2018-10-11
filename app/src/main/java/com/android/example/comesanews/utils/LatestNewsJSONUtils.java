package com.android.example.comesanews.utils;

import android.content.ContentValues;
import android.content.Context;
import android.text.Html;
import android.text.TextUtils;

import com.android.example.comesanews.data.NewsContract;

import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

public class LatestNewsJSONUtils {

    // This method parses JSON from a web response and returns an array of Strings
    public static ContentValues[] getSimpleNewsStringsFromJson(Context context, String newsJsonStr)
            throws JSONException {

        // JSON variables
        final String JSON_QUERY = "query";
        final String JSON_RESULTS = "results";
        final String JSON_RESULT = "result";


        // JSoup variables
        final String JSoup_LIST = "li";
        final String JSoup_TITLE = "post-title";
        final String JSoup_DATE = "date";
        final String JSoup_AUTHOR = "post-footer-author";
        final String JSoup_IMAGE_TAG = "img";
        final String JSoup_IMAGE_KEY = "abs:src";
        final String JSoup_WEB_TAG = "a";
        final String JSoup_WEB= "abs:href";

        Html.fromHtml(newsJsonStr).toString();

        JSONObject newsJson = new JSONObject(newsJsonStr);
        JSONObject query = newsJson.getJSONObject(JSON_QUERY);
        JSONObject results = query.getJSONObject(JSON_RESULTS);

        // If the JSON string is empty or null, then return early.
        if (TextUtils.isEmpty(newsJsonStr)) {
            return null;
        }

        String result = results.getString(JSON_RESULT);
        Document document = Jsoup.parse(result);
        Elements li = document.select(JSoup_LIST);

        ContentValues[] newsContentValues = new ContentValues[li.size()];

        for (int i = 0; i < li.size(); i++) {

            /* These are the values that will be collected */
            String title;
            String date;
            String author;
            String imageUrl;
            String webUrl;

            // Extract the value for the keys called "post-title", "date", "post-footer-author",
            // "img", "abs:src", "a" and "href".
            title = li.get(i).getElementsByClass(JSoup_TITLE).text();
            date = li.get(i).getElementsByClass(JSoup_DATE).text();
            author = li.get(i).getElementsByClass(JSoup_AUTHOR).text();
            imageUrl = li.get(i).getElementsByTag(JSoup_IMAGE_TAG).attr(JSoup_IMAGE_KEY);
            webUrl = li.get(i).getElementsByTag(JSoup_WEB_TAG).attr(JSoup_WEB);

            ContentValues newsValues = new ContentValues();
            newsValues.put(NewsContract.LatestNewsEntry.COLUMN_TITLE, title);
            newsValues.put(NewsContract.LatestNewsEntry.COLUMN_DATE, date);
            newsValues.put(NewsContract.LatestNewsEntry.COLUMN_AUTHOR, author);
            newsValues.put(NewsContract.LatestNewsEntry.COLUMN_IMAGE, imageUrl);
            newsValues.put(NewsContract.LatestNewsEntry.COLUMN_WEB, webUrl);

            newsContentValues[i] = newsValues;

        }

        return newsContentValues;
    }
}

