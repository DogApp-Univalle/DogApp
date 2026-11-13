package com.univalle.dogapp.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [CitaEntity::class], version = 1, exportSchema = false)
abstract class DogAppDatabase : RoomDatabase() {

    abstract fun citaDao(): CitaDao

    companion object {
        private const val NOMBRE = "dogapp.db"

        @Volatile
        private var INSTANCIA: DogAppDatabase? = null

        fun obtener(context: Context): DogAppDatabase =
            INSTANCIA ?: synchronized(this) {
                INSTANCIA ?: Room.databaseBuilder(
                    context.applicationContext,
                    DogAppDatabase::class.java,
                    NOMBRE
                ).build().also { INSTANCIA = it }
            }
    }
}
