package com.uniandes.vinilos.data.dao

import com.uniandes.vinilos.models.AuthResponse
import com.uniandes.vinilos.models.LoginRequest
import com.uniandes.vinilos.models.User
import com.uniandes.vinilos.network.AuthService
import dagger.hilt.android.scopes.ActivityRetainedScoped
import javax.inject.Inject

@ActivityRetainedScoped
class ApiAuthDao @Inject constructor(
    private val authService: AuthService
) : AuthDao {
    
    override suspend fun login(request: LoginRequest): AuthResponse? {
        return authService.login(request).body()
    }
    
    override suspend fun logout() {
        // No-op for now since we don't have a real logout endpoint
    }
    
    override suspend fun getCurrentUser(): User? {
        // For now, return null since we don't store user data
        return null
    }
    
    override suspend fun saveUser(user: User) {
        // No-op for now since we don't have local storage
    }
} 