package com.uniandes.vinilos.models

import kotlinx.serialization.Serializable

@Serializable
data class Performer(
    val id: Int,
    val name: String,
    val image: String,
    val description: String,
    val birthDate: String? = null,
    val albums: List<Album> = emptyList()
) 