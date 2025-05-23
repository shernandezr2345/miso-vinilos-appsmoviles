package com.uniandes.vinilos.ui.artist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.snackbar.Snackbar
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.uniandes.vinilos.R
import com.uniandes.vinilos.viewmodel.AddArtistViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddArtistFragment : Fragment() {

    private val viewModel: AddArtistViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_add_artist, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupSaveButton(view)
        observeViewModel()
    }

    private fun setupSaveButton(view: View) {
        view.findViewById<View>(R.id.saveButton).setOnClickListener {
            val name = view.findViewById<TextInputEditText>(R.id.nameEditText).text.toString()
            val imageUrl = view.findViewById<TextInputEditText>(R.id.imageUrlEditText).text.toString()
            val birthDate = view.findViewById<TextInputEditText>(R.id.birthDateEditText).text.toString()
            val description = view.findViewById<TextInputEditText>(R.id.descriptionEditText).text.toString()
            if (validateInputs(view, name, imageUrl, birthDate, description)) {
                viewModel.createArtist(name, imageUrl, birthDate, description)
            }
        }
    }
    private fun validateInputs(
        view: View,
        name: String,
        imageUrl: String,
        birthDate: String,
        description: String
    ): Boolean {
        if (name.isBlank()) {
            showError(
                view.findViewById<TextInputLayout>(R.id.nameTextInputLayout),
                "El nombre del artista es requerido"
            )
            return false
        }
        if (imageUrl.isBlank()) {
            showError(
                view.findViewById<TextInputLayout>(R.id.imageUrlTextInputLayout),
                "La URL de la imagen es requerida"
            )
            return false
        }
        if (birthDate.isBlank()) {
            showError(
                view.findViewById<TextInputLayout>(R.id.birthDateTextInputLayout),
                "La fecha de nacimiento es requerida"
            )
            return false
        }
        if (description.isBlank()) {
            showError(
                view.findViewById<TextInputLayout>(R.id.descriptionTextInputLayout),
                "La descripción es requerida"
            )
            return false
        }
        return true
    }

    private fun showError(field: TextInputLayout, message: String) {
        field.error = message
    }

    private fun observeViewModel() {
        viewModel.isLoading.observe(viewLifecycleOwner) {
            // TODO: Mostrar indicador de carga si es necesario
        }

        viewModel.error.observe(viewLifecycleOwner) { error ->
            error?.let { message ->
                // Show error via Snackbar instead of Toast
                view?.let { rootView ->
                    Snackbar.make(rootView, message, Snackbar.LENGTH_LONG).show()
                }
            }
        }

        viewModel.artistCreated.observe(viewLifecycleOwner) { created ->
            if (created) {
                findNavController().navigateUp()
            }
        }
    }
}
