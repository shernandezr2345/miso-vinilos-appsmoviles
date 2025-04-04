package com.uniandes.vinilos.repository

import com.uniandes.vinilos.network.AlbumService

class AlbumRepository(private val service: AlbumService) {
    suspend fun getAlbums() = service.getAlbums()
}