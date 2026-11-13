package com.univalle.dogapp.data.local

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

/**
 * Las cinco operaciones del sprint.
 *
 * IMPORTANTE: observarTodas() y observarPorId() devuelven LiveData para que
 * Home, Detalle y Editar se refresquen solos cuando cambia la base de datos.
 * Si devolvieran List, habria que refrescar a mano y se perderia el criterio
 * de HU 3.0 (la cita debe verse al volver al Home) y HU 4.0/HU 5.0.
 */
@Dao
interface CitaDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertar(cita: CitaEntity): Long

    @Update
    suspend fun actualizar(cita: CitaEntity)

    @Delete
    suspend fun eliminar(cita: CitaEntity)

    @Query("SELECT * FROM citas ORDER BY id ASC")
    fun observarTodas(): LiveData<List<CitaEntity>>

    @Query("SELECT * FROM citas WHERE id = :id")
    fun observarPorId(id: Int): LiveData<CitaEntity?>

    /** Lectura puntual para completar la edicion sin observar la base. */
    @Query("SELECT * FROM citas WHERE id = :id")
    suspend fun buscarPorId(id: Int): CitaEntity?
}
