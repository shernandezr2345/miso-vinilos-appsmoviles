package com.uniandes.vinilos.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.uniandes.vinilos.models.Musician
import com.uniandes.vinilos.data.repository.MusicianRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MusicianDetailViewModel @Inject constructor(
    private val repository: MusicianRepository
) : ViewModel() {
    private val _musician = MutableLiveData<Musician?>()
    val album: MutableLiveData<Musician?> = _musician

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    fun loadAlbum(albumId: Int) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                val albumDetail = repository.getMusicianById(albumId)
                _musician.value = albumDetail
            } catch (e: Exception) {
                _error.value = e.message ?: "Error loading album details"
            } finally {
                _isLoading.value = false
            }
        }
    }
} 