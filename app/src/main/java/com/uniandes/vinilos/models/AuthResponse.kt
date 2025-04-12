package com.uniandes.vinilos.models

import com.google.gson.annotations.SerializedName

data class AuthResponse(
    @SerializedName("token")
    val token: String,
    
    @SerializedName("user")
    val user: User
)

data class User(
    @SerializedName("id")
    val id: Int,
    
    @SerializedName("email")
    val email: String,
    
    @SerializedName("name")
    val name: String
) 