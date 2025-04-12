package com.uniandes.vinilos.network

import com.uniandes.vinilos.model.Album
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Url

interface AlbumService {
    @GET
    suspend fun getAlbums(@Url url: String = "${ApiConfig.ALBUMS_BASE_URL}"): List<Album>

    @GET
    suspend fun getAlbum(@Url url: String = "${ApiConfig.ALBUMS_BASE_URL}/{id}"): Album
}