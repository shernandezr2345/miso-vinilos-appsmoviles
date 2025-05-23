package com.uniandes.vinilos.ui

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.closeSoftKeyboard
import androidx.test.espresso.action.ViewActions.replaceText
import androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withClassName
import androidx.test.espresso.matcher.ViewMatchers.withContentDescription
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.uniandes.vinilos.R
import com.uniandes.vinilos.ui.MainActivity
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.Matchers.allOf
import org.hamcrest.Matchers.`is`
import org.hamcrest.TypeSafeMatcher
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@LargeTest
@HiltAndroidTest   // <-- Agregado aquí
@RunWith(AndroidJUnit4::class)
class SearchArtistTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)   // <-- Agregado aquí

    @get:Rule
    var mActivityScenarioRule = ActivityScenarioRule(MainActivity::class.java)

    @Before
    fun init() {
        hiltRule.inject()   // <-- Agregado aquí
    }

    @Test
    fun searchArtistTest() {
        val materialRadioButton = onView(
            allOf(
                withId(R.id.visitorRole), withText("Visitante"),
                childAtPosition(
                    allOf(
                        withId(R.id.roleRadioGroup),
                        childAtPosition(
                            withClassName(`is`("android.widget.LinearLayout")),
                            2
                        )
                    ),
                    0
                ),
                isDisplayed()
            )
        )
        materialRadioButton.perform(click())

        val materialButton = onView(
            allOf(
                withId(R.id.continueButton), withText("Continuar"),
                childAtPosition(
                    childAtPosition(
                        withId(R.id.nav_host_fragment),
                        0
                    ),
                    3
                ),
                isDisplayed()
            )
        )
        materialButton.perform(click())

        // Remove or comment out the block that references nav_view
        // val bottomNavigationItemView = onView(
        //     allOf(
        //         withId(R.id.navigation_artists), withContentDescription("Artists"),
        //         childAtPosition(
        //             childAtPosition(
        //                 withId(R.id.nav_view),
        //                 0
        //             ),
        //             1
        //         ),
        //         isDisplayed()
        //     )
        // )
        // bottomNavigationItemView.perform(click())

        val appCompatEditText = onView(
            allOf(
                withId(R.id.searchEditText),
                childAtPosition(
                    childAtPosition(
                        withId(R.id.nav_host_fragment),
                        0
                    ),
                    1
                ),
                isDisplayed()
            )
        )
        appCompatEditText.perform(replaceText("Joan"), closeSoftKeyboard())

        val recyclerView = onView(
            allOf(
                withId(R.id.artistsRecyclerView),
                childAtPosition(
                    withClassName(`is`("androidx.constraintlayout.widget.ConstraintLayout")),
                    2
                )
            )
        )
        recyclerView.perform(actionOnItemAtPosition<ViewHolder>(0, click()))

        // Remove or comment out the block that references toolbar
        // val appCompatImageButton = onView(
        //     allOf(
        //         withContentDescription("Navigate up"),
        //         childAtPosition(
        //             allOf(
        //                 withId(R.id.toolbar),
        //                 childAtPosition(
        //                     withClassName(`is`("androidx.constraintlayout.widget.ConstraintLayout")),
        //                     0
        //                 )
        //             ),
        //             2
        //         ),
        //         isDisplayed()
        //     )
        // )
        // appCompatImageButton.perform(click())
    }

    private fun childAtPosition(
        parentMatcher: Matcher<View>, position: Int
    ): Matcher<View> {
        return object : TypeSafeMatcher<View>() {
            override fun describeTo(description: Description) {
                description.appendText("Child at position $position in parent ")
                parentMatcher.describeTo(description)
            }

            public override fun matchesSafely(view: View): Boolean {
                val parent = view.parent
                return parent is ViewGroup && parentMatcher.matches(parent)
                        && view == parent.getChildAt(position)
            }
        }
    }
}
