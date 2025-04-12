package com.uniandes.vinilos.ui.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.navigation.fragment.findNavController
import com.uniandes.vinilos.R
import com.uniandes.vinilos.models.UserRole
import com.uniandes.vinilos.ui.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RoleSelectionFragment : BaseFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_role_selection, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val radioGroup = view.findViewById<RadioGroup>(R.id.roleRadioGroup)
        val continueButton = view.findViewById<View>(R.id.continueButton)

        // Set initial selection based on saved role
        when (userSession.userRole) {
            UserRole.VISITOR -> radioGroup.check(R.id.visitorRole)
            UserRole.COLLECTOR -> radioGroup.check(R.id.collectorRole)
        }

        continueButton.setOnClickListener {
            val selectedId = radioGroup.checkedRadioButtonId
            if (selectedId != -1) {
                val radioButton = view.findViewById<RadioButton>(selectedId)
                val selectedRole = UserRole.fromString(radioButton.text.toString())
                
                // Save the selected role
                userSession.userRole = selectedRole
                
                // Navigate to main screen
                findNavController().navigate(R.id.action_roleSelection_to_albums)
            }
        }
    }
} 