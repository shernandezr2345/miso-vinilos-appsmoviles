package com.uniandes.vinilos.data.dao

import com.uniandes.vinilos.models.Album
import com.uniandes.vinilos.models.Track
import com.uniandes.vinilos.models.CreateAlbumRequest
import com.uniandes.vinilos.network.AlbumService
import dagger.hilt.android.scopes.ActivityRetainedScoped
import retrofit2.Response
import javax.inject.Inject

@ActivityRetainedScoped
class ApiAlbumDao @Inject constructor(
    private val albumService: AlbumService
) : AlbumDao {
    
    override suspend fun getAllAlbums(): List<Album> {
        return try {
            val response = albumService.getAlbums()
            if (response.isSuccessful) {
                response.body() ?: emptyList()
            } else {
                emptyList()
            }
        } catch (e: Exception) {
            emptyList()
        }
    }
    
    override suspend fun getAlbumById(id: Int): Album? {
        return try {
            val response = albumService.getAlbum(id)
            if (response.isSuccessful) {
                response.body()
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }
    
    override suspend fun getAlbumTracks(albumId: Int): List<Track> {
        return try {
            val response = albumService.getAlbumTracks(albumId)
            if (response.isSuccessful) {
                response.body() ?: emptyList()
            } else {
                emptyList()
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun createAlbum(album: CreateAlbumRequest): Album? {
        return albumService.createAlbum(album).body()
    }

    suspend fun updateAlbum(id: Int, album: Album): Album? {
        return albumService.updateAlbum(id, album).body()
    }

    suspend fun deleteAlbum(id: Int): Boolean {
        return albumService.deleteAlbum(id).isSuccessful
    }
} 