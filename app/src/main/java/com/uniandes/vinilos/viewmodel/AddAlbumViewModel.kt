package com.uniandes.vinilos.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.uniandes.vinilos.data.dao.ApiAlbumDao
import com.uniandes.vinilos.models.Album
import com.uniandes.vinilos.models.CreateAlbumRequest
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddAlbumViewModel @Inject constructor(
    private val albumDao: ApiAlbumDao
) : ViewModel() {

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    private val _albumCreated = MutableLiveData<Boolean>()
    val albumCreated: LiveData<Boolean> = _albumCreated

    fun createAlbum(
        name: String,
        cover: String,
        releaseDate: String,
        description: String,
        genre: String,
        recordLabel: String
    ) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _error.value = null

                val album = CreateAlbumRequest(
                    name = name,
                    cover = cover,
                    releaseDate = releaseDate,
                    description = description,
                    genre = genre,
                    recordLabel = recordLabel
                )

                albumDao.createAlbum(album)
                _albumCreated.value = true
            } catch (e: Exception) {
                _error.value = e.message ?: "Error al crear el álbum"
                _albumCreated.value = false
            } finally {
                _isLoading.value = false
            }
        }
    }
} 