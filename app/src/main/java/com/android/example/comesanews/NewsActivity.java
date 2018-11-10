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

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.example.comesanews.data.NewsContract;
import com.android.example.comesanews.sync.NewsSyncUtils;

public class NewsActivity extends AppCompatActivity implements
        NewsAdapter.LatestNewsAdapterOnClickHandler,
        LoaderCallbacks<Cursor> {

    /*
     * The columns of data that we are interested in displaying within our MainActivity's list of
     * news data.
     */
    public static final String[] MAIN_NEWS_PROJECTION = {
            NewsContract.LatestNewsEntry.COLUMN_TITLE,
            NewsContract.LatestNewsEntry.COLUMN_DATE,
            NewsContract.LatestNewsEntry.COLUMN_IMAGE,
            NewsContract.LatestNewsEntry.COLUMN_WEB,
    };

    /*
     * We store the indices of the values in the array of Strings above to more quickly be able to
     * access the data from our query. If the order of the Strings above changes, these indices
     * must be adjusted to match the order of the Strings.
     */
    public static final int INDEX_TITLE = 0;
    public static final int INDEX_DATE = 1;
    public static final int INDEX_IMAGE = 2;
    public static final int INDEX_WEB = 3;

    // This ID will be used to identify the Loader responsible for loading our news.
    private static final int ID_NEWS_LOADER = 44;

    private RecyclerView mRecyclerView;
    private NewsAdapter mNewsAdapter;
    private ProgressBar mLoadingIndicator;

    private int mPosition = RecyclerView.NO_POSITION;

    private final static int NUM_GRIDS = 4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);

        Toolbar topToolbar = (Toolbar) findViewById(R.id.top_toolbar);
        setSupportActionBar(topToolbar);

        // Using findViewById, we get a reference to our RecyclerView from xml.
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view_news);

        /*
         * The ProgressBar that will indicate to the user that we are loading data. It will be
         * hidden when no data is loading.
         *
         */
        mLoadingIndicator = (ProgressBar) findViewById(R.id.pb_loading_indicator);

        // Get the orientation of the screen
        final int orientation = this.getResources().getConfiguration().orientation;

        // Set out the layout
        GridLayoutManager layoutManager
                = new GridLayoutManager(this, NUM_GRIDS);
        layoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                if (orientation == Configuration.ORIENTATION_PORTRAIT) {
                    return position == 0 ? 4 : 4;
                } else
                    return position == 0 ? 4 : 2;
            }
        });

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
        mNewsAdapter = new NewsAdapter(this, this);

        /*
         * Use mRecyclerView.setAdapter and pass in mNewsAdapter.
         * Setting the adapter attaches it to the RecyclerView in our layout.
         */
        mRecyclerView.setAdapter(mNewsAdapter);

        showLoading();

        // Ensures a loader is initialized and active.
        getSupportLoaderManager().initLoader(ID_NEWS_LOADER, null, this);

        // NewsSyncUtils's initialize method instead of startImmediateSync
        NewsSyncUtils.initialize(this);

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
    public void onClick(String url) {
        openWebPage(url);
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
        mNewsAdapter.swapCursor(data);
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
        mNewsAdapter.swapCursor(null);
    }

    public void openWebPage(String url) {
        Context context = this;

        // Launch the NewsDetailActivity using an explicit Intent
        Class destinationClass = NewsDetailActivity.class;
        Intent intentToStartNewsDetailActivity = new Intent(context, destinationClass);
        intentToStartNewsDetailActivity.putExtra(Intent.EXTRA_TEXT, url);
        startActivity(intentToStartNewsDetailActivity);
    }

    // Inflate the menu for this Activity
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.news, menu);
        return true;
    }

    // Handle clicks for this menu
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_contact_us) {
            composeEmail();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // Use an intent to launch an email app.
    public void composeEmail() {
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:")); // only email apps should handle this
        intent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[]{"mtondolo@gmail.com"});
        intent.putExtra(Intent.EXTRA_SUBJECT, "COMESA News feedback");
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }
}

