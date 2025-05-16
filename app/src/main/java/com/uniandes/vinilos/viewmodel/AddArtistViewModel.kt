package com.uniandes.vinilos.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.uniandes.vinilos.data.dao.ApiMusicianDao
import com.uniandes.vinilos.models.CreateArtistRequest
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddArtistViewModel @Inject constructor(
    private val artistDao: ApiMusicianDao
) : ViewModel() {

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    private val _artistCreated = MutableLiveData<Boolean>()
    val artistCreated: LiveData<Boolean> = _artistCreated

    fun createArtist(
        name: String,
        image: String,
        birthDate: String,
        description: String
    ) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _error.value = null

                val artist = CreateArtistRequest(
                    name = name,
                    image = image,
                    birthDate = birthDate,
                    description = description
                )

                artistDao.createArtist(artist)
                _artistCreated.value = true
            } catch (e: Exception) {
                _error.value = e.message ?: "Error al crear el artista"
                _artistCreated.value = false
            } finally {
                _isLoading.value = false
            }
        }
    }
}