package com.uniandes.vinilos.network

import com.uniandes.vinilos.model.Album
import retrofit2.http.GET
import retrofit2.http.Path

interface AlbumService {
    @GET("albums")
    suspend fun getAlbums(): List<Album>

    @GET("albums/{id}")
    suspend fun getAlbum(@Path("id") id: Int): Album
}