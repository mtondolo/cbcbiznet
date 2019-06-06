package com.android.example.comesapp;

import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.example.comesapp.data.NewsContract;
import com.android.example.comesapp.sync.EventSyncUtils;

public class EventsActivity extends AppCompatActivity implements
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

    GridLayoutManager mLayoutManager;
    private final static int NUM_GRIDS = 2;

    private int mPosition = RecyclerView.NO_POSITION;
    private final String RECYCLER_POSITION_KEY = "recycler_position";
    private static Bundle mBundleState;

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
                        mEventsAdapter.mCursor != null &&
                        position == mEventsAdapter.mCursor.getCount()) {
                    return 2;
                } else
                    return position == 0 ? 1 : 1;
            }
        });

        mRecyclerView.setLayoutManager(mLayoutManager);

        // Use setHasFixedSize(true) on mRecyclerView to designate that all items in the list
        // will have the same size.
        mRecyclerView.setHasFixedSize(true);

        // EventsAdapter is responsible for linking our events data with the Views
        // that will end up displaying our events data.
        mEventsAdapter = new EventsAdapter(this,
                new EventsAdapter.OnEnquireTextViewClickListener() {
                    @Override
                    public void onEnquireIsClick(View button, String enquireText) {

                        // Use an intent to launch an email app.
                        Intent intent = new Intent(Intent.ACTION_SENDTO);
                        intent.setData(Uri.parse("mailto:")); // only email apps should handle this
                        intent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[]{"mtondolo@gmail.com"});
                        intent.putExtra(Intent.EXTRA_SUBJECT, enquireText);
                        if (intent.resolveActivity(getPackageManager()) != null) {
                            startActivity(intent);
                        }
                    }
                });

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

