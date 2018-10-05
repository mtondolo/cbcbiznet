package com.android.example.comesanews;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class LatestNewsActivity extends AppCompatActivity {

    private TextView mLatestNewsListTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_latest_news);

        // Using findViewById, we get a reference to our TextView from xml.
        mLatestNewsListTextView = (TextView) findViewById(R.id.tv_latest_news);

        // This String array contains news items.
        String[] latestNewsItems = LatestNews.getLatestNewsItem();

        // Iterate through the array and append the Strings to the TextView
        for (String latestNewsItem : latestNewsItems) {
            mLatestNewsListTextView.append(latestNewsItem + "\n\n\n");
        }
    }
}
