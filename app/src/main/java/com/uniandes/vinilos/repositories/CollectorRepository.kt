package com.uniandes.vinilos.repositories

import com.uniandes.vinilos.data.dao.CollectorDao
import com.uniandes.vinilos.models.Collector
import com.uniandes.vinilos.models.CollectorAlbum
import retrofit2.Response

class CollectorRepository(private val collectorDao: CollectorDao) {
    suspend fun getCollectors(): Response<List<Collector>> {
        return collectorDao.getCollectors()
    }

    suspend fun getCollectorAlbums(collectorId: Int): Response<List<CollectorAlbum>> {
        return collectorDao.getCollectorAlbums(collectorId)
    }
} 