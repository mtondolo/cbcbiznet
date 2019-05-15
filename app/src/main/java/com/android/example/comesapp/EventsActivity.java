package com.android.example.comesapp;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.example.comesapp.utils.JsonUtils;
import com.android.example.comesapp.utils.NetworkUtils;

import java.net.URL;

public class EventsActivity extends AppCompatActivity {

    private TextView mEventsTextView;
    private TextView mErrorMessageDisplay;
    private ProgressBar mLoadingIndicator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_events);

        // Using findViewById, we get a reference to our TextView from xml.
        mEventsTextView = findViewById(R.id.tv_events);

        // This TextView is used to display errors and will be hidden if there are no errors
        mErrorMessageDisplay = (TextView) findViewById(R.id.tv_error_message_display);

        // The ProgressBar that will indicate to the user that we are loading data.
        // It will be hidden when no data is loading.
        mLoadingIndicator = (ProgressBar) findViewById(R.id.pb_loading_indicator);

        // Once all of our views are setup, we can load the events.
        loadEventsData();
    }

    // This method will tell some background method to get events data in the background.
    private void loadEventsData() {
        showLatestNewsDataView();
        new FetchEventsTask().execute();
    }

    // This method will make the View for the events data visible
    // and hide the error message.
    private void showLatestNewsDataView() {
        mErrorMessageDisplay.setVisibility(View.INVISIBLE);
        mEventsTextView.setVisibility(View.VISIBLE);
    }

    public class FetchEventsTask extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mLoadingIndicator.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(String... params) {
            URL eventsRequestUrl = NetworkUtils.buildEventUrl();
            try {
                String jsonEventsResponse = NetworkUtils
                        .getResponseFromHttpUrl(eventsRequestUrl);
                String simpleJsonEventsData = JsonUtils
                        .getEventFromJsonStr(EventsActivity.this, jsonEventsResponse);
                return simpleJsonEventsData;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String eventsData) {
            mLoadingIndicator.setVisibility(View.INVISIBLE);
            if (eventsData != null) {
                showLatestNewsDataView();

                // Append the Strings to the TextView.
                mEventsTextView.append((eventsData) + "\n\n\n");
            } else {
                showErrorMessage();
            }
        }
    }

    // This method will make the error message visible and hide the events View.
    private void showErrorMessage() {
        mEventsTextView.setVisibility(View.INVISIBLE);
        mErrorMessageDisplay.setVisibility(View.VISIBLE);
    }
}

