package com.uniandes.vinilos.models

import kotlinx.serialization.Serializable

@Serializable
data class Comment(
    val id: Int,
    val description: String,
    val rating: Int,
    val albumId: Int
) 