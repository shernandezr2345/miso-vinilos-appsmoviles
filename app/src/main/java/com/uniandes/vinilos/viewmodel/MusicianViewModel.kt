package com.uniandes.vinilos.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.uniandes.vinilos.models.Musician
import com.uniandes.vinilos.data.repository.MusicianRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MusicianViewModel @Inject constructor(
    private val repository: MusicianRepository
) : ViewModel() {

    private val _musicians = MutableStateFlow<List<Musician>>(emptyList())
    val musicians: StateFlow<List<Musician>> = _musicians

    private val _selectedMusician = MutableStateFlow<Musician?>(null)
    val selectedMusician: StateFlow<Musician?> = _selectedMusician

    fun loadMusicians() {
        viewModelScope.launch {
            _musicians.value = repository.getAllMusicians()
        }
    }

    fun selectMusician(musician: Musician) {
        _selectedMusician.value = musician
    }
}