package com.android.example.comesapp;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;
import android.widget.ImageView;

import com.android.example.comesapp.data.NewsContract;

import com.squareup.picasso.Picasso;

public class DetailNewsActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor> {

    // The columns of data that we are interested in displaying within our DetailNewsActivity
    public static final String[] NEWS_DETAIL_PROJECTION = {
            NewsContract.NewsEntry.COLUMN_IMAGE_URL,
            NewsContract.NewsEntry.COLUMN_HEADLINE,
            NewsContract.NewsEntry.COLUMN_DATE,
            NewsContract.NewsEntry.COLUMN_STORY
    };

    // We store the indices of the values in the array of Strings above to quickly access the data from our query.
    public static final int INDEX_IMAGE_URL = 0;
    public static final int INDEX_HEADLINE = 1;
    public static final int INDEX_DATE = 2;
    public static final int INDEX_STORY = 3;

    // This ID will be used to identify the Loader responsible for loading the story
    private static final int ID_DETAIL_LOADER = 353;

    // The URI that is used to access the news details
    private Uri mUri;

    private ImageView mDetailImageView;
    private TextView mDetailHeadlineView;
    private TextView mDetailDateView;
    private TextView mDetailStoryView;
    private TextView mDetailCopyrightView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_news);

        Toolbar topToolbar = (Toolbar) findViewById(R.id.detail_news_toolbar);
        setSupportActionBar(topToolbar);

        mDetailHeadlineView = (TextView) findViewById(R.id.detail_headline);
        mDetailDateView = (TextView) findViewById(R.id.detail_date);
        mDetailStoryView = (TextView) findViewById(R.id.detail_story);
        mDetailCopyrightView = (TextView) findViewById(R.id.detail_copyright);
        mDetailImageView = (ImageView) findViewById(R.id.detail_image);

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

        // Read the headline, date, story and image url from the cursor
        String detailHeadline = data.getString(INDEX_HEADLINE);
        String detailDate = data.getString(INDEX_DATE);
        String detailImage = data.getString(DetailNewsActivity.INDEX_IMAGE_URL);
        String detailStory = data.getString(INDEX_STORY);

        // Load the image using the given url
        if (detailImage.isEmpty()) {//url.isEmpty()
            Picasso.get()
                    .load(R.color.colorPrimary)
                    .placeholder(R.color.colorPrimary)
                    .resize(126, 78)
                    .centerCrop()
                    .into(mDetailImageView);
        } else {
            Picasso.get()
                    .load(detailImage)
                    .error(R.color.colorPrimary)
                    .fit()
                    .into(mDetailImageView);//this is our ImageView
        }

        // Format the story string from the database
        String formattedDetailStory = detailStory.replace("[", "");
        formattedDetailStory = formattedDetailStory.replace("]", "");
        formattedDetailStory = formattedDetailStory.replace("\"", "");
        formattedDetailStory = formattedDetailStory.replace("\'", "\"");
        formattedDetailStory = formattedDetailStory.replace(",", "");
        formattedDetailStory = formattedDetailStory.replace("^", ",");
        formattedDetailStory = formattedDetailStory.replace("\\n", System.getProperty("line.separator"));

        //Set the text to the views
        mDetailHeadlineView.setText(detailHeadline);
        mDetailDateView.setText(detailDate);
        mDetailStoryView.setText(formattedDetailStory);
        mDetailCopyrightView.setText(getString(R.string.detail_copyright));
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    }

}


