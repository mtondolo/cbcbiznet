package com.android.example.comesapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class EventsActivity extends AppCompatActivity {

    private TextView mEventsTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_events);

        // Using findViewById, we get a reference to our TextView from xml.
        mEventsTextView = findViewById(R.id.tv_events);

        // This String array contains events.
        String[] events = Events.getEvents();

        // Iterate through the array and append the Strings to the TextView
        for (String event : events) {
            mEventsTextView.append(event + "\n\n\n");
        }
    }
}
