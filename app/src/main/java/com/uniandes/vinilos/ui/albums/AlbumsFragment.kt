package com.uniandes.vinilos.ui.albums

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.floatingactionbutton.FloatingActionButton
import androidx.navigation.fragment.findNavController
import com.uniandes.vinilos.R
import com.uniandes.vinilos.models.UserRole
import com.uniandes.vinilos.ui.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AlbumsFragment : BaseFragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_albums, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupViews(view)
    }

    private fun setupViews(view: View) {
        // Handle FAB visibility and click
        view.findViewById<FloatingActionButton>(R.id.fabAddAlbum)?.also { fab ->
            if (userSession.isCollector()) {
                fab.visibility = View.VISIBLE
                fab.setOnClickListener {
                    findNavController().navigate(R.id.action_albums_to_addAlbum)
                }
            } else {
                fab.visibility = View.GONE
            }
        }

        // Handle welcome messages visibility
        view.findViewById<View>(R.id.collectorMessage)?.also { message ->
            message.visibility = if (userSession.isCollector()) View.VISIBLE else View.GONE
        }
        
        view.findViewById<View>(R.id.visitorMessage)?.also { message ->
            message.visibility = if (userSession.isVisitor()) View.VISIBLE else View.GONE
        }
    }
} 