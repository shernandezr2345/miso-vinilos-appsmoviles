package com.uniandes.vinilos.data.dao

import com.uniandes.vinilos.models.Musician

interface MusicianDao {
    suspend fun getAllMusician(): List<Musician>
    suspend fun getMusicianById(id: Int): Musician?
} 