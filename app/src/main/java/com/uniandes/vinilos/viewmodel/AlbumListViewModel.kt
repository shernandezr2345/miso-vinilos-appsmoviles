package com.uniandes.vinilos.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.uniandes.vinilos.models.Album
import com.uniandes.vinilos.data.repository.AlbumRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AlbumListViewModel @Inject constructor(
    private val repository: AlbumRepository
) : ViewModel() {
    private val _albums = MutableLiveData<List<Album>>()
    val albums: LiveData<List<Album>> = _albums

    private val _filteredAlbums = MutableLiveData<List<Album>>()
    val filteredAlbums: LiveData<List<Album>> = _filteredAlbums

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    private var allAlbums: List<Album> = emptyList()

    fun loadAlbums() {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                allAlbums = repository.getAllAlbums()
                _albums.value = allAlbums
                _filteredAlbums.value = allAlbums
            } catch (e: Exception) {
                _error.value = e.message ?: "Error loading albums"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun searchAlbums(query: String) {
        if (query.isEmpty()) {
            _filteredAlbums.value = allAlbums
            return
        }

        val searchQuery = query.lowercase().trim()
        _filteredAlbums.value = allAlbums.filter { album ->
            album.name.lowercase().contains(searchQuery) ||
            album.performers.any { it.name.lowercase().contains(searchQuery) }
        }
    }
} 