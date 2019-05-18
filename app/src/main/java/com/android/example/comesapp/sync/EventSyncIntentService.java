package com.android.example.comesapp.sync;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;

public class EventSyncIntentService extends IntentService {

    //Creates an IntentService.  Invoked by your subclass's constructor.
    public EventSyncIntentService() {
        super("EventSyncIntentService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        EventSyncTask.syncEvent(this);
    }
}
