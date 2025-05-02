package com.uniandes.vinilos.models

import com.uniandes.vinilos.models.Comment
import com.uniandes.vinilos.models.Performer
import com.uniandes.vinilos.models.Album

data class Collector(
    val id: Int,
    val name: String,
    val telephone: String,
    val email: String,
    val comments: List<Comment>,
    val favoritePerformers: List<Performer>
)

data class CollectorAlbum(
    val id: Int,
    val price: Double,
    val status: String,
    val album: Album,
    val collector: CollectorInfo
)

data class CollectorInfo(
    val id: Int,
    val name: String,
    val telephone: String,
    val email: String
) 