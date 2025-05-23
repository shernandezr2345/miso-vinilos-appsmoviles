package com.uniandes.vinilos.data.dao

import com.uniandes.vinilos.models.Collector
import com.uniandes.vinilos.models.CollectorAlbum
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Body
import retrofit2.http.POST

interface CollectorDao {
    @GET("/collectors")
    suspend fun getCollectors(): Response<List<Collector>>

    @GET("/collectors/{collectorId}/albums")
    suspend fun getCollectorAlbums(@Path("collectorId") collectorId: Int): Response<List<CollectorAlbum>>

    @POST("/collectors/{collectorId}/albums/{albumId}")
    suspend fun addAlbumToCollector(
        @Path("collectorId") collectorId: Int,
        @Path("albumId") albumId: Int,
        @Body body: AddAlbumToCollectorBody
    ): Response<Unit>
}

// Data class for the request body
data class AddAlbumToCollectorBody(
    val price: Int,
    val status: String
) 