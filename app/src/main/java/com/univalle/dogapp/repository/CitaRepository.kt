package com.univalle.dogapp.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import com.univalle.dogapp.data.local.CitaDao
import com.univalle.dogapp.data.local.CitaEntity
import com.univalle.dogapp.data.remote.DogApiService
import com.univalle.dogapp.model.Cita
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Unica puerta de entrada a los datos: une Room (SQLite) y Retrofit (dog.ceo).
 * Ni los Fragments ni los ViewModels hablan directamente con el DAO o con la API.
 */
class CitaRepository(
    private val dao: CitaDao,
    private val api: DogApiService
) {

    // ---- Lecturas reactivas (LiveData que viene del DAO) ----

    fun citas(): LiveData<List<Cita>> = dao.observarTodas().map { lista ->
        lista.map { it.aDominio() }
    }

    fun cita(id: Int): LiveData<Cita?> = dao.observarPorId(id).map { it?.aDominio() }

    suspend fun buscar(id: Int): Cita? = withContext(Dispatchers.IO) {
        dao.buscarPorId(id)?.aDominio()
    }

    // ---- Escrituras ----

    suspend fun guardar(cita: Cita): Long = withContext(Dispatchers.IO) {
        dao.insertar(CitaEntity.desde(cita))
    }

    suspend fun modificar(cita: Cita) = withContext(Dispatchers.IO) {
        dao.actualizar(CitaEntity.desde(cita))
    }

    suspend fun borrar(cita: Cita) = withContext(Dispatchers.IO) {
        dao.eliminar(CitaEntity.desde(cita))
    }

    // ---- API REST ----

    /**
     * Lista de razas para el autocompletado. Se pide una sola vez por pantalla
     * y se filtra en memoria; si no hay red devuelve lista vacia y la app sigue
     * funcionando (el campo queda escribible a mano).
     */
    suspend fun razas(): List<String> = withContext(Dispatchers.IO) {
        runCatching {
            api.listarRazas().message.keys
                .map { it.replaceFirstChar { c -> c.uppercase() } }
                .sorted()
        }.getOrDefault(emptyList())
    }

    /**
     * Foto de la raza elegida. Plan A: /breed/{raza}/images/random.
     * Plan B: /breeds/image/random (el del enunciado) si la raza no existe.
     * Plan C: null, y la vista usa el marcador local (funciona sin internet).
     */
    suspend fun fotoDeRaza(raza: String): String? = withContext(Dispatchers.IO) {
        val clave = raza.trim().lowercase().replace(" ", "")
        if (clave.isBlank()) return@withContext null
        runCatching { api.fotoPorRaza(clave).message }
            .recoverCatching { api.fotoAleatoria().message }
            .getOrNull()
            ?.takeIf { it.isNotBlank() }
    }
}
