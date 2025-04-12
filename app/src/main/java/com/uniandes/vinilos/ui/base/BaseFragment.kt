package com.uniandes.vinilos.ui.base

import android.view.View
import androidx.fragment.app.Fragment
import com.uniandes.vinilos.models.UserRole
import com.uniandes.vinilos.utils.UserSession
import javax.inject.Inject

abstract class BaseFragment : Fragment() {
    
    @Inject
    lateinit var userSession: UserSession
    
    protected val isCollector: Boolean
        get() = userSession.isCollector()
        
    protected val isVisitor: Boolean
        get() = userSession.isVisitor()
    
    protected fun showForRole(view: View, vararg roles: UserRole) {
        view.visibility = if (userSession.hasPermission(*roles)) View.VISIBLE else View.GONE
    }
    
    protected fun hideForRole(view: View, vararg roles: UserRole) {
        view.visibility = if (userSession.hasPermission(*roles)) View.GONE else View.VISIBLE
    }
    
    protected fun requireCollector(action: () -> Unit) {
        if (isCollector) {
            action()
        }
    }
} 