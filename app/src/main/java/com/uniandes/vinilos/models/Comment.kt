package com.uniandes.vinilos.models

data class Comment(
    val id: Int,
    val description: String,
    val rating: Int,
    val albumId: Int
) 