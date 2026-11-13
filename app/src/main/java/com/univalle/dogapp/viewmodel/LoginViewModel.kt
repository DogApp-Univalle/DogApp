package com.univalle.dogapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

/**
 * HU 1.0: el ViewModel solo guarda el estado de la autenticacion.
 * No conoce Context, ni la View, ni BiometricPrompt (eso es de la capa de vista).
 */
class LoginViewModel : ViewModel() {

    private val _accesoConcedido = MutableLiveData(false)
    val accesoConcedido: LiveData<Boolean> get() = _accesoConcedido

    fun autenticacionExitosa() {
        _accesoConcedido.value = true
    }

    fun autenticacionFallida() {
        _accesoConcedido.value = false
    }
}
