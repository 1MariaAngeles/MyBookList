package es.ejemplo.android.mybooklist.libros.infraestructure

import es.ejemplo.android.mybooklist.general.remote.GoogleBooksService
import es.ejemplo.android.mybooklist.libros.GeneroConteo
import es.ejemplo.android.mybooklist.libros.LibroDao
import es.ejemplo.android.mybooklist.libros.domain.Libro
import es.ejemplo.android.mybooklist.libros.domain.enums.Estados
import kotlinx.coroutines.flow.Flow

class LibroRepositoryImpl(
    private val libroDao: LibroDao,
    private val servicioApi: GoogleBooksService
) {
    // Operaciones de Base de Datos
    suspend fun guardarLibro(libro: Libro) = libroDao.guardarLibro(libro)
    
    suspend fun actualizarLibro(libro: Libro) = libroDao.actualizarLibro(libro)
    
    fun obtenerTodosLosLibros(): Flow<List<Libro>> = libroDao.obtenerTodos()
    
    fun obtenerLibrosPorEstado(estado: Estados): Flow<List<Libro>> = libroDao.obtenerPorEstado(estado)
    
    suspend fun obtenerLibroPorId(id: Int): Libro? = libroDao.obtenerPorId(id)
    
    suspend fun eliminarLibro(id: Int) = libroDao.eliminarPorId(id)

    // Estadísticas
    fun obtenerTotalPaginasLeidas(): Flow<Int?> = libroDao.obtenerTotalPaginasLeidas()
    
    fun obtenerConteoLibrosTerminados(): Flow<Int> = libroDao.obtenerLibrosTerminadosConteo()
    
    fun obtenerGenerosMasLeidos(): Flow<List<GeneroConteo>> = libroDao.obtenerGenerosMasLeidos()

    // Búsqueda Remota en Google Books
    suspend fun buscarLibrosRemoto(consulta: String): List<Libro> {
        return try {
            val respuesta = servicioApi.searchBooks(consulta)
            respuesta.items?.map { item ->
                Libro(
                    titulo = item.volumeInfo.title,
                    autor = item.volumeInfo.authors?.joinToString(", ") ?: "Autor desconocido",
                    descripcion = item.volumeInfo.description,
                    isbn = item.volumeInfo.industryIdentifiers?.firstOrNull { it.type == "ISBN_13" }?.identifier 
                           ?: item.volumeInfo.industryIdentifiers?.firstOrNull { it.type == "ISBN_10" }?.identifier,
                    portadaUrl = item.volumeInfo.imageLinks?.thumbnail?.replace("http:", "https:"),
                    paginasTotales = item.volumeInfo.pageCount,
                    genero = item.volumeInfo.categories?.firstOrNull(),
                    estado = Estados.Pendiente
                )
            } ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }
}
