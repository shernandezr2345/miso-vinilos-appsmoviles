package com.uniandes.vinilos.models

import kotlinx.serialization.Serializable

@Serializable
data class PerformerPrizes(
    val id: Int,
    val premiationDate: String,
)