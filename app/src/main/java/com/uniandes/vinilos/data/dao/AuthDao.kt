package com.uniandes.vinilos.data.dao

import com.uniandes.vinilos.models.AuthResponse
import com.uniandes.vinilos.models.LoginRequest
import com.uniandes.vinilos.models.User

interface AuthDao {
    suspend fun login(request: LoginRequest): AuthResponse?
    suspend fun logout()
    suspend fun getCurrentUser(): User?
    suspend fun saveUser(user: User)
} 