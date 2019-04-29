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
package com.android.example.comesapp;

import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import com.android.example.comesapp.data.NewsContract;
import com.android.example.comesapp.sync.NewsRefreshUtils;
import com.android.example.comesapp.sync.NewsSyncUtils;

public class NewsActivity extends AppCompatActivity implements
        RecyclerViewAdapter.RecyclerViewAdapterOnClickHandler,
        LoaderCallbacks<Cursor> {

    // The columns of data that we are interested in displaying within our NewsActivity's list of news data.
    public static final String[] NEWS_PROJECTION = {
            NewsContract.NewsEntry.COLUMN_DATE,
            NewsContract.NewsEntry.COLUMN_HEADLINE,
            NewsContract.NewsEntry.COLUMN_IMAGE_URL,
    };

    // We store the indices of the values in the array of Strings above to more quickly be able to access the data from our query.
    public static final int INDEX_DATE = 0;
    public static final int INDEX_HEADLINE = 1;
    public static final int INDEX_IMAGE_URL = 2;

    // This ID will be used to identify the Loader responsible for loading our news.
    private static final int ID_NEWS_LOADER = 44;

    // RecyclerView constants and variables
    private RecyclerView mRecyclerView;
    private final String RECYCLER_POSITION_KEY = "recycler_position";
    GridLayoutManager mLayoutManager;
    private static Bundle mBundleState;
    private int mPosition = RecyclerView.NO_POSITION;

    private RecyclerViewAdapter mRecyclerViewAdapter;
    private ProgressBar mLoadingIndicator;

    private final static int NUM_GRIDS = 2;

    private SwipeRefreshLayout mySwipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);

        Toolbar topToolbar = findViewById(R.id.top_toolbar);
        setSupportActionBar(topToolbar);

        // Lookup the swipe container view and set its properties
        mySwipeRefreshLayout = findViewById(R.id.swiperefresh);
        mySwipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);
        mySwipeRefreshLayout.setProgressViewOffset(false, 100, 150);

        /*
         * Sets up a SwipeRefreshLayout.OnRefreshListener that is invoked when the user
         * performs a swipe-to-refresh gesture.
         */
        mySwipeRefreshLayout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        NewsRefreshUtils.startImmediateRefresh(getApplicationContext());
                    }
                }
        );

        // Using findViewById, we get a reference to our RecyclerView from xml.
        mRecyclerView = findViewById(R.id.recyclerview_news);

        // The ProgressBar that will indicate to the user that we are loading data.
        mLoadingIndicator = findViewById(R.id.pb_loading_indicator);

        // Get the orientation of the screen
        final int orientation = this.getResources().getConfiguration().orientation;

        // Set out the layout
        mLayoutManager
                = new GridLayoutManager(this, NUM_GRIDS);
        mLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                if (orientation == Configuration.ORIENTATION_PORTRAIT) {
                    return position == 0 ? 2 : 2;
                } else if (orientation == Configuration.ORIENTATION_LANDSCAPE &&
                        mRecyclerViewAdapter.mCursor != null &&
                        position == mRecyclerViewAdapter.mCursor.getCount()) {
                    return 2;
                } else
                    return position == 0 ? 2 : 1;
            }
        });

        mRecyclerView.setLayoutManager(mLayoutManager);

        /*
         * Use setHasFixedSize(true) on mRecyclerView to designate that all items in the list
         * will have the same size.
         */
        mRecyclerView.setHasFixedSize(true);

        // The RecyclerViewAdapter is responsible for linking our news data with
        // the Views that will end up displaying our news data.
        mRecyclerViewAdapter = new RecyclerViewAdapter(this, this);

        // Use mRecyclerView.setAdapter and pass in mRecyclerViewAdapter.
        mRecyclerView.setAdapter(mRecyclerViewAdapter);

        showLoading();

        // Ensures a loader is initialized and active.
        getSupportLoaderManager().initLoader(ID_NEWS_LOADER, null, this);

        // NewsSyncUtils's initialize method instead of startImmediateSync
        NewsSyncUtils.initialize(this);
    }


    @Override
    protected void onPause() {
        super.onPause();

        // Save RecyclerView state
        mBundleState = new Bundle();
        mPosition = mLayoutManager.findFirstCompletelyVisibleItemPosition();
        mBundleState.putInt(RECYCLER_POSITION_KEY, mPosition);
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Restore RecyclerView state
        if (mBundleState != null) {
            mPosition = mBundleState.getInt(RECYCLER_POSITION_KEY);
            if (mPosition == RecyclerView.NO_POSITION) mPosition = 0;

            // Scroll the RecyclerView to mPosition
            mRecyclerView.smoothScrollToPosition(mPosition);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {

        // Save RecyclerView state
        outState.putInt(RECYCLER_POSITION_KEY, mLayoutManager.findFirstCompletelyVisibleItemPosition());
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        // Restore RecyclerView state
        if (savedInstanceState.containsKey(RECYCLER_POSITION_KEY)) {
            mPosition = savedInstanceState.getInt(RECYCLER_POSITION_KEY);
            if (mPosition == RecyclerView.NO_POSITION) mPosition = 0;
            // Scroll the RecyclerView to mPosition
            mRecyclerView.smoothScrollToPosition(mPosition);
        }

        super.onRestoreInstanceState(savedInstanceState);
    }

    // This method will make the View for the latest news data visible and hide the error message.
    private void showNewsDataView() {
        mLoadingIndicator.setVisibility(View.INVISIBLE);
        mRecyclerView.setVisibility(View.VISIBLE);
    }

    // This method will make the loading indicator visible and hide the news View and error message.
    private void showLoading() {
        mRecyclerView.setVisibility(View.INVISIBLE);
        mLoadingIndicator.setVisibility(View.VISIBLE);
    }

    // This method handles RecyclerView item clicks.
    @Override
    public void onClick(long dateTimeInMillis) {
        Intent newsDetailIntent = new Intent(NewsActivity.this, DetailNewsActivity.class);
        Uri uriForDateTimeInMillisClicked = NewsContract.NewsEntry.buildNewsUriWithDate(dateTimeInMillis);
        newsDetailIntent.setData(uriForDateTimeInMillisClicked);
        startActivity(newsDetailIntent);
    }

    // Instantiate and return a new Loader for the given ID.
    @Override
    public Loader<Cursor> onCreateLoader(int loaderId, Bundle bundle) {
        switch (loaderId) {
            case ID_NEWS_LOADER:
                Uri newsQueryUri = NewsContract.NewsEntry.CONTENT_URI;
                return new android.support.v4.content.CursorLoader(this,
                        newsQueryUri,
                        NEWS_PROJECTION,
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
        mRecyclerViewAdapter.swapCursor(data);
        if (mPosition == RecyclerView.NO_POSITION) mPosition = 0;
        mRecyclerView.smoothScrollToPosition(mPosition);
        if (data.getCount() != 0) showNewsDataView();
    }

    // Called when a previously created loader is being reset, and thus making its data unavailable.
    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

        // Since this Loader's data is now invalid,
        // we need to clear the Adapter that is displaying the data.
        mRecyclerViewAdapter.swapCursor(null);
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
        if (id == R.id.action_settings) {
            Intent startSettingsActivity = new Intent(this, SettingsActivity.class);
            startActivity(startSettingsActivity);
            return true;
        }
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



