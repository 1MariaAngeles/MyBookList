package es.ejemplo.android.mybooklist.general

import androidx.databinding.adapters.Converters
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import es.ejemplo.android.mybooklist.libros.domain.Libro
import es.ejemplo.android.mybooklist.libros.dto.out.LibroRepositoryPortOut

@Database(entities = [Libro::class], version = 1)
@TypeConverters(Converters::class)
abstract class AppDataBase : RoomDatabase() {
    abstract fun libroDao(): LibroRepositoryPortOut
}