package es.ejemplo.android.mybooklist.libros.ui

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import es.ejemplo.android.mybooklist.general.UserPreferences
import es.ejemplo.android.mybooklist.libros.domain.Libro
import es.ejemplo.android.mybooklist.libros.domain.enums.Estados
import es.ejemplo.android.mybooklist.libros.service.LibroService
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDateTime

class LibroViewModel(
    private val servicio: LibroService,
    private val prefs: UserPreferences
) : ViewModel() {

    private val _resultadosBusqueda = MutableStateFlow<List<Libro>>(emptyList())
    val resultadosBusqueda: StateFlow<List<Libro>> = _resultadosBusqueda.asStateFlow()

    private val _prediccionesLocales = MutableStateFlow<List<Libro>>(emptyList())
    val prediccionesLocales: StateFlow<List<Libro>> = _prediccionesLocales.asStateFlow()

    private val _estaCargando = MutableStateFlow(false)
    val estaCargando: StateFlow<Boolean> = _estaCargando.asStateFlow()

    private val _consultaBusqueda = MutableStateFlow("")
    val consultaBusqueda: StateFlow<String> = _consultaBusqueda.asStateFlow()

    val todosLosLibros = servicio.listarTodos()
    
    private val _metaLectura = MutableStateFlow(prefs.readingGoal)
    val metaLectura = _metaLectura.asStateFlow()

    private val _fotoPerfilUri = MutableStateFlow(prefs.profileImageUri)
    val fotoPerfilUri = _fotoPerfilUri.asStateFlow()

    private val _nombreUsuario = MutableStateFlow(prefs.userName)
    val nombreUsuario = _nombreUsuario.asStateFlow()

    val totalPaginasLeidas = servicio.obtenerTotalPaginas()
    val conteoLibrosTerminados = servicio.obtenerLibrosTerminados()
    val generosMasLeidos = servicio.obtenerGenerosFavoritos()

    fun actualizarConsulta(nuevaConsulta: String) {
        _consultaBusqueda.value = nuevaConsulta
        // Si borramos la búsqueda, limpiamos resultados previos
        if (nuevaConsulta.isBlank()) {
            _resultadosBusqueda.value = emptyList()
            _prediccionesLocales.value = emptyList()
        }
    }

    // Búsqueda MANUAL: Solo ocurre al pulsar el botón de buscar
    fun buscarLibros(consulta: String) {
        if (consulta.isBlank()) return
        
        viewModelScope.launch {
            _estaCargando.value = true
            try {
                // 1. Búsqueda Local (Manual)
                val locales = servicio.buscarLocal(consulta).first()
                _prediccionesLocales.value = locales

                // 2. Búsqueda en Google Books (Manual)
                Log.d("API_SEARCH", "Búsqueda manual iniciada: $consulta")
                val remotos = servicio.buscarEnGoogleBooks(consulta)
                _resultadosBusqueda.value = remotos
                
            } catch (e: Exception) {
                Log.e("API_SEARCH", "Error en búsqueda manual: ${e.message}")
                _resultadosBusqueda.value = emptyList()
            } finally {
                _estaCargando.value = false
            }
        }
    }

    fun guardarLibro(libro: Libro) {
        viewModelScope.launch {
            servicio.guardarLibro(libro)
        }
    }

    fun actualizarNombre(nuevoNombre: String) {
        val nombreLimpio = if (nuevoNombre.length > 15) nuevoNombre.take(15) else nuevoNombre
        prefs.userName = nombreLimpio
        _nombreUsuario.value = nombreLimpio
    }

    fun actualizarMeta(nuevaMeta: Int) {
        prefs.readingGoal = nuevaMeta
        _metaLectura.value = nuevaMeta
    }

    fun actualizarFotoPerfil(uri: String?) {
        prefs.profileImageUri = uri
        _fotoPerfilUri.value = uri
    }

    fun actualizarProgreso(libro: Libro, paginasLeidas: Int, paginasTotales: Int? = null) {
        viewModelScope.launch {
            servicio.actualizarProgreso(libro, paginasLeidas, paginasTotales)
        }
    }

    fun actualizarProgresoCapitulos(libro: Libro, capitulosLeidos: Int, capitulosTotales: Int? = null) {
        viewModelScope.launch {
            servicio.actualizarProgresoCapitulos(libro, capitulosLeidos, capitulosTotales)
        }
    }
    
    fun cambiarEstado(libro: Libro, nuevoEstado: Estados) {
        viewModelScope.launch {
            servicio.guardarLibro(libro.copy(estado = nuevoEstado))
        }
    }

    fun eliminarLibro(id: Int) {
        viewModelScope.launch {
            servicio.eliminarLibro(id)
        }
    }

    fun guardarResenia(idLibro: Int, comentario: String) {
        viewModelScope.launch {
            servicio.guardarResenia(idLibro, comentario)
        }
    }
    
    fun obtenerResenia(idLibro: Int) = servicio.obtenerResenia(idLibro)

    fun validarFechas(inicio: LocalDateTime?, fin: LocalDateTime?, pub: String?): String? {
        return servicio.validarFechas(inicio, fin, pub)
    }
}
