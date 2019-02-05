package com.android.example.muzpal.sync;

import android.app.IntentService;
import android.content.Intent;

public class ProductSyncIntentService extends IntentService {

    // Creates an IntentService.  Invoked by our subclass's constructor.
    public ProductSyncIntentService() {
        super("ProductSyncIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        ProductSyncTask.syncProduct(this);
    }
}
