package com.uniandes.vinilos.data.repository

import com.uniandes.vinilos.data.dao.AlbumDao
import com.uniandes.vinilos.models.Album
import com.uniandes.vinilos.models.Track
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AlbumRepository @Inject constructor(
    private val albumDao: AlbumDao
) {
    suspend fun getAllAlbums(): List<Album> {
        return albumDao.getAllAlbums()
    }

    suspend fun getAlbumById(id: Int): Album? {
        return albumDao.getAlbumById(id)
    }

    suspend fun getAlbumTracks(albumId: Int): List<Track> {
        return albumDao.getAlbumTracks(albumId)
    }
} 