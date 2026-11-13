package com.univalle.dogapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.univalle.dogapp.model.Cita
import com.univalle.dogapp.repository.CitaRepository

/**
 * HU 2.0: la lista de citas es el LiveData que emite el DAO, asi que el Home
 * se refresca solo al insertar, editar o eliminar.
 */
class HomeViewModel(private val repository: CitaRepository) : ViewModel() {

    val citas: LiveData<List<Cita>> = repository.citas()

    companion object {
        fun fabrica(repository: CitaRepository): ViewModelProvider.Factory = viewModelFactory {
            initializer { HomeViewModel(repository) }
        }
    }
}
