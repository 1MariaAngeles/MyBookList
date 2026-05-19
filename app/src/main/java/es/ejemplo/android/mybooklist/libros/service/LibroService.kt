package es.ejemplo.android.mybooklist.libros.service

import android.util.Log
import es.ejemplo.android.mybooklist.libros.domain.Libro
import es.ejemplo.android.mybooklist.libros.domain.enums.Estados
import es.ejemplo.android.mybooklist.libros.GeneroConteo
import es.ejemplo.android.mybooklist.libros.infraestructure.LibroRepositoryImpl
import es.ejemplo.android.mybooklist.resenia.domain.Resenia
import es.ejemplo.android.mybooklist.resenia.ReseniaDao
import kotlinx.coroutines.flow.Flow
import java.time.LocalDateTime
import java.time.LocalDate

class LibroService(
    private val repositorio: LibroRepositoryImpl,
    private val reseniaDao: ReseniaDao
) {

    fun listarTodos(): Flow<List<Libro>> = repositorio.obtenerTodosLosLibros()

    fun filtrarPorEstado(estado: Estados): Flow<List<Libro>> = repositorio.obtenerLibrosPorEstado(estado)

    suspend fun buscarById(id: Int): Libro? = repositorio.obtenerLibroPorId(id)

    suspend fun guardarLibro(libro: Libro) = repositorio.guardarLibro(libro)

    suspend fun eliminarLibro(id: Int) = repositorio.eliminarLibro(id)

    // Simplificado: Google Books ya maneja bien las tildes y espacios
    suspend fun buscarEnGoogleBooks(consulta: String): List<Libro> {
        if (consulta.isBlank()) return emptyList()
        return repositorio.buscarLibrosRemoto(consulta.trim())
    }

    fun buscarLocal(query: String): Flow<List<Libro>> = repositorio.buscarLocal(query.trim())

    suspend fun actualizarProgreso(libro: Libro, paginasLeidas: Int, paginasTotales: Int? = null) {
        val totalPaginas = paginasTotales ?: libro.paginasTotales ?: 0
        val paginasValidadas = if (totalPaginas > 0 && paginasLeidas > totalPaginas) totalPaginas else paginasLeidas

        val nuevoEstado = when {
            paginasValidadas <= 0 && libro.capitulosLeidos <= 0 -> Estados.Pendiente
            (totalPaginas > 0 && paginasValidadas >= totalPaginas) || 
            (libro.capitulosTotales != null && libro.capitulosTotales > 0 && libro.capitulosLeidos >= libro.capitulosTotales) -> Estados.Leido
            else -> Estados.Leyendo
        }

        val libroActualizado = libro.copy(
            paginasLeidas = paginasValidadas,
            paginasTotales = if (paginasTotales != null) paginasTotales else libro.paginasTotales,
            estado = nuevoEstado,
            fechaFin = if (nuevoEstado == Estados.Leido && libro.fechaFin == null) LocalDateTime.now() else libro.fechaFin,
            fechaInicio = if (libro.fechaInicio == null && paginasValidadas > 0) LocalDateTime.now() else libro.fechaInicio
        )
        repositorio.actualizarLibro(libroActualizado)
    }

    suspend fun actualizarProgresoCapitulos(libro: Libro, capitulosLeidos: Int, capitulosTotales: Int? = null) {
        val totalCap = capitulosTotales ?: libro.capitulosTotales ?: 0
        val capsValidados = if (totalCap > 0 && capitulosLeidos > totalCap) totalCap else capitulosLeidos

        val nuevoEstado = when {
            capsValidados <= 0 && libro.paginasLeidas <= 0 -> Estados.Pendiente
            (totalCap > 0 && capsValidados >= totalCap) || 
            (libro.paginasTotales != null && libro.paginasTotales > 0 && libro.paginasLeidas >= libro.paginasTotales) -> Estados.Leido
            else -> Estados.Leyendo
        }

        val libroActualizado = libro.copy(
            capitulosLeidos = capsValidados,
            capitulosTotales = if (capitulosTotales != null) capitulosTotales else libro.capitulosTotales,
            estado = nuevoEstado,
            fechaFin = if (nuevoEstado == Estados.Leido && libro.fechaFin == null) LocalDateTime.now() else libro.fechaFin,
            fechaInicio = if (libro.fechaInicio == null && capsValidados > 0) LocalDateTime.now() else libro.fechaInicio
        )
        repositorio.actualizarLibro(libroActualizado)
    }

    fun validarFechas(fechaInicio: LocalDateTime?, fechaFin: LocalDateTime?, fechaPublicacionStr: String?): String? {
        val ahora = LocalDateTime.now()
        if (fechaInicio != null && fechaInicio.isAfter(ahora)) return "No puedes empezar un libro en el futuro"
        if (fechaFin != null && fechaFin.isAfter(ahora)) return "No puedes terminar un libro en el futuro"
        if (fechaInicio != null && fechaFin != null && fechaFin.isBefore(fechaInicio)) return "La fecha de fin no puede ser anterior a la de inicio"
        
        if (fechaInicio != null && !fechaPublicacionStr.isNullOrBlank()) {
            try {
                val pubDate = if (fechaPublicacionStr.length == 4) {
                    LocalDate.of(fechaPublicacionStr.toInt(), 1, 1).atStartOfDay()
                } else {
                    val partes = fechaPublicacionStr.split("-")
                    when (partes.size) {
                        1 -> LocalDate.of(partes[0].toInt(), 1, 1).atStartOfDay()
                        2 -> LocalDate.of(partes[0].toInt(), partes[1].toInt(), 1).atStartOfDay()
                        else -> LocalDate.parse(fechaPublicacionStr).atStartOfDay()
                    }
                }
                if (fechaInicio.isBefore(pubDate)) return "No puedes haber empezado el libro antes de su publicación ($fechaPublicacionStr)"
            } catch (e: Exception) {}
        }
        return null
    }

    fun obtenerResenia(idLibro: Int): Flow<Resenia?> = reseniaDao.obtenerPorLibroId(idLibro)

    suspend fun guardarResenia(idLibro: Int, comentario: String) {
        val resenia = Resenia(idLibro = idLibro, comentario = comentario, ultimaEdicion = LocalDateTime.now())
        reseniaDao.guardarResenia(resenia)
    }

    fun obtenerTotalPaginas(): Flow<Int?> = repositorio.obtenerTotalPaginasLeidas()
    fun obtenerLibrosTerminados(): Flow<Int> = repositorio.obtenerConteoLibrosTerminados()
    fun obtenerGenerosFavoritos(): Flow<List<GeneroConteo>> = repositorio.obtenerGenerosMasLeidos()
}
