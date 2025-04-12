package com.uniandes.vinilos.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.uniandes.vinilos.data.repository.AlbumRepository
import com.uniandes.vinilos.models.Album
import com.uniandes.vinilos.models.Track
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AlbumViewModel(
    private val repository: AlbumRepository
) : ViewModel() {
    
    private val _albums = MutableStateFlow<List<Album>>(emptyList())
    val albums: StateFlow<List<Album>> = _albums.asStateFlow()
    
    private val _selectedAlbum = MutableStateFlow<Album?>(null)
    val selectedAlbum: StateFlow<Album?> = _selectedAlbum.asStateFlow()
    
    private val _albumTracks = MutableStateFlow<List<Track>>(emptyList())
    val albumTracks: StateFlow<List<Track>> = _albumTracks.asStateFlow()
    
    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading.asStateFlow()
    
    fun loadAlbums() {
        viewModelScope.launch {
            _loading.value = true
            try {
                _albums.value = repository.getAllAlbums()
            } finally {
                _loading.value = false
            }
        }
    }
    
    fun selectAlbum(albumId: Int) {
        viewModelScope.launch {
            _loading.value = true
            try {
                _selectedAlbum.value = repository.getAlbumById(albumId)
                _selectedAlbum.value?.let { album ->
                    _albumTracks.value = repository.getAlbumTracks(album.id)
                }
            } finally {
                _loading.value = false
            }
        }
    }
} 