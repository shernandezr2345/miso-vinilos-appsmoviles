package com.uniandes.vinilos.ui.artists

import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.uniandes.vinilos.R
import com.uniandes.vinilos.ui.MainActivity
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import android.view.View
import com.google.android.material.textfield.TextInputLayout


@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class AddArtistFragmentTest {
    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    private lateinit var activityScenario: ActivityScenario<MainActivity>
    private val timeoutInSeconds = 10L
    private lateinit var decorView: View

    @Before
    fun setup() {
        hiltRule.inject()
        activityScenario = ActivityScenario.launch(MainActivity::class.java)

        // Store the decorView for toast verification
        activityScenario.onActivity { activity ->
            decorView = activity.window.decorView
        }

        // Select collector role and continue
        onView(withId(R.id.collectorRole)).perform(click())
        onView(withId(R.id.continueButton)).perform(click())

        // Wait for navigation to complete
        Thread.sleep(1000)

        // Click on Artists in bottom navigation
        onView(withId(R.id.navigation_artists)).perform(click())

        // Wait for artists to load
        Thread.sleep(timeoutInSeconds * 500)

        // Click on Add Artist button (assuming there's a FAB or menu item for adding artists)
        onView(withId(R.id.addArtistFab)).perform(click())

        // Wait for form to load
        Thread.sleep(1000)
    }

    @Test
    fun testAddArtistFragment_FormIsDisplayed() {
        // Check that all form fields are displayed
        onView(withId(R.id.nameEditText))
            .check(matches(isDisplayed()))
        onView(withId(R.id.imageUrlEditText))
            .check(matches(isDisplayed()))
        onView(withId(R.id.birthDateEditText))
            .check(matches(isDisplayed()))
        onView(withId(R.id.descriptionEditText))
            .check(matches(isDisplayed()))
        onView(withId(R.id.saveButton))
            .check(matches(isDisplayed()))
    }

    @Test
    fun testAddArtistFragment_EmptyNameValidation() {
        // Try to save with empty name
        onView(withId(R.id.nameEditText)).perform(clearText())
        onView(withId(R.id.imageUrlEditText)).perform(clearText(), typeText("https://example.com/image.jpg"))
        onView(withId(R.id.birthDateEditText)).perform(clearText(), typeText("1990-01-01"))
        onView(withId(R.id.descriptionEditText)).perform(clearText(), typeText("Test description"))

        // Close keyboard and click save
        onView(withId(R.id.descriptionEditText)).perform(closeSoftKeyboard())
        onView(withId(R.id.saveButton)).perform(click())

        Thread.sleep(1000)

        // Check if error is shown on the TextInputLayout
        onView(withId(R.id.nameTextInputLayout))
            .check(matches(hasTextInputLayoutErrorText("El nombre del artista es requerido")))
    }

    @Test
    fun testAddArtistFragment_EmptyImageUrlValidation() {
        // Try to save with empty image URL
        onView(withId(R.id.nameEditText)).perform(clearText(), typeText("Test Artist"))
        onView(withId(R.id.imageUrlEditText)).perform(clearText())
        onView(withId(R.id.birthDateEditText)).perform(clearText(), typeText("1990-01-01"))
        onView(withId(R.id.descriptionEditText)).perform(clearText(), typeText("Test description"))

        // Close keyboard and click save
        onView(withId(R.id.descriptionEditText)).perform(closeSoftKeyboard())
        onView(withId(R.id.saveButton)).perform(click())

        // Check if error is shown on the TextInputLayout
        onView(withId(R.id.imageUrlTextInputLayout))
            .check(matches(hasTextInputLayoutErrorText("La URL de la imagen es requerida")))
    }

    @Test
    fun testAddArtistFragment_EmptyBirthDateValidation() {
        // Try to save with empty birth date
        onView(withId(R.id.nameEditText)).perform(clearText(), typeText("Test Artist"))
        onView(withId(R.id.imageUrlEditText)).perform(clearText(), typeText("https://example.com/image.jpg"))
        onView(withId(R.id.birthDateEditText)).perform(clearText())
        onView(withId(R.id.descriptionEditText)).perform(clearText(), typeText("Test description"))

        // Close keyboard and click save
        onView(withId(R.id.descriptionEditText)).perform(closeSoftKeyboard())
        onView(withId(R.id.saveButton)).perform(click())

        // Check if error is shown on the TextInputLayout
        onView(withId(R.id.birthDateTextInputLayout))
            .check(matches(hasTextInputLayoutErrorText("La fecha de nacimiento es requerida")))
    }

    @Test
    fun testAddArtistFragment_EmptyDescriptionValidation() {
        // Try to save with empty description
        onView(withId(R.id.nameEditText)).perform(clearText(), typeText("Test Artist"))
        onView(withId(R.id.imageUrlEditText)).perform(clearText(), typeText("https://example.com/image.jpg"))
        onView(withId(R.id.birthDateEditText)).perform(clearText(), typeText("1990-01-01"))
        onView(withId(R.id.descriptionEditText)).perform(clearText())

        // Close keyboard and click save
        onView(withId(R.id.nameEditText)).perform(closeSoftKeyboard())
        onView(withId(R.id.saveButton)).perform(click())

        // Check if error is shown on the TextInputLayout
        onView(withId(R.id.descriptionTextInputLayout))
            .check(matches(hasTextInputLayoutErrorText("La descripción es requerida")))
    }

    @Test
    fun testAddArtistFragment_SuccessfulSubmission() {
        // Fill all fields with valid data
        onView(withId(R.id.nameEditText)).perform(clearText(), typeText("Nuevo Artista de Prueba"))
        onView(withId(R.id.imageUrlEditText)).perform(clearText(), typeText("https://example.com/artist.jpg"))
        onView(withId(R.id.birthDateEditText)).perform(clearText(), typeText("1985-03-15"))
        onView(withId(R.id.descriptionEditText)).perform(clearText(), typeText("Test description"))

        // Close keyboard and click save
        onView(withId(R.id.descriptionEditText)).perform(closeSoftKeyboard())
        onView(withId(R.id.saveButton)).perform(click())

        // Wait for the save operation to complete (assumes navigation back happens on success)
        Thread.sleep(timeoutInSeconds * 500)

        // Now we should be back on the artists list, verify we're there by checking for the recycler view
        onView(withId(R.id.artistsRecyclerView))
            .check(matches(isDisplayed()))
    }

    /**
     * Custom matcher for TextInputLayout error text
     */
    private fun hasTextInputLayoutErrorText(expectedErrorText: String) = object : org.hamcrest.TypeSafeMatcher<View>() {
        override fun describeTo(description: org.hamcrest.Description) {
            description.appendText("with error text: $expectedErrorText")
        }

        override fun matchesSafely(view: View): Boolean {
            if (view !is TextInputLayout) return false
            val error = view.error ?: return false
            return error.toString() == expectedErrorText
        }
    }

}