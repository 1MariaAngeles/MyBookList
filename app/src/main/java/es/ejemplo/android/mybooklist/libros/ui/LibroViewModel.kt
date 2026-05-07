package es.ejemplo.android.mybooklist.libros.ui

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import es.ejemplo.android.mybooklist.libros.domain.Libro
import es.ejemplo.android.mybooklist.libros.domain.enums.Estados
import es.ejemplo.android.mybooklist.libros.service.LibroService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class LibroViewModel(private val servicio: LibroService) : ViewModel() {

    private val _resultadosBusqueda = MutableStateFlow<List<Libro>>(emptyList())
    val resultadosBusqueda: StateFlow<List<Libro>> = _resultadosBusqueda.asStateFlow()

    private val _estaCargando = MutableStateFlow(false)
    val estaCargando: StateFlow<Boolean> = _estaCargando.asStateFlow()

    // Flujos de la biblioteca (lectura en tiempo real)
    val todosLosLibros = servicio.listarTodos()
    
    fun obtenerLibrosPorEstado(estado: Estados) = servicio.filtrarPorEstado(estado)

    // Estadísticas
    val totalPaginasLeidas = servicio.obtenerTotalPaginas()
    val conteoLibrosTerminados = servicio.obtenerLibrosTerminados()
    val generosMasLeidos = servicio.obtenerGenerosFavoritos()

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
