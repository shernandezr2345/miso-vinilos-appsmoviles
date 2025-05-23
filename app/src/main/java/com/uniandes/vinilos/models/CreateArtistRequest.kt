package com.uniandes.vinilos.models

data class CreateArtistRequest(
    val name: String,
    val image: String,
    val birthDate: String,
    val description: String
) 