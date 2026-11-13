package com.univalle.dogapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.univalle.dogapp.model.Cita
import com.univalle.dogapp.repository.CitaRepository
import com.univalle.dogapp.utils.Constants
import kotlinx.coroutines.launch

/**
 * ViewModel compartido por HU 3.0 (crear) y HU 5.0 (editar): mismas reglas,
 * mismos campos y mismo autocompletado de razas.
 */
class FormCitaViewModel(private val repository: CitaRepository) : ViewModel() {

    private val _razas = MutableLiveData<List<String>>(emptyList())
    val razas: LiveData<List<String>> get() = _razas

    private val _cargando = MutableLiveData(false)
    val cargando: LiveData<Boolean> get() = _cargando

    private val _guardado = MutableLiveData(false)
    val guardado: LiveData<Boolean> get() = _guardado

    private val _citaId = MutableLiveData(Constants.ID_INVALIDO)

    /** Cita precargada para HU 5.0 (null mientras no haya id o si ya no existe). */
    val cita: LiveData<Cita?> = _citaId.switchMap { id ->
        if (id == Constants.ID_INVALIDO) MutableLiveData<Cita?>(null)
        else repository.cita(id)
    }

    init {
        cargarRazas()
    }

    /** Una sola llamada a la API por pantalla; el filtrado del prefijo es en memoria. */
    private fun cargarRazas() {
        viewModelScope.launch {
            _razas.value = repository.razas()
        }
    }

    fun cargar(id: Int) {
        _citaId.value = id
    }

    fun guardar(
        nombre: String,
        raza: String,
        propietario: String,
        telefono: String,
        sintoma: String
    ) {
        viewModelScope.launch {
            _cargando.value = true
            try {
                val url = repository.fotoDeRaza(raza)
                repository.guardar(
                    Cita(
                        nombreMascota = nombre,
                        raza = raza,
                        propietario = propietario,
                        telefono = telefono,
                        sintoma = sintoma,
                        imagenUrl = url,
                        fechaCreacion = System.currentTimeMillis()
                    )
                )
                _guardado.value = true
            } finally {
                _cargando.value = false
            }
        }
    }

    fun actualizar(
        nombre: String,
        raza: String,
        propietario: String,
        telefono: String,
        sintoma: String
    ) {
        val id = _citaId.value ?: return
        viewModelScope.launch {
            _cargando.value = true
            try {
                val anterior = repository.buscar(id) ?: return@launch
                // HU 5.0 "Importante": si cambia la raza, la foto se regenera.
                val url = if (anterior.raza == raza) anterior.imagenUrl
                else repository.fotoDeRaza(raza)

                repository.modificar(
                    anterior.copy(
                        nombreMascota = nombre,
                        raza = raza,
                        propietario = propietario,
                        telefono = telefono,
                        sintoma = sintoma,
                        imagenUrl = url
                    )
                )
                _guardado.value = true
            } finally {
                _cargando.value = false
            }
        }
    }

    fun consumirGuardado() {
        _guardado.value = false
    }

    companion object {
        fun fabrica(repository: CitaRepository): ViewModelProvider.Factory = viewModelFactory {
            initializer { FormCitaViewModel(repository) }
        }
    }
}
