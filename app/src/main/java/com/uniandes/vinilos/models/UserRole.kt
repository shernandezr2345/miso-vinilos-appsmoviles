package com.uniandes.vinilos.models

enum class UserRole {
    VISITOR,
    COLLECTOR;

    companion object {
        fun fromString(role: String): UserRole {
            return when (role.lowercase()) {
                "visitante" -> VISITOR
                "colleccionista" -> COLLECTOR
                else -> VISITOR // default role
            }
        }
    }
} 