package es.ejemplo.android.mybooklist.resenia

import androidx.room.*
import es.ejemplo.android.mybooklist.resenia.domain.Resenia
import kotlinx.coroutines.flow.Flow

@Dao
interface ReseniaDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun guardarResenia(resenia: Resenia)

    @Query("SELECT * FROM Resenia WHERE idLibro = :idLibro")
    fun obtenerPorLibroId(idLibro: Int): Flow<Resenia?>

    @Delete
    suspend fun eliminarResenia(resenia: Resenia)
}
