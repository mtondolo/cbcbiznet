package com.android.example.comesapp.sync;

import android.app.IntentService;
import android.content.Intent;

public class NewsRefreshIntentService extends IntentService {

    // Creates an IntentService.  Invoked by our subclass's constructor.
    public NewsRefreshIntentService() {
        super("NewsRefreshIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        NewsSyncTask.refreshNews(this);
    }
}