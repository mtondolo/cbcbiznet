package com.android.example.comesapp.sync;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;

public class NewsRefreshUtils {

    public static void startImmediateRefresh(@NonNull final Context context) {
        Intent intentToRefreshImmediately = new Intent(context, NewsRefreshIntentService.class);
        context.startService(intentToRefreshImmediately);
    }
}