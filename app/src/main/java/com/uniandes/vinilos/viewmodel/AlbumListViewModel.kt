package com.uniandes.vinilos.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.uniandes.vinilos.model.Album
import com.uniandes.vinilos.repository.AlbumRepository
import kotlinx.coroutines.launch

class AlbumListViewModel(private val repository: AlbumRepository) : ViewModel() {
    private val _albums = MutableLiveData<List<Album>>()
    val albums: LiveData<List<Album>> = _albums

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    fun loadAlbums() {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                val albums = repository.getAlbums()
                _albums.value = albums
            } catch (e: Exception) {
                _error.value = e.message ?: "Error loading albums"
            } finally {
                _isLoading.value = false
            }
        }
    }
} 