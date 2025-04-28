package com.uniandes.vinilos.ui.artists

import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import androidx.recyclerview.widget.RecyclerView
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers.Visibility
import androidx.test.espresso.matcher.ViewMatchers.hasMinimumChildCount
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withEffectiveVisibility
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.uniandes.vinilos.R
import com.uniandes.vinilos.ui.MainActivity
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class ArtistListFragmentTest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    private lateinit var activityScenario: ActivityScenario<MainActivity>
    private val timeoutInSeconds = 10L

    @Before
    fun setup() {
        hiltRule.inject()
        activityScenario = ActivityScenario.launch(MainActivity::class.java)

        // Select visitor role and continue
        onView(withId(R.id.visitorRole)).perform(click())
        onView(withId(R.id.continueButton)).perform(click())

        // Wait for navigation to complete
        Thread.sleep(1000)

        // Click on Albums in bottom navigation
        onView(withId(R.id.navigation_artists)).perform(click())

        // Wait for the albums to load
        Thread.sleep(timeoutInSeconds * 1000)
    }

    @After
    fun cleanup() {
        activityScenario.close()
    }

    @Test
    fun testArtistListFragment_UIElements() {
        // After loading, progress bar should be gone and RecyclerView visible
        onView(withId(R.id.loadingProgressBar))
            .check(matches(withEffectiveVisibility(Visibility.GONE)))
        onView(withId(R.id.artistsRecyclerView))
            .check(matches(isDisplayed()))
    }

    @Test
    fun testArtistListFragment_AlbumList() {
        // Verify that the RecyclerView has at least one item
        onView(withId(R.id.artistsRecyclerView))
            .check(matches(hasMinimumChildCount(1)))
    }

    @Test
    fun testArtistListFragment_ItemClick() {
        // Wait for the artist to load
        Thread.sleep(timeoutInSeconds * 500)

        // Click on the first item in the RecyclerView
        onView(withId(R.id.artistsRecyclerView))
            .perform(RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(0, click()))
        Thread.sleep(timeoutInSeconds * 500)

        // Verify we're on the detail screen by checking the title
        onView(withId(R.id.artistNameTextView))
            .check(matches(isDisplayed()))
        Thread.sleep(timeoutInSeconds * 500)

    }
}