package es.ejemplo.android.mybooklist.libros

import androidx.room.*
import es.ejemplo.android.mybooklist.libros.domain.Libro
import es.ejemplo.android.mybooklist.libros.domain.enums.Estados
import kotlinx.coroutines.flow.Flow

@Dao
interface LibroRepositoryPortOut {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveLibro(libro: Libro)

    @Update
    suspend fun updateLibro(libro: Libro)

    @Query("SELECT * FROM Libros")
    fun findAllLibros(): Flow<List<Libro>>

    @Query("SELECT * FROM Libros WHERE id = :id")
    suspend fun findById(id: Int): Libro?

    @Query("SELECT * FROM Libros WHERE estado = :estado")
    fun findByEstado(estado: Estados): Flow<List<Libro>>

    @Query("DELETE FROM Libros WHERE id = :id")
    suspend fun deleteById(id: Int)

    @Delete
    suspend fun deleteLibro(libro: Libro)

    // Consultas para estadísticas
    @Query("SELECT SUM(paginasLeidas) FROM Libros")
    fun getTotalPaginasLeidas(): Flow<Int?>

    @Query("SELECT COUNT(*) FROM Libros WHERE estado = 'Leido'")
    fun getLibrosTerminadosCount(): Flow<Int>

    @Query("SELECT genero, COUNT(*) as count FROM Libros WHERE genero IS NOT NULL GROUP BY genero ORDER BY count DESC")
    fun getGenerosMasLeidos(): Flow<List<GeneroCount>>
}

data class GeneroCount(
    val genero: String,
    val count: Int
)
