package com.android.example.comesapp.sync;

import android.app.IntentService;
import android.content.Intent;


public class EventsRefreshIntentService extends IntentService {

    // Creates an IntentService.  Invoked by our subclass's constructor.
    public EventsRefreshIntentService() {
        super("EventsRefreshIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        EventSyncTask.refreshEvents(this);
    }
}
