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
package com.android.example.comesanews;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.example.comesanews.utils.LatestNewsJSONUtils;
import com.android.example.comesanews.utils.NetworkUtils;

import java.net.URL;

public class LatestNewsActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private LatestNewsAdapter mLatestNewsAdapter;
    private TextView mErrorMessageDisplay;
    private ProgressBar mLoadingIndicator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_latest_news);

        // Using findViewById, we get a reference to our RecyclerView from xml.
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerview_policy);

        /* This TextView is used to display errors and will be hidden if there are no errors */
        mErrorMessageDisplay = (TextView) findViewById(R.id.tv_error_message_display);

        /*
         * The ProgressBar that will indicate to the user that we are loading data. It will be
         * hidden when no data is loading.
         *
         */
        mLoadingIndicator = (ProgressBar) findViewById(R.id.pb_loading_indicator);

        // LinearLayoutManager can support HORIZONTAL or VERTICAL orientations.
        LinearLayoutManager layoutManager
                = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(layoutManager);

        /*
         * Use setHasFixedSize(true) on mRecyclerView to designate that all items in the list
         * will have the same size.
         */
        mRecyclerView.setHasFixedSize(true);

        /*
         * The NewsAdapter is responsible for linking our news data with the Views that will end up
         * displaying our news data.
         */
        mLatestNewsAdapter = new LatestNewsAdapter();

        /*
         * Use mRecyclerView.setAdapter and pass in mNewsAdapter.
         * Setting the adapter attaches it to the RecyclerView in our layout.
         */
        mRecyclerView.setAdapter(mLatestNewsAdapter);

        /* Once all of our views are setup, we can load the latest news data. */
        loadLatestNewsData();
    }

    // This method will tell some background method to get the latest news data in the background.
    private void loadLatestNewsData() {
        showLatestNewsDataView();
        new FetchLatestNewsTask().execute();
    }

    /**
     * This method will make the View for the latest news data visible and
     * hide the error message.
     */
    private void showLatestNewsDataView() {
        mErrorMessageDisplay.setVisibility(View.INVISIBLE);
        mRecyclerView.setVisibility(View.VISIBLE);
    }

    /**
     * This method will make the error message visible and hide the news
     * View.
     */
    private void showErrorMessage() {
        mRecyclerView.setVisibility(View.INVISIBLE);
        mErrorMessageDisplay.setVisibility(View.VISIBLE);
    }

    public class FetchLatestNewsTask extends AsyncTask<String, Void, String[]> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mLoadingIndicator.setVisibility(View.VISIBLE);
        }

        @Override
        protected String[] doInBackground(String... params) {
            URL latestNewsRequestUrl = NetworkUtils.buildLatestNewsUrl();
            try {
                String jsonLatestNewsResponse = NetworkUtils
                        .getResponseFromHttpUrl(latestNewsRequestUrl);
                String[] simpleJsonLatestNewsData = LatestNewsJSONUtils
                        .getSimpleNewsStringsFromJson(LatestNewsActivity.this, jsonLatestNewsResponse);
                return simpleJsonLatestNewsData;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String[] latestNewsData) {
            mLoadingIndicator.setVisibility(View.INVISIBLE);
            if (latestNewsData != null) {
                showLatestNewsDataView();
                mLatestNewsAdapter.setLatestNewsData(latestNewsData);
            } else {
                showErrorMessage();
            }
        }
    }
}

