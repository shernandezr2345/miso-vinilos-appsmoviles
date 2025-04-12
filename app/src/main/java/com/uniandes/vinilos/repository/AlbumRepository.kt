package com.uniandes.vinilos.repository

import com.uniandes.vinilos.network.AlbumService
import com.uniandes.vinilos.network.ApiConfig

class AlbumRepository(private val service: AlbumService) {
    suspend fun getAlbums() = service.getAlbums()
    suspend fun getAlbum(id: Int) = service.getAlbum("${ApiConfig.ALBUMS_BASE_URL}/$id")
}