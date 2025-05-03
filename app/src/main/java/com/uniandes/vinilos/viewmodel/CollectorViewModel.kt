package com.uniandes.vinilos.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.uniandes.vinilos.models.Collector
import com.uniandes.vinilos.models.CollectorAlbum
import com.uniandes.vinilos.repositories.CollectorRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CollectorViewModel @Inject constructor(
    private val repository: CollectorRepository
) : ViewModel() {
    private val _collectors = MutableLiveData<List<Collector>>()
    val collectors: LiveData<List<Collector>> = _collectors

    private val _collectorAlbums = MutableLiveData<Map<Int, List<CollectorAlbum>>>()
    val collectorAlbums: LiveData<Map<Int, List<CollectorAlbum>>> = _collectorAlbums

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> = _loading

    fun fetchCollectors() {
        viewModelScope.launch {
            _loading.value = true
            try {
                val response = repository.getCollectors()
                if (response.isSuccessful) {
                    val collectorsList = response.body() ?: emptyList()
                    _collectors.value = collectorsList
                    fetchAlbumsForCollectors(collectorsList)
                } else {
                    _error.value = "Error fetching collectors: ${response.code()}"
                }
            } catch (e: Exception) {
                _error.value = "Error: ${e.message}"
            } finally {
                _loading.value = false
            }
        }
    }

    private fun fetchAlbumsForCollectors(collectors: List<Collector>) {
        viewModelScope.launch {
            val albumsMap = mutableMapOf<Int, List<CollectorAlbum>>()
            collectors.forEach { collector ->
                try {
                    val albumsResponse = repository.getCollectorAlbums(collector.id)
                    if (albumsResponse.isSuccessful) {
                        albumsMap[collector.id] = albumsResponse.body() ?: emptyList()
                    } else {
                        albumsMap[collector.id] = emptyList()
                    }
                } catch (e: Exception) {
                    albumsMap[collector.id] = emptyList()
                }
            }
            _collectorAlbums.value = albumsMap
        }
    }
} 