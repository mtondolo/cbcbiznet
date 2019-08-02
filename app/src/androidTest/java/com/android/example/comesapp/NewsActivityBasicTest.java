package com.android.example.comesapp;

import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.rule.ActivityTestRule;

import org.junit.Rule;
import org.junit.Test;

import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

/**
 * These test demos recylerview scrolling and item selection, options menu item selection and
 * navigation drawer item opening and item selection.
 */
public class NewsActivityBasicTest {

    public static final String NEWS_HEADLINE = "COMESA, UNCTAD Launch Deal to Establish " +
            "Regional Trade Information Portals";

    /**
     * The ActivityTestRule tells Android to start up news activity before running any tests inside
     * out test class.
     */
    @Rule
    public ActivityTestRule<NewsActivity> mActivityTestRule =
            new ActivityTestRule<>(NewsActivity.class);

    // This method tests the item selection of recylerview items.
    @Test
    public void clickRecylerviewItem_OpenDetailNewsActivity() {

        // Scrolls to a given position in the recylerview and selects a given item.
        onView(withId(R.id.recyclerview_news)).perform(RecyclerViewActions
                .actionOnItemAtPosition(0, click()));

        // Checks that the DetailNewsActivity opens with the correct headline displayed.
        onView(withId(R.id.detail_headline)).check(matches(withText(NEWS_HEADLINE)));
    }
}