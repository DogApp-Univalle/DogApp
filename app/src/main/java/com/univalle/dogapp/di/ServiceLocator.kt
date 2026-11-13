package com.univalle.dogapp.di

import android.content.Context
import com.univalle.dogapp.data.local.DogAppDatabase
import com.univalle.dogapp.data.remote.RetrofitClient
import com.univalle.dogapp.repository.CitaRepository

/**
 * Inyeccion de dependencias sin frameworks (la rubrica no pide Hilt ni Koin).
 * Construye una sola vez la base de datos, el servicio Retrofit y el repository.
 */
object ServiceLocator {

    @Volatile
    private var repository: CitaRepository? = null

    fun citaRepository(context: Context): CitaRepository =
        repository ?: synchronized(this) {
            repository ?: CitaRepository(
                dao = DogAppDatabase.obtener(context.applicationContext).citaDao(),
                api = RetrofitClient.api
            ).also { repository = it }
        }
}
