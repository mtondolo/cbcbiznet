package com.android.example.comesapp;

import android.content.Intent;
import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.format.DateUtils;
import android.view.View;
import android.widget.ImageView;

import com.android.example.comesapp.data.NewsContract;
import com.android.example.comesapp.databinding.ActivityDetailNewsBinding;

import com.squareup.picasso.Picasso;

public class DetailNewsActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor> {

    // The columns of data that we are interested in displaying within our DetailNewsActivity
    public static final String[] NEWS_DETAIL_PROJECTION = {
            NewsContract.NewsEntry.COLUMN_HEADLINE,
            NewsContract.NewsEntry.COLUMN_STORY,
            NewsContract.NewsEntry.COLUMN_STORY_URL,
            NewsContract.NewsEntry.COLUMN_CREATED_AT,
            NewsContract.NewsEntry.COLUMN_IMAGE_URL
    };

    // We store the indices of the values in the array of Strings above to quickly access the data from our query.
    public static final int INDEX_HEADLINE = 0;
    public static final int INDEX_STORY = 1;
    public static final int INDEX_STORY_URL = 2;
    public static final int INDEX_CREATED_AT = 3;
    public static final int INDEX_IMAGE_URL = 4;


    // This ID will be used to identify the Loader responsible for loading the story
    private static final int ID_DETAIL_LOADER = 353;

    // The URI that is used to access the news details
    private Uri mUri;

    ImageView backArrow;
    ImageView shareIcon;
    private String detailHeadline;
    private String detailStoryUrl;

    // Declaration for an ActivityDetailNewsBinding field called mActivityDetailNewsBinding
    private ActivityDetailNewsBinding mActivityDetailNewsBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_news);

        Toolbar topToolbar = findViewById(R.id.detail_news_toolbar);
        setSupportActionBar(topToolbar);

        // Instantiate mActivityDetailNewsBinding using DataBindingUtil
        mActivityDetailNewsBinding = DataBindingUtil.setContentView(this, R.layout.activity_detail_news);

        mUri = getIntent().getData();
        if (mUri == null) throw new NullPointerException("URI for DetailActivity cannot be null");

        // This connects our Activity into the loader lifecycle.
        getSupportLoaderManager().initLoader(ID_DETAIL_LOADER, null, this);

        // Set up a listener for the back button in the tool-bar
        backArrow = findViewById(R.id.icon_arrow_back);
        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // Set up a listener for the share icon in the tool-bar
        shareIcon = findViewById(R.id.icon_share);
        shareIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_SEND);
                intent.putExtra(Intent.EXTRA_SUBJECT, detailHeadline);
                intent.putExtra(Intent.EXTRA_TEXT,
                        getString(R.string.activity_name) + " - " + detailHeadline + "\n" + detailStoryUrl);
                intent.setType("text/plain");
                startActivity(Intent.createChooser(intent, "Share with"));
            }
        });
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

        // Read the headline, story, storyUrl, date and image url from the cursor
        detailHeadline = data.getString(INDEX_HEADLINE);
        String detailStory = data.getString(INDEX_STORY);
        detailStoryUrl = data.getString(INDEX_STORY_URL);
        long detailCreated_At = data.getLong(INDEX_CREATED_AT);
        String detailImage = data.getString(DetailNewsActivity.INDEX_IMAGE_URL);


        // Load the image using the given url
        if (detailImage.isEmpty()) {//url.isEmpty()
            Picasso.get()
                    .load(R.color.colorPrimary)
                    .placeholder(R.color.colorPrimary)
                    .resize(126, 78)
                    .centerCrop()
                    .into(mActivityDetailNewsBinding.detailImage);
        } else {
            Picasso.get()
                    .load(detailImage)
                    .error(R.color.colorPrimary)
                    .fit()
                    .into(mActivityDetailNewsBinding.detailImage);//this is our ImageView
        }

        // Format the story string from the database
        String formattedDetailStory = detailStory.replace("[", "");
        formattedDetailStory = formattedDetailStory.replace("]", "");
        formattedDetailStory = formattedDetailStory.replace("\"", "");
        formattedDetailStory = formattedDetailStory.replace("\'", "\"");
        formattedDetailStory = formattedDetailStory.replace(",", "");
        formattedDetailStory = formattedDetailStory.replace("^", ",");
        formattedDetailStory = formattedDetailStory.replace("\\n", System.getProperty("line.separator"));

        // Use mActivityDetailNewsBinding to display the data
        mActivityDetailNewsBinding.detailHeadline.setText(detailHeadline);


        // Get current time in milliseconds
        long currentDetailsDateLong = System.currentTimeMillis();

        // Convert time to relative time and add it to text view
        CharSequence relativeDetailDate = DateUtils.getRelativeTimeSpanString(detailCreated_At, currentDetailsDateLong,
                0L, DateUtils.FORMAT_ABBREV_ALL);
        mActivityDetailNewsBinding.detailCreatedAt.setText(relativeDetailDate);

        mActivityDetailNewsBinding.detailStory.setText(formattedDetailStory);
        mActivityDetailNewsBinding.detailCopyright.setText(getString(R.string.detail_copyright));
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    }

}


