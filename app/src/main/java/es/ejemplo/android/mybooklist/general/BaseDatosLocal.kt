package es.ejemplo.android.mybooklist.general

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import es.ejemplo.android.mybooklist.libros.domain.Libro
import es.ejemplo.android.mybooklist.libros.LibroDao
import es.ejemplo.android.mybooklist.libros.infraestructure.EstadosMapper
import es.ejemplo.android.mybooklist.resenia.domain.Resenia
import es.ejemplo.android.mybooklist.resenia.ReseniaDao

@Database(entities = [Libro::class, Resenia::class], version = 5, exportSchema = false)
@TypeConverters(EstadosMapper::class)
abstract class BaseDatosLocal : RoomDatabase() {
    abstract fun libroDao(): LibroDao
    abstract fun reseniaDao(): ReseniaDao
}
