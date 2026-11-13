package com.univalle.dogapp.model

/**
 * Modelo de dominio que usa la interfaz.
 * El turno que se muestra como "#1", "#2"... es el id autogenerado por Room.
 */
data class Cita(
    val id: Int = 0,
    val nombreMascota: String,
    val raza: String,
    val propietario: String,
    val telefono: String,
    val sintoma: String,
    val imagenUrl: String?,
    val fechaCreacion: Long
)
