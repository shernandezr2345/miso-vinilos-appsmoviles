package com.uniandes.vinilos.ui.albums

import com.uniandes.vinilos.R
import com.uniandes.vinilos.ui.BaseAccessibilityTest
import org.junit.Test
import dagger.hilt.android.testing.HiltAndroidTest

@HiltAndroidTest
class AlbumListAccessibilityTest : BaseAccessibilityTest() {

    @Test
    fun testSearchBarAccessibility() {
        checkEditTextAccessibility(R.id.searchEditText, "Busca Álbumes")
    }

    @Test
    fun testAddAlbumButtonAccessibility() {
        checkButtonAccessibility(R.id.addAlbumButton, "Add new album")
    }

    @Test
    fun testAlbumListAccessibility() {
        checkRecyclerViewAccessibility(R.id.albumsRecyclerView, "List of albums")
    }

    @Test
    fun testLoadingIndicatorAccessibility() {
        checkLoadingIndicatorAccessibility(R.id.loadingProgressBar, "Loading albums")
    }

    @Test
    fun testErrorViewAccessibility() {
        checkErrorViewAccessibility(R.id.errorTextView, "Error message")
    }
} 