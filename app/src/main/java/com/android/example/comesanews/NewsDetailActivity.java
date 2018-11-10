package com.android.example.comesanews;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebView;

public class NewsDetailActivity extends AppCompatActivity {

    private String mNewsLink;
    private WebView mWebView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_detail);

        mWebView = (WebView) findViewById(R.id.webview_news);

        Intent intentThatStartedThisActivity = getIntent();

        // Display the contents news link that was passed from NewsActivity
        if (intentThatStartedThisActivity != null) {
            if (intentThatStartedThisActivity.hasExtra(Intent.EXTRA_TEXT)) {
                mNewsLink = intentThatStartedThisActivity.getStringExtra(Intent.EXTRA_TEXT);
                mWebView.loadUrl(mNewsLink);
            }
        }
    }
}
