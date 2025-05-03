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
    val musician: MutableLiveData<Musician?> = _musician

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    fun loadMusician(MusicianId: Int) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                val musicianIdDetail = repository.getMusicianById(MusicianId)
                _musician.value = musicianIdDetail
            } catch (e: Exception) {
                _error.value = e.message ?: "Error loading musician details"
            } finally {
                _isLoading.value = false
            }
        }
    }
} 