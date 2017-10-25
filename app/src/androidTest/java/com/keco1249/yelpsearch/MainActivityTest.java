package com.keco1249.yelpsearch;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.hasDescendant;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static com.keco1249.yelpsearch.RecyclerViewMatcher.withRecyclerView;

/**
 * This is a sample of a basic espresso test. Espresso allows you to interact with the UI and check
 * the results of an interaction. I tend to prefer writing as many unit tests as possible and then
 * covering the basic edge cases in the UI with espresso tests since UI tests tend to take more work
 * to maintain.
 *
 * Another thing to note is that the way this is currently setup we are relying on the device to make
 * actual requests across the network. We know networking is not always reliable and this can cause
 * intermittent test failures. In order to avoid this we can setup our networking clients to return
 * "canned" responses. This can be done a variety of ways such as by using a dependency injection
 * framework such as Dagger. My preferred method is to create a flavor of the app that injects different
 * client implementations with pre-specified response data.
 */
@RunWith(AndroidJUnit4.class)
public class MainActivityTest {

    @Rule
    public ActivityTestRule<MainActivity> mActivityRule =
            new ActivityTestRule<>(MainActivity.class);

    @Test
    public void testSearchResults() throws InterruptedException {
        // Type in search query into edit text
        onView(withId(R.id.searchBarEditText)).perform(typeText("pizza"));

        // Hit search button
        onView(withId(R.id.action_search)).perform(click());

        // We need to sleep because we are relying on the network to return our search result and
        // result images. This is not a great solution because we aren't guaranteed that the api will
        // return our results in under 2 seconds. See explanation at top for a work around to make
        // tests more hermetic by mocking network requests and by doing so it also speeds things up.
        Thread.sleep(2000);

        // Test first item is Ian's pizza. Again, this isn't ideal because Yelp's api could return different
        // results at some point. We would want to return a "canned" response.
        onView(withRecyclerView(R.id.searchResultsRecyclerView).atPosition(0))
                .check(matches(hasDescendant(withText("Ian's Pizza"))));

        // TODO Write more tests to check other items in the recycler view
    }

    // TODO Cover other user flows
}
