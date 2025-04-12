package com.uniandes.vinilos.network

import com.uniandes.vinilos.models.AuthResponse
import com.uniandes.vinilos.models.LoginRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthService {
    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): Response<AuthResponse>
} 