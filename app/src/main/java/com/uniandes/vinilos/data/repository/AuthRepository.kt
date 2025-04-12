package com.uniandes.vinilos.data.repository

import com.uniandes.vinilos.data.dao.AuthDao
import com.uniandes.vinilos.models.AuthResponse
import com.uniandes.vinilos.models.LoginRequest
import com.uniandes.vinilos.models.User
import javax.inject.Inject

class AuthRepository @Inject constructor(
    private val authDao: AuthDao
) {
    suspend fun login(email: String, password: String): Result<AuthResponse> {
        return try {
            val request = LoginRequest(email, password)
            val response = authDao.login(request)
            if (response != null) {
                Result.success(response)
            } else {
                Result.failure(Exception("Login failed"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun logout() {
        authDao.logout()
    }
    
    suspend fun getCurrentUser(): User? {
        return authDao.getCurrentUser()
    }

    suspend fun saveUser(user: User) {
        authDao.saveUser(user)
    }
} 