package es.ejemplo.android.mybooklist.libros.infraestructure

import android.util.Log
import es.ejemplo.android.mybooklist.general.remote.*
import es.ejemplo.android.mybooklist.libros.GeneroConteo
import es.ejemplo.android.mybooklist.libros.LibroDao
import es.ejemplo.android.mybooklist.libros.domain.Libro
import es.ejemplo.android.mybooklist.libros.domain.enums.Estados
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import retrofit2.HttpException

class LibroRepositoryImpl(
    private val libroDao: LibroDao,
    private val googleApi: GoogleBooksService,
    private val openLibraryApi: OpenLibraryService,
    private val aniListApi: AniListService,
    private val mangaDexApi: MangaDexService
) {
    suspend fun guardarLibro(libro: Libro) = libroDao.guardarLibro(libro)
    suspend fun actualizarLibro(libro: Libro) = libroDao.actualizarLibro(libro)
    fun obtenerTodosLosLibros(): Flow<List<Libro>> = libroDao.obtenerTodos()
    fun obtenerLibrosPorEstado(estado: Estados): Flow<List<Libro>> = libroDao.obtenerPorEstado(estado)
    suspend fun obtenerLibroPorId(id: Int): Libro? = libroDao.obtenerPorId(id)
    suspend fun eliminarLibro(id: Int) = libroDao.eliminarPorId(id)
    fun obtenerTotalPaginasLeidas(): Flow<Int?> = libroDao.obtenerTotalPaginasLeidas()
    fun obtenerConteoLibrosTerminados(): Flow<Int> = libroDao.obtenerLibrosTerminadosConteo()
    fun obtenerGenerosMasLeidos(): Flow<List<GeneroConteo>> = libroDao.obtenerGenerosMasLeidos()
    fun buscarLocal(query: String): Flow<List<Libro>> = libroDao.buscarLocal(query)

    suspend fun buscarLibrosRemoto(consulta: String): List<Libro> = coroutineScope {
        if (consulta.isBlank()) return@coroutineScope emptyList()

        val googleJob = async { buscarEnGoogle(consulta) }
        val aniListJob = async { buscarEnAniList(consulta) }
        val mangaDexJob = async { buscarEnMangaDex(consulta) }
        val openLibraryJob = async { buscarEnOpenLibrary(consulta) }

        val resultados = mutableListOf<Libro>()
        resultados.addAll(googleJob.await())
        resultados.addAll(aniListJob.await())
        resultados.addAll(mangaDexJob.await())
        if (resultados.size < 5) {
            resultados.addAll(openLibraryJob.await())
        }
        resultados.distinctBy { "${it.titulo.lowercase()}-${it.autor.lowercase()}" }
    }

    private suspend fun buscarEnGoogle(consulta: String): List<Libro> {
        return try {
            val respuesta = googleApi.searchBooks(consulta)
            respuesta.items?.mapNotNull { item ->
                val info = item.volumeInfo ?: return@mapNotNull null
                Libro(
                    titulo = info.title ?: "Sin título",
                    autor = info.authors?.joinToString(", ") ?: "Autor desconocido",
                    descripcion = info.description ?: "",
                    isbn = info.industryIdentifiers?.firstOrNull { it.type == "ISBN_13" }?.identifier 
                           ?: info.industryIdentifiers?.firstOrNull { it.type == "ISBN_10" }?.identifier,
                    portadaUrl = info.imageLinks?.thumbnail?.replace("http:", "https:"),
                    paginasTotales = info.pageCount ?: 0,
                    generos = info.categories ?: emptyList(),
                    estado = Estados.Pendiente,
                    fechaPublicacion = info.publishedDate ?: ""
                )
            } ?: emptyList()
        } catch (e: Exception) {
            Log.e("API_SEARCH", "Google Books falló: ${e.message}")
            emptyList()
        }
    }

    private suspend fun buscarEnAniList(consulta: String): List<Libro> {
        return try {
            val gqlQuery = """
                query (${'$'}search: String) {
                  Page(perPage: 10) {
                    media(search: ${'$'}search, type: MANGA) {
                      id
                      title { romaji english }
                      description
                      coverImage { large }
                      chapters
                      genres
                    }
                  }
                }
            """.trimIndent()
            val response = aniListApi.searchManga(AniListRequest(gqlQuery, mapOf("search" to consulta)))
            response.data?.Page?.media?.map { media ->
                Libro(
                    titulo = media.title?.english ?: media.title?.romaji ?: "Sin título",
                    autor = "Manga / AniList",
                    descripcion = media.description?.replace(Regex("<[^>]*>"), "") ?: "",
                    isbn = null,
                    portadaUrl = media.coverImage?.large,
                    paginasTotales = null,
                    capitulosTotales = media.chapters,
                    generos = media.genres ?: emptyList(),
                    estado = Estados.Pendiente,
                    fechaPublicacion = null
                )
            } ?: emptyList()
        } catch (e: Exception) {
            Log.e("API_SEARCH", "AniList falló: ${e.message}")
            emptyList()
        }
    }

    private suspend fun buscarEnMangaDex(consulta: String): List<Libro> {
        return try {
            val response = mangaDexApi.searchManga(consulta)
            response.data?.map { data ->
                val coverFileName = data.relationships?.find { it.type == "cover_art" }?.attributes?.fileName
                val authorName = data.relationships?.find { it.type == "author" }?.attributes?.name ?: "Autor MangaDex"
                Libro(
                    titulo = data.attributes?.title?.values?.firstOrNull() ?: "Sin título",
                    autor = authorName,
                    descripcion = data.attributes?.description?.get("en") ?: "",
                    isbn = null,
                    portadaUrl = if (coverFileName != null) "https://uploads.mangadex.org/covers/${data.id}/$coverFileName" else null,
                    paginasTotales = null,
                    generos = emptyList(),
                    estado = Estados.Pendiente,
                    fechaPublicacion = data.attributes?.status
                )
            } ?: emptyList()
        } catch (e: Exception) {
            Log.e("API_SEARCH", "MangaDex falló: ${e.message}")
            emptyList()
        }
    }

    private suspend fun buscarEnOpenLibrary(consulta: String): List<Libro> {
        return try {
            val respuestaOL = openLibraryApi.searchBooks(consulta)
            respuestaOL.docs?.map { doc ->
                Libro(
                    titulo = doc.title ?: "Sin título",
                    autor = doc.author_name?.joinToString(", ") ?: "Autor desconocido",
                    descripcion = "Publicado por primera vez en ${doc.first_publish_year ?: "año desconocido"}",
                    isbn = doc.isbn?.firstOrNull(),
                    portadaUrl = doc.cover_i?.let { "https://covers.openlibrary.org/b/id/$it-M.jpg" },
                    paginasTotales = doc.number_of_pages_median ?: 0,
                    generos = doc.subject?.take(3) ?: emptyList(),
                    estado = Estados.Pendiente,
                    fechaPublicacion = doc.first_publish_year?.toString()
                )
            } ?: emptyList()
        } catch (e: Exception) {
            Log.e("API_SEARCH", "OpenLibrary falló: ${e.message}")
            emptyList()
        }
    }
}
