package es.ejemplo.android.mybooklist.libros.service

import android.os.Build
import androidx.annotation.RequiresApi
import es.ejemplo.android.mybooklist.libros.domain.Libro
import es.ejemplo.android.mybooklist.libros.domain.enums.Estados
import es.ejemplo.android.mybooklist.libros.GeneroConteo
import es.ejemplo.android.mybooklist.libros.infraestructure.LibroRepositoryImpl
import es.ejemplo.android.mybooklist.resenia.domain.Resenia
import es.ejemplo.android.mybooklist.resenia.ReseniaDao
import kotlinx.coroutines.flow.Flow
import java.time.LocalDateTime

class LibroService(
    private val repositorio: LibroRepositoryImpl,
    private val reseniaDao: ReseniaDao
) {

    fun listarTodos(): Flow<List<Libro>> = repositorio.obtenerTodosLosLibros()

    fun filtrarPorEstado(estado: Estados): Flow<List<Libro>> = repositorio.obtenerLibrosPorEstado(estado)

    suspend fun buscarPorId(id: Int): Libro? = repositorio.obtenerLibroPorId(id)

    suspend fun guardarLibro(libro: Libro) = repositorio.guardarLibro(libro)

    suspend fun eliminarLibro(id: Int) = repositorio.eliminarLibro(id)

    suspend fun buscarEnGoogleBooks(consulta: String): List<Libro> = repositorio.buscarLibrosRemoto(consulta)

    suspend fun actualizarProgreso(libro: Libro, paginasLeidas: Int) {
        val nuevoEstado = when {
            paginasLeidas <= 0 -> Estados.Pendiente
            paginasLeidas >= (libro.paginasTotales ?: Int.MAX_VALUE) -> Estados.Leido
            else -> Estados.Leyendo
        }

        val libroActualizado = libro.copy(
            paginasLeidas = paginasLeidas,
            estado = nuevoEstado,
            fechaFin = if (nuevoEstado == Estados.Leido) LocalDateTime.now() else libro.fechaFin,
            fechaInicio = if (libro.fechaInicio == null && paginasLeidas > 0) LocalDateTime.now() else libro.fechaInicio
        )
        repositorio.actualizarLibro(libroActualizado)
    }

    // Gestión de Reseñas
    fun obtenerResenia(idLibro: Int): Flow<Resenia?> = reseniaDao.obtenerPorLibroId(idLibro)

    suspend fun guardarResenia(idLibro: Int, comentario: String) {
        val resenia = Resenia(
            idLibro = idLibro,
            comentario = comentario,
            ultimaEdicion = LocalDateTime.now()
        )
        reseniaDao.guardarResenia(resenia)
    }

    // Estadísticas
    fun obtenerTotalPaginas(): Flow<Int?> = repositorio.obtenerTotalPaginasLeidas()
    fun obtenerLibrosTerminados(): Flow<Int> = repositorio.obtenerConteoLibrosTerminados()
    fun obtenerGenerosFavoritos(): Flow<List<GeneroConteo>> = repositorio.obtenerGenerosMasLeidos()
}
