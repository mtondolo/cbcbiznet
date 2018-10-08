package com.android.example.comesanews.utils;

import android.content.Context;
import android.text.Html;
import android.text.TextUtils;

import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class LatestNewsJSONUtils {

    // This method parses JSON from a web response and returns an array of Strings
    public static String[] getSimpleNewsStringsFromJson(Context context, String newsJsonStr)
            throws JSONException {

        Html.fromHtml(newsJsonStr).toString();


        String[] parsedNewsData = null;

        JSONObject newsJson = new JSONObject(newsJsonStr);
        JSONObject query = newsJson.getJSONObject("query");
        JSONObject results = query.getJSONObject("results");

        // If the JSON string is empty or null, then return early.
        if (TextUtils.isEmpty(newsJsonStr)) {
            return null;
        }

        String result = results.getString("result");
        Document document = Jsoup.parse(result);
        Elements li = document.select("li");

        parsedNewsData = new String[li.size()];

        for (int i = 0; i < li.size(); i++) {
            String title = li.get(i).getElementsByClass("post-title").text();
            String date = li.get(i).getElementsByClass("date").text();
            String author = li.get(i).getElementsByClass("post-footer-author").text();
            parsedNewsData[i] = title + date + author;
        }

        return parsedNewsData;
    }
}

