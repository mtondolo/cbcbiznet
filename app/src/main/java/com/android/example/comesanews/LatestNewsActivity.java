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

import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.AsyncTaskLoader;
import android.content.Context;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.example.comesanews.utils.LatestNewsJSONUtils;
import com.android.example.comesanews.utils.NetworkUtils;

import java.net.URL;

public class LatestNewsActivity extends AppCompatActivity implements
        LatestNewsAdapter.LatestNewsAdapterOnClickHandler,
        LoaderCallbacks<String[]> {

    private RecyclerView mRecyclerView;
    private LatestNewsAdapter mLatestNewsAdapter;
    private TextView mErrorMessageDisplay;
    private ProgressBar mLoadingIndicator;
    private static final int LATEST_NEWS_LOADER_ID = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_latest_news);

        // Using findViewById, we get a reference to our RecyclerView from xml.
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerview_latest_news);

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
        mLatestNewsAdapter = new LatestNewsAdapter(this);

        /*
         * Use mRecyclerView.setAdapter and pass in mNewsAdapter.
         * Setting the adapter attaches it to the RecyclerView in our layout.
         */
        mRecyclerView.setAdapter(mLatestNewsAdapter);

        // This ID will uniquely identify the Loader.
        int loaderId = LATEST_NEWS_LOADER_ID;

        /*
         * From LastestNewsActivity, we have implemented the LoaderCallbacks interface with the type of
         * String array.
         */
        android.support.v4.app.LoaderManager.LoaderCallbacks<String[]> callback = LatestNewsActivity.this;

        // The second parameter of the initLoader method below is a Bundle.
        Bundle bundleForLoader = null;

        // Ensures a loader is initialized and active.
        getSupportLoaderManager().initLoader(loaderId, bundleForLoader, callback);

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

    // This method handles RecyclerView item clicks.
    @Override
    public void onClick(String latestNewsItem) {
        Context context = this;
        Toast.makeText(context, latestNewsItem, Toast.LENGTH_SHORT)
                .show();
    }

    /**
     * Instantiate and return a new Loader for the given ID.
     */
    @Override
    public Loader<String[]> onCreateLoader(int id, Bundle loaderArgs) {

        return new AsyncTaskLoader<String[]>(this) {

            /* This String array will hold and help cache our latest news data */
            String[] mLatestNewsData = null;

            @Override
            protected void onStartLoading() {
                if (mLatestNewsData != null) {
                    deliverResult(mLatestNewsData);
                } else {
                    mLoadingIndicator.setVisibility(View.VISIBLE);
                    forceLoad();
                }
            }

            @Override
            public String[] loadInBackground() {
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
            public void deliverResult(String[] data) {
                mLatestNewsData = data;
                super.deliverResult(data);
            }
        };
    }

    /**
     * Called when a previously created loader has finished its load.
     */
    @Override
    public void onLoadFinished(Loader<String[]> loader, String[] data) {
        mLoadingIndicator.setVisibility(View.INVISIBLE);
        mLatestNewsAdapter.setLatestNewsData(data);
        if (null == data) {
            showErrorMessage();
        } else {
            showLatestNewsDataView();
        }

    }

    /**
     * Called when a previously created loader is being reset, and thus
     * making its data unavailable.
     */
    @Override
    public void onLoaderReset(Loader<String[]> loader) {
        /*
         * We aren't using this method at the moment, but we are required to Override
         * it to implement the LoaderCallbacks<String> interface
         */
    }

}

