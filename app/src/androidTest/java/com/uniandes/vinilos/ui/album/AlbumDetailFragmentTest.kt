package com.uniandes.vinilos.ui.album

import android.os.Bundle
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.navigation.Navigation
import androidx.navigation.testing.TestNavHostController
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.pressBack
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.uniandes.vinilos.R
import com.uniandes.vinilos.data.dao.AlbumDao
import com.uniandes.vinilos.data.repository.AlbumRepository
import com.uniandes.vinilos.models.*
import com.uniandes.vinilos.viewmodel.AlbumViewModel
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.`when`
import org.mockito.kotlin.mock

@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class AlbumDetailFragmentTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    private lateinit var navController: TestNavHostController
    private val mockAlbumDao: AlbumDao = mock()
    private val mockRepository: AlbumRepository = mock()
    private val mockViewModel: AlbumViewModel = mock()
    private val selectedAlbumFlow = MutableStateFlow<Album?>(null)

    private val testAlbum = Album(
        id = 1,
        name = "Test Album",
        cover = "http://example.com/cover.jpg",
        releaseDate = "2023-01-01",
        description = "Test Description",
        genre = "Rock",
        recordLabel = "Test Label",
        tracks = emptyList(),
        comments = emptyList(),
        performers = listOf(
            Performer(
                id = 1,
                name = "Test Performer",
                image = "http://example.com/performer.jpg",
                description = "Test Performer Description",
                birthDate = "1990-01-01"
            )
        )
    )

    @Before
    fun setup() {
        hiltRule.inject()
        
        // Setup navigation
        navController = TestNavHostController(ApplicationProvider.getApplicationContext())
        navController.setGraph(R.navigation.nav_graph)

        // Mock repository and ViewModel behavior
        runBlocking {
            `when`(mockRepository.getAlbumById(1)).thenReturn(testAlbum)
            selectedAlbumFlow.value = testAlbum
            `when`(mockViewModel.selectedAlbum).thenReturn(selectedAlbumFlow)
        }
    }

    @Test
    fun testAlbumDetailsDisplayed() {
        // Launch fragment with test album ID
        val scenario = launchFragmentInContainer<AlbumDetailFragment>(
            fragmentArgs = Bundle().apply {
                putInt("albumId", 1)
            }
        )
        
        // Verify album details are displayed
        onView(withId(R.id.albumTitleTextView))
            .check(matches(withText("Test Album")))
        onView(withId(R.id.albumDescriptionTextView))
            .check(matches(withText("Test Description")))
        onView(withId(R.id.albumGenreTextView))
            .check(matches(withText("Rock")))
    }

    @Test
    fun testPerformersListDisplayed() {
        // Launch fragment with test album ID
        val scenario = launchFragmentInContainer<AlbumDetailFragment>(
            fragmentArgs = Bundle().apply {
                putInt("albumId", 1)
            }
        )
        
        // Verify performers are displayed
        onView(withId(R.id.performersRecyclerView))
            .check(matches(isDisplayed()))
        onView(withText("Test Performer"))
            .check(matches(isDisplayed()))
    }

    @Test
    fun testBackNavigation() {
        // Launch fragment with test album ID
        val scenario = launchFragmentInContainer<AlbumDetailFragment>(
            fragmentArgs = Bundle().apply {
                putInt("albumId", 1)
            }
        )
        
        // Press back
        pressBack()
        
        // Verify navigation back to list
        assert(navController.currentDestination?.id == R.id.navigation_albums)
    }
} 