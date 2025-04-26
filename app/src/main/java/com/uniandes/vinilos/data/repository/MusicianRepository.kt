package com.uniandes.vinilos.data.repository


import com.uniandes.vinilos.data.dao.MusicianDao
import com.uniandes.vinilos.models.Musician
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MusicianRepository @Inject constructor(
    private val musicianDao: MusicianDao
){
    suspend fun getAllMusicians(): List<Musician> {
        return musicianDao.getAllMusician()
    }

    suspend fun getMusicianById(id: Int): Musician? {
        return musicianDao.getMusicianById(id)
    }

}