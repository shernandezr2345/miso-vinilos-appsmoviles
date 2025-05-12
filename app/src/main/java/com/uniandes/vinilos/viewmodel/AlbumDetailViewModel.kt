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
class AlbumDetailViewModel @Inject constructor(
    private val repository: AlbumRepository
) : ViewModel() {
    private val _album = MutableLiveData<Album>()
    val album: LiveData<Album> = _album

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    fun loadAlbum(albumId: Int) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _album.postValue(repository.getAlbumById(albumId))
            } catch (e: Exception) {
                _error.value = e.message ?: "Error loading album details"
            } finally {
                _isLoading.value = false
            }
        }
    }
} 