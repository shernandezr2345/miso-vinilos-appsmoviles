package com.uniandes.vinilos.ui.album

import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.navigation.Navigation
import androidx.navigation.testing.TestNavHostController
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.uniandes.vinilos.R
import com.uniandes.vinilos.data.dao.AlbumDao
import com.uniandes.vinilos.data.repository.AlbumRepository
import com.uniandes.vinilos.models.*
import com.uniandes.vinilos.viewmodel.AlbumListViewModel
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers.not
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.`when`
import org.mockito.kotlin.mock

@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class AlbumListFragmentTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    private lateinit var navController: TestNavHostController
    private val mockAlbumDao: AlbumDao = mock()
    private val mockRepository: AlbumRepository = mock()
    private val mockViewModel: AlbumListViewModel = mock()
    private val albumsFlow = MutableStateFlow<List<Album>>(emptyList())

    private val testAlbums = listOf(
        Album(
            id = 1,
            name = "Test Album 1",
            cover = "http://example.com/cover1.jpg",
            releaseDate = "2023-01-01",
            description = "Test Description 1",
            genre = "Rock",
            recordLabel = "Test Label 1",
            tracks = emptyList(),
            comments = emptyList(),
            performers = emptyList()
        ),
        Album(
            id = 2,
            name = "Test Album 2",
            cover = "http://example.com/cover2.jpg",
            releaseDate = "2023-02-01",
            description = "Test Description 2",
            genre = "Pop",
            recordLabel = "Test Label 2",
            tracks = emptyList(),
            comments = emptyList(),
            performers = emptyList()
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
            `when`(mockRepository.getAllAlbums()).thenReturn(testAlbums)
            albumsFlow.value = testAlbums
            `when`(mockViewModel.albums).thenReturn(albumsFlow)
        }
    }

    @Test
    fun testAlbumListDisplayed() {
        // Launch fragment
        val scenario = launchFragmentInContainer<AlbumListFragment>()
        
        // Verify albums are displayed
        onView(withId(R.id.albumsRecyclerView))
            .check(matches(isDisplayed()))
        
        // Verify album items are displayed
        onView(withText("Test Album 1"))
            .check(matches(isDisplayed()))
        onView(withText("Test Album 2"))
            .check(matches(isDisplayed()))
    }

    @Test
    fun testAlbumClickNavigation() {
        // Launch fragment
        val scenario = launchFragmentInContainer<AlbumListFragment>()
        
        // Click on first album
        onView(withText("Test Album 1"))
            .perform(click())
        
        // Verify navigation to detail screen
        assert(navController.currentDestination?.id == R.id.albumDetailFragment)
    }

    @Test
    fun testSearchFunctionality() {
        // Launch fragment
        val scenario = launchFragmentInContainer<AlbumListFragment>()
        
        // Type in search box
        onView(withId(R.id.searchEditText))
            .perform(typeText("Test Album 1"))
        
        // Verify only matching album is displayed
        onView(withText("Test Album 1"))
            .check(matches(isDisplayed()))
        onView(withText("Test Album 2"))
            .check(matches(not(isDisplayed())))
    }

    @Test
    fun testAddAlbumFabVisibilityForCollector() {
        // Launch fragment
        val scenario = launchFragmentInContainer<AlbumListFragment>()
        
        // Verify FAB is displayed for collectors
        onView(withId(R.id.addAlbumFab))
            .check(matches(isDisplayed()))
    }

    @Test
    fun testAddAlbumFabVisibilityForNonCollector() {
        // Launch fragment
        val scenario = launchFragmentInContainer<AlbumListFragment>()
        
        // Verify FAB is not displayed for non-collectors
        onView(withId(R.id.addAlbumFab))
            .check(matches(not(isDisplayed())))
    }
} 