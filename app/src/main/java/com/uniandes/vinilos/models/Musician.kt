package com.uniandes.vinilos.models
import kotlinx.serialization.Serializable

@Serializable
data class Musician(
    val id: Int,
    val name: String,
    val image: String,
    val description: String,
    val birthDate: String,
    val albums: List<Album>,
    val performerPrizes: List<PerformerPrizes>
) 