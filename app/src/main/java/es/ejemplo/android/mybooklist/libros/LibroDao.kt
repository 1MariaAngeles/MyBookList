package es.ejemplo.android.mybooklist.libros

import androidx.room.*
import es.ejemplo.android.mybooklist.libros.domain.Libro
import es.ejemplo.android.mybooklist.libros.domain.enums.Estados
import kotlinx.coroutines.flow.Flow

@Dao
interface LibroDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun guardarLibro(libro: Libro)

    @Update
    suspend fun actualizarLibro(libro: Libro)

    @Query("SELECT * FROM Libros")
    fun obtenerTodos(): Flow<List<Libro>>

    @Query("SELECT * FROM Libros WHERE id = :id")
    suspend fun obtenerPorId(id: Int): Libro?

    @Query("SELECT * FROM Libros WHERE estado = :estado")
    fun obtenerPorEstado(estado: Estados): Flow<List<Libro>>

    @Query("DELETE FROM Libros WHERE id = :id")
    suspend fun eliminarPorId(id: Int)

    @Delete
    suspend fun eliminarLibro(libro: Libro)

    @Query("SELECT SUM(paginasLeidas) FROM Libros")
    fun obtenerTotalPaginasLeidas(): Flow<Int?>

    @Query("SELECT COUNT(*) FROM Libros WHERE estado = 'Leido'")
    fun obtenerLibrosTerminadosConteo(): Flow<Int>

    @Query("SELECT genero, COUNT(*) as conteo FROM Libros WHERE genero IS NOT NULL GROUP BY genero ORDER BY conteo DESC")
    fun obtenerGenerosMasLeidos(): Flow<List<GeneroConteo>>
}

data class GeneroConteo(
    val genero: String,
    val conteo: Int
)
