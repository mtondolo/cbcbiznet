/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.android.example.comesanews.utils;

import android.net.Uri;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

// These utilities will be used to communicate with the network.
public class NetworkUtils {
    final static String COMESA_BASE_URL =
            "https://query.yahooapis.com/v1/public/yql?q=env%20'store%3A%2F%2Fdatatables." +
                    "org%2Falltableswithkeys'%3Bselect%20*%20from%20htmlstring%20where%20" +
                    "url%3D%22http%3A%2F%2Fwww.comesa.int%2F%22%20and%20xpath%3D'%2F%2Fh" +
                    "tml%2Fbody%2Fdiv%5B2%5D%2Fdiv%5B2%5D%2Fdiv%5B2%5D%2Fdiv%5B1%5D%2Fdiv" +
                    "%5B2%5D%2Fdiv%5B1%5D'&format=json&env=store%3A%2F%2Fdatatables.org%2F" +
                    "alltableswithkeys";

    // Builds the URL used to query the COMESA Website for latest news.
    public static URL buildLatestNewsUrl() {
        Uri builtUri = Uri.parse(COMESA_BASE_URL).buildUpon()
                .build();
        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return url;
    }

    // This method returns the entire result from the HTTP response.
    public static String getResponseFromHttpUrl(URL url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            InputStream in = urlConnection.getInputStream();
            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");
            boolean hasInput = scanner.hasNext();
            if (hasInput) {
                return scanner.next();
            } else {
                return null;
            }
        } finally {
            urlConnection.disconnect();
        }
    }
}