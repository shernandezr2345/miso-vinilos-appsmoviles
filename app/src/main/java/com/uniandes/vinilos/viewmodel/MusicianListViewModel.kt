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
class MusicianListViewModel @Inject constructor(
    private val repository: MusicianRepository
) : ViewModel() {
    private val _musicians = MutableLiveData<List<Musician>>()
    val albums: LiveData<List<Musician>> = _musicians

    private val _filteredMusicians = MutableLiveData<List<Musician>>()
    val filteredAlbums: LiveData<List<Musician>> = _filteredMusicians

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    private var allMusicians: List<Musician> = emptyList()

    fun loadMusicians() {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                allMusicians = repository.getAllMusicians()
                _musicians.value = allMusicians
                _filteredMusicians.value = allMusicians
            } catch (e: Exception) {
                _error.value = e.message ?: "Error loading albums"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun searchMusicians(query: String) {
        if (query.isEmpty()) {
            _filteredMusicians.value = allMusicians
            return
        }

        val searchQuery = query.lowercase().trim()
        _filteredMusicians.value = allMusicians.filter { musician ->
            musician.name.lowercase().contains(searchQuery)
        }
    }
} 