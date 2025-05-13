package com.uniandes.vinilos.ui.album

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.textfield.TextInputEditText
import com.uniandes.vinilos.R
import com.uniandes.vinilos.viewmodel.AddAlbumViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddAlbumFragment : Fragment() {
    private val viewModel: AddAlbumViewModel by viewModels()

    private val genreOptions = listOf("Classical", "Salsa", "Rock", "Folk")
    private val recordLabelOptions = listOf("Sony Music", "EMI", "Discos Fuentes", "Elektra", "Fania Records")

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_add_album, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Set up genre spinner
        view.findViewById<Spinner>(R.id.genreSpinner).adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, genreOptions)

        // Set up record label spinner
        view.findViewById<Spinner>(R.id.recordLabelSpinner).adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, recordLabelOptions)

        setupSaveButton(view)
        observeViewModel()
    }

    private fun setupSaveButton(view: View) {
        view.findViewById<View>(R.id.saveButton).setOnClickListener {
            val name = view.findViewById<TextInputEditText>(R.id.nameEditText).text.toString()
            val cover = view.findViewById<TextInputEditText>(R.id.coverEditText).text.toString()
            val releaseDate = view.findViewById<TextInputEditText>(R.id.releaseDateEditText).text.toString()
            val description = view.findViewById<TextInputEditText>(R.id.descriptionEditText).text.toString()
            val genre = view.findViewById<Spinner>(R.id.genreSpinner).selectedItem as String
            val recordLabel = view.findViewById<Spinner>(R.id.recordLabelSpinner).selectedItem as String

            if (validateInputs(name, cover, releaseDate, description, genre, recordLabel)) {
                viewModel.createAlbum(name, cover, releaseDate, description, genre, recordLabel)
            }
        }
    }

    private fun validateInputs(
        name: String,
        cover: String,
        releaseDate: String,
        description: String,
        genre: String,
        recordLabel: String
    ): Boolean {
        if (name.isBlank()) {
            showError("El nombre del álbum es requerido")
            return false
        }
        if (cover.isBlank()) {
            showError("La URL de la portada es requerida")
            return false
        }
        if (releaseDate.isBlank()) {
            showError("La fecha de lanzamiento es requerida")
            return false
        }
        if (description.isBlank()) {
            showError("La descripción es requerida")
            return false
        }
        if (genre.isBlank()) {
            showError("El género es requerido")
            return false
        }
        if (recordLabel.isBlank()) {
            showError("La discográfica es requerida")
            return false
        }
        return true
    }

    private fun showError(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    private fun observeViewModel() {
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            // TODO: Show loading indicator
        }

        viewModel.error.observe(viewLifecycleOwner) { error ->
            error?.let {
                showError(it)
            }
        }

        viewModel.albumCreated.observe(viewLifecycleOwner) { created ->
            if (created) {
                findNavController().navigateUp()
            }
        }
    }
} 