package com.uniandes.vinilos.ui

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.Rule
import org.junit.runner.RunWith
import com.uniandes.vinilos.ui.MainActivity
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.matcher.ViewMatchers.withId
import com.uniandes.vinilos.R

@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
abstract class BaseAccessibilityTest {
    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val activityRule = ActivityScenarioRule(MainActivity::class.java)

    @Before
    fun setup() {
        hiltRule.inject()
        // Select visitor role and continue
        onView(withId(R.id.visitorRole)).perform(click())
        onView(withId(R.id.continueButton)).perform(click())
    }

    protected fun checkViewAccessibility(viewId: Int, contentDescription: String) {
        onView(withId(viewId))
            .check(matches(hasContentDescription()))
            .check(matches(isEnabled()))
            .check(matches(isFocusable()))
    }

    protected fun checkImageViewAccessibility(viewId: Int, contentDescription: String) {
        onView(withId(viewId))
            .check(matches(hasContentDescription()))
            .check(matches(isDisplayed()))
    }

    protected fun checkRecyclerViewAccessibility(viewId: Int, contentDescription: String) {
        onView(withId(viewId))
            .check(matches(hasContentDescription()))
            .check(matches(isDisplayed()))
            .check(matches(isFocusable()))
    }

    protected fun checkButtonAccessibility(viewId: Int, contentDescription: String) {
        onView(withId(viewId))
            .check(matches(hasContentDescription()))
            .check(matches(isEnabled()))
            .check(matches(isFocusable()))
            .check(matches(isClickable()))
    }

    protected fun checkEditTextAccessibility(viewId: Int, expectedHint: String) {
        onView(withId(viewId))
            .check(matches(withHint(expectedHint)))
            .check(matches(isEnabled()))
            .check(matches(isFocusable()))
            .check(matches(isClickable()))
    }

    protected fun checkErrorViewAccessibility(viewId: Int, contentDescription: String) {
        onView(withId(viewId))
            .check(matches(hasContentDescription()))
            .check(matches(isEnabled()))
    }

    protected fun checkLoadingIndicatorAccessibility(viewId: Int, contentDescription: String) {
        onView(withId(viewId))
            .check(matches(hasContentDescription()))
            .check(matches(isEnabled()))
    }
} 