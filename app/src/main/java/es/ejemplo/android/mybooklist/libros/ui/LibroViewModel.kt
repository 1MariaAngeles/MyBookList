package es.ejemplo.android.mybooklist.libros.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import es.ejemplo.android.mybooklist.general.UserPreferences
import es.ejemplo.android.mybooklist.libros.domain.Libro
import es.ejemplo.android.mybooklist.libros.domain.enums.Estados
import es.ejemplo.android.mybooklist.libros.service.LibroService
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class LibroViewModel(
    private val servicio: LibroService,
    private val prefs: UserPreferences
) : ViewModel() {

    private val _resultadosBusqueda = MutableStateFlow<List<Libro>>(emptyList())
    val resultadosBusqueda: StateFlow<List<Libro>> = _resultadosBusqueda.asStateFlow()

    private val _estaCargando = MutableStateFlow(false)
    val estaCargando: StateFlow<Boolean> = _estaCargando.asStateFlow()

    private val _consultaBusqueda = MutableStateFlow("")
    val consultaBusqueda: StateFlow<String> = _consultaBusqueda.asStateFlow()

    val prediccionesLocales: StateFlow<List<Libro>> = _consultaBusqueda
        .debounce(300)
        .filter { it.length >= 2 }
        .flatMapLatest { servicio.buscarLocal(it) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

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

    fun actualizarConsulta(nuevaConsulta: String) {
        _consultaBusqueda.value = nuevaConsulta
    }

    fun buscarLibros(consulta: String) {
        viewModelScope.launch {
            _estaCargando.value = true
            _resultadosBusqueda.value = servicio.buscarEnGoogleBooks(consulta)
            _estaCargando.value = false
        }
    }

    fun guardarLibro(libro: Libro) {
        viewModelScope.launch {
            servicio.guardarLibro(libro)
        }
    }

    fun actualizarProgreso(libro: Libro, paginasLeidas: Int) {
        viewModelScope.launch {
            servicio.actualizarProgreso(libro, paginasLeidas)
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
}
