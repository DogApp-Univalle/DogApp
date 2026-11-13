package com.univalle.dogapp.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.univalle.dogapp.model.Cita

/**
 * Tabla "citas" de SQLite creada con Room.
 * Reglas del enunciado: nombre <= 15, propietario <= 30, telefono solo digitos <= 10.
 * El telefono se guarda como String para no perder ceros a la izquierda.
 */
@Entity(tableName = "citas")
data class CitaEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val nombreMascota: String,
    val raza: String,
    val propietario: String,
    val telefono: String,
    val sintoma: String,
    val imagenUrl: String?,
    val fechaCreacion: Long
) {
    fun aDominio(): Cita = Cita(
        id = id,
        nombreMascota = nombreMascota,
        raza = raza,
        propietario = propietario,
        telefono = telefono,
        sintoma = sintoma,
        imagenUrl = imagenUrl,
        fechaCreacion = fechaCreacion
    )

    companion object {
        fun desde(cita: Cita): CitaEntity = CitaEntity(
            id = cita.id,
            nombreMascota = cita.nombreMascota,
            raza = cita.raza,
            propietario = cita.propietario,
            telefono = cita.telefono,
            sintoma = cita.sintoma,
            imagenUrl = cita.imagenUrl,
            fechaCreacion = cita.fechaCreacion
        )
    }
}
