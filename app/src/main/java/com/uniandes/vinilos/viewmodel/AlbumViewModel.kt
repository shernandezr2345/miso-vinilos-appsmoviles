package com.uniandes.vinilos.viewmodel

import android.app.Application
import androidx.lifecycle.*
import com.uniandes.vinilos.model.Album
import com.uniandes.vinilos.network.AlbumService
import com.uniandes.vinilos.repository.AlbumRepository
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class AlbumViewModel(application: Application) : AndroidViewModel(application) {
    private val retrofit = Retrofit.Builder()
        .baseUrl("https://<tu-api>/api/v1/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val service = retrofit.create(AlbumService::class.java)
    private val repository = AlbumRepository(service)

    private val _albums = MutableLiveData<List<Album>>()
    val albums: LiveData<List<Album>> = _albums

    fun fetchAlbums() {
        viewModelScope.launch {
            try {
                _albums.value = repository.getAlbums()
            } catch (e: Exception) {
                // Manejo de error
            }
        }
    }
}