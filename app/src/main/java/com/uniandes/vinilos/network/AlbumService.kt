package com.uniandes.vinilos.network

import com.uniandes.vinilos.model.Album
import retrofit2.http.GET

interface AlbumService {
    @GET("albums")
    suspend fun getAlbums(): List<Album>
}