package es.ejemplo.android.mybooklist.libros.dto.out

import es.ejemplo.android.mybooklist.libros.domain.Libro
import androidx.room.*

@Dao
interface LibroRepositoryPortOut {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveLibro(libro: Libro)

    @Query("SELECT * FROM Libros")
    suspend fun findAllLibros(): List<Libro>

    @Query("SELECT * FROM Libros WHERE id = :id")
    suspend fun findById(id: Int): Libro?

    // En Room, el borrado por ID requiere una Query manual
    @Query("DELETE FROM Libros WHERE id = :id")
    suspend fun deleteById(id: Int)

    // Si quieres borrar pasando el objeto completo (más estilo JPA):
    @Delete
    suspend fun deleteLibro(libro: Libro)
}