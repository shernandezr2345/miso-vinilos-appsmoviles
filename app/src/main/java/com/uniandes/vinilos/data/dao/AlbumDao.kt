package com.uniandes.vinilos.data.dao

import com.uniandes.vinilos.models.Album
import com.uniandes.vinilos.models.Track

interface AlbumDao {
    suspend fun getAllAlbums(): List<Album>
    suspend fun getAlbumById(id: Int): Album?
    suspend fun getAlbumTracks(albumId: Int): List<Track>
} 