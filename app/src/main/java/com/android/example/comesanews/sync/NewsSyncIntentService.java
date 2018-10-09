package com.android.example.comesanews.sync;

import android.app.IntentService;
import android.content.Intent;

public class NewsSyncIntentService extends IntentService {

    // Creates an IntentService.  Invoked by our subclass's constructor.
    public NewsSyncIntentService() {
        super("NewsSyncIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        NewsSyncTask.syncNews(this);
    }
}
