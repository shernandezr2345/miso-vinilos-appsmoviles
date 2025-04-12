package com.uniandes.vinilos.network

object ApiConfig {
    const val BASE_URL = "http://10.0.2.2:3000"
    
    // You can add more base URLs for different services
    const val ALBUMS_BASE_URL = "$BASE_URL/api/albums"
    const val ARTISTS_BASE_URL = "$BASE_URL/api/artists"
    const val COLLECTORS_BASE_URL = "$BASE_URL/api/collectors"
} 