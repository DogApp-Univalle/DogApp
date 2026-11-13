package com.univalle.dogapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.univalle.dogapp.model.Cita
import com.univalle.dogapp.repository.CitaRepository
import kotlinx.coroutines.launch

/**
 * HU 4.0: lee la cita por id (LiveData) y la elimina.
 */
class DetalleViewModel(
    private val repository: CitaRepository,
    id: Int
) : ViewModel() {

    val cita: LiveData<Cita?> = repository.cita(id)

    private val _eliminada = MutableLiveData(false)
    val eliminada: LiveData<Boolean> get() = _eliminada

    fun eliminar(cita: Cita) {
        viewModelScope.launch {
            repository.borrar(cita)
            _eliminada.value = true
        }
    }

    companion object {
        fun fabrica(repository: CitaRepository, id: Int): ViewModelProvider.Factory =
            viewModelFactory {
                initializer { DetalleViewModel(repository, id) }
            }
    }
}
