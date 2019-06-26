package com.android.example.comesapp.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.android.example.comesapp.R;

public class NewsPreferences {

    // Returns true if the user prefers to see notifications from COMESANews, false otherwise.
    public static boolean areNotificationsEnabled(Context context) {
        String displayNotificationsKey = context.getString(R.string.pref_enable_notifications_key);
        boolean shouldDisplayNotificationsByDefault = context
                .getResources()
                .getBoolean(R.bool.show_notifications_by_default);
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        boolean shouldDisplayNotifications = sp
                .getBoolean(displayNotificationsKey, shouldDisplayNotificationsByDefault);
        return shouldDisplayNotifications;
    }

    // Returns the last time that a notification was shown (in UNIX time)
    public static long getLastNotificationTimeInMillis(Context context) {
        String lastNotificationKey = context.getString(R.string.pref_last_notification);
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        long lastNotificationTime = sp.getLong(lastNotificationKey, 0);
        return lastNotificationTime;
    }

    // Returns the elapsed time in milliseconds since the last notification was shown.
    public static long getElapsedTimeSinceLastNotification(Context context) {
        long lastNotificationTimeMillis =
                NewsPreferences.getLastNotificationTimeInMillis(context);
        long timeSinceLastNotification = System.currentTimeMillis() - lastNotificationTimeMillis;
        return timeSinceLastNotification;
    }

    // Saves the time that a notification is shown.
    public static void saveLastNotificationTime(Context context, long timeOfNotification) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sp.edit();
        String lastNotificationKey = context.getString(R.string.pref_last_notification);
        editor.putLong(lastNotificationKey, timeOfNotification);
        editor.apply();
    }

    // Returns the headline for the last notification.
    public static String getLastNotificationHeadline(Context context) {
        String lastNotificationKey = context.getString(R.string.pref_last_notification_flag);
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        String lastNotificationHeadline = sp.getString(lastNotificationKey, "");
        return lastNotificationHeadline;
    }

    // Saves the headline for the the last notification.
    public static void saveLastNotificationHeadline(Context context, String lastNotificationHeadline) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sp.edit();
        String lastNotificationKey = context.getString(R.string.pref_last_notification_flag);
        editor.putString(lastNotificationKey, lastNotificationHeadline);
        editor.apply();
    }

    // Returns the title for the last event title
    public static String getLastEventTitle(Context context) {
        String lastNotificationKey = context.getString(R.string.pref_last_event_title_flag);
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        String lastEventTitle = sp.getString(lastNotificationKey, "");
        return lastEventTitle;
    }

    // Saves the title for the the last event.
    public static void saveLastEventTitle(Context context, String lastEventTitle) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sp.edit();
        String lastEventTitleKey = context.getString(R.string.pref_last_event_title_flag);
        editor.putString(lastEventTitleKey, lastEventTitle);
        editor.apply();
    }

}

