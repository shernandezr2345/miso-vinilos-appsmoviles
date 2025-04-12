package com.uniandes.vinilos.models

import com.google.gson.annotations.SerializedName

data class Peer(
    @SerializedName("id")
    val id: Int,
    
    @SerializedName("name")
    val name: String,
    
    @SerializedName("ipAddress")
    val ipAddress: String,
    
    @SerializedName("port")
    val port: Int,
    
    @SerializedName("status")
    val status: String,
    
    @SerializedName("lastSeen")
    val lastSeen: String
) 