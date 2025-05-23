package com.uniandes.vinilos.network

import com.uniandes.vinilos.models.Musician
import com.uniandes.vinilos.models.CreateArtistRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path


interface MusicianService {

    @GET("musicians")
    suspend fun getAllMusicians(): Response<List<Musician>>

    @GET("musicians/{id}")
    suspend fun getMusicianById(@Path("id") id: Int): Response<Musician>

    @POST("musicians")
    suspend fun createMusicians(@Body musician: CreateArtistRequest): Response<Musician>

} 