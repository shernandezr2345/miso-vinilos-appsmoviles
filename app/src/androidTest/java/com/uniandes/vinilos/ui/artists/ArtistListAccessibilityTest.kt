package com.uniandes.vinilos.ui.artists

import com.uniandes.vinilos.R
import com.uniandes.vinilos.ui.BaseAccessibilityTest
import org.junit.Test
import dagger.hilt.android.testing.HiltAndroidTest
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.matcher.ViewMatchers.withId

@HiltAndroidTest
class ArtistListAccessibilityTest : BaseAccessibilityTest() {

    private fun goToArtistsScreen() {
        onView(withId(R.id.navigation_artists)).perform(click())
    }

    @Test
    fun testSearchBarAccessibility() {
        goToArtistsScreen()
        checkEditTextAccessibility(R.id.searchEditText, "Busca Artistas")
    }

    @Test
    fun testAddArtistButtonAccessibility() {
        goToArtistsScreen()
        checkButtonAccessibility(R.id.addArtistFab, "Agregar nuevo artista")
    }

    @Test
    fun testArtistListAccessibility() {
        goToArtistsScreen()
        checkRecyclerViewAccessibility(R.id.artistsRecyclerView, "List of artists")
    }

    @Test
    fun testLoadingIndicatorAccessibility() {
        goToArtistsScreen()
        checkLoadingIndicatorAccessibility(R.id.loadingProgressBar, "Loading artists")
    }

    @Test
    fun testErrorViewAccessibility() {
        goToArtistsScreen()
        checkErrorViewAccessibility(R.id.errorTextView, "Error message")
    }
} 