package com.uniandes.vinilos.ui.artists

import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import androidx.recyclerview.widget.RecyclerView
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.scrollTo
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
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class ArtistDetailFragmentTest {
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

        // Wait for albums to load and click first album
        Thread.sleep(timeoutInSeconds * 500)
        onView(withId(R.id.artistsRecyclerView))
            .perform(RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(0, click()))

        // Wait for detail to load
        Thread.sleep(timeoutInSeconds * 500)
    }

    @Test
    fun testArtistDetailFragment_LoadingState() {
        // Initially loading indicator should be gone after our wait
        onView(withId(R.id.loadingProgressBar))
            .check(matches(withEffectiveVisibility(Visibility.GONE)))

        // Content should be visible
        onView(withId(R.id.contentLayout))
            .check(matches(isDisplayed()))
    }

    @Test
    fun testArtistDetailFragment_BasicInfo() {
        // Check that all basic info views are displayed
        onView(withId(R.id.artistNameTextView))
            .check(matches(isDisplayed()))
        onView(withId(R.id.birthDateTextView))
            .check(matches(isDisplayed()))
        onView(withId(R.id.biographyTextView))
            .check(matches(isDisplayed()))
    }

    @Test
    fun testArtistDetailFragment_AlbumList() {
        // Check that tracks RecyclerView is displayed
        onView(withId(R.id.albumsRecyclerView))
            .perform(scrollTo())
            .check(matches(isDisplayed()))
            .check(matches(hasMinimumChildCount(1)))
    }

    @Test
    fun testArtistDetailFragment_PerformersPrizeList() {
        // Check that performers RecyclerView is displayed
        onView(withId(R.id.performersPrizesRecyclerView))
            .perform(scrollTo())
            .check(matches(isDisplayed()))
            .check(matches(hasMinimumChildCount(1)))
    }
}