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
public class NewsActivityTest {

    /**
     * The ActivityTestRule tells Android to start up news activity before running any tests inside
     * out test class.
     */
    @Rule
    public ActivityTestRule<NewsActivity> mActivityTestRule =
            new ActivityTestRule<>(NewsActivity.class);

    // This method tests the item selection of recylerview items.
    @Test
    public void selectRecylerviewItem() {

        // Scroll to a given position in a recylerview and select a given item.
        onView(withId(R.id.recyclerview_news)).perform(RecyclerViewActions
                .actionOnItemAtPosition(0, click()));

        String headline = "COMESA, UNCTAD Launch Deal to Establish Regional Trade Information Portals";
        String imageDescription = "COMESA SG Ms Chileshe Kapwewe with UNCTAD SG Dr Mukhisa Kituyi " +
                "during the latters visits to the COMES Secretarits on May 2019";
        String footerText = "Copyright © 2019 COMESA";

        // Get the reference for the textviews and check that they contain the correct text
        onView(withId(R.id.detail_headline)).check(matches(withText(headline)));
        onView(withId(R.id.image_description)).check(matches(withText(imageDescription)));
        onView(withId(R.id.detail_copyright)).check(matches(withText(footerText)));
    }
}