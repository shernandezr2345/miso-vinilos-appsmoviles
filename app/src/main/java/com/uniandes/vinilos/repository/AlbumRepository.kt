package com.uniandes.vinilos.repository

import com.uniandes.vinilos.model.Album
import com.uniandes.vinilos.network.AlbumService

class AlbumRepository(private val albumService: AlbumService) {
    suspend fun getAlbums(): List<Album> = albumService.getAlbums()
    
    suspend fun getAlbumById(id: Int): Album = albumService.getAlbum(id)
}