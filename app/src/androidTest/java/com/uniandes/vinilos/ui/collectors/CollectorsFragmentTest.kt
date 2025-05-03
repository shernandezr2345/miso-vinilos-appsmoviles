package com.uniandes.vinilos.ui.collectors

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
class CollectorsFragmentTest {

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
        Thread.sleep(1000)
        // Click on Collectors in bottom navigation
        onView(withId(R.id.navigation_collectors)).perform(click())
        Thread.sleep(timeoutInSeconds * 1000)
    }

    @After
    fun cleanup() {
        activityScenario.close()
    }

    @Test
    fun testCollectorsFragment_UIElements() {
        onView(withId(R.id.collectors_recycler_view))
            .check(matches(isDisplayed()))
    }

    @Test
    fun testCollectorsFragment_CollectorList() {
        onView(withId(R.id.collectors_recycler_view))
            .check(matches(hasMinimumChildCount(1)))
    }

//    @Test
//    fun testCollectorsFragment_ItemClick() {
//        // Optionally click on the first collector (if you have a detail screen, add checks here)
//        onView(withId(R.id.collectors_recycler_view))
//            .perform(RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(0, click()))
//        // Add assertions here if clicking opens a detail view
//        Thread.sleep(1000)
//    }
} 