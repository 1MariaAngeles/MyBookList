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
import java.time.format.DateTimeFormatter
import java.text.Normalizer

class LibroService(
    private val repositorio: LibroRepositoryImpl,
    private val reseniaDao: ReseniaDao
) {

    fun listarTodos(): Flow<List<Libro>> = repositorio.obtenerTodosLosLibros()

    fun filtrarPorEstado(estado: Estados): Flow<List<Libro>> = repositorio.obtenerLibrosPorEstado(estado)

    suspend fun buscarById(id: Int): Libro? = repositorio.obtenerLibroPorId(id)

    suspend fun guardarLibro(libro: Libro) = repositorio.guardarLibro(libro)

    suspend fun eliminarLibro(id: Int) = repositorio.eliminarLibro(id)

    // Búsqueda normalizada para la API (Mejorada para tildes y mayúsculas)
    suspend fun buscarEnGoogleBooks(consulta: String): List<Libro> {
        val normalized = Normalizer.normalize(consulta.trim(), Normalizer.Form.NFD)
        val queryLimpia = normalized.replace(Regex("\\p{InCombiningDiacriticalMarks}+"), "").lowercase()
        return repositorio.buscarLibrosRemoto(queryLimpia)
    }

    fun buscarLocal(query: String): Flow<List<Libro>> = repositorio.buscarLocal(query.trim())

    suspend fun actualizarProgreso(libro: Libro, paginasLeidas: Int) {
        val totalPaginas = libro.paginasTotales ?: 0
        val paginasValidadas = if (totalPaginas > 0 && paginasLeidas > totalPaginas) totalPaginas else paginasLeidas

        val nuevoEstado = when {
            paginasValidadas <= 0 && libro.capitulosLeidos <= 0 -> Estados.Pendiente
            (totalPaginas > 0 && paginasValidadas >= totalPaginas) || 
            (libro.capitulosTotales != null && libro.capitulosTotales > 0 && libro.capitulosLeidos >= libro.capitulosTotales) -> Estados.Leido
            else -> Estados.Leyendo
        }

        val libroActualizado = libro.copy(
            paginasLeidas = paginasValidadas,
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

    // Validación de fechas mejorada
    fun validarFechas(fechaInicio: LocalDateTime?, fechaFin: LocalDateTime?, fechaPublicacionStr: String?): String? {
        val ahora = LocalDateTime.now()

        // 1. Fecha de inicio no puede ser en el futuro
        if (fechaInicio != null && fechaInicio.isAfter(ahora)) {
            return "No puedes empezar un libro en el futuro"
        }

        // 2. Fecha de fin no puede ser en el futuro
        if (fechaFin != null && fechaFin.isAfter(ahora)) {
            return "No puedes terminar un libro en el futuro"
        }

        // 3. Fecha fin no puede ser anterior a fecha inicio
        if (fechaInicio != null && fechaFin != null && fechaFin.isBefore(fechaInicio)) {
            return "La fecha de fin no puede ser anterior a la de inicio"
        }
        
        // 4. Fecha de inicio no puede ser anterior a la fecha de publicación
        if (fechaInicio != null && !fechaPublicacionStr.isNullOrBlank()) {
            try {
                val pubDate = if (fechaPublicacionStr.length == 4) {
                    LocalDate.of(fechaPublicacionStr.toInt(), 1, 1).atStartOfDay()
                } else {
                    // Google Books usa AAAA-MM-DD o AAAA-MM
                    val partes = fechaPublicacionStr.split("-")
                    when (partes.size) {
                        1 -> LocalDate.of(partes[0].toInt(), 1, 1).atStartOfDay()
                        2 -> LocalDate.of(partes[0].toInt(), partes[1].toInt(), 1).atStartOfDay()
                        else -> LocalDate.parse(fechaPublicacionStr).atStartOfDay()
                    }
                }
                
                if (fechaInicio.isBefore(pubDate)) {
                    return "No puedes haber empezado el libro antes de su publicación ($fechaPublicacionStr)"
                }
            } catch (e: Exception) {
                // Si el formato es extraño, permitimos para no bloquear
            }
        }
        return null
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
