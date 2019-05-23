package com.android.example.comesapp;

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
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.example.comesapp.data.NewsContract;
import com.android.example.comesapp.sync.EventSyncUtils;

public class EventsActivity extends AppCompatActivity implements
        EventsAdapter.EventsAdapterOnClickHandler,
        LoaderCallbacks<Cursor> {

    // The columns of data that we are interested in displaying within our EventsActivity's list of events data.
    public static final String[] EVENTS_PROJECTION = {
            NewsContract.NewsEntry.COLUMN_TITLE,
            NewsContract.NewsEntry.COLUMN_VENUE,
    };

    // We store the indices of the values in the array of Strings above to more quickly be able to
    // access the data from our query. If the order of the Strings above changes, these indices
    // must be adjusted to match the order of the Strings.

    public static final int INDEX_TITLE = 0;
    public static final int INDEX_VENUE = 1;

    // This ID will be used to identify the Loader responsible for loading our policies.
    private static final int ID_EVENTS_LOADER = 45;

    private RecyclerView mRecyclerView;
    private EventsAdapter mEventsAdapter;
    private TextView mErrorMessageDisplay;
    private ProgressBar mLoadingIndicator;
    ImageView backArrow;

    private int mPosition = RecyclerView.NO_POSITION;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_events);

        // Using findViewById, we get a reference to our RecyclerView from xml.
        mRecyclerView = findViewById(R.id.recyclerview_events);

        // This TextView is used to display errors and will be hidden if there are no errors
        mErrorMessageDisplay = findViewById(R.id.tv_error_message_display);

        // The ProgressBar that will indicate to the user that we are loading data.
        // It will be hidden when no data is loading.
        mLoadingIndicator = findViewById(R.id.pb_loading_indicator);

        // LinearLayoutManager can support HORIZONTAL or VERTICAL orientations.
        LinearLayoutManager layoutManager
                = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(layoutManager);

        // Use setHasFixedSize(true) on mRecyclerView to designate that all items in the list
        // will have the same size.
        mRecyclerView.setHasFixedSize(true);

        // EventsAdapter is responsible for linking our events data with the Views
        // that will end up displaying our events data.
        mEventsAdapter = new EventsAdapter(this, this);

        // Use mRecyclerView.setAdapter and pass in mEventsAdapter.
        // Setting the adapter attaches it to the RecyclerView in our layout.
        mRecyclerView.setAdapter(mEventsAdapter);

        showLoading();

        // Ensures a loader is initialized and active.
        getSupportLoaderManager().initLoader(ID_EVENTS_LOADER, null, this);

        // Set up a listener for the back button in the tool-bar
        backArrow = findViewById(R.id.ic_arrow_back_event);
        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        EventSyncUtils.initialize(getApplicationContext());

    }

    // This method will make the loading indicator visible
    // and hide the events View and error message.
    private void showLoading() {
        mRecyclerView.setVisibility(View.INVISIBLE);
        mLoadingIndicator.setVisibility(View.VISIBLE);
    }

    // This method will make the View for the events data visible
    // and hide the error message.
    private void showEventsDataView() {
        mLoadingIndicator.setVisibility(View.INVISIBLE);
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
    public Loader<Cursor> onCreateLoader(int loaderId, Bundle bundle) {

        switch (loaderId) {
            case ID_EVENTS_LOADER:
                Uri eventsQueryUri = NewsContract.NewsEntry.EVENT_URI;
                return new android.support.v4.content.CursorLoader(this,
                        eventsQueryUri,
                        EVENTS_PROJECTION,
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
        mEventsAdapter.swapCursor(data);
        if (mPosition == RecyclerView.NO_POSITION) mPosition = 0;
        mRecyclerView.smoothScrollToPosition(mPosition);
        if (data.getCount() != 0) showEventsDataView();
    }

    // Called when a previously created loader is being reset, and thus making its data unavailable.
    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // Since this Loader's data is now invalid, we need to clear
        // the Adapter that is displaying the data.
        mEventsAdapter.swapCursor(null);
    }
}

