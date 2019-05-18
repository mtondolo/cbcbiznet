package com.android.example.comesapp.sync;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;

public class EventSyncUtils {
    public static void startImmediateSync(@NonNull final Context context) {
        Intent intentToSyncImmediately = new Intent(context, EventSyncIntentService.class);
        context.startService(intentToSyncImmediately);
    }
}
