package com.uniandes.vinilos.utils

import android.content.Context
import android.content.SharedPreferences
import com.uniandes.vinilos.models.UserRole
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserSession @Inject constructor(
    @ApplicationContext context: Context
) {
    private val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    var userRole: UserRole
        get() = UserRole.fromString(prefs.getString(KEY_ROLE, UserRole.VISITOR.name) ?: UserRole.VISITOR.name)
        set(value) = prefs.edit().putString(KEY_ROLE, value.name).apply()

    fun isCollector(): Boolean = userRole == UserRole.COLLECTOR
    
    fun isVisitor(): Boolean = userRole == UserRole.VISITOR
    
    fun hasPermission(vararg roles: UserRole): Boolean = roles.contains(userRole)
    
    fun clearSession() {
        prefs.edit().clear().apply()
    }

    companion object {
        private const val PREFS_NAME = "vinilos_prefs"
        private const val KEY_ROLE = "user_role"
    }
} 