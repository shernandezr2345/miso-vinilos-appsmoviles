package com.uniandes.vinilos.data.dao

import com.uniandes.vinilos.models.Musician
import com.uniandes.vinilos.network.MusicianService
import dagger.hilt.android.scopes.ActivityRetainedScoped
import javax.inject.Inject

@ActivityRetainedScoped
class ApiMusicianDao @Inject constructor(
    private val musicianService: MusicianService
) : MusicianDao {
    
    override suspend fun getAllMusician(): List<Musician> {
        return try {
            val response = musicianService.getAllMusicians()
            if (response.isSuccessful) {
                response.body() ?: emptyList()
            } else {
                emptyList()
            }
        } catch (e: Exception) {
            emptyList()
        }
    }
    
    override suspend fun getMusicianById(id: Int): Musician? {
        return try {
            val response = musicianService.getMusicianById(id)
            if (response.isSuccessful) {
                response.body()
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }
    

} 