package com.uniandes.vinilos.ui.collectors

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import com.uniandes.vinilos.R
import com.uniandes.vinilos.ui.BaseAccessibilityTest
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Test

@HiltAndroidTest
class CollectorsListAccessibilityTest : BaseAccessibilityTest() {

    @Test
    fun testSearchBarAccessibility() {
        // Navigate to Collectors screen using bottom navigation
        onView(withId(R.id.navigation_collectors)).perform(click())
        
        // Check search bar accessibility by hint
        onView(withId(R.id.searchEditText))
            .check(matches(withHint("Busca Coleccionistas")))
            .check(matches(isDisplayed()))
    }

    @Test
    fun testCollectorsListAccessibility() {
        // Navigate to Collectors screen using bottom navigation
        onView(withId(R.id.navigation_collectors)).perform(click())
        
        // Check recycler view accessibility
        onView(withId(R.id.collectors_recycler_view))
            .check(matches(hasContentDescription()))
            .check(matches(isDisplayed()))
            .check(matches(isFocusable()))
    }
} 