package com.android.example.comesapp;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.android.example.comesapp.utils.JsonUtils;
import com.android.example.comesapp.utils.NetworkUtils;

import java.net.URL;

public class EventsActivity extends AppCompatActivity {

    private TextView mEventsTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_events);

        // Using findViewById, we get a reference to our TextView from xml.
        mEventsTextView = findViewById(R.id.tv_events);

        // Once all of our views are setup, we can load the events.
        loadEventsData();
    }

    // This method will tell some background method to get events data in the background.
    private void loadEventsData() {
        new FetchEventsTask().execute();
    }

    public class FetchEventsTask extends AsyncTask<String, Void, String> {
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
            if (eventsData != null) {
                // Append the Strings to the TextView.
                mEventsTextView.append((eventsData) + "\n\n\n");
            }
        }
    }
}
