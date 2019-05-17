package com.android.example.comesapp;

import android.support.annotation.Nullable;
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

import com.android.example.comesapp.utils.JsonUtils;
import com.android.example.comesapp.utils.NetworkUtils;

import java.net.URL;

public class EventsActivity extends AppCompatActivity implements
        EventsAdapter.EventsAdapterOnClickHandler,
        LoaderCallbacks<String[]> {

    private RecyclerView mRecyclerView;
    private EventsAdapter mEventsAdapter;
    private TextView mErrorMessageDisplay;
    private ProgressBar mLoadingIndicator;
    private static final int EVENTS_LOADER_ID = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_events);

        // Using findViewById, we get a reference to our RecyclerView from xml.
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerview_events);

        // This TextView is used to display errors and will be hidden if there are no errors
        mErrorMessageDisplay = (TextView) findViewById(R.id.tv_error_message_display);

        // The ProgressBar that will indicate to the user that we are loading data.
        // It will be hidden when no data is loading.
        mLoadingIndicator = (ProgressBar) findViewById(R.id.pb_loading_indicator);

        // LinearLayoutManager can support HORIZONTAL or VERTICAL orientations.
        LinearLayoutManager layoutManager
                = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(layoutManager);

        // Use setHasFixedSize(true) on mRecyclerView to designate that all items in the list
        // will have the same size.
        mRecyclerView.setHasFixedSize(true);

        // EventsAdapter is responsible for linking our events data with the Views
        // that will end up displaying our events data.
        mEventsAdapter = new EventsAdapter(this);

        // Use mRecyclerView.setAdapter and pass in mEventsAdapter.
        // Setting the adapter attaches it to the RecyclerView in our layout.
        mRecyclerView.setAdapter(mEventsAdapter);

        // This ID will uniquely identify the Loader.
        int loaderId = EVENTS_LOADER_ID;

        // From EventsActivity, we have implemented the LoaderCallbacks interface with the type of
        // String array.
        android.support.v4.app.LoaderManager.LoaderCallbacks<String[]> callback = EventsActivity.this;

        // The second parameter of the initLoader method below is a Bundle.
        Bundle bundleForLoader = null;

        // Ensures a loader is initialized and active.
        getSupportLoaderManager().initLoader(loaderId, bundleForLoader, callback);
    }

    // This method will make the View for the events data visible
    // and hide the error message.
    private void showEventsDataView() {
        mErrorMessageDisplay.setVisibility(View.INVISIBLE);
        mRecyclerView.setVisibility(View.VISIBLE);
    }

    // This method handles RecyclerView item clicks.
    @Override
    public void onClick(String eventsItem) {
        Context context = this;
        Toast.makeText(context, eventsItem, Toast.LENGTH_SHORT)
                .show();
    }

    // Instantiate and return a new Loader for the given ID.
    @Override
    public Loader<String[]> onCreateLoader(int id, Bundle loaderArgs) {
        return new AsyncTaskLoader<String[]>(this) {

            /* This String array will hold and help cache our events data */
            String[] mEventsData = null;

            @Override
            protected void onStartLoading() {
                if (mEventsData != null) {
                    deliverResult(mEventsData);
                } else {
                    mLoadingIndicator.setVisibility(View.VISIBLE);
                    forceLoad();
                }
            }

            @Nullable
            @Override
            public String[] loadInBackground() {
                URL eventsRequestUrl = NetworkUtils.buildEventUrl();
                try {
                    String jsonEventsResponse = NetworkUtils
                            .getResponseFromHttpUrl(eventsRequestUrl);
                    String[] simpleJsonEventsData = JsonUtils
                            .getEventFromJsonStr(EventsActivity.this, jsonEventsResponse);
                    return simpleJsonEventsData;
                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
            }

            @Override
            public void deliverResult(@Nullable String[] data) {
                mEventsData = data;
                super.deliverResult(data);
            }
        };
    }

    // Called when a previously created loader has finished its load.
    @Override
    public void onLoadFinished(Loader<String[]> loader, String[] data) {
        mLoadingIndicator.setVisibility(View.INVISIBLE);
        mEventsAdapter.setEventsData(data);
        if (null == data) {
            showErrorMessage();
        } else {
            showEventsDataView();
        }

    }

    // Called when a previously created loader is being reset, and thus making its data unavailable.
    @Override
    public void onLoaderReset(Loader<String[]> loader) {
        // We aren't using this method at the moment, but we are required to Override
        // it to implement the LoaderCallbacks<String> interface.
    }

    // This method will make the error message visible and hide the events View.
    private void showErrorMessage() {
        mRecyclerView.setVisibility(View.INVISIBLE);
        mErrorMessageDisplay.setVisibility(View.VISIBLE);
    }
}

