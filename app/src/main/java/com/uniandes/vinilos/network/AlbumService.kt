package com.uniandes.vinilos.network

import com.uniandes.vinilos.models.Album
import com.uniandes.vinilos.models.Track
import retrofit2.Response
import retrofit2.http.*

interface AlbumService {
    @GET("albums")
    suspend fun getAlbums(): Response<List<Album>>

    @GET("albums/{id}")
    suspend fun getAlbum(@Path("id") id: Int): Response<Album>

    @GET("albums/{id}/tracks")
    suspend fun getAlbumTracks(@Path("id") id: Int): Response<List<Track>>

    @POST("albums")
    suspend fun createAlbum(@Body album: Album): Response<Album>

    @PUT("albums/{id}")
    suspend fun updateAlbum(@Path("id") id: Int, @Body album: Album): Response<Album>

    @DELETE("albums/{id}")
    suspend fun deleteAlbum(@Path("id") id: Int): Response<Unit>
} 