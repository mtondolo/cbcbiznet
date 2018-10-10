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

import android.database.Cursor;
import android.net.Uri;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.content.Context;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.example.comesanews.data.NewsContract;
import com.android.example.comesanews.sync.NewsSyncUtils;

public class LatestNewsActivity extends AppCompatActivity implements
        LatestNewsAdapter.LatestNewsAdapterOnClickHandler,
        LoaderCallbacks<Cursor> {

    /*
     * The columns of data that we are interested in displaying within our MainActivity's list of
     * news data.
     */
    public static final String[] MAIN_NEWS_PROJECTION = {
            NewsContract.LatestNewsEntry.COLUMN_TITLE,
            NewsContract.LatestNewsEntry.COLUMN_DATE,
            NewsContract.LatestNewsEntry.COLUMN_AUTHOR,
    };

    /*
     * We store the indices of the values in the array of Strings above to more quickly be able to
     * access the data from our query. If the order of the Strings above changes, these indices
     * must be adjusted to match the order of the Strings.
     */
    public static final int INDEX_TITLE = 0;
    public static final int INDEX_DATE = 1;
    public static final int INDEX_AUTHOR = 2;

    // This ID will be used to identify the Loader responsible for loading our news.
    private static final int ID_NEWS_LOADER = 44;

    private RecyclerView mRecyclerView;
    private LatestNewsAdapter mLatestNewsAdapter;
    private ProgressBar mLoadingIndicator;

    private int mPosition = RecyclerView.NO_POSITION;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_latest_news);

        // Using findViewById, we get a reference to our RecyclerView from xml.
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerview_latest_news);

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
        mLatestNewsAdapter = new LatestNewsAdapter(this, this);

        /*
         * Use mRecyclerView.setAdapter and pass in mNewsAdapter.
         * Setting the adapter attaches it to the RecyclerView in our layout.
         */
        mRecyclerView.setAdapter(mLatestNewsAdapter);

        showLoading();

        // Ensures a loader is initialized and active.
        getSupportLoaderManager().initLoader(ID_NEWS_LOADER, null, this);

        NewsSyncUtils.startImmediateSync(this);
    }

    /**
     * This method will make the View for the latest news data visible and
     * hide the error message.
     */
    private void showLatestNewsDataView() {
        mLoadingIndicator.setVisibility(View.INVISIBLE);
        mRecyclerView.setVisibility(View.VISIBLE);
    }

    /**
     * This method will make the loading indicator visible and hide the news View and error
     * message.
     */
    private void showLoading() {
        mRecyclerView.setVisibility(View.INVISIBLE);
        mLoadingIndicator.setVisibility(View.VISIBLE);
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
    public Loader<Cursor> onCreateLoader(int loaderId, Bundle bundle) {
        switch (loaderId) {
            case ID_NEWS_LOADER:
                Uri newsQueryUri = NewsContract.LatestNewsEntry.CONTENT_URI;
                return new android.support.v4.content.CursorLoader(this,
                        newsQueryUri,
                        MAIN_NEWS_PROJECTION,
                        null,
                        null,
                        null
                );
            default:
                throw new RuntimeException("Loader Not Implemented: " + loaderId);
        }
    }

    // Called when a previously created loader has finished its load.
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mLatestNewsAdapter.swapCursor(data);
        if (mPosition == RecyclerView.NO_POSITION) mPosition = 0;
        mRecyclerView.smoothScrollToPosition(mPosition);
        if (data.getCount() != 0) showLatestNewsDataView();
    }

    // Called when a previously created loader is being reset, and thus making its data unavailable.
    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        /*
         * Since this Loader's data is now invalid, we need to clear the Adapter that is
         * displaying the data.
         */
        mLatestNewsAdapter.swapCursor(null);
    }

}

