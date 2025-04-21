package com.uniandes.vinilos.ui.artists

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.uniandes.vinilos.R
import com.uniandes.vinilos.models.UserRole
import com.uniandes.vinilos.ui.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ArtistsFragment : BaseFragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_artists, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d("ArtistsFragment", "Fragmento de artistas cargado")
        setupViews(view)
    }

    private fun setupViews(view: View) {
        // Manejo del botón flotante (FAB) de agregar artista
        view.findViewById<FloatingActionButton>(R.id.addMusicianFab)?.also { fab ->
            if (userSession.isCollector()) {
                fab.visibility = View.VISIBLE
               /* fab.setOnClickListener {
                    findNavController().navigate(R.id.action_musicians_to_addMusician)
                }*/
            } else {
                fab.visibility = View.GONE
            }
        }

        // Manejo de mensajes de bienvenida
        view.findViewById<View>(R.id.collectorMessage)?.also { message ->
            message.visibility = if (userSession.isCollector()) View.VISIBLE else View.GONE
        }

        view.findViewById<View>(R.id.visitorMessage)?.also { message ->
            message.visibility = if (userSession.isVisitor()) View.VISIBLE else View.GONE
        }
    }
} 