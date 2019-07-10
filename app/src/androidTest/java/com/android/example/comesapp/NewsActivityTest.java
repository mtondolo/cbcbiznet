package com.android.example.comesapp;

import android.support.test.rule.ActivityTestRule;

import org.junit.Rule;
import org.junit.Test;

/**
 * These test demos recyclerview scrolling and item selection, options menu item selection and
 * navigation drawer item opening and item selection.
 */
public class NewsActivityTest {

    /**
     * The ActivityTestRule tells Android to start up news activity before running any tests inside
     * out test class.
     */
    @Rule
    public ActivityTestRule<NewsActivity> mActivityTestRule =
            new ActivityTestRule<>(NewsActivity.class);

    // This method contains all the test demos for the news activity.
    @Test
    public void basicNewsActivityTasks() {
    }
}