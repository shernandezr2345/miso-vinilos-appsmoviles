package com.uniandes.vinilos.models

import kotlinx.serialization.Serializable

@Serializable
data class Track(
    val id: Int,
    val name: String,
    val duration: String
) 