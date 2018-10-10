package com.android.example.comesanews.sync;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;

public class NewsSyncUtils {
    public static void startImmediateSync(@NonNull final Context context) {
        Intent intentToSyncImmediately = new Intent(context, NewsSyncIntentService.class);
        context.startService(intentToSyncImmediately);
    }
}
