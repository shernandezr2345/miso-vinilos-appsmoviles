package com.uniandes.vinilos.ui.albums


import androidx.recyclerview.widget.RecyclerView
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.uniandes.vinilos.R
import com.uniandes.vinilos.ui.MainActivity
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import androidx.test.espresso.matcher.ViewMatchers.Visibility

@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class AlbumListFragmentTest {

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
        
        // Wait for the albums to load
        Thread.sleep(timeoutInSeconds * 1000)
    }

    @After
    fun cleanup() {
        activityScenario.close()
    }

    @Test
    fun testAlbumListFragment_UIElements() {
        // After loading, progress bar should be gone and RecyclerView visible
        onView(withId(R.id.loadingProgressBar))
            .check(matches(withEffectiveVisibility(Visibility.GONE)))
        onView(withId(R.id.albumsRecyclerView))
            .check(matches(isDisplayed()))
    }

    @Test
    fun testAlbumListFragment_AlbumList() {
        // Verify that the RecyclerView has at least one item
        onView(withId(R.id.albumsRecyclerView))
            .check(matches(hasMinimumChildCount(1)))
    }

    @Test
    fun testAlbumListFragment_ItemClick() {
        // Wait for the albums to load
        Thread.sleep(timeoutInSeconds * 500)
        
        // Click on the first item in the RecyclerView
        onView(withId(R.id.albumsRecyclerView))
            .perform(RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(0, click()))
        Thread.sleep(timeoutInSeconds * 500)

        // Verify we're on the detail screen by checking the title
        onView(withId(R.id.albumTitleTextView))
            .check(matches(isDisplayed()))
        Thread.sleep(timeoutInSeconds * 500)

    }
} 