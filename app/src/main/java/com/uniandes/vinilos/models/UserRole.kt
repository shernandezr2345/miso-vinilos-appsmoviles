package com.uniandes.vinilos.models

enum class UserRole {
    VISITOR,
    COLLECTOR;

    companion object {
        fun fromString(role: String): UserRole {
            return when (role.lowercase()) {
                "visitante", "visitor" -> VISITOR
                "coleccionista", "collector" -> COLLECTOR
                else -> VISITOR // default role
            }
        }
    }
} 