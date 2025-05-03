package com.uniandes.vinilos.ui.albums

import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.recyclerview.widget.RecyclerView
import com.uniandes.vinilos.R
import com.uniandes.vinilos.ui.MainActivity
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class AlbumDetailFragmentTest {

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
        onView(withId(R.id.navigation_albums)).perform(click())
        
        // Wait for albums to load and click first album
        Thread.sleep(timeoutInSeconds * 500)
        onView(withId(R.id.albumsRecyclerView))
            .perform(RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(0, click()))
        
        // Wait for detail to load
        Thread.sleep(timeoutInSeconds * 500)
    }

    @Test
    fun testAlbumDetailFragment_LoadingState() {
        // Initially loading indicator should be gone after our wait
        onView(withId(R.id.loadingProgressBar))
            .check(matches(withEffectiveVisibility(Visibility.GONE)))
        
        // Content should be visible
        onView(withId(R.id.contentLayout))
            .check(matches(isDisplayed()))
    }

    @Test
    fun testAlbumDetailFragment_BasicInfo() {
        // Check that all basic info views are displayed
        onView(withId(R.id.albumCoverImageView))
            .check(matches(isDisplayed()))
        onView(withId(R.id.albumTitleTextView))
            .check(matches(isDisplayed()))
        onView(withId(R.id.albumArtistTextView))
            .check(matches(isDisplayed()))
        onView(withId(R.id.albumGenreTextView))
            .check(matches(isDisplayed()))
        onView(withId(R.id.albumReleaseDateTextView))
            .check(matches(isDisplayed()))
        onView(withId(R.id.albumDescriptionTextView))
            .check(matches(isDisplayed()))
    }

    @Test
    fun testAlbumDetailFragment_TracksList() {
        // Check that tracks RecyclerView is displayed
        onView(withId(R.id.tracksRecyclerView))
            .check(matches(isDisplayed()))
            .check(matches(hasMinimumChildCount(1)))
    }

    @Test
    fun testAlbumDetailFragment_PerformersList() {
        // Check that performers RecyclerView is displayed
        onView(withId(R.id.performersRecyclerView))
            .check(matches(isDisplayed()))
            .check(matches(hasMinimumChildCount(1)))
    }
} 