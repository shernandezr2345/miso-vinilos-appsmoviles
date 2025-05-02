package com.uniandes.vinilos.data.dao

import com.uniandes.vinilos.models.Collector
import com.uniandes.vinilos.models.CollectorAlbum
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface CollectorDao {
    @GET("/collectors")
    suspend fun getCollectors(): Response<List<Collector>>

    @GET("/collectors/{collectorId}/albums")
    suspend fun getCollectorAlbums(@Path("collectorId") collectorId: Int): Response<List<CollectorAlbum>>
} 