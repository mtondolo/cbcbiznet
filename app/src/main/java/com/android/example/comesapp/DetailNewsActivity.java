package com.android.example.comesapp;


import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;
import com.android.example.comesapp.data.NewsContract;

public class DetailNewsActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor> {

    // The columns of data that we are interested in displaying within our DetailNewsActivity
    public static final String[] NEWS_DETAIL_PROJECTION = {
            NewsContract.NewsEntry.COLUMN_STORY
    };

    // We store the indices of the values in the array of Strings above to quickly access the data from our query.
    public static final int INDEX_STORY = 0;

    // This ID will be used to identify the Loader responsible for loading the story
    private static final int ID_DETAIL_LOADER = 353;

    // The URI that is used to access the news details
    private Uri mUri;

    private TextView mStoryView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_news);

        mStoryView = (TextView) findViewById(R.id.tv_story);

        mUri = getIntent().getData();
        if (mUri == null) throw new NullPointerException("URI for DetailActivity cannot be null");

        // This connects our Activity into the loader lifecycle.
        getSupportLoaderManager().initLoader(ID_DETAIL_LOADER, null, this);
    }

    // Creates and returns a CursorLoader that loads the data for our URI and stores it in a Cursor.
    @Override
    public Loader<Cursor> onCreateLoader(int loaderId, Bundle loaderArg) {
        switch (loaderId) {

            case ID_DETAIL_LOADER:

                return new CursorLoader(this,
                        mUri,
                        NEWS_DETAIL_PROJECTION,
                        null,
                        null,
                        null);

            default:
                throw new RuntimeException("Loader Not Implemented: " + loaderId);
        }
    }

    // Runs on the main thread when a load is complete.
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        // we need to check the cursor to make sure we have the results that we are expecting.
        boolean cursorHasValidData = false;
        if (data != null && data.moveToFirst()) {

            // We have valid data, continue on to bind the data to the UI
            cursorHasValidData = true;
        }

        if (!cursorHasValidData) {

            // No data to display, simply return and do nothing
            return;
        }

        // Read the story from the cursor
        String story = data.getString(INDEX_STORY);

        //Set the text
        mStoryView.setText(story);

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
